diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
index 735f5c8b8b..d3cea25517 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
@@ -1,258 +1,258 @@
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
 
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistry;
-import org.hibernate.stat.StatisticsImplementor;
+import org.hibernate.stat.spi.StatisticsImplementor;
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
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@link #getJdbcServices().getDialect()}.{@link JdbcServices#getDialect()}
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
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 */
     public SqlExceptionHelper getSQLExceptionHelper();
 
 	public Settings getSettings();
 
 	/**
 	 * Get a nontransactional "current" session for Hibernate EntityManager
 	 */
 	public Session openTemporarySession() throws HibernateException;
 
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
 	public Session openSession(
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
 
 	public ServiceRegistry getServiceRegistry();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
index 45f39ddfb8..37333d80a8 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
@@ -1,118 +1,116 @@
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
-import org.hibernate.engine.jdbc.internal.proxy.ConnectionProxyHandler;
-import org.hibernate.stat.StatisticsImplementor;
 
 import java.sql.Connection;
 
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
 	 * Remove an observer
 	 *
 	 * @param connectionObserver The observer to remove.
 	 */
 	public void removeObserver(ConnectionObserver connectionObserver);
 
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
 
 	/**
 	 * Manually disconnect the underlying JDBC Connection.  The assumption here
 	 * is that the manager will be reconnected at a later point in time.
 	 *
 	 * @return The connection maintained here at time of disconnect.  Null if
 	 * there was no connection cached internally.
 	 */
 	public Connection manualDisconnect();
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at some point after manualDisconnect().
 	 *
 	 * @param suppliedConnection For user supplied connection strategy the user needs to hand us the connection
 	 * with which to reconnect.  It is an error to pass a connection in the other strategies.
 	 */
 	public void manualReconnect(Connection suppliedConnection);
 
 	public boolean isAutoCommit();
 
 	public boolean isReadyForSerialization();
 
 	public void notifyObserversStatementPrepared();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionEnvironment.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionEnvironment.java
index f45713c57b..9605e3270b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionEnvironment.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionEnvironment.java
@@ -1,71 +1,71 @@
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
 
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
-import org.hibernate.stat.StatisticsImplementor;
+import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * Provides access to transactional services.
  *
  * @author Steve Ebersole
  */
 public interface TransactionEnvironment {
 	/**
 	 * Retrieve the session factory for this environment.
 	 *
 	 * @return The session factory
 	 */
 	public SessionFactoryImplementor getSessionFactory();
 
 	/**
 	 * Retrieve the JDBC services for this environment.
 	 *
 	 * @return The JDBC services
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Retrieve the JTA platform for this environment.
 	 *
 	 * @return The JTA platform
 	 */
 	public JtaPlatform getJtaPlatform();
 
 	/**
 	 * Retrieve the transaction factory for this environment.
 	 *
 	 * @return The transaction factory
 	 */
 	public TransactionFactory getTransactionFactory();
 
 	/**
 	 * Get access to the statistics collector
 	 *
 	 * @return The statistics collector
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index e7e34f97c7..0c012b1dd4 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1311 +1,1316 @@
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
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
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
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.event.EventListeners;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistry;
-import org.hibernate.stat.ConcurrentStatisticsImpl;
+import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
 import org.hibernate.stat.Statistics;
-import org.hibernate.stat.StatisticsImplementor;
+import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.jboss.logging.Logger;
 
 
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
  * @see org.hibernate.Session
  * @see org.hibernate.hql.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactory, SessionFactoryImplementor {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SessionFactoryImpl.class.getName());
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
 	private final transient ServiceRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
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
 	private final transient TransactionEnvironment transactionEnvironment;
 
 	public SessionFactoryImpl(
 			Configuration cfg,
 	        Mapping mapping,
 			ServiceRegistry serviceRegistry,
 	        Settings settings,
 	        EventListeners listeners,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.buildingSessionFactory();
 
-		this.statistics = new ConcurrentStatisticsImpl( this );
-		getStatistics().setStatisticsEnabled( settings.isStatisticsEnabled() );
-        LOG.debugf("Statistics initialized [enabled=%s]", settings.isStatisticsEnabled());
+		this.statistics = buildStatistics( settings, serviceRegistry );
 
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
 
         LOG.debugf("Session factory constructed with filter configurations : %s", filters);
         LOG.debugf("Instantiating session factory with properties: %s", properties);
 
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
                     LOG.trace("Building cache for entity data [" + model.getEntityName() + "]");
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model,
 					accessStrategy,
 					this,
 					mapping
 			);
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
                 LOG.trace("Building cache for collection data [" + model.getRole() + "]");
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry.getService( PersisterFactory.class ).createCollectionPersister(
 					cfg,
 					model,
 					accessStrategy,
 					this
 			) ;
 			collectionPersisters.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
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
 
         LOG.debugf("Instantiated session factory");
 
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
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(settings, properties);
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache(null, updateTimestampsCache, settings, properties);
 			queryCaches = new HashMap<String,QueryCache>();
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
                     if (iterator.hasNext()) failingQueries.append(", ");
                     LOG.namedQueryError(queryName, e);
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
 
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
+	private Statistics buildStatistics(Settings settings, ServiceRegistry serviceRegistry) {
+		Statistics statistics = new ConcurrentStatisticsImpl( this );
+		getStatistics().setStatisticsEnabled( settings.isStatisticsEnabled() );
+		LOG.debugf("Statistics initialized [enabled=%s]", settings.isStatisticsEnabled());
+		return statistics;
+	}
+
 	public TransactionEnvironment getTransactionEnvironment() {
 		return transactionEnvironment;
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
         LOG.debugf("Checking %s named HQL queries", namedQueries.size());
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
                 LOG.debugf("Checking named query: %s", queryName);
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
 
         LOG.debugf("Checking %s named SQL queries", namedSqlQueries.size());
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
                 LOG.debugf("Checking named SQL query: %s", queryName);
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
 
 	public Session openSession(Connection connection, Interceptor sessionLocalInterceptor) {
 		return openSession(connection, false, Long.MIN_VALUE, sessionLocalInterceptor);
 	}
 
 	public Session openSession(Interceptor sessionLocalInterceptor) throws HibernateException {
 		// note that this timestamp is not correct if the connection provider
 		// returns an older JDBC connection that was associated with a
 		// transaction that was already begun before openSession() was called
 		// (don't know any possible solution to this!)
 		long timestamp = settings.getRegionFactory().nextTimestamp();
 		return openSession( null, true, timestamp, sessionLocalInterceptor );
 	}
 
 	public Session openSession(Connection connection) {
 		return openSession(connection, interceptor); //prevents this session from adding things to cache
 	}
 
 	public Session openSession() throws HibernateException {
 		return openSession(interceptor);
 	}
 
 	public Session openTemporarySession() throws HibernateException {
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
 
 	public Session openSession(
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
 
 	public Session getCurrentSession() throws HibernateException {
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
 
 	public JdbcServices getJdbcServices() {
 		return serviceRegistry.getService( JdbcServices.class );
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return getJdbcServices().getDialect();
 	}
 
 	public Interceptor getInterceptor()
 	{
 		return interceptor;
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	// from javax.naming.Referenceable
 	public Reference getReference() throws NamingException {
         LOG.debugf("Returning a Reference to the SessionFactory");
 		return new Reference(
 			SessionFactoryImpl.class.getName(),
 		    new StringRefAddr("uuid", uuid),
 		    SessionFactoryObjectFactory.class.getName(),
 		    null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
         LOG.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryObjectFactory.getInstance(uuid);
 		if (result==null) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryObjectFactory.getNamedInstance(name);
             if (result == null) throw new InvalidObjectException("Could not find a SessionFactory named: " + name);
             LOG.debugf("Resolved SessionFactory by name");
         } else LOG.debugf("Resolved SessionFactory by UID");
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
         LOG.trace("Deserializing");
 		in.defaultReadObject();
         LOG.debugf("Deserialized: %s", uuid);
 	}
 
 	private void writeObject(ObjectOutputStream out) throws IOException {
         LOG.debugf("Serializing: %s", uuid);
 		out.defaultWriteObject();
         LOG.trace("Serialized");
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
 		return classMetadata.get(entityName);
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
 				ReflectHelper.classForName( className );
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
 	 * Note: Be aware that the sessionFactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
             LOG.trace("Already closed");
 			return;
 		}
 
         LOG.closing();
 
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
                 if (LOG.isDebugEnabled()) LOG.debugf("Evicting second-level cache: %s",
                                                      MessageHelper.infoString(p, identifier, SessionFactoryImpl.this));
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
                 LOG.debugf("Evicting second-level cache: %s", p.getEntityName());
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
                 if (LOG.isDebugEnabled()) LOG.debugf("Evicting second-level cache: %s",
                                                      MessageHelper.collectionInfoString(p, ownerIdentifier, SessionFactoryImpl.this));
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
                 LOG.debugf("Evicting second-level cache: %s", p.getRole());
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
             if (regionName == null) throw new NullPointerException(
                                                                    "Region-name cannot be null (use Cache#evictDefaultQueryRegion to evict the default query cache)");
             if (settings.isQueryCacheEnabled()) {
                 QueryCache namedQueryCache = queryCaches.get(regionName);
                 // TODO : cleanup entries in queryCaches + allCacheRegions ?
                 if (namedQueryCache != null) namedQueryCache.clear();
 			}
 		}
 
 		public void evictQueryRegions() {
 			if ( queryCaches != null ) {
 				for ( QueryCache queryCache : queryCaches.values() ) {
 					queryCache.clear();
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
 
 		QueryCache currentQueryCache = queryCaches.get( regionName );
 		if ( currentQueryCache == null ) {
 			currentQueryCache = settings.getQueryCacheFactory().getQueryCache( regionName, updateTimestampsCache, settings, properties );
 			queryCaches.put( regionName, currentQueryCache );
 			allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 		}
 
 		return currentQueryCache;
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	public Map getAllSecondLevelCacheRegions() {
 		return new HashMap( allCacheRegions );
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
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return (IdentifierGenerator) identifierGenerators.get(rootEntityName);
 	}
 
 	private org.hibernate.engine.transaction.spi.TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( org.hibernate.engine.transaction.spi.TransactionFactory.class );
 	}
 
 	private boolean canAccessTransactionManager() {
 		try {
 			return serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager() != null;
 		}
 		catch (Exception e) {
 			return false;
 		}
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatibility
 		if ( impl == null ) {
 			if ( canAccessTransactionManager() ) {
 				impl = "jta";
 			}
 			else {
 				return null;
 			}
 		}
 
 		if ( "jta".equals( impl ) ) {
 			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
                 LOG.autoFlushWillNotWork();
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
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
                 LOG.unableToConstructCurrentSessionContext(impl, t);
 				return null;
 			}
 		}
 	}
 
 	public EventListeners getEventListeners() {
 		return eventListeners;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
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
             LOG.trace("Could not locate session factory by uuid [" + uuid + "] during session deserialization; trying name");
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
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
index cd09279ecb..6281ec0e6d 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
@@ -1,1142 +1,1139 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2005-2011, Red Hat Inc. or third-party contributors as
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
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Reader;
 import java.io.Serializable;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
-import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionException;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.ActionQueue;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.NonFlushedChanges;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.StatefulPersistenceContext;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.query.FilterQueryPlan;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.event.AutoFlushEvent;
 import org.hibernate.event.AutoFlushEventListener;
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.DirtyCheckEvent;
 import org.hibernate.event.DirtyCheckEventListener;
 import org.hibernate.event.EventListeners;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EvictEvent;
 import org.hibernate.event.EvictEventListener;
 import org.hibernate.event.FlushEvent;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.InitializeCollectionEvent;
 import org.hibernate.event.InitializeCollectionEventListener;
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.LoadEventListener.LoadType;
 import org.hibernate.event.LockEvent;
 import org.hibernate.event.LockEventListener;
 import org.hibernate.event.MergeEvent;
 import org.hibernate.event.MergeEventListener;
 import org.hibernate.event.PersistEvent;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.event.RefreshEvent;
 import org.hibernate.event.RefreshEventListener;
 import org.hibernate.event.ReplicateEvent;
 import org.hibernate.event.ReplicateEventListener;
 import org.hibernate.event.SaveOrUpdateEvent;
 import org.hibernate.event.SaveOrUpdateEventListener;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.stat.SessionStatistics;
-import org.hibernate.stat.SessionStatisticsImpl;
+import org.hibernate.stat.internal.SessionStatisticsImpl;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * Concrete implementation of a Session.
  *
  * Exposes two interfaces:<ul>
  *     <li>{@link Session} to the application</li>
  *     <li>{@link org.hibernate.engine.SessionImplementor} to other Hibernate components (SPI)</li>
  * </ul>
  *
  * This class is not thread-safe.
  *
  * @author Gavin King
  */
 public final class SessionImpl
 		extends AbstractSessionImpl
 		implements EventSource,
 				   org.hibernate.Session,
 				   TransactionContext,
 				   LobCreationContext {
 
 	// todo : need to find a clean way to handle the "event source" role
 	// a separate class responsible for generating/dispatching events just duplicates most of the Session methods...
 	// passing around separate interceptor, factory, actionQueue, and persistentContext is not manageable...
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SessionImpl.class.getName());
 
 	private transient long timestamp;
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
 	private transient TransactionCoordinatorImpl transactionCoordinator;
 	private transient EventListeners listeners;
 	private transient Interceptor interceptor;
 	private transient EntityNameResolver entityNameResolver = new CoordinatingEntityNameResolver();
 
 	private transient ConnectionReleaseMode connectionReleaseMode;
 	private transient FlushMode flushMode = FlushMode.AUTO;
 	private transient CacheMode cacheMode = CacheMode.NORMAL;
 	private transient EntityMode entityMode = EntityMode.POJO;
 	private transient boolean autoClear; //for EJB3
 	private transient boolean autoJoinTransactions = true;
 
 	private transient int dontFlushFromFind = 0;
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
 	private transient Session rootSession;
 	private transient Map childSessionsByEntityMode;
 
 	/**
 	 * Constructor used in building "child sessions".
 	 *
 	 * @param parent The parent session
 	 * @param entityMode
 	 */
 	private SessionImpl(SessionImpl parent, EntityMode entityMode) {
 		super( parent.factory );
 		this.rootSession = parent;
 		this.timestamp = parent.timestamp;
 		this.transactionCoordinator = parent.transactionCoordinator;
 		this.interceptor = parent.interceptor;
 		this.listeners = parent.listeners;
 		this.actionQueue = new ActionQueue( this );
 		this.entityMode = entityMode;
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = false;
 		this.autoCloseSessionEnabled = false;
 		this.connectionReleaseMode = null;
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
         if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
 
         LOG.debugf("Opened session [%s]", entityMode);
 	}
 
 	/**
 	 * Constructor used for openSession(...) processing, as well as construction
 	 * of sessions for getCurrentSession().
 	 *
 	 * @param connection The user-supplied connection to use for this session.
 	 * @param factory The factory from which this session was obtained
 	 * @param autoclose NOT USED
 	 * @param timestamp The timestamp for this session
 	 * @param interceptor The interceptor to be applied to this session
 	 * @param entityMode The entity-mode for this session
 	 * @param flushBeforeCompletionEnabled Should we auto flush before completion of transaction
 	 * @param autoCloseSessionEnabled Should we auto close after completion of transaction
 	 * @param connectionReleaseMode The mode by which we should release JDBC connections.
 	 */
 	SessionImpl(
 			final Connection connection,
 			final SessionFactoryImpl factory,
 			final boolean autoclose,
 			final long timestamp,
 			final Interceptor interceptor,
 			final EntityMode entityMode,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode) {
 		super( factory );
 		this.rootSession = null;
 		this.timestamp = timestamp;
 		this.entityMode = entityMode;
 		this.interceptor = interceptor;
 		this.listeners = factory.getEventListeners();
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.connectionReleaseMode = connectionReleaseMode;
 
 		this.transactionCoordinator = new TransactionCoordinatorImpl( connection, this );
 		this.transactionCoordinator.getJdbcCoordinator().getLogicalConnection().addObserver(
 				new ConnectionObserverStatsBridge( factory )
 		);
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
         if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
 
         LOG.debugf("Opened session at timestamp: %s", timestamp);
 	}
 
 	public Session getSession(EntityMode entityMode) {
 		if ( this.entityMode == entityMode ) {
 			return this;
 		}
 
 		if ( rootSession != null ) {
 			return rootSession.getSession( entityMode );
 		}
 
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		SessionImpl rtn = null;
 		if ( childSessionsByEntityMode == null ) {
 			childSessionsByEntityMode = new HashMap();
 		}
 		else {
 			rtn = (SessionImpl) childSessionsByEntityMode.get( entityMode );
 		}
 
 		if ( rtn == null ) {
 			rtn = new SessionImpl( this, entityMode );
 			childSessionsByEntityMode.put( entityMode, rtn );
 		}
 
 		return rtn;
 	}
 
 	public void clear() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.clear();
 		actionQueue.clear();
 	}
 
 	public long getTimestamp() {
 		checkTransactionSynchStatus();
 		return timestamp;
 	}
 
 	public Connection close() throws HibernateException {
         LOG.trace("Closing session");
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed" );
 		}
 
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().closeSession();
 		}
 
 		try {
 			try {
 				if ( childSessionsByEntityMode != null ) {
 					Iterator childSessions = childSessionsByEntityMode.values().iterator();
 					while ( childSessions.hasNext() ) {
 						final SessionImpl child = ( SessionImpl ) childSessions.next();
 						child.close();
 					}
 				}
 			}
 			catch( Throwable t ) {
 				// just ignore
 			}
 
 			if ( rootSession == null ) {
 				return transactionCoordinator.close();
 			}
 			else {
 				return null;
 			}
 		}
 		finally {
 			setClosed();
 			cleanup();
 		}
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return autoJoinTransactions;
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	public boolean isOpen() {
 		checkTransactionSynchStatus();
 		return !isClosed();
 	}
 
 	public boolean isFlushModeNever() {
 		return FlushMode.isManualFlushMode( getFlushMode() );
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
 	public void managedFlush() {
 		if ( isClosed() ) {
             LOG.trace("Skipping auto-flush due to session closed");
 			return;
 		}
         LOG.trace( "Automatically flushing session" );
 		flush();
 
 		if ( childSessionsByEntityMode != null ) {
 			Iterator iter = childSessionsByEntityMode.values().iterator();
 			while ( iter.hasNext() ) {
 				( (Session) iter.next() ).flush();
 			}
 		}
 	}
 
 	/**
 	 * Return changes to this session and its child sessions that have not been flushed yet.
 	 * <p/>
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		NonFlushedChanges nonFlushedChanges = new NonFlushedChangesImpl( this );
 		if ( childSessionsByEntityMode != null ) {
 			Iterator it = childSessionsByEntityMode.values().iterator();
 			while ( it.hasNext() ) {
 				nonFlushedChanges.extractFromSession( ( EventSource ) it.next() );
 			}
 		}
 		return nonFlushedChanges;
 	}
 
 	/**
 	 * Apply non-flushed changes from a different session to this session. It is assumed
 	 * that this SessionImpl is "clean" (e.g., has no non-flushed changes, no cached entities,
 	 * no cached collections, no queued actions). The specified NonFlushedChanges object cannot
 	 * be bound to any session.
 	 * <p/>
 	 * @param nonFlushedChanges the non-flushed changes
 	 */
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		replacePersistenceContext( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getPersistenceContext( entityMode) );
 		replaceActionQueue( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getActionQueue( entityMode ) );
 		if ( childSessionsByEntityMode != null ) {
 			for ( Iterator it = childSessionsByEntityMode.values().iterator(); it.hasNext(); ) {
 				( ( SessionImpl ) it.next() ).applyNonFlushedChanges( nonFlushedChanges );
 			}
 		}
 	}
 
 	private void replacePersistenceContext(StatefulPersistenceContext persistenceContextNew) {
 		if ( persistenceContextNew.getSession() != null ) {
 			throw new IllegalStateException( "new persistence context is already connected to a session " );
 		}
 		persistenceContext.clear();
 		ObjectInputStream ois = null;
 		try {
 			ois = new ObjectInputStream( new ByteArrayInputStream( serializePersistenceContext( persistenceContextNew ) ) );
 			this.persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not deserialize the persistence context",  ex );
 		}
 		catch (ClassNotFoundException ex) {
 			throw new SerializationException( "could not deserialize the persistence context", ex );
 		}
 		finally {
 			try {
 				if (ois != null) ois.close();
 			}
 			catch (IOException ex) {}
 		}
 	}
 
 	private static byte[] serializePersistenceContext(StatefulPersistenceContext pc) {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream( 512 );
 		ObjectOutputStream oos = null;
 		try {
 			oos = new ObjectOutputStream( baos );
 			( pc ).serialize( oos );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not serialize persistence context", ex );
 		}
 		finally {
 			if ( oos != null ) {
 				try {
 					oos.close();
 				}
 				catch( IOException ex ) {
 					//ignore
 				}
 			}
 		}
 		return baos.toByteArray();
 	}
 
 	private void replaceActionQueue(ActionQueue actionQueueNew) {
 		if ( actionQueue.hasAnyQueuedActions() ) {
 			throw new IllegalStateException( "cannot replace an ActionQueue with queued actions " );
 		}
 		actionQueue.clear();
 		ObjectInputStream ois = null;
 		try {
 			ois = new ObjectInputStream( new ByteArrayInputStream( serializeActionQueue( actionQueueNew ) ) );
 			actionQueue = ActionQueue.deserialize( ois, this );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not deserialize the action queue",  ex );
 		}
 		catch (ClassNotFoundException ex) {
 			throw new SerializationException( "could not deserialize the action queue", ex );
 		}
 		finally {
 			try {
 				if (ois != null) ois.close();
 			}
 			catch (IOException ex) {}
 		}
 	}
 
 	private static byte[] serializeActionQueue(ActionQueue actionQueue) {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream( 512 );
 		ObjectOutputStream oos = null;
 		try {
 			oos = new ObjectOutputStream( baos );
 			actionQueue.serialize( oos );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not serialize action queue", ex );
 		}
 		finally {
 			if ( oos != null ) {
 				try {
 					oos.close();
 				}
 				catch( IOException ex ) {
 					//ignore
 				}
 			}
 		}
 		return baos.toByteArray();
 	}
 
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 	public void managedClose() {
         LOG.trace( "Automatically closing session" );
 		close();
 	}
 
 	public Connection connection() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getDistinctConnectionProxy();
 	}
 
 	public boolean isConnected() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isOpen();
 	}
 
 	public boolean isTransactionInProgress() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.isTransactionInProgress();
 	}
 
 	@Override
 	public Connection disconnect() throws HibernateException {
 		errorIfClosed();
         LOG.debugf("Disconnecting session");
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
         LOG.debugf("Reconnecting session");
 		checkTransactionSynchStatus();
 		transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualReconnect( conn );
 	}
 
 	public void setAutoClear(boolean enabled) {
 		errorIfClosed();
 		autoClear = enabled;
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		errorIfClosed();
 		autoJoinTransactions = false;
 	}
 
 	/**
 	 * Check if there is a Hibernate or JTA transaction in progress and,
 	 * if there is not, flush if necessary, make sure the connection has
 	 * been committed (if it is not in autocommit mode) and run the after
 	 * completion processing
 	 */
 	public void afterOperation(boolean success) {
 		if ( ! transactionCoordinator.isTransactionInProgress() ) {
 			transactionCoordinator.afterNonTransactionalQuery( success );
 		}
 	}
 
 	@Override
 	public void afterTransactionBegin(TransactionImplementor hibernateTransaction) {
 		errorIfClosed();
 		interceptor.afterTransactionBegin( hibernateTransaction );
 	}
 
 	@Override
 	public void beforeTransactionCompletion(TransactionImplementor hibernateTransaction) {
 		LOG.trace( "before transaction completion" );
 		actionQueue.beforeTransactionCompletion();
 		if ( rootSession == null ) {
 			try {
 				interceptor.beforeTransactionCompletion( hibernateTransaction );
 			}
 			catch (Throwable t) {
                 LOG.exceptionInBeforeTransactionCompletionInterceptor(t);
 			}
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion(TransactionImplementor hibernateTransaction, boolean successful) {
 		LOG.trace( "after transaction completion" );
 		persistenceContext.afterTransactionCompletion();
 		actionQueue.afterTransactionCompletion( successful );
 		if ( rootSession == null && hibernateTransaction != null ) {
 			try {
 				interceptor.afterTransactionCompletion( hibernateTransaction );
 			}
 			catch (Throwable t) {
                 LOG.exceptionInAfterTransactionCompletionInterceptor(t);
 			}
 		}
 		if ( autoClear ) {
 			clear();
 		}
 	}
 
 	/**
 	 * clear all the internal collections, just
 	 * to help the garbage collector, does not
 	 * clear anything that is needed during the
 	 * afterTransactionCompletion() phase
 	 */
 	private void cleanup() {
 		persistenceContext.clear();
 	}
 
 	public LockMode getCurrentLockMode(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object == null ) {
 			throw new NullPointerException( "null object passed to getCurrentLockMode()" );
 		}
 		if ( object instanceof HibernateProxy ) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation(this);
 			if ( object == null ) {
 				return LockMode.NONE;
 			}
 		}
 		EntityEntry e = persistenceContext.getEntry(object);
 		if ( e == null ) {
 			throw new TransientObjectException( "Given object not associated with the session" );
 		}
 		if ( e.getStatus() != Status.MANAGED ) {
 			throw new ObjectDeletedException(
 					"The given object was deleted",
 					e.getId(),
 					e.getPersister().getEntityName()
 				);
 		}
 		return e.getLockMode();
 	}
 
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		// todo : should this get moved to PersistentContext?
 		// logically, is PersistentContext the "thing" to which an interceptor gets attached?
 		final Object result = persistenceContext.getEntity(key);
 		if ( result == null ) {
 			final Object newObject = interceptor.getEntity( key.getEntityName(), key.getIdentifier() );
 			if ( newObject != null ) {
 				lock( newObject, LockMode.NONE );
 			}
 			return newObject;
 		}
 		else {
 			return result;
 		}
 	}
 
 
 	// saveOrUpdate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void saveOrUpdate(Object object) throws HibernateException {
 		saveOrUpdate( null, object );
 	}
 
 	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
 		fireSaveOrUpdate( new SaveOrUpdateEvent( entityName, obj, this ) );
 	}
 
 	private void fireSaveOrUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		SaveOrUpdateEventListener[] saveOrUpdateEventListener = listeners.getSaveOrUpdateEventListeners();
 		for ( int i = 0; i < saveOrUpdateEventListener.length; i++ ) {
 			saveOrUpdateEventListener[i].onSaveOrUpdate(event);
 		}
 	}
 
 
 	// save() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Serializable save(Object obj) throws HibernateException {
 		return save( null, obj );
 	}
 
 	public Serializable save(String entityName, Object object) throws HibernateException {
 		return fireSave( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private Serializable fireSave(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		SaveOrUpdateEventListener[] saveEventListener = listeners.getSaveEventListeners();
 		for ( int i = 0; i < saveEventListener.length; i++ ) {
 			saveEventListener[i].onSaveOrUpdate(event);
 		}
 		return event.getResultId();
 	}
 
 
 	// update() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void update(Object obj) throws HibernateException {
 		update(null, obj);
 	}
 
 	public void update(String entityName, Object object) throws HibernateException {
 		fireUpdate( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private void fireUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		SaveOrUpdateEventListener[] updateEventListener = listeners.getUpdateEventListeners();
 		for ( int i = 0; i < updateEventListener.length; i++ ) {
 			updateEventListener[i].onSaveOrUpdate(event);
 		}
 	}
 
 
 	// lock() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent(entityName, object, lockMode, this) );
 	}
 
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return new LockRequestImpl(lockOptions);
 	}
 
 	public void lock(Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent(object, lockMode, this) );
 	}
 
 	private void fireLock(String entityName, Object object, LockOptions options) {
 		fireLock( new LockEvent( entityName, object, options, this) );
 	}
 
 	private void fireLock( Object object, LockOptions options) {
 		fireLock( new LockEvent( object, options, this ) );
 	}
 
 	private void fireLock(LockEvent lockEvent) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LockEventListener[] lockEventListener = listeners.getLockEventListeners();
 		for ( int i = 0; i < lockEventListener.length; i++ ) {
 			lockEventListener[i].onLock( lockEvent );
 		}
 	}
 
 
 	// persist() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persist(String entityName, Object object) throws HibernateException {
 		firePersist( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persist(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	public void persist(String entityName, Object object, Map copiedAlready)
 	throws HibernateException {
 		firePersist( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersist(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		PersistEventListener[] persistEventListener = listeners.getPersistEventListeners();
 		for ( int i = 0; i < persistEventListener.length; i++ ) {
 			persistEventListener[i].onPersist(event, copiedAlready);
 		}
 	}
 
 	private void firePersist(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		PersistEventListener[] createEventListener = listeners.getPersistEventListeners();
 		for ( int i = 0; i < createEventListener.length; i++ ) {
 			createEventListener[i].onPersist(event);
 		}
 	}
 
 
 	// persistOnFlush() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persistOnFlush(String entityName, Object object)
 			throws HibernateException {
 		firePersistOnFlush( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persistOnFlush(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	public void persistOnFlush(String entityName, Object object, Map copiedAlready)
 			throws HibernateException {
 		firePersistOnFlush( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersistOnFlush(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		PersistEventListener[] persistEventListener = listeners.getPersistOnFlushEventListeners();
 		for ( int i = 0; i < persistEventListener.length; i++ ) {
 			persistEventListener[i].onPersist(event, copiedAlready);
 		}
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		PersistEventListener[] createEventListener = listeners.getPersistOnFlushEventListeners();
 		for ( int i = 0; i < createEventListener.length; i++ ) {
 			createEventListener[i].onPersist(event);
 		}
 	}
 
 
 	// merge() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object merge(String entityName, Object object) throws HibernateException {
 		return fireMerge( new MergeEvent( entityName, object, this ) );
 	}
 
 	public Object merge(Object object) throws HibernateException {
 		return merge( null, object );
 	}
 
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		fireMerge( copiedAlready, new MergeEvent( entityName, object, this ) );
 	}
 
 	private Object fireMerge(MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		MergeEventListener[] mergeEventListener = listeners.getMergeEventListeners();
 		for ( int i = 0; i < mergeEventListener.length; i++ ) {
 			mergeEventListener[i].onMerge(event);
 		}
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		MergeEventListener[] mergeEventListener = listeners.getMergeEventListeners();
 		for ( int i = 0; i < mergeEventListener.length; i++ ) {
 			mergeEventListener[i].onMerge(event, copiedAlready);
 		}
 	}
 
 
 	// delete() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Delete a persistent object
 	 */
 	public void delete(Object object) throws HibernateException {
 		fireDelete( new DeleteEvent(object, this) );
 	}
 
 	/**
 	 * Delete a persistent object (by explicit entity name)
 	 */
 	public void delete(String entityName, Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, this ) );
 	}
 
 	/**
 	 * Delete a persistent object
 	 */
 	public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled, Set transientEntities) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, isCascadeDeleteEnabled, this ), transientEntities );
 	}
 
 	private void fireDelete(DeleteEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		DeleteEventListener[] deleteEventListener = listeners.getDeleteEventListeners();
 		for ( int i = 0; i < deleteEventListener.length; i++ ) {
 			deleteEventListener[i].onDelete( event );
 		}
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		DeleteEventListener[] deleteEventListener = listeners.getDeleteEventListeners();
 		for ( int i = 0; i < deleteEventListener.length; i++ ) {
 			deleteEventListener[i].onDelete( event, transientEntities );
 		}
 	}
 
 
 	// load()/get() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void load(Object object, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, object, this);
 		fireLoad( event, LoadEventListener.RELOAD );
 	}
 
 	public Object load(Class entityClass, Serializable id) throws HibernateException {
 		return load( entityClass.getName(), id );
 	}
 
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, false, this);
 		boolean success = false;
 		try {
 			fireLoad( event, LoadEventListener.LOAD );
 			if ( event.getResult() == null ) {
 				getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityName, id );
 			}
 			success = true;
 			return event.getResult();
 		}
 		finally {
 			afterOperation(success);
 		}
 	}
 
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
 		return get( entityClass.getName(), id );
 	}
 
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, false, this);
 		boolean success = false;
 		try {
 			fireLoad(event, LoadEventListener.GET);
 			success = true;
 			return event.getResult();
 		}
 		finally {
 			afterOperation(success);
 		}
 	}
 
 	/**
 	 * Load the data for the object with the specified id into a newly created object.
 	 * This is only called when lazily initializing a proxy.
 	 * Do NOT return a proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
         if (LOG.isDebugEnabled()) {
 			EntityPersister persister = getFactory().getEntityPersister(entityName);
             LOG.debugf("Initializing proxy: %s", MessageHelper.infoString(persister, id, getFactory()));
 		}
 
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, LoadEventListener.IMMEDIATE_LOAD);
 		return event.getResult();
 	}
 
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		// todo : remove
 		LoadEventListener.LoadType type = nullable
 				? LoadEventListener.INTERNAL_LOAD_NULLABLE
 				: eager
 						? LoadEventListener.INTERNAL_LOAD_EAGER
 						: LoadEventListener.INTERNAL_LOAD_LAZY;
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, type);
 		if ( !nullable ) {
 			UnresolvableObjectException.throwIfNull( event.getResult(), id, entityName );
 		}
 		return event.getResult();
 	}
 
 	public Object load(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return load( entityClass.getName(), id, lockMode );
 	}
 
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return load( entityClass.getName(), id, lockOptions );
 	}
 
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
 		fireLoad( event, LoadEventListener.LOAD );
 		return event.getResult();
 	}
 
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockOptions, this);
 		fireLoad( event, LoadEventListener.LOAD );
 		return event.getResult();
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return get( entityClass.getName(), id, lockOptions );
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
 	   	fireLoad(event, LoadEventListener.GET);
 		return event.getResult();
 	}
 
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockOptions, this);
 	   	fireLoad( event, LoadEventListener.GET );
 		return event.getResult();
 	}
 
 	private void fireLoad(LoadEvent event, LoadType loadType) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LoadEventListener[] loadEventListener = listeners.getLoadEventListeners();
 		for ( int i = 0; i < loadEventListener.length; i++ ) {
 			loadEventListener[i].onLoad(event, loadType);
 		}
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void refresh(Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, this) );
 	}
 
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, lockMode, this) );
 	}
 
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, lockOptions, this) );
 	}
 
 	public void refresh(Object object, Map refreshedAlready) throws HibernateException {
 		fireRefresh( refreshedAlready, new RefreshEvent( object, this ) );
 	}
 
 	private void fireRefresh(RefreshEvent refreshEvent) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		RefreshEventListener[] refreshEventListener = listeners.getRefreshEventListeners();
 		for ( int i = 0; i < refreshEventListener.length; i++ ) {
 			refreshEventListener[i].onRefresh( refreshEvent );
 		}
 	}
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent refreshEvent) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		RefreshEventListener[] refreshEventListener = listeners.getRefreshEventListeners();
 		for ( int i = 0; i < refreshEventListener.length; i++ ) {
 			refreshEventListener[i].onRefresh( refreshEvent, refreshedAlready );
 		}
 	}
 
 
 	// replicate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
 		fireReplicate( new ReplicateEvent(obj, replicationMode, this) );
 	}
 
 	public void replicate(String entityName, Object obj, ReplicationMode replicationMode)
 	throws HibernateException {
 		fireReplicate( new ReplicateEvent( entityName, obj, replicationMode, this ) );
 	}
 
 	private void fireReplicate(ReplicateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		ReplicateEventListener[] replicateEventListener = listeners.getReplicateEventListeners();
 		for ( int i = 0; i < replicateEventListener.length; i++ ) {
 			replicateEventListener[i].onReplicate(event);
 		}
 	}
 
 
 	// evict() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * remove any hard references to the entity that are held by the infrastructure
 	 * (references held by application or other persistant instances are okay)
 	 */
 	public void evict(Object object) throws HibernateException {
 		fireEvict( new EvictEvent( object, this ) );
 	}
 
 	private void fireEvict(EvictEvent evictEvent) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		EvictEventListener[] evictEventListener = listeners.getEvictEventListeners();
 		for ( int i = 0; i < evictEventListener.length; i++ ) {
 			evictEventListener[i].onEvict( evictEvent );
 		}
 	}
 
 	/**
 	 * detect in-memory changes, determine if the changes are to tables
 	 * named in the query and, if so, complete execution the flush
 	 */
 	protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
 		errorIfClosed();
 		if ( ! isTransactionInProgress() ) {
 			// do not auto-flush while outside a transaction
 			return false;
 		}
 		AutoFlushEvent event = new AutoFlushEvent(querySpaces, this);
 		AutoFlushEventListener[] autoFlushEventListener = listeners.getAutoFlushEventListeners();
 		for ( int i = 0; i < autoFlushEventListener.length; i++ ) {
 			autoFlushEventListener[i].onAutoFlush(event);
 		}
 		return event.isFlushRequired();
 	}
 
 	public boolean isDirty() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
         LOG.debugf("Checking session dirtiness");
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
             LOG.debugf("Session dirty (scheduled updates and insertions)");
 			return true;
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
index 0049f1f0a3..145e801df4 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
@@ -1,72 +1,72 @@
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
 package org.hibernate.impl;
 
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistry;
-import org.hibernate.stat.StatisticsImplementor;
+import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class TransactionEnvironmentImpl implements TransactionEnvironment {
 	private final SessionFactoryImpl sessionFactory;
 
 	public TransactionEnvironmentImpl(SessionFactoryImpl sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	protected ServiceRegistry serviceRegistry() {
 		return sessionFactory.getServiceRegistry();
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return serviceRegistry().getService( JdbcServices.class );
 	}
 
 	@Override
 	public JtaPlatform getJtaPlatform() {
 		return serviceRegistry().getService( JtaPlatform.class );
 	}
 
 	@Override
 	public TransactionFactory getTransactionFactory() {
 		return serviceRegistry().getService( TransactionFactory.class );
 	}
 
 	@Override
 	public StatisticsImplementor getStatisticsImplementor() {
 		return sessionFactory.getStatisticsImplementor();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
index 0c5b0f58b3..106813fb1b 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
@@ -1,315 +1,316 @@
 //$Id: StatisticsService.java 8262 2005-09-30 07:48:53Z oneovthafew $
 package org.hibernate.jmx;
 import javax.naming.InitialContext;
 import javax.naming.NameNotFoundException;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import org.hibernate.HibernateLogger;
 import org.hibernate.SessionFactory;
 import org.hibernate.impl.SessionFactoryObjectFactory;
 import org.hibernate.stat.CollectionStatistics;
 import org.hibernate.stat.EntityStatistics;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.Statistics;
-import org.hibernate.stat.StatisticsImpl;
+import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
+
 import org.jboss.logging.Logger;
 
 /**
  * JMX service for Hibernate statistics<br>
  * <br>
  * Register this MBean in your JMX server for a specific session factory
  * <pre>
  * //build the ObjectName you want
  * Hashtable tb = new Hashtable();
  * tb.put("type", "statistics");
  * tb.put("sessionFactory", "myFinancialApp");
  * ObjectName on = new ObjectName("hibernate", tb);
  * StatisticsService stats = new StatisticsService();
  * stats.setSessionFactory(sessionFactory);
  * server.registerMBean(stats, on);
  * </pre>
  * And call the MBean the way you want<br>
  * <br>
  * Register this MBean in your JMX server with no specific session factory
  * <pre>
  * //build the ObjectName you want
  * Hashtable tb = new Hashtable();
  * tb.put("type", "statistics");
  * tb.put("sessionFactory", "myFinancialApp");
  * ObjectName on = new ObjectName("hibernate", tb);
  * StatisticsService stats = new StatisticsService();
  * server.registerMBean(stats, on);
  * </pre>
  * And call the MBean by providing the <code>SessionFactoryJNDIName</code> first.
  * Then the session factory will be retrieved from JNDI and the statistics
  * loaded.
  *
  * @author Emmanuel Bernard
  */
 public class StatisticsService implements StatisticsServiceMBean {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, StatisticsService.class.getName());
 	//TODO: We probably should have a StatisticsNotPublishedException, to make it clean
 
 	SessionFactory sf;
 	String sfJNDIName;
-	Statistics stats = new StatisticsImpl();
+	Statistics stats = new ConcurrentStatisticsImpl();
 
 	/**
 	 * @see StatisticsServiceMBean#setSessionFactoryJNDIName(java.lang.String)
 	 */
 	public void setSessionFactoryJNDIName(String sfJNDIName) {
 		this.sfJNDIName = sfJNDIName;
 		try {
 			Object obj = new InitialContext().lookup(sfJNDIName);
 			if (obj instanceof Reference) {
 				Reference ref = (Reference) obj;
 				setSessionFactory( (SessionFactory) SessionFactoryObjectFactory.getInstance( (String) ref.get(0).getContent() ) );
 			}
 			else {
 				setSessionFactory( (SessionFactory) obj );
 			}
 		}
 		catch (NameNotFoundException e) {
             LOG.noSessionFactoryWithJndiName(sfJNDIName, e);
 			setSessionFactory(null);
 		}
 		catch (NamingException e) {
             LOG.unableToAccessSessionFactory(sfJNDIName, e);
 			setSessionFactory(null);
 		}
 		catch (ClassCastException e) {
             LOG.jndiNameDoesNotHandleSessionFactoryReference(sfJNDIName, e);
 			setSessionFactory(null);
 		}
 	}
 
 	/**
 	 * Useful to init this MBean wo a JNDI session factory name
 	 *
 	 * @param sf session factory to register
 	 */
 	public void setSessionFactory(SessionFactory sf) {
 		this.sf = sf;
 		if (sf == null) {
-			stats = new StatisticsImpl();
+			stats = new ConcurrentStatisticsImpl();
 		}
 		else {
 			stats = sf.getStatistics();
 		}
 
 	}
 	/**
 	 * @see StatisticsServiceMBean#clear()
 	 */
 	public void clear() {
 		stats.clear();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityStatistics(java.lang.String)
 	 */
 	public EntityStatistics getEntityStatistics(String entityName) {
 		return stats.getEntityStatistics(entityName);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionStatistics(java.lang.String)
 	 */
 	public CollectionStatistics getCollectionStatistics(String role) {
 		return stats.getCollectionStatistics(role);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheStatistics(java.lang.String)
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
 		return stats.getSecondLevelCacheStatistics(regionName);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getQueryStatistics(java.lang.String)
 	 */
 	public QueryStatistics getQueryStatistics(String hql) {
 		return stats.getQueryStatistics(hql);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityDeleteCount()
 	 */
 	public long getEntityDeleteCount() {
 		return stats.getEntityDeleteCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityInsertCount()
 	 */
 	public long getEntityInsertCount() {
 		return stats.getEntityInsertCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityLoadCount()
 	 */
 	public long getEntityLoadCount() {
 		return stats.getEntityLoadCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityFetchCount()
 	 */
 	public long getEntityFetchCount() {
 		return stats.getEntityFetchCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityUpdateCount()
 	 */
 	public long getEntityUpdateCount() {
 		return stats.getEntityUpdateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getQueryExecutionCount()
 	 */
 	public long getQueryExecutionCount() {
 		return stats.getQueryExecutionCount();
 	}
 	public long getQueryCacheHitCount() {
 		return stats.getQueryCacheHitCount();
 	}
 	public long getQueryExecutionMaxTime() {
 		return stats.getQueryExecutionMaxTime();
 	}
 	public long getQueryCacheMissCount() {
 		return stats.getQueryCacheMissCount();
 	}
 	public long getQueryCachePutCount() {
 		return stats.getQueryCachePutCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getFlushCount()
 	 */
 	public long getFlushCount() {
 		return stats.getFlushCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getConnectCount()
 	 */
 	public long getConnectCount() {
 		return stats.getConnectCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheHitCount()
 	 */
 	public long getSecondLevelCacheHitCount() {
 		return stats.getSecondLevelCacheHitCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheMissCount()
 	 */
 	public long getSecondLevelCacheMissCount() {
 		return stats.getSecondLevelCacheMissCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCachePutCount()
 	 */
 	public long getSecondLevelCachePutCount() {
 		return stats.getSecondLevelCachePutCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSessionCloseCount()
 	 */
 	public long getSessionCloseCount() {
 		return stats.getSessionCloseCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSessionOpenCount()
 	 */
 	public long getSessionOpenCount() {
 		return stats.getSessionOpenCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionLoadCount()
 	 */
 	public long getCollectionLoadCount() {
 		return stats.getCollectionLoadCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionFetchCount()
 	 */
 	public long getCollectionFetchCount() {
 		return stats.getCollectionFetchCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionUpdateCount()
 	 */
 	public long getCollectionUpdateCount() {
 		return stats.getCollectionUpdateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionRemoveCount()
 	 */
 	public long getCollectionRemoveCount() {
 		return stats.getCollectionRemoveCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionRecreateCount()
 	 */
 	public long getCollectionRecreateCount() {
 		return stats.getCollectionRecreateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getStartTime()
 	 */
 	public long getStartTime() {
 		return stats.getStartTime();
 	}
 
 	/**
 	 * @see StatisticsServiceMBean#isStatisticsEnabled()
 	 */
 	public boolean isStatisticsEnabled() {
 		return stats.isStatisticsEnabled();
 	}
 
 	/**
 	 * @see StatisticsServiceMBean#setStatisticsEnabled(boolean)
 	 */
 	public void setStatisticsEnabled(boolean enable) {
 		stats.setStatisticsEnabled(enable);
 	}
 
 	public void logSummary() {
 		stats.logSummary();
 	}
 
 	public String[] getCollectionRoleNames() {
 		return stats.getCollectionRoleNames();
 	}
 
 	public String[] getEntityNames() {
 		return stats.getEntityNames();
 	}
 
 	public String[] getQueries() {
 		return stats.getQueries();
 	}
 
 	public String[] getSecondLevelCacheRegionNames() {
 		return stats.getSecondLevelCacheRegionNames();
 	}
 
 	public long getSuccessfulTransactionCount() {
 		return stats.getSuccessfulTransactionCount();
 	}
 	public long getTransactionCount() {
 		return stats.getTransactionCount();
 	}
 
 	public long getCloseStatementCount() {
 		return stats.getCloseStatementCount();
 	}
 	public long getPrepareStatementCount() {
 		return stats.getPrepareStatementCount();
 	}
 
 	public long getOptimisticFailureCount() {
 		return stats.getOptimisticFailureCount();
 	}
 
 	public String getQueryExecutionMaxTimeQueryString() {
 		return stats.getQueryExecutionMaxTimeQueryString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatistics.java
index c1e94ddfec..7cc4ec5000 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatistics.java
@@ -1,45 +1,44 @@
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
 package org.hibernate.stat;
+
 import java.io.Serializable;
 
 /**
  * Collection related statistics
  *
  * @author Gavin King
- * @author Alex Snaps
  */
 public interface CollectionStatistics extends Serializable {
 
 	long getLoadCount();
 
 	long getFetchCount();
 
 	long getRecreateCount();
 
 	long getRemoveCount();
 
 	long getUpdateCount();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatisticsImpl.java
deleted file mode 100644
index f2e9c5946f..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/stat/CollectionStatisticsImpl.java
+++ /dev/null
@@ -1,76 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
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
- *
- */
-package org.hibernate.stat;
-
-
-/**
- * Collection related statistics
- *
- * @author Gavin King
- */
-public class CollectionStatisticsImpl extends CategorizedStatistics implements CollectionStatistics {
-
-	CollectionStatisticsImpl(String role) {
-		super(role);
-	}
-
-	long loadCount;
-	long fetchCount;
-	long updateCount;
-	long removeCount;
-	long recreateCount;
-
-	public long getLoadCount() {
-		return loadCount;
-	}
-
-	public long getFetchCount() {
-		return fetchCount;
-	}
-
-	public long getRecreateCount() {
-		return recreateCount;
-	}
-
-	public long getRemoveCount() {
-		return removeCount;
-	}
-
-	public long getUpdateCount() {
-		return updateCount;
-	}
-
-	public String toString() {
-		return new StringBuilder()
-				.append("CollectionStatistics")
-				.append("[loadCount=").append(this.loadCount)
-				.append(",fetchCount=").append(this.fetchCount)
-				.append(",recreateCount=").append(this.recreateCount)
-				.append(",removeCount=").append(this.removeCount)
-				.append(",updateCount=").append(this.updateCount)
-				.append(']')
-				.toString();
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentSecondLevelCacheStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentSecondLevelCacheStatisticsImpl.java
deleted file mode 100644
index f1550a1333..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentSecondLevelCacheStatisticsImpl.java
+++ /dev/null
@@ -1,87 +0,0 @@
-package org.hibernate.stat;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import java.util.concurrent.atomic.AtomicLong;
-import org.hibernate.cache.CacheKey;
-import org.hibernate.cache.Region;
-
-/**
- * Second level cache statistics of a specific region
- *
- * @author Alex Snaps
- */
-public class ConcurrentSecondLevelCacheStatisticsImpl extends CategorizedStatistics implements SecondLevelCacheStatistics {
-
-	private final transient Region region;
-	private AtomicLong hitCount = new AtomicLong();
-	private AtomicLong missCount = new AtomicLong();
-	private AtomicLong putCount = new AtomicLong();
-
-	ConcurrentSecondLevelCacheStatisticsImpl(Region region) {
-		super(region.getName());
-		this.region = region;
-	}
-
-	public long getHitCount() {
-		return hitCount.get();
-	}
-
-	public long getMissCount() {
-		return missCount.get();
-	}
-
-	public long getPutCount() {
-		return putCount.get();
-	}
-
-	public long getElementCountInMemory() {
-		return region.getElementCountInMemory();
-	}
-
-	public long getElementCountOnDisk() {
-		return region.getElementCountOnDisk();
-	}
-
-	public long getSizeInMemory() {
-		return region.getSizeInMemory();
-	}
-
-	public Map getEntries() {
-		Map map = new HashMap();
-		Iterator iter = region.toMap().entrySet().iterator();
-		while (iter.hasNext()) {
-			Map.Entry me = (Map.Entry) iter.next();
-			map.put(((CacheKey) me.getKey()).getKey(), me.getValue());
-		}
-		return map;
-	}
-
-	public String toString() {
-		StringBuilder buf = new StringBuilder()
-				.append("SecondLevelCacheStatistics")
-				.append("[hitCount=").append(this.hitCount)
-				.append(",missCount=").append(this.missCount)
-				.append(",putCount=").append(this.putCount);
-		//not sure if this would ever be null but wanted to be careful
-		if (region != null) {
-			buf.append(",elementCountInMemory=").append(this.getElementCountInMemory())
-					.append(",elementCountOnDisk=").append(this.getElementCountOnDisk())
-					.append(",sizeInMemory=").append(this.getSizeInMemory());
-		}
-		buf.append(']');
-		return buf.toString();
-	}
-
-	void incrementHitCount() {
-		hitCount.getAndIncrement();
-	}
-
-	void incrementMissCount() {
-		missCount.getAndIncrement();
-	}
-
-	void incrementPutCount() {
-		putCount.getAndIncrement();
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/EntityStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/EntityStatistics.java
index b156772c2c..5d95b89c92 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/EntityStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/EntityStatistics.java
@@ -1,47 +1,46 @@
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
 package org.hibernate.stat;
+
 import java.io.Serializable;
 
 /**
  * Entity related statistics
  *
  * @author Gavin King
- * @author Alex Snaps
  */
 public interface EntityStatistics extends Serializable {
 	long getDeleteCount();
 
 	long getInsertCount();
 
 	long getLoadCount();
 
 	long getUpdateCount();
 
 	long getFetchCount();
 
 	long getOptimisticFailureCount();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/EntityStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/EntityStatisticsImpl.java
deleted file mode 100644
index 0bb299089b..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/stat/EntityStatisticsImpl.java
+++ /dev/null
@@ -1,84 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
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
- *
- */
-package org.hibernate.stat;
-
-
-
-/**
- * Entity related statistics
- *
- * @author Gavin King
- */
-public class EntityStatisticsImpl extends CategorizedStatistics implements EntityStatistics {
-
-	EntityStatisticsImpl(String name) {
-		super(name);
-	}
-
-	long loadCount;
-	long updateCount;
-	long insertCount;
-	long deleteCount;
-	long fetchCount;
-	long optimisticFailureCount;
-
-	public long getDeleteCount() {
-		return deleteCount;
-	}
-
-	public long getInsertCount() {
-		return insertCount;
-	}
-
-	public long getLoadCount() {
-		return loadCount;
-	}
-
-	public long getUpdateCount() {
-		return updateCount;
-	}
-
-	public long getFetchCount() {
-		return fetchCount;
-	}
-
-	public long getOptimisticFailureCount() {
-		return optimisticFailureCount;
-	}
-
-	public String toString() {
-		return new StringBuilder()
-				.append("EntityStatistics")
-				.append("[loadCount=").append(this.loadCount)
-				.append(",updateCount=").append(this.updateCount)
-				.append(",insertCount=").append(this.insertCount)
-				.append(",deleteCount=").append(this.deleteCount)
-				.append(",fetchCount=").append(this.fetchCount)
-				.append(",optimisticLockFailureCount=").append(this.optimisticFailureCount)
-				.append(']')
-				.toString();
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/QueryStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/QueryStatistics.java
index 2b39ad3d39..592b5bd665 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/QueryStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/QueryStatistics.java
@@ -1,52 +1,51 @@
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
 package org.hibernate.stat;
+
 import java.io.Serializable;
 
 /**
  * Query statistics (HQL and SQL)
  * <p/>
  * Note that for a cached query, the cache miss is equals to the db count
  *
  * @author Gavin King
- * @author Alex Snaps
  */
 public interface QueryStatistics extends Serializable {
 	long getExecutionCount();
 
 	long getCacheHitCount();
 
 	long getCachePutCount();
 
 	long getCacheMissCount();
 
 	long getExecutionRowCount();
 
 	long getExecutionAvgTime();
 
 	long getExecutionMaxTime();
 
 	long getExecutionMinTime();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/QueryStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/QueryStatisticsImpl.java
deleted file mode 100644
index 019bc757c3..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/stat/QueryStatisticsImpl.java
+++ /dev/null
@@ -1,138 +0,0 @@
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
- *
- */
-package org.hibernate.stat;
-
-
-/**
- * Query statistics (HQL and SQL)
- * <p/>
- * Note that for a cached query, the cache miss is equals to the db count
- *
- * @author Gavin King
- */
-public class QueryStatisticsImpl extends CategorizedStatistics implements QueryStatistics {
-
-	/*package*/
-	long cacheHitCount;
-	/*package*/
-	long cacheMissCount;
-	/*package*/
-	long cachePutCount;
-	private long executionCount;
-	private long executionRowCount;
-	private long executionAvgTime;
-	private long executionMaxTime;
-	private long executionMinTime = Long.MAX_VALUE;
-
-	QueryStatisticsImpl(String query) {
-		super(query);
-	}
-
-	/**
-	 * queries executed to the DB
-	 */
-	public long getExecutionCount() {
-		return executionCount;
-	}
-
-	/**
-	 * Queries retrieved successfully from the cache
-	 */
-	public long getCacheHitCount() {
-		return cacheHitCount;
-	}
-
-	public long getCachePutCount() {
-		return cachePutCount;
-	}
-
-	public long getCacheMissCount() {
-		return cacheMissCount;
-	}
-
-	/**
-	 * Number of lines returned by all the executions of this query (from DB)
-	 * For now, {@link org.hibernate.Query#iterate()}
-	 * and {@link org.hibernate.Query#scroll()()} do not fill this statistic
-	 *
-	 * @return The number of rows cumulatively returned by the given query; iterate
-	 *         and scroll queries do not effect this total as their number of returned rows
-	 *         is not known at execution time.
-	 */
-	public long getExecutionRowCount() {
-		return executionRowCount;
-	}
-
-	/**
-	 * average time in ms taken by the execution of this query onto the DB
-	 */
-	public long getExecutionAvgTime() {
-		return executionAvgTime;
-	}
-
-	/**
-	 * max time in ms taken by the execution of this query onto the DB
-	 */
-	public long getExecutionMaxTime() {
-		return executionMaxTime;
-	}
-
-	/**
-	 * min time in ms taken by the execution of this query onto the DB
-	 */
-	public long getExecutionMinTime() {
-		return executionMinTime;
-	}
-
-	/**
-	 * add statistics report of a DB query
-	 *
-	 * @param rows rows count returned
-	 * @param time time taken
-	 */
-	void executed(long rows, long time) {
-		if (time < executionMinTime) executionMinTime = time;
-		if (time > executionMaxTime) executionMaxTime = time;
-		executionAvgTime = (executionAvgTime * executionCount + time) / (executionCount + 1);
-		executionCount++;
-		executionRowCount += rows;
-	}
-
-	public String toString() {
-		return new StringBuilder()
-				.append("QueryStatistics")
-				.append("[cacheHitCount=").append(this.cacheHitCount)
-				.append(",cacheMissCount=").append(this.cacheMissCount)
-				.append(",cachePutCount=").append(this.cachePutCount)
-				.append(",executionCount=").append(this.executionCount)
-				.append(",executionRowCount=").append(this.executionRowCount)
-				.append(",executionAvgTime=").append(this.executionAvgTime)
-				.append(",executionMaxTime=").append(this.executionMaxTime)
-				.append(",executionMinTime=").append(this.executionMinTime)
-				.append(']')
-				.toString();
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java
index 31fe3483d1..c71583b409 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatistics.java
@@ -1,50 +1,49 @@
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
 package org.hibernate.stat;
+
 import java.io.Serializable;
 import java.util.Map;
 
 /**
  * Second level cache statistics of a specific region
  *
  * @author Gavin King
- * @author Alex Snaps
  */
 public interface SecondLevelCacheStatistics extends Serializable {
 	
 	long getHitCount();
 
 	long getMissCount();
 
 	long getPutCount();
 
 	long getElementCountInMemory();
 
 	long getElementCountOnDisk();
 
 	long getSizeInMemory();
 
 	Map getEntries();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java
index 32313f192f..42b02290f3 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/SessionStatistics.java
@@ -1,55 +1,54 @@
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
 package org.hibernate.stat;
+
 import java.util.Set;
 
 /**
  * Information about the first-level (session) cache
  * for a particular session instance
  * @author Gavin King
  */
 public interface SessionStatistics {
-
 	/**
 	 * Get the number of entity instances associated with the session
 	 */
 	public int getEntityCount();
 	/**
 	 * Get the number of collection instances associated with the session
 	 */
 	public int getCollectionCount();
 
 	/**
 	 * Get the set of all <tt>EntityKey</tt>s
 	 * @see org.hibernate.engine.EntityKey
 	 */
 	public Set getEntityKeys();
 	/**
 	 * Get the set of all <tt>CollectionKey</tt>s
 	 * @see org.hibernate.engine.CollectionKey
 	 */
 	public Set getCollectionKeys();
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java b/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
index d51a18b834..f8ac25b076 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
@@ -1,235 +1,231 @@
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
 package org.hibernate.stat;
 
-
-
 /**
- * Statistics for a particular <tt>SessionFactory</tt>.
- * Beware of milliseconds metrics, they are depdendent of the JVM precision:
- * you may then encounter a 10 ms approximation dending on you OS platform.
+ * Exposes statistics for a particular {@link org.hibernate.SessionFactory}.  Beware of milliseconds metrics, they
+ * are dependent of the JVM precision: you may then encounter a 10 ms approximation depending on you OS platform.
  * Please refer to the JVM documentation for more information.
  * 
  * @author Emmanuel Bernard
  */
 public interface Statistics {
 	/**
 	 * reset all statistics
 	 */
 	public void clear();
 
     /**
 	 * find entity statistics per name
 	 * 
 	 * @param entityName entity name
 	 * @return EntityStatistics object
 	 */
 	public EntityStatistics getEntityStatistics(String entityName);
 	/**
 	 * Get collection statistics per role
 	 * 
 	 * @param role collection role
 	 * @return CollectionStatistics
 	 */
 	public CollectionStatistics getCollectionStatistics(String role);
 
     /**
 	 * Second level cache statistics per region
 	 * 
 	 * @param regionName region name
 	 * @return SecondLevelCacheStatistics
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName);
 
     /**
 	 * Query statistics from query string (HQL or SQL)
 	 * 
 	 * @param queryString query string
 	 * @return QueryStatistics
 	 */
 	public QueryStatistics getQueryStatistics(String queryString);
 
     /**
      * Get global number of entity deletes
 	 * @return entity deletion count
 	 */
 	public long getEntityDeleteCount();
 
     /**
      * Get global number of entity inserts
 	 * @return entity insertion count
 	 */
 	public long getEntityInsertCount();
 
     /**
      * Get global number of entity loads
 	 * @return entity load (from DB)
 	 */
 	public long getEntityLoadCount();
 	/**
      * Get global number of entity fetchs
 	 * @return entity fetch (from DB)
 	 */
 	public long getEntityFetchCount();
 
 	/**
      * Get global number of entity updates
 	 * @return entity update
 	 */
 	public long getEntityUpdateCount();
 
     /**
      * Get global number of executed queries
 	 * @return query execution count
 	 */
 	public long getQueryExecutionCount();
 
     /**
      * Get the time in milliseconds of the slowest query.
      */
 	public long getQueryExecutionMaxTime();
 	/**
 	 * Get the query string for the slowest query.
 	 */
 	public String getQueryExecutionMaxTimeQueryString();
 
     /**
      * Get the global number of cached queries successfully retrieved from cache
      */
 	public long getQueryCacheHitCount();
     /**
      * Get the global number of cached queries *not* found in cache
      */
 	public long getQueryCacheMissCount();
     /**
      * Get the global number of cacheable queries put in cache
      */
 	public long getQueryCachePutCount();
 	/**
      * Get the global number of flush executed by sessions (either implicit or explicit)
      */
 	public long getFlushCount();
 	/**
 	 * Get the global number of connections asked by the sessions
      * (the actual number of connections used may be much smaller depending
      * whether you use a connection pool or not)
 	 */
 	public long getConnectCount();
 	/**
      * Global number of cacheable entities/collections successfully retrieved from the cache
      */
 	public long getSecondLevelCacheHitCount();
 	/**
      * Global number of cacheable entities/collections not found in the cache and loaded from the database.
      */
 	public long getSecondLevelCacheMissCount();
 	/**
 	 * Global number of cacheable entities/collections put in the cache
 	 */
 	public long getSecondLevelCachePutCount();
 	/**
 	 * Global number of sessions closed
 	 */
 	public long getSessionCloseCount();
 	/**
 	 * Global number of sessions opened
 	 */
 	public long getSessionOpenCount();
 	/**
 	 * Global number of collections loaded
 	 */
 	public long getCollectionLoadCount();
 	/**
 	 * Global number of collections fetched
 	 */
 	public long getCollectionFetchCount();
 	/**
 	 * Global number of collections updated
 	 */
 	public long getCollectionUpdateCount();
 	/**
 	 * Global number of collections removed
 	 */
     //even on inverse="true"
 	public long getCollectionRemoveCount();
 	/**
 	 * Global number of collections recreated
 	 */
 	public long getCollectionRecreateCount();
 	/**
 	 * @return start time in ms (JVM standards {@link System#currentTimeMillis()})
 	 */
 	public long getStartTime();
 	/**
 	 * log in info level the main statistics
 	 */
 	public void logSummary();
 	/**
 	 * Are statistics logged
 	 */
 	public boolean isStatisticsEnabled();
 	/**
 	 * Enable statistics logs (this is a dynamic parameter)
 	 */
 	public void setStatisticsEnabled(boolean b);
 
 	/**
 	 * Get all executed query strings
 	 */
 	public String[] getQueries();
 	/**
 	 * Get the names of all entities
 	 */
 	public String[] getEntityNames();
 	/**
 	 * Get the names of all collection roles
 	 */
 	public String[] getCollectionRoleNames();
 	/**
 	 * Get all second-level cache region names
 	 */
 	public String[] getSecondLevelCacheRegionNames();
 	/**
 	 * The number of transactions we know to have been successful
 	 */
 	public long getSuccessfulTransactionCount();
 	/**
 	 * The number of transactions we know to have completed
 	 */
 	public long getTransactionCount();
 	/**
 	 * The number of prepared statements that were acquired
 	 */
 	public long getPrepareStatementCount();
 	/**
 	 * The number of prepared statements that were released
 	 */
 	public long getCloseStatementCount();
 	/**
 	 * The number of <tt>StaleObjectStateException</tt>s 
 	 * that occurred
 	 */
 	public long getOptimisticFailureCount();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java
deleted file mode 100644
index a941bc8bf7..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java
+++ /dev/null
@@ -1,666 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
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
- *
- */
-package org.hibernate.stat;
-
-import java.util.HashMap;
-import java.util.Map;
-import org.hibernate.HibernateLogger;
-import org.hibernate.cache.Region;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.internal.util.collections.ArrayHelper;
-import org.jboss.logging.Logger;
-
-/**
- * @see org.hibernate.stat.Statistics
- *
- * @author Gavin King
- *
- * @deprecated Use {@link org.hibernate.stat.ConcurrentStatisticsImpl} instead
- */
-@Deprecated
-public class StatisticsImpl implements Statistics, StatisticsImplementor {
-
-	//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, StatisticsImpl.class.getName());
-
-	private SessionFactoryImplementor sessionFactory;
-
-	private boolean isStatisticsEnabled;
-	private long startTime;
-	private long sessionOpenCount;
-	private long sessionCloseCount;
-	private long flushCount;
-	private long connectCount;
-
-	private long prepareStatementCount;
-	private long closeStatementCount;
-
-	private long entityLoadCount;
-	private long entityUpdateCount;
-	private long entityInsertCount;
-	private long entityDeleteCount;
-	private long entityFetchCount;
-	private long collectionLoadCount;
-	private long collectionUpdateCount;
-	private long collectionRemoveCount;
-	private long collectionRecreateCount;
-	private long collectionFetchCount;
-
-	private long secondLevelCacheHitCount;
-	private long secondLevelCacheMissCount;
-	private long secondLevelCachePutCount;
-
-	private long queryExecutionCount;
-	private long queryExecutionMaxTime;
-	private String queryExecutionMaxTimeQueryString;
-	private long queryCacheHitCount;
-	private long queryCacheMissCount;
-	private long queryCachePutCount;
-
-	private long commitedTransactionCount;
-	private long transactionCount;
-
-	private long optimisticFailureCount;
-
-	/** second level cache statistics per region */
-	private final Map secondLevelCacheStatistics = new HashMap();
-	/** entity statistics per name */
-	private final Map entityStatistics = new HashMap();
-	/** collection statistics per name */
-	private final Map collectionStatistics = new HashMap();
-	/** entity statistics per query string (HQL or SQL) */
-	private final Map queryStatistics = new HashMap();
-
-	public StatisticsImpl() {
-		clear();
-	}
-
-	public StatisticsImpl(SessionFactoryImplementor sessionFactory) {
-		clear();
-		this.sessionFactory = sessionFactory;
-	}
-
-	/**
-	 * reset all statistics
-	 */
-	public synchronized void clear() {
-		secondLevelCacheHitCount = 0;
-		secondLevelCacheMissCount = 0;
-		secondLevelCachePutCount = 0;
-
-		sessionCloseCount = 0;
-		sessionOpenCount = 0;
-		flushCount = 0;
-		connectCount = 0;
-
-		prepareStatementCount = 0;
-		closeStatementCount = 0;
-
-		entityDeleteCount = 0;
-		entityInsertCount = 0;
-		entityUpdateCount = 0;
-		entityLoadCount = 0;
-		entityFetchCount = 0;
-
-		collectionRemoveCount = 0;
-		collectionUpdateCount = 0;
-		collectionRecreateCount = 0;
-		collectionLoadCount = 0;
-		collectionFetchCount = 0;
-
-		queryExecutionCount = 0;
-		queryCacheHitCount = 0;
-		queryExecutionMaxTime = 0;
-		queryExecutionMaxTimeQueryString = null;
-		queryCacheMissCount = 0;
-		queryCachePutCount = 0;
-
-		transactionCount = 0;
-		commitedTransactionCount = 0;
-
-		optimisticFailureCount = 0;
-
-		secondLevelCacheStatistics.clear();
-		entityStatistics.clear();
-		collectionStatistics.clear();
-		queryStatistics.clear();
-
-		startTime = System.currentTimeMillis();
-	}
-
-	public synchronized void openSession() {
-		sessionOpenCount++;
-	}
-
-	public synchronized void closeSession() {
-		sessionCloseCount++;
-	}
-
-	public synchronized void flush() {
-		flushCount++;
-	}
-
-	public synchronized void connect() {
-		connectCount++;
-	}
-
-	public synchronized void loadEntity(String entityName) {
-		entityLoadCount++;
-		((EntityStatisticsImpl) getEntityStatistics(entityName)).loadCount++;
-	}
-
-	public synchronized void fetchEntity(String entityName) {
-		entityFetchCount++;
-		((EntityStatisticsImpl) getEntityStatistics(entityName)).fetchCount++;
-	}
-
-	/**
-	 * find entity statistics per name
-	 *
-	 * @param entityName entity name
-	 * @return EntityStatistics object
-	 */
-	public synchronized EntityStatistics getEntityStatistics(String entityName) {
-		EntityStatisticsImpl es = (EntityStatisticsImpl) entityStatistics.get(entityName);
-		if (es == null) {
-			es = new EntityStatisticsImpl(entityName);
-			entityStatistics.put(entityName, es);
-		}
-		return es;
-	}
-
-	public synchronized void updateEntity(String entityName) {
-		entityUpdateCount++;
-		EntityStatisticsImpl es = (EntityStatisticsImpl) getEntityStatistics(entityName);
-		es.updateCount++;
-	}
-
-	public synchronized void insertEntity(String entityName) {
-		entityInsertCount++;
-		EntityStatisticsImpl es = (EntityStatisticsImpl) getEntityStatistics(entityName);
-		es.insertCount++;
-	}
-
-	public synchronized void deleteEntity(String entityName) {
-		entityDeleteCount++;
-		EntityStatisticsImpl es = (EntityStatisticsImpl) getEntityStatistics(entityName);
-		es.deleteCount++;
-	}
-
-	/**
-	 * Get collection statistics per role
-	 *
-	 * @param role collection role
-	 * @return CollectionStatistics
-	 */
-	public synchronized CollectionStatistics getCollectionStatistics(String role) {
-		CollectionStatisticsImpl cs = (CollectionStatisticsImpl) collectionStatistics.get(role);
-		if (cs==null) {
-			cs = new CollectionStatisticsImpl(role);
-			collectionStatistics.put(role, cs);
-		}
-		return cs;
-	}
-
-	public synchronized void loadCollection(String role) {
-		collectionLoadCount++;
-		((CollectionStatisticsImpl) getCollectionStatistics(role)).loadCount++;
-	}
-
-	public synchronized void fetchCollection(String role) {
-		collectionFetchCount++;
-		((CollectionStatisticsImpl) getCollectionStatistics(role)).fetchCount++;
-	}
-
-	public synchronized void updateCollection(String role) {
-		collectionUpdateCount++;
-		((CollectionStatisticsImpl) getCollectionStatistics(role)).updateCount++;
-	}
-
-	public synchronized void recreateCollection(String role) {
-		collectionRecreateCount++;
-		((CollectionStatisticsImpl) getCollectionStatistics(role)).recreateCount++;
-	}
-
-	public synchronized void removeCollection(String role) {
-		collectionRemoveCount++;
-		((CollectionStatisticsImpl) getCollectionStatistics(role)).removeCount++;
-	}
-
-	/**
-	 * Second level cache statistics per region
-	 *
-	 * @param regionName region name
-	 * @return SecondLevelCacheStatistics
-	 */
-	public synchronized SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
-		SecondLevelCacheStatisticsImpl slcs = (SecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.get(regionName);
-		if ( slcs == null ) {
-			if ( sessionFactory == null ) {
-				return null;
-			}
-			Region region = sessionFactory.getSecondLevelCacheRegion( regionName );
-			if ( region == null ) {
-				return null;
-			}
-			slcs = new SecondLevelCacheStatisticsImpl(region);
-			secondLevelCacheStatistics.put( regionName, slcs );
-		}
-		return slcs;
-	}
-
-	public synchronized void secondLevelCachePut(String regionName) {
-		secondLevelCachePutCount++;
-		((SecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(regionName)).putCount++;
-	}
-
-	public synchronized void secondLevelCacheHit(String regionName) {
-		secondLevelCacheHitCount++;
-		((SecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(regionName)).hitCount++;
-	}
-
-	public synchronized void secondLevelCacheMiss(String regionName) {
-		secondLevelCacheMissCount++;
-		((SecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(regionName)).missCount++;
-	}
-
-	public synchronized void queryExecuted(String hql, int rows, long time) {
-		queryExecutionCount++;
-		if (queryExecutionMaxTime<time) {
-			queryExecutionMaxTime=time;
-			queryExecutionMaxTimeQueryString = hql;
-		}
-		if (hql!=null) {
-			QueryStatisticsImpl qs = (QueryStatisticsImpl) getQueryStatistics(hql);
-			qs.executed(rows, time);
-            LOG.hql(hql, new Long(time), new Long(rows));
-		}
-	}
-
-	public synchronized void queryCacheHit(String hql, String regionName) {
-		queryCacheHitCount++;
-		if (hql!=null) {
-			QueryStatisticsImpl qs = (QueryStatisticsImpl) getQueryStatistics(hql);
-			qs.cacheHitCount++;
-		}
-		SecondLevelCacheStatisticsImpl slcs = (SecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(regionName);
-		slcs.hitCount++;
-	}
-
-	public synchronized void queryCacheMiss(String hql, String regionName) {
-		queryCacheMissCount++;
-		if (hql!=null) {
-			QueryStatisticsImpl qs = (QueryStatisticsImpl) getQueryStatistics(hql);
-			qs.cacheMissCount++;
-		}
-		SecondLevelCacheStatisticsImpl slcs = (SecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(regionName);
-		slcs.missCount++;
-	}
-
-	public synchronized void queryCachePut(String hql, String regionName) {
-		queryCachePutCount++;
-		if (hql!=null) {
-			QueryStatisticsImpl qs = (QueryStatisticsImpl) getQueryStatistics(hql);
-			qs.cachePutCount++;
-		}
-		SecondLevelCacheStatisticsImpl slcs = (SecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(regionName);
-		slcs.putCount++;
-	}
-
-	/**
-	 * Query statistics from query string (HQL or SQL)
-	 *
-	 * @param queryString query string
-	 * @return QueryStatistics
-	 */
-	public synchronized QueryStatistics getQueryStatistics(String queryString) {
-		QueryStatisticsImpl qs = (QueryStatisticsImpl) queryStatistics.get(queryString);
-		if (qs==null) {
-			qs = new QueryStatisticsImpl(queryString);
-			queryStatistics.put(queryString, qs);
-		}
-		return qs;
-	}
-
-	/**
-	 * @return entity deletion count
-	 */
-	public long getEntityDeleteCount() {
-		return entityDeleteCount;
-	}
-
-	/**
-	 * @return entity insertion count
-	 */
-	public long getEntityInsertCount() {
-		return entityInsertCount;
-	}
-
-	/**
-	 * @return entity load (from DB)
-	 */
-	public long getEntityLoadCount() {
-		return entityLoadCount;
-	}
-
-	/**
-	 * @return entity fetch (from DB)
-	 */
-	public long getEntityFetchCount() {
-		return entityFetchCount;
-	}
-
-	/**
-	 * @return entity update
-	 */
-	public long getEntityUpdateCount() {
-		return entityUpdateCount;
-	}
-
-	public long getQueryExecutionCount() {
-		return queryExecutionCount;
-	}
-
-	public long getQueryCacheHitCount() {
-		return queryCacheHitCount;
-	}
-
-	public long getQueryCacheMissCount() {
-		return queryCacheMissCount;
-	}
-
-	public long getQueryCachePutCount() {
-		return queryCachePutCount;
-	}
-
-	/**
-	 * @return flush
-	 */
-	public long getFlushCount() {
-		return flushCount;
-	}
-
-	/**
-	 * @return session connect
-	 */
-	public long getConnectCount() {
-		return connectCount;
-	}
-
-	/**
-	 * @return second level cache hit
-	 */
-	public long getSecondLevelCacheHitCount() {
-		return secondLevelCacheHitCount;
-	}
-
-	/**
-	 * @return second level cache miss
-	 */
-	public long getSecondLevelCacheMissCount() {
-		return secondLevelCacheMissCount;
-	}
-
-	/**
-	 * @return second level cache put
-	 */
-	public long getSecondLevelCachePutCount() {
-		return secondLevelCachePutCount;
-	}
-
-	/**
-	 * @return session closing
-	 */
-	public long getSessionCloseCount() {
-		return sessionCloseCount;
-	}
-
-	/**
-	 * @return session opening
-	 */
-	public long getSessionOpenCount() {
-		return sessionOpenCount;
-	}
-
-	/**
-	 * @return collection loading (from DB)
-	 */
-	public long getCollectionLoadCount() {
-		return collectionLoadCount;
-	}
-
-	/**
-	 * @return collection fetching (from DB)
-	 */
-	public long getCollectionFetchCount() {
-		return collectionFetchCount;
-	}
-
-	/**
-	 * @return collection update
-	 */
-	public long getCollectionUpdateCount() {
-		return collectionUpdateCount;
-	}
-
-	/**
-	 * @return collection removal
-	 * FIXME: even if isInverse="true"?
-	 */
-	public long getCollectionRemoveCount() {
-		return collectionRemoveCount;
-	}
-	/**
-	 * @return collection recreation
-	 */
-	public long getCollectionRecreateCount() {
-		return collectionRecreateCount;
-	}
-
-	/**
-	 * @return start time in ms (JVM standards {@link System#currentTimeMillis()})
-	 */
-	public long getStartTime() {
-		return startTime;
-	}
-
-	/**
-	 * log in info level the main statistics
-	 */
-	public void logSummary() {
-        LOG.loggingStatistics();
-        LOG.startTime(startTime);
-        LOG.sessionsOpened(sessionOpenCount);
-        LOG.sessionsClosed(sessionCloseCount);
-        LOG.transactions(transactionCount);
-        LOG.successfulTransactions(commitedTransactionCount);
-        LOG.optimisticLockFailures(optimisticFailureCount);
-        LOG.flushes(flushCount);
-        LOG.connectionsObtained(connectCount);
-        LOG.statementsPrepared(prepareStatementCount);
-        LOG.statementsClosed(closeStatementCount);
-        LOG.secondLevelCachePuts(secondLevelCachePutCount);
-        LOG.secondLevelCacheHits(secondLevelCacheHitCount);
-        LOG.secondLevelCacheMisses(secondLevelCacheMissCount);
-        LOG.entitiesLoaded(entityLoadCount);
-        LOG.entitiesUpdated(entityUpdateCount);
-        LOG.entitiesInserted(entityInsertCount);
-        LOG.entitiesDeleted(entityDeleteCount);
-        LOG.entitiesFetched(entityFetchCount);
-        LOG.collectionsLoaded(collectionLoadCount);
-        LOG.collectionsUpdated(collectionUpdateCount);
-        LOG.collectionsRemoved(collectionRemoveCount);
-        LOG.collectionsRecreated(collectionRecreateCount);
-        LOG.collectionsFetched(collectionFetchCount);
-        LOG.queriesExecuted(queryExecutionCount);
-        LOG.queryCachePuts(queryCachePutCount);
-        LOG.queryCacheHits(queryCacheHitCount);
-        LOG.queryCacheMisses(queryCacheMissCount);
-        LOG.maxQueryTime(queryExecutionMaxTime);
-	}
-
-	/**
-	 * Are statistics logged
-	 */
-	public boolean isStatisticsEnabled() {
-		return isStatisticsEnabled;
-	}
-
-	/**
-	 * Enable statistics logs (this is a dynamic parameter)
-	 */
-	public void setStatisticsEnabled(boolean b) {
-		isStatisticsEnabled = b;
-	}
-
-	/**
-	 * @return Returns the max query execution time,
-	 * for all queries
-	 */
-	public long getQueryExecutionMaxTime() {
-		return queryExecutionMaxTime;
-	}
-
-	/**
-	 * Get all executed query strings
-	 */
-	public String[] getQueries() {
-		return ArrayHelper.toStringArray( queryStatistics.keySet() );
-	}
-
-	/**
-	 * Get the names of all entities
-	 */
-	public String[] getEntityNames() {
-		if (sessionFactory==null) {
-			return ArrayHelper.toStringArray( entityStatistics.keySet() );
-		}
-		else {
-			return ArrayHelper.toStringArray( sessionFactory.getAllClassMetadata().keySet() );
-		}
-	}
-
-	/**
-	 * Get the names of all collection roles
-	 */
-	public String[] getCollectionRoleNames() {
-		if (sessionFactory==null) {
-			return ArrayHelper.toStringArray( collectionStatistics.keySet() );
-		}
-		else {
-			return ArrayHelper.toStringArray( sessionFactory.getAllCollectionMetadata().keySet() );
-		}
-	}
-
-	/**
-	 * Get all second-level cache region names
-	 */
-	public String[] getSecondLevelCacheRegionNames() {
-		if (sessionFactory==null) {
-			return ArrayHelper.toStringArray( secondLevelCacheStatistics.keySet() );
-		}
-		else {
-			return ArrayHelper.toStringArray( sessionFactory.getAllSecondLevelCacheRegions().keySet() );
-		}
-	}
-
-	public void endTransaction(boolean success) {
-		transactionCount++;
-		if (success) commitedTransactionCount++;
-	}
-
-	public long getSuccessfulTransactionCount() {
-		return commitedTransactionCount;
-	}
-
-	public long getTransactionCount() {
-		return transactionCount;
-	}
-
-	public void closeStatement() {
-		closeStatementCount++;
-	}
-
-	public void prepareStatement() {
-		prepareStatementCount++;
-	}
-
-	public long getCloseStatementCount() {
-		return closeStatementCount;
-	}
-
-	public long getPrepareStatementCount() {
-		return prepareStatementCount;
-	}
-
-	public void optimisticFailure(String entityName) {
-		optimisticFailureCount++;
-		((EntityStatisticsImpl) getEntityStatistics(entityName)).optimisticFailureCount++;
-	}
-
-	public long getOptimisticFailureCount() {
-		return optimisticFailureCount;
-	}
-	@Override
-    public String toString() {
-		return new StringBuffer()
-			.append("Statistics[")
-			.append("start time=").append(startTime)
-			.append(",sessions opened=").append(sessionOpenCount)
-			.append(",sessions closed=").append(sessionCloseCount)
-			.append(",transactions=").append(transactionCount)
-			.append(",successful transactions=").append(commitedTransactionCount)
-			.append(",optimistic lock failures=").append(optimisticFailureCount)
-			.append(",flushes=").append(flushCount)
-			.append(",connections obtained=").append(connectCount)
-			.append(",statements prepared=").append(prepareStatementCount)
-			.append(",statements closed=").append(closeStatementCount)
-			.append(",second level cache puts=").append(secondLevelCachePutCount)
-			.append(",second level cache hits=").append(secondLevelCacheHitCount)
-			.append(",second level cache misses=").append(secondLevelCacheMissCount)
-			.append(",entities loaded=").append(entityLoadCount)
-			.append(",entities updated=").append(entityUpdateCount)
-			.append(",entities inserted=").append(entityInsertCount)
-			.append(",entities deleted=").append(entityDeleteCount)
-			.append(",entities fetched=").append(entityFetchCount)
-			.append(",collections loaded=").append(collectionLoadCount)
-			.append(",collections updated=").append(collectionUpdateCount)
-			.append(",collections removed=").append(collectionRemoveCount)
-			.append(",collections recreated=").append(collectionRecreateCount)
-			.append(",collections fetched=").append(collectionFetchCount)
-			.append(",queries executed to database=").append(queryExecutionCount)
-			.append(",query cache puts=").append(queryCachePutCount)
-			.append(",query cache hits=").append(queryCacheHitCount)
-			.append(",query cache misses=").append(queryCacheMissCount)
-			.append(",max query time=").append(queryExecutionMaxTime)
-			.append(']')
-			.toString();
-	}
-
-	public String getQueryExecutionMaxTimeQueryString() {
-		return queryExecutionMaxTimeQueryString;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/CategorizedStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/CategorizedStatistics.java
similarity index 81%
rename from hibernate-core/src/main/java/org/hibernate/stat/CategorizedStatistics.java
rename to hibernate-core/src/main/java/org/hibernate/stat/internal/CategorizedStatistics.java
index 5084ce748a..bc427a24c3 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/CategorizedStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/CategorizedStatistics.java
@@ -1,45 +1,44 @@
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
-package org.hibernate.stat;
+package org.hibernate.stat.internal;
+
 import java.io.Serializable;
 
 /**
- * Statistics for a particular "category" (a named entity,
- * collection role, second level cache region or query).
+ * Statistics for a particular "category" (a named entity, collection role, second level cache region or query).
  * 
  * @author Gavin King
  */
 public class CategorizedStatistics implements Serializable {
 	
 	private final String categoryName;
 
 	CategorizedStatistics(String categoryName) {
 		this.categoryName = categoryName;
 	}
 	
 	public String getCategoryName() {
 		return categoryName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentCollectionStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentCollectionStatisticsImpl.java
similarity index 58%
rename from hibernate-core/src/main/java/org/hibernate/stat/ConcurrentCollectionStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentCollectionStatisticsImpl.java
index 04c96dc6ae..fd8fa735ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentCollectionStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentCollectionStatisticsImpl.java
@@ -1,72 +1,97 @@
-package org.hibernate.stat;
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.stat.internal;
+
 import java.util.concurrent.atomic.AtomicLong;
 
+import org.hibernate.stat.CollectionStatistics;
+
 /**
  * Collection related statistics
  *
  * @author Alex Snaps
  */
 public class ConcurrentCollectionStatisticsImpl extends CategorizedStatistics implements CollectionStatistics {
-
 	ConcurrentCollectionStatisticsImpl(String role) {
 		super(role);
 	}
 
 	private	AtomicLong loadCount	 = new AtomicLong();
 	private	AtomicLong fetchCount	 = new AtomicLong();
 	private	AtomicLong updateCount	 = new AtomicLong();
 	private	AtomicLong removeCount	 = new AtomicLong();
 	private	AtomicLong recreateCount = new AtomicLong();
 
 	public long getLoadCount() {
 		return loadCount.get();
 	}
 
 	public long getFetchCount() {
 		return fetchCount.get();
 	}
 
 	public long getRecreateCount() {
 		return recreateCount.get();
 	}
 
 	public long getRemoveCount() {
 		return removeCount.get();
 	}
 
 	public long getUpdateCount() {
 		return updateCount.get();
 	}
 
 	public String toString() {
 		return new StringBuilder()
 				.append("CollectionStatistics")
 				.append("[loadCount=").append(this.loadCount)
 				.append(",fetchCount=").append(this.fetchCount)
 				.append(",recreateCount=").append(this.recreateCount)
 				.append(",removeCount=").append(this.removeCount)
 				.append(",updateCount=").append(this.updateCount)
 				.append(']')
 				.toString();
 	}
 
 	void incrementLoadCount() {
 		loadCount.getAndIncrement();
 	}
 
 	void incrementFetchCount() {
 		fetchCount.getAndIncrement();
 	}
 
 	void incrementUpdateCount() {
 		updateCount.getAndIncrement();
 	}
 
 	void incrementRecreateCount() {
 		recreateCount.getAndIncrement();
 	}
 
 	void incrementRemoveCount() {
 		removeCount.getAndIncrement();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentEntityStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentEntityStatisticsImpl.java
similarity index 63%
rename from hibernate-core/src/main/java/org/hibernate/stat/ConcurrentEntityStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentEntityStatisticsImpl.java
index 810a9ca720..53a4cb0cac 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentEntityStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentEntityStatisticsImpl.java
@@ -1,83 +1,108 @@
-package org.hibernate.stat;
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
+package org.hibernate.stat.internal;
+
 import java.util.concurrent.atomic.AtomicLong;
 
+import org.hibernate.stat.EntityStatistics;
 
 /**
  * Entity related statistics
  *
  * @author Alex Snaps
  */
 public class ConcurrentEntityStatisticsImpl extends CategorizedStatistics implements EntityStatistics {
 
 	ConcurrentEntityStatisticsImpl(String name) {
 		super(name);
 	}
 
 	private	AtomicLong loadCount			  =	new	AtomicLong();
 	private	AtomicLong updateCount			  =	new	AtomicLong();
 	private	AtomicLong insertCount			  =	new	AtomicLong();
 	private	AtomicLong deleteCount			  =	new	AtomicLong();
 	private	AtomicLong fetchCount			  =	new	AtomicLong();
 	private	AtomicLong optimisticFailureCount =	new	AtomicLong();
 
 	public long getDeleteCount() {
 		return deleteCount.get();
 	}
 
 	public long getInsertCount() {
 		return insertCount.get();
 	}
 
 	public long getLoadCount() {
 		return loadCount.get();
 	}
 
 	public long getUpdateCount() {
 		return updateCount.get();
 	}
 
 	public long getFetchCount() {
 		return fetchCount.get();
 	}
 
 	public long getOptimisticFailureCount() {
 		return optimisticFailureCount.get();
 	}
 
 	public String toString() {
 		return new StringBuilder()
 				.append("EntityStatistics")
 				.append("[loadCount=").append(this.loadCount)
 				.append(",updateCount=").append(this.updateCount)
 				.append(",insertCount=").append(this.insertCount)
 				.append(",deleteCount=").append(this.deleteCount)
 				.append(",fetchCount=").append(this.fetchCount)
 				.append(",optimisticLockFailureCount=").append(this.optimisticFailureCount)
 				.append(']')
 				.toString();
 	}
 
 	void incrementLoadCount() {
 		loadCount.getAndIncrement();
 	}
 
 	void incrementFetchCount() {
 		fetchCount.getAndIncrement();
 	}
 
 	void incrementUpdateCount() {
 		updateCount.getAndIncrement();
 	}
 
 	void incrementInsertCount() {
 		insertCount.getAndIncrement();
 	}
 
 	void incrementDeleteCount() {
 		deleteCount.getAndIncrement();
 	}
 
 	void incrementOptimisticFailureCount() {
 		optimisticFailureCount.getAndIncrement();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentQueryStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentQueryStatisticsImpl.java
similarity index 80%
rename from hibernate-core/src/main/java/org/hibernate/stat/ConcurrentQueryStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentQueryStatisticsImpl.java
index 0571c6ec22..1e43188724 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentQueryStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentQueryStatisticsImpl.java
@@ -1,154 +1,179 @@
-package org.hibernate.stat;
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
+package org.hibernate.stat.internal;
+
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.concurrent.locks.Lock;
 import java.util.concurrent.locks.ReadWriteLock;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 
+import org.hibernate.stat.QueryStatistics;
+
 /**
  * Query statistics (HQL and SQL)
  * <p/>
  * Note that for a cached query, the cache miss is equals to the db count
  *
  * @author Alex Snaps
  */
 public class ConcurrentQueryStatisticsImpl extends CategorizedStatistics implements QueryStatistics {
-
 	private final AtomicLong cacheHitCount = new AtomicLong();
 	private final AtomicLong cacheMissCount = new AtomicLong();
 	private final AtomicLong cachePutCount = new AtomicLong();
 	private final AtomicLong executionCount = new AtomicLong();
 	private final AtomicLong executionRowCount = new AtomicLong();
 	private final AtomicLong executionMaxTime = new AtomicLong();
 	private final AtomicLong executionMinTime = new AtomicLong(Long.MAX_VALUE);
 	private final AtomicLong totalExecutionTime = new AtomicLong();
 
 	private final Lock readLock;
 	private final Lock writeLock;
 
 	{
 		ReadWriteLock lock = new ReentrantReadWriteLock();
 		readLock = lock.readLock();
 		writeLock = lock.writeLock();
 	}
 
 	ConcurrentQueryStatisticsImpl(String query) {
 		super(query);
 	}
 
 	/**
 	 * queries executed to the DB
 	 */
 	public long getExecutionCount() {
 		return executionCount.get();
 	}
 
 	/**
 	 * Queries retrieved successfully from the cache
 	 */
 	public long getCacheHitCount() {
 		return cacheHitCount.get();
 	}
 
 	public long getCachePutCount() {
 		return cachePutCount.get();
 	}
 
 	public long getCacheMissCount() {
 		return cacheMissCount.get();
 	}
 
 	/**
 	 * Number of lines returned by all the executions of this query (from DB)
 	 * For now, {@link org.hibernate.Query#iterate()}
 	 * and {@link org.hibernate.Query#scroll()()} do not fill this statistic
 	 *
 	 * @return The number of rows cumulatively returned by the given query; iterate
 	 *         and scroll queries do not effect this total as their number of returned rows
 	 *         is not known at execution time.
 	 */
 	public long getExecutionRowCount() {
 		return executionRowCount.get();
 	}
 
 	/**
 	 * average time in ms taken by the excution of this query onto the DB
 	 */
 	public long getExecutionAvgTime() {
 		// We write lock here to be sure that we always calculate the average time
 		// with all updates from the executed applied: executionCount and totalExecutionTime
 		// both used in the calculation
 		writeLock.lock();
 		try {
 			long avgExecutionTime = 0;
 			if (executionCount.get() > 0) {
 				avgExecutionTime = totalExecutionTime.get() / executionCount.get();
 			}
 			return avgExecutionTime;
 		} finally {
 			writeLock.unlock();
 		}
 	}
 
 	/**
 	 * max time in ms taken by the excution of this query onto the DB
 	 */
 	public long getExecutionMaxTime() {
 		return executionMaxTime.get();
 	}
 
 	/**
 	 * min time in ms taken by the excution of this query onto the DB
 	 */
 	public long getExecutionMinTime() {
 		return executionMinTime.get();
 	}
 
 	/**
 	 * add statistics report of a DB query
 	 *
 	 * @param rows rows count returned
 	 * @param time time taken
 	 */
 	void executed(long rows, long time) {
 		// read lock is enough, concurrent updates are supported by the underlying type AtomicLong
 		// this only guards executed(long, long) to be called, when another thread is executing getExecutionAvgTime()
 		readLock.lock();
 		try {
 			// Less chances for a context switch
 			for (long old = executionMinTime.get(); (time < old) && !executionMinTime.compareAndSet(old, time); old = executionMinTime.get());
 			for (long old = executionMaxTime.get(); (time > old) && !executionMaxTime.compareAndSet(old, time); old = executionMaxTime.get());
 			executionCount.getAndIncrement();
 			executionRowCount.addAndGet(rows);
 			totalExecutionTime.addAndGet(time);
 		} finally {
 			readLock.unlock();
 		}
 	}
 
 	public String toString() {
 		return new StringBuilder()
 				.append("QueryStatistics")
 				.append("[cacheHitCount=").append(this.cacheHitCount)
 				.append(",cacheMissCount=").append(this.cacheMissCount)
 				.append(",cachePutCount=").append(this.cachePutCount)
 				.append(",executionCount=").append(this.executionCount)
 				.append(",executionRowCount=").append(this.executionRowCount)
 				.append(",executionAvgTime=").append(this.getExecutionAvgTime())
 				.append(",executionMaxTime=").append(this.executionMaxTime)
 				.append(",executionMinTime=").append(this.executionMinTime)
 				.append(']')
 				.toString();
 	}
 
 	void incrementCacheHitCount() {
 		cacheHitCount.getAndIncrement();
 	}
 
 	void incrementCacheMissCount() {
 		cacheMissCount.getAndIncrement();
 	}
 
 	void incrementCachePutCount() {
 		cachePutCount.getAndIncrement();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentSecondLevelCacheStatisticsImpl.java
similarity index 66%
rename from hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentSecondLevelCacheStatisticsImpl.java
index 316f3e10d5..191c80c70a 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/SecondLevelCacheStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentSecondLevelCacheStatisticsImpl.java
@@ -1,98 +1,112 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.stat;
+package org.hibernate.stat.internal;
+
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
+import java.util.concurrent.atomic.AtomicLong;
+
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.Region;
+import org.hibernate.stat.SecondLevelCacheStatistics;
 
 /**
  * Second level cache statistics of a specific region
  *
- * @author Gavin King
+ * @author Alex Snaps
  */
-public class SecondLevelCacheStatisticsImpl extends CategorizedStatistics implements SecondLevelCacheStatistics {
+public class ConcurrentSecondLevelCacheStatisticsImpl extends CategorizedStatistics implements SecondLevelCacheStatistics {
+	private final transient Region region;
+	private AtomicLong hitCount = new AtomicLong();
+	private AtomicLong missCount = new AtomicLong();
+	private AtomicLong putCount = new AtomicLong();
 
-	private transient Region region;
-	long hitCount;
-	long missCount;
-	long putCount;
-
-	SecondLevelCacheStatisticsImpl(Region region) {
-		super(region.getName());
+	ConcurrentSecondLevelCacheStatisticsImpl(Region region) {
+		super( region.getName() );
 		this.region = region;
 	}
 
 	public long getHitCount() {
-		return hitCount;
+		return hitCount.get();
 	}
 
 	public long getMissCount() {
-		return missCount;
+		return missCount.get();
 	}
 
 	public long getPutCount() {
-		return putCount;
+		return putCount.get();
 	}
 
 	public long getElementCountInMemory() {
 		return region.getElementCountInMemory();
 	}
 
 	public long getElementCountOnDisk() {
 		return region.getElementCountOnDisk();
 	}
 
 	public long getSizeInMemory() {
 		return region.getSizeInMemory();
 	}
 
 	public Map getEntries() {
 		Map map = new HashMap();
 		Iterator iter = region.toMap().entrySet().iterator();
 		while (iter.hasNext()) {
 			Map.Entry me = (Map.Entry) iter.next();
 			map.put(((CacheKey) me.getKey()).getKey(), me.getValue());
 		}
 		return map;
 	}
 
 	public String toString() {
-		StringBuilder builder = new StringBuilder()
+		StringBuilder buf = new StringBuilder()
 				.append("SecondLevelCacheStatistics")
 				.append("[hitCount=").append(this.hitCount)
 				.append(",missCount=").append(this.missCount)
 				.append(",putCount=").append(this.putCount);
 		//not sure if this would ever be null but wanted to be careful
 		if (region != null) {
-			builder.append(",elementCountInMemory=").append(this.getElementCountInMemory())
+			buf.append(",elementCountInMemory=").append(this.getElementCountInMemory())
 					.append(",elementCountOnDisk=").append(this.getElementCountOnDisk())
 					.append(",sizeInMemory=").append(this.getSizeInMemory());
 		}
-		builder.append(']');
-		return builder.toString();
+		buf.append(']');
+		return buf.toString();
+	}
+
+	void incrementHitCount() {
+		hitCount.getAndIncrement();
+	}
+
+	void incrementMissCount() {
+		missCount.getAndIncrement();
+	}
+
+	void incrementPutCount() {
+		putCount.getAndIncrement();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
index 4d42f74b71..f951841469 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
@@ -1,714 +1,720 @@
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
-package org.hibernate.stat;
+package org.hibernate.stat.internal;
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import java.util.concurrent.atomic.AtomicLong;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateLogger;
 import org.hibernate.cache.Region;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
-import org.jboss.logging.Logger;
+import org.hibernate.service.spi.Service;
+import org.hibernate.stat.CollectionStatistics;
+import org.hibernate.stat.EntityStatistics;
+import org.hibernate.stat.QueryStatistics;
+import org.hibernate.stat.SecondLevelCacheStatistics;
+import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
- * Implementation of {@link Statistics}, as well as {@link StatisticsImplementor}, based on the
- * {@link java.util.concurrent} package introduced in Java 5.
+ * Implementation of {@link org.hibernate.stat.Statistics} based on the {@link java.util.concurrent} package.
  *
  * @author Alex Snaps
  */
 @SuppressWarnings({ "unchecked" })
-public class ConcurrentStatisticsImpl implements Statistics, StatisticsImplementor {
+public class ConcurrentStatisticsImpl implements StatisticsImplementor, Service {
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       ConcurrentStatisticsImpl.class.getName());
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ConcurrentStatisticsImpl.class.getName());
 
 	private SessionFactoryImplementor sessionFactory;
 
 	private volatile boolean isStatisticsEnabled;
 	private volatile long startTime;
 	private AtomicLong sessionOpenCount = new AtomicLong();
 	private AtomicLong sessionCloseCount = new AtomicLong();
 	private AtomicLong flushCount = new AtomicLong();
 	private AtomicLong connectCount = new AtomicLong();
 
 	private AtomicLong prepareStatementCount = new AtomicLong();
 	private AtomicLong closeStatementCount = new AtomicLong();
 
 	private AtomicLong entityLoadCount = new AtomicLong();
 	private AtomicLong entityUpdateCount = new AtomicLong();
 	private AtomicLong entityInsertCount = new AtomicLong();
 	private AtomicLong entityDeleteCount = new AtomicLong();
 	private AtomicLong entityFetchCount = new AtomicLong();
 	private AtomicLong collectionLoadCount = new AtomicLong();
 	private AtomicLong collectionUpdateCount = new AtomicLong();
 	private AtomicLong collectionRemoveCount = new AtomicLong();
 	private AtomicLong collectionRecreateCount = new AtomicLong();
 	private AtomicLong collectionFetchCount = new AtomicLong();
 
 	private AtomicLong secondLevelCacheHitCount = new AtomicLong();
 	private AtomicLong secondLevelCacheMissCount = new AtomicLong();
 	private AtomicLong secondLevelCachePutCount = new AtomicLong();
 
 	private AtomicLong queryExecutionCount = new AtomicLong();
 	private AtomicLong queryExecutionMaxTime = new AtomicLong();
 	private volatile String queryExecutionMaxTimeQueryString;
 	private AtomicLong queryCacheHitCount = new AtomicLong();
 	private AtomicLong queryCacheMissCount = new AtomicLong();
 	private AtomicLong queryCachePutCount = new AtomicLong();
 
 	private AtomicLong committedTransactionCount = new AtomicLong();
 	private AtomicLong transactionCount = new AtomicLong();
 
 	private AtomicLong optimisticFailureCount = new AtomicLong();
 
 	/**
 	 * second level cache statistics per region
 	 */
 	private final ConcurrentMap secondLevelCacheStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per name
 	 */
 	private final ConcurrentMap entityStatistics = new ConcurrentHashMap();
 	/**
 	 * collection statistics per name
 	 */
 	private final ConcurrentMap collectionStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per query string (HQL or SQL)
 	 */
 	private final ConcurrentMap queryStatistics = new ConcurrentHashMap();
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public ConcurrentStatisticsImpl() {
 		clear();
 	}
 
 	public ConcurrentStatisticsImpl(SessionFactoryImplementor sessionFactory) {
 		clear();
 		this.sessionFactory = sessionFactory;
 	}
 
 	/**
 	 * reset all statistics
 	 */
 	public void clear() {
 		secondLevelCacheHitCount.set( 0 );
 		secondLevelCacheMissCount.set( 0 );
 		secondLevelCachePutCount.set( 0 );
 
 		sessionCloseCount.set( 0 );
 		sessionOpenCount.set( 0 );
 		flushCount.set( 0 );
 		connectCount.set( 0 );
 
 		prepareStatementCount.set( 0 );
 		closeStatementCount.set( 0 );
 
 		entityDeleteCount.set( 0 );
 		entityInsertCount.set( 0 );
 		entityUpdateCount.set( 0 );
 		entityLoadCount.set( 0 );
 		entityFetchCount.set( 0 );
 
 		collectionRemoveCount.set( 0 );
 		collectionUpdateCount.set( 0 );
 		collectionRecreateCount.set( 0 );
 		collectionLoadCount.set( 0 );
 		collectionFetchCount.set( 0 );
 
 		queryExecutionCount.set( 0 );
 		queryCacheHitCount.set( 0 );
 		queryExecutionMaxTime.set( 0 );
 		queryExecutionMaxTimeQueryString = null;
 		queryCacheMissCount.set( 0 );
 		queryCachePutCount.set( 0 );
 
 		transactionCount.set( 0 );
 		committedTransactionCount.set( 0 );
 
 		optimisticFailureCount.set( 0 );
 
 		secondLevelCacheStatistics.clear();
 		entityStatistics.clear();
 		collectionStatistics.clear();
 		queryStatistics.clear();
 
 		startTime = System.currentTimeMillis();
 	}
 
 	public void openSession() {
 		sessionOpenCount.getAndIncrement();
 	}
 
 	public void closeSession() {
 		sessionCloseCount.getAndIncrement();
 	}
 
 	public void flush() {
 		flushCount.getAndIncrement();
 	}
 
 	public void connect() {
 		connectCount.getAndIncrement();
 	}
 
 	public void loadEntity(String entityName) {
 		entityLoadCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementLoadCount();
 	}
 
 	public void fetchEntity(String entityName) {
 		entityFetchCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementFetchCount();
 	}
 
 	/**
 	 * find entity statistics per name
 	 *
 	 * @param entityName entity name
 	 *
 	 * @return EntityStatistics object
 	 */
 	public EntityStatistics getEntityStatistics(String entityName) {
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) entityStatistics.get( entityName );
 		if ( es == null ) {
 			es = new ConcurrentEntityStatisticsImpl( entityName );
 			ConcurrentEntityStatisticsImpl previous;
 			if ( ( previous = (ConcurrentEntityStatisticsImpl) entityStatistics.putIfAbsent(
 					entityName, es
 			) ) != null ) {
 				es = previous;
 			}
 		}
 		return es;
 	}
 
 	public void updateEntity(String entityName) {
 		entityUpdateCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementUpdateCount();
 	}
 
 	public void insertEntity(String entityName) {
 		entityInsertCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementInsertCount();
 	}
 
 	public void deleteEntity(String entityName) {
 		entityDeleteCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementDeleteCount();
 	}
 
 	/**
 	 * Get collection statistics per role
 	 *
 	 * @param role collection role
 	 *
 	 * @return CollectionStatistics
 	 */
 	public CollectionStatistics getCollectionStatistics(String role) {
 		ConcurrentCollectionStatisticsImpl cs = (ConcurrentCollectionStatisticsImpl) collectionStatistics.get( role );
 		if ( cs == null ) {
 			cs = new ConcurrentCollectionStatisticsImpl( role );
 			ConcurrentCollectionStatisticsImpl previous;
 			if ( ( previous = (ConcurrentCollectionStatisticsImpl) collectionStatistics.putIfAbsent(
 					role, cs
 			) ) != null ) {
 				cs = previous;
 			}
 		}
 		return cs;
 	}
 
 	public void loadCollection(String role) {
 		collectionLoadCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementLoadCount();
 	}
 
 	public void fetchCollection(String role) {
 		collectionFetchCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementFetchCount();
 	}
 
 	public void updateCollection(String role) {
 		collectionUpdateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementUpdateCount();
 	}
 
 	public void recreateCollection(String role) {
 		collectionRecreateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRecreateCount();
 	}
 
 	public void removeCollection(String role) {
 		collectionRemoveCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRemoveCount();
 	}
 
 	/**
 	 * Second level cache statistics per region
 	 *
 	 * @param regionName region name
 	 *
 	 * @return SecondLevelCacheStatistics
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
 		ConcurrentSecondLevelCacheStatisticsImpl slcs
 				= (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.get( regionName );
 		if ( slcs == null ) {
 			if ( sessionFactory == null ) {
 				return null;
 			}
 			Region region = sessionFactory.getSecondLevelCacheRegion( regionName );
 			if ( region == null ) {
 				return null;
 			}
 			slcs = new ConcurrentSecondLevelCacheStatisticsImpl( region );
 			ConcurrentSecondLevelCacheStatisticsImpl previous;
 			if ( ( previous = (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.putIfAbsent(
 					regionName, slcs
 			) ) != null ) {
 				slcs = previous;
 			}
 		}
 		return slcs;
 	}
 
 	public void secondLevelCachePut(String regionName) {
 		secondLevelCachePutCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementPutCount();
 	}
 
 	public void secondLevelCacheHit(String regionName) {
 		secondLevelCacheHitCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementHitCount();
 	}
 
 	public void secondLevelCacheMiss(String regionName) {
 		secondLevelCacheMissCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementMissCount();
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public void queryExecuted(String hql, int rows, long time) {
         LOG.hql(hql, Long.valueOf(time), Long.valueOf(rows));
 		queryExecutionCount.getAndIncrement();
 		boolean isLongestQuery = false;
 		for ( long old = queryExecutionMaxTime.get();
 			  ( isLongestQuery = time > old ) && ( !queryExecutionMaxTime.compareAndSet( old, time ) );
 			  old = queryExecutionMaxTime.get() ) {
 			// nothing to do here given the odd loop structure...
 		}
 		if ( isLongestQuery ) {
 			queryExecutionMaxTimeQueryString = hql;
 		}
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.executed( rows, time );
 		}
 	}
 
 	public void queryCacheHit(String hql, String regionName) {
 		queryCacheHitCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheHitCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementHitCount();
 	}
 
 	public void queryCacheMiss(String hql, String regionName) {
 		queryCacheMissCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheMissCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementMissCount();
 	}
 
 	public void queryCachePut(String hql, String regionName) {
 		queryCachePutCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCachePutCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementPutCount();
 	}
 
 	/**
 	 * Query statistics from query string (HQL or SQL)
 	 *
 	 * @param queryString query string
 	 *
 	 * @return QueryStatistics
 	 */
 	public QueryStatistics getQueryStatistics(String queryString) {
 		ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) queryStatistics.get( queryString );
 		if ( qs == null ) {
 			qs = new ConcurrentQueryStatisticsImpl( queryString );
 			ConcurrentQueryStatisticsImpl previous;
 			if ( ( previous = (ConcurrentQueryStatisticsImpl) queryStatistics.putIfAbsent(
 					queryString, qs
 			) ) != null ) {
 				qs = previous;
 			}
 		}
 		return qs;
 	}
 
 	/**
 	 * @return entity deletion count
 	 */
 	public long getEntityDeleteCount() {
 		return entityDeleteCount.get();
 	}
 
 	/**
 	 * @return entity insertion count
 	 */
 	public long getEntityInsertCount() {
 		return entityInsertCount.get();
 	}
 
 	/**
 	 * @return entity load (from DB)
 	 */
 	public long getEntityLoadCount() {
 		return entityLoadCount.get();
 	}
 
 	/**
 	 * @return entity fetch (from DB)
 	 */
 	public long getEntityFetchCount() {
 		return entityFetchCount.get();
 	}
 
 	/**
 	 * @return entity update
 	 */
 	public long getEntityUpdateCount() {
 		return entityUpdateCount.get();
 	}
 
 	public long getQueryExecutionCount() {
 		return queryExecutionCount.get();
 	}
 
 	public long getQueryCacheHitCount() {
 		return queryCacheHitCount.get();
 	}
 
 	public long getQueryCacheMissCount() {
 		return queryCacheMissCount.get();
 	}
 
 	public long getQueryCachePutCount() {
 		return queryCachePutCount.get();
 	}
 
 	/**
 	 * @return flush
 	 */
 	public long getFlushCount() {
 		return flushCount.get();
 	}
 
 	/**
 	 * @return session connect
 	 */
 	public long getConnectCount() {
 		return connectCount.get();
 	}
 
 	/**
 	 * @return second level cache hit
 	 */
 	public long getSecondLevelCacheHitCount() {
 		return secondLevelCacheHitCount.get();
 	}
 
 	/**
 	 * @return second level cache miss
 	 */
 	public long getSecondLevelCacheMissCount() {
 		return secondLevelCacheMissCount.get();
 	}
 
 	/**
 	 * @return second level cache put
 	 */
 	public long getSecondLevelCachePutCount() {
 		return secondLevelCachePutCount.get();
 	}
 
 	/**
 	 * @return session closing
 	 */
 	public long getSessionCloseCount() {
 		return sessionCloseCount.get();
 	}
 
 	/**
 	 * @return session opening
 	 */
 	public long getSessionOpenCount() {
 		return sessionOpenCount.get();
 	}
 
 	/**
 	 * @return collection loading (from DB)
 	 */
 	public long getCollectionLoadCount() {
 		return collectionLoadCount.get();
 	}
 
 	/**
 	 * @return collection fetching (from DB)
 	 */
 	public long getCollectionFetchCount() {
 		return collectionFetchCount.get();
 	}
 
 	/**
 	 * @return collection update
 	 */
 	public long getCollectionUpdateCount() {
 		return collectionUpdateCount.get();
 	}
 
 	/**
 	 * @return collection removal
 	 *         FIXME: even if isInverse="true"?
 	 */
 	public long getCollectionRemoveCount() {
 		return collectionRemoveCount.get();
 	}
 
 	/**
 	 * @return collection recreation
 	 */
 	public long getCollectionRecreateCount() {
 		return collectionRecreateCount.get();
 	}
 
 	/**
 	 * @return start time in ms (JVM standards {@link System#currentTimeMillis()})
 	 */
 	public long getStartTime() {
 		return startTime;
 	}
 
 	/**
 	 * log in info level the main statistics
 	 */
 	public void logSummary() {
         LOG.loggingStatistics();
         LOG.startTime(startTime);
         LOG.sessionsOpened(sessionOpenCount.get());
         LOG.sessionsClosed(sessionCloseCount.get());
         LOG.transactions(transactionCount.get());
         LOG.successfulTransactions(committedTransactionCount.get());
         LOG.optimisticLockFailures(optimisticFailureCount.get());
         LOG.flushes(flushCount.get());
         LOG.connectionsObtained(connectCount.get());
         LOG.statementsPrepared(prepareStatementCount.get());
         LOG.statementsClosed(closeStatementCount.get());
         LOG.secondLevelCachePuts(secondLevelCachePutCount.get());
         LOG.secondLevelCacheHits(secondLevelCacheHitCount.get());
         LOG.secondLevelCacheMisses(secondLevelCacheMissCount.get());
         LOG.entitiesLoaded(entityLoadCount.get());
         LOG.entitiesUpdated(entityUpdateCount.get());
         LOG.entitiesInserted(entityInsertCount.get());
         LOG.entitiesDeleted(entityDeleteCount.get());
         LOG.entitiesFetched(entityFetchCount.get());
         LOG.collectionsLoaded(collectionLoadCount.get());
         LOG.collectionsUpdated(collectionUpdateCount.get());
         LOG.collectionsRemoved(collectionRemoveCount.get());
         LOG.collectionsRecreated(collectionRecreateCount.get());
         LOG.collectionsFetched(collectionFetchCount.get());
         LOG.queriesExecuted(queryExecutionCount.get());
         LOG.queryCachePuts(queryCachePutCount.get());
         LOG.queryCacheHits(queryCacheHitCount.get());
         LOG.queryCacheMisses(queryCacheMissCount.get());
         LOG.maxQueryTime(queryExecutionMaxTime.get());
 	}
 
 	/**
 	 * Are statistics logged
 	 */
 	public boolean isStatisticsEnabled() {
 		return isStatisticsEnabled;
 	}
 
 	/**
 	 * Enable statistics logs (this is a dynamic parameter)
 	 */
 	public void setStatisticsEnabled(boolean b) {
 		isStatisticsEnabled = b;
 	}
 
 	/**
 	 * @return Returns the max query execution time,
 	 *         for all queries
 	 */
 	public long getQueryExecutionMaxTime() {
 		return queryExecutionMaxTime.get();
 	}
 
 	/**
 	 * Get all executed query strings
 	 */
 	public String[] getQueries() {
 		return ArrayHelper.toStringArray( queryStatistics.keySet() );
 	}
 
 	/**
 	 * Get the names of all entities
 	 */
 	public String[] getEntityNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( entityStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllClassMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get the names of all collection roles
 	 */
 	public String[] getCollectionRoleNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( collectionStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllCollectionMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get all second-level cache region names
 	 */
 	public String[] getSecondLevelCacheRegionNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( secondLevelCacheStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllSecondLevelCacheRegions().keySet() );
 		}
 	}
 
 	public void endTransaction(boolean success) {
 		transactionCount.getAndIncrement();
 		if ( success ) {
 			committedTransactionCount.getAndIncrement();
 		}
 	}
 
 	public long getSuccessfulTransactionCount() {
 		return committedTransactionCount.get();
 	}
 
 	public long getTransactionCount() {
 		return transactionCount.get();
 	}
 
 	public void closeStatement() {
 		closeStatementCount.getAndIncrement();
 	}
 
 	public void prepareStatement() {
 		prepareStatementCount.getAndIncrement();
 	}
 
 	public long getCloseStatementCount() {
 		return closeStatementCount.get();
 	}
 
 	public long getPrepareStatementCount() {
 		return prepareStatementCount.get();
 	}
 
 	public void optimisticFailure(String entityName) {
 		optimisticFailureCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementOptimisticFailureCount();
 	}
 
 	public long getOptimisticFailureCount() {
 		return optimisticFailureCount.get();
 	}
 
 	@Override
     public String toString() {
 		return new StringBuilder()
 				.append( "Statistics[" )
 				.append( "start time=" ).append( startTime )
 				.append( ",sessions opened=" ).append( sessionOpenCount )
 				.append( ",sessions closed=" ).append( sessionCloseCount )
 				.append( ",transactions=" ).append( transactionCount )
 				.append( ",successful transactions=" ).append( committedTransactionCount )
 				.append( ",optimistic lock failures=" ).append( optimisticFailureCount )
 				.append( ",flushes=" ).append( flushCount )
 				.append( ",connections obtained=" ).append( connectCount )
 				.append( ",statements prepared=" ).append( prepareStatementCount )
 				.append( ",statements closed=" ).append( closeStatementCount )
 				.append( ",second level cache puts=" ).append( secondLevelCachePutCount )
 				.append( ",second level cache hits=" ).append( secondLevelCacheHitCount )
 				.append( ",second level cache misses=" ).append( secondLevelCacheMissCount )
 				.append( ",entities loaded=" ).append( entityLoadCount )
 				.append( ",entities updated=" ).append( entityUpdateCount )
 				.append( ",entities inserted=" ).append( entityInsertCount )
 				.append( ",entities deleted=" ).append( entityDeleteCount )
 				.append( ",entities fetched=" ).append( entityFetchCount )
 				.append( ",collections loaded=" ).append( collectionLoadCount )
 				.append( ",collections updated=" ).append( collectionUpdateCount )
 				.append( ",collections removed=" ).append( collectionRemoveCount )
 				.append( ",collections recreated=" ).append( collectionRecreateCount )
 				.append( ",collections fetched=" ).append( collectionFetchCount )
 				.append( ",queries executed to database=" ).append( queryExecutionCount )
 				.append( ",query cache puts=" ).append( queryCachePutCount )
 				.append( ",query cache hits=" ).append( queryCacheHitCount )
 				.append( ",query cache misses=" ).append( queryCacheMissCount )
 				.append( ",max query time=" ).append( queryExecutionMaxTime )
 				.append( ']' )
 				.toString();
 	}
 
 	public String getQueryExecutionMaxTimeQueryString() {
 		return queryExecutionMaxTimeQueryString;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/SessionStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/SessionStatisticsImpl.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/stat/SessionStatisticsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/stat/internal/SessionStatisticsImpl.java
index 70c6c24cfb..3b60ebf6f9 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/SessionStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/SessionStatisticsImpl.java
@@ -1,66 +1,68 @@
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
-package org.hibernate.stat;
+package org.hibernate.stat.internal;
+
 import java.util.Collections;
 import java.util.Set;
+
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.stat.SessionStatistics;
 
 /**
  * @author Gavin King
  */
 public class SessionStatisticsImpl implements SessionStatistics {
 
 	private final SessionImplementor session;
 	
 	public SessionStatisticsImpl(SessionImplementor session) {
 		this.session = session;
 	}
 
 	public int getEntityCount() {
 		return session.getPersistenceContext().getEntityEntries().size();
 	}
 	
 	public int getCollectionCount() {
 		return session.getPersistenceContext().getCollectionEntries().size();
 	}
 	
 	public Set getEntityKeys() {
 		return Collections.unmodifiableSet( session.getPersistenceContext().getEntitiesByKey().keySet() );
 	}
 	
 	public Set getCollectionKeys() {
 		return Collections.unmodifiableSet( session.getPersistenceContext().getCollectionsByKey().keySet() );
 	}
 	
 	public String toString() {
 		return new StringBuffer()
 			.append("SessionStatistics[")
 			.append("entity count=").append( getEntityCount() )
 			.append("collection count=").append( getCollectionCount() )
 			.append(']')
 			.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/package.html b/hibernate-core/src/main/java/org/hibernate/stat/package.html
index 8fcebc6574..85468ead3c 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/stat/package.html
@@ -1,34 +1,33 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
   ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
   ~ distributed under license by Red Hat Middleware LLC.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
   ~
   -->
 
 <html>
 <head></head>
 <body>
 <p>
-	This package exposes statistics about a running 
-	Hibernate instance to the application.
+	This package exposes statistics about a running Hibernate instance to the application.
 </p>
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImplementor.java b/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsImplementor.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/stat/StatisticsImplementor.java
rename to hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsImplementor.java
index 88a6310b7b..21970d17c5 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsImplementor.java
@@ -1,205 +1,206 @@
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
-package org.hibernate.stat;
+package org.hibernate.stat.spi;
 
+import org.hibernate.stat.Statistics;
 
 /**
  * Statistics SPI for the Hibernate core.  This is essentially the "statistic collector" API, its the contract
  * called to collect various stats.
  * 
  * @author Emmanuel Bernard
  */
-public interface StatisticsImplementor {
+public interface StatisticsImplementor extends Statistics {
 	/**
 	 * Callback about a session being opened.
 	 */
 	public void openSession();
 
 	/**
 	 * Callback about a session being closed.
 	 */
 	public void closeSession();
 
 	/**
 	 * Callback about a flush occurring
 	 */
 	public void flush();
 
 	/**
 	 * Callback about a connection being obtained from {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider}
 	 */
 	public void connect();
 
 	/**
 	 * Callback about a statement being prepared.
 	 */
 	public void prepareStatement();
 
 	/**
 	 * Callback about a statement being closed.
 	 */
 	public void closeStatement();
 
 	/**
 	 * Callback about a transaction completing.
 	 *
 	 * @param success Was the transaction successful?
 	 */
 	public void endTransaction(boolean success);
 
 	/**
 	 * Callback about an entity being loaded.  This might indicate a proxy or a fully initialized entity, but in either
 	 * case it means without a separate SQL query being needed.
 	 *
 	 * @param entityName The name of the entity loaded.
 	 */
 	public void loadEntity(String entityName);
 
 	/**
 	 * Callback about an entity being fetched.  Unlike {@link #loadEntity} this indicates a separate query being
 	 * performed.
 	 *
 	 * @param entityName The name of the entity fetched.
 	 */
 	public void fetchEntity(String entityName);
 
 	/**
 	 * Callback about an entity being updated.
 	 *
 	 * @param entityName The name of the entity updated.
 	 */
 	public void updateEntity(String entityName);
 
 	/**
 	 * Callback about an entity being inserted
 	 *
 	 * @param entityName The name of the entity inserted
 	 */
 	public void insertEntity(String entityName);
 
 	/**
 	 * Callback about an entity being deleted.
 	 *
 	 * @param entityName The name of the entity deleted.
 	 */
 	public void deleteEntity(String entityName);
 
 	/**
 	 * Callback about an optimistic lock failure on an entity
 	 *
 	 * @param entityName The name of the entity.
 	 */
 	public void optimisticFailure(String entityName);
 
 	/**
 	 * Callback about a collection loading.  This might indicate a lazy collection or an initialized collection being
 	 * created, but in either case it means without a separate SQL query being needed.
 	 *
 	 * @param role The collection role.
 	 */
 	public void loadCollection(String role);
 
 	/**
 	 * Callback to indicate a collection being fetched.  Unlike {@link #loadCollection}, this indicates a separate
 	 * query was needed.
 	 *
 	 * @param role The collection role.
 	 */
 	public void fetchCollection(String role);
 
 	/**
 	 * Callback indicating a collection was updated.
 	 *
 	 * @param role The collection role.
 	 */
 	public void updateCollection(String role);
 
 	/**
 	 * Callback indicating a collection recreation (full deletion + full (re-)insertion).
 	 *
 	 * @param role The collection role.
 	 */
 	public void recreateCollection(String role);
 
 	/**
 	 * Callback indicating a collection removal.
 	 *
 	 * @param role The collection role.
 	 */
 	public void removeCollection(String role);
 
 	/**
 	 * Callback indicating a put into second level cache.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void secondLevelCachePut(String regionName);
 
 	/**
 	 * Callback indicating a get from second level cache resulted in a hit.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void secondLevelCacheHit(String regionName);
 
 	/**
 	 * Callback indicating a get from second level cache resulted in a miss.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void secondLevelCacheMiss(String regionName);
 
 	/**
 	 * Callback indicating a put into the query cache.
 	 *
 	 * @param hql The query
 	 * @param regionName The cache region
 	 */
 	public void queryCachePut(String hql, String regionName);
 
 	/**
 	 * Callback indicating a get from the query cache resulted in a hit.
 	 *
 	 * @param hql The query
 	 * @param regionName The name of the cache region
 	 */
 	public void queryCacheHit(String hql, String regionName);
 
 	/**
 	 * Callback indicating a get from the query cache resulted in a miss.
 	 *
 	 * @param hql The query
 	 * @param regionName The name of the cache region
 	 */
 	public void queryCacheMiss(String hql, String regionName);
 
 	/**
 	 * Callback indicating execution of a sql/hql query
 	 *
 	 * @param hql The query
 	 * @param rows Number of rows returned
 	 * @param time execution time
 	 */
 	public void queryExecuted(String hql, int rows, long time);
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
index f1267b2698..ebc840dd5f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
@@ -1,71 +1,71 @@
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
 package org.hibernate.test.common;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistry;
-import org.hibernate.stat.ConcurrentStatisticsImpl;
-import org.hibernate.stat.StatisticsImplementor;
+import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
+import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class TransactionEnvironmentImpl implements TransactionEnvironment {
 	private final ServiceRegistry serviceRegistry;
 	private final ConcurrentStatisticsImpl statistics = new ConcurrentStatisticsImpl();
 
 	public TransactionEnvironmentImpl(ServiceRegistry serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		throw new NotYetImplementedException( "Not available in this context" );
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return serviceRegistry.getService( JdbcServices.class );
 	}
 
 	@Override
 	public JtaPlatform getJtaPlatform() {
 		return serviceRegistry.getService( JtaPlatform.class );
 	}
 
 	@Override
 	public TransactionFactory getTransactionFactory() {
 		return serviceRegistry.getService( TransactionFactory.class );
 	}
 
 	@Override
 	public StatisticsImplementor getStatisticsImplementor() {
 		return statistics;
 	}
 }
