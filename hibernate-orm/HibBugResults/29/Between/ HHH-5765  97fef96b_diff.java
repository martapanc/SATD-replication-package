diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
index 6e030157e7..4a8eba6789 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
@@ -1,258 +1,265 @@
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
 package org.hibernate.engine;
 
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.sql.Connection;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.ConnectionReleaseMode;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.Region;
 import org.hibernate.cfg.Settings;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.stat.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
  * @see org.hibernate.impl.SessionFactoryImpl
  * @author Gavin King
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory {
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	public Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	public EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	public CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
+	 * Get the JdbcServices.
+	 * @return the JdbcServices
+	 */
+	public JdbcServices getJdbcServices();
+
+	/**
 	 * Get the SQL dialect.
 	 * <p/>
-	 * Shorthand for {@link #getSettings()}.{@link Settings#getDialect()}
+	 * Shorthand for {@link #getJdbcServices().getDialect()}.{@link JdbcServices#getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	public Interceptor getInterceptor();
 
 	public QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	public Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	public String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the connection provider
 	 */
 	public ConnectionProvider getConnectionProvider();
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	public String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	public String getImportedClassName(String name);
 
 
 	/**
 	 * Get the JTA transaction manager
 	 */
 	public TransactionManager getTransactionManager();
 
 
 	/**
 	 * Get the default query cache
 	 */
 	public QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	public QueryCache getQueryCache(String regionName) throws HibernateException;
 	
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	public UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 	
 	public NamedQueryDefinition getNamedQuery(String queryName);
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 	
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getSecondLevelCacheRegion(String regionName);
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	public Map getAllSecondLevelCacheRegions();
 	
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 */
 	public SQLExceptionConverter getSQLExceptionConverter();
 	   // TODO: deprecate???
 
 	/**
 	 * Retrieves the SQLExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionHelper for this SessionFactory.
 	 *
 	 */
 	public SQLExceptionHelper getSQLExceptionHelper();
 
 	public Settings getSettings();
 
 	/**
 	 * Get a nontransactional "current" session for Hibernate EntityManager
 	 */
 	public org.hibernate.classic.Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Open a session conforming to the given parameters.  Used mainly by
 	 * {@link org.hibernate.context.JTASessionContext} for current session processing.
 	 *
 	 * @param connection The external jdbc connection to use, if one (i.e., optional).
 	 * @param flushBeforeCompletionEnabled Should the session be auto-flushed
 	 * prior to transaction completion?
 	 * @param autoCloseSessionEnabled Should the session be auto-closed after
 	 * transaction completion?
 	 * @param connectionReleaseMode The release mode for managed jdbc connections.
 	 * @return An appropriate session.
 	 * @throws HibernateException
 	 */
 	public org.hibernate.classic.Session openSession(
 			final Connection connection,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode) throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	public FetchProfile getFetchProfile(String name);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
index 5a0837fa1f..64ebcd0635 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
@@ -1,250 +1,406 @@
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
 
+import java.io.IOException;
+import java.io.ObjectInputStream;
+import java.io.ObjectOutputStream;
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
+import org.hibernate.jdbc.BorrowedConnectionProxy;
 
 /**
  * LogicalConnectionImpl implementation
  *
  * @author Steve Ebersole
  */
 public class LogicalConnectionImpl implements LogicalConnectionImplementor {
 	private static final Logger log = LoggerFactory.getLogger( LogicalConnectionImpl.class );
 
 	private transient Connection physicalConnection;
+	private transient Connection borrowedConnection;
+
 	private final ConnectionReleaseMode connectionReleaseMode;
 	private final JdbcServices jdbcServices;
 	private final JdbcResourceRegistry jdbcResourceRegistry;
 	private final List<ConnectionObserver> observers = new ArrayList<ConnectionObserver>();
 	private boolean releasesEnabled = true;
 
 	private final boolean isUserSuppliedConnection;
 	private boolean isClosed;
 
 	public LogicalConnectionImpl(
 	        Connection userSuppliedConnection,
 	        ConnectionReleaseMode connectionReleaseMode,
 	        JdbcServices jdbcServices) {
-		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
-				! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
-			log.debug( "connection provider reports to not support aggressive release; overriding" );
-			connectionReleaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
-		}
 		this.physicalConnection = userSuppliedConnection;
-		this.connectionReleaseMode = connectionReleaseMode;
+		this.connectionReleaseMode =
+				determineConnectionReleaseMode(
+						userSuppliedConnection != null, connectionReleaseMode, jdbcServices
+				);
 		this.jdbcServices = jdbcServices;
 		this.jdbcResourceRegistry = new JdbcResourceRegistryImpl( jdbcServices.getSqlExceptionHelper() );
 
 		this.isUserSuppliedConnection = ( userSuppliedConnection != null );
 	}
 
+	public LogicalConnectionImpl(
+			ConnectionReleaseMode connectionReleaseMode,
+			JdbcServices jdbcServices,
+			boolean isUserSuppliedConnection,
+			boolean isClosed) {
+		this.connectionReleaseMode = determineConnectionReleaseMode(
+				isUserSuppliedConnection, connectionReleaseMode, jdbcServices
+		);
+		this.jdbcServices = jdbcServices;
+		this.jdbcResourceRegistry = new JdbcResourceRegistryImpl( jdbcServices.getSqlExceptionHelper() );
+		this.isUserSuppliedConnection = isUserSuppliedConnection;
+		this.isClosed = isClosed;
+	}
+
+	private static ConnectionReleaseMode determineConnectionReleaseMode(boolean isUserSuppliedConnection,
+																		ConnectionReleaseMode connectionReleaseMode,
+																		JdbcServices jdbcServices) {
+		if ( isUserSuppliedConnection ) {
+			return ConnectionReleaseMode.ON_CLOSE;
+		}
+		else if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
+				! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
+			log.debug( "connection provider reports to not support aggressive release; overriding" );
+			return ConnectionReleaseMode.AFTER_TRANSACTION;
+		}
+		else {
+			return connectionReleaseMode;
+		}
+	}
+
 	/**
 	 * {@inheritDoc}
 	 */
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
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
-		log.trace( "closing logical connection" );
 		Connection c = physicalConnection;
-		if ( !isUserSuppliedConnection && physicalConnection != null ) {
-			jdbcResourceRegistry.close();
-			releaseConnection();
-		}
-		// not matter what
-		physicalConnection = null;
-		isClosed = true;
-		for ( ConnectionObserver observer : observers ) {
-			observer.logicalConnectionClosed();
+		try {
+			releaseBorrowedConnection();
+			log.trace( "closing logical connection" );
+			if ( !isUserSuppliedConnection && physicalConnection != null ) {
+				jdbcResourceRegistry.close();
+				releaseConnection();
+			}
+			return c;
 		}
-		log.trace( "logical connection closed" );
-		return c;
+		finally {
+			// no matter what
+			physicalConnection = null;
+			isClosed = true;
+			log.trace( "logical connection closed" );
+			for ( ConnectionObserver observer : observers ) {
+				observer.logicalConnectionClosed();
+			}
+		}			
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
+	public boolean isUserSuppliedConnection() {
+		return isUserSuppliedConnection;
+	}
+
+	public boolean hasBorrowedConnection() {
+		return borrowedConnection != null;
+	}
+
+	public Connection borrowConnection() {
+		if ( isClosed ) {
+			throw new HibernateException( "connection has been closed" );
+		}
+		if ( isUserSuppliedConnection() ) {
+			return physicalConnection;
+		}
+		else {
+			if ( borrowedConnection == null ) {
+				borrowedConnection = BorrowedConnectionProxy.generateProxy( this );
+			}
+			return borrowedConnection;
+		}
+	}
+
+	public void releaseBorrowedConnection() {
+		if ( borrowedConnection != null ) {
+			try {
+				BorrowedConnectionProxy.renderUnuseable( borrowedConnection );
+			}
+			finally {
+				borrowedConnection = null;
+			}
+		}
+	}
+
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
 	}
 
 	public void disableReleases() {
 		log.trace( "disabling releases" );
 		releasesEnabled = false;
 	}
 
 	public void enableReleases() {
 		log.trace( "(re)enabling releases" );
 		releasesEnabled = true;
-		afterStatementExecution();
+		//FIXME: uncomment after new batch stuff is integrated!!!
+		//afterStatementExecution();
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
+		if ( physicalConnection == null ) {
+			return;
+		}
 		try {
-			if ( !physicalConnection.isClosed() ) {
+			if ( ! physicalConnection.isClosed() ) {
 				getJdbcServices().getSqlExceptionHelper().logAndClearWarnings( physicalConnection );
 			}
-			for ( ConnectionObserver observer : observers ) {
-				observer.physicalConnectionReleased();
+			if ( !isUserSuppliedConnection ) {
+				getJdbcServices().getConnectionProvider().closeConnection( physicalConnection );
 			}
-			getJdbcServices().getConnectionProvider().closeConnection( physicalConnection );
-			physicalConnection = null;
 			log.debug( "released JDBC connection" );
 		}
 		catch (SQLException sqle) {
 			throw getJdbcServices().getSqlExceptionHelper().convert( sqle, "Could not close connection" );
 		}
+		finally {
+			physicalConnection = null;
+		}
+		log.debug( "released JDBC connection" );
+		for ( ConnectionObserver observer : observers ) {
+			observer.physicalConnectionReleased();
+		}
+	}
+
+	/**
+	 * Manually disconnect the underlying JDBC Connection.  The assumption here
+	 * is that the manager will be reconnected at a later point in time.
+	 *
+	 * @return The connection mantained here at time of disconnect.  Null if
+	 * there was no connection cached internally.
+	 */
+	public Connection manualDisconnect() {
+		if ( isClosed ) {
+			throw new IllegalStateException( "cannot manually disconnect because logical connection is already closed" );
+		}
+		Connection c = physicalConnection;
+		releaseConnection();
+		return c;
 	}
+
+	/**
+	 * Manually reconnect the underlying JDBC Connection.  Should be called at
+	 * some point after manualDisconnect().
+	 * <p/>
+	 * This form is used for user-supplied connections.
+	 */
+	public void reconnect(Connection suppliedConnection) {
+		if ( isClosed ) {
+			throw new IllegalStateException( "cannot manually reconnect because logical connection is already closed" );
+		}
+		if ( isUserSuppliedConnection ) {
+			if ( suppliedConnection == null ) {
+				throw new IllegalArgumentException( "cannot reconnect a null user-supplied connection" );
+			}
+			else if ( suppliedConnection == physicalConnection ) {
+				log.warn( "reconnecting the same connection that is already connected; should this connection have been disconnected?" );
+			}
+			else if ( physicalConnection != null ) {
+				throw new IllegalArgumentException(
+						"cannot reconnect to a new user-supplied connection because currently connected; must disconnect before reconnecting."
+				);
+			}
+			physicalConnection = suppliedConnection;
+			log.debug( "reconnected JDBC connection" );
+		}
+		else {
+			if ( suppliedConnection != null ) {
+				throw new IllegalStateException( "unexpected user-supplied connection" );
+			}
+			log.debug( "called reconnect() with null connection (not user-supplied)" );
+		}
+	}
+
+	public boolean isReadyForSerialization() {
+		return isUserSuppliedConnection() ?
+				! isPhysicallyConnected() :
+				! getResourceRegistry().hasRegisteredResources()
+				;
+	}
+
+	public void serialize(ObjectOutputStream oos) throws IOException {
+		oos.writeBoolean( isUserSuppliedConnection );
+		oos.writeBoolean( isClosed );
+	}
+
+	public static LogicalConnectionImpl deserialize(
+			ObjectInputStream ois,
+			JdbcServices jdbcServices,
+			ConnectionReleaseMode connectionReleaseMode	) throws IOException {
+		return new LogicalConnectionImpl(
+				connectionReleaseMode,
+				jdbcServices,
+				ois.readBoolean(),
+				ois.readBoolean()
+		);
+ 	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
index 183c88f032..fc17909ff3 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
@@ -1,70 +1,71 @@
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
 
+import java.io.Serializable;
 import java.sql.Connection;
 
 /**
  * LogicalConnection contract
  *
  * @author Steve Ebersole
  */
-public interface LogicalConnection {
+public interface LogicalConnection extends Serializable {
 	/**
 	 * Is this logical connection open?  Another phraseology sometimes used is: "are we
 	 * logically connected"?
 	 *
 	 * @return True if logically connected; false otherwise.
 	 */
 	public boolean isOpen();
 
 	/**
 	 * Is this logical connection instance "physically" connected.  Meaning
 	 * do we currently internally have a cached connection.
 	 *
 	 * @return True if physically connected; false otherwise.
 	 */
 	public boolean isPhysicallyConnected();
 
 	/**
 	 * Retrieves the connection currently "logically" managed by this LogicalConnectionImpl.
 	 * <p/>
 	 * Note, that we may need to obtain a connection to return here if a
 	 * connection has either not yet been obtained (non-UserSuppliedConnectionProvider)
 	 * or has previously been aggressively released.
 	 *
 	 * @return The current Connection.
 	 */
 	public Connection getConnection();
 
 	/**
 	 * Release the underlying connection and clean up any other resources associated
 	 * with this logical connection.
 	 * <p/>
 	 * This leaves the logical connection in a "no longer useable" state.
 	 *
 	 * @return The physical connection which was being used.
 	 */
 	public Connection close();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index fc40f146e0..703b81deed 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1344 +1,1344 @@
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
 package org.hibernate.impl;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import javax.transaction.TransactionManager;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.TypeHelper;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.context.CurrentSessionContext;
 import org.hibernate.context.JTASessionContext;
 import org.hibernate.context.ManagedSessionContext;
 import org.hibernate.context.ThreadLocalSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.event.EventListeners;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.PersisterFactory;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServicesRegistry;
 import org.hibernate.stat.ConcurrentStatisticsImpl;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.EmptyIterator;
 import org.hibernate.util.ReflectHelper;
 
 
 /**
  * Concrete implementation of the <tt>SessionFactory</tt> interface. Has the following
  * responsibilities
  * <ul>
  * <li>caches configuration settings (immutably)
  * <li>caches "compiled" mappings ie. <tt>EntityPersister</tt>s and
  *     <tt>CollectionPersister</tt>s (immutable)
  * <li>caches "compiled" queries (memory sensitive cache)
  * <li>manages <tt>PreparedStatement</tt>s
  * <li> delegates JDBC <tt>Connection</tt> management to the <tt>ConnectionProvider</tt>
  * <li>factory for instances of <tt>SessionImpl</tt>
  * </ul>
  * This class must appear immutable to clients, even if it does all kinds of caching
  * and pooling under the covers. It is crucial that the class is not only thread
  * safe, but also highly concurrent. Synchronization must be used extremely sparingly.
  *
  * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.classic.Session
  * @see org.hibernate.hql.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl implements SessionFactory, SessionFactoryImplementor {
 
 	private static final Logger log = LoggerFactory.getLogger(SessionFactoryImpl.class);
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map collectionPersisters;
 	private final transient Map collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map identifierGenerators;
 	private final transient Map namedQueries;
 	private final transient Map namedSqlQueries;
 	private final transient Map sqlResultSetMappings;
 	private final transient Map filters;
 	private final transient Map fetchProfiles;
 	private final transient Map imports;
 	private final transient Interceptor interceptor;
 	private final transient ServicesRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient TransactionManager transactionManager;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map queryCaches;
 	private final transient Map allCacheRegions = new HashMap();
 	private final transient Statistics statistics;
 	private final transient EventListeners eventListeners;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient EntityNotFoundDelegate entityNotFoundDelegate;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserver observer;
 	private final transient HashMap entityNameResolvers = new HashMap();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient Cache cacheAccess = new CacheImpl();
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 
 	public SessionFactoryImpl(
 			Configuration cfg,
 	        Mapping mapping,
 			ServicesRegistry serviceRegistry,
 	        Settings settings,
 	        EventListeners listeners,
 			SessionFactoryObserver observer) throws HibernateException {
 		log.info("building session factory");
 
 		this.statistics = new ConcurrentStatisticsImpl( this );
 		getStatistics().setStatisticsEnabled( settings.isStatisticsEnabled() );
 		log.debug( "Statistics initialized [enabled={}]}", settings.isStatisticsEnabled() );
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 		this.interceptor = cfg.getInterceptor();
 		this.serviceRegistry = serviceRegistry;
 		this.settings = settings;
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
         this.eventListeners = listeners;
 		this.observer = observer != null ? observer : new SessionFactoryObserver() {
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 			public void sessionFactoryClosed(SessionFactory factory) {
 			}
 		};
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		if ( log.isDebugEnabled() ) {
 			log.debug("Session factory constructed with filter configurations : " + filters);
 		}
 
 		if ( log.isDebugEnabled() ) {
 			log.debug(
 					"instantiating session factory with properties: " + properties
 			);
 		}
 
 		// Caches
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		Iterator classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			PersistentClass model = (PersistentClass) classes.next();
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						cfg.getIdentifierGeneratorFactory(),
 						getDialect(),
 				        settings.getDefaultCatalogName(),
 				        settings.getDefaultSchemaName(),
 				        (RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 
 		entityPersisters = new HashMap();
 		Map entityAccessStrategies = new HashMap();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			final PersistentClass model = (PersistentClass) classes.next();
 			model.prepareTemporaryTables( mapping, getDialect() );
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) entityAccessStrategies.get( cacheRegionName );
 			if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final AccessType accessType = AccessType.parse( model.getCacheConcurrencyStrategy() );
 				if ( accessType != null ) {
 					log.trace( "Building cache for entity data [" + model.getEntityName() + "]" );
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = PersisterFactory.createClassPersister( model, accessStrategy, this, mapping );
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.parse( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				log.trace( "Building cache for collection data [" + model.getRole() + "]" );
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = PersisterFactory.createCollectionPersister( cfg, model, accessStrategy, this) ;
 			collectionPersisters.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = ( Set ) tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = ( Set ) tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		namedQueries = new HashMap( cfg.getNamedQueries() );
 		namedSqlQueries = new HashMap( cfg.getNamedSQLQueries() );
 		sqlResultSetMappings = new HashMap( cfg.getSqlResultSetMappings() );
 		imports = new HashMap( cfg.getImports() );
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryObjectFactory.addInstance(uuid, name, this, properties);
 
 		log.debug("instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( getJdbcServices(), cfg ).create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( getJdbcServices(), cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( getJdbcServices(), cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( getJdbcServices(), cfg );
 		}
 
 		if ( settings.getTransactionManagerLookup()!=null ) {
 			log.debug("obtaining JTA TransactionManager");
 			transactionManager = settings.getTransactionManagerLookup().getTransactionManager(properties);
 		}
 		else {
 			if ( settings.getTransactionFactory().isTransactionManagerRequired() ) {
 				throw new HibernateException("The chosen transaction strategy requires access to the JTA TransactionManager");
 			}
 			transactionManager = null;
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(settings, properties);
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache(null, updateTimestampsCache, settings, properties);
 			queryCaches = new HashMap();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			Map errors = checkNamedQueries();
 			if ( !errors.isEmpty() ) {
 				Set keys = errors.keySet();
 				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
 				for ( Iterator iterator = keys.iterator() ; iterator.hasNext() ; ) {
 					String queryName = ( String ) iterator.next();
 					HibernateException e = ( HibernateException ) errors.get( queryName );
 					failingQueries.append( queryName );
 					if ( iterator.hasNext() ) {
 						failingQueries.append( ", " );
 					}
 					log.error( "Error in named query: " + queryName, e );
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// EntityNotFoundDelegate
 		EntityNotFoundDelegate entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 		if ( entityNotFoundDelegate == null ) {
 			entityNotFoundDelegate = new EntityNotFoundDelegate() {
 				public void handleEntityNotFound(String entityName, Serializable id) {
 					throw new ObjectNotFoundException( id, entityName );
 				}
 			};
 		}
 		this.entityNotFoundDelegate = entityNotFoundDelegate;
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			Iterator fetches = mappingProfile.getFetches().iterator();
 			while ( fetches.hasNext() ) {
 				final org.hibernate.mapping.FetchProfile.Fetch mappingFetch =
 						( org.hibernate.mapping.FetchProfile.Fetch ) fetches.next();
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = ( EntityPersister ) ( entityName == null ? null : entityPersisters.get( entityName ) );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || !associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	private void registerEntityNameResolvers(EntityPersister persister) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizerMapping() == null ) {
 			return;
 		}
 		Iterator itr = persister.getEntityMetamodel().getTuplizerMapping().iterateTuplizers();
 		while ( itr.hasNext() ) {
 			final EntityTuplizer tuplizer = ( EntityTuplizer ) itr.next();
 			registerEntityNameResolvers( tuplizer );
 		}
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( int i = 0; i < resolvers.length; i++ ) {
 			registerEntityNameResolver( resolvers[i], tuplizer.getEntityMode() );
 		}
 	}
 
 	public void registerEntityNameResolver(EntityNameResolver resolver, EntityMode entityMode) {
 		LinkedHashSet resolversForMode = ( LinkedHashSet ) entityNameResolvers.get( entityMode );
 		if ( resolversForMode == null ) {
 			resolversForMode = new LinkedHashSet();
 			entityNameResolvers.put( entityMode, resolversForMode );
 		}
 		resolversForMode.add( resolver );
 	}
 
 	public Iterator iterateEntityNameResolvers(EntityMode entityMode) {
 		Set actualEntityNameResolvers = ( Set ) entityNameResolvers.get( entityMode );
 		return actualEntityNameResolvers == null
 				? EmptyIterator.INSTANCE
 				: actualEntityNameResolvers.iterator();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map checkNamedQueries() throws HibernateException {
 		Map errors = new HashMap();
 
 		// Check named HQL queries
 		log.debug("Checking " + namedQueries.size() + " named HQL queries");
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				log.debug("Checking named query: " + queryName);
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, CollectionHelper.EMPTY_MAP );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 
 		log.debug("Checking " + namedSqlQueries.size() + " named SQL queries");
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				log.debug("Checking named SQL query: " + queryName);
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( qd.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = ( ResultSetMappingDefinition ) sqlResultSetMappings.get( qd.getResultSetRef() );
 					if ( definition == null ) {
 						throw new MappingException( "Unable to find resultset-ref definition: " + qd.getResultSetRef() );
 					}
 					spec = new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        definition.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				else {
 					spec =  new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        qd.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				queryPlanCache.getNativeSQLQueryPlan( spec );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 
 		return errors;
 	}
 
 	public StatelessSession openStatelessSession() {
 		return new StatelessSessionImpl( null, this );
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return new StatelessSessionImpl( connection, this );
 	}
 
 	private SessionImpl openSession(
 		Connection connection,
 	    boolean autoClose,
 	    long timestamp,
 	    Interceptor sessionLocalInterceptor
 	) {
 		return new SessionImpl(
 		        connection,
 		        this,
 		        autoClose,
 		        timestamp,
 		        sessionLocalInterceptor == null ? interceptor : sessionLocalInterceptor,
 		        settings.getDefaultEntityMode(),
 		        settings.isFlushBeforeCompletionEnabled(),
 		        settings.isAutoCloseSessionEnabled(),
 		        settings.getConnectionReleaseMode()
 			);
 	}
 
 	public org.hibernate.classic.Session openSession(Connection connection, Interceptor sessionLocalInterceptor) {
 		return openSession(connection, false, Long.MIN_VALUE, sessionLocalInterceptor);
 	}
 
 	public org.hibernate.classic.Session openSession(Interceptor sessionLocalInterceptor)
 	throws HibernateException {
 		// note that this timestamp is not correct if the connection provider
 		// returns an older JDBC connection that was associated with a
 		// transaction that was already begun before openSession() was called
 		// (don't know any possible solution to this!)
 		long timestamp = settings.getRegionFactory().nextTimestamp();
 		return openSession( null, true, timestamp, sessionLocalInterceptor );
 	}
 
 	public org.hibernate.classic.Session openSession(Connection connection) {
 		return openSession(connection, interceptor); //prevents this session from adding things to cache
 	}
 
 	public org.hibernate.classic.Session openSession() throws HibernateException {
 		return openSession(interceptor);
 	}
 
 	public org.hibernate.classic.Session openTemporarySession() throws HibernateException {
 		return new SessionImpl(
 				null,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        false,
 		        false,
 		        ConnectionReleaseMode.AFTER_STATEMENT
 			);
 	}
 
 	public org.hibernate.classic.Session openSession(
 			final Connection connection,
 	        final boolean flushBeforeCompletionEnabled,
 	        final boolean autoCloseSessionEnabled,
 	        final ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
 		return new SessionImpl(
 				connection,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        flushBeforeCompletionEnabled,
 		        autoCloseSessionEnabled,
 		        connectionReleaseMode
 			);
 	}
 
 	public org.hibernate.classic.Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = (EntityPersister) entityPersisters.get(entityName);
 		if (result==null) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = (CollectionPersister) collectionPersisters.get(role);
 		if (result==null) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	public Settings getSettings() {
 		return settings;
 	}
 
+	public JdbcServices getJdbcServices() {
+		return serviceRegistry.getService( JdbcServices.class );
+	}
+
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
-		return serviceRegistry.getService( JdbcServices.class ).getDialect();
+		return getJdbcServices().getDialect();
 	}
 
 	public Interceptor getInterceptor()
 	{
 		return interceptor;
 	}
 
 	public TransactionFactory getTransactionFactory() {
 		return settings.getTransactionFactory();
 	}
 
 	public TransactionManager getTransactionManager() {
 		return transactionManager;
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	public SQLExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	// from javax.naming.Referenceable
 	public Reference getReference() throws NamingException {
 		log.debug("Returning a Reference to the SessionFactory");
 		return new Reference(
 			SessionFactoryImpl.class.getName(),
 		    new StringRefAddr("uuid", uuid),
 		    SessionFactoryObjectFactory.class.getName(),
 		    null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
 		log.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryObjectFactory.getInstance(uuid);
 		if (result==null) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryObjectFactory.getNamedInstance(name);
 			if (result==null) {
 				throw new InvalidObjectException("Could not find a SessionFactory named: " + name);
 			}
 			else {
 				log.debug("resolved SessionFactory by name");
 			}
 		}
 		else {
 			log.debug("resolved SessionFactory by uid");
 		}
 		return result;
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return (NamedQueryDefinition) namedQueries.get(queryName);
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return (NamedSQLQueryDefinition) namedSqlQueries.get(queryName);
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return (ResultSetMappingDefinition) sqlResultSetMappings.get(resultSetName);
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		log.trace("deserializing");
 		in.defaultReadObject();
 		log.debug("deserialized: " + uuid);
 	}
 
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		log.debug("serializing: " + uuid);
 		out.defaultWriteObject();
 		log.trace("serialized");
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return (CollectionMetadata) collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return (ClassMetadata) classMetadata.get(entityName);
 	}
 
 	/**
 	 * Return the names of all persistent (mapped) classes that extend or implement the
 	 * given class or interface, accounting for implicit/explicit polymorphism settings
 	 * and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList results = new ArrayList();
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			//test this entity to see if we must query it
 			EntityPersister testPersister = (EntityPersister) iter.next();
 			if ( testPersister instanceof Queryable ) {
 				Queryable testQueryable = (Queryable) testPersister;
 				String testClassName = testQueryable.getEntityName();
 				boolean isMappedClass = className.equals(testClassName);
 				if ( testQueryable.isExplicitPolymorphism() ) {
 					if ( isMappedClass ) {
 						return new String[] {className}; //NOTE EARLY EXIT
 					}
 				}
 				else {
 					if (isMappedClass) {
 						results.add(testClassName);
 					}
 					else {
 						final Class mappedClass = testQueryable.getMappedClass( EntityMode.POJO );
 						if ( mappedClass!=null && clazz.isAssignableFrom( mappedClass ) ) {
 							final boolean assignableSuperclass;
 							if ( testQueryable.isInherited() ) {
 								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass( EntityMode.POJO);
 								assignableSuperclass = clazz.isAssignableFrom(mappedSuperclass);
 							}
 							else {
 								assignableSuperclass = false;
 							}
 							if ( !assignableSuperclass ) {
 								results.add( testClassName );
 							}
 						}
 					}
 				}
 			}
 		}
 		return (String[]) results.toArray( new String[ results.size() ] );
 	}
 
 	public String getImportedClassName(String className) {
 		String result = (String) imports.get(className);
 		if (result==null) {
 			try {
 				ReflectHelper.classForName(className);
 				return className;
 			}
 			catch (ClassNotFoundException cnfe) {
 				return null;
 			}
 		}
 		else {
 			return result;
 		}
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return classMetadata;
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return collectionMetadata;
 	}
 
 	public Type getReferencedPropertyType(String className, String propertyName)
 		throws MappingException {
 		return getEntityPersister(className).getPropertyType(propertyName);
 	}
 
-	private JdbcServices getJdbcServices() {
-		return serviceRegistry.getService( JdbcServices.class );
-	}
-
 	public ConnectionProvider getConnectionProvider() {
 		return serviceRegistry.getService( JdbcServices.class ).getConnectionProvider();
 	}
 
 	/**
 	 * Closes the session factory, releasing all held resources.
 	 *
 	 * <ol>
 	 * <li>cleans up used cache regions and "stops" the cache provider.
 	 * <li>close the JDBC connection
 	 * <li>remove the JNDI binding
 	 * </ol>
 	 *
 	 * Note: Be aware that the sessionfactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			log.trace( "already closed" );
 			return;
 		}
 
 		log.info("closing");
 
 		isClosed = true;
 
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			EntityPersister p = (EntityPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = (CollectionPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		if ( settings.isQueryCacheEnabled() )  {
 			queryCache.destroy();
 
 			iter = queryCaches.values().iterator();
 			while ( iter.hasNext() ) {
 				QueryCache cache = (QueryCache) iter.next();
 				cache.destroy();
 			}
 			updateTimestampsCache.destroy();
 		}
 
 		settings.getRegionFactory().stop();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryObjectFactory.removeInstance(uuid, name, properties);
 
 		observer.sessionFactoryClosed( this );
 		eventListeners.destroyListeners();
 	}
 
 	private class CacheImpl implements Cache {
 		public boolean containsEntity(Class entityClass, Serializable identifier) {
 			return containsEntity( entityClass.getName(), identifier );
 		}
 
 		public boolean containsEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( identifier, p ) );
 		}
 
 		public void evictEntity(Class entityClass, Serializable identifier) {
 			evictEntity( entityClass.getName(), identifier );
 		}
 
 		public void evictEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug( 
 							"evicting second-level cache: " +
 									MessageHelper.infoString( p, identifier, SessionFactoryImpl.this )
 					);
 				}
 				p.getCacheAccessStrategy().evict( buildCacheKey( identifier, p ) );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable identifier, EntityPersister p) {
 			return new CacheKey(
 					identifier,
 					p.getIdentifierType(),
 					p.getRootEntityName(),
 					EntityMode.POJO,
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictEntityRegion(Class entityClass) {
 			evictEntityRegion( entityClass.getName() );
 		}
 
 		public void evictEntityRegion(String entityName) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug( "evicting second-level cache: " + p.getEntityName() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictEntityRegions() {
 			Iterator entityNames = entityPersisters.keySet().iterator();
 			while ( entityNames.hasNext() ) {
 				evictEntityRegion( ( String ) entityNames.next() );
 			}
 		}
 
 		public boolean containsCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( ownerIdentifier, p ) );
 		}
 
 		public void evictCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug(
 							"evicting second-level cache: " +
 									MessageHelper.collectionInfoString(p, ownerIdentifier, SessionFactoryImpl.this)
 					);
 				}
 				CacheKey cacheKey = buildCacheKey( ownerIdentifier, p );
 				p.getCacheAccessStrategy().evict( cacheKey );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable ownerIdentifier, CollectionPersister p) {
 			return new CacheKey(
 					ownerIdentifier,
 					p.getKeyType(),
 					p.getRole(),
 					EntityMode.POJO,
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictCollectionRegion(String role) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug( "evicting second-level cache: " + p.getRole() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictCollectionRegions() {
 			Iterator collectionRoles = collectionPersisters.keySet().iterator();
 			while ( collectionRoles.hasNext() ) {
 				evictCollectionRegion( ( String ) collectionRoles.next() );
 			}
 		}
 
 		public boolean containsQuery(String regionName) {
 			return queryCaches.get( regionName ) != null;
 		}
 
 		public void evictDefaultQueryRegion() {
 			if ( settings.isQueryCacheEnabled() ) {
 				queryCache.clear();
 			}
 		}
 
 		public void evictQueryRegion(String regionName) {
 			if ( regionName == null ) {
 				throw new NullPointerException(
 						"Region-name cannot be null (use Cache#evictDefaultQueryRegion to evict the default query cache)"
 				);
 			}
 			else {
 				synchronized ( allCacheRegions ) {
 					if ( settings.isQueryCacheEnabled() ) {
 						QueryCache namedQueryCache = ( QueryCache ) queryCaches.get( regionName );
 						if ( namedQueryCache != null ) {
 							namedQueryCache.clear();
 							// TODO : cleanup entries in queryCaches + allCacheRegions ?
 						}
 					}
 				}
 			}
 		}
 
 		public void evictQueryRegions() {
 			synchronized ( allCacheRegions ) {
 				Iterator regions = queryCaches.values().iterator();
 				while ( regions.hasNext() ) {
 					QueryCache cache = ( QueryCache ) regions.next();
 					cache.clear();
 					// TODO : cleanup entries in queryCaches + allCacheRegions ?
 				}
 			}
 		}
 	}
 
 	public Cache getCache() {
 		return cacheAccess;
 	}
 
 	public void evictEntity(String entityName, Serializable id) throws HibernateException {
 		getCache().evictEntity( entityName, id );
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getCache().evictEntityRegion( entityName );
 	}
 
 	public void evict(Class persistentClass, Serializable id) throws HibernateException {
 		getCache().evictEntity( persistentClass, id );
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getCache().evictEntityRegion( persistentClass );
 	}
 
 	public void evictCollection(String roleName, Serializable id) throws HibernateException {
 		getCache().evictCollection( roleName, id );
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getCache().evictCollectionRegion( roleName );
 	}
 
 	public void evictQueries() throws HibernateException {
 		if ( settings.isQueryCacheEnabled() ) {
 			queryCache.clear();
 		}
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return updateTimestampsCache;
 	}
 
 	public QueryCache getQueryCache() {
 		return queryCache;
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			return getQueryCache();
 		}
 
 		if ( !settings.isQueryCacheEnabled() ) {
 			return null;
 		}
 
 		synchronized ( allCacheRegions ) {
 			QueryCache currentQueryCache = ( QueryCache ) queryCaches.get( regionName );
 			if ( currentQueryCache == null ) {
 				currentQueryCache = settings.getQueryCacheFactory().getQueryCache( regionName, updateTimestampsCache, settings, properties );
 				queryCaches.put( regionName, currentQueryCache );
 				allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 			}
 			return currentQueryCache;
 		}
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		synchronized ( allCacheRegions ) {
 			return ( Region ) allCacheRegions.get( regionName );
 		}
 	}
 
 	public Map getAllSecondLevelCacheRegions() {
 		synchronized ( allCacheRegions ) {
 			return new HashMap( allCacheRegions );
 		}
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return statistics;
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return (StatisticsImplementor) statistics;
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = ( FilterDefinition ) filters.get( filterName );
 		if ( def == null ) {
 			throw new HibernateException( "No such filter configured [" + filterName + "]" );
 		}
 		return def;
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return fetchProfiles.containsKey( name );
 	}
 
 	public Set getDefinedFilterNames() {
 		return filters.keySet();
 	}
 
 	public BatcherFactory getBatcherFactory() {
 		return settings.getBatcherFactory();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return (IdentifierGenerator) identifierGenerators.get(rootEntityName);
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatability
 		if ( impl == null && transactionManager != null ) {
 			impl = "jta";
 		}
 
 		if ( impl == null ) {
 			return null;
 		}
 		else if ( "jta".equals( impl ) ) {
 			if ( settings.getTransactionFactory().areCallbacksLocalToHibernateTransactions() ) {
 				log.warn( "JTASessionContext being used with JDBCTransactionFactory; auto-flush will not operate correctly with getCurrentSession()" );
 			}
 			return new JTASessionContext( this );
 		}
 		else if ( "thread".equals( impl ) ) {
 			return new ThreadLocalSessionContext( this );
 		}
 		else if ( "managed".equals( impl ) ) {
 			return new ManagedSessionContext( this );
 		}
 		else {
 			try {
 				Class implClass = ReflectHelper.classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( new Object[] { this } );
 			}
 			catch( Throwable t ) {
 				log.error( "Unable to construct current session context [" + impl + "]", t );
 				return null;
 			}
 		}
 	}
 
 	public EventListeners getEventListeners()
 	{
 		return eventListeners;
 	}
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return ( FetchProfile ) fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	/**
 	 * Custom serialization hook used during Session serialization.
 	 *
 	 * @param oos The stream to which to write the factory
 	 * @throws IOException Indicates problems writing out the serial data stream
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeUTF( uuid );
 		oos.writeBoolean( name != null );
 		if ( name != null ) {
 			oos.writeUTF( name );
 		}
 	}
 
 	/**
 	 * Custom deserialization hook used during Session deserialization.
 	 *
 	 * @param ois The stream from which to "read" the factory
 	 * @return The deserialized factory
 	 * @throws IOException indicates problems reading back serial data stream
 	 * @throws ClassNotFoundException indicates problems reading back serial data stream
 	 */
 	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		String name = null;
 		if ( isNamed ) {
 			name = ois.readUTF();
 		}
 		Object result = SessionFactoryObjectFactory.getInstance( uuid );
 		if ( result == null ) {
 			log.trace( "could not locate session factory by uuid [" + uuid + "] during session deserialization; trying name" );
 			if ( isNamed ) {
 				result = SessionFactoryObjectFactory.getNamedInstance( name );
 			}
 			if ( result == null ) {
 				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
 			}
 		}
 		return ( SessionFactoryImpl ) result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
index e59fe7b46b..ca225e62c5 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
@@ -1,644 +1,610 @@
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
 package org.hibernate.jdbc;
 
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.ConcurrentModificationException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.ScrollMode;
 import org.hibernate.TransactionException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.jdbc.util.FormatStyle;
 import org.hibernate.util.JDBCExceptionReporter;
 
 /**
  * Manages prepared statements and batching.
  *
  * @author Gavin King
  */
 public abstract class AbstractBatcher implements Batcher {
 
 	private static int globalOpenPreparedStatementCount;
 	private static int globalOpenResultSetCount;
 
 	private int openPreparedStatementCount;
 	private int openResultSetCount;
 
 	protected static final Logger log = LoggerFactory.getLogger( AbstractBatcher.class );
 
 	private final ConnectionManager connectionManager;
 	private final SessionFactoryImplementor factory;
 
 	private PreparedStatement batchUpdate;
 	private String batchUpdateSQL;
 
 	private HashSet statementsToClose = new HashSet();
 	private HashSet resultSetsToClose = new HashSet();
 	private PreparedStatement lastQuery;
 
 	private boolean releasing = false;
 	private final Interceptor interceptor;
 
 	private long transactionTimeout = -1;
 	boolean isTransactionTimeoutSet;
 
 	public AbstractBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
 		this.connectionManager = connectionManager;
 		this.interceptor = interceptor;
 		this.factory = connectionManager.getFactory();
 	}
 
 	public void setTransactionTimeout(int seconds) {
 		isTransactionTimeoutSet = true;
 		transactionTimeout = System.currentTimeMillis() / 1000 + seconds;
 	}
 
 	public void unsetTransactionTimeout() {
 		isTransactionTimeoutSet = false;
 	}
 
 	protected PreparedStatement getStatement() {
 		return batchUpdate;
 	}
 
 	public CallableStatement prepareCallableStatement(String sql)
 	throws SQLException, HibernateException {
 		executeBatch();
 		logOpenPreparedStatement();
 		return getCallableStatement( connectionManager.getConnection(), sql, false);
 	}
 
 	public PreparedStatement prepareStatement(String sql)
 	throws SQLException, HibernateException {
 		return prepareStatement( sql, false );
 	}
 
 	public PreparedStatement prepareStatement(String sql, boolean getGeneratedKeys)
 			throws SQLException, HibernateException {
 		executeBatch();
 		logOpenPreparedStatement();
 		return getPreparedStatement(
 				connectionManager.getConnection(),
 		        sql,
 		        false,
 		        getGeneratedKeys,
 		        null,
 		        null,
 		        false
 		);
 	}
 
 	public PreparedStatement prepareStatement(String sql, String[] columnNames)
 			throws SQLException, HibernateException {
 		executeBatch();
 		logOpenPreparedStatement();
 		return getPreparedStatement(
 				connectionManager.getConnection(),
 		        sql,
 		        false,
 		        false,
 		        columnNames,
 		        null,
 		        false
 		);
 	}
 
 	public PreparedStatement prepareSelectStatement(String sql)
 			throws SQLException, HibernateException {
 		logOpenPreparedStatement();
 		return getPreparedStatement(
 				connectionManager.getConnection(),
 		        sql,
 		        false,
 		        false,
 		        null,
 		        null,
 		        false
 		);
 	}
 
 	public PreparedStatement prepareQueryStatement(
 			String sql,
 	        boolean scrollable,
 	        ScrollMode scrollMode) throws SQLException, HibernateException {
 		logOpenPreparedStatement();
 		PreparedStatement ps = getPreparedStatement(
 				connectionManager.getConnection(),
 		        sql,
 		        scrollable,
 		        scrollMode
 		);
 		setStatementFetchSize( ps );
 		statementsToClose.add( ps );
 		lastQuery = ps;
 		return ps;
 	}
 
 	public CallableStatement prepareCallableQueryStatement(
 			String sql,
 	        boolean scrollable,
 	        ScrollMode scrollMode) throws SQLException, HibernateException {
 		logOpenPreparedStatement();
 		CallableStatement ps = ( CallableStatement ) getPreparedStatement(
 				connectionManager.getConnection(),
 		        sql,
 		        scrollable,
 		        false,
 		        null,
 		        scrollMode,
 		        true
 		);
 		setStatementFetchSize( ps );
 		statementsToClose.add( ps );
 		lastQuery = ps;
 		return ps;
 	}
 
 	public void abortBatch(SQLException sqle) {
 		try {
 			if (batchUpdate!=null) closeStatement(batchUpdate);
 		}
 		catch (SQLException e) {
 			//noncritical, swallow and let the other propagate!
 			JDBCExceptionReporter.logExceptions(e);
 		}
 		finally {
 			batchUpdate=null;
 			batchUpdateSQL=null;
 		}
 	}
 
 	public ResultSet getResultSet(PreparedStatement ps) throws SQLException {
 		ResultSet rs = ps.executeQuery();
 		resultSetsToClose.add(rs);
 		logOpenResults();
 		return rs;
 	}
 
 	public ResultSet getResultSet(CallableStatement ps, Dialect dialect) throws SQLException {
 		ResultSet rs = dialect.getResultSet(ps);
 		resultSetsToClose.add(rs);
 		logOpenResults();
 		return rs;
 
 	}
 
 	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException {
 		boolean psStillThere = statementsToClose.remove( ps );
 		try {
 			if ( rs != null ) {
 				if ( resultSetsToClose.remove( rs ) ) {
 					logCloseResults();
 					rs.close();
 				}
 			}
 		}
 		finally {
 			if ( psStillThere ) {
 				closeQueryStatement( ps );
 			}
 		}
 	}
 
 	public PreparedStatement prepareBatchStatement(String sql)
 			throws SQLException, HibernateException {
 		sql = getSQL( sql );
 
 		if ( !sql.equals(batchUpdateSQL) ) {
 			batchUpdate=prepareStatement(sql); // calls executeBatch()
 			batchUpdateSQL=sql;
 		}
 		else {
 			log.debug("reusing prepared statement");
 			log(sql);
 		}
 		return batchUpdate;
 	}
 
 	public CallableStatement prepareBatchCallableStatement(String sql)
 			throws SQLException, HibernateException {
 		if ( !sql.equals(batchUpdateSQL) ) { // TODO: what if batchUpdate is a callablestatement ?
 			batchUpdate=prepareCallableStatement(sql); // calls executeBatch()
 			batchUpdateSQL=sql;
 		}
 		return (CallableStatement)batchUpdate;
 	}
 
 
 	public void executeBatch() throws HibernateException {
 		if (batchUpdate!=null) {
 			try {
 				try {
 					doExecuteBatch(batchUpdate);
 				}
 				finally {
 					closeStatement(batchUpdate);
 				}
 			}
 			catch (SQLException sqle) {
 				throw factory.getSQLExceptionHelper().convert(
 				        sqle,
 				        "Could not execute JDBC batch update",
 				        batchUpdateSQL
 					);
 			}
 			finally {
 				batchUpdate=null;
 				batchUpdateSQL=null;
 			}
 		}
 	}
 
 	public void closeStatement(PreparedStatement ps) throws SQLException {
 		logClosePreparedStatement();
 		closePreparedStatement(ps);
 	}
 
 	private void closeQueryStatement(PreparedStatement ps) throws SQLException {
 
 		try {
 			//work around a bug in all known connection pools....
 			if ( ps.getMaxRows()!=0 ) ps.setMaxRows(0);
 			if ( ps.getQueryTimeout()!=0 ) ps.setQueryTimeout(0);
 		}
 		catch (Exception e) {
 			log.warn("exception clearing maxRows/queryTimeout", e);
 //			ps.close(); //just close it; do NOT try to return it to the pool!
 			return; //NOTE: early exit!
 		}
 		finally {
 			closeStatement(ps);
 		}
 
 		if ( lastQuery==ps ) lastQuery = null;
 
 	}
 
 	/**
 	 * Actually releases the batcher, allowing it to cleanup internally held
 	 * resources.
 	 */
 	public void closeStatements() {
 		try {
 			releasing = true;
 
 			try {
 				if ( batchUpdate != null ) {
 					batchUpdate.close();
 				}
 			}
 			catch ( SQLException sqle ) {
 				//no big deal
 				log.warn( "Could not close a JDBC prepared statement", sqle );
 			}
 			batchUpdate = null;
 			batchUpdateSQL = null;
 
 			Iterator iter = resultSetsToClose.iterator();
 			while ( iter.hasNext() ) {
 				try {
 					logCloseResults();
 					( ( ResultSet ) iter.next() ).close();
 				}
 				catch ( SQLException e ) {
 					// no big deal
 					log.warn( "Could not close a JDBC result set", e );
 				}
 				catch ( ConcurrentModificationException e ) {
 					// this has been shown to happen occasionally in rare cases
 					// when using a transaction manager + transaction-timeout
 					// where the timeout calls back through Hibernate's
 					// registered transaction synchronization on a separate
 					// "reaping" thread.  In cases where that reaping thread
 					// executes through this block at the same time the main
 					// application thread does we can get into situations where
 					// these CMEs occur.  And though it is not "allowed" per-se,
 					// the end result without handling it specifically is infinite
 					// looping.  So here, we simply break the loop
 					log.info( "encountered CME attempting to release batcher; assuming cause is tx-timeout scenario and ignoring" );
 					break;
 				}
 				catch ( Throwable e ) {
 					// sybase driver (jConnect) throwing NPE here in certain
 					// cases, but we'll just handle the general "unexpected" case
 					log.warn( "Could not close a JDBC result set", e );
 				}
 			}
 			resultSetsToClose.clear();
 
 			iter = statementsToClose.iterator();
 			while ( iter.hasNext() ) {
 				try {
 					closeQueryStatement( ( PreparedStatement ) iter.next() );
 				}
 				catch ( ConcurrentModificationException e ) {
 					// see explanation above...
 					log.info( "encountered CME attempting to release batcher; assuming cause is tx-timeout scenario and ignoring" );
 					break;
 				}
 				catch ( SQLException e ) {
 					// no big deal
 					log.warn( "Could not close a JDBC statement", e );
 				}
 			}
 			statementsToClose.clear();
 		}
 		finally {
 			releasing = false;
 		}
 	}
 
 	protected abstract void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException;
 
 	private String preparedStatementCountsToString() {
 		return
 				" (open PreparedStatements: " +
 				openPreparedStatementCount +
 				", globally: " +
 				globalOpenPreparedStatementCount +
 				")";
 	}
 
 	private String resultSetCountsToString() {
 		return
 				" (open ResultSets: " +
 				openResultSetCount +
 				", globally: " +
 				globalOpenResultSetCount +
 				")";
 	}
 
 	private void logOpenPreparedStatement() {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "about to open PreparedStatement" + preparedStatementCountsToString() );
 			openPreparedStatementCount++;
 			globalOpenPreparedStatementCount++;
 		}
 	}
 
 	private void logClosePreparedStatement() {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "about to close PreparedStatement" + preparedStatementCountsToString() );
 			openPreparedStatementCount--;
 			globalOpenPreparedStatementCount--;
 		}
 	}
 
 	private void logOpenResults() {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "about to open ResultSet" + resultSetCountsToString() );
 			openResultSetCount++;
 			globalOpenResultSetCount++;
 		}
 	}
 	private void logCloseResults() {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "about to close ResultSet" + resultSetCountsToString() );
 			openResultSetCount--;
 			globalOpenResultSetCount--;
 		}
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	private void log(String sql) {
 		factory.getSettings().getSqlStatementLogger().logStatement( sql, FormatStyle.BASIC );
 	}
 
 	private PreparedStatement getPreparedStatement(
 			final Connection conn,
 	        final String sql,
 	        final boolean scrollable,
 	        final ScrollMode scrollMode) throws SQLException {
 		return getPreparedStatement(
 				conn,
 		        sql,
 		        scrollable,
 		        false,
 		        null,
 		        scrollMode,
 		        false
 		);
 	}
 
 	private CallableStatement getCallableStatement(
 			final Connection conn,
 	        String sql,
 	        boolean scrollable) throws SQLException {
 		if ( scrollable && !factory.getSettings().isScrollableResultSetsEnabled() ) {
 			throw new AssertionFailure("scrollable result sets are not enabled");
 		}
 
 		sql = getSQL( sql );
 		log( sql );
 
 		log.trace("preparing callable statement");
 		if ( scrollable ) {
 			return conn.prepareCall(
 					sql,
 			        ResultSet.TYPE_SCROLL_INSENSITIVE,
 			        ResultSet.CONCUR_READ_ONLY
 			);
 		}
 		else {
 			return conn.prepareCall( sql );
 		}
 	}
 
 	private String getSQL(String sql) {
 		sql = interceptor.onPrepareStatement( sql );
 		if ( sql==null || sql.length() == 0 ) {
 			throw new AssertionFailure( "Interceptor.onPrepareStatement() returned null or empty string." );
 		}
 		return sql;
 	}
 
 	private PreparedStatement getPreparedStatement(
 			final Connection conn,
 	        String sql,
 	        boolean scrollable,
 	        final boolean useGetGeneratedKeys,
 	        final String[] namedGeneratedKeys,
 	        final ScrollMode scrollMode,
 	        final boolean callable) throws SQLException {
 		if ( scrollable && !factory.getSettings().isScrollableResultSetsEnabled() ) {
 			throw new AssertionFailure("scrollable result sets are not enabled");
 		}
 		if ( useGetGeneratedKeys && !factory.getSettings().isGetGeneratedKeysEnabled() ) {
 			throw new AssertionFailure("getGeneratedKeys() support is not enabled");
 		}
 
 		sql = getSQL( sql );
 		log( sql );
 
 		log.trace( "preparing statement" );
 		PreparedStatement result;
 		if ( scrollable ) {
 			if ( callable ) {
 				result = conn.prepareCall( sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY );
 			}
 			else {
 				result = conn.prepareStatement( sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY );
 			}
 		}
 		else if ( useGetGeneratedKeys ) {
 			result = conn.prepareStatement( sql, PreparedStatement.RETURN_GENERATED_KEYS );
 		}
 		else if ( namedGeneratedKeys != null ) {
 			result = conn.prepareStatement( sql, namedGeneratedKeys );
 		}
 		else {
 			if ( callable ) {
 				result = conn.prepareCall( sql );
 			}
 			else {
 				result = conn.prepareStatement( sql );
 			}
 		}
 
 		setTimeout( result );
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().prepareStatement();
 		}
 
 		return result;
 
 	}
 
 	private void setTimeout(PreparedStatement result) throws SQLException {
 		if ( isTransactionTimeoutSet ) {
 			int timeout = (int) ( transactionTimeout - ( System.currentTimeMillis() / 1000 ) );
 			if (timeout<=0) {
 				throw new TransactionException("transaction timeout expired");
 			}
 			else {
 				result.setQueryTimeout(timeout);
 			}
 		}
 	}
 
 	private void closePreparedStatement(PreparedStatement ps) throws SQLException {
 		try {
 			log.trace("closing statement");
 			ps.close();
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().closeStatement();
 			}
 		}
 		finally {
 			if ( !releasing ) {
 				// If we are in the process of releasing, no sense
 				// checking for aggressive-release possibility.
 				connectionManager.afterStatement();
 			}
 		}
 	}
 
 	private void setStatementFetchSize(PreparedStatement statement) throws SQLException {
 		Integer statementFetchSize = factory.getSettings().getJdbcFetchSize();
 		if ( statementFetchSize!=null ) {
 			statement.setFetchSize( statementFetchSize.intValue() );
 		}
 	}
 
-	public Connection openConnection() throws HibernateException {
-		log.debug("opening JDBC connection");
-		try {
-			return factory.getConnectionProvider().getConnection();
-		}
-		catch (SQLException sqle) {
-			throw factory.getSQLExceptionHelper().convert(
-			        sqle,
-			        "Cannot open connection"
-				);
-		}
-	}
-
-	public void closeConnection(Connection conn) throws HibernateException {
-		if ( conn == null ) {
-			log.debug( "found null connection on AbstractBatcher#closeConnection" );
-			// EARLY EXIT!!!!
-			return;
-		}
-
-		if ( log.isDebugEnabled() ) {
-			log.debug( "closing JDBC connection" + preparedStatementCountsToString() + resultSetCountsToString() );
-		}
-
-		try {
-			if ( !conn.isClosed() ) {
-				JDBCExceptionReporter.logAndClearWarnings( conn );
-			}
-			factory.getConnectionProvider().closeConnection( conn );
-		}
-		catch ( SQLException sqle ) {
-			throw factory.getSQLExceptionHelper().convert( sqle, "Cannot close connection" );
-		}
-	}
-
 	public void cancelLastQuery() throws HibernateException {
 		try {
 			if (lastQuery!=null) lastQuery.cancel();
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "Cannot cancel query"
 				);
 		}
 	}
 
 	public boolean hasOpenResources() {
 		return resultSetsToClose.size() > 0 || statementsToClose.size() > 0;
 	}
 
 	public String openResourceStatsAsString() {
 		return preparedStatementCountsToString() + resultSetCountsToString();
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
index d8905915cd..66eaf9f7b6 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
@@ -1,190 +1,175 @@
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
 package org.hibernate.jdbc;
 
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.ScrollMode;
 import org.hibernate.dialect.Dialect;
 
 /**
  * Manages <tt>PreparedStatement</tt>s for a session. Abstracts JDBC
  * batching to maintain the illusion that a single logical batch
  * exists for the whole session, even when batching is disabled.
  * Provides transparent <tt>PreparedStatement</tt> caching.
  *
  * @see java.sql.PreparedStatement
  * @see org.hibernate.impl.SessionImpl
  * @author Gavin King
  */
 public interface Batcher {
 	/**
 	 * Get a prepared statement for use in loading / querying. If not explicitly
 	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
 	 * session is closed or disconnected.
 	 */
 	public PreparedStatement prepareQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException;
 	/**
 	 * Close a prepared statement opened with <tt>prepareQueryStatement()</tt>
 	 */
 	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException;
 	/**
 	 * Get a prepared statement for use in loading / querying. If not explicitly
 	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
 	 * session is closed or disconnected.
 	 */
 	public CallableStatement prepareCallableQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException;
 	
 	
 	/**
 	 * Get a non-batchable prepared statement to use for selecting. Does not
 	 * result in execution of the current batch.
 	 */
 	public PreparedStatement prepareSelectStatement(String sql) throws SQLException, HibernateException;
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
 	 * using JDBC3 getGeneratedKeys ({@link Connection#prepareStatement(String, int)}).
 	 * <p/>
 	 * Must be explicitly released by {@link #closeStatement} after use.
 	 */
 	public PreparedStatement prepareStatement(String sql, boolean useGetGeneratedKeys) throws SQLException, HibernateException;
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 * using JDBC3 getGeneratedKeys ({@link Connection#prepareStatement(String, String[])}).
 	 * <p/>
 	 * Must be explicitly released by {@link #closeStatement} after use.
 	 */
 	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException, HibernateException;
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 * <p/>
 	 * Must be explicitly released by {@link #closeStatement} after use.
 	 */
 	public PreparedStatement prepareStatement(String sql) throws SQLException, HibernateException;
 
 	/**
 	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
 	 * <p/>
 	 * Must be explicitly released by {@link #closeStatement} after use.
 	 */
 	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException;
 
 	/**
 	 * Close a prepared or callable statement opened using <tt>prepareStatement()</tt> or <tt>prepareCallableStatement()</tt>
 	 */
 	public void closeStatement(PreparedStatement ps) throws SQLException;
 
 	/**
 	 * Get a batchable prepared statement to use for inserting / deleting / updating
 	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
 	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
 	 * statement explicitly.
 	 * @see Batcher#addToBatch
 	 */
 	public PreparedStatement prepareBatchStatement(String sql) throws SQLException, HibernateException;
 
 	/**
 	 * Get a batchable callable statement to use for inserting / deleting / updating
 	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
 	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
 	 * statement explicitly.
 	 * @see Batcher#addToBatch
 	 */
 	public CallableStatement prepareBatchCallableStatement(String sql) throws SQLException, HibernateException;
 
 	/**
 	 * Add an insert / delete / update to the current batch (might be called multiple times
 	 * for single <tt>prepareBatchStatement()</tt>)
 	 */
 	public void addToBatch(Expectation expectation) throws SQLException, HibernateException;
 
 	/**
 	 * Execute the batch
 	 */
 	public void executeBatch() throws HibernateException;
 
 	/**
 	 * Close any query statements that were left lying around
 	 */
 	public void closeStatements();
 	/**
 	 * Execute the statement and return the result set
 	 */
 	public ResultSet getResultSet(PreparedStatement ps) throws SQLException;
 	/**
 	 * Execute the statement and return the result set from a callable statement
 	 */
 	public ResultSet getResultSet(CallableStatement ps, Dialect dialect) throws SQLException;
 
 	/**
 	 * Must be called when an exception occurs
 	 * @param sqle the (not null) exception that is the reason for aborting
 	 */
 	public void abortBatch(SQLException sqle);
 
 	/**
 	 * Cancel the current query statement
 	 */
 	public void cancelLastQuery() throws HibernateException;
 
 	public boolean hasOpenResources();
 
 	public String openResourceStatsAsString();
 
-	// TODO : remove these last two as batcher is no longer managing connections
-
-	/**
-	 * Obtain a JDBC connection
-	 *
-	 * @deprecated Obtain connections from {@link ConnectionProvider} instead
-	 */
-	public Connection openConnection() throws HibernateException;
-	/**
-	 * Dispose of the JDBC connection
-	 *
-	 * @deprecated Obtain connections from {@link ConnectionProvider} instead
-	 */
-	public void closeConnection(Connection conn) throws HibernateException;
-	
 	/**
 	 * Set the transaction timeout to <tt>seconds</tt> later
 	 * than the current system time.
 	 */
 	public void setTransactionTimeout(int seconds);
 	/**
 	 * Unset the transaction timeout, called after the end of a 
 	 * transaction.
 	 */
 	public void unsetTransactionTimeout();
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
index 179fe11aa4..ce85763cac 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
@@ -1,100 +1,102 @@
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
 package org.hibernate.jdbc;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 
 /**
  * An implementation of the <tt>Batcher</tt> interface that
  * actually uses batching
  * @author Gavin King
  */
 public class BatchingBatcher extends AbstractBatcher {
 
 	private int batchSize;
 	private Expectation[] expectations;
 	
 	public BatchingBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
 		super( connectionManager, interceptor );
 		expectations = new Expectation[ getFactory().getSettings().getJdbcBatchSize() ];
 	}
 
 	public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
 		if ( !expectation.canBeBatched() ) {
 			throw new HibernateException( "attempting to batch an operation which cannot be batched" );
 		}
 		PreparedStatement batchUpdate = getStatement();
 		batchUpdate.addBatch();
 		expectations[ batchSize++ ] = expectation;
 		if ( batchSize == getFactory().getSettings().getJdbcBatchSize() ) {
 			doExecuteBatch( batchUpdate );
 		}
 	}
 
 	protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
 		if ( batchSize == 0 ) {
 			log.debug( "no batched statements to execute" );
 		}
 		else {
 			if ( log.isDebugEnabled() ) {
 				log.debug( "Executing batch size: " + batchSize );
 			}
 
 			try {
 				checkRowCounts( ps.executeBatch(), ps );
 			}
 			catch (RuntimeException re) {
 				log.error( "Exception executing batch: ", re );
 				throw re;
 			}
 			finally {
 				batchSize = 0;
 			}
 
 		}
 
 	}
 
 	private void checkRowCounts(int[] rowCounts, PreparedStatement ps) throws SQLException, HibernateException {
 		int numberOfRowCounts = rowCounts.length;
 		if ( numberOfRowCounts != batchSize ) {
 			log.warn( "JDBC driver did not return the expected number of row counts" );
 		}
 		for ( int i = 0; i < numberOfRowCounts; i++ ) {
 			expectations[i].verifyOutcome( rowCounts[i], ps, i );
 		}
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java
index 31a51adb5f..3b3ff05b5d 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/BorrowedConnectionProxy.java
@@ -1,137 +1,138 @@
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
 package org.hibernate.jdbc;
 
 import org.hibernate.HibernateException;
+import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.lang.reflect.InvocationTargetException;
 import java.sql.Connection;
 
 /**
  * A proxy for <i>borrowed</i> connections which funnels all requests back
  * into the ConnectionManager from which it was borrowed to be properly
  * handled (in terms of connection release modes).
  * <p/>
  * Note: the term borrowed here refers to connection references obtained
  * via {@link org.hibernate.Session#connection()} for application usage.
  *
  * @author Steve Ebersole
  */
 public class BorrowedConnectionProxy implements InvocationHandler {
 
 	private static final Class[] PROXY_INTERFACES = new Class[] { Connection.class, ConnectionWrapper.class };
 
-	private final ConnectionManager connectionManager;
+	private final LogicalConnectionImpl logicalConnection;
 	private boolean useable = true;
 
-	public BorrowedConnectionProxy(ConnectionManager connectionManager) {
-		this.connectionManager = connectionManager;
+	public BorrowedConnectionProxy(LogicalConnectionImpl logicalConnection) {
+		this.logicalConnection = logicalConnection;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		if ( "close".equals( method.getName() ) ) {
-			connectionManager.releaseBorrowedConnection();
+			logicalConnection.releaseBorrowedConnection();
 			return null;
 		}
 		// should probably no-op commit/rollback here, at least in JTA scenarios
 		if ( !useable ) {
 			throw new HibernateException( "connnection proxy not usable after transaction completion" );
 		}
 
 		if ( "getWrappedConnection".equals( method.getName() ) ) {
-			return connectionManager.getConnection();
+			return logicalConnection.getConnection();
 		}
 
 		try {
-			return method.invoke( connectionManager.getConnection(), args );
+			return method.invoke( logicalConnection.getConnection(), args );
 		}
 		catch( InvocationTargetException e ) {
 			throw e.getTargetException();
 		}
 	}
 
 	/**
 	 * Generates a Connection proxy wrapping the connection managed by the passed
 	 * connection manager.
 	 *
-	 * @param connectionManager The connection manager to wrap with the
+	 * @param logicalConnection The logical connection to wrap with the
 	 * connection proxy.
 	 * @return The generated proxy.
 	 */
-	public static Connection generateProxy(ConnectionManager connectionManager) {
-		BorrowedConnectionProxy handler = new BorrowedConnectionProxy( connectionManager );
+	public static Connection generateProxy(LogicalConnectionImpl logicalConnection) {
+		BorrowedConnectionProxy handler = new BorrowedConnectionProxy( logicalConnection );
 		return ( Connection ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 		        PROXY_INTERFACES,
 		        handler
 		);
 	}
 
 	/**
 	 * Marks a borrowed connection as no longer usable.
 	 *
 	 * @param connection The connection (proxy) to be marked.
 	 */
 	public static void renderUnuseable(Connection connection) {
 		if ( connection != null && Proxy.isProxyClass( connection.getClass() ) ) {
 			InvocationHandler handler = Proxy.getInvocationHandler( connection );
 			if ( BorrowedConnectionProxy.class.isAssignableFrom( handler.getClass() ) ) {
 				( ( BorrowedConnectionProxy ) handler ).useable = false;
 			}
 		}
 	}
 
 	/**
 	 * Convience method for unwrapping a connection proxy and getting a
 	 * handle to an underlying connection.
 	 *
 	 * @param connection The connection (proxy) to be unwrapped.
 	 * @return The unwrapped connection.
 	 */
 	public static Connection getWrappedConnection(Connection connection) {
 		if ( connection != null && connection instanceof ConnectionWrapper ) {
 			return ( ( ConnectionWrapper ) connection ).getWrappedConnection();
 		}
 		else {
 			return connection;
 		}
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	public static ClassLoader getProxyClassLoader() {
 		return ConnectionWrapper.class.getClassLoader();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java b/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
index 4ebf6621e1..b728cdee72 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
@@ -1,559 +1,470 @@
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
 package org.hibernate.jdbc;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
-import org.hibernate.util.JDBCExceptionReporter;
 
 /**
  * Encapsulates JDBC Connection management logic needed by Hibernate.
  * <p/>
  * The lifecycle is intended to span a logical series of interactions with the
  * database.  Internally, this means the the lifecycle of the Session.
  *
  * @author Steve Ebersole
  */
 public class ConnectionManager implements Serializable {
 
 	private static final Logger log = LoggerFactory.getLogger( ConnectionManager.class );
 
 	// TODO: check if it's ok to change the method names in Callback
 	public static interface Callback extends ConnectionObserver {
 		public boolean isTransactionInProgress();
 	}
 
 	private transient SessionFactoryImplementor factory;
 	private final Callback callback;
 
-	private final ConnectionReleaseMode releaseMode;
-	private transient Connection connection;
-	private transient Connection borrowedConnection;
+	private transient LogicalConnectionImpl connection;
 
-	private final boolean wasConnectionSupplied;
 	private transient Batcher batcher;
 	private transient Interceptor interceptor;
-	private boolean isClosed;
-	private transient boolean isFlushing;
- 
+
 	/**
 	 * Constructs a ConnectionManager.
 	 * <p/>
 	 * This is the form used internally.
 	 * 
 	 * @param factory The SessionFactory.
 	 * @param callback An observer for internal state change.
 	 * @param releaseMode The mode by which to release JDBC connections.
 	 * @param connection An externally supplied connection.
 	 */ 
 	public ConnectionManager(
 	        SessionFactoryImplementor factory,
 	        Callback callback,
 	        ConnectionReleaseMode releaseMode,
 	        Connection connection,
 	        Interceptor interceptor) {
 		this.factory = factory;
 		this.callback = callback;
 
 		this.interceptor = interceptor;
 		this.batcher = factory.getSettings().getBatcherFactory().createBatcher( this, interceptor );
 
-		this.connection = connection;
-		wasConnectionSupplied = ( connection != null );
-
-		this.releaseMode = wasConnectionSupplied ? ConnectionReleaseMode.ON_CLOSE : releaseMode;
+		setupConnection( connection, releaseMode );
 	}
 
 	/**
 	 * Private constructor used exclusively from custom serialization
 	 */
 	private ConnectionManager(
-	        SessionFactoryImplementor factory,
-	        Callback callback,
-	        ConnectionReleaseMode releaseMode,
-	        Interceptor interceptor,
-	        boolean wasConnectionSupplied,
-	        boolean isClosed) {
+			SessionFactoryImplementor factory,
+			Callback callback,
+			ConnectionReleaseMode releaseMode,
+			Interceptor interceptor
+	) {
 		this.factory = factory;
 		this.callback = callback;
 
 		this.interceptor = interceptor;
 		this.batcher = factory.getSettings().getBatcherFactory().createBatcher( this, interceptor );
+	}
 
-		this.wasConnectionSupplied = wasConnectionSupplied;
-		this.isClosed = isClosed;
-		this.releaseMode = wasConnectionSupplied ? ConnectionReleaseMode.ON_CLOSE : releaseMode;
+	private void setupConnection(Connection suppliedConnection,
+								 ConnectionReleaseMode releaseMode
+	) {
+		connection =
+				new LogicalConnectionImpl(
+						suppliedConnection,
+						releaseMode,
+						factory.getJdbcServices()
+				);
+		connection.addObserver( callback );
 	}
 
 	/**
 	 * The session factory.
 	 *
 	 * @return the session factory.
 	 */
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	/**
 	 * The batcher managed by this ConnectionManager.
 	 *
 	 * @return The batcher.
 	 */
 	public Batcher getBatcher() {
 		return batcher;
 	}
 
 	/**
-	 * Was the connection being used here supplied by the user?
-	 *
-	 * @return True if the user supplied the JDBC connection; false otherwise
-	 */
-	public boolean isSuppliedConnection() {
-		return wasConnectionSupplied;
-	}
-
-	/**
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
 	public Connection getConnection() throws HibernateException {
-		if ( isClosed ) {
-			throw new HibernateException( "connection manager has been closed" );
-		}
-		if ( connection == null  ) {
-			openConnection();
-		}
-		return connection;
+		return connection.getConnection();
 	}
 
 	public boolean hasBorrowedConnection() {
 		// used from testsuite
-		return borrowedConnection != null;
+		return connection.hasBorrowedConnection();
 	}
 
 	public Connection borrowConnection() {
-		if ( isClosed ) {
-			throw new HibernateException( "connection manager has been closed" );
-		}
-		if ( isSuppliedConnection() ) {
-			return connection;
-		}
-		else {
-			if ( borrowedConnection == null ) {
-				borrowedConnection = BorrowedConnectionProxy.generateProxy( this );
-			}
-			return borrowedConnection;
-		}
+		return connection.borrowConnection();
 	}
 
 	public void releaseBorrowedConnection() {
-		if ( borrowedConnection != null ) {
-			try {
-				BorrowedConnectionProxy.renderUnuseable( borrowedConnection );
-			}
-			finally {
-				borrowedConnection = null;
-			}
-		}
+		connection.releaseBorrowedConnection();
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
-		return connection == null 
-			|| connection.isClosed()
-			|| connection.getAutoCommit();
+		return connection == null ||
+				! connection.isOpen() ||
+				! connection.isPhysicallyConnected() ||
+				connection.getConnection().getAutoCommit();
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
-		if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
+		if ( connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			return true;
 		}
-		else if ( releaseMode == ConnectionReleaseMode.AFTER_TRANSACTION ) {
+		else if ( connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION ) {
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
-		if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
+		if ( connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
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
-			return releaseMode == ConnectionReleaseMode.AFTER_TRANSACTION && inAutoCommitState;
+			return connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION && inAutoCommitState;
 		}
 	}
 
 	/**
 	 * Is this ConnectionManager instance "logically" connected.  Meaning
 	 * do we either have a cached connection available or do we have the
 	 * ability to obtain a connection on demand.
 	 *
 	 * @return True if logically connected; false otherwise.
 	 */
 	public boolean isCurrentlyConnected() {
-		return wasConnectionSupplied ? connection != null : !isClosed;
+		if ( connection != null ) {
+			if ( connection.isUserSuppliedConnection() ) {
+				return connection.isPhysicallyConnected();
+			}
+			else {
+				return connection.isOpen();
+			}
+		}
+		else {
+			return false;
+		}
 	}
 
 	/**
 	 * To be called after execution of each JDBC statement.  Used to
 	 * conditionally release the JDBC connection aggressively if
 	 * the configured release mode indicates.
 	 */
 	public void afterStatement() {
 		if ( isAggressiveRelease() ) {
-			if ( isFlushing ) {
-				log.debug( "skipping aggressive-release due to flush cycle" );
-			}
-			else if ( batcher.hasOpenResources() ) {
+			if ( batcher.hasOpenResources() ) {
 				log.debug( "skipping aggresive-release due to open resources on batcher" );
 			}
-			else if ( borrowedConnection != null ) {
-				log.debug( "skipping aggresive-release due to borrowed connection" );
-			}
 			else {
-				aggressiveRelease();
+				connection.afterStatementExecution();
 			}
 		}
 	}
 
 	/**
 	 * To be called after local transaction completion.  Used to conditionally
 	 * release the JDBC connection aggressively if the configured release mode
 	 * indicates.
 	 */
 	public void afterTransaction() {
-		if ( isAfterTransactionRelease() ) {
-			aggressiveRelease();
-		}
-		else if ( isAggressiveReleaseNoTransactionCheck() && batcher.hasOpenResources() ) {
-			log.info( "forcing batcher resource cleanup on transaction completion; forgot to close ScrollableResults/Iterator?" );
-			batcher.closeStatements();
-			aggressiveRelease();
-		}
-		else if ( isOnCloseRelease() ) {
-			// log a message about potential connection leaks
-			log.debug( "transaction completed on session with on_close connection release mode; be sure to close the session to release JDBC resources!" );
+		if ( connection != null ) {
+			if ( isAfterTransactionRelease() ) {
+				connection.afterTransaction();
+			}
+			else if ( isAggressiveReleaseNoTransactionCheck() && batcher.hasOpenResources() ) {
+				log.info( "forcing batcher resource cleanup on transaction completion; forgot to close ScrollableResults/Iterator?" );
+				batcher.closeStatements();
+				connection.afterTransaction();
+			}
+			else if ( isOnCloseRelease() ) {
+				// log a message about potential connection leaks
+				log.debug( "transaction completed on session with on_close connection release mode; be sure to close the session to release JDBC resources!" );
+			}
 		}
 		batcher.unsetTransactionTimeout();
 	}
 
 	private boolean isAfterTransactionRelease() {
-		return releaseMode == ConnectionReleaseMode.AFTER_TRANSACTION;
+		return connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION;
 	}
 
 	private boolean isOnCloseRelease() {
-		return releaseMode == ConnectionReleaseMode.ON_CLOSE;
+		return connection.getConnectionReleaseMode() == ConnectionReleaseMode.ON_CLOSE;
+	}
+
+	public boolean isLogicallyConnected() {
+		return connection != null && connection.isOpen();
 	}
 
 	/**
 	 * To be called after Session completion.  Used to release the JDBC
 	 * connection.
 	 *
 	 * @return The connection mantained here at time of close.  Null if
 	 * there was no connection cached internally.
 	 */
 	public Connection close() {
-		try {
-			return cleanup();
-		}
-		finally {
-			isClosed = true;
-		}
+		return cleanup();
 	}
 
 	/**
 	 * Manually disconnect the underlying JDBC Connection.  The assumption here
 	 * is that the manager will be reconnected at a later point in time.
 	 *
 	 * @return The connection mantained here at time of disconnect.  Null if
 	 * there was no connection cached internally.
 	 */
 	public Connection manualDisconnect() {
-		return cleanup();
+		if ( ! isLogicallyConnected() ) {
+			throw new IllegalStateException( "cannot manually disconnect because not logically connected." );
+		}
+		batcher.closeStatements();
+		return connection.manualDisconnect();
 	}
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for ConnectionProvider-supplied connections.
 	 */
 	public void manualReconnect() {
+		manualReconnect( null );
 	}
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for user-supplied connections.
 	 */
 	public void manualReconnect(Connection suppliedConnection) {
-		this.connection = suppliedConnection;
+		if ( ! isLogicallyConnected() ) {
+			throw new IllegalStateException( "cannot manually disconnect because not logically connected." );
+		}
+		connection.reconnect( suppliedConnection );
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
-		releaseBorrowedConnection();
-
 		if ( connection == null ) {
 			log.trace( "connection already null in cleanup : no action");
 			return null;
 		}
-
 		try {
 			log.trace( "performing cleanup" );
 
-			batcher.closeStatements();
-			Connection c = null;
-			if ( !wasConnectionSupplied ) {
-				closeConnection();
+			if ( isLogicallyConnected() ) {
+				batcher.closeStatements();
 			}
-			else {
-				c = connection;
-			}
-			connection = null;
+			Connection c = connection.close();
 			return c;
 		}
 		finally {
-			callback.physicalConnectionReleased();
-		}
-	}
-
-	/**
-	 * Performs actions required to perform an aggressive release of the
-	 * JDBC Connection.
-	 */
-	private void aggressiveRelease() {
-		if ( !wasConnectionSupplied ) {
-			log.debug( "aggressively releasing JDBC connection" );
-			if ( connection != null ) {
-				closeConnection();
-			}
-		}
-	}
-
-	/**
-	 * Pysically opens a JDBC Connection.
-	 *
-	 * @throws HibernateException
-	 */
-	private void openConnection() throws HibernateException {
-		if ( connection != null ) {
-			return;
-		}
-
-		log.debug("opening JDBC connection");
-		try {
-			connection = factory.getConnectionProvider().getConnection();
-		}
-		catch (SQLException sqle) {
-			throw factory.getSQLExceptionHelper().convert(
-					sqle,
-					"Cannot open connection"
-				);
-		}
-
-		callback.physicalConnectionObtained( connection ); // register synch; stats.connect()
-	}
-
-	/**
-	 * Physically closes the JDBC Connection.
-	 */
-	private void closeConnection() {
-		if ( log.isDebugEnabled() ) {
-			log.debug(
-					"releasing JDBC connection [" +
-					batcher.openResourceStatsAsString() + "]"
-				);
-		}
-
-		try {
-			if ( !connection.isClosed() ) {
-				JDBCExceptionReporter.logAndClearWarnings( connection );
-			}
-			factory.getConnectionProvider().closeConnection( connection );
 			connection = null;
 		}
-		catch (SQLException sqle) {
-			throw factory.getSQLExceptionHelper().convert(
-					sqle, 
-					"Cannot release connection"
-				);
-		}
 	}
 
 	/**
 	 * Callback to let us know that a flush is beginning.  We use this fact
 	 * to temporarily circumvent aggressive connection releasing until after
 	 * the flush cycle is complete {@link #flushEnding()}
 	 */
 	public void flushBeginning() {
 		log.trace( "registering flush begin" );
-		isFlushing = true;
+		connection.disableReleases();
 	}
 
 	/**
 	 * Callback to let us know that a flush is ending.  We use this fact to
 	 * stop circumventing aggressive releasing connections.
 	 */
 	public void flushEnding() {
 		log.trace( "registering flush end" );
-		isFlushing = false;
+		connection.enableReleases();
 		afterStatement();
 	}
 
 	public boolean isReadyForSerialization() {
-		return wasConnectionSupplied ? connection == null : !batcher.hasOpenResources();
+		return connection == null ? true : ! batcher.hasOpenResources() && connection.isReadyForSerialization();
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
 
 		oos.writeObject( factory );
 		oos.writeObject( interceptor );
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
 		factory = (SessionFactoryImplementor) ois.readObject();
 		interceptor = (Interceptor) ois.readObject();
 		ois.defaultReadObject();
-
-		this.batcher = factory.getSettings().getBatcherFactory().createBatcher( this, interceptor );
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
-		oos.writeBoolean( wasConnectionSupplied );
-		oos.writeBoolean( isClosed );
+		connection.serialize( oos );
 	}
 
 	public static ConnectionManager deserialize(
 			ObjectInputStream ois,
 	        SessionFactoryImplementor factory,
 	        Interceptor interceptor,
 	        ConnectionReleaseMode connectionReleaseMode,
 	        Callback callback) throws IOException {
-		return new ConnectionManager(
+		ConnectionManager connectionManager = new ConnectionManager(
 				factory,
 		        callback,
 		        connectionReleaseMode,
-		        interceptor,
-		        ois.readBoolean(),
-		        ois.readBoolean()
+		        interceptor
 		);
+		connectionManager.connection =
+				LogicalConnectionImpl.deserialize(
+						ois,
+						factory.getJdbcServices( ),
+						connectionReleaseMode
+				);
+		connectionManager.connection.addObserver( callback );
+		return connectionManager;
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
index 05e5ce8412..e722ecb165 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
@@ -1,53 +1,55 @@
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
 package org.hibernate.jdbc;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 
 /**
  * An implementation of the <tt>Batcher</tt> interface that does no batching
  *
  * @author Gavin King
  */
 public class NonBatchingBatcher extends AbstractBatcher {
 
 	public NonBatchingBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
 		super( connectionManager, interceptor );
 	}
 
 	public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
 		PreparedStatement statement = getStatement();
 		final int rowCount = statement.executeUpdate();
 		expectation.verifyOutcome( rowCount, statement, 0 );
 	}
 
 	protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
index 3f7287fbaa..fb69008fe0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
@@ -1,262 +1,263 @@
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
 package org.hibernate.test.jdbc.proxies;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
+import org.hibernate.stat.ConcurrentStatisticsImpl;
 import org.hibernate.test.common.BasicTestingJdbcServiceImpl;
 import org.hibernate.testing.junit.UnitTestCase;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class AggressiveReleaseTest extends UnitTestCase {
 
 	private static final Logger log = LoggerFactory.getLogger( AggressiveReleaseTest.class );
 	private BasicTestingJdbcServiceImpl services = new BasicTestingJdbcServiceImpl();
 
 	private static class ConnectionCounter implements ConnectionObserver {
 		public int obtainCount = 0;
 		public int releaseCount = 0;
 
 		public void physicalConnectionObtained(Connection connection) {
 			obtainCount++;
 		}
 
 		public void physicalConnectionReleased() {
 			releaseCount++;
 		}
 
 		public void logicalConnectionClosed() {
 		}
 	}
 
 	public AggressiveReleaseTest(String string) {
 		super( string );
 	}
 
 	public void setUp() throws SQLException {
 		services.prepare( true );
 
 		Connection connection = null;
 		Statement stmnt = null;
 		try {
 			connection = services.getConnectionProvider().getConnection();
 			stmnt = connection.createStatement();
 			stmnt.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			stmnt.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		}
 		finally {
 			if ( stmnt != null ) {
 				try {
 					stmnt.close();
 				}
 				catch ( SQLException ignore ) {
 					log.warn( "could not close statement used to set up schema", ignore );
 				}
 			}
 			if ( connection != null ) {
 				try {
 					connection.close();
 				}
 				catch ( SQLException ignore ) {
 					log.warn( "could not close connection used to set up schema", ignore );
 				}
 			}
 		}
 	}
 
 	public void tearDown() throws SQLException {
 		Connection connection = null;
 		Statement stmnt = null;
 		try {
 			connection = services.getConnectionProvider().getConnection();
 			stmnt = connection.createStatement();
 			stmnt.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		}
 		finally {
 			if ( stmnt != null ) {
 				try {
 					stmnt.close();
 				}
 				catch ( SQLException ignore ) {
 					log.warn( "could not close statement used to set up schema", ignore );
 				}
 			}
 			if ( connection != null ) {
 				try {
 					connection.close();
 				}
 				catch ( SQLException ignore ) {
 					log.warn( "could not close connection used to set up schema", ignore );
 				}
 			}
 		}
 
 		services.release();
 	}
 
 	public void testBasicRelease() {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		ConnectionCounter observer = new ConnectionCounter();
 		logicalConnection.addObserver( observer );
 
 		try {
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.obtainCount );
 			assertEquals( 0, observer.releaseCount );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 	}
 
 	public void testReleaseCircumventedByHeldResources() {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		ConnectionCounter observer = new ConnectionCounter();
 		logicalConnection.addObserver( observer );
 
 		try {
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.obtainCount );
 			assertEquals( 0, observer.releaseCount );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 
 			// open a result set and hold it open...
 			ps = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 
 			// open a second result set
 			PreparedStatement ps2 = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps2.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 			// and close it...
 			ps2.close();
 			// the release should be circumvented...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 
 			// let the close of the logical connection below release all resources (hopefully)...
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertEquals( 2, observer.obtainCount );
 		assertEquals( 2, observer.releaseCount );
 	}
 
 	public void testReleaseCircumventedManually() {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		ConnectionCounter observer = new ConnectionCounter();
 		logicalConnection.addObserver( observer );
 
 		try {
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.obtainCount );
 			assertEquals( 0, observer.releaseCount );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 
 			// disable releases...
 			logicalConnection.disableReleases();
 
 			// open a result set...
 			ps = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 			// and close it...
 			ps.close();
 			// the release should be circumvented...
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.obtainCount );
 			assertEquals( 1, observer.releaseCount );
 
 			// let the close of the logical connection below release all resources (hopefully)...
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertEquals( 2, observer.obtainCount );
 		assertEquals( 2, observer.releaseCount );
 	}
 }
