diff --git a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
index af932e6393..68e109e913 100644
--- a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
@@ -1,360 +1,369 @@
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
 package org.hibernate;
 
 import javax.naming.Referenceable;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
+import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.stat.Statistics;
 
 /**
  * The main contract here is the creation of {@link Session} instances.  Usually
  * an application has a single {@link SessionFactory} instance and threads
  * servicing client requests obtain {@link Session} instances from this factory.
  * <p/>
  * The internal state of a {@link SessionFactory} is immutable.  Once it is created
  * this internal state is set.  This internal state includes all of the metadata
  * about Object/Relational Mapping.
  * <p/>
  * Implementors <strong>must</strong> be threadsafe.
  *
  * @see org.hibernate.cfg.Configuration
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface SessionFactory extends Referenceable, Serializable {
+
+	public interface SessionFactoryOptions {
+		Interceptor getInterceptor();
+		EntityNotFoundDelegate getEntityNotFoundDelegate();
+	}
+
+	public SessionFactoryOptions getSessionFactoryOptions();
+
 	/**
 	 * Obtain a {@link Session} builder.
 	 *
 	 * @return The session builder
 	 */
 	public SessionBuilder withOptions();
 
 	/**
 	 * Open a {@link Session}.
 	 * <p/>
 	 * JDBC {@link Connection connection(s} will be obtained from the
 	 * configured {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} as needed
 	 * to perform requested work.
 	 *
 	 * @return The created session.
 	 *
 	 * @throws HibernateException Indicates a problem opening the session; pretty rare here.
 	 */
 	public Session openSession() throws HibernateException;
 
 	/**
 	 * Obtains the current session.  The definition of what exactly "current"
 	 * means controlled by the {@link org.hibernate.context.spi.CurrentSessionContext} impl configured
 	 * for use.
 	 * <p/>
 	 * Note that for backwards compatibility, if a {@link org.hibernate.context.spi.CurrentSessionContext}
 	 * is not configured but JTA is configured this will default to the {@link org.hibernate.context.internal.JTASessionContext}
 	 * impl.
 	 *
 	 * @return The current session.
 	 *
 	 * @throws HibernateException Indicates an issue locating a suitable current session.
 	 */
 	public Session getCurrentSession() throws HibernateException;
 
 	/**
 	 * Obtain a {@link StatelessSession} builder.
 	 *
 	 * @return The stateless session builder
 	 */
 	public StatelessSessionBuilder withStatelessOptions();
 
 	/**
 	 * Open a new stateless session.
 	 *
 	 * @return The created stateless session.
 	 */
 	public StatelessSession openStatelessSession();
 
 	/**
 	 * Open a new stateless session, utilizing the specified JDBC
 	 * {@link Connection}.
 	 *
 	 * @param connection Connection provided by the application.
 	 *
 	 * @return The created stateless session.
 	 */
 	public StatelessSession openStatelessSession(Connection connection);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
 	 *
 	 * @param entityClass The entity class
 	 *
 	 * @return The metadata associated with the given entity; may be null if no such
 	 * entity was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 */
 	public ClassMetadata getClassMetadata(Class entityClass);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
 	 *
 	 * @param entityName The entity class
 	 *
 	 * @return The metadata associated with the given entity; may be null if no such
 	 * entity was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 * @since 3.0
 	 */
 	public ClassMetadata getClassMetadata(String entityName);
 
 	/**
 	 * Get the {@link CollectionMetadata} associated with the named collection role.
 	 *
 	 * @param roleName The collection role (in form [owning-entity-name].[collection-property-name]).
 	 *
 	 * @return The metadata associated with the given collection; may be null if no such
 	 * collection was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 */
 	public CollectionMetadata getCollectionMetadata(String roleName);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} for all mapped entities.
 	 *
 	 * @return A map containing all {@link ClassMetadata} keyed by the
 	 * corresponding {@link String} entity-name.
 	 *
 	 * @throws HibernateException Generally empty map is returned instead of throwing.
 	 *
 	 * @since 3.0 changed key from {@link Class} to {@link String}.
 	 */
 	public Map<String,ClassMetadata> getAllClassMetadata();
 
 	/**
 	 * Get the {@link CollectionMetadata} for all mapped collections
 	 *
 	 * @return a map from <tt>String</tt> to <tt>CollectionMetadata</tt>
 	 *
 	 * @throws HibernateException Generally empty map is returned instead of throwing.
 	 */
 	public Map getAllCollectionMetadata();
 
 	/**
 	 * Retrieve the statistics fopr this factory.
 	 *
 	 * @return The statistics.
 	 */
 	public Statistics getStatistics();
 
 	/**
 	 * Destroy this <tt>SessionFactory</tt> and release all resources (caches,
 	 * connection pools, etc).
 	 * <p/>
 	 * It is the responsibility of the application to ensure that there are no
 	 * open {@link Session sessions} before calling this method as the impact
 	 * on those {@link Session sessions} is indeterminate.
 	 * <p/>
 	 * No-ops if already {@link #isClosed closed}.
 	 *
 	 * @throws HibernateException Indicates an issue closing the factory.
 	 */
 	public void close() throws HibernateException;
 
 	/**
 	 * Is this factory already closed?
 	 *
 	 * @return True if this factory is already closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Obtain direct access to the underlying cache regions.
 	 *
 	 * @return The direct cache access API.
 	 */
 	public Cache getCache();
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param persistentClass The entity class for which to evict data.
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntityRegion(Class)} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evict(Class persistentClass) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level  cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param persistentClass The entity class for which to evict data.
 	 * @param id The entity id
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#containsEntity(Class, Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evict(Class persistentClass, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param entityName The entity name for which to evict data.
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntityRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evictEntity(String entityName) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level  cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param entityName The entity name for which to evict data.
 	 * @param id The entity id
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntity(String,Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evictEntity(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param roleName The name of the collection role whose regions should be evicted
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'roleName' did not name a mapped collection or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictCollectionRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evictCollection(String roleName) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param roleName The name of the collection role
 	 * @param id The id of the collection owner
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'roleName' did not name a mapped collection or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictCollection(String,Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evictCollection(String roleName, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict any query result sets cached in the named query cache region.
 	 *
 	 * @param cacheRegion The named query cache region from which to evict.
 	 *
 	 * @throws HibernateException Since a not-found 'cacheRegion' simply no-ops,
 	 * this should indicate a problem communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictQueryRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evictQueries(String cacheRegion) throws HibernateException;
 
 	/**
 	 * Evict any query result sets cached in the default query cache region.
 	 *
 	 * @throws HibernateException Indicate a problem communicating with
 	 * underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictQueryRegions} accessed through
 	 * {@link #getCache()} instead.
 	 */
 	public void evictQueries() throws HibernateException;
 
 	/**
 	 * Obtain a set of the names of all filters defined on this SessionFactory.
 	 *
 	 * @return The set of filter names.
 	 */
 	public Set getDefinedFilterNames();
 
 	/**
 	 * Obtain the definition of a filter by name.
 	 *
 	 * @param filterName The name of the filter for which to obtain the definition.
 	 * @return The filter definition.
 	 * @throws HibernateException If no filter defined with the given name.
 	 */
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException;
 
 	/**
 	 * Determine if this session factory contains a fetch profile definition
 	 * registered under the given name.
 	 *
 	 * @param name The name to check
 	 * @return True if there is such a fetch profile; false otherwise.
 	 */
 	public boolean containsFetchProfileDefinition(String name);
 
 	/**
 	 * Retrieve this factory's {@link TypeHelper}
 	 *
 	 * @return The factory's {@link TypeHelper}
 	 */
 	public TypeHelper getTypeHelper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
index 425dfac570..54f29470d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
@@ -1,76 +1,95 @@
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
 package org.hibernate.cache.internal;
 
 import java.util.Comparator;
 
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.type.VersionType;
 
 /**
  * {@inheritDoc}
  *
  * @author Steve Ebersole
  */
 public class CacheDataDescriptionImpl implements CacheDataDescription {
 	private final boolean mutable;
 	private final boolean versioned;
 	private final Comparator versionComparator;
 
 	public CacheDataDescriptionImpl(boolean mutable, boolean versioned, Comparator versionComparator) {
 		this.mutable = mutable;
 		this.versioned = versioned;
 		this.versionComparator = versionComparator;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public Comparator getVersionComparator() {
 		return versionComparator;
 	}
 
 	public static CacheDataDescriptionImpl decode(PersistentClass model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.isVersioned(),
 				model.isVersioned() ? ( ( VersionType ) model.getVersion().getType() ).getComparator() : null
 		);
 	}
 
+	public static CacheDataDescriptionImpl decode(EntityBinding model) {
+		Comparator versionComparator = null;
+		if ( model.isVersioned() ) {
+			versionComparator = (
+					( VersionType ) model
+								.getVersioningValueBinding()
+								.getHibernateTypeDescriptor()
+								.getExplicitType()
+			).getComparator();
+		}
+
+		return new CacheDataDescriptionImpl(
+				model.isMutable(),
+				model.isVersioned(),
+				versionComparator
+		);
+	}
+
 	public static CacheDataDescriptionImpl decode(Collection model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.getOwner().isVersioned(),
 				model.getOwner().isVersioned() ? ( ( VersionType ) model.getOwner().getVersion().getType() ).getComparator() : null
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 7b88455ecb..1286675c25 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,410 +1,410 @@
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
 package org.hibernate.cfg;
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.internal.bridge.RegionFactoryCacheProviderBridge;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
-	protected SettingsFactory() {
+	public SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
         LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
         LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
         if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				Environment.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
         LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
         LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
         LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
         if (statementFetchSize != null) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
         LOG.debugf( "Connection release mode: %s", releaseModeName );
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
                 LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
         if ( defaultSchema != null ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
         if (defaultCatalog != null) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
         if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
         LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
         LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
         LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
         LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
         Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
         LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
         LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
         LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
         LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
         if (prefix != null) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
         LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
 		LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
         LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
 		if ( "validate".equals(autoSchemaExport) ) {
 			settings.setAutoValidateSchema( true );
 		}
 		if ( "update".equals(autoSchemaExport) ) {
 			settings.setAutoUpdateSchema( true );
 		}
 		if ( "create".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema( true );
 		}
 		if ( "create-drop".equals( autoSchemaExport ) ) {
 			settings.setAutoCreateSchema( true );
 			settings.setAutoDropSchema( true );
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
         LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
         LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
         LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		settings.setCheckNullability(checkNullability);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debugf("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.internal.StandardQueryCacheFactory"
 		);
         LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
 			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( queryCacheFactoryClassName )
 					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null && cachingEnabled ) {
 			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
 			if ( providerClassName != null ) {
 				// legacy behavior, apply the bridge...
 				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
 			}
 		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
 		);
         LOG.debugf( "Query translator: %s", className );
 		try {
 			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( className )
 					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null && cachingEnabled ) {
 			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
 			if ( providerClassName != null ) {
 				// legacy behavior, apply the bridge...
 				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
 			}
 		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 788a916eb0..a26521ee92 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1497 +1,1608 @@
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
 package org.hibernate.internal;
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
-import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
+import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 
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
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactory, SessionFactoryImplementor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
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
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map fetchProfiles;
 	private final transient Map imports;
-	private final transient Interceptor interceptor;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient CurrentSessionContext currentSessionContext;
-	private final transient EntityNotFoundDelegate entityNotFoundDelegate;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient HashMap entityNameResolvers = new HashMap();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient Cache cacheAccess = new CacheImpl();
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
+	private final transient SessionFactoryOptions sessionFactoryOptions;
 
 	@SuppressWarnings( {"unchecked"} )
 	public SessionFactoryImpl(
-			Configuration cfg,
+			final Configuration cfg,
 	        Mapping mapping,
 			ServiceRegistry serviceRegistry,
 	        Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
+		sessionFactoryOptions = new SessionFactoryOptions() {
+			private EntityNotFoundDelegate entityNotFoundDelegate;
+
+			@Override
+			public Interceptor getInterceptor() {
+				return cfg.getInterceptor();
+			}
+
+			@Override
+			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
+				if ( entityNotFoundDelegate == null ) {
+					if ( cfg.getEntityNotFoundDelegate() != null ) {
+						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
+					}
+					else {
+						entityNotFoundDelegate = new EntityNotFoundDelegate() {
+							public void handleEntityNotFound(String entityName, Serializable id) {
+								throw new ObjectNotFoundException( id, entityName );
+							}
+						};
+					}
+				}
+				return entityNotFoundDelegate;
+			}
+		};
+
 		this.settings = settings;
-		this.interceptor = cfg.getInterceptor();
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
         LOG.debugf("Session factory constructed with filter configurations : %s", filters);
         LOG.debugf("Instantiating session factory with properties: %s", properties);
 
 		// Caches
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		// todo : everything above here consider implementing as standard SF service.  specifically: stats, caches, types, function-reg
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
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
 				final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
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
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
                 LOG.trace("Building cache for collection data [" + model.getRole() + "]");
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
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
 		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, serviceRegistry.getService( JndiService.class ) );
 
         LOG.debugf("Instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, cfg ).create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg );
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
 
-		// EntityNotFoundDelegate
-		EntityNotFoundDelegate entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
-		if ( entityNotFoundDelegate == null ) {
-			entityNotFoundDelegate = new EntityNotFoundDelegate() {
-				public void handleEntityNotFound(String entityName, Serializable id) {
-					throw new ObjectNotFoundException( id, entityName );
-				}
-			};
-		}
-		this.entityNotFoundDelegate = entityNotFoundDelegate;
-
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
 
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
-	        Mapping mapping,
-			ServiceRegistry serviceRegistry,
+			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		// TODO: remove initialization of final variables; just setting to null to make compiler happy
 		this.name = null;
 		this.uuid = null;
-		this.entityPersisters = null;
-		this.classMetadata = null;
 		this.collectionPersisters = null;
 		this.collectionMetadata = null;
 		this.collectionRolesByEntityParticipant = null;
-		this.identifierGenerators = null;
 		this.namedQueries = null;
 		this.namedSqlQueries = null;
 		this.sqlResultSetMappings = null;
 		this.fetchProfiles = null;
 		this.imports = null;
-		this.interceptor = null;
 		this.queryCache = null;
 		this.updateTimestampsCache = null;
 		this.queryCaches = null;
 		this.currentSessionContext = null;
-		this.entityNotFoundDelegate = null;
 		this.sqlFunctionRegistry = null;
-		this.queryPlanCache = null;
 		this.transactionEnvironment = null;
 
-		ConfigurationService configurationService = serviceRegistry.getService( ConfigurationService.class );
-		this.settings = null;
+		this.sessionFactoryOptions = sessionFactoryOptions;
 
-		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
-				this,
-				metadata
+		this.properties = createPropertiesFromMap(
+				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
-		// TODO: get Interceptor from ConfurationService
-		//this.interceptor = cfg.getInterceptor();
+		// TODO: these should be moved into SessionFactoryOptions
+		this.settings = new SettingsFactory().buildSettings(
+				properties,
+				metadata.getServiceRegistry()
+		);
 
-		// TODO: find references to properties and make sure everything needed is available to services via
-		//       ConfigurationService
-		this.properties = null;
+		this.serviceRegistry =
+				metadata.getServiceRegistry()
+						.getService( SessionFactoryServiceRegistryFactory.class )
+						.buildServiceRegistry( this, metadata );
+
+		// TODO: get SQL functions from a new service
+		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
-		// TODO: should this be build along w/ metadata? seems like it should so app has more control over it...
-		//this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), metadata.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		for ( FilterDefinition filterDefinition : metadata.getFilterDefinitions() ) {
 			filters.put( filterDefinition.getFilterName(), filterDefinition );
 		}
 
         LOG.debugf("Session factory constructed with filter configurations : %s", filters);
-        LOG.debugf("Instantiating session factory with properties: %s", configurationService.getSettings() );
+        LOG.debugf("Instantiating session factory with properties: %s", properties );
+
+		// TODO: get RegionFactory from service registry
+		settings.getRegionFactory().start( settings, properties );
+		this.queryPlanCache = new QueryPlanCache( this );
+
+		class IntegratorObserver implements SessionFactoryObserver {
+			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
+
+			@Override
+			public void sessionFactoryCreated(SessionFactory factory) {
+			}
+
+			@Override
+			public void sessionFactoryClosed(SessionFactory factory) {
+				for ( Integrator integrator : integrators ) {
+					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
+				}
+			}
+		}
+
+		final IntegratorObserver integratorObserver = new IntegratorObserver();
+		this.observer.addObserver( integratorObserver );
+		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
+			// TODO: add Integrator.integrate(MetadataImplementor, ...)
+			// integrator.integrate( cfg, this, this.serviceRegistry );
+			integratorObserver.integrators.add( integrator );
+		}
+
+
+		//Generators:
+
+		identifierGenerators = new HashMap();
+		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
+			if ( entityBinding.isRoot() ) {
+				// TODO: create the IdentifierGenerator while the metadata is being build, then simply
+				// use EntityBinding.getIdentifierGenerator() (also remove getIdentifierGeneratorFactory from Mappings)
+				IdentifierGenerator generator = entityBinding.getEntityIdentifier().createIdentifierGenerator(
+						metadata.getIdentifierGeneratorFactory()
+				);
+				identifierGenerators.put( entityBinding.getEntity().getName(), generator );
+			}
+		}
+
+		///////////////////////////////////////////////////////////////////////
+		// Prepare persisters and link them up with their cache
+		// region/access-strategy
+
+		StringBuilder stringBuilder = new StringBuilder();
+		if ( settings.getCacheRegionPrefix() != null) {
+			stringBuilder
+					.append( settings.getCacheRegionPrefix() )
+					.append( '.' );
+		}
+		final String cacheRegionPrefix = stringBuilder.toString();
+
+		entityPersisters = new HashMap();
+		Map<String, EntityRegionAccessStrategy> entityAccessStrategies = new HashMap<String, EntityRegionAccessStrategy>();
+		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
+		for ( EntityBinding model : metadata.getEntityBindings() ) {
+			// TODO: should temp table prep happen when metadata is being built?
+			//model.prepareTemporaryTables( metadata, getDialect() );
+			// cache region is defined by the root-class in the hierarchy...
+			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
+			EntityRegionAccessStrategy accessStrategy = null;
+			if ( settings.isSecondLevelCacheEnabled() &&
+					rootEntityBinding.getCaching() != null &&
+					model.getCaching() != null &&
+					model.getCaching().getAccessType() != null ) {
+				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getCaching().getRegion();
+				accessStrategy = entityAccessStrategies.get( cacheRegionName );
+				if ( accessStrategy == null ) {
+					final AccessType accessType = model.getCaching().getAccessType();
+					LOG.trace("Building cache for entity data [" + model.getEntity().getName() + "]");
+					EntityRegion entityRegion =
+							settings.getRegionFactory().buildEntityRegion(
+									cacheRegionName,
+									properties,
+									CacheDataDescriptionImpl.decode( model )
+							);
+					accessStrategy = entityRegion.buildAccessStrategy( accessType );
+					entityAccessStrategies.put( cacheRegionName, accessStrategy );
+					allCacheRegions.put( cacheRegionName, entityRegion );
+				}
+			}
+			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
+					model, accessStrategy, this, metadata
+			);
+			entityPersisters.put( model.getEntity().getName(), cp );
+			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
+		}
+		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		// TODO: implement
+	}
 
+	@SuppressWarnings( {"unchecked"} )
+	private static Properties createPropertiesFromMap(Map map) {
+		Properties properties = new Properties();
+		properties.putAll( map );
+		return properties;
 	}
 
 	public Session openSession() throws HibernateException {
 		return withOptions().openSession();
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return withOptions()
 				.autoClose( false )
 				.flushBeforeCompletion( false )
 				.connectionReleaseMode( ConnectionReleaseMode.AFTER_STATEMENT )
 				.openSession();
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
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
 		if(LOG.isDebugEnabled())
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
 		if(LOG.isDebugEnabled())
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
 
+	@Override
+	public SessionFactoryOptions getSessionFactoryOptions() {
+		return sessionFactoryOptions;
+	}
+
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
-		return interceptor;
+		return sessionFactoryOptions.getInterceptor();
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
 
 	@Override
 	public Reference getReference() throws NamingException {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
         LOG.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( result == null ) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
             if ( result == null ) {
 				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 			}
             LOG.debugf("Resolved SessionFactory by name");
         }
 		else {
 			LOG.debugf("Resolved SessionFactory by UUID");
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
         LOG.trace( "Deserializing" );
 		in.defaultReadObject();
         LOG.debugf( "Deserialized: %s", uuid );
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
 		return getEntityPersister( className ).getPropertyType( propertyName );
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
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid, name, serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
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
 					EntityMode.POJO,			// we have to assume POJO
 					null, 						// and also assume non tenancy
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
 					EntityMode.POJO,			// we have to assume POJO
 					null,						// and also assume non tenancy
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
 		return getStatisticsImplementor();
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return serviceRegistry.getService( StatisticsImplementor.class );
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
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
-		return entityNotFoundDelegate;
+		return sessionFactoryOptions.getEntityNotFoundDelegate();
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
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( result == null ) {
             LOG.trace("Could not locate session factory by uuid [" + uuid + "] during session deserialization; trying name");
 			if ( isNamed ) {
 				result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			}
 			if ( result == null ) {
 				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
 			}
 		}
 		return ( SessionFactoryImpl ) result;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private EntityMode entityMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.entityMode = settings.getDefaultEntityMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					entityMode,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 		}
 
 		@Override
 		public SessionBuilder interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			this.flushBeforeCompletion = flushBeforeCompletion;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder entityMode(EntityMode entityMode) {
 			this.entityMode = entityMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
index 777592156e..26bfb32f9d 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
@@ -1,240 +1,248 @@
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
 package org.hibernate.jmx;
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.InvalidObjectException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.HibernateException;
+import org.hibernate.Interceptor;
+import org.hibernate.ObjectNotFoundException;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.StatelessSession;
 import org.hibernate.TypeHelper;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.internal.SessionFactoryRegistry;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
+import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jndi.internal.JndiServiceImpl;
 import org.hibernate.stat.Statistics;
 
 /**
  * A flyweight for <tt>SessionFactory</tt>. If the MBean itself does not
  * have classpath to the persistent classes, then a stub will be registered
  * with JNDI and the actual <tt>SessionFactoryImpl</tt> built upon first
  * access.
  *
  * @author Gavin King
  *
  * @deprecated See <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6190">HHH-6190</a> for details
  */
 @Deprecated
 public class SessionFactoryStub implements SessionFactory {
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryStub.class.getName());
 
 	private transient SessionFactory impl;
 	private transient HibernateService service;
 	private String uuid;
 	private String name;
 
 	SessionFactoryStub(HibernateService service) {
 		this.service = service;
 		this.name = service.getJndiName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 
 		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, new JndiServiceImpl( service.getProperties() )  );
 	}
 
 	@Override
+	public SessionFactoryOptions getSessionFactoryOptions() {
+		return impl.getSessionFactoryOptions();
+	}
+
+	@Override
 	public SessionBuilder withOptions() {
 		return getImpl().withOptions();
 	}
 
 	public Session openSession() throws HibernateException {
 		return getImpl().openSession();
 	}
 
 	public Session getCurrentSession() {
 		return getImpl().getCurrentSession();
 	}
 
 	private synchronized SessionFactory getImpl() {
 		if (impl==null) impl = service.buildSessionFactory();
 		return impl;
 	}
 
 	//readResolveObject
 	private Object readResolve() throws ObjectStreamException {
 		// look for the instance by uuid
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid ) ;
 		if ( result == null ) {
             // in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( result == null ) {
 				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 			}
             LOG.debugf("Resolved stub SessionFactory by name");
         }
 		else {
 			LOG.debugf("Resolved stub SessionFactory by UUID");
 		}
 		return result;
 	}
 
 	/**
 	 * @see javax.naming.Referenceable#getReference()
 	 */
 	@Override
 	public Reference getReference() throws NamingException {
 		return new Reference(
 				SessionFactoryStub.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getImpl().getClassMetadata(persistentClass);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName)
 	throws HibernateException {
 		return getImpl().getClassMetadata(entityName);
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return getImpl().getCollectionMetadata(roleName);
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return getImpl().getAllClassMetadata();
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return getImpl().getAllCollectionMetadata();
 	}
 
 	public void close() throws HibernateException {
 	}
 
 	public boolean isClosed() {
 		return false;
 	}
 
 	public Cache getCache() {
 		return getImpl().getCache();
 	}
 
 	public void evict(Class persistentClass, Serializable id)
 		throws HibernateException {
 		getImpl().evict(persistentClass, id);
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getImpl().evict(persistentClass);
 	}
 
 	public void evictEntity(String entityName, Serializable id)
 	throws HibernateException {
 		getImpl().evictEntity(entityName, id);
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getImpl().evictEntity(entityName);
 	}
 
 	public void evictCollection(String roleName, Serializable id)
 		throws HibernateException {
 		getImpl().evictCollection(roleName, id);
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getImpl().evictCollection(roleName);
 	}
 
 	public void evictQueries() throws HibernateException {
 		getImpl().evictQueries();
 	}
 
 	public void evictQueries(String cacheRegion) throws HibernateException {
 		getImpl().evictQueries(cacheRegion);
 	}
 
 	public Statistics getStatistics() {
 		return getImpl().getStatistics();
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return getImpl().withStatelessOptions();
 	}
 
 	public StatelessSession openStatelessSession() {
 		return getImpl().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection conn) {
 		return getImpl().openStatelessSession(conn);
 	}
 
 	public Set getDefinedFilterNames() {
 		return getImpl().getDefinedFilterNames();
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		return getImpl().getFilterDefinition( filterName );
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return getImpl().containsFetchProfileDefinition( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return getImpl().getTypeHelper();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
index a3cd180f49..a63d14aa3a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
@@ -1,67 +1,76 @@
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
 
 package org.hibernate.metamodel;
 
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.TypeDef;
 
 /**
  * @author Steve Ebersole
  */
 public interface Metadata {
 	/**
 	 * Exposes the options used to produce a {@link Metadata} instance.
 	 */
 	public static interface Options {
 		public SourceProcessingOrder getSourceProcessingOrder();
 		public NamingStrategy getNamingStrategy();
 		public SharedCacheMode getSharedCacheMode();
 		public AccessType getDefaultAccessType();
 		public boolean useNewIdentifierGenerators();
 		public String getDefaultSchemaName();
 		public String getDefaultCatalogName();
 	}
 
 	public Options getOptions();
 
+	public SessionFactoryBuilder getSessionFactoryBuilder();
+
 	public SessionFactory buildSessionFactory();
 
 	public Iterable<EntityBinding> getEntityBindings();
 
 	public EntityBinding getEntityBinding(String entityName);
 
+	/**
+	 * Get the "root" entity binding
+	 * @param entityName
+	 * @return the "root entity binding; simply returns entityBinding if it is the root entity binding
+	 */
+	public EntityBinding getRootEntityBinding(String entityName);
+
 	public Iterable<TypeDef> getTypeDefinitions();
 
 	public Iterable<FilterDefinition> getFilterDefinitions();
 
 	public IdGenerator getIdGenerator(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/SessionFactoryBuilder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/SessionFactoryBuilder.java
new file mode 100644
index 0000000000..c97ede7191
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/SessionFactoryBuilder.java
@@ -0,0 +1,39 @@
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
+package org.hibernate.metamodel;
+
+import org.hibernate.Interceptor;
+import org.hibernate.SessionFactory;
+import org.hibernate.proxy.EntityNotFoundDelegate;
+
+/**
+ * @author Gail Badner
+ */
+public interface SessionFactoryBuilder {
+	public SessionFactoryBuilder with(Interceptor interceptor);
+
+	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate);
+
+	public SessionFactory buildSessionFactory();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index b9adc3bd1e..9cf6599423 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,458 +1,462 @@
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
 package org.hibernate.metamodel.binding;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.source.hbm.HbmBindingContext;
 import org.hibernate.metamodel.source.hbm.HbmHelper;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * Provides the link between the domain and the relational model for an entity.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class EntityBinding {
 	private final EntityIdentifier entityIdentifier = new EntityIdentifier( this );
 	private final boolean isRoot;
 
 	private InheritanceType entityInheritanceType;
 	private EntityDiscriminator entityDiscriminator;
 	private SimpleAttributeBinding versionBinding;
 
 	private Entity entity;
 	private TableSpecification baseTable;
 
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 	private Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
 
 	private Caching caching;
 
 	private MetaAttributeContext metaAttributeContext;
 
 	private String proxyInterfaceName;
 	private boolean lazy;
 	private boolean mutable;
 	private boolean explicitPolymorphism;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private boolean hasSubselectLoadableCollections;
 	private int optimisticLockMode;
 
 	private Class entityPersisterClass;
 	private Boolean isAbstract;
 
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private List<String> synchronizedTableNames;
 
 	public EntityBinding(boolean isRoot) {
 		this.isRoot = isRoot;
 	}
 
 	// TODO: change to intialize from Doimain
 	public void fromHbmXml(HbmBindingContext bindingContext, XMLClass entityClazz, Entity entity) {
 		this.entity = entity;
 		metaAttributeContext = HbmHelper.extractMetaAttributeContext( entityClazz.getMeta(), true, bindingContext.getMetaAttributeContext() );
 
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		lazy = MappingHelper.getBooleanValue(
 				entityClazz.isLazy(), bindingContext.getMappingDefaults().isDefaultLazy()
 		);
 		proxyInterfaceName = entityClazz.getProxy();
 		dynamicUpdate = entityClazz.isDynamicUpdate();
 		dynamicInsert = entityClazz.isDynamicInsert();
 		batchSize = MappingHelper.getIntValue( entityClazz.getBatchSize(), 0 );
 		selectBeforeUpdate = entityClazz.isSelectBeforeUpdate();
 
 		// OPTIMISTIC LOCK MODE
 		String optimisticLockModeString = MappingHelper.getStringValue( entityClazz.getOptimisticLock(), "version" );
 		if ( "version".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + optimisticLockModeString );
 		}
 
 		// PERSISTER
 		if ( entityClazz.getPersister() != null ) {
 			try {
 				entityPersisterClass = ReflectHelper.classForName( entityClazz.getPersister() );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				throw new MappingException(
 						"Could not find persister class: "
 								+ entityClazz.getPersister()
 				);
 			}
 		}
 
 		// CUSTOM SQL
 		XMLSqlInsertElement sqlInsert = entityClazz.getSqlInsert();
 		if ( sqlInsert != null ) {
 			customInsert = HbmHelper.getCustomSql(
 					sqlInsert.getValue(),
 					sqlInsert.isCallable(),
 					sqlInsert.getCheck().value()
 			);
 		}
 
 		XMLSqlDeleteElement sqlDelete = entityClazz.getSqlDelete();
 		if ( sqlDelete != null ) {
 			customDelete = HbmHelper.getCustomSql(
 					sqlDelete.getValue(),
 					sqlDelete.isCallable(),
 					sqlDelete.getCheck().value()
 			);
 		}
 
 		XMLSqlUpdateElement sqlUpdate = entityClazz.getSqlUpdate();
 		if ( sqlUpdate != null ) {
 			customUpdate = HbmHelper.getCustomSql(
 					sqlUpdate.getValue(),
 					sqlUpdate.isCallable(),
 					sqlUpdate.getCheck().value()
 			);
 		}
 
 		if ( entityClazz.getSynchronize() != null ) {
 			for ( XMLSynchronizeElement synchronize : entityClazz.getSynchronize() ) {
 				addSynchronizedTable( synchronize.getTable() );
 			}
 		}
 
 		isAbstract = entityClazz.isAbstract();
 	}
 
 	public boolean isRoot() {
 		return isRoot;
 	}
 
 	public Entity getEntity() {
 		return entity;
 	}
 
 	public void setEntity(Entity entity) {
 		this.entity = entity;
 	}
 
 	public TableSpecification getBaseTable() {
 		return baseTable;
 	}
 
 	public void setBaseTable(TableSpecification baseTable) {
 		this.baseTable = baseTable;
 	}
 
 	public EntityIdentifier getEntityIdentifier() {
 		return entityIdentifier;
 	}
 
 	public void bindEntityIdentifier(SimpleAttributeBinding attributeBinding) {
 		if ( !Column.class.isInstance( attributeBinding.getValue() ) ) {
 			throw new MappingException(
 					"Identifier value must be a Column; instead it is: " + attributeBinding.getValue().getClass()
 			);
 		}
 		entityIdentifier.setValueBinding( attributeBinding );
 		baseTable.getPrimaryKey().addColumn( Column.class.cast( attributeBinding.getValue() ) );
 	}
 
 	public EntityDiscriminator getEntityDiscriminator() {
 		return entityDiscriminator;
 	}
 
 	public void setInheritanceType(InheritanceType entityInheritanceType) {
 		this.entityInheritanceType = entityInheritanceType;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return entityInheritanceType;
 	}
 
+	public boolean isVersioned() {
+		return versionBinding != null;
+	}
+
 	public SimpleAttributeBinding getVersioningValueBinding() {
 		return versionBinding;
 	}
 
 	public Iterable<AttributeBinding> getAttributeBindings() {
 		return attributeBindingMap.values();
 	}
 
 	public AttributeBinding getAttributeBinding(String name) {
 		return attributeBindingMap.get( name );
 	}
 
 	public Iterable<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings() {
 		return entityReferencingAttributeBindings;
 	}
 
 	public SimpleAttributeBinding makeSimpleIdAttributeBinding(String name) {
 		final SimpleAttributeBinding binding = makeSimpleAttributeBinding( name, true, true );
 		getEntityIdentifier().setValueBinding( binding );
 		return binding;
 	}
 
 	public EntityDiscriminator makeEntityDiscriminator(String attributeName) {
 		if ( entityDiscriminator != null ) {
 			throw new AssertionFailure( "Creation of entity discriminator was called more than once" );
 		}
 		entityDiscriminator = new EntityDiscriminator();
 		entityDiscriminator.setValueBinding( makeSimpleAttributeBinding( attributeName, true, false ) );
 		return entityDiscriminator;
 	}
 
 	public SimpleAttributeBinding makeVersionBinding(String attributeName) {
 		versionBinding = makeSimpleAttributeBinding( attributeName, true, false );
 		return versionBinding;
 	}
 
 	public SimpleAttributeBinding makeSimpleAttributeBinding(String name) {
 		return makeSimpleAttributeBinding( name, false, false );
 	}
 
 	private SimpleAttributeBinding makeSimpleAttributeBinding(String name, boolean forceNonNullable, boolean forceUnique) {
 		final SimpleAttributeBinding binding = new SimpleAttributeBinding( this, forceNonNullable, forceUnique );
 		registerAttributeBinding( name, binding );
 		binding.setAttribute( entity.getAttribute( name ) );
 		return binding;
 	}
 
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(String attributeName) {
 		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this );
 		registerAttributeBinding( attributeName, binding );
 		binding.setAttribute( entity.getAttribute( attributeName ) );
 		return binding;
 	}
 
 	public BagBinding makeBagAttributeBinding(String attributeName, CollectionElementType collectionElementType) {
 		final BagBinding binding = new BagBinding( this, collectionElementType );
 		registerAttributeBinding( attributeName, binding );
 		binding.setAttribute( entity.getAttribute( attributeName ) );
 		return binding;
 	}
 
 	private void registerAttributeBinding(String name, EntityReferencingAttributeBinding attributeBinding) {
 		entityReferencingAttributeBindings.add( attributeBinding );
 		registerAttributeBinding( name, (AttributeBinding) attributeBinding );
 	}
 
 	private void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
 		attributeBindingMap.put( name, attributeBinding );
 	}
 
 	public Caching getCaching() {
 		return caching;
 	}
 
 	public void setCaching(Caching caching) {
 		this.caching = caching;
 	}
 
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	public void setWhereFilter(String whereFilter) {
 		this.whereFilter = whereFilter;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
 		this.explicitPolymorphism = explicitPolymorphism;
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String getDiscriminatorValue() {
 		return entityDiscriminator == null ? null : entityDiscriminator.getDiscriminatorValue();
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public void setSelectBeforeUpdate(Boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	/* package-protected */
 	void setSubselectLoadableCollections(boolean hasSubselectLoadableCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectLoadableCollections;
 	}
 
 	public int getOptimisticLockMode() {
 		return optimisticLockMode;
 	}
 
 	public void setOptimisticLockMode(int optimisticLockMode) {
 		this.optimisticLockMode = optimisticLockMode;
 	}
 
 	public Class getEntityPersisterClass() {
 		return entityPersisterClass;
 	}
 
 	public void setEntityPersisterClass(Class entityPersisterClass) {
 		this.entityPersisterClass = entityPersisterClass;
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	protected void addSynchronizedTable(String tablename) {
 		if ( synchronizedTableNames == null ) {
 			synchronizedTableNames = new ArrayList<String>();
 		}
 		synchronizedTableNames.add( tablename );
 	}
 
 
 	// Custom SQL ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private String loaderName;
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String loaderName) {
 		this.loaderName = loaderName;
 	}
 
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index b3c35ff6f2..4ed83c11f3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,397 +1,475 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.DuplicateMappingException;
+import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
+import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
+import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.MetaAttribute;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotations.AnnotationBinder;
 import org.hibernate.metamodel.source.hbm.HbmBinder;
 import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
+ * @author Gail Badner
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MetadataImpl.class.getName()
 	);
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final Options options;
 	private ClassLoaderService classLoaderService;
 
 	private TypeResolver typeResolver = new TypeResolver();
+
+	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
+
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 	private final Database database = new Database();
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
+	private Map<String, EntityBinding> rootEntityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports;
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
 	// todo : keep as part of Database?
 	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final Binder[] binders;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			binders = new Binder[] {
 					new HbmBinder( this ),
 					new AnnotationBinder( this )
 			};
 		}
 		else {
 			binders = new Binder[] {
 					new AnnotationBinder( this ),
 					new HbmBinder( this )
 			};
 		}
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( binders, metadataSources );
 		bindIndependentMetadata( binders, metadataSources );
 		bindTypeDependentMetadata( binders, metadataSources );
 		bindMappingMetadata( binders, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( binders, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 	}
 
 	private void prepare(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(Binder[] binders, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingDependentMetadata( metadataSources );
 		}
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		fetchProfiles.put( profile.getName(), profile );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition def) {
 		filterDefs.put( def.getFilterName(), def );
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefs.values();
 	}
 
 	@Override
 	public void addIdGenerator(IdGenerator generator) {
 		idGenerators.put( generator.getName(), generator );
 	}
 
 	@Override
 	public IdGenerator getIdGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGenerators.get( name );
 	}
 	@Override
 	public void registerIdentifierGenerator(String name, String generatorClassName) {
 		 identifierGeneratorFactory.register( name, classLoaderService().classForName( generatorClassName ) );
 	}
 
 	@Override
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 	}
 
 	@Override
 	public void addNamedNativeQuery(String name, NamedSQLQueryDefinition def) {
 		namedNativeQueryDefs.put( name, def );
 	}
 
 	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid native query name" );
 		}
 		return namedNativeQueryDefs.get( name );
 	}
 
 	@Override
 	public void addNamedQuery(String name, NamedQueryDefinition def) {
 		namedQueryDefs.put( name, def );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryDefs.get( name );
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		final TypeDef previous = typeDefs.put( typeDef.getName(), typeDef );
 		if ( previous != null ) {
 			LOG.debugf( "Duplicate typedef name [%s] now -> %s", typeDef.getName(), typeDef.getTypeClass() );
 		}
 	}
 
 	@Override
 	public Iterable<TypeDef> getTypeDefinitions() {
 		return typeDefs.values();
 	}
 
 	public TypeDef getTypeDef(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService(){
 		if(classLoaderService==null){
 			classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
-		// todo : implement!!!!
-		return null;
+		return sessionFactoryBuilder.buildSessionFactory();
 	}
 
 	@Override
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
+	@Override
+	public EntityBinding getRootEntityBinding(String entityName) {
+		EntityBinding rootEntityBinding = rootEntityBindingMap.get( entityName );
+		if ( rootEntityBinding == null ) {
+			EntityBinding entityBinding = entityBindingMap.get( entityName );
+			if ( entityBinding == null ) {
+				throw new IllegalStateException( "Unknown entity binding: " + entityName );
+			}
+			if ( entityBinding.isRoot() ) {
+				rootEntityBinding = entityBinding;
+			}
+			else {
+				if ( entityBinding.getEntity().getSuperType() == null ) {
+					throw new IllegalStateException( "Entity binding has no root: " + entityName );
+				}
+				rootEntityBinding = getRootEntityBinding( entityBinding.getEntity().getSuperType().getName() );
+			}
+			rootEntityBindingMap.put( entityName, rootEntityBinding );
+		}
+		return rootEntityBinding;
+	}
+
 	public Iterable<EntityBinding> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	public void addEntity(EntityBinding entityBinding) {
 		final String entityName = entityBinding.getEntity().getName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, entityBinding );
 	}
 
 	public PluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	public Iterable<PluralAttributeBinding> getCollections() {
 		return collectionBindingMap.values();
 	}
 
 	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
 		final String owningEntityName = pluralAttributeBinding.getEntityBinding().getEntity().getName();
 		final String attributeName = pluralAttributeBinding.getAttribute().getName();
 		final String collectionRole = owningEntityName + '.' + attributeName;
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, pluralAttributeBinding );
 	}
 
 	public void addImport(String importName, String entityName) {
 		if ( imports == null ) {
 			imports = new HashMap<String, String>();
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 	public Iterable<FetchProfile> getFetchProfiles() {
 		return fetchProfiles.values();
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
+	public SessionFactoryBuilder getSessionFactoryBuilder() {
+		return sessionFactoryBuilder;
+	}
+
+	@Override
 	public NamingStrategy getNamingStrategy() {
 		return options.getNamingStrategy();
 	}
 
 	@Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return globalMetaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return this;
 	}
 
 	private static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	private static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 	private static final String DEFAULT_CASCADE = "none";
 	private static final String DEFAULT_PROPERTY_ACCESS = "property";
 
+	@Override
+	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
+		return identifierGeneratorFactory;
+	}
+
+	@Override
+	public Type getIdentifierType(String entityName) throws MappingException {
+		EntityBinding entityBinding = getEntityBinding( entityName );
+		if ( entityBinding == null ) {
+			throw new MappingException( "Entity binding not known: " + entityName );
+		}
+		return entityBinding
+				.getEntityIdentifier()
+				.getValueBinding()
+				.getHibernateTypeDescriptor()
+				.getExplicitType();
+	}
+
+	@Override
+	public String getIdentifierPropertyName(String entityName) throws MappingException {
+		EntityBinding entityBinding = getEntityBinding( entityName );
+		if ( entityBinding == null ) {
+			throw new MappingException( "Entity binding not known: " + entityName );
+		}
+		AttributeBinding idBinding = entityBinding.getEntityIdentifier().getValueBinding();
+		return idBinding == null ? null : idBinding.getAttribute().getName();
+	}
+
+	@Override
+	public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
+		EntityBinding entityBinding = getEntityBinding( entityName );
+		if ( entityBinding == null ) {
+			throw new MappingException( "Entity binding not known: " + entityName );
+		}
+		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
+		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
+		if ( attributeBinding == null ) {
+			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
+		}
+		return attributeBinding.getHibernateTypeDescriptor().getExplicitType();
+	}
+
 	private class MappingDefaultsImpl implements MappingDefaults {
 
 		@Override
 		public String getPackageName() {
 			return null;
 		}
 
 		@Override
 		public String getDefaultSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
 		public String getDefaultCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
 		public String getDefaultIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDefaultDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDefaultCascade() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
 		public String getDefaultAccess() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
 		public boolean isDefaultLazy() {
 			return true;
 		}
 
 		@Override
 		public Map<String, MetaAttribute> getMappingMetas() {
 			return Collections.emptyMap();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java
new file mode 100644
index 0000000000..c3ec4978eb
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java
@@ -0,0 +1,88 @@
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
+package org.hibernate.metamodel.source.internal;
+
+import java.io.Serializable;
+
+import org.hibernate.EmptyInterceptor;
+import org.hibernate.Interceptor;
+import org.hibernate.ObjectNotFoundException;
+import org.hibernate.SessionFactory;
+import org.hibernate.internal.SessionFactoryImpl;
+import org.hibernate.metamodel.SessionFactoryBuilder;
+import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.proxy.EntityNotFoundDelegate;
+
+/**
+ * @author Gail Badner
+ */
+public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
+	SessionFactoryOptionsImpl options;
+
+	private final MetadataImplementor metadata;
+
+	/* package-protected */
+	SessionFactoryBuilderImpl(MetadataImplementor metadata) {
+		this.metadata = metadata;
+		options = new SessionFactoryOptionsImpl();
+	}
+
+	@Override
+	public SessionFactoryBuilder with(Interceptor interceptor) {
+		this.options.interceptor = interceptor;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate) {
+		this.options.entityNotFoundDelegate = entityNotFoundDelegate;
+		return this;
+	}
+
+	@Override
+	public SessionFactory buildSessionFactory() {
+		return new SessionFactoryImpl(metadata, options, null );
+	}
+
+	private static class SessionFactoryOptionsImpl implements SessionFactory.SessionFactoryOptions {
+		private Interceptor interceptor = EmptyInterceptor.INSTANCE;
+
+		// TODO: should there be a DefaultEntityNotFoundDelegate.INSTANCE?
+		private EntityNotFoundDelegate entityNotFoundDelegate = new EntityNotFoundDelegate() {
+				public void handleEntityNotFound(String entityName, Serializable id) {
+					throw new ObjectNotFoundException( id, entityName );
+				}
+		};
+
+		@Override
+		public Interceptor getInterceptor() {
+			return interceptor;
+		}
+
+		@Override
+		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
+			return entityNotFoundDelegate;
+		}
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
index 8afd249da7..884a2400a9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
@@ -1,71 +1,73 @@
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
 package org.hibernate.metamodel.source.spi;
 
 import org.hibernate.engine.spi.FilterDefinition;
+import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.Metadata;
+import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.type.TypeResolver;
 
 /**
  * @author Steve Ebersole
  */
-public interface MetadataImplementor extends Metadata, BindingContext {
+public interface MetadataImplementor extends Metadata, BindingContext, Mapping {
 	public BasicServiceRegistry getServiceRegistry();
 
 	public Database getDatabase();
 
 	public TypeResolver getTypeResolver();
 
 	public void addImport(String entityName, String entityName1);
 
 	public void addEntity(EntityBinding entityBinding);
 
 	public void addCollection(PluralAttributeBinding collectionBinding);
 
 	public void addFetchProfile(FetchProfile profile);
 
 	public void addTypeDefinition(TypeDef typeDef);
 
 	public void addFilterDefinition(FilterDefinition filterDefinition);
 
 	public void addIdGenerator(IdGenerator generator);
 
 	public void registerIdentifierGenerator(String name, String clazz);
 
 	public void addNamedNativeQuery(String name, NamedSQLQueryDefinition def);
 
 	public void addNamedQuery(String name, NamedQueryDefinition def);
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index 3754334ed6..6e54be196b 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,140 +1,146 @@
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
 package org.hibernate.metamodel.binding;
 
 import java.util.Iterator;
 import java.util.Set;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Basic tests of {@code hbm.xml} and annotation binding code
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractBasicBindingTests extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private MetadataSources sources;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	protected BasicServiceRegistry basicServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Test
 	public void testSimpleEntityMapping() {
 		MetadataImpl metadata = addSourcesForSimpleEntityBinding( sources );
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
+		assertRoot( metadata, entityBinding );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNull( entityBinding.getVersioningValueBinding() );
 	}
 
 	@Test
 	public void testSimpleVersionedEntityMapping() {
 		MetadataImpl metadata = addSourcesForSimpleVersionedEntityBinding( sources );
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleVersionedEntity.class.getName() );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNotNull( entityBinding.getVersioningValueBinding() );
 		assertNotNull( entityBinding.getVersioningValueBinding().getAttribute() );
 	}
 
 	@Test
 	public void testEntityWithManyToOneMapping() {
 		MetadataImpl metadata = addSourcesForManyToOne( sources );
 
 		EntityBinding simpleEntityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertIdAndSimpleProperty( simpleEntityBinding );
 
 		Set<EntityReferencingAttributeBinding> referenceBindings = simpleEntityBinding.getAttributeBinding( "id" )
 				.getEntityReferencingAttributeBindings();
 		assertEquals( "There should be only one reference binding", 1, referenceBindings.size() );
 
 		EntityReferencingAttributeBinding referenceBinding = referenceBindings.iterator().next();
 		EntityBinding referencedEntityBinding = referenceBinding.getReferencedEntityBinding();
 		// TODO - Is this assertion correct (HF)?
 		assertEquals( "Should be the same entity binding", referencedEntityBinding, simpleEntityBinding );
 
 		EntityBinding entityWithManyToOneBinding = metadata.getEntityBinding( ManyToOneEntity.class.getName() );
 		Iterator<EntityReferencingAttributeBinding> it = entityWithManyToOneBinding.getEntityReferencingAttributeBindings()
 				.iterator();
 		assertTrue( it.hasNext() );
 		assertSame( entityWithManyToOneBinding.getAttributeBinding( "simpleEntity" ), it.next() );
 		assertFalse( it.hasNext() );
 	}
 
 	public abstract MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
 
 	public abstract MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources);
 
 	public abstract MetadataImpl addSourcesForManyToOne(MetadataSources sources);
 
 	protected void assertIdAndSimpleProperty(EntityBinding entityBinding) {
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getEntityIdentifier() );
 		assertNotNull( entityBinding.getEntityIdentifier().getValueBinding() );
-		assertTrue( entityBinding.isRoot() );
 
 		AttributeBinding idAttributeBinding = entityBinding.getAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getEntityIdentifier().getValueBinding() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		assertNotNull( idAttributeBinding.getValue() );
 		assertTrue( idAttributeBinding.getValue() instanceof Column );
 
 		AttributeBinding nameBinding = entityBinding.getAttributeBinding( "name" );
 		assertNotNull( nameBinding );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 	}
+
+	protected void assertRoot(MetadataImplementor metadata, EntityBinding entityBinding) {
+		assertTrue( entityBinding.isRoot() );
+		assertSame( entityBinding, metadata.getRootEntityBinding( entityBinding.getEntity().getName() ) );
+	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
index 6ff3fc50c6..787968c82f 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
@@ -1,62 +1,70 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import org.junit.After;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Hardy Ferentschik
  */
 public abstract class BaseAnnotationBindingTestCase extends BaseUnitTestCase {
 	protected MetadataSources sources;
 	protected MetadataImpl meta;
 
 	@After
 	public void tearDown() {
 		sources = null;
 		meta = null;
 	}
 
 	public void buildMetadataSources(Class<?>... classes) {
 		sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		for ( Class clazz : classes ) {
 			sources.addAnnotatedClass( clazz );
 		}
 	}
 
 	public EntityBinding getEntityBinding(Class<?> clazz) {
 		if ( meta == null ) {
 			meta = (MetadataImpl) sources.buildMetadata();
 		}
 		return meta.getEntityBinding( clazz.getName() );
 	}
+
+	public EntityBinding getRootEntityBinding(Class<?> clazz) {
+		if ( meta == null ) {
+			meta = (MetadataImpl) sources.buildMetadata();
+		}
+		return meta.getRootEntityBinding( clazz.getName() );
+	}
+
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceTypeTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceTypeTest.java
index cf914916aa..9fc6b785ea 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceTypeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/InheritanceTypeTest.java
@@ -1,69 +1,88 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.EntityBinding;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertNull;
+import static junit.framework.Assert.assertSame;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class InheritanceTypeTest extends BaseAnnotationBindingTestCase {
 	@Test
 	public void testNoInheritance() {
 		buildMetadataSources( SingleEntity.class );
 		EntityBinding entityBinding = getEntityBinding( SingleEntity.class );
-		assertTrue( entityBinding.isRoot() );
 		assertNull( entityBinding.getEntityDiscriminator() );
 	}
 
 	@Test
 	public void testDiscriminatorValue() {
 		buildMetadataSources(
 				RootOfSingleTableInheritance.class, SubclassOfSingleTableInheritance.class
 		);
 		EntityBinding entityBinding = getEntityBinding( SubclassOfSingleTableInheritance.class );
-		assertFalse( entityBinding.isRoot() );
 		assertEquals( "Wrong discriminator value", "foo", entityBinding.getDiscriminatorValue() );
 	}
 
+	@Test
+	public void testRootEntityBinding() {
+		buildMetadataSources(
+				SubclassOfSingleTableInheritance.class, SingleEntity.class, RootOfSingleTableInheritance.class
+		);
+
+		EntityBinding noInheritanceEntityBinding = getEntityBinding( SingleEntity.class );
+		EntityBinding subclassEntityBinding = getEntityBinding( SubclassOfSingleTableInheritance.class );
+		EntityBinding rootEntityBinding = getEntityBinding( RootOfSingleTableInheritance.class );
+
+		assertTrue( noInheritanceEntityBinding.isRoot() );
+		assertSame( noInheritanceEntityBinding, getRootEntityBinding( SingleEntity.class ) );
+
+		assertFalse( subclassEntityBinding.isRoot() );
+		assertSame( rootEntityBinding, getRootEntityBinding( SubclassOfSingleTableInheritance.class ) );
+
+		assertTrue( rootEntityBinding.isRoot() );
+		assertSame( rootEntityBinding, getRootEntityBinding( RootOfSingleTableInheritance.class ));
+	}
+
 	@Entity
 	class SingleEntity {
 		@Id
 		@GeneratedValue
 		private int id;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
index 607e295299..dd35e2836d 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
@@ -1,93 +1,113 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.util.Iterator;
 
 import org.junit.Test;
 
+import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
+import org.hibernate.SessionFactory;
+import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.FetchProfile;
+import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
+import static junit.framework.Assert.assertNotNull;
+import static junit.framework.Assert.assertSame;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class MetadataImplTest extends BaseUnitTestCase {
 
 	@Test(expected = IllegalArgumentException.class)
 	public void testAddingNullClass() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addClass( null );
 		sources.buildMetadata();
 	}
 
 	@Test(expected = IllegalArgumentException.class)
 	public void testAddingNullPackageName() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( null );
 		sources.buildMetadata();
 	}
 
 	@Test(expected = HibernateException.class)
 	public void testAddingNonExistingPackageName() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( "not.a.package" );
 		sources.buildMetadata();
 	}
 
 	@Test
 	public void testAddingPackageName() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( "org.hibernate.metamodel.source.internal" );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		assertFetchProfile( metadata );
 	}
 
 	@Test
 	public void testAddingPackageNameWithTrailingDot() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( "org.hibernate.metamodel.source.internal." );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		assertFetchProfile( metadata );
 	}
 
+	@Test
+	public void testGettingSessionFactoryBuilder() {
+		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
+		Metadata metadata = sources.buildMetadata();
+
+		SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
+		assertNotNull( sessionFactoryBuilder );
+		assertTrue( SessionFactoryBuilderImpl.class.isInstance( sessionFactoryBuilder ) );
+
+		SessionFactory sessionFactory = metadata.buildSessionFactory();
+		assertNotNull( sessionFactory );
+	}
+
 	private void assertFetchProfile(MetadataImpl metadata) {
 		Iterator<FetchProfile> profiles = metadata.getFetchProfiles().iterator();
 		assertTrue( profiles.hasNext() );
 		FetchProfile profile = profiles.next();
 		assertEquals( "wrong profile name", "package-configured-profile", profile.getName() );
 		assertFalse( profiles.hasNext() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
new file mode 100644
index 0000000000..8135c53c6c
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
@@ -0,0 +1,194 @@
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
+package org.hibernate.metamodel.source.internal;
+
+import java.io.Serializable;
+import java.util.Iterator;
+
+import org.junit.Test;
+
+import org.hibernate.CallbackException;
+import org.hibernate.EmptyInterceptor;
+import org.hibernate.EntityMode;
+import org.hibernate.Interceptor;
+import org.hibernate.ObjectNotFoundException;
+import org.hibernate.SessionFactory;
+import org.hibernate.Transaction;
+import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.SessionFactoryBuilder;
+import org.hibernate.proxy.EntityNotFoundDelegate;
+import org.hibernate.service.ServiceRegistryBuilder;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.hibernate.type.Type;
+
+import static junit.framework.Assert.assertNotNull;
+import static junit.framework.Assert.assertSame;
+import static junit.framework.Assert.assertTrue;
+
+/**
+ * @author Gail Badner
+ */
+public class SessionFactoryBuilderImplTest extends BaseUnitTestCase {
+
+	@Test
+	public void testGettingSessionFactoryBuilder() {
+		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
+		assertNotNull( sessionFactoryBuilder );
+		assertTrue( SessionFactoryBuilderImpl.class.isInstance( sessionFactoryBuilder ) );
+	}
+
+	@Test
+	public void testBuildSessionFactoryWithDefaultOptions() {
+		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
+		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
+		assertSame( EmptyInterceptor.INSTANCE, sessionFactory.getSessionFactoryOptions().getInterceptor() );
+		assertTrue( EntityNotFoundDelegate.class.isInstance(
+				sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate()
+		) );
+	}
+
+	@Test
+	public void testBuildSessionFactoryWithUpdatedOptions() {
+		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
+		Interceptor interceptor = new AnInterceptor();
+		EntityNotFoundDelegate entityNotFoundDelegate = new EntityNotFoundDelegate() {
+			@Override
+			public void handleEntityNotFound(String entityName, Serializable id) {
+				throw new ObjectNotFoundException( id, entityName );
+			}
+		};
+		sessionFactoryBuilder.with( interceptor );
+		sessionFactoryBuilder.with( entityNotFoundDelegate );
+		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
+		assertSame( interceptor, sessionFactory.getSessionFactoryOptions().getInterceptor() );
+		assertSame( entityNotFoundDelegate, sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate() );
+	}
+
+	private SessionFactoryBuilder getSessionFactoryBuilder() {
+		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
+		return  metadata.getSessionFactoryBuilder();
+	}
+
+	private static class AnInterceptor implements Interceptor {
+		private static final Interceptor INSTANCE = EmptyInterceptor.INSTANCE;
+
+		@Override
+		public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
+				throws CallbackException {
+			return INSTANCE.onLoad( entity, id, state, propertyNames, types );
+		}
+
+		@Override
+		public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types)
+				throws CallbackException {
+			return INSTANCE.onFlushDirty( entity, id, currentState, previousState, propertyNames, types );
+		}
+
+		@Override
+		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
+				throws CallbackException {
+			return INSTANCE.onSave( entity, id, state, propertyNames, types );
+		}
+
+		@Override
+		public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
+				throws CallbackException {
+			INSTANCE.onDelete( entity, id, state, propertyNames, types );
+		}
+
+		@Override
+		public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
+			INSTANCE.onCollectionRecreate( collection, key );
+		}
+
+		@Override
+		public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
+			INSTANCE.onCollectionRemove( collection, key );
+		}
+
+		@Override
+		public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
+			INSTANCE.onCollectionUpdate( collection, key );
+		}
+
+		@Override
+		public void preFlush(Iterator entities) throws CallbackException {
+			INSTANCE.preFlush( entities );
+		}
+
+		@Override
+		public void postFlush(Iterator entities) throws CallbackException {
+			INSTANCE.postFlush( entities );
+		}
+
+		@Override
+		public Boolean isTransient(Object entity) {
+			return INSTANCE.isTransient( entity );
+		}
+
+		@Override
+		public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
+			return INSTANCE.findDirty( entity, id, currentState, previousState, propertyNames, types );
+		}
+
+		@Override
+		public Object instantiate(String entityName, EntityMode entityMode, Serializable id)
+				throws CallbackException {
+			return INSTANCE.instantiate( entityName, entityMode, id );
+		}
+
+		@Override
+		public String getEntityName(Object object) throws CallbackException {
+			return INSTANCE.getEntityName( object );
+		}
+
+		@Override
+		public Object getEntity(String entityName, Serializable id) throws CallbackException {
+			return INSTANCE.getEntity( entityName, id );
+		}
+
+		@Override
+		public void afterTransactionBegin(Transaction tx) {
+			INSTANCE.afterTransactionBegin( tx );
+		}
+
+		@Override
+		public void beforeTransactionCompletion(Transaction tx) {
+			INSTANCE.beforeTransactionCompletion( tx );
+		}
+
+		@Override
+		public void afterTransactionCompletion(Transaction tx) {
+			INSTANCE.afterTransactionCompletion( tx );
+		}
+
+		@Override
+		public String onPrepareStatement(String sql) {
+			return INSTANCE.onPrepareStatement( sql );
+		}
+	}
+}
+
+
