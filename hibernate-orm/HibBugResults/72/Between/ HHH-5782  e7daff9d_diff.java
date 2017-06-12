diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
index d739f86c00..306c65b86a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
@@ -1,608 +1,681 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.ScrollMode;
+import org.hibernate.TransactionException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
 import org.hibernate.engine.jdbc.spi.ConnectionManager;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
-import org.hibernate.engine.jdbc.spi.LogicalConnection;
 import org.hibernate.jdbc.Batcher;
 import org.hibernate.jdbc.Expectation;
 
 /**
  * Encapsulates JDBC Connection management logic needed by Hibernate.
  * <p/>
  * The lifecycle is intended to span a logical series of interactions with the
  * database.  Internally, this means the the lifecycle of the Session.
  *
  * @author Steve Ebersole
  */
 public class ConnectionManagerImpl implements ConnectionManager {
 
 	private static final Logger log = LoggerFactory.getLogger( ConnectionManagerImpl.class );
 
 	public static interface Callback extends ConnectionObserver {
 		public boolean isTransactionInProgress();
 	}
 
 	// TODO: check if it's ok to change the method names in Callback
 
 	private transient SessionFactoryImplementor factory;
 	private transient Connection proxiedConnection;
 	private transient Interceptor interceptor;
 
 	private final Callback callback;
+	private long transactionTimeout = -1;
+	boolean isTransactionTimeoutSet;
 
 	private transient LogicalConnectionImpl logicalConnection;
 
 	/**
 	 * Constructs a ConnectionManager.
 	 * <p/>
 	 * This is the form used internally.
 	 * 
 	 * @param callback An observer for internal state change.
 	 * @param releaseMode The mode by which to release JDBC connections.
 	 * @param suppliedConnection An externally supplied connection.
 	 */ 
 	public ConnectionManagerImpl(
 	        SessionFactoryImplementor factory,
 	        Callback callback,
 	        ConnectionReleaseMode releaseMode,
 	        Connection suppliedConnection,
 	        Interceptor interceptor) {
 		this( factory,
 				callback,
 				interceptor,
 				new LogicalConnectionImpl(
 						suppliedConnection,
 						releaseMode,
 						factory.getJdbcServices(),
 						factory.getStatistics() != null ? factory.getStatisticsImplementor() : null,
 						factory.getSettings().getBatcherFactory()
 				)
 		);
 	}
 
 	/**
 	 * Private constructor used exclusively from custom serialization
 	 */
 	private ConnectionManagerImpl(
 			SessionFactoryImplementor factory,
 			Callback callback,
 			Interceptor interceptor,
 			LogicalConnectionImpl logicalConnection
 	) {
 		this.factory = factory;
 		this.callback = callback;
 		this.interceptor = interceptor;
 		setupConnection( logicalConnection );
 	}
 
 	private void setupConnection(LogicalConnectionImpl logicalConnection) {
 		this.logicalConnection = logicalConnection;
 		this.logicalConnection.addObserver( callback );
 		proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 	}
 
 	/**
 	 * Retrieves the connection currently managed by this ConnectionManager.
 	 * <p/>
 	 * Note, that we may need to obtain a connection to return here if a
 	 * connection has either not yet been obtained (non-UserSuppliedConnectionProvider)
 	 * or has previously been aggressively released (if supported in this environment).
 	 *
 	 * @return The current Connection.
 	 *
 	 * @throws HibernateException Indicates a connection is currently not
 	 * available (we are currently manually disconnected).
 	 */
 	@Override
 	public Connection getConnection() throws HibernateException {
 		return logicalConnection.getConnection();
 	}
 
 	@Override
 	public boolean hasBorrowedConnection() {
 		// used from testsuite
 		return logicalConnection.hasBorrowedConnection();
 	}
 
 	public Connection borrowConnection() {
 		return logicalConnection.borrowConnection();
 	}
 
 	@Override
 	public void releaseBorrowedConnection() {
 		logicalConnection.releaseBorrowedConnection();
 	}
 
 	/**
 	 * Is the connection considered "auto-commit"?
 	 *
 	 * @return True if we either do not have a connection, or the connection
 	 * really is in auto-commit mode.
 	 *
 	 * @throws SQLException Can be thrown by the Connection.isAutoCommit() check.
 	 */
 	public boolean isAutoCommit() throws SQLException {
 		return logicalConnection == null ||
 				! logicalConnection.isOpen() ||
 				! logicalConnection.isPhysicallyConnected() ||
 				logicalConnection.getConnection().getAutoCommit();
 	}
 
 	/**
 	 * Will connections be released after each statement execution?
 	 * <p/>
 	 * Connections will be released after each statement if either:<ul>
 	 * <li>the defined release-mode is {@link ConnectionReleaseMode#AFTER_STATEMENT}; or
 	 * <li>the defined release-mode is {@link ConnectionReleaseMode#AFTER_TRANSACTION} but
 	 * we are in auto-commit mode.
 	 * <p/>
 	 * release-mode = {@link ConnectionReleaseMode#ON_CLOSE} should [b]never[/b] release
 	 * a connection.
 	 *
 	 * @return True if the connections will be released after each statement; false otherwise.
 	 */
 	public boolean isAggressiveRelease() {
 		if ( logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			return true;
 		}
 		else if ( logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION ) {
 			boolean inAutoCommitState;
 			try {
 				inAutoCommitState = isAutoCommit() && ! callback.isTransactionInProgress();
 			}
 			catch( SQLException e ) {
 				// assume we are in an auto-commit state
 				inAutoCommitState = true;
 			}
 			return inAutoCommitState;
 		}
 		return false;
 	}
 
 	/**
 	 * Modified version of {@link #isAggressiveRelease} which does not force a
 	 * transaction check.  This is solely used from our {@link #afterTransaction}
 	 * callback, so no need to do the check; plus it seems to cause problems on
 	 * websphere (god i love websphere ;)
 	 * </p>
 	 * It uses this information to decide if an aggressive release was skipped
 	 * do to open resources, and if so forces a release.
 	 *
 	 * @return True if the connections will be released after each statement; false otherwise.
 	 */
 	private boolean isAggressiveReleaseNoTransactionCheck() {
 		if ( logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			return true;
 		}
 		else {
 			boolean inAutoCommitState;
 			try {
 				inAutoCommitState = isAutoCommit();
 			}
 			catch( SQLException e ) {
 				// assume we are in an auto-commit state
 				inAutoCommitState = true;
 			}
 			return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION && inAutoCommitState;
 		}
 	}
 
 	/**
 	 * Is this ConnectionManager instance "logically" connected.  Meaning
 	 * do we either have a cached connection available or do we have the
 	 * ability to obtain a connection on demand.
 	 *
 	 * @return True if logically connected; false otherwise.
 	 */
 	@Override
 	public boolean isCurrentlyConnected() {
 		return logicalConnection != null && logicalConnection.isLogicallyConnected();
 	}
 
 	/**
 	 * To be called after execution of each JDBC statement.  Used to
 	 * conditionally release the JDBC connection aggressively if
 	 * the configured release mode indicates.
 	 */
 	@Override
 	public void afterStatement() {
 		if ( isAggressiveRelease() ) {
 			logicalConnection.afterStatementExecution();
 		}
 	}
 
 	/**
 	 * To be called after local transaction completion.  Used to conditionally
 	 * release the JDBC connection aggressively if the configured release mode
 	 * indicates.
 	 */
 	public void afterTransaction() {
 		if ( logicalConnection != null ) {
 			if ( isAfterTransactionRelease() || isAggressiveReleaseNoTransactionCheck() ) {
 				logicalConnection.afterTransaction();
 			}
 			else if ( isOnCloseRelease() ) {
 				// log a message about potential connection leaks
 				log.debug( "transaction completed on session with on_close connection release mode; be sure to close the session to release JDBC resources!" );
 			}
 		}
+		unsetTransactionTimeout();		
 	}
 
 	private boolean isAfterTransactionRelease() {
 		return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION;
 	}
 
 	private boolean isOnCloseRelease() {
 		return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.ON_CLOSE;
 	}
 
 	public boolean isLogicallyConnected() {
 		return logicalConnection != null && logicalConnection.isOpen();
 	}
 
 	@Override
 	public void setTransactionTimeout(int seconds) {
-		logicalConnection.setTransactionTimeout( seconds );
+		isTransactionTimeoutSet = true;
+		transactionTimeout = System.currentTimeMillis() / 1000 + seconds;
 	}
 
 	/**
+	 * Unset the transaction timeout, called after the end of a
+	 * transaction.
+	 */
+	private void unsetTransactionTimeout() {
+		isTransactionTimeoutSet = false;
+	}
+
+	private void setStatementTimeout(PreparedStatement preparedStatement) throws SQLException {
+		if ( isTransactionTimeoutSet ) {
+			int timeout = (int) ( transactionTimeout - ( System.currentTimeMillis() / 1000 ) );
+			if ( timeout <=  0) {
+				throw new TransactionException("transaction timeout expired");
+			}
+			else {
+				preparedStatement.setQueryTimeout(timeout);
+			}
+		}
+	}
+
+
+	/**
 	 * To be called after Session completion.  Used to release the JDBC
 	 * connection.
 	 *
 	 * @return The connection mantained here at time of close.  Null if
 	 * there was no connection cached internally.
 	 */
 	@Override
 	public Connection close() {
 		return cleanup();
 	}
 
 	/**
 	 * Manually disconnect the underlying JDBC Connection.  The assumption here
 	 * is that the manager will be reconnected at a later point in time.
 	 *
 	 * @return The connection mantained here at time of disconnect.  Null if
 	 * there was no connection cached internally.
 	 */
 	@Override
 	public Connection manualDisconnect() {
 		if ( ! isLogicallyConnected() ) {
 			throw new IllegalStateException( "cannot manually disconnect because not logically connected." );
 		}
 		return logicalConnection.manualDisconnect();
 	}
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for ConnectionProvider-supplied connections.
 	 */
 	@Override
 	public void manualReconnect() {
 		manualReconnect( null );
 	}
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for user-supplied connections.
 	 */
 	@Override
 	public void manualReconnect(Connection suppliedConnection) {
 		if ( ! isLogicallyConnected() ) {
 			throw new IllegalStateException( "cannot manually disconnect because not logically connected." );
 		}
 		logicalConnection.reconnect( suppliedConnection );
 	}
 
 	/**
 	 * Releases the Connection and cleans up any resources associated with
 	 * that Connection.  This is intended for use:
 	 * 1) at the end of the session
 	 * 2) on a manual disconnect of the session
 	 * 3) from afterTransaction(), in the case of skipped aggressive releasing
 	 *
 	 * @return The released connection.
 	 * @throws HibernateException
 	 */
 	private Connection cleanup() throws HibernateException {
 		if ( logicalConnection == null ) {
 			log.trace( "connection already null in cleanup : no action");
 			return null;
 		}
 		try {
 			log.trace( "performing cleanup" );
 			Connection c = logicalConnection.close();
 			return c;
 		}
 		finally {
 			logicalConnection = null;
 		}
 	}
 
 	/**
 	 * Callback to let us know that a flush is beginning.  We use this fact
 	 * to temporarily circumvent aggressive connection releasing until after
 	 * the flush cycle is complete {@link #flushEnding()}
 	 */
 	@Override
 	public void flushBeginning() {
 		log.trace( "registering flush begin" );
 		logicalConnection.disableReleases();
 	}
 
 	/**
 	 * Callback to let us know that a flush is ending.  We use this fact to
 	 * stop circumventing aggressive releasing connections.
 	 */
 	@Override
 	public void flushEnding() {
 		log.trace( "registering flush end" );
 		logicalConnection.enableReleases();
 		afterStatement();
 	}
 
+	private abstract class StatementPreparer {
+		private final String sql;
+		StatementPreparer(String sql) {
+			this.sql = getSQL( sql );
+		}
+		public String getSqlToPrepare() {
+			return sql;
+		}
+		abstract PreparedStatement doPrepare() throws SQLException;
+		public void afterPrepare(PreparedStatement preparedStatement) throws SQLException {
+			setStatementTimeout( preparedStatement );
+		}
+	}
+
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
 	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, int)}).
 	 */
-	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
-			throws SQLException, HibernateException {
+	public PreparedStatement prepareStatement(String sql, final int autoGeneratedKeys)
+			throws HibernateException {
 		if ( autoGeneratedKeys == PreparedStatement.RETURN_GENERATED_KEYS ) {
 			checkAutoGeneratedKeysSupportEnabled();
 		}
-		executeBatch();
-		return proxiedConnection.prepareStatement(
-					getSQL( sql ),
-					autoGeneratedKeys
-		);
+		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				return proxiedConnection.prepareStatement( getSqlToPrepare(), autoGeneratedKeys );
+			}
+		};
+		return prepareStatement( statementPreparer, true );
 	}
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
 	 */
-	public PreparedStatement prepareStatement(String sql, String[] columnNames)
-			throws SQLException, HibernateException {
+	public PreparedStatement prepareStatement(String sql, final String[] columnNames) {
 		checkAutoGeneratedKeysSupportEnabled();
-		executeBatch();
-		return proxiedConnection.prepareStatement( getSQL( sql ), columnNames );
+		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				return proxiedConnection.prepareStatement( getSqlToPrepare(), columnNames );
+			}
+		};
+		return prepareStatement( statementPreparer, true );
 	}
 
 	private void checkAutoGeneratedKeysSupportEnabled() {
 		if ( ! factory.getSettings().isGetGeneratedKeysEnabled() ) {
 			throw new AssertionFailure("getGeneratedKeys() support is not enabled");
 		}
 	}
 
 	/**
 	 * Get a non-batchable prepared statement to use for selecting. Does not
 	 * result in execution of the current batch.
 	 */
-	public PreparedStatement prepareSelectStatement(String sql)
-			throws SQLException, HibernateException {
-		return proxiedConnection.prepareStatement( getSQL( sql ) );
+	public PreparedStatement prepareSelectStatement(String sql) {
+		return prepareStatement( sql, false, false );
 	}
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 */
-	public PreparedStatement prepareStatement(String sql, boolean isCallable)
-	throws SQLException, HibernateException {
-		return isCallable ? prepareCallableStatement( sql ) : prepareStatement( sql );
+	public PreparedStatement prepareStatement(String sql, final boolean isCallable) {
+		return prepareStatement( sql, isCallable, true );
 	}
 
 	/**
 	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
 	 */
-	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException {
-		executeBatch();
+	public CallableStatement prepareCallableStatement(String sql) {
 		log.trace("preparing callable statement");
-		return CallableStatement.class.cast(
-				proxiedConnection.prepareStatement( getSQL( sql ) )
-		);
+		return CallableStatement.class.cast( prepareStatement( sql, true, true ) );
+	}
+
+	public PreparedStatement prepareStatement(String sql, final boolean isCallable, boolean forceExecuteBatch) {
+		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				return prepareStatementInternal( getSqlToPrepare(), isCallable );
+			}
+		};
+		return prepareStatement( statementPreparer, forceExecuteBatch );
+	}
+
+	private PreparedStatement prepareStatementInternal(String sql, boolean isCallable) throws SQLException {
+		return isCallable ?
+				proxiedConnection.prepareCall( sql ) :
+				proxiedConnection.prepareStatement( sql );
+	}
+
+	private PreparedStatement prepareScrollableStatementInternal(String sql,
+																 ScrollMode scrollMode,
+																 boolean isCallable) throws SQLException {
+		return isCallable ?
+				proxiedConnection.prepareCall(
+						sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
+				) :
+				proxiedConnection.prepareStatement(
+						sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
+				);
 	}
 
 	/**
 	 * Get a batchable prepared statement to use for inserting / deleting / updating
 	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
 	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
 	 * statement explicitly.
 	 * @see org.hibernate.jdbc.Batcher#addToBatch
 	 */
-	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable)
-			throws SQLException, HibernateException {
+	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable) {
 		String batchUpdateSQL = getSQL( sql );
+
 		PreparedStatement batchUpdate = getBatcher().getStatement( batchUpdateSQL );
 		if ( batchUpdate == null ) {
-			batchUpdate = prepareStatement( batchUpdateSQL, isCallable ); // calls executeBatch()
+			batchUpdate = prepareStatement( batchUpdateSQL, isCallable, true ); // calls executeBatch()
 			getBatcher().setStatement( batchUpdateSQL, batchUpdate );
 		}
 		else {
 			log.debug( "reusing prepared statement" );
 			factory.getJdbcServices().getSqlStatementLogger().logStatement( batchUpdateSQL );
 		}
 		return batchUpdate;
 	}
 
 	private Batcher getBatcher() {
 		return logicalConnection.getBatcher();
 	}
 
-	private PreparedStatement prepareStatement(String sql)
-	throws SQLException, HibernateException {
-		executeBatch();
-		return proxiedConnection.prepareStatement( getSQL( sql ) );
-	}
-
 	/**
 	 * Get a prepared statement for use in loading / querying. If not explicitly
 	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
 	 * session is closed or disconnected.
 	 */
 	public PreparedStatement prepareQueryStatement(
 			String sql,
-			boolean isCallable) throws SQLException, HibernateException {
-		sql = getSQL( sql );
-		PreparedStatement result = (
-				isCallable ?
-						proxiedConnection.prepareCall(sql ) :
-		                proxiedConnection.prepareStatement( sql )
-				);
-		setStatementFetchSize( result );
-		logicalConnection.getResourceRegistry().registerLastQuery( result );
-		return result;
+			final boolean isScrollable,
+			final ScrollMode scrollMode,
+			final boolean isCallable
+	) {
+		if ( isScrollable && ! factory.getSettings().isScrollableResultSetsEnabled() ) {
+			throw new AssertionFailure("scrollable result sets are not enabled");
+		}
+		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				PreparedStatement ps =
+						isScrollable ?
+								prepareScrollableStatementInternal( getSqlToPrepare(), scrollMode, isCallable ) :
+								prepareStatementInternal( getSqlToPrepare(), isCallable )
+						;
+				return ps;
+			}
+			public void afterPrepare(PreparedStatement preparedStatement) throws SQLException {
+				super.afterPrepare( preparedStatement );
+				setStatementFetchSize( preparedStatement, getSqlToPrepare() );
+				logicalConnection.getResourceRegistry().registerLastQuery( preparedStatement );
+			}
+		};
+		return prepareStatement( statementPreparer, false );
+	}
+
+	private void setStatementFetchSize(PreparedStatement statement, String sql) throws SQLException {
+		if ( factory.getSettings().getJdbcFetchSize() != null ) {
+			statement.setFetchSize( factory.getSettings().getJdbcFetchSize() );
+		}
+	}
+
+	private PreparedStatement prepareStatement(StatementPreparer preparer, boolean forceExecuteBatch) {
+		if ( forceExecuteBatch ) {
+			executeBatch();
+		}
+		try {
+			PreparedStatement ps = preparer.doPrepare();
+			preparer.afterPrepare( ps );
+			return ps;
+		}
+		catch ( SQLException sqle ) {
+			log.error( "sqlexception escaped proxy", sqle );
+			throw logicalConnection.getJdbcServices().getSqlExceptionHelper().convert(
+					sqle, "could not prepare statement", preparer.getSqlToPrepare()
+			);
+		}
 	}
 
 	/**
 	 * Cancel the current query statement
 	 */
 	public void cancelLastQuery() throws HibernateException {
 		logicalConnection.getResourceRegistry().cancelLastQuery();
 	}
 
-	public PreparedStatement prepareScrollableQueryStatement(
-			String sql,
-	        ScrollMode scrollMode,
-			boolean isCallable) throws SQLException, HibernateException {
-		if ( ! factory.getSettings().isScrollableResultSetsEnabled() ) {
-			throw new AssertionFailure("scrollable result sets are not enabled");
-		}
-		sql = getSQL( sql );
-		PreparedStatement result = (
-				isCallable ?
-						proxiedConnection.prepareCall(
-								sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
-						) :
-		                proxiedConnection.prepareStatement(
-								sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
-						)
-				);
-		setStatementFetchSize( result );
-		logicalConnection.getResourceRegistry().registerLastQuery( result );
-		return result;
-	}
-
 	public void abortBatch(SQLException sqle) {
 		getBatcher().abortBatch( sqle );
 	}
 
-	public void addToBatch(Expectation expectation )  throws SQLException, HibernateException {
-		getBatcher().addToBatch( expectation );
+	public void addToBatch(Expectation expectation ) {
+		try {
+			getBatcher().addToBatch( expectation );
+		}
+		catch (SQLException sqle) {
+			throw logicalConnection.getJdbcServices().getSqlExceptionHelper().convert(
+					sqle, "could not add to batch statement" );
+		}
 	}
 
 	public void executeBatch() throws HibernateException {
 		getBatcher().executeBatch();
 	}
 
 	private String getSQL(String sql) {
 		sql = interceptor.onPrepareStatement( sql );
 		if ( sql==null || sql.length() == 0 ) {
 			throw new AssertionFailure( "Interceptor.onPrepareStatement() returned null or empty string." );
 		}
 		return sql;
 	}
 
-	private void setStatementFetchSize(PreparedStatement statement) throws SQLException {
-		if ( factory.getSettings().getJdbcFetchSize() !=null ) {
-			statement.setFetchSize( factory.getSettings().getJdbcFetchSize().intValue() );
-		}
-	}
-
 	public boolean isReadyForSerialization() {
 		return logicalConnection == null ? true : logicalConnection.isReadyForSerialization();
 	}
 
 	/**
 	 * Used during serialization.
 	 *
 	 * @param oos The stream to which we are being written.
 	 * @throws IOException Indicates an I/O error writing to the stream
 	 */
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if ( !isReadyForSerialization() ) {
 			throw new IllegalStateException( "Cannot serialize a ConnectionManager while connected" );
 		}
 		oos.defaultWriteObject();
 	}
 
 	/**
 	 * Used during deserialization.
 	 *
 	 * @param ois The stream from which we are being read.
 	 * @throws IOException Indicates an I/O error reading the stream
 	 * @throws ClassNotFoundException Indicates resource class resolution.
 	 */
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		logicalConnection.serialize( oos );
 	}
 
 	public static ConnectionManagerImpl deserialize(
 			ObjectInputStream ois,
 	        SessionFactoryImplementor factory,
 	        Interceptor interceptor,
 	        ConnectionReleaseMode connectionReleaseMode,
 	        Callback callback) throws IOException {
 		return new ConnectionManagerImpl(
 				factory,
 		        callback,
 				interceptor,
 				LogicalConnectionImpl.deserialize(
 						ois,
 						factory.getJdbcServices(),
 						factory.getStatistics() != null ? factory.getStatisticsImplementor() : null,
 						connectionReleaseMode,
 						factory.getSettings().getBatcherFactory()
 				)
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
index 90c3bed0e6..35a8f091b8 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
@@ -1,489 +1,452 @@
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
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.jdbc.Batcher;
 import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.jdbc.BorrowedConnectionProxy;
 import org.hibernate.stat.StatisticsImplementor;
 
 /**
  * LogicalConnectionImpl implementation
  *
  * @author Steve Ebersole
  */
 public class LogicalConnectionImpl implements LogicalConnectionImplementor {
 	private static final Logger log = LoggerFactory.getLogger( LogicalConnectionImpl.class );
 
 	private Connection physicalConnection;
 	private Connection borrowedConnection;
 
 	private final ConnectionReleaseMode connectionReleaseMode;
 	private final JdbcServices jdbcServices;
 	private final StatisticsImplementor statisticsImplementor;
 	private final JdbcResourceRegistryImpl jdbcResourceRegistry;
 	private final List<ConnectionObserver> observers = new ArrayList<ConnectionObserver>();
 
 	private boolean releasesEnabled = true;
-	private long transactionTimeout = -1;
-	boolean isTransactionTimeoutSet;
 
 	private final boolean isUserSuppliedConnection;
 
 	private boolean isClosed;
 
 	public LogicalConnectionImpl(Connection userSuppliedConnection,
 								 ConnectionReleaseMode connectionReleaseMode,
 								 JdbcServices jdbcServices,
 								 StatisticsImplementor statisticsImplementor,
 								 BatcherFactory batcherFactory
 	) {
 		this.jdbcServices = jdbcServices;
 		this.statisticsImplementor = statisticsImplementor;
 		this.physicalConnection = userSuppliedConnection;
 		this.connectionReleaseMode =
 				determineConnectionReleaseMode(
 						jdbcServices, userSuppliedConnection != null, connectionReleaseMode
 				);
 		this.jdbcResourceRegistry =
 				new JdbcResourceRegistryImpl(
 						getJdbcServices().getSqlExceptionHelper(),
 						batcherFactory
 				);
 
 		this.isUserSuppliedConnection = ( userSuppliedConnection != null );
 		this.isClosed = false;
 	}
 
 	// used for deserialization
 	private LogicalConnectionImpl(ConnectionReleaseMode connectionReleaseMode,
 								  JdbcServices jdbcServices,
 								  StatisticsImplementor statisticsImplementor,
 								  BatcherFactory batcherFactory,
 								  boolean isUserSuppliedConnection,
 								  boolean isClosed) {
 		this.connectionReleaseMode = determineConnectionReleaseMode(
 				jdbcServices, isUserSuppliedConnection, connectionReleaseMode
 		);
 		this.jdbcServices = jdbcServices;
 		this.statisticsImplementor = statisticsImplementor;
 		this.jdbcResourceRegistry =
 				new JdbcResourceRegistryImpl(
 						getJdbcServices().getSqlExceptionHelper(),
 						batcherFactory
 				);
 
 		this.isUserSuppliedConnection = isUserSuppliedConnection;
 		this.isClosed = isClosed;
 	}
 
 	private static ConnectionReleaseMode determineConnectionReleaseMode(JdbcServices jdbcServices,
 																		boolean isUserSuppliedConnection,
 																		ConnectionReleaseMode connectionReleaseMode) {
 		if ( isUserSuppliedConnection ) {
 			return ConnectionReleaseMode.ON_CLOSE;
 		}
 		else if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 				! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
 			log.debug( "connection provider reports to not support aggressive release; overriding" );
 			return ConnectionReleaseMode.AFTER_TRANSACTION;
 		}
 		else {
 			return connectionReleaseMode;
 		}
 	}
 
 	/**
-	 * Set the transaction timeout to <tt>seconds</tt> later
-	 * than the current system time.
-	 */
-	public void setTransactionTimeout(int seconds) {
-		isTransactionTimeoutSet = true;
-		transactionTimeout = System.currentTimeMillis() / 1000 + seconds;
-	}
-
-	/**
-	 * Unset the transaction timeout, called after the end of a
-	 * transaction.
-	 */
-	private void unsetTransactionTimeout() {
-		isTransactionTimeoutSet = false;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isTransactionTimeoutSet() {
-		return isTransactionTimeoutSet;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public long getTransactionTimeout() throws HibernateException {
-		if ( isTransactionTimeoutSet ) {
-			throw new HibernateException( "transaction timeout has not been set." );
-		}
-		return transactionTimeout;
-	}
-
-	/**
 	 * {@inheritDoc}
 	 */
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public StatisticsImplementor getStatisticsImplementor() {
 		return statisticsImplementor;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public JdbcResourceRegistry getResourceRegistry() {
 		return jdbcResourceRegistry;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void addObserver(ConnectionObserver observer) {
 		observers.add( observer );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isOpen() {
 		return !isClosed;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isLogicallyConnected() {
 		return isUserSuppliedConnection ?
 				isPhysicallyConnected() :
 				isOpen();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isPhysicallyConnected() {
 		return physicalConnection != null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection getConnection() throws HibernateException {
 		if ( isClosed ) {
 			throw new HibernateException( "Logical connection is closed" );
 		}
 		if ( physicalConnection == null ) {
 			if ( isUserSuppliedConnection ) {
 				// should never happen
 				throw new HibernateException( "User-supplied connection was null" );
 			}
 			obtainConnection();
 		}
 		return physicalConnection;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection close() {
 		Connection c = physicalConnection;
 		try {
 			releaseBorrowedConnection();
 			log.trace( "closing logical connection" );
 			if ( !isUserSuppliedConnection && physicalConnection != null ) {
 				jdbcResourceRegistry.close();
 				releaseConnection();
 			}
 			return c;
 		}
 		finally {
 			// no matter what
 			physicalConnection = null;
 			isClosed = true;
 			log.trace( "logical connection closed" );
 			for ( ConnectionObserver observer : observers ) {
 				observer.logicalConnectionClosed();
 			}
 		}			
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	public Batcher getBatcher() {
 		return jdbcResourceRegistry.getBatcher();
 	}
 
 	public boolean hasBorrowedConnection() {
 		return borrowedConnection != null;
 	}
 
 	public Connection borrowConnection() {
 		if ( isClosed ) {
 			throw new HibernateException( "connection has been closed" );
 		}
 		if ( isUserSuppliedConnection ) {
 			return physicalConnection;
 		}
 		else {
 			if ( borrowedConnection == null ) {
 				borrowedConnection = BorrowedConnectionProxy.generateProxy( this );
 			}
 			return borrowedConnection;
 		}
 	}
 
 	public void releaseBorrowedConnection() {
 		if ( borrowedConnection != null ) {
 			try {
 				BorrowedConnectionProxy.renderUnuseable( borrowedConnection );
 			}
 			finally {
 				borrowedConnection = null;
 			}
 		}
 	}
 
 	public void afterStatementExecution() {
 		log.trace( "starting after statement execution processing [{}]", connectionReleaseMode );
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			if ( ! releasesEnabled ) {
 				log.debug( "skipping aggressive release due to manual disabling" );
 				return;
 			}
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
 				log.debug( "skipping aggressive release due to registered resources" );
 				return;
 			}
 			else if ( borrowedConnection != null ) {
 				log.debug( "skipping aggresive-release due to borrowed connection" );
 			}			
 			releaseConnection();
 		}
 	}
 
 	public void afterTransaction() {
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ||
 				connectionReleaseMode == ConnectionReleaseMode.AFTER_TRANSACTION ) {
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
 				log.info( "forcing container resource cleanup on transaction completion" );
 				jdbcResourceRegistry.releaseResources();
 			}
 			aggressiveRelease();
 		}
-		unsetTransactionTimeout();
 	}
 
 	public void disableReleases() {
 		log.trace( "disabling releases" );
 		releasesEnabled = false;
 	}
 
 	public void enableReleases() {
 		log.trace( "(re)enabling releases" );
 		releasesEnabled = true;
 		//FIXME: uncomment after new batch stuff is integrated!!!
 		//afterStatementExecution();
 	}
 
 	/**
 	 * Force aggresive release of the underlying connection.
 	 */
 	public void aggressiveRelease() {
 		if ( isUserSuppliedConnection ) {
 			log.debug( "cannot aggressively release user-supplied connection; skipping" );
 		}
 		else {
 			log.debug( "aggressively releasing JDBC connection" );
 			if ( physicalConnection != null ) {
 				releaseConnection();
 			}
 		}
 	}
 
 
 	/**
 	 * Pysically opens a JDBC Connection.
 	 *
 	 * @throws org.hibernate.JDBCException Indicates problem opening a connection
 	 */
 	private void obtainConnection() throws JDBCException {
 		log.debug( "obtaining JDBC connection" );
 		try {
 			physicalConnection = getJdbcServices().getConnectionProvider().getConnection();
 			for ( ConnectionObserver observer : observers ) {
 				observer.physicalConnectionObtained( physicalConnection );
 			}
 			log.debug( "obtained JDBC connection" );
 		}
 		catch ( SQLException sqle) {
 			throw getJdbcServices().getSqlExceptionHelper().convert( sqle, "Could not open connection" );
 		}
 	}
 
 	/**
 	 * Physically closes the JDBC Connection.
 	 *
 	 * @throws JDBCException Indicates problem closing a connection
 	 */
 	private void releaseConnection() throws JDBCException {
 		log.debug( "releasing JDBC connection" );
 		if ( physicalConnection == null ) {
 			return;
 		}
 		try {
 			if ( ! physicalConnection.isClosed() ) {
 				getJdbcServices().getSqlExceptionHelper().logAndClearWarnings( physicalConnection );
 			}
 			if ( !isUserSuppliedConnection ) {
 				getJdbcServices().getConnectionProvider().closeConnection( physicalConnection );
 			}
 			log.debug( "released JDBC connection" );
 		}
 		catch (SQLException sqle) {
 			throw getJdbcServices().getSqlExceptionHelper().convert( sqle, "Could not close connection" );
 		}
 		finally {
 			physicalConnection = null;
 		}
 		log.debug( "released JDBC connection" );
 		for ( ConnectionObserver observer : observers ) {
 			observer.physicalConnectionReleased();
 		}
 	}
 
 	/**
 	 * Manually disconnect the underlying JDBC Connection.  The assumption here
 	 * is that the manager will be reconnected at a later point in time.
 	 *
 	 * @return The connection mantained here at time of disconnect.  Null if
 	 * there was no connection cached internally.
 	 */
 	public Connection manualDisconnect() {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually disconnect because logical connection is already closed" );
 		}
 		Connection c = physicalConnection;
 		jdbcResourceRegistry.releaseResources();
 		releaseConnection();
 		return c;
 	}
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for user-supplied connections.
 	 */
 	public void reconnect(Connection suppliedConnection) {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually reconnect because logical connection is already closed" );
 		}
 		if ( isUserSuppliedConnection ) {
 			if ( suppliedConnection == null ) {
 				throw new IllegalArgumentException( "cannot reconnect a null user-supplied connection" );
 			}
 			else if ( suppliedConnection == physicalConnection ) {
 				log.warn( "reconnecting the same connection that is already connected; should this connection have been disconnected?" );
 			}
 			else if ( physicalConnection != null ) {
 				throw new IllegalArgumentException(
 						"cannot reconnect to a new user-supplied connection because currently connected; must disconnect before reconnecting."
 				);
 			}
 			physicalConnection = suppliedConnection;
 			log.debug( "reconnected JDBC connection" );
 		}
 		else {
 			if ( suppliedConnection != null ) {
 				throw new IllegalStateException( "unexpected user-supplied connection" );
 			}
 			log.debug( "called reconnect() with null connection (not user-supplied)" );
 		}
 	}
 
 	public boolean isReadyForSerialization() {
 		return isUserSuppliedConnection ?
 				! isPhysicallyConnected() :
 				! getResourceRegistry().hasRegisteredResources()
 				;
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeBoolean( isUserSuppliedConnection );
 		oos.writeBoolean( isClosed );
 	}
 
 	public static LogicalConnectionImpl deserialize(ObjectInputStream ois,
 													JdbcServices jdbcServices,
 													StatisticsImplementor statisticsImplementor,
 													ConnectionReleaseMode connectionReleaseMode,
 													BatcherFactory batcherFactory
 	) throws IOException {
 		return new LogicalConnectionImpl(
 				connectionReleaseMode,
 				jdbcServices,
 				statisticsImplementor,
 				batcherFactory,
 				ois.readBoolean(),
 				ois.readBoolean()
 		);
  	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java
index 48cbac9c27..ef5e0fcf47 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java
@@ -1,246 +1,231 @@
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
 package org.hibernate.engine.jdbc.internal.proxy;
 
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.TransactionException;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.stat.StatisticsImplementor;
 
 /**
  * The {@link InvocationHandler} for intercepting messages to {@link java.sql.Connection} proxies.
  *
  * @author Steve Ebersole
  */
 public class ConnectionProxyHandler extends AbstractProxyHandler implements InvocationHandler, ConnectionObserver {
 	private static final Logger log = LoggerFactory.getLogger( ConnectionProxyHandler.class );
 
 	private LogicalConnectionImplementor logicalConnection;
 
 	public ConnectionProxyHandler(LogicalConnectionImplementor logicalConnection) {
 		super( logicalConnection.hashCode() );
 		this.logicalConnection = logicalConnection;
 		this.logicalConnection.addObserver( this );
 	}
 
 	/**
 	 * Access to our logical connection.
 	 *
 	 * @return the logical connection
 	 */
 	protected LogicalConnectionImplementor getLogicalConnection() {
 		errorIfInvalid();
 		return logicalConnection;
 	}
 
 	/**
 	 * Get reference to physical connection.
 	 * <p/>
 	 * NOTE : be sure this handler is still valid before calling!
 	 *
 	 * @return The physical connection
 	 */
 	private Connection extractPhysicalConnection() {
 		return logicalConnection.getConnection();
 	}
 
 	/**
 	 * Provide access to JDBCServices.
 	 * <p/>
 	 * NOTE : package-protected
 	 *
 	 * @return JDBCServices
 	 */
 	JdbcServices getJdbcServices() {
 		return logicalConnection.getJdbcServices();
 	}
 
 	/**
 	 * Provide access to JDBCContainer.
 	 * <p/>
 	 * NOTE : package-protected
 	 *
 	 * @return JDBCContainer
 	 */
 	JdbcResourceRegistry getResourceRegistry() {
 		return logicalConnection.getResourceRegistry();
 	}
 
 	protected Object continueInvocation(Object proxy, Method method, Object[] args) throws Throwable {
 		String methodName = method.getName();
 		log.trace( "Handling invocation of connection method [{}]", methodName );
 
 		// other methods allowed while invalid ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( "close".equals( methodName ) ) {
 			explicitClose();
 			return null;
 		}
 
 		errorIfInvalid();
 
 		// handle the JDBC 4 Wrapper#isWrapperFor and Wrapper#unwrap calls
 		//		these cause problems to the whole proxy scheme though as we need to return the raw objects
 		if ( "isWrapperFor".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( extractPhysicalConnection(), args );
 		}
 		if ( "unwrap".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( extractPhysicalConnection(), args );
 		}
 
 		if ( "getWrappedObject".equals( methodName ) ) {
 			return extractPhysicalConnection();
 		}
 
 		try {
 			Object result = method.invoke( extractPhysicalConnection(), args );
 			result = postProcess( result, proxy, method, args );
 
 			return result;
 		}
 		catch( InvocationTargetException e ) {
 			Throwable realException = e.getTargetException();
 			if ( SQLException.class.isInstance( realException ) ) {
 				throw logicalConnection.getJdbcServices().getSqlExceptionHelper()
 						.convert( ( SQLException ) realException, realException.getMessage() );
 			}
 			else {
 				throw realException;
 			}
 		}
 	}
 
 	private Object postProcess(Object result, Object proxy, Method method, Object[] args) throws SQLException {
 		String methodName = method.getName();
 		Object wrapped = result;
 		if ( "createStatement".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildStatement(
 					(Statement) result,
 					this,
 					( Connection ) proxy
 			);
 			postProcessStatement( ( Statement ) wrapped );
 		}
 		else if ( "prepareStatement".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildPreparedStatement(
 					( String ) args[0],
 					(PreparedStatement) result,
 					this,
 					( Connection ) proxy
 			);
 			postProcessPreparedStatement( ( Statement ) wrapped );
 		}
 		else if ( "prepareCall".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildCallableStatement(
 					( String ) args[0],
 					(CallableStatement) result,
 					this,
 					( Connection ) proxy
 			);
 			postProcessPreparedStatement( ( Statement ) wrapped );
 		}
 		else if ( "getMetaData".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildDatabaseMetaData( (DatabaseMetaData) result, this, ( Connection ) proxy );
 		}
 		return wrapped;
 	}
 
 	private void postProcessStatement(Statement statement) throws SQLException {
-		setTimeout( statement );
 		getResourceRegistry().register( statement );
 	}
 
 	private void postProcessPreparedStatement(Statement statement) throws SQLException  {
 		if ( getStatisticsImplementorOrNull() != null ) {
 			getStatisticsImplementorOrNull().prepareStatement();
 		}
 		postProcessStatement( statement );
 	}
 
 	private void explicitClose() {
 		if ( isValid() ) {
 			invalidateHandle();
 		}
 	}
 
 	private void invalidateHandle() {
 		log.trace( "Invalidating connection handle" );
 		logicalConnection = null;
 		invalidate();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void physicalConnectionObtained(Connection connection) {
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void physicalConnectionReleased() {
 		log.info( "logical connection releasing its physical connection");
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void logicalConnectionClosed() {
 		log.info( "*** logical connection closed ***" );
 		invalidateHandle();
 	}
 
 	/* package-protected */
 	StatisticsImplementor getStatisticsImplementorOrNull() {
 		return getLogicalConnection().getStatisticsImplementor();
 	}
-
-	private void setTimeout(Statement result) throws SQLException {
-		if ( logicalConnection.isTransactionTimeoutSet() ) {
-			int timeout = (int) ( logicalConnection.getTransactionTimeout() - ( System.currentTimeMillis() / 1000 ) );
-			if (timeout<=0) {
-				throw new TransactionException("transaction timeout expired");
-			}
-			else {
-				result.setQueryTimeout(timeout);
-			}
-		}
-	}
-
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
index 728a4b0966..733fa01891 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
@@ -1,195 +1,188 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.ScrollMode;
 import org.hibernate.jdbc.Expectation;
 
 /**
  * Encapsulates JDBC Connection management SPI.
  * <p/>
  * The lifecycle is intended to span a logical series of interactions with the
  * database.  Internally, this means the the lifecycle of the Session.
  *
  * @author Gail Badner
  */
 public interface ConnectionManager extends Serializable {
 
 	/**
 	 * Retrieves the connection currently managed by this ConnectionManager.
 	 * <p/>
 	 * Note, that we may need to obtain a connection to return here if a
 	 * connection has either not yet been obtained (non-UserSuppliedConnectionProvider)
 	 * or has previously been aggressively released (if supported in this environment).
 	 *
 	 * @return The current Connection.
 	 *
 	 * @throws HibernateException Indicates a connection is currently not
 	 * available (we are currently manually disconnected).
 	 */
-	Connection getConnection() throws HibernateException;
+	Connection getConnection();
 
 	// TODO: should this be removd from the SPI?
 	boolean hasBorrowedConnection();
 
 	// TODO: should this be removd from the SPI?
 	void releaseBorrowedConnection();
 
 	/**
 	 * Is this ConnectionManager instance "logically" connected.  Meaning
 	 * do we either have a cached connection available or do we have the
 	 * ability to obtain a connection on demand.
 	 *
 	 * @return True if logically connected; false otherwise.
 	 */
 	boolean isCurrentlyConnected();
 
 	/**
 	 * To be called after execution of each JDBC statement.  Used to
 	 * conditionally release the JDBC connection aggressively if
 	 * the configured release mode indicates.
 	 */
 	void afterStatement();
 
 	void setTransactionTimeout(int seconds);
 
 	/**
 	 * To be called after Session completion.  Used to release the JDBC
 	 * connection.
 	 *
 	 * @return The connection mantained here at time of close.  Null if
 	 * there was no connection cached internally.
 	 */
 	Connection close();
 
 	/**
 	 * Manually disconnect the underlying JDBC Connection.  The assumption here
 	 * is that the manager will be reconnected at a later point in time.
 	 *
 	 * @return The connection mantained here at time of disconnect.  Null if
 	 * there was no connection cached internally.
 	 */
 	Connection manualDisconnect();
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for ConnectionProvider-supplied connections.
 	 */
 	void manualReconnect();
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for user-supplied connections.
 	 */
 	void manualReconnect(Connection suppliedConnection);
 
 	/**
 	 * Callback to let us know that a flush is beginning.  We use this fact
 	 * to temporarily circumvent aggressive connection releasing until after
 	 * the flush cycle is complete {@link #flushEnding()}
 	 */
 	void flushBeginning();
 
 	/**
 	 * Callback to let us know that a flush is ending.  We use this fact to
 	 * stop circumventing aggressive releasing connections.
 	 */
 	void flushEnding();
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
 	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, int)}).
 	 */
-	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
-			throws SQLException, HibernateException;
+	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys);
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
 	 */
-	public PreparedStatement prepareStatement(String sql, String[] columnNames)
-			throws SQLException, HibernateException;
+	public PreparedStatement prepareStatement(String sql, String[] columnNames);
 
 	/**
 	 * Get a non-batchable prepared statement to use for selecting. Does not
 	 * result in execution of the current batch.
 	 */
-	public PreparedStatement prepareSelectStatement(String sql)
-			throws SQLException, HibernateException;
+	public PreparedStatement prepareSelectStatement(String sql);
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 */
-	public PreparedStatement prepareStatement(String sql, boolean isCallable)
-	throws SQLException, HibernateException ;
+	public PreparedStatement prepareStatement(String sql, boolean isCallable);
 
 	/**
 	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
 	 */
-	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException;
+	public CallableStatement prepareCallableStatement(String sql);
 
 	/**
 	 * Get a batchable prepared statement to use for inserting / deleting / updating
 	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
 	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
 	 * statement explicitly.
 	 * @see org.hibernate.jdbc.Batcher#addToBatch
 	 */
-	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable) throws SQLException, HibernateException;
+	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable);
 
 	/**
 	 * Get a prepared statement for use in loading / querying. If not explicitly
 	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
 	 * session is closed or disconnected.
 	 */
 	public PreparedStatement prepareQueryStatement(
 			String sql,
-			boolean isCallable) throws SQLException, HibernateException;
+			boolean isScrollable,
+			ScrollMode scrollMode,
+			boolean isCallable);
 	/**
 	 * Cancel the current query statement
 	 */
-	public void cancelLastQuery() throws HibernateException;
-
-	public PreparedStatement prepareScrollableQueryStatement(
-			String sql,
-	        ScrollMode scrollMode,
-			boolean isCallable) throws SQLException, HibernateException;
+	public void cancelLastQuery();
 
 	public void abortBatch(SQLException sqle);
 
-	public void addToBatch(Expectation expectation )  throws SQLException, HibernateException;
+	public void addToBatch(Expectation expectation );
 
-	public void executeBatch() throws HibernateException;
+	public void executeBatch();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
index a843e12c28..288554ff35 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
@@ -1,109 +1,95 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.stat.StatisticsImplementor;
 
 /**
  * The "internal" contract for LogicalConnection
  *
  * @author Steve Ebersole
  */
 public interface LogicalConnectionImplementor extends LogicalConnection {
 	/**
 	 * Obtains the JDBC services associated with this logical connection.
 	 *
 	 * @return JDBC services
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Obtains the statistics implementor.
 	 *
 	 * @return the statistics implementor
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 
 	/**
-	 * Is the transaction timeout set?
-	 *
-	 * @return true, if the transaction timeout is set; false otherwise
-	 */
-	public boolean isTransactionTimeoutSet();
-
-	/**
-	 * Gets the transaction timeout.
-	 *
-	 * @return the transaction time out
-	 */
-	public long getTransactionTimeout();
-
-	/**
 	 * Obtains the JDBC resource registry associated with this logical connection.
 	 *
 	 * @return The JDBC resource registry.
 	 */
 	public JdbcResourceRegistry getResourceRegistry();
 
 	/**
 	 * Add an observer interested in notification of connection events.
 	 *
 	 * @param observer The observer.
 	 */
 	public void addObserver(ConnectionObserver observer);
 
 	/**
 	 * The release mode under which this logical connection is operating.
 	 *
 	 * @return the release mode.
 	 */
 	public ConnectionReleaseMode getConnectionReleaseMode();
 
 	/**
 	 * Used to signify that a statement has completed execution which may
 	 * indicate that this logical connection need to perform an
 	 * aggressive release of its physical connection.
 	 */
 	public void afterStatementExecution();
 
 	/**
 	 * Used to signify that a transaction has completed which may indicate
 	 * that this logical connection need to perform an aggressive release
 	 * of its physical connection.
 	 */
 	public void afterTransaction();
 
 	/**
 	 * Manually (and temporarily) circumvent aggressive release processing.
 	 */
 	public void disableReleases();
 
 	/**
 	 * Re-enable aggressive release processing (after a prior {@link #disableReleases()} call.
 	 */
 	public void enableReleases();
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 02c9e6d8cd..b0e8d6ca91 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -752,1891 +752,1892 @@ public abstract class Loader {
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : new EntityKey( resolvedId, persisters[i], session.getEntityMode() );
 		}
 	}
 
 	private Serializable determineResultId(SessionImplementor session, Serializable optionalId, Type idType, Serializable resolvedId) {
 		final boolean idIsResultId = optionalId != null
 				&& resolvedId != null
 				&& idType.isEqual( optionalId, resolvedId, session.getEntityMode(), factory );
 		final Serializable resultId = idIsResultId ? optionalId : resolvedId;
 		return resultId;
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
 		final int maxRows = hasMaxRows( selection ) ?
 				selection.getMaxRows().intValue() :
 				Integer.MAX_VALUE;
 
 		final int entitySpan = getEntityPersisters().length;
 
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 		final ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), selection, session );
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final List results = new ArrayList();
 
 		try {
 
 			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 
 			EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 
 			if ( log.isTraceEnabled() ) log.trace( "processing result set" );
 
 			int count;
 			for ( count = 0; count < maxRows && rs.next(); count++ ) {
 				
 				if ( log.isTraceEnabled() ) log.debug("result set row: " + count);
 
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
 
 			if ( log.isTraceEnabled() ) {
 				log.trace( "done processing result set (" + count + " rows)" );
 			}
 
 		}
 		finally {
 			st.close();
 		}
 
 		initializeEntitiesAndCollections( hydratedObjects, rs, session, queryParameters.isReadOnly( session ) );
 
 		if ( createSubselects ) createSubselects( subselectResultKeys, queryParameters, session );
 
 		return results; //getResultList(results);
 
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
 			final boolean readOnly) 
 	throws HibernateException {
 		
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
 			if ( log.isTraceEnabled() ) {
 				log.trace( "total objects hydrated: " + hydratedObjectsSize );
 			}
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
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(Object[] row,
 														 ResultSet rs,
 														 SessionImplementor session)
 			throws SQLException, HibernateException {
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
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"found row of collection: " +
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) 
 					);
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
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"result set contains (possibly empty) collection: " +
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) 
 					);
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
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 	
 					if ( log.isDebugEnabled() ) {
 						log.debug( 
 								"result set contains (possibly empty) collection: " +
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) 
 							);
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
 					idType.isEqual( id, resultId, session.getEntityMode(), factory );
 			
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ?
 				null :
 				new EntityKey( resultId, persister, session.getEntityMode() );
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
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"result row: " + 
 					StringHelper.toString( keys ) 
 				);
 		}
 
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
 		if ( !persister.isInstance( object, session.getEntityMode() ) ) {
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
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( 
 					"Initializing object from ResultSet: " + 
 					MessageHelper.infoString( persister, id, getFactory() ) 
 				);
 		}
 		
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
 						session.getEntityMode(), session.getFactory()
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
 
 		final int firstRow = getFirstRow( selection );
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
 
 	private static boolean hasMaxRows(RowSelection selection) {
 		return selection != null && selection.getMaxRows() != null;
 	}
 
 	private static int getFirstRow(RowSelection selection) {
 		if ( selection == null || selection.getFirstRow() == null ) {
 			return 0;
 		}
 		else {
 			return selection.getFirstRow().intValue();
 		}
 	}
 
 	private int interpretFirstRow(int zeroBasedFirstResult) {
 		return getFactory().getDialect().convertToFirstRowValue( zeroBasedFirstResult );
 	}
 
 	/**
 	 * Should we pre-process the SQL string, adding a dialect-specific
 	 * LIMIT clause.
 	 */
 	private static boolean useLimit(final RowSelection selection, final Dialect dialect) {
 		return dialect.supportsLimit() && hasMaxRows( selection );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        final QueryParameters queryParameters,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		queryParameters.processFilters( getSQLString(), session );
 		String sql = queryParameters.getFilteredSQL();
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = useLimit( selection, dialect );
 		boolean hasFirstRow = getFirstRow( selection ) > 0;
 		boolean useOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 		
 		boolean useScrollableResultSetToSkip = hasFirstRow &&
 				!useOffset &&
 				getFactory().getSettings().isScrollableResultSetsEnabled();
 		ScrollMode scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;
 
 		if ( useLimit ) {
 			sql = dialect.getLimitString( 
 					sql.trim(), //use of trim() here is ugly?
 					useOffset ? getFirstRow(selection) : 0, 
 					getMaxOrLimit(selection, dialect) 
 				);
 		}
 
 		sql = preprocessSQL( sql, queryParameters, dialect );
 		
 		PreparedStatement st = null;
 
-		st = (
-				scroll || useScrollableResultSetToSkip ?
-						session.getJDBCContext().getConnectionManager().prepareScrollableQueryStatement( sql, scrollMode, callable ) :
-						session.getJDBCContext().getConnectionManager().prepareQueryStatement( sql, callable )
+		st = session.getJDBCContext().getConnectionManager().prepareQueryStatement( 
+				sql,
+				scroll || useScrollableResultSetToSkip,
+				scrollMode,
+				callable
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			if ( useLimit && dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			if ( useLimit && !dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 
 			if ( !useLimit ) {
 				setMaxRows( st, selection );
 			}
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout().intValue() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize().intValue() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						log.debug(
 								"Lock timeout [" + lockOptions.getTimeOut() +
 										"] requested but dialect reported to not support lock timeouts"
 						);
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			log.trace( "Bound [" + col + "] parameters total" );
 		}
 		catch ( SQLException sqle ) {
 			st.close();
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			st.close();
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Some dialect-specific LIMIT clauses require the maximum last row number
 	 * (aka, first_row_number + total_row_count), while others require the maximum
 	 * returned row count (the total maximum number of rows to return).
 	 *
 	 * @param selection The selection criteria
 	 * @param dialect The dialect
 	 * @return The appropriate value to bind into the limit clause.
 	 */
 	private static int getMaxOrLimit(final RowSelection selection, final Dialect dialect) {
 		final int firstRow = dialect.convertToFirstRowValue( getFirstRow( selection ) );
 		final int lastRow = selection.getMaxRows().intValue();
 		if ( dialect.useMaxForLimit() ) {
 			return lastRow + firstRow;
 		}
 		else {
 			return lastRow;
 		}
 	}
 
 	/**
 	 * Bind parameter values needed by the dialect-specific LIMIT clause.
 	 *
 	 * @param statement The statement to which to bind limit param values.
 	 * @param index The bind position from which to start binding
 	 * @param selection The selection object containing the limit information.
 	 * @return The number of parameter values bound.
 	 * @throws java.sql.SQLException Indicates problems binding parameter values.
 	 */
 	private int bindLimitParameters(
 			final PreparedStatement statement,
 			final int index,
 			final RowSelection selection) throws SQLException {
 		Dialect dialect = getFactory().getDialect();
 		if ( !dialect.supportsVariableLimit() ) {
 			return 0;
 		}
 		if ( !hasMaxRows( selection ) ) {
 			throw new AssertionFailure( "no max results set" );
 		}
 		int firstRow = interpretFirstRow( getFirstRow( selection ) );
 		int lastRow = getMaxOrLimit( selection, dialect );
 		boolean hasFirstRow = dialect.supportsLimitOffset() && ( firstRow > 0 || dialect.forceLimitUsage() );
 		boolean reverse = dialect.bindLimitParametersInReverseOrder();
 		if ( hasFirstRow ) {
 			statement.setInt( index + ( reverse ? 1 : 0 ), firstRow );
 		}
 		statement.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
 		return hasFirstRow ? 2 : 1;
 	}
 
 	/**
 	 * Use JDBC API to limit the number of rows returned by the SQL query if necessary
 	 */
 	private void setMaxRows(
 			final PreparedStatement st,
 			final RowSelection selection) throws SQLException {
 		if ( hasMaxRows( selection ) ) {
 			st.setMaxRows( selection.getMaxRows().intValue() + interpretFirstRow( getFirstRow( selection ) ) );
 		}
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
 			Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
 					if ( log.isDebugEnabled() ) {
 						log.debug(
 								"bindNamedParameters() " +
 								typedval.getValue() + " -> " + name +
 								" [" + ( locs[i] + startIndex ) + "]"
 							);
 					}
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Fetch a <tt>PreparedStatement</tt>, call <tt>setMaxRows</tt> and then execute it,
 	 * advance to the first result and return an SQL <tt>ResultSet</tt>
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final boolean autodiscovertypes,
 	        final boolean callable,
 	        final RowSelection selection,
 	        final SessionImplementor session) 
 	throws SQLException, HibernateException {
 	
 		ResultSet rs = null;
 		try {
 			Dialect dialect = getFactory().getDialect();
 			rs = st.executeQuery();
 			rs = wrapResultSetIfEnabled( rs , session );
 			
 			if ( !dialect.supportsLimitOffset() || !useLimit( selection, dialect ) ) {
 				advance( rs, selection );
 			}
 			
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			st.close();
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
 				log.debug("Wrapping result set [" + rs + "]");
 				return session.getFactory()
 						.getSettings()
 						.getJdbcSupport().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				log.info("Error wrapping result set", e);
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			log.trace("Building columnName->columnIndex cache");
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
 		
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"loading entity: " + 
 					MessageHelper.infoString( persister, id, identifierType, getFactory() ) 
 				);
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
 
 		log.debug("done entity load");
 		
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
 		
 		if ( log.isDebugEnabled() ) {
 			log.debug( "loading collection element by index" );
 		}
 
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
 
 		log.debug("done entity load");
 		
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
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"batch loading entity: " + 
 					MessageHelper.infoString(persister, ids, getFactory() ) 
 				);
 		}
 
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
 
 		log.debug("done entity batch load");
 		
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"loading collection: "+ 
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() )
 				);
 		}
 
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
 	
 		log.debug("done loading collection");
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Type type) throws HibernateException {
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"batch loading collection: "+ 
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() )
 				);
 		}
 
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
 		
 		log.debug("done batch load");
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Object[] parameterValues,
 	        final Type[] parameterTypes,
 	        final Map namedParameters,
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
 
 		if ( querySpaces == null || querySpaces.size() == 0 ) {
 			log.trace( "unexpected querySpaces is "+( querySpaces == null ? "null" : "empty" ) );
 		}
 		else {
 			log.trace( "querySpaces is "+querySpaces.toString() );
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
 				FilterKey.createFilterKeys(
 						session.getLoadQueryInfluencers().getEnabledFilters(),
 						session.getEntityMode()
 				),
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
 			boolean isImmutableNaturalKeyLookup = queryParameters.isNaturalKeyLookup()
 					&& getEntityPersisters()[0].getEntityMetamodel().hasImmutableNaturalId();
 
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
 		return;
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
 
 			PreparedStatement st = prepareQueryStatement( queryParameters, true, session );
 			ResultSet rs = getResultSet(st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), queryParameters.getRowSelection(), session);
 
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
 
 	public String toString() {
 		return getClass().getName() + '(' + getSQLString() + ')';
 	}
 
 }
