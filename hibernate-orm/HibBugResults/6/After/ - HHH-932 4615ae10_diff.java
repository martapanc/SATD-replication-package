diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java
index e55f772894..43b16b586c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java
@@ -1,673 +1,701 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 
 import java.io.FilterReader;
 import java.io.Reader;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.ScrollMode;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.function.AnsiTrimFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.jdbc.CharacterStream;
 import org.hibernate.engine.jdbc.ClobImplementer;
 import org.hibernate.engine.jdbc.NClobImplementer;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.SQLGrammarException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An abstract base class for HANA dialects. <br/>
  * <a href="http://help.sap.com/hana/html/sqlmain.html">SAP HANA Reference</a> <br/>
  *
  * NOTE: This dialect is currently configured to create foreign keys with
  * <code>on update cascade</code>.
  *
  * @author Andrew Clemons <andrew.clemons@sap.com>
  */
 public abstract class AbstractHANADialect extends Dialect {
 
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			return new StringBuilder( sql.length() + 20 ).append( sql )
+					.append( hasOffset ? " limit ? offset ?" : " limit ?" ).toString();
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersInReverseOrder() {
+			return true;
+		}
+	};
+
 	private static class CloseSuppressingReader extends FilterReader {
 		protected CloseSuppressingReader(final Reader in) {
 			super( in );
 		}
 
 		@Override
 		public void close() {
 			// do not close
 		}
 	}
 
 	// the ClobTypeDescriptor and NClobTypeDescriptor for HANA are slightly
 	// changed from the standard ones. The HANA JDBC driver currently closes any
 	// stream passed in via
 	// PreparedStatement.setCharacterStream(int,Reader,long)
 	// after the stream has been processed. this causes problems later if we are
 	// using non-contexual lob creation and HANA then closes our StringReader.
 	// see test case LobLocatorTest
 
 	private static final ClobTypeDescriptor HANA_CLOB_STREAM_BINDING = new ClobTypeDescriptor() {
 		/** serial version uid. */
 		private static final long serialVersionUID = -379042275442752102L;
 
 		@Override
 		public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected void doBind(final PreparedStatement st, final X value, final int index,
 						final WrapperOptions options) throws SQLException {
 					final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class,
 							options );
 
 					if ( value instanceof ClobImplementer ) {
 						st.setCharacterStream( index, new CloseSuppressingReader( characterStream.asReader() ),
 								characterStream.getLength() );
 					}
 					else {
 						st.setCharacterStream( index, characterStream.asReader(), characterStream.getLength() );
 					}
 
 				}
 			};
 		}
 	};
 
 	private static final NClobTypeDescriptor HANA_NCLOB_STREAM_BINDING = new NClobTypeDescriptor() {
 		/** serial version uid. */
 		private static final long serialVersionUID = 5651116091681647859L;
 
 		@Override
 		public <X> BasicBinder<X> getNClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected void doBind(final PreparedStatement st, final X value, final int index,
 						final WrapperOptions options) throws SQLException {
 					final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class,
 							options );
 
 					if ( value instanceof NClobImplementer ) {
 						st.setCharacterStream( index, new CloseSuppressingReader( characterStream.asReader() ),
 								characterStream.getLength() );
 					}
 					else {
 						st.setCharacterStream( index, characterStream.asReader(), characterStream.getLength() );
 					}
 
 				}
 			};
 		}
 	};
 
 	public AbstractHANADialect() {
 		super();
 
 		registerColumnType( Types.DECIMAL, "decimal($p, $s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 
 		// varbinary max length 5000
 		registerColumnType( Types.BINARY, 5000, "varbinary($l)" );
 		registerColumnType( Types.VARBINARY, 5000, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, 5000, "varbinary($l)" );
 
 		// for longer values, map to blob
 		registerColumnType( Types.BINARY, "blob" );
 		registerColumnType( Types.VARBINARY, "blob" );
 		registerColumnType( Types.LONGVARBINARY, "blob" );
 
 		registerColumnType( Types.CHAR, "varchar(1)" );
 		registerColumnType( Types.VARCHAR, 5000, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, 5000, "varchar($l)" );
 		registerColumnType( Types.NVARCHAR, 5000, "nvarchar($l)" );
 
 		// for longer values map to clob/nclob
 		registerColumnType( Types.LONGVARCHAR, "clob" );
 		registerColumnType( Types.VARCHAR, "clob" );
 		registerColumnType( Types.NVARCHAR, "nclob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.BOOLEAN, "tinyint" );
 
 		// map bit/tinyint to smallint since tinyint is unsigned on HANA
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 
 		registerHibernateType( Types.NCLOB, StandardBasicTypes.NCLOB.getName() );
 		registerHibernateType( Types.NVARCHAR, StandardBasicTypes.STRING.getName() );
 
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.DATE ) );
 		registerFunction( "to_seconddate", new StandardSQLFunction( "to_seconddate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "to_time", new StandardSQLFunction( "to_time", StandardBasicTypes.TIME ) );
 		registerFunction( "to_timestamp", new StandardSQLFunction( "to_timestamp", StandardBasicTypes.TIMESTAMP ) );
 
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP,
 				false ) );
 		registerFunction( "current_utcdate", new NoArgSQLFunction( "current_utcdate", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_utctime", new NoArgSQLFunction( "current_utctime", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_utctimestamp", new NoArgSQLFunction( "current_utctimestamp",
 				StandardBasicTypes.TIMESTAMP, false ) );
 
 		registerFunction( "add_days", new StandardSQLFunction( "add_days" ) );
 		registerFunction( "add_months", new StandardSQLFunction( "add_months" ) );
 		registerFunction( "add_seconds", new StandardSQLFunction( "add_seconds" ) );
 		registerFunction( "add_years", new StandardSQLFunction( "add_years" ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "days_between", new StandardSQLFunction( "days_between", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "isoweek", new StandardSQLFunction( "isoweek", StandardBasicTypes.STRING ) );
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "localtoutc", new StandardSQLFunction( "localtoutc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "next_day", new StandardSQLFunction( "next_day", StandardBasicTypes.DATE ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP, true ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.STRING ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "seconds_between", new StandardSQLFunction( "seconds_between", StandardBasicTypes.LONG ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekday", new StandardSQLFunction( "weekday", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "utctolocal", new StandardSQLFunction( "utctolocal", StandardBasicTypes.TIMESTAMP ) );
 
 		registerFunction( "to_bigint", new StandardSQLFunction( "to_bigint", StandardBasicTypes.LONG ) );
 		registerFunction( "to_binary", new StandardSQLFunction( "to_binary", StandardBasicTypes.BINARY ) );
 		registerFunction( "to_decimal", new StandardSQLFunction( "to_decimal", StandardBasicTypes.BIG_DECIMAL ) );
 		registerFunction( "to_double", new StandardSQLFunction( "to_double", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "to_int", new StandardSQLFunction( "to_int", StandardBasicTypes.INTEGER ) );
 		registerFunction( "to_integer", new StandardSQLFunction( "to_integer", StandardBasicTypes.INTEGER ) );
 		registerFunction( "to_real", new StandardSQLFunction( "to_real", StandardBasicTypes.FLOAT ) );
 		registerFunction( "to_smalldecimal",
 				new StandardSQLFunction( "to_smalldecimal", StandardBasicTypes.BIG_DECIMAL ) );
 		registerFunction( "to_smallint", new StandardSQLFunction( "to_smallint", StandardBasicTypes.SHORT ) );
 		registerFunction( "to_tinyint", new StandardSQLFunction( "to_tinyint", StandardBasicTypes.BYTE ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bin2hex", new StandardSQLFunction( "bin2hex", StandardBasicTypes.STRING ) );
 		registerFunction( "bitand", new StandardSQLFunction( "bitand", StandardBasicTypes.LONG ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "greatest", new StandardSQLFunction( "greatest" ) );
 		registerFunction( "hex2bin", new StandardSQLFunction( "hex2bin", StandardBasicTypes.BINARY ) );
 		registerFunction( "least", new StandardSQLFunction( "least" ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "power", new StandardSQLFunction( "power" ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "uminus", new StandardSQLFunction( "uminus" ) );
 
 		registerFunction( "to_alphanum", new StandardSQLFunction( "to_alphanum", StandardBasicTypes.STRING ) );
 		registerFunction( "to_nvarchar", new StandardSQLFunction( "to_nvarchar", StandardBasicTypes.STRING ) );
 		registerFunction( "to_varchar", new StandardSQLFunction( "to_varchar", StandardBasicTypes.STRING ) );
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.LONG ) );
 		registerFunction( "locate", new StandardSQLFunction( "locate", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "nchar", new StandardSQLFunction( "nchar", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "substr_after", new StandardSQLFunction( "substr_after", StandardBasicTypes.STRING ) );
 		registerFunction( "substr_before", new StandardSQLFunction( "substr_before", StandardBasicTypes.STRING ) );
 		registerFunction( "substring", new StandardSQLFunction( "substring", StandardBasicTypes.STRING ) );
 		registerFunction( "trim", new AnsiTrimFunction() );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "unicode", new StandardSQLFunction( "unicode", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "length(to_binary(?1))*8" ) );
 
 		registerFunction( "to_blob", new StandardSQLFunction( "to_blob", StandardBasicTypes.BLOB ) );
 		registerFunction( "to_clob", new StandardSQLFunction( "to_clob", StandardBasicTypes.CLOB ) );
 		registerFunction( "to_nclob", new StandardSQLFunction( "to_nclob", StandardBasicTypes.NCLOB ) );
 
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "current_connection", new NoArgSQLFunction( "current_connection", StandardBasicTypes.INTEGER,
 				false ) );
 		registerFunction( "current_schema", new NoArgSQLFunction( "current_schema", StandardBasicTypes.STRING, false ) );
 		registerFunction( "current_user", new NoArgSQLFunction( "current_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "grouping_id", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "(", ",", ")" ) );
 		registerFunction( "ifnull", new StandardSQLFunction( "ifnull" ) );
 		registerFunction( "map", new StandardSQLFunction( "map" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "session_context", new StandardSQLFunction( "session_context" ) );
 		registerFunction( "session_user", new NoArgSQLFunction( "session_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "sysuuid", new NoArgSQLFunction( "sysuuid", StandardBasicTypes.STRING, false ) );
 
 		registerHanaKeywords();
 
 		// createBlob() and createClob() are not supported by the HANA JDBC driver
 		getDefaultProperties().setProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(final SQLException sqlException, final String message, final String sql) {
 
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 				if ( errorCode == 131 ) {
 					// 131 - Transaction rolled back by lock wait timeout
 					return new LockTimeoutException( message, sqlException, sql );
 				}
 
 				if ( errorCode == 146 ) {
 					// 146 - Resource busy and acquire with NOWAIT specified
 					return new LockTimeoutException( message, sqlException, sql );
 				}
 
 				if ( errorCode == 132 ) {
 					// 132 - Transaction rolled back due to unavailable resource
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				if ( errorCode == 133 ) {
 					// 133 - Transaction rolled back by detected deadlock
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				// 259 - Invalid table name
 				// 260 - Invalid column name
 				// 261 - Invalid index name
 				// 262 - Invalid query name
 				// 263 - Invalid alias name
 				if ( errorCode == 257 || ( errorCode >= 259 && errorCode <= 263 ) ) {
 					throw new SQLGrammarException( message, sqlException, sql );
 				}
 
 				// 257 - Cannot insert NULL or update to NULL
 				// 301 - Unique constraint violated
 				// 461 - foreign key constraint violation
 				// 462 - failed on update or delete by foreign key constraint violation
 				if ( errorCode == 287 || errorCode == 301 || errorCode == 461 || errorCode == 462 ) {
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName(
 							sqlException );
 
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add (";
 	}
 
 	@Override
 	public String getAddColumnSuffixString() {
 		return ")";
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
 	@Override
 	public String getCreateSequenceString(final String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select current_timestamp from dummy";
 	}
 
 	@Override
 	public String getDropSequenceString(final String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getForUpdateString(final String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String getForUpdateString(final String aliases, final LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		final Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode> entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan( lockMode ) ) {
 				lockMode = lm;
 			}
 		}
 
 		// not sure why this is sometimes empty
 		if ( aliases == null || "".equals( aliases ) ) {
 			return getForUpdateString( lockMode );
 		}
 
 		return getForUpdateString( lockMode ) + " of " + aliases;
 	}
 
 	@Override
 	public String getLimitString(final String sql, final boolean hasOffset) {
 		return new StringBuilder( sql.length() + 20 ).append( sql )
 				.append( hasOffset ? " limit ? offset ?" : " limit ?" ).toString();
 	}
 
 	@Override
 	public String getNotExpression(final String expression) {
 		return "not (" + expression + ")";
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_name from sys.sequences";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(final String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getSequenceNextValString(final String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dummy";
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(final int sqlCode) {
 		switch ( sqlCode ) {
 		case Types.BOOLEAN:
 			return BitTypeDescriptor.INSTANCE;
 		case Types.CLOB:
 			return HANA_CLOB_STREAM_BINDING;
 		case Types.NCLOB:
 			return HANA_NCLOB_STREAM_BINDING;
 		case Types.TINYINT:
 			// tinyint is unsigned on HANA
 			return SmallIntTypeDescriptor.INSTANCE;
 		default:
 			return super.getSqlTypeDescriptorOverride( sqlCode );
 		}
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	protected void registerHanaKeywords() {
 		registerKeyword( "all" );
 		registerKeyword( "alter" );
 		registerKeyword( "as" );
 		registerKeyword( "before" );
 		registerKeyword( "begin" );
 		registerKeyword( "both" );
 		registerKeyword( "case" );
 		registerKeyword( "char" );
 		registerKeyword( "condition" );
 		registerKeyword( "connect" );
 		registerKeyword( "cross" );
 		registerKeyword( "cube" );
 		registerKeyword( "current_connection" );
 		registerKeyword( "current_date" );
 		registerKeyword( "current_schema" );
 		registerKeyword( "current_time" );
 		registerKeyword( "current_timestamp" );
 		registerKeyword( "current_user" );
 		registerKeyword( "current_utcdate" );
 		registerKeyword( "current_utctime" );
 		registerKeyword( "current_utctimestamp" );
 		registerKeyword( "currval" );
 		registerKeyword( "cursor" );
 		registerKeyword( "declare" );
 		registerKeyword( "distinct" );
 		registerKeyword( "else" );
 		registerKeyword( "elseif" );
 		registerKeyword( "elsif" );
 		registerKeyword( "end" );
 		registerKeyword( "except" );
 		registerKeyword( "exception" );
 		registerKeyword( "exec" );
 		registerKeyword( "for" );
 		registerKeyword( "from" );
 		registerKeyword( "full" );
 		registerKeyword( "group" );
 		registerKeyword( "having" );
 		registerKeyword( "if" );
 		registerKeyword( "in" );
 		registerKeyword( "inner" );
 		registerKeyword( "inout" );
 		registerKeyword( "intersect" );
 		registerKeyword( "into" );
 		registerKeyword( "is" );
 		registerKeyword( "join" );
 		registerKeyword( "leading" );
 		registerKeyword( "left" );
 		registerKeyword( "limit" );
 		registerKeyword( "loop" );
 		registerKeyword( "minus" );
 		registerKeyword( "natural" );
 		registerKeyword( "nextval" );
 		registerKeyword( "null" );
 		registerKeyword( "on" );
 		registerKeyword( "order" );
 		registerKeyword( "out" );
 		registerKeyword( "prior" );
 		registerKeyword( "return" );
 		registerKeyword( "returns" );
 		registerKeyword( "reverse" );
 		registerKeyword( "right" );
 		registerKeyword( "rollup" );
 		registerKeyword( "rowid" );
 		registerKeyword( "select" );
 		registerKeyword( "set" );
 		registerKeyword( "sql" );
 		registerKeyword( "start" );
 		registerKeyword( "sysdate" );
 		registerKeyword( "systime" );
 		registerKeyword( "systimestamp" );
 		registerKeyword( "sysuuid" );
 		registerKeyword( "top" );
 		registerKeyword( "trailing" );
 		registerKeyword( "union" );
 		registerKeyword( "using" );
 		registerKeyword( "utcdate" );
 		registerKeyword( "utctime" );
 		registerKeyword( "utctimestamp" );
 		registerKeyword( "values" );
 		registerKeyword( "when" );
 		registerKeyword( "where" );
 		registerKeyword( "while" );
 		registerKeyword( "with" );
 	}
 
 	@Override
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		// HANA does not support circular constraints
 		return false;
 	}
 
 	@Override
 	public ScrollMode defaultScrollMode() {
 		return ScrollMode.FORWARD_ONLY;
 	}
 
 	/**
 	 * HANA currently does not support check constraints.
 	 */
 	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		// http://scn.sap.com/thread/3221812
 		return false;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return true;
 	}
 
 	@Override
 	public int getMaxAliasLength() {
 		return 128;
 	}
 
 	/**
 	 * The default behaviour for 'on update restrict' on HANA is currently
 	 * to not allow any updates to any column of a row if the row has a 
 	 * foreign key. Make the default for foreign keys have 'on update cascade'
 	 * to work around the issue.
 	 */
 	@Override
 	public String getAddForeignKeyConstraintString(final String constraintName, final String[] foreignKey,
 			final String referencedTable, final String[] primaryKey, final boolean referencesPrimaryKey) {
 		return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey) + " on update cascade";
 	}
+
+	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
index a213582ca8..e3bfd242bd 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/CUBRIDDialect.java
@@ -1,381 +1,381 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.CUBRIDLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.engine.spi.RowSelection;
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
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return "NULL";
 	}
 
 	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "select last_insert_id()";
 	}
 
 	@Override
 	protected String getIdentityColumnString() {
 		//starts with 1, implicitly
 		return "not null auto_increment";
 	}
 
 	@Override
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
-	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
-		return new CUBRIDLimitHandler( this, sql, selection );
+	public LimitHandler getLimitHandler() {
+		return CUBRIDLimitHandler.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index 747fe4b4e2..69926a756a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,711 +1,721 @@
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
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.TopLimitHandler;
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
 
+	private final TopLimitHandler limitHandler;
+
 	/**
 	 * Creates new <code>Cache71Dialect</code> instance. Sets up the JDBC /
 	 * Cach&eacute; type mappings.
 	 */
 	public Cache71Dialect() {
 		super();
 		commonRegistration();
 		register71Functions();
+		this.limitHandler = new TopLimitHandler(true, true);
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
 
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 25 ? name.substring( 1, 25 ) : name;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return Boolean.FALSE;
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public Class getNativeIdentifierGeneratorClass() {
 		return IdentityGenerator.class;
 	}
 
 	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
 		// data type
 		return true;
 	}
 
 	@Override
 	public String getIdentityColumnString() throws MappingException {
 		// The keyword used to specify an identity column, if identity column key generation is supported.
 		return "identity";
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
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
+	public LimitHandler getLimitHandler() {
+		return limitHandler;
+	}
+
+	@Override
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
 		public String extractConstraintName(SQLException sqle) {
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
index d4fdf523a0..1a4783eb15 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
@@ -1,76 +1,112 @@
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
 
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
+
 
 /**
  * An SQL dialect for DB2/390. This class provides support for
  * DB2 Universal Database for OS/390, also known as DB2/390.
  *
  * @author Kristoffer Dyrkorn
  */
 public class DB2390Dialect extends DB2Dialect {
+
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			if (LimitHelper.hasFirstRow( selection )) {
+				throw new UnsupportedOperationException( "query result offset is not supported" );
+			}
+			return sql + " fetch first ? rows only";
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean useMaxForLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	};
+
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "select identity_val_local() from sysibm.sysdummy1";
 	}
 
 	@Override
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
 
+	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
index 5aa87d9d7c..8ce7cab6f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2400Dialect.java
@@ -1,79 +1,115 @@
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
 
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
+
 /**
  * An SQL dialect for DB2/400.  This class provides support for DB2 Universal Database for iSeries,
  * also known as DB2/400.
  *
  * @author Peter DeGregorio (pdegregorio)
  */
 public class DB2400Dialect extends DB2Dialect {
+
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			if (LimitHelper.hasFirstRow( selection )) {
+				throw new UnsupportedOperationException( "query result offset is not supported" );
+			}
+			return sql + " fetch first ? rows only";
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean useMaxForLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	};
+
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "select identity_val_local() from sysibm.sysdummy1";
 	}
 
 	@Override
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
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
 	public String getForUpdateString() {
 		return " for update with rs";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
index f70de19015..8935716c90 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
@@ -1,488 +1,527 @@
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
 
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.unique.DB2UniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
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
+
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			if (LimitHelper.hasFirstRow( selection )) {
+				//nest the main query in an outer select
+				return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
+						+ sql + " fetch first ? rows only ) as inner2_ ) as inner1_ where rownumber_ > "
+						+ "? order by rownumber_";
+			}
+			return sql + " fetch first ? rows only";
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean useMaxForLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	};
+
+
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
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "values identity_val_local()";
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		return "generated by default as identity";
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return "default";
 	}
 
 	@Override
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
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "declare global temporary table";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "not logged";
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		return "session." + super.generateTemporaryTableName( baseTableName );
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
 
+	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
index a79822b2e5..09e5913836 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
@@ -1,260 +1,339 @@
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
 
 import java.lang.reflect.Method;
 import java.sql.Types;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.function.AnsiTrimFunction;
 import org.hibernate.dialect.function.DerbyConcatFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DerbyCaseFragment;
 
 import org.jboss.logging.Logger;
 
 /**
  * Hibernate Dialect for Cloudscape 10 - aka Derby. This implements both an
  * override for the identity column generator as well as for the case statement
  * issue documented at:
  * http://www.jroller.com/comments/kenlars99/Weblog/cloudscape_soon_to_be_derby
  *
  * @author Simon Johnston
  *
  * @deprecated HHH-6073
  */
 @Deprecated
 public class DerbyDialect extends DB2Dialect {
 	@SuppressWarnings("deprecation")
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			DerbyDialect.class.getName()
 	);
 
 	private int driverVersionMajor;
 	private int driverVersionMinor;
+	private final LimitHandler limitHandler;
 
 	/**
 	 * Constructs a DerbyDialect
 	 */
 	@SuppressWarnings("deprecation")
 	public DerbyDialect() {
 		super();
 		if ( this.getClass() == DerbyDialect.class ) {
 			LOG.deprecatedDerbyDialect();
 		}
 
 		registerFunction( "concat", new DerbyConcatFunction() );
 		registerFunction( "trim", new AnsiTrimFunction() );
 		registerColumnType( Types.BLOB, "blob" );
 		determineDriverVersion();
 
 		if ( driverVersionMajor > 10 || ( driverVersionMajor == 10 && driverVersionMinor >= 7 ) ) {
 			registerColumnType( Types.BOOLEAN, "boolean" );
 		}
+
+		this.limitHandler = new DerbyLimitHandler();
 	}
 
 	private void determineDriverVersion() {
 		try {
 			// locate the derby sysinfo class and query its version info
 			final Class sysinfoClass = ReflectHelper.classForName( "org.apache.derby.tools.sysinfo", this.getClass() );
 			final Method majorVersionGetter = sysinfoClass.getMethod( "getMajorVersion", ReflectHelper.NO_PARAM_SIGNATURE );
 			final Method minorVersionGetter = sysinfoClass.getMethod( "getMinorVersion", ReflectHelper.NO_PARAM_SIGNATURE );
 			driverVersionMajor = (Integer) majorVersionGetter.invoke( null, ReflectHelper.NO_PARAMS );
 			driverVersionMinor = (Integer) minorVersionGetter.invoke( null, ReflectHelper.NO_PARAMS );
 		}
 		catch ( Exception e ) {
 			LOG.unableToLoadDerbyDriver( e.getMessage() );
 			driverVersionMajor = -1;
 			driverVersionMinor = -1;
 		}
 	}
 
 	private boolean isTenPointFiveReleaseOrNewer() {
 		return driverVersionMajor > 10 || ( driverVersionMajor == 10 && driverVersionMinor >= 5 );
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DerbyCaseFragment();
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		// technically sequence support was added in 10.6.1.0...
 		//
 		// The problem though is that I am not exactly sure how to differentiate 10.6.1.0 from any other 10.6.x release.
 		//
 		// http://db.apache.org/derby/docs/10.0/publishedapi/org/apache/derby/tools/sysinfo.html seems incorrect.  It
 		// states that derby's versioning scheme is major.minor.maintenance, but obviously 10.6.1.0 has 4 components
 		// to it, not 3.
 		//
 		// Let alone the fact that it states that versions with the matching major.minor are 'feature
 		// compatible' which is clearly not the case here (sequence support is a new feature...)
 		return driverVersionMajor > 10 || ( driverVersionMajor == 10 && driverVersionMinor >= 6 );
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		if ( supportsSequences() ) {
 			return "values next value for " + sequenceName;
 		}
 		else {
 			throw new MappingException( "Derby does not support sequence prior to release 10.6.1.0" );
 		}
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return isTenPointFiveReleaseOrNewer();
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		//HHH-4531
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return isTenPointFiveReleaseOrNewer();
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " for update with rs";
 	}
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		return " for update with rs";
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		return " for read only with rs";
 	}
 
 
+	@Override
+	public LimitHandler getLimitHandler() {
+		return limitHandler;
+	}
+
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * From Derby 10.5 Docs:
 	 * <pre>
 	 * Query
 	 * [ORDER BY clause]
 	 * [result offset clause]
 	 * [fetch first clause]
 	 * [FOR UPDATE clause]
 	 * [WITH {RR|RS|CS|UR}]
 	 * </pre>
 	 */
 	@Override
 	public String getLimitString(String query, final int offset, final int limit) {
 		final StringBuilder sb = new StringBuilder(query.length() + 50);
 		final String normalizedSelect = query.toLowerCase().trim();
 		final int forUpdateIndex = normalizedSelect.lastIndexOf( "for update") ;
 
 		if ( hasForUpdateClause( forUpdateIndex ) ) {
 			sb.append( query.substring( 0, forUpdateIndex-1 ) );
 		}
 		else if ( hasWithClause( normalizedSelect ) ) {
 			sb.append( query.substring( 0, getWithIndex( query ) - 1 ) );
 		}
 		else {
 			sb.append( query );
 		}
 
 		if ( offset == 0 ) {
 			sb.append( " fetch first " );
 		}
 		else {
 			sb.append( " offset " ).append( offset ).append( " rows fetch next " );
 		}
 
 		sb.append( limit ).append( " rows only" );
 
 		if ( hasForUpdateClause( forUpdateIndex ) ) {
 			sb.append( ' ' );
 			sb.append( query.substring( forUpdateIndex ) );
 		}
 		else if ( hasWithClause( normalizedSelect ) ) {
 			sb.append( ' ' ).append( query.substring( getWithIndex( query ) ) );
 		}
 		return sb.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		// we bind the limit and offset values directly into the sql...
 		return false;
 	}
 
 	private boolean hasForUpdateClause(int forUpdateIndex) {
 		return forUpdateIndex >= 0;
 	}
 
 	private boolean hasWithClause(String normalizedSelect){
 		return normalizedSelect.startsWith( "with ", normalizedSelect.length()-7 );
 	}
 
 	private int getWithIndex(String querySelect) {
 		int i = querySelect.lastIndexOf( "with " );
 		if ( i < 0 ) {
 			i = querySelect.lastIndexOf( "WITH " );
 		}
 		return i;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return null ;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
+
+	private final class DerbyLimitHandler extends AbstractLimitHandler {
+		/**
+		 * {@inheritDoc}
+		 * <p/>
+		 * From Derby 10.5 Docs:
+		 * <pre>
+		 * Query
+		 * [ORDER BY clause]
+		 * [result offset clause]
+		 * [fetch first clause]
+		 * [FOR UPDATE clause]
+		 * [WITH {RR|RS|CS|UR}]
+		 * </pre>
+		 */
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final StringBuilder sb = new StringBuilder( sql.length() + 50 );
+			final String normalizedSelect = sql.toLowerCase().trim();
+			final int forUpdateIndex = normalizedSelect.lastIndexOf( "for update" );
+
+			if (hasForUpdateClause( forUpdateIndex )) {
+				sb.append( sql.substring( 0, forUpdateIndex - 1 ) );
+			}
+			else if (hasWithClause( normalizedSelect )) {
+				sb.append( sql.substring( 0, getWithIndex( sql ) - 1 ) );
+			}
+			else {
+				sb.append( sql );
+			}
+
+			if (LimitHelper.hasFirstRow( selection )) {
+				sb.append( " offset ? rows fetch next " );
+			}
+			else {
+				sb.append( " fetch first " );
+			}
+
+			sb.append( "? rows only" );
+
+			if (hasForUpdateClause( forUpdateIndex )) {
+				sb.append( ' ' );
+				sb.append( sql.substring( forUpdateIndex ) );
+			}
+			else if (hasWithClause( normalizedSelect )) {
+				sb.append( ' ' ).append( sql.substring( getWithIndex( sql ) ) );
+			}
+			return sb.toString();
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return isTenPointFiveReleaseOrNewer();
+		}
+
+		@Override
+		@SuppressWarnings("deprecation")
+		public boolean supportsLimitOffset() {
+			return isTenPointFiveReleaseOrNewer();
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index dd2a1b9e1d..14bdb649f7 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -53,2160 +53,2158 @@ import org.hibernate.cfg.Environment;
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
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.env.internal.DefaultSchemaNameResolver;
 import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
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
 import org.hibernate.tool.schema.internal.TemporaryTableExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 import org.jboss.logging.Logger;
 
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
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 		sqlFunctions.put( name.toLowerCase(), function );
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
 		sqlKeywords.add( word );
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
 			return SequenceStyleGenerator.class;
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
+	 * Returns the delegate managing LIMIT clause.
+	 *
+	 * @return LIMIT clause delegate.
+	 */
+	public LimitHandler getLimitHandler() {
+		return new LegacyLimitHandler( this );
+	}
+
+	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
-	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
+	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
-	/**
-	 * Build delegate managing LIMIT clause.
-	 *
-	 * @param sql SQL query.
-	 * @param selection Selection criteria. {@code null} in case of unlimited number of rows.
-	 * @return LIMIT clause delegate.
-	 */
-	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
-		return new LegacyLimitHandler( this, sql, selection );
-	}
-
 
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
 
 	private StandardTableExporter tableExporter = new StandardTableExporter( this );
 	private StandardSequenceExporter sequenceExporter = new StandardSequenceExporter( this );
 	private StandardIndexExporter indexExporter = new StandardIndexExporter( this );
 	private StandardForeignKeyExporter foreignKeyExporter = new StandardForeignKeyExporter( this );
 	private StandardUniqueKeyExporter uniqueKeyExporter = new StandardUniqueKeyExporter( this );
 	private StandardAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter = new StandardAuxiliaryDatabaseObjectExporter( this );
 	private TemporaryTableExporter temporaryTableExporter = new TemporaryTableExporter( this );
 
 	public Exporter<Table> getTableExporter() {
 		return tableExporter;
 	}
 
 	public Exporter<Table> getTemporaryTableExporter() {
 		return temporaryTableExporter;
 	}
 
 	public Exporter<Sequence> getSequenceExporter() {
 		return sequenceExporter;
 	}
 
 	public Exporter<Index> getIndexExporter() {
 		return indexExporter;
 	}
 
 	public Exporter<ForeignKey> getForeignKeyExporter() {
 		return foreignKeyExporter;
 	}
 
 	public Exporter<Constraint> getUniqueKeyExporter() {
 		return uniqueKeyExporter;
 	}
 
 	public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
 		return auxiliaryObjectExporter;
 	}
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation command
 	 */
 	public String getCreateSchemaCommand(String schemaName) {
 		return "create schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop command
 	 */
 	public String getDropSchemaCommand(String schemaName) {
 		return "drop schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to retrieve the current schema name.  Works in conjunction
 	 * with {@link #getSchemaNameResolver()}, unless the return from there does not need this
 	 * information.  E.g., a custom impl might make use of the Java 1.7 addition of
 	 * the {@link java.sql.Connection#getSchema()} method
 	 *
 	 * @return The current schema retrieval SQL
 	 */
 	public String getCurrentSchemaCommand() {
 		return null;
 	}
 
 	/**
 	 * Get the strategy for determining the schema name of a Connection
 	 *
 	 * @return The schema name resolver strategy
 	 */
 	public SchemaNameResolver getSchemaNameResolver() {
 		return DefaultSchemaNameResolver.INSTANCE;
 	}
 
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
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * The syntax for the suffix used to add a column to a table (optional).
 	 *
 	 * @return The suffix "add column" fragment.
 	 */
 	public String getAddColumnSuffixString() {
 		return "";
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
 		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( quote( constraintName ) )
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
 	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
 	 *
 	 * @return {@code true} if the database/driver has this bug
 	 */
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
 
 	/**
 	 * Does this dialect/database support commenting on tables, columns, etc?
 	 *
 	 * @return {@code true} if commenting is supported
 	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	/**
 	 * Get the comment into a form supported for table definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getTableComment(String comment) {
 		return "";
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
index ccbe7337db..40e36f7789 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/FirebirdDialect.java
@@ -1,63 +1,98 @@
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
 
 import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Firebird.
  *
  * @author Reha CENANI
  */
 public class FirebirdDialect extends InterbaseDialect {
-	
+
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			return new StringBuilder( sql.length() + 20 )
+					.append( sql )
+					.insert( 6, hasOffset ? " first ? skip ?" : " first ?" )
+					.toString();
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersFirst() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersInReverseOrder() {
+			return true;
+		}
+	};
+
 	public FirebirdDialect() {
 		super();
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 	}
 	
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop generator " + sequenceName;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return new StringBuilder( sql.length() + 20 )
 				.append( sql )
 				.insert( 6, hasOffset ? " first ? skip ?" : " first ?" )
 				.toString();
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return true;
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
+
+	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
index 92afc1632f..0794b26b0e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
@@ -1,449 +1,476 @@
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
 
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorH2DatabaseImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.jboss.logging.Logger;
 
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
 
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersInReverseOrder() {
+			return true;
+		}
+	};
+
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
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		// not null is implicit
 		return "generated by default as identity";
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return "null";
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
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
 		public String extractConstraintName(SQLException sqle) {
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
 	}	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
index a6d8b5a50b..d8a9f1019b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
@@ -1,664 +1,705 @@
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
 import org.hibernate.MappingException;
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
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
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
 @SuppressWarnings("deprecation")
 public class HSQLDialect extends Dialect {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			HSQLDialect.class.getName()
 	);
 
+	private final class HSQLLimitHandler extends AbstractLimitHandler {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			if (hsqldbVersion < 20) {
+				return new StringBuilder( sql.length() + 10 )
+						.append( sql )
+						.insert(
+								sql.toLowerCase().indexOf( "select" ) + 6,
+								hasOffset ? " limit ? ?" : " top ?"
+						)
+						.toString();
+			}
+			else {
+				return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
+			}
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersFirst() {
+			return hsqldbVersion < 20;
+		}
+	}
+
 	/**
 	 * version is 18 for 1.8 or 20 for 2.0
 	 */
 	private int hsqldbVersion = 18;
+	private final LimitHandler limitHandler;
 
 
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
+
+		limitHandler = new HSQLLimitHandler();
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		//not null is implicit
 		return "generated by default as identity (start with 1)";
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return hsqldbVersion < 20 ? "null" : "default";
 	}
 
 	@Override
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
+	public LimitHandler getLimitHandler() {
+		return limitHandler;
+	}
+
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
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
 		public String extractConstraintName(SQLException sqle) {
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
 		public String extractConstraintName(SQLException sqle) {
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
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	// Hibernate uses this information for temporary tables that it uses for its own operations
 	// therefore the appropriate strategy is taken with different versions of HSQLDB
 
 	// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
 	// definition is shared by all users but data is private to the session
 	// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
 	// the definition and data is private to the session and table declaration
 	// can happen in the middle of a transaction
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		if ( hsqldbVersion < 20 ) {
 			return "HT_" + baseTableName;
 		}
 		else {
 			// With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
 			// statement (in-case there is a global name beginning with HT_)
 			return "MODULE.HT_" + baseTableName;
 		}
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		if ( hsqldbVersion < 20 ) {
 			return "create global temporary table";
 		}
 		else {
 			return "declare local temporary table";
 		}
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	@Override
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
 	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		// Different behavior for GLOBAL TEMPORARY (1.8) and LOCAL TEMPORARY (2.0)
 		if ( hsqldbVersion < 20 ) {
 			return Boolean.TRUE;
 		}
 		else {
 			return Boolean.FALSE;
 		}
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		// Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
 		// of the session (by default, data is cleared at commit).<p>
 		//
 		// Version 2.x LOCAL TEMPORARY table definitions do not persist beyond
 		// the end of the session (by default, data is cleared at commit).
 		return true;
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
index 65d4b7287e..1f7519b2a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
@@ -1,300 +1,307 @@
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
 
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.FirstLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.InformixUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
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
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString(String table, String column, int type)
 			throws MappingException {
 		return type == Types.BIGINT
 				? "select dbinfo('serial8') from informix.systables where tabid=1"
 				: "select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
 	}
 
 	@Override
 	public String getIdentityColumnString(int type) throws MappingException {
 		return type == Types.BIGINT ?
 				"serial8 not null" :
 				"serial not null";
 	}
 
 	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
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
+	public LimitHandler getLimitHandler() {
+		return FirstLimitHandler.INSTANCE;
+	}
+
+	@Override
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
 				.insert( querySelect.toLowerCase().indexOf( "select" ) + 6, " first " + limit )
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
 		public String extractConstraintName(SQLException sqle) {
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
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create temp table";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "with no log";
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
index b335bdf50e..cdfb64f4eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Ingres9Dialect.java
@@ -1,184 +1,221 @@
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
 
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
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
+
+	private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final String soff = " offset ?";
+			final String slim = " fetch first ? rows only";
+			final StringBuilder sb = new StringBuilder( sql.length() + soff.length() + slim.length() )
+					.append( sql );
+			if (LimitHelper.hasFirstRow( selection )) {
+				sb.append( soff );
+			}
+			if (LimitHelper.hasMaxRows( selection )) {
+				sb.append( slim );
+			}
+			return sb.toString();
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	};
+
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
 	public String getIdentitySelectString() {
 		return "select last_identity()";
 	}
 
 	@Override
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
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
index c58c01ee25..905d9b171a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
@@ -1,310 +1,317 @@
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
+import org.hibernate.dialect.pagination.FirstLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Ingres 9.2.
  * <p/>
  * Known limitations: <ul>
  *     <li>
  *         Only supports simple constants or columns on the left side of an IN,
  *         making {@code (1,2,3) in (...)} or {@code (subselect) in (...)} non-supported.
  *     </li>
  *     <li>
  *         Supports only 39 digits in decimal.
  *     </li>
  *     <li>
  *         Explicitly set USE_GET_GENERATED_KEYS property to false.
  *     </li>
  *     <li>
  *         Perform string casts to varchar; removes space padding.
  *     </li>
  * </ul>
  * 
  * @author Ian Booth
  * @author Bruce Lunsford
  * @author Max Rydahl Andersen
  * @author Raymond Fan
  */
 @SuppressWarnings("deprecation")
 public class IngresDialect extends Dialect {
 	/**
 	 * Constructs a IngresDialect
 	 */
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
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 		// There is no support for a native boolean type that accepts values
 		// of true, false or unknown. Using the tinyint type requires
 		// substitions of true and false.
 		getDefaultProperties().setProperty( Environment.QUERY_SUBSTITUTIONS, "true=1,false=0" );
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select uuid_to_char(uuid_create())";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		return " with null";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select nextval for " + sequenceName;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
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
 	public String getQuerySequencesString() {
 		return "select seq_name from iisequence";
 	}
 
 	@Override
 	public String getLowercaseFunction() {
 		return "lowercase";
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return FirstLimitHandler.INSTANCE;
+	}
+
+	@Override
 	public boolean supportsLimit() {
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
 		return new StringBuilder( querySelect.length() + 16 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "declare global temporary table";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit preserve rows with norecovery";
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		return "session." + super.generateTemporaryTableName( baseTableName );
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "date(now)";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsSubselectAsInPredicateLHS() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
index 746f42c17b..071c13cef7 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InterbaseDialect.java
@@ -1,147 +1,169 @@
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
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Interbase.
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class InterbaseDialect extends Dialect {
 
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			return hasOffset ? sql + " rows ? to ?" : sql + " rows ?";
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+	};
+
 	/**
 	 * Constructs a InterbaseDialect
 	 */
 	public InterbaseDialect() {
 		super();
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.BIGINT, "numeric(18,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "blob" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "blob sub_type 1" );
 		registerColumnType( Types.BOOLEAN, "smallint" );
 		
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","||",")" ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from RDB$DATABASE";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "gen_id( " + sequenceName + ", 1 )";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create generator " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "delete from RDB$GENERATORS where RDB$GENERATOR_NAME = '" + sequenceName.toUpperCase() + "'";
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select RDB$GENERATOR_NAME from RDB$GENERATORS";
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " with lock";
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return " for update of " + aliases + " with lock";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return hasOffset ? sql + " rows ? to ?" : sql + " rows ?";
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		// TODO : not sure which (either?) is correct, could not find docs on how to do this.
 		// did find various blogs and forums mentioning that select CURRENT_TIMESTAMP
 		// does not work...
 		return "{?= call CURRENT_TIMESTAMP }";
 //		return "select CURRENT_TIMESTAMP from RDB$DATABASE";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
index a0ff52bd55..2ed59b0e11 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
@@ -1,463 +1,485 @@
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
 
 import org.hibernate.JDBCException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for MySQL (prior to 5.x).
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class MySQLDialect extends Dialect {
 
+	private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			return sql + (hasOffset ? " limit ?, ?" : " limit ?");
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+	};
+
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
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "select last_insert_id()";
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		//starts with 1, implicitly
 		return "not null auto_increment";
 	}
 
 	@Override
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
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
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
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create temporary table if not exists";
 	}
 
 	@Override
 	public String getDropTemporaryTableString() {
 		return "drop temporary table";
 	}
 
 	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		// because we [drop *temporary* table...] we do not
 		// have to doAfterTransactionCompletion these in isolation.
 		return Boolean.FALSE;
 	}
 
 	@Override
 	public String getCastTypeName(int code) {
 		switch ( code ) {
 			case Types.INTEGER:
 			case Types.BIGINT:
 			case Types.SMALLINT:
 				return "signed";
 			case Types.FLOAT:
 			case Types.NUMERIC:
 			case Types.REAL:
 				return "decimal";
 			case Types.VARCHAR:
 				return "char";
 			case Types.VARBINARY:
 				return "binary";
 			default:
 				return super.getCastTypeName( code );
 		}
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java
index f2066ad401..2551dc4fe2 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle12cDialect.java
@@ -1,68 +1,68 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.dialect;
-
-import org.hibernate.cfg.Environment;
-import org.hibernate.dialect.pagination.LimitHandler;
-import org.hibernate.dialect.pagination.SQL2008StandardLimitHandler;
-import org.hibernate.engine.spi.RowSelection;
-
-/**
- * An SQL dialect for Oracle 12c.
- * 
- * @author zhouyanming (zhouyanming@gmail.com)
- */
-public class Oracle12cDialect extends Oracle10gDialect {
-
-        public Oracle12cDialect() {
-                super();
-        }
-        
-        @Override
-    	protected void registerDefaultProperties() {
-    		super.registerDefaultProperties();
-    		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "true" );
-    	}
-        
-        @Override
-    	public boolean supportsIdentityColumns() {
-    		return true;
-    	}
-
-    	@Override
-    	public boolean supportsInsertSelectIdentity() {
-    		return true;
-    	}
-
-    	@Override
-    	public String getIdentityColumnString() {
-    		return "generated as identity";
-    	}
-
-        @Override
-        public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
-                return new SQL2008StandardLimitHandler(sql, selection);
-        }
-
-}
\ No newline at end of file
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.dialect;
+
+import org.hibernate.cfg.Environment;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.SQL2008StandardLimitHandler;
+import org.hibernate.engine.spi.RowSelection;
+
+/**
+ * An SQL dialect for Oracle 12c.
+ * 
+ * @author zhouyanming (zhouyanming@gmail.com)
+ */
+public class Oracle12cDialect extends Oracle10gDialect {
+
+        public Oracle12cDialect() {
+                super();
+        }
+        
+        @Override
+    	protected void registerDefaultProperties() {
+    		super.registerDefaultProperties();
+    		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "true" );
+    	}
+        
+        @Override
+    	public boolean supportsIdentityColumns() {
+    		return true;
+    	}
+
+    	@Override
+    	public boolean supportsInsertSelectIdentity() {
+    		return true;
+    	}
+
+    	@Override
+    	public String getIdentityColumnString() {
+    		return "generated as identity";
+    	}
+
+		@Override
+		public LimitHandler getLimitHandler() {
+			return SQL2008StandardLimitHandler.INSTANCE;
+		}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
index a834d337d9..636d163cf2 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
@@ -1,623 +1,682 @@
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
 import java.util.List;
 
 import org.hibernate.JDBCException;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Oracle 8i.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings("deprecation")
 public class Oracle8iDialect extends Dialect {
+
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			sql = sql.trim();
+			boolean isForUpdate = false;
+			if (sql.toLowerCase().endsWith( " for update" )) {
+				sql = sql.substring( 0, sql.length() - 11 );
+				isForUpdate = true;
+			}
+
+			final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
+			if (hasOffset) {
+				pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
+			}
+			else {
+				pagingSelect.append( "select * from ( " );
+			}
+			pagingSelect.append( sql );
+			if (hasOffset) {
+				pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
+			}
+			else {
+				pagingSelect.append( " ) where rownum <= ?" );
+			}
+
+			if (isForUpdate) {
+				pagingSelect.append( " for update" );
+			}
+
+			return pagingSelect.toString();
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersInReverseOrder() {
+			return true;
+		}
+
+		@Override
+		public boolean useMaxForLimit() {
+			return true;
+		}
+	};
+
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
 	/**
 	 * Constructs a Oracle8iDialect
 	 */
 	public Oracle8iDialect() {
 		super();
 		registerCharacterTypeMappings();
 		registerNumericTypeMappings();
 		registerDateTimeTypeMappings();
 		registerLargeObjectTypeMappings();
 		registerReverseHibernateTypeMappings();
 		registerFunctions();
 		registerDefaultProperties();
 	}
 
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
 
 	protected void registerNumericTypeMappings() {
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 
 		registerColumnType( Types.BOOLEAN, "number(1,0)" );
 	}
 
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "date" );
 	}
 
 	protected void registerLargeObjectTypeMappings() {
 		registerColumnType( Types.BINARY, 2000, "raw($l)" );
 		registerColumnType( Types.BINARY, "long raw" );
 
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.LONGVARCHAR, "long" );
 		registerColumnType( Types.LONGVARBINARY, "long raw" );
 	}
 
 	protected void registerReverseHibernateTypeMappings() {
 	}
 
 	protected void registerFunctions() {
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "bitand", new StandardSQLFunction("bitand") );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "ltrim", new StandardSQLFunction("ltrim") );
 		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
 		registerFunction( "soundex", new StandardSQLFunction("soundex") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP) );
 
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 
 		registerFunction( "last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
 		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false) );
 		registerFunction( "systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "uid", new NoArgSQLFunction("uid", StandardBasicTypes.INTEGER, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 
 		registerFunction( "rowid", new NoArgSQLFunction("rowid", StandardBasicTypes.LONG, false) );
 		registerFunction( "rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false) );
 
 		// Multi-param string dialect functions...
 		registerFunction( "concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", "") );
 		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER) );
 		registerFunction( "instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER) );
 		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
 		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
 		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING) );
 		registerFunction( "translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
 		registerFunction( "atan2", new StandardSQLFunction("atan2", StandardBasicTypes.FLOAT) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.INTEGER) );
 		registerFunction( "mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 		registerFunction( "nvl2", new StandardSQLFunction("nvl2") );
 		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.FLOAT) );
 
 		// Multi-param date dialect functions...
 		registerFunction( "add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE) );
 		registerFunction( "months_between", new StandardSQLFunction("months_between", StandardBasicTypes.FLOAT) );
 		registerFunction( "next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE) );
 
 		registerFunction( "str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 	}
 
 	protected void registerDefaultProperties() {
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? BitTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 
 	// features which change between 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	/**
 	 * Map case support to the Oracle DECODE function.  Oracle did not
 	 * add support for CASE until 9i.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase().endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
 		final StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
 		if (hasOffset) {
 			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append( "select * from ( " );
 		}
 		pagingSelect.append( sql );
 		if (hasOffset) {
 			pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 		}
 		else {
 			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	/**
 	 * Allows access to the basic {@link Dialect#getSelectClauseNullString}
 	 * implementation...
 	 *
 	 * @param sqlType The {@link java.sql.Types} mapping type code
 	 * @return The appropriate select cluse fragment
 	 */
 	public String getBasicSelectClauseNullString(int sqlType) {
 		return super.getSelectClauseNullString( sqlType );
 	}
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		switch(sqlType) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				return "to_char(null)";
 			case Types.DATE:
 			case Types.TIMESTAMP:
 			case Types.TIME:
 				return "to_date(null)";
 			default:
 				return "to_number(null)";
 		}
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select sysdate from dual";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 
 
 	// features which remain constant across 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
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
 		return " cascade constraints";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
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
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return    " select sequence_name from all_sequences"
 				+ "  union"
 				+ " select synonym_name"
 				+ "   from all_synonyms us, all_sequences asq"
 				+ "  where asq.sequence_name = us.table_name"
 				+ "    and asq.sequence_owner = us.table_owner";
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
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
 		public String extractConstraintName(SQLException sqle) {
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "(", ")", sqle.getMessage() );
 			}
 			else if ( errorCode == 1400 ) {
 				// simple nullability constraint
 				return null;
 			}
 			else {
 				return null;
 			}
 		}
 
 	};
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				// interpreting Oracle exceptions is much much more precise based on their specific vendor codes.
 
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 
 				// lock timeouts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( errorCode == 30006 ) {
 					// ORA-30006: resource busy; acquire with WAIT timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( errorCode == 54 ) {
 					// ORA-00054: resource busy and acquire with NOWAIT specified or timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( 4021 == errorCode ) {
 					// ORA-04021 timeout occurred while waiting to lock object
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 
 
 				// deadlocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 60 == errorCode ) {
 					// ORA-00060: deadlock detected while waiting for resource
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 				else if ( 4020 == errorCode ) {
 					// ORA-04020 deadlock detected while trying to lock object
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 
 				// query cancelled ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1013 == errorCode ) {
 					// ORA-01013: user requested cancel of current operation
 					throw new QueryTimeoutException(  message, sqlException, sql );
 				}
 
 
 				// data integrity violation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1407 == errorCode ) {
 					// ORA-01407: cannot update column to NULL
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
 		statement.registerOutParameter( col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 30 ? name.substring( 0, 30 ) : name;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
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
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 	
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 	
 	@Override
 	public boolean forceLobAsLastValue() {
 		return true;
 	}
 
 	@Override
 	public boolean useFollowOnLocking() {
 		return true;
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
 	
 	@Override
 	public String getQueryHintString(String sql, List<String> hints) {
 		final String hint = StringHelper.join( ", ", hints.iterator() );
 		
 		if ( StringHelper.isEmpty( hint ) ) {
 			return sql;
 		}
 
 		final int pos = sql.indexOf( "select" );
 		if ( pos > -1 ) {
 			final StringBuilder buffer = new StringBuilder( sql.length() + hint.length() + 8 );
 			if ( pos > 0 ) {
 				buffer.append( sql.substring( 0, pos ) );
 			}
 			buffer.append( "select /*+ " ).append( hint ).append( " */" )
 					.append( sql.substring( pos + "select".length() ) );
 			sql = buffer.toString();
 		}
 
 		return sql;
 	}
 	
 	@Override
 	public int getMaxAliasLength() {
 		// Oracle's max identifier length is 30, but Hibernate needs to add "uniqueing info" so we account for that,
 		return 20;
 	}
 
 	@Override
 	public CallableStatementSupport getCallableStatementSupport() {
 		// Oracle supports returning cursors
 		return StandardCallableStatementSupport.REF_CURSOR_INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
index 687d23bdaa..6fe8cb271d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
@@ -1,153 +1,217 @@
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
 
 import org.hibernate.LockOptions;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.CaseFragment;
 
 /**
  * A dialect for Oracle 9i databases.
  * <p/>
  * Specifies to not use "ANSI join syntax" because 9i does not seem to properly handle it in all cases.
  *
  * @author Steve Ebersole
  */
 public class Oracle9iDialect extends Oracle8iDialect {
+
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			sql = sql.trim();
+			String forUpdateClause = null;
+			boolean isForUpdate = false;
+			final int forUpdateIndex = sql.toLowerCase().lastIndexOf( "for update" );
+			if (forUpdateIndex > -1) {
+				// save 'for update ...' and then remove it
+				forUpdateClause = sql.substring( forUpdateIndex );
+				sql = sql.substring( 0, forUpdateIndex - 1 );
+				isForUpdate = true;
+			}
+
+			final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
+			if (hasOffset) {
+				pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
+			}
+			else {
+				pagingSelect.append( "select * from ( " );
+			}
+			pagingSelect.append( sql );
+			if (hasOffset) {
+				pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
+			}
+			else {
+				pagingSelect.append( " ) where rownum <= ?" );
+			}
+
+			if (isForUpdate) {
+				pagingSelect.append( " " );
+				pagingSelect.append( forUpdateClause );
+			}
+
+			return pagingSelect.toString();
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersInReverseOrder() {
+			return true;
+		}
+
+		@Override
+		public boolean useMaxForLimit() {
+			return true;
+		}
+	};
+
 	@Override
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1 char)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l char)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
 
 	@Override
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 	}
 
 	@Override
 	public CaseFragment createCaseFragment() {
 		// Oracle did add support for ANSI CASE statements in 9i
 		return new ANSICaseFragment();
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		String forUpdateClause = null;
 		boolean isForUpdate = false;
 		final int forUpdateIndex = sql.toLowerCase().lastIndexOf( "for update") ;
 		if ( forUpdateIndex > -1 ) {
 			// save 'for update ...' and then remove it
 			forUpdateClause = sql.substring( forUpdateIndex );
 			sql = sql.substring( 0, forUpdateIndex-1 );
 			isForUpdate = true;
 		}
 
 		final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
 		if (hasOffset) {
 			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append( "select * from ( " );
 		}
 		pagingSelect.append( sql );
 		if (hasOffset) {
 			pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
 		}
 		else {
 			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " " );
 			pagingSelect.append( forUpdateClause );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		return getBasicSelectClauseNullString( sqlType );
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select systimestamp from dual";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
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
 		else if ( timeout > 0 ) {
 			// convert from milliseconds to seconds
 			final float seconds = timeout / 1000.0f;
 			timeout = Math.round( seconds );
 			return " for update wait " + timeout;
 		}
 		else {
 			return " for update";
 		}
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		return getWriteLockString( timeout );
 	}
 
 	/**
 	 * HHH-4907, I don't know if oracle 8 supports this syntax, so I'd think it is better add this 
 	 * method here. Reopen this issue if you found/know 8 supports it.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
index 7429584dc0..8bba7e8d40 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
@@ -1,545 +1,572 @@
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
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockOptions;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.PositionSubstringFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.procedure.internal.PostgresCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
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
 
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean bindLimitParametersInReverseOrder() {
+			return true;
+		}
+	};
+
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
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
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
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 	
 	@Override
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		/*
 		 * Parent's implementation for (aliases, lockOptions) ignores aliases.
 		 */
 		return getForUpdateString(aliases);
 	}
 
 	@Override
 	public String getIdentitySelectString(String table, String column, int type) {
 		return "select currval('" + table + '_' + column + "_seq')";
 	}
 
 	@Override
 	public String getIdentityColumnString(int type) {
 		return type==Types.BIGINT ?
 			"bigserial not null" :
 			"serial not null";
 	}
 
 	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
 	@Override
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
 		return SequenceGenerator.class;
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
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create temporary table";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit drop";
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
 		public String extractConstraintName(SQLException sqle) {
 			try {
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
 			catch (NumberFormatException nfe) {
 				return null;
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
index 3e13a521fe..be6ac46555 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
@@ -1,385 +1,420 @@
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
+import org.hibernate.dialect.pagination.AbstractLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.jboss.logging.Logger;
 
 /**
  * This is the Hibernate dialect for the Unisys 2200 Relational Database (RDMS).
  * This dialect was developed for use with Hibernate 3.0.5. Other versions may
  * require modifications to the dialect.
  * <p/>
  * Version History:
  * Also change the version displayed below in the constructor
  * 1.1
  * 1.0  2005-10-24  CDH - First dated version for use with CP 11
  *
  * @author Ploski and Hanson
  */
 @SuppressWarnings("deprecation")
 public class RDMSOS2200Dialect extends Dialect {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			RDMSOS2200Dialect.class.getName()
 	);
 
+	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+			if (hasOffset) {
+				throw new UnsupportedOperationException( "query result offset is not supported" );
+			}
+			return sql + " fetch first ? rows only ";
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean supportsLimitOffset() {
+			return false;
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	};
+
 	/**
 	 * Constructs a RDMSOS2200Dialect
 	 */
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
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
 
 		// The RDMS concat() function only supports 2 parameters
 		registerFunction( "concat", new SQLFunctionTemplate( StandardBasicTypes.STRING, "concat(?1, ?2)" ) );
 		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.STRING ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 
 		// RDMS does not directly support the trim() function, we use rtrim() and ltrim()
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "ltrim(rtrim(?1))" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "initcap", new StandardSQLFunction( "initcap" ) );
 
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "days", new StandardSQLFunction( "days", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 
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
 		registerColumnType( Types.BIT, "SMALLINT" );
 		registerColumnType( Types.TINYINT, "SMALLINT" );
 		registerColumnType( Types.BIGINT, "NUMERIC(21,0)" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.CHAR, "CHARACTER(1)" );
 		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.REAL, "REAL" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.NUMERIC, "NUMERIC(21,$l)" );
 		registerColumnType( Types.DECIMAL, "NUMERIC(21,$l)" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.VARCHAR, "CHARACTER($l)" );
 		registerColumnType( Types.BLOB, "BLOB($l)" );
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
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	/**
 	 * The RDMS DB supports the 'FOR UPDATE OF' clause. However, the RDMS-JDBC
 	 * driver does not support this feature, so a false is return.
 	 * The base dialect also returns a false, but we will leave this over-ride
 	 * in to make sure it stays false.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean forUpdateOfColumns() {
 		return false;
 	}
 
 	/**
 	 * Since the RDMS-JDBC driver does not support for updates, this string is
 	 * set to an empty string. Whenever, the driver does support this feature,
 	 * the returned string should be " FOR UPDATE OF". Note that RDMS does not
 	 * support the string 'FOR UPDATE' string.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getForUpdateString() {
 		// Original Dialect.java returns " for update";
 		return "";
 	}
 
 	// Verify the state of this new method in Hibernate 3.0 Dialect.java
 
 	/**
 	 * RDMS does not support Cascade Deletes.
 	 * Need to review this in the future when support is provided.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
 	/**
 	 * Currently, RDMS-JDBC does not support ForUpdate.
 	 * Need to review this in the future when support is provided.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		// The where clause was added to eliminate this statement from Brute Force Searches.
 		return "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		// We must return a valid RDMS/RSA command from this method to
 		// prevent RDMS/RSA from issuing *ERROR 400
 		return "";
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		// We must return a valid RDMS/RSA command from this method to
 		// prevent RDMS/RSA from issuing *ERROR 400
 		return "";
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		// Used with DROP TABLE to delete all records in the table.
 		return " including contents";
 	}
 
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return LIMIT_HANDLER;
+	}
+
+	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return sql + " fetch first " + limit + " rows only ";
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		// RDMS supports the UNION ALL clause.
 		return true;
 	}
 
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// RDMS has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
index 1ce703ce5f..4aa7db7854 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
@@ -1,122 +1,122 @@
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
 
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.SQLServer2005LimitHandler;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for Microsoft SQL 2005. (HHH-3936 fix)
  *
  * @author Yoryos Valotasios
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 @SuppressWarnings("deprecation")
 public class SQLServer2005Dialect extends SQLServerDialect {
 	private static final int MAX_LENGTH = 8000;
 
 	/**
 	 * Constructs a SQLServer2005Dialect
 	 */
 	public SQLServer2005Dialect() {
 		// HHH-3965 fix
 		// As per http://www.sql-server-helper.com/faq/sql-server-2005-varchar-max-p01.aspx
 		// use varchar(max) and varbinary(max) instead of TEXT and IMAGE types
 		registerColumnType( Types.BLOB, "varbinary(MAX)" );
 		registerColumnType( Types.VARBINARY, "varbinary(MAX)" );
 		registerColumnType( Types.VARBINARY, MAX_LENGTH, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "varbinary(MAX)" );
 
 		registerColumnType( Types.CLOB, "varchar(MAX)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar(MAX)" );
 		registerColumnType( Types.VARCHAR, "varchar(MAX)" );
 		registerColumnType( Types.VARCHAR, MAX_LENGTH, "varchar($l)" );
 
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BIT, "bit" );
 
 
 		registerFunction( "row_number", new NoArgSQLFunction( "row_number", StandardBasicTypes.INTEGER, true ) );
 	}
 
 	@Override
-	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
-		return new SQLServer2005LimitHandler( sql, selection );
+	public LimitHandler getLimitHandler() {
+		return new SQLServer2005LimitHandler();
 	}
 
 	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
 		// NOTE : since SQLServer2005 the nowait hint is supported
 		if ( lockOptions.getLockMode() == LockMode.UPGRADE_NOWAIT ) {
 			return tableName + " with (updlock, rowlock, nowait)";
 		}
 
 		final LockMode mode = lockOptions.getLockMode();
 		final boolean isNoWait = lockOptions.getTimeOut() == LockOptions.NO_WAIT;
 		final String noWaitStr = isNoWait ? ", nowait" : "";
 		switch ( mode ) {
 			case UPGRADE_NOWAIT:
 				return tableName + " with (updlock, rowlock, nowait)";
 			case UPGRADE:
 			case PESSIMISTIC_WRITE:
 			case WRITE:
 				return tableName + " with (updlock, rowlock" + noWaitStr + " )";
 			case PESSIMISTIC_READ:
 				return tableName + " with (holdlock, rowlock" + noWaitStr + " )";
 			default:
 				return tableName;
 		}
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 				if ( "HY008".equals( sqlState ) ) {
 					throw new QueryTimeoutException( message, sqlException, sql );
 				}
 				if (1222 == errorCode ) {
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				return null;
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
index c8dba6137c..864055149e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
@@ -1,212 +1,223 @@
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
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.dialect.pagination.TopLimitHandler;
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
 
+	private final LimitHandler limitHandler;
+
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
+
+		this.limitHandler = new TopLimitHandler( false, false );
 	}
 
 	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	static int getAfterSelectInsertPoint(String sql) {
 		final int selectIndex = sql.toLowerCase().indexOf( "select" );
 		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
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
 
 	/**
 	 * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String appendIdentitySelectToInsert(String insertSQL) {
 		return insertSQL + " select scope_identity()";
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return limitHandler;
+	}
+
+	@Override
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
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
index bbd6545dab..44f1ae4ea2 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
@@ -1,270 +1,277 @@
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
+import org.hibernate.dialect.pagination.FirstLimitHandler;
+import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for TimesTen 5.1.
  * <p/>
  * Known limitations:
  * joined-subclass support because of no CASE support in TimesTen
  * No support for subqueries that includes aggregation
  * - size() in HQL not supported
  * - user queries that does subqueries with aggregation
  * No CLOB/BLOB support
  * No cascade delete support.
  * No Calendar support
  * No support for updating primary keys.
  *
  * @author Sherry Listgarten and Max Andersen
  */
 @SuppressWarnings("deprecation")
 public class TimesTenDialect extends Dialect {
 	/**
 	 * Constructs a TimesTenDialect
 	 */
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
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod" ) );
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "getdate", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select first 1 " + sequenceName + ".nextval from sys.tables";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select NAME from sys.sequences";
 	}
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
 	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
 
 	@Override
+	public LimitHandler getLimitHandler() {
+		return FirstLimitHandler.INSTANCE;
+	}
+
+	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
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
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select first 1 sysdate from sys.tables";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// TimesTen has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java
index 8b80ea170b..96119a339d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/AbstractLimitHandler.java
@@ -1,174 +1,168 @@
 package org.hibernate.dialect.pagination;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Default implementation of {@link LimitHandler} interface. 
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public abstract class AbstractLimitHandler implements LimitHandler {
-	protected final String sql;
-	protected final RowSelection selection;
 
-	/**
-	 * Default constructor. SQL query and selection criteria required to allow LIMIT clause pre-processing.
-	 *
-	 * @param sql SQL query.
-	 * @param selection Selection criteria. {@code null} in case of unlimited number of rows.
-	 */
-	public AbstractLimitHandler(String sql, RowSelection selection) {
-		this.sql = sql;
-		this.selection = selection;
+	protected AbstractLimitHandler() {
+		// NOP
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this handler support bind variables (i.e., prepared statement
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
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
-	 * NOTE: what gets passed into {@link #AbstractLimitHandler(String, RowSelection)} is the zero-based offset.
-	 * Dialects which do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion
-	 * calls prior to injecting the limit values into the SQL string.
+	 * NOTE: what gets passed into {@link AbstractLimitHandler#processSql(String, org.hibernate.engine.spi.RowSelection)}
+     * is the zero-based offset. Dialects which do not {@link #supportsVariableLimit} should take care to perform
+     * any needed first-row-conversion calls prior to injecting the limit values into the SQL string.
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
 
 	@Override
-	public String getProcessedSql() {
+	public String processSql(String sql, RowSelection selection) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName() );
 	}
 
 	@Override
-	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index)
+	public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index)
 			throws SQLException {
-		return bindLimitParametersFirst() ? bindLimitParameters( statement, index ) : 0;
+		return bindLimitParametersFirst() ? bindLimitParameters( selection, statement, index ) : 0;
 	}
 
 	@Override
-	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index)
+	public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index)
 			throws SQLException {
-		return !bindLimitParametersFirst() ? bindLimitParameters( statement, index ) : 0;
+		return !bindLimitParametersFirst() ? bindLimitParameters( selection, statement, index ) : 0;
 	}
 
 	@Override
-	public void setMaxRows(PreparedStatement statement) throws SQLException {
+	public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
 	}
 
 	/**
 	 * Default implementation of binding parameter values needed by the LIMIT clause.
 	 *
+     * @param selection the selection criteria for rows.
 	 * @param statement Statement to which to bind limit parameter values.
 	 * @param index Index from which to start binding.
 	 * @return The number of parameter values bound.
 	 * @throws SQLException Indicates problems binding parameter values.
 	 */
-	protected int bindLimitParameters(PreparedStatement statement, int index)
+	protected final int bindLimitParameters(RowSelection selection, PreparedStatement statement, int index)
 			throws SQLException {
 		if ( !supportsVariableLimit() || !LimitHelper.hasMaxRows( selection ) ) {
 			return 0;
 		}
 		final int firstRow = convertToFirstRowValue( LimitHelper.getFirstRow( selection ) );
-		final int lastRow = getMaxOrLimit();
+		final int lastRow = getMaxOrLimit( selection );
 		final boolean hasFirstRow = supportsLimitOffset() && ( firstRow > 0 || forceLimitUsage() );
 		final boolean reverse = bindLimitParametersInReverseOrder();
 		if ( hasFirstRow ) {
 			statement.setInt( index + ( reverse ? 1 : 0 ), firstRow );
 		}
 		statement.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
 		return hasFirstRow ? 2 : 1;
 	}
 
 	/**
 	 * Some dialect-specific LIMIT clauses require the maximum last row number
 	 * (aka, first_row_number + total_row_count), while others require the maximum
 	 * returned row count (the total maximum number of rows to return).
 	 *
+	 * @param selection the selection criteria for rows.
+	 *
 	 * @return The appropriate value to bind into the limit clause.
 	 */
-	protected int getMaxOrLimit() {
+	protected final int getMaxOrLimit(RowSelection selection) {
 		final int firstRow = convertToFirstRowValue( LimitHelper.getFirstRow( selection ) );
 		final int lastRow = selection.getMaxRows();
 		return useMaxForLimit() ? lastRow + firstRow : lastRow;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java
index c26db45396..854ad581c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/CUBRIDLimitHandler.java
@@ -1,69 +1,60 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
-import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Limit handler for CUBRID
  *
  * @author Esen Sagynov (kadishmal at gmail dot com)
  */
 public class CUBRIDLimitHandler extends AbstractLimitHandler {
-	@SuppressWarnings("FieldCanBeLocal")
-	private final Dialect dialect;
 
-	/**
-	 * Constructs a CUBRIDLimitHandler
-	 *
-	 * @param dialect Currently not used
-	 * @param sql The SQL
-	 * @param selection The row selection options
-	 */
-	public CUBRIDLimitHandler(Dialect dialect, String sql, RowSelection selection) {
-		super( sql, selection );
-		this.dialect = dialect;
+	public static final CUBRIDLimitHandler INSTANCE = new CUBRIDLimitHandler();
+
+	private CUBRIDLimitHandler() {
+		// NOP
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
-	public String getProcessedSql() {
+	public String processSql(String sql, RowSelection selection) {
 		if ( LimitHelper.useLimit( this, selection ) ) {
 			// useLimitOffset: whether "offset" is set or not;
 			// if set, use "LIMIT offset, row_count" syntax;
 			// if not, use "LIMIT row_count"
 			final boolean useLimitOffset = LimitHelper.hasFirstRow( selection );
 			return sql + (useLimitOffset ? " limit ?, ?" : " limit ?");
 		}
 		else {
 			// or return unaltered SQL
 			return sql;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/FirstLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/FirstLimitHandler.java
new file mode 100644
index 0000000000..86ae606025
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/FirstLimitHandler.java
@@ -0,0 +1,68 @@
+/* 
+ * Hibernate, Relational Persistence for Idiomatic Java
+ * 
+ * JBoss, Home of Professional Open Source
+ * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
+ * as indicated by the @authors tag. All rights reserved.
+ * See the copyright.txt in the distribution for a
+ * full listing of individual contributors.
+ *
+ * This copyrighted material is made available to anyone wishing to use,
+ * modify, copy, or redistribute it subject to the terms and conditions
+ * of the GNU Lesser General Public License, v. 2.1.
+ * This program is distributed in the hope that it will be useful, but WITHOUT A
+ * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
+ * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
+ * You should have received a copy of the GNU Lesser General Public License,
+ * v.2.1 along with this distribution; if not, write to the Free Software
+ * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
+ * MA  02110-1301, USA.
+ */
+package org.hibernate.dialect.pagination;
+
+import org.hibernate.engine.spi.RowSelection;
+
+
+/**
+ * @author Brett Meyer
+ */
+public class FirstLimitHandler extends AbstractLimitHandler {
+
+	public static final FirstLimitHandler INSTANCE = new FirstLimitHandler();
+
+	private FirstLimitHandler() {
+		// NOP
+	}
+	
+	@Override
+	public String processSql(String sql, RowSelection selection) {
+		final boolean hasOffset = LimitHelper.hasFirstRow( selection );
+		if ( hasOffset ) {
+			throw new UnsupportedOperationException( "query result offset is not supported" );
+		}
+		return new StringBuilder( sql.length() + 16 )
+				.append( sql )
+				.insert( sql.toLowerCase().indexOf( "select" ) + 6, " first ?" )
+				.toString();
+	}
+
+	@Override
+	public boolean supportsLimit() {
+		return true;
+	}
+
+	@Override
+	public boolean useMaxForLimit() {
+		return true;
+	}
+
+	@Override
+	public boolean supportsLimitOffset() {
+		return false;
+	}
+
+	@Override
+	public boolean supportsVariableLimit() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java
index 9385e7ac60..c4d7a3305d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyLimitHandler.java
@@ -1,102 +1,99 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Limit handler that delegates all operations to the underlying dialect.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 @SuppressWarnings("deprecation")
 public class LegacyLimitHandler extends AbstractLimitHandler {
 	private final Dialect dialect;
 
 	/**
 	 * Constructs a LegacyLimitHandler
 	 *
 	 * @param dialect The dialect
-	 * @param sql The sql
-	 * @param selection The row selection
 	 */
-	public LegacyLimitHandler(Dialect dialect, String sql, RowSelection selection) {
-		super( sql, selection );
+	public LegacyLimitHandler(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return dialect.supportsLimit();
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return dialect.supportsLimitOffset();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return dialect.supportsVariableLimit();
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return dialect.bindLimitParametersInReverseOrder();
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return dialect.bindLimitParametersFirst();
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return dialect.useMaxForLimit();
 	}
 
 	@Override
 	public boolean forceLimitUsage() {
 		return dialect.forceLimitUsage();
 	}
 
 	@Override
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return dialect.convertToFirstRowValue( zeroBasedFirstResult );
 	}
 
 	@Override
-	public String getProcessedSql() {
+	public String processSql(String sql, RowSelection selection) {
 		final boolean useLimitOffset = supportsLimit()
 				&& supportsLimitOffset()
 				&& LimitHelper.hasFirstRow( selection )
 				&& LimitHelper.hasMaxRows( selection );
 		return dialect.getLimitString(
 				sql,
 				useLimitOffset ? LimitHelper.getFirstRow( selection ) : 0,
-				getMaxOrLimit()
+				getMaxOrLimit( selection )
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java
index 3a28fab606..3fa881a89a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LimitHandler.java
@@ -1,87 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
+import org.hibernate.engine.spi.RowSelection;
+
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 /**
  * Contract defining dialect-specific LIMIT clause handling. Typically implementers might consider extending
  * {@link AbstractLimitHandler} class.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public interface LimitHandler {
 	/**
 	 * Does this handler support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this handler supports some form of LIMIT.
 	 */
-	public boolean supportsLimit();
+	boolean supportsLimit();
 
 	/**
 	 * Does this handler's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the handler supports an offset within the limit support.
 	 */
-	public boolean supportsLimitOffset();
+	boolean supportsLimitOffset();
 
 	/**
 	 * Return processed SQL query.
 	 *
+     * @param sql       the SQL query to process.
+     * @param selection the selection criteria for rows.
+     *
 	 * @return Query statement with LIMIT clause applied.
 	 */
-	public String getProcessedSql();
+	String processSql(String sql, RowSelection selection);
 
 	/**
 	 * Bind parameter values needed by the LIMIT clause before original SELECT statement.
 	 *
+     * @param selection the selection criteria for rows.
 	 * @param statement Statement to which to bind limit parameter values.
 	 * @param index Index from which to start binding.
 	 * @return The number of parameter values bound.
 	 * @throws SQLException Indicates problems binding parameter values.
 	 */
-	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index) throws SQLException;
+	int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException;
 
 	/**
 	 * Bind parameter values needed by the LIMIT clause after original SELECT statement.
 	 *
+     * @param selection the selection criteria for rows.
 	 * @param statement Statement to which to bind limit parameter values.
 	 * @param index Index from which to start binding.
 	 * @return The number of parameter values bound.
 	 * @throws SQLException Indicates problems binding parameter values.
 	 */
-	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index) throws SQLException;
+	int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException;
 
 	/**
 	 * Use JDBC API to limit the number of rows returned by the SQL query. Typically handlers that do not
 	 * support LIMIT clause should implement this method.
 	 *
+     * @param selection the selection criteria for rows.
 	 * @param statement Statement which number of returned rows shall be limited.
 	 * @throws SQLException Indicates problems while limiting maximum rows returned.
 	 */
-	public void setMaxRows(PreparedStatement statement) throws SQLException;
+	void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java
index b5dc87fec3..3aef686ea4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/NoopLimitHandler.java
@@ -1,68 +1,65 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Handler not supporting query LIMIT clause. JDBC API is used to set maximum number of returned rows.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class NoopLimitHandler extends AbstractLimitHandler {
-	/**
-	 * Constructs a NoopLimitHandler
-	 *
-	 * @param sql The SQL
-	 * @param selection The row selection options
-	 */
-	public NoopLimitHandler(String sql, RowSelection selection) {
-		super( sql, selection );
+
+	public static final NoopLimitHandler INSTANCE = new NoopLimitHandler();
+
+	private NoopLimitHandler() {
+		// NOP
 	}
 
 	@Override
-	public String getProcessedSql() {
+	public String processSql(String sql, RowSelection selection) {
 		return sql;
 	}
 
 	@Override
-	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index) {
+	public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) {
 		return 0;
 	}
 
 	@Override
-	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index) {
+	public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) {
 		return 0;
 	}
 
 	@Override
-	public void setMaxRows(PreparedStatement statement) throws SQLException {
+	public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
 		if ( LimitHelper.hasMaxRows( selection ) ) {
 			statement.setMaxRows( selection.getMaxRows() + convertToFirstRowValue( LimitHelper.getFirstRow( selection ) ) );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQL2008StandardLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQL2008StandardLimitHandler.java
index c9f75cd9e7..1fab76ca3e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQL2008StandardLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQL2008StandardLimitHandler.java
@@ -1,64 +1,61 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.dialect.pagination;
-
-import org.hibernate.engine.spi.RowSelection;
-
-/**
- * LIMIT clause handler compatible with ISO and ANSI SQL:2008 standard.
- * 
- * @author zhouyanming (zhouyanming@gmail.com)
- */
-public class SQL2008StandardLimitHandler extends AbstractLimitHandler {
-
-	/**
-	 * Constructs a SQL2008StandardLimitHandler
-	 * 
-	 * @param sql
-	 *            The SQL
-	 * @param selection
-	 *            The row selection options
-	 */
-	public SQL2008StandardLimitHandler(String sql, RowSelection selection) {
-		super(sql, selection);
-	}
-
-	@Override
-	public boolean supportsLimit() {
-		return true;
-	}
-
-	@Override
-	public String getProcessedSql() {
-		if (LimitHelper.useLimit(this, selection)) {
-			return sql
-					+ (LimitHelper.hasFirstRow(selection) ? " offset ? rows fetch next ? rows only"
-							: " fetch first ? rows only");
-		} else {
-			// or return unaltered SQL
-			return sql;
-		}
-	}
-
-}
\ No newline at end of file
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.dialect.pagination;
+
+import org.hibernate.engine.spi.RowSelection;
+
+/**
+ * LIMIT clause handler compatible with ISO and ANSI SQL:2008 standard.
+ * 
+ * @author zhouyanming (zhouyanming@gmail.com)
+ */
+public class SQL2008StandardLimitHandler extends AbstractLimitHandler {
+
+	public static final SQL2008StandardLimitHandler INSTANCE = new SQL2008StandardLimitHandler();
+
+	/**
+	 * Constructs a SQL2008StandardLimitHandler
+	 */
+	private SQL2008StandardLimitHandler() {
+		// NOP
+	}
+
+	@Override
+	public boolean supportsLimit() {
+		return true;
+	}
+
+	@Override
+	public String processSql(String sql, RowSelection selection) {
+		if (LimitHelper.useLimit( this, selection )) {
+			return sql + (LimitHelper.hasFirstRow( selection ) ?
+					" offset ? rows fetch next ? rows only" : " fetch first ? rows only");
+		}
+		else {
+			// or return unaltered SQL
+			return sql;
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java
index d00a755406..bfd05e9c81 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/SQLServer2005LimitHandler.java
@@ -1,328 +1,322 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect.pagination;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * LIMIT clause handler compatible with SQL Server 2005 and later.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class SQLServer2005LimitHandler extends AbstractLimitHandler {
 	private static final String SELECT = "select";
 	private static final String SELECT_WITH_SPACE = SELECT + ' ';
 	private static final String FROM = "from";
 	private static final String DISTINCT = "distinct";
 	private static final String ORDER_BY = "order by";
 
 	private static final Pattern ALIAS_PATTERN = Pattern.compile( "(?i)\\sas\\s(.)+$" );
 
 	// Flag indicating whether TOP(?) expression has been added to the original query.
 	private boolean topAdded;
-	// True if offset greater than 0.
-	private boolean hasOffset = true;
 
 	/**
 	 * Constructs a SQLServer2005LimitHandler
-	 *
-	 * @param sql The SQL
-	 * @param selection The row selection options
 	 */
-	public SQLServer2005LimitHandler(String sql, RowSelection selection) {
-		super( sql, selection );
+	public SQLServer2005LimitHandler() {
+		// NOP
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
 		return true;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
 	@Override
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		// Our dialect paginated results aren't zero based. The first row should get the number 1 and so on
 		return zeroBasedFirstResult + 1;
 	}
 
 	/**
 	 * Add a LIMIT clause to the given SQL SELECT (HHH-2655: ROW_NUMBER for Paging)
 	 *
 	 * The LIMIT SQL will look like:
 	 *
 	 * <pre>
 	 * WITH query AS (
 	 *   SELECT inner_query.*
 	 *        , ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__
 	 *     FROM ( original_query_with_top_if_order_by_present_and_all_aliased_columns ) inner_query
 	 * )
 	 * SELECT alias_list FROM query WHERE __hibernate_row_nr__ >= offset AND __hibernate_row_nr__ < offset + last
 	 * </pre>
 	 *
 	 * When offset equals {@literal 0}, only <code>TOP(?)</code> expression is added to the original query.
 	 *
 	 * @return A new SQL statement with the LIMIT clause applied.
 	 */
 	@Override
-	public String getProcessedSql() {
+	public String processSql(String sql, RowSelection selection) {
 		final StringBuilder sb = new StringBuilder( sql );
 		if ( sb.charAt( sb.length() - 1 ) == ';' ) {
 			sb.setLength( sb.length() - 1 );
 		}
 
 		if ( LimitHelper.hasFirstRow( selection ) ) {
 			final String selectClause = fillAliasInSelectClause( sb );
 
 			final int orderByIndex = shallowIndexOfWord( sb, ORDER_BY, 0 );
 			if ( orderByIndex > 0 ) {
 				// ORDER BY requires using TOP.
 				addTopExpression( sb );
 			}
 
 			encloseWithOuterQuery( sb );
 
 			// Wrap the query within a with statement:
 			sb.insert( 0, "WITH query AS (" ).append( ") SELECT " ).append( selectClause ).append( " FROM query " );
 			sb.append( "WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?" );
 		}
 		else {
-			hasOffset = false;
 			addTopExpression( sb );
 		}
 
 		return sb.toString();
 	}
 
 	@Override
-	public int bindLimitParametersAtStartOfQuery(PreparedStatement statement, int index) throws SQLException {
+	public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
 		if ( topAdded ) {
 			// Binding TOP(?)
-			statement.setInt( index, getMaxOrLimit() - 1 );
+			statement.setInt( index, getMaxOrLimit( selection ) - 1 );
 			return 1;
 		}
 		return 0;
 	}
 
 	@Override
-	public int bindLimitParametersAtEndOfQuery(PreparedStatement statement, int index) throws SQLException {
-		return hasOffset ? super.bindLimitParametersAtEndOfQuery( statement, index ) : 0;
+	public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
+		return LimitHelper.hasFirstRow( selection ) ? super.bindLimitParametersAtEndOfQuery( selection, statement, index ) : 0;
 	}
 
 	/**
 	 * Adds missing aliases in provided SELECT clause and returns coma-separated list of them.
 	 * If query takes advantage of expressions like {@literal *} or {@literal {table}.*} inside SELECT clause,
 	 * method returns {@literal *}.
 	 *
 	 * @param sb SQL query.
 	 *
 	 * @return List of aliases separated with comas or {@literal *}.
 	 */
 	protected String fillAliasInSelectClause(StringBuilder sb) {
 		final List<String> aliases = new LinkedList<String>();
 		final int startPos = shallowIndexOf( sb, SELECT_WITH_SPACE, 0 );
 		int endPos = shallowIndexOfWord( sb, FROM, startPos );
 		int nextComa = startPos;
 		int prevComa = startPos;
 		int unique = 0;
 		boolean selectsMultipleColumns = false;
 
 		while ( nextComa != -1 ) {
 			prevComa = nextComa;
 			nextComa = shallowIndexOf( sb, ",", nextComa );
 			if ( nextComa > endPos ) {
 				break;
 			}
 			if ( nextComa != -1 ) {
 				final String expression = sb.substring( prevComa, nextComa );
 				if ( selectsMultipleColumns( expression ) ) {
 					selectsMultipleColumns = true;
 				}
 				else {
 					String alias = getAlias( expression );
 					if ( alias == null ) {
 						// Inserting alias. It is unlikely that we would have to add alias, but just in case.
 						alias = StringHelper.generateAlias( "page", unique );
 						sb.insert( nextComa, " as " + alias );
-						int aliasExprLength = ( " as " + alias ).length();
+						final int aliasExprLength = ( " as " + alias ).length();
 						++unique;
 						nextComa += aliasExprLength;
 						endPos += aliasExprLength;
 					}
 					aliases.add( alias );
 				}
 				++nextComa;
 			}
 		}
 		// Processing last column.
 		// Refreshing end position, because we might have inserted new alias.
 		endPos = shallowIndexOfWord( sb, FROM, startPos );
 		final String expression = sb.substring( prevComa, endPos );
 		if ( selectsMultipleColumns( expression ) ) {
 			selectsMultipleColumns = true;
 		}
 		else {
 			String alias = getAlias( expression );
 			if ( alias == null ) {
 				// Inserting alias. It is unlikely that we would have to add alias, but just in case.
 				alias = StringHelper.generateAlias( "page", unique );
 				sb.insert( endPos - 1, " as " + alias );
 			}
 			aliases.add( alias );
 		}
 
 		// In case of '*' or '{table}.*' expressions adding an alias breaks SQL syntax, returning '*'.
 		return selectsMultipleColumns ? "*" : StringHelper.join( ", ", aliases.iterator() );
 	}
 
 	/**
 	 * @param expression Select expression.
 	 *
 	 * @return {@code true} when expression selects multiple columns, {@code false} otherwise.
 	 */
 	private boolean selectsMultipleColumns(String expression) {
 		final String lastExpr = expression.trim().replaceFirst( "(?i)(.)*\\s", "" );
 		return "*".equals( lastExpr ) || lastExpr.endsWith( ".*" );
 	}
 
 	/**
 	 * Returns alias of provided single column selection or {@code null} if not found.
 	 * Alias should be preceded with {@code AS} keyword.
 	 *
 	 * @param expression Single column select expression.
 	 *
 	 * @return Column alias.
 	 */
 	private String getAlias(String expression) {
 		final Matcher matcher = ALIAS_PATTERN.matcher( expression );
 		if ( matcher.find() ) {
 			// Taking advantage of Java regular expressions greedy behavior while extracting the last AS keyword.
 			// Note that AS keyword can appear in CAST operator, e.g. 'cast(tab1.col1 as varchar(255)) as col1'.
 			return matcher.group( 0 ).replaceFirst( "(?i)(.)*\\sas\\s", "" ).trim();
 		}
 		return null;
 	}
 
 	/**
 	 * Encloses original SQL statement with outer query that provides {@literal __hibernate_row_nr__} column.
 	 *
 	 * @param sql SQL query.
 	 */
 	protected void encloseWithOuterQuery(StringBuilder sql) {
 		sql.insert( 0, "SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " );
 		sql.append( " ) inner_query " );
 	}
 
 	/**
 	 * Adds {@code TOP} expression. Parameter value is bind in
-	 * {@link #bindLimitParametersAtStartOfQuery(PreparedStatement, int)} method.
+	 * {@link #bindLimitParametersAtStartOfQuery(RowSelection, PreparedStatement, int)} method.
 	 *
 	 * @param sql SQL query.
 	 */
 	protected void addTopExpression(StringBuilder sql) {
 		final int distinctStartPos = shallowIndexOfWord( sql, DISTINCT, 0 );
 		if ( distinctStartPos > 0 ) {
 			// Place TOP after DISTINCT.
 			sql.insert( distinctStartPos + DISTINCT.length(), " TOP(?)" );
 		}
 		else {
 			final int selectStartPos = shallowIndexOf( sql, SELECT_WITH_SPACE, 0 );
 			// Place TOP after SELECT.
 			sql.insert( selectStartPos + SELECT.length(), " TOP(?)" );
 		}
 		topAdded = true;
 	}
 
 	/**
 	 * Returns index of the first case-insensitive match of search term surrounded by spaces
 	 * that is not enclosed in parentheses.
 	 *
 	 * @param sb String to search.
 	 * @param search Search term.
 	 * @param fromIndex The index from which to start the search.
 	 *
 	 * @return Position of the first match, or {@literal -1} if not found.
 	 */
 	private static int shallowIndexOfWord(final StringBuilder sb, final String search, int fromIndex) {
 		final int index = shallowIndexOf( sb, ' ' + search + ' ', fromIndex );
 		// In case of match adding one because of space placed in front of search term.
 		return index != -1 ? ( index + 1 ) : -1;
 	}
 
 	/**
 	 * Returns index of the first case-insensitive match of search term that is not enclosed in parentheses.
 	 *
 	 * @param sb String to search.
 	 * @param search Search term.
 	 * @param fromIndex The index from which to start the search.
 	 *
 	 * @return Position of the first match, or {@literal -1} if not found.
 	 */
 	private static int shallowIndexOf(StringBuilder sb, String search, int fromIndex) {
 		// case-insensitive match
 		final String lowercase = sb.toString().toLowerCase();
 		final int len = lowercase.length();
 		final int searchlen = search.length();
 		int pos = -1;
 		int depth = 0;
 		int cur = fromIndex;
 		do {
 			pos = lowercase.indexOf( search, cur );
 			if ( pos != -1 ) {
 				for ( int iter = cur; iter < pos; iter++ ) {
 					final char c = sb.charAt( iter );
 					if ( c == '(' ) {
 						depth = depth + 1;
 					}
 					else if ( c == ')' ) {
 						depth = depth - 1;
 					}
 				}
 				cur = pos + searchlen;
 			}
 		} while ( cur < len && depth != 0 && pos != -1 );
 		return depth == 0 ? pos : -1;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/TopLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/TopLimitHandler.java
new file mode 100644
index 0000000000..c63ed9de31
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/TopLimitHandler.java
@@ -0,0 +1,74 @@
+/* 
+ * Hibernate, Relational Persistence for Idiomatic Java
+ * 
+ * JBoss, Home of Professional Open Source
+ * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
+ * as indicated by the @authors tag. All rights reserved.
+ * See the copyright.txt in the distribution for a
+ * full listing of individual contributors.
+ *
+ * This copyrighted material is made available to anyone wishing to use,
+ * modify, copy, or redistribute it subject to the terms and conditions
+ * of the GNU Lesser General Public License, v. 2.1.
+ * This program is distributed in the hope that it will be useful, but WITHOUT A
+ * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
+ * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
+ * You should have received a copy of the GNU Lesser General Public License,
+ * v.2.1 along with this distribution; if not, write to the Free Software
+ * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
+ * MA  02110-1301, USA.
+ */
+package org.hibernate.dialect.pagination;
+
+import org.hibernate.engine.spi.RowSelection;
+
+
+/**
+ * @author Brett Meyer
+ */
+public class TopLimitHandler extends AbstractLimitHandler {
+	
+	private final boolean supportsVariableLimit;
+	
+	private final boolean bindLimitParametersFirst;
+
+	public TopLimitHandler(boolean supportsVariableLimit, boolean bindLimitParametersFirst) {
+		this.supportsVariableLimit = supportsVariableLimit;
+		this.bindLimitParametersFirst = bindLimitParametersFirst;
+	}
+
+	@Override
+	public boolean supportsLimit() {
+		return true;
+	}
+	
+	@Override
+	public boolean useMaxForLimit() {
+		return true;
+	}
+
+	@Override
+	public boolean supportsLimitOffset() {
+		return supportsVariableLimit;
+	}
+	
+	public boolean bindLimitParametersFirst() {
+		return bindLimitParametersFirst;
+	}
+
+	@Override
+	public String processSql(String sql, RowSelection selection) {
+		if (LimitHelper.hasFirstRow( selection )) {
+			throw new UnsupportedOperationException( "query result offset is not supported" );
+		}
+
+		final int selectIndex = sql.toLowerCase().indexOf( "select" );
+		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
+		final int insertionPoint = selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
+
+		return new StringBuilder( sql.length() + 8 )
+				.append( sql )
+				.insert( insertionPoint, " TOP ? " )
+				.toString();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index b836f37561..d1d8ff00e0 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -808,1918 +808,1916 @@ public abstract class Loader {
 						// this part copied from #getRow, this section could be refactored out
 						Object object = session.getEntityUsingInterceptor( keys[targetIndex] );
 						if ( object != null ) {
 							//its already loaded so don't need to hydrate it
 							instanceAlreadyLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									keys[targetIndex],
 									object,
 									lockModes[targetIndex],
 									session
 							);
 						}
 						else {
 							instanceNotYetLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									getEntityAliases()[targetIndex].getRowIdAlias(),
 									keys[targetIndex],
 									lockModes[targetIndex],
 									getOptionalObjectKey( queryParameters, session ),
 									queryParameters.getOptionalObject(),
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
 						null; //if null, owner will be retrieved from session
 
 				final CollectionPersister collectionPersister = collectionPersisters[i];
 				final Serializable key;
 				if ( owner == null ) {
 					key = null;
 				}
 				else {
 					key = collectionPersister.getCollectionType().getKeyOfOwner( owner, session );
 					//TODO: old version did not require hashmap lookup:
 					//keys[collectionOwner].getIdentifier()
 				}
 
 				readCollectionElement(
 						owner,
 						key,
 						collectionPersister,
 						descriptors[i],
 						resultSet,
 						session
 					);
 
 			}
 
 		}
 	}
 
 	private List doQuery(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final ResultSet rs = wrapper.getResultSet();
 		final Statement st = wrapper.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SessionImplementor session,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			int maxRows,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
 		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 		LOG.trace( "Processing result set" );
 		int count;
 
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
 			if ( DEBUG_ENABLED )
 				LOG.debugf( "Result set row: %s", count );
 			Object result = getRowFromResultSet(
 					rs,
 					session,
 					queryParameters,
 					lockModesArray,
 					optionalObjectKey,
 					hydratedObjects,
 					keys,
 					returnProxies,
 					forcedResultTransformer
 			);
 			results.add( result );
 			if ( createSubselects ) {
 				subselectResultKeys.add(keys);
 				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				rs,
 				session,
 				queryParameters.isReadOnly( session ),
 				afterLoadActions
 		);
 		if ( createSubselects ) {
 			createSubselects( subselectResultKeys, queryParameters, session );
 		}
 		return results;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for ( Loadable loadable : loadables ) {
 			if ( loadable.hasSubselectLoadableCollections() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( Object key : keys ) {
 				result[j].add( ( (EntityKey[]) key )[j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
 								aliases[i],
 								loadables[i],
 								queryParameters,
 								keySets[i],
 								namedParameterLocMap
 							);
 
 						session.getPersistenceContext()
 								.getBatchFetchQueue()
 								.addSubselect( rowKeys[i], subselectFetch );
 					}
 
 				}
 
 			}
 		}
 	}
 
 	private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			for(String name : queryParameters.getNamedParameters().keySet()){
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
 				);
 			}
 			return namedParameterLocMap;
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.<AfterLoadAction>emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 		//important: reuse the same event instances for performance!
 		final PreLoadEvent pre;
 		final PostLoadEvent post;
 		if ( session.isEventSource() ) {
 			pre = new PreLoadEvent( (EventSource) session );
 			post = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			pre = null;
 			post = null;
 		}
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 		
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedObjects != null ) {
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.postLoad( hydratedObject, session, post );
 				if ( afterLoadActions != null ) {
 					for ( AfterLoadAction afterLoadAction : afterLoadActions ) {
 						final EntityEntry entityEntry = session.getPersistenceContext().getEntry( hydratedObject );
 						if ( entityEntry == null ) {
 							// big problem
 							throw new HibernateException( "Could not locate EntityEntry immediately after two-phase load" );
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
 				}
 			}
 		}
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SessionImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		 return null;
 	}
 
 	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
 	private void registerNonExists(
 			final EntityKey[] keys,
 			final Loadable[] persisters,
 			final SessionImplementor session) {
 
 		final int[] owners = getOwners();
 		if ( owners != null ) {
 
 			EntityType[] ownerAssociationTypes = getOwnerAssociationTypes();
 			for ( int i = 0; i < keys.length; i++ ) {
 
 				int owner = owners[i];
 				if ( owner > -1 ) {
 					EntityKey ownerKey = keys[owner];
 					if ( keys[i] == null && ownerKey != null ) {
 
 						final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 						/*final boolean isPrimaryKey;
 						final boolean isSpecialOneToOne;
 						if ( ownerAssociationTypes == null || ownerAssociationTypes[i] == null ) {
 							isPrimaryKey = true;
 							isSpecialOneToOne = false;
 						}
 						else {
 							isPrimaryKey = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()==null;
 							isSpecialOneToOne = ownerAssociationTypes[i].getLHSPropertyName()!=null;
 						}*/
 
 						//TODO: can we *always* use the "null property" approach for everything?
 						/*if ( isPrimaryKey && !isSpecialOneToOne ) {
 							persistenceContext.addNonExistantEntityKey(
 									new EntityKey( ownerKey.getIdentifier(), persisters[i], session.getEntityMode() )
 							);
 						}
 						else if ( isSpecialOneToOne ) {*/
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
 						}
 						/*}
 						else {
 							persistenceContext.addNonExistantEntityUniqueKey( new EntityUniqueKey(
 									persisters[i].getEntityName(),
 									ownerAssociationTypes[i].getRHSUniqueKeyPropertyName(),
 									ownerKey.getIdentifier(),
 									persisters[owner].getIdentifierType(),
 									session.getEntityMode()
 							) );
 						}*/
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read one collection element from the current row of the JDBC result set
 	 */
 	private void readCollectionElement(
 			final Object optionalOwner,
 			final Serializable optionalKey,
 			final CollectionPersister persister,
 			final CollectionAliases descriptor,
 			final ResultSet rs,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		final Serializable collectionRowKey = (Serializable) persister.readKey(
 				rs,
 				descriptor.getSuffixedKeyAliases(),
 				session
 			);
 
 		if ( collectionRowKey != null ) {
 			// we found a collection element in the result set
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Found row of collection: %s",
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) );
 			}
 
 			Object owner = optionalOwner;
 			if ( owner == null ) {
 				owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
 				if ( owner == null ) {
 					//TODO: This is assertion is disabled because there is a bug that means the
 					//	  original owner of a transient, uninitialized collection is not known
 					//	  if the collection is re-referenced by a different object associated
 					//	  with the current Session
 					//throw new AssertionFailure("bug loading unowned collection");
 				}
 			}
 
 			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, collectionRowKey );
 
 			if ( rowCollection != null ) {
 				rowCollection.readFrom( rs, persister, descriptor, owner );
 			}
 
 		}
 		else if ( optionalKey != null ) {
 			// we did not find a collection element in the result set, so we
 			// ensure that a collection is created with the owner's identifier,
 			// since what we have is an empty collection
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Result set contains (possibly empty) collection: %s",
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) );
 			}
 
 			persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 		}
 
 		// else no collection element, but also no owner
 
 	}
 
 	/**
 	 * If this is a collection initializer, we need to tell the session that a collection
 	 * is being initialized, to account for the possibility of the collection having
 	 * no elements (hence no rows in the result set).
 	 */
 	private void handleEmptyCollections(
 			final Serializable[] keys,
 			final Object resultSetId,
 			final SessionImplementor session) {
 
 		if ( keys != null ) {
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
 					if ( debugEnabled ) {
 						LOG.debugf( "Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) );
 					}
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( ( ResultSet ) resultSetId )
 							.getLoadingCollection( collectionPersisters[j], keys[i] );
 				}
 			}
 		}
 
 		// else this is not a collection initializer (and empty collections will
 		// be detected by looking for the owner's identifier in the result set)
 	}
 
 	/**
 	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array.
 	 * Warning: this method is side-effecty.
 	 * <p/>
 	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
 	 */
 	private EntityKey getKeyFromResultSet(
 			final int i,
 			final Loadable persister,
 			final Serializable id,
 			final ResultSet rs,
 			final SessionImplementor session) throws HibernateException, SQLException {
 
 		Serializable resultId;
 
 		// if we know there is exactly 1 row, we can skip.
 		// it would be great if we could _always_ skip this;
 		// it is a problem for <key-many-to-one>
 
 		if ( isSingleRowLoader() && id != null ) {
 			resultId = id;
 		}
 		else {
 
 			Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 				);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ? null : session.generateEntityKey( resultId, persister );
 	}
 
 	/**
 	 * Check the version of the object in the <tt>ResultSet</tt> against
 	 * the object version in the session cache, throwing an exception
 	 * if the version numbers are different
 	 */
 	private void checkVersion(
 			final int i,
 			final Loadable persister,
 			final Serializable id,
 			final Object entity,
 			final ResultSet rs,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 				);
 			if ( !versionType.isEqual(version, currentVersion) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 		}
 
 	}
 
 	/**
 	 * Resolve any IDs for currently loaded objects, duplications within the
 	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the
 	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an
 	 * array of booleans (by side-effect) that determine whether the corresponding
 	 * object should be initialized.
 	 */
 	private Object[] getRow(
 			final ResultSet rs,
 			final Loadable[] persisters,
 			final EntityKey[] keys,
 			final Object optionalObject,
 			final EntityKey optionalObjectKey,
 			final LockMode[] lockModes,
 			final List hydratedObjects,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 
 		final Object[] rowResults = new Object[cols];
 
 		for ( int i = 0; i < cols; i++ ) {
 
 			Object object = null;
 			EntityKey key = keys[i];
 
 			if ( keys[i] == null ) {
 				//do nothing
 			}
 			else {
 
 				//If the object is already loaded, return the loaded one
 				object = session.getEntityUsingInterceptor( key );
 				if ( object != null ) {
 					//its already loaded so don't need to hydrate it
 					instanceAlreadyLoaded(
 							rs,
 							i,
 							persisters[i],
 							key,
 							object,
 							lockModes[i],
 							session
 						);
 				}
 				else {
 					object = instanceNotYetLoaded(
 							rs,
 							i,
 							persisters[i],
 							descriptors[i].getRowIdAlias(),
 							key,
 							lockModes[i],
 							optionalObjectKey,
 							optionalObject,
 							hydratedObjects,
 							session
 						);
 				}
 
 			}
 
 			rowResults[i] = object;
 
 		}
 
 		return rowResults;
 	}
 
 	/**
 	 * The entity instance is already in the session cache
 	 */
 	private void instanceAlreadyLoaded(
 			final ResultSet rs,
 			final int i,
 			final Loadable persister,
 			final EntityKey key,
 			final Object object,
 			final LockMode requestedLockMode,
 			final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 			);
 		}
 
 		if ( LockMode.NONE != requestedLockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 			final EntityEntry entry = session.getPersistenceContext().getEntry( object );
 			if ( entry.getLockMode().lessThan( requestedLockMode ) ) {
 				//we only check the version when _upgrading_ lock modes
 				if ( persister.isVersioned() ) {
 					checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				}
 				//we need to upgrade the lock mode to the mode requested
 				entry.setLockMode( requestedLockMode );
 			}
 		}
 	}
 
 
 	/**
 	 * The entity instance is not in the session cache
 	 */
 	private Object instanceNotYetLoaded(
 			final ResultSet rs,
 			final int i,
 			final Loadable persister,
 			final String rowIdAlias,
 			final EntityKey key,
 			final LockMode lockMode,
 			final EntityKey optionalObjectKey,
 			final Object optionalObject,
 			final List hydratedObjects,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 		final String instanceClass = getInstanceClass(
 				rs,
 				i,
 				persister,
 				key.getIdentifier(),
 				session
 		);
 
 		// see if the entity defines reference caching, and if so use the cached reference (if one).
 		if ( session.getCacheMode().isGetEnabled() && persister.canUseReferenceCacheEntries() ) {
 			final Object cachedEntry = CacheHelper.fromSharedCache(
 					session,
 					session.generateCacheKey(
 							key.getIdentifier(),
 							persister.getEntityMetamodel().getEntityType(),
 							key.getEntityName()
 					),
 					persister.getCacheAccessStrategy()
 			);
 			if ( cachedEntry != null ) {
 				CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( cachedEntry, factory );
 				return ( (ReferenceCacheEntryImpl) entry ).getReference();
 			}
 		}
 
 		final Object object;
 		if ( optionalObjectKey != null && key.equals( optionalObjectKey ) ) {
 			//its the given optional object
 			object = optionalObject;
 		}
 		else {
 			// instantiate a new instance
 			object = session.instantiate( instanceClass, key.getIdentifier() );
 		}
 
 		//need to hydrate it.
 
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		LockMode acquiredLockMode = lockMode == LockMode.NONE ? LockMode.READ : lockMode;
 		loadFromResultSet(
 				rs,
 				i,
 				object,
 				instanceClass,
 				key,
 				rowIdAlias,
 				acquiredLockMode,
 				persister,
 				session
 			);
 
 		//materialize associations (and initialize the object) later
 		hydratedObjects.add( object );
 
 		return object;
 	}
 
 	private boolean isEagerPropertyFetchEnabled(int i) {
 		boolean[] array = getEntityEagerPropertyFetches();
 		return array!=null && array[i];
 	}
 
 
 	/**
 	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
 	 * an array or "hydrated" values (do not resolve associations yet),
 	 * and pass the hydrates state to the session.
 	 */
 	private void loadFromResultSet(
 			final ResultSet rs,
 			final int i,
 			final Object object,
 			final String instanceEntityName,
 			final EntityKey key,
 			final String rowIdAlias,
 			final LockMode lockMode,
 			final Loadable rootPersister,
 			final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
 		if ( LOG.isTraceEnabled() )
 			LOG.tracev( "Initializing object from ResultSet: {0}", MessageHelper.infoString( persister, id, getFactory() ) );
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled(i);
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 			);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases(persister);
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 			);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if (ukName!=null) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex(ukName);
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, object ),
 						type,
 						persister.getEntityMode(),
 						session.getFactory()
 				);
 				session.getPersistenceContext().addEntity( euk, object );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				object,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 		);
 
 	}
 
 	/**
 	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
 	 */
 	private String getInstanceClass(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedDiscriminatorAlias(),
 					session,
 					null
 				);
 
 			final String result = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 
 			if ( result == null ) {
 				//woops we got an instance of another class hierarchy branch
 				throw new WrongClassException(
 						"Discriminator: " + discriminatorValue,
 						id,
 						persister.getEntityName()
 					);
 			}
 
 			return result;
 
 		}
 		else {
 			return persister.getEntityName();
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	private void advance(final ResultSet rs, final RowSelection selection)
 			throws SQLException {
 
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) rs.next();
 			}
 		}
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
-	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
-	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
-		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
-		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
+	protected LimitHandler getLimitHandler(RowSelection selection) {
+		final LimitHandler limitHandler = getFactory().getDialect().getLimitHandler();
+		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : NoopLimitHandler.INSTANCE;
 	}
 
 	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Process query string by applying filters, LIMIT clause, locks and comments if necessary.
 	 * Finally execute SQL statement and advance to the first row.
 	 */
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
-				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
-		String sql = limitHandler.getProcessedSql();
+		String sql = limitHandler.processSql( queryParameters.getFilteredSQL(), queryParameters.getRowSelection() );
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper( st, getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session ) );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        String sql,
 	        final QueryParameters queryParameters,
 	        final LimitHandler limitHandler,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 		
 		PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
-			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
+			col += limitHandler.bindLimitParametersAtStartOfQuery( selection, st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
-			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
+			col += limitHandler.bindLimitParametersAtEndOfQuery( selection, st, col );
 
-			limitHandler.setMaxRows( st );
+			limitHandler.setMaxRows( selection, st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( LOG.isDebugEnabled() ) {
 							LOG.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Bound [{0}] parameters total", col );
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SessionImplementor session) throws SQLException {
 		int span = 0;
 		span += bindPositionalParameters( statement, queryParameters, startIndex, session );
 		span += bindNamedParameters( statement, queryParameters.getNamedParameters(), startIndex + span, session );
 		return span;
 	}
 
 	/**
 	 * Bind positional parameter values to the JDBC prepared statement.
 	 * <p/>
 	 * Positional parameters are those specified by JDBC-style ? parameters
 	 * in the source query.  It is (currently) expected that these come
 	 * before any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( statement, values[i], startIndex + span, session );
 			span += types[i].getColumnSpan( getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Bind named parameters to the JDBC prepared statement.
 	 * <p/>
 	 * This is a generic implementation, the problem being that in the
 	 * general case we do not know enough information about the named
 	 * parameters to perform this in a complete manner here.  Thus this
 	 * is generally overridden on subclasses allowing named parameters to
 	 * apply the specific behavior.  The most usual limitation here is that
 	 * we need to assume the type span is always one...
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param namedParams A map of parameter names to values
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map<String, TypedValue> namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		int result = 0;
 		if ( CollectionHelper.isEmpty( namedParams ) ) {
 			return result;
 		}
 
 		for ( String name : namedParams.keySet() ) {
 			TypedValue typedValue = namedParams.get( name );
 			int columnSpan = typedValue.getType().getColumnSpan( getFactory() );
 			int[] locs = getNamedParameterLocs( name );
 			for ( int loc : locs ) {
 				if ( DEBUG_ENABLED ) {
 					LOG.debugf(
 							"bindNamedParameters() %s -> %s [%s]",
 							typedValue.getValue(),
 							name,
 							loc + startIndex
 					);
 				}
 				int start = loc * columnSpan + startIndex;
 				typedValue.getType().nullSafeSet( statement, typedValue.getValue(), start, session );
 			}
 			result += locs.length;
 		}
 		return result;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 			final PreparedStatement st,
 			final RowSelection selection,
 			final LimitHandler limitHandler,
 			final boolean autodiscovertypes,
 			final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		try {
 			ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 
 	}
 
 	private ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 				LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(final ResultSet rs) throws SQLException {
 		final ColumnNameCache cache = columnNameCache;
 		if ( cache == null ) {
 			//there is no need for a synchronized second check, as in worst case
 			//we'll have allocated an unnecessary ColumnNameCache
 			LOG.trace( "Building columnName -> columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 			return columnNameCache;
 		}
 		else {
 			return cache;
 		}
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	protected final List loadEntity(
 			final SessionImplementor session,
 			final Object id,
 			final Type identifierType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalIdentifier,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Loading entity: %s", MessageHelper.infoString( persister, id, identifierType, getFactory() ) );
 		}
 
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] { identifierType } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity: " +
 			        MessageHelper.infoString( persisters[persisters.length-1], id, identifierType, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 	        final SessionImplementor session,
 	        final Object key,
 	        final Object index,
 	        final Type keyType,
 	        final Type indexType,
 	        final EntityPersister persister) throws HibernateException {
 
 		LOG.debug( "Loading collection element by index" );
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] { keyType, indexType },
 							new Object[] { key, index }
 					),
 					false
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load collection element by index",
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by wrappers that batch load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	public final List loadEntityBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type idType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 
 		Type[] types = new Type[ids.length];
 		Arrays.fill( types, idType );
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( types );
 			qp.setPositionalParameterValues( ids );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalId );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity batch: " +
 			        MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ) );
 
 		Serializable[] ids = new Serializable[]{id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[]{type}, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 				);
 		}
 
 		LOG.debug( "Done loading collection" );
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ) );
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection batch: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 					getSQLString()
 				);
 		}
 
 		LOG.debug( "Done batch load" );
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Object[] parameterValues,
 			final Type[] parameterTypes,
 			final Map<String, TypedValue> namedParameters,
 			final Type type) throws HibernateException {
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections( session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load collection by subselect: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 					getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Return the query results, using the query cache, called
 	 * by subclasses that implement cacheable queries
 	 */
 	protected List list(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes) throws HibernateException {
 
 		final boolean cacheable = factory.getSettings().isQueryCacheEnabled() &&
 			queryParameters.isCacheable();
 
 		if ( cacheable ) {
 			return listUsingQueryCache( session, queryParameters, querySpaces, resultTypes );
 		}
 		else {
 			return listIgnoreQueryCache( session, queryParameters );
 		}
 	}
 
 	private List listIgnoreQueryCache(SessionImplementor session, QueryParameters queryParameters) {
 		return getResultList( doList( session, queryParameters ), queryParameters.getResultTransformer() );
 	}
 
 	private List listUsingQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
 		if ( querySpaces == null || querySpaces.size() == 0 )
 			LOG.tracev( "Unexpected querySpaces is {0}", ( querySpaces == null ? querySpaces : "empty" ) );
 		else {
 			LOG.tracev( "querySpaces is {0}", querySpaces );
 		}
 
 		List result = getResultFromQueryCache(
 				session,
 				queryParameters,
 				querySpaces,
 				resultTypes,
 				queryCache,
 				key
 			);
 
 		if ( result == null ) {
 			result = doList( session, queryParameters, key.getResultTransformer() );
 
 			putResultInQueryCache(
 					session,
 					queryParameters,
 					resultTypes,
 					queryCache,
 					key,
 					result
 			);
 		}
 
 		ResultTransformer resolvedTransformer = resolveResultTransformer( queryParameters.getResultTransformer() );
 		if ( resolvedTransformer != null ) {
 			result = (
 					areResultSetRowsTransformedImmediately() ?
 							key.getResultTransformer().retransformResults(
 									result,
 									getResultRowAliases(),
 									queryParameters.getResultTransformer(),
 									includeInResultRow()
 							) :
 							key.getResultTransformer().untransformToTuples(
 									result
 							)
 			);
 		}
 
 		return getResultList( result, queryParameters.getResultTransformer() );
 	}
 
 	private QueryKey generateQueryKey(
 			SessionImplementor session,
 			QueryParameters queryParameters) {
 		return QueryKey.generateQueryKey(
 				getSQLString(),
 				queryParameters,
 				FilterKey.createFilterKeys( session.getLoadQueryInfluencers().getEnabledFilters() ),
 				session,
 				createCacheableResultTransformer( queryParameters )
 		);
 	}
 
 	private CacheableResultTransformer createCacheableResultTransformer(QueryParameters queryParameters) {
 		return CacheableResultTransformer.create(
 				queryParameters.getResultTransformer(),
 				getResultRowAliases(),
 				includeInResultRow()
 		);
 	}
 
 	private List getResultFromQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key) {
 		List result = null;
 
 		if ( session.getCacheMode().isGetEnabled() ) {
 			boolean isImmutableNaturalKeyLookup =
 					queryParameters.isNaturalKeyLookup() &&
 							resultTypes.length == 1 &&
 							resultTypes[0].isEntityType() &&
 							getEntityPersister( EntityType.class.cast( resultTypes[0] ) )
 									.getEntityMetamodel()
 									.hasImmutableNaturalId();
 
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
 			if ( queryParameters.isReadOnlyInitialized() ) {
 				// The read-only/modifiable mode for the query was explicitly set.
 				// Temporarily set the default read-only/modifiable setting to the query's setting.
 				persistenceContext.setDefaultReadOnly( queryParameters.isReadOnly() );
 			}
 			else {
 				// The read-only/modifiable setting for the query was not initialized.
 				// Use the default read-only/modifiable from the persistence context instead.
 				queryParameters.setReadOnly( persistenceContext.isDefaultReadOnly() );
 			}
 			try {
 				result = queryCache.get(
 						key,
 						key.getResultTransformer().getCachedResultTypes( resultTypes ),
 						isImmutableNaturalKeyLookup,
 						querySpaces,
 						session
 				);
 			}
 			finally {
 				persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 			}
 
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				if ( result == null ) {
 					factory.getStatisticsImplementor()
 							.queryCacheMiss( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 				else {
 					factory.getStatisticsImplementor()
 							.queryCacheHit( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private EntityPersister getEntityPersister(EntityType entityType) {
 		return factory.getEntityPersister( entityType.getAssociatedEntityName() );
 	}
 
 	protected void putResultInQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		if ( session.getCacheMode().isPutEnabled() ) {
 			boolean put = queryCache.put(
 					key,
 					key.getResultTransformer().getCachedResultTypes( resultTypes ),
 					result,
 					queryParameters.isNaturalKeyLookup(),
 					session
 			);
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor()
 						.queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
 
 	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
 		return doList( session, queryParameters, null);
 	}
 
 	private List doList(final SessionImplementor session,
 						final QueryParameters queryParameters,
 						final ResultTransformer forcedResultTransformer)
 			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.nanoTime();
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query",
 					getSQLString()
 				);
 		}
 
 		if ( stats ) {
 			final long endTime = System.nanoTime();
 			final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 			getFactory().getStatisticsImplementor().queryExecuted(
 					getQueryIdentifier(),
 					result.size(),
 					milliseconds
 				);
 		}
 
 		return result;
 	}
 
 	/**
 	 * Check whether the current loader can support returning ScrollableResults.
 	 *
 	 * @throws HibernateException
 	 */
 	protected void checkScrollability() throws HibernateException {
 		// Allows various loaders (ok mainly the QueryLoader :) to check
 		// whether scrolling of their result set should be allowed.
 		//
 		// By default it is allowed.
 	}
 
 	/**
 	 * Does the result set to be scrolled contain collection fetches?
 	 *
 	 * @return True if it does, and thus needs the special fetching scroll
 	 * functionality; false otherwise.
 	 */
 	protected boolean needsFetchingScroll() {
 		return false;
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 *
 	 * @param queryParameters The parameters with which the query should be executed.
 	 * @param returnTypes The expected return types of the query
 	 * @param holderInstantiator If the return values are expected to be wrapped
 	 * in a holder, this is the thing that knows how to wrap them.
 	 * @param session The session from which the scroll request originated.
 	 * @return The ScrollableResults instance.
 	 * @throws HibernateException Indicates an error executing the query, or constructing
 	 * the ScrollableResults.
 	 */
 	protected ScrollableResults scroll(
 			final QueryParameters queryParameters,
 			final Type[] returnTypes,
 			final HolderInstantiator holderInstantiator,
 			final SessionImplementor session) throws HibernateException {
 
 		checkScrollability();
 
 		final boolean stats = getQueryIdentifier() != null &&
 				getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.nanoTime();
 
 		try {
 			// Don't use Collections#emptyList() here -- follow on locking potentially adds AfterLoadActions,
 			// so the list cannot be immutable.
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, true, new ArrayList<AfterLoadAction>(), session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 
 			if ( stats ) {
 				final long endTime = System.nanoTime();
 				final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 				getFactory().getStatisticsImplementor().queryExecuted(
 						getQueryIdentifier(),
 						0,
 						milliseconds
 					);
 			}
 
 			if ( needsFetchingScroll() ) {
 				return new FetchingScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
 						returnTypes,
 						holderInstantiator
 					);
 			}
 			else {
 				return new ScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
 						returnTypes,
 						holderInstantiator
 					);
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query using scroll",
 					getSQLString()
 				);
 		}
 
 	}
 
 	/**
 	 * Calculate and cache select-clause suffixes. Must be
 	 * called by subclasses after instantiation.
 	 */
 	protected void postInstantiate() {}
 
 	/**
 	 * Get the result set descriptor
 	 */
 	protected abstract EntityAliases[] getEntityAliases();
 
 	protected abstract CollectionAliases[] getCollectionAliases();
 
 	/**
 	 * Identifies the query for statistics reporting, if null,
 	 * no statistics will be reported
 	 */
 	protected String getQueryIdentifier() {
 		return null;
 	}
 
 	public final SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getSQLString() + ')';
 	}
 
 	/**
 	 * Wrapper class for {@link Statement} and associated {@link ResultSet}.
 	 */
 	protected static class SqlStatementWrapper {
 		private final Statement statement;
 		private final ResultSet resultSet;
 
 		private SqlStatementWrapper(Statement statement, ResultSet resultSet) {
 			this.resultSet = resultSet;
 			this.statement = statement;
 		}
 
 		public ResultSet getResultSet() {
 			return resultSet;
 		}
 
 		public Statement getStatement() {
 			return statement;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AbstractLoadPlanBasedLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AbstractLoadPlanBasedLoader.java
index e8b155e2e5..96721a4073 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AbstractLoadPlanBasedLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AbstractLoadPlanBasedLoader.java
@@ -1,537 +1,535 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
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
  *
  */
 package org.hibernate.loader.plan.exec.internal;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.ScrollMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
 import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A superclass for loader implementations based on using LoadPlans.
  *
  * @see org.hibernate.loader.entity.plan.EntityLoader
  * @see org.hibernate.loader.collection.plan.CollectionLoader
 
  * @author Gail Badner
  */
 public abstract class AbstractLoadPlanBasedLoader {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( AbstractLoadPlanBasedLoader.class );
 
 	private final SessionFactoryImplementor factory;
 
 	private ColumnNameCache columnNameCache;
 
 	/**
 	 * Constructs a {@link AbstractLoadPlanBasedLoader}.
 	 *
 	 * @param factory The session factory
 	 * @see SessionFactoryImplementor
 	 */
 	public AbstractLoadPlanBasedLoader(
 			SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected abstract LoadQueryDetails getStaticLoadQuery();
 
 	protected abstract int[] getNamedParameterLocs(String name);
 
 	protected abstract void autoDiscoverTypes(ResultSet rs);
 
 	protected List executeLoad(
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			LoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException {
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 		return executeLoad(
 				session,
 				queryParameters,
 				loadQueryDetails,
 				returnProxies,
 				forcedResultTransformer,
 				afterLoadActions
 		);
 	}
 
 	protected List executeLoad(
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			LoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
 		if ( queryParameters.isReadOnlyInitialized() ) {
 			// The read-only/modifiable mode for the query was explicitly set.
 			// Temporarily set the default read-only/modifiable setting to the query's setting.
 			persistenceContext.setDefaultReadOnly( queryParameters.isReadOnly() );
 		}
 		else {
 			// The read-only/modifiable setting for the query was not initialized.
 			// Use the default read-only/modifiable from the persistence context instead.
 			queryParameters.setReadOnly( persistenceContext.isDefaultReadOnly() );
 		}
 		persistenceContext.beforeLoad();
 		try {
 			List results = null;
 			final String sql = loadQueryDetails.getSqlStatement();
 			SqlStatementWrapper wrapper = null;
 			try {
 				wrapper = executeQueryStatement( sql, queryParameters, false, afterLoadActions, session );
 				results = loadQueryDetails.getResultSetProcessor().extractResults(
 						wrapper.getResultSet(),
 						session,
 						queryParameters,
 						new NamedParameterContext() {
 							@Override
 							public int[] getNamedParameterLocations(String name) {
 								return AbstractLoadPlanBasedLoader.this.getNamedParameterLocs( name );
 							}
 						},
 						returnProxies,
 						queryParameters.isReadOnly(),
 						forcedResultTransformer,
 						afterLoadActions
 				);
 			}
 			finally {
 				if ( wrapper != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release(
 							wrapper.getResultSet(),
 							wrapper.getStatement()
 					);
 					session.getTransactionCoordinator().getJdbcCoordinator().release( wrapper.getStatement() );
 				}
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 			return results;
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getStaticLoadQuery().getSqlStatement(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
-				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
-		String sql = limitHandler.getProcessedSql();
+		String sql = limitHandler.processSql( queryParameters.getFilteredSQL(), queryParameters.getRowSelection() );
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper( st, getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session ) );
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link org.hibernate.dialect.pagination.NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
-	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
-	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
-		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
-		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
+	protected LimitHandler getLimitHandler(RowSelection selection) {
+		final LimitHandler limitHandler = getFactory().getDialect().getLimitHandler();
+		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : NoopLimitHandler.INSTANCE;
 	}
 
 	private String preprocessSQL(
 			String sql,
 			QueryParameters queryParameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		return getFactory().getSettings().isCommentsEnabled()
 				? prependComment( sql, queryParameters )
 				: sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		final String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return "/* " + comment + " */ " + sql;
 		}
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 			final String sql,
 			final QueryParameters queryParameters,
 			final LimitHandler limitHandler,
 			final boolean scroll,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		final boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		final boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		final boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		final boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator()
 				.getStatementPreparer().prepareQueryStatement( sql, callable, scrollMode );
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
-			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
+			col += limitHandler.bindLimitParametersAtStartOfQuery( selection, st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
-			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
+			col += limitHandler.bindLimitParametersAtEndOfQuery( selection, st, col );
 
-			limitHandler.setMaxRows( st );
+			limitHandler.setMaxRows( selection, st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			final LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( log.isDebugEnabled() ) {
 							log.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( log.isTraceEnabled() ) {
 				log.tracev( "Bound [{0}] parameters total", col );
 			}
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw he;
 		}
 
 		return st;
 	}
 
 	protected ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SessionImplementor session) throws SQLException {
 		int span = 0;
 		span += bindPositionalParameters( statement, queryParameters, startIndex, session );
 		span += bindNamedParameters( statement, queryParameters.getNamedParameters(), startIndex + span, session );
 		return span;
 	}
 
 	/**
 	 * Bind positional parameter values to the JDBC prepared statement.
 	 * <p/>
 	 * Positional parameters are those specified by JDBC-style ? parameters
 	 * in the source query.  It is (currently) expected that these come
 	 * before any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( statement, values[i], startIndex + span, session );
 			span += types[i].getColumnSpan( getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Bind named parameters to the JDBC prepared statement.
 	 * <p/>
 	 * This is a generic implementation, the problem being that in the
 	 * general case we do not know enough information about the named
 	 * parameters to perform this in a complete manner here.  Thus this
 	 * is generally overridden on subclasses allowing named parameters to
 	 * apply the specific behavior.  The most usual limitation here is that
 	 * we need to assume the type span is always one...
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param namedParams A map of parameter names to values
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			final Iterator itr = namedParams.entrySet().iterator();
 			final boolean debugEnabled = log.isDebugEnabled();
 			int result = 0;
 			while ( itr.hasNext() ) {
 				final Map.Entry e = (Map.Entry) itr.next();
 				final String name = (String) e.getKey();
 				final TypedValue typedval = (TypedValue) e.getValue();
 				final int[] locs = getNamedParameterLocs( name );
 				for ( int loc : locs ) {
 					if ( debugEnabled ) {
 						log.debugf(
 								"bindNamedParameters() %s -> %s [%s]",
 								typedval.getValue(),
 								name,
 								loc + startIndex
 						);
 					}
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), loc + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 			final PreparedStatement st,
 			final RowSelection selection,
 			final LimitHandler limitHandler,
 			final boolean autodiscovertypes,
 			final SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		try {
 			ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	protected void advance(final ResultSet rs, final RowSelection selection) throws SQLException {
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) {
 					rs.next();
 				}
 			}
 		}
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 				if ( log.isDebugEnabled() ) {
 					log.debugf( "Wrapping result set [%s]", rs );
 				}
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				log.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			log.trace( "Building columnName->columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Wrapper class for {@link java.sql.Statement} and associated {@link java.sql.ResultSet}.
 	 */
 	protected static class SqlStatementWrapper {
 		private final Statement statement;
 		private final ResultSet resultSet;
 
 		private SqlStatementWrapper(Statement statement, ResultSet resultSet) {
 			this.resultSet = resultSet;
 			this.statement = statement;
 		}
 
 		public ResultSet getResultSet() {
 			return resultSet;
 		}
 
 		public Statement getStatement() {
 			return statement;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/dialect/SQLServer2005DialectTestCase.java b/hibernate-core/src/test/java/org/hibernate/dialect/SQLServer2005DialectTestCase.java
index 35aa9cf8c1..5dbc6cfc49 100644
--- a/hibernate-core/src/test/java/org/hibernate/dialect/SQLServer2005DialectTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/dialect/SQLServer2005DialectTestCase.java
@@ -1,216 +1,215 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * Unit test of the behavior of the SQLServerDialect utility methods
  *
  * @author Valotasion Yoryos
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class SQLServer2005DialectTestCase extends BaseUnitTestCase {
 	private SQLServer2005Dialect dialect;
 
 	@Before
 	public void setup() {
 		dialect = new SQLServer2005Dialect();
 	}
 
 	@After
 	public void tearDown() {
 		dialect = null;
 	}
 
 	@Test
 	public void testGetLimitString() {
 		String input = "select distinct f1 as f53245 from table849752 order by f234, f67 desc";
 
 		assertEquals(
-				"with query as (select inner_query.*, row_number() over (order by current_timestamp) as __hibernate_row_nr__ from ( " +
-						"select distinct top(?) f1 as f53245 from table849752 order by f234, f67 desc ) inner_query )" +
-						" select f53245 from query where __hibernate_row_nr__ >= ? and __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( input, toRowSelection( 10, 15 ) ).getProcessedSql().toLowerCase()
-		);
+			"with query as (select inner_query.*, row_number() over (order by current_timestamp) as __hibernate_row_nr__ from ( " +
+				"select distinct top(?) f1 as f53245 from table849752 order by f234, f67 desc ) inner_query )" +
+				" select f53245 from query where __hibernate_row_nr__ >= ? and __hibernate_row_nr__ < ?",
+			dialect.getLimitHandler().processSql( input, toRowSelection( 10, 15 ) ).toLowerCase() );
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-6950")
 	public void testGetLimitStringWithFromColumnName() {
 		final String fromColumnNameSQL = "select persistent0_.rid as rid1688_, " +
 				"persistent0_.deviationfromtarget as deviati16_1688_, " + // "from" character sequence as a part of the column name
 				"persistent0_.sortindex as sortindex1688_ " +
 				"from m_evalstate persistent0_ " +
 				"where persistent0_.customerid=?";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 						fromColumnNameSQL + " ) inner_query ) " +
 						"SELECT rid1688_, deviati16_1688_, sortindex1688_ FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( fromColumnNameSQL, toRowSelection( 1, 10 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( fromColumnNameSQL, toRowSelection( 1, 10 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8301")
 	public void testGetLimitStringAliasGeneration() {
 		final String notAliasedSQL = "select column1, column2, column3, column4 from table1";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 						"select column1 as page0_, column2 as page1_, column3 as page2_, column4 as page3_ from table1 ) inner_query ) " +
 						"SELECT page0_, page1_, page2_, page3_ FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( notAliasedSQL, toRowSelection( 3, 5 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( notAliasedSQL, toRowSelection( 3, 5 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-7019")
 	public void testGetLimitStringWithSubselect() {
 		final String subselectInSelectClauseSQL = "select persistent0_.id as col_0_0_, " +
 				"(select max(persistent1_.acceptancedate) " +
 				"from av_advisoryvariant persistent1_ " +
 				"where persistent1_.clientid=persistent0_.id) as col_1_0_ " +
 				"from c_customer persistent0_ " +
 				"where persistent0_.type='v'";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 						subselectInSelectClauseSQL + " ) inner_query ) " +
 						"SELECT col_0_0_, col_1_0_ FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( subselectInSelectClauseSQL, toRowSelection( 2, 5 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( subselectInSelectClauseSQL, toRowSelection( 2, 5 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-6728")
 	public void testGetLimitStringCaseSensitive() {
 		final String caseSensitiveSQL = "select persistent0_.id, persistent0_.uid AS tmp1, " +
 				"(select case when persistent0_.name = 'Smith' then 'Neo' else persistent0_.id end) " +
 				"from C_Customer persistent0_ " +
 				"where persistent0_.type='Va' " +
 				"order by persistent0_.Order";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 						"select TOP(?) persistent0_.id as page0_, persistent0_.uid AS tmp1, " +
 						"(select case when persistent0_.name = 'Smith' then 'Neo' else persistent0_.id end) as page1_ " +
 						"from C_Customer persistent0_ where persistent0_.type='Va' order by persistent0_.Order ) " +
 						"inner_query ) SELECT page0_, tmp1, page1_ FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( caseSensitiveSQL, toRowSelection( 1, 2 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( caseSensitiveSQL, toRowSelection( 1, 2 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-6310")
 	public void testGetLimitStringDistinctWithinAggregation() {
 		final String distinctInAggregateSQL = "select aggregate_function(distinct p.n) as f1 from table849752 p order by f1";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 						"select TOP(?) aggregate_function(distinct p.n) as f1 from table849752 p order by f1 ) inner_query ) " +
 						"SELECT f1 FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( distinctInAggregateSQL, toRowSelection( 2, 5 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( distinctInAggregateSQL, toRowSelection( 2, 5 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-7370")
 	public void testGetLimitStringWithMaxOnly() {
 		final String query = "select product2x0_.id as id0_, product2x0_.description as descript2_0_ " +
 				"from Product2 product2x0_ order by product2x0_.id";
 
 		assertEquals(
 				"select TOP(?) product2x0_.id as id0_, product2x0_.description as descript2_0_ " +
 						"from Product2 product2x0_ order by product2x0_.id",
-				dialect.buildLimitHandler( query, toRowSelection( 0, 1 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( query, toRowSelection( 0, 1 ) )
 		);
 
 		final String distinctQuery = "select distinct product2x0_.id as id0_, product2x0_.description as descript2_0_ " +
 				"from Product2 product2x0_ order by product2x0_.id";
 
 		assertEquals(
 				"select distinct TOP(?) product2x0_.id as id0_, product2x0_.description as descript2_0_ " +
 						"from Product2 product2x0_ order by product2x0_.id",
-				dialect.buildLimitHandler( distinctQuery, toRowSelection( 0, 5 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( distinctQuery, toRowSelection( 0, 5 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-7781")
 	public void testGetLimitStringWithCastOperator() {
 		final String query = "select cast(lc302_doku6_.redniBrojStavke as varchar(255)) as col_0_0_, lc302_doku6_.dokumentiID as col_1_0_ " +
 				"from LC302_Dokumenti lc302_doku6_ order by lc302_doku6_.dokumentiID DESC";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 					"select TOP(?) cast(lc302_doku6_.redniBrojStavke as varchar(255)) as col_0_0_, lc302_doku6_.dokumentiID as col_1_0_ " +
 					"from LC302_Dokumenti lc302_doku6_ order by lc302_doku6_.dokumentiID DESC ) inner_query ) " +
 					"SELECT col_0_0_, col_1_0_ FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( query, toRowSelection( 1, 3 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( query, toRowSelection( 1, 3 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8007")
 	public void testGetLimitStringSelectingMultipleColumnsFromSeveralTables() {
 		final String query = "select t1.*, t2.* from tab1 t1, tab2 t2 where t1.ref = t2.ref order by t1.id desc";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 						"select TOP(?) t1.*, t2.* from tab1 t1, tab2 t2 where t1.ref = t2.ref order by t1.id desc ) inner_query ) " +
 						"SELECT * FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( query, toRowSelection( 1, 3 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( query, toRowSelection( 1, 3 ) )
 		);
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8007")
 	public void testGetLimitStringSelectingAllColumns() {
 		final String query = "select * from tab1 t1, tab2 t2 where t1.ref = t2.ref order by t1.id desc";
 
 		assertEquals(
 				"WITH query AS (SELECT inner_query.*, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __hibernate_row_nr__ FROM ( " +
 						"select TOP(?) * from tab1 t1, tab2 t2 where t1.ref = t2.ref order by t1.id desc ) inner_query ) " +
 						"SELECT * FROM query WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?",
-				dialect.buildLimitHandler( query, toRowSelection( 1, 3 ) ).getProcessedSql()
+				dialect.getLimitHandler().processSql( query, toRowSelection( 1, 3 ) )
 		);
 	}
 
 	private RowSelection toRowSelection(int firstRow, int maxRows) {
 		RowSelection selection = new RowSelection();
 		selection.setFirstRow( firstRow );
 		selection.setMaxRows( maxRows );
 		return selection;
 	}
 }
