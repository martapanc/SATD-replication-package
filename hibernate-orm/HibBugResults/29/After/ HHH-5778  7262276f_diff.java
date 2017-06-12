diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 68959ed419..5e8056a6b5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,509 +1,499 @@
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
 
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.engine.jdbc.JdbcSupport;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilder;
 import org.hibernate.hql.QueryTranslatorFactory;
-import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.jdbc.util.SQLStatementLogger;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.transaction.TransactionManagerLookup;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Settings that affect the behaviour of Hibernate at runtime.
  *
  * @author Gavin King
  */
 public final class Settings {
 
 //	private boolean showSql;
 //	private boolean formatSql;
 	private SQLStatementLogger sqlStatementLogger;
 	private Integer maximumFetchDepth;
 	private Map querySubstitutions;
 	private int jdbcBatchSize;
 	private int defaultBatchFetchSize;
 	private boolean scrollableResultSetsEnabled;
 	private boolean getGeneratedKeysEnabled;
 	private String defaultSchemaName;
 	private String defaultCatalogName;
 	private Integer jdbcFetchSize;
 	private String sessionFactoryName;
 	private boolean autoCreateSchema;
 	private boolean autoDropSchema;
 	private boolean autoUpdateSchema;
 	private boolean autoValidateSchema;
 	private boolean queryCacheEnabled;
 	private boolean structuredCacheEntriesEnabled;
 	private boolean secondLevelCacheEnabled;
 	private String cacheRegionPrefix;
 	private boolean minimalPutsEnabled;
 	private boolean commentsEnabled;
 	private boolean statisticsEnabled;
 	private boolean jdbcBatchVersionedData;
 	private boolean identifierRollbackEnabled;
 	private boolean flushBeforeCompletionEnabled;
 	private boolean autoCloseSessionEnabled;
 	private ConnectionReleaseMode connectionReleaseMode;
 	private RegionFactory regionFactory;
 	private QueryCacheFactory queryCacheFactory;
 	private TransactionFactory transactionFactory;
 	private TransactionManagerLookup transactionManagerLookup;
-	private BatcherFactory batcherFactory;
 	private BatchBuilder batchBuilder;
 	private QueryTranslatorFactory queryTranslatorFactory;
 	private boolean wrapResultSetsEnabled;
 	private boolean orderUpdatesEnabled;
 	private boolean orderInsertsEnabled;
 	private EntityMode defaultEntityMode;
 	private boolean dataDefinitionImplicitCommit;
 	private boolean dataDefinitionInTransactionSupported;
 	private boolean strictJPAQLCompliance;
 	private boolean namedQueryStartupCheckingEnabled;
 	private EntityTuplizerFactory entityTuplizerFactory;
 	private boolean checkNullability;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 //	private BytecodeProvider bytecodeProvider;
 	private JdbcSupport jdbcSupport;
 	private String importFiles;
 
 	/**
 	 * Package protected constructor
 	 */
 	Settings() {
 	}
 
 	// public getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 //	public boolean isShowSqlEnabled() {
 //		return showSql;
 //	}
 //
 //	public boolean isFormatSqlEnabled() {
 //		return formatSql;
 //	}
 
 	public String getImportFiles() {
 		return importFiles;
 	}
 
 	public void setImportFiles(String importFiles) {
 		this.importFiles = importFiles;
 	}
 
 	public SQLStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	public String getDefaultSchemaName() {
 		return defaultSchemaName;
 	}
 
 	public String getDefaultCatalogName() {
 		return defaultCatalogName;
 	}
 
 	public int getJdbcBatchSize() {
 		return jdbcBatchSize;
 	}
 
 	public int getDefaultBatchFetchSize() {
 		return defaultBatchFetchSize;
 	}
 
 	public Map getQuerySubstitutions() {
 		return querySubstitutions;
 	}
 
 	public boolean isIdentifierRollbackEnabled() {
 		return identifierRollbackEnabled;
 	}
 
 	public boolean isScrollableResultSetsEnabled() {
 		return scrollableResultSetsEnabled;
 	}
 
 	public boolean isGetGeneratedKeysEnabled() {
 		return getGeneratedKeysEnabled;
 	}
 
 	public boolean isMinimalPutsEnabled() {
 		return minimalPutsEnabled;
 	}
 
 	public Integer getJdbcFetchSize() {
 		return jdbcFetchSize;
 	}
 
 	public TransactionFactory getTransactionFactory() {
 		return transactionFactory;
 	}
 
 	public String getSessionFactoryName() {
 		return sessionFactoryName;
 	}
 
 	public boolean isAutoCreateSchema() {
 		return autoCreateSchema;
 	}
 
 	public boolean isAutoDropSchema() {
 		return autoDropSchema;
 	}
 
 	public boolean isAutoUpdateSchema() {
 		return autoUpdateSchema;
 	}
 
 	public Integer getMaximumFetchDepth() {
 		return maximumFetchDepth;
 	}
 
 	public RegionFactory getRegionFactory() {
 		return regionFactory;
 	}
 
 	public TransactionManagerLookup getTransactionManagerLookup() {
 		return transactionManagerLookup;
 	}
 
 	public boolean isQueryCacheEnabled() {
 		return queryCacheEnabled;
 	}
 
 	public boolean isCommentsEnabled() {
 		return commentsEnabled;
 	}
 
 	public boolean isSecondLevelCacheEnabled() {
 		return secondLevelCacheEnabled;
 	}
 
 	public String getCacheRegionPrefix() {
 		return cacheRegionPrefix;
 	}
 
 	public QueryCacheFactory getQueryCacheFactory() {
 		return queryCacheFactory;
 	}
 
 	public boolean isStatisticsEnabled() {
 		return statisticsEnabled;
 	}
 
 	public boolean isJdbcBatchVersionedData() {
 		return jdbcBatchVersionedData;
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
-	public BatcherFactory getBatcherFactory() {
-		return batcherFactory;
-	}
-
 	public BatchBuilder getBatchBuilder() {
 		return batchBuilder;
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	public QueryTranslatorFactory getQueryTranslatorFactory() {
 		return queryTranslatorFactory;
 	}
 
 	public boolean isWrapResultSetsEnabled() {
 		return wrapResultSetsEnabled;
 	}
 
 	public boolean isOrderUpdatesEnabled() {
 		return orderUpdatesEnabled;
 	}
 
 	public boolean isOrderInsertsEnabled() {
 		return orderInsertsEnabled;
 	}
 
 	public boolean isStructuredCacheEntriesEnabled() {
 		return structuredCacheEntriesEnabled;
 	}
 
 	public EntityMode getDefaultEntityMode() {
 		return defaultEntityMode;
 	}
 
 	public boolean isAutoValidateSchema() {
 		return autoValidateSchema;
 	}
 
 	public boolean isDataDefinitionImplicitCommit() {
 		return dataDefinitionImplicitCommit;
 	}
 
 	public boolean isDataDefinitionInTransactionSupported() {
 		return dataDefinitionInTransactionSupported;
 	}
 
 	public boolean isStrictJPAQLCompliance() {
 		return strictJPAQLCompliance;
 	}
 
 	public boolean isNamedQueryStartupCheckingEnabled() {
 		return namedQueryStartupCheckingEnabled;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 //	public ComponentTuplizerFactory getComponentTuplizerFactory() {
 //		return componentTuplizerFactory;
 //	}
 
 	public JdbcSupport getJdbcSupport() {
 		return jdbcSupport;
 	}
 
 
 	// package protected setters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 //	void setShowSqlEnabled(boolean b) {
 //		showSql = b;
 //	}
 //
 //	void setFormatSqlEnabled(boolean b) {
 //		formatSql = b;
 //	}
 
 	void setSqlStatementLogger(SQLStatementLogger sqlStatementLogger) {
 		this.sqlStatementLogger = sqlStatementLogger;
 	}
 
 	void setDefaultSchemaName(String string) {
 		defaultSchemaName = string;
 	}
 
 	void setDefaultCatalogName(String string) {
 		defaultCatalogName = string;
 	}
 
 	void setJdbcBatchSize(int i) {
 		jdbcBatchSize = i;
 	}
 
 	void setDefaultBatchFetchSize(int i) {
 		defaultBatchFetchSize = i;
 	}
 
 	void setQuerySubstitutions(Map map) {
 		querySubstitutions = map;
 	}
 
 	void setIdentifierRollbackEnabled(boolean b) {
 		identifierRollbackEnabled = b;
 	}
 
 	void setMinimalPutsEnabled(boolean b) {
 		minimalPutsEnabled = b;
 	}
 
 	void setScrollableResultSetsEnabled(boolean b) {
 		scrollableResultSetsEnabled = b;
 	}
 
 	void setGetGeneratedKeysEnabled(boolean b) {
 		getGeneratedKeysEnabled = b;
 	}
 
 	void setJdbcFetchSize(Integer integer) {
 		jdbcFetchSize = integer;
 	}
 
 	void setTransactionFactory(TransactionFactory factory) {
 		transactionFactory = factory;
 	}
 
 	void setSessionFactoryName(String string) {
 		sessionFactoryName = string;
 	}
 
 	void setAutoCreateSchema(boolean b) {
 		autoCreateSchema = b;
 	}
 
 	void setAutoDropSchema(boolean b) {
 		autoDropSchema = b;
 	}
 
 	void setAutoUpdateSchema(boolean b) {
 		autoUpdateSchema = b;
 	}
 
 	void setMaximumFetchDepth(Integer i) {
 		maximumFetchDepth = i;
 	}
 
 	void setRegionFactory(RegionFactory regionFactory) {
 		this.regionFactory = regionFactory;
 	}
 
 	void setTransactionManagerLookup(TransactionManagerLookup lookup) {
 		transactionManagerLookup = lookup;
 	}
 
 	void setQueryCacheEnabled(boolean b) {
 		queryCacheEnabled = b;
 	}
 
 	void setCommentsEnabled(boolean commentsEnabled) {
 		this.commentsEnabled = commentsEnabled;
 	}
 
 	void setSecondLevelCacheEnabled(boolean secondLevelCacheEnabled) {
 		this.secondLevelCacheEnabled = secondLevelCacheEnabled;
 	}
 
 	void setCacheRegionPrefix(String cacheRegionPrefix) {
 		this.cacheRegionPrefix = cacheRegionPrefix;
 	}
 
 	void setQueryCacheFactory(QueryCacheFactory queryCacheFactory) {
 		this.queryCacheFactory = queryCacheFactory;
 	}
 
 	void setStatisticsEnabled(boolean statisticsEnabled) {
 		this.statisticsEnabled = statisticsEnabled;
 	}
 
 	void setJdbcBatchVersionedData(boolean jdbcBatchVersionedData) {
 		this.jdbcBatchVersionedData = jdbcBatchVersionedData;
 	}
 
 	void setFlushBeforeCompletionEnabled(boolean flushBeforeCompletionEnabled) {
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 	}
 
-	void setBatcherFactory(BatcherFactory batcher) {
-		this.batcherFactory = batcher;
-	}
-
 	void setBatcherBuilder(BatchBuilder batchBuilder) {
 		this.batchBuilder = batchBuilder;
 	}
 
 	void setAutoCloseSessionEnabled(boolean autoCloseSessionEnabled) {
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 	}
 
 	void setConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 		this.connectionReleaseMode = connectionReleaseMode;
 	}
 
 	void setQueryTranslatorFactory(QueryTranslatorFactory queryTranslatorFactory) {
 		this.queryTranslatorFactory = queryTranslatorFactory;
 	}
 
 	void setWrapResultSetsEnabled(boolean wrapResultSetsEnabled) {
 		this.wrapResultSetsEnabled = wrapResultSetsEnabled;
 	}
 
 	void setOrderUpdatesEnabled(boolean orderUpdatesEnabled) {
 		this.orderUpdatesEnabled = orderUpdatesEnabled;
 	}
 
 	void setOrderInsertsEnabled(boolean orderInsertsEnabled) {
 		this.orderInsertsEnabled = orderInsertsEnabled;
 	}
 
 	void setStructuredCacheEntriesEnabled(boolean structuredCacheEntriesEnabled) {
 		this.structuredCacheEntriesEnabled = structuredCacheEntriesEnabled;
 	}
 
 	void setDefaultEntityMode(EntityMode defaultEntityMode) {
 		this.defaultEntityMode = defaultEntityMode;
 	}
 
 	void setAutoValidateSchema(boolean autoValidateSchema) {
 		this.autoValidateSchema = autoValidateSchema;
 	}
 
 	void setDataDefinitionImplicitCommit(boolean dataDefinitionImplicitCommit) {
 		this.dataDefinitionImplicitCommit = dataDefinitionImplicitCommit;
 	}
 
 	void setDataDefinitionInTransactionSupported(boolean dataDefinitionInTransactionSupported) {
 		this.dataDefinitionInTransactionSupported = dataDefinitionInTransactionSupported;
 	}
 
 	void setStrictJPAQLCompliance(boolean strictJPAQLCompliance) {
 		this.strictJPAQLCompliance = strictJPAQLCompliance;
 	}
 
 	void setNamedQueryStartupCheckingEnabled(boolean namedQueryStartupCheckingEnabled) {
 		this.namedQueryStartupCheckingEnabled = namedQueryStartupCheckingEnabled;
 	}
 
 	void setEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
 		this.entityTuplizerFactory = entityTuplizerFactory;
 	}
 
 	public boolean isCheckNullability() {
 		return checkNullability;
 	}
 
 	public void setCheckNullability(boolean checkNullability) {
 		this.checkNullability = checkNullability;
 	}
 
 	//	void setComponentTuplizerFactory(ComponentTuplizerFactory componentTuplizerFactory) {
 //		this.componentTuplizerFactory = componentTuplizerFactory;
 //	}
 
 	void setJdbcSupport(JdbcSupport jdbcSupport) {
 		this.jdbcSupport = jdbcSupport;
 	}
 
 	//	public BytecodeProvider getBytecodeProvider() {
 //		return bytecodeProvider;
 //	}
 //
 //	void setBytecodeProvider(BytecodeProvider bytecodeProvider) {
 //		this.bytecodeProvider = bytecodeProvider;
 //	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 9a6c1ecf60..ca2a34f396 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,404 +1,378 @@
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
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.JdbcSupport;
 import org.hibernate.bytecode.BytecodeProvider;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilder;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.jdbc.BatcherFactory;
-import org.hibernate.jdbc.BatchingBatcherFactory;
-import org.hibernate.jdbc.NonBatchingBatcherFactory;
 import org.hibernate.jdbc.util.SQLStatementLogger;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.transaction.TransactionFactoryFactory;
 import org.hibernate.transaction.TransactionManagerLookup;
 import org.hibernate.transaction.TransactionManagerLookupFactory;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 	private static final Logger log = LoggerFactory.getLogger( SettingsFactory.class );
 	private static final long serialVersionUID = -1194386144994524825L;
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	protected SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, JdbcServices jdbcServices) {
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
 
 		settings.setJdbcSupport( new JdbcSupport( ! ConfigurationHelper.getBoolean( Environment.NON_CONTEXTUAL_LOB_CREATION, properties ) ) );
 
 		// Transaction settings:
 
 		TransactionFactory transactionFactory = createTransactionFactory(properties);
 		settings.setTransactionFactory(transactionFactory);
 		settings.setTransactionManagerLookup( createTransactionManagerLookup(properties) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
 		log.info("Automatic flush during beforeCompletion(): " + enabledDisabled(flushBeforeCompletion) );
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
 		log.info("Automatic session close at end of transaction: " + enabledDisabled(autoCloseSession) );
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) batchSize = 0;
 		if (batchSize>0) log.info("JDBC batch size: " + batchSize);
 		settings.setJdbcBatchSize(batchSize);
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
 		if (batchSize>0) log.info("JDBC batch updates for versioned data: " + enabledDisabled(jdbcBatchVersionedData) );
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
-		settings.setBatcherFactory( createBatcherFactory(properties, batchSize) );
 		settings.setBatcherBuilder( createBatchBuilder(properties, batchSize) );
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, meta.supportsScrollableResults());
 		log.info("Scrollable result sets: " + enabledDisabled(useScrollableResultSets) );
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
 		log.debug( "Wrap result sets: " + enabledDisabled(wrapResultSets) );
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		log.info("JDBC3 getGeneratedKeys(): " + enabledDisabled(useGetGeneratedKeys) );
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
 		if (statementFetchSize!=null) log.info("JDBC result set fetch size: " + statementFetchSize);
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
 		log.info( "Connection release mode: " + releaseModeName );
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = transactionFactory.getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {			
 				log.warn( "Overriding release mode as connection provider does not support 'after_statement'" );
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty(Environment.DEFAULT_SCHEMA);
 		String defaultCatalog = properties.getProperty(Environment.DEFAULT_CATALOG);
 		if (defaultSchema!=null) log.info("Default schema: " + defaultSchema);
 		if (defaultCatalog!=null) log.info("Default catalog: " + defaultCatalog);
 		settings.setDefaultSchemaName(defaultSchema);
 		settings.setDefaultCatalogName(defaultCatalog);
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger(Environment.MAX_FETCH_DEPTH, properties);
 		if (maxFetchDepth!=null) log.info("Maximum outer join fetch depth: " + maxFetchDepth);
 		settings.setMaximumFetchDepth(maxFetchDepth);
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		log.info("Default batch fetch size: " + batchFetchSize);
 		settings.setDefaultBatchFetchSize(batchFetchSize);
 
 		boolean comments = ConfigurationHelper.getBoolean(Environment.USE_SQL_COMMENTS, properties);
 		log.info( "Generate SQL with comments: " + enabledDisabled(comments) );
 		settings.setCommentsEnabled(comments);
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean(Environment.ORDER_UPDATES, properties);
 		log.info( "Order SQL updates by primary key: " + enabledDisabled(orderUpdates) );
 		settings.setOrderUpdatesEnabled(orderUpdates);
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
 		log.info( "Order SQL inserts for batching: " + enabledDisabled( orderInserts ) );
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap(Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties);
 		log.info("Query language substitutions: " + querySubstitutions);
 		settings.setQuerySubstitutions(querySubstitutions);
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 		log.info( "JPA-QL strict compliance: " + enabledDisabled( jpaqlCompliance ) );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean(Environment.USE_SECOND_LEVEL_CACHE, properties, true);
 		log.info( "Second-level cache: " + enabledDisabled(useSecondLevelCache) );
 		settings.setSecondLevelCacheEnabled(useSecondLevelCache);
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
 		log.info( "Query cache: " + enabledDisabled(useQueryCache) );
 		settings.setQueryCacheEnabled(useQueryCache);
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		log.info( "Optimize cache for minimal puts: " + enabledDisabled(useMinimalPuts) );
 		settings.setMinimalPutsEnabled(useMinimalPuts);
 
 		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
 		if ( StringHelper.isEmpty(prefix) ) prefix=null;
 		if (prefix!=null) log.info("Cache region prefix: "+ prefix);
 		settings.setCacheRegionPrefix(prefix);
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean(Environment.USE_STRUCTURED_CACHE, properties, false);
 		log.info( "Structured second-level cache entries: " + enabledDisabled(useStructuredCacheEntries) );
 		settings.setStructuredCacheEntriesEnabled(useStructuredCacheEntries);
 
 		if (useQueryCache) settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
 
 		//Statistics and logging:
 
 		boolean showSql = ConfigurationHelper.getBoolean(Environment.SHOW_SQL, properties);
 		if (showSql) log.info("Echoing all SQL to stdout");
 //		settings.setShowSqlEnabled(showSql);
 
 		boolean formatSql = ConfigurationHelper.getBoolean(Environment.FORMAT_SQL, properties);
 //		settings.setFormatSqlEnabled(formatSql);
 
 		settings.setSqlStatementLogger( new SQLStatementLogger( showSql, formatSql ) );
 
 		boolean useStatistics = ConfigurationHelper.getBoolean(Environment.GENERATE_STATISTICS, properties);
 		log.info( "Statistics: " + enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled(useStatistics);
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean(Environment.USE_IDENTIFIER_ROLLBACK, properties);
 		log.info( "Deleted entity synthetic identifier rollback: " + enabledDisabled(useIdentifierRollback) );
 		settings.setIdentifierRollbackEnabled(useIdentifierRollback);
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty(Environment.HBM2DDL_AUTO);
 		if ( "validate".equals(autoSchemaExport) ) settings.setAutoValidateSchema(true);
 		if ( "update".equals(autoSchemaExport) ) settings.setAutoUpdateSchema(true);
 		if ( "create".equals(autoSchemaExport) ) settings.setAutoCreateSchema(true);
 		if ( "create-drop".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema(true);
 			settings.setAutoDropSchema(true);
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
 		log.info( "Default entity-mode: " + defaultEntityMode );
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
 		log.info( "Named query checking : " + enabledDisabled( namedQueryChecking ) );
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
 		log.info( "Check Nullability in Core (should be disabled when Bean Validation is on): " + enabledDisabled(checkNullability) );
 		settings.setCheckNullability(checkNullability);
 
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
 		else if ( "cglib".equals( providerName ) ) {
 			return new org.hibernate.bytecode.cglib.BytecodeProviderImpl();
 		}
 		else {
 			log.debug( "using javassist as bytecode provider by default" );
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
 	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.StandardQueryCacheFactory"
 		);
 		log.info("Query cache factory: " + queryCacheFactoryClassName);
 		try {
 			return (QueryCacheFactory) ReflectHelper.classForName(queryCacheFactoryClassName).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, cnfe);
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		String regionFactoryClassName = ConfigurationHelper.getString( Environment.CACHE_REGION_FACTORY, properties, null );
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
 		log.info( "Cache region factory : " + regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException nsme ) {
 				// no constructor accepting Properties found, try no arg constructor
 				log.debug(
 						regionFactoryClassName + " did not provide constructor accepting java.util.Properties; " +
 								"attempting no-arg constructor."
 				);
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName ).newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.ast.ASTQueryTranslatorFactory"
 		);
 		log.info("Query translator: " + className);
 		try {
 			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryTranslatorFactory: " + className, cnfe);
 		}
 	}
 
-	protected BatcherFactory createBatcherFactory(Properties properties, int batchSize) {
-		String batcherClass = properties.getProperty(Environment.BATCH_STRATEGY);
-		BatcherFactory batcherFactory = null;
-		if (batcherClass==null) {
-			batcherFactory = batchSize == 0
-					? new NonBatchingBatcherFactory()
-					: new BatchingBatcherFactory( );
-		}
-		else {
-			log.info("Batcher factory: " + batcherClass);
-			try {
-				batcherFactory = (BatcherFactory) ReflectHelper.classForName(batcherClass).newInstance();
-			}
-			catch (Exception cnfe) {
-				throw new HibernateException("could not instantiate BatcherFactory: " + batcherClass, cnfe);
-			}
-		}
-		batcherFactory.setJdbcBatchSize( batchSize );
-		return batcherFactory;
-	}
-
 	protected BatchBuilder createBatchBuilder(Properties properties, int batchSize) {
-		//FIXME: uncomment to use BatchBuilder
-		/*
 		String batchBuilderClass = properties.getProperty(Environment.BATCH_STRATEGY);
+		BatchBuilder batchBuilder;
 		if (batchBuilderClass==null) {
-			return batchSize > 0
+			batchBuilder = batchSize > 0
 					? new BatchBuilder( batchSize )
 					: new BatchBuilder();
 		}
 		else {
-			log.info("Batcher factory: " + batchBuilderClass);
+			log.info("Batch factory: " + batchBuilderClass);
 			try {
-				return (BatchBuilder) ReflectHelper.classForName(batchBuilderClass).newInstance();
+				batchBuilder = (BatchBuilder) ReflectHelper.classForName(batchBuilderClass).newInstance();
 			}
 			catch (Exception cnfe) {
 				throw new HibernateException("could not instantiate BatchBuilder: " + batchBuilderClass, cnfe);
 			}
 		}
-		*/
-		return null;
+		batchBuilder.setJdbcBatchSize( batchSize );
+		return batchBuilder;
 	}
 
 	protected TransactionFactory createTransactionFactory(Properties properties) {
 		return TransactionFactoryFactory.buildTransactionFactory(properties);
 	}
 
 	protected TransactionManagerLookup createTransactionManagerLookup(Properties properties) {
 		return TransactionManagerLookupFactory.getTransactionManagerLookup(properties);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
index 1f58e3bda9..5d171f4a38 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
@@ -1,188 +1,208 @@
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
 package org.hibernate.engine.jdbc.batch.internal;
 
-import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchObserver;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
-import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 
 /**
  * Convenience base class for implementors of the Batch interface.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractBatchImpl implements Batch {
 	private static final Logger log = LoggerFactory.getLogger( AbstractBatchImpl.class );
 
+	private final SQLStatementLogger statementLogger;
+	private final SQLExceptionHelper exceptionHelper;
 	private Object key;
-	private LogicalConnectionImplementor logicalConnection;
-	private Connection connectionProxy;
 	private LinkedHashMap<String,PreparedStatement> statements = new LinkedHashMap<String,PreparedStatement>();
 	private LinkedHashSet<BatchObserver> observers = new LinkedHashSet<BatchObserver>();
 
-	protected AbstractBatchImpl(Object key, LogicalConnectionImplementor logicalConnection) {
+	protected AbstractBatchImpl(Object key,
+								SQLStatementLogger statementLogger,
+								SQLExceptionHelper exceptionHelper) {
+		if ( key == null || statementLogger == null || exceptionHelper == null ) {
+			throw new IllegalArgumentException( "key, statementLogger, and exceptionHelper must be non-null." );
+		}
 		this.key = key;
-		this.logicalConnection = logicalConnection;
-		this.connectionProxy = ProxyBuilder.buildConnection( logicalConnection );
+		this.statementLogger = statementLogger;
+		this.exceptionHelper = exceptionHelper;
 	}
 
 	/**
 	 * Perform batch execution.
 	 * <p/>
 	 * This is called from the explicit {@link #execute() execution}, but may also be called from elsewhere
 	 * depending on the exact implementation.
 	 */
 	protected abstract void doExecuteBatch();
 
 	/**
-	 * Convenience access to the underlying JDBC services.
+	 * Convenience access to the SQLException helper.
+	 *
+	 * @return The underlying SQLException helper.
+	 */
+	protected SQLExceptionHelper getSqlExceptionHelper() {
+		return exceptionHelper;
+	}
+
+	/**
+	 * Convenience access to the SQL statement logger.
 	 *
 	 * @return The underlying JDBC services.
 	 */
-	protected JdbcServices getJdbcServices() {
-		return logicalConnection.getJdbcServices();
+	protected SQLStatementLogger getSqlStatementLogger() {
+		return statementLogger;
 	}
 
 	/**
 	 * Access to the batch's map of statements (keyed by SQL statement string).
 	 *
 	 * @return This batch's statements.
 	 */
 	protected LinkedHashMap<String,PreparedStatement> getStatements() {
 		return statements;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final Object getKey() {
 		return key;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void addObserver(BatchObserver observer) {
 		observers.add( observer );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
-	public final PreparedStatement getBatchStatement(String sql, boolean callable) {
-		PreparedStatement statement = statements.get( sql );
-		if ( statement == null ) {
-			statement = buildBatchStatement( sql, callable );
-			statements.put( sql, statement );
-		}
-		else {
-			log.debug( "reusing batch statement" );
-			getJdbcServices().getSqlStatementLogger().logStatement( sql );
+	public final PreparedStatement getBatchStatement(Object key, String sql) {
+		checkConsistentBatchKey( key );
+		if ( sql == null ) {
+			throw new IllegalArgumentException( "sql must be non-null." );
 		}
+		PreparedStatement statement = statements.get( sql );
+		if ( statement != null ) {
+			log.debug( "reusing prepared statement" );
+			statementLogger.logStatement( sql );
+		}		
 		return statement;
 	}
 
-	private PreparedStatement buildBatchStatement(String sql, boolean callable) {
-		try {
-			if ( callable ) {
-				return connectionProxy.prepareCall( sql );
-			}
-			else {
-				return connectionProxy.prepareStatement( sql );
-			}
+	/**
+	 * {@inheritDoc}
+	 */
+	// TODO: should this be final???
+	@Override
+	public void addBatchStatement(Object key, String sql, PreparedStatement preparedStatement) {
+		checkConsistentBatchKey( key );
+		if ( sql == null ) {
+			throw new IllegalArgumentException( "sql must be non-null." );
+		}
+		if ( statements.put( sql, preparedStatement ) != null ) {
+			log.error( "PreparedStatement was already in the batch, [" + sql + "]." );
 		}
-		catch ( SQLException sqle ) {
-			log.error( "sqlexception escaped proxy", sqle );
-			throw getJdbcServices().getSqlExceptionHelper().convert( sqle, "could not prepare batch statement", sql );
+	}
+
+	protected void checkConsistentBatchKey(Object key) {
+		if ( ! this.key.equals( key ) ) {
+			throw new IllegalStateException(
+					"specified key ["+ key + "] is different from internal batch key [" + this.key + "]."
+			);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final void execute() {
 		notifyObserversExplicitExecution();
 		if ( statements.isEmpty() ) {
 			return;
 		}
 		try {
 			try {
 				doExecuteBatch();
 			}
 			finally {
-				releaseStatements();
+				release();
 			}
 		}
 		finally {
 			statements.clear();
 		}
 	}
 
 	private void releaseStatements() {
 		for ( PreparedStatement statement : getStatements().values() ) {
 			try {
 				statement.close();
 			}
 			catch ( SQLException e ) {
 				log.error( "unable to release batch statement..." );
 				log.error( "sqlexception escaped proxy", e );
 			}
 		}
 		getStatements().clear();
 	}
 
 	private void notifyObserversExplicitExecution() {
 		for ( BatchObserver observer : observers ) {
 			observer.batchExplicitlyExecuted();
 		}
 	}
 
 	/**
 	 * Convenience method to notify registered observers of an implicit execution of this batch.
 	 */
 	protected void notifyObserversImplicitExecution() {
 		for ( BatchObserver observer : observers ) {
 			observer.batchImplicitlyExecuted();
 		}
 	}
 
 	public void release() {
 		if ( getStatements() != null && !getStatements().isEmpty() ) {
 			log.info( "On release of batch it still contained JDBC statements" );
 		}
 		releaseStatements();
 		observers.clear();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilder.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilder.java
index 85febd5946..e01a7047b3 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilder.java
@@ -1,60 +1,64 @@
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
 package org.hibernate.engine.jdbc.batch.internal;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 
 /**
  * A builder for {@link Batch} instances.
  *
  * @author Steve Ebersole
  */
 public class BatchBuilder {
 	private static final Logger log = LoggerFactory.getLogger( BatchBuilder.class );
 
 	private int size;
 
 	public BatchBuilder() {
 	}
 
 	public BatchBuilder(int size) {
 		this.size = size;
 	}
 
-	public void setSize(int size) {
+	public void setJdbcBatchSize(int size) {
 		this.size = size;
 	}
 
-	public Batch buildBatch(Object key, LogicalConnectionImplementor logicalConnection) {
+	public Batch buildBatch(Object key,
+							SQLStatementLogger statementLogger,
+							SQLExceptionHelper exceptionHelper) {
 		log.trace( "building batch [size={}]", size );
 		return size > 1
-				? new BatchingBatch( key, logicalConnection, size )
-				: new NonBatchingBatch( key, logicalConnection );
+				? new BatchingBatch( key, statementLogger, exceptionHelper, size )
+				: new NonBatchingBatch( key, statementLogger, exceptionHelper );
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java
index d5d036e42a..f7c3b246a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java
@@ -1,125 +1,186 @@
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
 package org.hibernate.engine.jdbc.batch.internal;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.List;
 import java.util.Map;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
-import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 import org.hibernate.jdbc.Expectation;
 
 /**
- * A {@link org.hibernate.engine.jdbc.batch.spi.Batch} implementation which does batching based on a given size.  Once the batch size is exceeded, the
- * batch is implicitly executed.
+ * A {@link org.hibernate.engine.jdbc.batch.spi.Batch} implementation which does
+ * batching based on a given size.  Once the batch size is reached for a statement
+ * in the batch, the entire batch is implicitly executed.
  *
  * @author Steve Ebersole
  */
 public class BatchingBatch extends AbstractBatchImpl {
 	private static final Logger log = LoggerFactory.getLogger( BatchingBatch.class );
 
 	private final int batchSize;
-	private Expectation[] expectations;
-	private int batchPosition;
+	private Map<String, List<Expectation>>  expectationsBySql;
+	private int maxBatchPosition;
 
-	public BatchingBatch(Object key, LogicalConnectionImplementor logicalConnection, int batchSize) {
-		super( key, logicalConnection );
+	public BatchingBatch(Object key,
+						 SQLStatementLogger statementLogger,
+						 SQLExceptionHelper exceptionHelper,
+						 int batchSize) {
+		super( key, statementLogger, exceptionHelper );
 		this.batchSize = batchSize;
-		this.expectations = new Expectation[ batchSize ];
+		this.expectationsBySql = new HashMap<String, List<Expectation>>();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
-	public void addToBatch(Expectation expectation) {
-		if ( !expectation.canBeBatched() ) {
+	public void addToBatch(Object key, String sql, Expectation expectation) {
+		checkConsistentBatchKey( key );
+		if ( sql == null || expectation == null ) {
+			throw new AssertionFailure( "sql or expection was null." );
+		}
+		if ( ! expectation.canBeBatched() ) {
 			throw new HibernateException( "attempting to batch an operation which cannot be batched" );
 		}
-		for ( Map.Entry<String,PreparedStatement> entry : getStatements().entrySet() ) {
-			try {
-				entry.getValue().addBatch();
-			}
-			catch ( SQLException e ) {
-				log.error( "sqlexception escaped proxy", e );
-				throw getJdbcServices().getSqlExceptionHelper().convert( e, "could not perform addBatch", entry.getKey() );
-			}
+		final PreparedStatement statement = getStatements().get( sql );
+		try {
+			statement.addBatch();
+		}
+		catch ( SQLException e ) {
+			log.error( "sqlexception escaped proxy", e );
+			throw getSqlExceptionHelper().convert( e, "could not perform addBatch", sql );
 		}
-		expectations[ batchPosition++ ] = expectation;
-		if ( batchPosition == batchSize ) {
+		List<Expectation> expectations = expectationsBySql.get( sql );
+		if ( expectations == null ) {
+			expectations = new ArrayList<Expectation>( batchSize );
+			expectationsBySql.put( sql, expectations );
+		}
+		expectations.add( expectation );
+		maxBatchPosition = Math.max( maxBatchPosition, expectations.size() );
+		if ( maxBatchPosition == batchSize ) {
 			notifyObserversImplicitExecution();
 			doExecuteBatch();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	protected void doExecuteBatch() {
-		if ( batchPosition == 0 ) {
+		if ( maxBatchPosition == 0 ) {
 			log.debug( "no batched statements to execute" );
 		}
 		else {
 			if ( log.isDebugEnabled() ) {
-				log.debug( "Executing batch size: " + batchPosition );
+				log.debug( "Executing {} statements with maximum batch size {} ",
+						getStatements().size(), maxBatchPosition
+				);
 			}
-
 			try {
-				for ( Map.Entry<String,PreparedStatement> entry : getStatements().entrySet() ) {
-					try {
-						final PreparedStatement statement = entry.getValue();
-						checkRowCounts( statement.executeBatch(), statement );
-					}
-					catch ( SQLException e ) {
-						log.error( "sqlexception escaped proxy", e );
-						throw getJdbcServices().getSqlExceptionHelper()
-								.convert( e, "could not perform addBatch", entry.getKey() );
-					}
-				}
+				executeStatements();
 			}
 			catch ( RuntimeException re ) {
 				log.error( "Exception executing batch [{}]", re.getMessage() );
 				throw re;
 			}
 			finally {
-				batchPosition = 0;
+				for ( List<Expectation> expectations : expectationsBySql.values() ) {
+					expectations.clear();
+				}				
+				maxBatchPosition = 0;
+			}
+		}
+	}
+
+	private void executeStatements() {
+		for ( Map.Entry<String,PreparedStatement> entry : getStatements().entrySet() ) {
+			final String sql = entry.getKey();
+			final PreparedStatement statement = entry.getValue();
+			final List<Expectation> expectations = expectationsBySql.get( sql );
+			if ( batchSize < expectations.size() ) {
+				throw new IllegalStateException(
+						"Number of expectations [" + expectations.size() +
+								"] is greater than batch size [" + batchSize +
+								"] for statement [" + sql +
+								"]"
+				);
+			}
+			if ( expectations.size() > 0 ) {
+				if ( log.isDebugEnabled() ) {
+					log.debug( "Executing with batch of size {}: {}", expectations.size(), sql  );
+				}
+				executeStatement( sql, statement, expectations );
+				expectations.clear();
+			}
+			else {
+				if ( log.isDebugEnabled() ) {
+					log.debug( "Skipped executing because batch size is 0: ", sql );
+				}
 			}
+		}
+	}
 
+	private void executeStatement(String sql, PreparedStatement ps, List<Expectation> expectations) {
+		try {
+			checkRowCounts( sql, ps.executeBatch(), ps, expectations );
+		}
+		catch ( SQLException e ) {
+			log.error( "sqlexception escaped proxy", e );
+			throw getSqlExceptionHelper()
+					.convert( e, "could not execute statement: " + sql );
 		}
 	}
 
-	private void checkRowCounts(int[] rowCounts, PreparedStatement ps) throws SQLException, HibernateException {
+	private void checkRowCounts(String sql, int[] rowCounts, PreparedStatement ps, List<Expectation> expectations) {
 		int numberOfRowCounts = rowCounts.length;
-		if ( numberOfRowCounts != batchPosition ) {
+		if ( numberOfRowCounts != expectations.size() ) {
 			log.warn( "JDBC driver did not return the expected number of row counts" );
 		}
-		for ( int i = 0; i < numberOfRowCounts; i++ ) {
-			expectations[i].verifyOutcome( rowCounts[i], ps, i );
+		try {
+			for ( int i = 0; i < numberOfRowCounts; i++ ) {
+				expectations.get( i ).verifyOutcome( rowCounts[i], ps, i );
+			}
+		}
+		catch ( SQLException e ) {
+			log.error( "sqlexception escaped proxy", e );
+			throw getSqlExceptionHelper()
+					.convert( e, "row count verification failed for statement: ", sql );
 		}
 	}
 
+	public void release() {
+		expectationsBySql.clear();
+		maxBatchPosition = 0;
+	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/NonBatchingBatch.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/NonBatchingBatch.java
index e9becd14b6..64eb6b116a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/NonBatchingBatch.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/NonBatchingBatch.java
@@ -1,67 +1,71 @@
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
 package org.hibernate.engine.jdbc.batch.internal;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
-import java.util.Map;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
-import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 import org.hibernate.jdbc.Expectation;
 
 /**
  * An implementation of {@link org.hibernate.engine.jdbc.batch.spi.Batch} which does not perform batching.  It simply executes each statement as it is
  * encountered.
  *
  * @author Steve Ebersole
  */
 public class NonBatchingBatch extends AbstractBatchImpl {
 	private static final Logger log = LoggerFactory.getLogger( NonBatchingBatch.class );
 
-	protected NonBatchingBatch(Object key, LogicalConnectionImplementor logicalConnection) {
-		super( key, logicalConnection );
+	protected NonBatchingBatch(Object key,
+							SQLStatementLogger statementLogger,
+							SQLExceptionHelper exceptionHelper) {
+		super( key, statementLogger, exceptionHelper );
 	}
 
-	public void addToBatch(Expectation expectation) {
+	public void addToBatch(Object key, String sql, Expectation expectation) {
+		checkConsistentBatchKey( key );
+		if ( sql == null ) {
+			throw new IllegalArgumentException( "sql must be non-null." );
+		}
 		notifyObserversImplicitExecution();
-		for ( Map.Entry<String,PreparedStatement> entry : getStatements().entrySet() ) {
-			try {
-				final PreparedStatement statement = entry.getValue();
-				final int rowCount = statement.executeUpdate();
-				expectation.verifyOutcome( rowCount, statement, 0 );
-			}
-			catch ( SQLException e ) {
-				log.error( "sqlexception escaped proxy", e );
-				throw getJdbcServices().getSqlExceptionHelper().convert( e, "could not execute batch statement", entry.getKey() );
-			}
+		try {
+			final PreparedStatement statement = getStatements().get( sql );
+			final int rowCount = statement.executeUpdate();
+			expectation.verifyOutcome( rowCount, statement, 0 );
+		}
+		catch ( SQLException e ) {
+			log.error( "sqlexception escaped proxy", e );
+			throw getSqlExceptionHelper().convert( e, "could not execute batch statement", sql );
 		}
 	}
 
 	protected void doExecuteBatch() {
 		// nothing to do
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/Batch.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/Batch.java
index aeda16f69b..89ed9f39a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/Batch.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/Batch.java
@@ -1,81 +1,90 @@
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
 package org.hibernate.engine.jdbc.batch.spi;
 
 import java.sql.PreparedStatement;
 
 import org.hibernate.jdbc.Expectation;
 
 /**
  * Conceptually models a batch.
  * <p/>
  * Unlike directly in JDBC, here we add the ability to batch together multiple statements at a time.  In the underlying
  * JDBC this correlates to multiple {@link java.sql.PreparedStatement} objects (one for each DML string) maintained within the
  * batch.
  *
  * @author Steve Ebersole
  */
 public interface Batch {
 	/**
 	 * Retrieves the object being used to key (uniquely identify) this batch.
 	 *
 	 * @return The batch key.
 	 */
 	public Object getKey();
 
 	/**
 	 * Adds an observer to this batch.
 	 *
 	 * @param observer The batch observer.
 	 */
 	public void addObserver(BatchObserver observer);
 
 	/**
 	 * Get a statement which is part of the batch, creating if necessary (and storing for next time).
 	 *
 	 * @param sql The SQL statement.
-	 * @param callable Is the SQL statement callable?
 	 * @return The prepared statement instance, representing the SQL statement.
 	 */
-	public PreparedStatement getBatchStatement(String sql, boolean callable);
+	public PreparedStatement getBatchStatement(Object key, String sql);
+
+	/**
+	 * Store a statement in the batch.
+	 *
+	 * @param sql The SQL statement.
+	 */
+	public void addBatchStatement(Object key, String sql, PreparedStatement preparedStatement);
+
 
 	/**
 	 * Indicates completion of the current part of the batch.
 	 *
+	 * @param key
+	 * @param sql
 	 * @param expectation The expectation for the part's result.
 	 */
-	public void addToBatch(Expectation expectation);
+	public void addToBatch(Object key, String sql, Expectation expectation);
 
 	/**
 	 * Execute this batch.
 	 */
 	public void execute();
 
 	/**
 	 * Used to indicate that the batch instance is no longer needed and that, therefore, it can release its
 	 * resources.
 	 */
 	public void release();
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
index 306c65b86a..dddaa20eca 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
@@ -1,681 +1,611 @@
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
-import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.ScrollMode;
-import org.hibernate.TransactionException;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
+import org.hibernate.engine.jdbc.batch.internal.BatchBuilder;
+import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.spi.ConnectionManager;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
-import org.hibernate.jdbc.Batcher;
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
 
-	private transient SessionFactoryImplementor factory;
-	private transient Connection proxiedConnection;
 	private transient Interceptor interceptor;
 
 	private final Callback callback;
-	private long transactionTimeout = -1;
-	boolean isTransactionTimeoutSet;
-
 	private transient LogicalConnectionImpl logicalConnection;
+	private transient StatementPreparer statementPreparer;
+	private final transient BatchBuilder batchBuilder;
+	private Batch batch;
 
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
-						factory.getStatistics() != null ? factory.getStatisticsImplementor() : null,
-						factory.getSettings().getBatcherFactory()
+						factory.getStatistics() != null ? factory.getStatisticsImplementor() : null
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
-		this.factory = factory;
 		this.callback = callback;
 		this.interceptor = interceptor;
-		setupConnection( logicalConnection );
-	}
-
-	private void setupConnection(LogicalConnectionImpl logicalConnection) {
 		this.logicalConnection = logicalConnection;
 		this.logicalConnection.addObserver( callback );
-		proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
+		this.statementPreparer = new StatementPreparer( logicalConnection, factory.getSettings() );
+		this.batchBuilder = factory.getSettings().getBatchBuilder();
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
-		unsetTransactionTimeout();		
+		if ( statementPreparer != null ) {
+			statementPreparer.unsetTransactionTimeout();
+		}
 	}
 
 	private boolean isAfterTransactionRelease() {
 		return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION;
 	}
 
 	private boolean isOnCloseRelease() {
 		return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.ON_CLOSE;
 	}
 
-	public boolean isLogicallyConnected() {
+	private boolean isLogicallyConnected() {
 		return logicalConnection != null && logicalConnection.isOpen();
 	}
 
 	@Override
 	public void setTransactionTimeout(int seconds) {
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
-	private void setStatementTimeout(PreparedStatement preparedStatement) throws SQLException {
-		if ( isTransactionTimeoutSet ) {
-			int timeout = (int) ( transactionTimeout - ( System.currentTimeMillis() / 1000 ) );
-			if ( timeout <=  0) {
-				throw new TransactionException("transaction timeout expired");
-			}
-			else {
-				preparedStatement.setQueryTimeout(timeout);
-			}
-		}
+		statementPreparer.setTransactionTimeout( seconds );
 	}
 
-
 	/**
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
+		releaseBatch();
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
+			releaseBatch();
+			statementPreparer.close();
 			Connection c = logicalConnection.close();
 			return c;
 		}
 		finally {
+			batch = null;
+			statementPreparer = null;
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
 
-	private abstract class StatementPreparer {
-		private final String sql;
-		StatementPreparer(String sql) {
-			this.sql = getSQL( sql );
-		}
-		public String getSqlToPrepare() {
-			return sql;
-		}
-		abstract PreparedStatement doPrepare() throws SQLException;
-		public void afterPrepare(PreparedStatement preparedStatement) throws SQLException {
-			setStatementTimeout( preparedStatement );
-		}
-	}
-
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
 	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, int)}).
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
+	@Override
 	public PreparedStatement prepareStatement(String sql, final int autoGeneratedKeys)
 			throws HibernateException {
-		if ( autoGeneratedKeys == PreparedStatement.RETURN_GENERATED_KEYS ) {
-			checkAutoGeneratedKeysSupportEnabled();
-		}
-		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
-			public PreparedStatement doPrepare() throws SQLException {
-				return proxiedConnection.prepareStatement( getSqlToPrepare(), autoGeneratedKeys );
-			}
-		};
-		return prepareStatement( statementPreparer, true );
+		executeBatch();
+		return statementPreparer.prepareStatement( getSQL( sql ), autoGeneratedKeys );
 	}
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
+	@Override
 	public PreparedStatement prepareStatement(String sql, final String[] columnNames) {
-		checkAutoGeneratedKeysSupportEnabled();
-		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
-			public PreparedStatement doPrepare() throws SQLException {
-				return proxiedConnection.prepareStatement( getSqlToPrepare(), columnNames );
-			}
-		};
-		return prepareStatement( statementPreparer, true );
-	}
-
-	private void checkAutoGeneratedKeysSupportEnabled() {
-		if ( ! factory.getSettings().isGetGeneratedKeysEnabled() ) {
-			throw new AssertionFailure("getGeneratedKeys() support is not enabled");
-		}
+		executeBatch();
+		return statementPreparer.prepareStatement( getSQL( sql ), columnNames );
 	}
 
 	/**
 	 * Get a non-batchable prepared statement to use for selecting. Does not
 	 * result in execution of the current batch.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
 	 */
+	@Override
 	public PreparedStatement prepareSelectStatement(String sql) {
-		return prepareStatement( sql, false, false );
+		return statementPreparer.prepareStatement( getSQL( sql ), false );
 	}
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
+	@Override
 	public PreparedStatement prepareStatement(String sql, final boolean isCallable) {
-		return prepareStatement( sql, isCallable, true );
+		executeBatch();
+		return statementPreparer.prepareStatement( getSQL( sql ), isCallable );
 	}
 
 	/**
 	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
+	@Override
 	public CallableStatement prepareCallableStatement(String sql) {
+		executeBatch();
 		log.trace("preparing callable statement");
-		return CallableStatement.class.cast( prepareStatement( sql, true, true ) );
-	}
-
-	public PreparedStatement prepareStatement(String sql, final boolean isCallable, boolean forceExecuteBatch) {
-		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
-			public PreparedStatement doPrepare() throws SQLException {
-				return prepareStatementInternal( getSqlToPrepare(), isCallable );
-			}
-		};
-		return prepareStatement( statementPreparer, forceExecuteBatch );
-	}
-
-	private PreparedStatement prepareStatementInternal(String sql, boolean isCallable) throws SQLException {
-		return isCallable ?
-				proxiedConnection.prepareCall( sql ) :
-				proxiedConnection.prepareStatement( sql );
-	}
-
-	private PreparedStatement prepareScrollableStatementInternal(String sql,
-																 ScrollMode scrollMode,
-																 boolean isCallable) throws SQLException {
-		return isCallable ?
-				proxiedConnection.prepareCall(
-						sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
-				) :
-				proxiedConnection.prepareStatement(
-						sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
-				);
+		return CallableStatement.class.cast( statementPreparer.prepareStatement( getSQL( sql ), true ) );
 	}
 
 	/**
 	 * Get a batchable prepared statement to use for inserting / deleting / updating
 	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
 	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
 	 * statement explicitly.
-	 * @see org.hibernate.jdbc.Batcher#addToBatch
+	 * @see org.hibernate.engine.jdbc.batch.spi.Batch#addToBatch
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
-	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable) {
-		String batchUpdateSQL = getSQL( sql );
-
-		PreparedStatement batchUpdate = getBatcher().getStatement( batchUpdateSQL );
-		if ( batchUpdate == null ) {
-			batchUpdate = prepareStatement( batchUpdateSQL, isCallable, true ); // calls executeBatch()
-			getBatcher().setStatement( batchUpdateSQL, batchUpdate );
+	@Override
+	public PreparedStatement prepareBatchStatement(Object key, String sql, boolean isCallable) {
+		if ( key == null ) {
+			throw new IllegalArgumentException( "batch key must be non-null." );
 		}
-		else {
-			log.debug( "reusing prepared statement" );
-			factory.getJdbcServices().getSqlStatementLogger().logStatement( batchUpdateSQL );
+		String actualSQL = getSQL( sql );
+		PreparedStatement batchUpdate = null;
+		if ( batch != null ) {
+			if ( key.equals( batch.getKey() ) ) {
+				batchUpdate = batch.getBatchStatement( key, actualSQL );
+			}
+			else {
+				batch.execute();
+				batch = null;
+			}
+		}
+		if ( batch == null ) {
+			batch =  batchBuilder.buildBatch(
+					key,
+					logicalConnection.getJdbcServices().getSqlStatementLogger(),
+					logicalConnection.getJdbcServices().getSqlExceptionHelper()
+			);
+		}
+		if ( batchUpdate == null ) {
+			batchUpdate = statementPreparer.prepareStatement( actualSQL, isCallable );
+			batch.addBatchStatement( key, actualSQL, batchUpdate );
 		}
 		return batchUpdate;
 	}
 
-	private Batcher getBatcher() {
-		return logicalConnection.getBatcher();
-	}
-
 	/**
-	 * Get a prepared statement for use in loading / querying. If not explicitly
-	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
-	 * session is closed or disconnected.
+	 * Get a prepared statement for use in loading / querying. Does not
+	 * result in execution of the current batch.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
 	 */
+	@Override
 	public PreparedStatement prepareQueryStatement(
 			String sql,
 			final boolean isScrollable,
 			final ScrollMode scrollMode,
-			final boolean isCallable
-	) {
-		if ( isScrollable && ! factory.getSettings().isScrollableResultSetsEnabled() ) {
-			throw new AssertionFailure("scrollable result sets are not enabled");
-		}
-		StatementPreparer statementPreparer = new StatementPreparer( sql ) {
-			public PreparedStatement doPrepare() throws SQLException {
-				PreparedStatement ps =
-						isScrollable ?
-								prepareScrollableStatementInternal( getSqlToPrepare(), scrollMode, isCallable ) :
-								prepareStatementInternal( getSqlToPrepare(), isCallable )
-						;
-				return ps;
-			}
-			public void afterPrepare(PreparedStatement preparedStatement) throws SQLException {
-				super.afterPrepare( preparedStatement );
-				setStatementFetchSize( preparedStatement, getSqlToPrepare() );
-				logicalConnection.getResourceRegistry().registerLastQuery( preparedStatement );
-			}
-		};
-		return prepareStatement( statementPreparer, false );
-	}
-
-	private void setStatementFetchSize(PreparedStatement statement, String sql) throws SQLException {
-		if ( factory.getSettings().getJdbcFetchSize() != null ) {
-			statement.setFetchSize( factory.getSettings().getJdbcFetchSize() );
-		}
-	}
-
-	private PreparedStatement prepareStatement(StatementPreparer preparer, boolean forceExecuteBatch) {
-		if ( forceExecuteBatch ) {
-			executeBatch();
-		}
-		try {
-			PreparedStatement ps = preparer.doPrepare();
-			preparer.afterPrepare( ps );
-			return ps;
-		}
-		catch ( SQLException sqle ) {
-			log.error( "sqlexception escaped proxy", sqle );
-			throw logicalConnection.getJdbcServices().getSqlExceptionHelper().convert(
-					sqle, "could not prepare statement", preparer.getSqlToPrepare()
-			);
-		}
+			final boolean isCallable) {
+		PreparedStatement ps = (
+				isScrollable ?
+						statementPreparer.prepareScrollableQueryStatement(
+								getSQL( sql ), scrollMode, isCallable
+						) :
+						statementPreparer.prepareQueryStatement(
+								getSQL( sql ), isCallable
+						)
+		);
+		logicalConnection.getResourceRegistry().registerLastQuery( ps );
+		return ps;
 	}
 
 	/**
 	 * Cancel the current query statement
 	 */
+	@Override
 	public void cancelLastQuery() throws HibernateException {
 		logicalConnection.getResourceRegistry().cancelLastQuery();
 	}
 
-	public void abortBatch(SQLException sqle) {
-		getBatcher().abortBatch( sqle );
+	@Override
+	public void addToBatch(Object batchKey, String sql, Expectation expectation) {
+		batch.addToBatch( batchKey, sql, expectation );
 	}
 
-	public void addToBatch(Expectation expectation ) {
-		try {
-			getBatcher().addToBatch( expectation );
-		}
-		catch (SQLException sqle) {
-			throw logicalConnection.getJdbcServices().getSqlExceptionHelper().convert(
-					sqle, "could not add to batch statement" );
+	@Override
+	public void executeBatch() throws HibernateException {
+		if ( batch != null ) {
+			batch.execute();
+			batch.release(); // needed?
 		}
 	}
 
-	public void executeBatch() throws HibernateException {
-		getBatcher().executeBatch();
+	@Override
+	public void abortBatch() {
+		releaseBatch();
+	}
+
+	private void releaseBatch() {
+		if ( batch != null ) {
+			batch.release();
+		}
 	}
 
 	private String getSQL(String sql) {
 		sql = interceptor.onPrepareStatement( sql );
 		if ( sql==null || sql.length() == 0 ) {
 			throw new AssertionFailure( "Interceptor.onPrepareStatement() returned null or empty string." );
 		}
 		return sql;
 	}
 
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
-						connectionReleaseMode,
-						factory.getSettings().getBatcherFactory()
+						connectionReleaseMode
 				)
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
index a4c0249342..bcace088a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
@@ -1,317 +1,305 @@
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
 
-import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
-import java.util.ConcurrentModificationException;
 import java.util.HashMap;
 import java.util.HashSet;
-import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.spi.JdbcWrapper;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.InvalidatableWrapper;
-import org.hibernate.jdbc.Batcher;
-import org.hibernate.jdbc.BatcherFactory;
 
 /**
  * Standard implementation of the {@link org.hibernate.engine.jdbc.spi.JdbcResourceRegistry} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcResourceRegistryImpl implements JdbcResourceRegistry {
 	private static final Logger log = LoggerFactory.getLogger( JdbcResourceRegistryImpl.class );
 
 	private final HashMap<Statement,Set<ResultSet>> xref = new HashMap<Statement,Set<ResultSet>>();
 	private final Set<ResultSet> unassociatedResultSets = new HashSet<ResultSet>();
 	private final SQLExceptionHelper exceptionHelper;
-	private final Batcher batcher;
 
 	private Statement lastQuery;
 
-	public JdbcResourceRegistryImpl(SQLExceptionHelper exceptionHelper, BatcherFactory batcherFactory) {
+	public JdbcResourceRegistryImpl(SQLExceptionHelper exceptionHelper) {
 		this.exceptionHelper = exceptionHelper;
-		this.batcher = batcherFactory.createBatcher( exceptionHelper );
 	}
 
 	public void register(Statement statement) {
 		log.trace( "registering statement [" + statement + "]" );
 		if ( xref.containsKey( statement ) ) {
 			throw new HibernateException( "statement already registered with JDBCContainer" );
 		}
 		xref.put( statement, null );
 	}
 
-	public Batcher getBatcher() {
-		return batcher;
-	}
-
 	@SuppressWarnings({ "unchecked" })
 	public void registerLastQuery(Statement statement) {
 		log.trace( "registering last query statement [{}]", statement );		
 		if ( statement instanceof JdbcWrapper ) {
 			JdbcWrapper<Statement> wrapper = ( JdbcWrapper<Statement> ) statement;
 			registerLastQuery( wrapper.getWrappedObject() );
 			return;
 		}
 		lastQuery = statement;
 	}
 
 	public void cancelLastQuery() {
 		try {
 			if (lastQuery != null) {
 				lastQuery.cancel();
 			}
 		}
 		catch (SQLException sqle) {
 			throw exceptionHelper.convert(
 			        sqle,
 			        "Cannot cancel query"
 				);
 		}
 		finally {
 			lastQuery = null;
 		}
 	}
 
 	public void release(Statement statement) {
 		log.trace( "releasing statement [" + statement + "]" );
 		Set<ResultSet> resultSets = xref.get( statement );
 		if ( resultSets != null ) {
 			for ( ResultSet resultSet : resultSets ) {
 				close( resultSet );
 			}
 			resultSets.clear();
 		}
 		xref.remove( statement );
 		close( statement );
 	}
 
 	public void register(ResultSet resultSet) {
 		log.trace( "registering result set [" + resultSet + "]" );
 		Statement statement;
 		try {
 			statement = resultSet.getStatement();
 		}
 		catch ( SQLException e ) {
 			throw exceptionHelper.convert( e, "unable to access statement from resultset" );
 		}
 		if ( statement != null ) {
 			if ( log.isWarnEnabled() && !xref.containsKey( statement ) ) {
 				log.warn( "resultset's statement was not yet registered" );
 			}
 			Set<ResultSet> resultSets = xref.get( statement );
 			if ( resultSets == null ) {
 				resultSets = new HashSet<ResultSet>();
 				xref.put( statement, resultSets );
 			}
 			resultSets.add( resultSet );
 		}
 		else {
 			unassociatedResultSets.add( resultSet );
 		}
 	}
 
 	public void release(ResultSet resultSet) {
 		log.trace( "releasing result set [{}]", resultSet );
 		Statement statement;
 		try {
 			statement = resultSet.getStatement();
 		}
 		catch ( SQLException e ) {
 			throw exceptionHelper.convert( e, "unable to access statement from resultset" );
 		}
 		if ( statement != null ) {
 			if ( log.isWarnEnabled() && !xref.containsKey( statement ) ) {
 				log.warn( "resultset's statement was not registered" );
 			}
 			Set<ResultSet> resultSets = xref.get( statement );
 			if ( resultSets != null ) {
 				resultSets.remove( resultSet );
 				if ( resultSets.isEmpty() ) {
 					xref.remove( statement );
 				}
 			}
 		}
 		else {
 			boolean removed = unassociatedResultSets.remove( resultSet );
 			if ( !removed ) {
 				log.warn( "ResultSet had no statement associated with it, but was not yet registered" );
 			}
 		}
 		close( resultSet );
 	}
 
 	public boolean hasRegisteredResources() {
 		return ! xref.isEmpty() || ! unassociatedResultSets.isEmpty();
 	}
 
 	public void releaseResources() {
 		log.trace( "releasing JDBC container resources [{}]", this );
 		cleanup();
 	}
 
 	private void cleanup() {
-		batcher.closeStatements();		
 		for ( Map.Entry<Statement,Set<ResultSet>> entry : xref.entrySet() ) {
 			if ( entry.getValue() != null ) {
 				for ( ResultSet resultSet : entry.getValue() ) {
 					close( resultSet );
 				}
 				entry.getValue().clear();
 			}
 			close( entry.getKey() );
 		}
 		xref.clear();
 
 		for ( ResultSet resultSet : unassociatedResultSets ) {
 			close( resultSet );
 		}
 		unassociatedResultSets.clear();
 
 		// TODO: can ConcurrentModificationException still happen???
 		// Following is from old AbstractBatcher...
 		/*
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
         */
 	}
 
 	public void close() {
 		log.trace( "closing JDBC container [{}]", this );
 		cleanup();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected void close(Statement statement) {
 		log.trace( "closing prepared statement [{}]", statement );
 
 		if ( statement instanceof InvalidatableWrapper ) {
 			InvalidatableWrapper<Statement> wrapper = ( InvalidatableWrapper<Statement> ) statement;
 			close( wrapper.getWrappedObject() );
 			wrapper.invalidate();
 			return;
 		}
 
 		try {
 			// if we are unable to "clean" the prepared statement,
 			// we do not close it
 			try {
 				if ( statement.getMaxRows() != 0 ) {
 					statement.setMaxRows( 0 );
 				}
 				if ( statement.getQueryTimeout() != 0 ) {
 					statement.setQueryTimeout( 0 );
 				}
 			}
 			catch( SQLException sqle ) {
 				// there was a problem "cleaning" the prepared statement
 				log.debug( "Exception clearing maxRows/queryTimeout [{}]", sqle.getMessage() );
 				return; // EARLY EXIT!!!
 			}
 			statement.close();
 			if ( lastQuery == statement ) {
 				lastQuery = null;
 			}
 		}
 		catch( SQLException sqle ) {
 			log.debug( "Unable to release statement [{}]", sqle.getMessage() );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected void close(ResultSet resultSet) {
 		log.trace( "closing result set [{}]", resultSet );
 
 		if ( resultSet instanceof InvalidatableWrapper ) {
 			InvalidatableWrapper<ResultSet> wrapper = (InvalidatableWrapper<ResultSet>) resultSet;
 			close( wrapper.getWrappedObject() );
 			wrapper.invalidate();
 		}
 
 		try {
 			resultSet.close();
 		}
 		catch( SQLException e ) {
 			log.debug( "Unable to release result set [{}]", e.getMessage() );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
index 35a8f091b8..8191d94162 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
@@ -1,452 +1,430 @@
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
-import org.hibernate.jdbc.Batcher;
-import org.hibernate.jdbc.BatcherFactory;
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
-	private final JdbcResourceRegistryImpl jdbcResourceRegistry;
+	private final JdbcResourceRegistry jdbcResourceRegistry;
 	private final List<ConnectionObserver> observers = new ArrayList<ConnectionObserver>();
 
 	private boolean releasesEnabled = true;
 
 	private final boolean isUserSuppliedConnection;
 
 	private boolean isClosed;
 
 	public LogicalConnectionImpl(Connection userSuppliedConnection,
 								 ConnectionReleaseMode connectionReleaseMode,
 								 JdbcServices jdbcServices,
-								 StatisticsImplementor statisticsImplementor,
-								 BatcherFactory batcherFactory
+								 StatisticsImplementor statisticsImplementor
 	) {
-		this.jdbcServices = jdbcServices;
-		this.statisticsImplementor = statisticsImplementor;
+		this( connectionReleaseMode,
+				jdbcServices,
+				statisticsImplementor,
+				userSuppliedConnection != null,
+				false
+		);
 		this.physicalConnection = userSuppliedConnection;
-		this.connectionReleaseMode =
-				determineConnectionReleaseMode(
-						jdbcServices, userSuppliedConnection != null, connectionReleaseMode
-				);
-		this.jdbcResourceRegistry =
-				new JdbcResourceRegistryImpl(
-						getJdbcServices().getSqlExceptionHelper(),
-						batcherFactory
-				);
-
-		this.isUserSuppliedConnection = ( userSuppliedConnection != null );
-		this.isClosed = false;
 	}
 
-	// used for deserialization
 	private LogicalConnectionImpl(ConnectionReleaseMode connectionReleaseMode,
 								  JdbcServices jdbcServices,
 								  StatisticsImplementor statisticsImplementor,
-								  BatcherFactory batcherFactory,
 								  boolean isUserSuppliedConnection,
 								  boolean isClosed) {
 		this.connectionReleaseMode = determineConnectionReleaseMode(
 				jdbcServices, isUserSuppliedConnection, connectionReleaseMode
 		);
 		this.jdbcServices = jdbcServices;
 		this.statisticsImplementor = statisticsImplementor;
 		this.jdbcResourceRegistry =
-				new JdbcResourceRegistryImpl(
-						getJdbcServices().getSqlExceptionHelper(),
-						batcherFactory
-				);
+				new JdbcResourceRegistryImpl( getJdbcServices().getSqlExceptionHelper() );
 
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
 
-	public Batcher getBatcher() {
-		return jdbcResourceRegistry.getBatcher();
-	}
-
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
-													ConnectionReleaseMode connectionReleaseMode,
-													BatcherFactory batcherFactory
+													ConnectionReleaseMode connectionReleaseMode
 	) throws IOException {
 		return new LogicalConnectionImpl(
 				connectionReleaseMode,
 				jdbcServices,
 				statisticsImplementor,
-				batcherFactory,
 				ois.readBoolean(),
 				ois.readBoolean()
 		);
  	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/StatementPreparer.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/StatementPreparer.java
new file mode 100644
index 0000000000..2500f328f9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/StatementPreparer.java
@@ -0,0 +1,276 @@
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
+package org.hibernate.engine.jdbc.internal;
+
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Statement;
+
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.HibernateException;
+import org.hibernate.ScrollMode;
+import org.hibernate.TransactionException;
+import org.hibernate.cfg.Settings;
+import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
+import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+
+/**
+ * Prepares statements.
+ *
+ * @author Gail Badner
+ */
+public class StatementPreparer {
+
+	private static final Logger log = LoggerFactory.getLogger( StatementPreparer.class );
+
+	// TODO: Move JDBC settings into a different object...
+	private final Settings settings;
+	private final Connection proxiedConnection;
+	private final SQLExceptionHelper sqlExceptionHelper;
+
+	private long transactionTimeout = -1;
+	boolean isTransactionTimeoutSet;
+
+	/**
+	 * Constructs a StatementPreparer object
+	 * @param logicalConnection - the logical connection
+	 * @param settings - contains settings configured for preparing statements
+	 */
+	public StatementPreparer(LogicalConnectionImplementor logicalConnection, Settings settings) {
+		this.settings = settings;
+		proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
+		sqlExceptionHelper = logicalConnection.getJdbcServices().getSqlExceptionHelper();
+	}
+
+	private abstract class StatementPreparation {
+		private final String sql;
+		protected abstract PreparedStatement doPrepare() throws SQLException;
+		public StatementPreparation(String sql) {
+			this.sql = sql;
+		}
+		public String getSql() {
+			return sql;
+		}
+		public void postProcess(PreparedStatement preparedStatement) throws SQLException {
+			setStatementTimeout( preparedStatement );
+		}
+		public PreparedStatement prepareAndPostProcess() {
+			try {
+				PreparedStatement ps = doPrepare();
+				postProcess( ps );
+				return ps;
+			}
+			catch ( SQLException sqle ) {
+				log.error( "sqlexception escaped proxy", sqle );
+				throw sqlExceptionHelper.convert(
+						sqle, "could not prepare statement", sql
+				);
+			}
+		}
+	}
+
+	private abstract class QueryStatementPreparation extends StatementPreparation {
+		QueryStatementPreparation(String sql) {
+			super( sql );
+		}
+		public void postProcess(PreparedStatement preparedStatement) throws SQLException {
+			super.postProcess( preparedStatement );
+			setStatementFetchSize( preparedStatement );
+		}
+	}
+
+	public void close() {
+		try {
+			proxiedConnection.close();
+		}
+		catch (SQLException sqle) {
+			log.error( "sqlexception escaped proxy", sqle );
+			throw sqlExceptionHelper.convert( sqle, "could not close connection proxy" );
+		}
+	}
+
+	/**
+	 * Prepare a statement. If configured, the query timeout is set.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
+	 *
+	 * @param sql - the SQL for the statement to be prepared
+	 * @param isCallable - true, if a callable statement is to be prepared
+	 * @return the prepared statement
+	 */
+	public PreparedStatement prepareStatement(String sql, final boolean isCallable) {
+		StatementPreparation statementPreparation = new StatementPreparation( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				return isCallable ?
+						proxiedConnection.prepareCall( getSql() ) :
+						proxiedConnection.prepareStatement( getSql() );
+			}
+		};
+		return statementPreparation.prepareAndPostProcess();
+	}
+
+	/**
+	 * Get a prepared statement to use for inserting / deleting / updating,
+	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, int)}).
+	 * If configured, the query timeout is set.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
+
+	 * @param sql - the SQL for the statement to be prepared
+	 * @param autoGeneratedKeys - a flag indicating whether auto-generated
+	 *        keys should be returned; one of
+     *        <code>PreparedStatement.RETURN_GENERATED_KEYS</code> or
+     *	      <code>Statement.NO_GENERATED_KEYS</code>
+	 * @return the prepared statement
+	 */
+	public PreparedStatement prepareStatement(String sql, final int autoGeneratedKeys)
+			throws HibernateException {
+		if ( autoGeneratedKeys == PreparedStatement.RETURN_GENERATED_KEYS ) {
+			checkAutoGeneratedKeysSupportEnabled();
+		}
+		StatementPreparation statementPreparation = new StatementPreparation( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				return proxiedConnection.prepareStatement( getSql(), autoGeneratedKeys );
+			}
+		};
+		return statementPreparation.prepareAndPostProcess();
+	}
+
+	/**
+	 * Get a prepared statement to use for inserting / deleting / updating.
+	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
+	 * If configured, the query timeout is set.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
+	 */
+	public PreparedStatement prepareStatement(String sql, final String[] columnNames) {
+		checkAutoGeneratedKeysSupportEnabled();
+		StatementPreparation preparation = new StatementPreparation( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				return proxiedConnection.prepareStatement( getSql(), columnNames );
+			}
+		};
+		return preparation.prepareAndPostProcess();
+	}
+
+	private void checkAutoGeneratedKeysSupportEnabled() {
+		if ( ! settings.isGetGeneratedKeysEnabled() ) {
+			throw new AssertionFailure("getGeneratedKeys() support is not enabled");
+		}
+	}
+
+	/**
+	 * Get a prepared statement for use in loading / querying.
+	 * If configured, the query timeout and statement fetch size are set.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
+	 */
+	public PreparedStatement prepareQueryStatement(
+			String sql,
+			final boolean isCallable
+	) {
+		StatementPreparation prep = new QueryStatementPreparation( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+				return isCallable ?
+						proxiedConnection.prepareCall( getSql() ) :
+						proxiedConnection.prepareStatement( getSql() );
+			}
+		};
+		return prep.prepareAndPostProcess();
+	}
+
+	/**
+	 * Get a scrollable prepared statement for use in loading / querying.
+	 * If configured, the query timeout and statement fetch size are set.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
+	 */
+	public PreparedStatement prepareScrollableQueryStatement(
+			String sql,
+			final ScrollMode scrollMode,
+			final boolean isCallable
+	) {
+		if ( ! settings.isScrollableResultSetsEnabled() ) {
+			throw new AssertionFailure("scrollable result sets are not enabled");
+		}
+		StatementPreparation prep = new QueryStatementPreparation( sql ) {
+			public PreparedStatement doPrepare() throws SQLException {
+					return isCallable ?
+							proxiedConnection.prepareCall(
+									getSql(), scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
+							) :
+							proxiedConnection.prepareStatement(
+									getSql(), scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
+					);
+			}
+		};
+		return prep.prepareAndPostProcess();
+	}
+
+	/**
+	 * Sets the transaction timeout.
+	 * @param seconds - number of seconds until the the transaction times out.
+	 */
+	public void setTransactionTimeout(int seconds) {
+		isTransactionTimeoutSet = true;
+		transactionTimeout = System.currentTimeMillis() / 1000 + seconds;
+	}
+
+	/**
+	 * Unset the transaction timeout, called after the end of a
+	 * transaction.
+	 */
+	public void unsetTransactionTimeout() {
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
+	private void setStatementFetchSize(PreparedStatement statement) throws SQLException {
+		if ( settings.getJdbcFetchSize() != null ) {
+			statement.setFetchSize( settings.getJdbcFetchSize() );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/PreparedStatementProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/PreparedStatementProxyHandler.java
index cc8f76a2d7..e78e3e32bf 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/PreparedStatementProxyHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/PreparedStatementProxyHandler.java
@@ -1,74 +1,81 @@
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
 
 import java.lang.reflect.Method;
 import java.sql.Connection;
 import java.sql.Statement;
+import java.util.Arrays;
+
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Invocation handler for {@link java.sql.PreparedStatement} proxies
  *
  * @author Steve Ebersole
  */
 public class PreparedStatementProxyHandler extends AbstractStatementProxyHandler {
+	private static final Logger log = LoggerFactory.getLogger( ConnectionProxyHandler.class );
+
 	private final String sql;
 
 	protected PreparedStatementProxyHandler(
 			String sql,
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		super( statement, connectionProxyHandler, connectionProxy );
 		connectionProxyHandler.getJdbcServices().getSqlStatementLogger().logStatement( sql );
 		this.sql = sql;
 	}
 
 	protected void beginningInvocationHandling(Method method, Object[] args) {
 		if ( isExecution( method ) ) {
 			logExecution();
 		}
 		else {
 			journalPossibleParameterBind( method, args );
 		}
 	}
 
 	private void journalPossibleParameterBind(Method method, Object[] args) {
 		String methodName = method.getName();
 		// todo : is this enough???
 		if ( methodName.startsWith( "set" ) && args != null && args.length >= 2 ) {
 			journalParameterBind( method, args );
 		}
 	}
 
 	private void journalParameterBind(Method method, Object[] args) {
+		log.trace( "binding via {}: []", method.getName(), Arrays.asList( args ) );
 	}
 
 	private boolean isExecution(Method method) {
 		return false;
 	}
 
 	private void logExecution() {
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
index 733fa01891..9e7d35fb8a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
@@ -1,188 +1,213 @@
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
 	Connection getConnection();
 
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
 
+	/**
+	 * Sets the transaction timeout.
+	 * @param seconds - number of seconds until the the transaction times out.
+	 */
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
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
 	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys);
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
 	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
 	public PreparedStatement prepareStatement(String sql, String[] columnNames);
 
 	/**
 	 * Get a non-batchable prepared statement to use for selecting. Does not
 	 * result in execution of the current batch.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
 	 */
 	public PreparedStatement prepareSelectStatement(String sql);
 
 	/**
 	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
 	public PreparedStatement prepareStatement(String sql, boolean isCallable);
 
 	/**
 	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
 	public CallableStatement prepareCallableStatement(String sql);
 
 	/**
 	 * Get a batchable prepared statement to use for inserting / deleting / updating
 	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
 	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
 	 * statement explicitly.
-	 * @see org.hibernate.jdbc.Batcher#addToBatch
+	 * @see org.hibernate.engine.jdbc.batch.spi.Batch#addToBatch
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
+	 * released when the session is closed or disconnected.
 	 */
-	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable);
+	public PreparedStatement prepareBatchStatement(Object key, String sql, boolean isCallable);
 
 	/**
-	 * Get a prepared statement for use in loading / querying. If not explicitly
-	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
-	 * session is closed or disconnected.
+	 * Get a prepared statement for use in loading / querying. Does not
+	 * result in execution of the current batch.
+	 * <p/>
+	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
+	 * it will be released when the session is closed or disconnected.
 	 */
 	public PreparedStatement prepareQueryStatement(
 			String sql,
 			boolean isScrollable,
 			ScrollMode scrollMode,
 			boolean isCallable);
+	
 	/**
 	 * Cancel the current query statement
 	 */
 	public void cancelLastQuery();
 
-	public void abortBatch(SQLException sqle);
+	public void abortBatch();
 
-	public void addToBatch(Expectation expectation );
+	public void addToBatch(Object batchKey, String sql, Expectation expectation);
 
 	public void executeBatch();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index 703b81deed..ec43d984ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1344 +1,1339 @@
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
-import org.hibernate.jdbc.BatcherFactory;
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
 
-	public BatcherFactory getBatcherFactory() {
-		return settings.getBatcherFactory();
-	}
-
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
deleted file mode 100644
index d619475881..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
+++ /dev/null
@@ -1,161 +0,0 @@
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
-package org.hibernate.jdbc;
-
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
-import org.hibernate.HibernateException;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-
-/**
- * Manages prepared statements and batching.
- *
- * @author Gavin King
- */
-public abstract class AbstractBatcher implements Batcher {
-
-	protected static final Logger log = LoggerFactory.getLogger( AbstractBatcher.class );
-
-	private final SQLExceptionHelper exceptionHelper;
-	private final int jdbcBatchSize;
-
-	private PreparedStatement batchUpdate;
-	private String batchUpdateSQL;
-	private boolean isClosingBatchUpdate = false;
-
-	public AbstractBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
-		this.exceptionHelper = exceptionHelper;
-		this.jdbcBatchSize = jdbcBatchSize;
-	}
-
-	public final int getJdbcBatchSize() {
-		return jdbcBatchSize;
-	}
-
-	public boolean hasOpenResources() {
-		try {
-			return !isClosingBatchUpdate && batchUpdate != null && ! batchUpdate.isClosed();
-		}
-		catch (SQLException sqle) {
-			throw exceptionHelper.convert(
-					sqle,
-					"Could check to see if batch statement was closed",
-					batchUpdateSQL
-				);
-		}
-	}
-
-	public PreparedStatement getStatement(String sql) {
-		return batchUpdate != null && batchUpdateSQL.equals( sql ) ? batchUpdate : null;
-	}
-
-	public void setStatement(String sql, PreparedStatement ps) {
-		checkNotClosingBatchUpdate();
-		batchUpdateSQL = sql;
-		batchUpdate = ps;		
-	}
-
-	protected PreparedStatement getStatement() {
-		return batchUpdate;
-	}
-
-	public void abortBatch(SQLException sqle) {
-		closeStatements();
-	}
-
-	/**
-	 * Actually releases the batcher, allowing it to cleanup internally held
-	 * resources.
-	 */
-	public void closeStatements() {
-		try {
-			closeBatchUpdate();
-		}
-		catch ( SQLException sqle ) {
-			//no big deal
-			log.warn( "Could not close a JDBC prepared statement", sqle );
-		}
-		batchUpdate = null;
-		batchUpdateSQL = null;
-	}
-
-	public void executeBatch() throws HibernateException {
-		checkNotClosingBatchUpdate();
-		if (batchUpdate!=null) {
-			try {
-				try {
-					doExecuteBatch(batchUpdate);
-				}
-				finally {
-					closeBatchUpdate();
-				}
-			}
-			catch (SQLException sqle) {
-				throw exceptionHelper.convert(
-				        sqle,
-				        "Could not execute JDBC batch update",
-				        batchUpdateSQL
-					);
-			}
-			finally {
-				batchUpdate=null;
-				batchUpdateSQL=null;
-			}
-		}
-	}
-
-	protected abstract void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException;
-
-
-	private void closeBatchUpdate() throws SQLException{
-		checkNotClosingBatchUpdate();
-		try {
-			if ( batchUpdate != null ) {
-				isClosingBatchUpdate = true;
-				batchUpdate.close();
-			}
-		}
-		finally {
-			isClosingBatchUpdate = false;
-		}
-
-	}
-
-	private void checkNotClosingBatchUpdate() {
-		if ( isClosingBatchUpdate ) {
-			throw new IllegalStateException( "Cannot perform operation while closing batch update." );
-		}
-	}
-}
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
deleted file mode 100644
index df96c984e7..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
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
-package org.hibernate.jdbc;
-
-import java.sql.CallableStatement;
-import java.sql.Connection;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-
-import org.hibernate.HibernateException;
-import org.hibernate.ScrollMode;
-import org.hibernate.dialect.Dialect;
-
-/**
- * Manages <tt>PreparedStatement</tt>s for a session. Abstracts JDBC
- * batching to maintain the illusion that a single logical batch
- * exists for the whole session, even when batching is disabled.
- * Provides transparent <tt>PreparedStatement</tt> caching.
- *
- * @see java.sql.PreparedStatement
- * @see org.hibernate.impl.SessionImpl
- * @author Gavin King
- */
-public interface Batcher {
-
-	public PreparedStatement getStatement(String sql);
-	public void setStatement(String sql, PreparedStatement ps);
-	public boolean hasOpenResources();
-
-	/**
-	 * Add an insert / delete / update to the current batch (might be called multiple times
-	 * for single <tt>prepareBatchStatement()</tt>)
-	 */
-	public void addToBatch(Expectation expectation) throws SQLException, HibernateException;
-
-	/**
-	 * Execute the batch
-	 */
-	public void executeBatch() throws HibernateException;
-
-	/**
-	 * Must be called when an exception occurs
-	 * @param sqle the (not null) exception that is the reason for aborting
-	 */
-	public void abortBatch(SQLException sqle);
-
-	/**
-	 * Actually releases the batcher, allowing it to cleanup internally held
-	 * resources.
-	 */
-	public void closeStatements();	
-}
-
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
deleted file mode 100755
index bf03718847..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
+++ /dev/null
@@ -1,38 +0,0 @@
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
-package org.hibernate.jdbc;
-
-import org.hibernate.Interceptor;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-
-
-/**
- * Factory for <tt>Batcher</tt> instances.
- * @author Gavin King
- */
-public interface BatcherFactory {
-	public void setJdbcBatchSize(int jdbcBatchSize);
-	public Batcher createBatcher(SQLExceptionHelper exceptionHelper);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
deleted file mode 100644
index aae2972d32..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
+++ /dev/null
@@ -1,100 +0,0 @@
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
-package org.hibernate.jdbc;
-
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-
-import org.hibernate.HibernateException;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-
-/**
- * An implementation of the <tt>Batcher</tt> interface that
- * actually uses batching
- * @author Gavin King
- */
-public class BatchingBatcher extends AbstractBatcher {
-
-	private Expectation[] expectations;
-	
-	private int currentSize;
-	public BatchingBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
-		super( exceptionHelper, jdbcBatchSize );
-		expectations = new Expectation[ jdbcBatchSize ];
-		currentSize = 0;
-	}
-
-	public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
-		if ( !expectation.canBeBatched() ) {
-			throw new HibernateException( "attempting to batch an operation which cannot be batched" );
-		}
-		PreparedStatement batchUpdate = getStatement();
-		batchUpdate.addBatch();
-		expectations[ currentSize++ ] = expectation;
-		if ( currentSize == getJdbcBatchSize() ) {
-			doExecuteBatch( batchUpdate );
-		}
-	}
-
-	protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
-		if ( currentSize == 0 ) {
-			log.debug( "no batched statements to execute" );
-		}
-		else {
-			if ( log.isDebugEnabled() ) {
-				log.debug( "Executing batch size: " + currentSize );
-			}
-
-			try {
-				checkRowCounts( ps.executeBatch(), ps );
-			}
-			catch (RuntimeException re) {
-				log.error( "Exception executing batch: ", re );
-				throw re;
-			}
-			finally {
-				currentSize = 0;
-			}
-
-		}
-	}
-
-	private void checkRowCounts(int[] rowCounts, PreparedStatement ps) throws SQLException, HibernateException {
-		int numberOfRowCounts = rowCounts.length;
-		if ( numberOfRowCounts != currentSize ) {
-			log.warn( "JDBC driver did not return the expected number of row counts" );
-		}
-		for ( int i = 0; i < numberOfRowCounts; i++ ) {
-			expectations[i].verifyOutcome( rowCounts[i], ps, i );
-		}
-	}
-
-}
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
deleted file mode 100755
index a1e14dbc0b..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
+++ /dev/null
@@ -1,48 +0,0 @@
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
-package org.hibernate.jdbc;
-
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-
-
-/**
- * A BatcherFactory implementation which constructs Batcher instances
- * capable of actually performing batch operations.
- * 
- * @author Gavin King
- */
-public class BatchingBatcherFactory implements BatcherFactory {
-
-	private int jdbcBatchSize;
-
-	public void setJdbcBatchSize(int jdbcBatchSize) {
-		this.jdbcBatchSize = jdbcBatchSize;
-	}
-
-	public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
-		return new BatchingBatcher( exceptionHelper, jdbcBatchSize );
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
deleted file mode 100644
index 6dfea07339..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
+++ /dev/null
@@ -1,53 +0,0 @@
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
-package org.hibernate.jdbc;
-
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-
-import org.hibernate.HibernateException;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-
-/**
- * An implementation of the <tt>Batcher</tt> interface that does no batching
- *
- * @author Gavin King
- */
-public class NonBatchingBatcher extends AbstractBatcher {
-
-	public NonBatchingBatcher(SQLExceptionHelper exceptionHelper) {
-		super( exceptionHelper, 1 );
-	}
-
-	public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
-		PreparedStatement statement = getStatement();
-		final int rowCount = statement.executeUpdate();
-		expectation.verifyOutcome( rowCount, statement, 0 );
-	}
-
-	protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
deleted file mode 100755
index ee55421e0d..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
+++ /dev/null
@@ -1,49 +0,0 @@
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
-package org.hibernate.jdbc;
-
-import org.hibernate.AssertionFailure;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-
-
-/**
- * A BatcherFactory implementation which constructs Batcher instances
- * that do not perform batch operations.
- *
- * @author Gavin King
- */
-public class NonBatchingBatcherFactory implements BatcherFactory {
-
-	public void setJdbcBatchSize(int jdbcBatchSize) {
-		if ( jdbcBatchSize > 1 ) {
-			throw new AssertionFailure( "jdbcBatchSize must be 1 for " + getClass().getName() );
-		}
-	}
-
-	public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
-		return new NonBatchingBatcher( exceptionHelper );
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index d297f6e1a0..cabd43a6bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -86,1735 +86,1741 @@ import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.FilterHelper;
 import org.hibernate.util.StringHelper;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  *
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 	// TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	//SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final String sqlWhereString;
 	private final String sqlOrderByStringTemplate;
 	private final String sqlWhereStringTemplate;
 	private final boolean hasOrder;
 	protected final boolean hasWhere;
 	private final int baseIndex;
 	
 	private final String nodeName;
 	private final String elementNodeName;
 	private final String indexNodeName;
 
 	protected final boolean indexContainsFormula;
 	protected final boolean elementIsPureFormula;
 	
 	//types
 	private final Type keyType;
 	private final Type indexType;
 	protected final Type elementType;
 	private final Type identifierType;
 
 	//columns
 	protected final String[] keyColumnNames;
 	protected final String[] indexColumnNames;
 	protected final String[] indexFormulaTemplates;
 	protected final String[] indexFormulas;
 	protected final boolean[] indexColumnIsSettable;
 	protected final String[] elementColumnNames;
 	protected final String[] elementColumnWriters;
 	protected final String[] elementColumnReaders;
 	protected final String[] elementColumnReaderTemplates;
 	protected final String[] elementFormulaTemplates;
 	protected final String[] elementFormulas;
 	protected final boolean[] elementColumnIsSettable;
 	protected final boolean[] elementColumnIsInPrimaryKey;
 	protected final String[] indexColumnAliases;
 	protected final String[] elementColumnAliases;
 	protected final String[] keyColumnAliases;
 	
 	protected final String identifierColumnName;
 	private final String identifierColumnAlias;
 	//private final String unquotedIdentifierColumnName;
 
 	protected final String qualifiedTableName;
 
 	private final String queryLoaderName;
 
 	private final boolean isPrimitiveArray;
 	private final boolean isArray;
 	protected final boolean hasIndex;
 	protected final boolean hasIdentifier;
 	private final boolean isLazy;
 	private final boolean isExtraLazy;
 	private final boolean isInverse;
 	private final boolean isMutable;
 	private final boolean isVersioned;
 	protected final int batchSize;
 	private final FetchMode fetchMode;
 	private final boolean hasOrphanDelete;
 	private final boolean subselectLoadable;
 
 	//extra information about the element type
 	private final Class elementClass;
 	private final String entityName;
 
 	private final Dialect dialect;
 	private final SQLExceptionHelper sqlExceptionHelper;
 	private final SessionFactoryImplementor factory;
 	private final EntityPersister ownerPersister;
 	private final IdentifierGenerator identifierGenerator;
 	private final PropertyMapping elementPropertyMapping;
 	private final EntityPersister elementPersister;
 	private final CollectionRegionAccessStrategy cacheAccessStrategy;
 	private final CollectionType collectionType;
 	private CollectionInitializer initializer;
 	
 	private final CacheEntryStructure cacheEntryStructure;
 
 	// dynamic filters for the collection
 	private final FilterHelper filterHelper;
 
 	// dynamic filters specifically for many-to-many inside the collection
 	private final FilterHelper manyToManyFilterHelper;
 
 	private final String manyToManyWhereString;
 	private final String manyToManyWhereTemplate;
 
 	private final boolean hasManyToManyOrder;
 	private final String manyToManyOrderByTemplate;
 
 	// custom sql
 	private final boolean insertCallable;
 	private final boolean updateCallable;
 	private final boolean deleteCallable;
 	private final boolean deleteAllCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private final Serializable[] spaces;
 
 	private Map collectionPropertyColumnAliases = new HashMap();
 	private Map collectionPropertyColumnNames = new HashMap();
 
 	private static final Logger log = LoggerFactory.getLogger( AbstractCollectionPersister.class );
 
 	public AbstractCollectionPersister(
 			final Collection collection,
 			final CollectionRegionAccessStrategy cacheAccessStrategy,
 			final Configuration cfg,
 			final SessionFactoryImplementor factory) throws MappingException, CacheException {
 
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collection.isMap() ?
 					( CacheEntryStructure ) new StructuredMapCacheEntry() :
 					( CacheEntryStructure ) new StructuredCollectionCacheEntry();
 		}
 		else {
 			cacheEntryStructure = new UnstructuredCacheEntry();
 		}
 		
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collection.getCollectionType();
 		role = collection.getRole();
 		entityName = collection.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister(entityName);
 		queryLoaderName = collection.getLoaderName();
 		nodeName = collection.getNodeName();
 		isMutable = collection.isMutable();
 
 		Table table = collection.getCollectionTable();
 		fetchMode = collection.getElement().getFetchMode();
 		elementType = collection.getElement().getType();
 		//isSet = collection.isSet();
 		//isSorted = collection.isSorted();
 		isPrimitiveArray = collection.isPrimitiveArray();
 		isArray = collection.isArray();
 		subselectLoadable = collection.isSubselectLoadable();
 		
 		qualifiedTableName = table.getQualifiedName( 
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName() 
 			);
 
 		int spacesSize = 1 + collection.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collection.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collection.getWhere() ) ? "( " + collection.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate(sqlWhereString, dialect, factory.getSqlFunctionRegistry()) :
 				null;
 
 		hasOrphanDelete = collection.hasOrphanDelete();
 
 		int batch = collection.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collection.isOptimisticLocked();
 		
 		// KEY
 
 		keyType = collection.getKey().getType();
 		iter = collection.getKey().getColumnIterator();
 		int keySpan = collection.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName(dialect);
 			keyColumnAliases[k] = col.getAlias(dialect,collection.getOwner().getRootTable());
 			k++;
 		}
 		
 		//unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		//ELEMENT
 
 		String elemNode = collection.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister(entityName);
 			if ( elemNode==null ) {
 				elemNode = cfg.getClassMapping(entityName).getNodeName();
 			}
 			// NativeSQL: collect element column and auto-aliases
 			
 		}
 		else {
 			elementPersister = null;
 		}		
 		elementNodeName = elemNode;
 
 		int elementSpan = collection.getElement().getColumnSpan();
 		elementColumnAliases = new String[elementSpan];
 		elementColumnNames = new String[elementSpan];
 		elementColumnWriters = new String[elementSpan];
 		elementColumnReaders = new String[elementSpan];
 		elementColumnReaderTemplates = new String[elementSpan];
 		elementFormulaTemplates = new String[elementSpan];
 		elementFormulas = new String[elementSpan];
 		elementColumnIsSettable = new boolean[elementSpan];
 		elementColumnIsInPrimaryKey = new boolean[elementSpan];
 		boolean isPureFormula = true;
 		boolean hasNotNullableColumns = false;
 		int j = 0;
 		iter = collection.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias(dialect);
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName(dialect);
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr(dialect);
 				elementColumnReaderTemplates[j] = col.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementColumnIsSettable[j] = true;
 				elementColumnIsInPrimaryKey[j] = !col.isNullable();
 				if ( !col.isNullable() ) {
 					hasNotNullableColumns = true;
 				}
 				isPureFormula = false;
 			}
 			j++;
 		}
 		elementIsPureFormula = isPureFormula;
 		
 		//workaround, for backward compatibility of sets with no
 		//not-null columns, assume all columns are used in the
 		//row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collection.isIndexed();
 		if (hasIndex) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collection;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
 			indexColumnIsSettable = new boolean[indexSpan];
 			indexColumnAliases = new String[indexSpan];
 			int i = 0;
 			boolean hasFormula = false;
 			while ( iter.hasNext() ) {
 				Selectable s = (Selectable) iter.next();
 				indexColumnAliases[i] = s.getAlias(dialect);
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate(dialect, factory.getSqlFunctionRegistry());
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName(dialect);
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ? 
 					( (List) indexedCollection ).getBaseIndex() : 0;
 
 			indexNodeName = indexedCollection.getIndexNodeName(); 
 
 		}
 		else {
 			indexContainsFormula = false;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
 			indexNodeName = null;
 		}
 		
 		hasIdentifier = collection.isIdentified();
 		if (hasIdentifier) {
 			if ( collection.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collection;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = ( Column ) iter.next();
 			identifierColumnName = col.getQuotedName(dialect);
 			identifierColumnAlias = col.getAlias(dialect);
 			//unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					cfg.getIdentifierGeneratorFactory(),
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName(),
 					null
 			);
 		}
 		else {
 			identifierType = null;
 			identifierColumnName = null;
 			identifierColumnAlias = null;
 			//unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 		
 		//GENERATE THE SQL:
 				
 		//sqlSelectString = sqlSelectString();
 		//sqlSelectRowString = sqlSelectRowString();
 
 		if ( collection.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collection.getCustomSQLInsert();
 			insertCallable = collection.isCustomInsertCallable();
 			insertCheckStyle = collection.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLInsert(), insertCallable )
 		            : collection.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collection.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collection.getCustomSQLUpdate();
 			updateCallable = collection.isCustomUpdateCallable();
 			updateCheckStyle = collection.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLUpdate(), insertCallable )
 		            : collection.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collection.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collection.getCustomSQLDelete();
 			deleteCallable = collection.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collection.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collection.getCustomSQLDeleteAll();
 			deleteAllCallable = collection.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString(  collection.isIndexed() && !collection.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 		
 		logStaticSQL();
 		
 		isLazy = collection.isLazy();
 		isExtraLazy = collection.isExtraLazy();
 
 		isInverse = collection.isInverse();
 
 		if ( collection.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collection ).getElementClass();
 		}
 		else {
 			// for non-arrays, we don't need to know the element class
 			elementClass = null; //elementType.returnedClass();
 		}
 
 		if ( elementType.isComponentType() ) {
 			elementPropertyMapping = new CompositeElementPropertyMapping( 
 					elementColumnNames,
 					elementColumnReaders,
 					elementColumnReaderTemplates,
 					elementFormulaTemplates,
 					(CompositeType) elementType,
 					factory 
 				);
 		}
 		else if ( !elementType.isEntityType() ) {
 			elementPropertyMapping = new ElementPropertyMapping( 
 					elementColumnNames,
 					elementType 
 				);
 		}
 		else {
 			if ( elementPersister instanceof PropertyMapping ) { //not all classpersisters implement PropertyMapping!
 				elementPropertyMapping = (PropertyMapping) elementPersister;
 			}
 			else {
 				elementPropertyMapping = new ElementPropertyMapping( 
 						elementColumnNames,
 						elementType 
 					);
 			}
 		}
 
 		hasOrder = collection.getOrderBy() != null;
 		if ( hasOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			sqlOrderByStringTemplate = Template.renderOrderByStringTemplate(
 					collection.getOrderBy(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			sqlOrderByStringTemplate = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			manyToManyOrderByTemplate = Template.renderOrderByStringTemplate(
 					collection.getManyToManyOrdering(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTemplate = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "Static SQL for collection: " + getRole() );
 			if ( getSQLInsertRowString() != null ) {
 				log.debug( " Row insert: " + getSQLInsertRowString() );
 			}
 			if ( getSQLUpdateRowString() != null ) {
 				log.debug( " Row update: " + getSQLUpdateRowString() );
 			}
 			if ( getSQLDeleteRowString() != null ) {
 				log.debug( " Row delete: " + getSQLDeleteRowString() );
 			}
 			if ( getSQLDeleteString() != null ) {
 				log.debug( " One-shot delete: " + getSQLDeleteString() );
 			}
 		}
 	}
 
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			//if there is a user-specified loader, return that
 			//TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
 		if ( !isSubselectLoadable() ) {
 			return null;
 		}
 		
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		
 		SubselectFetch subselect = persistenceContext.getBatchFetchQueue()
 			.getSubselect( new EntityKey( key, getOwnerEntityPersister(), session.getEntityMode() ) );
 		
 		if (subselect == null) {
 			return null;
 		}
 		else {
 			
 			// Take care of any entities that might have
 			// been evicted!	
 			Iterator iter = subselect.getResult().iterator();
 			while ( iter.hasNext() ) {
 				if ( !persistenceContext.containsEntity( (EntityKey) iter.next() ) ) {
 					iter.remove();
 				}
 			}	
 			
 			// Run a subquery loader
 			return createSubselectInitializer( subselect, session );
 		}
 	}
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? StringHelper.replace( sqlOrderByStringTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? StringHelper.replace( manyToManyOrderByTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	public boolean hasWhere() {
 		return hasWhere;
 	}
 
 	protected String getSQLDeleteString() {
 		return sqlDeleteString;
 	}
 
 	protected String getSQLInsertRowString() {
 		return sqlInsertRowString;
 	}
 
 	protected String getSQLUpdateRowString() {
 		return sqlUpdateRowString;
 	}
 
 	protected String getSQLDeleteRowString() {
 		return sqlDeleteRowString;
 	}
 
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass() { //needed by arrays
 		return elementClass;
 	}
 
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session) 
 	throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session) 
 	throws HibernateException, SQLException {
 		Object index = getIndexType().nullSafeGet( rs, aliases, session, null );
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() - baseIndex );
 		}
 		return index;
 	}
 
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session) 
 	throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session) 
 	throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role );  //an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsSettable, session);
 		return i + ArrayHelper.countTrue(elementColumnIsSettable);
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue(indexColumnIsSettable);
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() + baseIndex );
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (elementIsPureFormula) {
 			throw new AssertionFailure("cannot use a formula-based element in the where condition");
 		}
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsInPrimaryKey, session);
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (indexContainsFormula) {
 			throw new AssertionFailure("cannot use a formula-based index in the where condition");
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	public boolean isArray() {
 		return isArray;
 	}
 
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 	
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		} else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); //strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ? 
 			"max(" + getIndexColumnNames()[0] + ") + 1": //lists, arrays
 			"count(" + getElementColumnNames()[0] + ")"; //sets, maps, bags
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn(selectValue)
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i=0; i<elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i=0; i<indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify(alias, indexColumnNames, indexFormulaTemplates);
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify(alias, elementColumnNames, elementFormulaTemplates);
 	}
 	
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for (int i=0; i<span; i++) {
 			if ( columnNames[i]==null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; //TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"Deleting collection: " + 
 						MessageHelper.collectionInfoString( this, id, getFactory() ) 
 					);
 			}
 
 			// Remove all the old entries
 
 			try {
 				int offset = 1;
 				PreparedStatement st = null;
 				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
 				boolean callable = isDeleteAllCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLDeleteString();
 				if ( useBatch ) {
-					st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+					st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( this, sql, callable );
 				}
 				else {
 					st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 				}
 
 
 				try {
 					offset+= expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
-						session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+						session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 					}
 					else {
 						expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
-						session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+						session.getJDBCContext().getConnectionManager().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						st.close();
 					}
 				}
 
 				if ( log.isDebugEnabled() ) {
 					log.debug( "done deleting collection" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 				        sqle,
 				        "could not delete collection: " + 
 				        MessageHelper.collectionInfoString( this, id, getFactory() ),
 				        getSQLDeleteString()
 					);
 			}
 
 		}
 
 	}
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"Inserting collection: " + 
 						MessageHelper.collectionInfoString( this, id, getFactory() ) 
 					);
 			}
 
 			try {
 				//create all the new entries
 				Iterator entries = collection.entries(this);
 				if ( entries.hasNext() ) {
 					collection.preInsert( this );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 							boolean callable = isInsertCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLInsertRowString();
 
 							if ( useBatch ) {
-								st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+								st = session.getJDBCContext().getConnectionManager().prepareBatchStatement(
+										this, sql, callable
+								);
 							}
 							else {
 								st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 							}
 
 
 							try {
 								offset+= expectation.prepare( st );
 
 								//TODO: copy/paste from insertRows()
 								int loc = writeKey( st, id, offset, session );
 								if ( hasIdentifier ) {
 									loc = writeIdentifier( st, collection.getIdentifier(entry, i), loc, session );
 								}
 								if ( hasIndex /*&& !indexIsFormula*/ ) {
 									loc = writeIndex( st, collection.getIndex(entry, i, this), loc, session );
 								}
 								loc = writeElement(st, collection.getElement(entry), loc, session );
 
 								if ( useBatch ) {
-									session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+									session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 								}
 								else {
 									expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 								}
 
 								collection.afterRowInsert( this, entry, i );
 								count++;
 							}
 							catch ( SQLException sqle ) {
 								if ( useBatch ) {
-									session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+									session.getJDBCContext().getConnectionManager().abortBatch();
 								}
 								throw sqle;
 							}
 							finally {
 								if ( !useBatch ) {
 									st.close();
 								}
 							}
 
 						}
 						i++;
 					}
 
 					if ( log.isDebugEnabled() ) {
 						log.debug( "done inserting collection: " + count + " rows inserted" );
 					}
 
 				}
 				else {
 					if ( log.isDebugEnabled() ) {
 						log.debug( "collection was empty" );
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 				        sqle,
 				        "could not insert collection: " + 
 				        MessageHelper.collectionInfoString( this, id, getFactory() ),
 				        getSQLInsertRowString()
 					);
 			}
 		}
 	}
 	
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"Deleting rows of collection: " + 
 						MessageHelper.collectionInfoString( this, id, getFactory() ) 
 					);
 			}
 			
 			boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 			
 			try {
 				//delete all the deleted entries
 				Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 				if ( deletes.hasNext() ) {
 					int offset = 1;
 					int count = 0;
 					while ( deletes.hasNext() ) {
 						PreparedStatement st = null;
 						Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 						boolean callable = isDeleteCallable();
 						boolean useBatch = expectation.canBeBatched();
 						String sql = getSQLDeleteRowString();
 
 						if ( useBatch ) {
-							st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+							st = session.getJDBCContext().getConnectionManager().prepareBatchStatement(
+									this, sql, callable
+							);
 						}
 						else {
 							st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 						}
 
 						try {
 							expectation.prepare( st );
 
 							Object entry = deletes.next();
 							int loc = offset;
 							if ( hasIdentifier ) {
 								writeIdentifier( st, entry, loc, session );
 							}
 							else {
 								loc = writeKey( st, id, loc, session );
 								if ( deleteByIndex ) {
 									writeIndexToWhere( st, entry, loc, session );
 								}
 								else {
 									writeElementToWhere( st, entry, loc, session );
 								}
 							}
 
 							if ( useBatch ) {
-								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
-								session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+								session.getJDBCContext().getConnectionManager().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								st.close();
 							}
 						}
 
 						if ( log.isDebugEnabled() ) {
 							log.debug( "done deleting collection rows: " + count + " deleted" );
 						}
 					}
 				}
 				else {
 					if ( log.isDebugEnabled() ) {
 						log.debug( "no rows to delete" );
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 				        sqle,
 				        "could not delete collection rows: " + 
 				        MessageHelper.collectionInfoString( this, id, getFactory() ),
 				        getSQLDeleteRowString()
 					);
 			}
 		}
 	}
 	
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"Inserting rows of collection: " + 
 						MessageHelper.collectionInfoString( this, id, getFactory() ) 
 					);
 			}
 
 			try {
 				//insert all the new entries
 				collection.preInsert( this );
 				Iterator entries = collection.entries( this );
 				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean callable = isInsertCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLInsertRowString();
 				int i = 0;
 				int count = 0;
 				while ( entries.hasNext() ) {
 					int offset = 1;
 					Object entry = entries.next();
 					PreparedStatement st = null;
 					if ( collection.needsInserting( entry, i, elementType ) ) {
 
 						if ( useBatch ) {
 							if ( st == null ) {
-								st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+								st = session.getJDBCContext().getConnectionManager().prepareBatchStatement(
+										this, sql, callable
+								);
 							}
 						}
 						else {
 							st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 						}
 
 						try {
 							offset += expectation.prepare( st );
 							//TODO: copy/paste from recreate()
 							offset = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								offset = writeIdentifier( st, collection.getIdentifier(entry, i), offset, session );
 							}
 							if ( hasIndex /*&& !indexIsFormula*/ ) {
 								offset = writeIndex( st, collection.getIndex(entry, i, this), offset, session );
 							}
 							writeElement(st, collection.getElement(entry), offset, session );
 
 							if ( useBatch ) {
-								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
-								session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+								session.getJDBCContext().getConnectionManager().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								st.close();
 							}
 						}
 					}
 					i++;
 				}
 				if ( log.isDebugEnabled() ) {
 					log.debug( "done inserting rows: " + count + " inserted" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 				        sqle,
 				        "could not insert collection rows: " + 
 				        MessageHelper.collectionInfoString( this, id, getFactory() ),
 				        getSQLInsertRowString()
 					);
 			}
 
 		}
 	}
 
 
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	public abstract boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuffer buffer = new StringBuffer();
 		manyToManyFilterHelper.render( buffer, alias, enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			if ( indexFragments == null ) {
 				String[] tmp = new String[indexColumnNames.length];
 				for ( int i = 0; i < indexColumnNames.length; i++ ) {
 					tmp[i] = indexColumnNames[i] == null
 							? indexFormulas[i]
 							: indexColumnNames[i];
 					indexFragments = tmp;
 				}
 			}
 			return indexFragments;
 		}
 
 		return elementPropertyMapping.toColumns( propertyName );
 	}
 
 	public Type getType() {
 		return elementPropertyMapping.getType(); //==elementType ??
 	}
 
 	public String getName() {
 		return getRole();
 	}
 
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return ( Loadable ) elementPersister;
 	}
 
 	public boolean isCollection() {
 		return true;
 	}
 
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session) 
 	throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( "Updating rows of collection: " + role + "#" + id );
 			}
 
 			//update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( "done updating rows: " + count + " updated" );
 			}
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session) 
 	throws HibernateException;
 
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 
 		StringBuffer sessionFilterFragment = new StringBuffer();
 		filterHelper.render( sessionFilterFragment, alias, enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	protected boolean isInsertCallable() {
 		return insertCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	protected boolean isUpdateCallable() {
 		return updateCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	protected boolean isDeleteCallable() {
 		return deleteCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	protected boolean isDeleteAllCallable() {
 		return deleteAllCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 	
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public String getIndexNodeName() {
 		return indexNodeName;
 	}
 
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SQLExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return filterHelper.isAffectedBy( session.getEnabledFilters() ) ||
 		        ( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 	
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = (String[]) collectionPropertyColumnAliases.get(propertyName);
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 		
 		String result[] = new String[rawAliases.length];
 		for ( int i=0; i<rawAliases.length; i++ ) {
 			result[i] = new Alias(suffix).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 	
 	//TODO: formulas ?
 	public void initCollectionPropertyMap() {
 
 		initCollectionPropertyMap( "key", keyType, keyColumnAliases, keyColumnNames );
 		initCollectionPropertyMap( "element", elementType, elementColumnAliases, elementColumnNames );
 		if (hasIndex) {
 			initCollectionPropertyMap( "index", indexType, indexColumnAliases, indexColumnNames );
 		}
 		if (hasIdentifier) {
 			initCollectionPropertyMap( 
 					"id", 
 					identifierType, 
 					new String[] { identifierColumnAlias }, 
 					new String[] { identifierColumnName } 
 				);
 		}
 	}
 
 	private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
 		
 		collectionPropertyColumnAliases.put(aliasName, columnAliases);
 		collectionPropertyColumnNames.put(aliasName, columnNames);
 	
 		if( type.isComponentType() ) {
 			CompositeType ct = (CompositeType) type;
 			String[] propertyNames = ct.getPropertyNames();
 			for (int i = 0; i < propertyNames.length; i++) {
 				String name = propertyNames[i];
 				collectionPropertyColumnAliases.put( aliasName + "." + name, columnAliases[i] );
 				collectionPropertyColumnNames.put( aliasName + "." + name, columnNames[i] );
 			}
 		} 
 		
 	}
 
 	public int getSize(Serializable key, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement(sqlSelectSizeString);
 			try {
 				getKeyType().nullSafeSet(st, key, 1, session);
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next() ? rs.getInt(1) - baseIndex : 0;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch (SQLException sqle) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " + 
 					MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 				);
 		}
 	}
 	
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists(key, incrementIndexByBase(index), getIndexType(), sqlDetectRowByIndexString, session);
 	}
 
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 		return exists(key, element, getElementType(), sqlDetectRowByElementString, session);
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement(sql);
 			try {
 				getKeyType().nullSafeSet(st, key, 1, session);
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next();
 				}
 				finally {
 					rs.close();
 				}
 			}
 			catch( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch (SQLException sqle) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " + 
 					MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 				);
 		}
 	}
 
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement(sqlSelectRowByIndexString);
 			try {
 				getKeyType().nullSafeSet(st, key, 1, session);
 				getIndexType().nullSafeSet( st, incrementIndexByBase(index), keyColumnNames.length + 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet(rs, elementColumnAliases, session, owner);
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch (SQLException sqle) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " + 
 					MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 				);
 		}
 	}
 
 	public boolean isExtraLazy() {
 		return isExtraLazy;
 	}
 	
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	/**
 	 * Intended for internal use only.  In fact really only currently used from 
 	 * test suite for assertion purposes.
 	 *
 	 * @return The default collection initializer for this persister/collection.
 	 */
 	public CollectionInitializer getInitializer() {
 		return initializer;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
index 1a2184cac9..4fa624149b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
@@ -1,334 +1,336 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.BatchingCollectionInitializer;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.loader.collection.SubselectCollectionLoader;
 import org.hibernate.mapping.Collection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.Update;
 import org.hibernate.type.AssociationType;
 import org.hibernate.util.ArrayHelper;
 
 /**
  * Collection persister for collections of values and many-to-many associations.
  *
  * @author Gavin King
  */
 public class BasicCollectionPersister extends AbstractCollectionPersister {
 
 	public boolean isCascadeDeleteEnabled() {
 		return false;
 	}
 
 	public BasicCollectionPersister(
 			Collection collection,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			Configuration cfg,
 			SessionFactoryImplementor factory) throws MappingException, CacheException {
 		super( collection, cacheAccessStrategy, cfg, factory );
 	}
 
 	/**
 	 * Generate the SQL DELETE that deletes all rows
 	 */
 	protected String generateDeleteString() {
 		
 		Delete delete = new Delete()
 				.setTableName( qualifiedTableName )
 				.addPrimaryKeyColumns( keyColumnNames );
 		
 		if ( hasWhere ) delete.setWhere( sqlWhereString );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete collection " + getRole() );
 		}
 		
 		return delete.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL INSERT that creates a new row
 	 */
 	protected String generateInsertRowString() {
 		
 		Insert insert = new Insert( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames );
 		
 		if ( hasIdentifier) insert.addColumn( identifierColumnName );
 		
 		if ( hasIndex /*&& !indexIsFormula*/ ) {
 			insert.addColumns( indexColumnNames, indexColumnIsSettable );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert collection row " + getRole() );
 		}
 		
 		//if ( !elementIsFormula ) {
 			insert.addColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		//}
 		
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a row
 	 */
 	protected String generateUpdateRowString() {
 		
 		Update update = new Update( getDialect() )
 			.setTableName( qualifiedTableName );
 		
 		//if ( !elementIsFormula ) {
 			update.addColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		//}
 		
 		if ( hasIdentifier ) {
 			update.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		else if ( hasIndex && !indexContainsFormula ) {
 			update.addPrimaryKeyColumns( ArrayHelper.join( keyColumnNames, indexColumnNames ) );
 		}
 		else {
 			update.addPrimaryKeyColumns( keyColumnNames );
 			update.addPrimaryKeyColumns( elementColumnNames, elementColumnIsInPrimaryKey, elementColumnWriters );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update collection row " + getRole() );
 		}
 		
 		return update.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL DELETE that deletes a particular row
 	 */
 	protected String generateDeleteRowString() {
 		
 		Delete delete = new Delete()
 			.setTableName( qualifiedTableName );
 		
 		if ( hasIdentifier ) {
 			delete.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		else if ( hasIndex && !indexContainsFormula ) {
 			delete.addPrimaryKeyColumns( ArrayHelper.join( keyColumnNames, indexColumnNames ) );
 		}
 		else {
 			delete.addPrimaryKeyColumns( keyColumnNames );
 			delete.addPrimaryKeyColumns( elementColumnNames, elementColumnIsInPrimaryKey, elementColumnWriters );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete collection row " + getRole() );
 		}
 		
 		return delete.toStatementString();
 	}
 
 	public boolean consumesEntityAlias() {
 		return false;
 	}
 
 	public boolean consumesCollectionAlias() {
 //		return !isOneToMany();
 		return true;
 	}
 
 	public boolean isOneToMany() {
 		return false;
 	}
 
 	public boolean isManyToMany() {
 		return elementType.isEntityType(); //instanceof AssociationType;
 	}
 
 	protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException {
 		
 		if ( ArrayHelper.isAllFalse(elementColumnIsSettable) ) return 0;
 
 		try {
 			PreparedStatement st = null;
 			Expectation expectation = Expectations.appropriateExpectation( getUpdateCheckStyle() );
 			boolean callable = isUpdateCallable();
 			boolean useBatch = expectation.canBeBatched();
 			Iterator entries = collection.entries( this );
 			String sql = getSQLUpdateRowString();
 			int i = 0;
 			int count = 0;
 			while ( entries.hasNext() ) {
 				Object entry = entries.next();
 				if ( collection.needsUpdating( entry, i, elementType ) ) {
 					int offset = 1;
 
 					if ( useBatch ) {
 						if ( st == null ) {
-							st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+							st = session.getJDBCContext().getConnectionManager().prepareBatchStatement(
+									this, sql, callable
+							);
 						}
 					}
 					else {
 						st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 					}
 
 					try {
 						offset+= expectation.prepare( st );
 						int loc = writeElement( st, collection.getElement( entry ), offset, session );
 						if ( hasIdentifier ) {
 							writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 						}
 						else {
 							loc = writeKey( st, id, loc, session );
 							if ( hasIndex && !indexContainsFormula ) {
 								writeIndexToWhere( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 							else {
 								writeElementToWhere( st, collection.getSnapshotElement( entry, i ), loc, session );
 							}
 						}
 
 						if ( useBatch ) {
-							session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+							session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 						}
 						else {
 							expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 						}
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
-							session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+							session.getJDBCContext().getConnectionManager().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							st.close();
 						}
 					}
 					count++;
 				}
 				i++;
 			}
 			return count;
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not update collection rows: " + MessageHelper.collectionInfoString( this, id, getFactory() ),
 					getSQLUpdateRowString()
 				);
 		}
 	}
 
 	public String selectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String entitySuffix,
 	        String collectionSuffix,
 	        boolean includeCollectionColumns) {
 		// we need to determine the best way to know that two joinables
 		// represent a single many-to-many...
 		if ( rhs != null && isManyToMany() && !rhs.isCollection() ) {
 			AssociationType elementType = ( ( AssociationType ) getElementType() );
 			if ( rhs.equals( elementType.getAssociatedJoinable( getFactory() ) ) ) {
 				return manyToManySelectFragment( rhs, rhsAlias, lhsAlias, collectionSuffix );
 			}
 		}
 		return includeCollectionColumns ? selectFragment( lhsAlias, collectionSuffix ) : "";
 	}
 
 	private String manyToManySelectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String collectionSuffix) {
 		SelectFragment frag = generateSelectFragment( lhsAlias, collectionSuffix );
 
 		String[] elementColumnNames = rhs.getKeyColumnNames();
 		frag.addColumns( rhsAlias, elementColumnNames, elementColumnAliases );
 		appendIndexColumns( frag, lhsAlias );
 		appendIdentifierColumns( frag, lhsAlias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); //strip leading ','
 	}
 
 	/**
 	 * Create the <tt>CollectionLoader</tt>
 	 *
 	 * @see org.hibernate.loader.collection.BasicCollectionLoader
 	 */
 	protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException {
 		return BatchingCollectionInitializer.createBatchingCollectionInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return "";
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return "";
 	}
 
 	protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session) {
 		return new SubselectCollectionLoader( 
 				this,
 				subselect.toSubselectString( getCollectionType().getLHSPropertyName() ),
 				subselect.getResult(),
 				subselect.getQueryParameters(),
 				subselect.getNamedParameterLocMap(),
 				session.getFactory(),
 				session.getLoadQueryInfluencers() 
 		);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
index 5045323364..2057a92cd6 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
@@ -1,380 +1,381 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.BatchingCollectionInitializer;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.loader.collection.SubselectOneToManyLoader;
 import org.hibernate.loader.entity.CollectionElementLoader;
 import org.hibernate.mapping.Collection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 import org.hibernate.util.ArrayHelper;
 
 /**
  * Collection persister for one-to-many associations.
  *
  * @author Gavin King
  */
 public class OneToManyPersister extends AbstractCollectionPersister {
 
 	private final boolean cascadeDeleteEnabled;
 	private final boolean keyIsNullable;
 	private final boolean keyIsUpdateable;
 
 	protected boolean isRowDeleteEnabled() {
 		return keyIsUpdateable && keyIsNullable;
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return keyIsUpdateable;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public OneToManyPersister(
 			Collection collection,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			Configuration cfg,
 			SessionFactoryImplementor factory) throws MappingException, CacheException {
 		super( collection, cacheAccessStrategy, cfg, factory );
 		cascadeDeleteEnabled = collection.getKey().isCascadeDeleteEnabled() &&
 				factory.getDialect().supportsCascadeDelete();
 		keyIsNullable = collection.getKey().isNullable();
 		keyIsUpdateable = collection.getKey().isUpdateable();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates all the foreign keys to null
 	 */
 	protected String generateDeleteString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames, "null" )
 				.addPrimaryKeyColumns( keyColumnNames );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames, "null" );
 		
 		if ( hasWhere ) update.setWhere( sqlWhereString );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "delete one-to-many " + getRole() );
 		}
 		
 		return update.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a foreign key to a value
 	 */
 	protected String generateInsertRowString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames );
 		
 		//identifier collections not supported for 1-to-many
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "create one-to-many row " + getRole() );
 		}
 		
 		return update.addPrimaryKeyColumns( elementColumnNames, elementColumnWriters )
 				.toStatementString();
 	}
 
 	/**
 	 * Not needed for one-to-many association
 	 */
 	protected String generateUpdateRowString() {
 		return null;
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a particular row's foreign
 	 * key to null
 	 */
 	protected String generateDeleteRowString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames, "null" );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames, "null" );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "delete one-to-many row " + getRole() );
 		}
 		
 		//use a combination of foreign key columns and pk columns, since
 		//the ordering of removal and addition is not guaranteed when
 		//a child moves from one parent to another
 		String[] rowSelectColumnNames = ArrayHelper.join(keyColumnNames, elementColumnNames);
 		return update.addPrimaryKeyColumns( rowSelectColumnNames )
 				.toStatementString();
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 	public boolean consumesCollectionAlias() {
 		return true;
 	}
 
 	public boolean isOneToMany() {
 		return true;
 	}
 
 	public boolean isManyToMany() {
 		return false;
 	}
 
 	protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException {
 
 		// we finish all the "removes" first to take care of possible unique
 		// constraints and so that we can take better advantage of batching
 		
 		try {
 			int count = 0;
 			if ( isRowDeleteEnabled() ) {
 				boolean useBatch = true;
 				PreparedStatement st = null;
 				// update removed rows fks to null
 				try {
 					int i = 0;
 	
 					Iterator entries = collection.entries( this );
 					int offset = 1;
 					Expectation expectation = Expectations.NONE;
 					while ( entries.hasNext() ) {
 	
 						Object entry = entries.next();
 						if ( collection.needsUpdating( entry, i, elementType ) ) {  // will still be issued when it used to be null
+							String sql = getSQLDeleteRowString();
 							if ( st == null ) {
-								String sql = getSQLDeleteRowString();
 								if ( isDeleteCallable() ) {
 									expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 									useBatch = expectation.canBeBatched();
 									st = useBatch
-											? session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, true )
+											? session.getJDBCContext().getConnectionManager().prepareBatchStatement( this, sql, true )
 								            : session.getJDBCContext().getConnectionManager().prepareStatement( sql, true );
 									offset += expectation.prepare( st );
 								}
 								else {
 									st = session.getJDBCContext().getConnectionManager().prepareBatchStatement(
-											getSQLDeleteRowString(),
-											false
+											this, sql, false
 									);
 								}
 							}
 							int loc = writeKey( st, id, offset, session );
 							writeElementToWhere( st, collection.getSnapshotElement(entry, i), loc, session );
 							if ( useBatch ) {
-								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						i++;
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
-						session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+						session.getJDBCContext().getConnectionManager().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						st.close();
 					}
 				}
 			}
 			
 			if ( isRowInsertEnabled() ) {
 				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean callable = isInsertCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLInsertRowString();
 				PreparedStatement st = null;
 				// now update all changed or added rows fks
 				try {
 					int i = 0;
 					Iterator entries = collection.entries( this );
 					while ( entries.hasNext() ) {
 						Object entry = entries.next();
 						int offset = 1;
 						if ( collection.needsUpdating( entry, i, elementType ) ) {
 							if ( useBatch ) {
 								if ( st == null ) {
-									st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+									st = session.getJDBCContext().getConnectionManager().prepareBatchStatement(
+											this, sql, callable
+									);
 								}
 							}
 							else {
 								st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 							}
 
 							offset += expectation.prepare( st );
 
 							int loc = writeKey( st, id, offset, session );
 							if ( hasIndex && !indexContainsFormula ) {
 								loc = writeIndexToWhere( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 
 							writeElementToWhere( st, collection.getElement( entry ), loc, session );
 
 							if ( useBatch ) {
-								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						i++;
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
-						session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+						session.getJDBCContext().getConnectionManager().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						st.close();
 					}
 				}
 			}
 
 			return count;
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not update collection rows: " + 
 					MessageHelper.collectionInfoString( this, id, getFactory() ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	public String selectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String entitySuffix,
 	        String collectionSuffix,
 	        boolean includeCollectionColumns) {
 		StringBuffer buf = new StringBuffer();
 		if ( includeCollectionColumns ) {
 //			buf.append( selectFragment( lhsAlias, "" ) )//ignore suffix for collection columns!
 			buf.append( selectFragment( lhsAlias, collectionSuffix ) )
 					.append( ", " );
 		}
 		OuterJoinLoadable ojl = ( OuterJoinLoadable ) getElementPersister();
 		return buf.append( ojl.selectFragment( lhsAlias, entitySuffix ) )//use suffix for the entity columns
 				.toString();
 	}
 
 	/**
 	 * Create the <tt>OneToManyLoader</tt>
 	 *
 	 * @see org.hibernate.loader.collection.OneToManyLoader
 	 */
 	protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers) 
 			throws MappingException {
 		return BatchingCollectionInitializer.createBatchingOneToManyInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
 	}
 
 	public String fromJoinFragment(String alias,
 								   boolean innerJoin,
 								   boolean includeSubclasses) {
 		return ( ( Joinable ) getElementPersister() ).fromJoinFragment( alias, innerJoin, includeSubclasses );
 	}
 
 	public String whereJoinFragment(String alias,
 									boolean innerJoin,
 									boolean includeSubclasses) {
 		return ( ( Joinable ) getElementPersister() ).whereJoinFragment( alias, innerJoin, includeSubclasses );
 	}
 
 	public String getTableName() {
 		return ( ( Joinable ) getElementPersister() ).getTableName();
 	}
 
 	public String filterFragment(String alias) throws MappingException {
 		String result = super.filterFragment( alias );
 		if ( getElementPersister() instanceof Joinable ) {
 			result += ( ( Joinable ) getElementPersister() ).oneToManyFilterFragment( alias );
 		}
 		return result;
 
 	}
 
 	protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session) {
 		return new SubselectOneToManyLoader( 
 				this,
 				subselect.toSubselectString( getCollectionType().getLHSPropertyName() ),
 				subselect.getResult(),
 				subselect.getQueryParameters(),
 				subselect.getNamedParameterLocMap(),
 				session.getFactory(),
 				session.getLoadQueryInfluencers()
 			);
 	}
 
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		return new CollectionElementLoader( this, getFactory(), session.getLoadQueryInfluencers() )
 				.loadElement( session, key, incrementIndexByBase(index) );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 528f412472..ba5e71e7bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1384,2278 +1384,2278 @@ public abstract class AbstractEntityPersister
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return ( LockingStrategy ) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockMode lockMode,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 	
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockOptions lockOptions,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root(propertyPath);
 		Type type = propertyMapping.toType(rootPropertyName);
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = ( AssociationType ) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
 		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	protected String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuffer buf = new StringBuffer().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 	
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}	
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
 	protected String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 	
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
 	protected String[] getSubclassPropertySubclassNameClosure() {
 		return subclassPropertySubclassNameClosure;
 	}
 
 	protected String[] getSubclassColumnClosure() {
 		return subclassColumnClosure;
 	}
 
 	protected String[] getSubclassColumnAliasClosure() {
 		return subclassColumnAliasClosure;
 	}
 
 	public String[] getSubclassColumnReaderTemplateClosure() {
 		return subclassColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaClosure() {
 		return subclassFormulaClosure;
 	}
 
 	protected String[] getSubclassFormulaTemplateClosure() {
 		return subclassFormulaTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaAliasClosure() {
 		return subclassFormulaAliasClosure;
 	}
 
 	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = ( String[] ) subclassPropertyAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	public String[] getSubclassPropertyColumnNames(String propertyName) {
 		//TODO: should we allow suffixes on these ?
 		return ( String[] ) subclassPropertyColumnNames.get( propertyName );
 	}
 
 
 
 	//This is really ugly, but necessary:
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
 
 		// ALIASES
 		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = ( Property ) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
 			if ( prop.isComposite() ) {
 				Component component = ( Component ) prop.getValue();
 				Iterator compProps = component.getPropertyIterator();
 				internalInitSubclassPropertyAliasesMap( propname, compProps );
 			}
 			else {
 				String[] aliases = new String[prop.getColumnSpan()];
 				String[] cols = new String[prop.getColumnSpan()];
 				Iterator colIter = prop.getColumnIterator();
 				int l = 0;
 				while ( colIter.hasNext() ) {
 					Selectable thing = ( Selectable ) colIter.next();
 					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
 					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
 					l++;
 				}
 
 				subclassPropertyAliases.put( propname, aliases );
 				subclassPropertyColumnNames.put( propname, cols );
 			}
 		}
 
 	}
 
 	public Object loadByUniqueKey(
 			String propertyName,
 			Object uniqueKey,
 			SessionImplementor session) throws HibernateException {
 		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
 	}
 
 	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
 		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
 				&& propertyName.indexOf('.')<0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
 
 		if ( useStaticLoader ) {
 			return ( EntityLoader ) uniqueKeyLoaders.get( propertyName );
 		}
 		else {
 			return createUniqueKeyLoader(
 					propertyMapping.toType( propertyName ),
 					propertyMapping.toColumns( propertyName ),
 					session.getLoadQueryInfluencers()
 			);
 		}
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		return entityMetamodel.getPropertyIndex(propertyName);
 	}
 
 	protected void createUniqueKeyLoaders() throws MappingException {
 		Type[] propertyTypes = getPropertyTypes();
 		String[] propertyNames = getPropertyNames();
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( propertyUniqueness[i] ) {
 				//don't need filters for the static loaders
 				uniqueKeyLoaders.put(
 						propertyNames[i],
 						createUniqueKeyLoader(
 								propertyTypes[i],
 								getPropertyColumnNames( i ),
 								LoadQueryInfluencers.NONE
 						)
 				);
 				//TODO: create uk loaders for component properties
 			}
 		}
 	}
 
 	private EntityLoader createUniqueKeyLoader(
 			Type uniqueKeyType,
 			String[] columns,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		if ( uniqueKeyType.isEntityType() ) {
 			String className = ( ( EntityType ) uniqueKeyType ).getAssociatedEntityName();
 			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
 		}
 		return new EntityLoader(
 				this,
 				columns,
 				uniqueKeyType,
 				1,
 				LockMode.NONE,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	protected boolean hasWhere() {
 		return sqlWhereString != null;
 	}
 
 	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
 		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
 			propertyMapping.initPropertyPaths( getSubclassPropertyNameClosure()[i],
 					getSubclassPropertyTypeClosure()[i],
 					getSubclassPropertyColumnNameClosure()[i],
 					getSubclassPropertyColumnReaderClosure()[i],
 					getSubclassPropertyColumnReaderTemplateClosure()[i],
 					getSubclassPropertyFormulaTemplateClosure()[i],
 					mapping );
 		}
 	}
 
 	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
 		String idProp = getIdentifierPropertyName();
 		if ( idProp != null ) {
 			propertyMapping.initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			propertyMapping.initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			propertyMapping.initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths( ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[]{getDiscriminatorColumnName()},
 				new String[]{getDiscriminatorColumnReaders()},
 				new String[]{getDiscriminatorColumnReaderTemplate()},
 				new String[]{getDiscriminatorFormulaTemplate()},
 				getFactory() );
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths(mapping);
 		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths(mapping);
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 				lockMode,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 			lockOptions,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch( StaleStateException e ) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch( TooManyRowsAffectedException e ) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch ( Throwable t ) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(final boolean[] includeProperty,
 										  final int j,
 										  final Object[] oldFields,
 										  final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns( getPropertyColumnNames(i), propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
 					getPropertyUpdateability() : //optimistic-lock="all", include all updatable properties
 					includeProperty; //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k=0; k<propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
         return includeProperty[ getVersionProperty() ] ||
 				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
 			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1 );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final boolean[][] includeColumns,
 	        final int j,
 	        final PreparedStatement ps,
 	        final SessionImplementor session,
 	        int index) throws SQLException, HibernateException {
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Dehydrating entity: " + MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				//index += getPropertyColumnSpan( i );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			index += 1;
 		}
 		else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			index += getIdentifierColumnSpan();
 		}
 
 		return index;
 
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 	        final Serializable id,
 	        final Object object,
 	        final Loadable rootLoadable,
 	        final String[][] suffixedPropertyColumns,
 	        final boolean allProperties,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Hydrating entity: " + MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = sequentialSelect.executeQuery();
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = BackrefPropertyAccessor.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				sequentialResultSet.close();
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				sequentialSelect.close();
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
 		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSettings().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException("no sequential selects");
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 	        final boolean[] notNull,
 	        String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Inserting entity: " + getEntityName() + " (native id)" );
 			if ( isVersioned() ) {
 				log.trace( "Version: " + Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session );
 			}
 			public Object getEntity() {
 				return object;
 			}
 		};
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
 		return getFactory().getDialect().getIdentitySelectString(
 				getTableName(0),
 				getKeyColumns(0)[0],
 				getIdentifierType().sqlTypes( getFactory() )[0]
 		);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 			.setTableName( getTableName(0) )
 			.addColumns( getKeyColumns(0) )
 			.addCondition( getPropertyColumnNames(propertyName), "=?" )
 			.toStatementString();
 	}
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 	        final Object[] fields,
 	        final boolean[] notNull,
 	        final int j,
 	        final String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Inserting entity: " + MessageHelper.infoString( this, id, getFactory() ) );
 			if ( j == 0 && isVersioned() ) {
 				log.trace( "Version: " + Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		boolean callable = isInsertCallable( j );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		try {
 
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
-				insert = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+				insert = session.getJDBCContext().getConnectionManager().prepareBatchStatement( this, sql, callable );
 			}
 			else {
 				insert = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index );
 
 				if ( useBatch ) {
 					// TODO : shouldnt inserts be Expectations.NONE?
-					session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+					session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 				}
 				else {
 					expectation.verifyOutcome( insert.executeUpdate(), insert, -1 );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
-					session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+					session.getJDBCContext().getConnectionManager().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					insert.close();
 				}
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 				);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	protected boolean update(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean callable = isUpdateCallable( j );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Updating entity: " + MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion ) {
 				log.trace( "Existing version: " + oldVersion + " -> New version: " + fields[getVersionProperty()] );
 			}
 		}
 
 		try {
 
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
-				update = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+				update = session.getJDBCContext().getConnectionManager().prepareBatchStatement( this, sql, callable );
 			}
 			else {
 				update = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index );
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && Versioning.OPTIMISTIC_LOCK_VERSION == entityMetamodel.getOptimisticLockMode() ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
 							getPropertyUpdateability() : includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
-					session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+					session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 					return true;
 				}
 				else {
 					return check( update.executeUpdate(), id, j, expectation, update );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
-					session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+					session.getJDBCContext().getConnectionManager().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					update.close();
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Deleting entity: " + MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion ) {
 				log.trace( "Version: " + version );
 			}
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( log.isTraceEnabled() ) {
 				log.trace( "delete handled by foreign key constraint: " + getTableName( j ) );
 			}
 			return; //EARLY EXIT!
 		}
 
 		try {
 
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
-				delete = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
+				delete = session.getJDBCContext().getConnectionManager().prepareBatchStatement( this, sql, callable );
 			}
 			else {
 				delete = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
-					session.getJDBCContext().getConnectionManager().addToBatch( expectation );
+					session.getJDBCContext().getConnectionManager().addToBatch( this, sql, expectation );
 				}
 				else {
 					check( delete.executeUpdate(), id, j, expectation, delete );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
-					session.getJDBCContext().getConnectionManager().abortBatch( sqle );
+					session.getJDBCContext().getConnectionManager().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					delete.close();
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object, session.getEntityMode() )
 				);
 			propsToUpdate = getPropertyUpdateability( object, session.getEntityMode() );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION;
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			EntityKey key = new EntityKey( id, this, session.getEntityMode() );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "Static SQL for entity: " + getEntityName() );
 			if ( sqlLazySelectString != null ) {
 				log.debug( " Lazy select: " + sqlLazySelectString );
 			}
 			if ( sqlVersionSelectString != null ) {
 				log.debug( " Version select: " + sqlVersionSelectString );
 			}
 			if ( sqlSnapshotSelectString != null ) {
 				log.debug( " Snapshot select: " + sqlSnapshotSelectString );
 			}
 			for ( int j = 0; j < getTableSpan(); j++ ) {
 				log.debug( " Insert " + j + ": " + getSQLInsertStrings()[j] );
 				log.debug( " Update " + j + ": " + getSQLUpdateStrings()[j] );
 				log.debug( " Delete " + j + ": " + getSQLDeleteStrings()[j] );
 
 			}
 			if ( sqlIdentityInsertString != null ) {
 				log.debug( " Identity insert: " + sqlIdentityInsertString );
 			}
 			if ( sqlUpdateByRowIdString != null ) {
 				log.debug( " Update by row id (all fields): " + sqlUpdateByRowIdString );
 			}
 			if ( sqlLazyUpdateByRowIdString != null ) {
 				log.debug( " Update by row id (non-lazy fields): " + sqlLazyUpdateByRowIdString );
 			}
 			if ( sqlInsertGeneratedValuesSelectString != null ) {
 				log.debug( "Insert-generated property select: " + sqlInsertGeneratedValuesSelectString );
 			}
 			if ( sqlUpdateGeneratedValuesSelectString != null ) {
 				log.debug( "Update-generated property select: " + sqlUpdateGeneratedValuesSelectString );
 			}
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuffer sessionFilterFragment = new StringBuffer();
 		filterHelper.render( sessionFilterFragment, generateFilterConditionAlias( alias ), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinFragment.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinFragment.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinFragment.LEFT_OUTER_JOIN :
 					JoinFragment.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 
 	}
 
 	public void postInstantiate() throws MappingException {
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 	}
 
 	private void createLoaders() {
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 	
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingAction.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingAction.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( log.isTraceEnabled() ) {
 			log.trace(
 					"Fetching entity: " +
 					MessageHelper.infoString( this, id, getFactory() )
 				);
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restirctions) these need to be next in precendence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) loaders.get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) loaders.get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is ccurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is ccurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity, EntityMode entityMode) {
 		return hasUninitializedLazyProperties( entity, entityMode ) ?
 				getNonLazyPropertyUpdateability() :
 				getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( log.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
 				log.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	protected EntityTuplizer getTuplizer(SessionImplementor session) {
 		return getTuplizer( session.getEntityMode() );
 	}
 
 	protected EntityTuplizer getTuplizer(EntityMode entityMode) {
 		return entityMetamodel.getTuplizer( entityMode );
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return ( VersionType ) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		//if ( hasLazyProperties() ) {
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = FieldInterceptionHelper.injectFieldInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity, session.getEntityMode() );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( hasCache() ) {
 			CacheKey ck = new CacheKey(
 					id,
 					getIdentifierType(),
 					getRootEntityName(),
 					session.getEntityMode(),
 					session.getFactory()
 				);
 			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType(propertyName);
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
diff --git a/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java b/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java
index 1a207b62f7..4df42c0a47 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java
@@ -1,129 +1,128 @@
 package org.hibernate.test.insertordering;
 
 import java.util.List;
 import java.util.ArrayList;
 import java.util.Iterator;
-import java.sql.SQLException;
 import java.sql.PreparedStatement;
 
 import junit.framework.Test;
 
+import org.hibernate.engine.jdbc.batch.internal.BatchBuilder;
+import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
+import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 import org.hibernate.testing.junit.functional.FunctionalTestCase;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.Session;
-import org.hibernate.HibernateException;
-import org.hibernate.jdbc.BatchingBatcher;
 import org.hibernate.jdbc.Expectation;
-import org.hibernate.jdbc.BatcherFactory;
-import org.hibernate.jdbc.Batcher;
 
 /**
  * {@inheritDoc}
  *
  * @author Steve Ebersole
  */
 public class InsertOrderingTest extends FunctionalTestCase {
 	public InsertOrderingTest(String string) {
 		super( string );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( InsertOrderingTest.class );
 	}
 
 	public String[] getMappings() {
 		return new String[] { "insertordering/Mapping.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.ORDER_INSERTS, "true" );
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "10" );
-		cfg.setProperty( Environment.BATCH_STRATEGY, StatsBatcherFactory.class.getName() );
+		cfg.setProperty( Environment.BATCH_STRATEGY, StatsBatchBuilder.class.getName() );
 	}
 
 	public void testBatchOrdering() {
 		Session s = openSession();
 		s.beginTransaction();
 		int iterations = 12;
 		for ( int i = 0; i < iterations; i++ ) {
 			User user = new User( "user-" + i );
 			Group group = new Group( "group-" + i );
 			s.save( user );
 			s.save( group );
 			user.addMembership( group );
 		}
-		StatsBatcher.reset();
+		StatsBatch.reset();
 		s.getTransaction().commit();
 		s.close();
 
-		assertEquals( 3, StatsBatcher.batchSizes.size() );
+		assertEquals( 3, StatsBatch.batchSizes.size() );
 
 		s = openSession();
 		s.beginTransaction();
 		Iterator users = s.createQuery( "from User u left join fetch u.memberships m left join fetch m.group" ).list().iterator();
 		while ( users.hasNext() ) {
 			s.delete( users.next() );
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public static class Counter {
 		public int count = 0;
 	}
 
-	public static class StatsBatcher extends BatchingBatcher {
+	public static class StatsBatch extends BatchingBatch {
 		private static String batchSQL;
 		private static List batchSizes = new ArrayList();
 		private static int currentBatch = -1;
 
-		public StatsBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
-			super( exceptionHelper, jdbcBatchSize );
+		public StatsBatch(Object key, SQLStatementLogger statementLogger, SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
+			super( key, statementLogger, exceptionHelper, jdbcBatchSize );
 		}
 
 		static void reset() {
 			batchSizes = new ArrayList();
 			currentBatch = -1;
 			batchSQL = null;
 		}
 
-		public void setStatement(String sql, PreparedStatement ps) {
+		public void addBatchStatement(Object key, String sql, PreparedStatement ps) {
 			if ( batchSQL == null || ! batchSQL.equals( sql ) ) {
 				currentBatch++;
 				batchSQL = sql;
 				batchSizes.add( currentBatch, new Counter() );
 				System.out.println( "--------------------------------------------------------" );
 				System.out.println( "Preparing statement [" + sql + "]" );
 			}
-			super.setStatement( sql, ps );
+			super.addBatchStatement( key, sql, ps );
 		}
 
-		public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
+		public void addToBatch(Object key, String sql, Expectation expectation) {
 			Counter counter = ( Counter ) batchSizes.get( currentBatch );
 			counter.count++;
 			System.out.println( "Adding to batch [" + batchSQL + "]" );
-			super.addToBatch( expectation );
+			super.addToBatch( key, sql, expectation );
 		}
 
-		protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
+		protected void doExecuteBatch() {
 			System.out.println( "executing batch [" + batchSQL + "]" );
 			System.out.println( "--------------------------------------------------------" );
-			super.doExecuteBatch( ps );
+			super.doExecuteBatch();
 		}
 	}
 
-	public static class StatsBatcherFactory implements BatcherFactory {
+	public static class StatsBatchBuilder extends BatchBuilder {
 		private int jdbcBatchSize;
 
 		public void setJdbcBatchSize(int jdbcBatchSize) {
 			this.jdbcBatchSize = jdbcBatchSize;
 		}
-		public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
-			return new StatsBatcher( exceptionHelper, jdbcBatchSize );
+		public Batch buildBatch(Object key, SQLStatementLogger statementLogger, SQLExceptionHelper exceptionHelper) {
+			return new StatsBatch(key, statementLogger, exceptionHelper, jdbcBatchSize );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
index 2a893a8fd2..e32122b4ed 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
@@ -1,282 +1,278 @@
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
-import org.hibernate.jdbc.NonBatchingBatcherFactory;
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
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_STATEMENT,
 				services,
-				null,
-				new NonBatchingBatcherFactory()
+				null
 		);
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
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_STATEMENT,
 				services,
-				null,
-				new NonBatchingBatcherFactory()
+				null
 		);
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
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_STATEMENT,
 				services,
-				null,
-				new NonBatchingBatcherFactory()
+				null
 		);
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
index 7de8b8f8ef..8107c1ea0b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
@@ -1,152 +1,148 @@
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
 import java.sql.DatabaseMetaData;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
-import org.hibernate.jdbc.NonBatchingBatcherFactory;
 import org.hibernate.test.common.BasicTestingJdbcServiceImpl;
 import org.hibernate.testing.junit.UnitTestCase;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class BasicConnectionProxyTest extends UnitTestCase {
 	private BasicTestingJdbcServiceImpl services = new BasicTestingJdbcServiceImpl();
 
 	public BasicConnectionProxyTest(String string) {
 		super( string );
 	}
 
 	public void setUp() {
 		services.prepare( false );
 	}
 
 	public void tearDown() {
 		services.release();
 	}
 
 	public void testDatabaseMetaDataHandling() throws Throwable {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_TRANSACTION,
 				services,
-				null,
-				new NonBatchingBatcherFactory()
+				null
 		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		try {
 			DatabaseMetaData metaData = proxiedConnection.getMetaData();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ResultSet rs1 = metaData.getCatalogs();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			rs1.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			metaData.getCatalogs();
 			metaData.getSchemas();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		}
 	}
 
 	public void testExceptionHandling() {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_TRANSACTION,
 				services,
-				null,
-				new NonBatchingBatcherFactory()
+				null
 		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		try {
 			proxiedConnection.prepareStatement( "select count(*) from NON_EXISTENT" ).executeQuery();
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		catch ( JDBCException ok ) {
 			// expected outcome
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 	public void testBasicJdbcUsage() throws JDBCException {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_TRANSACTION,
 				services,
-				null,
-				new NonBatchingBatcherFactory()
+				null
 		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 
 		try {
 			Statement stmnt = proxiedConnection.createStatement();
 			stmnt.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			stmnt.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			stmnt.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 
 			ps = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/manytomany/batchload/BatchedManyToManyTest.java b/hibernate-core/src/test/java/org/hibernate/test/manytomany/batchload/BatchedManyToManyTest.java
index 8c71891ac3..ca66ad9aa1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/manytomany/batchload/BatchedManyToManyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/manytomany/batchload/BatchedManyToManyTest.java
@@ -1,180 +1,181 @@
 /*
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
  */
 package org.hibernate.test.manytomany.batchload;
 
 import java.util.List;
 
 import junit.framework.Test;
 import junit.framework.Assert;
 
+import org.hibernate.engine.jdbc.batch.internal.BatchBuilder;
+import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
+import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
 import org.hibernate.testing.junit.functional.FunctionalTestCase;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.Session;
 import org.hibernate.Hibernate;
 import org.hibernate.Interceptor;
 import org.hibernate.EmptyInterceptor;
-import org.hibernate.jdbc.BatcherFactory;
-import org.hibernate.jdbc.NonBatchingBatcher;
-import org.hibernate.jdbc.Batcher;
 import org.hibernate.stat.CollectionStatistics;
 import org.hibernate.loader.collection.BatchingCollectionInitializer;
 import org.hibernate.persister.collection.AbstractCollectionPersister;
 
 /**
  * Tests loading of many-to-many collection which should trigger
  * a batch load.
  *
  * @author Steve Ebersole
  */
 public class BatchedManyToManyTest extends FunctionalTestCase {
 	public BatchedManyToManyTest(String string) {
 		super( string );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( BatchedManyToManyTest.class );
 	}
 
 	public String[] getMappings() {
 		return new String[] { "manytomany/batchload/UserGroupBatchLoad.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
-		cfg.setProperty( Environment.BATCH_STRATEGY, TestingBatcherFactory.class.getName() );
+		cfg.setProperty( Environment.BATCH_STRATEGY, TestingBatchBuilder.class.getName() );
 	}
 
-	public static class TestingBatcherFactory implements BatcherFactory {
+	public static class TestingBatchBuilder extends BatchBuilder {
 		private int jdbcBatchSize;
 
 		public void setJdbcBatchSize(int jdbcBatchSize) {
 			this.jdbcBatchSize = jdbcBatchSize;
 		}
-		public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
-			return new TestingBatcher( exceptionHelper, jdbcBatchSize );
+		public Batch buildBatch(Object key, SQLStatementLogger statementLogger, SQLExceptionHelper exceptionHelper) {
+			return new TestingBatch(key, statementLogger, exceptionHelper, jdbcBatchSize );
 		}
 	}
 
-	public static class TestingBatcher extends NonBatchingBatcher {
-		public TestingBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
-			super( exceptionHelper );
+	public static class TestingBatch extends NonBatchingBatch {
+		public TestingBatch(Object key, SQLStatementLogger statementLogger, SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
+			super( key, statementLogger, exceptionHelper );
 		}
 	}
 
 	public void testProperLoaderSetup() {
 		AbstractCollectionPersister cp = ( AbstractCollectionPersister )
 				sfi().getCollectionPersister( User.class.getName() + ".groups" );
 		assertClassAssignability( BatchingCollectionInitializer.class, cp.getInitializer().getClass() );
 		BatchingCollectionInitializer initializer = ( BatchingCollectionInitializer ) cp.getInitializer();
 		assertEquals( 50, findMaxBatchSize( initializer.getBatchSizes() ) );
 	}
 
 	private int findMaxBatchSize(int[] batchSizes) {
 		int max = 0;
 		for ( int size : batchSizes ) {
 			if ( size > max ) {
 				max = size;
 			}
 		}
 		return max;
 	}
 
 	public void testLoadingNonInverseSide() {
 		prepareTestData();
 
 		sfi().getStatistics().clear();
 		CollectionStatistics userGroupStats = sfi().getStatistics()
 				.getCollectionStatistics( User.class.getName() + ".groups" );
 		CollectionStatistics groupUserStats = sfi().getStatistics()
 				.getCollectionStatistics( Group.class.getName() + ".users" );
 
 		Interceptor testingInterceptor = new EmptyInterceptor() {
 			public String onPrepareStatement(String sql) {
 				// ugh, this is the best way I could come up with to assert this.
 				// unfortunately, this is highly dependent on the dialect and its
 				// outer join fragment.  But at least this wil fail on the majority
 				// of dialects...
 				Assert.assertFalse(
 						"batch load of many-to-many should use inner join",
 						sql.toLowerCase().contains( "left outer join" )
 				);
 				return super.onPrepareStatement( sql );
 			}
 		};
 
 		Session s = openSession( testingInterceptor );
 		s.beginTransaction();
 		List users = s.createQuery( "from User u" ).list();
 		User user = ( User ) users.get( 0 );
 		assertTrue( Hibernate.isInitialized( user ) );
 		assertTrue( Hibernate.isInitialized( user.getGroups() ) );
 		user = ( User ) users.get( 1 );
 		assertTrue( Hibernate.isInitialized( user ) );
 		assertTrue( Hibernate.isInitialized( user.getGroups() ) );
 		assertEquals( 1, userGroupStats.getFetchCount() ); // should have been just one fetch (the batch fetch)
 		assertEquals( 1, groupUserStats.getFetchCount() ); // should have been just one fetch (the batch fetch)
 		s.getTransaction().commit();
 		s.close();
 
 		cleanupTestData();
 	}
 
 	protected void prepareTestData() {
 		// set up the test data
 		User me = new User( "steve" );
 		User you = new User( "not steve" );
 		Group developers = new Group( "developers" );
 		Group translators = new Group( "translators" );
 		Group contributors = new Group( "contributors" );
 		me.getGroups().add( developers );
 		developers.getUsers().add( me );
 		you.getGroups().add( translators );
 		translators.getUsers().add( you );
 		you.getGroups().add( contributors );
 		contributors.getUsers().add( you );
 		Session s = openSession();
 		s.beginTransaction();
 		s.save( me );
 		s.save( you );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected void cleanupTestData() {
 		// clean up the test data
 		Session s = openSession();
 		s.beginTransaction();
 		// User is the non-inverse side...
 		List<User> users = s.createQuery( "from User" ).list();
 		for ( User user : users ) {
 			s.delete( user );
 		}
 		s.flush();
 		s.createQuery( "delete Group" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 }
diff --git a/hibernate-core/src/test/resources/log4j.properties b/hibernate-core/src/test/resources/log4j.properties
index a55e3d85fb..f8e9147f11 100644
--- a/hibernate-core/src/test/resources/log4j.properties
+++ b/hibernate-core/src/test/resources/log4j.properties
@@ -1,17 +1,18 @@
 log4j.appender.stdout=org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target=System.out
 log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
 
 
 log4j.rootLogger=info, stdout
 
 log4j.logger.org.hibernate.test=info
 log4j.logger.org.hibernate.tool.hbm2ddl=debug
+log4j.logger.org.hibernate.engine.jdbc.internal=trace
 log4j.logger.org.hibernate.engine.jdbc=trace
 log4j.logger.org.hibernate.hql.ast.QueryTranslatorImpl=trace
 log4j.logger.org.hibernate.hql.ast.HqlSqlWalker=trace
 log4j.logger.org.hibernate.hql.ast.SqlGenerator=trace
 log4j.logger.org.hibernate.hql.ast.AST=trace
 log4j.logger.org.hibernate.type.descriptor.sql.BasicBinder=trace
-log4j.logger.org.hibernate.type.BasicTypeRegistry=trace
\ No newline at end of file
+log4j.logger.org.hibernate.type.BasicTypeRegistry=trace
