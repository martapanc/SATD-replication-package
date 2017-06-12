diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/RegionFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/RegionFactoryInitiator.java
index ffa33b8e72..4a934b6ad2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/RegionFactoryInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/RegionFactoryInitiator.java
@@ -1,85 +1,103 @@
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
 package org.hibernate.cache.internal;
 
 import java.util.Map;
 
 import org.hibernate.boot.registry.StandardServiceInitiator;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cache.spi.RegionFactory;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.jboss.logging.Logger;
 
 /**
  * Initiator for the {@link RegionFactory} service.
- *
+ * 
  * @author Hardy Ferentschik
+ * @author Brett Meyer
  */
 public class RegionFactoryInitiator implements StandardServiceInitiator<RegionFactory> {
+
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class,
+			RegionFactoryInitiator.class.getName() );
+
 	/**
 	 * Singleton access
 	 */
 	public static final RegionFactoryInitiator INSTANCE = new RegionFactoryInitiator();
 
-	/**
-	 * Property name to use to configure the full qualified class name for the {@code RegionFactory}
-	 */
-	public static final String IMPL_NAME = "hibernate.cache.region.factory_class";
-
 	@Override
 	public Class<RegionFactory> getServiceInitiated() {
 		return RegionFactory.class;
 	}
 
 	@Override
-	@SuppressWarnings( { "unchecked" })
+	@SuppressWarnings({ "unchecked" })
 	public RegionFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
-		final Object setting = configurationValues.get( IMPL_NAME );
-		return registry.getService( StrategySelector.class ).resolveDefaultableStrategy(
-				RegionFactory.class,
-				setting,
-				NoCachingRegionFactory.INSTANCE
-		);
+		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( AvailableSettings.USE_SECOND_LEVEL_CACHE,
+				configurationValues, true );
+		boolean useQueryCache = ConfigurationHelper.getBoolean( AvailableSettings.USE_QUERY_CACHE, configurationValues );
+
+		RegionFactory regionFactory;
+
+		// The cache provider is needed when we either have second-level cache enabled
+		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
+		if ( useSecondLevelCache || useQueryCache ) {
+			final Object setting = configurationValues.get( AvailableSettings.CACHE_REGION_FACTORY );
+			regionFactory = registry.getService( StrategySelector.class ).resolveDefaultableStrategy(
+					RegionFactory.class, setting, NoCachingRegionFactory.INSTANCE );
+		}
+		else {
+			regionFactory = NoCachingRegionFactory.INSTANCE;
+		}
+
+		LOG.debugf( "Cache region factory : %s", regionFactory.getClass().getName() );
+
+		return regionFactory;
 	}
 
 	/**
 	 * Map legacy names unto the new corollary.
 	 *
-	 * todo this shouldn't be public, nor really static.  hack for org.hibernate.cfg.SettingsFactory.createRegionFactory()
+	 * TODO: temporary hack for org.hibernate.cfg.SettingsFactory.createRegionFactory()
 	 *
 	 * @param name The (possibly legacy) factory name
 	 *
 	 * @return The factory name to use.
 	 */
 	public static String mapLegacyNames(final String name) {
 		if ( "org.hibernate.cache.EhCacheRegionFactory".equals( name ) ) {
 			return "org.hibernate.cache.ehcache.EhCacheRegionFactory";
 		}
 
 		if ( "org.hibernate.cache.SingletonEhCacheRegionFactory".equals( name ) ) {
 			return "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory";
 		}
 
 		return name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 67209c644b..7219d08948 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,520 +1,485 @@
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
 import org.hibernate.NullPrecedence;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
 	private static final long serialVersionUID = -1194386144994524825L;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	public SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final boolean debugEnabled =  LOG.isDebugEnabled();
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty( AvailableSettings.SESSION_FACTORY_NAME );
 		settings.setSessionFactoryName( sessionFactoryName );
 		settings.setSessionFactoryNameAlsoJndiName(
 				ConfigurationHelper.getBoolean( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, props, true )
 		);
 
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
 
 		MultiTableBulkIdStrategy multiTableBulkIdStrategy = serviceRegistry.getService( StrategySelector.class )
 				.resolveStrategy(
 						MultiTableBulkIdStrategy.class,
 						properties.getProperty( AvailableSettings.HQL_BULK_ID_STRATEGY )
 				);
 		if ( multiTableBulkIdStrategy == null ) {
 			multiTableBulkIdStrategy = jdbcServices.getDialect().supportsTemporaryTables()
 					? TemporaryTableBulkIdStrategy.INSTANCE
 					: new PersistentTableBulkIdStrategy();
 		}
 		settings.setMultiTableBulkIdStrategy( multiTableBulkIdStrategy );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(AvailableSettings.FLUSH_BEFORE_COMPLETION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		}
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(AvailableSettings.AUTO_CLOSE_SESSION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		}
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(AvailableSettings.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(AvailableSettings.BATCH_VERSIONED_DATA, properties, false);
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				AvailableSettings.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		}
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(AvailableSettings.WRAP_RESULT_SETS, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		}
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(AvailableSettings.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		if ( debugEnabled ) {
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		}
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(AvailableSettings.STATEMENT_FETCH_SIZE, properties);
 		if ( statementFetchSize != null && debugEnabled ) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		}
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 		String releaseModeName = ConfigurationHelper.getString( AvailableSettings.RELEASE_CONNECTIONS, properties, "auto" );
 		if ( debugEnabled ) {
 			LOG.debugf( "Connection release mode: %s", releaseModeName );
 		}
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
 				// we need to make sure the underlying JDBC connection access supports aggressive release...
 				boolean supportsAgrressiveRelease = multiTenancyStrategy.requiresMultiTenantConnectionProvider()
 						? serviceRegistry.getService( MultiTenantConnectionProvider.class ).supportsAggressiveRelease()
 						: serviceRegistry.getService( ConnectionProvider.class ).supportsAggressiveRelease();
 				if ( ! supportsAgrressiveRelease ) {
 					LOG.unsupportedAfterStatement();
 					releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 				}
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		final BatchFetchStyle batchFetchStyle = BatchFetchStyle.interpret( properties.get( AvailableSettings.BATCH_FETCH_STYLE ) );
 		LOG.debugf( "Using BatchFetchStyle : " + batchFetchStyle.name() );
 		settings.setBatchFetchStyle( batchFetchStyle );
 
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( AvailableSettings.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( AvailableSettings.DEFAULT_CATALOG );
 		if ( defaultSchema != null && debugEnabled ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
 		if ( defaultCatalog != null && debugEnabled ) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( AvailableSettings.MAX_FETCH_DEPTH, properties );
 		if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		}
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( AvailableSettings.USE_SQL_COMMENTS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		}
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( AvailableSettings.ORDER_UPDATES, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		}
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(AvailableSettings.ORDER_INSERTS, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		}
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		String defaultNullPrecedence = ConfigurationHelper.getString(
 				AvailableSettings.DEFAULT_NULL_ORDERING, properties, "none", "first", "last"
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default null ordering: %s", defaultNullPrecedence );
 		}
 		settings.setDefaultNullPrecedence( NullPrecedence.parse( defaultNullPrecedence ) );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap( AvailableSettings.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		}
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( AvailableSettings.JPAQL_STRICT_COMPLIANCE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		}
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( AvailableSettings.USE_SECOND_LEVEL_CACHE, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		}
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(AvailableSettings.USE_QUERY_CACHE, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		}
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
-		// The cache provider is needed when we either have second-level cache enabled
-		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
-		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
+		settings.setRegionFactory( serviceRegistry.getService( RegionFactory.class ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				AvailableSettings.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		}
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( AvailableSettings.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
 		if ( prefix != null && debugEnabled ) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( AvailableSettings.USE_STRUCTURED_CACHE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		}
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 		boolean useDirectReferenceCacheEntries = ConfigurationHelper.getBoolean(
 				AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES,
 				properties,
 				false
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache direct-reference entries: %s", enabledDisabled(useDirectReferenceCacheEntries) );
 		}
 		settings.setDirectReferenceCacheEntriesEnabled( useDirectReferenceCacheEntries );
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( AvailableSettings.GENERATE_STATISTICS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		}
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( AvailableSettings.USE_IDENTIFIER_ROLLBACK, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		}
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( AvailableSettings.HBM2DDL_AUTO );
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
 		settings.setImportFiles( properties.getProperty( AvailableSettings.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( AvailableSettings.DEFAULT_ENTITY_MODE ) );
 		if ( debugEnabled ) {
 			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		}
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( AvailableSettings.QUERY_STARTUP_CHECKING, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		}
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(AvailableSettings.CHECK_NULLABILITY, properties, true);
 		if ( debugEnabled ) {
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		}
 		settings.setCheckNullability(checkNullability);
 
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
 //		String provider = properties.getProperty( AvailableSettings.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		boolean initializeLazyStateOutsideTransactionsEnabled = ConfigurationHelper.getBoolean(
 				AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS,
 				properties,
 				false
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Allow initialization of lazy state outside session : : %s", enabledDisabled( initializeLazyStateOutsideTransactionsEnabled ) );
 		}
 		settings.setInitializeLazyStateOutsideTransactions( initializeLazyStateOutsideTransactionsEnabled );
 
 		boolean jtaTrackByThread = ConfigurationHelper.getBoolean(
 				AvailableSettings.JTA_TRACK_BY_THREAD,
 				properties,
 				true
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "JTA Track by Thread: %s", enabledDisabled(jtaTrackByThread) );
 		}
 		settings.setJtaTrackByThread( jtaTrackByThread );
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debug("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				AvailableSettings.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
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
-
-	private static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
-		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
-				ConfigurationHelper.getString(
-						AvailableSettings.CACHE_REGION_FACTORY, properties, null
-				)
-		);
-		if ( regionFactoryClassName == null || !cachingEnabled) {
-			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
-		}
-		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
-		try {
-			try {
-				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
-						.classForName( regionFactoryClassName )
-						.getConstructor( Properties.class )
-						.newInstance( properties );
-			}
-			catch ( NoSuchMethodException e ) {
-				// no constructor accepting Properties found, try no arg constructor
-				LOG.debugf(
-						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
-						regionFactoryClassName
-				);
-				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
-						.classForName( regionFactoryClassName )
-						.newInstance();
-			}
-		}
-		catch ( Exception e ) {
-			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
-		}
-	}
 	//todo remove this once we move to new metamodel
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
 						AvailableSettings.CACHE_REGION_FACTORY, properties, null
 				)
 		);
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
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String className = ConfigurationHelper.getString(
 				AvailableSettings.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
 		);
 		LOG.debugf( "Query translator: %s", className );
 		try {
 			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( className )
 					.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/AbstractEhcacheRegionFactory.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/AbstractEhcacheRegionFactory.java
index b12a88e188..b935309000 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/AbstractEhcacheRegionFactory.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/AbstractEhcacheRegionFactory.java
@@ -1,234 +1,243 @@
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
 package org.hibernate.cache.ehcache;
 
+import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Properties;
 
 import net.sf.ehcache.CacheManager;
 import net.sf.ehcache.Ehcache;
 import net.sf.ehcache.util.ClassLoaderUtil;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.nonstop.NonstopAccessStrategyFactory;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheCollectionRegion;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheEntityRegion;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheQueryResultsRegion;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheTimestampsRegion;
 import org.hibernate.cache.ehcache.internal.strategy.EhcacheAccessStrategyFactory;
 import org.hibernate.cache.ehcache.internal.strategy.EhcacheAccessStrategyFactoryImpl;
 import org.hibernate.cache.ehcache.internal.util.HibernateEhcacheUtils;
 import org.hibernate.cache.ehcache.management.impl.ProviderMBeanRegistrationHelper;
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.TimestampsRegion;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.Settings;
 import org.hibernate.service.spi.InjectService;
 
 /**
  * Abstract implementation of an Ehcache specific RegionFactory.
  *
  * @author Chris Dennis
  * @author Greg Luck
  * @author Emmanuel Bernard
  * @author Abhishek Sanoujam
  * @author Alex Snaps
  */
 abstract class AbstractEhcacheRegionFactory implements RegionFactory {
 
 	/**
 	 * The Hibernate system property specifying the location of the ehcache configuration file name.
 	 * <p/>
 	 * If not set, ehcache.xml will be looked for in the root of the classpath.
 	 * <p/>
 	 * If set to say ehcache-1.xml, ehcache-1.xml will be looked for in the root of the classpath.
 	 */
 	public static final String NET_SF_EHCACHE_CONFIGURATION_RESOURCE_NAME = "net.sf.ehcache.configurationResourceName";
 
 	private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(
 			EhCacheMessageLogger.class,
 			AbstractEhcacheRegionFactory.class.getName()
 	);
 
 	/**
 	 * MBean registration helper class instance for Ehcache Hibernate MBeans.
 	 */
 	protected final ProviderMBeanRegistrationHelper mbeanRegistrationHelper = new ProviderMBeanRegistrationHelper();
 
 	/**
 	 * Ehcache CacheManager that supplied Ehcache instances for this Hibernate RegionFactory.
 	 */
 	protected volatile CacheManager manager;
 
 	/**
 	 * Settings object for the Hibernate persistence unit.
 	 */
 	protected Settings settings;
 
 	/**
 	 * {@link EhcacheAccessStrategyFactory} for creating various access strategies
 	 */
 	protected final EhcacheAccessStrategyFactory accessStrategyFactory =
 			new NonstopAccessStrategyFactory( new EhcacheAccessStrategyFactoryImpl() );
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * In Ehcache we default to minimal puts since this should have minimal to no
 	 * affect on unclustered users, and has great benefit for clustered users.
 	 *
 	 * @return true, optimize for minimal puts
 	 */
 	@Override
 	public boolean isMinimalPutsEnabledByDefault() {
 
 		return true;
 	}
 
 	@Override
 	public long nextTimestamp() {
 		return net.sf.ehcache.util.Timestamper.next();
 	}
 
 	@Override
 	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException {
 		return new EhcacheEntityRegion( accessStrategyFactory, getCache( regionName ), settings, metadata, properties );
 	}
 
 	@Override
 	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException {
 		return new EhcacheNaturalIdRegion(
 				accessStrategyFactory,
 				getCache( regionName ),
 				settings,
 				metadata,
 				properties
 		);
 	}
 
 	@Override
 	public CollectionRegion buildCollectionRegion(
 			String regionName,
 			Properties properties,
 			CacheDataDescription metadata)
 			throws CacheException {
 		return new EhcacheCollectionRegion(
 				accessStrategyFactory,
 				getCache( regionName ),
 				settings,
 				metadata,
 				properties
 		);
 	}
 
 	@Override
 	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
 		return new EhcacheQueryResultsRegion( accessStrategyFactory, getCache( regionName ), properties );
 	}
 
 	@InjectService
 	@SuppressWarnings("UnusedDeclaration")
 	public void setClassLoaderService(ClassLoaderService classLoaderService) {
 		this.classLoaderService = classLoaderService;
 	}
 
 	private ClassLoaderService classLoaderService;
 
 	@Override
 	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
 		return new EhcacheTimestampsRegion( accessStrategyFactory, getCache( regionName ), properties );
 	}
 
 	private Ehcache getCache(String name) throws CacheException {
 		try {
 			Ehcache cache = manager.getEhcache( name );
 			if ( cache == null ) {
 				LOG.unableToFindEhCacheConfiguration( name );
 				manager.addCache( name );
 				cache = manager.getEhcache( name );
 				LOG.debug( "started EHCache region: " + name );
 			}
 			HibernateEhcacheUtils.validateEhcache( cache );
 			return cache;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 
 	}
 
 	/**
 	 * Load a resource from the classpath.
 	 */
 	protected URL loadResource(String configurationResourceName) {
 		URL url = null;
 		if ( classLoaderService != null ) {
 			url = classLoaderService.locateResource( configurationResourceName );
 		}
 		if ( url == null ) {
 			final ClassLoader standardClassloader = ClassLoaderUtil.getStandardClassLoader();
 			if ( standardClassloader != null ) {
 				url = standardClassloader.getResource( configurationResourceName );
 			}
 			if ( url == null ) {
 				url = AbstractEhcacheRegionFactory.class.getResource( configurationResourceName );
 			}
+			if ( url == null ) {
+				try {
+					url = new URL( configurationResourceName );
+				}
+				catch ( MalformedURLException e ) {
+					// ignore
+				}
+			}
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Creating EhCacheRegionFactory from a specified resource: %s.  Resolved to URL: %s",
 					configurationResourceName,
 					url
 			);
 		}
 		if ( url == null ) {
 
 			LOG.unableToLoadConfiguration( configurationResourceName );
 		}
 		return url;
 	}
 
 	/**
 	 * Default access-type used when the configured using JPA 2.0 config.  JPA 2.0 allows <code>@Cacheable(true)</code> to be attached to an
 	 * entity without any access type or usage qualification.
 	 * <p/>
 	 * We are conservative here in specifying {@link AccessType#READ_WRITE} so as to follow the mantra of "do no harm".
 	 * <p/>
 	 * This is a Hibernate 3.5 method.
 	 */
 	public AccessType getDefaultAccessType() {
 		return AccessType.READ_WRITE;
 	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/EhCacheRegionFactory.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/EhCacheRegionFactory.java
index 6b28b6ee25..6dca8d5dae 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/EhCacheRegionFactory.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/EhCacheRegionFactory.java
@@ -1,135 +1,129 @@
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
 package org.hibernate.cache.ehcache;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Properties;
 
 import net.sf.ehcache.CacheManager;
 import net.sf.ehcache.config.Configuration;
 import net.sf.ehcache.config.ConfigurationFactory;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.util.HibernateEhcacheUtils;
 import org.hibernate.cfg.Settings;
 
 /**
  * A non-singleton EhCacheRegionFactory implementation.
  *
  * @author Chris Dennis
  * @author Greg Luck
  * @author Emmanuel Bernard
  * @author Abhishek Sanoujam
  * @author Alex Snaps
  */
 public class EhCacheRegionFactory extends AbstractEhcacheRegionFactory {
 
 	private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(
 			EhCacheMessageLogger.class,
 			EhCacheRegionFactory.class.getName()
 	);
 
 
 	/**
 	 * Creates a non-singleton EhCacheRegionFactory
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public EhCacheRegionFactory() {
 	}
 
 	/**
 	 * Creates a non-singleton EhCacheRegionFactory
 	 *
 	 * @param prop Not used
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public EhCacheRegionFactory(Properties prop) {
 		super();
 	}
 
 	@Override
 	public void start(Settings settings, Properties properties) throws CacheException {
 		this.settings = settings;
 		if ( manager != null ) {
 			LOG.attemptToRestartAlreadyStartedEhCacheProvider();
 			return;
 		}
 
 		try {
 			String configurationResourceName = null;
 			if ( properties != null ) {
 				configurationResourceName = (String) properties.get( NET_SF_EHCACHE_CONFIGURATION_RESOURCE_NAME );
 			}
 			if ( configurationResourceName == null || configurationResourceName.length() == 0 ) {
 				final Configuration configuration = ConfigurationFactory.parseConfiguration();
 				manager = new CacheManager( configuration );
 			}
 			else {
-				URL url;
-				try {
-					url = new URL( configurationResourceName );
-				}
-				catch (MalformedURLException e) {
-					url = loadResource( configurationResourceName );
-				}
+				URL url = loadResource( configurationResourceName );
 				final Configuration configuration = HibernateEhcacheUtils.loadAndCorrectConfiguration( url );
 				manager = new CacheManager( configuration );
 			}
 			mbeanRegistrationHelper.registerMBean( manager, properties );
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			if ( e.getMessage().startsWith(
 					"Cannot parseConfiguration CacheManager. Attempt to create a new instance of " +
 							"CacheManager using the diskStorePath"
 			) ) {
 				throw new CacheException(
 						"Attempt to restart an already started EhCacheRegionFactory. " +
 								"Use sessionFactory.close() between repeated calls to buildSessionFactory. " +
 								"Consider using SingletonEhCacheRegionFactory. Error from ehcache was: " + e.getMessage()
 				);
 			}
 			else {
 				throw new CacheException( e );
 			}
 		}
 	}
 
 	@Override
 	public void stop() {
 		try {
 			if ( manager != null ) {
 				mbeanRegistrationHelper.unregisterMBean();
 				manager.shutdown();
 				manager = null;
 			}
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/StrategyRegistrationProviderImpl.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/StrategyRegistrationProviderImpl.java
index 3cedfe4f0a..2c68ca1df8 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/StrategyRegistrationProviderImpl.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/StrategyRegistrationProviderImpl.java
@@ -1,70 +1,72 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.ehcache;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
 import org.hibernate.boot.registry.selector.StrategyRegistration;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.cache.spi.RegionFactory;
 
 /**
  * Makes the 2 contained region factory implementations available to the Hibernate
  * {@link org.hibernate.boot.registry.selector.spi.StrategySelector} service.
  *
  * @author Steve Ebersole
  */
 public class StrategyRegistrationProviderImpl implements StrategyRegistrationProvider {
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterable<StrategyRegistration> getStrategyRegistrations() {
 		final List<StrategyRegistration> strategyRegistrations = new ArrayList<StrategyRegistration>();
 
 		strategyRegistrations.add(
 				new SimpleStrategyRegistrationImpl(
 						RegionFactory.class,
 						EhCacheRegionFactory.class,
 						"ehcache",
+						EhCacheRegionFactory.class.getName(),
 						EhCacheRegionFactory.class.getSimpleName(),
 						// legacy impl class name
 						"org.hibernate.cache.EhCacheRegionFactory"
 				)
 		);
 
 		strategyRegistrations.add(
 				new SimpleStrategyRegistrationImpl(
 						RegionFactory.class,
 						SingletonEhCacheRegionFactory.class,
 						"ehcache-singleton",
+						SingletonEhCacheRegionFactory.class.getName(),
 						SingletonEhCacheRegionFactory.class.getSimpleName(),
 						// legacy impl class name
 						"org.hibernate.cache.SingletonEhCacheRegionFactory"
 				)
 		);
 
 		return strategyRegistrations;
 	}
 }
diff --git a/hibernate-ehcache/src/main/resources/OSGI-INF/blueprint/blueprint.xml b/hibernate-ehcache/src/main/resources/OSGI-INF/blueprint/blueprint.xml
new file mode 100644
index 0000000000..fbbb6739e6
--- /dev/null
+++ b/hibernate-ehcache/src/main/resources/OSGI-INF/blueprint/blueprint.xml
@@ -0,0 +1,10 @@
+<?xml version="1.0" encoding="UTF-8"?>
+
+<blueprint  default-activation="eager" 
+            xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
+            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
+
+  <bean id="strategyRegistrationProvider" class="org.hibernate.cache.ehcache.StrategyRegistrationProviderImpl"/>
+  <service ref="strategyRegistrationProvider" interface="org.hibernate.boot.registry.selector.StrategyRegistrationProvider"/>
+  
+</blueprint>
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
index 3a35625ec0..c1012cb785 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
@@ -1,601 +1,612 @@
 package org.hibernate.cache.infinispan;
 
+import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.impl.BaseRegion;
 import org.hibernate.cache.infinispan.naturalid.NaturalIdRegionImpl;
 import org.hibernate.cache.infinispan.query.QueryResultsRegionImpl;
 import org.hibernate.cache.infinispan.timestamp.ClusteredTimestampsRegionImpl;
 import org.hibernate.cache.infinispan.timestamp.TimestampTypeOverrides;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.infinispan.tm.HibernateTransactionManagerLookup;
 import org.hibernate.cache.infinispan.util.CacheCommandFactory;
 import org.hibernate.cache.infinispan.util.Caches;
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.TimestampsRegion;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.Settings;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.infinispan.AdvancedCache;
 import org.infinispan.commands.module.ModuleCommandFactory;
 import org.infinispan.configuration.cache.CacheMode;
 import org.infinispan.configuration.cache.Configuration;
 import org.infinispan.configuration.cache.ConfigurationBuilder;
 import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
 import org.infinispan.configuration.parsing.ParserRegistry;
 import org.infinispan.factories.GlobalComponentRegistry;
 import org.infinispan.manager.DefaultCacheManager;
 import org.infinispan.manager.EmbeddedCacheManager;
 import org.infinispan.transaction.TransactionMode;
 import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
 import org.infinispan.util.FileLookupFactory;
 import org.infinispan.util.concurrent.IsolationLevel;
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 
 /**
  * A {@link RegionFactory} for <a href="http://www.jboss.org/infinispan">Infinispan</a>-backed cache
  * regions.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class InfinispanRegionFactory implements RegionFactory {
 
    private static final Log log = LogFactory.getLog(InfinispanRegionFactory.class);
 
    private static final String PREFIX = "hibernate.cache.infinispan.";
 
    private static final String CONFIG_SUFFIX = ".cfg";
 
    private static final String STRATEGY_SUFFIX = ".eviction.strategy";
 
    private static final String WAKE_UP_INTERVAL_SUFFIX = ".eviction.wake_up_interval";
 
    private static final String MAX_ENTRIES_SUFFIX = ".eviction.max_entries";
 
    private static final String LIFESPAN_SUFFIX = ".expiration.lifespan";
 
    private static final String MAX_IDLE_SUFFIX = ".expiration.max_idle";
 
 //   private static final String STATISTICS_SUFFIX = ".statistics";
 
    /** 
     * Classpath or filesystem resource containing Infinispan configurations the factory should use.
     * 
     * @see #DEF_INFINISPAN_CONFIG_RESOURCE
     */
    public static final String INFINISPAN_CONFIG_RESOURCE_PROP = "hibernate.cache.infinispan.cfg";
 
    public static final String INFINISPAN_GLOBAL_STATISTICS_PROP = "hibernate.cache.infinispan.statistics";
 
    /**
     * Property that controls whether Infinispan should interact with the
     * transaction manager as a {@link javax.transaction.Synchronization} or as
     * an XA resource. If the property is set to true, it will be a
     * synchronization, otherwise an XA resource.
     *
     * @see #DEF_USE_SYNCHRONIZATION
     */
    public static final String INFINISPAN_USE_SYNCHRONIZATION_PROP = "hibernate.cache.infinispan.use_synchronization";
    
 	private static final String NATURAL_ID_KEY = "naturalid";
 
 	/**
 	 * Name of the configuration that should be used for natural id caches.
 	 *
 	 * @see #DEF_ENTITY_RESOURCE
 	 */
 	public static final String NATURAL_ID_CACHE_RESOURCE_PROP = PREFIX + NATURAL_ID_KEY + CONFIG_SUFFIX;
 
    private static final String ENTITY_KEY = "entity";
    
    /**
     * Name of the configuration that should be used for entity caches.
     * 
     * @see #DEF_ENTITY_RESOURCE
     */
    public static final String ENTITY_CACHE_RESOURCE_PROP = PREFIX + ENTITY_KEY + CONFIG_SUFFIX;
    
    private static final String COLLECTION_KEY = "collection";
    
    /**
     * Name of the configuration that should be used for collection caches.
     * No default value, as by default we try to use the same Infinispan cache
     * instance we use for entity caching.
     * 
     * @see #ENTITY_CACHE_RESOURCE_PROP
     * @see #DEF_ENTITY_RESOURCE
     */
    public static final String COLLECTION_CACHE_RESOURCE_PROP = PREFIX + COLLECTION_KEY + CONFIG_SUFFIX;
 
    private static final String TIMESTAMPS_KEY = "timestamps";
 
    /**
     * Name of the configuration that should be used for timestamp caches.
     * 
     * @see #DEF_TIMESTAMPS_RESOURCE
     */
    public static final String TIMESTAMPS_CACHE_RESOURCE_PROP = PREFIX + TIMESTAMPS_KEY + CONFIG_SUFFIX;
 
    private static final String QUERY_KEY = "query";
 
    /**
     * Name of the configuration that should be used for query caches.
     * 
     * @see #DEF_QUERY_RESOURCE
     */
    public static final String QUERY_CACHE_RESOURCE_PROP = PREFIX + QUERY_KEY + CONFIG_SUFFIX;
 
    /**
     * Default value for {@link #INFINISPAN_CONFIG_RESOURCE_PROP}. Specifies the "infinispan-configs.xml" file in this package.
     */
    public static final String DEF_INFINISPAN_CONFIG_RESOURCE = "org/hibernate/cache/infinispan/builder/infinispan-configs.xml";
 
    /**
     * Default value for {@link #ENTITY_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_ENTITY_RESOURCE = "entity";
 
    /**
     * Default value for {@link #TIMESTAMPS_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_TIMESTAMPS_RESOURCE = "timestamps";
 
    /**
     * Default value for {@link #QUERY_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_QUERY_RESOURCE = "local-query";
 
    /**
     * Default value for {@link #INFINISPAN_USE_SYNCHRONIZATION_PROP}.
     */
    public static final boolean DEF_USE_SYNCHRONIZATION = true;
 
    /**
     * Name of the pending puts cache.
     */
    public static final String PENDING_PUTS_CACHE_NAME = "pending-puts";
 
    private EmbeddedCacheManager manager;
 
    private final Map<String, TypeOverrides> typeOverrides = new HashMap<String, TypeOverrides>();
 
    private final Set<String> definedConfigurations = new HashSet<String>();
 
    private org.infinispan.transaction.lookup.TransactionManagerLookup transactionManagerlookup;
 
    private List<String> regionNames = new ArrayList<String>();
    
    /**
     * Create a new instance using the default configuration.
     */
    public InfinispanRegionFactory() {
    }
 
    /**
     * Create a new instance using conifguration properties in <code>props</code>.
     * 
     * @param props
     *           Environmental properties; currently unused.
     */
    public InfinispanRegionFactory(Properties props) {
    }
 
    /** {@inheritDoc} */
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building collection cache region [" + regionName + "]");
       AdvancedCache cache = getCache(regionName, COLLECTION_KEY, properties);
       CollectionRegionImpl region = new CollectionRegionImpl(
             cache, regionName, metadata, this);
       startRegion(region, regionName);
       return region;
    }
 
    /** {@inheritDoc} */
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building entity cache region [" + regionName + "]");
       AdvancedCache cache = getCache(regionName, ENTITY_KEY, properties);
       EntityRegionImpl region = new EntityRegionImpl(
             cache, regionName, metadata, this);
       startRegion(region, regionName);
       return region;
    }
 
 	@Override
 	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException {
 		if (log.isDebugEnabled()) {
 			log.debug("Building natural id cache region [" + regionName + "]");
 		}
 		AdvancedCache cache = getCache(regionName, NATURAL_ID_KEY, properties);
 		NaturalIdRegionImpl region = new NaturalIdRegionImpl(
 				cache, regionName, metadata, this);
 		startRegion(region, regionName);
 		return region;
 	}
 	
    /**
     * {@inheritDoc}
     */
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties)
             throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building query results cache region [" + regionName + "]");
       String cacheName = typeOverrides.get(QUERY_KEY).getCacheName();
       // If region name is not default one, lookup a cache for that region name
       if (!regionName.equals("org.hibernate.cache.internal.StandardQueryCache"))
          cacheName = regionName;
 
       AdvancedCache cache = getCache(cacheName, QUERY_KEY, properties);
       QueryResultsRegionImpl region = new QueryResultsRegionImpl(
             cache, regionName, this);
       startRegion(region, regionName);
       return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties)
             throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building timestamps cache region [" + regionName + "]");
       AdvancedCache cache = getCache(regionName, TIMESTAMPS_KEY, properties);
       TimestampsRegionImpl region = createTimestampsRegion(cache, regionName);
       startRegion(region, regionName);
       return region;
    }
 
    protected TimestampsRegionImpl createTimestampsRegion(
          AdvancedCache cache, String regionName) {
       if (Caches.isClustered(cache))
          return new ClusteredTimestampsRegionImpl(cache, regionName, this);
       else
          return new TimestampsRegionImpl(cache, regionName, this);
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean isMinimalPutsEnabledByDefault() {
       return true;
    }
 
    @Override
    public AccessType getDefaultAccessType() {
       return AccessType.TRANSACTIONAL;
    }
 
    /**
     * {@inheritDoc}
     */
    public long nextTimestamp() {
       return System.currentTimeMillis() / 100;
    }
    
    public void setCacheManager(EmbeddedCacheManager manager) {
       this.manager = manager;
    }
 
    public EmbeddedCacheManager getCacheManager() {
       return manager;
    }
 
    /**
     * {@inheritDoc}
     */
    public void start(Settings settings, Properties properties) throws CacheException {
       log.debug("Starting Infinispan region factory");
       try {
          transactionManagerlookup = createTransactionManagerLookup(settings, properties);
          manager = createCacheManager(properties);
          initGenericDataTypeOverrides();
          Enumeration keys = properties.propertyNames();
          while (keys.hasMoreElements()) {
             String key = (String) keys.nextElement();
             int prefixLoc;
             if ((prefixLoc = key.indexOf(PREFIX)) != -1) {
                dissectProperty(prefixLoc, key, properties);
             }
          }
          defineGenericDataTypeCacheConfigurations(properties);
          definePendingPutsCache();
       } catch (CacheException ce) {
          throw ce;
       } catch (Throwable t) {
           throw new CacheException("Unable to start region factory", t);
       }
    }
 
    private void definePendingPutsCache() {
       ConfigurationBuilder builder = new ConfigurationBuilder();
       // A local, lightweight cache for pending puts, which is
       // non-transactional and has aggressive expiration settings.
       // Locking is still required since the putFromLoad validator
       // code uses conditional operations (i.e. putIfAbsent).
       builder.clustering().cacheMode(CacheMode.LOCAL)
          .transaction().transactionMode(TransactionMode.NON_TRANSACTIONAL)
          .expiration().maxIdle(TimeUnit.SECONDS.toMillis(60))
          .storeAsBinary().enabled(false)
          .locking().isolationLevel(IsolationLevel.READ_COMMITTED)
          .jmxStatistics().disable();
 
       manager.defineConfiguration(PENDING_PUTS_CACHE_NAME, builder.build());
    }
 
    protected org.infinispan.transaction.lookup.TransactionManagerLookup createTransactionManagerLookup(
             Settings settings, Properties properties) {
       return new HibernateTransactionManagerLookup(settings, properties);
    }
 
    /**
     * {@inheritDoc}
     */
    public void stop() {
       log.debug("Stop region factory");
       stopCacheRegions();
       stopCacheManager();
    }
 
    protected void stopCacheRegions() {
       log.debug("Clear region references");
       getCacheCommandFactory(manager.getCache().getAdvancedCache())
             .clearRegions(regionNames);
       regionNames.clear();
    }
 
    protected void stopCacheManager() {
       log.debug("Stop cache manager");
       manager.stop();
    }
    
    /**
     * Returns an unmodifiable map containing configured entity/collection type configuration overrides.
     * This method should be used primarily for testing/checking purpouses.
     * 
     * @return an unmodifiable map.
     */
    public Map<String, TypeOverrides> getTypeOverrides() {
       return Collections.unmodifiableMap(typeOverrides);
    }
    
    public Set<String> getDefinedConfigurations() {
       return Collections.unmodifiableSet(definedConfigurations);
    }
 
    protected EmbeddedCacheManager createCacheManager(Properties properties) throws CacheException {
       try {
          String configLoc = ConfigurationHelper.getString(
                INFINISPAN_CONFIG_RESOURCE_PROP, properties, DEF_INFINISPAN_CONFIG_RESOURCE);
          ClassLoader classLoader = ClassLoaderHelper.getContextClassLoader();
-         InputStream is = FileLookupFactory.newInstance().lookupFileStrict(
-               configLoc, classLoader);
+         InputStream is;
+         try {
+	         is = FileLookupFactory.newInstance().lookupFileStrict(
+	               configLoc, classLoader);
+         }
+         catch ( FileNotFoundException e ) {
+        	 // In some environments (ex: OSGi), hibernate-infinispan may not
+        	 // be in the app CL.  It's important to also try this CL.
+        	 classLoader = this.getClass().getClassLoader();
+        	 is = FileLookupFactory.newInstance().lookupFileStrict(
+                     configLoc, classLoader);
+         }
          ParserRegistry parserRegistry = new ParserRegistry(classLoader);
          ConfigurationBuilderHolder holder = parserRegistry.parse(is);
 
          // Override global jmx statistics exposure
          String globalStats = extractProperty(
                INFINISPAN_GLOBAL_STATISTICS_PROP, properties);
          if (globalStats != null)
             holder.getGlobalConfigurationBuilder().globalJmxStatistics()
                   .enabled(Boolean.parseBoolean(globalStats));
 
          return createCacheManager(holder);
       } catch (IOException e) {
          throw new CacheException("Unable to create default cache manager", e);
       }
    }
 
    protected EmbeddedCacheManager createCacheManager(
          ConfigurationBuilderHolder holder) {
       return new DefaultCacheManager(holder, true);
    }
 
    private void startRegion(BaseRegion region, String regionName) {
       regionNames.add(regionName);
       getCacheCommandFactory(region.getCache()).addRegion(regionName, region);
    }
 
    private Map<String, TypeOverrides> initGenericDataTypeOverrides() {
       TypeOverrides entityOverrides = new TypeOverrides();
       entityOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(ENTITY_KEY, entityOverrides);
       TypeOverrides collectionOverrides = new TypeOverrides();
       collectionOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(COLLECTION_KEY, collectionOverrides);
       TypeOverrides naturalIdOverrides = new TypeOverrides();
       naturalIdOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(NATURAL_ID_KEY, naturalIdOverrides);
       TypeOverrides timestampOverrides = new TimestampTypeOverrides();
       timestampOverrides.setCacheName(DEF_TIMESTAMPS_RESOURCE);
       typeOverrides.put(TIMESTAMPS_KEY, timestampOverrides);
       TypeOverrides queryOverrides = new TypeOverrides();
       queryOverrides.setCacheName(DEF_QUERY_RESOURCE);
       typeOverrides.put(QUERY_KEY, queryOverrides);
       return typeOverrides;
    }
 
    private void dissectProperty(int prefixLoc, String key, Properties properties) {
       TypeOverrides cfgOverride;
       int suffixLoc;
       if (!key.equals(INFINISPAN_CONFIG_RESOURCE_PROP) && (suffixLoc = key.indexOf(CONFIG_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setCacheName(extractProperty(key, properties));
       } else if ((suffixLoc = key.indexOf(STRATEGY_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionStrategy(extractProperty(key, properties));
       } else if ((suffixLoc = key.indexOf(WAKE_UP_INTERVAL_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionWakeUpInterval(Long.parseLong(extractProperty(key, properties)));
       } else if ((suffixLoc = key.indexOf(MAX_ENTRIES_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionMaxEntries(Integer.parseInt(extractProperty(key, properties)));
       } else if ((suffixLoc = key.indexOf(LIFESPAN_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setExpirationLifespan(Long.parseLong(extractProperty(key, properties)));
       } else if ((suffixLoc = key.indexOf(MAX_IDLE_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setExpirationMaxIdle(Long.parseLong(extractProperty(key, properties)));
       }
    }
 
    private String extractProperty(String key, Properties properties) {
       String value = ConfigurationHelper.extractPropertyValue(key, properties);
       log.debugf("Configuration override via property %s: %s", key, value);
       return value;
    }
 
    private TypeOverrides getOrCreateConfig(int prefixLoc, String key, int suffixLoc) {
       String name = key.substring(prefixLoc + PREFIX.length(), suffixLoc);
       TypeOverrides cfgOverride = typeOverrides.get(name);
       if (cfgOverride == null) {
          cfgOverride = new TypeOverrides();
          typeOverrides.put(name, cfgOverride);
       }
       return cfgOverride;
    }
 
    private void defineGenericDataTypeCacheConfigurations(Properties properties) {
       String[] defaultGenericDataTypes = new String[]{ENTITY_KEY, COLLECTION_KEY, TIMESTAMPS_KEY, QUERY_KEY};
       for (String type : defaultGenericDataTypes) {
          TypeOverrides override = overrideStatisticsIfPresent(typeOverrides.get(type), properties);
          String cacheName = override.getCacheName();
          ConfigurationBuilder builder = new ConfigurationBuilder();
          // Read base configuration
          applyConfiguration(cacheName, builder);
 
          // Apply overrides
          override.applyTo(builder);
          // Configure transaction manager
          configureTransactionManager(builder, cacheName, properties);
          // Define configuration, validate and then apply
          Configuration cfg = builder.build();
          override.validateInfinispanConfiguration(cfg);
          manager.defineConfiguration(cacheName, cfg);
          definedConfigurations.add(cacheName);
       }
    }
 
    private AdvancedCache getCache(String regionName, String typeKey, Properties properties) {
       TypeOverrides regionOverride = typeOverrides.get(regionName);
       if (!definedConfigurations.contains(regionName)) {
          String templateCacheName;
          Configuration regionCacheCfg;
          ConfigurationBuilder builder = new ConfigurationBuilder();
          if (regionOverride != null) {
             if (log.isDebugEnabled()) log.debug("Cache region specific configuration exists: " + regionOverride);
             String cacheName = regionOverride.getCacheName();
             if (cacheName != null) // Region specific override with a given cache name
                templateCacheName = cacheName;
             else // Region specific override without cache name, so template cache name is generic for data type.
                templateCacheName = typeOverrides.get(typeKey).getCacheName();
 
             // Read template configuration
             applyConfiguration(templateCacheName, builder);
 
             regionOverride = overrideStatisticsIfPresent(regionOverride, properties);
             regionOverride.applyTo(builder);
 
          } else {
             // No region specific overrides, template cache name is generic for data type.
             templateCacheName = typeOverrides.get(typeKey).getCacheName();
             // Read template configuration
             builder.read(manager.getCacheConfiguration(templateCacheName));
             // Apply overrides
             typeOverrides.get(typeKey).applyTo(builder);
          }
          // Configure transaction manager
          configureTransactionManager(builder, templateCacheName, properties);
          // Define configuration
          manager.defineConfiguration(regionName, builder.build());
          definedConfigurations.add(regionName);
       }
       AdvancedCache cache = manager.getCache(regionName).getAdvancedCache();
       if (!cache.getStatus().allowInvocations()) {
          cache.start();
       }
       return createCacheWrapper(cache);
    }
 
    private void applyConfiguration(String cacheName, ConfigurationBuilder builder) {
       Configuration cfg = manager.getCacheConfiguration(cacheName);
       if (cfg != null)
          builder.read(cfg);
    }
 
    private CacheCommandFactory getCacheCommandFactory(AdvancedCache cache) {
       GlobalComponentRegistry globalCr = cache.getComponentRegistry()
             .getGlobalComponentRegistry();
 
       Map<Byte, ModuleCommandFactory> factories =
          (Map<Byte, ModuleCommandFactory>) globalCr
                .getComponent("org.infinispan.modules.command.factories");
 
       for (ModuleCommandFactory factory : factories.values()) {
          if (factory instanceof CacheCommandFactory)
             return (CacheCommandFactory) factory;
       }
 
       throw new CacheException("Infinispan custom cache command factory not " +
             "installed (possibly because the classloader where Infinispan " +
             "lives couldn't find the Hibernate Infinispan cache provider)");
    }
 
    protected AdvancedCache createCacheWrapper(AdvancedCache cache) {
       return cache;
    }
 
    private void configureTransactionManager(ConfigurationBuilder builder,
          String cacheName, Properties properties) {
       // Get existing configuration to verify whether a tm was configured or not.
       Configuration baseCfg = manager.getCacheConfiguration(cacheName);
       if (baseCfg != null && baseCfg.transaction().transactionMode().isTransactional()) {
          String ispnTmLookupClassName = baseCfg.transaction().transactionManagerLookup().getClass().getName();
          String hbTmLookupClassName = org.hibernate.cache.infinispan.tm.HibernateTransactionManagerLookup.class.getName();
          if (GenericTransactionManagerLookup.class.getName().equals(ispnTmLookupClassName)) {
             log.debug("Using default Infinispan transaction manager lookup " +
                   "instance (GenericTransactionManagerLookup), overriding it " +
                   "with Hibernate transaction manager lookup");
             builder.transaction().transactionManagerLookup(transactionManagerlookup);
          } else if (ispnTmLookupClassName != null && !ispnTmLookupClassName.equals(hbTmLookupClassName)) {
             log.debug("Infinispan is configured [" + ispnTmLookupClassName + "] with a different transaction manager lookup " +
                             "class than Hibernate [" + hbTmLookupClassName + "]");
          } else {
             // Infinispan TM lookup class null, so apply Hibernate one directly
             builder.transaction().transactionManagerLookup(transactionManagerlookup);
          }
 
          String useSyncProp = extractProperty(INFINISPAN_USE_SYNCHRONIZATION_PROP, properties);
          boolean useSync = useSyncProp == null ? DEF_USE_SYNCHRONIZATION : Boolean.parseBoolean(useSyncProp);
          builder.transaction().useSynchronization(useSync);
       }
    }
 
    private TypeOverrides overrideStatisticsIfPresent(TypeOverrides override, Properties properties) {
       String globalStats = extractProperty(INFINISPAN_GLOBAL_STATISTICS_PROP, properties);
       if (globalStats != null) {
          override.setExposeStatistics(Boolean.parseBoolean(globalStats));
       }
       return override;
    }
 }
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/StrategyRegistrationProviderImpl.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/StrategyRegistrationProviderImpl.java
index 611e0587e5..8197d09ea3 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/StrategyRegistrationProviderImpl.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/StrategyRegistrationProviderImpl.java
@@ -1,65 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.infinispan;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.boot.registry.selector.StrategyRegistration;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
 import org.hibernate.cache.spi.RegionFactory;
 
 /**
  * Makes the 2 contained region factory implementations available to the Hibernate
  * {@link org.hibernate.boot.registry.selector.spi.StrategySelector} service.
  *
  * @author Steve Ebersole
  */
 public class StrategyRegistrationProviderImpl implements StrategyRegistrationProvider {
 	@Override
 	public Iterable<StrategyRegistration> getStrategyRegistrations() {
 		final List<StrategyRegistration> strategyRegistrations = new ArrayList<StrategyRegistration>();
 
 		strategyRegistrations.add(
 				new SimpleStrategyRegistrationImpl(
 						RegionFactory.class,
 						InfinispanRegionFactory.class,
 						"infinispan",
+						InfinispanRegionFactory.class.getName(),
 						InfinispanRegionFactory.class.getSimpleName()
 				)
 		);
 
 		strategyRegistrations.add(
 				new SimpleStrategyRegistrationImpl(
 						RegionFactory.class,
 						JndiInfinispanRegionFactory.class,
 						"infinispan-jndi",
+						JndiInfinispanRegionFactory.class.getName(),
 						JndiInfinispanRegionFactory.class.getSimpleName()
 				)
 		);
 
 		return strategyRegistrations;
 	}
 }
diff --git a/hibernate-infinispan/src/main/resources/META-INF/services/org.hibernate.boot.registry.selector.AvailabilityAnnouncer b/hibernate-infinispan/src/main/resources/META-INF/services/org.hibernate.boot.registry.selector.AvailabilityAnnouncer
deleted file mode 100644
index e69de29bb2..0000000000
diff --git a/hibernate-infinispan/src/main/resources/META-INF/services/org.hibernate.boot.registry.selector.StrategyRegistrationProvider b/hibernate-infinispan/src/main/resources/META-INF/services/org.hibernate.boot.registry.selector.StrategyRegistrationProvider
new file mode 100644
index 0000000000..b59a47b2c6
--- /dev/null
+++ b/hibernate-infinispan/src/main/resources/META-INF/services/org.hibernate.boot.registry.selector.StrategyRegistrationProvider
@@ -0,0 +1 @@
+org.hibernate.cache.infinispan.StrategyRegistrationProviderImpl
\ No newline at end of file
diff --git a/hibernate-infinispan/src/main/resources/OSGI-INF/blueprint/blueprint.xml b/hibernate-infinispan/src/main/resources/OSGI-INF/blueprint/blueprint.xml
new file mode 100644
index 0000000000..ff3a39ab69
--- /dev/null
+++ b/hibernate-infinispan/src/main/resources/OSGI-INF/blueprint/blueprint.xml
@@ -0,0 +1,10 @@
+<?xml version="1.0" encoding="UTF-8"?>
+
+<blueprint  default-activation="eager" 
+            xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
+            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
+
+  <bean id="strategyRegistrationProvider" class="org.hibernate.cache.infinispan.StrategyRegistrationProviderImpl"/>
+  <service ref="strategyRegistrationProvider" interface="org.hibernate.boot.registry.selector.StrategyRegistrationProvider"/>
+  
+</blueprint>
