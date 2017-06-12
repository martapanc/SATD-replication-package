diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
index f7978c382f..d5c979d2fa 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
@@ -1,296 +1,278 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CharIndexFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.AbstractTransactSQLIdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An abstract base class for Sybase and MS SQL Server dialects.
  *
  * @author Gavin King
  */
 abstract class AbstractTransactSQLDialect extends Dialect {
 	public AbstractTransactSQLDialect() {
 		super();
 		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.BIGINT, "numeric(19,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "datetime" );
 		registerColumnType( Types.TIME, "datetime" );
 		registerColumnType( Types.TIMESTAMP, "datetime" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "image" );
 		registerColumnType( Types.CLOB, "text" );
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.LONG ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "str", new StandardSQLFunction( "str", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "getdate", StandardBasicTypes.DATE ) );
 
 		registerFunction( "getdate", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "getutcdate", new NoArgSQLFunction( "getutcdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "datename", new StandardSQLFunction( "datename", StandardBasicTypes.STRING ) );
 
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
 		registerFunction( "square", new StandardSQLFunction( "square" ) );
 		registerFunction( "rand", new StandardSQLFunction( "rand", StandardBasicTypes.FLOAT ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		registerFunction( "isnull", new StandardSQLFunction( "isnull" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "+", ")" ) );
 
 		registerFunction( "length", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "ltrim(rtrim(?1))" ) );
 		registerFunction( "locate", new CharIndexFunction() );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		return "";
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentitySelectString() {
-		return "select @@identity";
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		//starts with 1, implicitly
-		return "identity not null";
-	}
-
-	@Override
-	public boolean supportsInsertSelectIdentity() {
-		return true;
-	}
-
-	@Override
-	public String appendIdentitySelectToInsert(String insertSQL) {
-		return insertSQL + "\nselect @@identity";
-	}
-
-	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
 		return lockOptions.getLockMode().greaterThan( LockMode.READ ) ? tableName + " holdlock" : tableName;
 	}
 
 	@Override
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
 		// TODO:  merge additional lockoptions support in Dialect.applyLocksToSql
 		final Iterator itr = aliasedLockOptions.getAliasLockIterator();
 		final StringBuilder buffer = new StringBuilder( sql );
 		int correction = 0;
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			final LockMode lockMode = (LockMode) entry.getValue();
 			if ( lockMode.greaterThan( LockMode.READ ) ) {
 				final String alias = (String) entry.getKey();
 				int start = -1;
 				int end = -1;
 				if ( sql.endsWith( " " + alias ) ) {
 					start = ( sql.length() - alias.length() ) + correction;
 					end = start + alias.length();
 				}
 				else {
 					int position = sql.indexOf( " " + alias + " " );
 					if ( position <= -1 ) {
 						position = sql.indexOf( " " + alias + "," );
 					}
 					if ( position > -1 ) {
 						start = position + correction + 1;
 						end = start + alias.length();
 					}
 				}
 
 				if ( start > -1 ) {
 					final String lockHint = appendLockHint( lockMode, alias );
 					buffer.replace( start, end, lockHint );
 					correction += ( lockHint.length() - alias.length() );
 				}
 			}
 		}
 		return buffer.toString();
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		// sql server just returns automatically
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
 		// This assumes you will want to ignore any update counts
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
 
 		// You may still have other ResultSets or update counts left to process here
 		// but you can't do it now or the ResultSet you just got will be closed
 		return ps.getResultSet();
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select getdate()";
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						return "#" + baseName;
 					}
 				},
 				// sql-server, at least needed this dropped after use; strange!
 				AfterUseAction.DROP,
 				TempTableDdlTransactionHandling.NONE
 		);
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select newid()";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
 	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 	
 	@Override
 	public boolean supportsTuplesInSubqueries() {
 		return false;
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new AbstractTransactSQLIdentityColumnSupport();
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
index 19cd5610fd..d1a0062e21 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
@@ -1,363 +1,350 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.CUBRIDIdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.pagination.CUBRIDLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for CUBRID (8.3.x and later).
  *
  * @author Seok Jeong Il
  */
 public class CUBRIDDialect extends Dialect {
 	/**
 	 * Constructs a CUBRIDDialect
 	 */
 	public CUBRIDDialect() {
 		super();
 
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BIT, "bit(8)" );
 		registerColumnType( Types.BLOB, "bit varying(65535)" );
 		registerColumnType( Types.BOOLEAN, "bit(8)" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.CLOB, "string" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "double" );
 		registerColumnType( Types.SMALLINT, "short" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.TINYINT, "short" );
 		registerColumnType( Types.VARBINARY, 2000, "bit varying($l)" );
 		registerColumnType( Types.VARCHAR, "string" );
 		registerColumnType( Types.VARCHAR, 2000, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bin", new StandardSQLFunction( "bin", StandardBasicTypes.STRING ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.LONG ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
 		registerFunction( "lengthb", new StandardSQLFunction( "lengthb", StandardBasicTypes.LONG ) );
 		registerFunction( "lengthh", new StandardSQLFunction( "lengthh", StandardBasicTypes.LONG ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "trim", new StandardSQLFunction( "trim" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log2", new StandardSQLFunction( "log2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "random", new NoArgSQLFunction( "random", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "ceil", new StandardSQLFunction( "ceil", StandardBasicTypes.INTEGER ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 
 		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
 		registerFunction( "timediff", new StandardSQLFunction( "timediff", StandardBasicTypes.TIME ) );
 
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "sys_date", new NoArgSQLFunction( "sys_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "sys_time", new NoArgSQLFunction( "sys_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "systime", new NoArgSQLFunction( "systime", StandardBasicTypes.TIME, false ) );
 
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction(
 				"current_timestamp",
 				StandardBasicTypes.TIMESTAMP,
 				false
 		)
 		);
 		registerFunction(
 				"sys_timestamp", new NoArgSQLFunction(
 				"sys_timestamp",
 				StandardBasicTypes.TIMESTAMP,
 				false
 		)
 		);
 		registerFunction( "systimestamp", new NoArgSQLFunction( "systimestamp", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "localtime", new NoArgSQLFunction( "localtime", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction(
 				"localtimestamp", new NoArgSQLFunction(
 				"localtimestamp",
 				StandardBasicTypes.TIMESTAMP,
 				false
 		)
 		);
 
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "from_days", new StandardSQLFunction( "from_days", StandardBasicTypes.DATE ) );
 		registerFunction( "from_unixtime", new StandardSQLFunction( "from_unixtime", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "months_between", new StandardSQLFunction( "months_between", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sec_to_time", new StandardSQLFunction( "sec_to_time", StandardBasicTypes.TIME ) );
 		registerFunction( "time_to_sec", new StandardSQLFunction( "time_to_sec", StandardBasicTypes.INTEGER ) );
 		registerFunction( "to_days", new StandardSQLFunction( "to_days", StandardBasicTypes.LONG ) );
 		registerFunction( "unix_timestamp", new StandardSQLFunction( "unix_timestamp", StandardBasicTypes.LONG ) );
 		registerFunction( "utc_date", new NoArgSQLFunction( "utc_date", StandardBasicTypes.STRING ) );
 		registerFunction( "utc_time", new NoArgSQLFunction( "utc_time", StandardBasicTypes.STRING ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekday", new StandardSQLFunction( "weekday", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.LONG ) );
 
 		registerFunction( "bit_count", new StandardSQLFunction( "bit_count", StandardBasicTypes.LONG ) );
 		registerFunction( "md5", new StandardSQLFunction( "md5", StandardBasicTypes.STRING ) );
 
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substring", StandardBasicTypes.STRING ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod" ) );
 
 		registerFunction( "power", new StandardSQLFunction( "power" ) );
 		registerFunction( "stddev", new StandardSQLFunction( "stddev" ) );
 		registerFunction( "variance", new StandardSQLFunction( "variance" ) );
 		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 		registerFunction( "nvl2", new StandardSQLFunction( "nvl2" ) );
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.INTEGER ) );
 		registerFunction( "instrb", new StandardSQLFunction( "instrb", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
 
 		registerFunction( "add_months", new StandardSQLFunction( "add_months", StandardBasicTypes.DATE ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.LONG, false ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 
 		registerKeyword( "TYPE" );
 		registerKeyword( "YEAR" );
 		registerKeyword( "MONTH" );
 		registerKeyword( "ALIAS" );
 		registerKeyword( "VALUE" );
 		registerKeyword( "FIRST" );
 		registerKeyword( "ROLE" );
 		registerKeyword( "CLASS" );
 		registerKeyword( "BIT" );
 		registerKeyword( "TIME" );
 		registerKeyword( "QUERY" );
 		registerKeyword( "DATE" );
 		registerKeyword( "USER" );
 		registerKeyword( "ACTION" );
 		registerKeyword( "SYS_USER" );
 		registerKeyword( "ZONE" );
 		registerKeyword( "LANGUAGE" );
 		registerKeyword( "DICTIONARY" );
 		registerKeyword( "DATA" );
 		registerKeyword( "TEST" );
 		registerKeyword( "SUPERCLASS" );
 		registerKeyword( "SECTION" );
 		registerKeyword( "LOWER" );
 		registerKeyword( "LIST" );
 		registerKeyword( "OID" );
 		registerKeyword( "DAY" );
 		registerKeyword( "IF" );
 		registerKeyword( "ATTRIBUTE" );
 		registerKeyword( "STRING" );
 		registerKeyword( "SEARCH" );
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentityInsertString() {
-		return "NULL";
-	}
-
-	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
-	public String getIdentitySelectString() {
-		return "select last_insert_id()";
-	}
-
-	@Override
-	protected String getIdentityColumnString() {
-		//starts with 1, implicitly
-		return "not null auto_increment";
-	}
-
-	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + sequenceName + ".next_value from table({1}) as T(X)";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create serial " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop serial " + sequenceName;
 	}
 
 	@Override
 	public String getDropForeignKeyString() {
 		return " drop foreign key ";
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select name from db_serial";
 	}
 
 	@Override
 	public char openQuote() {
 		return '[';
 	}
 
 	@Override
 	public char closeQuote() {
 		return ']';
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " ";
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsIfExistsBeforeTableName() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return CUBRIDLimitHandler.INSTANCE;
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new CUBRIDIdentityColumnSupport();
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index 94256d808e..2c85135789 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,694 +1,677 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import org.hibernate.LockMode;
-import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.ConditionalParenthesisFunction;
 import org.hibernate.dialect.function.ConvertFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.Chache71IdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.TopLimitHandler;
 import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CacheJoinFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 /**
  * Cach&eacute; 2007.1 dialect.
  *
  * This class is required in order to use Hibernate with Intersystems Cach&eacute; SQL.  Compatible with
  * Cach&eacute; 2007.1.
  *
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
  * <b>NOTE:</b> Please contact your administrator for the userid and password you should use when
  *         attempting access via JDBC.  By default, these are chosen to be "_SYSTEM" and "SYS" respectively
  *         as noted in the SQL standard.
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
 
 	private final TopLimitHandler limitHandler;
 
 	/**
 	 * Creates new <code>Cache71Dialect</code> instance. Sets up the JDBC /
 	 * Cach&eacute; type mappings.
 	 */
 	public Cache71Dialect() {
 		super();
 		commonRegistration();
 		register71Functions();
 		this.limitHandler = new TopLimitHandler(true, true);
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
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.VARBINARY, "longvarbinary" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.BLOB, "longvarbinary" );
 		registerColumnType( Types.CLOB, "longvarchar" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", StandardBasicTypes.STRING ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
 		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "($length(?1)*8)" ) );
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
 
 	@Override
 	public boolean hasAlterTable() {
 		// Does this dialect support the ALTER TABLE syntax?
 		return true;
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		// Do we need to qualify index names with the schema name?
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("StringBufferReplaceableByString")
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
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") REFERENCES " )
 				.append( referencedTable )
 				.append( " (" )
 				.append( StringHelper.join( ", ", primaryKey ) )
 				.append( ") " )
 				.toString();
 	}
 
 	/**
 	 * Does this dialect support check constraints?
 	 *
 	 * @return {@code false} (Cache does not support check constraints)
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public boolean supportsCheck() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		// The syntax used to add a column to a table
 		return " add column";
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		// Completely optional cascading drop clause.
 		return "";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		// Do we need to drop constraints before dropping tables in this dialect?
 		return true;
 	}
 
 	@Override
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	@Override
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new GlobalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						final String name = super.generateIdTableName( baseName );
 						return name.length() > 25 ? name.substring( 1, 25 ) : name;
 					}
 
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create global temporary table";
 					}
 				},
 				AfterUseAction.DROP
 		);
 	}
 
-	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
 	@Override
 	public Class getNativeIdentifierGeneratorClass() {
 		return IdentityGenerator.class;
 	}
 
-	@Override
-	public boolean hasDataTypeInIdentityColumn() {
-		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
-		// data type
-		return true;
-	}
-
-	@Override
-	public String getIdentityColumnString() throws MappingException {
-		// The keyword used to specify an identity column, if identity column key generation is supported.
-		return "identity";
-	}
+	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
-	public String getIdentitySelectString() {
-		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new Chache71IdentityColumnSupport();
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
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
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
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
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return limitHandler;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean bindLimitParametersFirst() {
 		// Does the LIMIT clause come at the start of the SELECT statement, rather than at the end?
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean useMaxForLimit() {
 		// Does the LIMIT clause take a "maximum" row number instead of a total number of returned rows?
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hasOffset ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 
 		// This does not support the Cache SQL 'DISTINCT BY (comma-list)' extensions,
 		// but this extension is not supported through Hibernate anyway.
 		final int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
 
 		return new StringBuilder( sql.length() + 8 )
 				.append( sql )
 				.insert( insertionPoint, " TOP ? " )
 				.toString();
 	}
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getLowercaseFunction() {
 		// The name of the SQL function that transforms a string to lowercase
 		return "lower";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		// Create an OuterJoinGenerator for this dialect.
 		return new CacheJoinFragment();
 	}
 
 	@Override
 	public String getNoColumnsInsertString() {
 		// The keyword used to insert a row without specifying
 		// any column values
 		return " default values";
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new CacheSQLExceptionConversionDelegate( this );
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
 	 * The Cache ViolatedConstraintNameExtracter.
 	 */
 	public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
 		}
 	};
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
index 5cbafe3ba8..136f0b1437 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
@@ -1,95 +1,97 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
+import org.hibernate.dialect.identity.DB2390IdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 
 
 /**
  * An SQL dialect for DB2/390. This class provides support for
  * DB2 Universal Database for OS/390, also known as DB2/390.
  *
  * @author Kristoffer Dyrkorn
  */
 public class DB2390Dialect extends DB2Dialect {
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			if (LimitHelper.hasFirstRow( selection )) {
 				throw new UnsupportedOperationException( "query result offset is not supported" );
 			}
 			return sql + " fetch first " + getMaxOrLimit( selection ) + " rows only";
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean supportsVariableLimit() {
 			return false;
 		}
 	};
 
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	@Override
-	public String getIdentitySelectString() {
-		return "select identity_val_local() from sysibm.sysdummy1";
-	}
-
-	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		if ( limit == 0 ) {
 			return sql;
 		}
 		return sql + " fetch first " + limit + " rows only ";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new DB2390IdentityColumnSupport();
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
index bda632ae78..13c219e001 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
@@ -1,98 +1,100 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
+import org.hibernate.dialect.identity.DB2390IdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * An SQL dialect for DB2/400.  This class provides support for DB2 Universal Database for iSeries,
  * also known as DB2/400.
  *
  * @author Peter DeGregorio (pdegregorio)
  */
 public class DB2400Dialect extends DB2Dialect {
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			if (LimitHelper.hasFirstRow( selection )) {
 				throw new UnsupportedOperationException( "query result offset is not supported" );
 			}
 			return sql + " fetch first " + getMaxOrLimit( selection ) + " rows only";
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean supportsVariableLimit() {
 			return false;
 		}
 	};
 
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	@Override
-	public String getIdentitySelectString() {
-		return "select identity_val_local() from sysibm.sysdummy1";
-	}
-
-	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		if ( limit == 0 ) {
 			return sql;
 		}
 		return sql + " fetch first " + limit + " rows only ";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " for update with rs";
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new DB2390IdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
index 42108b4d94..a7de657df0 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
@@ -1,569 +1,556 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.JDBCException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.DB2IdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.unique.DB2UniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An SQL dialect for DB2.
  *
  * @author Gavin King
  */
 public class DB2Dialect extends Dialect {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( DB2Dialect.class );
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			if (LimitHelper.hasFirstRow( selection )) {
 				//nest the main query in an outer select
 				return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
 						+ sql + " fetch first " + getMaxOrLimit( selection ) + " rows only ) as inner2_ ) as inner1_ where rownumber_ > "
 						+ selection.getFirstRow() + " order by rownumber_";
 			}
 			return sql + " fetch first " + getMaxOrLimit( selection ) +  " rows only";
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean supportsVariableLimit() {
 			return false;
 		}
 	};
 
 
 	private final UniqueDelegate uniqueDelegate;
 
 	/**
 	 * Constructs a DB2Dialect
 	 */
 	public DB2Dialect() {
 		super();
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "varchar($l) for bit data" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "blob($l)" );
 		registerColumnType( Types.CLOB, "clob($l)" );
 		registerColumnType( Types.LONGVARCHAR, "long varchar" );
 		registerColumnType( Types.LONGVARBINARY, "long varchar for bit data" );
 		registerColumnType( Types.BINARY, "varchar($l) for bit data" );
 		registerColumnType( Types.BINARY, 254, "char($l) for bit data" );
 		registerColumnType( Types.BOOLEAN, "smallint" );
 
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "absval", new StandardSQLFunction( "absval" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "float", new StandardSQLFunction( "float", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "stddev", new StandardSQLFunction( "stddev", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "variance", new StandardSQLFunction( "variance", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "julian_day", new StandardSQLFunction( "julian_day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
 		registerFunction(
 				"midnight_seconds",
 				new StandardSQLFunction( "midnight_seconds", StandardBasicTypes.INTEGER )
 		);
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek_iso", new StandardSQLFunction( "dayofweek_iso", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "days", new StandardSQLFunction( "days", StandardBasicTypes.LONG ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction(
 				"current_timestamp",
 				new NoArgSQLFunction( "current timestamp", StandardBasicTypes.TIMESTAMP, false )
 		);
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "timestamp_iso", new StandardSQLFunction( "timestamp_iso", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week_iso", new StandardSQLFunction( "week_iso", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "double", new StandardSQLFunction( "double", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "varchar", new StandardSQLFunction( "varchar", StandardBasicTypes.STRING ) );
 		registerFunction( "real", new StandardSQLFunction( "real", StandardBasicTypes.FLOAT ) );
 		registerFunction( "bigint", new StandardSQLFunction( "bigint", StandardBasicTypes.LONG ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "integer", new StandardSQLFunction( "integer", StandardBasicTypes.INTEGER ) );
 		registerFunction( "smallint", new StandardSQLFunction( "smallint", StandardBasicTypes.SHORT ) );
 
 		registerFunction( "digits", new StandardSQLFunction( "digits", StandardBasicTypes.STRING ) );
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "posstr", new StandardSQLFunction( "posstr", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "length(?1)*8" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "rtrim(char(?1))" ) );
 
 		registerKeyword( "current" );
 		registerKeyword( "date" );
 		registerKeyword( "time" );
 		registerKeyword( "timestamp" );
 		registerKeyword( "fetch" );
 		registerKeyword( "first" );
 		registerKeyword( "rows" );
 		registerKeyword( "only" );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 		
 		uniqueDelegate = new DB2UniqueDelegate( this );
 	}
 
 	@Override
 	public String getLowercaseFunction() {
 		return "lcase";
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentitySelectString() {
-		return "values identity_val_local()";
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		return "generated by default as identity";
-	}
-
-	@Override
-	public String getIdentityInsertString() {
-		return "default";
-	}
-
-	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "values nextval for " + sequenceName;
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select seqname from sysibm.syssequences";
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset == 0 ) {
 			return sql + " fetch first " + limit + " rows only";
 		}
 		//nest the main query in an outer select
 		return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
 				+ sql + " fetch first " + limit + " rows only ) as inner2_ ) as inner1_ where rownumber_ > "
 				+ offset + " order by rownumber_";
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 *
 	 * DB2 does have a one-based offset, however this was actually already handled in the limit string building
 	 * (the '?+1' bit).  To not mess up inheritors, I'll leave that part alone and not touch the offset here.
 	 */
 	@Override
 	@SuppressWarnings("deprecation")
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public String getForUpdateString() {
 		return " for read only with rs use and keep update locks";
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		//as far as I know, DB2 doesn't support this
 		return false;
 	}
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String literal;
 		switch ( sqlType ) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				literal = "'x'";
 				break;
 			case Types.DATE:
 				literal = "'2000-1-1'";
 				break;
 			case Types.TIMESTAMP:
 				literal = "'2000-1-1 00:00:00'";
 				break;
 			case Types.TIME:
 				literal = "'00:00:00'";
 				break;
 			default:
 				literal = "0";
 		}
 		return "nullif(" + literal + ',' + literal + ')';
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
 		// This assumes you will want to ignore any update counts 
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
 
 		return ps.getResultSet();
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new GlobalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						return "session." + super.generateIdTableName( baseName );
 					}
 
 					@Override
 					public String getCreateIdTableCommand() {
 						return "declare global temporary table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "not logged";
 					}
 				},
 				AfterUseAction.CLEAN
 		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "values current timestamp";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * NOTE : DB2 is know to support parameters in the <tt>SELECT</tt> clause, but only in casted form
 	 * (see {@link #requiresCastingOfParametersInSelectClause()}).
 	 */
 	@Override
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * DB2 in fact does require that parameters appearing in the select clause be wrapped in cast() calls
 	 * to tell the DB parser the type of the select value.
 	 */
 	@Override
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		//DB2 v9.1 doesn't support 'cross join' syntax
 		return ", ";
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? SmallIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 				if( -952 == errorCode && "57014".equals( sqlState )){
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				return null;
 			}
 		};
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	/**
 	 * Handle DB2 "support" for null precedence...
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nullPrecedence Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 *
 	 * @return
 	 */
 	@Override
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nullPrecedence) {
 		if ( nullPrecedence == null || nullPrecedence == NullPrecedence.NONE ) {
 			return super.renderOrderByElement( expression, collation, order, NullPrecedence.NONE );
 		}
 
 		// DB2 FTW!  A null precedence was explicitly requested, but DB2 "support" for null precedence
 		// is a joke.  Basically it supports combos that align with what it does anyway.  Here is the
 		// support matrix:
 		//		* ASC + NULLS FIRST -> case statement
 		//		* ASC + NULLS LAST -> just drop the NULLS LAST from sql fragment
 		//		* DESC + NULLS FIRST -> just drop the NULLS FIRST from sql fragment
 		//		* DESC + NULLS LAST -> case statement
 
 		if ( ( nullPrecedence == NullPrecedence.FIRST  && "desc".equalsIgnoreCase( order ) )
 				|| ( nullPrecedence == NullPrecedence.LAST && "asc".equalsIgnoreCase( order ) ) ) {
 			// we have one of:
 			//		* ASC + NULLS LAST
 			//		* DESC + NULLS FIRST
 			// so just drop the null precedence.  *NOTE: we could pass along the null precedence here,
 			// but only DB2 9.7 or greater understand it; dropping it is more portable across DB2 versions
 			return super.renderOrderByElement( expression, collation, order, NullPrecedence.NONE );
 		}
 
 		return String.format(
 				Locale.ENGLISH,
 				"case when %s is null then %s else %s end, %s %s",
 				expression,
 				nullPrecedence == NullPrecedence.FIRST ? "0" : "1",
 				nullPrecedence == NullPrecedence.FIRST ? "1" : "0",
 				expression,
 				order
 		);
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new DB2IdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index fd534376c8..54d044b6e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,1850 +1,1854 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.DatabaseMetaData;
 import java.sql.NClob;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.ScrollMode;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Sequence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CastFunction;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
 import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.env.internal.DefaultSchemaNameResolver;
 import org.hibernate.engine.jdbc.env.spi.AnsiSqlKeywords;
 import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
 import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
 import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
 import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.persistent.PersistentTableBulkIdStrategy;
 import org.hibernate.id.IdentityGenerator;
-import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Table;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
 import org.hibernate.tool.schema.internal.StandardAuxiliaryDatabaseObjectExporter;
 import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
 import org.hibernate.tool.schema.internal.StandardIndexExporter;
 import org.hibernate.tool.schema.internal.StandardSequenceExporter;
 import org.hibernate.tool.schema.internal.StandardTableExporter;
 import org.hibernate.tool.schema.internal.StandardUniqueKeyExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
-import org.jboss.logging.Logger;
-
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.  Subclasses implement Hibernate compatibility
  * with different systems.  Subclasses should provide a public default constructor that register a set of type
  * mappings and default Hibernate properties.  Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 @SuppressWarnings("deprecation")
 public abstract class Dialect implements ConversionContext {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			Dialect.class.getName()
 	);
 
 	/**
 	 * Defines a default batch size constant
 	 */
 	public static final String DEFAULT_BATCH_SIZE = "15";
 
 	/**
 	 * Defines a "no batching" batch size constant
 	 */
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used as opening for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 
 	/**
 	 * Characters used as closing for quoting SQL identifiers
 	 */
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 
 	private final UniqueDelegate uniqueDelegate;
 
 
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
 
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		return instantiateDialect( Environment.getProperties().getProperty( Environment.DIALECT ) );
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
 		final String dialectName = props.getProperty( Environment.DIALECT );
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
 			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
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
 	 * Allows the Dialect to contribute additional types
 	 *
 	 * @param typeContributions Callback to contribute the types
 	 * @param serviceRegistry The service registry
 	 */
 	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
 		// by default, nothing to do
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		final String result = typeNames.get( code );
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
 		final String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length )
 			);
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
 	 * Return an expression casting the value to the specified type
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_PRECISION} and
 	 * {@link Column#DEFAULT_SCALE} as the precision/scale.
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_LENGTH} as the length
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
 		return cast( value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale );
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
 					// the BLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setBinaryStream( 1L );
 					// the BLOB from the detached state
 					final InputStream detachedStream = original.getBinaryStream();
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
 					// the CLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the CLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
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
 					// the NCLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the NCLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
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
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator(
 						session
 				);
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
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator( session );
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
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator( session );
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
 		final String result = hibernateTypeNames.get( code );
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
 		final String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format(
 							"No Hibernate type mapping for type [code=%s, length=%s]",
 							code,
 							length
 					)
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
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( name.toLowerCase( Locale.ROOT ), function );
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
-		if ( supportsIdentityColumns() ) {
+		if ( getIdentityColumnSupport().supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else {
 			return SequenceStyleGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
+	 * Get the appropriate {@link IdentityColumnSupport}
+	 *
+	 * @return the IdentityColumnSupport
+	 * @since 5.1
+	 */
+	public IdentityColumnSupport getIdentityColumnSupport(){
+		return new IdentityColumnSupportImpl();
+	}
+
+	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
+	 *
+	 * @deprecated use {@link IdentityColumnSupport} method instead
 	 */
+	@Deprecated
 	public boolean supportsIdentityColumns() {
-		return false;
+		return getIdentityColumnSupport().supportsIdentityColumns();
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
+	 *
+	 * @deprecated use {@link IdentityColumnSupport} method instead
 	 */
+	@Deprecated
 	public boolean supportsInsertSelectIdentity() {
-		return false;
+		return getIdentityColumnSupport().supportsInsertSelectIdentity();
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
+	 *
+	 * @deprecated use {@link IdentityColumnSupport} method instead
 	 */
+	@Deprecated
 	public boolean hasDataTypeInIdentityColumn() {
-		return true;
+		return getIdentityColumnSupport().hasDataTypeInIdentityColumn();
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
+	 *
+	 * @deprecated use {@link IdentityColumnSupport} method instead
 	 */
+	@Deprecated
 	public String appendIdentitySelectToInsert(String insertString) {
-		return insertString;
+		return getIdentityColumnSupport().appendIdentitySelectToInsert( insertString );
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
-	 */
-	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
-		return getIdentitySelectString();
-	}
-
-	/**
-	 * Get the select command to use to retrieve the last generated IDENTITY
-	 * value.
 	 *
-	 * @return The appropriate select command
-	 * @throws MappingException If IDENTITY generation is not supported.
-	 *
-	 * @deprecated use {@link Dialect#getIdentitySelectString(String, String, int)} instead
+	 * @deprecated use {@link IdentityColumnSupport} method instead
 	 */
 	@Deprecated
-	protected String getIdentitySelectString() throws MappingException {
-		throw new MappingException( getClass().getName() + " does not support identity key generation" );
+	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
+		return getIdentityColumnSupport().getIdentitySelectString( table, column, type );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
-	 */
-	public String getIdentityColumnString(int type) throws MappingException {
-		return getIdentityColumnString();
-	}
-
-	/**
-	 * The syntax used during DDL to define a column as being an IDENTITY.
-	 *
-	 * @return The appropriate DDL fragment.
-	 * @throws MappingException If IDENTITY generation is not supported.
 	 *
-	 * @deprecated use {@link Dialect#getIdentityColumnString(int)} instead
+	 * @deprecated use {@link IdentityColumnSupport} method instead
 	 */
 	@Deprecated
-	protected String getIdentityColumnString() throws MappingException {
-		throw new MappingException( getClass().getName() + " does not support identity key generation" );
+	public String getIdentityColumnString(int type) throws MappingException {
+		return getIdentityColumnSupport().getIdentityColumnString( type );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
+	 *
+	 * @deprecated use {@link IdentityColumnSupport} method instead
 	 */
+	@Deprecated
 	public String getIdentityInsertString() {
-		return null;
+		return getIdentityColumnSupport().getIdentityInsertString();
 	}
 
-
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
 
 	public SequenceInformationExtractor getSequenceInformationExtractor() {
 		if ( getQuerySequencesString() == null ) {
 			return SequenceInformationExtractorNoOpImpl.INSTANCE;
 		}
 		else {
 			return SequenceInformationExtractorLegacyImpl.INSTANCE;
 		}
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
 	 * Returns the delegate managing LIMIT clause.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler getLimitHandler() {
 		return new LegacyLimitHandler( this );
 	}
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
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
 	 * @return The corresponding db/dialect specific offset.
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
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
 		final LockMode lockMode = lockOptions.getLockMode();
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
 			case UPGRADE_SKIPLOCKED:
 				return getForUpdateSkipLockedString();
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
 	@SuppressWarnings({"unchecked", "UnusedParameters"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		final Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan( lockMode ) ) {
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
 	 * Retrieves the <tt>FOR UPDATE SKIP LOCKED</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE SKIP LOCKED</tt> clause string.
 	 */
 	public String getForUpdateSkipLockedString() {
 		// by default we report no support for SKIP_LOCKED lock semantics
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
 	 * Get the <tt>FOR UPDATE OF column_list SKIP LOCKED</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE colunm_list SKIP LOCKED</tt> clause string.
 	 */
 	public String getForUpdateSkipLockedString(String aliases) {
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
 	 * @deprecated use {@code appendLockHint(LockOptions,String)} instead
 	 */
 	@Deprecated
 	public String appendLockHint(LockMode mode, String tableName) {
 		return appendLockHint( new LockOptions( mode ), tableName );
 	}
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param lockOptions The lock options to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockOptions lockOptions, String tableName){
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
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
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
 
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new PersistentTableBulkIdStrategy();
 	}
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by position*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by name*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
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
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet}.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
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
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
 	 *
 	 * @return The SQLExceptionConversionDelegate for this dialect
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
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
 	 *
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * Does this dialect support case insensitive LIKE restrictions?
 	 *
 	 * @return {@code true} if the underlying database supports case insensitive like comparison,
 	 * {@code false} otherwise.  The default is {@code false}.
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
 	 * <p/>
 	 * The maximum here should account for the fact that Hibernate often needs to append "uniqueing" information
 	 * to the end of generated aliases.  That "uniqueing" information will be added to the end of a identifier
 	 * generated to the length specified here; so be sure to leave some room (generally speaking 5 positions will
 	 * suffice).
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
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
index 17214160ef..19d3d0e8f9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
@@ -1,456 +1,442 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.H2IdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorH2DatabaseImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
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
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			H2Dialect.class.getName()
 	);
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 	};
 
 	private final String querySequenceString;
 	private final SequenceInformationExtractor sequenceInformationExtractor;
 
 	/**
 	 * Constructs a H2Dialect
 	 */
 	public H2Dialect() {
 		super();
 
 		String querySequenceString = "select sequence_name from information_schema.sequences";
 		SequenceInformationExtractor sequenceInformationExtractor = SequenceInformationExtractorH2DatabaseImpl.INSTANCE;
 		try {
 			// HHH-2300
 			final Class h2ConstantsClass = ReflectHelper.classForName( "org.h2.engine.Constants" );
 			final int majorVersion = (Integer) h2ConstantsClass.getDeclaredField( "VERSION_MAJOR" ).get( null );
 			final int minorVersion = (Integer) h2ConstantsClass.getDeclaredField( "VERSION_MINOR" ).get( null );
 			final int buildId = (Integer) h2ConstantsClass.getDeclaredField( "BUILD_ID" ).get( null );
 			if ( buildId < 32 ) {
 				querySequenceString = "select name from information_schema.sequences";
 				sequenceInformationExtractor = SequenceInformationExtractorLegacyImpl.INSTANCE;
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
 		this.sequenceInformationExtractor = sequenceInformationExtractor;
 
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
 		// http://code.google.com/p/h2database/issues/detail?id=235
 		getDefaultProperties().setProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		// not null is implicit
-		return "generated by default as identity";
-	}
-
-	@Override
-	public String getIdentitySelectString() {
-		return "call identity()";
-	}
-
-	@Override
-	public String getIdentityInsertString() {
-		return "null";
-	}
-
-	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence if exists " + sequenceName;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return querySequenceString;
 	}
 
 	@Override
 	public SequenceInformationExtractor getSequenceInformationExtractor() {
 		return sequenceInformationExtractor;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 			// 23000: Check constraint violation: {0}
 			// 23001: Unique index or primary key violation: {0}
 			if ( sqle.getSQLState().startsWith( "23" ) ) {
 				final String message = sqle.getMessage();
 				final int idx = message.indexOf( "violation: " );
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
 					final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 					if (40001 == errorCode) {
 						// DEADLOCK DETECTED
 						return new LockAcquisitionException(message, sqlException, sql);
 					}
 
 					if (50200 == errorCode) {
 						// LOCK NOT AVAILABLE
 						return new PessimisticLockException(message, sqlException, sql);
 					}
 
 					if ( 90006 == errorCode ) {
 						// NULL not allowed for column [90006-145]
 						final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 						return new ConstraintViolationException( message, sqlException, sql, constraintName );
 					}
 
 					return null;
 				}
 			};
 		}
 		return delegate;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create cached local temporary table if not exists";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						// actually 2 different options are specified here:
 						//		1) [on commit drop] - says to drop the table on transaction commit
 						//		2) [transactional] - says to not perform an implicit commit of any current transaction
 						return "on commit drop transactional";					}
 				},
 				AfterUseAction.CLEAN,
 				TempTableDdlTransactionHandling.NONE
 		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp()";
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean requiresParensForTupleDistinctCounts() {
 		return true;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		// see http://groups.google.com/group/h2-database/browse_thread/thread/562d8a49e2dabe99?hl=en
 		return true;
 	}
 	
 	@Override
 	public boolean supportsTuplesInSubqueries() {
 		return false;
 	}
 	
 	@Override
 	public boolean dropConstraints() {
 		// We don't need to drop constraints before dropping tables, that just leads to error
 		// messages about missing tables when we don't have a schema in the database
 		return false;
-	}	
+	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new H2IdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
index 99e60e3a2a..244a32e45b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
@@ -1,675 +1,661 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.HSQLIdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 
 import java.io.Serializable;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
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
 @SuppressWarnings("deprecation")
 public class HSQLDialect extends Dialect {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			HSQLDialect.class.getName()
 	);
 
 	private final class HSQLLimitHandler extends AbstractLimitHandler {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			if (hsqldbVersion < 20) {
 				return new StringBuilder( sql.length() + 10 )
 						.append( sql )
 						.insert(
 								sql.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6,
 								hasOffset ? " limit ? ?" : " top ?"
 						)
 						.toString();
 			}
 			else {
 				return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
 			}
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersFirst() {
 			return hsqldbVersion < 20;
 		}
 	}
 
 	/**
 	 * version is 18 for 1.8 or 20 for 2.0
 	 */
 	private int hsqldbVersion = 18;
 	private final LimitHandler limitHandler;
 
 
 	/**
 	 * Constructs a HSQLDialect
 	 */
 	public HSQLDialect() {
 		super();
 
 		try {
 			final Class props = ReflectHelper.classForName( "org.hsqldb.persist.HsqlDatabaseProperties" );
 			final String versionString = (String) props.getDeclaredField( "THIS_VERSION" ).get( null );
 
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
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex" ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw" ) );
 
 		// system functions
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 
 		// datetime functions
 		if ( hsqldbVersion < 20 ) {
 			registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 		}
 		else {
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
 			registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.INTEGER ) );
 		}
 
 		// function templates
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		limitHandler = new HSQLLimitHandler();
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		//not null is implicit
-		return "generated by default as identity (start with 1)";
-	}
-
-	@Override
-	public String getIdentitySelectString() {
-		return "call identity()";
-	}
-
-	@Override
-	public String getIdentityInsertString() {
-		return hsqldbVersion < 20 ? "null" : "default";
-	}
-
-	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		if ( hsqldbVersion >= 20 ) {
 			return " for update";
 		}
 		else {
 			return "";
 		}
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return limitHandler;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hsqldbVersion < 20 ) {
 			return new StringBuilder( sql.length() + 10 )
 					.append( sql )
 					.insert(
 							sql.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6,
 							hasOffset ? " limit ? ?" : " top ?"
 					)
 					.toString();
 		}
 		else {
 			return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
 		}
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return hsqldbVersion < 20;
 	}
 
 	@Override
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsColumnCheck() {
 		return hsqldbVersion >= 20;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	/**
 	 * HSQL will start with 0, by default.  In order for Hibernate to know that this not transient,
 	 * manually start with 1.
 	 */
 	@Override
 	protected String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName + " start with 1";
 	}
 	
 	/**
 	 * Because of the overridden {@link #getCreateSequenceString(String)}, we must also override
 	 * {@link #getCreateSequenceString(String, int, int)} to prevent 2 instances of "start with".
 	 */
 	@Override
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return "create sequence " + sequenceName + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	@Override
 	protected String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		// this assumes schema support, which is present in 1.8.0 and later...
 		return "select sequence_name from information_schema.system_sequences";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return hsqldbVersion < 20 ? EXTRACTER_18 : EXTRACTER_20;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
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
 	private static final ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
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
 
 	@Override
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
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		// Hibernate uses this information for temporary tables that it uses for its own operations
 		// therefore the appropriate strategy is taken with different versions of HSQLDB
 
 		// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
 		// definition is shared by all users but data is private to the session
 		// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
 		// the definition and data is private to the session and table declaration
 		// can happen in the middle of a transaction
 
 		if ( hsqldbVersion < 20 ) {
 			return new GlobalTemporaryTableBulkIdStrategy(
 					new IdTableSupportStandardImpl() {
 						@Override
 						public String generateIdTableName(String baseName) {
 							return "HT_" + baseName;
 						}
 
 						@Override
 						public String getCreateIdTableCommand() {
 							return "create global temporary table";
 						}
 					},
 					// Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
 					// of the session (by default, data is cleared at commit).
 					AfterUseAction.CLEAN
 			);
 		}
 		else {
 			return new LocalTemporaryTableBulkIdStrategy(
 					new IdTableSupportStandardImpl() {
 						@Override
 						public String generateIdTableName(String baseName) {
 							// With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
 							// statement (in-case there is a global name beginning with HT_)
 							return "MODULE.HT_" + baseName;
 						}
 
 						@Override
 						public String getCreateIdTableCommand() {
 							return "declare local temporary table";
 						}
 					},
 					AfterUseAction.DROP,
 					TempTableDdlTransactionHandling.NONE
 			);
 		}
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * HSQLDB 1.8.x requires CALL CURRENT_TIMESTAMP but this should not
 	 * be treated as a callable statement. It is equivalent to
 	 * "select current_timestamp from dual" in some databases.
 	 * HSQLDB 2.0 also supports VALUES CURRENT_TIMESTAMP
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 	/**
 	 * For HSQLDB 2.0, this is a copy of the base class implementation.
 	 * For HSQLDB 1.8, only READ_UNCOMMITTED is supported.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
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
 
 	private static class ReadUncommittedLockingStrategy extends SelectLockingStrategy {
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
 
 	@Override
 	public boolean supportsCommentOn() {
 		return hsqldbVersion >= 20;
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return hsqldbVersion >= 20;
 	}
 
 	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return hsqldbVersion >= 20;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public String toBooleanValueString(boolean bool) {
 		return String.valueOf( bool );
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new HSQLIdentityColumnSupport( this.hsqldbVersion );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
index 61af2f2c70..00b64ef770 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
@@ -1,299 +1,280 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
-import org.hibernate.MappingException;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.InformixIdentityColumnSupport;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.InformixUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 /**
  * Informix dialect.<br>
  * <br>
  * Seems to work with Informix Dynamic Server Version 7.31.UD3,  Informix JDBC driver version 2.21JC3.
  *
  * @author Steve Molitor
  */
 public class InformixDialect extends Dialect {
 	
 	private final UniqueDelegate uniqueDelegate;
 
 	/**
 	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
 	 * Informix type mappings.
 	 */
 	public InformixDialect() {
 		super();
 
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.BINARY, "byte" );
 		// Informix doesn't have a bit type
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.FLOAT, "smallfloat" );
 		registerColumnType( Types.INTEGER, "integer" );
 		// or BYTE
 		registerColumnType( Types.LONGVARBINARY, "blob" );
 		// or TEXT?
 		registerColumnType( Types.LONGVARCHAR, "clob" );
 		// or MONEY
 		registerColumnType( Types.NUMERIC, "decimal" );
 		registerColumnType( Types.REAL, "smallfloat" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "datetime year to fraction(5)" );
 		registerColumnType( Types.TIME, "datetime hour to second" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.VARBINARY, "byte" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 32739, "lvarchar($l)" );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		
 		uniqueDelegate = new InformixUniqueDelegate( this );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
-	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentitySelectString(String table, String column, int type)
-			throws MappingException {
-		return type == Types.BIGINT
-				? "select dbinfo('serial8') from informix.systables where tabid=1"
-				: "select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
-	}
-
-	@Override
-	public String getIdentityColumnString(int type) throws MappingException {
-		return type == Types.BIGINT ?
-				"serial8 not null" :
-				"serial not null";
-	}
-
-	@Override
-	public boolean hasDataTypeInIdentityColumn() {
-		return false;
-	}
-
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder result = new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			result.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		result.append( " constraint " ).append( constraintName );
 
 		return result.toString();
 	}
 
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint primary key constraint " + constraintName + " ";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from informix.systables where tabid=1";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select tabname from informix.systables where tabtype='Q'";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( querySelect.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -268 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == -691 ) {
 				constraintName = extractUsingTemplate(
 						"Missing key in referenced table for referential constraint (",
 						").",
 						sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -692 ) {
 				constraintName = extractUsingTemplate(
 						"Key value for constraint (",
 						") is still being referenced.",
 						sqle.getMessage()
 				);
 			}
 
 			if ( constraintName != null ) {
 				// strip table-owner because Informix always returns constraint names as "<table-owner>.<constraint-name>"
 				final int i = constraintName.indexOf( '.' );
 				if ( i != -1 ) {
 					constraintName = constraintName.substring( i + 1 );
 				}
 			}
 
 			return constraintName;
 		}
 
 	};
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select distinct current timestamp from informix.systables";
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create temp table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "with no log";
 					}
 				},
 				AfterUseAction.CLEAN,
 				null
 		);
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new InformixIdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
index 37be5772f1..65730edb13 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres10Dialect.java
@@ -1,85 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 import java.util.Properties;
 
 import org.hibernate.cfg.Environment;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.Ingres10IdentityColumnSupport;
 
 /**
  * A SQL dialect for Ingres 10 and later versions.
  * <p/>
  * Changes:
  * <ul>
  * <li>Add native BOOLEAN type support</li>
  * <li>Add identity column support</li>
  * </ul>
  *
  * @author Raymond Fan
  */
 public class Ingres10Dialect extends Ingres9Dialect {
 	/**
 	 * Constructs a Ingres10Dialect
 	 */
 	public Ingres10Dialect() {
 		super();
 		registerBooleanSupport();
 		registerDefaultProperties();
 	}
 
 	protected void registerBooleanSupport() {
 		// Boolean type (mapping/BooleanType) mapping maps SQL BIT to Java
 		// Boolean. In order to create a boolean column, BIT needs to be mapped
 		// to boolean as well, similar to H2Dialect.
 		registerColumnType( Types.BIT, "boolean" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 	}
 
 	private void registerDefaultProperties() {
 		// true, false and unknown are now valid values
 		// Remove the query substitutions previously added in IngresDialect.
 		final Properties properties = getDefaultProperties();
 		final String querySubst = properties.getProperty( Environment.QUERY_SUBSTITUTIONS );
 		if ( querySubst != null ) {
 			final String newQuerySubst = querySubst.replace( "true=1,false=0", "" );
 			properties.setProperty( Environment.QUERY_SUBSTITUTIONS, newQuerySubst );
 		}
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "true" : "false";
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new Ingres10IdentityColumnSupport();
 	}
 
-	@Override
-	public boolean hasDataTypeInIdentityColumn() {
-		return true;
-	}
-
-	@Override
-	public String getIdentitySelectString() {
-		return "select last_identity()";
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		return "not null generated by default as identity";
-	}
-
-	@Override
-	public String getIdentityInsertString() {
-		return "default";
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
index b67d8901c5..fbb70e1a21 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
@@ -1,204 +1,206 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.Ingres9IdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for Ingres 9.3 and later versions.
  * <p/>
  * Changes:
  * <ul>
  * <li>Support for the SQL functions current_time, current_timestamp and current_date added</li>
  * <li>Type mapping of <code>Types.TIMESTAMP</code> changed from "timestamp with time zone" to "timestamp(9) with time zone"</li>
  * <li>Improved handling of "SELECT...FOR UPDATE" statements</li>
  * <li>Added support for pooled sequences</li>
  * <li>Added support for SELECT queries with limit and offset</li>
  * <li>Added getIdentitySelectString</li>
  * <li>Modified concatination operator</li>
  * </ul>
  *
  * @author Enrico Schenk
  * @author Raymond Fan
  */
 public class Ingres9Dialect extends IngresDialect {
 
 	private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final String soff = " offset " + selection.getFirstRow();
 			final String slim = " fetch first " + getMaxOrLimit( selection ) + " rows only";
 			final StringBuilder sb = new StringBuilder( sql.length() + soff.length() + slim.length() )
 					.append( sql );
 			if (LimitHelper.hasFirstRow( selection )) {
 				sb.append( soff );
 			}
 			if (LimitHelper.hasMaxRows( selection )) {
 				sb.append( slim );
 			}
 			return sb.toString();
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean supportsVariableLimit() {
 			return false;
 		}
 	};
 
 	/**
 	 * Constructs a Ingres9Dialect
 	 */
 	public Ingres9Dialect() {
 		super();
 		registerDateTimeFunctions();
 		registerDateTimeColumnTypes();
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 	}
 
 	/**
 	 * Register functions current_time, current_timestamp, current_date
 	 */
 	protected void registerDateTimeFunctions() {
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction(
 				"current_timestamp",
 				StandardBasicTypes.TIMESTAMP,
 				false
 		)
 		);
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 	}
 
 	/**
 	 * Register column types date, time, timestamp
 	 */
 	protected void registerDateTimeColumnTypes() {
 		registerColumnType( Types.DATE, "ansidate" );
 		registerColumnType( Types.TIMESTAMP, "timestamp(9) with time zone" );
 	}
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
-	public String getIdentitySelectString() {
-		return "select last_identity()";
-	}
-
-	@Override
 	public String getQuerySequencesString() {
 		return "select seq_name from iisequences";
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select current_timestamp";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "current_timestamp";
 	}
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
 	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		final StringBuilder soff = new StringBuilder( " offset " + offset );
 		final StringBuilder slim = new StringBuilder( " fetch first " + limit + " rows only" );
 		final StringBuilder sb = new StringBuilder( querySelect.length() + soff.length() + slim.length() )
 				.append( querySelect );
 		if ( offset > 0 ) {
 			sb.append( soff );
 		}
 		if ( limit > 0 ) {
 			sb.append( slim );
 		}
 		return sb.toString();
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new Ingres9IdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
index e8610b216d..02458a864b 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/JDataStoreDialect.java
@@ -1,92 +1,83 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.JDataStoreIdentityColumnSupport;
 
 /**
  * A Dialect for JDataStore.
  * 
  * @author Vishy Kasar
  */
 public class JDataStoreDialect extends Dialect {
 	/**
 	 * Creates new JDataStoreDialect
 	 */
 	public JDataStoreDialect() {
 		super();
 
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
 
 		registerColumnType( Types.BLOB, "varbinary" );
 		registerColumnType( Types.CLOB, "varchar" );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentitySelectString() {
-		// NOT_SUPPORTED_SHOULD_USE_JDBC3_PreparedStatement.getGeneratedKeys_method
-		return null;
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		return "autoincrement";
-	}
-
-	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
 
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new JDataStoreIdentityColumnSupport();
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java
index 041f0745ee..706cc778c7 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MimerSQLDialect.java
@@ -1,184 +1,186 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.MimerSQLIdentityColumnSupport;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An Hibernate 3 SQL dialect for Mimer SQL. This dialect requires Mimer SQL 9.2.1 or later
  * because of the mappings to NCLOB, BINARY, and BINARY VARYING.
  *
  * @author Fredrik lund <fredrik.alund@mimer.se>
  */
 @SuppressWarnings("deprecation")
 public class MimerSQLDialect extends Dialect {
 
 	private static final int NATIONAL_CHAR_LENGTH = 2000;
 	private static final int BINARY_MAX_LENGTH = 2000;
 
 	/**
 	 * Even thoug Mimer SQL supports character and binary columns up to 15 000 in lenght,
 	 * this is also the maximum width of the table (exluding LOBs). To avoid breaking the limit all the
 	 * time we limit the length of the character columns to CHAR_MAX_LENTH, NATIONAL_CHAR_LENGTH for national
 	 * characters, and BINARY_MAX_LENGTH for binary types.
 	 */
 	public MimerSQLDialect() {
 		super();
 		registerColumnType( Types.BIT, "ODBC.BIT" );
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.TINYINT, "ODBC.TINYINT" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.CHAR, "NCHAR(1)" );
 		registerColumnType( Types.VARCHAR, NATIONAL_CHAR_LENGTH, "NATIONAL CHARACTER VARYING($l)" );
 		registerColumnType( Types.VARCHAR, "NCLOB($l)" );
 		registerColumnType( Types.LONGVARCHAR, "CLOB($1)" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.VARBINARY, BINARY_MAX_LENGTH, "BINARY VARYING($l)" );
 		registerColumnType( Types.VARBINARY, "BLOB($1)" );
 		registerColumnType( Types.LONGVARBINARY, "BLOB($1)" );
 		registerColumnType( Types.BINARY, BINARY_MAX_LENGTH, "BINARY" );
 		registerColumnType( Types.BINARY, "BLOB($1)" );
 		registerColumnType( Types.NUMERIC, "NUMERIC(19, $l)" );
 		registerColumnType( Types.BLOB, "BLOB($l)" );
 		registerColumnType( Types.CLOB, "NCLOB($l)" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 
 		registerFunction( "dacos", new StandardSQLFunction( "dacos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "acos", new StandardSQLFunction( "dacos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dasin", new StandardSQLFunction( "dasin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "dasin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "datan", new StandardSQLFunction( "datan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "datan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "datan2", new StandardSQLFunction( "datan2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "datan2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dcos", new StandardSQLFunction( "dcos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "dcos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dcot", new StandardSQLFunction( "dcot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "dcot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ddegrees", new StandardSQLFunction( "ddegrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "ddegrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dexp", new StandardSQLFunction( "dexp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "dexp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dlog", new StandardSQLFunction( "dlog", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "dlog", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dlog10", new StandardSQLFunction( "dlog10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "dlog10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dradian", new StandardSQLFunction( "dradian", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radian", new StandardSQLFunction( "dradian", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dsin", new StandardSQLFunction( "dsin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "dsin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "dsqrt", new StandardSQLFunction( "dsqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "dsqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dtan", new StandardSQLFunction( "dtan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "dtan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "dpower", new StandardSQLFunction( "dpower" ) );
 		registerFunction( "power", new StandardSQLFunction( "dpower" ) );
 
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 
 
 		registerFunction( "varchar", new StandardSQLFunction( "varchar", StandardBasicTypes.STRING ) );
 		registerFunction( "real", new StandardSQLFunction( "real", StandardBasicTypes.FLOAT ) );
 		registerFunction( "bigint", new StandardSQLFunction( "bigint", StandardBasicTypes.LONG ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "integer", new StandardSQLFunction( "integer", StandardBasicTypes.INTEGER ) );
 		registerFunction( "smallint", new StandardSQLFunction( "smallint", StandardBasicTypes.SHORT ) );
 
 		registerFunction( "ascii_char", new StandardSQLFunction( "ascii_char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "ascii_code", new StandardSQLFunction( "ascii_code", StandardBasicTypes.STRING ) );
 		registerFunction( "unicode_char", new StandardSQLFunction( "unicode_char", StandardBasicTypes.LONG ) );
 		registerFunction( "unicode_code", new StandardSQLFunction( "unicode_code", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.LONG ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.STRING ) );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, "50" );
 	}
 
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return false;
-	}
-
-	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select next_value of " + sequenceName + " from system.onerow";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create unique sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_schema || '.' || sequence_name from information_schema.ext_sequences";
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new MimerSQLIdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
index 46c74ac851..0bc60bbc11 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
@@ -1,534 +1,525 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.MySQLIdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for MySQL (prior to 5.x).
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class MySQLDialect extends Dialect {
 
 	private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			return sql + (hasOffset ? " limit ?, ?" : " limit ?");
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 	};
 
 	/**
 	 * Constructs a MySQLDialect
 	 */
 	public MySQLDialect() {
 		super();
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.BOOLEAN, "bit" ); // HHH-6935
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "datetime" );
 		registerColumnType( Types.VARBINARY, "longblob" );
 		registerColumnType( Types.VARBINARY, 16777215, "mediumblob" );
 		registerColumnType( Types.VARBINARY, 65535, "blob" );
 		registerColumnType( Types.VARBINARY, 255, "tinyblob" );
 		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "longblob" );
 		registerColumnType( Types.LONGVARBINARY, 16777215, "mediumblob" );
 		registerColumnType( Types.NUMERIC, "decimal($p,$s)" );
 		registerColumnType( Types.BLOB, "longblob" );
 //		registerColumnType( Types.BLOB, 16777215, "mediumblob" );
 //		registerColumnType( Types.BLOB, 65535, "blob" );
 		registerColumnType( Types.CLOB, "longtext" );
 //		registerColumnType( Types.CLOB, 16777215, "mediumtext" );
 //		registerColumnType( Types.CLOB, 65535, "text" );
 		registerVarcharTypes();
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bin", new StandardSQLFunction( "bin", StandardBasicTypes.STRING ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.LONG ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "ord", new StandardSQLFunction( "ord", StandardBasicTypes.INTEGER ) );
 		registerFunction( "quote", new StandardSQLFunction( "quote" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "unhex", new StandardSQLFunction( "unhex", StandardBasicTypes.STRING ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "crc32", new StandardSQLFunction( "crc32", StandardBasicTypes.LONG ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log2", new StandardSQLFunction( "log2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "stddev", new StandardSQLFunction("std", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil", StandardBasicTypes.INTEGER ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 
 		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
 		registerFunction( "timediff", new StandardSQLFunction( "timediff", StandardBasicTypes.TIME ) );
 		registerFunction( "date_format", new StandardSQLFunction( "date_format", StandardBasicTypes.STRING ) );
 
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "from_days", new StandardSQLFunction( "from_days", StandardBasicTypes.DATE ) );
 		registerFunction( "from_unixtime", new StandardSQLFunction( "from_unixtime", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "localtime", new NoArgSQLFunction( "localtime", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "localtimestamp", new NoArgSQLFunction( "localtimestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "microseconds", new StandardSQLFunction( "microseconds", StandardBasicTypes.INTEGER ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sec_to_time", new StandardSQLFunction( "sec_to_time", StandardBasicTypes.TIME ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "time_to_sec", new StandardSQLFunction( "time_to_sec", StandardBasicTypes.INTEGER ) );
 		registerFunction( "to_days", new StandardSQLFunction( "to_days", StandardBasicTypes.LONG ) );
 		registerFunction( "unix_timestamp", new StandardSQLFunction( "unix_timestamp", StandardBasicTypes.LONG ) );
 		registerFunction( "utc_date", new NoArgSQLFunction( "utc_date", StandardBasicTypes.STRING ) );
 		registerFunction( "utc_time", new NoArgSQLFunction( "utc_time", StandardBasicTypes.STRING ) );
 		registerFunction( "utc_timestamp", new NoArgSQLFunction( "utc_timestamp", StandardBasicTypes.STRING ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekday", new StandardSQLFunction( "weekday", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekofyear", new StandardSQLFunction( "weekofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "yearweek", new StandardSQLFunction( "yearweek", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "oct", new StandardSQLFunction( "oct", StandardBasicTypes.STRING ) );
 
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.LONG ) );
 
 		registerFunction( "bit_count", new StandardSQLFunction( "bit_count", StandardBasicTypes.LONG ) );
 		registerFunction( "encrypt", new StandardSQLFunction( "encrypt", StandardBasicTypes.STRING ) );
 		registerFunction( "md5", new StandardSQLFunction( "md5", StandardBasicTypes.STRING ) );
 		registerFunction( "sha1", new StandardSQLFunction( "sha1", StandardBasicTypes.STRING ) );
 		registerFunction( "sha", new StandardSQLFunction( "sha", StandardBasicTypes.STRING ) );
 
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 
 		getDefaultProperties().setProperty( Environment.MAX_FETCH_DEPTH, "2" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
 	protected void registerVarcharTypes() {
 		registerColumnType( Types.VARCHAR, "longtext" );
 //		registerColumnType( Types.VARCHAR, 16777215, "mediumtext" );
 //		registerColumnType( Types.VARCHAR, 65535, "text" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "longtext" );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public String getIdentitySelectString() {
-		return "select last_insert_id()";
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		//starts with 1, implicitly
-		return "not null auto_increment";
-	}
-
-	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final String cols = StringHelper.join( ", ", foreignKey );
 		final String referencedCols = StringHelper.join( ", ", primaryKey );
 		return String.format(
 				" add constraint %s foreign key (%s) references %s (%s)",
 				constraintName,
 				cols,
 				referencedTable,
 				referencedCols
 		);
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getDropForeignKeyString() {
 		return " drop foreign key ";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return sql + (hasOffset ? " limit ?, ?" : " limit ?");
 	}
 
 	@Override
 	public char closeQuote() {
 		return '`';
 	}
 
 	@Override
 	public char openQuote() {
 		return '`';
 	}
 
 	@Override
 	public boolean canCreateCatalog() {
 		return true;
 	}
 
 	@Override
 	public String[] getCreateCatalogCommand(String catalogName) {
 		return new String[] { "create database " + catalogName };
 	}
 
 	@Override
 	public String[] getDropCatalogCommand(String catalogName) {
 		return new String[] { "drop database " + catalogName };
 	}
 
 	@Override
 	public boolean canCreateSchema() {
 		return false;
 	}
 
 	@Override
 	public String[] getCreateSchemaCommand(String schemaName) {
 		throw new UnsupportedOperationException( "MySQL does not support dropping creating/dropping schemas in the JDBC sense" );
 	}
 
 	@Override
 	public String[] getDropSchemaCommand(String schemaName) {
 		throw new UnsupportedOperationException( "MySQL does not support dropping creating/dropping schemas in the JDBC sense" );
 	}
 
 	@Override
 	public boolean supportsIfExistsBeforeTableName() {
 		return true;
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select uuid()";
 	}
 
 	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
 	@Override
 	public String getTableComment(String comment) {
 		return " comment='" + comment + "'";
 	}
 
 	@Override
 	public String getColumnComment(String comment) {
 		return " comment '" + comment + "'";
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create temporary table if not exists";
 					}
 
 					@Override
 					public String getDropIdTableCommand() {
 						return "drop temporary table";
 					}
 				},
 				AfterUseAction.DROP,
 				TempTableDdlTransactionHandling.NONE
 		);
 	}
 
 	@Override
 	public String getCastTypeName(int code) {
 		switch ( code ) {
 			case Types.INTEGER:
 			case Types.BIGINT:
 			case Types.SMALLINT:
 				return smallIntegerCastTarget();
 			case Types.FLOAT:
 			case Types.REAL: {
 				return floatingPointNumberCastTarget();
 			}
 			case Types.NUMERIC:
 				return fixedPointNumberCastTarget();
 			case Types.VARCHAR:
 				return "char";
 			case Types.VARBINARY:
 				return "binary";
 			default:
 				return super.getCastTypeName( code );
 		}
 	}
 
 	/**
 	 * Determine the cast target for {@link Types#INTEGER}, {@link Types#BIGINT} and {@link Types#SMALLINT}
 	 *
 	 * @return The proper cast target type.
 	 */
 	protected String smallIntegerCastTarget() {
 		return "signed";
 	}
 
 	/**
 	 * Determine the cast target for {@link Types#FLOAT} and {@link Types#REAL} (DOUBLE)
 	 *
 	 * @return The proper cast target type.
 	 */
 	protected String floatingPointNumberCastTarget() {
 		// MySQL does not allow casting to DOUBLE nor FLOAT, so we have to cast these as DECIMAL.
 		// MariaDB does allow casting to DOUBLE, although not FLOAT.
 		return fixedPointNumberCastTarget();
 	}
 
 	/**
 	 * Determine the cast target for {@link Types#NUMERIC}
 	 *
 	 * @return The proper cast target type.
 	 */
 	protected String fixedPointNumberCastTarget() {
 		// NOTE : the precision/scale are somewhat arbitrary choices, but MySQL/MariaDB
 		// effectively require *some* values
 		return "decimal(" + Column.DEFAULT_PRECISION + "," + Column.DEFAULT_SCALE + ")";
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
 		return ps.getResultSet();
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 
 	@Override
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder();
 		if ( nulls != NullPrecedence.NONE ) {
 			// Workaround for NULLS FIRST / LAST support.
 			orderByElement.append( "case when " ).append( expression ).append( " is null then " );
 			if ( nulls == NullPrecedence.FIRST ) {
 				orderByElement.append( "0 else 1" );
 			}
 			else {
 				orderByElement.append( "1 else 0" );
 			}
 			orderByElement.append( " end, " );
 		}
 		// Nulls precedence has already been handled so passing NONE value.
 		orderByElement.append( super.renderOrderByElement( expression, collation, order, NullPrecedence.NONE ) );
 		return orderByElement.toString();
 	}
 
 	// locking support
 
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		return " for update";
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		return " lock in share mode";
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		// note: at least my local MySQL 5.1 install shows this not working...
 		return false;
 	}
 
 	@Override
 	public boolean supportsSubqueryOnMutatingTable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		// yes, we do handle "lock timeout" conditions in the exception conversion delegate,
 		// but that's a hardcoded lock timeout period across the whole entire MySQL database.
 		// MySQL does not support specifying lock timeouts as part of the SQL statement, which is really
 		// what this meta method is asking.
 		return false;
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 
 				if ( "41000".equals( sqlState ) ) {
 					return new LockTimeoutException( message, sqlException, sql );
 				}
 
 				if ( "40001".equals( sqlState ) ) {
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public String getNotExpression(String expression) {
 		return "not (" + expression + ")";
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new MySQLIdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java
index 6fd2ce5a97..abe92519f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java
@@ -1,50 +1,34 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.SQL2008StandardLimitHandler;
 
 /**
  * An SQL dialect for Oracle 12c.
  *
  * @author zhouyanming (zhouyanming@gmail.com)
  */
 public class Oracle12cDialect extends Oracle10gDialect {
 	public Oracle12cDialect() {
 		super();
 		getDefaultProperties().setProperty( Environment.BATCH_VERSIONED_DATA, "true" );
 	}
 
 	@Override
 	protected void registerDefaultProperties() {
 		super.registerDefaultProperties();
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "true" );
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
-	public boolean supportsInsertSelectIdentity() {
-		return true;
-	}
-
-	@Override
-	public String getIdentityColumnString() {
-		return "generated as identity";
-	}
-
-	@Override
 	public LimitHandler getLimitHandler() {
 		return SQL2008StandardLimitHandler.INSTANCE;
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
index 7b493aa2ef..623e5b6849 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
@@ -1,565 +1,549 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockOptions;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.PositionSubstringFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.PostgreSQL81IdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
-import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.procedure.internal.PostgresCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 /**
  * An SQL dialect for Postgres
  * <p/>
  * For discussion of BLOB support in Postgres, as of 8.4, have a peek at
  * <a href="http://jdbc.postgresql.org/documentation/84/binary-data.html">http://jdbc.postgresql.org/documentation/84/binary-data.html</a>.
  * For the effects in regards to Hibernate see <a href="http://in.relation.to/15492.lace">http://in.relation.to/15492.lace</a>
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class PostgreSQL81Dialect extends Dialect {
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 	};
 
 	/**
 	 * Constructs a PostgreSQL81Dialect
 	 */
 	public PostgreSQL81Dialect() {
 		super();
 		registerColumnType( Types.BIT, "bool" );
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.SMALLINT, "int2" );
 		registerColumnType( Types.TINYINT, "int2" );
 		registerColumnType( Types.INTEGER, "int4" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float4" );
 		registerColumnType( Types.DOUBLE, "float8" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "bytea" );
 		registerColumnType( Types.BINARY, "bytea" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.LONGVARBINARY, "bytea" );
 		registerColumnType( Types.CLOB, "text" );
 		registerColumnType( Types.BLOB, "oid" );
 		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
 		registerColumnType( Types.OTHER, "uuid" );
 
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cbrt", new StandardSQLFunction("cbrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
 		registerFunction( "degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
 		registerFunction( "rand", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "to_ascii", new StandardSQLFunction("to_ascii") );
 		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", StandardBasicTypes.STRING) );
 		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", StandardBasicTypes.STRING) );
 		registerFunction( "md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING) );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 		registerFunction( "char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG) );
 		registerFunction( "bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG) );
 		registerFunction( "octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG) );
 
 		registerFunction( "age", new StandardSQLFunction("age") );
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIME, false) );
 		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP) );
 		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", StandardBasicTypes.STRING) );
 
 		registerFunction( "current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false) );
 		registerFunction( "session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 		registerFunction( "current_database", new NoArgSQLFunction("current_database", StandardBasicTypes.STRING, true) );
 		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, true) );
 		
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.DATE) );
 		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", StandardBasicTypes.TIMESTAMP) );
 		registerFunction( "to_number", new StandardSQLFunction("to_number", StandardBasicTypes.BIG_DECIMAL) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","||",")" ) );
 
 		registerFunction( "locate", new PositionSubstringFunction() );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		getDefaultProperties().setProperty( Environment.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	@Override
 	public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
 				// Force BLOB binding.  Otherwise, byte[] fields annotated
 				// with @Lob will attempt to use
 				// BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING.  Since the
 				// dialect uses oid for Blobs, byte arrays cannot be used.
 				descriptor = BlobTypeDescriptor.BLOB_BINDING;
 				break;
 			}
 			case Types.CLOB: {
 				descriptor = ClobTypeDescriptor.CLOB_BINDING;
 				break;
 			}
 			default: {
 				descriptor = super.getSqlTypeDescriptorOverride( sqlCode );
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName );
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "nextval ('" + sequenceName + "')";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		//starts with 1, implicitly
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select relname from pg_class where relkind='S'";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
-	
+
 	@Override
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		/*
 		 * Parent's implementation for (aliases, lockOptions) ignores aliases.
 		 */
 		return getForUpdateString(aliases);
 	}
 
 	@Override
-	public String getIdentitySelectString(String table, String column, int type) {
-		return "select currval('" + table + '_' + column + "_seq')";
-	}
-
-	@Override
-	public String getIdentityColumnString(int type) {
-		return type==Types.BIGINT ?
-			"bigserial not null" :
-			"serial not null";
-	}
-
-	@Override
-	public boolean hasDataTypeInIdentityColumn() {
-		return false;
-	}
-
-	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	@Override
 	public String getCaseInsensitiveLike(){
 		return "ilike";
 	}
 
 	@Override
 	public boolean supportsCaseInsensitiveLike() {
 		return true;
 	}
 
 	@Override
 	public Class getNativeIdentifierGeneratorClass() {
 		return SequenceStyleGenerator.class;
 	}
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public boolean useInputStreamToInsertBlob() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	/**
 	 * Workaround for postgres bug #1453
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String typeName = getTypeName( sqlType, 1, 1, 0 );
 		//trim off the length/precision/scale
 		final int loc = typeName.indexOf( '(' );
 		if ( loc > -1 ) {
 			typeName = typeName.substring( 0, loc );
 		}
 		return "null::" + typeName;
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create temporary table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "on commit drop";
 					}
 				},
 				AfterUseAction.CLEAN,
 				null
 		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
 	@Override
 	public boolean requiresParensForTupleDistinctCounts() {
 		return true;
 	}
 
 	@Override
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "true" : "false";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
 	 * Constraint-name extractor for Postgres constraint violation exceptions.
 	 * Orginally contributed by Denny Bartelt.
 	 */
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			final int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( sqle ) );
 			switch (sqlState) {
 				// CHECK VIOLATION
 				case 23514: return extractUsingTemplate( "violates check constraint \"","\"", sqle.getMessage() );
 				// UNIQUE VIOLATION
 				case 23505: return extractUsingTemplate( "violates unique constraint \"","\"", sqle.getMessage() );
 				// FOREIGN KEY VIOLATION
 				case 23503: return extractUsingTemplate( "violates foreign key constraint \"","\"", sqle.getMessage() );
 				// NOT NULL VIOLATION
 				case 23502: return extractUsingTemplate( "null value in column \"","\" violates not-null constraint", sqle.getMessage() );
 				// TODO: RESTRICT VIOLATION
 				case 23001: return null;
 				// ALL OTHER
 				default: return null;
 			}
 		}
 	};
 	
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 
 				if ( "40P01".equals( sqlState ) ) {
 					// DEADLOCK DETECTED
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				if ( "55P03".equals( sqlState ) ) {
 					// LOCK NOT AVAILABLE
 					return new PessimisticLockException( message, sqlException, sql );
 				}
 
 				// returning null allows other delegates to operate
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		// Register the type of the out param - PostgreSQL uses Types.OTHER
 		statement.registerOutParameter( col++, Types.OTHER );
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	/**
 	 * only necessary for postgre < 7.4  See http://anoncvs.postgresql.org/cvsweb.cgi/pgsql/doc/src/sgml/ref/create_sequence.sgml
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) {
 		return getCreateSequenceString( sequenceName ) + " start " + initialValue + " increment " + incrementSize;
 	}
 	
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for update nowait";
 		}
 		else {
 			return " for update";
 		}
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for share nowait";
 		}
 		else {
 			return " for share";
 		}
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 	
 	@Override
 	public String getForUpdateNowaitString() {
 		return getForUpdateString() + " nowait ";
 	}
 	
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases ) + " nowait ";
 	}
 
 	@Override
 	public CallableStatementSupport getCallableStatementSupport() {
 		return PostgresCallableStatementSupport.INSTANCE;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		if ( position != 1 ) {
 			throw new UnsupportedOperationException( "PostgreSQL only supports REF_CURSOR parameters as the first parameter" );
 		}
 		return (ResultSet) statement.getObject( 1 );
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException( "PostgreSQL only supports accessing REF_CURSOR parameters by name" );
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new PostgreSQL81IdentityColumnSupport();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
index 7374bf3f3b..b619abb49d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
@@ -1,208 +1,204 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.SQLServerIdentityColumnSupport;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.TopLimitHandler;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Microsoft SQL Server 2000
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class SQLServerDialect extends AbstractTransactSQLDialect {
 	private static final int PARAM_LIST_SIZE_LIMIT = 2100;
 
 	private final LimitHandler limitHandler;
 
 	/**
 	 * Constructs a SQLServerDialect
 	 */
 	public SQLServerDialect() {
 		registerColumnType( Types.VARBINARY, "image" );
 		registerColumnType( Types.VARBINARY, 8000, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "image" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.BOOLEAN, "bit" );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(second, ?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(minute, ?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(hour, ?1)" ) );
 		registerFunction( "locate", new StandardSQLFunction( "charindex", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(?1, ?3)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1 % ?2" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1) * 8" ) );
 
 		registerFunction( "trim", new AnsiTrimEmulationFunction() );
 
 		registerKeyword( "top" );
 
 		this.limitHandler = new TopLimitHandler( false, false );
 	}
 
 	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	static int getAfterSelectInsertPoint(String sql) {
 		final int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select" );
 		final int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select distinct" );
 		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( getAfterSelectInsertPoint( querySelect ), " top " + limit )
 				.toString();
 	}
 
-	/**
-	 * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
-	 * <p/>
-	 * {@inheritDoc}
-	 */
-	@Override
-	public String appendIdentitySelectToInsert(String insertSQL) {
-		return insertSQL + " select scope_identity()";
-	}
-
 	@Override
 	public LimitHandler getLimitHandler() {
 		return limitHandler;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public char closeQuote() {
 		return ']';
 	}
 
 	@Override
 	public char openQuote() {
 		return '[';
 	}
 
 	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
 		final LockMode mode = lockOptions.getLockMode();
 		switch ( mode ) {
 			case UPGRADE:
 			case UPGRADE_NOWAIT:
 			case PESSIMISTIC_WRITE:
 			case WRITE:
 				return tableName + " with (updlock, rowlock)";
 			case PESSIMISTIC_READ:
 				return tableName + " with (holdlock, rowlock)";
 			case UPGRADE_SKIPLOCKED:
 				return tableName + " with (updlock, rowlock, readpast)";
 			default:
 				return tableName;
 		}
 	}
 
 
 	/**
 	 * The current_timestamp is more accurate, but only known to be supported in SQL Server 7.0 and later and
 	 * Sybase not known to support it at all
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select current_timestamp";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		// SQL Server (at least up through 2005) does not support defining
 		// cascade delete constraints which can circle back to the mutating
 		// table
 		return false;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		// note: at least my local SQL Server 2005 Express shows this not working...
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		// here assume SQLServer2005 using snapshot isolation, which does not have this problem
 		return false;
 	}
 
 	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		// here assume SQLServer2005 using snapshot isolation, which does not have this problem
 		return false;
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.TINYINT ?
 				SmallIntTypeDescriptor.INSTANCE :
 				super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
-	
+
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new SQLServerIdentityColumnSupport();
+	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
index a0478eec2f..730da230c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseAnywhereDialect.java
@@ -1,45 +1,47 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.SybaseAnywhereIdentityColumnSupport;
+
 /**
  * SQL Dialect for Sybase Anywhere
  * extending Sybase (Enterprise) Dialect
  * (Tested on ASA 8.x)
  */
 public class SybaseAnywhereDialect extends SybaseDialect {
 	/**
 	 * Sybase Anywhere syntax would require a "DEFAULT" for each column specified,
 	 * but I suppose Hibernate use this syntax only with tables with just 1 column
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getNoColumnsInsertString() {
 		return "values (default)";
 	}
 
 	/**
 	 * ASA does not require to drop constraint before dropping tables, so disable it.
 	 * <p/>
 	 * NOTE : Also, the DROP statement syntax used by Hibernate to drop constraints is 
 	 * not compatible with ASA.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
-	public boolean supportsInsertSelectIdentity() {
-		return false;
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new SybaseAnywhereIdentityColumnSupport();
 	}
-	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
index 4b6e6287c6..c56b1c6472 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
@@ -1,279 +1,274 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.model.relational.QualifiedNameImpl;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.Teradata14IdentityColumnSupport;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Index;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.tool.schema.internal.StandardIndexExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Map;
 
 /**
  * A dialect for the Teradata database
  */
 public class Teradata14Dialect extends TeradataDialect {
 	/**
 	 * Constructor
 	 */
 	StandardIndexExporter TeraIndexExporter = null;
 
 	public Teradata14Dialect() {
 		super();
 		//registerColumnType data types
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.BINARY, "VARBYTE(100)" );
 		registerColumnType( Types.LONGVARBINARY, "VARBYTE(32000)" );
 		registerColumnType( Types.LONGVARCHAR, "VARCHAR(32000)" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		registerFunction( "current_time", new SQLFunctionTemplate( StandardBasicTypes.TIME, "current_time" ) );
 		registerFunction( "current_date", new SQLFunctionTemplate( StandardBasicTypes.DATE, "current_date" ) );
 
 		TeraIndexExporter =  new TeradataIndexExporter( this );
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "Add";
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * <tt>java.sql.Types</tt> typecode.
 	 *
 	 * @param code <tt>java.sql.Types</tt> typecode
 	 * @param length the length or precision of the column
 	 * @param precision the precision of the column
 	 * @param scale the scale of the column
 	 *
 	 * @return the database type name
 	 *
 	 * @throws HibernateException
 	 */
 	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		/*
 		 * We might want a special case for 19,2. This is very common for money types
 		 * and here it is converted to 18,1
 		 */
 		float f = precision > 0 ? (float) scale / (float) precision : 0;
 		int p = ( precision > 38 ? 38 : precision );
 		int s = ( precision > 38 ? (int) ( 38.0 * f ) : ( scale > 38 ? 38 : scale ) );
 		return super.getTypeName( code, length, p, s );
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	@Override
-	public String getIdentityColumnString() {
-		return "generated by default as identity not null";
-	}
-
-	@Override
-	public String getIdentityInsertString() {
-		return "null";
-	}
-
-	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		statement.registerOutParameter( col, Types.REF );
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement cs) throws SQLException {
 		boolean isResultSet = cs.execute();
 		while ( !isResultSet && cs.getUpdateCount() != -1 ) {
 			isResultSet = cs.getMoreResults();
 		}
 		return cs.getResultSet();
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 
 			int errorCode = sqle.getErrorCode();
 			if ( errorCode == 27003 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == 2700 ) {
 				constraintName = extractUsingTemplate( "Referential constraint", "violation:", sqle.getMessage() );
 			}
 			else if ( errorCode == 5317 ) {
 				constraintName = extractUsingTemplate( "Check constraint (", ") violated.", sqle.getMessage() );
 			}
 
 			if ( constraintName != null ) {
 				int i = constraintName.indexOf( '.' );
 				if ( i != -1 ) {
 					constraintName = constraintName.substring( i + 1 );
 				}
 			}
 			return constraintName;
 		}
 	};
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		String sMsg = " Locking row for write ";
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return sMsg + " nowait ";
 		}
 		return sMsg;
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		String sMsg = " Locking row for read  ";
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return sMsg + " nowait ";
 		}
 		return sMsg;
 	}
 
 	@Override
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString() + " " + sql;
 	}
 
 	@Override
 	public boolean useFollowOnLocking() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 
 	@Override
 	public Exporter<Index> getIndexExporter() {
 		return TeraIndexExporter;
 	}
 
 
 	private class TeradataIndexExporter extends StandardIndexExporter implements Exporter<Index> {
 
 		public TeradataIndexExporter(Dialect dialect) {
 			super(dialect);
 		}
 
 		@Override
 		public String[] getSqlCreateStrings(Index index, Metadata metadata) {
 			final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
 			final String tableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 					index.getTable().getQualifiedTableName(),
 					jdbcEnvironment.getDialect()
 			);
 
 			final String indexNameForCreation;
 			if ( getDialect().qualifyIndexName() ) {
 				indexNameForCreation = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 						new QualifiedNameImpl(
 								index.getTable().getQualifiedTableName().getCatalogName(),
 								index.getTable().getQualifiedTableName().getSchemaName(),
 								jdbcEnvironment.getIdentifierHelper().toIdentifier( index.getName() )
 						),
 						jdbcEnvironment.getDialect()
 				);
 			}
 			else {
 				indexNameForCreation = index.getName();
 			}
 
 			StringBuilder colBuf = new StringBuilder("");
 			boolean first = true;
 			Iterator<Column> columnItr = index.getColumnIterator();
 			while ( columnItr.hasNext() ) {
 				final Column column = columnItr.next();
 				if ( first ) {
 					first = false;
 				}
 				else {
 					colBuf.append( ", " );
 				}
 				colBuf.append( ( column.getQuotedName( jdbcEnvironment.getDialect() )) );
 			}
 			colBuf.append( ")" );
 
 			final StringBuilder buf = new StringBuilder()
 					.append( "create index " )
 					.append( indexNameForCreation )
 					.append(  "(" + colBuf  )
 					.append( " on " )
 					.append( tableName );
 
 			return new String[] { buf.toString() };
 		}
 	}
 
-
+	@Override
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new Teradata14IdentityColumnSupport();
+	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/AbstractTransactSQLIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/AbstractTransactSQLIdentityColumnSupport.java
new file mode 100644
index 0000000000..8245f08320
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/AbstractTransactSQLIdentityColumnSupport.java
@@ -0,0 +1,40 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+import org.hibernate.MappingException;
+
+/**
+ * @author Andrea Boriero
+ */
+public class AbstractTransactSQLIdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) throws MappingException {
+		//starts with 1, implicitly
+		return "identity not null";
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
+		return "select @@identity";
+	}
+
+	@Override
+	public boolean supportsInsertSelectIdentity() {
+		return true;
+	}
+
+	@Override
+	public String appendIdentitySelectToInsert(String insertSQL) {
+		return insertSQL + "\nselect @@identity";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/CUBRIDIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/CUBRIDIdentityColumnSupport.java
new file mode 100644
index 0000000000..8d366dc809
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/CUBRIDIdentityColumnSupport.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class CUBRIDIdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return "NULL";
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "select last_insert_id()";
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		//starts with 1, implicitly
+		return "not null auto_increment";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/Chache71IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Chache71IdentityColumnSupport.java
new file mode 100644
index 0000000000..400982b2ab
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Chache71IdentityColumnSupport.java
@@ -0,0 +1,37 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+import org.hibernate.MappingException;
+
+/**
+ * @author Andrea Boriero
+ */
+public class Chache71IdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public boolean hasDataTypeInIdentityColumn() {
+		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
+		// data type
+		return true;
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) throws MappingException {
+		// The keyword used to specify an identity column, if identity column key generation is supported.
+		return "identity";
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/DB2390IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/DB2390IdentityColumnSupport.java
new file mode 100644
index 0000000000..c7f678ce58
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/DB2390IdentityColumnSupport.java
@@ -0,0 +1,17 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class DB2390IdentityColumnSupport extends DB2IdentityColumnSupport {
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "select identity_val_local() from sysibm.sysdummy1";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/DB2IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/DB2IdentityColumnSupport.java
new file mode 100644
index 0000000000..a8478b2fd1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/DB2IdentityColumnSupport.java
@@ -0,0 +1,32 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class DB2IdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "values identity_val_local()";
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		return "generated by default as identity";
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return "default";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/GetGeneratedKeysDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/GetGeneratedKeysDelegate.java
new file mode 100644
index 0000000000..acb478a612
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/GetGeneratedKeysDelegate.java
@@ -0,0 +1,73 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.id.IdentifierGeneratorHelper;
+import org.hibernate.id.PostInsertIdentityPersister;
+import org.hibernate.id.insert.AbstractReturningDelegate;
+import org.hibernate.id.insert.IdentifierGeneratingInsert;
+import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
+
+/**
+ * Delegate for dealing with IDENTITY columns using JDBC3 getGeneratedKeys
+ *
+ * @author Andrea Boriero
+ */
+public class GetGeneratedKeysDelegate
+		extends AbstractReturningDelegate
+		implements InsertGeneratedIdentifierDelegate {
+	private final PostInsertIdentityPersister persister;
+	private final Dialect dialect;
+
+	public GetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
+		super( persister );
+		this.persister = persister;
+		this.dialect = dialect;
+	}
+
+	@Override
+	public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
+		IdentifierGeneratingInsert insert = new IdentifierGeneratingInsert( dialect );
+		insert.addIdentityColumn( persister.getRootTableKeyColumnNames()[0] );
+		return insert;
+	}
+
+	@Override
+	protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
+		return session
+				.getJdbcCoordinator()
+				.getStatementPreparer()
+				.prepareStatement( insertSQL, PreparedStatement.RETURN_GENERATED_KEYS );
+	}
+
+	@Override
+	public Serializable executeAndExtract(PreparedStatement insert, SessionImplementor session)
+			throws SQLException {
+		session.getJdbcCoordinator().getResultSetReturn().executeUpdate( insert );
+		ResultSet rs = null;
+		try {
+			rs = insert.getGeneratedKeys();
+			return IdentifierGeneratorHelper.getGeneratedIdentity(
+					rs,
+					persister.getRootTableKeyColumnNames()[0],
+					persister.getIdentifierType()
+			);
+		}
+		finally {
+			if ( rs != null ) {
+				session.getJdbcCoordinator().getResourceRegistry().release( rs, insert );
+			}
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/H2IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/H2IdentityColumnSupport.java
new file mode 100644
index 0000000000..a114319e98
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/H2IdentityColumnSupport.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class H2IdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		// not null is implicit
+		return "generated by default as identity";
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "call identity()";
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return "null";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/HSQLIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/HSQLIdentityColumnSupport.java
new file mode 100644
index 0000000000..2f6478695a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/HSQLIdentityColumnSupport.java
@@ -0,0 +1,41 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class HSQLIdentityColumnSupport extends IdentityColumnSupportImpl {
+
+	final private int hsqldbVersion;
+
+	public HSQLIdentityColumnSupport(int hsqldbVersion) {
+
+		this.hsqldbVersion = hsqldbVersion;
+	}
+
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		//not null is implicit
+		return "generated by default as identity (start with 1)";
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "call identity()";
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return hsqldbVersion < 20 ? "null" : "default";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/IdentityColumnSupport.java
new file mode 100644
index 0000000000..c3bf2f2451
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/IdentityColumnSupport.java
@@ -0,0 +1,106 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+import org.hibernate.MappingException;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.id.PostInsertIdentityPersister;
+
+/**
+ * Represents a support for the Dialect identity key generation
+ * 
+ * @author Andrea Boriero
+ *
+ * @since 5.1
+ */
+public interface IdentityColumnSupport {
+	/**
+	 * Does this dialect support identity column key generation?
+	 *
+	 * @return True if IDENTITY columns are supported; false otherwise.
+	 */
+	public boolean supportsIdentityColumns();
+
+	/**
+	 * Does the dialect support some form of inserting and selecting
+	 * the generated IDENTITY value all in the same statement.
+	 *
+	 * @return True if the dialect supports selecting the just
+	 * generated IDENTITY in the insert statement.
+	 */
+	public boolean supportsInsertSelectIdentity();
+
+	/**
+	 * Whether this dialect have an Identity clause added to the data type or a
+	 * completely separate identity data type
+	 *
+	 * @return boolean
+	 */
+	public boolean hasDataTypeInIdentityColumn();
+
+	/**
+	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
+	 * "select identity" clause to the  insert statement.
+	 * <p/>
+	 * Note, if {@link #supportsInsertSelectIdentity} == false then
+	 * the insert-string should be returned without modification.
+	 *
+	 * @param insertString The insert command
+	 *
+	 * @return The insert command with any necessary identity select
+	 * clause attached.
+	 */
+	public String appendIdentitySelectToInsert(String insertString);
+
+	/**
+	 * Get the select command to use to retrieve the last generated IDENTITY
+	 * value for a particular table
+	 *
+	 * @param table The table into which the insert was done
+	 * @param column The PK column.
+	 * @param type The {@link java.sql.Types} type code.
+	 *
+	 * @return The appropriate select command
+	 *
+	 * @throws MappingException If IDENTITY generation is not supported.
+	 */
+	public String getIdentitySelectString(String table, String column, int type) throws MappingException;
+
+
+	/**
+	 * The syntax used during DDL to define a column as being an IDENTITY of
+	 * a particular type.
+	 *
+	 * @param type The {@link java.sql.Types} type code.
+	 *
+	 * @return The appropriate DDL fragment.
+	 *
+	 * @throws MappingException If IDENTITY generation is not supported.
+	 */
+	public String getIdentityColumnString(int type) throws MappingException;
+
+
+	/**
+	 * The keyword used to insert a generated value into an identity column (or null).
+	 * Need if the dialect does not support inserts that specify no column values.
+	 *
+	 * @return The appropriate keyword.
+	 */
+	public String getIdentityInsertString();
+
+	/**
+	 * The Delegate for dealing with IDENTITY columns using JDBC3 getGeneratedKeys
+	 *
+	 * @param persister The persister
+	 * @param dialect The dialect against which to generate the delegate
+	 *
+	 * @return the dialect specific GetGeneratedKeys delegate
+	 */
+	public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(
+			PostInsertIdentityPersister persister,
+			Dialect dialect);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/IdentityColumnSupportImpl.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/IdentityColumnSupportImpl.java
new file mode 100644
index 0000000000..ba27a1151e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/IdentityColumnSupportImpl.java
@@ -0,0 +1,59 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+import org.hibernate.MappingException;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.id.PostInsertIdentityPersister;
+
+/**
+ * @author Andrea Boriero
+ */
+public class IdentityColumnSupportImpl implements IdentityColumnSupport {
+
+	@Override
+	public boolean supportsIdentityColumns() {
+		return false;
+	}
+
+	@Override
+	public boolean supportsInsertSelectIdentity() {
+		return false;
+	}
+
+	@Override
+	public boolean hasDataTypeInIdentityColumn() {
+		return true;
+	}
+
+	@Override
+	public String appendIdentitySelectToInsert(String insertString) {
+		return insertString;
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
+		throw new MappingException( getClass().getName() + " does not support identity key generation" );
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) throws MappingException {
+		throw new MappingException( getClass().getName() + " does not support identity key generation" );
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return null;
+	}
+
+	@Override
+	public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(
+			PostInsertIdentityPersister persister,
+			Dialect dialect) {
+		return new GetGeneratedKeysDelegate( persister, dialect );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/InformixIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/InformixIdentityColumnSupport.java
new file mode 100644
index 0000000000..1b75f32223
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/InformixIdentityColumnSupport.java
@@ -0,0 +1,41 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+import java.sql.Types;
+
+import org.hibernate.MappingException;
+
+/**
+ * @author Andrea Boriero
+ */
+public class InformixIdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type)
+			throws MappingException {
+		return type == Types.BIGINT
+				? "select dbinfo('serial8') from informix.systables where tabid=1"
+				: "select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) throws MappingException {
+		return type == Types.BIGINT ?
+				"serial8 not null" :
+				"serial not null";
+	}
+
+	@Override
+	public boolean hasDataTypeInIdentityColumn() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/Ingres10IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Ingres10IdentityColumnSupport.java
new file mode 100644
index 0000000000..387b53b82c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Ingres10IdentityColumnSupport.java
@@ -0,0 +1,32 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class Ingres10IdentityColumnSupport extends Ingres9IdentityColumnSupport {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public boolean hasDataTypeInIdentityColumn() {
+		return true;
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		return "not null generated by default as identity";
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return "default";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/Ingres9IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Ingres9IdentityColumnSupport.java
new file mode 100644
index 0000000000..c46b06071f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Ingres9IdentityColumnSupport.java
@@ -0,0 +1,17 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class Ingres9IdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "select last_identity()";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/JDataStoreIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/JDataStoreIdentityColumnSupport.java
new file mode 100644
index 0000000000..a932f6eb9c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/JDataStoreIdentityColumnSupport.java
@@ -0,0 +1,28 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class JDataStoreIdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		// NOT_SUPPORTED_SHOULD_USE_JDBC3_PreparedStatement.getGeneratedKeys_method
+		return null;
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		return "autoincrement";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/MimerSQLIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/MimerSQLIdentityColumnSupport.java
new file mode 100644
index 0000000000..8f374805f5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/MimerSQLIdentityColumnSupport.java
@@ -0,0 +1,17 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class MimerSQLIdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/MySQLIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/MySQLIdentityColumnSupport.java
new file mode 100644
index 0000000000..9ff2d2a351
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/MySQLIdentityColumnSupport.java
@@ -0,0 +1,28 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class MySQLIdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "select last_insert_id()";
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		//starts with 1, implicitly
+		return "not null auto_increment";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/Oracle12cIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Oracle12cIdentityColumnSupport.java
new file mode 100644
index 0000000000..4166459a2e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Oracle12cIdentityColumnSupport.java
@@ -0,0 +1,27 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class Oracle12cIdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public boolean supportsInsertSelectIdentity() {
+		return true;
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		return "generated as identity";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/PostgreSQL81IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/PostgreSQL81IdentityColumnSupport.java
new file mode 100644
index 0000000000..99b0490770
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/PostgreSQL81IdentityColumnSupport.java
@@ -0,0 +1,36 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+import java.sql.Types;
+
+/**
+ * @author Andrea Boriero
+ */
+public class PostgreSQL81IdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	@Override
+	public String getIdentitySelectString(String table, String column, int type) {
+		return "select currval('" + table + '_' + column + "_seq')";
+	}
+
+	@Override
+	public String getIdentityColumnString(int type) {
+		return type == Types.BIGINT ?
+				"bigserial not null" :
+				"serial not null";
+	}
+
+	@Override
+	public boolean hasDataTypeInIdentityColumn() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/SQLServerIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/SQLServerIdentityColumnSupport.java
new file mode 100644
index 0000000000..dfaa0dddfa
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/SQLServerIdentityColumnSupport.java
@@ -0,0 +1,22 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class SQLServerIdentityColumnSupport extends AbstractTransactSQLIdentityColumnSupport {
+	/**
+	 * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
+	 * <p/>
+	 * {@inheritDoc}
+	 */
+	@Override
+	public String appendIdentitySelectToInsert(String insertSQL) {
+		return insertSQL + " select scope_identity()";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/SybaseAnywhereIdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/SybaseAnywhereIdentityColumnSupport.java
new file mode 100644
index 0000000000..2c6a81af44
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/SybaseAnywhereIdentityColumnSupport.java
@@ -0,0 +1,17 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class SybaseAnywhereIdentityColumnSupport extends AbstractTransactSQLIdentityColumnSupport {
+	@Override
+	public boolean supportsInsertSelectIdentity() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/identity/Teradata14IdentityColumnSupport.java b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Teradata14IdentityColumnSupport.java
new file mode 100644
index 0000000000..c0d6c4483e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/identity/Teradata14IdentityColumnSupport.java
@@ -0,0 +1,22 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.identity;
+
+/**
+ * @author Andrea Boriero
+ */
+public class Teradata14IdentityColumnSupport extends IdentityColumnSupportImpl {
+	@Override
+	public String getIdentityColumnString(int type) {
+		return "generated by default as identity not null";
+	}
+
+	@Override
+	public String getIdentityInsertString() {
+		return "null";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
index 0a12f9aaf6..973f330309 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
@@ -1,195 +1,146 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.id;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.insert.AbstractReturningDelegate;
 import org.hibernate.id.insert.AbstractSelectingDelegate;
 import org.hibernate.id.insert.IdentifierGeneratingInsert;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.id.insert.InsertSelectIdentityInsert;
 
 /**
  * A generator for use with ANSI-SQL IDENTITY columns used as the primary key.
  * The IdentityGenerator for autoincrement/identity key generation.
  * <br><br>
  * Indicates to the <tt>Session</tt> that identity (ie. identity/autoincrement
  * column) key generation should be used.
  *
  * @author Christoph Sturm
  */
 public class IdentityGenerator extends AbstractPostInsertGenerator {
 
 	@Override
 	public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(
 			PostInsertIdentityPersister persister,
 			Dialect dialect,
 			boolean isGetGeneratedKeysEnabled) throws HibernateException {
 		if ( isGetGeneratedKeysEnabled ) {
-			return new GetGeneratedKeysDelegate( persister, dialect );
+			return dialect.getIdentityColumnSupport().buildGetGeneratedKeysDelegate( persister, dialect );
 		}
-		else if ( dialect.supportsInsertSelectIdentity() ) {
+		else if ( dialect.getIdentityColumnSupport().supportsInsertSelectIdentity() ) {
 			return new InsertSelectDelegate( persister, dialect );
 		}
 		else {
 			return new BasicDelegate( persister, dialect );
 		}
 	}
 
-	/**
-	 * Delegate for dealing with IDENTITY columns using JDBC3 getGeneratedKeys
-	 */
-	public static class GetGeneratedKeysDelegate
-			extends AbstractReturningDelegate
-			implements InsertGeneratedIdentifierDelegate {
-		private final PostInsertIdentityPersister persister;
-		private final Dialect dialect;
-
-		public GetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
-			super( persister );
-			this.persister = persister;
-			this.dialect = dialect;
-		}
-
-		@Override
-		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
-			IdentifierGeneratingInsert insert = new IdentifierGeneratingInsert( dialect );
-			insert.addIdentityColumn( persister.getRootTableKeyColumnNames()[0] );
-			return insert;
-		}
 
-		@Override
-		protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
-			return session
-					.getJdbcCoordinator()
-					.getStatementPreparer()
-					.prepareStatement( insertSQL, PreparedStatement.RETURN_GENERATED_KEYS );
-		}
-
-		@Override
-		public Serializable executeAndExtract(PreparedStatement insert, SessionImplementor session)
-				throws SQLException {
-			session.getJdbcCoordinator().getResultSetReturn().executeUpdate( insert );
-			ResultSet rs = null;
-			try {
-				rs = insert.getGeneratedKeys();
-				return IdentifierGeneratorHelper.getGeneratedIdentity(
-						rs,
-						persister.getRootTableKeyColumnNames()[0],
-						persister.getIdentifierType()
-				);
-			}
-			finally {
-				if ( rs != null ) {
-					session.getJdbcCoordinator().getResourceRegistry().release( rs, insert );
-				}
-			}
-		}
-	}
 
 	/**
 	 * Delegate for dealing with IDENTITY columns where the dialect supports returning
 	 * the generated IDENTITY value directly from the insert statement.
 	 */
 	public static class InsertSelectDelegate
 			extends AbstractReturningDelegate
 			implements InsertGeneratedIdentifierDelegate {
 		private final PostInsertIdentityPersister persister;
 		private final Dialect dialect;
 
 		public InsertSelectDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
 			super( persister );
 			this.persister = persister;
 			this.dialect = dialect;
 		}
 
 		@Override
 		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
 			InsertSelectIdentityInsert insert = new InsertSelectIdentityInsert( dialect );
 			insert.addIdentityColumn( persister.getRootTableKeyColumnNames()[0] );
 			return insert;
 		}
 
 		@Override
 		protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
 			return session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( insertSQL, PreparedStatement.NO_GENERATED_KEYS );
 		}
 
 		@Override
 		public Serializable executeAndExtract(PreparedStatement insert, SessionImplementor session)
 				throws SQLException {
 			ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().execute( insert );
 			try {
 				return IdentifierGeneratorHelper.getGeneratedIdentity(
 						rs,
 						persister.getRootTableKeyColumnNames()[0],
 						persister.getIdentifierType()
 				);
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( rs, insert );
 			}
 		}
 
 		public Serializable determineGeneratedIdentifier(SessionImplementor session, Object entity) {
 			throw new AssertionFailure( "insert statement returns generated value" );
 		}
 	}
 
 	/**
 	 * Delegate for dealing with IDENTITY columns where the dialect requires an
 	 * additional command execution to retrieve the generated IDENTITY value
 	 */
 	public static class BasicDelegate
 			extends AbstractSelectingDelegate
 			implements InsertGeneratedIdentifierDelegate {
 		private final PostInsertIdentityPersister persister;
 		private final Dialect dialect;
 
 		public BasicDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
 			super( persister );
 			this.persister = persister;
 			this.dialect = dialect;
 		}
 
 		@Override
 		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
 			IdentifierGeneratingInsert insert = new IdentifierGeneratingInsert( dialect );
 			insert.addIdentityColumn( persister.getRootTableKeyColumnNames()[0] );
 			return insert;
 		}
 
 		@Override
 		protected String getSelectSQL() {
 			return persister.getIdentitySelectString();
 		}
 
 		@Override
 		protected Serializable getResult(
 				SessionImplementor session,
 				ResultSet rs,
 				Object object) throws SQLException {
 			return IdentifierGeneratorHelper.getGeneratedIdentity(
 					rs,
 					persister.getRootTableKeyColumnNames()[0],
 					persister.getIdentifierType()
 			);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java b/hibernate-core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java
index 80b9b2f6da..e130e19704 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/insert/InsertSelectIdentityInsert.java
@@ -1,25 +1,25 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.id.insert;
 import org.hibernate.dialect.Dialect;
 
 /**
  * Specialized IdentifierGeneratingInsert which appends the database
  * specific clause which signifies to return generated IDENTITY values
  * to the end of the insert statement.
  * 
  * @author Steve Ebersole
  */
 public class InsertSelectIdentityInsert extends IdentifierGeneratingInsert {
 	public InsertSelectIdentityInsert(Dialect dialect) {
 		super( dialect );
 	}
 
 	public String toStatementString() {
-		return getDialect().appendIdentitySelectToInsert( super.toStatementString() );
+		return getDialect().getIdentityColumnSupport().appendIdentitySelectToInsert( super.toStatementString() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index cfaa0bb5e6..bfa264ee75 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,860 +1,860 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.Exportable;
 import org.hibernate.boot.model.relational.Namespace;
 import org.hibernate.boot.model.relational.QualifiedTableName;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tool.schema.extract.spi.ColumnInformation;
 import org.hibernate.tool.schema.extract.spi.TableInformation;
 
 import org.jboss.logging.Logger;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 @SuppressWarnings("unchecked")
 public class Table implements RelationalModel, Serializable, Exportable {
 	private Identifier catalog;
 	private Identifier schema;
 	private Identifier name;
 
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map<ForeignKeyKey, ForeignKey> foreignKeys = new LinkedHashMap<ForeignKeyKey, ForeignKey>();
 	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private List<String> checkConstraints = new ArrayList<String>();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables;
 	private String comment;
 
 	public Table() {
 	}
 
 	public Table(String name) {
 		setName( name );
 	}
 
 	public Table(
 			Namespace namespace,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(
 			Identifier catalog,
 			Identifier schema,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = catalog;
 		this.schema = schema;
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Namespace namespace, Identifier physicalTableName, String subselect, boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Namespace namespace, String subselect, boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public void setName(String name) {
 		this.name = Identifier.toIdentifier( name );
 	}
 
 	public String getName() {
 		return name == null ? null : name.getText();
 	}
 
 	public Identifier getNameIdentifier() {
 		return name;
 	}
 
 	public String getQuotedName() {
 		return name == null ? null : name.toString();
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return name == null ? null : name.render( dialect );
 	}
 
 	public QualifiedTableName getQualifiedTableName() {
 		return name == null ? null : new QualifiedTableName( catalog, schema, name );
 	}
 
 	public boolean isQuoted() {
 		return name.isQuoted();
 	}
 
 	public void setQuoted(boolean quoted) {
 		if ( quoted == name.isQuoted() ) {
 			return;
 		}
 		this.name = new Identifier( name.getText(), quoted );
 	}
 
 	public void setSchema(String schema) {
 		this.schema = Identifier.toIdentifier( schema );
 	}
 
 	public String getSchema() {
 		return schema == null ? null : schema.getText();
 	}
 
 	public String getQuotedSchema() {
 		return schema == null ? null : schema.toString();
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schema == null ? null : schema.render( dialect );
 	}
 
 	public boolean isSchemaQuoted() {
 		return schema != null && schema.isQuoted();
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = Identifier.toIdentifier( catalog );
 	}
 
 	public String getCatalog() {
 		return catalog == null ? null : catalog.getText();
 	}
 
 	public String getQuotedCatalog() {
 		return catalog == null ? null : catalog.render();
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalog == null ? null : catalog.render( dialect );
 	}
 
 	public boolean isCatalogQuoted() {
 		return catalog != null && catalog.isQuoted();
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(Identifier name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		return (Column) columns.get( name.getCanonicalName() );
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
 	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Map<ForeignKeyKey, ForeignKey> getForeignKeys() {
 		return Collections.unmodifiableMap( foreignKeys );
 	}
 
 	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
 		result = prime * result + ((name == null) ? 0 : name.hashCode());
 		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return Identifier.areEqual( name, table.name )
 				&& Identifier.areEqual( schema, table.schema )
 				&& Identifier.areEqual( catalog, table.catalog );
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase(Locale.ROOT)
 						.startsWith( columnInfo.getTypeName().toLowerCase(Locale.ROOT) )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase(Locale.ROOT) +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(
 			Dialect dialect,
 			Mapping p,
 			TableInformation tableInfo,
 			String defaultCatalog,
 			String defaultSchema) throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		
 		while ( iter.hasNext() ) {
 			final Column column = (Column) iter.next();
 			final ColumnInformation columnInfo = tableInfo.getColumn( Identifier.toIdentifier( column.getName(), column.isQuoted() ) );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
 					String keyName = Constraint.generateName( "UK_", this, column );
 					UniqueKey uk = getOrCreateUniqueKey( keyName );
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
 							.getColumnDefinitionUniquenessFragment( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				alter.append( dialect.getAddColumnSuffixString() );
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		if ( results.isEmpty() ) {
 			Logger.getLogger( SchemaUpdate.class ).debugf( "No alter strings for table : %s", getQuotedName() );
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
-				if ( dialect.hasDataTypeInIdentityColumn() ) {
+				if ( dialect.getIdentityColumnSupport().hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
-						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( p ) ) );
+						.append( dialect.getIdentityColumnSupport().getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
 				String keyName = Constraint.generateName( "UK_", this, col );
 				UniqueKey uk = getOrCreateUniqueKey( keyName );
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
 						.getColumnDefinitionUniquenessFragment( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		buf.append( dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			for ( String checkConstraint : checkConstraints ) {
 				buf.append( ", check (" )
 						.append( checkConstraint )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return dialect.getDropTableString( getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = Constraint.generateName( "UK_", this, keyColumns );
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
 		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
 	}
 
 	public ForeignKey createForeignKey(
 			String keyName,
 			List keyColumns,
 			String referencedEntityName,
 			List referencedColumns) {
 		final ForeignKeyKey key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			fk.setTable( this );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 
 			// NOTE : if the name is null, we will generate an implicit name during second pass processing
 			// after we know the referenced table name (which might not be resolved yet).
 			fk.setName( keyName );
 
 			foreignKeys.put( key, fk );
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() ).append( "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() ).append( "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator<String> getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				comments.add( "comment on table " + tableName + " is '" + comment + "'" );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					comments.add( "comment on column " + tableName + '.' + column.getQuotedName( dialect ) + " is '" + columnComment + "'" );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return Table.qualify(
 				render( catalog ),
 				render( schema ),
 				name.render()
 		);
 	}
 
 	private String render(Identifier identifier) {
 		return identifier == null ? null : identifier.render();
 	}
 
 
 	public static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk.columns.equals( columns ) &&
 					fkk.referencedClassName.equals( referencedClassName ) && fkk.referencedColumns
 					.equals( referencedColumns );
 		}
 
 		@Override
 		public String toString() {
 			return "ForeignKeyKey{" +
 					"columns=" + StringHelper.join( ",", columns ) +
 					", referencedClassName='" + referencedClassName + '\'' +
 					", referencedColumns=" + StringHelper.join( ",", referencedColumns ) +
 					'}';
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index e51df7a861..499d1f9092 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1437,2303 +1437,2304 @@ public abstract class AbstractEntityPersister
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 				.append(
 						StringHelper.join(
 								"=? and ",
 								aliasedIdColumns
 						)
 				)
 				.append( "=?" )
 				.append( whereJoinFragment( getRootAlias(), true, false ) )
 				.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.trace(
 					"Forcing version increment [" + MessageHelper.infoString( this, id, getFactory() ) + "; "
 							+ getVersionType().toLoggableString( currentVersion, getFactory() ) + " -> "
 							+ getVersionType().toLoggableString( nextVersion, getFactory() ) + "]"
 			);
 		}
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException sqle) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 							MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting version: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 	}
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
 		lockers.put( LockMode.UPGRADE_SKIPLOCKED, generateLocker( LockMode.UPGRADE_SKIPLOCKED ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return (LockingStrategy) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 			Object version,
 			Object object,
 			LockMode lockMode,
 			SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 
 	public void lock(
 			Serializable id,
 			Object version,
 			Object object,
 			LockOptions lockOptions,
 			SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root( propertyPath );
 		Type type = propertyMapping.toType( rootPropertyName );
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = (AssociationType) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf(
 				getSubclassPropertyNameClosure(),
 				rootPropertyName
 		); //TODO: optimize this better!
 		return index == -1 ? 0 : getSubclassPropertyTableNumber( index );
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	public static String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuilder buf = new StringBuilder().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf( subclassPropertyNameClosure, propertyName );
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
 	@Override
 	public String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
 	@Override
 	public int[] resolveAttributeIndexes(String[] attributeNames) {
 		if ( attributeNames == null || attributeNames.length == 0 ) {
 			return new int[0];
 		}
 		int[] fields = new int[attributeNames.length];
 		int counter = 0;
 
 		// We sort to get rid of duplicates
 		Arrays.sort( attributeNames );
 
 		Integer index0 = entityMetamodel.getPropertyIndexOrNull( attributeNames[0] );
 		if ( index0 != null ) {
 			fields[counter++] = index0;
 		}
 
 		for ( int i = 0, j = 1; j < attributeNames.length; ++i, ++j ) {
 			if ( !attributeNames[i].equals( attributeNames[j] ) ) {
 				Integer index = entityMetamodel.getPropertyIndexOrNull( attributeNames[j] );
 				if ( index != null ) {
 					fields[counter++] = index;
 				}
 			}
 		}
 
 		return Arrays.copyOf( fields, counter );
 	}
 
 	protected String[] getSubclassPropertySubclassNameClosure() {
 		return subclassPropertySubclassNameClosure;
 	}
 
 	protected String[] getSubclassColumnClosure() {
 		return subclassColumnClosure;
 	}
 
 	protected String[] getSubclassColumnAliasClosure() {
 		return subclassColumnAliasClosure;
 	}
 
 	public String[] getSubclassColumnReaderTemplateClosure() {
 		return subclassColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaClosure() {
 		return subclassFormulaClosure;
 	}
 
 	protected String[] getSubclassFormulaTemplateClosure() {
 		return subclassFormulaTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaAliasClosure() {
 		return subclassFormulaAliasClosure;
 	}
 
 	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
 		String[] rawAliases = (String[]) subclassPropertyAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String[] result = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	public String[] getSubclassPropertyColumnNames(String propertyName) {
 		//TODO: should we allow suffixes on these ?
 		return (String[]) subclassPropertyColumnNames.get( propertyName );
 	}
 
 
 	//This is really ugly, but necessary:
 
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
 
 		// ALIASES
 		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( !entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = (CompositeType) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] {idAliases[i]}
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] {idColumnNames[i]}
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] {idAliases[i]}
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] {idColumnNames[i]}
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] {idAliases[i]} );
 					subclassPropertyColumnNames.put( idPropertyNames[i], new String[] {idColumnNames[i]} );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] {getDiscriminatorAlias()} );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] {getDiscriminatorColumnName()} );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = (Property) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
 			if ( prop.isComposite() ) {
 				Component component = (Component) prop.getValue();
 				Iterator compProps = component.getPropertyIterator();
 				internalInitSubclassPropertyAliasesMap( propname, compProps );
 			}
 			else {
 				String[] aliases = new String[prop.getColumnSpan()];
 				String[] cols = new String[prop.getColumnSpan()];
 				Iterator colIter = prop.getColumnIterator();
 				int l = 0;
 				while ( colIter.hasNext() ) {
 					Selectable thing = (Selectable) colIter.next();
 					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
 					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
 					l++;
 				}
 
 				subclassPropertyAliases.put( propname, aliases );
 				subclassPropertyColumnNames.put( propname, cols );
 			}
 		}
 
 	}
 
 	public Object loadByUniqueKey(
 			String propertyName,
 			Object uniqueKey,
 			SessionImplementor session) throws HibernateException {
 		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
 	}
 
 	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
 		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
 				&& propertyName.indexOf( '.' ) < 0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
 
 		if ( useStaticLoader ) {
 			return (EntityLoader) uniqueKeyLoaders.get( propertyName );
 		}
 		else {
 			return createUniqueKeyLoader(
 					propertyMapping.toType( propertyName ),
 					propertyMapping.toColumns( propertyName ),
 					session.getLoadQueryInfluencers()
 			);
 		}
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		return entityMetamodel.getPropertyIndex( propertyName );
 	}
 
 	protected void createUniqueKeyLoaders() throws MappingException {
 		Type[] propertyTypes = getPropertyTypes();
 		String[] propertyNames = getPropertyNames();
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( propertyUniqueness[i] ) {
 				//don't need filters for the static loaders
 				uniqueKeyLoaders.put(
 						propertyNames[i],
 						createUniqueKeyLoader(
 								propertyTypes[i],
 								getPropertyColumnNames( i ),
 								LoadQueryInfluencers.NONE
 						)
 				);
 				//TODO: create uk loaders for component properties
 			}
 		}
 	}
 
 	private EntityLoader createUniqueKeyLoader(
 			Type uniqueKeyType,
 			String[] columns,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		if ( uniqueKeyType.isEntityType() ) {
 			String className = ( (EntityType) uniqueKeyType ).getAssociatedEntityName();
 			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
 		}
 		return new EntityLoader(
 				this,
 				columns,
 				uniqueKeyType,
 				1,
 				LockMode.NONE,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	protected boolean hasWhere() {
 		return sqlWhereString != null;
 	}
 
 	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
 		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
 			propertyMapping.initPropertyPaths(
 					getSubclassPropertyNameClosure()[i],
 					getSubclassPropertyTypeClosure()[i],
 					getSubclassPropertyColumnNameClosure()[i],
 					getSubclassPropertyColumnReaderClosure()[i],
 					getSubclassPropertyColumnReaderTemplateClosure()[i],
 					getSubclassPropertyFormulaTemplateClosure()[i],
 					mapping
 			);
 		}
 	}
 
 	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
 		String idProp = getIdentifierPropertyName();
 		if ( idProp != null ) {
 			propertyMapping.initPropertyPaths(
 					idProp, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping
 			);
 		}
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			propertyMapping.initPropertyPaths(
 					null, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping
 			);
 		}
 		if ( !entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			propertyMapping.initPropertyPaths(
 					ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping
 			);
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths(
 				ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[] {getDiscriminatorColumnName()},
 				new String[] {getDiscriminatorColumnReaders()},
 				new String[] {getDiscriminatorColumnReaderTemplate()},
 				new String[] {getDiscriminatorFormulaTemplate()},
 				getFactory()
 		);
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths( mapping );
 		initOrdinaryPropertyPaths( mapping ); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths( mapping );
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockMode, getFactory(), loadQueryInfluencers );
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockOptions, getFactory(), loadQueryInfluencers );
 	}
 
 	/**
 	 * Used internally to create static loaders.  These are the default set of loaders used to handle get()/load()
 	 * processing.  lock() handling is done by the LockingStrategy instances (see {@link #getLocker})
 	 *
 	 * @param lockMode The lock mode to apply to the thing being loaded.
 	 *
 	 * @return
 	 *
 	 * @throws MappingException
 	 */
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(
 			int rows,
 			Serializable id,
 			int tableNumber,
 			Expectation expectation,
 			PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch (StaleStateException e) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch (TooManyRowsAffectedException e) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 							MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch (Throwable t) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(
 			final boolean[] includeProperty,
 			final int j,
 			final Object[] oldFields,
 			final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[] {rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns(
 						getPropertyColumnNames( i ),
 						propertyColumnUpdateable[i], propertyColumnWriters[i]
 				);
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				update.addColumns(
 						getPropertyColumnNames( i ),
 						propertyColumnUpdateable[i], propertyColumnWriters[i]
 				);
 				hasColumns = true;
 			}
 		}
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 					?
 					getPropertyUpdateability()
 					//optimistic-lock="all", include all updatable properties
 					:
 					includeProperty;             //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
 		return includeProperty[getVersionProperty()]
 				|| entityMetamodel.isVersionGenerated();
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			// the incoming 'includeProperty' array only accounts for insertable defined at the root level, it
 			// does not account for partially generated composites etc.  We also need to account for generation
 			// values
 			if ( isPropertyOfTable( i, j ) ) {
 				if ( !lobProperties.contains( i ) ) {
 					final InDatabaseValueGenerationStrategy generationStrategy = entityMetamodel.getInDatabaseValueGenerationStrategies()[i];
 					if ( generationStrategy != null && generationStrategy.getGenerationTiming().includesInsert() ) {
 						if ( generationStrategy.referenceColumnsInSql() ) {
 							final String[] values;
 							if ( generationStrategy.getReferencedColumnValues() == null ) {
 								values = propertyColumnWriters[i];
 							}
 							else {
 								final int numberOfColumns = propertyColumnWriters[i].length;
 								values = new String[numberOfColumns];
 								for ( int x = 0; x < numberOfColumns; x++ ) {
 									if ( generationStrategy.getReferencedColumnValues()[x] != null ) {
 										values[x] = generationStrategy.getReferencedColumnValues()[x];
 									}
 									else {
 										values[x] = propertyColumnWriters[i][x];
 									}
 								}
 							}
 							insert.addColumns( getPropertyColumnNames( i ), propertyColumnInsertable[i], values );
 						}
 					}
 					else if ( includeProperty[i] ) {
 						insert.addColumns(
 								getPropertyColumnNames( i ),
 								propertyColumnInsertable[i],
 								propertyColumnWriters[i]
 						);
 					}
 				}
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns(
 						getPropertyColumnNames( i ),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i]
 				);
 			}
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
-			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
+			result = getFactory().getDialect().getIdentityColumnSupport().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 *
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties except lobs
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) && !lobProperties.contains( i ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames( i ), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// HHH-4635 & HHH-8103
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				insert.addColumns( getPropertyColumnNames( i ), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		final Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session,
 			boolean isUpdate) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1, isUpdate );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 			final Object[] fields,
 			final Object rowId,
 			final boolean[] includeProperty,
 			final boolean[][] includeColumns,
 			final int j,
 			final PreparedStatement ps,
 			final SessionImplementor session,
 			int index,
 			boolean isUpdate) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Dehydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 
 		if ( !isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 
 		if ( isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 
 		return index;
 
 	}
 
 	private int dehydrateId(
 			final Serializable id,
 			final Object rowId,
 			final PreparedStatement ps,
 			final SessionImplementor session,
 			int index) throws SQLException {
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			return 1;
 		}
 		else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			return getIdentifierColumnSpan();
 		}
 		return 0;
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 			final Serializable id,
 			final Object object,
 			final Loadable rootLoadable,
 			final String[][] suffixedPropertyColumns,
 			final boolean allProperties,
 			final SessionImplementor session) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Hydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = session.getJdbcCoordinator().getResultSetReturn().extract( sequentialSelect );
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = PropertyAccessStrategyBackRefImpl.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ?
 								propertyColumnAliases[i] :
 								suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				session.getJdbcCoordinator().getResourceRegistry().release( sequentialResultSet, sequentialSelect );
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				session.getJdbcCoordinator().getResourceRegistry().release( sequentialSelect );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
-		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
+		return !useGetGeneratedKeys() && getFactory().getDialect().getIdentityColumnSupport().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSessionFactoryOptions().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException( "no sequential selects" );
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 			final boolean[] notNull,
 			String sql,
 			final Object object,
 			final SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0} (native id)", getEntityName() );
 			if ( isVersioned() ) {
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session, false );
 			}
 
 			public Object getEntity() {
 				return object;
 			}
 		};
 
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
-		return getFactory().getDialect().getIdentitySelectString(
-				getTableName( 0 ),
-				getKeyColumns( 0 )[0],
-				getIdentifierType().sqlTypes( getFactory() )[0]
-		);
+		return getFactory().getDialect().getIdentityColumnSupport()
+				.getIdentitySelectString(
+						getTableName( 0 ),
+						getKeyColumns( 0 )[0],
+						getIdentifierType().sqlTypes( getFactory() )[0]
+				);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getTableName( 0 ) )
 				.addColumns( getKeyColumns( 0 ) )
 				.addCondition( getPropertyColumnNames( propertyName ), "=?" )
 				.toStatementString();
 	}
 
 	private BasicBatchKey inserBatchKey;
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 			final Object[] fields,
 			final boolean[] notNull,
 			final int j,
 			final String sql,
 			final Object object,
 			final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( j == 0 && isVersioned() ) {
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		// TODO : shouldn't inserts be Expectations.NONE?
 		final Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		if ( useBatch && inserBatchKey == null ) {
 			inserBatchKey = new BasicBatchKey(
 					getEntityName() + "#INSERT",
 					expectation
 			);
 		}
 		final boolean callable = isInsertCallable( j );
 
 		try {
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
 				insert = session
 						.getJdbcCoordinator()
 						.getBatch( inserBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				insert = session
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index, false );
 
 				if ( useBatch ) {
 					session.getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome(
 							session.getJdbcCoordinator()
 									.getResultSetReturn()
 									.executeUpdate( insert ), insert, -1
 					);
 				}
 
 			}
 			catch (SQLException e) {
 				if ( useBatch ) {
 					session.getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getJdbcCoordinator().getResourceRegistry().release( insert );
 					session.getJdbcCoordinator().afterStatementExecution();
 				}
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 			);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 			final Object[] fields,
 			final Object[] oldFields,
 			final Object rowId,
 			final boolean[] includeProperty,
 			final int j,
 			final Object oldVersion,
 			final Object object,
 			final String sql,
 			final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update(
 						id,
 						fields,
 						oldFields,
 						rowId,
 						includeProperty,
 						j,
 						oldVersion,
 						object,
 						sql,
 						session
 				);
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
 			final Serializable id,
 			final Object[] fields,
 			final Object[] oldFields,
 			final Object rowId,
 			final boolean[] includeProperty,
 			final int j,
 			final Object oldVersion,
 			final Object object,
 			final String sql,
 			final SessionImplementor session) throws HibernateException {
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion ) {
 				LOG.tracev( "Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()] );
 			}
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index += expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate(
 						id,
 						fields,
 						rowId,
 						includeProperty,
 						propertyColumnUpdateable,
 						j,
 						update,
 						session,
 						index,
 						true
 				);
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 							? getPropertyUpdateability()
 							: includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 							);
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check(
 							session.getJdbcCoordinator().getResultSetReturn().executeUpdate( update ),
 							id,
 							j,
 							expectation,
 							update
 					);
 				}
 
 			}
 			catch (SQLException e) {
 				if ( useBatch ) {
 					session.getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getJdbcCoordinator().getResourceRegistry().release( update );
 					session.getJdbcCoordinator().afterStatementExecution();
 				}
 			}
 
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 			);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion ) {
 				LOG.tracev( "Version: {0}", version );
 			}
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( traceEnabled ) {
 				LOG.tracev( "Delete handled by foreign key constraint: {0}", getTableName( j ) );
 			}
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check(
 							session.getJdbcCoordinator().getResultSetReturn().executeUpdate( delete ),
 							id,
 							j,
 							expectation,
 							delete
 					);
 				}
 
 			}
 			catch (SQLException sqle) {
 				if ( useBatch ) {
 					session.getJdbcCoordinator().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getJdbcCoordinator().getResourceRegistry().release( delete );
 					session.getJdbcCoordinator().afterStatementExecution();
 				}
 			}
 
 		}
 		catch (SQLException sqle) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 							MessageHelper.infoString( this, id, getFactory() ),
 					sql
 			);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 			final Object[] fields,
 			final int[] dirtyFields,
 			final boolean hasDirtyCollection,
 			final Object[] oldFields,
 			final Object oldVersion,
 			final Object object,
 			final Object rowId,
 			final SessionImplementor session) throws HibernateException {
 
 		// apply any pre-update in-memory value generation
 		if ( getEntityMetamodel().hasPreUpdateGeneratedValues() ) {
 			final InMemoryValueGenerationStrategy[] strategies = getEntityMetamodel().getInMemoryValueGenerationStrategies();
 			for ( int i = 0; i < strategies.length; i++ ) {
 				if ( strategies[i] != null && strategies[i].getGenerationTiming().includesUpdate() ) {
 					fields[i] = strategies[i].getValueGenerator().generateValue( (Session) session, object );
 					setPropertyValue( object, i, fields[i] );
 					// todo : probably best to add to dirtyFields if not-null
 				}
 			}
 		}
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && !isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( !isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 				);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 		// apply any pre-insert in-memory value generation
 		preInsertInMemoryValueGeneration( fields, object, session );
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		// apply any pre-insert in-memory value generation
 		preInsertInMemoryValueGeneration( fields, object, session );
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	private void preInsertInMemoryValueGeneration(Object[] fields, Object object, SessionImplementor session) {
 		if ( getEntityMetamodel().hasPreInsertGeneratedValues() ) {
 			final InMemoryValueGenerationStrategy[] strategies = getEntityMetamodel().getInMemoryValueGenerationStrategies();
 			for ( int i = 0; i < strategies.length; i++ ) {
 				if ( strategies[i] != null && strategies[i].getGenerationTiming().includesInsert() ) {
 					fields[i] = strategies[i].getValueGenerator().generateValue( (Session) session, object );
 					setPropertyValue( object, i, fields[i] );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Static SQL for entity: %s", getEntityName() );
 			if ( sqlLazySelectString != null ) {
 				LOG.debugf( " Lazy select: %s", sqlLazySelectString );
 			}
 			if ( sqlVersionSelectString != null ) {
 				LOG.debugf( " Version select: %s", sqlVersionSelectString );
 			}
 			if ( sqlSnapshotSelectString != null ) {
 				LOG.debugf( " Snapshot select: %s", sqlSnapshotSelectString );
 			}
 			for ( int j = 0; j < getTableSpan(); j++ ) {
 				LOG.debugf( " Insert %s: %s", j, getSQLInsertStrings()[j] );
 				LOG.debugf( " Update %s: %s", j, getSQLUpdateStrings()[j] );
 				LOG.debugf( " Delete %s: %s", j, getSQLDeleteStrings()[j] );
 			}
 			if ( sqlIdentityInsertString != null ) {
 				LOG.debugf( " Identity insert: %s", sqlIdentityInsertString );
 			}
 			if ( sqlUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (all fields): %s", sqlUpdateByRowIdString );
 			}
 			if ( sqlLazyUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (non-lazy fields): %s", sqlLazyUpdateByRowIdString );
 			}
 			if ( sqlInsertGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Insert-generated property select: %s", sqlInsertGeneratedValuesSelectString );
 			}
 			if ( sqlUpdateGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Update-generated property select: %s", sqlUpdateGeneratedValuesSelectString );
 			}
 		}
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator( alias ), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator( alias ), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
 	}
 
 	@Override
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin(
 				alias,
 				innerJoin,
 				includeSubclasses,
 				Collections.<String>emptySet()
 		).toFromFragmentString();
 	}
 
 	@Override
 	public String fromJoinFragment(
 			String alias,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toFromFragmentString();
 	}
 
 	@Override
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin(
 				alias,
 				innerJoin,
 				includeSubclasses,
 				Collections.<String>emptySet()
 		).toWhereFragmentString();
 	}
 
 	@Override
 	public String whereJoinFragment(
 			String alias,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(
 			String name,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// IMPL NOTE : all joins join to the pk of the driving table
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() );
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		// IMPL NOTE : notice that we skip the first table; it is the driving table!
 		for ( int j = 1; j < tableSpan; j++ ) {
 			final JoinType joinType = determineSubclassTableJoinType(
 					j,
 					innerJoin,
 					includeSubclasses,
 					treatAsDeclarations
 			);
 
 			if ( joinType != null && joinType != JoinType.NONE ) {
 				join.addJoin(
 						getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						joinType
 				);
 			}
 		}
 		return join;
 	}
 
 	protected JoinType determineSubclassTableJoinType(
 			int subclassTableNumber,
 			boolean canInnerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 
 		if ( isClassOrSuperclassTable( subclassTableNumber ) ) {
 			final boolean shouldInnerJoin = canInnerJoin
 					&& !isInverseTable( subclassTableNumber )
 					&& !isNullableTable( subclassTableNumber );
 			// the table is either this persister's driving table or (one of) its super class persister's driving
 			// tables which can be inner joined as long as the `shouldInnerJoin` condition resolves to true
 			return shouldInnerJoin ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN;
 		}
 
 		// otherwise we have a subclass table and need to look a little deeper...
 
 		// IMPL NOTE : By default includeSubclasses indicates that all subclasses should be joined and that each
 		// subclass ought to be joined by outer-join.  However, TREAT-AS always requires that an inner-join be used
 		// so we give TREAT-AS higher precedence...
 
 		if ( isSubclassTableIndicatedByTreatAsDeclarations( subclassTableNumber, treatAsDeclarations ) ) {
 			return JoinType.INNER_JOIN;
 		}
 
 		if ( includeSubclasses
 				&& !isSubclassTableSequentialSelect( subclassTableNumber )
 				&& !isSubclassTableLazy( subclassTableNumber ) ) {
 			return JoinType.LEFT_OUTER_JOIN;
 		}
 
 		return JoinType.NONE;
 	}
 
 	protected boolean isSubclassTableIndicatedByTreatAsDeclarations(
 			int subclassTableNumber,
 			Set<String> treatAsDeclarations) {
 		return false;
 	}
 
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		// IMPL NOTE : notice that we skip the first table; it is the driving table!
 		for ( int i = 1; i < tableNumbers.length; i++ ) {
 			final int j = tableNumbers[i];
 			jf.addJoin(
 					getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j )
 							? JoinType.LEFT_OUTER_JOIN
 							: JoinType.INNER_JOIN
 			);
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(
 			final int[] subclassColumnNumbers,
 			final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate(
 						subalias,
 						columnReaderTemplates[columnNumber],
 						columnAliases[columnNumber]
 				);
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join(
 				"=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) )
 		) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 			final int[] columnNumbers,
 			final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias(
 				getRootAlias(),
 				drivingTable
 		); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	/**
 	 * Post-construct is a callback for AbstractEntityPersister subclasses to call after they are all done with their
 	 * constructor processing.  It allows AbstractEntityPersister to extend its construction after all subclass-specific
 	 * details have been handled.
 	 *
 	 * @param mapping The mapping
 	 *
 	 * @throws MappingException Indicates a problem accessing the Mapping
 	 */
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths( mapping );
 
 		//doLateInit();
 		prepareEntityIdentifierDefinition();
 	}
 
 	private void doLateInit() {
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/Insert.java b/hibernate-core/src/main/java/org/hibernate/sql/Insert.java
index a2faa2ad01..49d828c92d 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/Insert.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/Insert.java
@@ -1,121 +1,121 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.sql;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.Map;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.type.LiteralType;
 
 /**
  * An SQL <tt>INSERT</tt> statement
  *
  * @author Gavin King
  */
 public class Insert {
 	private Dialect dialect;
 	private String tableName;
 	private String comment;
 	private Map columns = new LinkedHashMap();
 
 	public Insert(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	public Insert setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 	public Insert addColumn(String columnName) {
 		return addColumn(columnName, "?");
 	}
 
 	public Insert addColumns(String[] columnNames) {
 		for ( int i=0; i<columnNames.length; i++ ) {
 			addColumn( columnNames[i] );
 		}
 		return this;
 	}
 
 	public Insert addColumns(String[] columnNames, boolean[] insertable) {
 		for ( int i=0; i<columnNames.length; i++ ) {
 			if ( insertable[i] ) {
 				addColumn( columnNames[i] );
 			}
 		}
 		return this;
 	}
 
 	public Insert addColumns(String[] columnNames, boolean[] insertable, String[] valueExpressions) {
 		for ( int i=0; i<columnNames.length; i++ ) {
 			if ( insertable[i] ) {
 				addColumn( columnNames[i], valueExpressions[i] );
 			}
 		}
 		return this;
 	}
 
 	public Insert addColumn(String columnName, String valueExpression) {
 		columns.put(columnName, valueExpression);
 		return this;
 	}
 
 	public Insert addColumn(String columnName, Object value, LiteralType type) throws Exception {
 		return addColumn( columnName, type.objectToSQLString(value, dialect) );
 	}
 
 	public Insert addIdentityColumn(String columnName) {
-		String value = dialect.getIdentityInsertString();
+		String value = dialect.getIdentityColumnSupport().getIdentityInsertString();
 		if ( value != null ) {
 			addColumn( columnName, value );
 		}
 		return this;
 	}
 
 	public Insert setTableName(String tableName) {
 		this.tableName = tableName;
 		return this;
 	}
 
 	public String toStatementString() {
 		StringBuilder buf = new StringBuilder( columns.size()*15 + tableName.length() + 10 );
 		if ( comment != null ) {
 			buf.append( "/* " ).append( comment ).append( " */ " );
 		}
 		buf.append("insert into ")
 			.append(tableName);
 		if ( columns.size()==0 ) {
 			buf.append(' ').append( dialect.getNoColumnsInsertString() );
 		}
 		else {
 			buf.append(" (");
 			Iterator iter = columns.keySet().iterator();
 			while ( iter.hasNext() ) {
 				buf.append( iter.next() );
 				if ( iter.hasNext() ) {
 					buf.append( ", " );
 				}
 			}
 			buf.append(") values (");
 			iter = columns.values().iterator();
 			while ( iter.hasNext() ) {
 				buf.append( iter.next() );
 				if ( iter.hasNext() ) {
 					buf.append( ", " );
 				}
 			}
 			buf.append(')');
 		}
 		return buf.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardTableExporter.java b/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardTableExporter.java
index 636b8ce13e..e67b7983b4 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardTableExporter.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardTableExporter.java
@@ -1,197 +1,197 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tool.schema.internal;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.QualifiedName;
 import org.hibernate.boot.model.relational.QualifiedNameParser;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.tool.schema.spi.Exporter;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardTableExporter implements Exporter<Table> {
 	protected final Dialect dialect;
 
 	public StandardTableExporter(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	@Override
 	public String[] getSqlCreateStrings(Table table, Metadata metadata) {
 		final QualifiedName tableName = new QualifiedNameParser.NameParts(
 				Identifier.toIdentifier( table.getCatalog(), table.isCatalogQuoted() ),
 				Identifier.toIdentifier( table.getSchema(), table.isSchemaQuoted() ),
 				table.getNameIdentifier()
 		);
 
 		final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
 		StringBuilder buf =
 				new StringBuilder( tableCreateString( table.hasPrimaryKey() ) )
 						.append( ' ' )
 						.append(
 								jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 										tableName,
 										jdbcEnvironment.getDialect()
 								)
 						)
 						.append( " (" );
 
 
 		boolean isPrimaryKeyIdentity = table.hasPrimaryKey()
 				&& table.getIdentifierValue() != null
 				&& table.getIdentifierValue().isIdentityColumn( metadata.getIdentifierGeneratorFactory(), dialect );
 		// this is the much better form moving forward as we move to metamodel
 		//boolean isPrimaryKeyIdentity = hasPrimaryKey
 		//				&& table.getPrimaryKey().getColumnSpan() == 1
 		//				&& table.getPrimaryKey().getColumn( 0 ).isIdentity();
 
 		// Try to find out the name of the primary key in case the dialect needs it to create an identity
 		String pkColName = null;
 		if ( table.hasPrimaryKey() ) {
 			Column pkColumn = (Column) table.getPrimaryKey().getColumns().iterator().next();
 			pkColName = pkColumn.getQuotedName( dialect );
 		}
 
 		final Iterator columnItr = table.getColumnIterator();
 		boolean isFirst = true;
 		while ( columnItr.hasNext() ) {
 			final Column col = (Column) columnItr.next();
 			if ( isFirst ) {
 				isFirst = false;
 			}
 			else {
 				buf.append( ", " );
 			}
 			String colName = col.getQuotedName( dialect );
 
 			buf.append( colName ).append( ' ' );
 
 			if ( isPrimaryKeyIdentity && colName.equals( pkColName ) ) {
 				// to support dialects that have their own identity data type
-				if ( dialect.hasDataTypeInIdentityColumn() ) {
+				if ( dialect.getIdentityColumnSupport().hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, metadata ) );
 				}
 				buf.append( ' ' )
-						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( metadata ) ) );
+						.append( dialect.getIdentityColumnSupport().getIdentityColumnString( col.getSqlTypeCode( metadata ) ) );
 			}
 			else {
 				buf.append( col.getSqlType( dialect, metadata )  );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 
 			if ( col.isUnique() ) {
 				String keyName = Constraint.generateName( "UK_", table, col );
 				UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 				uk.addColumn( col );
 				buf.append(
 						dialect.getUniqueDelegate()
 								.getColumnDefinitionUniquenessFragment( col )
 				);
 			}
 
 			if ( col.getCheckConstraint() != null && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 		}
 		if ( table.hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( table.getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		applyTableCheck( table, buf );
 
 		buf.append( ')' );
 		applyTableTypeString( buf );
 
 		List<String> sqlStrings = new ArrayList<String>();
 		sqlStrings.add( buf.toString() );
 
 		applyComments( table, sqlStrings );
 
 		return sqlStrings.toArray( new String[ sqlStrings.size() ] );
 	}
 
 	protected void applyComments(Table table, List<String> sqlStrings) {
 		if ( table.getComment() != null ) {
 			sqlStrings.add( dialect.getTableComment( table.getComment() ) );
 		}
 	}
 
 	protected void applyTableTypeString(StringBuilder buf) {
 		buf.append( dialect.getTableTypeString() );
 	}
 
 	protected void applyTableCheck(Table table, StringBuilder buf) {
 		if ( dialect.supportsTableCheck() ) {
 			final Iterator<String> checkConstraints = table.getCheckConstraintsIterator();
 			while ( checkConstraints.hasNext() ) {
 				buf.append( ", check (" )
 						.append( checkConstraints.next() )
 						.append( ')' );
 			}
 		}
 	}
 
 	protected String tableCreateString(boolean hasPrimaryKey) {
 		return hasPrimaryKey ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString();
 
 	}
 
 	@Override
 	public String[] getSqlDropStrings(Table table, Metadata metadata) {
 		StringBuilder buf = new StringBuilder( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 
 		final QualifiedName tableName = new QualifiedNameParser.NameParts(
 				Identifier.toIdentifier( table.getCatalog(), table.isCatalogQuoted() ),
 				Identifier.toIdentifier( table.getSchema(), table.isSchemaQuoted() ),
 				table.getNameIdentifier()
 		);
 		final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
 		buf.append( jdbcEnvironment.getQualifiedObjectNameFormatter().format( tableName, jdbcEnvironment.getDialect() ) )
 				.append( dialect.getCascadeConstraintsString() );
 
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 
 		return new String[] { buf.toString() };
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/OracleSeqIdGenDialect.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/OracleSeqIdGenDialect.java
index 4c4ddea886..82699b6572 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/OracleSeqIdGenDialect.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/OracleSeqIdGenDialect.java
@@ -1,20 +1,22 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.annotations.lob;
 
+import org.hibernate.dialect.identity.IdentityColumnSupport;
+import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
 import org.hibernate.dialect.Oracle10gDialect;
-import org.hibernate.id.SequenceIdentityGenerator;
 
 /**
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class OracleSeqIdGenDialect extends Oracle10gDialect {
+
 	@Override
-	public Class<?> getNativeIdentifierGeneratorClass() {
-		return SequenceIdentityGenerator.class;
+	public IdentityColumnSupport getIdentityColumnSupport() {
+		return new IdentityColumnSupportImpl();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java b/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
index 1256603e2c..1526d28e3f 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
@@ -1,168 +1,168 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Container class for different implementation of the {@link DialectCheck} interface.
  *
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 abstract public class DialectChecks {
 	public static class SupportsSequences implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsSequences();
 		}
 	}
 
 	public static class SupportsExpectedLobUsagePattern implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsExpectedLobUsagePattern();
 		}
 	}
 
 	public static class UsesInputStreamToInsertBlob implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.useInputStreamToInsertBlob();
 		}
 	}
 
 	public static class SupportsIdentityColumns implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
-			return dialect.supportsIdentityColumns();
+			return dialect.getIdentityColumnSupport().supportsIdentityColumns();
 		}
 	}
 
 	public static class SupportsColumnCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsColumnCheck();
 		}
 	}
 
 	public static class SupportsEmptyInListCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsEmptyInList();
 		}
 	}
 
 	public static class CaseSensitiveCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.areStringComparisonsCaseInsensitive();
 		}
 	}
 
 	public static class SupportsResultSetPositioningOnForwardOnlyCursorCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsResultSetPositionQueryMethodsOnForwardOnlyCursor();
 		}
 	}
 
 	public static class SupportsCascadeDeleteCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsCascadeDelete();
 		}
 	}
 
 	public static class SupportsCircularCascadeDeleteCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsCircularCascadeDeleteConstraints();
 		}
 	}
 
 	public static class SupportsUnboundedLobLocatorMaterializationCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsExpectedLobUsagePattern() && dialect.supportsUnboundedLobLocatorMaterialization();
 		}
 	}
 
 	public static class SupportSubqueryAsLeftHandSideInPredicate implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsSubselectAsInPredicateLHS();
 		}
 	}
 
 	public static class SupportLimitCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLimit();
 		}
 	}
 
 	public static class SupportLimitAndOffsetCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLimit() && dialect.supportsLimitOffset();
 		}
 	}
 
 	public static class SupportsParametersInInsertSelectCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsParametersInInsertSelect();
 		}
 	}
 
 	public static class HasSelfReferentialForeignKeyBugCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.hasSelfReferentialForeignKeyBug();
 		}
 	}
 
 	public static class SupportsRowValueConstructorSyntaxInInListCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsRowValueConstructorSyntaxInInList();
 		}
 	}
 
 	public static class DoesReadCommittedCauseWritersToBlockReadersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.doesReadCommittedCauseWritersToBlockReaders();
 		}
 	}
 
 	public static class DoesReadCommittedNotCauseWritersToBlockReadersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return ! dialect.doesReadCommittedCauseWritersToBlockReaders();
 		}
 	}
 
 	public static class DoesRepeatableReadCauseReadersToBlockWritersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.doesRepeatableReadCauseReadersToBlockWriters();
 		}
 	}
 
 	public static class DoesRepeatableReadNotCauseReadersToBlockWritersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return ! dialect.doesRepeatableReadCauseReadersToBlockWriters();
 		}
 	}
 
 	public static class SupportsExistsInSelectCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsExistsInSelect();
 		}
 	}
 	
 	public static class SupportsLobValueChangePropogation implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLobValueChangePropogation();
 		}
 	}
 	
 	public static class SupportsLockTimeouts implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLockTimeouts();
 		}
 	}
 
 	public static class DoubleQuoteQuoting implements DialectCheck {
 		@Override
 		public boolean isMatch(Dialect dialect) {
 			return '\"' == dialect.openQuote() && '\"' == dialect.closeQuote();
 		}
 	}
 }
