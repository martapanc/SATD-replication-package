diff --git a/hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
deleted file mode 100644
index d820594a9a..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
+++ /dev/null
@@ -1,74 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.engine;
-
-import org.hibernate.HibernateException;
-import org.hibernate.jdbc.Work;
-
-import java.io.Serializable;
-import java.sql.Connection;
-import java.sql.SQLException;
-
-/**
- * Allows work to be done outside the current transaction, by suspending it,
- * and performing work in a new transaction
- * 
- * @author Emmanuel Bernard
- */
-public abstract class TransactionHelper {
-
-	// todo : remove this and just have subclasses use IsolationDelegate directly...
-
-	/**
-	 * The work to be done
-	 */
-	protected abstract Serializable doWorkInCurrentTransaction(Connection conn, String sql) throws SQLException;
-
-	/**
-	 * Suspend the current transaction and perform work in a new transaction
-	 */
-	public Serializable doWorkInNewTransaction(final SessionImplementor session) throws HibernateException {
-		class WorkToDo implements Work {
-			Serializable generatedValue;
-
-			@Override
-			public void execute(Connection connection) throws SQLException {
-				String sql = null;
-				try {
-					generatedValue = doWorkInCurrentTransaction( connection, sql );
-				}
-				catch( SQLException e ) {
-					throw session.getFactory().getSQLExceptionHelper().convert(
-							e,
-							"could not get or update next value",
-							sql
-					);
-				}
-			}
-		}
-		WorkToDo work = new WorkToDo();
-		session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork( work, true );
-		return work.generatedValue;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
index 241530c1a6..dbd92ad62b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
@@ -1,236 +1,260 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.engine.jdbc.spi.StatementPreparer;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 
 /**
  * Standard Hibernate implementation of {@link JdbcCoordinator}
  * <p/>
  * IMPL NOTE : Custom serialization handling!
  *
  * @author Steve Ebersole
  */
 public class JdbcCoordinatorImpl implements JdbcCoordinator {
 	private static final Logger log = LoggerFactory.getLogger( JdbcCoordinatorImpl.class );
 
 	private transient TransactionCoordinatorImpl transactionCoordinator;
 
 	private final transient LogicalConnectionImpl logicalConnection;
 
 	private transient Batch currentBatch;
 
 	public JdbcCoordinatorImpl(
 			Connection userSuppliedConnection,
 			TransactionCoordinatorImpl transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 		this.logicalConnection = new LogicalConnectionImpl(
 				userSuppliedConnection,
 				transactionCoordinator.getTransactionContext().getConnectionReleaseMode(),
 				transactionCoordinator.getTransactionContext().getTransactionEnvironment().getJdbcServices()
 		);
 	}
 
 	private JdbcCoordinatorImpl(LogicalConnectionImpl logicalConnection) {
 		this.logicalConnection = logicalConnection;
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return transactionCoordinator;
 	}
 
 	@Override
 	public LogicalConnectionImplementor getLogicalConnection() {
 		return logicalConnection;
 	}
 
 	protected TransactionEnvironment transactionEnvironment() {
 		return getTransactionCoordinator().getTransactionContext().getTransactionEnvironment();
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return transactionEnvironment().getSessionFactory();
 	}
 
 	protected BatchBuilder batchBuilder() {
 		return sessionFactory().getServiceRegistry().getService( BatchBuilder.class );
 	}
 
 	private SQLExceptionHelper sqlExceptionHelper() {
 		return transactionEnvironment().getJdbcServices().getSqlExceptionHelper();
 	}
 
 
 	private int flushDepth = 0;
 
 	@Override
 	public void flushBeginning() {
 		if ( flushDepth == 0 ) {
 			logicalConnection.disableReleases();
 		}
 		flushDepth++;
 	}
 
 	@Override
 	public void flushEnding() {
 		flushDepth--;
 		if ( flushDepth < 0 ) {
 			throw new HibernateException( "Mismatched flush handling" );
 		}
 		if ( flushDepth == 0 ) {
 			logicalConnection.enableReleases();
 		}
 	}
 
 	@Override
 	public Connection close() {
 		if ( currentBatch != null ) {
 			log.warn( "Closing un-released batch" );
 			currentBatch.release();
 		}
 		return logicalConnection.close();
 	}
 
 	@Override
 	public Batch getBatch(BatchKey key) {
 		if ( currentBatch != null ) {
 			if ( currentBatch.getKey().equals( key ) ) {
 				return currentBatch;
 			}
 			else {
 				currentBatch.execute();
 				currentBatch.release();
 			}
 		}
 		currentBatch = batchBuilder().buildBatch( key, this );
 		return currentBatch;
 	}
 
 	@Override
 	public void abortBatch() {
 		if ( currentBatch != null ) {
 			currentBatch.release();
 		}
 	}
 
 	private transient StatementPreparer statementPreparer;
 
 	@Override
 	public StatementPreparer getStatementPreparer() {
 		if ( statementPreparer == null ) {
 			statementPreparer = new StatementPreparerImpl( this );
 		}
 		return statementPreparer;
 	}
 
 	@Override
 	public void setTransactionTimeOut(int timeOut) {
 		getStatementPreparer().setTransactionTimeOut( timeOut );
 	}
 
 	/**
 	 * To be called after local transaction completion.  Used to conditionally
 	 * release the JDBC connection aggressively if the configured release mode
 	 * indicates.
 	 */
 	public void afterTransaction() {
 		logicalConnection.afterTransaction();
 		if ( statementPreparer != null ) {
 			statementPreparer.unsetTransactionTimeOut();
 		}
 	}
 
 	public void coordinateWork(Work work) {
 		Connection connection = getLogicalConnection().getDistinctConnectionProxy();
 		try {
 			work.execute( connection );
 			getLogicalConnection().afterStatementExecution();
 		}
 		catch ( SQLException e ) {
 			throw sqlExceptionHelper().convert( e, "error executing work" );
 		}
 		finally {
 			try {
 				if ( ! connection.isClosed() ) {
 					connection.close();
 				}
 			}
 			catch (SQLException e) {
 				log.debug( "Error closing connection proxy", e );
 			}
 		}
 	}
 
+	@Override
+	public <T> T coordinateWork(ReturningWork<T> work) {
+		Connection connection = getLogicalConnection().getDistinctConnectionProxy();
+		try {
+			T result = work.execute( connection );
+			getLogicalConnection().afterStatementExecution();
+			return result;
+		}
+		catch ( SQLException e ) {
+			throw sqlExceptionHelper().convert( e, "error executing work" );
+		}
+		finally {
+			try {
+				if ( ! connection.isClosed() ) {
+					connection.close();
+				}
+			}
+			catch (SQLException e) {
+				log.debug( "Error closing connection proxy", e );
+			}
+		}
+	}
+
 	public void executeBatch() {
 		if ( currentBatch != null ) {
 			currentBatch.execute();
 			currentBatch.release(); // needed?
 		}
 	}
 
 	@Override
 	public void cancelLastQuery() {
 		logicalConnection.getResourceRegistry().cancelLastQuery();
 	}
 
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		if ( ! logicalConnection.isReadyForSerialization() ) {
 			throw new HibernateException( "Cannot serialize Session while connected" );
 		}
 		logicalConnection.serialize( oos );
 	}
 
 	public static JdbcCoordinatorImpl deserialize(
 			ObjectInputStream ois,
 			TransactionContext transactionContext) throws IOException, ClassNotFoundException {
 		return new JdbcCoordinatorImpl( LogicalConnectionImpl.deserialize( ois, transactionContext ) );
  	}
 
 	public void afterDeserialize(TransactionCoordinatorImpl transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java
index d1d9a143de..b693081470 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java
@@ -1,96 +1,101 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
+import org.hibernate.id.IntegralDataTypeHolder;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 
 import java.io.Serializable;
 import java.sql.Connection;
 
 /**
  * Coordinates JDBC-related activities.
  *
  * @author Steve Ebersole
  */
 public interface JdbcCoordinator extends Serializable {
 	/**
 	 * Retrieve the transaction coordinator associated with this JDBC coordinator.
 	 *
 	 * @return The transaction coordinator
 	 */
 	public TransactionCoordinator getTransactionCoordinator();
 
 	/**
 	 * Retrieves the logical connection associated with this JDBC coordinator.
 	 *
 	 * @return The logical connection
 	 */
 	public LogicalConnectionImplementor getLogicalConnection();
 
 	/**
 	 * Get a batch instance.
 	 *
 	 * @param key The unique batch key.
 	 *
 	 * @return The batch
 	 */
 	public Batch getBatch(BatchKey key);
 
 	public void abortBatch();
 
 	/**
 	 * Obtain the statement preparer associated with this JDBC coordinator.
 	 *
 	 * @return This coordinator's statement preparer
 	 */
 	public StatementPreparer getStatementPreparer();
 
 	/**
 	 * Callback to let us know that a flush is beginning.  We use this fact
 	 * to temporarily circumvent aggressive connection releasing until after
 	 * the flush cycle is complete {@link #flushEnding()}
 	 */
 	public void flushBeginning();
 
 	/**
 	 * Callback to let us know that a flush is ending.  We use this fact to
 	 * stop circumventing aggressive releasing connections.
 	 */
 	public void flushEnding();
 
 	public Connection close();
 
 	public void afterTransaction();
 
 	public void coordinateWork(Work work);
 
+	public <T> T coordinateWork(ReturningWork<T> work);
+
 	public void executeBatch();
 
 	public void cancelLastQuery();
 
 	public void setTransactionTimeOut(int timeout);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLStatementLogger.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLStatementLogger.java
index 141c14ce79..5be6dd39ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLStatementLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLStatementLogger.java
@@ -1,104 +1,109 @@
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
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.jdbc.util.FormatStyle;
+import org.hibernate.jdbc.util.Formatter;
 
 /**
  * Centralize logging for SQL statements.
  *
  * @author Steve Ebersole
  */
 public class SQLStatementLogger {
 	private static final Logger log = LoggerFactory.getLogger( SQLStatementLogger.class );
 
 	private boolean logToStdout;
 	private boolean format;
 
 	/**
 	 * Constructs a new SQLStatementLogger instance.
 	 */
 	public SQLStatementLogger() {
 		this( false, false );
 	}
 
 	/**
 	 * Constructs a new SQLStatementLogger instance.
 	 *
 	 * @param logToStdout Should we log to STDOUT in addition to our internal logger.
 	 * @param format Should we format the statements prior to logging
 	 */
 	public SQLStatementLogger(boolean logToStdout, boolean format) {
 		this.logToStdout = logToStdout;
 		this.format = format;
 	}
 
 	/**
 	 * Are we currently logging to stdout?
 	 *
 	 * @return True if we are currently logging to stdout; false otherwise.
 	 */
 	public boolean isLogToStdout() {
 		return logToStdout;
 	}
 
 	/**
 	 * Enable (true) or disable (false) logging to stdout.
 	 *
 	 * @param logToStdout True to enable logging to stdout; false to disable.
 	 */
 	public void setLogToStdout(boolean logToStdout) {
 		this.logToStdout = logToStdout;
 	}
 
 	public boolean isFormat() {
 		return format;
 	}
 
 	public void setFormat(boolean format) {
 		this.format = format;
 	}
 
 	/**
 	 * Log a SQL statement string.
 	 *
 	 * @param statement The SQL statement.
 	 */
 	public void logStatement(String statement) {
 		// for now just assume a DML log for formatting
+		logStatement( statement, FormatStyle.BASIC.getFormatter() );
+	}
+
+	public void logStatement(String statement, Formatter formatter) {
 		if ( format ) {
 			if ( logToStdout || log.isDebugEnabled() ) {
-				statement = FormatStyle.BASIC.getFormatter().format( statement );
+				statement = formatter.format( statement );
 			}
 		}
 		log.debug( statement );
 		if ( logToStdout ) {
 			System.out.println( "Hibernate: " + statement );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
index 01a4e611d2..f00fbf55ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
@@ -1,123 +1,188 @@
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
 package org.hibernate.engine.transaction.internal.jdbc;
 
 
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.engine.transaction.spi.IsolationDelegate;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * The isolation delegate for JDBC {@link Connection} based transactions
  *
  * @author Steve Ebersole
  */
 public class JdbcIsolationDelegate implements IsolationDelegate {
 	private static final Logger log = LoggerFactory.getLogger( JdbcIsolationDelegate.class );
 
 	private final TransactionCoordinator transactionCoordinator;
 
 	public JdbcIsolationDelegate(TransactionCoordinator transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 	}
 
 	protected ConnectionProvider connectionProvider() {
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getJdbcServices().getConnectionProvider();
 	}
 
 	protected SQLExceptionHelper sqlExceptionHelper() {
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getJdbcServices().getSqlExceptionHelper();
 	}
 
 	@Override
 	public void delegateWork(Work work, boolean transacted) throws HibernateException {
 		boolean wasAutoCommit = false;
 		try {
 			// todo : should we use a connection proxy here?
 			Connection connection = connectionProvider().getConnection();
 			try {
 				if ( transacted ) {
 					if ( connection.getAutoCommit() ) {
 						wasAutoCommit = true;
 						connection.setAutoCommit( false );
 					}
 				}
 
 				work.execute( connection );
 
 				if ( transacted ) {
 					connection.commit();
 				}
 			}
 			catch ( Exception e ) {
 				try {
 					if ( transacted && !connection.isClosed() ) {
 						connection.rollback();
 					}
 				}
 				catch ( Exception ignore ) {
 					log.info( "unable to rollback connection on exception [" + ignore + "]" );
 				}
 
 				if ( e instanceof HibernateException ) {
 					throw (HibernateException) e;
 				}
 				else if ( e instanceof SQLException ) {
 					throw sqlExceptionHelper().convert( (SQLException) e, "error performing isolated work" );
 				}
 				else {
 					throw new HibernateException( "error performing isolated work", e );
 				}
 			}
 			finally {
 				if ( transacted && wasAutoCommit ) {
 					try {
 						connection.setAutoCommit( true );
 					}
 					catch ( Exception ignore ) {
 						log.trace( "was unable to reset connection back to auto-commit" );
 					}
 				}
 				try {
 					connectionProvider().closeConnection( connection );
 				}
 				catch ( Exception ignore ) {
 					log.info( "Unable to release isolated connection [" + ignore + "]" );
 				}
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper().convert( sqle, "unable to obtain isolated JDBC connection" );
 		}
 	}
+
+	@Override
+	public <T> T delegateWork(ReturningWork<T> work, boolean transacted) throws HibernateException {
+		boolean wasAutoCommit = false;
+		try {
+			// todo : should we use a connection proxy here?
+			Connection connection = connectionProvider().getConnection();
+			try {
+				if ( transacted ) {
+					if ( connection.getAutoCommit() ) {
+						wasAutoCommit = true;
+						connection.setAutoCommit( false );
+					}
+				}
+
+				T result = work.execute( connection );
+
+				if ( transacted ) {
+					connection.commit();
+				}
+
+				return result;
+			}
+			catch ( Exception e ) {
+				try {
+					if ( transacted && !connection.isClosed() ) {
+						connection.rollback();
+					}
+				}
+				catch ( Exception ignore ) {
+					log.info( "unable to rollback connection on exception [" + ignore + "]" );
+				}
+
+				if ( e instanceof HibernateException ) {
+					throw (HibernateException) e;
+				}
+				else if ( e instanceof SQLException ) {
+					throw sqlExceptionHelper().convert( (SQLException) e, "error performing isolated work" );
+				}
+				else {
+					throw new HibernateException( "error performing isolated work", e );
+				}
+			}
+			finally {
+				if ( transacted && wasAutoCommit ) {
+					try {
+						connection.setAutoCommit( true );
+					}
+					catch ( Exception ignore ) {
+						log.trace( "was unable to reset connection back to auto-commit" );
+					}
+				}
+				try {
+					connectionProvider().closeConnection( connection );
+				}
+				catch ( Exception ignore ) {
+					log.info( "Unable to release isolated connection [" + ignore + "]" );
+				}
+			}
+		}
+		catch ( SQLException sqle ) {
+			throw sqlExceptionHelper().convert( sqle, "unable to obtain isolated JDBC connection" );
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
index 7e071428fe..42bcfa9966 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
@@ -1,185 +1,294 @@
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
 package org.hibernate.engine.transaction.internal.jta;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import javax.transaction.NotSupportedException;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.engine.transaction.spi.IsolationDelegate;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * An isolation delegate for JTA environments.
  *
  * @author Steve Ebersole
  */
 public class JtaIsolationDelegate implements IsolationDelegate {
 	private static final Logger log = LoggerFactory.getLogger( JtaIsolationDelegate.class );
 
 	private final TransactionCoordinator transactionCoordinator;
 
 	public JtaIsolationDelegate(TransactionCoordinator transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 	}
 
 	protected TransactionManager transactionManager() {
 		return transactionCoordinator.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJtaPlatform()
 				.retrieveTransactionManager();
 	}
 
 	protected ConnectionProvider connectionProvider() {
 		return transactionCoordinator.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getConnectionProvider();
 	}
 
 	protected SQLExceptionHelper sqlExceptionHelper() {
 		return transactionCoordinator.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getSqlExceptionHelper();
 	}
 
 	@Override
 	public void delegateWork(Work work, boolean transacted) throws HibernateException {
 		TransactionManager transactionManager = transactionManager();
 
 		try {
 			// First we suspend any current JTA transaction
 			Transaction surroundingTransaction = transactionManager.suspend();
 			if ( log.isDebugEnabled() ) {
 				log.debug( "surrounding JTA transaction suspended [" + surroundingTransaction + "]" );
 			}
 
 			boolean hadProblems = false;
 			try {
 				// then perform the requested work
 				if ( transacted ) {
 					doTheWorkInNewTransaction( work, transactionManager );
 				}
 				else {
 					doTheWorkInNoTransaction( work );
 				}
 			}
 			catch ( HibernateException e ) {
 				hadProblems = true;
 				throw e;
 			}
 			finally {
 				try {
 					transactionManager.resume( surroundingTransaction );
 					if ( log.isDebugEnabled() ) {
 						log.debug( "surrounding JTA transaction resumed [" + surroundingTransaction + "]" );
 					}
 				}
 				catch( Throwable t ) {
 					// if the actually work had an error use that, otherwise error based on t
 					if ( !hadProblems ) {
 						//noinspection ThrowFromFinallyBlock
 						throw new HibernateException( "Unable to resume previously suspended transaction", t );
 					}
 				}
 			}
 		}
 		catch ( SystemException e ) {
 			throw new HibernateException( "Unable to suspend current JTA transaction", e );
 		}
 	}
 
 	private void doTheWorkInNewTransaction(Work work, TransactionManager transactionManager) {
 		try {
 			// start the new isolated transaction
 			transactionManager.begin();
 
 			try {
 				doTheWork( work );
 				// if everythign went ok, commit the isolated transaction
 				transactionManager.commit();
 			}
 			catch ( Exception e ) {
 				try {
 					transactionManager.rollback();
 				}
 				catch ( Exception ignore ) {
 					log.info( "Unable to rollback isolated transaction on error [" + e + "] : [" + ignore + "]" );
 				}
 			}
 		}
 		catch ( SystemException e ) {
 			throw new HibernateException( "Unable to start isolated transaction", e );
 		}
 		catch ( NotSupportedException e ) {
 			throw new HibernateException( "Unable to start isolated transaction", e );
 		}
 	}
 
 	private void doTheWorkInNoTransaction(Work work) {
 		doTheWork( work );
 	}
 
 	private void doTheWork(Work work) {
 		try {
 			// obtain our isolated connection
 			Connection connection = connectionProvider().getConnection();
 			try {
 				// do the actual work
 				work.execute( connection );
 			}
 			catch ( HibernateException e ) {
 				throw e;
 			}
 			catch ( Exception e ) {
 				throw new HibernateException( "Unable to perform isolated work", e );
 			}
 			finally {
 				try {
 					// no matter what, release the connection (handle)
 					connectionProvider().closeConnection( connection );
 				}
 				catch ( Throwable ignore ) {
 					log.info( "Unable to release isolated connection [" + ignore + "]" );
 				}
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper().convert( sqle, "unable to obtain isolated JDBC connection" );
 		}
 	}
 
+	@Override
+	public <T> T delegateWork(ReturningWork<T> work, boolean transacted) throws HibernateException {
+		TransactionManager transactionManager = transactionManager();
+
+		try {
+			// First we suspend any current JTA transaction
+			Transaction surroundingTransaction = transactionManager.suspend();
+			if ( log.isDebugEnabled() ) {
+				log.debug( "surrounding JTA transaction suspended [" + surroundingTransaction + "]" );
+			}
+
+			boolean hadProblems = false;
+			try {
+				// then perform the requested work
+				if ( transacted ) {
+					return doTheWorkInNewTransaction( work, transactionManager );
+				}
+				else {
+					return doTheWorkInNoTransaction( work );
+				}
+			}
+			catch ( HibernateException e ) {
+				hadProblems = true;
+				throw e;
+			}
+			finally {
+				try {
+					transactionManager.resume( surroundingTransaction );
+					if ( log.isDebugEnabled() ) {
+						log.debug( "surrounding JTA transaction resumed [" + surroundingTransaction + "]" );
+					}
+				}
+				catch( Throwable t ) {
+					// if the actually work had an error use that, otherwise error based on t
+					if ( !hadProblems ) {
+						//noinspection ThrowFromFinallyBlock
+						throw new HibernateException( "Unable to resume previously suspended transaction", t );
+					}
+				}
+			}
+		}
+		catch ( SystemException e ) {
+			throw new HibernateException( "Unable to suspend current JTA transaction", e );
+		}
+	}
+
+	private <T> T doTheWorkInNewTransaction(ReturningWork<T> work, TransactionManager transactionManager) {
+		T result = null;
+		try {
+			// start the new isolated transaction
+			transactionManager.begin();
+
+			try {
+				result = doTheWork( work );
+				// if everything went ok, commit the isolated transaction
+				transactionManager.commit();
+			}
+			catch ( Exception e ) {
+				try {
+					transactionManager.rollback();
+				}
+				catch ( Exception ignore ) {
+					log.info( "Unable to rollback isolated transaction on error [" + e + "] : [" + ignore + "]" );
+				}
+			}
+		}
+		catch ( SystemException e ) {
+			throw new HibernateException( "Unable to start isolated transaction", e );
+		}
+		catch ( NotSupportedException e ) {
+			throw new HibernateException( "Unable to start isolated transaction", e );
+		}
+		return result;
+	}
+
+	private <T> T doTheWorkInNoTransaction(ReturningWork<T> work) {
+		return doTheWork( work );
+	}
+
+	private <T> T doTheWork(ReturningWork<T> work) {
+		try {
+			// obtain our isolated connection
+			Connection connection = connectionProvider().getConnection();
+			try {
+				// do the actual work
+				return work.execute( connection );
+			}
+			catch ( HibernateException e ) {
+				throw e;
+			}
+			catch ( Exception e ) {
+				throw new HibernateException( "Unable to perform isolated work", e );
+			}
+			finally {
+				try {
+					// no matter what, release the connection (handle)
+					connectionProvider().closeConnection( connection );
+				}
+				catch ( Throwable ignore ) {
+					log.info( "Unable to release isolated connection [" + ignore + "]" );
+				}
+			}
+		}
+		catch ( SQLException e ) {
+			throw sqlExceptionHelper().convert( e, "unable to obtain isolated JDBC connection" );
+		}
+	}
+
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/IsolationDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/IsolationDelegate.java
index 68935e95f4..e744b0d00a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/IsolationDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/IsolationDelegate.java
@@ -1,44 +1,57 @@
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
 package org.hibernate.engine.transaction.spi;
 
 import org.hibernate.HibernateException;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 
 /**
  * Contract for performing work in a manner that isolates it from any current transaction.
  *
  * @author Steve Ebersole
  */
 public interface IsolationDelegate {
 	/**
 	 * Perform the given work in isolation from current transaction.
 	 *
 	 * @param work The work to be performed.
 	 * @param transacted Should the work itself be done in a (isolated) transaction?
 	 *
 	 * @throws HibernateException Indicates a problem performing the work.
 	 */
 	public void delegateWork(Work work, boolean transacted) throws HibernateException;
+
+	/**
+	 * Perform the given work in isolation from current transaction.
+	 *
+	 * @param work The work to be performed.
+	 * @param transacted Should the work itself be done in a (isolated) transaction?
+	 *
+	 * @return The work result
+	 *
+	 * @throws HibernateException Indicates a problem performing the work.
+	 */
+	public <T> T delegateWork(ReturningWork<T> work, boolean transacted) throws HibernateException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
index bad7b90643..265c0ae216 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
@@ -1,286 +1,292 @@
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
 package org.hibernate.id;
 
-import java.io.Serializable;
-import java.sql.Connection;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.Properties;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 import org.hibernate.id.enhanced.AccessCallback;
 import org.hibernate.id.enhanced.OptimizerFactory;
 import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.util.FormatStyle;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TransactionHelper;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+import java.io.Serializable;
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.Properties;
 
 /**
  *
  * A hilo <tt>IdentifierGenerator</tt> that returns a <tt>Long</tt>, constructed using
  * a hi/lo algorithm. The hi value MUST be fetched in a seperate transaction
  * to the <tt>Session</tt> transaction so the generator must be able to obtain
  * a new connection and commit it. Hence this implementation may not
  * be used  when the user is supplying connections. In this
  * case a <tt>SequenceHiLoGenerator</tt> would be a better choice (where
  * supported).<br>
  * <br>
  *
  * A hilo <tt>IdentifierGenerator</tt> that uses a database
  * table to store the last generated values. A table can contains
  * several hi values. They are distinct from each other through a key
  * <p/>
  * <p>This implementation is not compliant with a user connection</p>
  * <p/>
  * 
  * <p>Allowed parameters (all of them are optional):</p>
  * <ul>
  * <li>table: table name (default <tt>hibernate_sequences</tt>)</li>
  * <li>primary_key_column: key column name (default <tt>sequence_name</tt>)</li>
  * <li>value_column: hi value column name(default <tt>sequence_next_hi_value</tt>)</li>
  * <li>primary_key_value: key value for the current entity (default to the entity's primary table name)</li>
  * <li>primary_key_length: length of the key column in DB represented as a varchar (default to 255)</li>
  * <li>max_lo: max low value before increasing hi (default to Short.MAX_VALUE)</li>
  * </ul>
  *
  * @author Emmanuel Bernard
  * @author <a href="mailto:kr@hbt.de">Klaus Richarz</a>.
  */
-public class MultipleHiLoPerTableGenerator 
-	extends TransactionHelper
-	implements PersistentIdentifierGenerator, Configurable {
-	
-	private static final Logger log = LoggerFactory.getLogger(MultipleHiLoPerTableGenerator.class);
+public class MultipleHiLoPerTableGenerator implements PersistentIdentifierGenerator, Configurable {
+	private static final Logger log = LoggerFactory.getLogger( MultipleHiLoPerTableGenerator.class );
 	
 	public static final String ID_TABLE = "table";
 	public static final String PK_COLUMN_NAME = "primary_key_column";
 	public static final String PK_VALUE_NAME = "primary_key_value";
 	public static final String VALUE_COLUMN_NAME = "value_column";
 	public static final String PK_LENGTH_NAME = "primary_key_length";
 
 	private static final int DEFAULT_PK_LENGTH = 255;
 	public static final String DEFAULT_TABLE = "hibernate_sequences";
 	private static final String DEFAULT_PK_COLUMN = "sequence_name";
 	private static final String DEFAULT_VALUE_COLUMN = "sequence_next_hi_value";
 	
 	private String tableName;
 	private String pkColumnName;
 	private String valueColumnName;
 	private String query;
 	private String insert;
 	private String update;
 
 	//hilo params
 	public static final String MAX_LO = "max_lo";
 
 	private int maxLo;
 	private OptimizerFactory.LegacyHiLoAlgorithmOptimizer hiloOptimizer;
 
 	private Class returnClass;
 	private int keySize;
 
 
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 			new StringBuffer( dialect.getCreateTableString() )
 					.append( ' ' )
 					.append( tableName )
 					.append( " ( " )
 					.append( pkColumnName )
 					.append( ' ' )
 					.append( dialect.getTypeName( Types.VARCHAR, keySize, 0, 0 ) )
 					.append( ",  " )
 					.append( valueColumnName )
 					.append( ' ' )
 					.append( dialect.getTypeName( Types.INTEGER ) )
 					.append( " ) " )
 					.toString()
 		};
 	}
 
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		StringBuffer sqlDropString = new StringBuffer( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			sqlDropString.append( "if exists " );
 		}
 		sqlDropString.append( tableName ).append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			sqlDropString.append( " if exists" );
 		}
 		return new String[] { sqlDropString.toString() };
 	}
 
 	public Object generatorKey() {
 		return tableName;
 	}
 
-	public Serializable doWorkInCurrentTransaction(Connection conn, String sql) throws SQLException {
-		IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( returnClass );
-		int rows;
-		do {
-			SQL_STATEMENT_LOGGER.logStatement( query, FormatStyle.BASIC );
-			PreparedStatement qps = conn.prepareStatement( query );
-			PreparedStatement ips = null;
-			try {
-				ResultSet rs = qps.executeQuery();
-				boolean isInitialized = rs.next();
-				if ( !isInitialized ) {
-					value.initialize( 0 );
-					SQL_STATEMENT_LOGGER.logStatement( insert, FormatStyle.BASIC );
-					ips = conn.prepareStatement( insert );
-					value.bind( ips, 1 );
-					ips.execute();
-				}
-				else {
-					value.initialize( rs, 0 );
-				}
-				rs.close();
-			}
-			catch (SQLException sqle) {
-				log.error("could not read or init a hi value", sqle);
-				throw sqle;
-			}
-			finally {
-				if (ips != null) {
-					ips.close();
-				}
-				qps.close();
-			}
+	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
+		final ReturningWork<IntegralDataTypeHolder> work = new ReturningWork<IntegralDataTypeHolder>() {
+			@Override
+			public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
+				IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( returnClass );
+				SQLStatementLogger statementLogger = session
+						.getFactory()
+						.getServiceRegistry()
+						.getService( JdbcServices.class )
+						.getSqlStatementLogger();
+				int rows;
+				do {
+					statementLogger.logStatement( query, FormatStyle.BASIC.getFormatter() );
+					PreparedStatement qps = connection.prepareStatement( query );
+					PreparedStatement ips = null;
+					try {
+						ResultSet rs = qps.executeQuery();
+						boolean isInitialized = rs.next();
+						if ( !isInitialized ) {
+							value.initialize( 0 );
+							statementLogger.logStatement( insert, FormatStyle.BASIC.getFormatter() );
+							ips = connection.prepareStatement( insert );
+							value.bind( ips, 1 );
+							ips.execute();
+						}
+						else {
+							value.initialize( rs, 0 );
+						}
+						rs.close();
+					}
+					catch (SQLException sqle) {
+						log.error("could not read or init a hi value", sqle);
+						throw sqle;
+					}
+					finally {
+						if (ips != null) {
+							ips.close();
+						}
+						qps.close();
+					}
 
-			SQL_STATEMENT_LOGGER.logStatement( update, FormatStyle.BASIC );
-			PreparedStatement ups = conn.prepareStatement( update );
-			try {
-				value.copy().increment().bind( ups, 1 );
-				value.bind( ups, 2 );
-				rows = ups.executeUpdate();
-			}
-			catch (SQLException sqle) {
-				log.error("could not update hi value in: " + tableName, sqle);
-				throw sqle;
-			}
-			finally {
-				ups.close();
-			}
-		} while ( rows==0 );
+					statementLogger.logStatement( update, FormatStyle.BASIC.getFormatter() );
+					PreparedStatement ups = connection.prepareStatement( update );
+					try {
+						value.copy().increment().bind( ups, 1 );
+						value.bind( ups, 2 );
+						rows = ups.executeUpdate();
+					}
+					catch (SQLException sqle) {
+						log.error("could not update hi value in: " + tableName, sqle);
+						throw sqle;
+					}
+					finally {
+						ups.close();
+					}
+				} while ( rows==0 );
 
-		return value;
-	}
+				return value;
+			}
+		};
 
-	public synchronized Serializable generate(final SessionImplementor session, Object obj)
-		throws HibernateException {
 		// maxLo < 1 indicates a hilo generator with no hilo :?
 		if ( maxLo < 1 ) {
 			//keep the behavior consistent even for boundary usages
 			IntegralDataTypeHolder value = null;
 			while ( value == null || value.lt( 1 ) ) {
-				value = (IntegralDataTypeHolder) doWorkInNewTransaction( session );
+				value = session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork( work, true );
 			}
 			return value.makeValue();
 		}
 
 		return hiloOptimizer.generate(
 				new AccessCallback() {
 					public IntegralDataTypeHolder getNextValue() {
-						return (IntegralDataTypeHolder) doWorkInNewTransaction( session );
+						return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork( work, true );
 					}
 				}
 		);
 	}
 
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 
 		tableName = normalizer.normalizeIdentifierQuoting( ConfigurationHelper.getString( ID_TABLE, params, DEFAULT_TABLE ) );
 		if ( tableName.indexOf( '.' ) < 0 ) {
 			tableName = dialect.quote( tableName );
 			final String schemaName = dialect.quote(
 					normalizer.normalizeIdentifierQuoting( params.getProperty( SCHEMA ) )
 			);
 			final String catalogName = dialect.quote(
 					normalizer.normalizeIdentifierQuoting( params.getProperty( CATALOG ) )
 			);
 			tableName = Table.qualify( catalogName, schemaName, tableName );
 		}
 		else {
 			// if already qualified there is not much we can do in a portable manner so we pass it
 			// through and assume the user has set up the name correctly.
 		}
 
 		pkColumnName = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						ConfigurationHelper.getString( PK_COLUMN_NAME, params, DEFAULT_PK_COLUMN )
 				)
 		);
 		valueColumnName = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						ConfigurationHelper.getString( VALUE_COLUMN_NAME, params, DEFAULT_VALUE_COLUMN )
 				)
 		);
 		keySize = ConfigurationHelper.getInt(PK_LENGTH_NAME, params, DEFAULT_PK_LENGTH);
 		String keyValue = ConfigurationHelper.getString(PK_VALUE_NAME, params, params.getProperty(TABLE) );
 
 		query = "select " +
 			valueColumnName +
 			" from " +
 			dialect.appendLockHint( LockMode.PESSIMISTIC_WRITE, tableName ) +
 			" where " + pkColumnName + " = '" + keyValue + "'" +
 			dialect.getForUpdateString();
 
 		update = "update " +
 			tableName +
 			" set " +
 			valueColumnName +
 			" = ? where " +
 			valueColumnName +
 			" = ? and " +
 			pkColumnName +
 			" = '" + 
 			keyValue 
 			+ "'";
 		
 		insert = "insert into " + tableName +
 			"(" + pkColumnName + ", " +	valueColumnName + ") " +
 			"values('"+ keyValue +"', ?)";
 
 
 		//hilo config
 		maxLo = ConfigurationHelper.getInt(MAX_LO, params, Short.MAX_VALUE);
 		returnClass = type.getReturnedClass();
 
 		if ( maxLo >= 1 ) {
 			hiloOptimizer = new OptimizerFactory.LegacyHiLoAlgorithmOptimizer( returnClass, maxLo );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
index 6a33796fba..44d4760607 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
@@ -1,110 +1,106 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.id;
 
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.jdbc.util.SQLStatementLogger;
 
 /**
  * An <tt>IdentifierGenerator</tt> that requires creation of database objects.
  * <br><br>
  * All <tt>PersistentIdentifierGenerator</tt>s that also implement
  * <tt>Configurable</tt> have access to a special mapping parameter: schema
  *
  * @see IdentifierGenerator
  * @see Configurable
  * @author Gavin King
  */
 public interface PersistentIdentifierGenerator extends IdentifierGenerator {
 
 	/**
 	 * The configuration parameter holding the schema name
 	 */
 	public static final String SCHEMA = "schema";
 
 	/**
 	 * The configuration parameter holding the table name for the
 	 * generated id
 	 */
 	public static final String TABLE = "target_table";
 
 	/**
 	 * The configuration parameter holding the table names for all
 	 * tables for which the id must be unique
 	 */
 	public static final String TABLES = "identity_tables";
 
 	/**
 	 * The configuration parameter holding the primary key column
 	 * name of the generated id
 	 */
 	public static final String PK = "target_column";
 
     /**
      * The configuration parameter holding the catalog name
      */
     public static final String CATALOG = "catalog";
 
 	/**
 	 * The key under whcih to find the {@link org.hibernate.cfg.ObjectNameNormalizer} in the config param map.
 	 */
 	public static final String IDENTIFIER_NORMALIZER = "identifier_normalizer";
 
 	/**
 	 * The SQL required to create the underlying database objects.
 	 *
 	 * @param dialect The dialect against which to generate the create command(s)
 	 * @return The create command(s)
 	 * @throws HibernateException problem creating the create command(s)
 	 */
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException;
 
 	/**
 	 * The SQL required to remove the underlying database objects.
 	 *
 	 * @param dialect The dialect against which to generate the drop command(s)
 	 * @return The drop command(s)
 	 * @throws HibernateException problem creating the drop command(s)
 	 */
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException;
 
 	/**
 	 * Return a key unique to the underlying database objects. Prevents us from
 	 * trying to create/remove them multiple times.
 	 * 
 	 * @return Object an identifying key for this generator
 	 */
 	public Object generatorKey();
 
-	static final SQLStatementLogger SQL_STATEMENT_LOGGER = new SQLStatementLogger( false, false );
-
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
index 8cff01a2a3..1c8019b5df 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
@@ -1,231 +1,227 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.id;
 
-import java.io.Serializable;
-import java.sql.Connection;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.Properties;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.cfg.ObjectNameNormalizer;
-import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.jdbc.util.FormatStyle;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TransactionHelper;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.jdbc.ReturningWork;
+import org.hibernate.jdbc.util.FormatStyle;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+import java.io.Serializable;
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.Properties;
 
 /**
  * An <tt>IdentifierGenerator</tt> that uses a database
  * table to store the last generated value. It is not
  * intended that applications use this strategy directly.
  * However, it may be used to build other (efficient)
  * strategies. The returned type is any supported by
  * {@link IntegralDataTypeHolder}
  * <p/>
  * The value MUST be fetched in a separate transaction
  * from that of the main {@link SessionImplementor session}
  * transaction so the generator must be able to obtain a new
  * connection and commit it. Hence this implementation may only
  * be used when Hibernate is fetching connections, not when the
  * user is supplying connections.
  * <p/>
  * Again, the return types supported here are any of the ones
  * supported by {@link IntegralDataTypeHolder}.  This is new
  * as of 3.5.  Prior to that this generator only returned {@link Integer}
  * values.
  * <p/>
  * Mapping parameters supported: table, column
  *
  * @see TableHiLoGenerator
  * @author Gavin King
  */
-public class TableGenerator extends TransactionHelper
-	implements PersistentIdentifierGenerator, Configurable {
+public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
 	/* COLUMN and TABLE should be renamed but it would break the public API */
 	/** The column parameter */
 	public static final String COLUMN = "column";
 	
 	/** Default column name */
 	public static final String DEFAULT_COLUMN_NAME = "next_hi";
 	
 	/** The table parameter */
 	public static final String TABLE = "table";
 	
 	/** Default table name */	
 	public static final String DEFAULT_TABLE_NAME = "hibernate_unique_key";
 
 	private static final Logger log = LoggerFactory.getLogger(TableGenerator.class);
 
 	private Type identifierType;
 	private String tableName;
 	private String columnName;
 	private String query;
 	private String update;
 
 	public void configure(Type type, Properties params, Dialect dialect) {
 		identifierType = type;
 
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 
 		tableName = ConfigurationHelper.getString( TABLE, params, DEFAULT_TABLE_NAME );
 		if ( tableName.indexOf( '.' ) < 0 ) {
 			final String schemaName = normalizer.normalizeIdentifierQuoting( params.getProperty( SCHEMA ) );
 			final String catalogName = normalizer.normalizeIdentifierQuoting( params.getProperty( CATALOG ) );
 			tableName = Table.qualify(
 					dialect.quote( catalogName ),
 					dialect.quote( schemaName ),
 					dialect.quote( tableName )
 			);
 		}
 		else {
 			// if already qualified there is not much we can do in a portable manner so we pass it
 			// through and assume the user has set up the name correctly.
 		}
 
 		columnName = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						ConfigurationHelper.getString( COLUMN, params, DEFAULT_COLUMN_NAME )
 				)
 		);
 
 		query = "select " + 
 			columnName + 
 			" from " + 
 			dialect.appendLockHint(LockMode.PESSIMISTIC_WRITE, tableName) +
 			dialect.getForUpdateString();
 
 		update = "update " + 
 			tableName + 
 			" set " + 
 			columnName + 
 			" = ? where " + 
 			columnName + 
 			" = ?";
 	}
 
 	public synchronized Serializable generate(SessionImplementor session, Object object) {
 		return generateHolder( session ).makeValue();
 	}
 
 	protected IntegralDataTypeHolder generateHolder(SessionImplementor session) {
-		return (IntegralDataTypeHolder) doWorkInNewTransaction( session );
+		final SQLStatementLogger statementLogger = session
+				.getFactory()
+				.getServiceRegistry()
+				.getService( JdbcServices.class )
+				.getSqlStatementLogger();
+		return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
+				new ReturningWork<IntegralDataTypeHolder>() {
+					@Override
+					public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
+						IntegralDataTypeHolder value = buildHolder();
+						int rows;
+						do {
+							// The loop ensures atomicity of the
+							// select + update even for no transaction
+							// or read committed isolation level
+
+							statementLogger.logStatement( query, FormatStyle.BASIC.getFormatter() );
+							PreparedStatement qps = connection.prepareStatement( query );
+							try {
+								ResultSet rs = qps.executeQuery();
+								if ( !rs.next() ) {
+									String err = "could not read a hi value - you need to populate the table: " + tableName;
+									log.error(err);
+									throw new IdentifierGenerationException(err);
+								}
+								value.initialize( rs, 1 );
+								rs.close();
+							}
+							catch (SQLException e) {
+								log.error("could not read a hi value", e);
+								throw e;
+							}
+							finally {
+								qps.close();
+							}
+
+							statementLogger.logStatement( update, FormatStyle.BASIC.getFormatter() );
+							PreparedStatement ups = connection.prepareStatement(update);
+							try {
+								value.copy().increment().bind( ups, 1 );
+								value.bind( ups, 2 );
+								rows = ups.executeUpdate();
+							}
+							catch (SQLException sqle) {
+								log.error("could not update hi value in: " + tableName, sqle);
+								throw sqle;
+							}
+							finally {
+								ups.close();
+							}
+						}
+						while (rows==0);
+						return value;
+					}
+				},
+				true
+		);
 	}
 
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 			dialect.getCreateTableString() + " " + tableName + " ( " + columnName + " " + dialect.getTypeName(Types.INTEGER) + " )",
 			"insert into " + tableName + " values ( 0 )"
 		};
 	}
 
 	public String[] sqlDropStrings(Dialect dialect) {
 		StringBuffer sqlDropString = new StringBuffer( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			sqlDropString.append( "if exists " );
 		}
 		sqlDropString.append( tableName ).append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			sqlDropString.append( " if exists" );
 		}
 		return new String[] { sqlDropString.toString() };
 	}
 
 	public Object generatorKey() {
 		return tableName;
 	}
 
-	/**
-	 * Get the next value.
-	 *
-	 * @param conn The sql connection to use.
-	 * @param sql n/a
-	 *
-	 * @return Prior to 3.5 this method returned an {@link Integer}.  Since 3.5 it now
-	 * returns a {@link IntegralDataTypeHolder}
-	 *
-	 * @throws SQLException
-	 */
-	public Serializable doWorkInCurrentTransaction(Connection conn, String sql) throws SQLException {
-		IntegralDataTypeHolder value = buildHolder();
-		int rows;
-		do {
-			// The loop ensures atomicity of the
-			// select + update even for no transaction
-			// or read committed isolation level
-
-			sql = query;
-			SQL_STATEMENT_LOGGER.logStatement( sql, FormatStyle.BASIC );
-			PreparedStatement qps = conn.prepareStatement(query);
-			try {
-				ResultSet rs = qps.executeQuery();
-				if ( !rs.next() ) {
-					String err = "could not read a hi value - you need to populate the table: " + tableName;
-					log.error(err);
-					throw new IdentifierGenerationException(err);
-				}
-				value.initialize( rs, 1 );
-				rs.close();
-			}
-			catch (SQLException sqle) {
-				log.error("could not read a hi value", sqle);
-				throw sqle;
-			}
-			finally {
-				qps.close();
-			}
-
-			sql = update;
-			SQL_STATEMENT_LOGGER.logStatement( sql, FormatStyle.BASIC );
-			PreparedStatement ups = conn.prepareStatement(update);
-			try {
-				value.copy().increment().bind( ups, 1 );
-				value.bind( ups, 2 );
-				rows = ups.executeUpdate();
-			}
-			catch (SQLException sqle) {
-				log.error("could not update hi value in: " + tableName, sqle);
-				throw sqle;
-			}
-			finally {
-				ups.close();
-			}
-		}
-		while (rows==0);
-		return value;
-	}
-
 	protected IntegralDataTypeHolder buildHolder() {
 		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
index a1d4db56b4..b2bc06083d 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
@@ -1,582 +1,580 @@
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
 package org.hibernate.id.enhanced;
 
-import java.sql.Types;
-import java.sql.Connection;
-import java.sql.SQLException;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.util.Properties;
-import java.util.Collections;
-import java.util.Map;
-import java.io.Serializable;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
+import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
-import org.hibernate.engine.TransactionHelper;
+import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.id.Configurable;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.id.PersistentIdentifierGenerator;
-import org.hibernate.id.Configurable;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.type.Type;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.HibernateException;
-import org.hibernate.MappingException;
-import org.hibernate.LockOptions;
-import org.hibernate.LockMode;
-import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.util.FormatStyle;
 import org.hibernate.mapping.Table;
+import org.hibernate.type.Type;
 import org.hibernate.util.StringHelper;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+import java.io.Serializable;
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.Collections;
+import java.util.Map;
+import java.util.Properties;
 
 /**
  * An enhanced version of table-based id generation.
  * <p/>
  * Unlike the simplistic legacy one (which, btw, was only ever intended for subclassing
  * support) we "segment" the table into multiple values.  Thus a single table can
  * actually serve as the persistent storage for multiple independent generators.  One
  * approach would be to segment the values by the name of the entity for which we are
  * performing generation, which would mean that we would have a row in the generator
  * table for each entity name.  Or any configuration really; the setup is very flexible.
  * <p/>
  * In this respect it is very similar to the legacy
  * {@link org.hibernate.id.MultipleHiLoPerTableGenerator} in terms of the
  * underlying storage structure (namely a single table capable of holding
  * multiple generator values).  The differentiator is, as with
  * {@link SequenceStyleGenerator} as well, the externalized notion
  * of an optimizer.
  * <p/>
  * <b>NOTE</b> that by default we use a single row for all generators (based
  * on {@link #DEF_SEGMENT_VALUE}).  The configuration parameter
  * {@link #CONFIG_PREFER_SEGMENT_PER_ENTITY} can be used to change that to
  * instead default to using a row for each entity name.
  * <p/>
  * Configuration parameters:
  * <table>
  * 	 <tr>
  *     <td><b>NAME</b></td>
  *     <td><b>DEFAULT</b></td>
  *     <td><b>DESCRIPTION</b></td>
  *   </tr>
  *   <tr>
  *     <td>{@link #TABLE_PARAM}</td>
  *     <td>{@link #DEF_TABLE}</td>
  *     <td>The name of the table to use to store/retrieve values</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #VALUE_COLUMN_PARAM}</td>
  *     <td>{@link #DEF_VALUE_COLUMN}</td>
  *     <td>The name of column which holds the sequence value for the given segment</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #SEGMENT_COLUMN_PARAM}</td>
  *     <td>{@link #DEF_SEGMENT_COLUMN}</td>
  *     <td>The name of the column which holds the segment key</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #SEGMENT_VALUE_PARAM}</td>
  *     <td>{@link #DEF_SEGMENT_VALUE}</td>
  *     <td>The value indicating which segment is used by this generator; refers to values in the {@link #SEGMENT_COLUMN_PARAM} column</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #SEGMENT_LENGTH_PARAM}</td>
  *     <td>{@link #DEF_SEGMENT_LENGTH}</td>
  *     <td>The data length of the {@link #SEGMENT_COLUMN_PARAM} column; used for schema creation</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #INITIAL_PARAM}</td>
  *     <td>{@link #DEFAULT_INITIAL_VALUE}</td>
  *     <td>The initial value to be stored for the given segment</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #INCREMENT_PARAM}</td>
  *     <td>{@link #DEFAULT_INCREMENT_SIZE}</td>
  *     <td>The increment size for the underlying segment; see the discussion on {@link Optimizer} for more details.</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #OPT_PARAM}</td>
  *     <td><i>depends on defined increment size</i></td>
  *     <td>Allows explicit definition of which optimization strategy to use</td>
  *   </tr>
  * </table>
  *
  * @author Steve Ebersole
  */
-public class TableGenerator extends TransactionHelper implements PersistentIdentifierGenerator, Configurable {
+public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
 	private static final Logger log = LoggerFactory.getLogger( TableGenerator.class );
 
 	public static final String CONFIG_PREFER_SEGMENT_PER_ENTITY = "prefer_entity_table_as_segment_value";
 
 	public static final String TABLE_PARAM = "table_name";
 	public static final String DEF_TABLE = "hibernate_sequences";
 
 	public static final String VALUE_COLUMN_PARAM = "value_column_name";
 	public static final String DEF_VALUE_COLUMN = "next_val";
 
 	public static final String SEGMENT_COLUMN_PARAM = "segment_column_name";
 	public static final String DEF_SEGMENT_COLUMN = "sequence_name";
 
 	public static final String SEGMENT_VALUE_PARAM = "segment_value";
 	public static final String DEF_SEGMENT_VALUE = "default";
 
 	public static final String SEGMENT_LENGTH_PARAM = "segment_value_length";
 	public static final int DEF_SEGMENT_LENGTH = 255;
 
 	public static final String INITIAL_PARAM = "initial_value";
 	public static final int DEFAULT_INITIAL_VALUE = 1;
 
 	public static final String INCREMENT_PARAM = "increment_size";
 	public static final int DEFAULT_INCREMENT_SIZE = 1;
 
 	public static final String OPT_PARAM = "optimizer";
 
 
 	private Type identifierType;
 
 	private String tableName;
 
 	private String segmentColumnName;
 	private String segmentValue;
 	private int segmentValueLength;
 
 	private String valueColumnName;
 	private int initialValue;
 	private int incrementSize;
 
 	private String selectQuery;
 	private String insertQuery;
 	private String updateQuery;
 
 	private Optimizer optimizer;
 	private long accessCount = 0;
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object generatorKey() {
 		return tableName;
 	}
 
 	/**
 	 * Type mapping for the identifier.
 	 *
 	 * @return The identifier type mapping.
 	 */
 	public final Type getIdentifierType() {
 		return identifierType;
 	}
 
 	/**
 	 * The name of the table in which we store this generator's persistent state.
 	 *
 	 * @return The table name.
 	 */
 	public final String getTableName() {
 		return tableName;
 	}
 
 	/**
 	 * The name of the column in which we store the segment to which each row
 	 * belongs.  The value here acts as PK.
 	 *
 	 * @return The segment column name
 	 */
 	public final String getSegmentColumnName() {
 		return segmentColumnName;
 	}
 
 	/**
 	 * The value in {@link #getSegmentColumnName segment column} which
 	 * corresponding to this generator instance.  In other words this value
 	 * indicates the row in which this generator instance will store values.
 	 *
 	 * @return The segment value for this generator instance.
 	 */
 	public final String getSegmentValue() {
 		return segmentValue;
 	}
 
 	/**
 	 * The size of the {@link #getSegmentColumnName segment column} in the
 	 * underlying table.
 	 * <p/>
 	 * <b>NOTE</b> : should really have been called 'segmentColumnLength' or
 	 * even better 'segmentColumnSize'
 	 *
 	 * @return the column size.
 	 */
 	public final int getSegmentValueLength() {
 		return segmentValueLength;
 	}
 
 	/**
 	 * The name of the column in which we store our persistent generator value.
 	 *
 	 * @return The name of the value column.
 	 */
 	public final String getValueColumnName() {
 		return valueColumnName;
 	}
 
 	/**
 	 * The initial value to use when we find no previous state in the
 	 * generator table corresponding to our sequence.
 	 *
 	 * @return The initial value to use.
 	 */
 	public final int getInitialValue() {
 		return initialValue;
 	}
 
 	/**
 	 * The amount of increment to use.  The exact implications of this
 	 * depends on the {@link #getOptimizer() optimizer} being used.
 	 *
 	 * @return The increment amount.
 	 */
 	public final int getIncrementSize() {
 		return incrementSize;
 	}
 
 	/**
 	 * The optimizer being used by this generator.
 	 *
 	 * @return Out optimizer.
 	 */
 	public final Optimizer getOptimizer() {
 		return optimizer;
 	}
 
 	/**
 	 * Getter for property 'tableAccessCount'.  Only really useful for unit test
 	 * assertions.
 	 *
 	 * @return Value for property 'tableAccessCount'.
 	 */
 	public final long getTableAccessCount() {
 		return accessCount;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		identifierType = type;
 
 		tableName = determineGeneratorTableName( params, dialect );
 		segmentColumnName = determineSegmentColumnName( params, dialect );
 		valueColumnName = determineValueColumnName( params, dialect );
 
 		segmentValue = determineSegmentValue( params );
 
 		segmentValueLength = determineSegmentColumnSize( params );
 		initialValue = determineInitialValue( params );
 		incrementSize = determineIncrementSize( params );
 
 		this.selectQuery = buildSelectQuery( dialect );
 		this.updateQuery = buildUpdateQuery();
 		this.insertQuery = buildInsertQuery();
 
 		// if the increment size is greater than one, we prefer pooled optimization; but we
 		// need to see if the user prefers POOL or POOL_LO...
 		String defaultPooledOptimizerStrategy = ConfigurationHelper.getBoolean( Environment.PREFER_POOLED_VALUES_LO, params, false )
 				? OptimizerFactory.POOL_LO
 				: OptimizerFactory.POOL;
 		final String defaultOptimizerStrategy = incrementSize <= 1 ? OptimizerFactory.NONE : defaultPooledOptimizerStrategy;
 		final String optimizationStrategy = ConfigurationHelper.getString( OPT_PARAM, params, defaultOptimizerStrategy );
 		optimizer = OptimizerFactory.buildOptimizer(
 				optimizationStrategy,
 				identifierType.getReturnedClass(),
 				incrementSize,
 				ConfigurationHelper.getInt( INITIAL_PARAM, params, -1 )
 		);
 	}
 
 	/**
 	 * Determine the table name to use for the generator values.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getTableName()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect
 	 * @return The table name to use.
 	 */
 	protected String determineGeneratorTableName(Properties params, Dialect dialect) {
 		String name = ConfigurationHelper.getString( TABLE_PARAM, params, DEF_TABLE );
 		boolean isGivenNameUnqualified = name.indexOf( '.' ) < 0;
 		if ( isGivenNameUnqualified ) {
 			ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 			name = normalizer.normalizeIdentifierQuoting( name );
 			// if the given name is un-qualified we may neen to qualify it
 			String schemaName = normalizer.normalizeIdentifierQuoting( params.getProperty( SCHEMA ) );
 			String catalogName = normalizer.normalizeIdentifierQuoting( params.getProperty( CATALOG ) );
 			name = Table.qualify(
 					dialect.quote( catalogName ),
 					dialect.quote( schemaName ),
 					dialect.quote( name)
 			);
 		}
 		else {
 			// if already qualified there is not much we can do in a portable manner so we pass it
 			// through and assume the user has set up the name correctly.
 		}
 		return name;
 	}
 
 	/**
 	 * Determine the name of the column used to indicate the segment for each
 	 * row.  This column acts as the primary key.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getSegmentColumnName()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect
 	 * @return The name of the segment column
 	 */
 	protected String determineSegmentColumnName(Properties params, Dialect dialect) {
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 		String name = ConfigurationHelper.getString( SEGMENT_COLUMN_PARAM, params, DEF_SEGMENT_COLUMN );
 		return dialect.quote( normalizer.normalizeIdentifierQuoting( name ) );
 	}
 
 	/**
 	 * Determine the name of the column in which we will store the generator persistent value.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getValueColumnName()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect
 	 * @return The name of the value column
 	 */
 	protected String determineValueColumnName(Properties params, Dialect dialect) {
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 		String name = ConfigurationHelper.getString( VALUE_COLUMN_PARAM, params, DEF_VALUE_COLUMN );
 		return dialect.quote( normalizer.normalizeIdentifierQuoting( name ) );
 	}
 
 	/**
 	 * Determine the segment value corresponding to this generator instance.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getSegmentValue()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The name of the value column
 	 */
 	protected String determineSegmentValue(Properties params) {
 		String segmentValue = params.getProperty( SEGMENT_VALUE_PARAM );
 		if ( StringHelper.isEmpty( segmentValue ) ) {
 			segmentValue = determineDefaultSegmentValue( params );
 		}
 		return segmentValue;
 	}
 
 	/**
 	 * Used in the cases where {@link #determineSegmentValue} is unable to
 	 * determine the value to use.
 	 *
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The default segment value to use.
 	 */
 	protected String determineDefaultSegmentValue(Properties params) {
 		boolean preferSegmentPerEntity = ConfigurationHelper.getBoolean( CONFIG_PREFER_SEGMENT_PER_ENTITY, params, false );
 		String defaultToUse = preferSegmentPerEntity ? params.getProperty( TABLE ) : DEF_SEGMENT_VALUE;
 		log.info( "explicit segment value for id generator [" + tableName + '.' + segmentColumnName + "] suggested; using default [" + defaultToUse + "]" );
 		return defaultToUse;
 	}
 
 	/**
 	 * Determine the size of the {@link #getSegmentColumnName segment column}
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getSegmentValueLength()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The size of the segment column
 	 */
 	protected int determineSegmentColumnSize(Properties params) {
 		return ConfigurationHelper.getInt( SEGMENT_LENGTH_PARAM, params, DEF_SEGMENT_LENGTH );
 	}
 
 	protected int determineInitialValue(Properties params) {
 		return ConfigurationHelper.getInt( INITIAL_PARAM, params, DEFAULT_INITIAL_VALUE );
 	}
 
 	protected int determineIncrementSize(Properties params) {
 		return ConfigurationHelper.getInt( INCREMENT_PARAM, params, DEFAULT_INCREMENT_SIZE );
 	}
 
 	protected String buildSelectQuery(Dialect dialect) {
 		final String alias = "tbl";
 		String query = "select " + StringHelper.qualify( alias, valueColumnName ) +
 				" from " + tableName + ' ' + alias +
 				" where " + StringHelper.qualify( alias, segmentColumnName ) + "=?";
 		LockOptions lockOptions = new LockOptions( LockMode.PESSIMISTIC_WRITE );
 		lockOptions.setAliasSpecificLockMode( alias, LockMode.PESSIMISTIC_WRITE );
 		Map updateTargetColumnsMap = Collections.singletonMap( alias, new String[] { valueColumnName } );
 		return dialect.applyLocksToSql( query, lockOptions, updateTargetColumnsMap );
 	}
 
 	protected String buildUpdateQuery() {
 		return "update " + tableName +
 				" set " + valueColumnName + "=? " +
 				" where " + valueColumnName + "=? and " + segmentColumnName + "=?";
 	}
 
 	protected String buildInsertQuery() {
 		return "insert into " + tableName + " (" + segmentColumnName + ", " + valueColumnName + ") " + " values (?,?)";
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
+		final SQLStatementLogger statementLogger = session
+				.getFactory()
+				.getServiceRegistry()
+				.getService( JdbcServices.class )
+				.getSqlStatementLogger();
 		return optimizer.generate(
 				new AccessCallback() {
+					@Override
 					public IntegralDataTypeHolder getNextValue() {
-						return ( IntegralDataTypeHolder ) doWorkInNewTransaction( session );
+						return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
+								new ReturningWork<IntegralDataTypeHolder>() {
+									@Override
+									public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
+										IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
+										int rows;
+										do {
+											statementLogger.logStatement( selectQuery, FormatStyle.BASIC.getFormatter() );
+											PreparedStatement selectPS = connection.prepareStatement( selectQuery );
+											try {
+												selectPS.setString( 1, segmentValue );
+												ResultSet selectRS = selectPS.executeQuery();
+												if ( !selectRS.next() ) {
+													value.initialize( initialValue );
+													PreparedStatement insertPS = null;
+													try {
+														statementLogger.logStatement( insertQuery, FormatStyle.BASIC.getFormatter() );
+														insertPS = connection.prepareStatement( insertQuery );
+														insertPS.setString( 1, segmentValue );
+														value.bind( insertPS, 2 );
+														insertPS.execute();
+													}
+													finally {
+														if ( insertPS != null ) {
+															insertPS.close();
+														}
+													}
+												}
+												else {
+													value.initialize( selectRS, 1 );
+												}
+												selectRS.close();
+											}
+											catch ( SQLException e ) {
+												log.error( "could not read or init a hi value", e );
+												throw e;
+											}
+											finally {
+												selectPS.close();
+											}
+
+											statementLogger.logStatement( updateQuery, FormatStyle.BASIC.getFormatter() );
+											PreparedStatement updatePS = connection.prepareStatement( updateQuery );
+											try {
+												final IntegralDataTypeHolder updateValue = value.copy();
+												if ( optimizer.applyIncrementSizeToSourceValues() ) {
+													updateValue.add( incrementSize );
+												}
+												else {
+													updateValue.increment();
+												}
+												updateValue.bind( updatePS, 1 );
+												value.bind( updatePS, 2 );
+												updatePS.setString( 3, segmentValue );
+												rows = updatePS.executeUpdate();
+											}
+											catch ( SQLException e ) {
+												log.error( "could not updateQuery hi value in: " + tableName, e );
+												throw e;
+											}
+											finally {
+												updatePS.close();
+											}
+										}
+										while ( rows == 0 );
+
+										accessCount++;
+
+										return value;
+									}
+								},
+								true
+						);
 					}
 				}
 		);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
-	public Serializable doWorkInCurrentTransaction(Connection conn, String sql) throws SQLException {
-		IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
-		int rows;
-		do {
-			SQL_STATEMENT_LOGGER.logStatement( selectQuery, FormatStyle.BASIC );
-			PreparedStatement selectPS = conn.prepareStatement( selectQuery );
-			try {
-				selectPS.setString( 1, segmentValue );
-				ResultSet selectRS = selectPS.executeQuery();
-				if ( !selectRS.next() ) {
-					value.initialize( initialValue );
-					PreparedStatement insertPS = null;
-					try {
-						SQL_STATEMENT_LOGGER.logStatement( insertQuery, FormatStyle.BASIC );
-						insertPS = conn.prepareStatement( insertQuery );
-						insertPS.setString( 1, segmentValue );
-						value.bind( insertPS, 2 );
-						insertPS.execute();
-					}
-					finally {
-						if ( insertPS != null ) {
-							insertPS.close();
-						}
-					}
-				}
-				else {
-					value.initialize( selectRS, 1 );
-				}
-				selectRS.close();
-			}
-			catch ( SQLException sqle ) {
-				log.error( "could not read or init a hi value", sqle );
-				throw sqle;
-			}
-			finally {
-				selectPS.close();
-			}
-
-			SQL_STATEMENT_LOGGER.logStatement( updateQuery, FormatStyle.BASIC );
-			PreparedStatement updatePS = conn.prepareStatement( updateQuery );
-			try {
-				final IntegralDataTypeHolder updateValue = value.copy();
-				if ( optimizer.applyIncrementSizeToSourceValues() ) {
-					updateValue.add( incrementSize );
-				}
-				else {
-					updateValue.increment();
-				}
-				updateValue.bind( updatePS, 1 );
-				value.bind( updatePS, 2 );
-				updatePS.setString( 3, segmentValue );
-				rows = updatePS.executeUpdate();
-			}
-			catch ( SQLException sqle ) {
-				log.error( "could not updateQuery hi value in: " + tableName, sqle );
-				throw sqle;
-			}
-			finally {
-				updatePS.close();
-			}
-		}
-		while ( rows == 0 );
-
-		accessCount++;
-
-		return value;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 				new StringBuffer()
 						.append( dialect.getCreateTableString() )
 						.append( ' ' )
 						.append( tableName )
 						.append( " ( " )
 						.append( segmentColumnName )
 						.append( ' ' )
 						.append( dialect.getTypeName( Types.VARCHAR, segmentValueLength, 0, 0 ) )
 						.append( " not null " )
 						.append( ",  " )
 						.append( valueColumnName )
 						.append( ' ' )
 						.append( dialect.getTypeName( Types.BIGINT ) )
 						.append( ", primary key ( " )
 						.append( segmentColumnName )
 						.append( " ) ) " )
 						.toString()
 		};
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		StringBuffer sqlDropString = new StringBuffer().append( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			sqlDropString.append( "if exists " );
 		}
 		sqlDropString.append( tableName ).append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			sqlDropString.append( " if exists" );
 		}
 		return new String[] { sqlDropString.toString() };
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
index 931128d136..cbad28cb17 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
@@ -1,211 +1,198 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.id.enhanced;
 
-import java.io.Serializable;
-import java.sql.Connection;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.TransactionHelper;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
+import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.util.FormatStyle;
-import org.hibernate.jdbc.util.SQLStatementLogger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
 
 /**
  * Describes a table used to mimic sequence behavior
  *
  * @author Steve Ebersole
  */
-public class TableStructure extends TransactionHelper implements DatabaseStructure {
+public class TableStructure implements DatabaseStructure {
 	private static final Logger log = LoggerFactory.getLogger( TableStructure.class );
-	private static final SQLStatementLogger SQL_STATEMENT_LOGGER = new SQLStatementLogger( false, false );
 
 	private final String tableName;
 	private final String valueColumnName;
 	private final int initialValue;
 	private final int incrementSize;
 	private final Class numberType;
 	private final String selectQuery;
 	private final String updateQuery;
 
 	private boolean applyIncrementSizeToSourceValues;
 	private int accessCounter;
 
 	public TableStructure(
 			Dialect dialect,
 			String tableName,
 			String valueColumnName,
 			int initialValue,
 			int incrementSize,
 			Class numberType) {
 		this.tableName = tableName;
 		this.initialValue = initialValue;
 		this.incrementSize = incrementSize;
 		this.valueColumnName = valueColumnName;
 		this.numberType = numberType;
 
 		selectQuery = "select " + valueColumnName + " as id_val" +
 				" from " + dialect.appendLockHint( LockMode.PESSIMISTIC_WRITE, tableName ) +
 				dialect.getForUpdateString();
 
 		updateQuery = "update " + tableName +
 				" set " + valueColumnName + "= ?" +
 				" where " + valueColumnName + "=?";
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getName() {
 		return tableName;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int getInitialValue() {
 		return initialValue;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int getIncrementSize() {
 		return incrementSize;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int getTimesAccessed() {
 		return accessCounter;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void prepare(Optimizer optimizer) {
 		applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public AccessCallback buildCallback(final SessionImplementor session) {
 		return new AccessCallback() {
+			@Override
 			public IntegralDataTypeHolder getNextValue() {
-				return ( IntegralDataTypeHolder ) doWorkInNewTransaction( session );
+				return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
+						new ReturningWork<IntegralDataTypeHolder>() {
+							@Override
+							public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
+								final SQLStatementLogger statementLogger = session
+										.getFactory()
+										.getServiceRegistry()
+										.getService( JdbcServices.class )
+										.getSqlStatementLogger();
+								IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( numberType );
+								int rows;
+								do {
+									statementLogger.logStatement( selectQuery, FormatStyle.BASIC.getFormatter() );
+									PreparedStatement selectStatement = connection.prepareStatement( selectQuery );
+									try {
+										ResultSet selectRS = selectStatement.executeQuery();
+										if ( !selectRS.next() ) {
+											String err = "could not read a hi value - you need to populate the table: " + tableName;
+											log.error( err );
+											throw new IdentifierGenerationException( err );
+										}
+										value.initialize( selectRS, 1 );
+										selectRS.close();
+									}
+									catch ( SQLException sqle ) {
+										log.error( "could not read a hi value", sqle );
+										throw sqle;
+									}
+									finally {
+										selectStatement.close();
+									}
+
+									statementLogger.logStatement( updateQuery, FormatStyle.BASIC.getFormatter() );
+									PreparedStatement updatePS = connection.prepareStatement( updateQuery );
+									try {
+										final int increment = applyIncrementSizeToSourceValues ? incrementSize : 1;
+										final IntegralDataTypeHolder updateValue = value.copy().add( increment );
+										updateValue.bind( updatePS, 1 );
+										value.bind( updatePS, 2 );
+										rows = updatePS.executeUpdate();
+									}
+									catch ( SQLException e ) {
+										log.error( "could not updateQuery hi value in: " + tableName, e );
+										throw e;
+									}
+									finally {
+										updatePS.close();
+									}
+								} while ( rows == 0 );
+
+								accessCounter++;
+
+								return value;
+							}
+						},
+						true
+				);
 			}
 		};
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 				dialect.getCreateTableString() + " " + tableName + " ( " + valueColumnName + " " + dialect.getTypeName( Types.BIGINT ) + " )",
 				"insert into " + tableName + " values ( " + initialValue + " )"
 		};
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		StringBuffer sqlDropString = new StringBuffer().append( "drop table " );
 		if ( dialect.supportsIfExistsBeforeTableName() ) {
 			sqlDropString.append( "if exists " );
 		}
 		sqlDropString.append( tableName ).append( dialect.getCascadeConstraintsString() );
 		if ( dialect.supportsIfExistsAfterTableName() ) {
 			sqlDropString.append( " if exists" );
 		}
 		return new String[] { sqlDropString.toString() };
 	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	protected Serializable doWorkInCurrentTransaction(Connection conn, String sql) throws SQLException {
-		IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( numberType );
-		int rows;
-		do {
-			SQL_STATEMENT_LOGGER.logStatement( selectQuery, FormatStyle.BASIC );
-			PreparedStatement selectPS = conn.prepareStatement( selectQuery );
-			try {
-				ResultSet selectRS = selectPS.executeQuery();
-				if ( !selectRS.next() ) {
-					String err = "could not read a hi value - you need to populate the table: " + tableName;
-					log.error( err );
-					throw new IdentifierGenerationException( err );
-				}
-				value.initialize( selectRS, 1 );
-				selectRS.close();
-			}
-			catch ( SQLException sqle ) {
-				log.error( "could not read a hi value", sqle );
-				throw sqle;
-			}
-			finally {
-				selectPS.close();
-			}
-
-			SQL_STATEMENT_LOGGER.logStatement( updateQuery, FormatStyle.BASIC );
-			PreparedStatement updatePS = conn.prepareStatement( updateQuery );
-			try {
-				final int increment = applyIncrementSizeToSourceValues ? incrementSize : 1;
-				final IntegralDataTypeHolder updateValue = value.copy().add( increment );
-				updateValue.bind( updatePS, 1 );
-				value.bind( updatePS, 2 );
-				rows = updatePS.executeUpdate();
-			}
-			catch ( SQLException sqle ) {
-				log.error( "could not updateQuery hi value in: " + tableName, sqle );
-				throw sqle;
-			}
-			finally {
-				updatePS.close();
-			}
-		} while ( rows == 0 );
-
-		accessCounter++;
-
-		return value;
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/ReturningWork.java b/hibernate-core/src/main/java/org/hibernate/jdbc/ReturningWork.java
new file mode 100644
index 0000000000..83b9d4ced9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/ReturningWork.java
@@ -0,0 +1,46 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jdbc;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+
+/**
+ * A discrete piece of work following the lines of {@link Work} but returning a result.
+ *
+ * @author Steve Ebersole
+ */
+public interface ReturningWork<T> {
+	/**
+	 * Execute the discrete work encapsulated by this work instance using the supplied connection.
+	 *
+	 * @param connection The connection on which to perform the work.
+	 *
+	 * @return The work result
+	 * 
+	 * @throws SQLException Thrown during execution of the underlying JDBC interaction.
+	 * @throws org.hibernate.HibernateException Generally indicates a wrapped SQLException.
+	 */
+	public T execute(Connection connection) throws SQLException;
+}
