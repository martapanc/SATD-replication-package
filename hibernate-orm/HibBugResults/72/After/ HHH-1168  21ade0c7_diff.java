diff --git a/hibernate-core/src/main/java/org/hibernate/LockOptions.java b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
index a86b8b4f91..016a8ced22 100644
--- a/hibernate-core/src/main/java/org/hibernate/LockOptions.java
+++ b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
@@ -1,263 +1,296 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 /**
  * Contains locking details (LockMode, Timeout and Scope).
  * 
  * @author Scott Marlow
  */
 public class LockOptions implements Serializable {
 	/**
 	 * NONE represents LockMode.NONE (timeout + scope do not apply)
 	 */
 	public static final LockOptions NONE = new LockOptions(LockMode.NONE);
 
 	/**
 	 * READ represents LockMode.READ (timeout + scope do not apply)
 	 */
 	public static final LockOptions READ = new LockOptions(LockMode.READ);
 
 	/**
 	 * UPGRADE represents LockMode.UPGRADE (will wait forever for lock and
 	 * scope of false meaning only entity is locked)
 	 */
+	@SuppressWarnings("deprecation")
 	public static final LockOptions UPGRADE = new LockOptions(LockMode.UPGRADE);
 
 	/**
 	 * Indicates that the database should not wait at all to acquire the pessimistic lock.
 	 * @see #getTimeOut
 	 */
 	public static final int NO_WAIT = 0;
 
 	/**
 	 * Indicates that there is no timeout for the acquisition.
 	 * @see #getTimeOut
 	 */
 	public static final int WAIT_FOREVER = -1;
 
 	private LockMode lockMode = LockMode.NONE;
 	private int timeout = WAIT_FOREVER;
-	private Map aliasSpecificLockModes = null; //initialize lazily as LockOptions is frequently created without needing this
+
+	//initialize lazily as LockOptions is frequently created without needing this
+	private Map<String,LockMode> aliasSpecificLockModes = null;
 
 	public LockOptions() {
 	}
 
 	public LockOptions( LockMode lockMode) {
 		this.lockMode = lockMode;
 	}
 
 
 	/**
 	 * Retrieve the overall lock mode in effect for this set of options.
 	 * <p/>
 	 * In certain contexts (hql and criteria), lock-modes can be defined in an
 	 * even more granular {@link #setAliasSpecificLockMode(String, LockMode) per-alias} fashion
 	 *
 	 * @return The overall lock mode.
 	 */
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	/**
 	 * Set the overall {@link LockMode} to be used.  The default is
 	 * {@link LockMode#NONE}
 	 *
 	 * @param lockMode The new overall lock mode to use.
 	 *
 	 * @return this (for method chaining).
 	 */
 	public LockOptions setLockMode(LockMode lockMode) {
 		this.lockMode = lockMode;
 		return this;
 	}
 
 
 	/**
 	 * Specify the {@link LockMode} to be used for a specific query alias.
 	 *
 	 * @param alias used to reference the LockMode.
 	 * @param lockMode The lock mode to apply to the given alias
 	 * @return this LockRequest instance for operation chaining.
 	 *
 	 * @see Query#setLockMode(String, LockMode)
 	 * @see Criteria#setLockMode(LockMode)
 	 * @see Criteria#setLockMode(String, LockMode)
 	 */
 	public LockOptions setAliasSpecificLockMode(String alias, LockMode lockMode) {
 		if ( aliasSpecificLockModes == null ) {
-			aliasSpecificLockModes = new HashMap();
+			aliasSpecificLockModes = new HashMap<String,LockMode>();
 		}
 		aliasSpecificLockModes.put( alias, lockMode );
 		return this;
 	}
 
 	/**
 	 * Get the {@link LockMode} explicitly specified for the given alias via
 	 * {@link #setAliasSpecificLockMode}
 	 * <p/>
 	 * Differs from {@link #getEffectiveLockMode} in that here we only return
 	 * explicitly specified alias-specific lock modes.
 	 *
 	 * @param alias The alias for which to locate the explicit lock mode.
 	 *
 	 * @return The explicit lock mode for that alias.
 	 */
 	public LockMode getAliasSpecificLockMode(String alias) {
 		if ( aliasSpecificLockModes == null ) {
 			return null;
 		}
-		return (LockMode) aliasSpecificLockModes.get( alias );
+		return aliasSpecificLockModes.get( alias );
 	}
 
 	/**
 	 * Determine the {@link LockMode} to apply to the given alias.  If no
 	 * mode was explicitly {@link #setAliasSpecificLockMode set}, the
 	 * {@link #getLockMode overall mode} is returned.  If the overall lock mode is
 	 * <tt>null</tt> as well, {@link LockMode#NONE} is returned.
 	 * <p/>
 	 * Differs from {@link #getAliasSpecificLockMode} in that here we fallback to we only return
 	 * the overall lock mode.
 	 *
 	 * @param alias The alias for which to locate the effective lock mode.
 	 *
 	 * @return The effective lock mode.
 	 */
 	public LockMode getEffectiveLockMode(String alias) {
 		LockMode lockMode = getAliasSpecificLockMode( alias );
 		if ( lockMode == null ) {
 			lockMode = this.lockMode;
 		}
 		return lockMode == null ? LockMode.NONE : lockMode;
 	}
 
+	public boolean hasAliasSpecificLockModes() {
+		return aliasSpecificLockModes != null
+				&& ! aliasSpecificLockModes.isEmpty();
+	}
+
 	/**
 	 * Get the number of aliases that have specific lock modes defined.
 	 *
 	 * @return the number of explicitly defined alias lock modes.
 	 */
 	public int getAliasLockCount() {
 		if ( aliasSpecificLockModes == null ) {
 			return 0;
 		}
 		return aliasSpecificLockModes.size();
 	}
 
 	/**
 	 * Iterator for accessing Alias (key) and LockMode (value) as Map.Entry
 	 *
 	 * @return Iterator for accessing the Map.Entry's
 	 */
 	public Iterator getAliasLockIterator() {
 		if ( aliasSpecificLockModes == null ) {
 			return Collections.emptyList().iterator();
 		}
 		return aliasSpecificLockModes.entrySet().iterator();
 	}
 
 	/**
+	 * Currently needed for follow-on locking
+	 *
+	 * @return The greatest of all requested lock modes.
+	 */
+	public LockMode findGreatestLockMode() {
+		LockMode lockModeToUse = getLockMode();
+		if ( lockModeToUse == null ) {
+			lockModeToUse = LockMode.NONE;
+		}
+
+		if ( aliasSpecificLockModes == null ) {
+			return lockModeToUse;
+		}
+
+		for ( LockMode lockMode : aliasSpecificLockModes.values() ) {
+			if ( lockMode.greaterThan( lockModeToUse ) ) {
+				lockModeToUse = lockMode;
+			}
+		}
+
+		return lockModeToUse;
+	}
+
+	/**
 	 * Retrieve the current timeout setting.
 	 * <p/>
 	 * The timeout is the amount of time, in milliseconds, we should instruct the database
 	 * to wait for any requested pessimistic lock acquisition.
 	 * <p/>
 	 * {@link #NO_WAIT} and {@link #WAIT_FOREVER} represent 2 "magic" values.
 	 *
 	 * @return timeout in milliseconds, or {@link #NO_WAIT} or {@link #WAIT_FOREVER}
 	 */
 	public int getTimeOut() {
 		return timeout;
 	}
 
 	/**
 	 * Set the timeout setting.
 	 * <p/>
 	 * See {@link #getTimeOut} for a discussion of meaning.
 	 *
 	 * @param timeout The new timeout setting.
 	 *
 	 * @return this (for method chaining).
 	 *
 	 * @see #getTimeOut
 	 */
 	public LockOptions setTimeOut(int timeout) {
 		this.timeout = timeout;
 		return this;
 	}
 
 	private boolean scope=false;
 
 	/**
 	 * Retrieve the current lock scope setting.
 	 * <p/>
 	 * "scope" is a JPA defined term.  It is basically a cascading of the lock to associations.
 	 *
 	 * @return true if locking will be extended to owned associations
 	 */
 	public boolean getScope() {
 		return scope;
 	}
 
 	/**
 	 * Set the cope.
 	 *
 	 * @param scope The new scope setting
 	 *
 	 * @return this (for method chaining).
 	 */
 	public LockOptions setScope(boolean scope) {
 		this.scope = scope;
 		return this;
 	}
 
 	public LockOptions makeCopy() {
 		final LockOptions copy = new LockOptions();
 		copy( this, copy );
 		return copy;
 	}
 
 	/**
-	 * Shallow copy From to Dest
+	 * Perform a shallow copy
+	 *
+	 * @param source Source for the copy (copied from)
+	 * @param destination Destination for the copy (copied to)
 	 *
-	 * @param from is copied from
-	 * @param dest is copied to
-	 * @return dest
+	 * @return destination
 	 */
-	public static LockOptions copy(LockOptions from, LockOptions dest) {
-		dest.setLockMode(from.getLockMode());
-		dest.setScope(from.getScope());
-		dest.setTimeOut(from.getTimeOut());
-		if ( from.aliasSpecificLockModes != null ) {
-			dest.aliasSpecificLockModes = new HashMap( from.aliasSpecificLockModes );
+	public static LockOptions copy(LockOptions source, LockOptions destination) {
+		destination.setLockMode( source.getLockMode() );
+		destination.setScope( source.getScope() );
+		destination.setTimeOut( source.getTimeOut() );
+		if ( source.aliasSpecificLockModes != null ) {
+			destination.aliasSpecificLockModes = new HashMap<String,LockMode>( source.aliasSpecificLockModes );
 		}
-		return dest;
+		return destination;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index ce2aa4653f..1b8cd205c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1403,1004 +1403,1012 @@ public abstract class Dialect implements ConversionContext {
 
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
 
+	/**
+	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
+	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
+	 * by subsequent selects.
+	 *
+	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
+	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
+	 */
 	public boolean useFollowOnLocking() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
index 20fce8b934..f4e10225c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
@@ -1,1594 +1,1604 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.internal;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.util.Hashtable;
 import java.util.Properties;
 import java.util.Set;
 import javax.naming.NameNotFoundException;
 import javax.naming.NamingException;
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 
 import org.jboss.logging.BasicLogger;
 import org.jboss.logging.Cause;
 import org.jboss.logging.LogMessage;
 import org.jboss.logging.Message;
 import org.jboss.logging.MessageLogger;
 
 import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
 import org.hibernate.cache.CacheException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.loading.internal.CollectionLoadContext;
 import org.hibernate.engine.loading.internal.EntityLoadContext;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.engine.jdbc.dialect.internal.AbstractDialectResolver;
 import org.hibernate.engine.jndi.JndiException;
 import org.hibernate.engine.jndi.JndiNameException;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 
 import static org.jboss.logging.Logger.Level.DEBUG;
 import static org.jboss.logging.Logger.Level.ERROR;
 import static org.jboss.logging.Logger.Level.INFO;
 import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * The jboss-logging {@link MessageLogger} for the hibernate-core module.  It reserves message ids ranging from
  * 00001 to 10000 inclusively.
  * <p/>
  * New messages must be added after the last message defined to ensure message codes are unique.
  */
 @MessageLogger(projectCode = "HHH")
 public interface CoreMessageLogger extends BasicLogger {
 
 	@LogMessage(level = WARN)
 	@Message(value = "Already session bound on call to bind(); make sure you clean up your sessions!", id = 2)
 	void alreadySessionBound();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Autocommit mode: %s", id = 6)
 	void autoCommitMode(boolean autocommit);
 
 	@LogMessage(level = WARN)
 	@Message(value = "JTASessionContext being used with JDBCTransactionFactory; auto-flush will not operate correctly with getCurrentSession()",
 			id = 8)
 	void autoFlushWillNotWork();
 
 	@LogMessage(level = INFO)
 	@Message(value = "On release of batch it still contained JDBC statements", id = 10)
 	void batchContainedStatementsOnRelease();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Bytecode provider name : %s", id = 21)
 	void bytecodeProvider(String provider);
 
 	@LogMessage(level = WARN)
 	@Message(value = "c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.",
 			id = 22)
 	void c3p0ProviderClassNotFound(String c3p0ProviderClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "I/O reported cached file could not be found : %s : %s", id = 23)
 	void cachedFileNotFound(String path,
 							FileNotFoundException error);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Cache provider: %s", id = 24)
 	void cacheProvider(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Calling joinTransaction() on a non JTA EntityManager", id = 27)
 	void callingJoinTransactionOnNonJtaEntityManager();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Cleaning up connection pool [%s]", id = 30)
 	void cleaningUpConnectionPool(String url);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Closing", id = 31)
 	void closing();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections fetched (minimize this): %s", id = 32)
 	void collectionsFetched(long collectionFetchCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections loaded: %s", id = 33)
 	void collectionsLoaded(long collectionLoadCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections recreated: %s", id = 34)
 	void collectionsRecreated(long collectionRecreateCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections removed: %s", id = 35)
 	void collectionsRemoved(long collectionRemoveCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections updated: %s", id = 36)
 	void collectionsUpdated(long collectionUpdateCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Columns: %s", id = 37)
 	void columns(Set keySet);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Composite-id class does not override equals(): %s", id = 38)
 	void compositeIdClassDoesNotOverrideEquals(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Composite-id class does not override hashCode(): %s", id = 39)
 	void compositeIdClassDoesNotOverrideHashCode(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuration resource: %s", id = 40)
 	void configurationResource(String resource);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configured SessionFactory: %s", id = 41)
 	void configuredSessionFactory(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from file: %s", id = 42)
 	void configuringFromFile(String file);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from resource: %s", id = 43)
 	void configuringFromResource(String resource);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from URL: %s", id = 44)
 	void configuringFromUrl(URL url);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from XML document", id = 45)
 	void configuringFromXmlDocument();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Connection properties: %s", id = 46)
 	void connectionProperties(Properties connectionProps);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Connections obtained: %s", id = 48)
 	void connectionsObtained(long connectCount);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Container is providing a null PersistenceUnitRootUrl: discovery impossible", id = 50)
 	void containerProvidingNullPersistenceUnitRootUrl();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ignoring bag join fetch [%s] due to prior collection join fetch", id = 51)
 	void containsJoinFetchedCollection(String role);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Creating subcontext: %s", id = 53)
 	void creatingSubcontextInfo(String intermediateContextName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Defining %s=true ignored in HEM", id = 59)
 	void definingFlushBeforeCompletionIgnoredInHem(String flushBeforeCompletion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@ForceDiscriminator is deprecated use @DiscriminatorOptions instead.", id = 62)
 	void deprecatedForceDescriminatorAnnotation();
 
 	@LogMessage(level = WARN)
 	@Message(value = "The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead",
 			id = 63)
 	void deprecatedOracle9Dialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "The OracleDialect dialect has been deprecated; use Oracle8iDialect instead", id = 64)
 	void deprecatedOracleDialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "DEPRECATED : use [%s] instead with custom [%s] implementation", id = 65)
 	void deprecatedUuidGenerator(String name,
 								 String name2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disallowing insert statement comment for select-identity due to Oracle driver bug", id = 67)
 	void disallowingInsertStatementComment();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Duplicate generator name %s", id = 69)
 	void duplicateGeneratorName(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Duplicate generator table: %s", id = 70)
 	void duplicateGeneratorTable(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Duplicate import: %s -> %s", id = 71)
 	void duplicateImport(String entityName,
 						 String rename);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Duplicate joins for class: %s", id = 72)
 	void duplicateJoins(String entityName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "entity-listener duplication, first event definition will be used: %s", id = 73)
 	void duplicateListener(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Found more than one <persistence-unit-metadata>, subsequent ignored", id = 74)
 	void duplicateMetadata();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities deleted: %s", id = 76)
 	void entitiesDeleted(long entityDeleteCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities fetched (minimize this): %s", id = 77)
 	void entitiesFetched(long entityFetchCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities inserted: %s", id = 78)
 	void entitiesInserted(long entityInsertCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities loaded: %s", id = 79)
 	void entitiesLoaded(long entityLoadCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities updated: %s", id = 80)
 	void entitiesUpdated(long entityUpdateCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@org.hibernate.annotations.Entity used on a non root entity: ignored for %s", id = 81)
 	void entityAnnotationOnNonRoot(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity Manager closed by someone else (%s must not be used)", id = 82)
 	void entityManagerClosedBySomeoneElse(String autoCloseSession);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names",
 			id = 84)
 	void entityMappedAsNonAbstract(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "%s %s found", id = 85)
 	void exceptionHeaderFound(String exceptionHeader,
 							  String metaInfOrmXml);
 
 	@LogMessage(level = INFO)
 	@Message(value = "%s No %s found", id = 86)
 	void exceptionHeaderNotFound(String exceptionHeader,
 								 String metaInfOrmXml);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception in interceptor afterTransactionCompletion()", id = 87)
 	void exceptionInAfterTransactionCompletionInterceptor(@Cause Throwable e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception in interceptor beforeTransactionCompletion()", id = 88)
 	void exceptionInBeforeTransactionCompletionInterceptor(@Cause Throwable e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sub-resolver threw unexpected exception, continuing to next : %s", id = 89)
 	void exceptionInSubResolver(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Expected type: %s, actual value: %s", id = 91)
 	void expectedType(String name,
 					  String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "An item was expired by the cache while it was locked (increase your cache timeout): %s", id = 92)
 	void expired(Object key);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Bound factory to JNDI name: %s", id = 94)
 	void factoryBoundToJndiName(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "A factory was renamed from [%s] to [%s] in JNDI", id = 96)
 	void factoryJndiRename(String oldName, String newName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unbound factory from JNDI name: %s", id = 97)
 	void factoryUnboundFromJndiName(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "A factory was unbound from name: %s", id = 98)
 	void factoryUnboundFromName(String name);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "an assertion failure occured" + " (this may indicate a bug in Hibernate, but is more likely due"
 			+ " to unsafe use of the session): %s", id = 99)
 	void failed(Throwable throwable);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Fail-safe cleanup (collections) : %s", id = 100)
 	void failSafeCollectionsCleanup(CollectionLoadContext collectionLoadContext);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Fail-safe cleanup (entities) : %s", id = 101)
 	void failSafeEntitiesCleanup(EntityLoadContext entityLoadContext);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Fetching database metadata", id = 102)
 	void fetchingDatabaseMetadata();
 
 	@LogMessage(level = WARN)
 	@Message(value = "firstResult/maxResults specified with collection fetch; applying in memory!", id = 104)
 	void firstOrMaxResultsSpecifiedWithCollectionFetch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Flushes: %s", id = 105)
 	void flushes(long flushCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Forcing container resource cleanup on transaction completion", id = 106)
 	void forcingContainerResourceCleanup();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences",
 			id = 107)
 	void forcingTableUse();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Foreign keys: %s", id = 108)
 	void foreignKeys(Set keySet);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Found mapping document in jar: %s", id = 109)
 	void foundMappingDocument(String name);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Getters of lazy classes cannot be final: %s.%s", id = 112)
 	void gettersOfLazyClassesCannotBeFinal(String entityName,
 										   String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "GUID identifier generated: %s", id = 113)
 	void guidGenerated(String result);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Handling transient entity in delete processing", id = 114)
 	void handlingTransientEntity();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate connection pool size: %s", id = 115)
 	void hibernateConnectionPoolSize(int poolSize);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Config specified explicit optimizer of [%s], but [%s=%s; honoring optimizer setting", id = 116)
 	void honoringOptimizerSetting(String none,
 								  String incrementParam,
 								  int incrementSize);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "HQL: %s, time: %sms, rows: %s", id = 117)
 	void hql(String hql,
 			 Long valueOf,
 			 Long valueOf2);
 
 	@LogMessage(level = WARN)
 	@Message(value = "HSQLDB supports only READ_UNCOMMITTED isolation", id = 118)
 	void hsqldbSupportsOnlyReadCommittedIsolation();
 
 	@LogMessage(level = WARN)
 	@Message(value = "On EntityLoadContext#clear, hydratingEntities contained [%s] entries", id = 119)
 	void hydratingEntitiesCount(int size);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ignoring unique constraints specified on table generator [%s]", id = 120)
 	void ignoringTableGeneratorConstraints(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Ignoring unrecognized query hint [%s]", id = 121)
 	void ignoringUnrecognizedQueryHint(String hintName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "IllegalArgumentException in class: %s, getter method of property: %s", id = 122)
 	void illegalPropertyGetterArgument(String name,
 									   String propertyName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "IllegalArgumentException in class: %s, setter method of property: %s", id = 123)
 	void illegalPropertySetterArgument(String name,
 									   String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@Immutable used on a non root entity: ignored for %s", id = 124)
 	void immutableAnnotationOnNonRoot(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Mapping metadata cache was not completely processed", id = 125)
 	void incompleteMappingMetadataCacheProcessing();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Indexes: %s", id = 126)
 	void indexes(Set keySet);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Could not bind JNDI listener", id = 127)
 	void couldNotBindJndiListener();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Instantiating explicit connection provider: %s", id = 130)
 	void instantiatingExplicitConnectionProvider(String providerClassName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Array element type error\n%s", id = 132)
 	void invalidArrayElementType(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s",
 			id = 133)
 	void invalidDiscriminatorAnnotation(String className);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Application attempted to edit read only item: %s", id = 134)
 	void invalidEditOfReadOnlyItem(Object key);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Invalid JNDI name: %s", id = 135)
 	void invalidJndiName(String name,
 						 @Cause JndiNameException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Inapropriate use of @OnDelete on entity, annotation ignored: %s", id = 136)
 	void invalidOnDeleteAnnotation(String entityName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Root entity should not hold an PrimaryKeyJoinColum(s), will be ignored", id = 137)
 	void invalidPrimaryKeyJoinColumnAnnotation();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s",
 			id = 138)
 	void invalidSubStrategy(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s", id = 139)
 	void invalidTableAnnotation(String className);
 
 	@LogMessage(level = INFO)
 	@Message(value = "JACC contextID: %s", id = 140)
 	void jaccContextId(String contextId);
 
 	@LogMessage(level = INFO)
 	@Message(value = "java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]", id = 141)
 	void JavaSqlTypesMappedSameCodeMultipleTimes(int code,
 												 String old,
 												 String name);
 
 	@Message(value = "Javassist Enhancement failed: %s", id = 142)
 	String javassistEnhancementFailed(String entityName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "%s = false breaks the EJB3 specification", id = 144)
 	void jdbcAutoCommitFalseBreaksEjb3Spec(String autocommit);
 
 	@LogMessage(level = WARN)
 	@Message(value = "No JDBC Driver class was specified by property %s", id = 148)
 	void jdbcDriverNotSpecified(String driver);
 
 	@LogMessage(level = INFO)
 	@Message(value = "JDBC isolation level: %s", id = 149)
 	void jdbcIsolationLevel(String isolationLevelToString);
 
 	@Message(value = "JDBC rollback failed", id = 151)
 	String jdbcRollbackFailed();
 
 	@Message(value = "JDBC URL was not specified by property %s", id = 152)
 	String jdbcUrlNotSpecified(String url);
 
 	@LogMessage(level = INFO)
 	@Message(value = "JNDI InitialContext properties:%s", id = 154)
 	void jndiInitialContextProperties(Hashtable hash);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "JNDI name %s does not handle a session factory reference", id = 155)
 	void jndiNameDoesNotHandleSessionFactoryReference(String sfJNDIName,
 													  @Cause ClassCastException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Lazy property fetching available for: %s", id = 157)
 	void lazyPropertyFetchingAvailable(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts",
 			id = 159)
 	void loadingCollectionKeyNotFound(CollectionKey collectionKey);
 
 	@LogMessage(level = WARN)
 	@Message(value = "On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries", id = 160)
 	void localLoadingCollectionKeysCount(int size);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Logging statistics....", id = 161)
 	void loggingStatistics();
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "*** Logical connection closed ***", id = 162)
 	void logicalConnectionClosed();
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Logical connection releasing its physical connection", id = 163)
 	void logicalConnectionReleasingPhysicalConnection();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Max query time: %sms", id = 173)
 	void maxQueryTime(long queryExecutionMaxTime);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Function template anticipated %s arguments, but %s arguments encountered", id = 174)
 	void missingArguments(int anticipatedNumberOfArguments,
 						  int numberOfArguments);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s",
 			id = 175)
 	void missingEntityAnnotation(String className);
 
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error in named query: %s", id = 177)
 	void namedQueryError(String queryName,
 						 @Cause HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Naming exception occurred accessing factory: %s", id = 178)
 	void namingExceptionAccessingFactory(NamingException exception);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Narrowing proxy to %s - this operation breaks ==", id = 179)
 	void narrowingProxy(Class concreteProxyClass);
 
 	@LogMessage(level = WARN)
 	@Message(value = "FirstResult/maxResults specified on polymorphic query; applying in memory!", id = 180)
 	void needsLimit();
 
 	@LogMessage(level = WARN)
 	@Message(value = "No appropriate connection provider encountered, assuming application will be supplying connections",
 			id = 181)
 	void noAppropriateConnectionProvider();
 
 	@LogMessage(level = INFO)
 	@Message(value = "No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)",
 			id = 182)
 	void noDefaultConstructor(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "no persistent classes found for query class: %s", id = 183)
 	void noPersistentClassesFound(String query);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "No session factory with JNDI name %s", id = 184)
 	void noSessionFactoryWithJndiName(String sfJNDIName,
 									  @Cause NameNotFoundException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Optimistic lock failures: %s", id = 187)
 	void optimisticLockFailures(long optimisticFailureCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@OrderBy not allowed for an indexed collection, annotation ignored.", id = 189)
 	void orderByAnnotationIndexedCollection();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Overriding %s is dangerous, this might break the EJB3 specification implementation", id = 193)
 	void overridingTransactionStrategyDangerous(String transactionStrategy);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Package not found or wo package-info.java: %s", id = 194)
 	void packageNotFound(String packageName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Parameter position [%s] occurred as both JPA and Hibernate positional parameter", id = 195)
 	void parameterPositionOccurredAsBothJpaAndHibernatePositionalParameter(Integer position);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error parsing XML (%s) : %s", id = 196)
 	void parsingXmlError(int lineNumber,
 						 String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error parsing XML: %s(%s) %s", id = 197)
 	void parsingXmlErrorForFile(String file,
 								int lineNumber,
 								String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Warning parsing XML (%s) : %s", id = 198)
 	void parsingXmlWarning(int lineNumber,
 						   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Warning parsing XML: %s(%s) %s", id = 199)
 	void parsingXmlWarningForFile(String file,
 								  int lineNumber,
 								  String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Persistence provider caller does not implement the EJB3 spec correctly."
 			+ "PersistenceUnitInfo.getNewTempClassLoader() is null.", id = 200)
 	void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended",
 			id = 201)
 	void pooledOptimizerReportedInitialValue(IntegralDataTypeHolder value);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "PreparedStatement was already in the batch, [%s].", id = 202)
 	void preparedStatementAlreadyInBatch(String sql);
 
 	@LogMessage(level = WARN)
 	@Message(value = "processEqualityExpression() : No expression to process!", id = 203)
 	void processEqualityExpression();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Processing PersistenceUnitInfo [\n\tname: %s\n\t...]", id = 204)
 	void processingPersistenceUnitInfoName(String persistenceUnitName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Loaded properties from resource hibernate.properties: %s", id = 205)
 	void propertiesLoaded(Properties maskOut);
 
 	@LogMessage(level = INFO)
 	@Message(value = "hibernate.properties not found", id = 206)
 	void propertiesNotFound();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Property %s not found in class but described in <mapping-file/> (possible typo error)", id = 207)
 	void propertyNotFound(String property);
 
 	@LogMessage(level = WARN)
 	@Message(value = "%s has been deprecated in favor of %s; that provider will be used instead.", id = 208)
 	void providerClassDeprecated(String providerClassName,
 								 String actualProviderClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.",
 			id = 209)
 	void proxoolProviderClassNotFound(String proxoolProviderClassName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Queries executed to database: %s", id = 210)
 	void queriesExecuted(long queryExecutionCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache hits: %s", id = 213)
 	void queryCacheHits(long queryCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache misses: %s", id = 214)
 	void queryCacheMisses(long queryCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache puts: %s", id = 215)
 	void queryCachePuts(long queryCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "RDMSOS2200Dialect version: 1.0", id = 218)
 	void rdmsOs2200Dialect();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from cache file: %s", id = 219)
 	void readingCachedMappings(File cachedFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from file: %s", id = 220)
 	void readingMappingsFromFile(String path);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from resource: %s", id = 221)
 	void readingMappingsFromResource(String resourceName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "read-only cache configured for mutable collection [%s]", id = 222)
 	void readOnlyCacheConfiguredForMutableCollection(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!",
 			id = 223)
 	void recognizedObsoleteHibernateNamespace(String oldHibernateNamespace,
 											  String hibernateNamespace);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Property [%s] has been renamed to [%s]; update your properties appropriately", id = 225)
 	void renamedProperty(Object propertyName,
 						 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Required a different provider: %s", id = 226)
 	void requiredDifferentProvider(String provider);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema export", id = 227)
 	void runningHbm2ddlSchemaExport();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema update", id = 228)
 	void runningHbm2ddlSchemaUpdate();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running schema validator", id = 229)
 	void runningSchemaValidator();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema export complete", id = 230)
 	void schemaExportComplete();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Schema export unsuccessful", id = 231)
 	void schemaExportUnsuccessful(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema update complete", id = 232)
 	void schemaUpdateComplete();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Scoping types to session factory %s after already scoped %s", id = 233)
 	void scopingTypesToSessionFactoryAfterAlreadyScoped(SessionFactoryImplementor factory,
 														SessionFactoryImplementor factory2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Searching for mapping documents in jar: %s", id = 235)
 	void searchingForMappingDocuments(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache hits: %s", id = 237)
 	void secondLevelCacheHits(long secondLevelCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache misses: %s", id = 238)
 	void secondLevelCacheMisses(long secondLevelCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache puts: %s", id = 239)
 	void secondLevelCachePuts(long secondLevelCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Service properties: %s", id = 240)
 	void serviceProperties(Properties properties);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions closed: %s", id = 241)
 	void sessionsClosed(long sessionCloseCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions opened: %s", id = 242)
 	void sessionsOpened(long sessionOpenCount);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Setters of lazy classes cannot be final: %s.%s", id = 243)
 	void settersOfLazyClassesCannotBeFinal(String entityName,
 										   String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@Sort not allowed for an indexed collection, annotation ignored.", id = 244)
 	void sortAnnotationIndexedCollection();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Manipulation query [%s] resulted in [%s] split queries", id = 245)
 	void splitQueries(String sourceQuery,
 					  int length);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "SQLException escaped proxy", id = 246)
 	void sqlExceptionEscapedProxy(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "SQL Error: %s, SQLState: %s", id = 247)
 	void sqlWarning(int errorCode,
 					String sqlState);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting query cache at region: %s", id = 248)
 	void startingQueryCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting service at JNDI name: %s", id = 249)
 	void startingServiceAtJndiName(String boundName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting update timestamps cache at region: %s", id = 250)
 	void startingUpdateTimestampsCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Start time: %s", id = 251)
 	void startTime(long startTime);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements closed: %s", id = 252)
 	void statementsClosed(long closeStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements prepared: %s", id = 253)
 	void statementsPrepared(long prepareStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Stopping service", id = 255)
 	void stoppingService();
 
 	@LogMessage(level = INFO)
 	@Message(value = "sub-resolver threw unexpected exception, continuing to next : %s", id = 257)
 	void subResolverException(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Successful transactions: %s", id = 258)
 	void successfulTransactions(long committedTransactionCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Synchronization [%s] was already registered", id = 259)
 	void synchronizationAlreadyRegistered(Synchronization synchronization);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception calling user Synchronization [%s] : %s", id = 260)
 	void synchronizationFailed(Synchronization synchronization,
 							   Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table found: %s", id = 261)
 	void tableFound(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table not found: %s", id = 262)
 	void tableNotFound(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transactions: %s", id = 266)
 	void transactions(long transactionCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Transaction started on non-root session", id = 267)
 	void transactionStartedOnNonRootSession();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transaction strategy: %s", id = 268)
 	void transactionStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Type [%s] defined no registration keys; ignoring", id = 269)
 	void typeDefinedNoRegistrationKeys(BasicType type);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Type registration [%s] overrides previous : %s", id = 270)
 	void typeRegistrationOverridesPrevious(String key,
 										   Type old);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Naming exception occurred accessing Ejb3Configuration", id = 271)
 	void unableToAccessEjb3Configuration(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error while accessing session factory with JNDI name %s", id = 272)
 	void unableToAccessSessionFactory(String sfJNDIName,
 									  @Cause NamingException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error accessing type info result set : %s", id = 273)
 	void unableToAccessTypeInfoResultSet(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to apply constraints on DDL for %s", id = 274)
 	void unableToApplyConstraints(String className,
 								  @Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind Ejb3Configuration to JNDI", id = 276)
 	void unableToBindEjb3ConfigurationToJndi(@Cause JndiException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind factory to JNDI", id = 277)
 	void unableToBindFactoryToJndi(@Cause JndiException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not bind value '%s' to parameter: %s; %s", id = 278)
 	void unableToBindValueToParameter(String nullSafeToString,
 									  int index,
 									  String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to build enhancement metamodel for %s", id = 279)
 	void unableToBuildEnhancementMetamodel(String className);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s",
 			id = 280)
 	void unableToBuildSessionFactoryUsingMBeanClasspath(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up callable statement", id = 281)
 	void unableToCleanUpCallableStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up prepared statement", id = 282)
 	void unableToCleanUpPreparedStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to cleanup temporary id table after use [%s]", id = 283)
 	void unableToCleanupTemporaryIdTable(Throwable t);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing connection", id = 284)
 	void unableToCloseConnection(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error closing InitialContext [%s]", id = 285)
 	void unableToCloseInitialContext(String string);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing input files: %s", id = 286)
 	void unableToCloseInputFiles(String name,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream", id = 287)
 	void unableToCloseInputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream for %s", id = 288)
 	void unableToCloseInputStreamForResource(String resourceName,
 											 @Cause IOException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to close iterator", id = 289)
 	void unableToCloseIterator(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close jar: %s", id = 290)
 	void unableToCloseJar(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing output file: %s", id = 291)
 	void unableToCloseOutputFile(String outputFile,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing output stream", id = 292)
 	void unableToCloseOutputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Problem closing pooled connection", id = 293)
 	void unableToClosePooledConnection(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session", id = 294)
 	void unableToCloseSession(@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session during rollback", id = 295)
 	void unableToCloseSessionDuringRollback(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing stream", id = 296)
 	void unableToCloseStream(@Cause IOException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close stream on hibernate.properties: %s", id = 297)
 	void unableToCloseStreamError(IOException error);
 
 	@Message(value = "JTA commit failed", id = 298)
 	String unableToCommitJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema update", id = 299)
 	void unableToCompleteSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema validation", id = 300)
 	void unableToCompleteSchemaValidation(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to configure SQLExceptionConverter : %s", id = 301)
 	void unableToConfigureSqlExceptionConverter(HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to construct current session context [%s]", id = 302)
 	void unableToConstructCurrentSessionContext(String impl,
 												@Cause Throwable e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to construct instance of specified SQLExceptionConverter : %s", id = 303)
 	void unableToConstructSqlExceptionConverter(Throwable t);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not copy system properties, system properties will be ignored", id = 304)
 	void unableToCopySystemProperties();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not create proxy factory for:%s", id = 305)
 	void unableToCreateProxyFactory(String entityName,
 									@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error creating schema ", id = 306)
 	void unableToCreateSchema(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not deserialize cache file: %s : %s", id = 307)
 	void unableToDeserializeCache(String path,
 								  SerializationException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy cache: %s", id = 308)
 	void unableToDestroyCache(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy query cache: %s: %s", id = 309)
 	void unableToDestroyQueryCache(String region,
 								   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy update timestamps cache: %s: %s", id = 310)
 	void unableToDestroyUpdateTimestampsCache(String region,
 											  String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to determine lock mode value : %s -> %s", id = 311)
 	void unableToDetermineLockModeValue(String hintName,
 										Object value);
 
 	@Message(value = "Could not determine transaction status", id = 312)
 	String unableToDetermineTransactionStatus();
 
 	@Message(value = "Could not determine transaction status after commit", id = 313)
 	String unableToDetermineTransactionStatusAfterCommit();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to drop temporary id table after use [%s]", id = 314)
 	void unableToDropTemporaryIdTable(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception executing batch [%s]", id = 315)
 	void unableToExecuteBatch(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error executing resolver [%s] : %s", id = 316)
 	void unableToExecuteResolver(AbstractDialectResolver abstractDialectResolver,
 								 String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not find any META-INF/persistence.xml file in the classpath", id = 318)
 	void unableToFindPersistenceXmlInClasspath();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not get database metadata", id = 319)
 	void unableToGetDatabaseMetadata(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate configured schema name resolver [%s] %s", id = 320)
 	void unableToInstantiateConfiguredSchemaNameResolver(String resolverClassName,
 														 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to interpret specified optimizer [%s], falling back to noop", id = 321)
 	void unableToLocateCustomOptimizerClass(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate specified optimizer [%s], falling back to noop", id = 322)
 	void unableToInstantiateOptimizer(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate UUID generation strategy class : %s", id = 325)
 	void unableToInstantiateUuidGenerationStrategy(Exception ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Cannot join transaction: do not override %s", id = 326)
 	void unableToJoinTransaction(String transactionStrategy);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error performing load command : %s", id = 327)
 	void unableToLoadCommand(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to load/access derby driver class sysinfo to check versions : %s", id = 328)
 	void unableToLoadDerbyDriver(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Problem loading properties from hibernate.properties", id = 329)
 	void unableToLoadProperties();
 
 	@Message(value = "Unable to locate config file: %s", id = 330)
 	String unableToLocateConfigFile(String path);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate configured schema name resolver class [%s] %s", id = 331)
 	void unableToLocateConfiguredSchemaNameResolver(String resolverClassName,
 													String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate MBeanServer on JMX service shutdown", id = 332)
 	void unableToLocateMBeanServer();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate requested UUID generation strategy class : %s", id = 334)
 	void unableToLocateUuidGenerationStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to log SQLWarnings : %s", id = 335)
 	void unableToLogSqlWarnings(SQLException sqle);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not log warnings", id = 336)
 	void unableToLogWarnings(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on PersistenceException: ", id = 337)
 	void unableToMarkForRollbackOnPersistenceException(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on TransientObjectException: ", id = 338)
 	void unableToMarkForRollbackOnTransientObjectException(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata: %s", id = 339)
 	void unableToObjectConnectionMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata: %s", id = 340)
 	void unableToObjectConnectionToQueryMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata : %s", id = 341)
 	void unableToObtainConnectionMetadata(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata : %s", id = 342)
 	void unableToObtainConnectionToQueryMetadata(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not obtain initial context", id = 343)
 	void unableToObtainInitialContext(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not parse the package-level metadata [%s]", id = 344)
 	void unableToParseMetadata(String packageName);
 
 	@Message(value = "JDBC commit failed", id = 345)
 	String unableToPerformJdbcCommit();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error during managed flush [%s]", id = 346)
 	void unableToPerformManagedFlush(String message);
 
 	@Message(value = "Unable to query java.sql.DatabaseMetaData", id = 347)
 	String unableToQueryDatabaseMetadata();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to read class: %s", id = 348)
 	void unableToReadClass(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not read column value from result set: %s; %s", id = 349)
 	void unableToReadColumnValueFromResultSet(String name,
 											  String message);
 
 	@Message(value = "Could not read a hi value - you need to populate the table: %s", id = 350)
 	String unableToReadHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not read or init a hi value", id = 351)
 	void unableToReadOrInitHiValue(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to release batch statement...", id = 352)
 	void unableToReleaseBatchStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not release a cache lock : %s", id = 353)
 	void unableToReleaseCacheLock(CacheException ce);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release initial context: %s", id = 354)
 	void unableToReleaseContext(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release created MBeanServer : %s", id = 355)
 	void unableToReleaseCreatedMBeanServer(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release isolated connection [%s]", id = 356)
 	void unableToReleaseIsolatedConnection(Throwable ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release type info result set", id = 357)
 	void unableToReleaseTypeInfoResultSet();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to erase previously added bag join fetch", id = 358)
 	void unableToRemoveBagJoinFetch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not resolve aggregate function [%s]; using standard definition", id = 359)
 	void unableToResolveAggregateFunction(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to resolve mapping file [%s]", id = 360)
 	void unableToResolveMappingFile(String xmlFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to retreive cache from JNDI [%s]: %s", id = 361)
 	void unableToRetrieveCache(String namespace,
 							   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to retrieve type info result set : %s", id = 362)
 	void unableToRetrieveTypeInfoResultSet(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback connection on exception [%s]", id = 363)
 	void unableToRollbackConnection(Exception ignore);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback isolated transaction on error [%s] : [%s]", id = 364)
 	void unableToRollbackIsolatedTransaction(Exception e,
 											 Exception ignore);
 
 	@Message(value = "JTA rollback failed", id = 365)
 	String unableToRollbackJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error running schema update", id = 366)
 	void unableToRunSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not set transaction to rollback only", id = 367)
 	void unableToSetTransactionToRollbackOnly(@Cause SystemException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while stopping service", id = 368)
 	void unableToStopHibernateService(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error stopping service [%s] : %s", id = 369)
 	void unableToStopService(Class class1,
 							 String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]",
 			id = 370)
 	void unableToSwitchToMethodUsingColumnIndex(Method method);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not synchronize database state with session: %s", id = 371)
 	void unableToSynchronizeDatabaseStateWithSession(HibernateException he);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not toggle autocommit", id = 372)
 	void unableToToggleAutoCommit(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to transform class: %s", id = 373)
 	void unableToTransformClass(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not unbind factory from JNDI", id = 374)
 	void unableToUnbindFactoryFromJndi(@Cause JndiException e);
 
 	@Message(value = "Could not update hi value in: %s", id = 375)
 	Object unableToUpdateHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not updateQuery hi value in: %s", id = 376)
 	void unableToUpdateQueryHiValue(String tableName,
 									@Cause SQLException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error wrapping result set", id = 377)
 	void unableToWrapResultSet(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "I/O reported error writing cached file : %s: %s", id = 378)
 	void unableToWriteCachedFile(String path,
 								 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unexpected literal token type [%s] passed for numeric processing", id = 380)
 	void unexpectedLiteralTokenType(int type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "JDBC driver did not return the expected number of row counts", id = 381)
 	void unexpectedRowCounts();
 
 	@LogMessage(level = WARN)
 	@Message(value = "unrecognized bytecode provider [%s], using javassist by default", id = 382)
 	void unknownBytecodeProvider(String providerName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Ingres major version [%s]; using Ingres 9.2 dialect", id = 383)
 	void unknownIngresVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Oracle major version [%s]", id = 384)
 	void unknownOracleVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Microsoft SQL Server major version [%s] using SQL Server 2000 dialect", id = 385)
 	void unknownSqlServerVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "ResultSet had no statement associated with it, but was not yet registered", id = 386)
 	void unregisteredResultSetWithoutStatement();
 
 	@LogMessage(level = WARN)
 	@Message(value = "ResultSet's statement was not registered", id = 387)
 	void unregisteredStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 388)
 	void unsuccessful(String sql);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 389)
 	void unsuccessfulCreate(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Overriding release mode as connection provider does not support 'after_statement'", id = 390)
 	void unsupportedAfterStatement();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ingres 10 is not yet fully supported; using Ingres 9.3 dialect", id = 391)
 	void unsupportedIngresVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Hibernate does not support SequenceGenerator.initialValue() unless '%s' set", id = 392)
 	void unsupportedInitialValue(String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly",
 			id = 393)
 	void unsupportedMultiTableBulkHqlJpaql(int majorVersion,
 										   int minorVersion,
 										   int buildId);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Oracle 11g is not yet fully supported; using Oracle 10g dialect", id = 394)
 	void unsupportedOracleVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Usage of obsolete property: %s no longer supported, use: %s", id = 395)
 	void unsupportedProperty(Object propertyName,
 							 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Updating schema", id = 396)
 	void updatingSchema();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using ASTQueryTranslatorFactory", id = 397)
 	void usingAstQueryTranslatorFactory();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Explicit segment value for id generator [%s.%s] suggested; using default [%s]", id = 398)
 	void usingDefaultIdGeneratorSegmentValue(String tableName,
 											 String segmentColumnName,
 											 String defaultToUse);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using default transaction strategy (direct JDBC transactions)", id = 399)
 	void usingDefaultTransactionStrategy();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using dialect: %s", id = 400)
 	void usingDialect(Dialect dialect);
 
 	@LogMessage(level = INFO)
 	@Message(value = "using driver [%s] at URL [%s]", id = 401)
 	void usingDriver(String driverClassName,
 					 String url);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using Hibernate built-in connection pool (not for production use!)", id = 402)
 	void usingHibernateBuiltInConnectionPool();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Don't use old DTDs, read the Hibernate 3.x Migration Guide!", id = 404)
 	void usingOldDtd();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using bytecode reflection optimizer", id = 406)
 	void usingReflectionOptimizer();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using java.io streams to persist binary types", id = 407)
 	void usingStreams();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using workaround for JVM bug in java.sql.Timestamp", id = 408)
 	void usingTimestampWorkaround();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead",
 			id = 409)
 	void usingUuidHexGenerator(String name,
 							   String name2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Validator not found: ignoring", id = 410)
 	void validatorNotFound();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Core {%s}", id = 412)
 	void version(String versionString);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Warnings creating temp table : %s", id = 413)
 	void warningsCreatingTempTable(SQLWarning warning);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.",
 			id = 414)
 	void willNotRegisterListeners();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Write locks via update not supported for non-versioned entities [%s]", id = 416)
 	void writeLocksNotSupported(String entityName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Writing generated schema to file: %s", id = 417)
 	void writingGeneratedSchemaToFile(String outputFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Adding override for %s: %s", id = 418)
 	void addingOverrideFor(String name,
 						   String name2);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s",
 			id = 419)
 	void resolvedSqlTypeDescriptorForDifferentSqlCode(String name,
 													  String valueOf,
 													  String name2,
 													  String valueOf2);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Closing un-released batch", id = 420)
 	void closingUnreleasedBatch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as %s is true", id = 421)
 	void disablingContextualLOBCreation(String nonContextualLobCreation);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as connection was null", id = 422)
 	void disablingContextualLOBCreationSinceConnectionNull();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4",
 			id = 423)
 	void disablingContextualLOBCreationSinceOldJdbcVersion(int jdbcMajorVersion);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as createClob() method threw error : %s", id = 424)
 	void disablingContextualLOBCreationSinceCreateClobFailed(Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not close session; swallowing exception[%s] as transaction completed", id = 425)
 	void unableToCloseSessionButSwallowingError(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "You should set hibernate.transaction.manager_lookup_class if cache is enabled", id = 426)
 	void setManagerLookupClass();
 
 //	@LogMessage(level = WARN)
 //	@Message(value = "Using deprecated %s strategy [%s], use newer %s strategy instead [%s]", id = 427)
 //	void deprecatedTransactionManagerStrategy(String name,
 //											  String transactionManagerStrategy,
 //											  String name2,
 //											  String jtaPlatform);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting",
 			id = 428)
 	void legacyTransactionManagerStrategy(String name,
 										  String jtaPlatform);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Setting entity-identifier value binding where one already existed : %s.", id = 429)
 	void entityIdentifierValueBindingExists(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead",
 			id = 430)
 	void deprecatedDerbyDialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to determine H2 database version, certain features may not work", id = 431)
 	void undeterminedH2Version();
 
 	@LogMessage(level = WARN)
 	@Message(value = "There were not column names specified for index %s on table %s", id = 432)
 	void noColumnsSpecifiedForIndex(String indexName, String tableName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache puts: %s", id = 433)
 	void timestampCachePuts(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache hits: %s", id = 434)
 	void timestampCacheHits(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache misses: %s", id = 435)
 	void timestampCacheMisses(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity manager factory name (%s) is already registered.  If entity manager will be clustered "+
 			"or passivated, specify a unique value for property '%s'", id = 436)
 	void entityManagerFactoryAlreadyRegistered(String emfName, String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n" +
 			"\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)" , id = 437)
 	void cannotResolveNonNullableTransientDependencies(String transientEntityString,
 													   Set<String> dependentEntityStrings,
 													   Set<String> nonNullableAssociationPaths);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache puts: %s", id = 438)
 	void naturalIdCachePuts(long naturalIdCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache hits: %s", id = 439)
 	void naturalIdCacheHits(long naturalIdCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache misses: %s", id = 440)
 	void naturalIdCacheMisses(long naturalIdCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Max NaturalId query time: %sms", id = 441)
 	void naturalIdMaxQueryTime(long naturalIdQueryExecutionMaxTime);
 	
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId queries executed to database: %s", id = 442)
 	void naturalIdQueriesExecuted(long naturalIdQueriesExecutionCount);
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Dialect [%s] limits the number of elements in an IN predicate to %s entries.  " +
 					"However, the given parameter list [%s] contained %s entries, which will likely cause failures " +
 					"to execute the query in the database",
 			id = 443
 	)
 	void tooManyInExpressions(String dialectName, int limit, String paramName, int size);
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Encountered request for locking however dialect reports that database prefers locking be done in a " +
-					"separate select; results will be locked after initial query executes",
+					"separate select (follow-on locking); results will be locked after initial query executes",
 			id = 444
 	)
 	void usingFollowOnLocking();
+
+	@LogMessage(level = WARN)
+	@Message(
+			value = "Alias-specific lock modes requested, which is not currently supported with follow-on locking; " +
+					"all acquired locks will be [%s]",
+			id = 445
+	)
+	void aliasSpecificLockingWithFollowOnLocking(LockMode lockMode);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 67d1907e49..66a618930d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1273 +1,1286 @@
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
 
 	protected static interface AfterLoadAction {
 		public void afterLoad(SessionImplementor session, Object entity, Loadable persister);
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( dialect.useFollowOnLocking() ) {
 			LOG.usingFollowOnLocking();
-			final LockOptions lockOptions = parameters.getLockOptions();
+			// currently only one lock mode is allowed in follow-on locking
+			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
+			final LockOptions lockOptions = new LockOptions( lockMode );
+			lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
+			lockOptions.setScope( parameters.getLockOptions().getScope() );
 			afterLoadActions.add(
 					new AfterLoadAction() {
-						private final LockOptions originalLockOptions = lockOptions.makeCopy();
 						@Override
 						public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
-							( (Session) session ).buildLockRequest( originalLockOptions ).lock( persister.getEntityName(), entity );
+							( (Session) session ).buildLockRequest( lockOptions ).lock( persister.getEntityName(), entity );
 						}
 					}
 			);
 			parameters.setLockOptions( new LockOptions() );
 			return true;
 		}
 		return false;
 	}
 
+	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
+		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
+
+		if ( lockOptions.hasAliasSpecificLockModes() ) {
+			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
+		}
+
+		return lockModeToUse;
+	}
+
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
 			// key value upon which to doAfterTransactionCompletion the breaking logic.  However,
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
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
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
 
 		final ResultSet rs = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final Statement st = rs.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions );
 		}
 		finally {
 			st.close();
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
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
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
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
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
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index c5f0add9a6..521ffac762 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,274 +1,288 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.loader.criteria;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.OuterJoinLoader;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A <tt>Loader</tt> for <tt>Criteria</tt> queries. Note that criteria queries are
  * more like multi-object <tt>load()</tt>s than like HQL queries.
  *
  * @author Gavin King
  */
 public class CriteriaLoader extends OuterJoinLoader {
 
 	//TODO: this class depends directly upon CriteriaImpl, 
 	//      in the impl package ... add a CriteriaImplementor 
 	//      interface
 
 	//NOTE: unlike all other Loaders, this one is NOT
 	//      multithreaded, or cacheable!!
 
 	private final CriteriaQueryTranslator translator;
 	private final Set querySpaces;
 	private final Type[] resultTypes;
 	//the user visible aliases, which are unknown to the superclass,
 	//these are not the actual "physical" SQL aliases
 	private final String[] userAliases;
 	private final boolean[] includeInResultRow;
 	private final int resultRowLength;
 
 	public CriteriaLoader(
 			final OuterJoinLoadable persister, 
 			final SessionFactoryImplementor factory, 
 			final CriteriaImpl criteria, 
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers) throws HibernateException {
 		super( factory, loadQueryInfluencers );
 
 		translator = new CriteriaQueryTranslator(
 				factory, 
 				criteria, 
 				rootEntityName, 
 				CriteriaQueryTranslator.ROOT_SQL_ALIAS
 			);
 
 		querySpaces = translator.getQuerySpaces();
 		
 		CriteriaJoinWalker walker = new CriteriaJoinWalker(
 				persister, 
 				translator,
 				factory, 
 				criteria, 
 				rootEntityName, 
 				loadQueryInfluencers
 			);
 
 		initFromWalker(walker);
 		
 		userAliases = walker.getUserAliases();
 		resultTypes = walker.getResultTypes();
 		includeInResultRow = walker.includeInResultRow();
 		resultRowLength = ArrayHelper.countTrue( includeInResultRow );
 
 		postInstantiate();
 
 	}
 	
 	public ScrollableResults scroll(SessionImplementor session, ScrollMode scrollMode) 
 	throws HibernateException {
 		QueryParameters qp = translator.getQueryParameters();
 		qp.setScrollMode(scrollMode);
 		return scroll(qp, resultTypes, null, session);
 	}
 
 	public List list(SessionImplementor session) 
 	throws HibernateException {
 		return list( session, translator.getQueryParameters(), querySpaces, resultTypes );
 
 	}
 
 	protected String[] getResultRowAliases() {
 		return userAliases;
 	}
 
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return true;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 	throws SQLException, HibernateException {
 		return resolveResultTransformer( transformer ).transformTuple(
 				getResultRow( row, rs, session),
 				getResultRowAliases()
 		);
 	}
 			
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		final Object[] result;
 		if ( translator.hasProjection() ) {
 			Type[] types = translator.getProjectedTypes();
 			result = new Object[types.length];
 			String[] columnAliases = translator.getProjectedColumnAliases();
 			for ( int i=0, pos=0; i<result.length; i++ ) {
 				int numColumns = types[i].getColumnSpan( session.getFactory() );
 				if ( numColumns > 1 ) {
 			    	String[] typeColumnAliases = ArrayHelper.slice( columnAliases, pos, numColumns );
 					result[i] = types[i].nullSafeGet(rs, typeColumnAliases, session, null);
 				}
 				else {
 					result[i] = types[i].nullSafeGet(rs, columnAliases[pos], session, null);
 				}
 				pos += numColumns;
 			}
 		}
 		else {
 			result = toResultRow( row );
 		}
 		return result;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( resultRowLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[ resultRowLength ];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInResultRow[i] ) result[j++] = row[i];
 			}
 			return result;
 		}
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
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
 
 		if ( dialect.useFollowOnLocking() ) {
 			// Dialect prefers to perform locking in a separate step
 			LOG.usingFollowOnLocking();
-			final LockOptions lockOptionsToUse = new LockOptions();
-			lockOptionsToUse.setLockMode( lockOptions.getEffectiveLockMode( "this_" ) );
+
+			final LockMode lockMode = determineFollowOnLockMode( lockOptions );
+			final LockOptions lockOptionsToUse = new LockOptions( lockMode );
 			lockOptionsToUse.setTimeOut( lockOptions.getTimeOut() );
 			lockOptionsToUse.setScope( lockOptions.getScope() );
 
 			afterLoadActions.add(
 					new AfterLoadAction() {
 						@Override
 						public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 							( (Session) session ).buildLockRequest( lockOptionsToUse )
 									.lock( persister.getEntityName(), entity );
 						}
 					}
 			);
 			parameters.setLockOptions( new LockOptions() );
 			return sql;
 		}
 
 		final LockOptions locks = new LockOptions(lockOptions.getLockMode());
 		locks.setScope( lockOptions.getScope());
 		locks.setTimeOut( lockOptions.getTimeOut());
 
 		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 		final String[] drivingSqlAliases = getAliases();
 		for ( int i = 0; i < drivingSqlAliases.length; i++ ) {
 			final LockMode lockMode = lockOptions.getAliasSpecificLockMode( drivingSqlAliases[i] );
 			if ( lockMode != null ) {
 				final Lockable drivingPersister = ( Lockable ) getEntityPersisters()[i];
 				final String rootSqlAlias = drivingPersister.getRootTableAlias( drivingSqlAliases[i] );
 				locks.setAliasSpecificLockMode( rootSqlAlias, lockMode );
 				if ( keyColumnNames != null ) {
 					keyColumnNames.put( rootSqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 				}
 			}
 		}
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
+
+
+	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
+		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
+
+		if ( lockOptions.getAliasLockCount() > 1 ) {
+			// > 1 here because criteria always uses alias map for the root lock mode (under 'this_')
+			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
+		}
+
+		return lockModeToUse;
+	}
+
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		final String[] entityAliases = getAliases();
 		if ( entityAliases == null ) {
 			return null;
 		}
 		final int size = entityAliases.length;
 		LockMode[] lockModesArray = new LockMode[size];
 		for ( int i=0; i<size; i++ ) {
 			LockMode lockMode = lockOptions.getAliasSpecificLockMode( entityAliases[i] );
 			lockModesArray[i] = lockMode==null ? lockOptions.getLockMode() : lockMode;
 		}
 		return lockModesArray;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) {
 		return resolveResultTransformer( resultTransformer ).transformList( results );
 	}
 	
 	protected String getQueryIdentifier() { 
 		return "[CRITERIA] " + getSQLString(); 
 	}
 
 }
