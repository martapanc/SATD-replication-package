diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
index 915c492f43..8e3c084207 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
@@ -1,304 +1,304 @@
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
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CharIndexFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
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
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "select @@identity";
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		//starts with 1, implicitly
 		return "identity not null";
 	}
 
 	@Override
 	public boolean supportsInsertSelectIdentity() {
 		return true;
 	}
 
 	@Override
 	public String appendIdentitySelectToInsert(String insertSQL) {
 		return insertSQL + "\nselect @@identity";
 	}
 
 	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
 		return lockOptions.getLockMode().greaterThan( LockMode.READ ) ? tableName + " holdlock" : tableName;
 	}
 
 	@Override
-	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
+	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
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
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		return "#" + baseTableName;
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		// sql-server, at least needed this dropped after use; strange!
 		return true;
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 5119f540ef..bd1e08cbbd 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -455,2001 +455,2001 @@ public abstract class Dialect implements ConversionContext {
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 	/**
 	 * Build delegate managing LIMIT clause.
 	 *
 	 * @param sql SQL query.
 	 * @param selection Selection criteria. {@code null} in case of unlimited number of rows.
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
 		return new LegacyLimitHandler( this, sql, selection );
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
-	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
+	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
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
 	}
 
 	/**
 	 * Get the comment into a form supported for column definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied before the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the table name
 	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied after the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the table name
 	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * Generate a DROP TABLE statement
 	 *
 	 * @param tableName The name of the table to drop
 	 *
 	 * @return The DROP TABLE command
 	 */
 	public String getDropTableString(String tableName) {
 		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
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
 
 	/**
 	 * Does this dialect support cascaded delete on foreign key definitions?
 	 *
 	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
 	 */
 	public boolean supportsCascadeDelete() {
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
 	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 * @return The cross join separator
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
 	 * Renders an ordering fragment
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase() );
 		}
 		return orderByElement.toString();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java
index 9e164b9e05..6cf85a47ed 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE157Dialect.java
@@ -1,122 +1,122 @@
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
 
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect targeting Sybase Adaptive Server Enterprise (ASE) 15.7 and higher.
  * <p/>
  *
  * @author Junyan Ren
  */
 public class SybaseASE157Dialect extends SybaseASE15Dialect {
 
 	/**
 	 * Constructs a SybaseASE157Dialect
 	 */
 	public SybaseASE157Dialect() {
 		super();
 
 		registerFunction( "create_locator", new SQLFunctionTemplate( StandardBasicTypes.BINARY, "create_locator(?1, ?2)" ) );
 		registerFunction( "locator_literal", new SQLFunctionTemplate( StandardBasicTypes.BINARY, "locator_literal(?1, ?2)" ) );
 		registerFunction( "locator_valid", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "locator_valid(?1)" ) );
 		registerFunction( "return_lob", new SQLFunctionTemplate( StandardBasicTypes.BINARY, "return_lob(?1, ?2)" ) );
 		registerFunction( "setdata", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "setdata(?1, ?2, ?3)" ) );
 		registerFunction( "charindex", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "charindex(?1, ?2, ?3)" ) );
 	}
 
 	@Override
 	public String getTableTypeString() {
 		//HHH-7298 I don't know if this would break something or cause some side affects
 		//but it is required to use 'select for update'
 		return " lock datarows";
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
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String appendLockHint(LockOptions mode, String tableName) {
 		return tableName;
 	}
 
 	@Override
-	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
+	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 				if("JZ0TO".equals( sqlState ) || "JZ006".equals( sqlState )){
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				if ( 515 == errorCode && "ZZZZZ".equals( sqlState ) ) {
 					// Attempt to insert NULL value into column; column does not allow nulls.
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 				return null;
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java
index c1b006958c..e4f7434ba6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ColumnNameCache.java
@@ -1,75 +1,75 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.engine.jdbc;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 /**
  * Cache of column-name -> column-index resolutions
  *
  * @author Steve Ebersole
  */
 public class ColumnNameCache {
 	private static final float LOAD_FACTOR = .75f;
 
-	private final Map<String, Integer> columnNameToIndexCache;
+	private final ConcurrentHashMap<String, Integer> columnNameToIndexCache;
 
 	/**
 	 * Constructs a ColumnNameCache
 	 *
 	 * @param columnCount The number of columns to be cached.
 	 */
 	public ColumnNameCache(int columnCount) {
 		// should *not* need to grow beyond the size of the total number of columns in the rs
 		this.columnNameToIndexCache = new ConcurrentHashMap<String, Integer>(
 				columnCount + (int)( columnCount * LOAD_FACTOR ) + 1,
 				LOAD_FACTOR
 		);
 	}
 
 	/**
 	 * Resolve the column name/alias to its index
 	 *
 	 * @param columnName The name/alias of the column
 	 * @param rs The ResultSet
 	 *
 	 * @return The index
 	 *
 	 * @throws SQLException INdicates a problems accessing the underlying JDBC ResultSet
 	 */
 	public int getIndexForColumnName(String columnName, ResultSet rs) throws SQLException {
 		final Integer cached = columnNameToIndexCache.get( columnName );
 		if ( cached != null ) {
 			return cached;
 		}
 		else {
 			final int index = rs.findColumn( columnName );
 			columnNameToIndexCache.put( columnName, index);
 			return index;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
index d7f7756641..da87cb4f12 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
@@ -1,613 +1,613 @@
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
 package org.hibernate.hql.internal.ast;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import antlr.ANTLRException;
 import antlr.RecognitionException;
 import antlr.TokenStreamException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.QueryExecutionRequestException;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.exec.BasicExecutor;
 import org.hibernate.hql.internal.ast.exec.MultiTableDeleteExecutor;
 import org.hibernate.hql.internal.ast.exec.MultiTableUpdateExecutor;
 import org.hibernate.hql.internal.ast.exec.StatementExecutor;
 import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.InsertStatement;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.Statement;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.NodeTraverser;
 import org.hibernate.hql.spi.FilterTranslator;
 import org.hibernate.hql.spi.ParameterTranslations;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.loader.hql.QueryLoader;
+import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.Type;
 
 /**
  * A QueryTranslator that uses an Antlr-based parser.
  *
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 public class QueryTranslatorImpl implements FilterTranslator {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			QueryTranslatorImpl.class.getName()
 	);
 
 	private SessionFactoryImplementor factory;
 
 	private final String queryIdentifier;
 	private String hql;
 	private boolean shallowQuery;
 	private Map tokenReplacements;
 
 	//TODO:this is only needed during compilation .. can we eliminate the instvar?
 	private Map enabledFilters;
 
 	private boolean compiled;
 	private QueryLoader queryLoader;
 	private StatementExecutor statementExecutor;
 
 	private Statement sqlAst;
 	private String sql;
 
 	private ParameterTranslations paramTranslations;
-	private List collectedParameterSpecifications;
+	private List<ParameterSpecification> collectedParameterSpecifications;
 
 
 	/**
 	 * Creates a new AST-based query translator.
 	 *
 	 * @param queryIdentifier The query-identifier (used in stats collection)
 	 * @param query The hql query to translate
 	 * @param enabledFilters Currently enabled filters
 	 * @param factory The session factory constructing this translator instance.
 	 */
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 			String query,
 			Map enabledFilters,
 			SessionFactoryImplementor factory) {
 		this.queryIdentifier = queryIdentifier;
 		this.hql = query;
 		this.compiled = false;
 		this.shallowQuery = false;
 		this.enabledFilters = enabledFilters;
 		this.factory = factory;
 	}
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param replacements Defined query substitutions.
 	 * @param shallow      Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	@Override
 	public void compile(
 			Map replacements,
 			boolean shallow) throws QueryException, MappingException {
 		doCompile( replacements, shallow, null );
 	}
 
 	/**
 	 * Compile a filter. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param collectionRole the role name of the collection used as the basis for the filter.
 	 * @param replacements   Defined query substitutions.
 	 * @param shallow        Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	@Override
 	public void compile(
 			String collectionRole,
 			Map replacements,
 			boolean shallow) throws QueryException, MappingException {
 		doCompile( replacements, shallow, collectionRole );
 	}
 
 	/**
 	 * Performs both filter and non-filter compiling.
 	 *
 	 * @param replacements   Defined query substitutions.
 	 * @param shallow        Does this represent a shallow (scalar or entity-id) select?
 	 * @param collectionRole the role name of the collection used as the basis for the filter, NULL if this
 	 *                       is not a filter.
 	 */
 	private synchronized void doCompile(Map replacements, boolean shallow, String collectionRole) {
 		// If the query is already compiled, skip the compilation.
 		if ( compiled ) {
 			LOG.debug( "compile() : The query is already compiled, skipping..." );
 			return;
 		}
 
 		// Remember the parameters for the compilation.
 		this.tokenReplacements = replacements;
 		if ( tokenReplacements == null ) {
 			tokenReplacements = new HashMap();
 		}
 		this.shallowQuery = shallow;
 
 		try {
 			// PHASE 1 : Parse the HQL into an AST.
 			final HqlParser parser = parse( true );
 
 			// PHASE 2 : Analyze the HQL AST, and produce an SQL AST.
 			final HqlSqlWalker w = analyze( parser, collectionRole );
 
 			sqlAst = (Statement) w.getAST();
 
 			// at some point the generate phase needs to be moved out of here,
 			// because a single object-level DML might spawn multiple SQL DML
 			// command executions.
 			//
 			// Possible to just move the sql generation for dml stuff, but for
 			// consistency-sake probably best to just move responsiblity for
 			// the generation phase completely into the delegates
 			// (QueryLoader/StatementExecutor) themselves.  Also, not sure why
 			// QueryLoader currently even has a dependency on this at all; does
 			// it need it?  Ideally like to see the walker itself given to the delegates directly...
 
 			if ( sqlAst.needsExecutor() ) {
 				statementExecutor = buildAppropriateStatementExecutor( w );
 			}
 			else {
 				// PHASE 3 : Generate the SQL.
 				generate( (QueryNode) sqlAst );
 				queryLoader = new QueryLoader( this, factory, w.getSelectClause() );
 			}
 
 			compiled = true;
 		}
 		catch ( QueryException qe ) {
 			if ( qe.getQueryString() == null ) {
 				throw qe.wrapWithQueryString( hql );
 			}
 			else {
 				throw qe;
 			}
 		}
 		catch ( RecognitionException e ) {
 			// we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
 			LOG.trace( "Converted antlr.RecognitionException", e );
 			throw QuerySyntaxException.convert( e, hql );
 		}
 		catch ( ANTLRException e ) {
 			// we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
 			LOG.trace( "Converted antlr.ANTLRException", e );
 			throw new QueryException( e.getMessage(), hql );
 		}
 
 		//only needed during compilation phase...
 		this.enabledFilters = null;
 	}
 
 	private void generate(AST sqlAst) throws QueryException, RecognitionException {
 		if ( sql == null ) {
 			final SqlGenerator gen = new SqlGenerator( factory );
 			gen.statement( sqlAst );
 			sql = gen.getSQL();
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "HQL: %s", hql );
 				LOG.debugf( "SQL: %s", sql );
 			}
 			gen.getParseErrorHandler().throwQueryException();
 			collectedParameterSpecifications = gen.getCollectedParameters();
 		}
 	}
 
 	private static final ASTPrinter SQL_TOKEN_PRINTER = new ASTPrinter( SqlTokenTypes.class );
 
 	private HqlSqlWalker analyze(HqlParser parser, String collectionRole) throws QueryException, RecognitionException {
 		final HqlSqlWalker w = new HqlSqlWalker( this, factory, parser, tokenReplacements, collectionRole );
 		final AST hqlAst = parser.getAST();
 
 		// Transform the tree.
 		w.statement( hqlAst );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( SQL_TOKEN_PRINTER.showAsString( w.getAST(), "--- SQL AST ---" ) );
 		}
 
 		w.getParseErrorHandler().throwQueryException();
 
 		return w;
 	}
 
 	private HqlParser parse(boolean filter) throws TokenStreamException, RecognitionException {
 		// Parse the query string into an HQL AST.
 		final HqlParser parser = HqlParser.getInstance( hql );
 		parser.setFilter( filter );
 
 		LOG.debugf( "parse() - HQL: %s", hql );
 		parser.statement();
 
 		final AST hqlAst = parser.getAST();
 
 		final NodeTraverser walker = new NodeTraverser( new JavaConstantConverter() );
 		walker.traverseDepthFirst( hqlAst );
 
 		showHqlAst( hqlAst );
 
 		parser.getParseErrorHandler().throwQueryException();
 		return parser;
 	}
 
 	private static final ASTPrinter HQL_TOKEN_PRINTER = new ASTPrinter( HqlTokenTypes.class );
 
 	void showHqlAst(AST hqlAst) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( HQL_TOKEN_PRINTER.showAsString( hqlAst, "--- HQL AST ---" ) );
 		}
 	}
 
 	private void errorIfDML() throws HibernateException {
 		if ( sqlAst.needsExecutor() ) {
 			throw new QueryExecutionRequestException( "Not supported for DML operations", hql );
 		}
 	}
 
 	private void errorIfSelect() throws HibernateException {
 		if ( !sqlAst.needsExecutor() ) {
 			throw new QueryExecutionRequestException( "Not supported for select queries", hql );
 		}
 	}
 	@Override
 	public String getQueryIdentifier() {
 		return queryIdentifier;
 	}
 
 	public Statement getSqlAST() {
 		return sqlAst;
 	}
 
 	private HqlSqlWalker getWalker() {
 		return sqlAst.getWalker();
 	}
 
 	/**
 	 * Types of the return values of an <tt>iterate()</tt> style query.
 	 *
 	 * @return an array of <tt>Type</tt>s.
 	 */
 	@Override
 	public Type[] getReturnTypes() {
 		errorIfDML();
 		return getWalker().getReturnTypes();
 	}
 	@Override
 	public String[] getReturnAliases() {
 		errorIfDML();
 		return getWalker().getReturnAliases();
 	}
 	@Override
 	public String[][] getColumnNames() {
 		errorIfDML();
 		return getWalker().getSelectClause().getColumnNames();
 	}
 	@Override
 	public Set getQuerySpaces() {
 		return getWalker().getQuerySpaces();
 	}
 
 	@Override
 	public List list(SessionImplementor session, QueryParameters queryParameters)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		QueryNode query = ( QueryNode ) sqlAst;
 		boolean hasLimit = queryParameters.getRowSelection() != null && queryParameters.getRowSelection().definesLimits();
 		boolean needsDistincting = ( query.getSelectClause().isDistinct() || hasLimit ) && containsCollectionFetches();
 
 		QueryParameters queryParametersToUse;
 		if ( hasLimit && containsCollectionFetches() ) {
 			LOG.firstOrMaxResultsSpecifiedWithCollectionFetch();
 			RowSelection selection = new RowSelection();
 			selection.setFetchSize( queryParameters.getRowSelection().getFetchSize() );
 			selection.setTimeout( queryParameters.getRowSelection().getTimeout() );
 			queryParametersToUse = queryParameters.createCopyUsing( selection );
 		}
 		else {
 			queryParametersToUse = queryParameters;
 		}
 
 		List results = queryLoader.list( session, queryParametersToUse );
 
 		if ( needsDistincting ) {
 			int includedCount = -1;
 			// NOTE : firstRow is zero-based
 			int first = !hasLimit || queryParameters.getRowSelection().getFirstRow() == null
 						? 0
 						: queryParameters.getRowSelection().getFirstRow();
 			int max = !hasLimit || queryParameters.getRowSelection().getMaxRows() == null
 						? -1
 						: queryParameters.getRowSelection().getMaxRows();
 			List tmp = new ArrayList();
 			IdentitySet distinction = new IdentitySet();
 			for ( final Object result : results ) {
 				if ( !distinction.add( result ) ) {
 					continue;
 				}
 				includedCount++;
 				if ( includedCount < first ) {
 					continue;
 				}
 				tmp.add( result );
 				// NOTE : ( max - 1 ) because first is zero-based while max is not...
 				if ( max >= 0 && ( includedCount - first ) >= ( max - 1 ) ) {
 					break;
 				}
 			}
 			results = tmp;
 		}
 
 		return results;
 	}
 
 	/**
 	 * Return the query results as an iterator
 	 */
 	@Override
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.iterate( queryParameters, session );
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 */
 	@Override
 	public ScrollableResults scroll(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.scroll( queryParameters, session );
 	}
 	@Override
 	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException {
 		errorIfSelect();
 		return statementExecutor.execute( queryParameters, session );
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 */
 	@Override
 	public String getSQLString() {
 		return sql;
 	}
 	@Override
 	public List<String> collectSqlStrings() {
 		ArrayList<String> list = new ArrayList<String>();
 		if ( isManipulationStatement() ) {
 			String[] sqlStatements = statementExecutor.getSqlStatements();
 			Collections.addAll( list, sqlStatements );
 		}
 		else {
 			list.add( sql );
 		}
 		return list;
 	}
 
 	// -- Package local methods for the QueryLoader delegate --
 
 	public boolean isShallowQuery() {
 		return shallowQuery;
 	}
 	@Override
 	public String getQueryString() {
 		return hql;
 	}
 	@Override
 	public Map getEnabledFilters() {
 		return enabledFilters;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		return getWalker().getNamedParameterLocations( name );
 	}
 	@Override
 	public boolean containsCollectionFetches() {
 		errorIfDML();
 		List collectionFetches = ( ( QueryNode ) sqlAst ).getFromClause().getCollectionFetches();
 		return collectionFetches != null && collectionFetches.size() > 0;
 	}
 	@Override
 	public boolean isManipulationStatement() {
 		return sqlAst.needsExecutor();
 	}
 	@Override
 	public void validateScrollability() throws HibernateException {
 		// Impl Note: allows multiple collection fetches as long as the
 		// entire fecthed graph still "points back" to a single
 		// root entity for return
 
 		errorIfDML();
 
 		QueryNode query = ( QueryNode ) sqlAst;
 
 		// If there are no collection fetches, then no further checks are needed
 		List collectionFetches = query.getFromClause().getCollectionFetches();
 		if ( collectionFetches.isEmpty() ) {
 			return;
 		}
 
 		// A shallow query is ok (although technically there should be no fetching here...)
 		if ( isShallowQuery() ) {
 			return;
 		}
 
 		// Otherwise, we have a non-scalar select with defined collection fetch(es).
 		// Make sure that there is only a single root entity in the return (no tuples)
 		if ( getReturnTypes().length > 1 ) {
 			throw new HibernateException( "cannot scroll with collection fetches and returned tuples" );
 		}
 
 		FromElement owner = null;
 		for ( Object o : query.getSelectClause().getFromElementsForLoad() ) {
 			// should be the first, but just to be safe...
 			final FromElement fromElement = (FromElement) o;
 			if ( fromElement.getOrigin() == null ) {
 				owner = fromElement;
 				break;
 			}
 		}
 
 		if ( owner == null ) {
 			throw new HibernateException( "unable to locate collection fetch(es) owner for scrollability checks" );
 		}
 
 		// This is not strictly true.  We actually just need to make sure that
 		// it is ordered by root-entity PK and that that order-by comes before
 		// any non-root-entity ordering...
 
 		AST primaryOrdering = query.getOrderByClause().getFirstChild();
 		if ( primaryOrdering != null ) {
 			// TODO : this is a bit dodgy, come up with a better way to check this (plus see above comment)
 			String [] idColNames = owner.getQueryable().getIdentifierColumnNames();
 			String expectedPrimaryOrderSeq = StringHelper.join(
 			        ", ",
 			        StringHelper.qualify( owner.getTableAlias(), idColNames )
 			);
 			if (  !primaryOrdering.getText().startsWith( expectedPrimaryOrderSeq ) ) {
 				throw new HibernateException( "cannot scroll results with collection fetches which are not ordered primarily by the root entity's PK" );
 			}
 		}
 	}
 
 	private StatementExecutor buildAppropriateStatementExecutor(HqlSqlWalker walker) {
 		Statement statement = ( Statement ) walker.getAST();
 		if ( walker.getStatementType() == HqlSqlTokenTypes.DELETE ) {
 			FromElement fromElement = walker.getFinalFromClause().getFromElement();
 			Queryable persister = fromElement.getQueryable();
 			if ( persister.isMultiTable() ) {
 				return new MultiTableDeleteExecutor( walker );
 			}
 			else {
 				return new BasicExecutor( walker, persister );
 			}
 		}
 		else if ( walker.getStatementType() == HqlSqlTokenTypes.UPDATE ) {
 			FromElement fromElement = walker.getFinalFromClause().getFromElement();
 			Queryable persister = fromElement.getQueryable();
 			if ( persister.isMultiTable() ) {
 				// even here, if only properties mapped to the "base table" are referenced
 				// in the set and where clauses, this could be handled by the BasicDelegate.
 				// TODO : decide if it is better performance-wise to doAfterTransactionCompletion that check, or to simply use the MultiTableUpdateDelegate
 				return new MultiTableUpdateExecutor( walker );
 			}
 			else {
 				return new BasicExecutor( walker, persister );
 			}
 		}
 		else if ( walker.getStatementType() == HqlSqlTokenTypes.INSERT ) {
 			return new BasicExecutor( walker, ( ( InsertStatement ) statement ).getIntoClause().getQueryable() );
 		}
 		else {
 			throw new QueryException( "Unexpected statement type" );
 		}
 	}
 	@Override
 	public ParameterTranslations getParameterTranslations() {
 		if ( paramTranslations == null ) {
 			paramTranslations = new ParameterTranslationsImpl( getWalker().getParameters() );
-//			paramTranslations = new ParameterTranslationsImpl( collectedParameterSpecifications );
 		}
 		return paramTranslations;
 	}
 
-	public List getCollectedParameterSpecifications() {
+	public List<ParameterSpecification> getCollectedParameterSpecifications() {
 		return collectedParameterSpecifications;
 	}
 
 	@Override
 	public Class getDynamicInstantiationResultType() {
 		AggregatedSelectExpression aggregation = queryLoader.getAggregatedSelectExpression();
 		return aggregation == null ? null : aggregation.getAggregationResultType();
 	}
 
 	public static class JavaConstantConverter implements NodeTraverser.VisitationStrategy {
 		private AST dotRoot;
 		@Override
 		public void visit(AST node) {
 			if ( dotRoot != null ) {
 				// we are already processing a dot-structure
                 if (ASTUtil.isSubtreeChild(dotRoot, node)) return;
                 // we are now at a new tree level
                 dotRoot = null;
 			}
 
 			if ( node.getType() == HqlTokenTypes.DOT ) {
 				dotRoot = node;
 				handleDotStructure( dotRoot );
 			}
 		}
 		private void handleDotStructure(AST dotStructureRoot) {
 			String expression = ASTUtil.getPathText( dotStructureRoot );
 			Object constant = ReflectHelper.getConstantValue( expression );
 			if ( constant != null ) {
 				dotStructureRoot.setFirstChild( null );
 				dotStructureRoot.setType( HqlTokenTypes.JAVA_CONSTANT );
 				dotStructureRoot.setText( expression );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java
index 0c9b8846b5..e8339b99c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java
@@ -1,376 +1,376 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.hql.internal.ast;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.LinkedList;
 import java.util.List;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.NullPrecedence;
 import org.hibernate.QueryException;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.SqlGeneratorBase;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.FunctionNode;
 import org.hibernate.hql.internal.ast.tree.Node;
 import org.hibernate.hql.internal.ast.tree.ParameterContainer;
 import org.hibernate.hql.internal.ast.tree.ParameterNode;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.type.Type;
 
 /**
  * Generates SQL by overriding callback methods in the base class, which does
  * the actual SQL AST walking.
  *
  * @author Joshua Davis
  * @author Steve Ebersole
  */
 public class SqlGenerator extends SqlGeneratorBase implements ErrorReporter {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SqlGenerator.class.getName());
 
 	public static boolean REGRESSION_STYLE_CROSS_JOINS = false;
 
 	/**
 	 * all append invocations on the buf should go through this Output instance variable.
 	 * The value of this variable may be temporarily substituted by sql function processing code
 	 * to catch generated arguments.
 	 * This is because sql function templates need arguments as separate string chunks
 	 * that will be assembled into the target dialect-specific function call.
 	 */
 	private SqlWriter writer = new DefaultWriter();
 
 	private ParseErrorHandler parseErrorHandler;
 	private SessionFactoryImplementor sessionFactory;
 	private LinkedList<SqlWriter> outputStack = new LinkedList<SqlWriter>();
 	private final ASTPrinter printer = new ASTPrinter( SqlTokenTypes.class );
-	private List collectedParameters = new ArrayList();
+	private List<ParameterSpecification> collectedParameters = new ArrayList<ParameterSpecification>();
 
 
 	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int traceDepth = 0;
 
 	@Override
 	public void traceIn(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) return;
 		if ( inputState.guessing > 0 ) return;
 		String prefix = StringHelper.repeat( '-', ( traceDepth++ * 2 ) ) + "-> ";
 		String traceText = ruleName + " (" + buildTraceNodeName( tree ) + ")";
 		LOG.trace( prefix + traceText );
 	}
 
 	private String buildTraceNodeName(AST tree) {
 		return tree == null
 				? "???"
 				: tree.getText() + " [" + printer.getTokenTypeName( tree.getType() ) + "]";
 	}
 
 	@Override
 	public void traceOut(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) return;
 		if ( inputState.guessing > 0 ) return;
 		String prefix = "<-" + StringHelper.repeat( '-', ( --traceDepth * 2 ) ) + " ";
 		LOG.trace( prefix + ruleName );
 	}
 
-	public List getCollectedParameters() {
+	public List<ParameterSpecification> getCollectedParameters() {
 		return collectedParameters;
 	}
 
 	@Override
     protected void out(String s) {
 		writer.clause( s );
 	}
 
 	@Override
     protected void out(AST n) {
 		if ( n instanceof Node ) {
 			out( ( ( Node ) n ).getRenderText( sessionFactory ) );
 		}
 		else {
 			super.out( n );
 		}
 
 		if ( n instanceof ParameterNode ) {
 			collectedParameters.add( ( ( ParameterNode ) n ).getHqlParameterSpecification() );
 		}
 		else if ( n instanceof ParameterContainer ) {
 			if ( ( ( ParameterContainer ) n ).hasEmbeddedParameters() ) {
 				ParameterSpecification[] specifications = ( ( ParameterContainer ) n ).getEmbeddedParameters();
 				if ( specifications != null ) {
 					collectedParameters.addAll( Arrays.asList( specifications ) );
 				}
 			}
 		}
 	}
 
 	@Override
     protected void commaBetweenParameters(String comma) {
 		writer.commaBetweenParameters( comma );
 	}
 
 	@Override
     public void reportError(RecognitionException e) {
 		parseErrorHandler.reportError( e ); // Use the delegate.
 	}
 
 	@Override
     public void reportError(String s) {
 		parseErrorHandler.reportError( s ); // Use the delegate.
 	}
 
 	@Override
     public void reportWarning(String s) {
 		parseErrorHandler.reportWarning( s );
 	}
 
 	public ParseErrorHandler getParseErrorHandler() {
 		return parseErrorHandler;
 	}
 
 	public SqlGenerator(SessionFactoryImplementor sfi) {
 		super();
 		parseErrorHandler = new ErrorCounter();
 		sessionFactory = sfi;
 	}
 
 	public String getSQL() {
 		return getStringBuilder().toString();
 	}
 
 	@Override
     protected void optionalSpace() {
 		int c = getLastChar();
 		switch ( c ) {
 			case -1:
 				return;
 			case ' ':
 				return;
 			case ')':
 				return;
 			case '(':
 				return;
 			default:
 				out( " " );
 		}
 	}
 
 	@Override
     protected void beginFunctionTemplate(AST node, AST nameNode) {
 		// NOTE for AGGREGATE both nodes are the same; for METHOD the first is the METHOD, the second is the
 		// 		METHOD_NAME
 		FunctionNode functionNode = ( FunctionNode ) node;
 		SQLFunction sqlFunction = functionNode.getSQLFunction();
 		if ( sqlFunction == null ) {
 			// if SQLFunction is null we just write the function out as it appears in the hql statement
 			super.beginFunctionTemplate( node, nameNode );
 		}
 		else {
 			// this function has a registered SQLFunction -> redirect output and catch the arguments
 			outputStack.addFirst( writer );
 			writer = new FunctionArguments();
 		}
 	}
 
 	@Override
     protected void endFunctionTemplate(AST node) {
 		FunctionNode functionNode = ( FunctionNode ) node;
 		SQLFunction sqlFunction = functionNode.getSQLFunction();
 		if ( sqlFunction == null ) {
 			super.endFunctionTemplate( node );
 		}
 		else {
 			final Type functionType = functionNode.getFirstArgumentType();
 			// this function has a registered SQLFunction -> redirect output and catch the arguments
 			FunctionArguments functionArguments = ( FunctionArguments ) writer;
 			writer = outputStack.removeFirst();
 			out( sqlFunction.render( functionType, functionArguments.getArgs(), sessionFactory ) );
 		}
 	}
 
 	// --- Inner classes (moved here from sql-gen.g) ---
 
 	/**
 	 * Writes SQL fragments.
 	 */
 	interface SqlWriter {
 		void clause(String clause);
 
 		/**
 		 * todo remove this hack
 		 * The parameter is either ", " or " , ". This is needed to pass sql generating tests as the old
 		 * sql generator uses " , " in the WHERE and ", " in SELECT.
 		 *
 		 * @param comma either " , " or ", "
 		 */
 		void commaBetweenParameters(String comma);
 	}
 
 	/**
 	 * SQL function processing code redirects generated SQL output to an instance of this class
 	 * which catches function arguments.
 	 */
 	class FunctionArguments implements SqlWriter {
 		private int argInd;
 		private final List<String> args = new ArrayList<String>(3);
 
 		public void clause(String clause) {
 			if ( argInd == args.size() ) {
 				args.add( clause );
 			}
 			else {
 				args.set( argInd, args.get( argInd ) + clause );
 			}
 		}
 
 		public void commaBetweenParameters(String comma) {
 			++argInd;
 		}
 
 		public List getArgs() {
 			return args;
 		}
 	}
 
 	/**
 	 * The default SQL writer.
 	 */
 	class DefaultWriter implements SqlWriter {
 		public void clause(String clause) {
 			getStringBuilder().append( clause );
 		}
 
 		public void commaBetweenParameters(String comma) {
 			getStringBuilder().append( comma );
 		}
 	}
 
     public static void panic() {
 		throw new QueryException( "TreeWalker: panic" );
 	}
 
 	@Override
     protected void fromFragmentSeparator(AST a) {
 		// check two "adjecent" nodes at the top of the from-clause tree
 		AST next = a.getNextSibling();
 		if ( next == null || !hasText( a ) ) {
 			return;
 		}
 
 		FromElement left = ( FromElement ) a;
 		FromElement right = ( FromElement ) next;
 
 		///////////////////////////////////////////////////////////////////////
 		// HACK ALERT !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 		// Attempt to work around "ghost" ImpliedFromElements that occasionally
 		// show up between the actual things being joined.  This consistently
 		// occurs from index nodes (at least against many-to-many).  Not sure
 		// if there are other conditions
 		//
 		// Essentially, look-ahead to the next FromElement that actually
 		// writes something to the SQL
 		while ( right != null && !hasText( right ) ) {
 			right = ( FromElement ) right.getNextSibling();
 		}
 		if ( right == null ) {
 			return;
 		}
 		///////////////////////////////////////////////////////////////////////
 
 		if ( !hasText( right ) ) {
 			return;
 		}
 
 		if ( right.getRealOrigin() == left ||
 		     ( right.getRealOrigin() != null && right.getRealOrigin() == left.getRealOrigin() ) ) {
 			// right represents a joins originating from left; or
 			// both right and left reprersent joins originating from the same FromElement
 			if ( right.getJoinSequence() != null && right.getJoinSequence().isThetaStyle() ) {
 				writeCrossJoinSeparator();
 			}
 			else {
 				out( " " );
 			}
 		}
 		else {
 			// these are just two unrelated table references
 			writeCrossJoinSeparator();
 		}
 	}
 
 	private void writeCrossJoinSeparator() {
 		if ( REGRESSION_STYLE_CROSS_JOINS ) {
 			out( ", " );
 		}
 		else {
 			out( sessionFactory.getDialect().getCrossJoinSeparator() );
 		}
 	}
 
 	@Override
     protected void nestedFromFragment(AST d, AST parent) {
 		// check a set of parent/child nodes in the from-clause tree
 		// to determine if a comma is required between them
 		if ( d != null && hasText( d ) ) {
 			if ( parent != null && hasText( parent ) ) {
 				// again, both should be FromElements
 				FromElement left = ( FromElement ) parent;
 				FromElement right = ( FromElement ) d;
 				if ( right.getRealOrigin() == left ) {
 					// right represents a joins originating from left...
 					if ( right.getJoinSequence() != null && right.getJoinSequence().isThetaStyle() ) {
 						out( ", " );
 					}
 					else {
 						out( " " );
 					}
 				}
 				else {
 					// not so sure this is even valid subtree.  but if it was, it'd
 					// represent two unrelated table references...
 					out( ", " );
 				}
 			}
 			out( d );
 		}
 	}
 
 	@Override
 	protected String renderOrderByElement(String expression, String order, String nulls) {
 		final NullPrecedence nullPrecedence = NullPrecedence.parse( nulls, sessionFactory.getSettings().getDefaultNullPrecedence() );
 		return sessionFactory.getDialect().renderOrderByElement( expression, null, order, nullPrecedence );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
index ceb1b87ab8..3138a76940 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
@@ -1,696 +1,695 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.TypeDiscriminatorMetadata;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.DiscriminatorMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Represents a single mapped class mentioned in an HQL FROM clause.  Each
  * class reference will have the following symbols:
  * <ul>
  * <li>A class name - This is the name of the Java class that is mapped by Hibernate.</li>
  * <li>[optional] an HQL alias for the mapped class.</li>
  * <li>A table name - The name of the table that is mapped to the Java class.</li>
  * <li>A table alias - The alias for the table that will be used in the resulting SQL.</li>
  * </ul>
  *
  * @author josh
  */
 public class FromElement extends HqlSqlWalkerNode implements DisplayableNode, ParameterContainer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, FromElement.class.getName());
 
 	private String className;
 	private String classAlias;
 	private String tableAlias;
 	private String collectionTableAlias;
 	private FromClause fromClause;
 	private boolean includeSubclasses = true;
 	private boolean collectionJoin = false;
 	private FromElement origin;
 	private String[] columns;
 	private String role;
 	private boolean fetch;
 	private boolean isAllPropertyFetch;
 	private boolean filter = false;
 	private int sequence = -1;
 	private boolean useFromFragment = false;
 	private boolean initialized = false;
 	private FromElementType elementType;
 	private boolean useWhereFragment = true;
 	private List destinations = new LinkedList();
 	private boolean manyToMany = false;
 	private String withClauseFragment = null;
 	private String withClauseJoinAlias;
 	private boolean dereferencedBySuperclassProperty;
 	private boolean dereferencedBySubclassProperty;
 
 	public FromElement() {
 	}
 
 	/**
 	 * Constructor form used to initialize {@link ComponentJoin}
 	 *
 	 * @param fromClause The FROM clause to which this element belongs
 	 * @param origin The origin (LHS) of this element
 	 * @param alias The alias applied to this element
 	 */
 	protected FromElement(
 			FromClause fromClause,
 			FromElement origin,
 			String alias) {
 		this.fromClause = fromClause;
 		this.origin = origin;
 		this.classAlias = alias;
 		this.tableAlias = origin.getTableAlias();
 		super.initialize( fromClause.getWalker() );
 	}
 
 	protected void initializeComponentJoin(FromElementType elementType) {
 		this.elementType = elementType;
 		fromClause.registerFromElement( this );
 		initialized = true;
 	}
 
 	public String getCollectionSuffix() {
 		return elementType.getCollectionSuffix();
 	}
 
 	public void setCollectionSuffix(String suffix) {
 		elementType.setCollectionSuffix(suffix);
 	}
 
 	public void initializeCollection(FromClause fromClause, String classAlias, String tableAlias) {
 		doInitialize( fromClause, tableAlias, null, classAlias, null, null );
 		initialized = true;
 	}
 
 	public void initializeEntity(
 	        FromClause fromClause,
 	        String className,
 	        EntityPersister persister,
 	        EntityType type,
 	        String classAlias,
 	        String tableAlias) {
 		doInitialize( fromClause, tableAlias, className, classAlias, persister, type );
 		this.sequence = fromClause.nextFromElementCounter();
 		initialized = true;
 	}
 
 	private void doInitialize(FromClause fromClause, String tableAlias, String className, String classAlias,
 							  EntityPersister persister, EntityType type) {
 		if ( initialized ) {
 			throw new IllegalStateException( "Already initialized!!" );
 		}
 		this.fromClause = fromClause;
 		this.tableAlias = tableAlias;
 		this.className = className;
 		this.classAlias = classAlias;
 		this.elementType = new FromElementType( this, persister, type );
 		// Register the FromElement with the FROM clause, now that we have the names and aliases.
 		fromClause.registerFromElement( this );
 		LOG.debugf( "%s : %s (%s) -> %s", fromClause, className, classAlias == null ? "<no alias>" : classAlias, tableAlias );
 	}
 
 	public EntityPersister getEntityPersister() {
 		return elementType.getEntityPersister();
 	}
 
 	@Override
     public Type getDataType() {
 		return elementType.getDataType();
 	}
 
 	public Type getSelectType() {
 		return elementType.getSelectType();
 	}
 
 	public Queryable getQueryable() {
 		return elementType.getQueryable();
 	}
 
 	public String getClassName() {
 		return className;
 	}
 
 	public String getClassAlias() {
 		return classAlias;
 		//return classAlias == null ? className : classAlias;
 	}
 
 	private String getTableName() {
 		Queryable queryable = getQueryable();
 		return ( queryable != null ) ? queryable.getTableName() : "{none}";
 	}
 
 	public String getTableAlias() {
 		return tableAlias;
 	}
 
 	/**
 	 * Render the identifier select, but in a 'scalar' context (i.e. generate the column alias).
 	 *
 	 * @param i the sequence of the returned type
 	 * @return the identifier select with the column alias.
 	 */
 	String renderScalarIdentifierSelect(int i) {
 		return elementType.renderScalarIdentifierSelect( i );
 	}
 
 	void checkInitialized() {
 		if ( !initialized ) {
 			throw new IllegalStateException( "FromElement has not been initialized!" );
 		}
 	}
 
 	/**
 	 * Returns the identifier select SQL fragment.
 	 *
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the identifier select SQL fragment.
 	 */
 	String renderIdentifierSelect(int size, int k) {
 		return elementType.renderIdentifierSelect( size, k );
 	}
 
 	/**
 	 * Returns the property select SQL fragment.
 	 *
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the property select SQL fragment.
 	 */
 	String renderPropertySelect(int size, int k) {
 		return elementType.renderPropertySelect( size, k, isAllPropertyFetch );
 	}
 
 	String renderCollectionSelectFragment(int size, int k) {
 		return elementType.renderCollectionSelectFragment( size, k );
 	}
 
 	String renderValueCollectionSelectFragment(int size, int k) {
 		return elementType.renderValueCollectionSelectFragment( size, k );
 	}
 
 	public FromClause getFromClause() {
 		return fromClause;
 	}
 
 	/**
 	 * Returns true if this FromElement was implied by a path, or false if this FROM element is explicitly declared in
 	 * the FROM clause.
 	 *
 	 * @return true if this FromElement was implied by a path, or false if this FROM element is explicitly declared
 	 */
 	public boolean isImplied() {
 		return false;	// This is an explicit FROM element.
 	}
 
 	/**
 	 * Returns additional display text for the AST node.
 	 *
 	 * @return String - The additional display text.
 	 */
 	public String getDisplayText() {
 		StringBuilder buf = new StringBuilder();
 		buf.append( "FromElement{" );
 		appendDisplayText( buf );
 		buf.append( "}" );
 		return buf.toString();
 	}
 
 	protected void appendDisplayText(StringBuilder buf) {
 		buf.append( isImplied() ? (
 				isImpliedInFromClause() ? "implied in FROM clause" : "implied" )
 				: "explicit" );
 		buf.append( "," ).append( isCollectionJoin() ? "collection join" : "not a collection join" );
 		buf.append( "," ).append( fetch ? "fetch join" : "not a fetch join" );
 		buf.append( "," ).append( isAllPropertyFetch ? "fetch all properties" : "fetch non-lazy properties" );
 		buf.append( ",classAlias=" ).append( getClassAlias() );
 		buf.append( ",role=" ).append( role );
 		buf.append( ",tableName=" ).append( getTableName() );
 		buf.append( ",tableAlias=" ).append( getTableAlias() );
 		FromElement origin = getRealOrigin();
 		buf.append( ",origin=" ).append( origin == null ? "null" : origin.getText() );
 		buf.append( ",columns={" );
 		if ( columns != null ) {
 			for ( int i = 0; i < columns.length; i++ ) {
 				buf.append( columns[i] );
 				if ( i < columns.length ) {
 					buf.append( " " );
 				}
 			}
 		}
 		buf.append( ",className=" ).append( className );
 		buf.append( "}" );
 	}
 
 	@Override
     public int hashCode() {
 		return super.hashCode();
 	}
 
 	@Override
     public boolean equals(Object obj) {
 		return super.equals( obj );
 	}
 
 
 	public void setJoinSequence(JoinSequence joinSequence) {
 		elementType.setJoinSequence( joinSequence );
 	}
 
 	public JoinSequence getJoinSequence() {
 		return elementType.getJoinSequence();
 	}
 
 	public void setIncludeSubclasses(boolean includeSubclasses) {
 		if ( !includeSubclasses && isDereferencedBySuperclassOrSubclassProperty() && LOG.isTraceEnabled() )
 			LOG.trace( "Attempt to disable subclass-inclusions : ", new Exception( "Stack-trace source" ) );
 		this.includeSubclasses = includeSubclasses;
 	}
 
 	public boolean isIncludeSubclasses() {
 		return includeSubclasses;
 	}
 
 	public boolean isDereferencedBySuperclassOrSubclassProperty() {
 		return dereferencedBySubclassProperty || dereferencedBySuperclassProperty;
 	}
 
 	public String getIdentityColumn() {
 		final String[] cols = getIdentityColumns();
 		if ( cols.length == 1 ) {
 			return cols[0];
 		}
 		else {
 			return "(" + StringHelper.join( ", ", cols ) + ")";
 		}
 	}
 
 	public String[] getIdentityColumns() {
 		checkInitialized();
 		final String table = getTableAlias();
 		if ( table == null ) {
 			throw new IllegalStateException( "No table alias for node " + this );
 		}
 
 		final String propertyName;
 		if ( getEntityPersister() != null && getEntityPersister().getEntityMetamodel() != null
 				&& getEntityPersister().getEntityMetamodel().hasNonIdentifierPropertyNamedId() ) {
 			propertyName = getEntityPersister().getIdentifierPropertyName();
 		}
 		else {
 			propertyName = EntityPersister.ENTITY_ID;
 		}
 
 		if ( getWalker().getStatementType() == HqlSqlTokenTypes.SELECT ) {
 			return getPropertyMapping( propertyName ).toColumns( table, propertyName );
 		}
 		else {
 			return getPropertyMapping( propertyName ).toColumns( propertyName );
 		}
 	}
 
 	public void setCollectionJoin(boolean collectionJoin) {
 		this.collectionJoin = collectionJoin;
 	}
 
 	public boolean isCollectionJoin() {
 		return collectionJoin;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	public void setQueryableCollection(QueryableCollection queryableCollection) {
 		elementType.setQueryableCollection( queryableCollection );
 	}
 
 	public QueryableCollection getQueryableCollection() {
 		return elementType.getQueryableCollection();
 	}
 
 	public void setColumns(String[] columns) {
 		this.columns = columns;
 	}
 
 	public void setOrigin(FromElement origin, boolean manyToMany) {
 		this.origin = origin;
 		this.manyToMany = manyToMany;
 		origin.addDestination( this );
 		if ( origin.getFromClause() == this.getFromClause() ) {
 			// TODO: Figure out a better way to get the FROM elements in a proper tree structure.
 			// If this is not the destination of a many-to-many, add it as a child of the origin.
 			if ( manyToMany ) {
 				ASTUtil.appendSibling( origin, this );
 			}
 			else {
 				if ( !getWalker().isInFrom() && !getWalker().isInSelect() ) {
 					getFromClause().addChild( this );
 				}
 				else {
 					origin.addChild( this );
 				}
 			}
 		}
 		else if ( !getWalker().isInFrom() ) {
 			// HHH-276 : implied joins in a subselect where clause - The destination needs to be added
 			// to the destination's from clause.
 			getFromClause().addChild( this );	// Not sure if this is will fix everything, but it works.
 		}
 		else {
 			// Otherwise, the destination node was implied by the FROM clause and the FROM clause processor
 			// will automatically add it in the right place.
 		}
 	}
 
 	public boolean isManyToMany() {
 		return manyToMany;
 	}
 
 	private void addDestination(FromElement fromElement) {
 		destinations.add( fromElement );
 	}
 
 	public List getDestinations() {
 		return destinations;
 	}
 
 	public FromElement getOrigin() {
 		return origin;
 	}
 
 	public FromElement getRealOrigin() {
 		if ( origin == null ) {
 			return null;
 		}
 		if ( origin.getText() == null || "".equals( origin.getText() ) ) {
 			return origin.getRealOrigin();
 		}
 		return origin;
 	}
 
 	public static final String DISCRIMINATOR_PROPERTY_NAME = "class";
 	private TypeDiscriminatorMetadata typeDiscriminatorMetadata;
 
 	private static class TypeDiscriminatorMetadataImpl implements TypeDiscriminatorMetadata {
 		private final DiscriminatorMetadata persisterDiscriminatorMetadata;
 		private final String alias;
 
 		private TypeDiscriminatorMetadataImpl(
 				DiscriminatorMetadata persisterDiscriminatorMetadata,
 				String alias) {
 			this.persisterDiscriminatorMetadata = persisterDiscriminatorMetadata;
 			this.alias = alias;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getSqlFragment() {
 			return persisterDiscriminatorMetadata.getSqlFragment( alias );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Type getResolutionType() {
 			return persisterDiscriminatorMetadata.getResolutionType();
 		}
 	}
 
 	public TypeDiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( typeDiscriminatorMetadata == null ) {
 			typeDiscriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return typeDiscriminatorMetadata;
 	}
 
 	private TypeDiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		final String aliasToUse = getTableAlias();
 		Queryable queryable = getQueryable();
 		if ( queryable == null ) {
 			QueryableCollection collection = getQueryableCollection();
 			if ( ! collection.getElementType().isEntityType() ) {
 				throw new QueryException( "type discrimination cannot be applied to value collection [" + collection.getRole() + "]" );
 			}
 			queryable = (Queryable) collection.getElementPersister();
 		}
 
 		handlePropertyBeingDereferenced( getDataType(), DISCRIMINATOR_PROPERTY_NAME );
 
 		return new TypeDiscriminatorMetadataImpl( queryable.getTypeDiscriminatorMetadata(), aliasToUse );
 	}
 
 	public Type getPropertyType(String propertyName, String propertyPath) {
 		return elementType.getPropertyType( propertyName, propertyPath );
 	}
 
 	public String[] toColumns(String tableAlias, String path, boolean inSelect) {
 		return elementType.toColumns( tableAlias, path, inSelect );
 	}
 
 	public String[] toColumns(String tableAlias, String path, boolean inSelect, boolean forceAlias) {
 		return elementType.toColumns( tableAlias, path, inSelect, forceAlias );
 	}
 
 	public PropertyMapping getPropertyMapping(String propertyName) {
 		return elementType.getPropertyMapping( propertyName );
 	}
 
 	public void setFetch(boolean fetch) {
 		this.fetch = fetch;
 		// Fetch can't be used with scroll() or iterate().
 		if ( fetch && getWalker().isShallowQuery() ) {
 			throw new QueryException( QueryTranslator.ERROR_CANNOT_FETCH_WITH_ITERATE );
 		}
 	}
 
 	public boolean isFetch() {
 		return fetch;
 	}
 
 	public int getSequence() {
 		return sequence;
 	}
 
 	public void setFilter(boolean b) {
 		filter = b;
 	}
 
 	public boolean isFilter() {
 		return filter;
 	}
 
 	public boolean useFromFragment() {
 		checkInitialized();
 		// If it's not implied or it is implied and it's a many to many join where the target wasn't found.
 		return !isImplied() || this.useFromFragment;
 	}
 
 	public void setUseFromFragment(boolean useFromFragment) {
 		this.useFromFragment = useFromFragment;
 	}
 
 	public boolean useWhereFragment() {
 		return useWhereFragment;
 	}
 
 	public void setUseWhereFragment(boolean b) {
 		useWhereFragment = b;
 	}
 
 
 	public void setCollectionTableAlias(String collectionTableAlias) {
 		this.collectionTableAlias = collectionTableAlias;
 	}
 
 	public String getCollectionTableAlias() {
 		return collectionTableAlias;
 	}
 
 	public boolean isCollectionOfValuesOrComponents() {
 		return elementType.isCollectionOfValuesOrComponents();
 	}
 
 	public boolean isEntity() {
 		return elementType.isEntity();
 	}
 
 	public void setImpliedInFromClause(boolean flag) {
 		throw new UnsupportedOperationException( "Explicit FROM elements can't be implied in the FROM clause!" );
 	}
 
 	public boolean isImpliedInFromClause() {
 		return false;	// Since this is an explicit FROM element, it can't be implied in the FROM clause.
 	}
 
 	public void setInProjectionList(boolean inProjectionList) {
 		// Do nothing, eplicit from elements are *always* in the projection list.
 	}
 
 	public boolean inProjectionList() {
 		return !isImplied() && isFromOrJoinFragment();
 	}
 
 	public boolean isFromOrJoinFragment() {
 		return getType() == SqlTokenTypes.FROM_FRAGMENT || getType() == SqlTokenTypes.JOIN_FRAGMENT;
 	}
 
 	public boolean isAllPropertyFetch() {
 		return isAllPropertyFetch;
 	}
 
 	public void setAllPropertyFetch(boolean fetch) {
 		isAllPropertyFetch = fetch;
 	}
 
 	public String getWithClauseFragment() {
 		return withClauseFragment;
 	}
 
 	public String getWithClauseJoinAlias() {
 		return withClauseJoinAlias;
 	}
 
 	public void setWithClauseFragment(String withClauseJoinAlias, String withClauseFragment) {
 		this.withClauseJoinAlias = withClauseJoinAlias;
 		this.withClauseFragment = withClauseFragment;
 	}
 
 	public boolean hasCacheablePersister() {
 		if ( getQueryableCollection() != null ) {
 			return getQueryableCollection().hasCache();
 		}
 		else {
 			return getQueryable().hasCache();
 		}
 	}
 
 	public void handlePropertyBeingDereferenced(Type propertySource, String propertyName) {
 		if ( getQueryableCollection() != null && CollectionProperties.isCollectionProperty( propertyName ) ) {
 			// propertyName refers to something like collection.size...
 			return;
 		}
 		if ( propertySource.isComponentType() ) {
 			// property name is a sub-path of a component...
 			return;
 		}
 
 		Queryable persister = getQueryable();
 		if ( persister != null ) {
 			try {
 				Queryable.Declarer propertyDeclarer = persister.getSubclassPropertyDeclarer( propertyName );
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Handling property dereference [{0} ({1}) -> {2} ({3})]",
 							persister.getEntityName(), getClassAlias(), propertyName, propertyDeclarer );
 				}
 				if ( propertyDeclarer == Queryable.Declarer.SUBCLASS ) {
 					dereferencedBySubclassProperty = true;
 					includeSubclasses = true;
 				}
 				else if ( propertyDeclarer == Queryable.Declarer.SUPERCLASS ) {
 					dereferencedBySuperclassProperty = true;
 				}
 			}
 			catch( QueryException ignore ) {
 				// ignore it; the incoming property could not be found so we
 				// cannot be sure what to do here.  At the very least, the
 				// safest is to simply not apply any dereference toggling...
 
 			}
 		}
 	}
 
 	public boolean isDereferencedBySuperclassProperty() {
 		return dereferencedBySuperclassProperty;
 	}
 
 	public boolean isDereferencedBySubclassProperty() {
 		return dereferencedBySubclassProperty;
 	}
 
 
 	// ParameterContainer impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-	private List embeddedParameters;
+	private List<ParameterSpecification> embeddedParameters;
 
+	@Override
 	public void addEmbeddedParameter(ParameterSpecification specification) {
 		if ( embeddedParameters == null ) {
-			embeddedParameters = new ArrayList();
+			embeddedParameters = new ArrayList<ParameterSpecification>();
 		}
 		embeddedParameters.add( specification );
 	}
 
+	@Override
 	public boolean hasEmbeddedParameters() {
 		return embeddedParameters != null && ! embeddedParameters.isEmpty();
 	}
 
+	@Override
 	public ParameterSpecification[] getEmbeddedParameters() {
-		return ( ParameterSpecification[] ) embeddedParameters.toArray( new ParameterSpecification[ embeddedParameters.size() ] );
+		return embeddedParameters.toArray( new ParameterSpecification[ embeddedParameters.size() ] );
 	}
 
 	public ParameterSpecification getIndexCollectionSelectorParamSpec() {
 		return elementType.getIndexCollectionSelectorParamSpec();
 	}
 
 	public void setIndexCollectionSelectorParamSpec(ParameterSpecification indexCollectionSelectorParamSpec) {
 		if ( indexCollectionSelectorParamSpec == null ) {
 			if ( elementType.getIndexCollectionSelectorParamSpec() != null ) {
 				embeddedParameters.remove( elementType.getIndexCollectionSelectorParamSpec() );
 				elementType.setIndexCollectionSelectorParamSpec( null );
 			}
 		}
 		else {
 			elementType.setIndexCollectionSelectorParamSpec( indexCollectionSelectorParamSpec );
 			addEmbeddedParameter( indexCollectionSelectorParamSpec );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
index c579b9b6d5..0c12b17c5e 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
@@ -1,216 +1,217 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.hql.internal.ast.tree;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.List;
 
 import antlr.RecognitionException;
 import antlr.SemanticException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Represents the [] operator and provides it's semantics.
  *
  * @author josh
  */
 public class IndexNode extends FromReferenceNode {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, IndexNode.class.getName() );
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		throw new UnsupportedOperationException( "An IndexNode cannot generate column text!" );
 	}
 
 	@Override
     public void prepareForDot(String propertyName) throws SemanticException {
 		FromElement fromElement = getFromElement();
 		if ( fromElement == null ) {
 			throw new IllegalStateException( "No FROM element for index operator!" );
 		}
 		QueryableCollection queryableCollection = fromElement.getQueryableCollection();
 		if ( queryableCollection != null && !queryableCollection.isOneToMany() ) {
 
 			FromReferenceNode collectionNode = ( FromReferenceNode ) getFirstChild();
 			String path = collectionNode.getPath() + "[]." + propertyName;
 			LOG.debugf( "Creating join for many-to-many elements for %s", path );
 			FromElementFactory factory = new FromElementFactory( fromElement.getFromClause(), fromElement, path );
 			// This will add the new from element to the origin.
 			FromElement elementJoin = factory.createElementJoin( queryableCollection );
 			setFromElement( elementJoin );
 		}
 	}
 
 	public void resolveIndex(AST parent) throws SemanticException {
 		throw new UnsupportedOperationException();
 	}
 
 	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
 	throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		FromReferenceNode collectionNode = ( FromReferenceNode ) getFirstChild();
 		SessionFactoryHelper sessionFactoryHelper = getSessionFactoryHelper();
 		collectionNode.resolveIndex( this );		// Fully resolve the map reference, create implicit joins.
 
 		Type type = collectionNode.getDataType();
 		if ( !type.isCollectionType() ) {
 			throw new SemanticException( "The [] operator cannot be applied to type " + type.toString() );
 		}
 		String collectionRole = ( ( CollectionType ) type ).getRole();
 		QueryableCollection queryableCollection = sessionFactoryHelper.requireQueryableCollection( collectionRole );
 		if ( !queryableCollection.hasIndex() ) {
 			throw new QueryException( "unindexed fromElement before []: " + collectionNode.getPath() );
 		}
 
 		// Generate the inner join -- The elements need to be joined to the collection they are in.
 		FromElement fromElement = collectionNode.getFromElement();
 		String elementTable = fromElement.getTableAlias();
 		FromClause fromClause = fromElement.getFromClause();
 		String path = collectionNode.getPath();
 
 		FromElement elem = fromClause.findCollectionJoin( path );
 		if ( elem == null ) {
 			FromElementFactory factory = new FromElementFactory( fromClause, fromElement, path );
 			elem = factory.createCollectionElementsJoin( queryableCollection, elementTable );
 			LOG.debugf( "No FROM element found for the elements of collection join path %s, created %s", path, elem );
 		}
 		else {
 			LOG.debugf( "FROM element found for collection join path %s", path );
 		}
 
 		// The 'from element' that represents the elements of the collection.
 		setFromElement( fromElement );
 
 		// Add the condition to the join sequence that qualifies the indexed element.
 		AST selector = collectionNode.getNextSibling();
 		if ( selector == null ) {
 			throw new QueryException( "No index value!" );
 		}
 
 		// Sometimes use the element table alias, sometimes use the... umm... collection table alias (many to many)
 		String collectionTableAlias = elementTable;
 		if ( elem.getCollectionTableAlias() != null ) {
 			collectionTableAlias = elem.getCollectionTableAlias();
 		}
 
 		// TODO: get SQL rendering out of here, create an AST for the join expressions.
 		// Use the SQL generator grammar to generate the SQL text for the index expression.
 		JoinSequence joinSequence = fromElement.getJoinSequence();
 		String[] indexCols = queryableCollection.getIndexColumnNames();
 		if ( indexCols.length != 1 ) {
 			throw new QueryException( "composite-index appears in []: " + collectionNode.getPath() );
 		}
 		SqlGenerator gen = new SqlGenerator( getSessionFactoryHelper().getFactory() );
 		try {
 			gen.simpleExpr( selector ); //TODO: used to be exprNoParens! was this needed?
 		}
 		catch ( RecognitionException e ) {
 			throw new QueryException( e.getMessage(), e );
 		}
 		String selectorExpression = gen.getSQL();
 		joinSequence.addCondition( collectionTableAlias + '.' + indexCols[0] + " = " + selectorExpression );
-		List paramSpecs = gen.getCollectedParameters();
+		List<ParameterSpecification> paramSpecs = gen.getCollectedParameters();
 		if ( paramSpecs != null ) {
 			switch ( paramSpecs.size() ) {
 				case 0 :
 					// nothing to do
 					break;
 				case 1 :
-					ParameterSpecification paramSpec = ( ParameterSpecification ) paramSpecs.get( 0 );
+					ParameterSpecification paramSpec = paramSpecs.get( 0 );
 					paramSpec.setExpectedType( queryableCollection.getIndexType() );
 					fromElement.setIndexCollectionSelectorParamSpec( paramSpec );
 					break;
 				default:
 					fromElement.setIndexCollectionSelectorParamSpec(
 							new AggregatedIndexCollectionSelectorParameterSpecifications( paramSpecs )
 					);
 					break;
 			}
 		}
 
 		// Now, set the text for this node.  It should be the element columns.
 		String[] elementColumns = queryableCollection.getElementColumnNames( elementTable );
 		setText( elementColumns[0] );
 		setResolved();
 	}
 
 	/**
 	 * In the (rare?) case where the index selector contains multiple parameters...
 	 */
 	private static class AggregatedIndexCollectionSelectorParameterSpecifications implements ParameterSpecification {
-		private final List paramSpecs;
+		private final List<ParameterSpecification> paramSpecs;
 
-		public AggregatedIndexCollectionSelectorParameterSpecifications(List paramSpecs) {
+		public AggregatedIndexCollectionSelectorParameterSpecifications(List<ParameterSpecification> paramSpecs) {
 			this.paramSpecs = paramSpecs;
 		}
 
+		@Override
 		public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
 		throws SQLException {
 			int bindCount = 0;
-			Iterator itr = paramSpecs.iterator();
-			while ( itr.hasNext() ) {
-				final ParameterSpecification paramSpec = ( ParameterSpecification ) itr.next();
+			for ( ParameterSpecification paramSpec : paramSpecs ) {
 				bindCount += paramSpec.bind( statement, qp, session, position + bindCount );
 			}
 			return bindCount;
 		}
 
+		@Override
 		public Type getExpectedType() {
 			return null;
 		}
 
+		@Override
 		public void setExpectedType(Type expectedType) {
 		}
 
+		@Override
 		public String renderDisplayInfo() {
 			return "index-selector [" + collectDisplayInfo() + "]" ;
 		}
 
 		private String collectDisplayInfo() {
 			StringBuilder buffer = new StringBuilder();
-			Iterator itr = paramSpecs.iterator();
-			while ( itr.hasNext() ) {
-				buffer.append( ( ( ParameterSpecification ) itr.next() ).renderDisplayInfo() );
+			for ( ParameterSpecification paramSpec : paramSpecs ) {
+				buffer.append( ( paramSpec ).renderDisplayInfo() );
 			}
 			return buffer.toString();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 784559c18d..685c7a778d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2689 +1,2692 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Abstract superclass of object loading (and querying) strategies. This class implements
  * useful common functionality that concrete loaders delegate to. It is not intended that this
  * functionality would be directly accessed by client code. (Hence, all methods of this class
  * are declared <tt>protected</tt> or <tt>private</tt>.) This class relies heavily upon the
  * <tt>Loadable</tt> interface, which is the contract between this class and
  * <tt>EntityPersister</tt>s that may be loaded by it.<br>
  * <br>
  * The present implementation is able to load any number of columns of entities and at most
  * one collection role per query.
  *
  * @author Gavin King
  * @see org.hibernate.persister.entity.Loadable
  */
 public abstract class Loader {
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
-
+   	protected static final boolean DEBUG_ENABLED = LOG.isDebugEnabled();
 	private final SessionFactoryImplementor factory;
 	private ColumnNameCache columnNameCache;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	public abstract String getSQLString();
 
 	/**
 	 * An array of persisters of entity classes contained in each row of results;
 	 * implemented by all subclasses
 	 *
 	 * @return The entity persisters.
 	 */
 	protected abstract Loadable[] getEntityPersisters();
 
 	/**
 	 * An array indicating whether the entities have eager property fetching
 	 * enabled.
 	 *
 	 * @return Eager property fetching indicators.
 	 */
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return null;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner").  The
 	 * indexes contained here are relative to the result of
 	 * {@link #getEntityPersisters}.
 	 *
 	 * @return The owner indicators (see discussion above).
 	 */
 	protected int[] getOwners() {
 		return null;
 	}
 
 	/**
 	 * An array of the owner types corresponding to the {@link #getOwners()}
 	 * returns.  Indices indicating no owner would be null here.
 	 *
 	 * @return The types for the owners.
 	 */
 	protected EntityType[] getOwnerAssociationTypes() {
 		return null;
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only
 	 * collection loaders return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return null;
 	}
 
 	/**
 	 * Get the index of the entity that owns the collection, or -1
 	 * if there is no owner in the query results (ie. in the case of a
 	 * collection initializer) or no collection.
 	 */
 	protected int[] getCollectionOwners() {
 		return null;
 	}
 
 	protected int[][] getCompositeKeyManyToOneTargetIndices() {
 		return null;
 	}
 
 	/**
 	 * What lock options does this load entities with?
 	 *
 	 * @param lockOptions a collection of lock options specified dynamically via the Query interface
 	 */
 	//protected abstract LockOptions[] getLockOptions(Map lockOptions);
 	protected abstract LockMode[] getLockModes(LockOptions lockOptions);
 
 	/**
 	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
 	 * empty superclass implementation merely returns its first
 	 * argument.
 	 */
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		return sql;
 	}
 
 	/**
 	 * Does this query return objects that might be already cached
 	 * by the session, whose lock mode may need upgrading
 	 */
 	protected boolean upgradeLocks() {
 		return false;
 	}
 
 	/**
 	 * Return false is this loader is a batch entity loader
 	 */
 	protected boolean isSingleRowLoader() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL table aliases of entities whose
 	 * associations are subselect-loadable, returning
 	 * null if this loader does not support subselect
 	 * loading
 	 */
 	protected String[] getAliases() {
 		return null;
 	}
 
 	/**
 	 * Modify the SQL, adding lock hints and comments, if necessary
 	 */
 	protected String preprocessSQL(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		sql = applyLocks( sql, parameters, dialect, afterLoadActions );
 		return getFactory().getSettings().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( dialect.useFollowOnLocking() ) {
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
 			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
 				LOG.usingFollowOnLocking();
 				lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
 				lockOptions.setScope( parameters.getLockOptions().getScope() );
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptions ).lock( persister.getEntityName(), entity );
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.hasAliasSpecificLockModes() ) {
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuilder( comment.length() + sql.length() + 5 )
 					.append( "/* " )
 					.append( comment )
 					.append( " */ " )
 					.append( sql )
 					.toString();
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	public List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
 		return doQueryAndInitializeNonLazyCollections(
 				session,
 				queryParameters,
 				returnProxies,
 				null
 		);
 	}
 
 	public List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException, SQLException {
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
 		persistenceContext.beforeLoad();
 		List result;
 		try {
 			try {
 				result = doQuery( session, queryParameters, returnProxies, forcedResultTransformer );
 			}
 			finally {
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 		return result;
 	}
 
 	/**
 	 * Loads a single row from the result set.  This is the processing used from the
 	 * ScrollableResults where no collection fetches were encountered.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		final Object result;
 		try {
 			result = getRowFromResultSet(
 			        resultSet,
 					session,
 					queryParameters,
 					getLockModes( queryParameters.getLockOptions() ),
 					null,
 					hydratedObjects,
 					new EntityKey[entitySpan],
 					returnProxies
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not read next row of results",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private Object sequentialLoad(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final EntityKey keyToRead) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		Object result = null;
 		final EntityKey[] loadedKeys = new EntityKey[entitySpan];
 
 		try {
 			do {
 				Object loaded = getRowFromResultSet(
 						resultSet,
 						session,
 						queryParameters,
 						getLockModes( queryParameters.getLockOptions() ),
 						null,
 						hydratedObjects,
 						loadedKeys,
 						returnProxies
 				);
 				if ( ! keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0] )
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SessionImplementor session) throws SQLException {
 		EntityKey currentRowKey = getKeyFromResultSet(
 				persisterIndex, getEntityPersisters()[persisterIndex], null, resultSet, session
 		);
 		return keyToRead.equals( currentRowKey );
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isAfterLast() ) {
 				// don't even bother trying to read further
 				return null;
 			}
 
 			if ( resultSet.isBeforeFirst() ) {
 				resultSet.next();
 			}
 
 			// We call getKeyFromResultSet() here so that we can know the
 			// key value upon which to perform the breaking logic.  However,
 			// it is also then called from getRowFromResultSet() which is certainly
 			// not the most efficient.  But the call here is needed, and there
 			// currently is no other way without refactoring of the doQuery()/getRowFromResultSet()
 			// methods
 			final EntityKey currentKey = getKeyFromResultSet(
 					0,
 					getEntityPersisters()[0],
 					null,
 					resultSet,
 					session
 				);
 
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, currentKey );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not perform sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final boolean isLogicallyAfterLast) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isFirst() ) {
 				// don't even bother trying to read any further
 				return null;
 			}
 
 			EntityKey keyToRead = null;
 			// This check is needed since processing leaves the cursor
 			// after the last physical row for the current logical row;
 			// thus if we are after the last physical row, this might be
 			// caused by either:
 			//      1) scrolling to the last logical row
 			//      2) scrolling past the last logical row
 			// In the latter scenario, the previous logical row
 			// really is the last logical row.
 			//
 			// In all other cases, we should process back two
 			// logical records (the current logic row, plus the
 			// previous logical row).
 			if ( resultSet.isAfterLast() && isLogicallyAfterLast ) {
 				// position cursor to the last row
 				resultSet.last();
 				keyToRead = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 			}
 			else {
 				// Since the result set cursor is always left at the first
 				// physical row after the "last processed", we need to jump
 				// back one position to get the key value we are interested
 				// in skipping
 				resultSet.previous();
 
 				// sequentially read the result set in reverse until we recognize
 				// a change in the key value.  At that point, we are pointed at
 				// the last physical sequential row for the logical row in which
 				// we are interested in processing
 				boolean firstPass = true;
 				final EntityKey lastKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 				while ( resultSet.previous() ) {
 					EntityKey checkKey = getKeyFromResultSet(
 							0,
 							getEntityPersisters()[0],
 							null,
 							resultSet,
 							session
 						);
 
 					if ( firstPass ) {
 						firstPass = false;
 						keyToRead = checkKey;
 					}
 
 					if ( !lastKey.equals( checkKey ) ) {
 						break;
 					}
 				}
 
 			}
 
 			// Read backwards until we read past the first physical sequential
 			// row with the key we are interested in loading
 			while ( resultSet.previous() ) {
 				EntityKey checkKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 
 				if ( !keyToRead.equals( checkKey ) ) {
 					break;
 				}
 			}
 
 			// Finally, read ahead one row to position result set cursor
 			// at the first physical row we are interested in loading
 			resultSet.next();
 
 			// and doAfterTransactionCompletion the load
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, keyToRead );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies) throws SQLException, HibernateException {
 		return getRowFromResultSet(
 				resultSet,
 				session,
 				queryParameters,
 				lockModesArray,
 				optionalObjectKey,
 				hydratedObjects,
 				keys,
 				returnProxies,
 				null
 		);
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies,
 	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
 		registerNonExists( keys, persisters, session );
 
 		// this call is side-effecty
 		Object[] row = getRow(
 		        resultSet,
 				persisters,
 				keys,
 				queryParameters.getOptionalObject(),
 				optionalObjectKey,
 				lockModesArray,
 				hydratedObjects,
 				session
 		);
 
 		readCollectionElements( row, resultSet, session );
 
 		if ( returnProxies ) {
 			// now get an existing proxy for each row element (if there is one)
 			for ( int i = 0; i < entitySpan; i++ ) {
 				Object entity = row[i];
 				Object proxy = session.getPersistenceContext().proxyFor( persisters[i], keys[i], entity );
 				if ( entity != proxy ) {
 					// force the proxy to resolve itself
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple( getResultRow( row, resultSet, session ), getResultRowAliases() )
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
 		}
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			if ( idType.isComponentType() && getCompositeKeyManyToOneTargetIndices() != null ) {
 				// we may need to force resolve any key-many-to-one(s)
 				int[] keyManyToOneTargetIndices = getCompositeKeyManyToOneTargetIndices()[i];
 				// todo : better solution is to order the index processing based on target indices
 				//		that would account for multiple levels whereas this scheme does not
 				if ( keyManyToOneTargetIndices != null ) {
 					for ( int targetIndex : keyManyToOneTargetIndices ) {
 						if ( targetIndex < numberOfPersistersToProcess ) {
 							final Type targetIdType = persisters[targetIndex].getIdentifierType();
 							final Serializable targetId = (Serializable) targetIdType.resolve(
 									hydratedKeyState[targetIndex],
 									session,
 									null
 							);
 							// todo : need a way to signal that this key is resolved and its data resolved
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
 						}
 
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
-		boolean isDebugEnabled = LOG.isDebugEnabled();
+
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
-		   if ( isDebugEnabled ) 
+		   if ( DEBUG_ENABLED )
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
 
 		if ( LOG.isTraceEnabled() )
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
-		for (int i=0; i<loadables.length; i++ ) {
-			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
+		for ( Loadable loadable : loadables ) {
+			if ( loadable.hasSubselectLoadableCollections() ) {
+				return true;
+			}
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
-			for ( int i=0; i<keys.size(); i++ ) {
-				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
+			for ( Object key : keys ) {
+				result[j].add( ( (EntityKey[]) key )[j] );
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
-			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
-			while ( piter.hasNext() ) {
-				String name = (String) piter.next();
+			for(String name : queryParameters.getNamedParameters().keySet()){
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
-					);
+				);
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
 			if ( LOG.isTraceEnabled() )
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
 	        final LockMode lockMode,
 	        final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 				);
 		}
 
 		if ( LockMode.NONE != lockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 
 			final boolean isVersionCheckNeeded = persister.isVersioned() &&
 					session.getPersistenceContext().getEntry(object)
 							.getLockMode().lessThan( lockMode );
 			// we don't need to worry about existing version being uninitialized
 			// because this block isn't called by a re-entrant load (re-entrant
 			// loads _always_ have lock mode NONE)
 			if (isVersionCheckNeeded) {
 				//we only check the version when _upgrading_ lock modes
 				checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				//we need to upgrade the lock mode to the mode requested
 				session.getPersistenceContext().getEntry(object)
 						.setLockMode(lockMode);
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
 	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
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
 				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.getProcessedSql();
 
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
 	        final String sql,
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
 			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
 
 			limitHandler.setMaxRows( st );
 
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
-			final Map namedParams,
+			final Map<String, TypedValue> namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
-		if ( namedParams != null ) {
-			// assumes that types are all of span 1
-			Iterator iter = namedParams.entrySet().iterator();
-			final boolean debugEnabled = LOG.isDebugEnabled();
-			int result = 0;
-			while ( iter.hasNext() ) {
-				Map.Entry e = ( Map.Entry ) iter.next();
-				String name = ( String ) e.getKey();
-				TypedValue typedval = ( TypedValue ) e.getValue();
-				int[] locs = getNamedParameterLocs( name );
-				for ( int i = 0; i < locs.length; i++ ) {
-					if ( debugEnabled ) LOG.debugf( "bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex );
-					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
-				}
-				result += locs.length;
-			}
+		int result = 0;
+		if ( CollectionHelper.isEmpty( namedParams ) ) {
 			return result;
 		}
-		else {
-			return 0;
+
+		for ( String name : namedParams.keySet() ) {
+			TypedValue typedValue = namedParams.get( name );
+			int columnSpan = typedValue.getType().getColumnSpan( getFactory() );
+			int[] locs = getNamedParameterLocs( name );
+			for ( int loc : locs ) {
+				if ( DEBUG_ENABLED ) {
+					LOG.debugf(
+							"bindNamedParameters() %s -> %s [%s]",
+							typedValue.getValue(),
+							name,
+							loc + startIndex
+					);
+				}
+				int start = loc * columnSpan + startIndex;
+				typedValue.getType().nullSafeSet( statement, typedValue.getValue(), start, session );
+			}
+			result += locs.length;
 		}
+		return result;
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
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 			   if ( LOG.isDebugEnabled() )
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
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
-			LOG.trace( "Building columnName->columnIndex cache" );
+			LOG.trace( "Building columnName -> columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
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
 			        "could not collection element by index",
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
-	        final Map namedParameters,
+	        final Map<String, TypedValue> namedParameters,
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
 	        final Set querySpaces,
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
 			final Set querySpaces,
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
 			final Set querySpaces,
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
 
 	private void putResultInQueryCache(
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
 		if ( stats ) startTime = System.currentTimeMillis();
 
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
 			getFactory().getStatisticsImplementor().queryExecuted(
 					getQueryIdentifier(),
 					result.size(),
 					System.currentTimeMillis() - startTime
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
-		return;
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
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, true, Collections.<AfterLoadAction>emptyList(), session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 
 			if ( stats ) {
 				getFactory().getStatisticsImplementor().queryExecuted(
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
index 81246e8aeb..009cd54731 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectCollectionLoader.java
@@ -1,93 +1,96 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.loader.collection;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.Type;
 
 /**
  * Implements subselect fetching for a collection
  * @author Gavin King
  */
 public class SubselectCollectionLoader extends BasicCollectionLoader {
 	
 	private final Serializable[] keys;
 	private final Type[] types;
 	private final Object[] values;
-	private final Map namedParameters;
-	private final Map namedParameterLocMap;
+	private final Map<String, TypedValue> namedParameters;
+	private final Map<String, int[]> namedParameterLocMap;
 
 	public SubselectCollectionLoader(
 			QueryableCollection persister, 
 			String subquery,
 			Collection entityKeys,
 			QueryParameters queryParameters,
-			Map namedParameterLocMap,
+			Map<String, int[]> namedParameterLocMap,
 			SessionFactoryImplementor factory, 
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		super( persister, 1, subquery, factory, loadQueryInfluencers );
 
 		keys = new Serializable[ entityKeys.size() ];
 		Iterator iter = entityKeys.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			keys[i++] = ( (EntityKey) iter.next() ).getIdentifier();
 		}
 		
 		this.namedParameters = queryParameters.getNamedParameters();
 		this.types = queryParameters.getFilteredPositionalParameterTypes();
 		this.values = queryParameters.getFilteredPositionalParameterValues();
 		this.namedParameterLocMap = namedParameterLocMap;
 		
 	}
 
+	@Override
 	public void initialize(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		loadCollectionSubselect( 
 				session, 
 				keys, 
 				values,
 				types,
 				namedParameters,
 				getKeyType() 
 		);
 	}
 
+	@Override
 	public int[] getNamedParameterLocs(String name) {
-		return (int[]) namedParameterLocMap.get( name );
+		return namedParameterLocMap.get( name );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
index 4ef55cee5d..f830301645 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/SubselectOneToManyLoader.java
@@ -1,91 +1,93 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.loader.collection;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.Type;
 
 /**
  * Implements subselect fetching for a one to many association
  * @author Gavin King
  */
 public class SubselectOneToManyLoader extends OneToManyLoader {
 	
 	private final Serializable[] keys;
 	private final Type[] types;
 	private final Object[] values;
-	private final Map namedParameters;
-	private final Map namedParameterLocMap;
+	private final Map<String, TypedValue> namedParameters;
+	private final Map<String, int[]> namedParameterLocMap;
 
 	public SubselectOneToManyLoader(
 			QueryableCollection persister, 
 			String subquery,
 			Collection entityKeys,
 			QueryParameters queryParameters,
-			Map namedParameterLocMap,
+			Map<String, int[]> namedParameterLocMap,
 			SessionFactoryImplementor factory, 
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		super( persister, 1, subquery, factory, loadQueryInfluencers );
 
 		keys = new Serializable[ entityKeys.size() ];
 		Iterator iter = entityKeys.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			keys[i++] = ( (EntityKey) iter.next() ).getIdentifier();
 		}
 		
 		this.namedParameters = queryParameters.getNamedParameters();
 		this.types = queryParameters.getFilteredPositionalParameterTypes();
 		this.values = queryParameters.getFilteredPositionalParameterValues();
 		this.namedParameterLocMap = namedParameterLocMap;
 	}
 
+	@Override
 	public void initialize(Serializable id, SessionImplementor session) throws HibernateException {
 		loadCollectionSubselect( 
 				session, 
 				keys, 
 				values,
 				types,
 				namedParameters,
 				getKeyType() 
 		);
 	}
-
+	@Override
 	public int[] getNamedParameterLocs(String name) {
-		return (int[]) namedParameterLocMap.get( name );
+		return namedParameterLocMap.get( name );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
index b626e375ec..e8fd1013c6 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
@@ -1,702 +1,702 @@
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
 package org.hibernate.loader.custom;
 
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.Loader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Extension point for loaders which use a SQL result set with "unexpected" column aliases.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CustomLoader extends Loader {
 
 	// Currently *not* cachable if autodiscover types is in effect (e.g. "select * ...")
 
 	private final String sql;
 	private final Set querySpaces = new HashSet();
 	private final Map namedParameterBindPoints;
 
 	private final Queryable[] entityPersisters;
 	private final int[] entiytOwners;
 	private final EntityAliases[] entityAliases;
 
 	private final QueryableCollection[] collectionPersisters;
 	private final int[] collectionOwners;
 	private final CollectionAliases[] collectionAliases;
 
 	private final LockMode[] lockModes;
 
 	private boolean[] includeInResultRow;
 
 //	private final String[] sqlAliases;
 //	private final String[] sqlAliasSuffixes;
 	private final ResultRowProcessor rowProcessor;
 
 	// this is only needed (afaict) for processing results from the query cache;
 	// however, this cannot possibly work in the case of discovered types...
 	private Type[] resultTypes;
 
 	// this is only needed (afaict) for ResultTransformer processing...
 	private String[] transformerAliases;
 
 	public CustomLoader(CustomQuery customQuery, SessionFactoryImplementor factory) {
 		super( factory );
 
 		this.sql = customQuery.getSQL();
 		this.querySpaces.addAll( customQuery.getQuerySpaces() );
 		this.namedParameterBindPoints = customQuery.getNamedParameterBindPoints();
 
 		List entityPersisters = new ArrayList();
 		List entityOwners = new ArrayList();
 		List entityAliases = new ArrayList();
 
 		List collectionPersisters = new ArrayList();
 		List collectionOwners = new ArrayList();
 		List collectionAliases = new ArrayList();
 
 		List lockModes = new ArrayList();
 		List resultColumnProcessors = new ArrayList();
 		List nonScalarReturnList = new ArrayList();
 		List resultTypes = new ArrayList();
 		List specifiedAliases = new ArrayList();
 		int returnableCounter = 0;
 		boolean hasScalars = false;
 
 		List includeInResultRowList = new ArrayList();
 
 		Iterator itr = customQuery.getCustomQueryReturns().iterator();
 		while ( itr.hasNext() ) {
 			final Return rtn = ( Return ) itr.next();
 			if ( rtn instanceof ScalarReturn ) {
 				ScalarReturn scalarRtn = ( ScalarReturn ) rtn;
 				resultTypes.add( scalarRtn.getType() );
 				specifiedAliases.add( scalarRtn.getColumnAlias() );
 				resultColumnProcessors.add(
 						new ScalarResultColumnProcessor(
 								StringHelper.unquote( scalarRtn.getColumnAlias(), factory.getDialect() ),
 								scalarRtn.getType()
 						)
 				);
 				includeInResultRowList.add( true );
 				hasScalars = true;
 			}
 			else if ( rtn instanceof RootReturn ) {
 				RootReturn rootRtn = ( RootReturn ) rtn;
 				Queryable persister = ( Queryable ) factory.getEntityPersister( rootRtn.getEntityName() );
 				entityPersisters.add( persister );
 				lockModes.add( (rootRtn.getLockMode()) );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				entityOwners.add( -1 );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( rootRtn.getAlias() );
 				entityAliases.add( rootRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof CollectionReturn ) {
 				CollectionReturn collRtn = ( CollectionReturn ) rtn;
 				String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 				QueryableCollection persister = ( QueryableCollection ) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				lockModes.add( collRtn.getLockMode() );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				collectionOwners.add( -1 );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( collRtn.getAlias() );
 				collectionAliases.add( collRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = ( Queryable ) ( ( EntityType ) elementType ).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( -1 );
 					entityAliases.add( collRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
 				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof EntityFetchReturn ) {
 				EntityFetchReturn fetchRtn = ( EntityFetchReturn ) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				entityOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				EntityType fetchedType = ( EntityType ) ownerPersister.getPropertyType( fetchRtn.getOwnerProperty() );
 				String entityName = fetchedType.getAssociatedEntityName( getFactory() );
 				Queryable persister = ( Queryable ) factory.getEntityPersister( entityName );
 				entityPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				entityAliases.add( fetchRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 				includeInResultRowList.add( false );
 			}
 			else if ( rtn instanceof CollectionFetchReturn ) {
 				CollectionFetchReturn fetchRtn = ( CollectionFetchReturn ) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				collectionOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				String role = ownerPersister.getEntityName() + '.' + fetchRtn.getOwnerProperty();
 				QueryableCollection persister = ( QueryableCollection ) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				collectionAliases.add( fetchRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = ( Queryable ) ( ( EntityType ) elementType ).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( ownerIndex );
 					entityAliases.add( fetchRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
 				includeInResultRowList.add( false );
 			}
 			else {
 				throw new HibernateException( "unexpected custom query return type : " + rtn.getClass().getName() );
 			}
 		}
 
 		this.entityPersisters = new Queryable[ entityPersisters.size() ];
 		for ( int i = 0; i < entityPersisters.size(); i++ ) {
 			this.entityPersisters[i] = ( Queryable ) entityPersisters.get( i );
 		}
 		this.entiytOwners = ArrayHelper.toIntArray( entityOwners );
 		this.entityAliases = new EntityAliases[ entityAliases.size() ];
 		for ( int i = 0; i < entityAliases.size(); i++ ) {
 			this.entityAliases[i] = ( EntityAliases ) entityAliases.get( i );
 		}
 
 		this.collectionPersisters = new QueryableCollection[ collectionPersisters.size() ];
 		for ( int i = 0; i < collectionPersisters.size(); i++ ) {
 			this.collectionPersisters[i] = ( QueryableCollection ) collectionPersisters.get( i );
 		}
 		this.collectionOwners = ArrayHelper.toIntArray( collectionOwners );
 		this.collectionAliases = new CollectionAliases[ collectionAliases.size() ];
 		for ( int i = 0; i < collectionAliases.size(); i++ ) {
 			this.collectionAliases[i] = ( CollectionAliases ) collectionAliases.get( i );
 		}
 
 		this.lockModes = new LockMode[ lockModes.size() ];
 		for ( int i = 0; i < lockModes.size(); i++ ) {
 			this.lockModes[i] = ( LockMode ) lockModes.get( i );
 		}
 
 		this.resultTypes = ArrayHelper.toTypeArray( resultTypes );
 		this.transformerAliases = ArrayHelper.toStringArray( specifiedAliases );
 
 		this.rowProcessor = new ResultRowProcessor(
 				hasScalars,
 		        ( ResultColumnProcessor[] ) resultColumnProcessors.toArray( new ResultColumnProcessor[ resultColumnProcessors.size() ] )
 		);
 
 		this.includeInResultRow = ArrayHelper.toBooleanArray( includeInResultRowList );
 	}
 
 	private Queryable determineAppropriateOwnerPersister(NonScalarReturn ownerDescriptor) {
 		String entityName = null;
 		if ( ownerDescriptor instanceof RootReturn ) {
 			entityName = ( ( RootReturn ) ownerDescriptor ).getEntityName();
 		}
 		else if ( ownerDescriptor instanceof CollectionReturn ) {
 			CollectionReturn collRtn = ( CollectionReturn ) ownerDescriptor;
 			String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 			CollectionPersister persister = getFactory().getCollectionPersister( role );
 			EntityType ownerType = ( EntityType ) persister.getElementType();
 			entityName = ownerType.getAssociatedEntityName( getFactory() );
 		}
 		else if ( ownerDescriptor instanceof FetchReturn ) {
 			FetchReturn fetchRtn = ( FetchReturn ) ownerDescriptor;
 			Queryable persister = determineAppropriateOwnerPersister( fetchRtn.getOwner() );
 			Type ownerType = persister.getPropertyType( fetchRtn.getOwnerProperty() );
 			if ( ownerType.isEntityType() ) {
 				entityName = ( ( EntityType ) ownerType ).getAssociatedEntityName( getFactory() );
 			}
 			else if ( ownerType.isCollectionType() ) {
 				Type ownerCollectionElementType = ( ( CollectionType ) ownerType ).getElementType( getFactory() );
 				if ( ownerCollectionElementType.isEntityType() ) {
 					entityName = ( ( EntityType ) ownerCollectionElementType ).getAssociatedEntityName( getFactory() );
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			throw new HibernateException( "Could not determine fetch owner : " + ownerDescriptor );
 		}
 
 		return ( Queryable ) getFactory().getEntityPersister( entityName );
 	}
 
 	@Override
     protected String getQueryIdentifier() {
 		return sql;
 	}
 
 	@Override
     public String getSQLString() {
 		return sql;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
     protected LockMode[] getLockModes(LockOptions lockOptions) {
 		return lockModes;
 	}
 
 	@Override
     protected Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	@Override
     protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
     protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 
 	@Override
     protected int[] getOwners() {
 		return entiytOwners;
 	}
 
 	public List list(SessionImplementor session, QueryParameters queryParameters) throws HibernateException {
 		return list( session, queryParameters, querySpaces, resultTypes );
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		final LockOptions lockOptions = parameters.getLockOptions();
 		if ( lockOptions == null ||
 				( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 		// user is request locking, lets see if we can apply locking directly to the SQL...
 
 		// 		some dialects wont allow locking with paging...
 		afterLoadActions.add(
 				new AfterLoadAction() {
 					private final LockOptions originalLockOptions = lockOptions.makeCopy();
 					@Override
 					public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 						( (Session) session ).buildLockRequest( originalLockOptions ).lock( persister.getEntityName(), entity );
 					}
 				}
 		);
 		parameters.getLockOptions().setLockMode( LockMode.READ );
 
 		return sql;
 	}
 
 	public ScrollableResults scroll(
 			final QueryParameters queryParameters,
 			final SessionImplementor session) throws HibernateException {
 		return scroll(
 				queryParameters,
 				resultTypes,
 				getHolderInstantiator( queryParameters.getResultTransformer(), getReturnAliasesForTransformer() ),
 				session
 		);
 	}
 
 	static private HolderInstantiator getHolderInstantiator(ResultTransformer resultTransformer, String[] queryReturnAliases) {
 		if ( resultTransformer == null ) {
 			return HolderInstantiator.NOOP_INSTANTIATOR;
 		}
 		else {
 			return new HolderInstantiator(resultTransformer, queryReturnAliases);
 		}
 	}
 
 	@Override
     protected String[] getResultRowAliases() {
 		return transformerAliases;
 	}
 
 	@Override
     protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveResultTransformer( null, resultTransformer );
 	}
 
 	@Override
     protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	@Override
     protected Object getResultColumnOrRow(
 			Object[] row,
 	        ResultTransformer transformer,
 	        ResultSet rs,
 	        SessionImplementor session) throws SQLException, HibernateException {
 		return rowProcessor.buildResultRow( row, rs, transformer != null, session );
 	}
 
 	@Override
     protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return rowProcessor.buildResultRow( row, rs, session );
 	}
 
 	@Override
     protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		// meant to handle dynamic instantiation queries...(Copy from QueryLoader)
 		HolderInstantiator holderInstantiator = HolderInstantiator.getHolderInstantiator(
 				null,
 				resultTransformer,
 				getReturnAliasesForTransformer()
 		);
 		if ( holderInstantiator.isRequired() ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = ( Object[] ) results.get( i );
 				Object result = holderInstantiator.instantiate(row);
 				results.set( i, result );
 			}
 
 			return resultTransformer.transformList(results);
 		}
 		else {
 			return results;
 		}
 	}
 
 	private String[] getReturnAliasesForTransformer() {
 		return transformerAliases;
 	}
 
 	@Override
     protected EntityAliases[] getEntityAliases() {
 		return entityAliases;
 	}
 
 	@Override
     protected CollectionAliases[] getCollectionAliases() {
 		return collectionAliases;
 	}
 
 	@Override
     public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object loc = namedParameterBindPoints.get( name );
 		if ( loc == null ) {
 			throw new QueryException(
 					"Named parameter does not appear in Query: " + name,
 					sql
 			);
 		}
 		if ( loc instanceof Integer ) {
-			return new int[] { ( ( Integer ) loc ).intValue() };
+			return new int[] { (Integer) loc };
 		}
 		else {
 			return ArrayHelper.toIntArray( ( List ) loc );
 		}
 	}
 
 
 	public class ResultRowProcessor {
 		private final boolean hasScalars;
 		private ResultColumnProcessor[] columnProcessors;
 
 		public ResultRowProcessor(boolean hasScalars, ResultColumnProcessor[] columnProcessors) {
 			this.hasScalars = hasScalars || ( columnProcessors == null || columnProcessors.length == 0 );
 			this.columnProcessors = columnProcessors;
 		}
 
 		public void prepareForAutoDiscovery(Metadata metadata) throws SQLException {
 			if ( columnProcessors == null || columnProcessors.length == 0 ) {
 				int columns = metadata.getColumnCount();
 				columnProcessors = new ResultColumnProcessor[ columns ];
 				for ( int i = 1; i <= columns; i++ ) {
 					columnProcessors[ i - 1 ] = new ScalarResultColumnProcessor( i );
 				}
 
 			}
 		}
 
 		/**
 		 * Build a logical result row.
 		 * <p/>
 		 * At this point, Loader has already processed all non-scalar result data.  We
 		 * just need to account for scalar result data here...
 		 *
 		 * @param data Entity data defined as "root returns" and already handled by the
 		 * normal Loader mechanism.
 		 * @param resultSet The JDBC result set (positioned at the row currently being processed).
 		 * @param hasTransformer Does this query have an associated {@link ResultTransformer}
 		 * @param session The session from which the query request originated.
 		 * @return The logical result row
 		 * @throws SQLException
 		 * @throws HibernateException
 		 */
 		public Object buildResultRow(
 				Object[] data,
 				ResultSet resultSet,
 				boolean hasTransformer,
 				SessionImplementor session) throws SQLException, HibernateException {
 			Object[] resultRow = buildResultRow( data, resultSet, session );
 			return ( hasTransformer )
 			       ? resultRow
 			       : ( resultRow.length == 1 )
 			         ? resultRow[0]
 			         : resultRow;
 		}
 		public Object[] buildResultRow(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			Object[] resultRow;
 			if ( !hasScalars ) {
 				resultRow = data;
 			}
 			else {
 				// build an array with indices equal to the total number
 				// of actual returns in the result Hibernate will return
 				// for this query (scalars + non-scalars)
 				resultRow = new Object[ columnProcessors.length ];
 				for ( int i = 0; i < columnProcessors.length; i++ ) {
 					resultRow[i] = columnProcessors[i].extract( data, resultSet, session );
 				}
 			}
 
 			return resultRow;
 		}
 	}
 
 	private static interface ResultColumnProcessor {
 		public Object extract(Object[] data, ResultSet resultSet, SessionImplementor session) throws SQLException, HibernateException;
 		public void performDiscovery(Metadata metadata, List<Type> types, List<String> aliases) throws SQLException, HibernateException;
 	}
 
 	public class NonScalarResultColumnProcessor implements ResultColumnProcessor {
 		private final int position;
 
 		public NonScalarResultColumnProcessor(int position) {
 			this.position = position;
 		}
 
 		@Override
 		public Object extract(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			return data[ position ];
 		}
 
 		@Override
 		public void performDiscovery(Metadata metadata, List<Type> types, List<String> aliases) {
 		}
 
 	}
 
 	public class ScalarResultColumnProcessor implements ResultColumnProcessor {
 		private int position = -1;
 		private String alias;
 		private Type type;
 
 		public ScalarResultColumnProcessor(int position) {
 			this.position = position;
 		}
 
 		public ScalarResultColumnProcessor(String alias, Type type) {
 			this.alias = alias;
 			this.type = type;
 		}
 
 		@Override
 		public Object extract(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			return type.nullSafeGet( resultSet, alias, session, null );
 		}
 
 		@Override
 		public void performDiscovery(Metadata metadata, List<Type> types, List<String> aliases) throws SQLException {
 			if ( alias == null ) {
 				alias = metadata.getColumnName( position );
 			}
 			else if ( position < 0 ) {
 				position = metadata.resolveColumnPosition( alias );
 			}
 			if ( type == null ) {
 				type = metadata.getHibernateType( position );
 			}
 			types.add( type );
 			aliases.add( alias );
 		}
 	}
 
 	@Override
     protected void autoDiscoverTypes(ResultSet rs) {
 		try {
 			Metadata metadata = new Metadata( getFactory(), rs );
 			rowProcessor.prepareForAutoDiscovery( metadata );
 
 			List<String> aliases = new ArrayList<String>();
 			List<Type> types = new ArrayList<Type>();
 			for ( int i = 0; i < rowProcessor.columnProcessors.length; i++ ) {
 				rowProcessor.columnProcessors[i].performDiscovery( metadata, types, aliases );
 			}
 
 			// lets make sure we did not end up with duplicate aliases.  this can occur when the user supplied query
 			// did not rename same-named columns.  e.g.:
 			//		select u.username, u2.username from t_user u, t_user u2 ...
 			//
 			// the above will lead to an unworkable situation in most cases (the difference is how the driver/db
 			// handle this situation.  But if the 'aliases' variable contains duplicate names, then we have that
 			// troublesome condition, so lets throw an error.  See HHH-5992
 			final HashSet<String> aliasesSet = new HashSet<String>();
 			for ( String alias : aliases ) {
 				boolean alreadyExisted = !aliasesSet.add( alias );
 				if ( alreadyExisted ) {
 					throw new NonUniqueDiscoveredSqlAliasException(
 							"Encountered a duplicated sql alias [" + alias +
 									"] during auto-discovery of a native-sql query"
 					);
 				}
 			}
 
 			resultTypes = ArrayHelper.toTypeArray( types );
 			transformerAliases = ArrayHelper.toStringArray( aliases );
 		}
 		catch ( SQLException e ) {
 			throw new HibernateException( "Exception while trying to autodiscover types.", e );
 		}
 	}
 
 	private static class Metadata {
 		private final SessionFactoryImplementor factory;
 		private final ResultSet resultSet;
 		private final ResultSetMetaData resultSetMetaData;
 
 		public Metadata(SessionFactoryImplementor factory, ResultSet resultSet) throws HibernateException {
 			try {
 				this.factory = factory;
 				this.resultSet = resultSet;
 				this.resultSetMetaData = resultSet.getMetaData();
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not extract result set metadata", e );
 			}
 		}
 
 		public int getColumnCount() throws HibernateException {
 			try {
 				return resultSetMetaData.getColumnCount();
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not determine result set column count", e );
 			}
 		}
 
 		public int resolveColumnPosition(String columnName) throws HibernateException {
 			try {
 				return resultSet.findColumn( columnName );
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not resolve column name in result set [" + columnName + "]", e );
 			}
 		}
 
 		public String getColumnName(int position) throws HibernateException {
 			try {
 				return factory.getDialect().getColumnAliasExtractor().extractColumnAlias( resultSetMetaData, position );
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not resolve column name [" + position + "]", e );
 			}
 		}
 
 		public Type getHibernateType(int columnPos) throws SQLException {
 			int columnType = resultSetMetaData.getColumnType( columnPos );
 			int scale = resultSetMetaData.getScale( columnPos );
 			int precision = resultSetMetaData.getPrecision( columnPos );
             int length = precision;
             if ( columnType == 1 && precision == 0 ) {
                 length = resultSetMetaData.getColumnDisplaySize( columnPos );
             }
 			return factory.getTypeResolver().heuristicType(
 					factory.getDialect().getHibernateTypeName(
 							columnType,
 							length,
 							precision,
 							scale
 					)
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
index 3a45e39d53..e3a4c73136 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,639 +1,617 @@
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
 package org.hibernate.loader.hql;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
 import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.SelectClause;
 import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A delegate that implements the Loader part of QueryTranslator.
  *
  * @author josh
  */
 public class QueryLoader extends BasicLoader {
 
 	/**
 	 * The query translator that is delegating to this object.
 	 */
 	private QueryTranslatorImpl queryTranslator;
 
 	private Queryable[] entityPersisters;
 	private String[] entityAliases;
 	private String[] sqlAliases;
 	private String[] sqlAliasSuffixes;
 	private boolean[] includeInSelect;
 
 	private String[] collectionSuffixes;
 
 	private boolean hasScalars;
 	private String[][] scalarColumnNames;
 	//private Type[] sqlResultTypes;
 	private Type[] queryReturnTypes;
 
-	private final Map sqlAliasByEntityAlias = new HashMap(8);
+	private final Map<String, String> sqlAliasByEntityAlias = new HashMap<String, String>(8);
 
 	private EntityType[] ownerAssociationTypes;
 	private int[] owners;
 	private boolean[] entityEagerPropertyFetches;
 
 	private int[] collectionOwners;
 	private QueryableCollection[] collectionPersisters;
 
 	private int selectLength;
 
 	private AggregatedSelectExpression aggregatedSelectExpression;
 	private String[] queryReturnAliases;
 
 	private LockMode[] defaultLockModes;
 
 
 	/**
 	 * Creates a new Loader implementation.
 	 *
 	 * @param queryTranslator The query translator that is the delegator.
 	 * @param factory The factory from which this loader is being created.
 	 * @param selectClause The AST representing the select clause for loading.
 	 */
 	public QueryLoader(
 			final QueryTranslatorImpl queryTranslator,
 	        final SessionFactoryImplementor factory,
 	        final SelectClause selectClause) {
 		super( factory );
 		this.queryTranslator = queryTranslator;
 		initialize( selectClause );
 		postInstantiate();
 	}
 
 	private void initialize(SelectClause selectClause) {
 
 		List fromElementList = selectClause.getFromElementsForLoad();
 
 		hasScalars = selectClause.isScalarSelect();
 		scalarColumnNames = selectClause.getColumnNames();
 		//sqlResultTypes = selectClause.getSqlResultTypes();
 		queryReturnTypes = selectClause.getQueryReturnTypes();
 
 		aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
 		queryReturnAliases = selectClause.getQueryReturnAliases();
 
 		List collectionFromElements = selectClause.getCollectionFromElements();
 		if ( collectionFromElements != null && collectionFromElements.size()!=0 ) {
 			int length = collectionFromElements.size();
 			collectionPersisters = new QueryableCollection[length];
 			collectionOwners = new int[length];
 			collectionSuffixes = new String[length];
 			for ( int i=0; i<length; i++ ) {
 				FromElement collectionFromElement = (FromElement) collectionFromElements.get(i);
 				collectionPersisters[i] = collectionFromElement.getQueryableCollection();
 				collectionOwners[i] = fromElementList.indexOf( collectionFromElement.getOrigin() );
 //				collectionSuffixes[i] = collectionFromElement.getColumnAliasSuffix();
 //				collectionSuffixes[i] = Integer.toString( i ) + "_";
 				collectionSuffixes[i] = collectionFromElement.getCollectionSuffix();
 			}
 		}
 
 		int size = fromElementList.size();
 		entityPersisters = new Queryable[size];
 		entityEagerPropertyFetches = new boolean[size];
 		entityAliases = new String[size];
 		sqlAliases = new String[size];
 		sqlAliasSuffixes = new String[size];
 		includeInSelect = new boolean[size];
 		owners = new int[size];
 		ownerAssociationTypes = new EntityType[size];
 
 		for ( int i = 0; i < size; i++ ) {
 			final FromElement element = ( FromElement ) fromElementList.get( i );
 			entityPersisters[i] = ( Queryable ) element.getEntityPersister();
 
 			if ( entityPersisters[i] == null ) {
 				throw new IllegalStateException( "No entity persister for " + element.toString() );
 			}
 
 			entityEagerPropertyFetches[i] = element.isAllPropertyFetch();
 			sqlAliases[i] = element.getTableAlias();
 			entityAliases[i] = element.getClassAlias();
 			sqlAliasByEntityAlias.put( entityAliases[i], sqlAliases[i] );
 			// TODO should we just collect these like with the collections above?
 			sqlAliasSuffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + "_";
 //			sqlAliasSuffixes[i] = element.getColumnAliasSuffix();
 			includeInSelect[i] = !element.isFetch();
 			if ( includeInSelect[i] ) {
 				selectLength++;
 			}
 
 			owners[i] = -1; //by default
 			if ( element.isFetch() ) {
 				if ( element.isCollectionJoin() || element.getQueryableCollection() != null ) {
 					// This is now handled earlier in this method.
 				}
 				else if ( element.getDataType().isEntityType() ) {
 					EntityType entityType = ( EntityType ) element.getDataType();
 					if ( entityType.isOneToOne() ) {
 						owners[i] = fromElementList.indexOf( element.getOrigin() );
 					}
 					ownerAssociationTypes[i] = entityType;
 				}
 			}
 		}
 
 		//NONE, because its the requested lock mode, not the actual! 
 		defaultLockModes = ArrayHelper.fillArray( LockMode.NONE, size );
 	}
 
 	public AggregatedSelectExpression getAggregatedSelectExpression() {
 		return aggregatedSelectExpression;
 	}
 
 
 	// -- Loader implementation --
 
 	public final void validateScrollability() throws HibernateException {
 		queryTranslator.validateScrollability();
 	}
-
+	@Override
 	protected boolean needsFetchingScroll() {
 		return queryTranslator.containsCollectionFetches();
 	}
-
+	@Override
 	public Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
-
+	@Override
 	public String[] getAliases() {
 		return sqlAliases;
 	}
 
 	public String[] getSqlAliasSuffixes() {
 		return sqlAliasSuffixes;
 	}
-
+	@Override
 	public String[] getSuffixes() {
 		return getSqlAliasSuffixes();
 	}
-
+	@Override
 	public String[] getCollectionSuffixes() {
 		return collectionSuffixes;
 	}
-
+	@Override
 	protected String getQueryIdentifier() {
 		return queryTranslator.getQueryIdentifier();
 	}
 
 	/**
 	 * The SQL query string to be called.
 	 */
+	@Override
 	public String getSQLString() {
 		return queryTranslator.getSQLString();
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only collection loaders
 	 * return a non-null value
 	 */
+	@Override
 	protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
-
+	@Override
 	protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
-
+	@Override
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return entityEagerPropertyFetches;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner")
 	 */
+	@Override
 	protected int[] getOwners() {
 		return owners;
 	}
-
+	@Override
 	protected EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	// -- Loader overrides --
-
+	@Override
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	/**
 	 * @param lockOptions a collection of lock modes specified dynamically via the Query interface
 	 */
+	@Override
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		if ( lockOptions == null ) {
 			return defaultLockModes;
 		}
 
 		if ( lockOptions.getAliasLockCount() == 0
 				&& ( lockOptions.getLockMode() == null || LockMode.NONE.equals( lockOptions.getLockMode() ) ) ) {
 			return defaultLockModes;
 		}
 
 		// unfortunately this stuff can't be cached because
 		// it is per-invocation, not constant for the
 		// QueryTranslator instance
 
 		LockMode[] lockModesArray = new LockMode[entityAliases.length];
 		for ( int i = 0; i < entityAliases.length; i++ ) {
 			LockMode lockMode = lockOptions.getEffectiveLockMode( entityAliases[i] );
 			if ( lockMode == null ) {
 				//NONE, because its the requested lock mode, not the actual!
 				lockMode = LockMode.NONE;
 			}
 			lockModesArray[i] = lockMode;
 		}
 
 		return lockModesArray;
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		// we are given a map of user-alias -> lock mode
 		// create a new map of sql-alias -> lock mode
 
 		final LockOptions lockOptions = parameters.getLockOptions();
 
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 
 		// user is request locking, lets see if we can apply locking directly to the SQL...
 
 		// 		some dialects wont allow locking with paging...
 		if ( shouldUseFollowOnLocking( parameters, dialect, afterLoadActions ) ) {
 			return sql;
 		}
 
 		//		there are other conditions we might want to add here, such as checking the result types etc
 		//		but those are better served after we have redone the SQL generation to use ASTs.
 
 
 		// we need both the set of locks and the columns to reference in locks
 		// as the ultimate output of this section...
 		final LockOptions locks = new LockOptions( lockOptions.getLockMode() );
-		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
+		final Map<String, String[]> keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap<String, String[]>() : null;
 
 		locks.setScope( lockOptions.getScope() );
 		locks.setTimeOut( lockOptions.getTimeOut() );
 
-		final Iterator itr = sqlAliasByEntityAlias.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			final Map.Entry entry = (Map.Entry) itr.next();
-			final String userAlias = (String) entry.getKey();
-			final String drivingSqlAlias = (String) entry.getValue();
+		for ( Map.Entry<String, String> entry : sqlAliasByEntityAlias.entrySet() ) {
+			final String userAlias =  entry.getKey();
+			final String drivingSqlAlias = entry.getValue();
 			if ( drivingSqlAlias == null ) {
 				throw new IllegalArgumentException( "could not locate alias to apply lock mode : " + userAlias );
 			}
 			// at this point we have (drivingSqlAlias) the SQL alias of the driving table
 			// corresponding to the given user alias.  However, the driving table is not
 			// (necessarily) the table against which we want to apply locks.  Mainly,
 			// the exception case here is joined-subclass hierarchies where we instead
 			// want to apply the lock against the root table (for all other strategies,
 			// it just happens that driving and root are the same).
-			final QueryNode select = ( QueryNode ) queryTranslator.getSqlAST();
-			final Lockable drivingPersister = ( Lockable ) select.getFromClause()
+			final QueryNode select = (QueryNode) queryTranslator.getSqlAST();
+			final Lockable drivingPersister = (Lockable) select.getFromClause()
 					.findFromElementByUserOrSqlAlias( userAlias, drivingSqlAlias )
 					.getQueryable();
 			final String sqlAlias = drivingPersister.getRootTableAlias( drivingSqlAlias );
 
 			final LockMode effectiveLockMode = lockOptions.getEffectiveLockMode( userAlias );
 			locks.setAliasSpecificLockMode( sqlAlias, effectiveLockMode );
 
 			if ( keyColumnNames != null ) {
 				keyColumnNames.put( sqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 			}
 		}
 
 		// apply the collected locks and columns
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
-
+	@Override
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 		// todo : scalars???
 //		if ( row.length != lockModesArray.length ) {
 //			return;
 //		}
 //
 //		for ( int i = 0; i < lockModesArray.length; i++ ) {
 //			if ( LockMode.OPTIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //				final EntityEntry pcEntry =
 //			}
 //			else if ( LockMode.PESSIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //
 //			}
 //		}
 	}
-
+	@Override
 	protected boolean upgradeLocks() {
 		return true;
 	}
 
 	private boolean hasSelectNew() {
 		return aggregatedSelectExpression != null &&  aggregatedSelectExpression.getResultTransformer() != null;
 	}
-
+	@Override
 	protected String[] getResultRowAliases() {
 		return queryReturnAliases;
 	}
-	
+	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.resolveResultTransformer( implicitResultTransformer, resultTransformer );
 	}
-
+	@Override
 	protected boolean[] includeInResultRow() {
 		boolean[] includeInResultTuple = includeInSelect;
 		if ( hasScalars ) {
 			includeInResultTuple = new boolean[ queryReturnTypes.length ];
 			Arrays.fill( includeInResultTuple, true );
 		}
 		return includeInResultTuple;
 	}
-
+	@Override
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		Object[] resultRow = getResultRow( row, rs, session );
 		boolean hasTransform = hasSelectNew() || transformer!=null;
 		return ( ! hasTransform && resultRow.length == 1 ?
 				resultRow[ 0 ] :
 				resultRow
 		);
 	}
 
+	@Override
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = scalarColumnNames;
 			int queryCols = queryReturnTypes.length;
 			resultRow = new Object[queryCols];
 			for ( int i = 0; i < queryCols; i++ ) {
 				resultRow[i] = queryReturnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
 		else {
 			resultRow = toResultRow( row );
 		}
 		return resultRow;
 	}
 
+	@SuppressWarnings("unchecked")
+	@Override
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		// meant to handle dynamic instantiation queries...
 		HolderInstantiator holderInstantiator = buildHolderInstantiator( resultTransformer );
 		if ( holderInstantiator.isRequired() ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = ( Object[] ) results.get( i );
 				Object result = holderInstantiator.instantiate(row);
 				results.set( i, result );
 			}
 
 			if ( !hasSelectNew() && resultTransformer != null ) {
 				return resultTransformer.transformList(results);
 			}
 			else {
 				return results;
 			}
 		}
 		else {
 			return results;
 		}
 	}
 
 	private HolderInstantiator buildHolderInstantiator(ResultTransformer queryLocalResultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.getHolderInstantiator(
 				implicitResultTransformer,
 				queryLocalResultTransformer,
 				queryReturnAliases
 		);
 	}
 	// --- Query translator methods ---
 
 	public List list(
 			SessionImplementor session,
 			QueryParameters queryParameters) throws HibernateException {
 		checkQuery( queryParameters );
 		return list( session, queryParameters, queryTranslator.getQuerySpaces(), queryReturnTypes );
 	}
 
 	private void checkQuery(QueryParameters queryParameters) {
 		if ( hasSelectNew() && queryParameters.getResultTransformer() != null ) {
 			throw new QueryException( "ResultTransformer is not allowed for 'select new' queries." );
 		}
 	}
 
 	public Iterator iterate(
 			QueryParameters queryParameters,
 			EventSource session) throws HibernateException {
 		checkQuery( queryParameters );
 		final boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.currentTimeMillis();
 		}
 
 		try {
 			if ( queryParameters.isCallable() ) {
 				throw new QueryException("iterate() not supported for callable statements");
 			}
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, Collections.<AfterLoadAction>emptyList(), session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 			final Iterator result = new IteratorImpl(
 					rs,
 			        st,
 			        session,
 			        queryParameters.isReadOnly( session ),
 			        queryReturnTypes,
 			        queryTranslator.getColumnNames(),
 			        buildHolderInstantiator( queryParameters.getResultTransformer() )
 			);
 
 			if ( stats ) {
 				session.getFactory().getStatisticsImplementor().queryExecuted(
 //						"HQL: " + queryTranslator.getQueryString(),
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
 				);
 			}
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query using iterate",
 			        getSQLString()
 				);
 		}
 
 	}
 
 	public ScrollableResults scroll(
 			final QueryParameters queryParameters,
 	        final SessionImplementor session) throws HibernateException {
 		checkQuery( queryParameters );
 		return scroll( 
 				queryParameters,
 				queryReturnTypes,
 				buildHolderInstantiator( queryParameters.getResultTransformer() ),
 				session
 		);
 	}
 
 	// -- Implementation private methods --
 
 	private Object[] toResultRow(Object[] row) {
 		if ( selectLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[selectLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					result[j++] = row[i];
 				}
 			}
 			return result;
 		}
 	}
 
 	/**
 	 * Returns the locations of all occurrences of the named parameter.
 	 */
+	@Override
 	public int[] getNamedParameterLocs(String name) throws QueryException {
 		return queryTranslator.getParameterTranslations().getNamedParameterSqlLocations( name );
 	}
 
 	/**
 	 * We specifically override this method here, because in general we know much more
 	 * about the parameters and their appropriate bind positions here then we do in
-	 * our super because we track them explciitly here through the ParameterSpecification
+	 * our super because we track them explicitly here through the ParameterSpecification
 	 * interface.
 	 *
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
+	@Override
 	protected int bindParameterValues(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException {
-//		int position = bindFilterParameterValues( statement, queryParameters, startIndex, session );
 		int position = startIndex;
-//		List parameterSpecs = queryTranslator.getSqlAST().getWalker().getParameters();
-		List parameterSpecs = queryTranslator.getCollectedParameterSpecifications();
-		Iterator itr = parameterSpecs.iterator();
-		while ( itr.hasNext() ) {
-			ParameterSpecification spec = ( ParameterSpecification ) itr.next();
+		List<ParameterSpecification> parameterSpecs = queryTranslator.getCollectedParameterSpecifications();
+		for ( ParameterSpecification spec : parameterSpecs ) {
 			position += spec.bind( statement, queryParameters, session, position );
 		}
 		return position - startIndex;
 	}
-
-	private int bindFilterParameterValues(
-			PreparedStatement st,
-			QueryParameters queryParameters,
-			int position,
-			SessionImplementor session) throws SQLException {
-		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
-		// see the discussion there in DynamicFilterParameterSpecification's javadocs as to why
-		// it is currently not done that way.
-		int filteredParamCount = queryParameters.getFilteredPositionalParameterTypes() == null
-				? 0
-				: queryParameters.getFilteredPositionalParameterTypes().length;
-		int nonfilteredParamCount = queryParameters.getPositionalParameterTypes() == null
-				? 0
-				: queryParameters.getPositionalParameterTypes().length;
-		int filterParamCount = filteredParamCount - nonfilteredParamCount;
-		for ( int i = 0; i < filterParamCount; i++ ) {
-			Type type = queryParameters.getFilteredPositionalParameterTypes()[i];
-			Object value = queryParameters.getFilteredPositionalParameterValues()[i];
-			type.nullSafeSet( st, value, position, session );
-			position += type.getColumnSpan( getFactory() );
-		}
-
-		return position;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java
index de27388ea4..a5fffb6804 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/AbstractExplicitParameterSpecification.java
@@ -1,76 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.param;
 import org.hibernate.type.Type;
 
 /**
  * Convenience base class for explicitly defined query parameters.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractExplicitParameterSpecification implements ExplicitParameterSpecification {
 	private final int sourceLine;
 	private final int sourceColumn;
 	private Type expectedType;
 
 	/**
 	 * Constructs an AbstractExplicitParameterSpecification.
 	 *
 	 * @param sourceLine See {@link #getSourceLine()}
 	 * @param sourceColumn See {@link #getSourceColumn()} 
 	 */
 	protected AbstractExplicitParameterSpecification(int sourceLine, int sourceColumn) {
 		this.sourceLine = sourceLine;
 		this.sourceColumn = sourceColumn;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int getSourceLine() {
 		return sourceLine;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int getSourceColumn() {
 		return sourceColumn;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getExpectedType() {
 		return expectedType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setExpectedType(Type expectedType) {
 		this.expectedType = expectedType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
index 67c774f2d1..eb8f6be70a 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/CollectionFilterKeyParameterSpecification.java
@@ -1,91 +1,83 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.param;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * A specialized ParameterSpecification impl for dealing with a collection-key as part of a collection filter
  * compilation.
  *
  * @author Steve Ebersole
  */
 public class CollectionFilterKeyParameterSpecification implements ParameterSpecification {
 	private final String collectionRole;
 	private final Type keyType;
 	private final int queryParameterPosition;
 
 	/**
 	 * Creates a specialized collection-filter collection-key parameter spec.
 	 *
 	 * @param collectionRole The collection role being filtered.
 	 * @param keyType The mapped collection-key type.
 	 * @param queryParameterPosition The position within {@link org.hibernate.engine.spi.QueryParameters} where
 	 * we can find the appropriate param value to bind.
 	 */
 	public CollectionFilterKeyParameterSpecification(String collectionRole, Type keyType, int queryParameterPosition) {
 		this.collectionRole = collectionRole;
 		this.keyType = keyType;
 		this.queryParameterPosition = queryParameterPosition;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int bind(
 			PreparedStatement statement,
 			QueryParameters qp,
 			SessionImplementor session,
 			int position) throws SQLException {
 		Object value = qp.getPositionalParameterValues()[queryParameterPosition];
 		keyType.nullSafeSet( statement, value, position, session );
 		return keyType.getColumnSpan( session.getFactory() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getExpectedType() {
 		return keyType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setExpectedType(Type expectedType) {
 		// todo : throw exception?
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String renderDisplayInfo() {
 		return "collection-filter-key=" + collectionRole;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
index cc1fca6054..3258d76639 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/DynamicFilterParameterSpecification.java
@@ -1,108 +1,100 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.param;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Collection;
 import java.util.Iterator;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * A specialized ParameterSpecification impl for dealing with a dynamic filter parameters.
  * 
  * @see org.hibernate.Session#enableFilter(String)
  *
  * @author Steve Ebersole
  */
 public class DynamicFilterParameterSpecification implements ParameterSpecification {
 	private final String filterName;
 	private final String parameterName;
 	private final Type definedParameterType;
 
 	/**
 	 * Constructs a parameter specification for a particular filter parameter.
 	 *
 	 * @param filterName The name of the filter
 	 * @param parameterName The name of the parameter
 	 * @param definedParameterType The paremeter type specified on the filter metadata
 	 */
 	public DynamicFilterParameterSpecification(
 			String filterName,
 			String parameterName,
 			Type definedParameterType) {
 		this.filterName = filterName;
 		this.parameterName = parameterName;
 		this.definedParameterType = definedParameterType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int bind(
 			PreparedStatement statement,
 			QueryParameters qp,
 			SessionImplementor session,
 			int start) throws SQLException {
 		final int columnSpan = definedParameterType.getColumnSpan( session.getFactory() );
 		final Object value = session.getLoadQueryInfluencers().getFilterParameterValue( filterName + '.' + parameterName );
 		if ( Collection.class.isInstance( value ) ) {
 			int positions = 0;
 			Iterator itr = ( ( Collection ) value ).iterator();
 			while ( itr.hasNext() ) {
 				definedParameterType.nullSafeSet( statement, itr.next(), start + positions, session );
 				positions += columnSpan;
 			}
 			return positions;
 		}
 		else {
 			definedParameterType.nullSafeSet( statement, value, start, session );
 			return columnSpan;
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getExpectedType() {
 		return definedParameterType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setExpectedType(Type expectedType) {
 		// todo : throw exception?  maybe warn if not the same?
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String renderDisplayInfo() {
 		return "dynamic-filter={filterName=" + filterName + ",paramName=" + parameterName + "}";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
index 90d08fb14f..02efca32b2 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
@@ -1,85 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.param;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
  * Parameter bind specification for an explicit named parameter.
  *
  * @author Steve Ebersole
  */
-public class NamedParameterSpecification extends AbstractExplicitParameterSpecification implements ParameterSpecification {
+public class NamedParameterSpecification extends AbstractExplicitParameterSpecification {
 	private final String name;
 
 	/**
 	 * Constructs a named parameter bind specification.
 	 *
 	 * @param sourceLine See {@link #getSourceLine()}
 	 * @param sourceColumn See {@link #getSourceColumn()} 
 	 * @param name The named parameter name.
 	 */
 	public NamedParameterSpecification(int sourceLine, int sourceColumn, String name) {
 		super( sourceLine, sourceColumn );
 		this.name = name;
 	}
 
 	/**
 	 * Bind the appropriate value into the given statement at the specified position.
 	 *
 	 * @param statement The statement into which the value should be bound.
 	 * @param qp The defined values for the current query execution.
 	 * @param session The session against which the current execution is occuring.
 	 * @param position The position from which to start binding value(s).
 	 *
 	 * @return The number of sql bind positions "eaten" by this bind operation.
 	 */
 	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
 	        throws SQLException {
 		TypedValue typedValue = qp.getNamedParameters().get( name );
 		typedValue.getType().nullSafeSet( statement, typedValue.getValue(), position, session );
 		return typedValue.getType().getColumnSpan( session.getFactory() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String renderDisplayInfo() {
 		return "name=" + name + ", expectedType=" + getExpectedType();
 	}
 
 	/**
 	 * Getter for property 'name'.
 	 *
 	 * @return Value for property 'name'.
 	 */
 	public String getName() {
 		return name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
index f9f314a0e0..24c047b96d 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
@@ -1,86 +1,86 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.param;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Parameter bind specification for an explicit  positional (or ordinal) parameter.
  *
  * @author Steve Ebersole
  */
-public class PositionalParameterSpecification extends AbstractExplicitParameterSpecification implements ParameterSpecification {
+public class PositionalParameterSpecification extends AbstractExplicitParameterSpecification  {
 	private final int hqlPosition;
 
 	/**
 	 * Constructs a position/ordinal parameter bind specification.
 	 *
 	 * @param sourceLine See {@link #getSourceLine()}
 	 * @param sourceColumn See {@link #getSourceColumn()}
 	 * @param hqlPosition The position in the source query, relative to the other source positional parameters.
 	 */
 	public PositionalParameterSpecification(int sourceLine, int sourceColumn, int hqlPosition) {
 		super( sourceLine, sourceColumn );
 		this.hqlPosition = hqlPosition;
 	}
 
 	/**
 	 * Bind the appropriate value into the given statement at the specified position.
 	 *
 	 * @param statement The statement into which the value should be bound.
 	 * @param qp The defined values for the current query execution.
 	 * @param session The session against which the current execution is occuring.
 	 * @param position The position from which to start binding value(s).
 	 *
 	 * @return The number of sql bind positions "eaten" by this bind operation.
 	 */
 	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position) throws SQLException {
 		Type type = qp.getPositionalParameterTypes()[hqlPosition];
 		Object value = qp.getPositionalParameterValues()[hqlPosition];
 
 		type.nullSafeSet( statement, value, position, session );
 		return type.getColumnSpan( session.getFactory() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String renderDisplayInfo() {
 		return "ordinal=" + hqlPosition + ", expectedType=" + getExpectedType();
 	}
 
 	/**
 	 * Getter for property 'hqlPosition'.
 	 *
 	 * @return Value for property 'hqlPosition'.
 	 */
 	public int getHqlPosition() {
 		return hqlPosition;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
index 84e2580d24..2407e23bb0 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/VersionTypeSeedParameterSpecification.java
@@ -1,80 +1,72 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.param;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Parameter bind specification used for optimisitc lock version seeding (from insert statements).
  *
  * @author Steve Ebersole
  */
 public class VersionTypeSeedParameterSpecification implements ParameterSpecification {
-	private VersionType type;
+	private final VersionType type;
 
 	/**
 	 * Constructs a version seed parameter bind specification.
 	 *
 	 * @param type The version type.
 	 */
 	public VersionTypeSeedParameterSpecification(VersionType type) {
 		this.type = type;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
 	        throws SQLException {
 		type.nullSafeSet( statement, type.seed( session ), position, session );
 		return 1;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getExpectedType() {
 		return type;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setExpectedType(Type expectedType) {
 		// expected type is intrinsic here...
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String renderDisplayInfo() {
 		return "version-seed, type=" + type;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
index 71895a88dd..4897bff2ea 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
@@ -1,139 +1,139 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.sql;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Gavin King
  */
 public class ForUpdateFragment {
 	private final StringBuilder aliases = new StringBuilder();
 	private boolean isNowaitEnabled;
 	private boolean isSkipLockedEnabled;
 	private final Dialect dialect;
 	private LockMode lockMode;
 	private LockOptions lockOptions;
 
 	public ForUpdateFragment(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
-	public ForUpdateFragment(Dialect dialect, LockOptions lockOptions, Map keyColumnNames) throws QueryException {
+	public ForUpdateFragment(Dialect dialect, LockOptions lockOptions, Map<String, String[]> keyColumnNames) throws QueryException {
 		this( dialect );
 		LockMode upgradeType = null;
 		Iterator iter = lockOptions.getAliasLockIterator();
 		this.lockOptions =  lockOptions;
 
 		if ( !iter.hasNext()) {  // no tables referenced
 			final LockMode lockMode = lockOptions.getLockMode();
 			if ( LockMode.READ.lessThan( lockMode ) ) {
 				upgradeType = lockMode;
 				this.lockMode = lockMode;
 			}
 		}
 
 		while ( iter.hasNext() ) {
 			final Map.Entry me = ( Map.Entry ) iter.next();
 			final LockMode lockMode = ( LockMode ) me.getValue();
 			if ( LockMode.READ.lessThan( lockMode ) ) {
 				final String tableAlias = ( String ) me.getKey();
 				if ( dialect.forUpdateOfColumns() ) {
-					String[] keyColumns = ( String[] ) keyColumnNames.get( tableAlias ); //use the id column alias
+					String[] keyColumns = keyColumnNames.get( tableAlias ); //use the id column alias
 					if ( keyColumns == null ) {
 						throw new IllegalArgumentException( "alias not found: " + tableAlias );
 					}
 					keyColumns = StringHelper.qualify( tableAlias, keyColumns );
-					for ( int i = 0; i < keyColumns.length; i++ ) {
-						addTableAlias( keyColumns[i] );
+					for ( String keyColumn : keyColumns ) {
+						addTableAlias( keyColumn );
 					}
 				}
 				else {
 					addTableAlias( tableAlias );
 				}
 				if ( upgradeType != null && lockMode != upgradeType ) {
 					throw new QueryException( "mixed LockModes" );
 				}
 				upgradeType = lockMode;
 			}
 		}
 
 		if ( upgradeType == LockMode.UPGRADE_NOWAIT ) {
 			setNowaitEnabled( true );
 		}
 
 		if ( upgradeType == LockMode.UPGRADE_SKIPLOCKED ) {
 			setSkipLockedEnabled( true );
 		}
 	}
 
 	public ForUpdateFragment addTableAlias(String alias) {
 		if ( aliases.length() > 0 ) {
 			aliases.append( ", " );
 		}
 		aliases.append( alias );
 		return this;
 	}
 
 	public String toFragmentString() {
 		if ( lockOptions!= null ) {
 			return dialect.getForUpdateString( aliases.toString(), lockOptions );
 		}
 		else if ( aliases.length() == 0) {
 			if ( lockMode != null ) {
 				return dialect.getForUpdateString( lockMode );
 			}
 			return "";
 		}
 		// TODO:  pass lockmode
 		if(isNowaitEnabled) {
 			return dialect.getForUpdateNowaitString( aliases.toString() );
 		}
 		else if (isSkipLockedEnabled) {
 			return dialect.getForUpdateSkipLockedString( aliases.toString() );
 		}
 		else {
 			return dialect.getForUpdateString( aliases.toString() );
 		}
 	}
 
 	public ForUpdateFragment setNowaitEnabled(boolean nowait) {
 		isNowaitEnabled = nowait;
 		return this;
 	}
 
 	public ForUpdateFragment setSkipLockedEnabled(boolean skipLocked) {
 		isSkipLockedEnabled = skipLocked;
 		return this;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index 3febc377dd..7ae2972361 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,813 +1,814 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
 public class ComponentType extends AbstractType implements CompositeType, ProcedureParameterExtractionAware {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 
 	protected final EntityMode entityMode;
 	protected final ComponentTuplizer componentTuplizer;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[ propertySpan ];
 		this.propertyTypes = new Type[ propertySpan ];
 		this.propertyNullability = new boolean[ propertySpan ];
 		this.cascade = new CascadeStyle[ propertySpan ];
 		this.joinedFetch = new FetchMode[ propertySpan ];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
 		}
 
 		this.entityMode = metamodel.getEntityMode();
 		this.componentTuplizer = metamodel.getComponentTuplizer();
 	}
 
 	public boolean isKey() {
 		return isKey;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public ComponentTuplizer getComponentTuplizer() {
 		return componentTuplizer;
 	}
 	@Override
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		int span = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			span += propertyTypes[i].getColumnSpan( mapping );
 		}
 		return span;
 	}
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		int[] sqlTypes = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int[] subtypes = propertyTypes[i].sqlTypes( mapping );
-			for ( int j = 0; j < subtypes.length; j++ ) {
-				sqlTypes[n++] = subtypes[j];
+			for ( int subtype : subtypes ) {
+				sqlTypes[n++] = subtype;
 			}
 		}
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 
 	@Override
     public final boolean isComponentType() {
 		return true;
 	}
 
 	public Class getReturnedClass() {
 		return componentTuplizer.getMappedClass();
 	}
 
 	@Override
     public boolean isSame(Object x, Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public boolean isEqual(Object x, Object y)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public int compare(Object x, Object y) {
 		if ( x == y ) {
 			return 0;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int propertyCompare = propertyTypes[i].compare( xvalues[i], yvalues[i] );
 			if ( propertyCompare != 0 ) {
 				return propertyCompare;
 			}
 		}
 		return 0;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	@Override
     public int getHashCode(Object x) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public boolean isDirty(Object x, Object y, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			if ( propertyTypes[i].isDirty( xvalues[i], yvalues[i], session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public boolean isDirty(Object x, Object y, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		int loc = 0;
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
 				                      propertyTypes[i].isDirty( xvalues[i], yvalues[i], session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
 				final boolean dirty = propertyTypes[i].isDirty( xvalues[i], yvalues[i], subcheckable, session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
     public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
-			return current != null;
+			return true;
 		}
 		Object[] currentValues = getPropertyValues( current, session );
 		Object[] oldValues = ( Object[] ) old;
 		int loc = 0;
 		for ( int i = 0; i < currentValues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
 			if ( propertyTypes[i].isModified( oldValues[i], currentValues[i], subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
-
+	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
-
+	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 			begin += propertyTypes[i].getColumnSpan( session.getFactory() );
 		}
 	}
-
+	@Override
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int begin,
 			boolean[] settable,
 			SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len == 0 ) {
 				//noop
 			}
 			else if ( len == 1 ) {
 				if ( settable[loc] ) {
 					propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 					begin++;
 				}
 			}
 			else {
 				boolean[] subsettable = new boolean[len];
 				System.arraycopy( settable, loc, subsettable, 0, len );
 				propertyTypes[i].nullSafeSet( st, subvalues[i], begin, subsettable, session );
 				begin += ArrayHelper.countTrue( subsettable );
 			}
 			loc += len;
 		}
 	}
 
 	private Object[] nullSafeGetValues(Object value, EntityMode entityMode) throws HibernateException {
 		if ( value == null ) {
 			return new Object[propertySpan];
 		}
 		else {
 			return getPropertyValues( value, entityMode );
 		}
 	}
-
+	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
-
+	@Override
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValue( component, i, entityMode );
 	}
 
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
 		return componentTuplizer.getPropertyValue( component, i );
 	}
-
+	@Override
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, entityMode );
 	}
-
+	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an 
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return ( Object[] ) component;
 		} else {
 			return componentTuplizer.getPropertyValues( component );
 		}
 	}
-
+	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		componentTuplizer.setPropertyValues( component, values );
 	}
-
+	@Override
 	public Type[] getSubtypes() {
 		return propertyTypes;
 	}
-
+	@Override
 	public String getName() {
 		return "component" + ArrayHelper.toString( propertyNames );
 	}
-
+	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
-		Map result = new HashMap();
+
 		if ( entityMode == null ) {
 			throw new ClassCastException( value.getClass().getName() );
 		}
+		Map<String,String> result = new HashMap<String, String>();
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
 		}
 		return StringHelper.unqualify( getName() ) + result.toString();
 	}
-
+	@Override
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
-
+	@Override
 	public Object deepCopy(Object component, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( component == null ) {
 			return null;
 		}
 
 		Object[] values = getPropertyValues( component, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			values[i] = propertyTypes[i].deepCopy( values[i], factory );
 		}
 
 		Object result = instantiate( entityMode );
 		setPropertyValues( result, values, entityMode );
 
 		//not absolutely necessary, but helps for some
 		//equals()/hashCode() implementations
 		if ( componentTuplizer.hasParentProperty() ) {
 			componentTuplizer.setParent( result, componentTuplizer.getParent( component ), factory );
 		}
 
 		return result;
 	}
-
+	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null
 				? instantiate( owner, session )
 				: target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	@Override
     public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null ?
 				instantiate( owner, session ) :
 				target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache,
 				foreignKeyDirection
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	/**
 	 * This method does not populate the component parent
 	 */
 	public Object instantiate(EntityMode entityMode) throws HibernateException {
 		return componentTuplizer.instantiate();
 	}
 
 	public Object instantiate(Object parent, SessionImplementor session)
 			throws HibernateException {
 
 		Object result = instantiate( entityMode );
 
 		if ( componentTuplizer.hasParentProperty() && parent != null ) {
 			componentTuplizer.setParent(
 					result,
 					session.getPersistenceContext().proxyFor( parent ),
 					session.getFactory()
 			);
 		}
 
 		return result;
 	}
-
+	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return cascade[i];
 	}
-
+	@Override
 	public boolean isMutable() {
 		return true;
 	}
 
 	@Override
     public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			Object[] values = getPropertyValues( value, entityMode );
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				values[i] = propertyTypes[i].disassemble( values[i], session, owner );
 			}
 			return values;
 		}
 	}
 
 	@Override
     public Object assemble(Serializable object, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Object[] values = ( Object[] ) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( ( Serializable ) values[i], session, owner );
 			}
 			Object result = instantiate( owner, session );
 			setPropertyValues( result, assembled, entityMode );
 			return result;
 		}
 	}
-
+	@Override
 	public FetchMode getFetchMode(int i) {
 		return joinedFetch[i];
 	}
 
 	@Override
     public Object hydrate(
 			final ResultSet rs,
 			final String[] names,
 			final SessionImplementor session,
 			final Object owner)
 			throws HibernateException, SQLException {
 
 		int begin = 0;
 		boolean notNull = false;
 		Object[] values = new Object[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int length = propertyTypes[i].getColumnSpan( session.getFactory() );
 			String[] range = ArrayHelper.slice( names, begin, length ); //cache this
 			Object val = propertyTypes[i].hydrate( rs, range, session, owner );
 			if ( val == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = val;
 			begin += length;
 		}
 
 		return notNull ? values : null;
 	}
 
 	@Override
     public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value != null ) {
 			Object result = instantiate( owner, session );
 			Object[] values = ( Object[] ) value;
 			Object[] resolvedValues = new Object[values.length]; //only really need new array during semiresolve!
 			for ( int i = 0; i < values.length; i++ ) {
 				resolvedValues[i] = propertyTypes[i].resolve( values[i], session, owner );
 			}
 			setPropertyValues( result, resolvedValues, entityMode );
 			return result;
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
     public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//note that this implementation is kinda broken
 		//for components with many-to-one associations
 		return resolve( value, session, owner );
 	}
-
+	@Override
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	@Override
     public boolean isXMLElement() {
 		return true;
 	}
-
+	@Override
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
-
+	@Override
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		replaceNode( node, ( Element ) value );
 	}
-
+	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value == null ) {
 			return result;
 		}
 		Object[] values = getPropertyValues( value, EntityMode.POJO ); //TODO!!!!!!!
 		int loc = 0;
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy( propertyNullness, 0, result, loc, propertyNullness.length );
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
-
+	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	public int getPropertyIndex(String name) {
 		String[] names = getPropertyNames();
 		for ( int i = 0, max = names.length; i < max; i++ ) {
 			if ( names[i].equals( name ) ) {
 				return i;
 			}
 		}
 		throw new PropertyNotFoundException(
 				"Unable to locate property named " + name + " on " + getReturnedClass().getName()
 		);
 	}
 
 	private Boolean canDoExtraction;
 
 	@Override
 	public boolean canDoExtraction() {
 		if ( canDoExtraction == null ) {
 			canDoExtraction = determineIfProcedureParamExtractionCanBePerformed();
 		}
 		return canDoExtraction;
 	}
 
 	private boolean determineIfProcedureParamExtractionCanBePerformed() {
 		for ( Type propertyType : propertyTypes ) {
 			if ( ! ProcedureParameterExtractionAware.class.isInstance( propertyType ) ) {
 				return false;
 			}
 			if ( ! ( (ProcedureParameterExtractionAware) propertyType ).canDoExtraction() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
 		Object[] values = new Object[propertySpan];
 
 		int currentIndex = startIndex;
 		boolean notNull = false;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[i];
 			final Object value = propertyType.extract( statement, currentIndex, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = value;
 			currentIndex += propertyType.getColumnSpan( session.getFactory() );
 		}
 
 		if ( ! notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session) throws SQLException {
 		// for this form to work all sub-property spans must be one (1)...
 
 		Object[] values = new Object[propertySpan];
 
 		int indx = 0;
 		boolean notNull = false;
 		for ( String paramName : paramNames ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[indx];
 			final Object value = propertyType.extract( statement, new String[] { paramName }, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[indx] = value;
 		}
 
 		if ( ! notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subselect/CompositeIdTypeBindingTest.java b/hibernate-core/src/test/java/org/hibernate/test/subselect/CompositeIdTypeBindingTest.java
new file mode 100644
index 0000000000..302c12cfcd
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/subselect/CompositeIdTypeBindingTest.java
@@ -0,0 +1,89 @@
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
+package org.hibernate.test.subselect;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import junit.framework.Assert;
+import org.junit.Test;
+
+import org.hibernate.Session;
+import org.hibernate.dialect.H2Dialect;
+import org.hibernate.testing.SkipForDialect;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Strong Liu <stliu@hibernate.org>
+ */
+@SkipForDialect(value = H2Dialect.class, comment = "H2 doesn't support this sql syntax")
+@TestForIssue( jiraKey = "HHH-8312")
+public class CompositeIdTypeBindingTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Employee.class, EmployeeGroup.class };
+	}
+
+	@Test
+	public void testCompositeTypeBinding() {
+		Session session = openSession();
+		session.beginTransaction();
+
+		EmployeeGroup employeegroup = new EmployeeGroup( new EmployeeGroupId( "a", "b" ) );
+		employeegroup.addEmployee( new Employee( "stliu" ) );
+		employeegroup.addEmployee( new Employee( "david" ) );
+		session.save( employeegroup );
+
+
+		employeegroup = new EmployeeGroup( new EmployeeGroupId( "c", "d" ) );
+		employeegroup.addEmployee( new Employee( "gail" ) );
+		employeegroup.addEmployee( new Employee( "steve" ) );
+		session.save( employeegroup );
+
+
+		session.getTransaction().commit();
+
+		session.close();
+
+		session = openSession();
+
+		List<EmployeeGroupId> parameters = new ArrayList<EmployeeGroupId>();
+		parameters.add( new EmployeeGroupId( "a", "b" ) );
+		parameters.add( new EmployeeGroupId( "c", "d" ) );
+		parameters.add( new EmployeeGroupId( "e", "f" ) );
+
+		List result = session.createQuery( "select eg from EmployeeGroup eg where eg.id in (:employeegroupIds)" )
+				.setParameterList( "employeegroupIds", parameters ).list();
+
+		Assert.assertEquals( 2, result.size() );
+
+		employeegroup = (EmployeeGroup) result.get( 0 );
+
+		Assert.assertEquals( "a", employeegroup.getId().getGroupName() );
+		Assert.assertNotNull( employeegroup.getEmployees() );
+		Assert.assertEquals( 2, employeegroup.getEmployees().size() );
+		session.close();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subselect/Employee.java b/hibernate-core/src/test/java/org/hibernate/test/subselect/Employee.java
new file mode 100644
index 0000000000..17852a2f0c
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/subselect/Employee.java
@@ -0,0 +1,53 @@
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
+package org.hibernate.test.subselect;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+
+@Entity
+public class Employee {
+	@Id
+	@GeneratedValue
+	private Long id;
+	private String name;
+
+	public String getName() {
+		return name;
+	}
+
+	@SuppressWarnings("unused")
+	private Employee() {
+	}
+
+	public Employee(String name) {
+		this.name = name;
+	}
+
+	@Override
+	public String toString() {
+		return name;
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subselect/Employeegroup.java b/hibernate-core/src/test/java/org/hibernate/test/subselect/Employeegroup.java
new file mode 100644
index 0000000000..f859eb4ab9
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/subselect/Employeegroup.java
@@ -0,0 +1,70 @@
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
+package org.hibernate.test.subselect;
+
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.OneToMany;
+
+import org.hibernate.annotations.Fetch;
+import org.hibernate.annotations.FetchMode;
+
+@Entity
+public class EmployeeGroup {
+	@Id
+	private EmployeeGroupId id;
+
+	@OneToMany(cascade = CascadeType.ALL)
+	@Fetch(FetchMode.SUBSELECT)
+	private List<Employee> employees = new ArrayList<Employee>();
+
+	public EmployeeGroup(EmployeeGroupId id) {
+		this.id = id;
+	}
+
+	@SuppressWarnings("unused")
+	private EmployeeGroup() {
+	}
+
+	public boolean addEmployee(Employee employee) {
+		return employees.add(employee);
+	}
+
+	public List<Employee> getEmployees() {
+		return employees;
+	}
+
+	public EmployeeGroupId getId() {
+		return id;
+	}
+
+	@Override
+	public String toString() {
+		return id.toString();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/subselect/EmployeegroupId.java b/hibernate-core/src/test/java/org/hibernate/test/subselect/EmployeegroupId.java
new file mode 100644
index 0000000000..2a97e6033f
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/subselect/EmployeegroupId.java
@@ -0,0 +1,58 @@
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
+package org.hibernate.test.subselect;
+
+
+import java.io.Serializable;
+import javax.persistence.Embeddable;
+
+@Embeddable
+public class EmployeeGroupId
+		implements Serializable {
+	private static final long serialVersionUID = 1L;
+	private String groupName;
+	private String departmentName;
+
+	@SuppressWarnings("unused")
+	private EmployeeGroupId() {
+	}
+
+	public EmployeeGroupId(String groupName, String departmentName) {
+		this.groupName = groupName;
+		this.departmentName = departmentName;
+	}
+
+	public String getDepartmentName() {
+		return departmentName;
+	}
+
+	public String getGroupName() {
+		return groupName;
+	}
+
+	@Override
+	public String toString() {
+		return "groupName: " + groupName + ", departmentName: " + departmentName;
+	}
+}
