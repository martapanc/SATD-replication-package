diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 1b8cd205c4..dab857b72c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1059,1356 +1059,1361 @@ public abstract class Dialect implements ConversionContext {
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
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
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
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
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
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
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
 
+	/**
+	 * Does this dialect support columns that are both unique and not null.
+	 * 
+	 * @return True if supported, false otherwise.
+	 */
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
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
 	 * 
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
 	
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
 	 * 
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 */
 	public boolean useFollowOnLocking() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index 971154eb25..cbcbe436ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,894 +1,901 @@
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
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 public class Table implements RelationalModel, Serializable {
 
 	private String name;
 	private String schema;
 	private String catalog;
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map indexes = new LinkedHashMap();
 	private Map foreignKeys = new LinkedHashMap();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private final int uniqueInteger;
 	private boolean quoted;
 	private boolean schemaQuoted;
 	private boolean catalogQuoted;
 	private static int tableCounter = 0;
 	private List checkConstraints = new ArrayList();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables = false;
 	private String comment;
 
 	static class ForeignKeyKey implements Serializable {
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
 	}
 
 	public Table() {
 		uniqueInteger = tableCounter++;
 	}
 
 	public Table(String name) {
 		this();
 		setName( name );
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
 
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * returns quoted name as it would be in the mapping file.
 	 */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return quoted ?
 				dialect.openQuote() + name + dialect.closeQuote() :
 				name;
 	}
 
 	/**
 	 * returns quoted name as it is in the mapping file.
 	 */
 	public String getQuotedSchema() {
 		return schemaQuoted ?
 				"`" + schema + "`" :
 				schema;
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schemaQuoted ?
 				dialect.openQuote() + schema + dialect.closeQuote() :
 				schema;
 	}
 
 	public String getQuotedCatalog() {
 		return catalogQuoted ?
 				"`" + catalog + "`" :
 				catalog;
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalogQuoted ?
 				dialect.openQuote() + catalog + dialect.closeQuote() :
 				catalog;
 	}
 
 	public void setName(String name) {
 		if ( name.charAt( 0 ) == '`' ) {
 			quoted = true;
 			this.name = name.substring( 1, name.length() - 1 );
 		}
 		else {
 			this.name = name;
 		}
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
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = (Column) getColumn( column );
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
 
 	public Iterator getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Iterator getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse = 0;
 
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
 		result = prime * result
 			+ ((catalog == null) ? 0 : isCatalogQuoted() ? catalog.hashCode() : catalog.toLowerCase().hashCode());
 		result = prime * result + ((name == null) ? 0 : isQuoted() ? name.hashCode() : name.toLowerCase().hashCode());
 		result = prime * result
 			+ ((schema == null) ? 0 : isSchemaQuoted() ? schema.hashCode() : schema.toLowerCase().hashCode());
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
 
 		return isQuoted() ? name.equals(table.getName()) : name.equalsIgnoreCase(table.getName())
 			&& ((schema == null && table.getSchema() != null) ? false : (schema == null) ? true : isSchemaQuoted() ? schema.equals(table.getSchema()) : schema.equalsIgnoreCase(table.getSchema()))
 			&& ((catalog == null && table.getCatalog() != null) ? false : (catalog == null) ? true : isCatalogQuoted() ? catalog.equals(table.getCatalog()) : catalog.equalsIgnoreCase(table.getCatalog()));
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
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase()
 						.startsWith( columnInfo.getTypeName().toLowerCase() )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase() +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
 									String defaultSchema)
 			throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		while ( iter.hasNext() ) {
 			Column column = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( column.getName() );
 
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
 
-				boolean useUniqueConstraint = column.isUnique() &&
-						dialect.supportsUnique() &&
-						( column.isNullable() || dialect.supportsNotNullUnique() );
-				if ( useUniqueConstraint ) {
+				// If the column is 1.) unique, 2.) the dialect supports the
+				// unique syntax, 3.) the column is nullable or supports
+				// "not null unique", and 4.) a constraint will not be created
+				if ( column.isUnique() && dialect.supportsUnique()
+						&& ( column.isNullable()
+								|| dialect.supportsNotNullUnique() )
+						&& !dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 					alter.append( " unique" );
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
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlTemporaryTableCreateString(Dialect dialect, Mapping mapping) throws HibernateException {
 		StringBuilder buffer = new StringBuilder( dialect.getCreateTemporaryTableString() )
 				.append( ' ' )
 				.append( name )
 				.append( " (" );
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Column column = (Column) itr.next();
 			buffer.append( column.getQuotedName( dialect ) ).append( ' ' );
 			buffer.append( column.getSqlType( dialect, mapping ) );
 			if ( column.isNullable() ) {
 				buffer.append( dialect.getNullColumnString() );
 			}
 			else {
 				buffer.append( " not null" );
 			}
 			if ( itr.hasNext() ) {
 				buffer.append( ", " );
 			}
 		}
 		buffer.append( ") " );
 		buffer.append( dialect.getCreateTemporaryTablePostfix() );
 		return buffer.toString();
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
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( p ) ) );
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
-
-			boolean useUniqueConstraint = col.isUnique() &&
-					( col.isNullable() || dialect.supportsNotNullUnique() );
-			if ( useUniqueConstraint ) {
-				if ( dialect.supportsUnique() ) {
-					buf.append( " unique" );
-				}
-				else {
+			
+			// If the column is 1.) unique and nullable or 2.) unique,
+			// not null, and the dialect supports unique not null			
+			if ( col.isUnique()
+					&& ( col.isNullable()
+							|| dialect.supportsNotNullUnique() ) ) {
+				if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
+					// If the constraint is supported, do not add to the column syntax.
 					UniqueKey uk = getOrCreateUniqueKey( col.getQuotedName( dialect ) + '_' );
 					uk.addColumn( col );
 				}
+				else if ( dialect.supportsUnique() ) {
+					// Otherwise, add to the column syntax if supported.
+					buf.append( " unique" );
+				}
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
 
 		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 			Iterator ukiter = getUniqueKeyIterator();
 			while ( ukiter.hasNext() ) {
 				UniqueKey uk = (UniqueKey) ukiter.next();
 				String constraint = uk.sqlConstraintString( dialect );
 				if ( constraint != null ) {
 					buf.append( ", " ).append( constraint );
 				}
 			}
 		}
 		/*Iterator idxiter = getIndexIterator();
 		while ( idxiter.hasNext() ) {
 			Index idx = (Index) idxiter.next();
 			buf.append(',').append( idx.sqlConstraintString(dialect) );
 		}*/
 
 		if ( dialect.supportsTableCheck() ) {
 			Iterator chiter = checkConstraints.iterator();
 			while ( chiter.hasNext() ) {
 				buf.append( ", check (" )
 						.append( chiter.next() )
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
 		StringBuilder buf = new StringBuilder( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index = (Index) indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return (Index) indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current = (Index) indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = (UniqueKey) uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = "UK" + uniqueColumnString( keyColumns.iterator() );
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return (UniqueKey) uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = (UniqueKey) uniqueKeys.get( keyName );
 
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
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName,
 									   List referencedColumns) {
 		Object key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = (ForeignKey) foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			if ( keyName != null ) {
 				fk.setName( keyName );
 			}
 			else {
 				fk.setName( "FK" + uniqueColumnString( keyColumns.iterator(), referencedEntityName ) );
 				//TODO: add referencedClass to disambiguate to FKs on the same
 				//      columns, pointing to different tables
 			}
 			fk.setTable( this );
 			foreignKeys.put( key, fk );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 	public String uniqueColumnString(Iterator iterator) {
 		return uniqueColumnString( iterator, null );
 	}
 
 	public String uniqueColumnString(Iterator iterator, String referencedEntityName) {
 		int result = 0;
 		if ( referencedEntityName != null ) {
 			result += referencedEntityName.hashCode();
 		}
 		while ( iterator.hasNext() ) {
 			result += iterator.next().hashCode();
 		}
 		return ( Integer.toHexString( name.hashCode() ) + Integer.toHexString( result ) ).toUpperCase();
 	}
 
 
 	public String getSchema() {
 		return schema;
 	}
 
 	public void setSchema(String schema) {
 		if ( schema != null && schema.charAt( 0 ) == '`' ) {
 			schemaQuoted = true;
 			this.schema = schema.substring( 1, schema.length() - 1 );
 		}
 		else {
 			this.schema = schema;
 		}
 	}
 
 	public String getCatalog() {
 		return catalog;
 	}
 
 	public void setCatalog(String catalog) {
 		if ( catalog != null && catalog.charAt( 0 ) == '`' ) {
 			catalogQuoted = true;
 			this.catalog = catalog.substring( 1, catalog.length() - 1 );
 		}
 		else {
 			this.catalog = catalog;
 		}
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
 
 	public boolean isSchemaQuoted() {
 		return schemaQuoted;
 	}
 	public boolean isCatalogQuoted() {
 		return catalogQuoted;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public void setQuoted(boolean quoted) {
 		this.quoted = quoted;
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
 			buf.append( getCatalog() + "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() + "." );
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
 
 	public Iterator getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				StringBuilder buf = new StringBuilder()
 						.append( "comment on table " )
 						.append( tableName )
 						.append( " is '" )
 						.append( comment )
 						.append( "'" );
 				comments.add( buf.toString() );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					StringBuilder buf = new StringBuilder()
 							.append( "comment on column " )
 							.append( tableName )
 							.append( '.' )
 							.append( column.getQuotedName( dialect ) )
 							.append( " is '" )
 							.append( columnComment )
 							.append( "'" );
 					comments.add( buf.toString() );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
index f703f88a4c..36cd559c6b 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
@@ -1,110 +1,110 @@
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
 package org.hibernate.mapping;
 import java.util.Iterator;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 
 /**
  * A relational unique key constraint
  *
  * @author Gavin King
  */
 public class UniqueKey extends Constraint {
 
 	public String sqlConstraintString(Dialect dialect) {
+		// TODO: This may not be necessary, but not all callers currently
+		// check it on their own.  Go through their logic.
+		if ( !isGenerated( dialect ) ) return null;
+		
 		StringBuilder buf = new StringBuilder( "unique (" );
-		boolean hadNullableColumn = false;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column column = (Column) iter.next();
-			if ( !hadNullableColumn && column.isNullable() ) {
-				hadNullableColumn = true;
-			}
 			buf.append( column.getQuotedName( dialect ) );
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 		}
-		//do not add unique constraint on DB not supporting unique and nullable columns
-		return !hadNullableColumn || dialect.supportsNotNullUnique() ?
-				buf.append( ')' ).toString() :
-				null;
+		
+		return buf.append( ')' ).toString();
 	}
 
 	@Override
     public String sqlConstraintString(
 			Dialect dialect,
 			String constraintName,
 			String defaultCatalog,
 			String defaultSchema) {
+		// TODO: This may not be necessary, but not all callers currently
+		// check it on their own.  Go through their logic.
+		if ( !isGenerated( dialect ) ) return null;
+		
 		StringBuilder buf = new StringBuilder(
-				dialect.getAddUniqueConstraintString( constraintName )
-		).append( '(' );
+		dialect.getAddUniqueConstraintString( constraintName ) ).append( '(' );
 		Iterator iter = getColumnIterator();
-		boolean nullable = false;
 		while ( iter.hasNext() ) {
 			Column column = (Column) iter.next();
-			if ( !nullable && column.isNullable() ) nullable = true;
 			buf.append( column.getQuotedName( dialect ) );
 			if ( iter.hasNext() ) buf.append( ", " );
 		}
-		return !nullable || dialect.supportsNotNullUnique() ? buf.append( ')' ).toString() : null;
+		return buf.append( ')' ).toString();
 	}
 
 	@Override
     public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 			return super.sqlCreateString( dialect, p, defaultCatalog, defaultSchema );
 		}
 		else {
 			return Index.buildSqlCreateIndexString( dialect, getName(), getTable(), getColumnIterator(), true,
 					defaultCatalog, defaultSchema );
 		}
 	}
 
 	@Override
     public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 			return super.sqlDropString( dialect, defaultCatalog, defaultSchema );
 		}
 		else {
 			return Index.buildSqlDropIndexString( dialect, getTable(), getName(), defaultCatalog, defaultSchema );
 		}
 	}
 
 	@Override
     public boolean isGenerated(Dialect dialect) {
+		if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) return false;
 		if ( dialect.supportsNotNullUnique() ) return true;
+		
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
-			if ( ( (Column) iter.next() ).isNullable() ) {
-				return false;
-			}
+			// Dialect does not support "not null unique" and this column is not null.
+			if ( ! ( (Column) iter.next() ).isNullable() ) return false;
 		}
 		return true;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
index 4d7562a776..72ce0df0f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Table.java
@@ -1,298 +1,302 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.metamodel.relational;
 
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Models the concept of a relational <tt>TABLE</tt> (or <tt>VIEW</tt>).
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class Table extends AbstractTableSpecification implements Exportable {
 	private final Schema database;
 	private final Identifier tableName;
 	private final ObjectName objectName;
 	private final String qualifiedName;
 
 	private final LinkedHashMap<String,Index> indexes = new LinkedHashMap<String,Index>();
 	private final LinkedHashMap<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private final List<CheckConstraint> checkConstraints = new ArrayList<CheckConstraint>();
 	private final List<String> comments = new ArrayList<String>();
 
 	public Table(Schema database, String tableName) {
 		this( database, Identifier.toIdentifier( tableName ) );
 	}
 
 	public Table(Schema database, Identifier tableName) {
 		this.database = database;
 		this.tableName = tableName;
 		objectName = new ObjectName( database.getName().getSchema(), database.getName().getCatalog(), tableName );
 		this.qualifiedName = objectName.toText();
 	}
 
 	@Override
 	public Schema getSchema() {
 		return database;
 	}
 
 	public Identifier getTableName() {
 		return tableName;
 	}
 
 	@Override
 	public String getLoggableValueQualifier() {
 		return qualifiedName;
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return qualifiedName;
 	}
 
 	@Override
 	public String toLoggableString() {
 		return qualifiedName;
 	}
 
 	@Override
 	public Iterable<Index> getIndexes() {
 		return indexes.values();
 	}
 
 	public Index getOrCreateIndex(String name) {
 		if( indexes.containsKey( name ) ){
 			return indexes.get( name );
 		}
 		Index index = new Index( this, name );
 		indexes.put(name, index );
 		return index;
 	}
 
 	@Override
 	public Iterable<UniqueKey> getUniqueKeys() {
 		return uniqueKeys.values();
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String name) {
 		if( uniqueKeys.containsKey( name ) ){
 			return uniqueKeys.get( name );
 		}
 		UniqueKey uniqueKey = new UniqueKey( this, name );
 		uniqueKeys.put(name, uniqueKey );
 		return uniqueKey;
 	}
 
 	@Override
 	public Iterable<CheckConstraint> getCheckConstraints() {
 		return checkConstraints;
 	}
 
 	@Override
 	public void addCheckConstraint(String checkCondition) {
         //todo ? StringHelper.isEmpty( checkCondition );
         //todo default name?
 		checkConstraints.add( new CheckConstraint( this, "", checkCondition ) );
 	}
 
 	@Override
 	public Iterable<String> getComments() {
 		return comments;
 	}
 
 	@Override
 	public void addComment(String comment) {
 		comments.add( comment );
 	}
 
 	@Override
 	public String getQualifiedName(Dialect dialect) {
 		return objectName.toText( dialect );
 	}
 
 	public String[] sqlCreateStrings(Dialect dialect) {
 		boolean hasPrimaryKey = getPrimaryKey().getColumns().iterator().hasNext();
 		StringBuilder buf =
 				new StringBuilder(
 						hasPrimaryKey ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( objectName.toText( dialect ) )
 				.append( " (" );
 
 
 		// TODO: fix this when identity columns are supported by new metadata (HHH-6436)
 		// for now, assume false
 		//boolean identityColumn = idValue != null && idValue.isIdentityColumn( metadata.getIdentifierGeneratorFactory(), dialect );
 		boolean isPrimaryKeyIdentity = false;
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkColName = null;
 		if ( hasPrimaryKey && isPrimaryKeyIdentity ) {
 			Column pkColumn = getPrimaryKey().getColumns().iterator().next();
 			pkColName = pkColumn.getColumnName().encloseInQuotesIfQuoted( dialect );
 		}
 
 		boolean isFirst = true;
 		for ( SimpleValue simpleValue : values() ) {
 			if ( ! Column.class.isInstance( simpleValue ) ) {
 				continue;
 			}
 			if ( isFirst ) {
 				isFirst = false;
 			}
 			else {
 				buf.append( ", " );
 			}
 			Column col = ( Column ) simpleValue;
 			String colName = col.getColumnName().encloseInQuotesIfQuoted( dialect );
 
 			buf.append( colName ).append( ' ' );
 
 			if ( isPrimaryKeyIdentity && colName.equals( pkColName ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( getTypeString( col, dialect ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getDatatype().getTypeCode() ) );
 			}
 			else {
 				buf.append( getTypeString( col, dialect ) );
 
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
 
-			boolean useUniqueConstraint = col.isUnique() &&
-					( col.isNullable() || dialect.supportsNotNullUnique() );
-			if ( useUniqueConstraint ) {
-				if ( dialect.supportsUnique() ) {
-					buf.append( " unique" );
-				}
-				else {
+			// If the column is 1.) unique and nullable or 2.) unique,
+			// not null, and the dialect supports unique not null			
+			if ( col.isUnique()
+					&& ( col.isNullable()
+							|| dialect.supportsNotNullUnique() ) ) {
+				if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
+					// If the constraint is supported, do not add to the column syntax.
 					UniqueKey uk = getOrCreateUniqueKey( col.getColumnName().encloseInQuotesIfQuoted( dialect ) + '_' );
 					uk.addColumn( col );
 				}
+				else if ( dialect.supportsUnique() ) {
+					// Otherwise, add to the column syntax if supported.
+					buf.append( " unique" );
+				}
 			}
 
 			if ( col.getCheckCondition() != null && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckCondition() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 		}
 		if ( hasPrimaryKey ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintStringInCreateTable( dialect ) );
 		}
 
 		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 			for ( UniqueKey uk : uniqueKeys.values() ) {
 				String constraint = uk.sqlConstraintStringInCreateTable( dialect );
 				if ( constraint != null ) {
 					buf.append( ", " ).append( constraint );
 				}
 			}
 		}
 
 		if ( dialect.supportsTableCheck() ) {
 			for ( CheckConstraint checkConstraint : checkConstraints ) {
 				buf.append( ", check (" )
 						.append( checkConstraint )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 		buf.append( dialect.getTableTypeString() );
 
 		String[] sqlStrings = new String[ comments.size() + 1 ];
 		sqlStrings[ 0 ] = buf.toString();
 
 		for ( int i = 0 ; i < comments.size(); i++ ) {
 			sqlStrings[ i + 1 ] = dialect.getTableComment( comments.get( i ) );
 		}
 
 		return sqlStrings;
 	}
 
 	private static String getTypeString(Column col, Dialect dialect) {
 		String typeString = null;
 		if ( col.getSqlType() != null ) {
 			typeString = col.getSqlType();
 		}
 		else {
 			Size size = col.getSize() == null ?
 					new Size( ) :
 					col.getSize();
 
 			typeString = dialect.getTypeName(
 						col.getDatatype().getTypeCode(),
 						size.getLength(),
 						size.getPrecision(),
 						size.getScale()
 			);
 		}
 		return typeString;
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) {
 		StringBuilder buf = new StringBuilder( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( getQualifiedName( dialect ) )
 				.append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return new String[] { buf.toString() };
 	}
 
 	@Override
 	public String toString() {
 		return "Table{name=" + qualifiedName + '}';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
index 4fbffeba1e..1c5413e473 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
@@ -1,106 +1,100 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.metamodel.relational;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Models a SQL <tt>INDEX</tt> defined as UNIQUE
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class UniqueKey extends AbstractConstraint implements Constraint {
 	protected UniqueKey(Table table, String name) {
 		super( table, name );
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		StringBuilder sb = new StringBuilder( getTable().getLoggableValueQualifier() );
 		sb.append( ".UK" );
 		for ( Column column : getColumns() ) {
 			sb.append( '_' ).append( column.getColumnName().getName() );
 		}
 		return sb.toString();
 	}
 
 	@Override
     public boolean isCreationVetoed(Dialect dialect) {
-		if ( dialect.supportsNotNullUnique() ) {
-			return false;
-		}
-
+		if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) return true;
+		if ( dialect.supportsNotNullUnique() ) return false;
+		
 		for ( Column column : getColumns() ) {
-			if ( column.isNullable() ) {
-				return true;
-			}
+			// Dialect does not support "not null unique" and this column is not null.
+			if ( ! column.isNullable() ) return true;
 		}
 		return false;
 	}
 
 	public String sqlConstraintStringInCreateTable(Dialect dialect) {
+		// TODO: This may not be necessary, but not all callers currently
+		// check it on their own.  Go through their logic.
+		if ( isCreationVetoed( dialect ) ) return null;
+				
 		StringBuilder buf = new StringBuilder( "unique (" );
-		boolean hadNullableColumn = false;
 		boolean first = true;
 		for ( Column column : getColumns() ) {
 			if ( first ) {
 				first = false;
 			}
 			else {
 				buf.append( ", " );
 			}
-			if ( !hadNullableColumn && column.isNullable() ) {
-				hadNullableColumn = true;
-			}
 			buf.append( column.getColumnName().encloseInQuotesIfQuoted( dialect ) );
 		}
-		//do not add unique constraint on DB not supporting unique and nullable columns
-		return !hadNullableColumn || dialect.supportsNotNullUnique() ?
-				buf.append( ')' ).toString() :
-				null;
+		return buf.append( ')' ).toString();
 	}
 
 	@Override
     public String sqlConstraintStringInAlterTable(Dialect dialect) {
+		// TODO: This may not be necessary, but not all callers currently
+		// check it on their own.  Go through their logic.
+		if ( isCreationVetoed( dialect ) ) return null;
+				
 		StringBuilder buf = new StringBuilder(
-				dialect.getAddUniqueConstraintString( getName() )
-		).append( '(' );
-		boolean nullable = false;
+		dialect.getAddUniqueConstraintString( getName() ) ).append( '(' );
 		boolean first = true;
 		for ( Column column : getColumns() ) {
 			if ( first ) {
 				first = false;
 			}
 			else {
 				buf.append( ", " );
 			}
-			if ( !nullable && column.isNullable() ) {
-				nullable = true;
-			}
 			buf.append( column.getColumnName().encloseInQuotesIfQuoted( dialect ) );
 		}
-		return !nullable || dialect.supportsNotNullUnique() ? buf.append( ')' ).toString() : null;
+		return buf.append( ')' ).toString();
 	}
 }
