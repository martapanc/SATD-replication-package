diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
index e60d28c3cd..e92db29c61 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
@@ -1,945 +1,941 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.internal;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.NullPrecedence;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.SchemaAutoTooling;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.config.internal.ConfigurationServiceImpl;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
 import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 import org.jboss.logging.Logger;
 
 import static org.hibernate.cfg.AvailableSettings.AUTO_CLOSE_SESSION;
 import static org.hibernate.cfg.AvailableSettings.AUTO_EVICT_COLLECTION_CACHE;
 import static org.hibernate.cfg.AvailableSettings.AUTO_SESSION_EVENTS_LISTENER;
 import static org.hibernate.cfg.AvailableSettings.BATCH_FETCH_STYLE;
 import static org.hibernate.cfg.AvailableSettings.BATCH_VERSIONED_DATA;
 import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_PREFIX;
 import static org.hibernate.cfg.AvailableSettings.CHECK_NULLABILITY;
 import static org.hibernate.cfg.AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY;
 import static org.hibernate.cfg.AvailableSettings.DEFAULT_BATCH_FETCH_SIZE;
 import static org.hibernate.cfg.AvailableSettings.DEFAULT_ENTITY_MODE;
 import static org.hibernate.cfg.AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS;
 import static org.hibernate.cfg.AvailableSettings.FLUSH_BEFORE_COMPLETION;
 import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
 import static org.hibernate.cfg.AvailableSettings.HQL_BULK_ID_STRATEGY;
 import static org.hibernate.cfg.AvailableSettings.INTERCEPTOR;
 import static org.hibernate.cfg.AvailableSettings.JPAQL_STRICT_COMPLIANCE;
 import static org.hibernate.cfg.AvailableSettings.JTA_TRACK_BY_THREAD;
 import static org.hibernate.cfg.AvailableSettings.LOG_SESSION_METRICS;
 import static org.hibernate.cfg.AvailableSettings.MAX_FETCH_DEPTH;
 import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER;
 import static org.hibernate.cfg.AvailableSettings.ORDER_INSERTS;
 import static org.hibernate.cfg.AvailableSettings.ORDER_UPDATES;
 import static org.hibernate.cfg.AvailableSettings.QUERY_CACHE_FACTORY;
 import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
 import static org.hibernate.cfg.AvailableSettings.QUERY_SUBSTITUTIONS;
 import static org.hibernate.cfg.AvailableSettings.RELEASE_CONNECTIONS;
 import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME;
 import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI;
 import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
 import static org.hibernate.cfg.AvailableSettings.STATEMENT_FETCH_SIZE;
 import static org.hibernate.cfg.AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES;
 import static org.hibernate.cfg.AvailableSettings.USE_GET_GENERATED_KEYS;
 import static org.hibernate.cfg.AvailableSettings.USE_IDENTIFIER_ROLLBACK;
 import static org.hibernate.cfg.AvailableSettings.USE_MINIMAL_PUTS;
 import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
 import static org.hibernate.cfg.AvailableSettings.USE_SCROLLABLE_RESULTSET;
 import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
 import static org.hibernate.cfg.AvailableSettings.USE_SQL_COMMENTS;
 import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;
 import static org.hibernate.cfg.AvailableSettings.WRAP_RESULT_SETS;
 import static org.hibernate.engine.config.spi.StandardConverters.BOOLEAN;
 
 /**
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
 	private static final Logger log = Logger.getLogger( SessionFactoryBuilderImpl.class );
 
 	private final MetadataImplementor metadata;
 	private final SessionFactoryOptionsImpl options;
 
 	SessionFactoryBuilderImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
 		options = new SessionFactoryOptionsImpl( metadata.getMetadataBuildingOptions().getServiceRegistry() );
 
 		if ( metadata.getSqlFunctionMap() != null ) {
 			for ( Map.Entry<String, SQLFunction> sqlFunctionEntry : metadata.getSqlFunctionMap().entrySet() ) {
 				applySqlFunction( sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue() );
 			}
 		}
 	}
 
 	@Override
 	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory) {
 		this.options.validatorFactoryReference = validatorFactory;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyBeanManager(Object beanManager) {
 		this.options.beanManagerReference = beanManager;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyName(String sessionFactoryName) {
 		this.options.sessionFactoryName = sessionFactoryName;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyNameAsJndiName(boolean isJndiName) {
 		this.options.sessionFactoryNameAlsoJndiName = isJndiName;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyAutoClosing(boolean enabled) {
 		this.options.autoCloseSessionEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyAutoFlushing(boolean enabled) {
 		this.options.flushBeforeCompletionEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyStatisticsSupport(boolean enabled) {
 		this.options.statisticsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver... observers) {
 		this.options.sessionFactoryObserverList.addAll( Arrays.asList( observers ) );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor) {
 		this.options.interceptor = interceptor;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy) {
 		this.options.customEntityDirtinessStrategy = strategy;
 		return this;
 	}
 
 
 	@Override
 	public SessionFactoryBuilder addEntityNameResolver(EntityNameResolver... entityNameResolvers) {
 		this.options.entityNameResolvers.addAll( Arrays.asList( entityNameResolvers ) );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.options.entityNotFoundDelegate = entityNotFoundDelegate;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyIdentifierRollbackSupport(boolean enabled) {
 		this.options.identifierRollbackEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDefaultEntityMode(EntityMode entityMode) {
 		this.options.defaultEntityMode = entityMode;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyNullabilityChecking(boolean enabled) {
 		this.options.checkNullability = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyLazyInitializationOutsideTransaction(boolean enabled) {
 		this.options.initializeLazyStateOutsideTransactions = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
 		options.entityTuplizerFactory = entityTuplizerFactory;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyEntityTuplizer(
 			EntityMode entityMode,
 			Class<? extends EntityTuplizer> tuplizerClass) {
 		this.options.entityTuplizerFactory.registerDefaultTuplizerClass( entityMode, tuplizerClass );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy) {
 		this.options.multiTableBulkIdStrategy = strategy;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyBatchFetchStyle(BatchFetchStyle style) {
 		this.options.batchFetchStyle = style;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDefaultBatchFetchSize(int size) {
 		this.options.defaultBatchFetchSize = size;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMaximumFetchDepth(int depth) {
 		this.options.maximumFetchDepth = depth;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDefaultNullPrecedence(NullPrecedence nullPrecedence) {
 		this.options.defaultNullPrecedence = nullPrecedence;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyOrderingOfInserts(boolean enabled) {
 		this.options.orderInsertsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyOrderingOfUpdates(boolean enabled) {
 		this.options.orderUpdatesEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMultiTenancyStrategy(MultiTenancyStrategy strategy) {
 		this.options.multiTenancyStrategy = strategy;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver) {
 		this.options.currentTenantIdentifierResolver = resolver;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyJtaTrackingByThread(boolean enabled) {
 		this.options.jtaTrackByThread = enabled;
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public SessionFactoryBuilder applyQuerySubstitutions(Map substitutions) {
 		this.options.querySubstitutions.putAll( substitutions );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyStrictJpaQueryLanguageCompliance(boolean enabled) {
 		this.options.strictJpaQueryLanguageCompliance = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyNamedQueryCheckingOnStartup(boolean enabled) {
 		this.options.namedQueryStartupCheckingEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applySecondLevelCacheSupport(boolean enabled) {
 		this.options.secondLevelCacheEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyQueryCacheSupport(boolean enabled) {
 		this.options.queryCacheEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyQueryCacheFactory(QueryCacheFactory factory) {
 		this.options.queryCacheFactory = factory;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyCacheRegionPrefix(String prefix) {
 		this.options.cacheRegionPrefix = prefix;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMinimalPutsForCaching(boolean enabled) {
 		this.options.minimalPutsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyStructuredCacheEntries(boolean enabled) {
 		this.options.structuredCacheEntriesEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDirectReferenceCaching(boolean enabled) {
 		this.options.directReferenceCacheEntriesEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyAutomaticEvictionOfCollectionCaches(boolean enabled) {
 		this.options.autoEvictCollectionCache = enabled;
 		return this;
 	}
 
 
 	@Override
 	public SessionFactoryBuilder applyJdbcBatchSize(int size) {
 		this.options.jdbcBatchSize = size;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyJdbcBatchingForVersionedEntities(boolean enabled) {
 		this.options.jdbcBatchVersionedData = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyScrollableResultsSupport(boolean enabled) {
 		this.options.scrollableResultSetsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyResultSetsWrapping(boolean enabled) {
 		this.options.wrapResultSetsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyGetGeneratedKeysSupport(boolean enabled) {
 		this.options.getGeneratedKeysEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyJdbcFetchSize(int size) {
 		this.options.jdbcFetchSize = size;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 		this.options.connectionReleaseMode = connectionReleaseMode;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applySqlComments(boolean enabled) {
 		this.options.commentsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applySqlFunction(String registrationName, SQLFunction sqlFunction) {
 		if ( this.options.sqlFunctions == null ) {
 			this.options.sqlFunctions = new HashMap<String, SQLFunction>();
 		}
 		this.options.sqlFunctions.put( registrationName, sqlFunction );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T extends SessionFactoryBuilder> T unwrap(Class<T> type) {
 		return (T) this;
 	}
 
 	@Override
 	public SessionFactory build() {
 		metadata.validate();
 		return new SessionFactoryImpl( metadata, options );
 	}
 
 	public static class SessionFactoryOptionsImpl implements SessionFactoryOptions {
 		private final StandardServiceRegistry serviceRegistry;
 
 		// integration
 		private Object beanManagerReference;
 		private Object validatorFactoryReference;
 
 		// SessionFactory behavior
 		private String sessionFactoryName;
 		private boolean sessionFactoryNameAlsoJndiName;
 
 		// Session behavior
 		private boolean flushBeforeCompletionEnabled;
 		private boolean autoCloseSessionEnabled;
 
 		// Statistics/Interceptor/observers
 		private boolean statisticsEnabled;
 		private Interceptor interceptor;
 		private List<SessionFactoryObserver> sessionFactoryObserverList = new ArrayList<SessionFactoryObserver>();
 		private BaselineSessionEventsListenerBuilder baselineSessionEventsListenerBuilder;	// not exposed on builder atm
 
 		// persistence behavior
 		private CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 		private List<EntityNameResolver> entityNameResolvers = new ArrayList<EntityNameResolver>();
 		private EntityNotFoundDelegate entityNotFoundDelegate;
 		private boolean identifierRollbackEnabled;
 		private EntityMode defaultEntityMode;
 		private EntityTuplizerFactory entityTuplizerFactory = new EntityTuplizerFactory();
 		private boolean checkNullability;
 		private boolean initializeLazyStateOutsideTransactions;
 		private MultiTableBulkIdStrategy multiTableBulkIdStrategy;
 		private BatchFetchStyle batchFetchStyle;
 		private int defaultBatchFetchSize;
 		private Integer maximumFetchDepth;
 		private NullPrecedence defaultNullPrecedence;
 		private boolean orderUpdatesEnabled;
 		private boolean orderInsertsEnabled;
 
 		// multi-tenancy
 		private MultiTenancyStrategy multiTenancyStrategy;
 		private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 
 		// JTA timeout detection
 		private boolean jtaTrackByThread;
 
 		// Queries
 		private Map querySubstitutions;
 		private boolean strictJpaQueryLanguageCompliance;
 		private boolean namedQueryStartupCheckingEnabled;
 
 		// Caching
 		private boolean secondLevelCacheEnabled;
 		private boolean queryCacheEnabled;
 		private QueryCacheFactory queryCacheFactory;
 		private String cacheRegionPrefix;
 		private boolean minimalPutsEnabled;
 		private boolean structuredCacheEntriesEnabled;
 		private boolean directReferenceCacheEntriesEnabled;
 		private boolean autoEvictCollectionCache;
 
 		// Schema tooling
 		private SchemaAutoTooling schemaAutoTooling;
 
 		// JDBC Handling
 		private boolean dataDefinitionImplicitCommit;			// not exposed on builder atm
 		private boolean dataDefinitionInTransactionSupported;	// not exposed on builder atm
 		private boolean getGeneratedKeysEnabled;
 		private int jdbcBatchSize;
 		private boolean jdbcBatchVersionedData;
 		private Integer jdbcFetchSize;
 		private boolean scrollableResultSetsEnabled;
 		private boolean commentsEnabled;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean wrapResultSetsEnabled;
 
 		private Map<String, SQLFunction> sqlFunctions;
 
 
 		public SessionFactoryOptionsImpl(StandardServiceRegistry serviceRegistry) {
 			this.serviceRegistry = serviceRegistry;
 
 			final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 			ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );
 			final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 			final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 			final Map configurationSettings = new HashMap();
 			//noinspection unchecked
 			configurationSettings.putAll( cfgService.getSettings() );
 			//noinspection unchecked
 			configurationSettings.putAll( jdbcServices.getJdbcEnvironment().getDialect().getDefaultProperties() );
 			cfgService = new ConfigurationServiceImpl( configurationSettings );
 			( (ConfigurationServiceImpl) cfgService ).injectServices( (ServiceRegistryImplementor) serviceRegistry );
 
 			this.beanManagerReference = configurationSettings.get( "javax.persistence.bean.manager" );
 			this.validatorFactoryReference = configurationSettings.get( "javax.persistence.validation.factory" );
 
 			this.sessionFactoryName = (String) configurationSettings.get( SESSION_FACTORY_NAME );
 			this.sessionFactoryNameAlsoJndiName = cfgService.getSetting(
 					SESSION_FACTORY_NAME_IS_JNDI,
 					BOOLEAN,
 					true
 			);
 
 			this.flushBeforeCompletionEnabled = cfgService.getSetting( FLUSH_BEFORE_COMPLETION, BOOLEAN, false );
 			this.autoCloseSessionEnabled = cfgService.getSetting( AUTO_CLOSE_SESSION, BOOLEAN, false );
 
 			this.statisticsEnabled = cfgService.getSetting( GENERATE_STATISTICS, BOOLEAN, false );
 			this.interceptor = strategySelector.resolveDefaultableStrategy(
 					Interceptor.class,
 					configurationSettings.get( INTERCEPTOR ),
 					EmptyInterceptor.INSTANCE
 			);
 			// todo : expose this from builder?
 			final String autoSessionEventsListenerName = (String) configurationSettings.get(
 					AUTO_SESSION_EVENTS_LISTENER
 			);
 			final Class<? extends SessionEventListener> autoSessionEventsListener = autoSessionEventsListenerName == null
 					? null
 					: strategySelector.selectStrategyImplementor( SessionEventListener.class, autoSessionEventsListenerName );
 
 			final boolean logSessionMetrics = cfgService.getSetting( LOG_SESSION_METRICS, BOOLEAN, statisticsEnabled );
 			this.baselineSessionEventsListenerBuilder = new BaselineSessionEventsListenerBuilder( logSessionMetrics, autoSessionEventsListener );
 
 			this.customEntityDirtinessStrategy = strategySelector.resolveDefaultableStrategy(
 					CustomEntityDirtinessStrategy.class,
 					configurationSettings.get( CUSTOM_ENTITY_DIRTINESS_STRATEGY ),
 					DefaultCustomEntityDirtinessStrategy.INSTANCE
 			);
 
 			this.entityNotFoundDelegate = StandardEntityNotFoundDelegate.INSTANCE;
 			this.identifierRollbackEnabled = cfgService.getSetting( USE_IDENTIFIER_ROLLBACK, BOOLEAN, false );
 			this.defaultEntityMode = EntityMode.parse( (String) configurationSettings.get( DEFAULT_ENTITY_MODE ) );
 			this.checkNullability = cfgService.getSetting( CHECK_NULLABILITY, BOOLEAN, true );
 			this.initializeLazyStateOutsideTransactions = cfgService.getSetting( ENABLE_LAZY_LOAD_NO_TRANS, BOOLEAN, false );
 
 			this.multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configurationSettings );
 			this.currentTenantIdentifierResolver = strategySelector.resolveStrategy(
 					CurrentTenantIdentifierResolver.class,
 					configurationSettings.get( MULTI_TENANT_IDENTIFIER_RESOLVER )
 			);
 
-			this.multiTableBulkIdStrategy = strategySelector.resolveStrategy(
+			this.multiTableBulkIdStrategy = strategySelector.resolveDefaultableStrategy(
 					MultiTableBulkIdStrategy.class,
-					configurationSettings.get( HQL_BULK_ID_STRATEGY )
+					configurationSettings.get( HQL_BULK_ID_STRATEGY ),
+					jdbcServices.getJdbcEnvironment().getDialect().getDefaultMultiTableBulkIdStrategy()
 			);
-			if ( this.multiTableBulkIdStrategy == null ) {
-				this.multiTableBulkIdStrategy = jdbcServices.getDialect().supportsTemporaryTables()
-						? TemporaryTableBulkIdStrategy.INSTANCE
-						: new PersistentTableBulkIdStrategy();
-			}
 
 			this.batchFetchStyle = BatchFetchStyle.interpret( configurationSettings.get( BATCH_FETCH_STYLE ) );
 			this.defaultBatchFetchSize = ConfigurationHelper.getInt( DEFAULT_BATCH_FETCH_SIZE, configurationSettings, -1 );
 			this.maximumFetchDepth = ConfigurationHelper.getInteger( MAX_FETCH_DEPTH, configurationSettings );
 			final String defaultNullPrecedence = ConfigurationHelper.getString(
 					AvailableSettings.DEFAULT_NULL_ORDERING, configurationSettings, "none", "first", "last"
 			);
 			this.defaultNullPrecedence = NullPrecedence.parse( defaultNullPrecedence );
 			this.orderUpdatesEnabled = ConfigurationHelper.getBoolean( ORDER_UPDATES, configurationSettings );
 			this.orderInsertsEnabled = ConfigurationHelper.getBoolean( ORDER_INSERTS, configurationSettings );
 
 			this.jtaTrackByThread = cfgService.getSetting( JTA_TRACK_BY_THREAD, BOOLEAN, true );
 
 			this.querySubstitutions = ConfigurationHelper.toMap( QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", configurationSettings );
 			this.strictJpaQueryLanguageCompliance = cfgService.getSetting( JPAQL_STRICT_COMPLIANCE, BOOLEAN, false );
 			this.namedQueryStartupCheckingEnabled = cfgService.getSetting( QUERY_STARTUP_CHECKING, BOOLEAN, true );
 
 			this.secondLevelCacheEnabled = cfgService.getSetting( USE_SECOND_LEVEL_CACHE, BOOLEAN, true );
 			this.queryCacheEnabled = cfgService.getSetting( USE_QUERY_CACHE, BOOLEAN, false );
 			this.queryCacheFactory = strategySelector.resolveDefaultableStrategy(
 					QueryCacheFactory.class,
 					configurationSettings.get( QUERY_CACHE_FACTORY ),
 					StandardQueryCacheFactory.INSTANCE
 			);
 			this.cacheRegionPrefix = ConfigurationHelper.extractPropertyValue(
 					CACHE_REGION_PREFIX,
 					configurationSettings
 			);
 			this.minimalPutsEnabled = cfgService.getSetting(
 					USE_MINIMAL_PUTS,
 					BOOLEAN,
 					serviceRegistry.getService( RegionFactory.class ).isMinimalPutsEnabledByDefault()
 			);
 			this.structuredCacheEntriesEnabled = cfgService.getSetting( USE_STRUCTURED_CACHE, BOOLEAN, false );
 			this.directReferenceCacheEntriesEnabled = cfgService.getSetting( USE_DIRECT_REFERENCE_CACHE_ENTRIES,BOOLEAN, false );
 			this.autoEvictCollectionCache = cfgService.getSetting( AUTO_EVICT_COLLECTION_CACHE, BOOLEAN, false );
 
 			try {
 				this.schemaAutoTooling = SchemaAutoTooling.interpret( (String) configurationSettings.get( AvailableSettings.HBM2DDL_AUTO ) );
 			}
 			catch (Exception e) {
 				log.warn( e.getMessage() + "  Ignoring" );
 			}
 
 
 			final ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 			this.dataDefinitionImplicitCommit = meta.doesDataDefinitionCauseTransactionCommit();
 			this.dataDefinitionInTransactionSupported = meta.supportsDataDefinitionInTransaction();
 
 			this.jdbcBatchSize = ConfigurationHelper.getInt( STATEMENT_BATCH_SIZE, configurationSettings, 0 );
 			if ( !meta.supportsBatchUpdates() ) {
 				this.jdbcBatchSize = 0;
 			}
 
 			this.jdbcBatchVersionedData = ConfigurationHelper.getBoolean( BATCH_VERSIONED_DATA, configurationSettings, false );
 			this.scrollableResultSetsEnabled = ConfigurationHelper.getBoolean(
 					USE_SCROLLABLE_RESULTSET,
 					configurationSettings,
 					meta.supportsScrollableResults()
 			);
 			this.wrapResultSetsEnabled = ConfigurationHelper.getBoolean(
 					WRAP_RESULT_SETS,
 					configurationSettings,
 					false
 			);
 			this.getGeneratedKeysEnabled = ConfigurationHelper.getBoolean(
 					USE_GET_GENERATED_KEYS,
 					configurationSettings,
 					meta.supportsGetGeneratedKeys()
 			);
 			this.jdbcFetchSize = ConfigurationHelper.getInteger( STATEMENT_FETCH_SIZE, configurationSettings );
 
 			final String releaseModeName = ConfigurationHelper.getString( RELEASE_CONNECTIONS, configurationSettings, "auto" );
 			if ( "auto".equals( releaseModeName ) ) {
 				this.connectionReleaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 			}
 			else {
 				connectionReleaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			}
 
 			this.commentsEnabled = ConfigurationHelper.getBoolean( USE_SQL_COMMENTS, configurationSettings );
 		}
 
 		@Override
 		public StandardServiceRegistry getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		@Override
 		public Object getBeanManagerReference() {
 			return beanManagerReference;
 		}
 
 		@Override
 		public Object getValidatorFactoryReference() {
 			return validatorFactoryReference;
 		}
 
 		@Override
 		public String getSessionFactoryName() {
 			return sessionFactoryName;
 		}
 
 		@Override
 		public boolean isSessionFactoryNameAlsoJndiName() {
 			return sessionFactoryNameAlsoJndiName;
 		}
 
 		@Override
 		public boolean isFlushBeforeCompletionEnabled() {
 			return flushBeforeCompletionEnabled;
 		}
 
 		@Override
 		public boolean isAutoCloseSessionEnabled() {
 			return autoCloseSessionEnabled;
 		}
 
 		@Override
 		public boolean isStatisticsEnabled() {
 			return statisticsEnabled;
 		}
 
 		@Override
 		public Interceptor getInterceptor() {
 			return interceptor;
 		}
 
 		@Override
 		public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
 			return baselineSessionEventsListenerBuilder;
 		}
 
 		@Override
 		public SessionFactoryObserver[] getSessionFactoryObservers() {
 			return sessionFactoryObserverList.toArray( new SessionFactoryObserver[sessionFactoryObserverList.size()] );
 		}
 
 		@Override
 		public boolean isIdentifierRollbackEnabled() {
 			return identifierRollbackEnabled;
 		}
 
 		@Override
 		public EntityMode getDefaultEntityMode() {
 			return defaultEntityMode;
 		}
 
 		public EntityTuplizerFactory getEntityTuplizerFactory() {
 			return entityTuplizerFactory;
 		}
 
 		@Override
 		public boolean isCheckNullability() {
 			return checkNullability;
 		}
 
 		@Override
 		public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
 			return initializeLazyStateOutsideTransactions;
 		}
 
 		@Override
 		public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
 			return multiTableBulkIdStrategy;
 		}
 
 		@Override
 		public BatchFetchStyle getBatchFetchStyle() {
 			return batchFetchStyle;
 		}
 
 		@Override
 		public int getDefaultBatchFetchSize() {
 			return defaultBatchFetchSize;
 		}
 
 		@Override
 		public Integer getMaximumFetchDepth() {
 			return maximumFetchDepth;
 		}
 
 		@Override
 		public NullPrecedence getDefaultNullPrecedence() {
 			return defaultNullPrecedence;
 		}
 
 		@Override
 		public boolean isOrderUpdatesEnabled() {
 			return orderUpdatesEnabled;
 		}
 
 		@Override
 		public boolean isOrderInsertsEnabled() {
 			return orderInsertsEnabled;
 		}
 
 		@Override
 		public MultiTenancyStrategy getMultiTenancyStrategy() {
 			return multiTenancyStrategy;
 		}
 
 		@Override
 		public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 			return currentTenantIdentifierResolver;
 		}
 
 		@Override
 		public boolean isJtaTrackByThread() {
 			return jtaTrackByThread;
 		}
 
 		@Override
 		public Map getQuerySubstitutions() {
 			return querySubstitutions;
 		}
 
 		@Override
 		public boolean isStrictJpaQueryLanguageCompliance() {
 			return strictJpaQueryLanguageCompliance;
 		}
 
 		@Override
 		public boolean isNamedQueryStartupCheckingEnabled() {
 			return namedQueryStartupCheckingEnabled;
 		}
 
 		@Override
 		public boolean isSecondLevelCacheEnabled() {
 			return secondLevelCacheEnabled;
 		}
 
 		@Override
 		public boolean isQueryCacheEnabled() {
 			return queryCacheEnabled;
 		}
 
 		@Override
 		public QueryCacheFactory getQueryCacheFactory() {
 			return queryCacheFactory;
 		}
 
 		@Override
 		public String getCacheRegionPrefix() {
 			return cacheRegionPrefix;
 		}
 
 		@Override
 		public boolean isMinimalPutsEnabled() {
 			return minimalPutsEnabled;
 		}
 
 		@Override
 		public boolean isStructuredCacheEntriesEnabled() {
 			return structuredCacheEntriesEnabled;
 		}
 
 		@Override
 		public boolean isDirectReferenceCacheEntriesEnabled() {
 			return directReferenceCacheEntriesEnabled;
 		}
 
 		public boolean isAutoEvictCollectionCache() {
 			return autoEvictCollectionCache;
 		}
 
 		@Override
 		public SchemaAutoTooling getSchemaAutoTooling() {
 			return schemaAutoTooling;
 		}
 
 		@Override
 		public boolean isDataDefinitionImplicitCommit() {
 			return dataDefinitionImplicitCommit;
 		}
 
 		@Override
 		public boolean isDataDefinitionInTransactionSupported() {
 			return dataDefinitionInTransactionSupported;
 		}
 
 		@Override
 		public int getJdbcBatchSize() {
 			return jdbcBatchSize;
 		}
 
 		@Override
 		public boolean isJdbcBatchVersionedData() {
 			return jdbcBatchVersionedData;
 		}
 
 		@Override
 		public boolean isScrollableResultSetsEnabled() {
 			return scrollableResultSetsEnabled;
 		}
 
 		@Override
 		public boolean isWrapResultSetsEnabled() {
 			return wrapResultSetsEnabled;
 		}
 
 		@Override
 		public boolean isGetGeneratedKeysEnabled() {
 			return getGeneratedKeysEnabled;
 		}
 
 		@Override
 		public Integer getJdbcFetchSize() {
 			return jdbcFetchSize;
 		}
 
 		@Override
 		public ConnectionReleaseMode getConnectionReleaseMode() {
 			return connectionReleaseMode;
 		}
 
 		@Override
 		public boolean isCommentsEnabled() {
 			return commentsEnabled;
 		}
 
 		@Override
 		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 			return customEntityDirtinessStrategy;
 		}
 
 
 		@Override
 		public EntityNameResolver[] getEntityNameResolvers() {
 			return entityNameResolvers.toArray( new EntityNameResolver[entityNameResolvers.size()] );
 		}
 
 		@Override
 		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 			return entityNotFoundDelegate;
 		}
 
 		@Override
 		public Map<String, SQLFunction> getCustomSqlFunctionMap() {
 			return sqlFunctions;
 		}
 
 		@Override
 		public void setCheckNullability(boolean enabled) {
 			this.checkNullability = enabled;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java
index e17512cc83..5285dfe8c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java
@@ -1,398 +1,410 @@
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
 package org.hibernate.boot.registry.selector.internal;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
 import org.hibernate.boot.registry.selector.StrategyRegistration;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.boot.registry.selector.spi.StrategySelectionException;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.dialect.CUBRIDDialect;
 import org.hibernate.dialect.Cache71Dialect;
 import org.hibernate.dialect.DB2390Dialect;
 import org.hibernate.dialect.DB2400Dialect;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.DerbyTenFiveDialect;
 import org.hibernate.dialect.DerbyTenSevenDialect;
 import org.hibernate.dialect.DerbyTenSixDialect;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.FirebirdDialect;
 import org.hibernate.dialect.FrontBaseDialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InformixDialect;
 import org.hibernate.dialect.Ingres10Dialect;
 import org.hibernate.dialect.Ingres9Dialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.InterbaseDialect;
 import org.hibernate.dialect.JDataStoreDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MimerSQLDialect;
 import org.hibernate.dialect.MySQL5Dialect;
 import org.hibernate.dialect.MySQL5InnoDBDialect;
 import org.hibernate.dialect.Oracle10gDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.Oracle9iDialect;
 import org.hibernate.dialect.PointbaseDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQL82Dialect;
 import org.hibernate.dialect.PostgreSQL9Dialect;
 import org.hibernate.dialect.PostgresPlusDialect;
 import org.hibernate.dialect.ProgressDialect;
 import org.hibernate.dialect.SAPDBDialect;
 import org.hibernate.dialect.SQLServer2005Dialect;
 import org.hibernate.dialect.SQLServer2008Dialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE157Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.BorlandEnterpriseServerJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JBossAppServerJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JOTMJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JOnASJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JRun4JtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.OC4JJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.OrionJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.ResinJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.SunOneJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.WebSphereExtendedJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.WebSphereJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.WeblogicJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.event.internal.EntityCopyAllowedLoggedObserver;
 import org.hibernate.event.internal.EntityCopyAllowedObserver;
 import org.hibernate.event.internal.EntityCopyNotAllowedObserver;
 import org.hibernate.event.spi.EntityCopyObserver;
+import org.hibernate.hql.spi.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
 import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
 
 import org.jboss.logging.Logger;
 
 /**
  * Builder for StrategySelector instances.
  *
  * @author Steve Ebersole
  */
 public class StrategySelectorBuilder {
 	private static final Logger log = Logger.getLogger( StrategySelectorBuilder.class );
 
 	private final List<StrategyRegistration> explicitStrategyRegistrations = new ArrayList<StrategyRegistration>();
 
 	/**
 	 * Adds an explicit (as opposed to discovered) strategy registration.
 	 *
 	 * @param strategy The strategy
 	 * @param implementation The strategy implementation
 	 * @param name The registered name
 	 * @param <T> The type of the strategy.  Used to make sure that the strategy and implementation are type
 	 * compatible.
 	 */
 	@SuppressWarnings("unchecked")
 	public <T> void addExplicitStrategyRegistration(Class<T> strategy, Class<? extends T> implementation, String name) {
 		addExplicitStrategyRegistration( new SimpleStrategyRegistrationImpl<T>( strategy, implementation, name ) );
 	}
 
 	/**
 	 * Adds an explicit (as opposed to discovered) strategy registration.
 	 *
 	 * @param strategyRegistration The strategy implementation registration.
 	 * @param <T> The type of the strategy.  Used to make sure that the strategy and implementation are type
 	 * compatible.
 	 */
 	public <T> void addExplicitStrategyRegistration(StrategyRegistration<T> strategyRegistration) {
 		if ( !strategyRegistration.getStrategyRole().isInterface() ) {
 			// not good form...
 			log.debug( "Registering non-interface strategy : " + strategyRegistration.getStrategyRole().getName()  );
 		}
 
 		if ( ! strategyRegistration.getStrategyRole().isAssignableFrom( strategyRegistration.getStrategyImplementation() ) ) {
 			throw new StrategySelectionException(
 					"Implementation class [" + strategyRegistration.getStrategyImplementation().getName()
 							+ "] does not implement strategy interface ["
 							+ strategyRegistration.getStrategyRole().getName() + "]"
 			);
 		}
 		explicitStrategyRegistrations.add( strategyRegistration );
 	}
 
 	/**
 	 * Builds the selector.
 	 *
 	 * @param classLoaderService The class loading service used to (attempt to) resolve any un-registered
 	 * strategy implementations.
 	 *
 	 * @return The selector.
 	 */
 	public StrategySelector buildSelector(ClassLoaderService classLoaderService) {
 		final StrategySelectorImpl strategySelector = new StrategySelectorImpl( classLoaderService );
 
 		// build the baseline...
 		addDialects( strategySelector );
 		addJtaPlatforms( strategySelector );
 		addTransactionFactories( strategySelector );
 		addMultiTableBulkIdStrategies( strategySelector );
 		addEntityCopyObserverStrategies( strategySelector );
 
 		// apply auto-discovered registrations
 		for ( StrategyRegistrationProvider provider : classLoaderService.loadJavaServices( StrategyRegistrationProvider.class ) ) {
 			for ( StrategyRegistration discoveredStrategyRegistration : provider.getStrategyRegistrations() ) {
 				applyFromStrategyRegistration( strategySelector, discoveredStrategyRegistration );
 			}
 		}
 
 		// apply customizations
 		for ( StrategyRegistration explicitStrategyRegistration : explicitStrategyRegistrations ) {
 			applyFromStrategyRegistration( strategySelector, explicitStrategyRegistration );
 		}
 
 		return strategySelector;
 	}
 
 	@SuppressWarnings("unchecked")
 	private <T> void applyFromStrategyRegistration(StrategySelectorImpl strategySelector, StrategyRegistration<T> strategyRegistration) {
 		for ( String name : strategyRegistration.getSelectorNames() ) {
 			strategySelector.registerStrategyImplementor(
 					strategyRegistration.getStrategyRole(),
 					name,
 					strategyRegistration.getStrategyImplementation()
 			);
 		}
 	}
 
 	private void addDialects(StrategySelectorImpl strategySelector) {
 		addDialect( strategySelector, Cache71Dialect.class );
 		addDialect( strategySelector, CUBRIDDialect.class );
 		addDialect( strategySelector, DB2Dialect.class );
 		addDialect( strategySelector, DB2390Dialect.class );
 		addDialect( strategySelector, DB2400Dialect.class );
 		addDialect( strategySelector, DerbyTenFiveDialect.class );
 		addDialect( strategySelector, DerbyTenSixDialect.class );
 		addDialect( strategySelector, DerbyTenSevenDialect.class );
 		addDialect( strategySelector, FirebirdDialect.class );
 		addDialect( strategySelector, FrontBaseDialect.class );
 		addDialect( strategySelector, H2Dialect.class );
 		addDialect( strategySelector, HSQLDialect.class );
 		addDialect( strategySelector, InformixDialect.class );
 		addDialect( strategySelector, IngresDialect.class );
 		addDialect( strategySelector, Ingres9Dialect.class );
 		addDialect( strategySelector, Ingres10Dialect.class );
 		addDialect( strategySelector, InterbaseDialect.class );
 		addDialect( strategySelector, JDataStoreDialect.class );
 		addDialect( strategySelector, MckoiDialect.class );
 		addDialect( strategySelector, MimerSQLDialect.class );
 		addDialect( strategySelector, MySQL5Dialect.class );
 		addDialect( strategySelector, MySQL5InnoDBDialect.class );
 		addDialect( strategySelector, MySQL5Dialect.class );
 		addDialect( strategySelector, MySQL5InnoDBDialect.class );
 		addDialect( strategySelector, Oracle8iDialect.class );
 		addDialect( strategySelector, Oracle9iDialect.class );
 		addDialect( strategySelector, Oracle10gDialect.class );
 		addDialect( strategySelector, PointbaseDialect.class );
 		addDialect( strategySelector, PostgresPlusDialect.class );
 		addDialect( strategySelector, PostgreSQL81Dialect.class );
 		addDialect( strategySelector, PostgreSQL82Dialect.class );
 		addDialect( strategySelector, PostgreSQL9Dialect.class );
 		addDialect( strategySelector, ProgressDialect.class );
 		addDialect( strategySelector, SAPDBDialect.class );
 		addDialect( strategySelector, SQLServerDialect.class );
 		addDialect( strategySelector, SQLServer2005Dialect.class );
 		addDialect( strategySelector, SQLServer2008Dialect.class );
 		addDialect( strategySelector, Sybase11Dialect.class );
 		addDialect( strategySelector, SybaseAnywhereDialect.class );
 		addDialect( strategySelector, SybaseASE15Dialect.class );
 		addDialect( strategySelector, SybaseASE157Dialect.class );
 		addDialect( strategySelector, TeradataDialect.class );
 		addDialect( strategySelector, TimesTenDialect.class );
 	}
 
 	private void addDialect(StrategySelectorImpl strategySelector, Class<? extends Dialect> dialectClass) {
 		String simpleName = dialectClass.getSimpleName();
 		if ( simpleName.endsWith( "Dialect" ) ) {
 			simpleName = simpleName.substring( 0, simpleName.length() - "Dialect".length() );
 		}
 		strategySelector.registerStrategyImplementor( Dialect.class, simpleName, dialectClass );
 	}
 
 	private void addJtaPlatforms(StrategySelectorImpl strategySelector) {
 		addJtaPlatforms(
 				strategySelector,
 				BorlandEnterpriseServerJtaPlatform.class,
 				"Borland",
 				"org.hibernate.service.jta.platform.internal.BorlandEnterpriseServerJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				BitronixJtaPlatform.class,
 				"Bitronix",
 				"org.hibernate.service.jta.platform.internal.BitronixJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JBossAppServerJtaPlatform.class,
 				"JBossAS",
 				"org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JBossStandAloneJtaPlatform.class,
 				"JBossTS",
 				"org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JOnASJtaPlatform.class,
 				"JOnAS",
 				"org.hibernate.service.jta.platform.internal.JOnASJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JOTMJtaPlatform.class,
 				"JOTM",
 				"org.hibernate.service.jta.platform.internal.JOTMJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JRun4JtaPlatform.class,
 				"JRun4",
 				"org.hibernate.service.jta.platform.internal.JRun4JtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				OC4JJtaPlatform.class,
 				"OC4J",
 				"org.hibernate.service.jta.platform.internal.OC4JJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				OrionJtaPlatform.class,
 				"Orion",
 				"org.hibernate.service.jta.platform.internal.OrionJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				ResinJtaPlatform.class,
 				"Resin",
 				"org.hibernate.service.jta.platform.internal.ResinJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				SunOneJtaPlatform.class,
 				"SunOne",
 				"org.hibernate.service.jta.platform.internal.SunOneJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				WeblogicJtaPlatform.class,
 				"Weblogic",
 				"org.hibernate.service.jta.platform.internal.WeblogicJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				WebSphereJtaPlatform.class,
 				"WebSphere",
 				"org.hibernate.service.jta.platform.internal.WebSphereJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				WebSphereExtendedJtaPlatform.class,
 				"WebSphereExtended",
 				"org.hibernate.service.jta.platform.internal.WebSphereExtendedJtaPlatform"
 		);
 	}
 
 	private void addJtaPlatforms(StrategySelectorImpl strategySelector, Class<? extends JtaPlatform> impl, String... names) {
 		for ( String name : names ) {
 			strategySelector.registerStrategyImplementor( JtaPlatform.class, name, impl );
 		}
 	}
 
 	private void addTransactionFactories(StrategySelectorImpl strategySelector) {
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, JdbcTransactionFactory.SHORT_NAME, JdbcTransactionFactory.class );
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, "org.hibernate.transaction.JDBCTransactionFactory", JdbcTransactionFactory.class );
 
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, JtaTransactionFactory.SHORT_NAME, JtaTransactionFactory.class );
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, "org.hibernate.transaction.JTATransactionFactory", JtaTransactionFactory.class );
 
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, CMTTransactionFactory.SHORT_NAME, CMTTransactionFactory.class );
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, "org.hibernate.transaction.CMTTransactionFactory", CMTTransactionFactory.class );
 	}
 
 	private void addMultiTableBulkIdStrategies(StrategySelectorImpl strategySelector) {
 		strategySelector.registerStrategyImplementor(
 				MultiTableBulkIdStrategy.class,
 				PersistentTableBulkIdStrategy.SHORT_NAME,
 				PersistentTableBulkIdStrategy.class
 		);
 		strategySelector.registerStrategyImplementor(
 				MultiTableBulkIdStrategy.class,
+				GlobalTemporaryTableBulkIdStrategy.SHORT_NAME,
+				GlobalTemporaryTableBulkIdStrategy.class
+		);
+		strategySelector.registerStrategyImplementor(
+				MultiTableBulkIdStrategy.class,
+				LocalTemporaryTableBulkIdStrategy.SHORT_NAME,
+				LocalTemporaryTableBulkIdStrategy.class
+		);
+		strategySelector.registerStrategyImplementor(
+				MultiTableBulkIdStrategy.class,
 				TemporaryTableBulkIdStrategy.SHORT_NAME,
 				TemporaryTableBulkIdStrategy.class
 		);
 	}
 
 	private void addEntityCopyObserverStrategies(StrategySelectorImpl strategySelector) {
 		strategySelector.registerStrategyImplementor(
 				EntityCopyObserver.class,
 				EntityCopyNotAllowedObserver.SHORT_NAME,
 				EntityCopyNotAllowedObserver.class
 		);
 		strategySelector.registerStrategyImplementor(
 				EntityCopyObserver.class,
 				EntityCopyAllowedObserver.SHORT_NAME,
 				EntityCopyAllowedObserver.class
 		);
 		strategySelector.registerStrategyImplementor(
 				EntityCopyObserver.class,
 				EntityCopyAllowedLoggedObserver.SHORT_NAME,
 				EntityCopyAllowedLoggedObserver.class
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index 69926a756a..1cfb478853 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,721 +1,722 @@
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
+
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
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.TopLimitHandler;
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
 
 	private final TopLimitHandler limitHandler;
 
 	/**
 	 * Creates new <code>Cache71Dialect</code> instance. Sets up the JDBC /
 	 * Cach&eacute; type mappings.
 	 */
 	public Cache71Dialect() {
 		super();
 		commonRegistration();
 		register71Functions();
 		this.limitHandler = new TopLimitHandler(true, true);
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
 	public LimitHandler getLimitHandler() {
 		return limitHandler;
 	}
 
 	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 8dda1949c4..cddb071ce6 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,2518 +1,2528 @@
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.ScrollMode;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Sequence;
 import org.hibernate.cfg.Environment;
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
 import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
 import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
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
 
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.NClob;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
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
 		sqlFunctions.put( name.toLowerCase(Locale.ROOT), function );
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
 	 * Returns the delegate managing LIMIT clause.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler getLimitHandler() {
 		return new LegacyLimitHandler( this );
 	}
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
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
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
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
 
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		// mimic the old behavior for now...
+		return supportsTemporaryTables()
+				? LocalTemporaryTableBulkIdStrategy.INSTANCE
+				: new PersistentTableBulkIdStrategy();
+	}
+
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
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied before the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the constraint name
 	 */
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied after the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the constraint name
 	 */
 	public boolean supportsIfExistsAfterConstraintName() {
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
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase(Locale.ROOT) );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
index bd5112e908..c57be684dd 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
@@ -1,684 +1,691 @@
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
 import java.util.Locale;
 
 import org.hibernate.JDBCException;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
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
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			sql = sql.trim();
 			boolean isForUpdate = false;
 			if (sql.toLowerCase(Locale.ROOT
                         ).endsWith( " for update" )) {
 				sql = sql.substring( 0, sql.length() - 11 );
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
 				pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 			}
 			else {
 				pagingSelect.append( " ) where rownum <= ?" );
 			}
 
 			if (isForUpdate) {
 				pagingSelect.append( " for update" );
 			}
 
 			return pagingSelect.toString();
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 	};
 
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
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase(Locale.ROOT).endsWith( " for update" ) ) {
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
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy();
+	}
+
+	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
index fdc9f843a7..51c83a1246 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
@@ -1,414 +1,421 @@
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
 import java.util.Locale;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.jboss.logging.Logger;
 
 /**
  * An SQL dialect for Oracle 9 (uses ANSI-style syntax where possible).
  *
  * @author Gavin King
  * @author David Channon
  *
  * @deprecated Use either Oracle9iDialect or Oracle10gDialect instead
  */
 @SuppressWarnings("deprecation")
 @Deprecated
 public class Oracle9Dialect extends Dialect {
 
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			Oracle9Dialect.class.getName()
 	);
 
 	/**
 	 * Constructs a Oracle9Dialect
 	 */
 	public Oracle9Dialect() {
 		super();
 		LOG.deprecatedOracle9Dialect();
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 		registerColumnType( Types.CHAR, "char(1 char)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l char)" );
 		registerColumnType( Types.VARCHAR, "long" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "stddev", new StandardSQLFunction( "stddev", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "variance", new StandardSQLFunction( "variance", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "initcap", new StandardSQLFunction( "initcap" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
 
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIME, false ) );
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction(
 				"current_timestamp",
 				StandardBasicTypes.TIMESTAMP,
 				false
 		)
 		);
 
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 		registerFunction( "systimestamp", new NoArgSQLFunction( "systimestamp", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "uid", new NoArgSQLFunction( "uid", StandardBasicTypes.INTEGER, false ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 
 		registerFunction( "rowid", new NoArgSQLFunction( "rowid", StandardBasicTypes.LONG, false ) );
 		registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.LONG, false ) );
 
 		// Multi-param string dialect functions...
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.INTEGER ) );
 		registerFunction( "instrb", new StandardSQLFunction( "instrb", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "substrb", new StandardSQLFunction( "substrb", StandardBasicTypes.STRING ) );
 		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.FLOAT ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.INTEGER ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 		registerFunction( "nvl2", new StandardSQLFunction( "nvl2" ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.FLOAT ) );
 
 		// Multi-param date dialect functions...
 		registerFunction( "add_months", new StandardSQLFunction( "add_months", StandardBasicTypes.DATE ) );
 		registerFunction( "months_between", new StandardSQLFunction( "months_between", StandardBasicTypes.FLOAT ) );
 		registerFunction( "next_day", new StandardSQLFunction( "next_day", StandardBasicTypes.DATE ) );
 
 		registerFunction( "str", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 	}
 
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
 	public String getLimitString(String sql, boolean hasOffset) {
 
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase(Locale.ROOT).endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length() - 11 );
 			isForUpdate = true;
 		}
 
 		final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
 		if ( hasOffset ) {
 			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append( "select * from ( " );
 		}
 		pagingSelect.append( sql );
 		if ( hasOffset ) {
 			pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
 		}
 		else {
 			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
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
 		return "select sequence_name from user_sequences";
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
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
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
 	public int registerResultSetOutParameter(java.sql.CallableStatement statement, int col) throws SQLException {
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
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy();
+	}
+
+	@Override
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
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select systimestamp from dual";
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
 	public String getNotExpression(String expression) {
 		return "not (" + expression + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/GlobalTemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/GlobalTemporaryTableBulkIdStrategy.java
new file mode 100644
index 0000000000..04f2390384
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/GlobalTemporaryTableBulkIdStrategy.java
@@ -0,0 +1,165 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.hql.spi;
+
+import java.sql.PreparedStatement;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.engine.config.spi.ConfigurationService;
+import org.hibernate.engine.config.spi.StandardConverters;
+import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.Queryable;
+
+/**
+ * Strategy based on ANSI SQL's definition of a "global temporary table".
+ *
+ * @author Steve Ebersole
+ */
+public class GlobalTemporaryTableBulkIdStrategy implements MultiTableBulkIdStrategy {
+	public static final String CLEAN_UP_ID_TABLES = "hibernate.hql.bulk_id_strategy.global_temporary.clean_up";
+
+	public static final String SHORT_NAME = "global_temporary";
+
+	private boolean cleanUpTables;
+	private List<String> tableCleanUpDdl;
+
+	@Override
+	public void prepare(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata) {
+		final ConfigurationService configService = metadata.getMetadataBuildingOptions()
+				.getServiceRegistry()
+				.getService( ConfigurationService.class );
+		this.cleanUpTables = configService.getSetting(
+				CLEAN_UP_ID_TABLES,
+				StandardConverters.BOOLEAN,
+				false
+		);
+
+		final List<Table> idTableDefinitions = new ArrayList<Table>();
+
+		for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
+			if ( !MultiTableBulkIdHelper.INSTANCE.needsIdTable( entityBinding ) ) {
+				continue;
+			}
+			final Table idTableDefinition = generateIdTableDefinition( entityBinding, metadata );
+			idTableDefinitions.add( idTableDefinition );
+
+			if ( cleanUpTables ) {
+				if ( tableCleanUpDdl == null ) {
+					tableCleanUpDdl = new ArrayList<String>();
+				}
+				tableCleanUpDdl.add( idTableDefinition.sqlDropString( jdbcServices.getDialect(), null, null  ) );
+			}
+		}
+
+		// we export them all at once to better reuse JDBC resources
+		exportTableDefinitions( idTableDefinitions, jdbcServices, connectionAccess, metadata );
+	}
+
+	protected Table generateIdTableDefinition(PersistentClass entityMapping, MetadataImplementor metadata) {
+		return MultiTableBulkIdHelper.INSTANCE.generateIdTableDefinition(
+				entityMapping,
+				null,
+				null,
+				false
+		);
+	}
+
+	protected void exportTableDefinitions(
+			List<Table> idTableDefinitions,
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata) {
+		MultiTableBulkIdHelper.INSTANCE.exportTableDefinitions(
+				idTableDefinitions,
+				jdbcServices,
+				connectionAccess,
+				metadata
+		);
+	}
+
+	@Override
+	public void release(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess) {
+		if ( ! cleanUpTables ) {
+			return;
+		}
+
+		MultiTableBulkIdHelper.INSTANCE.cleanupTableDefinitions( jdbcServices, connectionAccess, tableCleanUpDdl );
+	}
+
+	@Override
+	public UpdateHandler buildUpdateHandler(
+			SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedUpdateHandlerImpl( factory, walker ) {
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				// clean up our id-table rows
+				cleanUpRows( determineIdTableName( persister ), session );
+			}
+		};
+	}
+
+	private void cleanUpRows(String tableName, SessionImplementor session) {
+		final String sql = "delete from " + tableName;
+		PreparedStatement ps = null;
+		try {
+			ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
+			session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
+		}
+		finally {
+			if ( ps != null ) {
+				try {
+					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
+				}
+				catch( Throwable ignore ) {
+					// ignore
+				}
+			}
+		}
+	}
+
+	@Override
+	public DeleteHandler buildDeleteHandler(
+			SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedDeleteHandlerImpl( factory, walker ) {
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				// clean up our id-table rows
+				cleanUpRows( determineIdTableName( persister ), session );
+			}
+		};
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/LocalTemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/LocalTemporaryTableBulkIdStrategy.java
new file mode 100644
index 0000000000..1c8b39e930
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/LocalTemporaryTableBulkIdStrategy.java
@@ -0,0 +1,263 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.hql.spi;
+
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.SQLWarning;
+import java.sql.Statement;
+
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.jdbc.AbstractWork;
+import org.hibernate.persister.entity.Queryable;
+
+import org.jboss.logging.Logger;
+
+/**
+ * Strategy based on ANSI SQL's definition of a "local temporary table" (local to each db session).
+ *
+ * @author Steve Ebersole
+ */
+public class LocalTemporaryTableBulkIdStrategy implements MultiTableBulkIdStrategy {
+	public static final LocalTemporaryTableBulkIdStrategy INSTANCE = new LocalTemporaryTableBulkIdStrategy();
+
+	public static final String SHORT_NAME = "temporary";
+
+	private static final CoreMessageLogger log = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			LocalTemporaryTableBulkIdStrategy.class.getName()
+	);
+
+	@Override
+	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata) {
+		// nothing to do
+	}
+
+	@Override
+	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
+		// nothing to do
+	}
+
+	@Override
+	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedUpdateHandlerImpl( factory, walker ) {
+			@Override
+			protected void prepareForUse(Queryable persister, SessionImplementor session) {
+				createTempTable( persister, session );
+			}
+
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				releaseTempTable( persister, session );
+			}
+		};
+	}
+
+	@Override
+	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedDeleteHandlerImpl( factory, walker ) {
+			@Override
+			protected void prepareForUse(Queryable persister, SessionImplementor session) {
+				createTempTable( persister, session );
+			}
+
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				releaseTempTable( persister, session );
+			}
+		};
+	}
+
+
+	protected void createTempTable(Queryable persister, SessionImplementor session) {
+		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
+		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
+		TemporaryTableCreationWork work = new TemporaryTableCreationWork( persister );
+		if ( shouldIsolateTemporaryTableDDL( session ) ) {
+			session.getTransactionCoordinator()
+					.getTransaction()
+					.createIsolationDelegate()
+					.delegateWork( work, shouldTransactIsolatedTemporaryTableDDL( session ) );
+		}
+		else {
+			final Connection connection = session.getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.getLogicalConnection()
+					.getConnection();
+			work.execute( connection );
+			session.getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.afterStatementExecution();
+		}
+	}
+
+	protected void releaseTempTable(Queryable persister, SessionImplementor session) {
+		if ( session.getFactory().getDialect().dropTemporaryTableAfterUse() ) {
+			TemporaryTableDropWork work = new TemporaryTableDropWork( persister, session );
+			if ( shouldIsolateTemporaryTableDDL( session ) ) {
+				session.getTransactionCoordinator()
+						.getTransaction()
+						.createIsolationDelegate()
+						.delegateWork( work, shouldTransactIsolatedTemporaryTableDDL( session ) );
+			}
+			else {
+				final Connection connection = session.getTransactionCoordinator()
+						.getJdbcCoordinator()
+						.getLogicalConnection()
+						.getConnection();
+				work.execute( connection );
+				session.getTransactionCoordinator()
+						.getJdbcCoordinator()
+						.afterStatementExecution();
+			}
+		}
+		else {
+			// at the very least cleanup the data :)
+			PreparedStatement ps = null;
+			try {
+				final String sql = "delete from " + persister.getTemporaryIdTableName();
+				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
+				session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
+			}
+			catch( Throwable t ) {
+				log.unableToCleanupTemporaryIdTable(t);
+			}
+			finally {
+				if ( ps != null ) {
+					try {
+						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
+					}
+					catch( Throwable ignore ) {
+						// ignore
+					}
+				}
+			}
+		}
+	}
+
+	protected boolean shouldIsolateTemporaryTableDDL(SessionImplementor session) {
+		Boolean dialectVote = session.getFactory().getDialect().performTemporaryTableDDLInIsolation();
+		if ( dialectVote != null ) {
+			return dialectVote;
+		}
+		return session.getFactory().getSettings().isDataDefinitionImplicitCommit();
+	}
+
+	protected boolean shouldTransactIsolatedTemporaryTableDDL(SessionImplementor session) {
+		// is there ever a time when it makes sense to do this?
+//		return session.getFactory().getSettings().isDataDefinitionInTransactionSupported();
+		return false;
+	}
+
+	private static class TemporaryTableCreationWork extends AbstractWork {
+		private final Queryable persister;
+
+		private TemporaryTableCreationWork(Queryable persister) {
+			this.persister = persister;
+		}
+
+		@Override
+		public void execute(Connection connection) {
+			try {
+				Statement statement = connection.createStatement();
+				try {
+					statement.executeUpdate( persister.getTemporaryIdTableDDL() );
+					persister.getFactory()
+							.getServiceRegistry()
+							.getService( JdbcServices.class )
+							.getSqlExceptionHelper()
+							.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
+				}
+				finally {
+					try {
+						statement.close();
+					}
+					catch( Throwable ignore ) {
+						// ignore
+					}
+				}
+			}
+			catch( Exception e ) {
+				log.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
+			}
+		}
+	}
+
+	private static SqlExceptionHelper.WarningHandler CREATION_WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport() {
+		public boolean doProcess() {
+			return log.isDebugEnabled();
+		}
+
+		public void prepare(SQLWarning warning) {
+			log.warningsCreatingTempTable( warning );
+		}
+
+		@Override
+		protected void logWarning(String description, String message) {
+			log.debug( description );
+			log.debug( message );
+		}
+	};
+
+	private static class TemporaryTableDropWork extends AbstractWork {
+		private final Queryable persister;
+		private final SessionImplementor session;
+
+		private TemporaryTableDropWork(Queryable persister, SessionImplementor session) {
+			this.persister = persister;
+			this.session = session;
+		}
+
+		@Override
+		public void execute(Connection connection) {
+			final String command = session.getFactory().getDialect().getDropTemporaryTableString()
+					+ ' ' + persister.getTemporaryIdTableName();
+			try {
+				Statement statement = connection.createStatement();
+				try {
+					statement.executeUpdate( command );
+				}
+				finally {
+					try {
+						statement.close();
+					}
+					catch( Throwable ignore ) {
+						// ignore
+					}
+				}
+			}
+			catch( Exception e ) {
+				log.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
+			}
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdHelper.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdHelper.java
new file mode 100644
index 0000000000..e8fd8fdb19
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdHelper.java
@@ -0,0 +1,207 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.hql.spi;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+import java.sql.Statement;
+import java.util.Iterator;
+import java.util.List;
+
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.JoinedSubclass;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.RootClass;
+import org.hibernate.mapping.Subclass;
+import org.hibernate.mapping.Table;
+import org.hibernate.mapping.UnionSubclass;
+
+import org.jboss.logging.Logger;
+
+/**
+ * @author Steve Ebersole
+ */
+public class MultiTableBulkIdHelper {
+	private static final Logger log = Logger.getLogger( MultiTableBulkIdHelper.class );
+
+	/**
+	 * Singleton access
+	 */
+	public static final MultiTableBulkIdHelper INSTANCE = new MultiTableBulkIdHelper();
+
+	private MultiTableBulkIdHelper() {
+	}
+
+	public boolean needsIdTable(PersistentClass entityBinding) {
+		// need id table if the entity has secondary tables (joins)
+		if ( entityBinding.getJoinClosureSpan() > 0 ) {
+			return true;
+		}
+
+		// need an id table if the entity is part of either a JOINED or UNION inheritance
+		// hierarchy.  We do not allow inheritance strategy mixing, so work on that assumption
+		// here...
+		final RootClass rootEntityBinding = entityBinding.getRootClass();
+		final Iterator itr = rootEntityBinding.getSubclassIterator();
+		if ( itr.hasNext() ) {
+			final Subclass subclassEntityBinding = (Subclass) itr.next();
+			if ( subclassEntityBinding instanceof JoinedSubclass || subclassEntityBinding instanceof UnionSubclass ) {
+				return true;
+			}
+		}
+
+		return false;
+	}
+
+
+	public Table generateIdTableDefinition(
+			PersistentClass entityMapping,
+			String catalog,
+			String schema,
+			boolean generateSessionIdColumn) {
+		Table idTable = new Table( entityMapping.getTemporaryIdTableName() );
+		idTable.setComment( "Used to hold id values for the " + entityMapping.getEntityName() + " class" );
+
+		if ( catalog != null ) {
+			idTable.setCatalog( catalog );
+		}
+		if ( schema != null ) {
+			idTable.setSchema( schema );
+		}
+
+		Iterator itr = entityMapping.getTable().getPrimaryKey().getColumnIterator();
+		while( itr.hasNext() ) {
+			Column column = (Column) itr.next();
+			idTable.addColumn( column.clone()  );
+		}
+
+		if ( generateSessionIdColumn ) {
+			Column sessionIdColumn = new Column( "hib_sess_id" );
+			sessionIdColumn.setSqlType( "CHAR(36)" );
+			sessionIdColumn.setComment( "Used to hold the Hibernate Session identifier" );
+			idTable.addColumn( sessionIdColumn );
+		}
+
+		return idTable;
+	}
+
+	protected void exportTableDefinitions(
+			List<Table> idTableDefinitions,
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata) {
+		try {
+			Connection connection;
+			try {
+				connection = connectionAccess.obtainConnection();
+			}
+			catch (UnsupportedOperationException e) {
+				// assume this comes from org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl
+				log.debug( "Unable to obtain JDBC connection; assuming ID tables already exist or wont be needed" );
+				return;
+			}
+
+			try {
+				// TODO: session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().createStatement();
+				Statement statement = connection.createStatement();
+				for ( Table idTableDefinition : idTableDefinitions ) {
+					try {
+						final String sql = idTableDefinition.sqlCreateString( jdbcServices.getDialect(), metadata, null, null );
+						jdbcServices.getSqlStatementLogger().logStatement( sql );
+						// TODO: ResultSetExtractor
+						statement.execute( sql );
+					}
+					catch (SQLException e) {
+						log.debugf( "Error attempting to export id-table [%s] : %s", idTableDefinition.getName(), e.getMessage() );
+					}
+				}
+
+				// TODO
+//				session.getTransactionCoordinator().getJdbcCoordinator().release( statement );
+				statement.close();
+			}
+			catch (SQLException e) {
+				log.error( "Unable to use JDBC Connection to create Statement", e );
+			}
+			finally {
+				try {
+					connectionAccess.releaseConnection( connection );
+				}
+				catch (SQLException ignore) {
+				}
+			}
+		}
+		catch (SQLException e) {
+			log.error( "Unable obtain JDBC Connection", e );
+		}
+	}
+
+	public void cleanupTableDefinitions(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			List<String> tableCleanUpDdl) {
+		if ( tableCleanUpDdl == null ) {
+			return;
+		}
+
+		try {
+			Connection connection = connectionAccess.obtainConnection();
+
+			try {
+				// TODO: session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().createStatement();
+				Statement statement = connection.createStatement();
+
+				for ( String cleanupDdl : tableCleanUpDdl ) {
+					try {
+						jdbcServices.getSqlStatementLogger().logStatement( cleanupDdl );
+						statement.execute( cleanupDdl );
+					}
+					catch (SQLException e) {
+						log.debugf( "Error attempting to cleanup id-table : [%s]", e.getMessage() );
+					}
+				}
+
+				// TODO
+//				session.getTransactionCoordinator().getJdbcCoordinator().release( statement );
+				statement.close();
+			}
+			catch (SQLException e) {
+				log.error( "Unable to use JDBC Connection to create Statement", e );
+			}
+			finally {
+				try {
+					connectionAccess.releaseConnection( connection );
+				}
+				catch (SQLException ignore) {
+				}
+			}
+		}
+		catch (SQLException e) {
+			log.error( "Unable obtain JDBC Connection", e );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java
index 8535775854..29f24cd4e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java
@@ -1,331 +1,259 @@
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
 package org.hibernate.hql.spi;
 
-import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
-import java.sql.Statement;
 import java.sql.Types;
 import java.util.ArrayList;
-import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.internal.AbstractSessionImpl;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.SelectValues;
 import org.hibernate.type.UUIDCharType;
 
 import org.jboss.logging.Logger;
 
 /**
+ * This is a strategy that mimics temporary tables for databases which do not support
+ * temporary tables.  It follows a pattern similar to the ANSI SQL definition of global
+ * temporary table using a "session id" column to segment rows from the various sessions.
+ *
  * @author Steve Ebersole
  */
 public class PersistentTableBulkIdStrategy implements MultiTableBulkIdStrategy {
 	private static final CoreMessageLogger log = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			PersistentTableBulkIdStrategy.class.getName()
 	);
 
 	public static final String SHORT_NAME = "persistent";
 
 	public static final String CLEAN_UP_ID_TABLES = "hibernate.hql.bulk_id_strategy.persistent.clean_up";
 	public static final String SCHEMA = "hibernate.hql.bulk_id_strategy.persistent.schema";
 	public static final String CATALOG = "hibernate.hql.bulk_id_strategy.persistent.catalog";
 
 	private String catalog;
 	private String schema;
 	private boolean cleanUpTables;
 	private List<String> tableCleanUpDdl;
 
+	/**
+	 * Creates the tables for all the entities that might need it
+	 *
+	 * @param jdbcServices The JdbcService object
+	 * @param connectionAccess Access to the JDBC Connection
+	 * @param metadata Access to the O/RM mapping information
+	 */
 	@Override
 	public void prepare(
 			JdbcServices jdbcServices,
 			JdbcConnectionAccess connectionAccess,
 			MetadataImplementor metadata) {
 		final ConfigurationService configService = metadata.getMetadataBuildingOptions()
 				.getServiceRegistry()
 				.getService( ConfigurationService.class );
 		this.catalog = configService.getSetting(
 				CATALOG,
 				StandardConverters.STRING,
 				configService.getSetting( AvailableSettings.DEFAULT_CATALOG, StandardConverters.STRING )
 		);
 		this.schema = configService.getSetting(
 				SCHEMA,
 				StandardConverters.STRING,
 				configService.getSetting( AvailableSettings.DEFAULT_SCHEMA, StandardConverters.STRING )
 		);
 		this.cleanUpTables = configService.getSetting(
 				CLEAN_UP_ID_TABLES,
 				StandardConverters.BOOLEAN,
 				false
 		);
 
 		final List<Table> idTableDefinitions = new ArrayList<Table>();
 
 		for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
+			if ( !MultiTableBulkIdHelper.INSTANCE.needsIdTable( entityBinding ) ) {
+				continue;
+			}
 			final Table idTableDefinition = generateIdTableDefinition( entityBinding, metadata );
 			idTableDefinitions.add( idTableDefinition );
+
+			if ( cleanUpTables ) {
+				if ( tableCleanUpDdl == null ) {
+					tableCleanUpDdl = new ArrayList<String>();
+				}
+				tableCleanUpDdl.add( idTableDefinition.sqlDropString( jdbcServices.getDialect(), null, null  ) );
+			}
 		}
+
+		// we export them all at once to better reuse JDBC resources
 		exportTableDefinitions( idTableDefinitions, jdbcServices, connectionAccess, metadata );
 	}
 
 	protected Table generateIdTableDefinition(PersistentClass entityMapping, MetadataImplementor metadata) {
-		Table idTable = new Table( entityMapping.getTemporaryIdTableName() );
-		if ( catalog != null ) {
-			idTable.setCatalog( catalog );
-		}
-		if ( schema != null ) {
-			idTable.setSchema( schema );
-		}
-		Iterator itr = entityMapping.getTable().getPrimaryKey().getColumnIterator();
-		while( itr.hasNext() ) {
-			Column column = (Column) itr.next();
-			idTable.addColumn( column.clone()  );
-		}
-		Column sessionIdColumn = new Column( "hib_sess_id" );
-		sessionIdColumn.setSqlType( "CHAR(36)" );
-		sessionIdColumn.setComment( "Used to hold the Hibernate Session identifier" );
-		idTable.addColumn( sessionIdColumn );
-
-		idTable.setComment( "Used to hold id values for the " + entityMapping.getEntityName() + " class" );
-		return idTable;
+		return MultiTableBulkIdHelper.INSTANCE.generateIdTableDefinition(
+				entityMapping,
+				catalog,
+				schema,
+				true
+		);
 	}
 
 	protected void exportTableDefinitions(
 			List<Table> idTableDefinitions,
 			JdbcServices jdbcServices,
 			JdbcConnectionAccess connectionAccess,
 			MetadataImplementor metadata) {
-		try {
-			Connection connection;
-			try {
-				connection = connectionAccess.obtainConnection();
-			}
-			catch (UnsupportedOperationException e) {
-				// assume this comes from org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl
-				log.debug( "Unable to obtain JDBC connection; assuming ID tables already exist or wont be needed" );
-				return;
-			}
-
-			try {
-				// TODO: session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().createStatement();
-				Statement statement = connection.createStatement();
-				for ( Table idTableDefinition : idTableDefinitions ) {
-					if ( cleanUpTables ) {
-						if ( tableCleanUpDdl == null ) {
-							tableCleanUpDdl = new ArrayList<String>();
-						}
-						tableCleanUpDdl.add( idTableDefinition.sqlDropString( jdbcServices.getDialect(), null, null  ) );
-					}
-					try {
-						final String sql = idTableDefinition.sqlCreateString( jdbcServices.getDialect(), metadata, null, null );
-						jdbcServices.getSqlStatementLogger().logStatement( sql );
-						// TODO: ResultSetExtractor
-						statement.execute( sql );
-					}
-					catch (SQLException e) {
-						log.debugf( "Error attempting to export id-table [%s] : %s", idTableDefinition.getName(), e.getMessage() );
-					}
-				}
-				
-				// TODO
-//				session.getTransactionCoordinator().getJdbcCoordinator().release( statement );
-				statement.close();
-			}
-			catch (SQLException e) {
-				log.error( "Unable to use JDBC Connection to create Statement", e );
-			}
-			finally {
-				try {
-					connectionAccess.releaseConnection( connection );
-				}
-				catch (SQLException ignore) {
-				}
-			}
-		}
-		catch (SQLException e) {
-			log.error( "Unable obtain JDBC Connection", e );
-		}
+		MultiTableBulkIdHelper.INSTANCE.exportTableDefinitions(
+				idTableDefinitions,
+				jdbcServices,
+				connectionAccess,
+				metadata
+		);
 	}
 
 	@Override
 	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
-		if ( ! cleanUpTables || tableCleanUpDdl == null ) {
+		if ( ! cleanUpTables ) {
 			return;
 		}
 
-		try {
-			Connection connection = connectionAccess.obtainConnection();
-
-			try {
-				// TODO: session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().createStatement();
-				Statement statement = connection.createStatement();
-
-				for ( String cleanupDdl : tableCleanUpDdl ) {
-					try {
-						jdbcServices.getSqlStatementLogger().logStatement( cleanupDdl );
-						statement.execute( cleanupDdl );
-					}
-					catch (SQLException e) {
-						log.debugf( "Error attempting to cleanup id-table : [%s]", e.getMessage() );
-					}
-				}
-				
-				// TODO
-//				session.getTransactionCoordinator().getJdbcCoordinator().release( statement );
-				statement.close();
-			}
-			catch (SQLException e) {
-				log.error( "Unable to use JDBC Connection to create Statement", e );
-			}
-			finally {
-				try {
-					connectionAccess.releaseConnection( connection );
-				}
-				catch (SQLException ignore) {
-				}
-			}
-		}
-		catch (SQLException e) {
-			log.error( "Unable obtain JDBC Connection", e );
-		}
+		MultiTableBulkIdHelper.INSTANCE.cleanupTableDefinitions( jdbcServices, connectionAccess, tableCleanUpDdl );
 	}
 
 	@Override
 	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
 		return new TableBasedUpdateHandlerImpl( factory, walker, catalog, schema ) {
 			@Override
 			protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
 				selectClause.addParameter( Types.CHAR, 36 );
 			}
 
 			@Override
 			protected String generateIdSubselect(Queryable persister) {
 				return super.generateIdSubselect( persister ) + " where hib_sess_id=?";
 			}
 
 			@Override
 			protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
 				bindSessionIdentifier( ps, session, pos );
 				return 1;
 			}
 
 			@Override
 			protected void handleAddedParametersOnUpdate(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
 				bindSessionIdentifier( ps, session, position );
 			}
 
 			@Override
 			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
 				// clean up our id-table rows
 				cleanUpRows( determineIdTableName( persister ), session );
 			}
 		};
 	}
 
 	private void bindSessionIdentifier(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
 		if ( ! AbstractSessionImpl.class.isInstance( session ) ) {
 			throw new HibernateException( "Only available on SessionImpl instances" );
 		}
 		UUIDCharType.INSTANCE.set( ps, ( (AbstractSessionImpl) session ).getSessionIdentifier(), position, session );
 	}
 
 	private void cleanUpRows(String tableName, SessionImplementor session) {
 		final String sql = "delete from " + tableName + " where hib_sess_id=?";
 		try {
 			PreparedStatement ps = null;
 			try {
 				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
 				bindSessionIdentifier( ps, session, 1 );
 				session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
 			}
 			finally {
 				if ( ps != null ) {
 					try {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 		}
 		catch (SQLException e) {
 			throw convert( session.getFactory(), e, "Unable to clean up id table [" + tableName + "]", sql );
 		}
 	}
 
 	protected JDBCException convert(SessionFactoryImplementor factory, SQLException e, String message, String sql) {
 		throw factory.getSQLExceptionHelper().convert( e, message, sql );
 	}
 
 	@Override
 	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
 		return new TableBasedDeleteHandlerImpl( factory, walker, catalog, schema ) {
 			@Override
 			protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
 				selectClause.addParameter( Types.CHAR, 36 );
 			}
 
 			@Override
 			protected String generateIdSubselect(Queryable persister) {
 				return super.generateIdSubselect( persister ) + " where hib_sess_id=?";
 			}
 
 			@Override
 			protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
 				bindSessionIdentifier( ps, session, pos );
 				return 1;
 			}
 
 			@Override
 			protected void handleAddedParametersOnDelete(PreparedStatement ps, SessionImplementor session) throws SQLException {
 				bindSessionIdentifier( ps, session, 1 );
 			}
 
 			@Override
 			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
 				// clean up our id-table rows
 				cleanUpRows( determineIdTableName( persister ), session );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
index 4d780a18d7..758b034646 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
@@ -1,261 +1,64 @@
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
 package org.hibernate.hql.spi;
 
-import java.sql.Connection;
-import java.sql.PreparedStatement;
-import java.sql.SQLWarning;
-import java.sql.Statement;
-
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.jdbc.AbstractWork;
-import org.hibernate.persister.entity.Queryable;
-
-import org.jboss.logging.Logger;
+import org.hibernate.internal.log.DeprecationLogger;
 
 /**
  * @author Steve Ebersole
+ *
+ * @deprecated Use the more specific {@link org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy} instead.
  */
-public class TemporaryTableBulkIdStrategy implements MultiTableBulkIdStrategy {
+@Deprecated
+public class TemporaryTableBulkIdStrategy extends LocalTemporaryTableBulkIdStrategy {
 	public static final TemporaryTableBulkIdStrategy INSTANCE = new TemporaryTableBulkIdStrategy();
 
 	public static final String SHORT_NAME = "temporary";
 
-	private static final CoreMessageLogger log = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			TemporaryTableBulkIdStrategy.class.getName()
-	);
-
 	@Override
 	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata) {
-		// nothing to do
+		DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfTemporaryTableBulkIdStrategy();
+		super.prepare( jdbcServices, connectionAccess, metadata );
 	}
 
 	@Override
 	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
-		// nothing to do
+		super.release( jdbcServices, connectionAccess );
 	}
 
 	@Override
 	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedUpdateHandlerImpl( factory, walker ) {
-			@Override
-			protected void prepareForUse(Queryable persister, SessionImplementor session) {
-				createTempTable( persister, session );
-			}
-
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				releaseTempTable( persister, session );
-			}
-		};
+		return super.buildUpdateHandler( factory, walker );
 	}
 
 	@Override
 	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedDeleteHandlerImpl( factory, walker ) {
-			@Override
-			protected void prepareForUse(Queryable persister, SessionImplementor session) {
-				createTempTable( persister, session );
-			}
-
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				releaseTempTable( persister, session );
-			}
-		};
-	}
-
-
-	protected void createTempTable(Queryable persister, SessionImplementor session) {
-		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
-		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
-		TemporaryTableCreationWork work = new TemporaryTableCreationWork( persister );
-		if ( shouldIsolateTemporaryTableDDL( session ) ) {
-			session.getTransactionCoordinator()
-					.getTransaction()
-					.createIsolationDelegate()
-					.delegateWork( work, shouldTransactIsolatedTemporaryTableDDL( session ) );
-		}
-		else {
-			final Connection connection = session.getTransactionCoordinator()
-					.getJdbcCoordinator()
-					.getLogicalConnection()
-					.getConnection();
-			work.execute( connection );
-			session.getTransactionCoordinator()
-					.getJdbcCoordinator()
-					.afterStatementExecution();
-		}
-	}
-
-	protected void releaseTempTable(Queryable persister, SessionImplementor session) {
-		if ( session.getFactory().getDialect().dropTemporaryTableAfterUse() ) {
-			TemporaryTableDropWork work = new TemporaryTableDropWork( persister, session );
-			if ( shouldIsolateTemporaryTableDDL( session ) ) {
-				session.getTransactionCoordinator()
-						.getTransaction()
-						.createIsolationDelegate()
-						.delegateWork( work, shouldTransactIsolatedTemporaryTableDDL( session ) );
-			}
-			else {
-				final Connection connection = session.getTransactionCoordinator()
-						.getJdbcCoordinator()
-						.getLogicalConnection()
-						.getConnection();
-				work.execute( connection );
-				session.getTransactionCoordinator()
-						.getJdbcCoordinator()
-						.afterStatementExecution();
-			}
-		}
-		else {
-			// at the very least cleanup the data :)
-			PreparedStatement ps = null;
-			try {
-				final String sql = "delete from " + persister.getTemporaryIdTableName();
-				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
-				session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
-			}
-			catch( Throwable t ) {
-				log.unableToCleanupTemporaryIdTable(t);
-			}
-			finally {
-				if ( ps != null ) {
-					try {
-						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
-					}
-					catch( Throwable ignore ) {
-						// ignore
-					}
-				}
-			}
-		}
+		return super.buildDeleteHandler( factory, walker );
 	}
-
-	protected boolean shouldIsolateTemporaryTableDDL(SessionImplementor session) {
-		Boolean dialectVote = session.getFactory().getDialect().performTemporaryTableDDLInIsolation();
-		if ( dialectVote != null ) {
-			return dialectVote;
-		}
-		return session.getFactory().getSettings().isDataDefinitionImplicitCommit();
-	}
-
-	protected boolean shouldTransactIsolatedTemporaryTableDDL(SessionImplementor session) {
-		// is there ever a time when it makes sense to do this?
-//		return session.getFactory().getSettings().isDataDefinitionInTransactionSupported();
-		return false;
-	}
-
-	private static class TemporaryTableCreationWork extends AbstractWork {
-		private final Queryable persister;
-
-		private TemporaryTableCreationWork(Queryable persister) {
-			this.persister = persister;
-		}
-
-		@Override
-		public void execute(Connection connection) {
-			try {
-				Statement statement = connection.createStatement();
-				try {
-					statement.executeUpdate( persister.getTemporaryIdTableDDL() );
-					persister.getFactory()
-							.getServiceRegistry()
-							.getService( JdbcServices.class )
-							.getSqlExceptionHelper()
-							.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
-				}
-				finally {
-					try {
-						statement.close();
-					}
-					catch( Throwable ignore ) {
-						// ignore
-					}
-				}
-			}
-			catch( Exception e ) {
-				log.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
-			}
-		}
-	}
-
-	private static SqlExceptionHelper.WarningHandler CREATION_WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport() {
-		public boolean doProcess() {
-			return log.isDebugEnabled();
-		}
-
-		public void prepare(SQLWarning warning) {
-			log.warningsCreatingTempTable( warning );
-		}
-
-		@Override
-		protected void logWarning(String description, String message) {
-			log.debug( description );
-			log.debug( message );
-		}
-	};
-
-	private static class TemporaryTableDropWork extends AbstractWork {
-		private final Queryable persister;
-		private final SessionImplementor session;
-
-		private TemporaryTableDropWork(Queryable persister, SessionImplementor session) {
-			this.persister = persister;
-			this.session = session;
-		}
-
-		@Override
-		public void execute(Connection connection) {
-			final String command = session.getFactory().getDialect().getDropTemporaryTableString()
-					+ ' ' + persister.getTemporaryIdTableName();
-			try {
-				Statement statement = connection.createStatement();
-				try {
-					statement.executeUpdate( command );
-				}
-				finally {
-					try {
-						statement.close();
-					}
-					catch( Throwable ignore ) {
-						// ignore
-					}
-				}
-			}
-			catch( Exception e ) {
-				log.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
-			}
-		}
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
index 0b9874171b..34a76b06f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
@@ -1,154 +1,163 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.internal.log;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.annotations.LogMessage;
 import org.jboss.logging.annotations.Message;
 import org.jboss.logging.annotations.MessageLogger;
 import org.jboss.logging.annotations.ValidIdRange;
 
 import static org.jboss.logging.Logger.Level.INFO;
 import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * Class to consolidate logging about usage of deprecated features.
  *
  * @author Steve Ebersole
  */
 @MessageLogger( projectCode = "HHH" )
 @ValidIdRange( min = 90000001, max = 90001000 )
 public interface DeprecationLogger {
 	public static final DeprecationLogger DEPRECATION_LOGGER = Logger.getMessageLogger(
 			DeprecationLogger.class,
 			"org.hibernate.orm.deprecation"
 	);
 
 	/**
 	 * Log about usage of deprecated Scanner setting
 	 */
 	@LogMessage( level = INFO )
 	@Message(
 			value = "Found usage of deprecated setting for specifying Scanner [hibernate.ejb.resource_scanner]; " +
 					"use [hibernate.archive.scanner] instead",
 			id = 90000001
 	)
 	public void logDeprecatedScannerSetting();
 
 	/**
 	 * Log message indicating the use of multiple EntityModes for a single entity.
 	 */
 	@LogMessage( level = WARN )
 	@Message(
 			value = "Support for an entity defining multiple entity-modes is deprecated",
 			id = 90000002
 	)
 	public void logDeprecationOfMultipleEntityModeSupport();
 
 	/**
 	 * Log message indicating the use of DOM4J EntityMode.
 	 */
 	@LogMessage( level = WARN )
 	@Message(
 			value = "Use of DOM4J entity-mode is considered deprecated",
 			id = 90000003
 	)
 	public void logDeprecationOfDomEntityModeSupport();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been " +
 					"removed, embed-xml attributes are no longer supported and should be removed from mappings.",
 			id = 90000004
 	)
 	public void logDeprecationOfEmbedXmlSupport();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Defining an entity [%s] with no physical id attribute is no longer supported; please map the " +
 					"identifier to a physical entity attribute",
 			id = 90000005
 	)
 	public void logDeprecationOfNonNamedIdAttribute(String entityName);
 
 	/**
 	 * Log a warning about an attempt to specify no-longer-supported NamingStrategy
 	 *
 	 * @param setting - The old setting that indicates the NamingStrategy to use
 	 * @param implicitInstead - The new setting that indicates the ImplicitNamingStrategy to use
 	 * @param physicalInstead - The new setting that indicates the PhysicalNamingStrategy to use
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via setting [%s]; NamingStrategy " +
 					"has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy; use [%s] or [%s], respectively, instead.",
 			id = 90000006
 	)
 	void logDeprecatedNamingStrategySetting(String setting, String implicitInstead, String physicalInstead);
 
 	/**
 	 * Log a warning about an attempt to specify unsupported NamingStrategy
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via command-line argument [--naming]. " +
 					"NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy; use [--implicit-naming] or [--physical-naming], respectively, instead.",
 			id = 90000007
 	)
 	void logDeprecatedNamingStrategyArgument();
 
 	/**
 	 * Log a warning about an attempt to specify unsupported NamingStrategy
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via Ant task argument. " +
 					"NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy.",
 			id = 90000008
 	)
 	void logDeprecatedNamingStrategyAntArgument();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "The outer-join attribute on <many-to-many> has been deprecated. " +
 					"Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, " +
 					"<bag>, <idbag>, or <list>, which will only initialize entities (not as " +
 					"a proxy) as needed.",
 			id = 90000009
 	)
 	void deprecatedManyToManyOuterJoin();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "The fetch attribute on <many-to-many> has been deprecated. " +
 					"Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, " +
 					"<bag>, <idbag>, or <list>, which will only initialize entities (not as " +
 					"a proxy) as needed.",
 			id = 90000010
 	)
 	void deprecatedManyToManyFetch();
+
+
+	@LogMessage(level = WARN)
+	@Message(
+			value = "org.hibernate.hql.spi.TemporaryTableBulkIdStrategy (temporary) has been deprecated in favor of the" +
+					" more specific org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy (local_temporary).",
+			id = 90000010
+	)
+	void logDeprecationOfTemporaryTableBulkIdStrategy();
 }
