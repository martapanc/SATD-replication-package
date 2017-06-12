diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 1933042ce2..68959ed419 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,499 +1,509 @@
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
+import org.hibernate.engine.jdbc.batch.internal.BatchBuilder;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.jdbc.BatcherFactory;
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
 	private BatcherFactory batcherFactory;
+	private BatchBuilder batchBuilder;
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
 
 	public BatcherFactory getBatcherFactory() {
 		return batcherFactory;
 	}
 
+	public BatchBuilder getBatchBuilder() {
+		return batchBuilder;
+	}
+
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
 
 	void setBatcherFactory(BatcherFactory batcher) {
 		this.batcherFactory = batcher;
 	}
 
+	void setBatcherBuilder(BatchBuilder batchBuilder) {
+		this.batchBuilder = batchBuilder;
+	}
+
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
index b2c50d415c..9a6c1ecf60 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,377 +1,404 @@
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
+import org.hibernate.engine.jdbc.batch.internal.BatchBuilder;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.jdbc.BatchingBatcherFactory;
 import org.hibernate.jdbc.NonBatchingBatcherFactory;
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
 		settings.setBatcherFactory( createBatcherFactory(properties, batchSize) );
+		settings.setBatcherBuilder( createBatchBuilder(properties, batchSize) );
 
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
 
 	protected BatcherFactory createBatcherFactory(Properties properties, int batchSize) {
 		String batcherClass = properties.getProperty(Environment.BATCH_STRATEGY);
+		BatcherFactory batcherFactory = null;
 		if (batcherClass==null) {
-			return batchSize == 0
+			batcherFactory = batchSize == 0
 					? new NonBatchingBatcherFactory()
-					: new BatchingBatcherFactory();
+					: new BatchingBatcherFactory( );
 		}
 		else {
 			log.info("Batcher factory: " + batcherClass);
 			try {
-				return (BatcherFactory) ReflectHelper.classForName(batcherClass).newInstance();
+				batcherFactory = (BatcherFactory) ReflectHelper.classForName(batcherClass).newInstance();
 			}
 			catch (Exception cnfe) {
 				throw new HibernateException("could not instantiate BatcherFactory: " + batcherClass, cnfe);
 			}
 		}
+		batcherFactory.setJdbcBatchSize( batchSize );
+		return batcherFactory;
+	}
+
+	protected BatchBuilder createBatchBuilder(Properties properties, int batchSize) {
+		//FIXME: uncomment to use BatchBuilder
+		/*
+		String batchBuilderClass = properties.getProperty(Environment.BATCH_STRATEGY);
+		if (batchBuilderClass==null) {
+			return batchSize > 0
+					? new BatchBuilder( batchSize )
+					: new BatchBuilder();
+		}
+		else {
+			log.info("Batcher factory: " + batchBuilderClass);
+			try {
+				return (BatchBuilder) ReflectHelper.classForName(batchBuilderClass).newInstance();
+			}
+			catch (Exception cnfe) {
+				throw new HibernateException("could not instantiate BatchBuilder: " + batchBuilderClass, cnfe);
+			}
+		}
+		*/
+		return null;
 	}
 
 	protected TransactionFactory createTransactionFactory(Properties properties) {
 		return TransactionFactoryFactory.buildTransactionFactory(properties);
 	}
 
 	protected TransactionManagerLookup createTransactionManagerLookup(Properties properties) {
 		return TransactionManagerLookupFactory.getTransactionManagerLookup(properties);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java
index 594c8e8679..3e9e3f88ac 100644
--- a/hibernate-core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/ThreadLocalSessionContext.java
@@ -1,387 +1,387 @@
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
 package org.hibernate.context;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.util.HashMap;
 import java.util.Map;
 import javax.transaction.Synchronization;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.classic.Session;
 import org.hibernate.engine.SessionFactoryImplementor;
 
 /**
  * A {@link CurrentSessionContext} impl which scopes the notion of current
  * session by the current thread of execution.  Unlike the JTA counterpart,
  * threads do not give us a nice hook to perform any type of cleanup making
  * it questionable for this impl to actually generate Session instances.  In
  * the interest of usability, it was decided to have this default impl
  * actually generate a session upon first request and then clean it up
  * after the {@link org.hibernate.Transaction} associated with that session
  * is committed/rolled-back.  In order for ensuring that happens, the sessions
  * generated here are unusable until after {@link Session#beginTransaction()}
  * has been called. If <tt>close()</tt> is called on a session managed by
  * this class, it will be automatically unbound.
  * <p/>
  * Additionally, the static {@link #bind} and {@link #unbind} methods are
  * provided to allow application code to explicitly control opening and
  * closing of these sessions.  This, with some from of interception,
  * is the preferred approach.  It also allows easy framework integration
  * and one possible approach for implementing long-sessions.
  * <p/>
  * The {@link #buildOrObtainSession}, {@link #isAutoCloseEnabled},
  * {@link #isAutoFlushEnabled}, {@link #getConnectionReleaseMode}, and
  * {@link #buildCleanupSynch} methods are all provided to allow easy
  * subclassing (for long-running session scenarios, for example).
  *
  * @author Steve Ebersole
  */
 public class ThreadLocalSessionContext implements CurrentSessionContext {
 
 	private static final Logger log = LoggerFactory.getLogger( ThreadLocalSessionContext.class );
 	private static final Class[] SESS_PROXY_INTERFACES = new Class[] {
 			org.hibernate.classic.Session.class,
 	        org.hibernate.engine.SessionImplementor.class,
-	        org.hibernate.jdbc.JDBCContext.Context.class,
+	        org.hibernate.engine.jdbc.spi.JDBCContext.Context.class,
 	        org.hibernate.event.EventSource.class
 	};
 
 	/**
 	 * A ThreadLocal maintaining current sessions for the given execution thread.
 	 * The actual ThreadLocal variable is a java.util.Map to account for
 	 * the possibility for multiple SessionFactorys being used during execution
 	 * of the given thread.
 	 */
 	private static final ThreadLocal context = new ThreadLocal();
 
 	protected final SessionFactoryImplementor factory;
 
 	public ThreadLocalSessionContext(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final Session currentSession() throws HibernateException {
 		Session current = existingSession( factory );
 		if (current == null) {
 			current = buildOrObtainSession();
 			// register a cleanup synch
 			current.getTransaction().registerSynchronization( buildCleanupSynch() );
 			// wrap the session in the transaction-protection proxy
 			if ( needsWrapping( current ) ) {
 				current = wrap( current );
 			}
 			// then bind it
 			doBind( current, factory );
 		}
 		return current;
 	}
 
 	private boolean needsWrapping(Session session) {
 		// try to make sure we don't wrap and already wrapped session
 		return session != null
 		       && ! Proxy.isProxyClass( session.getClass() )
 		       || ( Proxy.getInvocationHandler( session ) != null
 		       && ! ( Proxy.getInvocationHandler( session ) instanceof TransactionProtectionWrapper ) );
 	}
 
 	/**
 	 * Getter for property 'factory'.
 	 *
 	 * @return Value for property 'factory'.
 	 */
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	/**
 	 * Strictly provided for subclassing purposes; specifically to allow long-session
 	 * support.
 	 * <p/>
 	 * This implementation always just opens a new session.
 	 *
 	 * @return the built or (re)obtained session.
 	 */
 	protected Session buildOrObtainSession() {
 		return factory.openSession(
 				null,
 		        isAutoFlushEnabled(),
 		        isAutoCloseEnabled(),
 		        getConnectionReleaseMode()
 			);
 	}
 
 	protected CleanupSynch buildCleanupSynch() {
 		return new CleanupSynch( factory );
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns true.
 	 *
 	 * @return Whether or not the the session should be closed by transaction completion.
 	 */
 	protected boolean isAutoCloseEnabled() {
 		return true;
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns true.
 	 *
 	 * @return Whether or not the the session should be flushed prior transaction completion.
 	 */
 	protected boolean isAutoFlushEnabled() {
 		return true;
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns after_transaction.
 	 *
 	 * @return The connection release mode for any built sessions.
 	 */
 	protected ConnectionReleaseMode getConnectionReleaseMode() {
 		return factory.getSettings().getConnectionReleaseMode();
 	}
 
 	protected Session wrap(Session session) {
 		TransactionProtectionWrapper wrapper = new TransactionProtectionWrapper( session );
 		Session wrapped = ( Session ) Proxy.newProxyInstance(
 				Session.class.getClassLoader(),
 		        SESS_PROXY_INTERFACES,
 		        wrapper
 			);
 		// yick!  need this for proper serialization/deserialization handling...
 		wrapper.setWrapped( wrapped );
 		return wrapped;
 	}
 
 	/**
 	 * Associates the given session with the current thread of execution.
 	 *
 	 * @param session The session to bind.
 	 */
 	public static void bind(org.hibernate.Session session) {
 		SessionFactory factory = session.getSessionFactory();
 		cleanupAnyOrphanedSession( factory );
 		doBind( session, factory );
 	}
 
 	private static void cleanupAnyOrphanedSession(SessionFactory factory) {
 		Session orphan = doUnbind( factory, false );
 		if ( orphan != null ) {
 			log.warn( "Already session bound on call to bind(); make sure you clean up your sessions!" );
 			try {
 				if ( orphan.getTransaction() != null && orphan.getTransaction().isActive() ) {
 					try {
 						orphan.getTransaction().rollback();
 					}
 					catch( Throwable t ) {
 						log.debug( "Unable to rollback transaction for orphaned session", t );
 					}
 				}
 				orphan.close();
 			}
 			catch( Throwable t ) {
 				log.debug( "Unable to close orphaned session", t );
 			}
 		}
 	}
 
 	/**
 	 * Unassociate a previously bound session from the current thread of execution.
 	 *
 	 * @return The session which was unbound.
 	 */
 	public static Session unbind(SessionFactory factory) {
 		return doUnbind( factory, true );
 	}
 
 	private static Session existingSession(SessionFactory factory) {
 		Map sessionMap = sessionMap();
 		if ( sessionMap == null ) {
 			return null;
 		}
 		else {
 			return ( Session ) sessionMap.get( factory );
 		}
 	}
 
 	protected static Map sessionMap() {
 		return ( Map ) context.get();
 	}
 
 	private static void doBind(org.hibernate.Session session, SessionFactory factory) {
 		Map sessionMap = sessionMap();
 		if ( sessionMap == null ) {
 			sessionMap = new HashMap();
 			context.set( sessionMap );
 		}
 		sessionMap.put( factory, session );
 	}
 
 	private static Session doUnbind(SessionFactory factory, boolean releaseMapIfEmpty) {
 		Map sessionMap = sessionMap();
 		Session session = null;
 		if ( sessionMap != null ) {
 			session = ( Session ) sessionMap.remove( factory );
 			if ( releaseMapIfEmpty && sessionMap.isEmpty() ) {
 				context.set( null );
 			}
 		}
 		return session;
 	}
 
 	/**
 	 * JTA transaction synch used for cleanup of the internal session map.
 	 */
 	protected static class CleanupSynch implements Synchronization, Serializable {
 		protected final SessionFactory factory;
 
 		public CleanupSynch(SessionFactory factory) {
 			this.factory = factory;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public void beforeCompletion() {
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public void afterCompletion(int i) {
 			unbind( factory );
 		}
 	}
 
 	private class TransactionProtectionWrapper implements InvocationHandler, Serializable {
 		private final Session realSession;
 		private Session wrappedSession;
 
 		public TransactionProtectionWrapper(Session realSession) {
 			this.realSession = realSession;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 			try {
 				// If close() is called, guarantee unbind()
 				if ( "close".equals( method.getName()) ) {
 					unbind( realSession.getSessionFactory() );
 				}
 				else if ( "toString".equals( method.getName() )
 					     || "equals".equals( method.getName() )
 					     || "hashCode".equals( method.getName() )
 				         || "getStatistics".equals( method.getName() )
 					     || "isOpen".equals( method.getName() )
 						 || "getListeners".equals( method.getName() ) //useful for HSearch in particular
 						) {
 					// allow these to go through the the real session no matter what
 				}
 				else if ( !realSession.isOpen() ) {
 					// essentially, if the real session is closed allow any
 					// method call to pass through since the real session
 					// will complain by throwing an appropriate exception;
 					// NOTE that allowing close() above has the same basic effect,
 					//   but we capture that there simply to doAfterTransactionCompletion the unbind...
 				}
 				else if ( !realSession.getTransaction().isActive() ) {
 					// limit the methods available if no transaction is active
 					if ( "beginTransaction".equals( method.getName() )
 					     || "getTransaction".equals( method.getName() )
 					     || "isTransactionInProgress".equals( method.getName() )
 					     || "setFlushMode".equals( method.getName() )
 					     || "getSessionFactory".equals( method.getName() ) ) {
 						log.trace( "allowing method [" + method.getName() + "] in non-transacted context" );
 					}
 					else if ( "reconnect".equals( method.getName() )
 					          || "disconnect".equals( method.getName() ) ) {
 						// allow these (deprecated) methods to pass through
 					}
 					else {
 						throw new HibernateException( method.getName() + " is not valid without active transaction" );
 					}
 				}
 				log.trace( "allowing proxied method [" + method.getName() + "] to proceed to real session" );
 				return method.invoke( realSession, args );
 			}
 			catch ( InvocationTargetException e ) {
 				if ( e.getTargetException() instanceof RuntimeException ) {
 					throw ( RuntimeException ) e.getTargetException();
 				}
 				else {
 					throw e;
 				}
 			}
 		}
 
 		/**
 		 * Setter for property 'wrapped'.
 		 *
 		 * @param wrapped Value to set for property 'wrapped'.
 		 */
 		public void setWrapped(Session wrapped) {
 			this.wrappedSession = wrapped;
 		}
 
 
 		// serialization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		private void writeObject(ObjectOutputStream oos) throws IOException {
 			// if a ThreadLocalSessionContext-bound session happens to get
 			// serialized, to be completely correct, we need to make sure
 			// that unbinding of that session occurs.
 			oos.defaultWriteObject();
 			if ( existingSession( factory ) == wrappedSession ) {
 				unbind( factory );
 			}
 		}
 
 		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 			// on the inverse, it makes sense that if a ThreadLocalSessionContext-
 			// bound session then gets deserialized to go ahead and re-bind it to
 			// the ThreadLocalSessionContext session map.
 			ois.defaultReadObject();
 			realSession.getTransaction().registerSynchronization( buildCleanupSynch() );
 			doBind( wrappedSession, factory );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
index 38886dbb48..12e7b4c575 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadSelectLockingStrategy.java
@@ -1,140 +1,139 @@
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
 package org.hibernate.dialect.lock;
 
 import org.hibernate.LockOptions;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through select statements.
  * <p/>
  * For non-read locks, this is achieved through the Dialect's specific
  * SELECT ... FOR UPDATE syntax.
  *
  * This strategy is valid for LockMode.PESSIMISTIC_READ
  *
  * This class is a clone of SelectLockingStrategy.
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  * @see org.hibernate.dialect.Dialect#getForUpdateString(org.hibernate.LockMode)
  * @see org.hibernate.dialect.Dialect#appendLockHint(org.hibernate.LockMode, String)
  * @since 3.5
  */
 public class PessimisticReadSelectLockingStrategy extends AbstractSelectLockingStrategy {
 	/**
 	 * Construct a locking strategy based on SQL SELECT statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.
 	 */
 	public PessimisticReadSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		super( lockable, lockMode );
 	}
 
 	/**
 	 * @see org.hibernate.dialect.lock.LockingStrategy#lock
 	 */
 	public void lock(
 			Serializable id,
 			Object version,
 			Object object,
 			int timeout,
 			SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		final String sql = determineSql( timeout );
 		SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				getLockable().getIdentifierType().nullSafeSet( st, id, 1, session );
 				if ( getLockable().isVersioned() ) {
 					getLockable().getVersionType().nullSafeSet(
 							st,
 							version,
 							getLockable().getIdentifierType().getColumnSpan( factory ) + 1,
 							session
 					);
 				}
 
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						if ( factory.getStatistics().isStatisticsEnabled() ) {
 							factory.getStatisticsImplementor()
 									.optimisticFailure( getLockable().getEntityName() );
 						}
 						throw new StaleObjectStateException( getLockable().getEntityName(), id );
 					}
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			JDBCException e = session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not lock: " + MessageHelper.infoString( getLockable(), id, session.getFactory() ),
 					sql
 			);
 			throw new PessimisticLockException( "could not obtain pessimistic lock", e, object );
 		}
 	}
 
 	protected String generateLockString(int lockTimeout) {
 		SessionFactoryImplementor factory = getLockable().getFactory();
 		LockOptions lockOptions = new LockOptions( getLockMode() );
 		lockOptions.setTimeOut( lockTimeout );
 		SimpleSelect select = new SimpleSelect( factory.getDialect() )
 				.setLockOptions( lockOptions )
 				.setTableName( getLockable().getRootTableName() )
 				.addColumn( getLockable().getRootTableIdentifierColumnNames()[0] )
 				.addCondition( getLockable().getRootTableIdentifierColumnNames(), "=?" );
 		if ( getLockable().isVersioned() ) {
 			select.addCondition( getLockable().getVersionColumnName(), "=?" );
 		}
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			select.setComment( getLockMode() + " lock " + getLockable().getEntityName() );
 		}
 		return select.toStatementString();
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
index ec4874c26f..9d473ca9b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticReadUpdateLockingStrategy.java
@@ -1,148 +1,147 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 import org.hibernate.LockMode;
 import org.hibernate.HibernateException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through update statements.
  * <p/>
  * This strategy is valid for LockMode.PESSIMISTIC_READ
  *
  * This class is a clone of UpdateLockingStrategy.
  *
  * @since 3.5
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  */
 public class PessimisticReadUpdateLockingStrategy implements LockingStrategy {
 	private static final Logger log = LoggerFactory.getLogger( PessimisticReadUpdateLockingStrategy.class );
 
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String sql;
 
 	/**
 	 * Construct a locking strategy based on SQL UPDATE statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indictates the type of lock to be acquired.  Note that
 	 * read-locks are not valid for this strategy.
 	 */
 	public PessimisticReadUpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.PESSIMISTIC_READ ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for update statement" );
 		}
 		if ( !lockable.isVersioned() ) {
 			log.warn( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 			this.sql = null;
 		}
 		else {
 			this.sql = generateLockString();
 		}
 	}
 
    /**
 	 * @see org.hibernate.dialect.lock.LockingStrategy#lock
 	 */
 	public void lock(
       Serializable id,
       Object version,
       Object object,
       int timeout, SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
 		SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				lockable.getVersionType().nullSafeSet( st, version, 1, session );
 				int offset = 2;
 
 				lockable.getIdentifierType().nullSafeSet( st, id, offset, session );
 				offset += lockable.getIdentifierType().getColumnSpan( factory );
 
 				if ( lockable.isVersioned() ) {
 					lockable.getVersionType().nullSafeSet( st, version, offset, session );
 				}
 
 				int affected = st.executeUpdate();
 				if ( affected < 0 ) {  // todo:  should this instead check for exactly one row modified?
 					factory.getStatisticsImplementor().optimisticFailure( lockable.getEntityName() );
 					throw new StaleObjectStateException( lockable.getEntityName(), id );
 				}
 
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			JDBCException e = session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
 					sql
 				);
 			throw new PessimisticLockException("could not obtain pessimistic lock", e, object);
 		}
 	}
 
 	protected String generateLockString() {
 		SessionFactoryImplementor factory = lockable.getFactory();
 		Update update = new Update( factory.getDialect() );
 		update.setTableName( lockable.getRootTableName() );
 		update.addPrimaryKeyColumns( lockable.getRootTableIdentifierColumnNames() );
 		update.setVersionColumnName( lockable.getVersionColumnName() );
 		update.addColumn( lockable.getVersionColumnName() );
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			update.setComment( lockMode + " lock " + lockable.getEntityName() );
 		}
 		return update.toStatementString();
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
index b81f2f42c6..d7cd8e96c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteSelectLockingStrategy.java
@@ -1,140 +1,139 @@
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
 package org.hibernate.dialect.lock;
 
 import org.hibernate.LockOptions;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.exception.JDBCExceptionHelper;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through select statements.
  * <p/>
  * For non-read locks, this is achieved through the Dialect's specific
  * SELECT ... FOR UPDATE syntax.
  *
  * This strategy is valid for LockMode.PESSIMISTIC_WRITE
  *
  * This class is a clone of SelectLockingStrategy.
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  * @see org.hibernate.dialect.Dialect#getForUpdateString(org.hibernate.LockMode)
  * @see org.hibernate.dialect.Dialect#appendLockHint(org.hibernate.LockMode, String)
  * @since 3.5
  */
 public class PessimisticWriteSelectLockingStrategy extends AbstractSelectLockingStrategy {
 	/**
 	 * Construct a locking strategy based on SQL SELECT statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indicates the type of lock to be acquired.
 	 */
 	public PessimisticWriteSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		super( lockable, lockMode );
 	}
 
 	/**
 	 * @see LockingStrategy#lock
 	 */
 	public void lock(
 			Serializable id,
 			Object version,
 			Object object,
 			int timeout,
 			SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		final String sql = determineSql( timeout );
 		SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				getLockable().getIdentifierType().nullSafeSet( st, id, 1, session );
 				if ( getLockable().isVersioned() ) {
 					getLockable().getVersionType().nullSafeSet(
 							st,
 							version,
 							getLockable().getIdentifierType().getColumnSpan( factory ) + 1,
 							session
 					);
 				}
 
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						if ( factory.getStatistics().isStatisticsEnabled() ) {
 							factory.getStatisticsImplementor()
 									.optimisticFailure( getLockable().getEntityName() );
 						}
 						throw new StaleObjectStateException( getLockable().getEntityName(), id );
 					}
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			JDBCException e = session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not lock: " + MessageHelper.infoString( getLockable(), id, session.getFactory() ),
 					sql
 			);
 			throw new PessimisticLockException( "could not obtain pessimistic lock", e, object );
 		}
 	}
 
 	protected String generateLockString(int lockTimeout) {
 		SessionFactoryImplementor factory = getLockable().getFactory();
 		LockOptions lockOptions = new LockOptions( getLockMode() );
 		lockOptions.setTimeOut( lockTimeout );
 		SimpleSelect select = new SimpleSelect( factory.getDialect() )
 				.setLockOptions( lockOptions )
 				.setTableName( getLockable().getRootTableName() )
 				.addColumn( getLockable().getRootTableIdentifierColumnNames()[0] )
 				.addCondition( getLockable().getRootTableIdentifierColumnNames(), "=?" );
 		if ( getLockable().isVersioned() ) {
 			select.addCondition( getLockable().getVersionColumnName(), "=?" );
 		}
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			select.setComment( getLockMode() + " lock " + getLockable().getEntityName() );
 		}
 		return select.toStatementString();
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
index dfc745e782..47d9af492c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/PessimisticWriteUpdateLockingStrategy.java
@@ -1,148 +1,147 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 import org.hibernate.LockMode;
 import org.hibernate.HibernateException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * A pessimistic locking strategy where the locks are obtained through update statements.
  * <p/>
  * This strategy is valid for LockMode.PESSIMISTIC_WRITE
  *
  * This class is a clone of UpdateLockingStrategy.
  *
  * @since 3.5
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  */
 public class PessimisticWriteUpdateLockingStrategy implements LockingStrategy {
 	private static final Logger log = LoggerFactory.getLogger( PessimisticWriteUpdateLockingStrategy.class );
 
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String sql;
 
 	/**
 	 * Construct a locking strategy based on SQL UPDATE statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indictates the type of lock to be acquired.  Note that
 	 * read-locks are not valid for this strategy.
 	 */
 	public PessimisticWriteUpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.PESSIMISTIC_READ ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for update statement" );
 		}
 		if ( !lockable.isVersioned() ) {
 			log.warn( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 			this.sql = null;
 		}
 		else {
 			this.sql = generateLockString();
 		}
 	}
 
    /**
 	 * @see LockingStrategy#lock
 	 */
 	public void lock(
       Serializable id,
       Object version,
       Object object,
       int timeout, SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
 		SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				lockable.getVersionType().nullSafeSet( st, version, 1, session );
 				int offset = 2;
 
 				lockable.getIdentifierType().nullSafeSet( st, id, offset, session );
 				offset += lockable.getIdentifierType().getColumnSpan( factory );
 
 				if ( lockable.isVersioned() ) {
 					lockable.getVersionType().nullSafeSet( st, version, offset, session );
 				}
 
 				int affected = st.executeUpdate();
 				if ( affected < 0 ) {  // todo:  should this instead check for exactly one row modified?
 					factory.getStatisticsImplementor().optimisticFailure( lockable.getEntityName() );
 					throw new StaleObjectStateException( lockable.getEntityName(), id );
 				}
 
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			JDBCException e = session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
 					sql
 				);
 			throw new PessimisticLockException("could not obtain pessimistic lock", e, object);
 		}
 	}
 
 	protected String generateLockString() {
 		SessionFactoryImplementor factory = lockable.getFactory();
 		Update update = new Update( factory.getDialect() );
 		update.setTableName( lockable.getRootTableName() );
 		update.addPrimaryKeyColumns( lockable.getRootTableIdentifierColumnNames() );
 		update.setVersionColumnName( lockable.getVersionColumnName() );
 		update.addColumn( lockable.getVersionColumnName() );
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			update.setComment( lockMode + " lock " + lockable.getEntityName() );
 		}
 		return update.toStatementString();
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
index 42a45fa1aa..65e563f76b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/SelectLockingStrategy.java
@@ -1,134 +1,133 @@
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
 package org.hibernate.dialect.lock;
 
 import org.hibernate.LockOptions;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.exception.JDBCExceptionHelper;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
  * A locking strategy where the locks are obtained through select statements.
  * <p/>
  * For non-read locks, this is achieved through the Dialect's specific
  * SELECT ... FOR UPDATE syntax.
  *
  * @see org.hibernate.dialect.Dialect#getForUpdateString(org.hibernate.LockMode)
  * @see org.hibernate.dialect.Dialect#appendLockHint(org.hibernate.LockMode, String)
  * @since 3.2
  *
  * @author Steve Ebersole
  */
 public class SelectLockingStrategy extends AbstractSelectLockingStrategy {
 	/**
 	 * Construct a locking strategy based on SQL SELECT statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indictates the type of lock to be acquired.
 	 */
 	public SelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		super( lockable, lockMode );
 	}
 
 	/**
 	 * @see LockingStrategy#lock
 	 */
 	public void lock(
 	        Serializable id,
 	        Object version,
 	        Object object,
 	        int timeout, 
 	        SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		final String sql = determineSql( timeout );
 		SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				getLockable().getIdentifierType().nullSafeSet( st, id, 1, session );
 				if ( getLockable().isVersioned() ) {
 					getLockable().getVersionType().nullSafeSet(
 							st,
 							version,
 							getLockable().getIdentifierType().getColumnSpan( factory ) + 1,
 							session
 					);
 				}
 
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						if ( factory.getStatistics().isStatisticsEnabled() ) {
 							factory.getStatisticsImplementor()
 									.optimisticFailure( getLockable().getEntityName() );
 						}
 						throw new StaleObjectStateException( getLockable().getEntityName(), id );
 					}
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not lock: " + MessageHelper.infoString( getLockable(), id, session.getFactory() ),
 					sql
 				);
 		}
 	}
 
 	protected String generateLockString(int timeout) {
 		SessionFactoryImplementor factory = getLockable().getFactory();
 		LockOptions lockOptions = new LockOptions( getLockMode() );
 		lockOptions.setTimeOut( timeout );
 		SimpleSelect select = new SimpleSelect( factory.getDialect() )
 				.setLockOptions( lockOptions )
 				.setTableName( getLockable().getRootTableName() )
 				.addColumn( getLockable().getRootTableIdentifierColumnNames()[0] )
 				.addCondition( getLockable().getRootTableIdentifierColumnNames(), "=?" );
 		if ( getLockable().isVersioned() ) {
 			select.addCondition( getLockable().getVersionColumnName(), "=?" );
 		}
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			select.setComment( getLockMode() + " lock " + getLockable().getEntityName() );
 		}
 		return select.toStatementString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
index b101ace9b7..a8c7e2b47d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/UpdateLockingStrategy.java
@@ -1,145 +1,144 @@
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
 package org.hibernate.dialect.lock;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * A locking strategy where the locks are obtained through update statements.
  * <p/>
  * This strategy is not valid for read style locks.
  *
  * @since 3.2
  *
  * @author Steve Ebersole
  */
 public class UpdateLockingStrategy implements LockingStrategy {
 	private static final Logger log = LoggerFactory.getLogger( UpdateLockingStrategy.class );
 
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String sql;
 
 	/**
 	 * Construct a locking strategy based on SQL UPDATE statements.
 	 *
 	 * @param lockable The metadata for the entity to be locked.
 	 * @param lockMode Indictates the type of lock to be acquired.  Note that
 	 * read-locks are not valid for this strategy.
 	 */
 	public UpdateLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		if ( lockMode.lessThan( LockMode.UPGRADE ) ) {
 			throw new HibernateException( "[" + lockMode + "] not valid for update statement" );
 		}
 		if ( !lockable.isVersioned() ) {
 			log.warn( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 			this.sql = null;
 		}
 		else {
 			this.sql = generateLockString();
 		}
 	}
 
 	/**
 	 * @see LockingStrategy#lock
 	 */
 	public void lock(
 	        Serializable id,
 	        Object version,
 	        Object object,
 	        int timeout,
 	        SessionImplementor session) throws StaleObjectStateException, JDBCException {
 		if ( !lockable.isVersioned() ) {
 			throw new HibernateException( "write locks via update not supported for non-versioned entities [" + lockable.getEntityName() + "]" );
 		}
 		// todo : should we additionally check the current isolation mode explicitly?
 		SessionFactoryImplementor factory = session.getFactory();
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				lockable.getVersionType().nullSafeSet( st, version, 1, session );
 				int offset = 2;
 
 				lockable.getIdentifierType().nullSafeSet( st, id, offset, session );
 				offset += lockable.getIdentifierType().getColumnSpan( factory );
 
 				if ( lockable.isVersioned() ) {
 					lockable.getVersionType().nullSafeSet( st, version, offset, session );
 				}
 
 				int affected = st.executeUpdate();
 				if ( affected < 0 ) {
 					factory.getStatisticsImplementor().optimisticFailure( lockable.getEntityName() );
 					throw new StaleObjectStateException( lockable.getEntityName(), id );
 				}
 
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not lock: " + MessageHelper.infoString( lockable, id, session.getFactory() ),
 			        sql
 			);
 		}
 	}
 
 	protected String generateLockString() {
 		SessionFactoryImplementor factory = lockable.getFactory();
 		Update update = new Update( factory.getDialect() );
 		update.setTableName( lockable.getRootTableName() );
 		update.addPrimaryKeyColumns( lockable.getRootTableIdentifierColumnNames() );
 		update.setVersionColumnName( lockable.getVersionColumnName() );
 		update.addColumn( lockable.getVersionColumnName() );
 		if ( factory.getSettings().isCommentsEnabled() ) {
 			update.setComment( lockMode + " lock " + lockable.getEntityName() );
 		}
 		return update.toStatementString();
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/ActionQueue.java b/hibernate-core/src/main/java/org/hibernate/engine/ActionQueue.java
index f76e01c322..56b5bcb939 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/ActionQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/ActionQueue.java
@@ -1,733 +1,733 @@
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
 package org.hibernate.engine;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.action.AfterTransactionCompletionProcess;
 import org.hibernate.action.BeforeTransactionCompletionProcess;
 import org.hibernate.action.BulkOperationCleanupAction;
 import org.hibernate.action.CollectionRecreateAction;
 import org.hibernate.action.CollectionRemoveAction;
 import org.hibernate.action.CollectionUpdateAction;
 import org.hibernate.action.EntityDeleteAction;
 import org.hibernate.action.EntityIdentityInsertAction;
 import org.hibernate.action.EntityInsertAction;
 import org.hibernate.action.EntityUpdateAction;
 import org.hibernate.action.Executable;
 import org.hibernate.cache.CacheException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for maintaining the queue of actions related to events.
  * </p>
  * The ActionQueue holds the DML operations queued as part of a session's
  * transactional-write-behind semantics.  DML operations are queued here
  * until a flush forces them to be executed against the database.
  *
  * @author Steve Ebersole
  */
 public class ActionQueue {
 
 	private static final Logger log = LoggerFactory.getLogger( ActionQueue.class );
 	private static final int INIT_QUEUE_LIST_SIZE = 5;
 
 	private SessionImplementor session;
 
 	// Object insertions, updates, and deletions have list semantics because
 	// they must happen in the right order so as to respect referential
 	// integrity
 	private ArrayList insertions;
 	private ArrayList deletions;
 	private ArrayList updates;
 	// Actually the semantics of the next three are really "Bag"
 	// Note that, unlike objects, collection insertions, updates,
 	// deletions are not really remembered between flushes. We
 	// just re-use the same Lists for convenience.
 	private ArrayList collectionCreations;
 	private ArrayList collectionUpdates;
 	private ArrayList collectionRemovals;
 
 	private AfterTransactionCompletionProcessQueue afterTransactionProcesses;
 	private BeforeTransactionCompletionProcessQueue beforeTransactionProcesses;
 
 	/**
 	 * Constructs an action queue bound to the given session.
 	 *
 	 * @param session The session "owning" this queue.
 	 */
 	public ActionQueue(SessionImplementor session) {
 		this.session = session;
 		init();
 	}
 
 	private void init() {
 		insertions = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		deletions = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		updates = new ArrayList( INIT_QUEUE_LIST_SIZE );
 
 		collectionCreations = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		collectionRemovals = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		collectionUpdates = new ArrayList( INIT_QUEUE_LIST_SIZE );
 
 		afterTransactionProcesses = new AfterTransactionCompletionProcessQueue( session );
 		beforeTransactionProcesses = new BeforeTransactionCompletionProcessQueue( session );
 	}
 
 	public void clear() {
 		updates.clear();
 		insertions.clear();
 		deletions.clear();
 
 		collectionCreations.clear();
 		collectionRemovals.clear();
 		collectionUpdates.clear();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityInsertAction action) {
 		insertions.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityDeleteAction action) {
 		deletions.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityUpdateAction action) {
 		updates.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionRecreateAction action) {
 		collectionCreations.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionRemoveAction action) {
 		collectionRemovals.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionUpdateAction action) {
 		collectionUpdates.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityIdentityInsertAction insert) {
 		insertions.add( insert );
 	}
 
 	public void addAction(BulkOperationCleanupAction cleanupAction) {
 		registerCleanupActions( cleanupAction );
 	}
 
 	public void registerProcess(AfterTransactionCompletionProcess process) {
 		afterTransactionProcesses.register( process );
 	}
 
 	public void registerProcess(BeforeTransactionCompletionProcess process) {
 		beforeTransactionProcesses.register( process );
 	}
 
 	/**
 	 * Perform all currently queued entity-insertion actions.
 	 *
 	 * @throws HibernateException error executing queued insertion actions.
 	 */
 	public void executeInserts() throws HibernateException {
 		executeActions( insertions );
 	}
 
 	/**
 	 * Perform all currently queued actions.
 	 *
 	 * @throws HibernateException error executing queued actions.
 	 */
 	public void executeActions() throws HibernateException {
 		executeActions( insertions );
 		executeActions( updates );
 		executeActions( collectionRemovals );
 		executeActions( collectionUpdates );
 		executeActions( collectionCreations );
 		executeActions( deletions );
 	}
 
 	/**
 	 * Prepares the internal action queues for execution.
 	 *
 	 * @throws HibernateException error preparing actions.
 	 */
 	public void prepareActions() throws HibernateException {
 		prepareActions( collectionRemovals );
 		prepareActions( collectionUpdates );
 		prepareActions( collectionCreations );
 	}
 
 	/**
 	 * Performs cleanup of any held cache softlocks.
 	 *
 	 * @param success Was the transaction successful.
 	 */
 	public void afterTransactionCompletion(boolean success) {
 		afterTransactionProcesses.afterTransactionCompletion( success );
 	}
 
 	/**
 	 * Execute any registered {@link BeforeTransactionCompletionProcess}
 	 */
 	public void beforeTransactionCompletion() {
 		beforeTransactionProcesses.beforeTransactionCompletion();
 	}
 
 	/**
 	 * Check whether the given tables/query-spaces are to be executed against
 	 * given the currently queued actions.
 	 *
 	 * @param tables The table/query-spaces to check.
 	 *
 	 * @return True if we contain pending actions against any of the given
 	 *         tables; false otherwise.
 	 */
 	public boolean areTablesToBeUpdated(Set tables) {
 		return areTablesToUpdated( updates, tables ) ||
 				areTablesToUpdated( insertions, tables ) ||
 				areTablesToUpdated( deletions, tables ) ||
 				areTablesToUpdated( collectionUpdates, tables ) ||
 				areTablesToUpdated( collectionCreations, tables ) ||
 				areTablesToUpdated( collectionRemovals, tables );
 	}
 
 	/**
 	 * Check whether any insertion or deletion actions are currently queued.
 	 *
 	 * @return True if insertions or deletions are currently queued; false otherwise.
 	 */
 	public boolean areInsertionsOrDeletionsQueued() {
 		return ( insertions.size() > 0 || deletions.size() > 0 );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private static boolean areTablesToUpdated(List actions, Set tableSpaces) {
 		for ( Executable action : (List<Executable>) actions ) {
 			final Serializable[] spaces = action.getPropertySpaces();
 			for ( Serializable space : spaces ) {
 				if ( tableSpaces.contains( space ) ) {
 					if ( log.isDebugEnabled() ) {
 						log.debug( "changes must be flushed to space: " + space );
 					}
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void executeActions(List list) throws HibernateException {
 		int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			execute( ( Executable ) list.get( i ) );
 		}
 		list.clear();
-		session.getBatcher().executeBatch();
+		session.getJDBCContext().getConnectionManager().executeBatch();
 	}
 
 	public void execute(Executable executable) {
 		try {
 			executable.execute();
 		}
 		finally {
 			registerCleanupActions( executable );
 		}
 	}
 
 	private void registerCleanupActions(Executable executable) {
 		beforeTransactionProcesses.register( executable.getBeforeTransactionCompletionProcess() );
 		if ( session.getFactory().getSettings().isQueryCacheEnabled() ) {
 			final String[] spaces = (String[]) executable.getPropertySpaces();
 			afterTransactionProcesses.addSpacesToInvalidate( spaces );
 			session.getFactory().getUpdateTimestampsCache().preinvalidate( spaces );
 		}
 		afterTransactionProcesses.register( executable.getAfterTransactionCompletionProcess() );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void prepareActions(List queue) throws HibernateException {
 		for ( Executable executable : (List<Executable>) queue ) {
 			executable.beforeExecutions();
 		}
 	}
 
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	public String toString() {
 		return new StringBuffer()
 				.append( "ActionQueue[insertions=" ).append( insertions )
 				.append( " updates=" ).append( updates )
 				.append( " deletions=" ).append( deletions )
 				.append( " collectionCreations=" ).append( collectionCreations )
 				.append( " collectionRemovals=" ).append( collectionRemovals )
 				.append( " collectionUpdates=" ).append( collectionUpdates )
 				.append( "]" )
 				.toString();
 	}
 
 	public int numberOfCollectionRemovals() {
 		return collectionRemovals.size();
 	}
 
 	public int numberOfCollectionUpdates() {
 		return collectionUpdates.size();
 	}
 
 	public int numberOfCollectionCreations() {
 		return collectionCreations.size();
 	}
 
 	public int numberOfDeletions() {
 		return deletions.size();
 	}
 
 	public int numberOfUpdates() {
 		return updates.size();
 	}
 
 	public int numberOfInsertions() {
 		return insertions.size();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void sortCollectionActions() {
 		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
 			//sort the updates by fk
 			java.util.Collections.sort( collectionCreations );
 			java.util.Collections.sort( collectionUpdates );
 			java.util.Collections.sort( collectionRemovals );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void sortActions() {
 		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
 			//sort the updates by pk
 			java.util.Collections.sort( updates );
 		}
 		if ( session.getFactory().getSettings().isOrderInsertsEnabled() ) {
 			sortInsertActions();
 		}
 	}
 
 	/**
 	 * Order the {@link #insertions} queue such that we group inserts
 	 * against the same entity together (without violating constraints).  The
 	 * original order is generated by cascade order, which in turn is based on
 	 * the directionality of foreign-keys.  So even though we will be changing
 	 * the ordering here, we need to make absolutely certain that we do not
 	 * circumvent this FK ordering to the extent of causing constraint
 	 * violations
 	 */
 	private void sortInsertActions() {
 		new InsertActionSorter().sort();
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public ArrayList cloneDeletions() {
 		return ( ArrayList ) deletions.clone();
 	}
 
 	public void clearFromFlushNeededCheck(int previousCollectionRemovalSize) {
 		collectionCreations.clear();
 		collectionUpdates.clear();
 		updates.clear();
 		// collection deletions are a special case since update() can add
 		// deletions of collections not loaded by the session.
 		for ( int i = collectionRemovals.size() - 1; i >= previousCollectionRemovalSize; i-- ) {
 			collectionRemovals.remove( i );
 		}
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public boolean hasAfterTransactionActions() {
 		return afterTransactionProcesses.processes.size() > 0;
 	}
 
 	public boolean hasBeforeTransactionActions() {
 		return beforeTransactionProcesses.processes.size() > 0;
 	}
 
 	public boolean hasAnyQueuedActions() {
 		return updates.size() > 0 ||
 				insertions.size() > 0 ||
 				deletions.size() > 0 ||
 				collectionUpdates.size() > 0 ||
 				collectionRemovals.size() > 0 ||
 				collectionCreations.size() > 0;
 	}
 
 	/**
 	 * Used by the owning session to explicitly control serialization of the
 	 * action queue
 	 *
 	 * @param oos The stream to which the action queue should get written
 	 *
 	 * @throws IOException Indicates an error writing to the stream
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		log.trace( "serializing action-queue" );
 
 		int queueSize = insertions.size();
 		log.trace( "starting serialization of [" + queueSize + "] insertions entries" );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( insertions.get( i ) );
 		}
 
 		queueSize = deletions.size();
 		log.trace( "starting serialization of [" + queueSize + "] deletions entries" );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( deletions.get( i ) );
 		}
 
 		queueSize = updates.size();
 		log.trace( "starting serialization of [" + queueSize + "] updates entries" );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( updates.get( i ) );
 		}
 
 		queueSize = collectionUpdates.size();
 		log.trace( "starting serialization of [" + queueSize + "] collectionUpdates entries" );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionUpdates.get( i ) );
 		}
 
 		queueSize = collectionRemovals.size();
 		log.trace( "starting serialization of [" + queueSize + "] collectionRemovals entries" );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionRemovals.get( i ) );
 		}
 
 		queueSize = collectionCreations.size();
 		log.trace( "starting serialization of [" + queueSize + "] collectionCreations entries" );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionCreations.get( i ) );
 		}
 	}
 
 	/**
 	 * Used by the owning session to explicitly control deserialization of the
 	 * action queue
 	 *
 	 * @param ois The stream from which to read the action queue
 	 * @param session The session to which the action queue belongs
 	 *
 	 * @return The deserialized action queue
 	 *
 	 * @throws IOException indicates a problem reading from the stream
 	 * @throws ClassNotFoundException Generally means we were unable to locate user classes.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public static ActionQueue deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		log.trace( "deserializing action-queue" );
 		ActionQueue rtn = new ActionQueue( session );
 
 		int queueSize = ois.readInt();
 		log.trace( "starting deserialization of [" + queueSize + "] insertions entries" );
 		rtn.insertions = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			rtn.insertions.add( ois.readObject() );
 		}
 
 		queueSize = ois.readInt();
 		log.trace( "starting deserialization of [" + queueSize + "] deletions entries" );
 		rtn.deletions = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			rtn.deletions.add( ois.readObject() );
 		}
 
 		queueSize = ois.readInt();
 		log.trace( "starting deserialization of [" + queueSize + "] updates entries" );
 		rtn.updates = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			rtn.updates.add( ois.readObject() );
 		}
 
 		queueSize = ois.readInt();
 		log.trace( "starting deserialization of [" + queueSize + "] collectionUpdates entries" );
 		rtn.collectionUpdates = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			rtn.collectionUpdates.add( ois.readObject() );
 		}
 
 		queueSize = ois.readInt();
 		log.trace( "starting deserialization of [" + queueSize + "] collectionRemovals entries" );
 		rtn.collectionRemovals = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			rtn.collectionRemovals.add( ois.readObject() );
 		}
 
 		queueSize = ois.readInt();
 		log.trace( "starting deserialization of [" + queueSize + "] collectionCreations entries" );
 		rtn.collectionCreations = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			rtn.collectionCreations.add( ois.readObject() );
 		}
 		return rtn;
 	}
 
 	private static class BeforeTransactionCompletionProcessQueue {
 		private SessionImplementor session;
 		private List<BeforeTransactionCompletionProcess> processes = new ArrayList<BeforeTransactionCompletionProcess>();
 
 		private BeforeTransactionCompletionProcessQueue(SessionImplementor session) {
 			this.session = session;
 		}
 
 		public void register(BeforeTransactionCompletionProcess process) {
 			if ( process == null ) {
 				return;
 			}
 			processes.add( process );
 		}
 
 		public void beforeTransactionCompletion() {
 			final int size = processes.size();
 			for ( int i = 0; i < size; i++ ) {
 				try {
 					BeforeTransactionCompletionProcess process = processes.get( i );
 					process.doBeforeTransactionCompletion( session );
 				}
 				catch ( HibernateException he ) {
 					throw he;
 				}
 				catch ( Exception e ) {
 					throw new AssertionFailure( "Unable to perform beforeTransactionCompletion callback", e );
 				}
 			}
 			processes.clear();
 		}
 	}
 
 	private static class AfterTransactionCompletionProcessQueue {
 		private SessionImplementor session;
 		private Set<String> querySpacesToInvalidate = new HashSet<String>();
 		private List<AfterTransactionCompletionProcess> processes
 				= new ArrayList<AfterTransactionCompletionProcess>( INIT_QUEUE_LIST_SIZE * 3 );
 
 		private AfterTransactionCompletionProcessQueue(SessionImplementor session) {
 			this.session = session;
 		}
 
 		public void addSpacesToInvalidate(String[] spaces) {
 			if ( spaces == null ) {
 				return;
 			}
 			for ( int i = 0, max = spaces.length; i < max; i++ ) {
 				addSpaceToInvalidate( spaces[i] );
 			}
 		}
 
 		public void addSpaceToInvalidate(String space) {
 			querySpacesToInvalidate.add( space );
 		}
 
 		public void register(AfterTransactionCompletionProcess process) {
 			if ( process == null ) {
 				return;
 			}
 			processes.add( process );
 		}
 
 		public void afterTransactionCompletion(boolean success) {
 			final int size = processes.size();
 			for ( int i = 0; i < size; i++ ) {
 				try {
 					AfterTransactionCompletionProcess process = processes.get( i );
 					process.doAfterTransactionCompletion( success, session );
 				}
 				catch ( CacheException ce ) {
 					log.error( "could not release a cache lock", ce );
 					// continue loop
 				}
 				catch ( Exception e ) {
 					throw new AssertionFailure( "Exception releasing cache locks", e );
 				}
 			}
 			processes.clear();
 
 			if ( session.getFactory().getSettings().isQueryCacheEnabled() ) {
 				session.getFactory().getUpdateTimestampsCache().invalidate(
 						querySpacesToInvalidate.toArray( new String[ querySpacesToInvalidate.size()] )
 				);
 			}
 			querySpacesToInvalidate.clear();
 		}
 	}
 
 	/**
 	 * Sorts the insert actions using more hashes.
 	 *
 	 * @author Jay Erb
 	 */
 	private class InsertActionSorter {
 		// the mapping of entity names to their latest batch numbers.
 		private HashMap latestBatches = new HashMap();
 		private HashMap entityBatchNumber;
 
 		// the map of batch numbers to EntityInsertAction lists
 		private HashMap actionBatches = new HashMap();
 
 		public InsertActionSorter() {
 			//optimize the hash size to eliminate a rehash.
 			entityBatchNumber = new HashMap( insertions.size() + 1, 1.0f );
 		}
 
 		/**
 		 * Sort the insert actions.
 		 */
 		@SuppressWarnings({ "unchecked", "UnnecessaryBoxing" })
 		public void sort() {
 			// the list of entity names that indicate the batch number
 			for ( EntityInsertAction action : (List<EntityInsertAction>) insertions ) {
 				// remove the current element from insertions. It will be added back later.
 				String entityName = action.getEntityName();
 
 				// the entity associated with the current action.
 				Object currentEntity = action.getInstance();
 
 				Integer batchNumber;
 				if ( latestBatches.containsKey( entityName ) ) {
 					// There is already an existing batch for this type of entity.
 					// Check to see if the latest batch is acceptable.
 					batchNumber = findBatchNumber( action, entityName );
 				}
 				else {
 					// add an entry for this type of entity.
 					// we can be assured that all referenced entities have already
 					// been processed,
 					// so specify that this entity is with the latest batch.
 					// doing the batch number before adding the name to the list is
 					// a faster way to get an accurate number.
 
 					batchNumber = Integer.valueOf( actionBatches.size() );
 					latestBatches.put( entityName, batchNumber );
 				}
 				entityBatchNumber.put( currentEntity, batchNumber );
 				addToBatch( batchNumber, action );
 			}
 			insertions.clear();
 
 			// now rebuild the insertions list. There is a batch for each entry in the name list.
 			for ( int i = 0; i < actionBatches.size(); i++ ) {
 				List batch = ( List ) actionBatches.get( new Integer( i ) );
 				for ( Object aBatch : batch ) {
 					EntityInsertAction action = (EntityInsertAction) aBatch;
 					insertions.add( action );
 				}
 			}
 		}
 
 		/**
 		 * Finds an acceptable batch for this entity to be a member as part of the {@link InsertActionSorter}
 		 *
 		 * @param action The action being sorted
 		 * @param entityName The name of the entity affected by the action
 		 *
 		 * @return An appropriate batch number; todo document this process better
 		 */
 		@SuppressWarnings({ "UnnecessaryBoxing", "unchecked" })
 		private Integer findBatchNumber(
 				EntityInsertAction action,
 				String entityName) {
 			// loop through all the associated entities and make sure they have been
 			// processed before the latest
 			// batch associated with this entity type.
 
 			// the current batch number is the latest batch for this entity type.
 			Integer latestBatchNumberForType = ( Integer ) latestBatches.get( entityName );
 
 			// loop through all the associations of the current entity and make sure that they are processed
 			// before the current batch number
 			Object[] propertyValues = action.getState();
 			Type[] propertyTypes = action.getPersister().getClassMetadata()
 					.getPropertyTypes();
 
 			for ( int i = 0; i < propertyValues.length; i++ ) {
 				Object value = propertyValues[i];
 				Type type = propertyTypes[i];
 				if ( type.isEntityType() && value != null ) {
 					// find the batch number associated with the current association, if any.
 					Integer associationBatchNumber = ( Integer ) entityBatchNumber.get( value );
 					if ( associationBatchNumber != null && associationBatchNumber.compareTo( latestBatchNumberForType ) > 0 ) {
 						// create a new batch for this type. The batch number is the number of current batches.
 						latestBatchNumberForType = Integer.valueOf( actionBatches.size() );
 						latestBatches.put( entityName, latestBatchNumberForType );
 						// since this entity will now be processed in the latest possible batch,
 						// we can be assured that it will come after all other associations,
 						// there's not need to continue checking.
 						break;
 					}
 				}
 			}
 			return latestBatchNumberForType;
 		}
 
 		@SuppressWarnings({ "unchecked" })
 		private void addToBatch(Integer batchNumber, EntityInsertAction action) {
 			List actions = ( List ) actionBatches.get( batchNumber );
 
 			if ( actions == null ) {
 				actions = new LinkedList();
 				actionBatches.put( batchNumber, actions );
 			}
 			actions.add( action );
 		}
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
index 69b7901273..ca3a585439 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
@@ -1,369 +1,362 @@
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
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
-import java.util.Set;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Transaction;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.event.EventListeners;
 import org.hibernate.impl.CriteriaImpl;
-import org.hibernate.jdbc.Batcher;
-import org.hibernate.jdbc.JDBCContext;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 
 /**
  * Defines the internal contract between the <tt>Session</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt> or <tt>EntityPersister</tt>.
  *
  * @see org.hibernate.Session the interface to the application
  * @see org.hibernate.impl.SessionImpl the actual implementation
  * @author Gavin King
  */
 public interface SessionImplementor extends Serializable {
 
 	/**
 	 * Retrieves the interceptor currently in use by this event source.
 	 *
 	 * @return The interceptor.
 	 */
 	public Interceptor getInterceptor();
 	
 	/**
 	 * Enable/disable automatic cache clearing from after transaction
 	 * completion (for EJB3)
 	 */
 	public void setAutoClear(boolean enabled);
 		
 	/**
 	 * Does this <tt>Session</tt> have an active Hibernate transaction
 	 * or is there a JTA transaction in progress?
 	 */
 	public boolean isTransactionInProgress();
 
 	/**
 	 * Initialize the collection (if not already initialized)
 	 */
 	public void initializeCollection(PersistentCollection collection, boolean writing) 
 	throws HibernateException;
 	
 	/**
 	 * Load an instance without checking if it was deleted. 
 	 * 
 	 * When <tt>nullable</tt> is disabled this method may create a new proxy or 
 	 * return an existing proxy; if it does not exist, throw an exception.
 	 * 
 	 * When <tt>nullable</tt> is enabled, the method does not create new proxies 
 	 * (but might return an existing proxy); if it does not exist, return 
 	 * <tt>null</tt>.
 	 * 
 	 * When <tt>eager</tt> is enabled, the object is eagerly fetched
 	 */
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) 
 	throws HibernateException;
 
 	/**
 	 * Load an instance immediately. This method is only called when lazily initializing a proxy.
 	 * Do not return the proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * System time before the start of the transaction
 	 */
 	public long getTimestamp();
 	/**
 	 * Get the creating <tt>SessionFactoryImplementor</tt>
 	 */
 	public SessionFactoryImplementor getFactory();
-	/**
-	 * Get the prepared statement <tt>Batcher</tt> for this session
-	 */
-	public Batcher getBatcher();
-	
+
 	/**
 	 * Execute a <tt>find()</tt> query
 	 */
 	public List list(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute an <tt>iterate()</tt> query
 	 */
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a <tt>scroll()</tt> query
 	 */
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a criteria query
 	 */
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode);
 	/**
 	 * Execute a criteria query
 	 */
 	public List list(CriteriaImpl criteria);
 	
 	/**
 	 * Execute a filter
 	 */
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Iterate a filter
 	 */
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 	
 	/**
 	 * Get the <tt>EntityPersister</tt> for any instance
 	 * @param entityName optional entity name
 	 * @param object the entity instance
 	 */
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;
 	
 	/**
 	 * Get the entity instance associated with the given <tt>Key</tt>,
 	 * calling the Interceptor if necessary
 	 */
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException;
 
 	/**
 	 * Notify the session that the transaction completed, so we no longer
 	 * own the old locks. (Also we should release cache softlocks.) May
 	 * be called multiple times during the transaction completion process.
 	 * Also called after an autocommit, in which case the second argument
 	 * is null.
 	 */
 	public void afterTransactionCompletion(boolean successful, Transaction tx);
 	
 	/**
 	 * Notify the session that the transaction is about to complete
 	 */
 	public void beforeTransactionCompletion(Transaction tx);
 
 	/**
 	 * Return the identifier of the persistent object, or null if 
 	 * not associated with the session
 	 */
 	public Serializable getContextEntityIdentifier(Object object);
 
 	/**
 	 * The best guess entity name for an entity not in an association
 	 */
 	public String bestGuessEntityName(Object object);
 	
 	/**
 	 * The guessed entity name for an entity not in an association
 	 */
 	public String guessEntityName(Object entity) throws HibernateException;
 	
 	/** 
 	 * Instantiate the entity class, initializing with the given identifier
 	 */
 	public Object instantiate(String entityName, Serializable id) throws HibernateException;
 	
 	/**
 	 * Execute an SQL Query
 	 */
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) 
 	throws HibernateException;
 	
 	/**
 	 * Execute an SQL Query
 	 */
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) 
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a fully built list.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The result list.
 	 * @throws HibernateException
 	 */
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a scrollable result.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The resulting scrollable result.
 	 * @throws HibernateException
 	 */
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Retreive the currently set value for a filter parameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter parameter value.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public Object getFilterParameterValue(String filterParameterName);
 
 	/**
 	 * Retreive the type for a given filter parrameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter param type
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public Type getFilterParameterType(String filterParameterName);
 
 	/**
 	 * Return the currently enabled filters.  The filter map is keyed by filter
 	 * name, with values corresponding to the {@link org.hibernate.impl.FilterImpl}
 	 * instance.
 	 * @return The currently enabled filters.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public Map getEnabledFilters();
 	
 	public int getDontFlushFromFind();
 	
 	/**
 	 * Retrieves the configured event listeners from this event source.
 	 *
 	 * @return The configured event listeners.
 	 */
 	public EventListeners getListeners();
 	
 	//TODO: temporary
 	
 	/**
 	 * Get the persistence context for this session
 	 */
 	public PersistenceContext getPersistenceContext();
 	
 	/**
 	 * Execute a HQL update or delete query
 	 */
 	int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException;
 	
 	/**
 	 * Execute a native SQL update or delete query
 	 */
 	int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException;
 
 
 	/**
 	 * Return changes to this session that have not been flushed yet.
 	 *
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException;
 
 	/**
 	 * Apply non-flushed changes from a different session to this session. It is assumed
 	 * that this SessionImpl is "clean" (e.g., has no non-flushed changes, no cached entities,
 	 * no cached collections, no queued actions). The specified NonFlushedChanges object cannot
 	 * be bound to any session.
 	 * <p/>
 	 * @param nonFlushedChanges the non-flushed changes
 	 */
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException;	
 
 	// copied from Session:
 	
 	public EntityMode getEntityMode();
 	public CacheMode getCacheMode();
 	public void setCacheMode(CacheMode cm);
 	public boolean isOpen();
 	public boolean isConnected();
 	public FlushMode getFlushMode();
 	public void setFlushMode(FlushMode fm);
 	public Connection connection();
 	public void flush();
 	
 	/**
 	 * Get a Query instance for a named query or named native SQL query
 	 */
 	public Query getNamedQuery(String name);
 	/**
 	 * Get a Query instance for a named native SQL query
 	 */
 	public Query getNamedSQLQuery(String name);
 	
 	public boolean isEventSource();
 
 	public void afterScrollOperation();
 
 	/**
 	 * Get the <i>internal</i> fetch profile currently associated with this session.
 	 *
 	 * @return The current internal fetch profile, or null if none currently associated.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public String getFetchProfile();
 
 	/**
 	 * Set the current <i>internal</i> fetch profile for this session.
 	 *
 	 * @param name The internal fetch profile name to use
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	public void setFetchProfile(String name);
 
 	public JDBCContext getJDBCContext();
 
 	/**
 	 * Determine whether the session is closed.  Provided seperately from
 	 * {@link #isOpen()} as this method does not attempt any JTA synch
 	 * registration, where as {@link #isOpen()} does; which makes this one
 	 * nicer to use for most internal purposes.
 	 *
 	 * @return True if the session is closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Get the load query influencers associated with this session.
 	 *
 	 * @return the load query influencers associated with this session;
 	 * should never be null.
 	 */
 	public LoadQueryInfluencers getLoadQueryInfluencers();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
similarity index 52%
rename from hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
index b728cdee72..d739f86c00 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/ConnectionManager.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ConnectionManagerImpl.java
@@ -1,470 +1,608 @@
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
-package org.hibernate.jdbc;
+package org.hibernate.engine.jdbc.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
-import java.io.Serializable;
+import java.sql.CallableStatement;
 import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
+
+import org.hibernate.AssertionFailure;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
+import org.hibernate.ScrollMode;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
+import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
+import org.hibernate.engine.jdbc.spi.ConnectionManager;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
+import org.hibernate.engine.jdbc.spi.LogicalConnection;
+import org.hibernate.jdbc.Batcher;
+import org.hibernate.jdbc.Expectation;
 
 /**
  * Encapsulates JDBC Connection management logic needed by Hibernate.
  * <p/>
  * The lifecycle is intended to span a logical series of interactions with the
  * database.  Internally, this means the the lifecycle of the Session.
  *
  * @author Steve Ebersole
  */
-public class ConnectionManager implements Serializable {
+public class ConnectionManagerImpl implements ConnectionManager {
 
-	private static final Logger log = LoggerFactory.getLogger( ConnectionManager.class );
+	private static final Logger log = LoggerFactory.getLogger( ConnectionManagerImpl.class );
 
-	// TODO: check if it's ok to change the method names in Callback
 	public static interface Callback extends ConnectionObserver {
 		public boolean isTransactionInProgress();
 	}
 
+	// TODO: check if it's ok to change the method names in Callback
+
 	private transient SessionFactoryImplementor factory;
-	private final Callback callback;
+	private transient Connection proxiedConnection;
+	private transient Interceptor interceptor;
 
-	private transient LogicalConnectionImpl connection;
+	private final Callback callback;
 
-	private transient Batcher batcher;
-	private transient Interceptor interceptor;
+	private transient LogicalConnectionImpl logicalConnection;
 
 	/**
 	 * Constructs a ConnectionManager.
 	 * <p/>
 	 * This is the form used internally.
 	 * 
-	 * @param factory The SessionFactory.
 	 * @param callback An observer for internal state change.
 	 * @param releaseMode The mode by which to release JDBC connections.
-	 * @param connection An externally supplied connection.
+	 * @param suppliedConnection An externally supplied connection.
 	 */ 
-	public ConnectionManager(
+	public ConnectionManagerImpl(
 	        SessionFactoryImplementor factory,
 	        Callback callback,
 	        ConnectionReleaseMode releaseMode,
-	        Connection connection,
+	        Connection suppliedConnection,
 	        Interceptor interceptor) {
-		this.factory = factory;
-		this.callback = callback;
-
-		this.interceptor = interceptor;
-		this.batcher = factory.getSettings().getBatcherFactory().createBatcher( this, interceptor );
-
-		setupConnection( connection, releaseMode );
+		this( factory,
+				callback,
+				interceptor,
+				new LogicalConnectionImpl(
+						suppliedConnection,
+						releaseMode,
+						factory.getJdbcServices(),
+						factory.getStatistics() != null ? factory.getStatisticsImplementor() : null,
+						factory.getSettings().getBatcherFactory()
+				)
+		);
 	}
 
 	/**
 	 * Private constructor used exclusively from custom serialization
 	 */
-	private ConnectionManager(
+	private ConnectionManagerImpl(
 			SessionFactoryImplementor factory,
 			Callback callback,
-			ConnectionReleaseMode releaseMode,
-			Interceptor interceptor
+			Interceptor interceptor,
+			LogicalConnectionImpl logicalConnection
 	) {
 		this.factory = factory;
 		this.callback = callback;
-
 		this.interceptor = interceptor;
-		this.batcher = factory.getSettings().getBatcherFactory().createBatcher( this, interceptor );
-	}
-
-	private void setupConnection(Connection suppliedConnection,
-								 ConnectionReleaseMode releaseMode
-	) {
-		connection =
-				new LogicalConnectionImpl(
-						suppliedConnection,
-						releaseMode,
-						factory.getJdbcServices()
-				);
-		connection.addObserver( callback );
-	}
-
-	/**
-	 * The session factory.
-	 *
-	 * @return the session factory.
-	 */
-	public SessionFactoryImplementor getFactory() {
-		return factory;
+		setupConnection( logicalConnection );
 	}
 
-	/**
-	 * The batcher managed by this ConnectionManager.
-	 *
-	 * @return The batcher.
-	 */
-	public Batcher getBatcher() {
-		return batcher;
+	private void setupConnection(LogicalConnectionImpl logicalConnection) {
+		this.logicalConnection = logicalConnection;
+		this.logicalConnection.addObserver( callback );
+		proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
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
+	@Override
 	public Connection getConnection() throws HibernateException {
-		return connection.getConnection();
+		return logicalConnection.getConnection();
 	}
 
+	@Override
 	public boolean hasBorrowedConnection() {
 		// used from testsuite
-		return connection.hasBorrowedConnection();
+		return logicalConnection.hasBorrowedConnection();
 	}
 
 	public Connection borrowConnection() {
-		return connection.borrowConnection();
+		return logicalConnection.borrowConnection();
 	}
 
+	@Override
 	public void releaseBorrowedConnection() {
-		connection.releaseBorrowedConnection();
+		logicalConnection.releaseBorrowedConnection();
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
-		return connection == null ||
-				! connection.isOpen() ||
-				! connection.isPhysicallyConnected() ||
-				connection.getConnection().getAutoCommit();
+		return logicalConnection == null ||
+				! logicalConnection.isOpen() ||
+				! logicalConnection.isPhysicallyConnected() ||
+				logicalConnection.getConnection().getAutoCommit();
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
-		if ( connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
+		if ( logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			return true;
 		}
-		else if ( connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION ) {
+		else if ( logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION ) {
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
-		if ( connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
+		if ( logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_STATEMENT ) {
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
-			return connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION && inAutoCommitState;
+			return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION && inAutoCommitState;
 		}
 	}
 
 	/**
 	 * Is this ConnectionManager instance "logically" connected.  Meaning
 	 * do we either have a cached connection available or do we have the
 	 * ability to obtain a connection on demand.
 	 *
 	 * @return True if logically connected; false otherwise.
 	 */
+	@Override
 	public boolean isCurrentlyConnected() {
-		if ( connection != null ) {
-			if ( connection.isUserSuppliedConnection() ) {
-				return connection.isPhysicallyConnected();
-			}
-			else {
-				return connection.isOpen();
-			}
-		}
-		else {
-			return false;
-		}
+		return logicalConnection != null && logicalConnection.isLogicallyConnected();
 	}
 
 	/**
 	 * To be called after execution of each JDBC statement.  Used to
 	 * conditionally release the JDBC connection aggressively if
 	 * the configured release mode indicates.
 	 */
+	@Override
 	public void afterStatement() {
 		if ( isAggressiveRelease() ) {
-			if ( batcher.hasOpenResources() ) {
-				log.debug( "skipping aggresive-release due to open resources on batcher" );
-			}
-			else {
-				connection.afterStatementExecution();
-			}
+			logicalConnection.afterStatementExecution();
 		}
 	}
 
 	/**
 	 * To be called after local transaction completion.  Used to conditionally
 	 * release the JDBC connection aggressively if the configured release mode
 	 * indicates.
 	 */
 	public void afterTransaction() {
-		if ( connection != null ) {
-			if ( isAfterTransactionRelease() ) {
-				connection.afterTransaction();
-			}
-			else if ( isAggressiveReleaseNoTransactionCheck() && batcher.hasOpenResources() ) {
-				log.info( "forcing batcher resource cleanup on transaction completion; forgot to close ScrollableResults/Iterator?" );
-				batcher.closeStatements();
-				connection.afterTransaction();
+		if ( logicalConnection != null ) {
+			if ( isAfterTransactionRelease() || isAggressiveReleaseNoTransactionCheck() ) {
+				logicalConnection.afterTransaction();
 			}
 			else if ( isOnCloseRelease() ) {
 				// log a message about potential connection leaks
 				log.debug( "transaction completed on session with on_close connection release mode; be sure to close the session to release JDBC resources!" );
 			}
 		}
-		batcher.unsetTransactionTimeout();
 	}
 
 	private boolean isAfterTransactionRelease() {
-		return connection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION;
+		return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION;
 	}
 
 	private boolean isOnCloseRelease() {
-		return connection.getConnectionReleaseMode() == ConnectionReleaseMode.ON_CLOSE;
+		return logicalConnection.getConnectionReleaseMode() == ConnectionReleaseMode.ON_CLOSE;
 	}
 
 	public boolean isLogicallyConnected() {
-		return connection != null && connection.isOpen();
+		return logicalConnection != null && logicalConnection.isOpen();
+	}
+
+	@Override
+	public void setTransactionTimeout(int seconds) {
+		logicalConnection.setTransactionTimeout( seconds );
 	}
 
 	/**
 	 * To be called after Session completion.  Used to release the JDBC
 	 * connection.
 	 *
 	 * @return The connection mantained here at time of close.  Null if
 	 * there was no connection cached internally.
 	 */
+	@Override
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
+	@Override
 	public Connection manualDisconnect() {
 		if ( ! isLogicallyConnected() ) {
 			throw new IllegalStateException( "cannot manually disconnect because not logically connected." );
 		}
-		batcher.closeStatements();
-		return connection.manualDisconnect();
+		return logicalConnection.manualDisconnect();
 	}
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for ConnectionProvider-supplied connections.
 	 */
+	@Override
 	public void manualReconnect() {
 		manualReconnect( null );
 	}
 
 	/**
 	 * Manually reconnect the underlying JDBC Connection.  Should be called at
 	 * some point after manualDisconnect().
 	 * <p/>
 	 * This form is used for user-supplied connections.
 	 */
+	@Override
 	public void manualReconnect(Connection suppliedConnection) {
 		if ( ! isLogicallyConnected() ) {
 			throw new IllegalStateException( "cannot manually disconnect because not logically connected." );
 		}
-		connection.reconnect( suppliedConnection );
+		logicalConnection.reconnect( suppliedConnection );
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
-		if ( connection == null ) {
+		if ( logicalConnection == null ) {
 			log.trace( "connection already null in cleanup : no action");
 			return null;
 		}
 		try {
 			log.trace( "performing cleanup" );
-
-			if ( isLogicallyConnected() ) {
-				batcher.closeStatements();
-			}
-			Connection c = connection.close();
+			Connection c = logicalConnection.close();
 			return c;
 		}
 		finally {
-			connection = null;
+			logicalConnection = null;
 		}
 	}
 
 	/**
 	 * Callback to let us know that a flush is beginning.  We use this fact
 	 * to temporarily circumvent aggressive connection releasing until after
 	 * the flush cycle is complete {@link #flushEnding()}
 	 */
+	@Override
 	public void flushBeginning() {
 		log.trace( "registering flush begin" );
-		connection.disableReleases();
+		logicalConnection.disableReleases();
 	}
 
 	/**
 	 * Callback to let us know that a flush is ending.  We use this fact to
 	 * stop circumventing aggressive releasing connections.
 	 */
+	@Override
 	public void flushEnding() {
 		log.trace( "registering flush end" );
-		connection.enableReleases();
+		logicalConnection.enableReleases();
 		afterStatement();
 	}
 
+	/**
+	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
+	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, int)}).
+	 */
+	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
+			throws SQLException, HibernateException {
+		if ( autoGeneratedKeys == PreparedStatement.RETURN_GENERATED_KEYS ) {
+			checkAutoGeneratedKeysSupportEnabled();
+		}
+		executeBatch();
+		return proxiedConnection.prepareStatement(
+					getSQL( sql ),
+					autoGeneratedKeys
+		);
+	}
+
+	/**
+	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
+	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
+	 */
+	public PreparedStatement prepareStatement(String sql, String[] columnNames)
+			throws SQLException, HibernateException {
+		checkAutoGeneratedKeysSupportEnabled();
+		executeBatch();
+		return proxiedConnection.prepareStatement( getSQL( sql ), columnNames );
+	}
+
+	private void checkAutoGeneratedKeysSupportEnabled() {
+		if ( ! factory.getSettings().isGetGeneratedKeysEnabled() ) {
+			throw new AssertionFailure("getGeneratedKeys() support is not enabled");
+		}
+	}
+
+	/**
+	 * Get a non-batchable prepared statement to use for selecting. Does not
+	 * result in execution of the current batch.
+	 */
+	public PreparedStatement prepareSelectStatement(String sql)
+			throws SQLException, HibernateException {
+		return proxiedConnection.prepareStatement( getSQL( sql ) );
+	}
+
+	/**
+	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
+	 */
+	public PreparedStatement prepareStatement(String sql, boolean isCallable)
+	throws SQLException, HibernateException {
+		return isCallable ? prepareCallableStatement( sql ) : prepareStatement( sql );
+	}
+
+	/**
+	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
+	 */
+	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException {
+		executeBatch();
+		log.trace("preparing callable statement");
+		return CallableStatement.class.cast(
+				proxiedConnection.prepareStatement( getSQL( sql ) )
+		);
+	}
+
+	/**
+	 * Get a batchable prepared statement to use for inserting / deleting / updating
+	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
+	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
+	 * statement explicitly.
+	 * @see org.hibernate.jdbc.Batcher#addToBatch
+	 */
+	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable)
+			throws SQLException, HibernateException {
+		String batchUpdateSQL = getSQL( sql );
+		PreparedStatement batchUpdate = getBatcher().getStatement( batchUpdateSQL );
+		if ( batchUpdate == null ) {
+			batchUpdate = prepareStatement( batchUpdateSQL, isCallable ); // calls executeBatch()
+			getBatcher().setStatement( batchUpdateSQL, batchUpdate );
+		}
+		else {
+			log.debug( "reusing prepared statement" );
+			factory.getJdbcServices().getSqlStatementLogger().logStatement( batchUpdateSQL );
+		}
+		return batchUpdate;
+	}
+
+	private Batcher getBatcher() {
+		return logicalConnection.getBatcher();
+	}
+
+	private PreparedStatement prepareStatement(String sql)
+	throws SQLException, HibernateException {
+		executeBatch();
+		return proxiedConnection.prepareStatement( getSQL( sql ) );
+	}
+
+	/**
+	 * Get a prepared statement for use in loading / querying. If not explicitly
+	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
+	 * session is closed or disconnected.
+	 */
+	public PreparedStatement prepareQueryStatement(
+			String sql,
+			boolean isCallable) throws SQLException, HibernateException {
+		sql = getSQL( sql );
+		PreparedStatement result = (
+				isCallable ?
+						proxiedConnection.prepareCall(sql ) :
+		                proxiedConnection.prepareStatement( sql )
+				);
+		setStatementFetchSize( result );
+		logicalConnection.getResourceRegistry().registerLastQuery( result );
+		return result;
+	}
+
+	/**
+	 * Cancel the current query statement
+	 */
+	public void cancelLastQuery() throws HibernateException {
+		logicalConnection.getResourceRegistry().cancelLastQuery();
+	}
+
+	public PreparedStatement prepareScrollableQueryStatement(
+			String sql,
+	        ScrollMode scrollMode,
+			boolean isCallable) throws SQLException, HibernateException {
+		if ( ! factory.getSettings().isScrollableResultSetsEnabled() ) {
+			throw new AssertionFailure("scrollable result sets are not enabled");
+		}
+		sql = getSQL( sql );
+		PreparedStatement result = (
+				isCallable ?
+						proxiedConnection.prepareCall(
+								sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
+						) :
+		                proxiedConnection.prepareStatement(
+								sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
+						)
+				);
+		setStatementFetchSize( result );
+		logicalConnection.getResourceRegistry().registerLastQuery( result );
+		return result;
+	}
+
+	public void abortBatch(SQLException sqle) {
+		getBatcher().abortBatch( sqle );
+	}
+
+	public void addToBatch(Expectation expectation )  throws SQLException, HibernateException {
+		getBatcher().addToBatch( expectation );
+	}
+
+	public void executeBatch() throws HibernateException {
+		getBatcher().executeBatch();
+	}
+
+	private String getSQL(String sql) {
+		sql = interceptor.onPrepareStatement( sql );
+		if ( sql==null || sql.length() == 0 ) {
+			throw new AssertionFailure( "Interceptor.onPrepareStatement() returned null or empty string." );
+		}
+		return sql;
+	}
+
+	private void setStatementFetchSize(PreparedStatement statement) throws SQLException {
+		if ( factory.getSettings().getJdbcFetchSize() !=null ) {
+			statement.setFetchSize( factory.getSettings().getJdbcFetchSize().intValue() );
+		}
+	}
+
 	public boolean isReadyForSerialization() {
-		return connection == null ? true : ! batcher.hasOpenResources() && connection.isReadyForSerialization();
+		return logicalConnection == null ? true : logicalConnection.isReadyForSerialization();
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
-
-		oos.writeObject( factory );
-		oos.writeObject( interceptor );
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
-		factory = (SessionFactoryImplementor) ois.readObject();
-		interceptor = (Interceptor) ois.readObject();
 		ois.defaultReadObject();
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
-		connection.serialize( oos );
+		logicalConnection.serialize( oos );
 	}
 
-	public static ConnectionManager deserialize(
+	public static ConnectionManagerImpl deserialize(
 			ObjectInputStream ois,
 	        SessionFactoryImplementor factory,
 	        Interceptor interceptor,
 	        ConnectionReleaseMode connectionReleaseMode,
 	        Callback callback) throws IOException {
-		ConnectionManager connectionManager = new ConnectionManager(
+		return new ConnectionManagerImpl(
 				factory,
 		        callback,
-		        connectionReleaseMode,
-		        interceptor
-		);
-		connectionManager.connection =
+				interceptor,
 				LogicalConnectionImpl.deserialize(
 						ois,
-						factory.getJdbcServices( ),
-						connectionReleaseMode
-				);
-		connectionManager.connection.addObserver( callback );
-		return connectionManager;
+						factory.getJdbcServices(),
+						factory.getStatistics() != null ? factory.getStatisticsImplementor() : null,
+						connectionReleaseMode,
+						factory.getSettings().getBatcherFactory()
+				)
+		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/JDBCContext.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JDBCContextImpl.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/jdbc/JDBCContext.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JDBCContextImpl.java
index b87d32131f..bc3e7fb75a 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/JDBCContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JDBCContextImpl.java
@@ -1,363 +1,369 @@
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
-package org.hibernate.jdbc;
+package org.hibernate.engine.jdbc.internal;
 
-import java.io.Serializable;
 import java.io.ObjectOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import javax.transaction.TransactionManager;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.SessionException;
 import org.hibernate.Transaction;
 import org.hibernate.TransactionException;
-import org.hibernate.engine.jdbc.spi.ConnectionObserver;
+import org.hibernate.engine.jdbc.spi.ConnectionManager;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.transaction.synchronization.CallbackCoordinator;
 import org.hibernate.transaction.synchronization.HibernateSynchronizationImpl;
 import org.hibernate.util.JTAHelper;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.transaction.TransactionFactory;
 
 /**
  * Acts as the mediary between "entity-mode related" sessions in terms of
  * their interaction with the JDBC data store.
  *
  * @author Steve Ebersole
  */
-public class JDBCContext implements Serializable, ConnectionManager.Callback {
+public class JDBCContextImpl implements ConnectionManagerImpl.Callback, JDBCContext {
 
 	// TODO : make this the factory for "entity mode related" sessions;
 	// also means making this the target of transaction-synch and the
 	// thing that knows how to cascade things between related sessions
 	//
 	// At that point, perhaps this thing is a "SessionContext", and
 	// ConnectionManager is a "JDBCContext"?  A "SessionContext" should
 	// live in the impl package...
 
-	private static final Logger log = LoggerFactory.getLogger( JDBCContext.class );
-
-	public static interface Context extends TransactionFactory.Context {
-		/**
-		 * We cannot rely upon this method being called! It is only
-		 * called if we are using Hibernate Transaction API.
-		 */
-		public void afterTransactionBegin(Transaction tx);
-		public void beforeTransactionCompletion(Transaction tx);
-		public void afterTransactionCompletion(boolean success, Transaction tx);
-		public ConnectionReleaseMode getConnectionReleaseMode();
-		public boolean isAutoCloseSessionEnabled();
-	}
+	private static final Logger log = LoggerFactory.getLogger( JDBCContextImpl.class );
 
 	private Context owner;
-	private ConnectionManager connectionManager;
+	private ConnectionManagerImpl connectionManager;
 	private transient boolean isTransactionCallbackRegistered;
 	private transient Transaction hibernateTransaction;
 
 	private CallbackCoordinator jtaSynchronizationCallbackCoordinator;
 
-	public JDBCContext(Context owner, Connection connection, Interceptor interceptor) {
+	public JDBCContextImpl(Context owner, Connection connection, Interceptor interceptor) {
 		this.owner = owner;
-		this.connectionManager = new ConnectionManager(
+		this.connectionManager = new ConnectionManagerImpl(
 		        owner.getFactory(),
-		        this,
+				this,
 		        owner.getConnectionReleaseMode(),
 		        connection,
 		        interceptor
-			);
+		);
 
 		final boolean registerSynchronization = owner.isAutoCloseSessionEnabled()
 		        || owner.isFlushBeforeCompletionEnabled()
 		        || owner.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION;
 		if ( registerSynchronization ) {
 			registerSynchronizationIfPossible();
 		}
 	}
 
 	/**
 	 * Private constructor used exclusively for custom serialization...
 	 *
 	 */
-	private JDBCContext() {
+	private JDBCContextImpl() {
 	}
 
+	@Override
 	public CallbackCoordinator getJtaSynchronizationCallbackCoordinator() {
 		return jtaSynchronizationCallbackCoordinator;
 	}
 
-	public CallbackCoordinator getJtaSynchronizationCallbackCoordinator(javax.transaction.Transaction jtaTransaction) {
+	private CallbackCoordinator getJtaSynchronizationCallbackCoordinator(javax.transaction.Transaction jtaTransaction) {
 		jtaSynchronizationCallbackCoordinator = new CallbackCoordinator( owner, this, jtaTransaction, hibernateTransaction );
 		return jtaSynchronizationCallbackCoordinator;
 	}
 
+	@Override
 	public void cleanUpJtaSynchronizationCallbackCoordinator() {
 		jtaSynchronizationCallbackCoordinator = null;
 	}
 
 
 	// ConnectionManager.Callback implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
+	@Override
 	public void physicalConnectionObtained(Connection connection) {
 		if ( owner.getFactory().getStatistics().isStatisticsEnabled() ) {
 			owner.getFactory().getStatisticsImplementor().connect();
 		}
 	}
 
+	@Override
 	public void physicalConnectionReleased() {
 		if ( !isTransactionCallbackRegistered ) {
 			afterTransactionCompletion( false, null );
 			// Note : success = false, because we don't know the outcome of the transaction
 		}
 	}
 
+	@Override
 	public void logicalConnectionClosed() {
 		// TODO: anything need to be done?
 	}
 
+	@Override
 	public SessionFactoryImplementor getFactory() {
 		return owner.getFactory();
 	}
 
+	@Override
 	public ConnectionManager getConnectionManager() {
 		return connectionManager;
 	}
 
 	public Connection borrowConnection() {
 		return connectionManager.borrowConnection();
 	}
 	
+	@Override
 	public Connection connection() throws HibernateException {
 		if ( owner.isClosed() ) {
 			throw new SessionException( "Session is closed" );
 		}
 
 		return connectionManager.getConnection();
 	}
 
+	@Override
 	public boolean registerCallbackIfNecessary() {
 		if ( isTransactionCallbackRegistered ) {
 			return false;
 		}
 		else {
 			isTransactionCallbackRegistered = true;
 			return true;
 		}
 
 	}
 
+	@Override
 	public boolean registerSynchronizationIfPossible() {
 		if ( isTransactionCallbackRegistered ) {
 			// we already have a callback registered; either a local
 			// (org.hibernate.Transaction) transaction has accepted
 			// callback responsibilities, or we have previously
 			// registered a transaction synch.
 			return true;
 		}
 		boolean localCallbacksOnly = owner.getFactory().getSettings()
 				.getTransactionFactory()
 				.areCallbacksLocalToHibernateTransactions();
 		if ( localCallbacksOnly ) {
 			// the configured transaction-factory says it only supports
 			// local callback mode, so no sense attempting to register a
 			// JTA Synchronization
 			return false;
 		}
 		TransactionManager tm = owner.getFactory().getTransactionManager();
 		if ( tm == null ) {
 			// if there is no TM configured, we will not be able to access
 			// the javax.transaction.Transaction object in order to
 			// register a synch anyway.
 			return false;
 		}
 		else {
 			try {
 				if ( !isTransactionInProgress() ) {
 					log.trace( "TransactionFactory reported no active transaction; Synchronization not registered" );
 					return false;
 				}
 				else {
 					javax.transaction.Transaction tx = tm.getTransaction();
 					if ( JTAHelper.isMarkedForRollback( tx ) ) {
 						// transactions marked for rollback-only cause some TM impls to throw exceptions
 						log.debug( "Transaction is marked for rollback; skipping Synchronization registration" );
 						return false;
 					}
 					else {
 						if ( hibernateTransaction == null ) {
 							hibernateTransaction = owner.getFactory().getSettings().getTransactionFactory().createTransaction( this, owner );
 						}
 						tx.registerSynchronization(
 								new HibernateSynchronizationImpl( getJtaSynchronizationCallbackCoordinator( tx ) )
 						);
 //						tx.registerSynchronization( new CacheSynchronization(owner, this, tx, hibernateTransaction) );
 						isTransactionCallbackRegistered = true;
 						log.debug("successfully registered Synchronization");
 						return true;
 					}
 				}
 			}
 			catch( HibernateException e ) {
 				throw e;
 			}
 			catch (Exception e) {
 				throw new TransactionException( "could not register synchronization with JTA TransactionManager", e );
 			}
 		}
 	}
 	
+	@Override
 	public boolean isTransactionInProgress() {
 		return owner.getFactory().getSettings().getTransactionFactory()
 				.isTransactionInProgress( this, owner, hibernateTransaction );
 	}
 
+	@Override
 	public Transaction getTransaction() throws HibernateException {
 		if (hibernateTransaction==null) {
 			hibernateTransaction = owner.getFactory().getSettings()
 					.getTransactionFactory()
 					.createTransaction( this, owner );
 		}
 		return hibernateTransaction;
 	}
 	
+	@Override
 	public void beforeTransactionCompletion(Transaction tx) {
 		log.trace( "before transaction completion" );
 		owner.beforeTransactionCompletion(tx);
 	}
 	
 	/**
 	 * We cannot rely upon this method being called! It is only
 	 * called if we are using Hibernate Transaction API.
 	 */
+	@Override
 	public void afterTransactionBegin(Transaction tx) {
 		log.trace( "after transaction begin" );
 		owner.afterTransactionBegin(tx);
 	}
 
+	@Override
 	public void afterTransactionCompletion(boolean success, Transaction tx) {
 		log.trace( "after transaction completion" );
 
 		if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 			getFactory().getStatisticsImplementor().endTransaction(success);
 		}
 
 		connectionManager.afterTransaction();
 
 		isTransactionCallbackRegistered = false;
 		hibernateTransaction = null;
 		owner.afterTransactionCompletion(success, tx);
 	}
 	
 	/**
 	 * Called after executing a query outside the scope of
 	 * a Hibernate or JTA transaction
 	 */
+	@Override
 	public void afterNontransactionalQuery(boolean success) {
 		log.trace( "after autocommit" );
 		try {
 			// check to see if the connection is in auto-commit 
 			// mode (no connection means aggressive connection
 			// release outside a JTA transaction context, so MUST
 			// be autocommit mode)
 			boolean isAutocommit = connectionManager.isAutoCommit();
 
 			connectionManager.afterTransaction();
 			
 			if ( isAutocommit ) {
 				owner.afterTransactionCompletion(success, null);
 			}
 		}
 		catch (SQLException sqle) {
 			throw owner.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not inspect JDBC autocommit mode"
 				);
 		}
 	}
 
 
 	// serialization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	public boolean isReadyForSerialization() {
+		return connectionManager.isReadyForSerialization();
+	}
+
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		// isTransactionCallbackRegistered denotes whether any Hibernate
 		// Transaction has registered as a callback against this
 		// JDBCContext; only one such callback is allowed.  Directly
 		// serializing this value causes problems with JDBCTransaction,
 		// or really any Transaction impl where the callback is local
 		// to the Transaction instance itself, since that Transaction
 		// is not serialized along with the JDBCContext.  Thus we
 		// handle that fact here explicitly...
 		oos.defaultWriteObject();
 		boolean deserHasCallbackRegistered = isTransactionCallbackRegistered
 				&& ! owner.getFactory().getSettings().getTransactionFactory()
 				.areCallbacksLocalToHibernateTransactions();
 		oos.writeBoolean( deserHasCallbackRegistered );
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		isTransactionCallbackRegistered = ois.readBoolean();
 	}
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 * @throws IOException
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		connectionManager.serialize( oos );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @throws IOException
 	 */
-	public static JDBCContext deserialize(
+	public static JDBCContextImpl deserialize(
 			ObjectInputStream ois,
 	        Context context,
-	        Interceptor interceptor) throws IOException {
-		JDBCContext jdbcContext = new JDBCContext();
+	        Interceptor interceptor) throws IOException, ClassNotFoundException {
+		JDBCContextImpl jdbcContext = new JDBCContextImpl();
 		jdbcContext.owner = context;
-		jdbcContext.connectionManager = ConnectionManager.deserialize(
+		jdbcContext.connectionManager = ConnectionManagerImpl.deserialize(
 				ois,
 				context.getFactory(),
-		        interceptor,
+				interceptor,
 		        context.getConnectionReleaseMode(),
 		        jdbcContext
 		);
 		return jdbcContext;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
index d540c01cbf..a4c0249342 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
@@ -1,218 +1,317 @@
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
 
+import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
+import java.util.ConcurrentModificationException;
 import java.util.HashMap;
 import java.util.HashSet;
+import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
+import org.hibernate.engine.jdbc.spi.JdbcWrapper;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.InvalidatableWrapper;
+import org.hibernate.jdbc.Batcher;
+import org.hibernate.jdbc.BatcherFactory;
 
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
+	private final Batcher batcher;
 
-	public JdbcResourceRegistryImpl(SQLExceptionHelper exceptionHelper) {
+	private Statement lastQuery;
+
+	public JdbcResourceRegistryImpl(SQLExceptionHelper exceptionHelper, BatcherFactory batcherFactory) {
 		this.exceptionHelper = exceptionHelper;
+		this.batcher = batcherFactory.createBatcher( exceptionHelper );
 	}
 
 	public void register(Statement statement) {
 		log.trace( "registering statement [" + statement + "]" );
 		if ( xref.containsKey( statement ) ) {
 			throw new HibernateException( "statement already registered with JDBCContainer" );
 		}
 		xref.put( statement, null );
 	}
 
+	public Batcher getBatcher() {
+		return batcher;
+	}
+
+	@SuppressWarnings({ "unchecked" })
+	public void registerLastQuery(Statement statement) {
+		log.trace( "registering last query statement [{}]", statement );		
+		if ( statement instanceof JdbcWrapper ) {
+			JdbcWrapper<Statement> wrapper = ( JdbcWrapper<Statement> ) statement;
+			registerLastQuery( wrapper.getWrappedObject() );
+			return;
+		}
+		lastQuery = statement;
+	}
+
+	public void cancelLastQuery() {
+		try {
+			if (lastQuery != null) {
+				lastQuery.cancel();
+			}
+		}
+		catch (SQLException sqle) {
+			throw exceptionHelper.convert(
+			        sqle,
+			        "Cannot cancel query"
+				);
+		}
+		finally {
+			lastQuery = null;
+		}
+	}
+
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
-		return ! ( xref.isEmpty() && unassociatedResultSets.isEmpty() );
+		return ! xref.isEmpty() || ! unassociatedResultSets.isEmpty();
 	}
 
 	public void releaseResources() {
 		log.trace( "releasing JDBC container resources [{}]", this );
 		cleanup();
 	}
 
 	private void cleanup() {
+		batcher.closeStatements();		
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
+
+		// TODO: can ConcurrentModificationException still happen???
+		// Following is from old AbstractBatcher...
+		/*
+		Iterator iter = resultSetsToClose.iterator();
+		while ( iter.hasNext() ) {
+			try {
+				logCloseResults();
+				( ( ResultSet ) iter.next() ).close();
+			}
+			catch ( SQLException e ) {
+				// no big deal
+				log.warn( "Could not close a JDBC result set", e );
+			}
+			catch ( ConcurrentModificationException e ) {
+				// this has been shown to happen occasionally in rare cases
+				// when using a transaction manager + transaction-timeout
+				// where the timeout calls back through Hibernate's
+				// registered transaction synchronization on a separate
+				// "reaping" thread.  In cases where that reaping thread
+				// executes through this block at the same time the main
+				// application thread does we can get into situations where
+				// these CMEs occur.  And though it is not "allowed" per-se,
+				// the end result without handling it specifically is infinite
+				// looping.  So here, we simply break the loop
+				log.info( "encountered CME attempting to release batcher; assuming cause is tx-timeout scenario and ignoring" );
+				break;
+			}
+			catch ( Throwable e ) {
+				// sybase driver (jConnect) throwing NPE here in certain
+				// cases, but we'll just handle the general "unexpected" case
+				log.warn( "Could not close a JDBC result set", e );
+			}
+		}
+		resultSetsToClose.clear();
+
+		iter = statementsToClose.iterator();
+		while ( iter.hasNext() ) {
+			try {
+				closeQueryStatement( ( PreparedStatement ) iter.next() );
+			}
+			catch ( ConcurrentModificationException e ) {
+				// see explanation above...
+				log.info( "encountered CME attempting to release batcher; assuming cause is tx-timeout scenario and ignoring" );
+				break;
+			}
+			catch ( SQLException e ) {
+				// no big deal
+				log.warn( "Could not close a JDBC statement", e );
+			}
+		}
+		statementsToClose.clear();
+        */
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
+			if ( lastQuery == statement ) {
+				lastQuery = null;
+			}
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
index 64ebcd0635..90c3bed0e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
@@ -1,406 +1,489 @@
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
+import org.hibernate.jdbc.Batcher;
+import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.jdbc.BorrowedConnectionProxy;
+import org.hibernate.stat.StatisticsImplementor;
 
 /**
  * LogicalConnectionImpl implementation
  *
  * @author Steve Ebersole
  */
 public class LogicalConnectionImpl implements LogicalConnectionImplementor {
 	private static final Logger log = LoggerFactory.getLogger( LogicalConnectionImpl.class );
 
-	private transient Connection physicalConnection;
-	private transient Connection borrowedConnection;
+	private Connection physicalConnection;
+	private Connection borrowedConnection;
 
 	private final ConnectionReleaseMode connectionReleaseMode;
 	private final JdbcServices jdbcServices;
-	private final JdbcResourceRegistry jdbcResourceRegistry;
+	private final StatisticsImplementor statisticsImplementor;
+	private final JdbcResourceRegistryImpl jdbcResourceRegistry;
 	private final List<ConnectionObserver> observers = new ArrayList<ConnectionObserver>();
+
 	private boolean releasesEnabled = true;
+	private long transactionTimeout = -1;
+	boolean isTransactionTimeoutSet;
 
 	private final boolean isUserSuppliedConnection;
+
 	private boolean isClosed;
 
-	public LogicalConnectionImpl(
-	        Connection userSuppliedConnection,
-	        ConnectionReleaseMode connectionReleaseMode,
-	        JdbcServices jdbcServices) {
+	public LogicalConnectionImpl(Connection userSuppliedConnection,
+								 ConnectionReleaseMode connectionReleaseMode,
+								 JdbcServices jdbcServices,
+								 StatisticsImplementor statisticsImplementor,
+								 BatcherFactory batcherFactory
+	) {
+		this.jdbcServices = jdbcServices;
+		this.statisticsImplementor = statisticsImplementor;
 		this.physicalConnection = userSuppliedConnection;
 		this.connectionReleaseMode =
 				determineConnectionReleaseMode(
-						userSuppliedConnection != null, connectionReleaseMode, jdbcServices
+						jdbcServices, userSuppliedConnection != null, connectionReleaseMode
+				);
+		this.jdbcResourceRegistry =
+				new JdbcResourceRegistryImpl(
+						getJdbcServices().getSqlExceptionHelper(),
+						batcherFactory
 				);
-		this.jdbcServices = jdbcServices;
-		this.jdbcResourceRegistry = new JdbcResourceRegistryImpl( jdbcServices.getSqlExceptionHelper() );
 
 		this.isUserSuppliedConnection = ( userSuppliedConnection != null );
+		this.isClosed = false;
 	}
 
-	public LogicalConnectionImpl(
-			ConnectionReleaseMode connectionReleaseMode,
-			JdbcServices jdbcServices,
-			boolean isUserSuppliedConnection,
-			boolean isClosed) {
+	// used for deserialization
+	private LogicalConnectionImpl(ConnectionReleaseMode connectionReleaseMode,
+								  JdbcServices jdbcServices,
+								  StatisticsImplementor statisticsImplementor,
+								  BatcherFactory batcherFactory,
+								  boolean isUserSuppliedConnection,
+								  boolean isClosed) {
 		this.connectionReleaseMode = determineConnectionReleaseMode(
-				isUserSuppliedConnection, connectionReleaseMode, jdbcServices
+				jdbcServices, isUserSuppliedConnection, connectionReleaseMode
 		);
 		this.jdbcServices = jdbcServices;
-		this.jdbcResourceRegistry = new JdbcResourceRegistryImpl( jdbcServices.getSqlExceptionHelper() );
+		this.statisticsImplementor = statisticsImplementor;
+		this.jdbcResourceRegistry =
+				new JdbcResourceRegistryImpl(
+						getJdbcServices().getSqlExceptionHelper(),
+						batcherFactory
+				);
+
 		this.isUserSuppliedConnection = isUserSuppliedConnection;
 		this.isClosed = isClosed;
 	}
 
-	private static ConnectionReleaseMode determineConnectionReleaseMode(boolean isUserSuppliedConnection,
-																		ConnectionReleaseMode connectionReleaseMode,
-																		JdbcServices jdbcServices) {
+	private static ConnectionReleaseMode determineConnectionReleaseMode(JdbcServices jdbcServices,
+																		boolean isUserSuppliedConnection,
+																		ConnectionReleaseMode connectionReleaseMode) {
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
+	 * Set the transaction timeout to <tt>seconds</tt> later
+	 * than the current system time.
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
+	private void unsetTransactionTimeout() {
+		isTransactionTimeoutSet = false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isTransactionTimeoutSet() {
+		return isTransactionTimeoutSet;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public long getTransactionTimeout() throws HibernateException {
+		if ( isTransactionTimeoutSet ) {
+			throw new HibernateException( "transaction timeout has not been set." );
+		}
+		return transactionTimeout;
+	}
+
+	/**
 	 * {@inheritDoc}
 	 */
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	public StatisticsImplementor getStatisticsImplementor() {
+		return statisticsImplementor;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
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
+	public boolean isLogicallyConnected() {
+		return isUserSuppliedConnection ?
+				isPhysicallyConnected() :
+				isOpen();
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
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
 
-	public boolean isUserSuppliedConnection() {
-		return isUserSuppliedConnection;
+	public Batcher getBatcher() {
+		return jdbcResourceRegistry.getBatcher();
 	}
 
 	public boolean hasBorrowedConnection() {
 		return borrowedConnection != null;
 	}
 
 	public Connection borrowConnection() {
 		if ( isClosed ) {
 			throw new HibernateException( "connection has been closed" );
 		}
-		if ( isUserSuppliedConnection() ) {
+		if ( isUserSuppliedConnection ) {
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
+			else if ( borrowedConnection != null ) {
+				log.debug( "skipping aggresive-release due to borrowed connection" );
+			}			
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
+		unsetTransactionTimeout();
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
+		jdbcResourceRegistry.releaseResources();
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
-		return isUserSuppliedConnection() ?
+		return isUserSuppliedConnection ?
 				! isPhysicallyConnected() :
 				! getResourceRegistry().hasRegisteredResources()
 				;
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeBoolean( isUserSuppliedConnection );
 		oos.writeBoolean( isClosed );
 	}
 
-	public static LogicalConnectionImpl deserialize(
-			ObjectInputStream ois,
-			JdbcServices jdbcServices,
-			ConnectionReleaseMode connectionReleaseMode	) throws IOException {
+	public static LogicalConnectionImpl deserialize(ObjectInputStream ois,
+													JdbcServices jdbcServices,
+													StatisticsImplementor statisticsImplementor,
+													ConnectionReleaseMode connectionReleaseMode,
+													BatcherFactory batcherFactory
+	) throws IOException {
 		return new LogicalConnectionImpl(
 				connectionReleaseMode,
 				jdbcServices,
+				statisticsImplementor,
+				batcherFactory,
 				ois.readBoolean(),
 				ois.readBoolean()
 		);
  	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java
index 5e7c424b5a..18e6773770 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java
@@ -1,168 +1,168 @@
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
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.sql.Connection;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 
 /**
  * Basic support for building {@link Statement}-based proxy handlers
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStatementProxyHandler extends AbstractProxyHandler {
 	private static final Logger log = LoggerFactory.getLogger( AbstractStatementProxyHandler.class );
 
 	private ConnectionProxyHandler connectionProxyHandler;
 	private Connection connectionProxy;
 	private Statement statement;
 
 	protected AbstractStatementProxyHandler(
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		super( statement.hashCode() );
 		this.statement = statement;
 		this.connectionProxyHandler = connectionProxyHandler;
 		this.connectionProxy = connectionProxy;
 	}
 
 	protected ConnectionProxyHandler getConnectionProxy() {
 		errorIfInvalid();
 		return connectionProxyHandler;
 	}
 
 	protected JdbcServices getJdbcServices() {
 		return getConnectionProxy().getJdbcServices();
 	}
 
 	protected JdbcResourceRegistry getResourceRegistry() {
 		return getConnectionProxy().getResourceRegistry();
 	}
 
 	protected Statement getStatement() {
 		errorIfInvalid();
 		return statement;
 	}
 
 	protected Statement getStatementWithoutChecks() {
 		return statement;
 	}
 
 	protected Object continueInvocation(Object proxy, Method method, Object[] args) throws Throwable {
 		String methodName = method.getName();
 		log.trace( "Handling invocation of statement method [{}]", methodName );
 
 		// other methods allowed while invalid ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( "close".equals( methodName ) ) {
 			explicitClose( ( Statement ) proxy );
 			return null;
 		}
 		if ( "invalidate".equals( methodName ) ) {
 			invalidateHandle();
 			return null;
 		}
 
 		errorIfInvalid();
 
 		// handle the JDBC 4 Wrapper#isWrapperFor and Wrapper#unwrap calls
 		//		these cause problems to the whole proxy scheme though as we need to return the raw objects
 		if ( "isWrapperFor".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( getStatementWithoutChecks(), args );
 		}
 		if ( "unwrap".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( getStatementWithoutChecks(), args );
 		}
 
 		if ( "getWrappedObject".equals( methodName ) ) {
 			return getStatementWithoutChecks();
 		}
 
 		if ( "getConnection".equals( methodName ) ) {
 			return connectionProxy;
 		}
 
 		beginningInvocationHandling( method, args );
 
 		try {
 			Object result = method.invoke( statement, args );
 			result = wrapIfNecessary( result, proxy, method );
 			return result;
 		}
 		catch ( InvocationTargetException e ) {
 			Throwable realException = e.getTargetException();
 			if ( SQLException.class.isInstance( realException ) ) {
 				throw connectionProxyHandler.getJdbcServices().getSqlExceptionHelper()
 						.convert( ( SQLException ) realException, realException.getMessage() );
 			}
 			else {
 				throw realException;
 			}
 		}
 	}
 
 	private Object wrapIfNecessary(Object result, Object proxy, Method method) {
 		if ( !( ResultSet.class.isAssignableFrom( method.getReturnType() ) ) ) {
 			return result;
 		}
 
 		final ResultSet wrapper;
 		if ( "getGeneratedKeys".equals( method.getName() ) ) {
-			wrapper = ProxyBuilder.buildImplicitResultSet( ( ResultSet ) result, connectionProxyHandler, connectionProxy );
+			wrapper = ProxyBuilder.buildImplicitResultSet( ( ResultSet ) result, connectionProxyHandler, connectionProxy, ( Statement ) proxy );
 		}
 		else {
 			wrapper = ProxyBuilder.buildResultSet( ( ResultSet ) result, this, ( Statement ) proxy );
 		}
 		getResourceRegistry().register( wrapper );
 		return wrapper;
 	}
 
 	protected void beginningInvocationHandling(Method method, Object[] args) {
 	}
 
 	private void explicitClose(Statement proxy) {
 		if ( isValid() ) {
 			LogicalConnectionImplementor lc = getConnectionProxy().getLogicalConnection();
 			getResourceRegistry().release( proxy );
 			lc.afterStatementExecution();
 		}
 	}
 
 	private void invalidateHandle() {
 		connectionProxyHandler = null;
 		statement = null;
 		invalidate();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/CallableStatementProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/CallableStatementProxyHandler.java
new file mode 100644
index 0000000000..62af047056
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/CallableStatementProxyHandler.java
@@ -0,0 +1,65 @@
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
+package org.hibernate.engine.jdbc.internal.proxy;
+
+import java.lang.reflect.InvocationTargetException;
+import java.lang.reflect.Method;
+import java.sql.CallableStatement;
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.sql.Statement;
+
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
+/**
+ * Invocation handler for {@link java.sql.CallableStatement} proxies
+ *
+ * @author Gail Badner
+ */
+public class CallableStatementProxyHandler extends PreparedStatementProxyHandler {
+
+	private static final Logger log = LoggerFactory.getLogger( CallableStatementProxyHandler.class );
+
+	protected CallableStatementProxyHandler(
+			String sql,
+			Statement statement,
+			ConnectionProxyHandler connectionProxyHandler,
+			Connection connectionProxy) {
+		super( sql, statement, connectionProxyHandler, connectionProxy );
+	}
+
+	protected Object continueInvocation(Object proxy, Method method, Object[] args) throws Throwable {
+		if ( ! "executeQuery".equals( method.getName() ) ) {
+			return super.continueInvocation( proxy, method, args ); // EARLY RETURN!
+		}
+		errorIfInvalid();
+		return executeQuery();
+	}
+
+	private Object executeQuery() throws SQLException {
+		return getConnectionProxy().getJdbcServices().getDialect().getResultSet( ( CallableStatement ) this );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java
index a0099f7afe..48cbac9c27 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ConnectionProxyHandler.java
@@ -1,213 +1,246 @@
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
 
+import org.hibernate.TransactionException;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
+import org.hibernate.stat.StatisticsImplementor;
 
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
-			result = wrapIfNecessary( result, proxy, method, args );
+			result = postProcess( result, proxy, method, args );
 
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
 
-	private Object wrapIfNecessary(Object result, Object proxy, Method method, Object[] args) {
+	private Object postProcess(Object result, Object proxy, Method method, Object[] args) throws SQLException {
 		String methodName = method.getName();
 		Object wrapped = result;
 		if ( "createStatement".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildStatement(
 					(Statement) result,
 					this,
 					( Connection ) proxy
 			);
-			getResourceRegistry().register( ( Statement ) wrapped );
+			postProcessStatement( ( Statement ) wrapped );
 		}
 		else if ( "prepareStatement".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildPreparedStatement(
 					( String ) args[0],
 					(PreparedStatement) result,
 					this,
 					( Connection ) proxy
 			);
-			getResourceRegistry().register( ( Statement ) wrapped );
+			postProcessPreparedStatement( ( Statement ) wrapped );
 		}
 		else if ( "prepareCall".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildCallableStatement(
 					( String ) args[0],
 					(CallableStatement) result,
 					this,
 					( Connection ) proxy
 			);
-			getResourceRegistry().register( ( Statement ) wrapped );
+			postProcessPreparedStatement( ( Statement ) wrapped );
 		}
 		else if ( "getMetaData".equals( methodName ) ) {
 			wrapped = ProxyBuilder.buildDatabaseMetaData( (DatabaseMetaData) result, this, ( Connection ) proxy );
 		}
 		return wrapped;
 	}
 
+	private void postProcessStatement(Statement statement) throws SQLException {
+		setTimeout( statement );
+		getResourceRegistry().register( statement );
+	}
+
+	private void postProcessPreparedStatement(Statement statement) throws SQLException  {
+		if ( getStatisticsImplementorOrNull() != null ) {
+			getStatisticsImplementorOrNull().prepareStatement();
+		}
+		postProcessStatement( statement );
+	}
+
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
+
+	/* package-protected */
+	StatisticsImplementor getStatisticsImplementorOrNull() {
+		return getLogicalConnection().getStatisticsImplementor();
+	}
+
+	private void setTimeout(Statement result) throws SQLException {
+		if ( logicalConnection.isTransactionTimeoutSet() ) {
+			int timeout = (int) ( logicalConnection.getTransactionTimeout() - ( System.currentTimeMillis() / 1000 ) );
+			if (timeout<=0) {
+				throw new TransactionException("transaction timeout expired");
+			}
+			else {
+				result.setQueryTimeout(timeout);
+			}
+		}
+	}
+
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ImplicitResultSetProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ImplicitResultSetProxyHandler.java
index eff472aaa2..719ce25a6f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ImplicitResultSetProxyHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ImplicitResultSetProxyHandler.java
@@ -1,81 +1,88 @@
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
 
 import java.sql.Connection;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 
 /**
  * Invocation handler for {@link java.sql.ResultSet} proxies obtained from other JDBC object proxies
  *
  * @author Steve Ebersole
  */
 public class ImplicitResultSetProxyHandler extends AbstractResultSetProxyHandler {
 	private ConnectionProxyHandler connectionProxyHandler;
 	private Connection connectionProxy;
 	private Statement sourceStatement;
 
 	public ImplicitResultSetProxyHandler(ResultSet resultSet, ConnectionProxyHandler connectionProxyHandler, Connection connectionProxy) {
 		super( resultSet );
 		this.connectionProxyHandler = connectionProxyHandler;
 		this.connectionProxy = connectionProxy;
 	}
 
+	public ImplicitResultSetProxyHandler(ResultSet resultSet, ConnectionProxyHandler connectionProxyHandler, Connection connectionProxy, Statement sourceStatement) {
+		super( resultSet );
+		this.connectionProxyHandler = connectionProxyHandler;
+		this.connectionProxy = connectionProxy;
+		this.sourceStatement = sourceStatement;
+	}
+
 	@Override
 	protected JdbcServices getJdbcServices() {
 		return connectionProxyHandler.getJdbcServices();
 	}
 
 	@Override
 	protected JdbcResourceRegistry getResourceRegistry() {
 		return connectionProxyHandler.getResourceRegistry();
 	}
 
 	@Override
 	protected Statement getExposableStatement() {
 		if ( sourceStatement == null ) {
 			try {
 				Statement stmnt = getResultSet().getStatement();
 				if ( stmnt == null ) {
 					return null;
 				}
 				sourceStatement = ProxyBuilder.buildImplicitStatement( stmnt, connectionProxyHandler, connectionProxy );
 			}
 			catch ( SQLException e ) {
 				throw getJdbcServices().getSqlExceptionHelper().convert( e, e.getMessage() );
 			}
 		}
 		return sourceStatement;
 	}
 
 	protected void invalidateHandle() {
 		sourceStatement = null;
 		super.invalidateHandle();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java
index 7401b790dd..de2f27592f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java
@@ -1,208 +1,222 @@
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
 
 import java.lang.reflect.Proxy;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.Statement;
 
+import org.hibernate.Interceptor;
 import org.hibernate.engine.jdbc.spi.JdbcWrapper;
 import org.hibernate.engine.jdbc.spi.InvalidatableWrapper;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 
 /**
  * Centralized builder for proxy instances
  *
  * @author Steve Ebersole
  */
 public class ProxyBuilder {
 
 	// Connection ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] CONNECTION_PROXY_INTERFACES = new Class[] {
 			Connection.class,
 			JdbcWrapper.class
 	};
 
 	public static Connection buildConnection(LogicalConnectionImplementor logicalConnection) {
 		ConnectionProxyHandler proxyHandler = new ConnectionProxyHandler( logicalConnection );
 		return ( Connection ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				CONNECTION_PROXY_INTERFACES,
 				proxyHandler
 		);
 	}
 
 
 	// Statement ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] STMNT_PROXY_INTERFACES = new Class[] {
 			Statement.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
 	public static Statement buildStatement(
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		BasicStatementProxyHandler proxyHandler = new BasicStatementProxyHandler(
 				statement,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		return ( Statement ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				STMNT_PROXY_INTERFACES,
 				proxyHandler
 		);
 	}
 
 	public static Statement buildImplicitStatement(
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		if ( statement == null ) {
 			return null;
 		}
 		ImplicitStatementProxyHandler handler = new ImplicitStatementProxyHandler( statement, connectionProxyHandler, connectionProxy );
 		return ( Statement ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				STMNT_PROXY_INTERFACES,
 				handler
 		);
 	}
 
 
 	// PreparedStatement ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] PREPARED_STMNT_PROXY_INTERFACES = new Class[] {
 			PreparedStatement.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
 	public static PreparedStatement buildPreparedStatement(
 			String sql,
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		PreparedStatementProxyHandler proxyHandler = new PreparedStatementProxyHandler(
 				sql,
 				statement,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		return ( PreparedStatement ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				PREPARED_STMNT_PROXY_INTERFACES,
 				proxyHandler
 		);
 	}
 
 
 	// CallableStatement ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] CALLABLE_STMNT_PROXY_INTERFACES = new Class[] {
 			CallableStatement.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
 	public static CallableStatement buildCallableStatement(
 			String sql,
 			CallableStatement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
-		PreparedStatementProxyHandler proxyHandler = new PreparedStatementProxyHandler(
+		CallableStatementProxyHandler proxyHandler = new CallableStatementProxyHandler(
 				sql,
 				statement,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		return ( CallableStatement ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				CALLABLE_STMNT_PROXY_INTERFACES,
 				proxyHandler
 		);
 	}
 
 
 	// ResultSet ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] RESULTSET_PROXY_INTERFACES = new Class[] {
 			ResultSet.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
 
 	public static ResultSet buildResultSet(
 			ResultSet resultSet,
 			AbstractStatementProxyHandler statementProxyHandler,
 			Statement statementProxy) {
 		ResultSetProxyHandler proxyHandler = new ResultSetProxyHandler( resultSet, statementProxyHandler, statementProxy );
 		return ( ResultSet ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				RESULTSET_PROXY_INTERFACES,
 				proxyHandler
 		);
 	}
 
 	public static ResultSet buildImplicitResultSet(
 			ResultSet resultSet,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		ImplicitResultSetProxyHandler proxyHandler = new ImplicitResultSetProxyHandler( resultSet, connectionProxyHandler, connectionProxy );
 		return ( ResultSet ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				RESULTSET_PROXY_INTERFACES,
 				proxyHandler
 		);
 	}
 
+	public static ResultSet buildImplicitResultSet(
+			ResultSet resultSet,
+			ConnectionProxyHandler connectionProxyHandler,
+			Connection connectionProxy,
+			Statement sourceStatement) {
+		ImplicitResultSetProxyHandler proxyHandler = new ImplicitResultSetProxyHandler( resultSet, connectionProxyHandler, connectionProxy, sourceStatement );
+		return ( ResultSet ) Proxy.newProxyInstance(
+				JdbcWrapper.class.getClassLoader(),
+				RESULTSET_PROXY_INTERFACES,
+				proxyHandler
+		);
+	}
+
 
 	// DatabaseMetaData ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] METADATA_PROXY_INTERFACES = new Class[] {
 			DatabaseMetaData.class,
 			JdbcWrapper.class
 	};
 
 	public static DatabaseMetaData buildDatabaseMetaData(
 			DatabaseMetaData metaData,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		DatabaseMetaDataProxyHandler handler = new DatabaseMetaDataProxyHandler( metaData, connectionProxyHandler, connectionProxy );
 		return ( DatabaseMetaData ) Proxy.newProxyInstance(
 				JdbcWrapper.class.getClassLoader(),
 				METADATA_PROXY_INTERFACES,
 				handler
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
new file mode 100644
index 0000000000..728a4b0966
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
@@ -0,0 +1,195 @@
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
+package org.hibernate.engine.jdbc.spi;
+
+import java.io.Serializable;
+import java.sql.CallableStatement;
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+
+import org.hibernate.HibernateException;
+import org.hibernate.ScrollMode;
+import org.hibernate.jdbc.Expectation;
+
+/**
+ * Encapsulates JDBC Connection management SPI.
+ * <p/>
+ * The lifecycle is intended to span a logical series of interactions with the
+ * database.  Internally, this means the the lifecycle of the Session.
+ *
+ * @author Gail Badner
+ */
+public interface ConnectionManager extends Serializable {
+
+	/**
+	 * Retrieves the connection currently managed by this ConnectionManager.
+	 * <p/>
+	 * Note, that we may need to obtain a connection to return here if a
+	 * connection has either not yet been obtained (non-UserSuppliedConnectionProvider)
+	 * or has previously been aggressively released (if supported in this environment).
+	 *
+	 * @return The current Connection.
+	 *
+	 * @throws HibernateException Indicates a connection is currently not
+	 * available (we are currently manually disconnected).
+	 */
+	Connection getConnection() throws HibernateException;
+
+	// TODO: should this be removd from the SPI?
+	boolean hasBorrowedConnection();
+
+	// TODO: should this be removd from the SPI?
+	void releaseBorrowedConnection();
+
+	/**
+	 * Is this ConnectionManager instance "logically" connected.  Meaning
+	 * do we either have a cached connection available or do we have the
+	 * ability to obtain a connection on demand.
+	 *
+	 * @return True if logically connected; false otherwise.
+	 */
+	boolean isCurrentlyConnected();
+
+	/**
+	 * To be called after execution of each JDBC statement.  Used to
+	 * conditionally release the JDBC connection aggressively if
+	 * the configured release mode indicates.
+	 */
+	void afterStatement();
+
+	void setTransactionTimeout(int seconds);
+
+	/**
+	 * To be called after Session completion.  Used to release the JDBC
+	 * connection.
+	 *
+	 * @return The connection mantained here at time of close.  Null if
+	 * there was no connection cached internally.
+	 */
+	Connection close();
+
+	/**
+	 * Manually disconnect the underlying JDBC Connection.  The assumption here
+	 * is that the manager will be reconnected at a later point in time.
+	 *
+	 * @return The connection mantained here at time of disconnect.  Null if
+	 * there was no connection cached internally.
+	 */
+	Connection manualDisconnect();
+
+	/**
+	 * Manually reconnect the underlying JDBC Connection.  Should be called at
+	 * some point after manualDisconnect().
+	 * <p/>
+	 * This form is used for ConnectionProvider-supplied connections.
+	 */
+	void manualReconnect();
+
+	/**
+	 * Manually reconnect the underlying JDBC Connection.  Should be called at
+	 * some point after manualDisconnect().
+	 * <p/>
+	 * This form is used for user-supplied connections.
+	 */
+	void manualReconnect(Connection suppliedConnection);
+
+	/**
+	 * Callback to let us know that a flush is beginning.  We use this fact
+	 * to temporarily circumvent aggressive connection releasing until after
+	 * the flush cycle is complete {@link #flushEnding()}
+	 */
+	void flushBeginning();
+
+	/**
+	 * Callback to let us know that a flush is ending.  We use this fact to
+	 * stop circumventing aggressive releasing connections.
+	 */
+	void flushEnding();
+
+	/**
+	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
+	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, int)}).
+	 */
+	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
+			throws SQLException, HibernateException;
+
+	/**
+	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
+	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
+	 */
+	public PreparedStatement prepareStatement(String sql, String[] columnNames)
+			throws SQLException, HibernateException;
+
+	/**
+	 * Get a non-batchable prepared statement to use for selecting. Does not
+	 * result in execution of the current batch.
+	 */
+	public PreparedStatement prepareSelectStatement(String sql)
+			throws SQLException, HibernateException;
+
+	/**
+	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
+	 */
+	public PreparedStatement prepareStatement(String sql, boolean isCallable)
+	throws SQLException, HibernateException ;
+
+	/**
+	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
+	 */
+	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException;
+
+	/**
+	 * Get a batchable prepared statement to use for inserting / deleting / updating
+	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
+	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
+	 * statement explicitly.
+	 * @see org.hibernate.jdbc.Batcher#addToBatch
+	 */
+	public PreparedStatement prepareBatchStatement(String sql, boolean isCallable) throws SQLException, HibernateException;
+
+	/**
+	 * Get a prepared statement for use in loading / querying. If not explicitly
+	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
+	 * session is closed or disconnected.
+	 */
+	public PreparedStatement prepareQueryStatement(
+			String sql,
+			boolean isCallable) throws SQLException, HibernateException;
+	/**
+	 * Cancel the current query statement
+	 */
+	public void cancelLastQuery() throws HibernateException;
+
+	public PreparedStatement prepareScrollableQueryStatement(
+			String sql,
+	        ScrollMode scrollMode,
+			boolean isCallable) throws SQLException, HibernateException;
+
+	public void abortBatch(SQLException sqle);
+
+	public void addToBatch(Expectation expectation )  throws SQLException, HibernateException;
+
+	public void executeBatch() throws HibernateException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JDBCContext.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JDBCContext.java
new file mode 100644
index 0000000000..4e177d44a0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JDBCContext.java
@@ -0,0 +1,83 @@
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
+package org.hibernate.engine.jdbc.spi;
+
+import java.io.Serializable;
+import java.sql.Connection;
+
+import org.hibernate.ConnectionReleaseMode;
+import org.hibernate.HibernateException;
+import org.hibernate.Transaction;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.transaction.TransactionFactory;
+import org.hibernate.transaction.synchronization.CallbackCoordinator;
+
+/**
+ * Acts as the SPI for the mediary between "entity-mode related" sessions in terms of
+ * their interaction with the JDBC data store.
+ *
+ * @author Gail Badner
+ */
+public interface JDBCContext extends Serializable {
+
+	// TODO: Document these methods...
+
+	CallbackCoordinator getJtaSynchronizationCallbackCoordinator();
+
+	void cleanUpJtaSynchronizationCallbackCoordinator();
+
+	SessionFactoryImplementor getFactory();
+
+	ConnectionManager getConnectionManager();
+
+	Connection connection() throws HibernateException;
+
+	boolean registerCallbackIfNecessary();
+
+	boolean registerSynchronizationIfPossible();
+
+	boolean isTransactionInProgress();
+
+	Transaction getTransaction() throws HibernateException;
+
+	void beforeTransactionCompletion(Transaction tx);
+
+	void afterTransactionBegin(Transaction tx);
+
+	void afterTransactionCompletion(boolean success, Transaction tx);
+
+	void afterNontransactionalQuery(boolean success);
+
+	public static interface Context extends TransactionFactory.Context {
+		/**
+		 * We cannot rely upon this method being called! It is only
+		 * called if we are using Hibernate Transaction API.
+		 */
+		public void afterTransactionBegin(Transaction tx);
+		public void beforeTransactionCompletion(Transaction tx);
+		public void afterTransactionCompletion(boolean success, Transaction tx);
+		public ConnectionReleaseMode getConnectionReleaseMode();
+		public boolean isAutoCloseSessionEnabled();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcResourceRegistry.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcResourceRegistry.java
index b37be3e8ef..078c6d48ec 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcResourceRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcResourceRegistry.java
@@ -1,81 +1,85 @@
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
 
 import java.sql.ResultSet;
 import java.sql.Statement;
 
 /**
  * Defines a registry of JDBC resources related to a particular unit of work.
  *
  * @author Steve Ebersole
  */
 public interface JdbcResourceRegistry {
 	/**
 	 * Register a JDBC statement.
 	 *
 	 * @param statement The statement to register.
 	 */
 	public void register(Statement statement);
 
+	public void registerLastQuery(Statement statement);
+
+	public void cancelLastQuery();
+	
 	/**
 	 * Release a previously registered statement.
 	 *
 	 * @param statement The statement to release.
 	 */
 	public void release(Statement statement);
 
 	/**
 	 * Register a JDBC result set.
 	 *
 	 * @param resultSet The result set to register.
 	 */
 	public void register(ResultSet resultSet);
 
 	/**
 	 * Release a previously registered result set.
 	 *
 	 * @param resultSet The result set to release.
 	 */
 	public void release(ResultSet resultSet);
 
 	/**
 	 * Does this registry currently have any registered resources?
 	 *
 	 * @return True if the registry does have registered resources; false otherwise.
 	 */
 	public boolean hasRegisteredResources();
 
 	/**
 	 * Release all registered resources.
 	 */
 	public void releaseResources();
 
 	/**
 	 * Close this registry.  Also {@link #releaseResources releases} any registered resources.
 	 * <p/>
 	 * After execution, the registry is considered unusable.
 	 */
 	public void close();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
index fc17909ff3..1c54e3f6aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
@@ -1,71 +1,71 @@
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
 import java.sql.Connection;
 
 /**
  * LogicalConnection contract
  *
  * @author Steve Ebersole
  */
-public interface LogicalConnection extends Serializable {
+public interface LogicalConnection {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
index a3c1ad0d48..a843e12c28 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnectionImplementor.java
@@ -1,85 +1,109 @@
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
+import org.hibernate.stat.StatisticsImplementor;
 
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
+	 * Obtains the statistics implementor.
+	 *
+	 * @return the statistics implementor
+	 */
+	public StatisticsImplementor getStatisticsImplementor();
+
+	/**
+	 * Is the transaction timeout set?
+	 *
+	 * @return true, if the transaction timeout is set; false otherwise
+	 */
+	public boolean isTransactionTimeoutSet();
+
+	/**
+	 * Gets the transaction timeout.
+	 *
+	 * @return the transaction time out
+	 */
+	public long getTransactionTimeout();
+
+	/**
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
+
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
index 3b7ceb9156..e930ef4ff4 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
@@ -1,226 +1,225 @@
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
 package org.hibernate.engine.query;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.action.BulkOperationCleanupAction;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.event.EventSource;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.loader.custom.sql.SQLCustomQuery;
 import org.hibernate.type.Type;
 import org.hibernate.util.ArrayHelper;
 
 /**
  * Defines a query execution plan for a native-SQL query.
  *
  * @author Steve Ebersole
  */
 public class NativeSQLQueryPlan implements Serializable {
 	private final String sourceQuery;
 
 	private final SQLCustomQuery customQuery;
 
 	private static final Logger log = LoggerFactory.getLogger(NativeSQLQueryPlan.class);
 
 	public NativeSQLQueryPlan(
 			NativeSQLQuerySpecification specification,
 			SessionFactoryImplementor factory) {
 		this.sourceQuery = specification.getQueryString();
 
 		customQuery = new SQLCustomQuery(
 				specification.getQueryString(),
 				specification.getQueryReturns(),
 				specification.getQuerySpaces(),
 				factory );
 	}
 
 	public String getSourceQuery() {
 		return sourceQuery;
 	}
 
 	public SQLCustomQuery getCustomQuery() {
 		return customQuery;
 	}
 
 	private int[] getNamedParameterLocs(String name) throws QueryException {
 		Object loc = customQuery.getNamedParameterBindPoints().get( name );
 		if ( loc == null ) {
 			throw new QueryException(
 					"Named parameter does not appear in Query: " + name,
 					customQuery.getSQL() );
 		}
 		if ( loc instanceof Integer ) {
 			return new int[] { ((Integer) loc ).intValue() };
 		}
 		else {
 			return ArrayHelper.toIntArray( (List) loc );
 		}
 	}
 
 	/**
 	 * Perform binding of all the JDBC bind parameter values based on the user-defined
 	 * positional query parameters (these are the '?'-style hibernate query
 	 * params) into the JDBC {@link PreparedStatement}.
 	 *
 	 * @param st The prepared statement to which to bind the parameter values.
 	 * @param queryParameters The query parameters specified by the application.
 	 * @param start JDBC paramer binds are positional, so this is the position
 	 * from which to start binding.
 	 * @param session The session from which the query originated.
 	 *
 	 * @return The number of JDBC bind positions accounted for during execution.
 	 *
 	 * @throws SQLException Some form of JDBC error binding the values.
 	 * @throws HibernateException Generally indicates a mapping problem or type mismatch.
 	 */
 	private int bindPositionalParameters(
 			final PreparedStatement st,
 			final QueryParameters queryParameters,
 			final int start,
 			final SessionImplementor session) throws SQLException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for (int i = 0; i < values.length; i++) {
 			types[i].nullSafeSet( st, values[i], start + span, session );
 			span += types[i].getColumnSpan( session.getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Perform binding of all the JDBC bind parameter values based on the user-defined
 	 * named query parameters into the JDBC {@link PreparedStatement}.
 	 *
 	 * @param ps The prepared statement to which to bind the parameter values.
 	 * @param namedParams The named query parameters specified by the application.
 	 * @param start JDBC paramer binds are positional, so this is the position
 	 * from which to start binding.
 	 * @param session The session from which the query originated.
 	 *
 	 * @return The number of JDBC bind positions accounted for during execution.
 	 *
 	 * @throws SQLException Some form of JDBC error binding the values.
 	 * @throws HibernateException Generally indicates a mapping problem or type mismatch.
 	 */
 	private int bindNamedParameters(
 			final PreparedStatement ps,
 			final Map namedParams,
 			final int start,
 			final SessionImplementor session) throws SQLException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = (Map.Entry) iter.next();
 				String name = (String) e.getKey();
 				TypedValue typedval = (TypedValue) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for (int i = 0; i < locs.length; i++) {
 					if ( log.isDebugEnabled() ) {
 						log.debug( "bindNamedParameters() "
 								+ typedval.getValue() + " -> " + name + " ["
 								+ (locs[i] + start ) + "]" );
 					}
 					typedval.getType().nullSafeSet( ps, typedval.getValue(),
 							locs[i] + start, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	protected void coordinateSharedCacheCleanup(SessionImplementor session) {
 		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, getCustomQuery().getQuerySpaces() );
 
 		if ( session.isEventSource() ) {
 			( ( EventSource ) session ).getActionQueue().addAction( action );
 		}
 		else {
 			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
 	}
 
 	public int performExecuteUpdate(QueryParameters queryParameters,
 			SessionImplementor session) throws HibernateException {
 
 		coordinateSharedCacheCleanup( session );
 
 		if(queryParameters.isCallable()) {
 			throw new IllegalArgumentException("callable not yet supported for native queries");
 		}
 
 		int result = 0;
 		PreparedStatement ps;
 		try {
 			queryParameters.processFilters( this.customQuery.getSQL(),
 					session );
 			String sql = queryParameters.getFilteredSQL();
 
-			ps = session.getBatcher().prepareStatement( sql );
+			ps = session.getJDBCContext().getConnectionManager().prepareStatement( sql, false );
 
 			try {
 				int col = 1;
 				col += bindPositionalParameters( ps, queryParameters, col,
 						session );
 				col += bindNamedParameters( ps, queryParameters
 						.getNamedParameters(), col, session );
 				result = ps.executeUpdate();
 			}
 			finally {
 				if ( ps != null ) {
-					session.getBatcher().closeStatement( ps );
+					ps.close();
 				}
 			}
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle, "could not execute native bulk manipulation query", this.sourceQuery );
 		}
 
 		return result;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
index a0c77f26d2..50f486cd0d 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
@@ -1,281 +1,283 @@
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
 package org.hibernate.hql.ast.exec;
 
 import java.sql.PreparedStatement;
 import java.sql.Connection;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 import java.util.List;
 import java.util.Collections;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.BulkOperationCleanupAction;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.transaction.Isolater;
 import org.hibernate.engine.transaction.IsolatedWork;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.ast.HqlSqlWalker;
 import org.hibernate.hql.ast.SqlGenerator;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.InsertSelect;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.util.JDBCExceptionReporter;
 import org.hibernate.util.StringHelper;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Implementation of AbstractStatementExecutor.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStatementExecutor implements StatementExecutor {
 	private static final Logger LOG = LoggerFactory.getLogger( AbstractStatementExecutor.class );
 
 	private final Logger log;
 	private final HqlSqlWalker walker;
 	private List idSelectParameterSpecifications = Collections.EMPTY_LIST;
 
 	public AbstractStatementExecutor(HqlSqlWalker walker, Logger log) {
 		this.walker = walker;
 		this.log = log;
 	}
 
 	protected HqlSqlWalker getWalker() {
 		return walker;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return walker.getSessionFactoryHelper().getFactory();
 	}
 
 	protected List getIdSelectParameterSpecifications() {
 		return idSelectParameterSpecifications;
 	}
 
 	protected abstract Queryable[] getAffectedQueryables();
 
 	protected String generateIdInsertSelect(Queryable persister, String tableAlias, AST whereClause) {
 		Select select = new Select( getFactory().getDialect() );
 		SelectFragment selectFragment = new SelectFragment()
 				.addColumns( tableAlias, persister.getIdentifierColumnNames(), persister.getIdentifierColumnNames() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 
 		String rootTableName = persister.getTableName();
 		String fromJoinFragment = persister.fromJoinFragment( tableAlias, true, false );
 		String whereJoinFragment = persister.whereJoinFragment( tableAlias, true, false );
 
 		select.setFromClause( rootTableName + ' ' + tableAlias + fromJoinFragment );
 
 		if ( whereJoinFragment == null ) {
 			whereJoinFragment = "";
 		}
 		else {
 			whereJoinFragment = whereJoinFragment.trim();
 			if ( whereJoinFragment.startsWith( "and" ) ) {
 				whereJoinFragment = whereJoinFragment.substring( 4 );
 			}
 		}
 
 		String userWhereClause = "";
 		if ( whereClause.getNumberOfChildren() != 0 ) {
 			// If a where clause was specified in the update/delete query, use it to limit the
 			// returned ids here...
 			try {
 				SqlGenerator sqlGenerator = new SqlGenerator( getFactory() );
 				sqlGenerator.whereClause( whereClause );
 				userWhereClause = sqlGenerator.getSQL().substring( 7 );  // strip the " where "
 				idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
 			}
 			catch ( RecognitionException e ) {
 				throw new HibernateException( "Unable to generate id select for DML operation", e );
 			}
 			if ( whereJoinFragment.length() > 0 ) {
 				whereJoinFragment += " and ";
 			}
 		}
 
 		select.setWhereClause( whereJoinFragment + userWhereClause );
 
 		InsertSelect insert = new InsertSelect( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert-select for " + persister.getEntityName() + " ids" );
 		}
 		insert.setTableName( persister.getTemporaryIdTableName() );
 		insert.setSelect( select );
 		return insert.toStatementString();
 	}
 
 	protected String generateIdSubselect(Queryable persister) {
 		return "select " + StringHelper.join( ", ", persister.getIdentifierColumnNames() ) +
 			        " from " + persister.getTemporaryIdTableName();
 	}
 
 	protected void createTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
 		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
 		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
 		IsolatedWork work = new IsolatedWork() {
 			public void doWork(Connection connection) throws HibernateException {
 				try {
 					Statement statement = connection.createStatement();
 					try {
 						statement.executeUpdate( persister.getTemporaryIdTableDDL() );
 						JDBCExceptionReporter.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
 					}
 					finally {
 						try {
 							statement.close();
 						}
 						catch( Throwable ignore ) {
 							// ignore
 						}
 					}
 				}
 				catch( Exception e ) {
 					log.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
 				}
 			}
 		};
 		if ( shouldIsolateTemporaryTableDDL() ) {
 			if ( getFactory().getSettings().isDataDefinitionInTransactionSupported() ) {
 				Isolater.doIsolatedWork( work, session );
 			}
 			else {
 				Isolater.doNonTransactedWork( work, session );
 			}
 		}
 		else {
 			work.doWork( session.getJDBCContext().getConnectionManager().getConnection() );
 			session.getJDBCContext().getConnectionManager().afterStatement();
 		}
 	}
 
 	private static JDBCExceptionReporter.WarningHandler CREATION_WARNING_HANDLER = new JDBCExceptionReporter.WarningHandlerLoggingSupport() {
 		public boolean doProcess() {
 			return LOG.isDebugEnabled();
 		}
 
 		public void prepare(SQLWarning warning) {
 			LOG.debug( "Warnings creating temp table", warning );
 		}
 
 		@Override
 		protected void logWarning(String description, String message) {
 			LOG.debug( description );
 			LOG.debug( message );
 		}
 	};
 
 	protected void dropTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
 		if ( getFactory().getDialect().dropTemporaryTableAfterUse() ) {
 			IsolatedWork work = new IsolatedWork() {
 				public void doWork(Connection connection) throws HibernateException {
 					final String command = session.getFactory().getDialect().getDropTemporaryTableString()
 							+ ' ' + persister.getTemporaryIdTableName();
 					try {
 						Statement statement = connection.createStatement();
 						try {
 							statement = connection.createStatement();
 							statement.executeUpdate( command );
 						}
 						finally {
 							try {
 								statement.close();
 							}
 							catch( Throwable ignore ) {
 								// ignore
 							}
 						}
 					}
 					catch( Exception e ) {
 						log.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
 					}
 				}
 			};
 
 			if ( shouldIsolateTemporaryTableDDL() ) {
 				if ( getFactory().getSettings().isDataDefinitionInTransactionSupported() ) {
 					Isolater.doIsolatedWork( work, session );
 				}
 				else {
 					Isolater.doNonTransactedWork( work, session );
 				}
 			}
 			else {
 				work.doWork( session.getJDBCContext().getConnectionManager().getConnection() );
 				session.getJDBCContext().getConnectionManager().afterStatement();
 			}
 		}
 		else {
 			// at the very least cleanup the data :)
 			PreparedStatement ps = null;
 			try {
-				ps = session.getBatcher().prepareStatement( "delete from " + persister.getTemporaryIdTableName() );
+				ps = session.getJDBCContext().getConnectionManager().prepareStatement( "delete from " + persister.getTemporaryIdTableName(),
+						false
+				);
 				ps.executeUpdate();
 			}
 			catch( Throwable t ) {
 				log.warn( "unable to cleanup temporary id table after use [" + t + "]" );
 			}
 			finally {
 				if ( ps != null ) {
 					try {
-						session.getBatcher().closeStatement( ps );
+						ps.close();
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 		}
 	}
 
 	protected void coordinateSharedCacheCleanup(SessionImplementor session) {
 		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, getAffectedQueryables() );
 
 		if ( session.isEventSource() ) {
 			( ( EventSource ) session ).getActionQueue().addAction( action );
 		}
 		else {
 			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	protected boolean shouldIsolateTemporaryTableDDL() {
 		Boolean dialectVote = getFactory().getDialect().performTemporaryTableDDLInIsolation();
 		if ( dialectVote != null ) {
 			return dialectVote.booleanValue();
 		}
 		else {
 			return getFactory().getSettings().isDataDefinitionImplicitCommit();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
index 3e03e0b84f..53c0aeee67 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/BasicExecutor.java
@@ -1,121 +1,120 @@
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
 package org.hibernate.hql.ast.exec;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.ast.HqlSqlWalker;
 import org.hibernate.hql.ast.QuerySyntaxException;
 import org.hibernate.hql.ast.SqlGenerator;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 
 import antlr.RecognitionException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Implementation of BasicExecutor.
  *
  * @author Steve Ebersole
  */
 public class BasicExecutor extends AbstractStatementExecutor {
 	private static final Logger log = LoggerFactory.getLogger( BasicExecutor.class );
 
 	private final Queryable persister;
 	private final String sql;
 	private final List parameterSpecifications;
 
 	public BasicExecutor(HqlSqlWalker walker, Queryable persister) {
 		super( walker, log );
 		this.persister = persister;
 		try {
 			SqlGenerator gen = new SqlGenerator( getFactory() );
 			gen.statement( walker.getAST() );
 			sql = gen.getSQL();
 			gen.getParseErrorHandler().throwQueryException();
 			parameterSpecifications = gen.getCollectedParameters();
 		}
 		catch ( RecognitionException e ) {
 			throw QuerySyntaxException.convert( e );
 		}
 	}
 
 	public String[] getSqlStatements() {
 		return new String[] { sql };
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
 
 		coordinateSharedCacheCleanup( session );
 
 		PreparedStatement st = null;
 		RowSelection selection = parameters.getRowSelection();
 
 		try {
 			try {
-				st = session.getBatcher().prepareStatement( sql );
+				st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, false );
 				Iterator parameterSpecifications = this.parameterSpecifications.iterator();
 				int pos = 1;
 				while ( parameterSpecifications.hasNext() ) {
 					final ParameterSpecification paramSpec = ( ParameterSpecification ) parameterSpecifications.next();
 					pos += paramSpec.bind( st, parameters, session, pos );
 				}
 				if ( selection != null ) {
 					if ( selection.getTimeout() != null ) {
 						st.setQueryTimeout( selection.getTimeout().intValue() );
 					}
 				}
 
 				return st.executeUpdate();
 			}
 			finally {
 				if ( st != null ) {
-					session.getBatcher().closeStatement( st );
+					st.close();
 				}
 			}
 		}
 		catch( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute update query",
 			        sql
 				);
 		}
 	}
 
 	protected Queryable[] getAffectedQueryables() {
 		return new Queryable[] { persister };
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
index 271340d240..4290cb2ef4 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
@@ -1,164 +1,163 @@
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
 package org.hibernate.hql.ast.exec;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.ast.HqlSqlWalker;
 import org.hibernate.hql.ast.tree.DeleteStatement;
 import org.hibernate.hql.ast.tree.FromElement;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.Delete;
 import org.hibernate.util.StringHelper;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Implementation of MultiTableDeleteExecutor.
  *
  * @author Steve Ebersole
  */
 public class MultiTableDeleteExecutor extends AbstractStatementExecutor {
 	private static final Logger log = LoggerFactory.getLogger( MultiTableDeleteExecutor.class );
 
 	private final Queryable persister;
 	private final String idInsertSelect;
 	private final String[] deletes;
 
 	public MultiTableDeleteExecutor(HqlSqlWalker walker) {
 		super( walker, log );
 
 		if ( !walker.getSessionFactoryHelper().getFactory().getDialect().supportsTemporaryTables() ) {
 			throw new HibernateException( "cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables" );
 		}
 
 		DeleteStatement deleteStatement = ( DeleteStatement ) walker.getAST();
 		FromElement fromElement = deleteStatement.getFromClause().getFromElement();
 		String bulkTargetAlias = fromElement.getTableAlias();
 		this.persister = fromElement.getQueryable();
 
 		this.idInsertSelect = generateIdInsertSelect( persister, bulkTargetAlias, deleteStatement.getWhereClause() );
 		log.trace( "Generated ID-INSERT-SELECT SQL (multi-table delete) : " +  idInsertSelect );
 
 		String[] tableNames = persister.getConstraintOrderedTableNameClosure();
 		String[][] columnNames = persister.getContraintOrderedTableKeyColumnClosure();
 		String idSubselect = generateIdSubselect( persister );
 
 		deletes = new String[tableNames.length];
 		for ( int i = tableNames.length - 1; i >= 0; i-- ) {
 			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
 			//      the difficulty is the ordering of the tables here vs the cascade attributes on the persisters ->
 			//          the table info gotten here should really be self-contained (i.e., a class representation
 			//          defining all the needed attributes), then we could then get an array of those
 			final Delete delete = new Delete()
 					.setTableName( tableNames[i] )
 					.setWhere( "(" + StringHelper.join( ", ", columnNames[i] ) + ") IN (" + idSubselect + ")" );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "bulk delete" );
 			}
 
 			deletes[i] = delete.toStatementString();
 		}
 	}
 
 	public String[] getSqlStatements() {
 		return deletes;
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
 		coordinateSharedCacheCleanup( session );
 
 		createTemporaryTableIfNecessary( persister, session );
 
 		try {
 			// First, save off the pertinent ids, saving the number of pertinent ids for return
 			PreparedStatement ps = null;
 			int resultCount = 0;
 			try {
 				try {
-					ps = session.getBatcher().prepareStatement( idInsertSelect );
+					ps = session.getJDBCContext().getConnectionManager().prepareStatement( idInsertSelect, false );
 					Iterator paramSpecifications = getIdSelectParameterSpecifications().iterator();
 					int pos = 1;
 					while ( paramSpecifications.hasNext() ) {
 						final ParameterSpecification paramSpec = ( ParameterSpecification ) paramSpecifications.next();
 						pos += paramSpec.bind( ps, parameters, session, pos );
 					}
 					resultCount = ps.executeUpdate();
 				}
 				finally {
 					if ( ps != null ) {
-						session.getBatcher().closeStatement( ps );
+						ps.close();
 					}
 				}
 			}
 			catch( SQLException e ) {
 				throw getFactory().getSQLExceptionHelper().convert(
 				        e,
 				        "could not insert/select ids for bulk delete",
 				        idInsertSelect
 					);
 			}
 
 			// Start performing the deletes
 			for ( int i = 0; i < deletes.length; i++ ) {
 				try {
 					try {
-						ps = session.getBatcher().prepareStatement( deletes[i] );
+						ps = session.getJDBCContext().getConnectionManager().prepareStatement( deletes[i], false );
 						ps.executeUpdate();
 					}
 					finally {
 						if ( ps != null ) {
-							session.getBatcher().closeStatement( ps );
+							ps.close();
 						}
 					}
 				}
 				catch( SQLException e ) {
 					throw getFactory().getSQLExceptionHelper().convert(
 					        e,
 					        "error performing bulk delete",
 					        deletes[i]
 						);
 				}
 			}
 
 			return resultCount;
 		}
 		finally {
 			dropTemporaryTableIfNecessary( persister, session );
 		}
 	}
 
 	protected Queryable[] getAffectedQueryables() {
 		return new Queryable[] { persister };
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
index f558a17b67..17f4cfc2a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
@@ -1,199 +1,198 @@
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
 package org.hibernate.hql.ast.exec;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.ast.HqlSqlWalker;
 import org.hibernate.hql.ast.tree.AssignmentSpecification;
 import org.hibernate.hql.ast.tree.FromElement;
 import org.hibernate.hql.ast.tree.UpdateStatement;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.Update;
 import org.hibernate.util.StringHelper;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Implementation of MultiTableUpdateExecutor.
  *
  * @author Steve Ebersole
  */
 public class MultiTableUpdateExecutor extends AbstractStatementExecutor {
 	private static final Logger log = LoggerFactory.getLogger( MultiTableUpdateExecutor.class );
 
 	private final Queryable persister;
 	private final String idInsertSelect;
 	private final String[] updates;
 	private final ParameterSpecification[][] hqlParameters;
 
 	public MultiTableUpdateExecutor(HqlSqlWalker walker) {
 		super( walker, log );
 
 		if ( !walker.getSessionFactoryHelper().getFactory().getDialect().supportsTemporaryTables() ) {
 			throw new HibernateException( "cannot doAfterTransactionCompletion multi-table updates using dialect not supporting temp tables" );
 		}
 
 		UpdateStatement updateStatement = ( UpdateStatement ) walker.getAST();
 		FromElement fromElement = updateStatement.getFromClause().getFromElement();
 		String bulkTargetAlias = fromElement.getTableAlias();
 		this.persister = fromElement.getQueryable();
 
 		this.idInsertSelect = generateIdInsertSelect( persister, bulkTargetAlias, updateStatement.getWhereClause() );
 		log.trace( "Generated ID-INSERT-SELECT SQL (multi-table update) : " +  idInsertSelect );
 
 		String[] tableNames = persister.getConstraintOrderedTableNameClosure();
 		String[][] columnNames = persister.getContraintOrderedTableKeyColumnClosure();
 
 		String idSubselect = generateIdSubselect( persister );
 		List assignmentSpecifications = walker.getAssignmentSpecifications();
 
 		updates = new String[tableNames.length];
 		hqlParameters = new ParameterSpecification[tableNames.length][];
 		for ( int tableIndex = 0; tableIndex < tableNames.length; tableIndex++ ) {
 			boolean affected = false;
 			List parameterList = new ArrayList();
 			Update update = new Update( getFactory().getDialect() )
 					.setTableName( tableNames[tableIndex] )
 					.setWhere( "(" + StringHelper.join( ", ", columnNames[tableIndex] ) + ") IN (" + idSubselect + ")" );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				update.setComment( "bulk update" );
 			}
 			final Iterator itr = assignmentSpecifications.iterator();
 			while ( itr.hasNext() ) {
 				final AssignmentSpecification specification = ( AssignmentSpecification ) itr.next();
 				if ( specification.affectsTable( tableNames[tableIndex] ) ) {
 					affected = true;
 					update.appendAssignmentFragment( specification.getSqlAssignmentFragment() );
 					if ( specification.getParameters() != null ) {
 						for ( int paramIndex = 0; paramIndex < specification.getParameters().length; paramIndex++ ) {
 							parameterList.add( specification.getParameters()[paramIndex] );
 						}
 					}
 				}
 			}
 			if ( affected ) {
 				updates[tableIndex] = update.toStatementString();
 				hqlParameters[tableIndex] = ( ParameterSpecification[] ) parameterList.toArray( new ParameterSpecification[0] );
 			}
 		}
 	}
 
 	public Queryable getAffectedQueryable() {
 		return persister;
 	}
 
 	public String[] getSqlStatements() {
 		return updates;
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
 		coordinateSharedCacheCleanup( session );
 
 		createTemporaryTableIfNecessary( persister, session );
 
 		try {
 			// First, save off the pertinent ids, as the return value
 			PreparedStatement ps = null;
 			int resultCount = 0;
 			try {
 				try {
-					ps = session.getBatcher().prepareStatement( idInsertSelect );
+					ps = session.getJDBCContext().getConnectionManager().prepareStatement( idInsertSelect, false );
 //					int parameterStart = getWalker().getNumberOfParametersInSetClause();
 //					List allParams = getIdSelectParameterSpecifications();
 //					Iterator whereParams = allParams.subList( parameterStart, allParams.size() ).iterator();
 					Iterator whereParams = getIdSelectParameterSpecifications().iterator();
 					int sum = 1; // jdbc params are 1-based
 					while ( whereParams.hasNext() ) {
 						sum += ( ( ParameterSpecification ) whereParams.next() ).bind( ps, parameters, session, sum );
 					}
 					resultCount = ps.executeUpdate();
 				}
 				finally {
 					if ( ps != null ) {
-						session.getBatcher().closeStatement( ps );
+						ps.close();
 					}
 				}
 			}
 			catch( SQLException e ) {
 				throw getFactory().getSQLExceptionHelper().convert(
 				        e,
 				        "could not insert/select ids for bulk update",
 				        idInsertSelect
 					);
 			}
 
 			// Start performing the updates
 			for ( int i = 0; i < updates.length; i++ ) {
 				if ( updates[i] == null ) {
 					continue;
 				}
 				try {
 					try {
-						ps = session.getBatcher().prepareStatement( updates[i] );
+						ps = session.getJDBCContext().getConnectionManager().prepareStatement( updates[i], false );
 						if ( hqlParameters[i] != null ) {
 							int position = 1; // jdbc params are 1-based
 							for ( int x = 0; x < hqlParameters[i].length; x++ ) {
 								position += hqlParameters[i][x].bind( ps, parameters, session, position );
 							}
 						}
 						ps.executeUpdate();
 					}
 					finally {
 						if ( ps != null ) {
-							session.getBatcher().closeStatement( ps );
+							ps.close();
 						}
 					}
 				}
 				catch( SQLException e ) {
 					throw getFactory().getSQLExceptionHelper().convert(
 					        e,
 					        "error performing bulk update",
 					        updates[i]
 						);
 				}
 			}
 
 			return resultCount;
 		}
 		finally {
 			dropTemporaryTableIfNecessary( persister, session );
 		}
 	}
 
 	protected Queryable[] getAffectedQueryables() {
 		return new Queryable[] { persister };
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java
index 0f139ad668..d183b1a592 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/GUIDGenerator.java
@@ -1,89 +1,88 @@
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
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 
 /**
  * Generates <tt>string</tt> values using the SQL Server NEWID() function.
  *
  * @author Joseph Fifield
  */
 public class GUIDGenerator implements IdentifierGenerator {
 	private static final Logger log = LoggerFactory.getLogger(GUIDGenerator.class);
 	private static boolean warned = false;
 
 	public GUIDGenerator() {
 		if ( ! warned ) {
 			warned = true;
 			log.warn(
 					"DEPRECATED : use {} instead with custom {} implementation",
 					UUIDGenerator.class.getName(),
 					UUIDGenerationStrategy.class.getName()
 			);
 		}
 	}
 
 	public Serializable generate(SessionImplementor session, Object obj)
 	throws HibernateException {
 		
 		final String sql = session.getFactory().getDialect().getSelectGUIDString();
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement(sql);
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement(sql);
 			try {
 				ResultSet rs = st.executeQuery();
 				final String result;
 				try {
 					rs.next();
 					result = rs.getString(1);
 				}
 				finally {
 					rs.close();
 				}
 				log.debug("GUID identifier generated: " + result);
 				return result;
 			}
 			finally {
-				session.getBatcher().closeStatement(st);
+				st.close();
 			}
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve GUID",
 					sql
 				);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
index 7bcacae8f7..65aa1e4b5f 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IdentityGenerator.java
@@ -1,191 +1,191 @@
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
 package org.hibernate.id;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.PreparedStatement;
 
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.id.insert.IdentifierGeneratingInsert;
 import org.hibernate.id.insert.AbstractSelectingDelegate;
 import org.hibernate.id.insert.AbstractReturningDelegate;
 import org.hibernate.id.insert.InsertSelectIdentityInsert;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.HibernateException;
 import org.hibernate.AssertionFailure;
 
 /**
  * A generator for use with ANSI-SQL IDENTITY columns used as the primary key.
  * The IdentityGenerator for autoincrement/identity key generation.
  * <br><br>
  * Indicates to the <tt>Session</tt> that identity (ie. identity/autoincrement
  * column) key generation should be used.
  *
  * @author Christoph Sturm
  */
 public class IdentityGenerator extends AbstractPostInsertGenerator {
 
 	public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(
 			PostInsertIdentityPersister persister,
 	        Dialect dialect,
 	        boolean isGetGeneratedKeysEnabled) throws HibernateException {
 		if ( isGetGeneratedKeysEnabled ) {
 			return new GetGeneratedKeysDelegate( persister, dialect );
 		}
 		else if ( dialect.supportsInsertSelectIdentity() ) {
 			return new InsertSelectDelegate( persister, dialect );
 		}
 		else {
 			return new BasicDelegate( persister, dialect );
 		}
 	}
 
 	/**
 	 * Delegate for dealing with IDENTITY columns using JDBC3 getGeneratedKeys
 	 */
 	public static class GetGeneratedKeysDelegate
 			extends AbstractReturningDelegate
 			implements InsertGeneratedIdentifierDelegate {
 		private final PostInsertIdentityPersister persister;
 		private final Dialect dialect;
 
 		public GetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
 			super( persister );
 			this.persister = persister;
 			this.dialect = dialect;
 		}
 
 		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
 			IdentifierGeneratingInsert insert = new IdentifierGeneratingInsert( dialect );
 			insert.addIdentityColumn( persister.getRootTableKeyColumnNames()[0] );
 			return insert;
 		}
 
 		protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
-			return session.getBatcher().prepareStatement( insertSQL, true );
+			return session.getJDBCContext().getConnectionManager().prepareStatement( insertSQL, PreparedStatement.RETURN_GENERATED_KEYS );
 		}
 
 		public Serializable executeAndExtract(PreparedStatement insert) throws SQLException {
 			insert.executeUpdate();
 			ResultSet rs = null;
 			try {
 				rs = insert.getGeneratedKeys();
 				return IdentifierGeneratorHelper.getGeneratedIdentity(
 						rs,
 						persister.getIdentifierType()
 				);
 			}
 			finally {
 				if ( rs != null ) {
 					rs.close();
 				}
 			}
 		}
 	}
 
 	/**
 	 * Delegate for dealing with IDENTITY columns where the dialect supports returning
 	 * the generated IDENTITY value directly from the insert statement.
 	 */
 	public static class InsertSelectDelegate
 			extends AbstractReturningDelegate
 			implements InsertGeneratedIdentifierDelegate {
 		private final PostInsertIdentityPersister persister;
 		private final Dialect dialect;
 
 		public InsertSelectDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
 			super( persister );
 			this.persister = persister;
 			this.dialect = dialect;
 		}
 
 		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
 			InsertSelectIdentityInsert insert = new InsertSelectIdentityInsert( dialect );
 			insert.addIdentityColumn( persister.getRootTableKeyColumnNames()[0] );
 			return insert;
 		}
 
 		protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
-			return session.getBatcher().prepareStatement( insertSQL, false );
+			return session.getJDBCContext().getConnectionManager().prepareStatement( insertSQL, PreparedStatement.NO_GENERATED_KEYS );
 		}
 
 		public Serializable executeAndExtract(PreparedStatement insert) throws SQLException {
 			if ( !insert.execute() ) {
 				while ( !insert.getMoreResults() && insert.getUpdateCount() != -1 ) {
 					// do nothing until we hit the rsult set containing the generated id
 				}
 			}
 			ResultSet rs = insert.getResultSet();
 			try {
 				return IdentifierGeneratorHelper.getGeneratedIdentity( rs, persister.getIdentifierType() );
 			}
 			finally {
 				rs.close();
 			}
 		}
 
 		public Serializable determineGeneratedIdentifier(SessionImplementor session, Object entity) {
 			throw new AssertionFailure( "insert statement returns generated value" );
 		}
 	}
 
 	/**
 	 * Delegate for dealing with IDENTITY columns where the dialect requires an
 	 * additional command execution to retrieve the generated IDENTITY value
 	 */
 	public static class BasicDelegate
 			extends AbstractSelectingDelegate
 			implements InsertGeneratedIdentifierDelegate {
 		private final PostInsertIdentityPersister persister;
 		private final Dialect dialect;
 
 		public BasicDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
 			super( persister );
 			this.persister = persister;
 			this.dialect = dialect;
 		}
 
 		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
 			IdentifierGeneratingInsert insert = new IdentifierGeneratingInsert( dialect );
 			insert.addIdentityColumn( persister.getRootTableKeyColumnNames()[0] );
 			return insert;
 		}
 
 		protected String getSelectSQL() {
 			return persister.getIdentitySelectString();
 		}
 
 		protected Serializable getResult(
 				SessionImplementor session,
 		        ResultSet rs,
 		        Object object) throws SQLException {
 			return IdentifierGeneratorHelper.getGeneratedIdentity( rs, persister.getIdentifierType() );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
index 0673a46527..7cdd9bf16e 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
@@ -1,155 +1,154 @@
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
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 import org.hibernate.util.StringHelper;
 
 /**
  * <b>increment</b><br>
  * <br>
  * An <tt>IdentifierGenerator</tt> that returns a <tt>long</tt>, constructed by
  * counting from the maximum primary key value at startup. Not safe for use in a
  * cluster!<br>
  * <br>
  * Mapping parameters supported, but not usually needed: tables, column.
  * (The tables parameter specified a comma-separated list of table names.)
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class IncrementGenerator implements IdentifierGenerator, Configurable {
 	private static final Logger log = LoggerFactory.getLogger(IncrementGenerator.class);
 
 	private Class returnClass;
 	private String sql;
 
 	private IntegralDataTypeHolder previousValueHolder;
 
 	public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
 		if ( sql != null ) {
 			initializePreviousValueHolder( session );
 		}
 		return previousValueHolder.makeValueThenIncrement();
 	}
 
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		returnClass = type.getReturnedClass();
 
 		ObjectNameNormalizer normalizer =
 				( ObjectNameNormalizer ) params.get( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER );
 
 		String column = params.getProperty( "column" );
 		if ( column == null ) {
 			column = params.getProperty( PersistentIdentifierGenerator.PK );
 		}
 		column = dialect.quote( normalizer.normalizeIdentifierQuoting( column ) );
 
 		String tableList = params.getProperty( "tables" );
 		if ( tableList == null ) {
 			tableList = params.getProperty( PersistentIdentifierGenerator.TABLES );
 		}
 		String[] tables = StringHelper.split( ", ", tableList );
 
 		final String schema = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						params.getProperty( PersistentIdentifierGenerator.SCHEMA )
 				)
 		);
 		final String catalog = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						params.getProperty( PersistentIdentifierGenerator.CATALOG )
 				)
 		);
 
 		StringBuffer buf = new StringBuffer();
 		for ( int i=0; i < tables.length; i++ ) {
 			final String tableName = dialect.quote( normalizer.normalizeIdentifierQuoting( tables[i] ) );
 			if ( tables.length > 1 ) {
 				buf.append( "select " ).append( column ).append( " from " );
 			}
 			buf.append( Table.qualify( catalog, schema, tableName ) );
 			if ( i < tables.length-1 ) {
 				buf.append( " union " );
 			}
 		}
 		if ( tables.length > 1 ) {
 			buf.insert( 0, "( " ).append( " ) ids_" );
 			column = "ids_." + column;
 		}
 		
 		sql = "select max(" + column + ") from " + buf.toString();
 	}
 
 	private void initializePreviousValueHolder(SessionImplementor session) {
 		previousValueHolder = IdentifierGeneratorHelper.getIntegralDataTypeHolder( returnClass );
 
 		log.debug( "fetching initial value: " + sql );
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( rs.next() ) {
 						previousValueHolder.initialize( rs, 0L ).increment();
 					}
 					else {
 						previousValueHolder.initialize( 1L );
 					}
 					sql = null;
 					log.debug( "first free id: " + previousValueHolder.makeValue() );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement(st);
+				st.close();
 			}
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not fetch initial value for increment generator",
 					sql
 			);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java
index 9a1e25a4f1..87f37e08a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/SequenceGenerator.java
@@ -1,164 +1,163 @@
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
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.ObjectNameNormalizer;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 /**
  * <b>sequence</b><br>
  * <br>
  * Generates <tt>long</tt> values using an oracle-style sequence. A higher
  * performance algorithm is <tt>SequenceHiLoGenerator</tt>.<br>
  * <br>
  * Mapping parameters supported: sequence, parameters.
  *
  * @see SequenceHiLoGenerator
  * @see TableHiLoGenerator
  * @author Gavin King
  */
 public class SequenceGenerator implements PersistentIdentifierGenerator, Configurable {
 	private static final Logger log = LoggerFactory.getLogger(SequenceGenerator.class);
 
 	/**
 	 * The sequence parameter
 	 */
 	public static final String SEQUENCE = "sequence";
 
 	/**
 	 * The parameters parameter, appended to the create sequence DDL.
 	 * For example (Oracle): <tt>INCREMENT BY 1 START WITH 1 MAXVALUE 100 NOCACHE</tt>.
 	 */
 	public static final String PARAMETERS = "parameters";
 
 	private String sequenceName;
 	private String parameters;
 	private Type identifierType;
 	private String sql;
 
 	protected Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 		sequenceName = normalizer.normalizeIdentifierQuoting(
 				ConfigurationHelper.getString( SEQUENCE, params, "hibernate_sequence" )
 		);
 		parameters = params.getProperty( PARAMETERS );
 
 		if ( sequenceName.indexOf( '.' ) < 0 ) {
 			final String schemaName = normalizer.normalizeIdentifierQuoting( params.getProperty( SCHEMA ) );
 			final String catalogName = normalizer.normalizeIdentifierQuoting( params.getProperty( CATALOG ) );
 			sequenceName = Table.qualify(
 					dialect.quote( catalogName ),
 					dialect.quote( schemaName ),
 					dialect.quote( sequenceName )
 			);
 		}
 		else {
 			// if already qualified there is not much we can do in a portable manner so we pass it
 			// through and assume the user has set up the name correctly.
 		}
 
 		this.identifierType = type;
 		sql = dialect.getSequenceNextValString( sequenceName );
 	}
 
 	public Serializable generate(SessionImplementor session, Object obj) {
 		return generateHolder( session ).makeValue();
 	}
 
 	protected IntegralDataTypeHolder generateHolder(SessionImplementor session) {
 		try {
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				ResultSet rs = st.executeQuery();
 				try {
 					rs.next();
 					IntegralDataTypeHolder result = buildHolder();
 					result.initialize( rs, 1 );
 					if ( log.isDebugEnabled() ) {
 						log.debug("Sequence identifier generated: " + result);
 					}
 					return result;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement(st);
+				st.close();
 			}
 
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not get next sequence value",
 					sql
 			);
 		}
 	}
 
 	protected IntegralDataTypeHolder buildHolder() {
 		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
 	}
 
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		String[] ddl = dialect.getCreateSequenceStrings(sequenceName);
 		if ( parameters != null ) {
 			ddl[ddl.length - 1] += ' ' + parameters;
 		}
 		return ddl;
 	}
 
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return dialect.getDropSequenceStrings(sequenceName);
 	}
 
 	public Object generatorKey() {
 		return sequenceName;
 	}
 
 	public String getSequenceName() {
 		return sequenceName;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
index 4bc6c6d88e..5382fbeda2 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/SequenceIdentityGenerator.java
@@ -1,123 +1,123 @@
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
 package org.hibernate.id;
 
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.id.insert.AbstractReturningDelegate;
 import org.hibernate.id.insert.IdentifierGeneratingInsert;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.sql.Insert;
 import org.hibernate.type.Type;
 import org.hibernate.engine.SessionImplementor;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Properties;
 
 /**
  * A generator which combines sequence generation with immediate retrieval
  * through JDBC3 {@link java.sql.Connection#prepareStatement(String, String[]) getGeneratedKeys}.
  * In this respect it works much like ANSI-SQL IDENTITY generation.
  * <p/>
  * This generator only known to work with newer Oracle drivers compiled for
  * JDK 1.4 (JDBC3).
  * <p/>
  * Note: Due to a bug in Oracle drivers, sql comments on these insert statements
  * are completely disabled.
  *
  * @author Steve Ebersole
  */
 public class SequenceIdentityGenerator extends SequenceGenerator
 		implements PostInsertIdentifierGenerator {
 
 	private static final Logger log = LoggerFactory.getLogger( SequenceIdentityGenerator.class );
 
 	public Serializable generate(SessionImplementor s, Object obj) {
 		return IdentifierGeneratorHelper.POST_INSERT_INDICATOR;
 	}
 
 	public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(
 			PostInsertIdentityPersister persister,
 	        Dialect dialect,
 	        boolean isGetGeneratedKeysEnabled) throws HibernateException {
 		return new Delegate( persister, dialect, getSequenceName() );
 	}
 
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		super.configure( type, params, dialect );
 	}
 
 	public static class Delegate extends AbstractReturningDelegate {
 		private final Dialect dialect;
 		private final String sequenceNextValFragment;
 		private final String[] keyColumns;
 
 		public Delegate(PostInsertIdentityPersister persister, Dialect dialect, String sequenceName) {
 			super( persister );
 			this.dialect = dialect;
 			this.sequenceNextValFragment = dialect.getSelectSequenceNextValString( sequenceName );
 			this.keyColumns = getPersister().getRootTableKeyColumnNames();
 			if ( keyColumns.length > 1 ) {
 				throw new HibernateException( "sequence-identity generator cannot be used with with multi-column keys" );
 			}
 		}
 
 		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
 			NoCommentsInsert insert = new NoCommentsInsert( dialect );
 			insert.addColumn( getPersister().getRootTableKeyColumnNames()[0], sequenceNextValFragment );
 			return insert;
 		}
 
 		protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
-			return session.getBatcher().prepareStatement( insertSQL, keyColumns );
+			return session.getJDBCContext().getConnectionManager().prepareStatement( insertSQL, keyColumns );
 		}
 
 		protected Serializable executeAndExtract(PreparedStatement insert) throws SQLException {
 			insert.executeUpdate();
 			return IdentifierGeneratorHelper.getGeneratedIdentity(
 					insert.getGeneratedKeys(),
 			        getPersister().getIdentifierType()
 			);
 		}
 	}
 
 	public static class NoCommentsInsert extends IdentifierGeneratingInsert {
 		public NoCommentsInsert(Dialect dialect) {
 			super( dialect );
 		}
 
 		public Insert setComment(String comment) {
 			// don't allow comments on these insert statements as comments totally
 			// blow up the Oracle getGeneratedKeys "support" :(
 			log.info( "disallowing insert statement comment for select-identity due to Oracle driver bug" );
 			return this;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
index 6c4d990057..d946d2cf1e 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStructure.java
@@ -1,164 +1,163 @@
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
 package org.hibernate.id.enhanced;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.HibernateException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 
 /**
  * Describes a sequence.
  *
  * @author Steve Ebersole
  */
 public class SequenceStructure implements DatabaseStructure {
 	private static final Logger log = LoggerFactory.getLogger( SequenceStructure.class );
 
 	private final String sequenceName;
 	private final int initialValue;
 	private final int incrementSize;
 	private final Class numberType;
 	private final String sql;
 	private boolean applyIncrementSizeToSourceValues;
 	private int accessCounter;
 
 	public SequenceStructure(
 			Dialect dialect,
 			String sequenceName,
 			int initialValue,
 			int incrementSize,
 			Class numberType) {
 		this.sequenceName = sequenceName;
 		this.initialValue = initialValue;
 		this.incrementSize = incrementSize;
 		this.numberType = numberType;
 		sql = dialect.getSequenceNextValString( sequenceName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String getName() {
 		return sequenceName;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int getIncrementSize() {
 		return incrementSize;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int getTimesAccessed() {
 		return accessCounter;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int getInitialValue() {
 		return initialValue;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public AccessCallback buildCallback(final SessionImplementor session) {
 		return new AccessCallback() {
 			public IntegralDataTypeHolder getNextValue() {
 				accessCounter++;
 				try {
-					PreparedStatement st = session.getBatcher().prepareSelectStatement( sql );
+					PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 					try {
 						ResultSet rs = st.executeQuery();
 						try {
 							rs.next();
 							IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( numberType );
 							value.initialize( rs, 1 );
 							if ( log.isDebugEnabled() ) {
 								log.debug( "Sequence value obtained: " + value.makeValue() );
 							}
 							return value;
 						}
 						finally {
 							try {
 								rs.close();
 							}
 							catch( Throwable ignore ) {
 								// intentionally empty
 							}
 						}
 					}
 					finally {
-						session.getBatcher().closeStatement( st );
+						st.close();
 					}
 
 				}
 				catch ( SQLException sqle) {
 					throw session.getFactory().getSQLExceptionHelper().convert(
 							sqle,
 							"could not get next sequence value",
 							sql
 					);
 				}
 			}
 		};
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void prepare(Optimizer optimizer) {
 		applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		int sourceIncrementSize = applyIncrementSizeToSourceValues ? incrementSize : 1;
 		return dialect.getCreateSequenceStrings( sequenceName, initialValue, sourceIncrementSize );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return dialect.getDropSequenceStrings( sequenceName );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
index f9632573ca..7ca386c4d5 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractReturningDelegate.java
@@ -1,83 +1,82 @@
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
 package org.hibernate.id.insert;
 
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.pretty.MessageHelper;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 /**
  * Abstract InsertGeneratedIdentifierDelegate implementation where the
  * underlying strategy causes the enerated identitifer to be returned as an
  * effect of performing the insert statement.  Thus, there is no need for an
  * additional sql statement to determine the generated identitifer.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractReturningDelegate implements InsertGeneratedIdentifierDelegate {
 	private final PostInsertIdentityPersister persister;
 
 	public AbstractReturningDelegate(PostInsertIdentityPersister persister) {
 		this.persister = persister;
 	}
 
 	public final Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder) {
 		try {
 			// prepare and execute the insert
 			PreparedStatement insert = prepare( insertSQL, session );
 			try {
 				binder.bindValues( insert );
 				return executeAndExtract( insert );
 			}
 			finally {
 				releaseStatement( insert, session );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not insert: " + MessageHelper.infoString( persister ),
 			        insertSQL
 				);
 		}
 	}
 
 	protected PostInsertIdentityPersister getPersister() {
 		return persister;
 	}
 
 	protected abstract PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException;
 
 	protected abstract Serializable executeAndExtract(PreparedStatement insert) throws SQLException;
 
 	protected void releaseStatement(PreparedStatement insert, SessionImplementor session) throws SQLException {
-		session.getBatcher().closeStatement( insert );
+		insert.close();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
index 3af21fe2ce..9b93eab5d7 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/insert/AbstractSelectingDelegate.java
@@ -1,135 +1,134 @@
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
 package org.hibernate.id.insert;
 
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.id.PostInsertIdentityPersister;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.ResultSet;
 import java.io.Serializable;
 
 /**
  * Abstract InsertGeneratedIdentifierDelegate implementation where the
  * underlying strategy requires an subsequent select after the insert
  * to determine the generated identifier.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractSelectingDelegate implements InsertGeneratedIdentifierDelegate {
 	private final PostInsertIdentityPersister persister;
 
 	protected AbstractSelectingDelegate(PostInsertIdentityPersister persister) {
 		this.persister = persister;
 	}
 
 	public final Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder) {
 		try {
 			// prepare and execute the insert
-			PreparedStatement insert = session.getBatcher().prepareStatement( insertSQL, false );
+			PreparedStatement insert = session.getJDBCContext().getConnectionManager().prepareStatement( insertSQL, PreparedStatement.NO_GENERATED_KEYS );
 			try {
 				binder.bindValues( insert );
 				insert.executeUpdate();
 			}
 			finally {
-				session.getBatcher().closeStatement( insert );
+				insert.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not insert: " + MessageHelper.infoString( persister ),
 			        insertSQL
 				);
 		}
 
 		final String selectSQL = getSelectSQL();
 
 		try {
 			//fetch the generated id in a separate query
-			PreparedStatement idSelect = session.getBatcher().prepareStatement( selectSQL );
+			PreparedStatement idSelect = session.getJDBCContext().getConnectionManager().prepareStatement( selectSQL, false );
 			try {
 				bindParameters( session, idSelect, binder.getEntity() );
 				ResultSet rs = idSelect.executeQuery();
 				try {
 					return getResult( session, rs, binder.getEntity() );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( idSelect );
+				idSelect.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not retrieve generated id after insert: " + MessageHelper.infoString( persister ),
 			        insertSQL
 			);
 		}
 	}
 
 	/**
 	 * Get the SQL statement to be used to retrieve generated key values.
 	 *
 	 * @return The SQL command string
 	 */
 	protected abstract String getSelectSQL();
 
 	/**
 	 * Bind any required parameter values into the SQL command {@link #getSelectSQL}.
 	 *
 	 * @param session The session
 	 * @param ps The prepared {@link #getSelectSQL SQL} command
 	 * @param entity The entity being saved.
 	 * @throws SQLException
 	 */
 	protected void bindParameters(
 			SessionImplementor session,
 	        PreparedStatement ps,
 	        Object entity) throws SQLException {
 	}
 
 	/**
 	 * Extract the generated key value from the given result set.
 	 *
 	 * @param session The session
 	 * @param rs The result set containing the generated primay key values.
 	 * @param entity The entity being saved.
 	 * @return The generated identifier
 	 * @throws SQLException
 	 */
 	protected abstract Serializable getResult(
 			SessionImplementor session,
 	        ResultSet rs,
 	        Object entity) throws SQLException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
index 8636e7d554..824e18c41b 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
@@ -1,290 +1,290 @@
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
 package org.hibernate.impl;
 
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.loader.Loader;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the <tt>ScrollableResults</tt> interface
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractScrollableResults implements ScrollableResults {
 
 	private static final Logger log = LoggerFactory.getLogger( AbstractScrollableResults.class );
 
 	private final ResultSet resultSet;
 	private final PreparedStatement ps;
 	private final SessionImplementor session;
 	private final Loader loader;
 	private final QueryParameters queryParameters;
 	private final Type[] types;
 	private HolderInstantiator holderInstantiator;
 
 	public AbstractScrollableResults(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        SessionImplementor sess,
 			Loader loader,
 			QueryParameters queryParameters,
 	        Type[] types,
 	        HolderInstantiator holderInstantiator) throws MappingException {
 		this.resultSet=rs;
 		this.ps=ps;
 		this.session = sess;
 		this.loader = loader;
 		this.queryParameters = queryParameters;
 		this.types = types;
 		this.holderInstantiator = holderInstantiator!=null && holderInstantiator.isRequired()
 		        ? holderInstantiator 
 		        : null;
 	}
 
 	protected abstract Object[] getCurrentRow();
 
 	protected ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	protected PreparedStatement getPs() {
 		return ps;
 	}
 
 	protected SessionImplementor getSession() {
 		return session;
 	}
 
 	protected Loader getLoader() {
 		return loader;
 	}
 
 	protected QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	protected Type[] getTypes() {
 		return types;
 	}
 
 	protected HolderInstantiator getHolderInstantiator() {
 		return holderInstantiator;
 	}
 
 	public final void close() throws HibernateException {
 		try {
 			// not absolutely necessary, but does help with aggressive release
-			session.getBatcher().closeQueryStatement( ps, resultSet );
+			//session.getJDBCContext().getConnectionManager().closeQueryStatement( ps, resultSet );
+			ps.close();
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not close results"
 				);
 		}
 		finally {
 			try {
 				session.getPersistenceContext().getLoadContexts().cleanup( resultSet );
 			}
 			catch( Throwable ignore ) {
 				// ignore this error for now
 				log.trace( "exception trying to cleanup load context : " + ignore.getMessage() );
 			}
 		}
 	}
 
 	public final Object[] get() throws HibernateException {
 		return getCurrentRow();
 	}
 
 	public final Object get(int col) throws HibernateException {
 		return getCurrentRow()[col];
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are identical.
 	 *
 	 * @param col the column
 	 * @param returnType a "final" type
 	 */
 	protected final Object getFinal(int col, Type returnType) throws HibernateException {
 		if ( holderInstantiator!=null ) {
 			throw new HibernateException("query specifies a holder class");
 		}
 		
 		if ( returnType.getReturnedClass()==types[col].getReturnedClass() ) {
 			return get(col);
 		}
 		else {
 			return throwInvalidColumnTypeException(col, types[col], returnType);
 		}
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are "assignable".
 	 *
 	 * @param col the column
 	 * @param returnType any type
 	 */
 	protected final Object getNonFinal(int col, Type returnType) throws HibernateException {
 		if ( holderInstantiator!=null ) {
 			throw new HibernateException("query specifies a holder class");
 		}
 		
 		if ( returnType.getReturnedClass().isAssignableFrom( types[col].getReturnedClass() ) ) {
 			return get(col);
 		}
 		else {
 			return throwInvalidColumnTypeException(col, types[col], returnType);
 		}
 	}
 
 	public final BigDecimal getBigDecimal(int col) throws HibernateException {
 		return (BigDecimal) getFinal(col, Hibernate.BIG_DECIMAL);
 	}
 
 	public final BigInteger getBigInteger(int col) throws HibernateException {
 		return (BigInteger) getFinal(col, Hibernate.BIG_INTEGER);
 	}
 
 	public final byte[] getBinary(int col) throws HibernateException {
 		return (byte[]) getFinal(col, Hibernate.BINARY);
 	}
 
 	public final String getText(int col) throws HibernateException {
 		return (String) getFinal(col, Hibernate.TEXT);
 	}
 
 	public final Blob getBlob(int col) throws HibernateException {
 		return (Blob) getNonFinal(col, Hibernate.BLOB);
 	}
 
 	public final Clob getClob(int col) throws HibernateException {
 		return (Clob) getNonFinal(col, Hibernate.CLOB);
 	}
 
 	public final Boolean getBoolean(int col) throws HibernateException {
 		return (Boolean) getFinal(col, Hibernate.BOOLEAN);
 	}
 
 	public final Byte getByte(int col) throws HibernateException {
 		return (Byte) getFinal(col, Hibernate.BYTE);
 	}
 
 	public final Character getCharacter(int col) throws HibernateException {
 		return (Character) getFinal(col, Hibernate.CHARACTER);
 	}
 
 	public final Date getDate(int col) throws HibernateException {
 		return (Date) getNonFinal(col, Hibernate.TIMESTAMP);
 	}
 
 	public final Calendar getCalendar(int col) throws HibernateException {
 		return (Calendar) getNonFinal(col, Hibernate.CALENDAR);
 	}
 
 	public final Double getDouble(int col) throws HibernateException {
 		return (Double) getFinal(col, Hibernate.DOUBLE);
 	}
 
 	public final Float getFloat(int col) throws HibernateException {
 		return (Float) getFinal(col, Hibernate.FLOAT);
 	}
 
 	public final Integer getInteger(int col) throws HibernateException {
 		return (Integer) getFinal(col, Hibernate.INTEGER);
 	}
 
 	public final Long getLong(int col) throws HibernateException {
 		return (Long) getFinal(col, Hibernate.LONG);
 	}
 
 	public final Short getShort(int col) throws HibernateException {
 		return (Short) getFinal(col, Hibernate.SHORT);
 	}
 
 	public final String getString(int col) throws HibernateException {
 		return (String) getFinal(col, Hibernate.STRING);
 	}
 
 	public final Locale getLocale(int col) throws HibernateException {
 		return (Locale) getFinal(col, Hibernate.LOCALE);
 	}
 
 	/*public final Currency getCurrency(int col) throws HibernateException {
 		return (Currency) get(col);
 	}*/
 
 	public final TimeZone getTimeZone(int col) throws HibernateException {
 		return (TimeZone) getNonFinal(col, Hibernate.TIMEZONE);
 	}
 
 	public final Type getType(int i) {
 		return types[i];
 	}
 
 	private Object throwInvalidColumnTypeException(
 	        int i,
 	        Type type,
 	        Type returnType) throws HibernateException {
 		throw new HibernateException( 
 				"incompatible column types: " + 
 				type.getName() + 
 				", " + 
 				returnType.getName() 
 		);
 	}
 
 	protected void afterScrollOperation() {
 		session.afterScrollOperation();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
index e11f3e12b7..b3f718077d 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
@@ -1,189 +1,188 @@
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
 package org.hibernate.impl;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.NoSuchElementException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.HibernateIterator;
 import org.hibernate.event.EventSource;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * An implementation of <tt>java.util.Iterator</tt> that is
  * returned by <tt>iterate()</tt> query execution methods.
  * @author Gavin King
  */
 public final class IteratorImpl implements HibernateIterator {
 
 	private static final Logger log = LoggerFactory.getLogger(IteratorImpl.class);
 
 	private ResultSet rs;
 	private final EventSource session;
 	private boolean readOnly;
 	private final Type[] types;
 	private final boolean single;
 	private Object currentResult;
 	private boolean hasNext;
 	private final String[][] names;
 	private PreparedStatement ps;
 	private HolderInstantiator holderInstantiator;
 
 	public IteratorImpl(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        EventSource sess,
 	        boolean readOnly,
 	        Type[] types,
 	        String[][] columnNames,
 	        HolderInstantiator holderInstantiator)
 	throws HibernateException, SQLException {
 
 		this.rs=rs;
 		this.ps=ps;
 		this.session = sess;
 		this.readOnly = readOnly;
 		this.types = types;
 		this.names = columnNames;
 		this.holderInstantiator = holderInstantiator;
 
 		single = types.length==1;
 
 		postNext();
 	}
 
 	public void close() throws JDBCException {
 		if (ps!=null) {
 			try {
 				log.debug("closing iterator");
-				session.getBatcher().closeQueryStatement(ps, rs);
+				ps.close();
 				ps = null;
 				rs = null;
 				hasNext = false;
 			}
 			catch (SQLException e) {
 				log.info( "Unable to close iterator", e );
 				throw session.getFactory().getSQLExceptionHelper().convert(
 				        e,
 				        "Unable to close iterator"
 					);
 			}
 			finally {
 				try {
 					session.getPersistenceContext().getLoadContexts().cleanup( rs );
 				}
 				catch( Throwable ignore ) {
 					// ignore this error for now
 					log.trace( "exception trying to cleanup load context : " + ignore.getMessage() );
 				}
 			}
 		}
 	}
 
 	private void postNext() throws SQLException {
 		log.debug("attempting to retrieve next results");
 		this.hasNext = rs.next();
 		if (!hasNext) {
 			log.debug("exhausted results");
 			close();
 		}
 		else {
 			log.debug("retrieved next results");
 		}
 	}
 
 	public boolean hasNext() {
 		return hasNext;
 	}
 
 	public Object next() throws HibernateException {
 		if ( !hasNext ) throw new NoSuchElementException("No more results");
 		boolean sessionDefaultReadOnlyOrig = session.isDefaultReadOnly();
 		session.setDefaultReadOnly( readOnly );
 		try {
 			boolean isHolder = holderInstantiator.isRequired();
 
 			log.debug("assembling results");
 			if ( single && !isHolder ) {
 				currentResult = types[0].nullSafeGet( rs, names[0], session, null );
 			}
 			else {
 				Object[] currentResults = new Object[types.length];
 				for (int i=0; i<types.length; i++) {
 					currentResults[i] = types[i].nullSafeGet( rs, names[i], session, null );
 				}
 
 				if (isHolder) {
 					currentResult = holderInstantiator.instantiate(currentResults);
 				}
 				else {
 					currentResult = currentResults;
 				}
 			}
 
 			postNext();
 			log.debug("returning current results");
 			return currentResult;
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not get next iterator result"
 				);
 		}
 		finally {
 			session.setDefaultReadOnly( sessionDefaultReadOnlyOrig );
 		}
 	}
 
 	public void remove() {
 		if (!single) {
 			throw new UnsupportedOperationException("Not a single column hibernate query result set");
 		}
 		if (currentResult==null) {
 			throw new IllegalStateException("Called Iterator.remove() before next()");
 		}
 		if ( !( types[0] instanceof EntityType ) ) {
 			throw new UnsupportedOperationException("Not an entity");
 		}
 		
 		session.delete( 
 				( (EntityType) types[0] ).getAssociatedEntityName(), 
 				currentResult,
 				false,
 		        null
 			);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
index 24cb64a4e8..25b99c52b2 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
@@ -1,2370 +1,2361 @@
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
 package org.hibernate.impl;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Reader;
 import java.io.Serializable;
 import java.io.ByteArrayOutputStream;
 import java.io.ByteArrayInputStream;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
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
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.LockOptions;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.ActionQueue;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.NonFlushedChanges;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.StatefulPersistenceContext;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
+import org.hibernate.engine.jdbc.internal.JDBCContextImpl;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.engine.query.FilterQueryPlan;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
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
-import org.hibernate.exception.JDBCExceptionHelper;
-import org.hibernate.jdbc.Batcher;
-import org.hibernate.jdbc.JDBCContext;
 import org.hibernate.jdbc.Work;
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
 import org.hibernate.stat.SessionStatisticsImpl;
 import org.hibernate.type.Type;
 import org.hibernate.type.SerializationException;
 import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.StringHelper;
 
 
 /**
  * Concrete implementation of a Session, and also the central, organizing component
  * of Hibernate's internal implementation. As such, this class exposes two interfaces;
  * Session itself, to the application, and SessionImplementor, to other components
  * of Hibernate. This class is not threadsafe.
  *
  * @author Gavin King
  */
 public final class SessionImpl extends AbstractSessionImpl 
 		implements EventSource, org.hibernate.classic.Session, JDBCContext.Context, LobCreationContext {
 
 	// todo : need to find a clean way to handle the "event source" role
 	// a seperate classs responsible for generating/dispatching events just duplicates most of the Session methods...
 	// passing around seperate reto interceptor, factory, actionQueue, and persistentContext is not manageable...
 
 	private static final Logger log = LoggerFactory.getLogger(SessionImpl.class);
 
 	private transient EntityMode entityMode = EntityMode.POJO;
 	private transient boolean autoClear; //for EJB3
 	
 	private transient long timestamp;
 	private transient FlushMode flushMode = FlushMode.AUTO;
 	private transient CacheMode cacheMode = CacheMode.NORMAL;
 
 	private transient Interceptor interceptor;
 
 	private transient int dontFlushFromFind = 0;
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
-	private transient JDBCContext jdbcContext;
+	private transient JDBCContextImpl jdbcContext;
 	private transient EventListeners listeners;
 
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 	private transient ConnectionReleaseMode connectionReleaseMode;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
 	private transient Session rootSession;
 	private transient Map childSessionsByEntityMode;
 
 	private transient EntityNameResolver entityNameResolver = new CoordinatingEntityNameResolver();
 
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
 		this.jdbcContext = parent.jdbcContext;
 		this.interceptor = parent.interceptor;
 		this.listeners = parent.listeners;
 		this.actionQueue = new ActionQueue( this );
 		this.entityMode = entityMode;
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = false;
 		this.autoCloseSessionEnabled = false;
 		this.connectionReleaseMode = null;
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().openSession();
 		}
 
 		log.debug( "opened session [" + entityMode + "]" );
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
-		this.jdbcContext = new JDBCContext( this, connection, interceptor );
+		this.jdbcContext = new JDBCContextImpl( this, connection, interceptor );
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().openSession();
 		}
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( "opened session at timestamp: " + timestamp );
 		}
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
 
-	public Batcher getBatcher() {
-		errorIfClosed();
-		checkTransactionSynchStatus();
-		// TODO : should remove this exposure
-		//  and have all references to the session's batcher use the ConnectionManager.
-		return jdbcContext.getConnectionManager().getBatcher();
-	}
-
 	public long getTimestamp() {
 		checkTransactionSynchStatus();
 		return timestamp;
 	}
 
 	public Connection close() throws HibernateException {
 		log.trace( "closing session" );
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
 				return jdbcContext.getConnectionManager().close();
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
 		checkTransactionSynchStatus();
 		return connectionReleaseMode;
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
 			log.trace( "skipping auto-flush due to session closed" );
 			return;
 		}
 		log.trace("automatically flushing session");
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
 			( ( StatefulPersistenceContext ) pc ).serialize( oos );
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
 		log.trace( "automatically closing session" );
 		close();
 	}
 
 	public Connection connection() throws HibernateException {
 		errorIfClosed();
 		return jdbcContext.borrowConnection();
 	}
 
 	public boolean isConnected() {
 		checkTransactionSynchStatus();
 		return !isClosed() && jdbcContext.getConnectionManager().isCurrentlyConnected();
 	}
 	
 	public boolean isTransactionInProgress() {
 		checkTransactionSynchStatus();
 		return !isClosed() && jdbcContext.isTransactionInProgress();
 	}
 
 	public Connection disconnect() throws HibernateException {
 		errorIfClosed();
 		log.debug( "disconnecting session" );
 		return jdbcContext.getConnectionManager().manualDisconnect();
 	}
 
 	public void reconnect() throws HibernateException {
 		errorIfClosed();
 		log.debug( "reconnecting session" );
 		checkTransactionSynchStatus();
 		jdbcContext.getConnectionManager().manualReconnect();
 	}
 
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
 		log.debug( "reconnecting session" );
 		checkTransactionSynchStatus();
 		jdbcContext.getConnectionManager().manualReconnect( conn );
 	}
 
 	public void beforeTransactionCompletion(Transaction tx) {
 		log.trace( "before transaction completion" );
 		actionQueue.beforeTransactionCompletion();
 		if ( rootSession == null ) {
 			try {
 				interceptor.beforeTransactionCompletion(tx);
 			}
 			catch (Throwable t) {
 				log.error("exception in interceptor beforeTransactionCompletion()", t);
 			}
 		}
 	}
 	
 	public void setAutoClear(boolean enabled) {
 		errorIfClosed();
 		autoClear = enabled;
 	}
 	
 	/**
 	 * Check if there is a Hibernate or JTA transaction in progress and, 
 	 * if there is not, flush if necessary, make sure the connection has 
 	 * been committed (if it is not in autocommit mode) and run the after 
 	 * completion processing
 	 */
 	public void afterOperation(boolean success) {
 		if ( !jdbcContext.isTransactionInProgress() ) {
 			jdbcContext.afterNontransactionalQuery( success );
 		}
 	}
 
 	public void afterTransactionCompletion(boolean success, Transaction tx) {
 		log.trace( "after transaction completion" );
 		persistenceContext.afterTransactionCompletion();
 		actionQueue.afterTransactionCompletion(success);
 		if ( rootSession == null && tx != null ) {
 			try {
 				interceptor.afterTransactionCompletion(tx);
 			}
 			catch (Throwable t) {
 				log.error("exception in interceptor afterTransactionCompletion()", t);
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
 		saveOrUpdate(null, object);
 	}
 
 	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
 		fireSaveOrUpdate( new SaveOrUpdateEvent(entityName, obj, this) );
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
 
 	public void save(Object obj, Serializable id) throws HibernateException {
 		save(null, obj, id);
 	}
 
 	public Serializable save(Object obj) throws HibernateException {
 		return save(null, obj);
 	}
 
 	public Serializable save(String entityName, Object object) throws HibernateException {
 		return fireSave( new SaveOrUpdateEvent(entityName, object, this) );
 	}
 
 	public void save(String entityName, Object object, Serializable id) throws HibernateException {
 		fireSave( new SaveOrUpdateEvent(entityName, object, id, this) );
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
 
 	public void update(Object obj, Serializable id) throws HibernateException {
 		update(null, obj, id);
 	}
 
 	public void update(String entityName, Object object) throws HibernateException {
 		fireUpdate( new SaveOrUpdateEvent(entityName, object, this) );
 	}
 
 	public void update(String entityName, Object object, Serializable id) throws HibernateException {
 		fireUpdate(new SaveOrUpdateEvent(entityName, object, id, this));
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
 		fireLock( new LockEvent( object, options, this) );
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
 		firePersist( new PersistEvent(entityName, object, this) );
 	}
 
 	public void persist(Object object) throws HibernateException {
 		persist(null, object);
 	}
 
 	public void persist(String entityName, Object object, Map copiedAlready)
 	throws HibernateException {
 		firePersist( copiedAlready, new PersistEvent(entityName, object, this) );
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
 		firePersistOnFlush( new PersistEvent(entityName, object, this) );
 	}
 
 	public void persistOnFlush(Object object) throws HibernateException {
 		persist(null, object);
 	}
 
 	public void persistOnFlush(String entityName, Object object, Map copiedAlready)
 			throws HibernateException {
 		firePersistOnFlush( copiedAlready, new PersistEvent(entityName, object, this) );
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
 		return fireMerge( new MergeEvent(entityName, object, this) );
 	}
 
 	public Object merge(Object object) throws HibernateException {
 		return merge(null, object);
 	}
 
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		fireMerge( copiedAlready, new MergeEvent(entityName, object, this) );
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
 
 
 	// saveOrUpdateCopy() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object saveOrUpdateCopy(String entityName, Object object)
 			throws HibernateException {
 		return fireSaveOrUpdateCopy( new MergeEvent(entityName, object, this) );
 	}
 
 	public Object saveOrUpdateCopy(Object object) throws HibernateException {
 		return saveOrUpdateCopy( null, object );
 	}
 
 	public Object saveOrUpdateCopy(String entityName, Object object, Serializable id)
 			throws HibernateException {
 		return fireSaveOrUpdateCopy( new MergeEvent(entityName, object, id, this) );
 	}
 
 	public Object saveOrUpdateCopy(Object object, Serializable id)
 			throws HibernateException {
 		return saveOrUpdateCopy( null, object, id );
 	}
 
 	public void saveOrUpdateCopy(String entityName, Object object, Map copiedAlready)
 			throws HibernateException {
 		fireSaveOrUpdateCopy( copiedAlready, new MergeEvent( entityName, object, this ) );
 	}
 
 	private void fireSaveOrUpdateCopy(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		MergeEventListener[] saveOrUpdateCopyEventListener = listeners.getSaveOrUpdateCopyEventListeners();
 		for ( int i = 0; i < saveOrUpdateCopyEventListener.length; i++ ) {
 			saveOrUpdateCopyEventListener[i].onMerge(event, copiedAlready);
 		}
 	}
 
 	private Object fireSaveOrUpdateCopy(MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		MergeEventListener[] saveOrUpdateCopyEventListener = listeners.getSaveOrUpdateCopyEventListeners();
 		for ( int i = 0; i < saveOrUpdateCopyEventListener.length; i++ ) {
 			saveOrUpdateCopyEventListener[i].onMerge(event);
 		}
 		return event.getResult();
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
 		if ( log.isDebugEnabled() ) {
 			EntityPersister persister = getFactory().getEntityPersister(entityName);
 			log.debug( "initializing proxy: " + MessageHelper.infoString( persister, id, getFactory() ) );
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
 		return load( entityClass.getName(), id, lockOptions);
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
 		return get( entityClass.getName(), id, lockOptions);
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
 	   	fireLoad(event, LoadEventListener.GET);
 		return event.getResult();
 	}
 
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockOptions, this);
 	   	fireLoad(event, LoadEventListener.GET);
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
 		fireRefresh( refreshedAlready, new RefreshEvent(object, this) );
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
 		fireReplicate( new ReplicateEvent(entityName, obj, replicationMode, this) );
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
 		fireEvict( new EvictEvent(object, this) );
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
 		log.debug("checking session dirtiness");
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
 			log.debug("session dirty (scheduled updates and insertions)");
 			return true;
 		}
 		else {
 			DirtyCheckEvent event = new DirtyCheckEvent(this);
 			DirtyCheckEventListener[] dirtyCheckEventListener = listeners.getDirtyCheckEventListeners();
 			for ( int i = 0; i < dirtyCheckEventListener.length; i++ ) {
 				dirtyCheckEventListener[i].onDirtyCheck(event);
 			}
 			return event.isDirty();
 		}
 	}
 
 	public void flush() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new HibernateException("Flush during cascade is dangerous");
 		}
 		FlushEventListener[] flushEventListener = listeners.getFlushEventListeners();
 		for ( int i = 0; i < flushEventListener.length; i++ ) {
 			flushEventListener[i].onFlush( new FlushEvent(this) );
 		}
 	}
 
 	public void forceFlush(EntityEntry entityEntry) throws HibernateException {
 		errorIfClosed();
 		if ( log.isDebugEnabled() ) {
 			log.debug(
 				"flushing to force deletion of re-saved object: " +
 				MessageHelper.infoString( entityEntry.getPersister(), entityEntry.getId(), getFactory() )
 			);
 		}
 
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new ObjectDeletedException(
 				"deleted object would be re-saved by cascade (remove deleted object from associations)",
 				entityEntry.getId(),
 				entityEntry.getPersister().getEntityName()
 			);
 		}
 
 		flush();
 	}
 
 
 	/**
 	 * Retrieve a list of persistent objects using a hibernate query
 	 */
 	public List find(String query) throws HibernateException {
 		return list( query, new QueryParameters() );
 	}
 
 	public List find(String query, Object value, Type type) throws HibernateException {
 		return list( query, new QueryParameters(type, value) );
 	}
 
 	public List find(String query, Object[] values, Type[] types) throws HibernateException {
 		return list( query, new QueryParameters(types, values) );
 	}
 
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		List results = CollectionHelper.EMPTY_LIST;
 		boolean success = false;
 
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		return result;
 	}
 
     public int executeNativeUpdate(NativeSQLQuerySpecification nativeQuerySpecification,
             QueryParameters queryParameters) throws HibernateException {
         errorIfClosed();
         checkTransactionSynchStatus();
         queryParameters.validateParameters();
         NativeSQLQueryPlan plan = getNativeSQLQueryPlan(nativeQuerySpecification);
 
         
         autoFlushIfRequired( plan.getCustomQuery().getQuerySpaces() );
         
         boolean success = false;
         int result = 0;
         try {
             result = plan.performExecuteUpdate(queryParameters, this);
             success = true;
         } finally {
             afterOperation(success);
         }
         return result;
     }
 
 	public Iterator iterate(String query) throws HibernateException {
 		return iterate( query, new QueryParameters() );
 	}
 
 	public Iterator iterate(String query, Object value, Type type) throws HibernateException {
 		return iterate( query, new QueryParameters(type, value) );
 	}
 
 	public Iterator iterate(String query, Object[] values, Type[] types) throws HibernateException {
 		return iterate( query, new QueryParameters(types, values) );
 	}
 
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, true );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return plan.performIterate( queryParameters, this );
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return plan.performScroll( queryParameters, this );
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public int delete(String query) throws HibernateException {
 		return delete( query, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY );
 	}
 
 	public int delete(String query, Object value, Type type) throws HibernateException {
 		return delete( query, new Object[]{value}, new Type[]{type} );
 	}
 
 	public int delete(String query, Object[] values, Type[] types) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( query == null ) {
 			throw new IllegalArgumentException("attempt to doAfterTransactionCompletion delete-by-query with null query");
 		}
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "delete: " + query );
 			if ( values.length != 0 ) {
 				log.trace( "parameters: " + StringHelper.toString( values ) );
 			}
 		}
 
 		List list = find( query, values, types );
 		int deletionCount = list.size();
 		for ( int i = 0; i < deletionCount; i++ ) {
 			delete( list.get( i ) );
 		}
 
 		return deletionCount;
 	}
 
 	public Query createFilter(Object collection, String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		CollectionFilterImpl filter = new CollectionFilterImpl(
 				queryString,
 		        collection,
 		        this,
 		        getFilterQueryPlan( collection, queryString, null, false ).getParameterMetadata()
 		);
 		filter.setComment( queryString );
 		return filter;
 	}
 	
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.getNamedQuery(queryName);
 	}
 
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( factory.getEntityPersister(entityName), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Object result = interceptor.instantiate( persister.getEntityName(), entityMode, id );
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		return result;
 	}
 
 	public EntityMode getEntityMode() {
 		checkTransactionSynchStatus();
 		return entityMode;
 	}
 
 	public void setFlushMode(FlushMode flushMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( log.isTraceEnabled() ) {
 			log.trace("setting flush mode to: " + flushMode);
 		}
 		this.flushMode = flushMode;
 	}
 	
 	public FlushMode getFlushMode() {
 		checkTransactionSynchStatus();
 		return flushMode;
 	}
 
 	public CacheMode getCacheMode() {
 		checkTransactionSynchStatus();
 		return cacheMode;
 	}
 	
 	public void setCacheMode(CacheMode cacheMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( log.isTraceEnabled() ) {
 			log.trace("setting cache mode to: " + cacheMode);
 		}
 		this.cacheMode= cacheMode; 
 	}
 
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return jdbcContext.getTransaction();
 	}
 	
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		if ( rootSession != null ) {
 			// todo : should seriously consider not allowing a txn to begin from a child session
 			//      can always route the request to the root session...
 			log.warn( "Transaction started on non-root session" );
 		}
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 	
 	public void afterTransactionBegin(Transaction tx) {
 		errorIfClosed();
 		interceptor.afterTransactionBegin(tx);
 	}
 
 	public EntityPersister getEntityPersister(final String entityName, final Object object) {
 		errorIfClosed();
 		if (entityName==null) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			// try block is a hack around fact that currently tuplizers are not
 			// given the opportunity to resolve a subclass entity name.  this
 			// allows the (we assume custom) interceptor the ability to
 			// influence this decision if we were not able to based on the
 			// given entityName
 			try {
 				return factory.getEntityPersister( entityName )
 						.getSubclassEntityPersister( object, getFactory(), entityMode );
 			}
 			catch( HibernateException e ) {
 				try {
 					return getEntityPersister( null, object );
 				}
 				catch( HibernateException e2 ) {
 					throw e;
 				}
 			}
 		}
 	}
 
 	// not for internal use:
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.getSession() != this ) {
 				throw new TransientObjectException( "The proxy was not associated with this session" );
 			}
 			return li.getIdentifier();
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			if ( entry == null ) {
 				throw new TransientObjectException( "The instance was not associated with this session" );
 			}
 			return entry.getId();
 		}
 	}
 
 	/**
 	 * Get the id value for an object that is actually associated with the session. This
 	 * is a bit stricter than getEntityIdentifierIfNotUnsaved().
 	 */
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		if ( object instanceof HibernateProxy ) {
 			return getProxyIdentifier(object);
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			return entry != null ? entry.getId() : null;
 		}
 	}
 	
 	private Serializable getProxyIdentifier(Object proxy) {
 		return ( (HibernateProxy) proxy ).getHibernateLazyInitializer().getIdentifier();
 	}
 
 	public Collection filter(Object collection, String filter) throws HibernateException {
 		return listFilter( collection, filter, new QueryParameters( new Type[1], new Object[1] ) );
 	}
 
 	public Collection filter(Object collection, String filter, Object value, Type type) throws HibernateException {
 		return listFilter( collection, filter, new QueryParameters( new Type[]{null, type}, new Object[]{null, value} ) );
 	}
 
 	public Collection filter(Object collection, String filter, Object[] values, Type[] types)
 	throws HibernateException {
 		Object[] vals = new Object[values.length + 1];
 		Type[] typs = new Type[types.length + 1];
 		System.arraycopy( values, 0, vals, 1, values.length );
 		System.arraycopy( types, 0, typs, 1, types.length );
 		return listFilter( collection, filter, new QueryParameters( typs, vals ) );
 	}
 
 	private FilterQueryPlan getFilterQueryPlan(
 			Object collection,
 			String filter,
 			QueryParameters parameters,
 			boolean shallow) throws HibernateException {
 		if ( collection == null ) {
 			throw new NullPointerException( "null collection passed to filter" );
 		}
 
 		CollectionEntry entry = persistenceContext.getCollectionEntryOrNull( collection );
 		final CollectionPersister roleBeforeFlush = (entry == null) ? null : entry.getLoadedPersister();
 
 		FilterQueryPlan plan = null;
 		if ( roleBeforeFlush == null ) {
 			// if it was previously unreferenced, we need to flush in order to
 			// get its state into the database in order to execute query
 			flush();
 			entry = persistenceContext.getCollectionEntryOrNull( collection );
 			CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 			if ( roleAfterFlush == null ) {
 				throw new QueryException( "The collection was unreferenced" );
 			}
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 		}
 		else {
 			// otherwise, we only need to flush if there are in-memory changes
 			// to the queried tables
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleBeforeFlush.getRole(), shallow, getEnabledFilters() );
 			if ( autoFlushIfRequired( plan.getQuerySpaces() ) ) {
 				// might need to run a different filter entirely after the flush
 				// because the collection role may have changed
 				entry = persistenceContext.getCollectionEntryOrNull( collection );
 				CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 				if ( roleBeforeFlush != roleAfterFlush ) {
 					if ( roleAfterFlush == null ) {
 						throw new QueryException( "The collection was dereferenced" );
 					}
 					plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 				}
 			}
 		}
 
 		if ( parameters != null ) {
 			parameters.getPositionalParameterValues()[0] = entry.getLoadedKey();
 			parameters.getPositionalParameterTypes()[0] = entry.getLoadedPersister().getKeyType();
 		}
 
 		return plan;
 	}
 
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, false );
 		List results = CollectionHelper.EMPTY_LIST;
 
 		boolean success = false;
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		return plan.performIterate( queryParameters, this );
 	}
 
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteria.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteria,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public List list(CriteriaImpl criteria) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteria.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteria,
 					implementors[i],
 					getLoadQueryInfluencers()
 				);
 
 			spaces.addAll( loaders[i].getQuerySpaces() );
 
 		}
 
 		autoFlushIfRequired(spaces);
 
 		List results = Collections.EMPTY_LIST;
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			for( int i=0; i<size; i++ ) {
 				final List currentResults = loaders[i].list(this);
 				currentResults.addAll(results);
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
 	public boolean contains(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			//do not use proxiesByKey, since not all
 			//proxies that point to this session's
 			//instances are in that collection!
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				//if it is an uninitialized proxy, pointing
 				//with this session, then when it is accessed,
 				//the underlying instance will be "contained"
 				return li.getSession()==this;
 			}
 			else {
 				//if it is initialized, see if the underlying
 				//instance is contained, since we need to 
 				//account for the fact that it might have been
 				//evicted
 				object = li.getImplementation();
 			}
 		}
 		// A session is considered to contain an entity only if the entity has
 		// an entry in the session's persistence context and the entry reports
 		// that the entity has not been removed
 		EntityEntry entry = persistenceContext.getEntry( object );
 		return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 	}
 	
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createQuery(queryString);
 	}
 	
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createSQLQuery(sql);
 	}
 
 	public Query createSQLQuery(String sql, String returnAlias, Class returnClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new SQLQueryImpl(
 				sql,
 		        new String[] { returnAlias },
 		        new Class[] { returnClass },
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 	}
 
 	public Query createSQLQuery(String sql, String returnAliases[], Class returnClasses[]) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new SQLQueryImpl(
 				sql,
 		        returnAliases,
 		        returnClasses,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 	}
 
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "scroll SQL query: " + customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return loader.scroll(queryParameters, this);
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) 
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "SQL query: " + customQuery.getSQL() );
 		}
 		
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			List results = loader.list(this, queryParameters);
 			success = true;
 			return results;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 	}
 
 	public SessionFactory getSessionFactory() {
 		checkTransactionSynchStatus();
 		return factory;
 	}
 	
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		InitializeCollectionEventListener[] listener = listeners.getInitializeCollectionEventListeners();
 		for ( int i = 0; i < listener.length; i++ ) {
 			listener[i].onInitializeCollection( new InitializeCollectionEvent(collection, this) );
 		}
 	}
 
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			LazyInitializer initializer = ( ( HibernateProxy ) object ).getHibernateLazyInitializer();
 			// it is possible for this method to be called during flush processing,
 			// so make certain that we do not accidently initialize an uninitialized proxy
 			if ( initializer.isUninitialized() ) {
 				return initializer.getEntityName();
 			}
 			object = initializer.getImplementation();
 		}
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if (entry==null) {
 			return guessEntityName(object);
 		}
 		else {
 			return entry.getPersister().getEntityName();
 		}
 	}
 	
 	public String getEntityName(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if (object instanceof HibernateProxy) {
 			if ( !persistenceContext.containsProxy( object ) ) {
 				throw new TransientObjectException("proxy was not associated with the session");
 			}
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if ( entry == null ) {
 			throwTransientObjectException( object );
 		}
 		return entry.getPersister().getEntityName();
 	}
 
 	private void throwTransientObjectException(Object object) throws HibernateException {
 		throw new TransientObjectException(
 				"object references an unsaved transient instance - save the transient instance before flushing: " +
 				guessEntityName(object)
 			);
 	}
 
 	public String guessEntityName(Object object) throws HibernateException {
 		errorIfClosed();
 		return entityNameResolver.resolveEntityName( object );
 	}
 
 	public void cancelQuery() throws HibernateException {
 		errorIfClosed();
-		getBatcher().cancelLastQuery();
+		getJDBCContext().getConnectionManager().cancelLastQuery();
 	}
 
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
 	public String toString() {
 		StringBuffer buf = new StringBuffer(500)
 			.append( "SessionImpl(" );
 		if ( !isClosed() ) {
 			buf.append(persistenceContext)
 				.append(";")
 				.append(actionQueue);
 		}
 		else {
 			buf.append("<closed>");
 		}
 		return buf.append(')').toString();
 	}
 
 	public EventListeners getListeners() {
 		return listeners;
 	}
 
 	public ActionQueue getActionQueue() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 	
 	public PersistenceContext getPersistenceContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 	
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl(this);
 	}
 
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
 	public boolean isReadOnly(Object entityOrProxy) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );	
 	}
 
 	public void setReadOnly(Object entity, boolean readOnly) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly(entity, readOnly);
 	}
 
 	public void doWork(Work work) throws HibernateException {
 		try {
 			work.execute( jdbcContext.getConnectionManager().getConnection() );
 			jdbcContext.getConnectionManager().afterStatement();
 		}
 		catch ( SQLException e ) {
 			throw factory.getSQLExceptionHelper().convert( e, "error executing work" );
 		}
 	}
 
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	public JDBCContext getJDBCContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return jdbcContext;
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter enableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void disableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object getFilterParameterValue(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterValue( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type getFilterParameterType(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterType( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Map getEnabledFilters() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilters();
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String getFetchProfile() {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getInternalFetchProfile();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setFetchProfile(String fetchProfile) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.setInternalFetchProfile( fetchProfile );
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 
 	private void checkTransactionSynchStatus() {
 		if ( jdbcContext != null && !isClosed() ) {
 			jdbcContext.registerSynchronizationIfPossible();
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param ois The input stream from which we are being read...
 	 * @throws IOException Indicates a general IO stream exception
 	 * @throws ClassNotFoundException Indicates a class resolution issue
 	 */
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		log.trace( "deserializing session" );
 
 		ois.defaultReadObject();
 
 		entityNameResolver = new CoordinatingEntityNameResolver();
 
 		boolean isRootSession = ois.readBoolean();
 		connectionReleaseMode = ConnectionReleaseMode.parse( ( String ) ois.readObject() );
 		entityMode = EntityMode.parse( ( String ) ois.readObject() );
 		autoClear = ois.readBoolean();
 		flushMode = FlushMode.parse( ( String ) ois.readObject() );
 		cacheMode = CacheMode.parse( ( String ) ois.readObject() );
 		flushBeforeCompletionEnabled = ois.readBoolean();
 		autoCloseSessionEnabled = ois.readBoolean();
 		interceptor = ( Interceptor ) ois.readObject();
 
 		factory = SessionFactoryImpl.deserialize( ois );
 		listeners = factory.getEventListeners();
 
 		if ( isRootSession ) {
-			jdbcContext = JDBCContext.deserialize( ois, this, interceptor );
+			jdbcContext = JDBCContextImpl.deserialize( ois, this, interceptor );
 		}
 
 		persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		actionQueue = ActionQueue.deserialize( ois, this );
 
 		loadQueryInfluencers = ( LoadQueryInfluencers ) ois.readObject();
 
 		childSessionsByEntityMode = ( Map ) ois.readObject();
 
 		// LoadQueryInfluencers.getEnabledFilters() tries to validate each enabled
 		// filter, which will fail when called before FilterImpl.afterDeserialize( factory );
 		// Instead lookup the filter by name and then call FilterImpl.afterDeserialize( factory ).
 		Iterator iter = loadQueryInfluencers.getEnabledFilterNames().iterator();
 		while ( iter.hasNext() ) {
 			String filterName = ( String ) iter.next();
 			 ( ( FilterImpl ) loadQueryInfluencers.getEnabledFilter( filterName )  )
 					.afterDeserialize( factory );
 		}
 
 		if ( isRootSession && childSessionsByEntityMode != null ) {
 			iter = childSessionsByEntityMode.values().iterator();
 			while ( iter.hasNext() ) {
 				final SessionImpl child = ( ( SessionImpl ) iter.next() );
 				child.rootSession = this;
 				child.jdbcContext = this.jdbcContext;
 			}
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param oos The output stream to which we are being written...
 	 * @throws IOException Indicates a general IO stream exception
 	 */
 	private void writeObject(ObjectOutputStream oos) throws IOException {
-		if ( !jdbcContext.getConnectionManager().isReadyForSerialization() ) {
+		if ( !jdbcContext.isReadyForSerialization() ) {
 			throw new IllegalStateException( "Cannot serialize a session while connected" );
 		}
 
 		log.trace( "serializing session" );
 
 		oos.defaultWriteObject();
 
 		oos.writeBoolean( rootSession == null );
 		oos.writeObject( connectionReleaseMode.toString() );
 		oos.writeObject( entityMode.toString() );
 		oos.writeBoolean( autoClear );
 		oos.writeObject( flushMode.toString() );
 		oos.writeObject( cacheMode.toString() );
 		oos.writeBoolean( flushBeforeCompletionEnabled );
 		oos.writeBoolean( autoCloseSessionEnabled );
 		// we need to writeObject() on this since interceptor is user defined
 		oos.writeObject( interceptor );
 
 		factory.serialize( oos );
 
 		if ( rootSession == null ) {
 			jdbcContext.serialize( oos );
 		}
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 		oos.writeObject( childSessionsByEntityMode );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object execute(Callback callback) {
 		Connection connection = jdbcContext.getConnectionManager().getConnection();
 		try {
 			return callback.executeOnConnection( connection );
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"Error creating contextual LOB : " + e.getMessage()
 			);
 		}
 		finally {
 			jdbcContext.getConnectionManager().afterStatement();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypeHelper getTypeHelper() {
 		return getSessionFactory().getTypeHelper();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public LobHelper getLobHelper() {
 		if ( lobHelper == null ) {
 			lobHelper = new LobHelperImpl( this );
 		}
 		return lobHelper;
 	}
 
 	private transient LobHelperImpl lobHelper;
 
 	private static class LobHelperImpl implements LobHelper {
 		private final SessionImpl session;
 
 		private LobHelperImpl(SessionImpl session) {
 			this.session = session;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Blob createBlob(byte[] bytes) {
 			return lobCreator().createBlob( bytes );
 		}
 
 		private LobCreator lobCreator() {
 			return session.getFactory().getSettings().getJdbcSupport().getLobCreator( session );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Blob createBlob(InputStream stream, long length) {
 			return lobCreator().createBlob( stream, length );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createClob(String string) {
 			return lobCreator().createClob( string );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createClob(Reader reader, long length) {
 			return lobCreator().createClob( reader, length );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createNClob(String string) {
 			return lobCreator().createNClob( string );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createNClob(Reader reader, long length) {
 			return lobCreator().createNClob( reader, length );
 		}
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
 		public String resolveEntityName(Object entity) {
 			String entityName = interceptor.getEntityName( entity );
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			Iterator itr = factory.iterateEntityNameResolvers( entityMode );
 			while ( itr.hasNext() ) {
 				final EntityNameResolver resolver = ( EntityNameResolver ) itr.next();
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			// the old-time stand-by...
 			return entity.getClass().getName();
 		}
 	}
 
 	private class LockRequestImpl implements LockRequest {
 		private final LockOptions lockOptions;
 		private LockRequestImpl(LockOptions lo) {
 			lockOptions = new LockOptions();
 			LockOptions.copy(lo, lockOptions);
 		}
 
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode(lockMode);
 			return this;
 		}
 
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut(timeout);
 			return this;
 		}
 
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope(scope);
 			return this;
 		}
 
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
 		public void lock(Object object) throws HibernateException {
 			fireLock( object, lockOptions );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
index 39104b889b..c18b38f2fd 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
@@ -1,701 +1,695 @@
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
 package org.hibernate.impl;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.StatelessSession;
 import org.hibernate.Transaction;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.StatefulPersistenceContext;
 import org.hibernate.engine.Versioning;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.NonFlushedChanges;
+import org.hibernate.engine.jdbc.internal.JDBCContextImpl;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.event.EventListeners;
 import org.hibernate.id.IdentifierGeneratorHelper;
-import org.hibernate.jdbc.Batcher;
-import org.hibernate.jdbc.JDBCContext;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 import org.hibernate.util.CollectionHelper;
 
 /**
  * @author Gavin King
  */
 public class StatelessSessionImpl extends AbstractSessionImpl
 		implements JDBCContext.Context, StatelessSession {
 
 	private static final Logger log = LoggerFactory.getLogger( StatelessSessionImpl.class );
 
-	private JDBCContext jdbcContext;
+	private JDBCContextImpl jdbcContext;
 	private PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );
 
 	StatelessSessionImpl(Connection connection, SessionFactoryImpl factory) {
 		super( factory );
-		this.jdbcContext = new JDBCContext( this, connection, EmptyInterceptor.INSTANCE );
+		this.jdbcContext = new JDBCContextImpl( this, connection, EmptyInterceptor.INSTANCE );
 	}
 
 
 	// inserts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Serializable insert(Object entity) {
 		errorIfClosed();
 		return insert(null, entity);
 	}
 
 	public Serializable insert(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifierGenerator().generate(this, entity);
 		Object[] state = persister.getPropertyValues(entity, EntityMode.POJO);
 		if ( persister.isVersioned() ) {
 			boolean substitute = Versioning.seedVersion(state, persister.getVersionProperty(), persister.getVersionType(), this);
 			if ( substitute ) {
 				persister.setPropertyValues( entity, state, EntityMode.POJO );
 			}
 		}
 		if ( id == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			id = persister.insert(state, entity, this);
 		}
 		else {
 			persister.insert(id, state, entity, this);
 		}
 		persister.setIdentifier( entity, id, this );
 		return id;
 	}
 
 
 	// deletes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void delete(Object entity) {
 		errorIfClosed();
 		delete(null, entity);
 	}
 
 	public void delete(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object version = persister.getVersion(entity, EntityMode.POJO);
 		persister.delete(id, version, entity, this);
 	}
 
 
 	// updates ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void update(Object entity) {
 		errorIfClosed();
 		update(null, entity);
 	}
 
 	public void update(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object[] state = persister.getPropertyValues(entity, EntityMode.POJO);
 		Object oldVersion;
 		if ( persister.isVersioned() ) {
 			oldVersion = persister.getVersion(entity, EntityMode.POJO);
 			Object newVersion = Versioning.increment( oldVersion, persister.getVersionType(), this );
 			Versioning.setVersion(state, newVersion, persister);
 			persister.setPropertyValues(entity, state, EntityMode.POJO);
 		}
 		else {
 			oldVersion = null;
 		}
 		persister.update(id, state, null, false, null, oldVersion, entity, null, this);
 	}
 
 
 	// loading ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object get(Class entityClass, Serializable id) {
 		return get( entityClass.getName(), id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	public Object get(String entityName, Serializable id) {
 		return get(entityName, id, LockMode.NONE);
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		errorIfClosed();
 		Object result = getFactory().getEntityPersister(entityName)
 				.load(id, null, lockMode, this);
 		if ( temporaryPersistenceContext.isLoadFinished() ) {
 			temporaryPersistenceContext.clear();
 		}
 		return result;
 	}
 
 	public void refresh(Object entity) {
 		refresh( bestGuessEntityName( entity ), entity, LockMode.NONE );
 	}
 
 	public void refresh(String entityName, Object entity) {
 		refresh( entityName, entity, LockMode.NONE );
 	}
 
 	public void refresh(Object entity, LockMode lockMode) {
 		refresh( bestGuessEntityName( entity ), entity, lockMode );
 	}
 
 	public void refresh(String entityName, Object entity, LockMode lockMode) {
 		final EntityPersister persister = this.getEntityPersister( entityName, entity );
 		final Serializable id = persister.getIdentifier( entity, this );
 		if ( log.isTraceEnabled() ) {
 			log.trace(
 					"refreshing transient " +
 					MessageHelper.infoString( persister, id, this.getFactory() )
 			);
 		}
 		// TODO : can this ever happen???
 //		EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
 //		if ( source.getPersistenceContext().getEntry( key ) != null ) {
 //			throw new PersistentObjectException(
 //					"attempted to refresh transient instance when persistent " +
 //					"instance was already associated with the Session: " +
 //					MessageHelper.infoString( persister, id, source.getFactory() )
 //			);
 //		}
 
 		if ( persister.hasCache() ) {
 			final CacheKey ck = new CacheKey(
 					id,
 			        persister.getIdentifierType(),
 			        persister.getRootEntityName(),
 			        this.getEntityMode(),
 			        this.getFactory()
 			);
 			persister.getCacheAccessStrategy().evict( ck );
 		}
 
 		String previousFetchProfile = this.getFetchProfile();
 		Object result = null;
 		try {
 			this.setFetchProfile( "refresh" );
 			result = persister.load( id, entity, lockMode, this );
 		}
 		finally {
 			this.setFetchProfile( previousFetchProfile );
 		}
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 	}
 
 	public Object immediateLoad(String entityName, Serializable id)
 			throws HibernateException {
 		throw new SessionException("proxies cannot be fetched by a stateless session");
 	}
 
 	public void initializeCollection(
 			PersistentCollection collection,
 	        boolean writing) throws HibernateException {
 		throw new SessionException("collections cannot be fetched by a stateless session");
 	}
 
 	public Object instantiate(
 			String entityName,
 	        Serializable id) throws HibernateException {
 		errorIfClosed();
 		return getFactory().getEntityPersister( entityName )
 				.instantiate( id, this );
 	}
 
 	public Object internalLoad(
 			String entityName,
 	        Serializable id,
 	        boolean eager,
 	        boolean nullable) throws HibernateException {
 		errorIfClosed();
 		EntityPersister persister = getFactory().getEntityPersister( entityName );
 		// first, try to load it from the temp PC associated to this SS
 		Object loaded = temporaryPersistenceContext.getEntity( new EntityKey( id, persister, getEntityMode() ) );
 		if ( loaded != null ) {
 			// we found it in the temp PC.  Should indicate we are in the midst of processing a result set
 			// containing eager fetches via join fetch
 			return loaded;
 		}
 		if ( !eager && persister.hasProxy() ) {
 			// if the metadata allowed proxy creation and caller did not request forceful eager loading,
 			// generate a proxy
 			return persister.createProxy( id, this );
 		}
 		// otherwise immediately materialize it
 		return get( entityName, id );
 	}
 
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 
 	public boolean isOpen() {
 		return !isClosed();
 	}
 
 	public void close() {
 		managedClose();
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return factory.getSettings().getConnectionReleaseMode();
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return factory.getSettings().isAutoCloseSessionEnabled();
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return true;
 	}
 
 	public boolean isFlushModeNever() {
 		return false;
 	}
 
 	public void managedClose() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed!" );
 		}
 		jdbcContext.getConnectionManager().close();
 		setClosed();
 	}
 
 	public void managedFlush() {
 		errorIfClosed();
-		getBatcher().executeBatch();
+		getJDBCContext().getConnectionManager().executeBatch();
 	}
 
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 	public void afterTransactionCompletion(boolean successful, Transaction tx) {}
 
 	public void beforeTransactionCompletion(Transaction tx) {}
 
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 		return guessEntityName(object);
 	}
 
 	public Connection connection() {
 		errorIfClosed();
 		return jdbcContext.borrowConnection();
 	}
 
 	public int executeUpdate(String query, QueryParameters queryParameters)
 			throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
-	public Batcher getBatcher() {
-		errorIfClosed();
-		return jdbcContext.getConnectionManager()
-				.getBatcher();
-	}
-
 	public CacheMode getCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
 	public int getDontFlushFromFind() {
 		return 0;
 	}
 
 	public Map getEnabledFilters() {
 		return CollectionHelper.EMPTY_MAP;
 	}
 
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		return null;
 	}
 
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	public EntityPersister getEntityPersister(String entityName, Object object)
 			throws HibernateException {
 		errorIfClosed();
 		if ( entityName==null ) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			return factory.getEntityPersister( entityName )
 					.getSubclassEntityPersister( object, getFactory(), EntityMode.POJO );
 		}
 	}
 
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		return null;
 	}
 
 	public Type getFilterParameterType(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object getFilterParameterValue(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public FlushMode getFlushMode() {
 		return FlushMode.COMMIT;
 	}
 
 	public Interceptor getInterceptor() {
 		return EmptyInterceptor.INSTANCE;
 	}
 
 	public EventListeners getListeners() {
 		throw new UnsupportedOperationException();
 	}
 
 	public PersistenceContext getPersistenceContext() {
 		return temporaryPersistenceContext;
 	}
 
 	public long getTimestamp() {
 		throw new UnsupportedOperationException();
 	}
 
 	public String guessEntityName(Object entity) throws HibernateException {
 		errorIfClosed();
 		return entity.getClass().getName();
 	}
 
 
 	public boolean isConnected() {
 		return jdbcContext.getConnectionManager().isCurrentlyConnected();
 	}
 
 	public boolean isTransactionInProgress() {
 		return jdbcContext.isTransactionInProgress();
 	}
 
 	public void setAutoClear(boolean enabled) {
 		throw new UnsupportedOperationException();
 	}
 
 	public void setCacheMode(CacheMode cm) {
 		throw new UnsupportedOperationException();
 	}
 
 	public void setFlushMode(FlushMode fm) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return jdbcContext.getTransaction();
 	}
 
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
 	public boolean isEventSource() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean readOnly) throws HibernateException {
 		if ( readOnly == true ) {
 			throw new UnsupportedOperationException();
 		}
 	}
 
 /////////////////////////////////////////////////////////////////////////////////////////////////////
 
 	//TODO: COPY/PASTE FROM SessionImpl, pull up!
 
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		List results = CollectionHelper.EMPTY_LIST;
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	public void afterOperation(boolean success) {
 		if ( !jdbcContext.isTransactionInProgress() ) {
 			jdbcContext.afterNontransactionalQuery(success);
 		}
 	}
 
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
 		errorIfClosed();
 		String entityName = criteria.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable( entityName ),
 		        factory,
 		        criteria,
 		        entityName,
 		        getLoadQueryInfluencers()
 		);
 		return loader.scroll(this, scrollMode);
 	}
 
 	public List list(CriteriaImpl criteria) throws HibernateException {
 		errorIfClosed();
 		String[] implementors = factory.getImplementors( criteria.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		for( int i=0; i <size; i++ ) {
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 			        factory,
 			        criteria,
 			        implementors[i],
 			        getLoadQueryInfluencers()
 			);
 		}
 
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 		try {
 			for( int i=0; i<size; i++ ) {
 				final List currentResults = loaders[i].list(this);
 				currentResults.addAll(results);
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		boolean success = false;
 		List results;
 		try {
 			results = loader.list(this, queryParameters);
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll(queryParameters, this);
 	}
 
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		return plan.performScroll( queryParameters, this );
 	}
 
 	public void afterScrollOperation() {
 		temporaryPersistenceContext.clear();
 	}
 
 	public void flush() {}
 
 	public NonFlushedChanges getNonFlushedChanges() {
 		throw new UnsupportedOperationException();
 	}
 
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) {
 		throw new UnsupportedOperationException();
 	}
 
 	public String getFetchProfile() {
 		return null;
 	}
 
 	public JDBCContext getJDBCContext() {
 		return jdbcContext;
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return LoadQueryInfluencers.NONE;
 	}
 
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
 		errorIfClosed();
 		// nothing to do
 	}
 
 	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id) {
 		errorIfClosed();
 		// not in any meaning we need to worry about here.
 		return false;
 	}
 
 	public void setFetchProfile(String name) {}
 
 	public void afterTransactionBegin(Transaction tx) {}
 
 	protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
 		// no auto-flushing to support in stateless session
 		return false;
 	}
 	
 	public int executeNativeUpdate(NativeSQLQuerySpecification nativeSQLQuerySpecification,
 			QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		NativeSQLQueryPlan plan = getNativeSQLQueryPlan(nativeSQLQuerySpecification);
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate(queryParameters, this);
 			success = true;
 		} finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
index ca225e62c5..d619475881 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
@@ -1,610 +1,161 @@
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
 
-import java.sql.CallableStatement;
-import java.sql.Connection;
 import java.sql.PreparedStatement;
-import java.sql.ResultSet;
 import java.sql.SQLException;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.ConcurrentModificationException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
-import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
-import org.hibernate.Interceptor;
-import org.hibernate.ScrollMode;
-import org.hibernate.TransactionException;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
-import org.hibernate.exception.JDBCExceptionHelper;
-import org.hibernate.jdbc.util.FormatStyle;
-import org.hibernate.util.JDBCExceptionReporter;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 
 /**
  * Manages prepared statements and batching.
  *
  * @author Gavin King
  */
 public abstract class AbstractBatcher implements Batcher {
 
-	private static int globalOpenPreparedStatementCount;
-	private static int globalOpenResultSetCount;
-
-	private int openPreparedStatementCount;
-	private int openResultSetCount;
-
 	protected static final Logger log = LoggerFactory.getLogger( AbstractBatcher.class );
 
-	private final ConnectionManager connectionManager;
-	private final SessionFactoryImplementor factory;
+	private final SQLExceptionHelper exceptionHelper;
+	private final int jdbcBatchSize;
 
 	private PreparedStatement batchUpdate;
 	private String batchUpdateSQL;
+	private boolean isClosingBatchUpdate = false;
 
-	private HashSet statementsToClose = new HashSet();
-	private HashSet resultSetsToClose = new HashSet();
-	private PreparedStatement lastQuery;
-
-	private boolean releasing = false;
-	private final Interceptor interceptor;
-
-	private long transactionTimeout = -1;
-	boolean isTransactionTimeoutSet;
-
-	public AbstractBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-		this.connectionManager = connectionManager;
-		this.interceptor = interceptor;
-		this.factory = connectionManager.getFactory();
-	}
-
-	public void setTransactionTimeout(int seconds) {
-		isTransactionTimeoutSet = true;
-		transactionTimeout = System.currentTimeMillis() / 1000 + seconds;
-	}
-
-	public void unsetTransactionTimeout() {
-		isTransactionTimeoutSet = false;
-	}
-
-	protected PreparedStatement getStatement() {
-		return batchUpdate;
-	}
-
-	public CallableStatement prepareCallableStatement(String sql)
-	throws SQLException, HibernateException {
-		executeBatch();
-		logOpenPreparedStatement();
-		return getCallableStatement( connectionManager.getConnection(), sql, false);
-	}
-
-	public PreparedStatement prepareStatement(String sql)
-	throws SQLException, HibernateException {
-		return prepareStatement( sql, false );
-	}
-
-	public PreparedStatement prepareStatement(String sql, boolean getGeneratedKeys)
-			throws SQLException, HibernateException {
-		executeBatch();
-		logOpenPreparedStatement();
-		return getPreparedStatement(
-				connectionManager.getConnection(),
-		        sql,
-		        false,
-		        getGeneratedKeys,
-		        null,
-		        null,
-		        false
-		);
-	}
-
-	public PreparedStatement prepareStatement(String sql, String[] columnNames)
-			throws SQLException, HibernateException {
-		executeBatch();
-		logOpenPreparedStatement();
-		return getPreparedStatement(
-				connectionManager.getConnection(),
-		        sql,
-		        false,
-		        false,
-		        columnNames,
-		        null,
-		        false
-		);
+	public AbstractBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
+		this.exceptionHelper = exceptionHelper;
+		this.jdbcBatchSize = jdbcBatchSize;
 	}
 
-	public PreparedStatement prepareSelectStatement(String sql)
-			throws SQLException, HibernateException {
-		logOpenPreparedStatement();
-		return getPreparedStatement(
-				connectionManager.getConnection(),
-		        sql,
-		        false,
-		        false,
-		        null,
-		        null,
-		        false
-		);
+	public final int getJdbcBatchSize() {
+		return jdbcBatchSize;
 	}
 
-	public PreparedStatement prepareQueryStatement(
-			String sql,
-	        boolean scrollable,
-	        ScrollMode scrollMode) throws SQLException, HibernateException {
-		logOpenPreparedStatement();
-		PreparedStatement ps = getPreparedStatement(
-				connectionManager.getConnection(),
-		        sql,
-		        scrollable,
-		        scrollMode
-		);
-		setStatementFetchSize( ps );
-		statementsToClose.add( ps );
-		lastQuery = ps;
-		return ps;
-	}
-
-	public CallableStatement prepareCallableQueryStatement(
-			String sql,
-	        boolean scrollable,
-	        ScrollMode scrollMode) throws SQLException, HibernateException {
-		logOpenPreparedStatement();
-		CallableStatement ps = ( CallableStatement ) getPreparedStatement(
-				connectionManager.getConnection(),
-		        sql,
-		        scrollable,
-		        false,
-		        null,
-		        scrollMode,
-		        true
-		);
-		setStatementFetchSize( ps );
-		statementsToClose.add( ps );
-		lastQuery = ps;
-		return ps;
-	}
-
-	public void abortBatch(SQLException sqle) {
+	public boolean hasOpenResources() {
 		try {
-			if (batchUpdate!=null) closeStatement(batchUpdate);
-		}
-		catch (SQLException e) {
-			//noncritical, swallow and let the other propagate!
-			JDBCExceptionReporter.logExceptions(e);
+			return !isClosingBatchUpdate && batchUpdate != null && ! batchUpdate.isClosed();
 		}
-		finally {
-			batchUpdate=null;
-			batchUpdateSQL=null;
+		catch (SQLException sqle) {
+			throw exceptionHelper.convert(
+					sqle,
+					"Could check to see if batch statement was closed",
+					batchUpdateSQL
+				);
 		}
 	}
 
-	public ResultSet getResultSet(PreparedStatement ps) throws SQLException {
-		ResultSet rs = ps.executeQuery();
-		resultSetsToClose.add(rs);
-		logOpenResults();
-		return rs;
+	public PreparedStatement getStatement(String sql) {
+		return batchUpdate != null && batchUpdateSQL.equals( sql ) ? batchUpdate : null;
 	}
 
-	public ResultSet getResultSet(CallableStatement ps, Dialect dialect) throws SQLException {
-		ResultSet rs = dialect.getResultSet(ps);
-		resultSetsToClose.add(rs);
-		logOpenResults();
-		return rs;
-
+	public void setStatement(String sql, PreparedStatement ps) {
+		checkNotClosingBatchUpdate();
+		batchUpdateSQL = sql;
+		batchUpdate = ps;		
 	}
 
-	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException {
-		boolean psStillThere = statementsToClose.remove( ps );
-		try {
-			if ( rs != null ) {
-				if ( resultSetsToClose.remove( rs ) ) {
-					logCloseResults();
-					rs.close();
-				}
-			}
-		}
-		finally {
-			if ( psStillThere ) {
-				closeQueryStatement( ps );
-			}
-		}
+	protected PreparedStatement getStatement() {
+		return batchUpdate;
 	}
 
-	public PreparedStatement prepareBatchStatement(String sql)
-			throws SQLException, HibernateException {
-		sql = getSQL( sql );
-
-		if ( !sql.equals(batchUpdateSQL) ) {
-			batchUpdate=prepareStatement(sql); // calls executeBatch()
-			batchUpdateSQL=sql;
-		}
-		else {
-			log.debug("reusing prepared statement");
-			log(sql);
-		}
-		return batchUpdate;
+	public void abortBatch(SQLException sqle) {
+		closeStatements();
 	}
 
-	public CallableStatement prepareBatchCallableStatement(String sql)
-			throws SQLException, HibernateException {
-		if ( !sql.equals(batchUpdateSQL) ) { // TODO: what if batchUpdate is a callablestatement ?
-			batchUpdate=prepareCallableStatement(sql); // calls executeBatch()
-			batchUpdateSQL=sql;
+	/**
+	 * Actually releases the batcher, allowing it to cleanup internally held
+	 * resources.
+	 */
+	public void closeStatements() {
+		try {
+			closeBatchUpdate();
+		}
+		catch ( SQLException sqle ) {
+			//no big deal
+			log.warn( "Could not close a JDBC prepared statement", sqle );
 		}
-		return (CallableStatement)batchUpdate;
+		batchUpdate = null;
+		batchUpdateSQL = null;
 	}
 
-
 	public void executeBatch() throws HibernateException {
+		checkNotClosingBatchUpdate();
 		if (batchUpdate!=null) {
 			try {
 				try {
 					doExecuteBatch(batchUpdate);
 				}
 				finally {
-					closeStatement(batchUpdate);
+					closeBatchUpdate();
 				}
 			}
 			catch (SQLException sqle) {
-				throw factory.getSQLExceptionHelper().convert(
+				throw exceptionHelper.convert(
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
 
-	public void closeStatement(PreparedStatement ps) throws SQLException {
-		logClosePreparedStatement();
-		closePreparedStatement(ps);
-	}
-
-	private void closeQueryStatement(PreparedStatement ps) throws SQLException {
-
-		try {
-			//work around a bug in all known connection pools....
-			if ( ps.getMaxRows()!=0 ) ps.setMaxRows(0);
-			if ( ps.getQueryTimeout()!=0 ) ps.setQueryTimeout(0);
-		}
-		catch (Exception e) {
-			log.warn("exception clearing maxRows/queryTimeout", e);
-//			ps.close(); //just close it; do NOT try to return it to the pool!
-			return; //NOTE: early exit!
-		}
-		finally {
-			closeStatement(ps);
-		}
-
-		if ( lastQuery==ps ) lastQuery = null;
-
-	}
-
-	/**
-	 * Actually releases the batcher, allowing it to cleanup internally held
-	 * resources.
-	 */
-	public void closeStatements() {
-		try {
-			releasing = true;
-
-			try {
-				if ( batchUpdate != null ) {
-					batchUpdate.close();
-				}
-			}
-			catch ( SQLException sqle ) {
-				//no big deal
-				log.warn( "Could not close a JDBC prepared statement", sqle );
-			}
-			batchUpdate = null;
-			batchUpdateSQL = null;
-
-			Iterator iter = resultSetsToClose.iterator();
-			while ( iter.hasNext() ) {
-				try {
-					logCloseResults();
-					( ( ResultSet ) iter.next() ).close();
-				}
-				catch ( SQLException e ) {
-					// no big deal
-					log.warn( "Could not close a JDBC result set", e );
-				}
-				catch ( ConcurrentModificationException e ) {
-					// this has been shown to happen occasionally in rare cases
-					// when using a transaction manager + transaction-timeout
-					// where the timeout calls back through Hibernate's
-					// registered transaction synchronization on a separate
-					// "reaping" thread.  In cases where that reaping thread
-					// executes through this block at the same time the main
-					// application thread does we can get into situations where
-					// these CMEs occur.  And though it is not "allowed" per-se,
-					// the end result without handling it specifically is infinite
-					// looping.  So here, we simply break the loop
-					log.info( "encountered CME attempting to release batcher; assuming cause is tx-timeout scenario and ignoring" );
-					break;
-				}
-				catch ( Throwable e ) {
-					// sybase driver (jConnect) throwing NPE here in certain
-					// cases, but we'll just handle the general "unexpected" case
-					log.warn( "Could not close a JDBC result set", e );
-				}
-			}
-			resultSetsToClose.clear();
-
-			iter = statementsToClose.iterator();
-			while ( iter.hasNext() ) {
-				try {
-					closeQueryStatement( ( PreparedStatement ) iter.next() );
-				}
-				catch ( ConcurrentModificationException e ) {
-					// see explanation above...
-					log.info( "encountered CME attempting to release batcher; assuming cause is tx-timeout scenario and ignoring" );
-					break;
-				}
-				catch ( SQLException e ) {
-					// no big deal
-					log.warn( "Could not close a JDBC statement", e );
-				}
-			}
-			statementsToClose.clear();
-		}
-		finally {
-			releasing = false;
-		}
-	}
-
 	protected abstract void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException;
 
-	private String preparedStatementCountsToString() {
-		return
-				" (open PreparedStatements: " +
-				openPreparedStatementCount +
-				", globally: " +
-				globalOpenPreparedStatementCount +
-				")";
-	}
-
-	private String resultSetCountsToString() {
-		return
-				" (open ResultSets: " +
-				openResultSetCount +
-				", globally: " +
-				globalOpenResultSetCount +
-				")";
-	}
-
-	private void logOpenPreparedStatement() {
-		if ( log.isDebugEnabled() ) {
-			log.debug( "about to open PreparedStatement" + preparedStatementCountsToString() );
-			openPreparedStatementCount++;
-			globalOpenPreparedStatementCount++;
-		}
-	}
-
-	private void logClosePreparedStatement() {
-		if ( log.isDebugEnabled() ) {
-			log.debug( "about to close PreparedStatement" + preparedStatementCountsToString() );
-			openPreparedStatementCount--;
-			globalOpenPreparedStatementCount--;
-		}
-	}
-
-	private void logOpenResults() {
-		if ( log.isDebugEnabled() ) {
-			log.debug( "about to open ResultSet" + resultSetCountsToString() );
-			openResultSetCount++;
-			globalOpenResultSetCount++;
-		}
-	}
-	private void logCloseResults() {
-		if ( log.isDebugEnabled() ) {
-			log.debug( "about to close ResultSet" + resultSetCountsToString() );
-			openResultSetCount--;
-			globalOpenResultSetCount--;
-		}
-	}
-
-	protected SessionFactoryImplementor getFactory() {
-		return factory;
-	}
-
-	private void log(String sql) {
-		factory.getSettings().getSqlStatementLogger().logStatement( sql, FormatStyle.BASIC );
-	}
-
-	private PreparedStatement getPreparedStatement(
-			final Connection conn,
-	        final String sql,
-	        final boolean scrollable,
-	        final ScrollMode scrollMode) throws SQLException {
-		return getPreparedStatement(
-				conn,
-		        sql,
-		        scrollable,
-		        false,
-		        null,
-		        scrollMode,
-		        false
-		);
-	}
-
-	private CallableStatement getCallableStatement(
-			final Connection conn,
-	        String sql,
-	        boolean scrollable) throws SQLException {
-		if ( scrollable && !factory.getSettings().isScrollableResultSetsEnabled() ) {
-			throw new AssertionFailure("scrollable result sets are not enabled");
-		}
-
-		sql = getSQL( sql );
-		log( sql );
-
-		log.trace("preparing callable statement");
-		if ( scrollable ) {
-			return conn.prepareCall(
-					sql,
-			        ResultSet.TYPE_SCROLL_INSENSITIVE,
-			        ResultSet.CONCUR_READ_ONLY
-			);
-		}
-		else {
-			return conn.prepareCall( sql );
-		}
-	}
 
-	private String getSQL(String sql) {
-		sql = interceptor.onPrepareStatement( sql );
-		if ( sql==null || sql.length() == 0 ) {
-			throw new AssertionFailure( "Interceptor.onPrepareStatement() returned null or empty string." );
-		}
-		return sql;
-	}
-
-	private PreparedStatement getPreparedStatement(
-			final Connection conn,
-	        String sql,
-	        boolean scrollable,
-	        final boolean useGetGeneratedKeys,
-	        final String[] namedGeneratedKeys,
-	        final ScrollMode scrollMode,
-	        final boolean callable) throws SQLException {
-		if ( scrollable && !factory.getSettings().isScrollableResultSetsEnabled() ) {
-			throw new AssertionFailure("scrollable result sets are not enabled");
-		}
-		if ( useGetGeneratedKeys && !factory.getSettings().isGetGeneratedKeysEnabled() ) {
-			throw new AssertionFailure("getGeneratedKeys() support is not enabled");
-		}
-
-		sql = getSQL( sql );
-		log( sql );
-
-		log.trace( "preparing statement" );
-		PreparedStatement result;
-		if ( scrollable ) {
-			if ( callable ) {
-				result = conn.prepareCall( sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY );
-			}
-			else {
-				result = conn.prepareStatement( sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY );
-			}
-		}
-		else if ( useGetGeneratedKeys ) {
-			result = conn.prepareStatement( sql, PreparedStatement.RETURN_GENERATED_KEYS );
-		}
-		else if ( namedGeneratedKeys != null ) {
-			result = conn.prepareStatement( sql, namedGeneratedKeys );
-		}
-		else {
-			if ( callable ) {
-				result = conn.prepareCall( sql );
-			}
-			else {
-				result = conn.prepareStatement( sql );
-			}
-		}
-
-		setTimeout( result );
-
-		if ( factory.getStatistics().isStatisticsEnabled() ) {
-			factory.getStatisticsImplementor().prepareStatement();
-		}
-
-		return result;
-
-	}
-
-	private void setTimeout(PreparedStatement result) throws SQLException {
-		if ( isTransactionTimeoutSet ) {
-			int timeout = (int) ( transactionTimeout - ( System.currentTimeMillis() / 1000 ) );
-			if (timeout<=0) {
-				throw new TransactionException("transaction timeout expired");
-			}
-			else {
-				result.setQueryTimeout(timeout);
-			}
-		}
-	}
-
-	private void closePreparedStatement(PreparedStatement ps) throws SQLException {
+	private void closeBatchUpdate() throws SQLException{
+		checkNotClosingBatchUpdate();
 		try {
-			log.trace("closing statement");
-			ps.close();
-			if ( factory.getStatistics().isStatisticsEnabled() ) {
-				factory.getStatisticsImplementor().closeStatement();
+			if ( batchUpdate != null ) {
+				isClosingBatchUpdate = true;
+				batchUpdate.close();
 			}
 		}
 		finally {
-			if ( !releasing ) {
-				// If we are in the process of releasing, no sense
-				// checking for aggressive-release possibility.
-				connectionManager.afterStatement();
-			}
+			isClosingBatchUpdate = false;
 		}
-	}
 
-	private void setStatementFetchSize(PreparedStatement statement) throws SQLException {
-		Integer statementFetchSize = factory.getSettings().getJdbcFetchSize();
-		if ( statementFetchSize!=null ) {
-			statement.setFetchSize( statementFetchSize.intValue() );
-		}
 	}
 
-	public void cancelLastQuery() throws HibernateException {
-		try {
-			if (lastQuery!=null) lastQuery.cancel();
+	private void checkNotClosingBatchUpdate() {
+		if ( isClosingBatchUpdate ) {
+			throw new IllegalStateException( "Cannot perform operation while closing batch update." );
 		}
-		catch (SQLException sqle) {
-			throw factory.getSQLExceptionHelper().convert(
-			        sqle,
-			        "Cannot cancel query"
-				);
-		}
-	}
-
-	public boolean hasOpenResources() {
-		return resultSetsToClose.size() > 0 || statementsToClose.size() > 0;
 	}
-
-	public String openResourceStatsAsString() {
-		return preparedStatementCountsToString() + resultSetCountsToString();
-	}
-
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
index 66eaf9f7b6..df96c984e7 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
@@ -1,175 +1,76 @@
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
-	/**
-	 * Get a prepared statement for use in loading / querying. If not explicitly
-	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
-	 * session is closed or disconnected.
-	 */
-	public PreparedStatement prepareQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException;
-	/**
-	 * Close a prepared statement opened with <tt>prepareQueryStatement()</tt>
-	 */
-	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException;
-	/**
-	 * Get a prepared statement for use in loading / querying. If not explicitly
-	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
-	 * session is closed or disconnected.
-	 */
-	public CallableStatement prepareCallableQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException;
-	
-	
-	/**
-	 * Get a non-batchable prepared statement to use for selecting. Does not
-	 * result in execution of the current batch.
-	 */
-	public PreparedStatement prepareSelectStatement(String sql) throws SQLException, HibernateException;
-
-	/**
-	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
-	 * using JDBC3 getGeneratedKeys ({@link Connection#prepareStatement(String, int)}).
-	 * <p/>
-	 * Must be explicitly released by {@link #closeStatement} after use.
-	 */
-	public PreparedStatement prepareStatement(String sql, boolean useGetGeneratedKeys) throws SQLException, HibernateException;
-
-	/**
-	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
-	 * using JDBC3 getGeneratedKeys ({@link Connection#prepareStatement(String, String[])}).
-	 * <p/>
-	 * Must be explicitly released by {@link #closeStatement} after use.
-	 */
-	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException, HibernateException;
-
-	/**
-	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
-	 * <p/>
-	 * Must be explicitly released by {@link #closeStatement} after use.
-	 */
-	public PreparedStatement prepareStatement(String sql) throws SQLException, HibernateException;
-
-	/**
-	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
-	 * <p/>
-	 * Must be explicitly released by {@link #closeStatement} after use.
-	 */
-	public CallableStatement prepareCallableStatement(String sql) throws SQLException, HibernateException;
-
-	/**
-	 * Close a prepared or callable statement opened using <tt>prepareStatement()</tt> or <tt>prepareCallableStatement()</tt>
-	 */
-	public void closeStatement(PreparedStatement ps) throws SQLException;
-
-	/**
-	 * Get a batchable prepared statement to use for inserting / deleting / updating
-	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
-	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
-	 * statement explicitly.
-	 * @see Batcher#addToBatch
-	 */
-	public PreparedStatement prepareBatchStatement(String sql) throws SQLException, HibernateException;
 
-	/**
-	 * Get a batchable callable statement to use for inserting / deleting / updating
-	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
-	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
-	 * statement explicitly.
-	 * @see Batcher#addToBatch
-	 */
-	public CallableStatement prepareBatchCallableStatement(String sql) throws SQLException, HibernateException;
+	public PreparedStatement getStatement(String sql);
+	public void setStatement(String sql, PreparedStatement ps);
+	public boolean hasOpenResources();
 
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
-	 * Close any query statements that were left lying around
-	 */
-	public void closeStatements();
-	/**
-	 * Execute the statement and return the result set
-	 */
-	public ResultSet getResultSet(PreparedStatement ps) throws SQLException;
-	/**
-	 * Execute the statement and return the result set from a callable statement
-	 */
-	public ResultSet getResultSet(CallableStatement ps, Dialect dialect) throws SQLException;
-
-	/**
 	 * Must be called when an exception occurs
 	 * @param sqle the (not null) exception that is the reason for aborting
 	 */
 	public void abortBatch(SQLException sqle);
 
 	/**
-	 * Cancel the current query statement
-	 */
-	public void cancelLastQuery() throws HibernateException;
-
-	public boolean hasOpenResources();
-
-	public String openResourceStatsAsString();
-
-	/**
-	 * Set the transaction timeout to <tt>seconds</tt> later
-	 * than the current system time.
-	 */
-	public void setTransactionTimeout(int seconds);
-	/**
-	 * Unset the transaction timeout, called after the end of a 
-	 * transaction.
+	 * Actually releases the batcher, allowing it to cleanup internally held
+	 * resources.
 	 */
-	public void unsetTransactionTimeout();
+	public void closeStatements();	
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
index b31cad2e4c..bf03718847 100755
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/BatcherFactory.java
@@ -1,36 +1,38 @@
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
 
 import org.hibernate.Interceptor;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 
 
 /**
  * Factory for <tt>Batcher</tt> instances.
  * @author Gavin King
  */
 public interface BatcherFactory {
-	public Batcher createBatcher(ConnectionManager connectionManager, Interceptor interceptor);
+	public void setJdbcBatchSize(int jdbcBatchSize);
+	public Batcher createBatcher(SQLExceptionHelper exceptionHelper);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
index ce85763cac..aae2972d32 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcher.java
@@ -1,102 +1,100 @@
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
-import org.hibernate.Interceptor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 
 /**
  * An implementation of the <tt>Batcher</tt> interface that
  * actually uses batching
  * @author Gavin King
  */
 public class BatchingBatcher extends AbstractBatcher {
 
-	private int batchSize;
 	private Expectation[] expectations;
 	
-	public BatchingBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-		super( connectionManager, interceptor );
-		expectations = new Expectation[ getFactory().getSettings().getJdbcBatchSize() ];
+	private int currentSize;
+	public BatchingBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
+		super( exceptionHelper, jdbcBatchSize );
+		expectations = new Expectation[ jdbcBatchSize ];
+		currentSize = 0;
 	}
 
 	public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
 		if ( !expectation.canBeBatched() ) {
 			throw new HibernateException( "attempting to batch an operation which cannot be batched" );
 		}
 		PreparedStatement batchUpdate = getStatement();
 		batchUpdate.addBatch();
-		expectations[ batchSize++ ] = expectation;
-		if ( batchSize == getFactory().getSettings().getJdbcBatchSize() ) {
+		expectations[ currentSize++ ] = expectation;
+		if ( currentSize == getJdbcBatchSize() ) {
 			doExecuteBatch( batchUpdate );
 		}
 	}
 
 	protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
-		if ( batchSize == 0 ) {
+		if ( currentSize == 0 ) {
 			log.debug( "no batched statements to execute" );
 		}
 		else {
 			if ( log.isDebugEnabled() ) {
-				log.debug( "Executing batch size: " + batchSize );
+				log.debug( "Executing batch size: " + currentSize );
 			}
 
 			try {
 				checkRowCounts( ps.executeBatch(), ps );
 			}
 			catch (RuntimeException re) {
 				log.error( "Exception executing batch: ", re );
 				throw re;
 			}
 			finally {
-				batchSize = 0;
+				currentSize = 0;
 			}
 
 		}
-
 	}
 
 	private void checkRowCounts(int[] rowCounts, PreparedStatement ps) throws SQLException, HibernateException {
 		int numberOfRowCounts = rowCounts.length;
-		if ( numberOfRowCounts != batchSize ) {
+		if ( numberOfRowCounts != currentSize ) {
 			log.warn( "JDBC driver did not return the expected number of row counts" );
 		}
 		for ( int i = 0; i < numberOfRowCounts; i++ ) {
 			expectations[i].verifyOutcome( rowCounts[i], ps, i );
 		}
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
index 504bcd1044..a1e14dbc0b 100755
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/BatchingBatcherFactory.java
@@ -1,42 +1,48 @@
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
 
-import org.hibernate.Interceptor;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 
 
 /**
  * A BatcherFactory implementation which constructs Batcher instances
  * capable of actually performing batch operations.
  * 
  * @author Gavin King
  */
 public class BatchingBatcherFactory implements BatcherFactory {
 
-	public Batcher createBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-		return new BatchingBatcher( connectionManager, interceptor );
+	private int jdbcBatchSize;
+
+	public void setJdbcBatchSize(int jdbcBatchSize) {
+		this.jdbcBatchSize = jdbcBatchSize;
+	}
+
+	public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
+		return new BatchingBatcher( exceptionHelper, jdbcBatchSize );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
index e722ecb165..6dfea07339 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcher.java
@@ -1,55 +1,53 @@
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
-import org.hibernate.Interceptor;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 
 /**
  * An implementation of the <tt>Batcher</tt> interface that does no batching
  *
  * @author Gavin King
  */
 public class NonBatchingBatcher extends AbstractBatcher {
 
-	public NonBatchingBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-		super( connectionManager, interceptor );
+	public NonBatchingBatcher(SQLExceptionHelper exceptionHelper) {
+		super( exceptionHelper, 1 );
 	}
 
 	public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
 		PreparedStatement statement = getStatement();
 		final int rowCount = statement.executeUpdate();
 		expectation.verifyOutcome( rowCount, statement, 0 );
 	}
 
 	protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
index 58f4c0d978..ee55421e0d 100755
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/NonBatchingBatcherFactory.java
@@ -1,42 +1,49 @@
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
 
-import org.hibernate.Interceptor;
+import org.hibernate.AssertionFailure;
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 
 
 /**
  * A BatcherFactory implementation which constructs Batcher instances
  * that do not perform batch operations.
  *
  * @author Gavin King
  */
 public class NonBatchingBatcherFactory implements BatcherFactory {
 
-	public Batcher createBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-		return new NonBatchingBatcher( connectionManager, interceptor );
+	public void setJdbcBatchSize(int jdbcBatchSize) {
+		if ( jdbcBatchSize > 1 ) {
+			throw new AssertionFailure( "jdbcBatchSize must be 1 for " + getClass().getName() );
+		}
+	}
+
+	public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
+		return new NonBatchingBatcher( exceptionHelper );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index a32a1725ec..02c9e6d8cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2652 +1,2642 @@
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
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.LockOptions;
 import org.hibernate.cache.FilterKey;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.QueryKey;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.EntityUniqueKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.TwoPhaseLoad;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PreLoadEvent;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.impl.FetchingScrollableResultsImpl;
 import org.hibernate.impl.ScrollableResultsImpl;
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
 import org.hibernate.util.StringHelper;
 
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
 
 	private static final Logger log = LoggerFactory.getLogger( Loader.class );
 
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
 	protected abstract String getSQLString();
 
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
 	protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws HibernateException {
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
 	protected String preprocessSQL(String sql, QueryParameters parameters, Dialect dialect)
 			throws HibernateException {
 
 		sql = applyLocks( sql, parameters.getLockOptions(), dialect );
 		
 		return getFactory().getSettings().isCommentsEnabled() ?
 				prependComment( sql, parameters ) : sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuffer( comment.length() + sql.length() + 5 )
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
 	private List doQueryAndInitializeNonLazyCollections(
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
 
 	private List doQueryAndInitializeNonLazyCollections(
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
 				if ( result == null ) {
 					result = loaded;
 				}
 			} 
 			while ( keyToRead.equals( loadedKeys[0] ) && resultSet.next() );
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
 			return new EntityKey( 
 					optionalId,
 					session.getEntityPersister( optionalEntityName, optionalObject ), 
 					session.getEntityMode()
 				);
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
 
 		return forcedResultTransformer == null ?
 				getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session ) :
 				forcedResultTransformer.transformTuple(
 						getResultRow( row, resultSet, session ),
 						getResultRowAliases()
 				)
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
 			keys[ entitySpan - 1 ] = new EntityKey( optionalId, persisters[ entitySpan - 1 ], session.getEntityMode() );
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
 							keys[targetIndex] = new EntityKey( targetId, persisters[targetIndex], session.getEntityMode() );
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
 							object = instanceNotYetLoaded(
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
-			session.getBatcher().closeQueryStatement( st, rs );
+			st.close();
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
-		
-		if (callable) {
-			st = session.getBatcher()
-				.prepareCallableQueryStatement( sql, scroll || useScrollableResultSetToSkip, scrollMode );
-		} 
-		else {
-			st = session.getBatcher()
-				.prepareQueryStatement( sql, scroll || useScrollableResultSetToSkip, scrollMode );
-		}
-				
+
+		st = (
+				scroll || useScrollableResultSetToSkip ?
+						session.getJDBCContext().getConnectionManager().prepareScrollableQueryStatement( sql, scrollMode, callable ) :
+						session.getJDBCContext().getConnectionManager().prepareQueryStatement( sql, callable )
+		);
 
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
-			session.getBatcher().closeQueryStatement( st, null );
+			st.close();
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
-			session.getBatcher().closeQueryStatement( st, null );
+			st.close();
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
-			if (callable) {
-				rs = session.getBatcher().getResultSet( (CallableStatement) st, dialect );
-			} 
-			else {
-				rs = session.getBatcher().getResultSet( st );
-			}
+			rs = st.executeQuery();
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
-			session.getBatcher().closeQueryStatement( st, rs );
+			st.close();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 5285f91572..d297f6e1a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1861 +1,1820 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.util.ArrayHelper;
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
-					if ( callable ) {
-						st = session.getBatcher().prepareBatchCallableStatement( sql );
-					}
-					else {
-						st = session.getBatcher().prepareBatchStatement( sql );
-					}
+					st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 				}
 				else {
-					if ( callable ) {
-						st = session.getBatcher().prepareCallableStatement( sql );
-					}
-					else {
-						st = session.getBatcher().prepareStatement( sql );
-					}
+					st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 				}
 
 
 				try {
 					offset+= expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
-						session.getBatcher().addToBatch( expectation );
+						session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 					}
 					else {
 						expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
-						session.getBatcher().abortBatch( sqle );
+						session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
-						session.getBatcher().closeStatement( st );
+						st.close();
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
-								if ( callable ) {
-									st = session.getBatcher().prepareBatchCallableStatement( sql );
-								}
-								else {
-									st = session.getBatcher().prepareBatchStatement( sql );
-								}
+								st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 							}
 							else {
-								if ( callable ) {
-									st = session.getBatcher().prepareCallableStatement( sql );
-								}
-								else {
-									st = session.getBatcher().prepareStatement( sql );
-								}
+								st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
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
-									session.getBatcher().addToBatch( expectation );
+									session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 								}
 								else {
 									expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 								}
 
 								collection.afterRowInsert( this, entry, i );
 								count++;
 							}
 							catch ( SQLException sqle ) {
 								if ( useBatch ) {
-									session.getBatcher().abortBatch( sqle );
+									session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 								}
 								throw sqle;
 							}
 							finally {
 								if ( !useBatch ) {
-									session.getBatcher().closeStatement( st );
+									st.close();
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
-							if ( callable ) {
-								st = session.getBatcher().prepareBatchCallableStatement( sql );
-							}
-							else {
-								st = session.getBatcher().prepareBatchStatement( sql );
-							}
+							st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 						}
 						else {
-							if ( callable ) {
-								st = session.getBatcher().prepareCallableStatement( sql );
-							}
-							else {
-								st = session.getBatcher().prepareStatement( sql );
-							}
+							st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
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
-								session.getBatcher().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
-								session.getBatcher().abortBatch( sqle );
+								session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
-								session.getBatcher().closeStatement( st );
+								st.close();
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
-								if ( callable ) {
-									st = session.getBatcher().prepareBatchCallableStatement( sql );
-								}
-								else {
-									st = session.getBatcher().prepareBatchStatement( sql );
-								}
+								st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 							}
 						}
 						else {
-							if ( callable ) {
-								st = session.getBatcher().prepareCallableStatement( sql );
-							}
-							else {
-								st = session.getBatcher().prepareStatement( sql );
-							}
+							st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
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
-								session.getBatcher().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
-								session.getBatcher().abortBatch( sqle );
+								session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
-								session.getBatcher().closeStatement( st );
+								st.close();
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
-			PreparedStatement st = session.getBatcher().prepareSelectStatement(sqlSelectSizeString);
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement(sqlSelectSizeString);
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
-				session.getBatcher().closeStatement( st );
+				st.close();
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
-			PreparedStatement st = session.getBatcher().prepareSelectStatement(sql);
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement(sql);
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
-				session.getBatcher().closeStatement( st );
+				st.close();
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
-			PreparedStatement st = session.getBatcher().prepareSelectStatement(sqlSelectRowByIndexString);
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement(sqlSelectRowByIndexString);
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
-				session.getBatcher().closeStatement( st );
+				st.close();
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
index c6c197f67e..1a2184cac9 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
@@ -1,345 +1,334 @@
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
-import org.hibernate.exception.JDBCExceptionHelper;
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
-							if ( callable ) {
-								st = session.getBatcher().prepareBatchCallableStatement( sql );
-							}
-							else {
-								st = session.getBatcher().prepareBatchStatement( sql );
-							}
+							st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 						}
 					}
 					else {
-						if ( callable ) {
-							st = session.getBatcher().prepareCallableStatement( sql );
-						}
-						else {
-							st = session.getBatcher().prepareStatement( sql );
-						}
+						st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
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
-							session.getBatcher().addToBatch( expectation );
+							session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 						}
 						else {
 							expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 						}
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
-							session.getBatcher().abortBatch( sqle );
+							session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
-							session.getBatcher().closeStatement( st );
+							st.close();
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
index 977040a011..5045323364 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
@@ -1,388 +1,380 @@
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
-import org.hibernate.exception.JDBCExceptionHelper;
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
 							if ( st == null ) {
 								String sql = getSQLDeleteRowString();
 								if ( isDeleteCallable() ) {
 									expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 									useBatch = expectation.canBeBatched();
 									st = useBatch
-											? session.getBatcher().prepareBatchCallableStatement( sql )
-								            : session.getBatcher().prepareCallableStatement( sql );
+											? session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, true )
+								            : session.getJDBCContext().getConnectionManager().prepareStatement( sql, true );
 									offset += expectation.prepare( st );
 								}
 								else {
-									st = session.getBatcher().prepareBatchStatement( getSQLDeleteRowString() );
+									st = session.getJDBCContext().getConnectionManager().prepareBatchStatement(
+											getSQLDeleteRowString(),
+											false
+									);
 								}
 							}
 							int loc = writeKey( st, id, offset, session );
 							writeElementToWhere( st, collection.getSnapshotElement(entry, i), loc, session );
 							if ( useBatch ) {
-								session.getBatcher().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
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
-						session.getBatcher().abortBatch( sqle );
+						session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
-						session.getBatcher().closeStatement( st );
+						st.close();
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
-									if ( callable ) {
-										st = session.getBatcher().prepareBatchCallableStatement( sql );
-									}
-									else {
-										st = session.getBatcher().prepareBatchStatement( sql );
-									}
+									st = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 								}
 							}
 							else {
-								if ( callable ) {
-									st = session.getBatcher().prepareCallableStatement( sql );
-								}
-								else {
-									st = session.getBatcher().prepareStatement( sql );
-								}
+								st = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 							}
 
 							offset += expectation.prepare( st );
 
 							int loc = writeKey( st, id, offset, session );
 							if ( hasIndex && !indexContainsFormula ) {
 								loc = writeIndexToWhere( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 
 							writeElementToWhere( st, collection.getElement( entry ), loc, session );
 
 							if ( useBatch ) {
-								session.getBatcher().addToBatch( expectation );
+								session.getJDBCContext().getConnectionManager().addToBatch( expectation );
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
-						session.getBatcher().abortBatch( sqle );
+						session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
-						session.getBatcher().closeStatement( st );
+						st.close();
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
index 92845315fe..528f412472 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,4123 +1,4092 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.StructuredCacheEntry;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.engine.Versioning;
-import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.intercept.FieldInterceptionHelper;
 import org.hibernate.intercept.FieldInterceptor;
 import org.hibernate.intercept.LazyPropertyInitializer;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoader;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.Tuplizer;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.FilterHelper;
 import org.hibernate.util.StringHelper;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 		SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
 	private static final Logger log = LoggerFactory.getLogger( AbstractEntityPersister.class );
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryStructure cacheEntryStructure;
 	private final EntityMetamodel entityMetamodel;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	private final int batchSize;
 	private final boolean hasSubselectLoadableCollections;
 	protected final String rowIdName;
 
 	private final Set lazyProperties;
 
 	// The optional SQL string defined in the where attribute
 	private final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	//information about properties of this class,
 	//including inherited properties
 	//(only really needed for updatable/insertable properties)
 	private final int[] propertyColumnSpans;
 	private final String[] propertySubclassNames;
 	private final String[][] propertyColumnAliases;
 	private final String[][] propertyColumnNames;
 	private final String[][] propertyColumnFormulaTemplates;
 	private final String[][] propertyColumnReaderTemplates;
 	private final String[][] propertyColumnWriters;
 	private final boolean[][] propertyColumnUpdateable;
 	private final boolean[][] propertyColumnInsertable;
 	private final boolean[] propertyUniqueness;
 	private final boolean[] propertySelectable;
 
 	//information about lazy properties of this class
 	private final String[] lazyPropertyNames;
 	private final int[] lazyPropertyNumbers;
 	private final Type[] lazyPropertyTypes;
 	private final String[][] lazyPropertyColumnAliases;
 
 	//information about all properties in class hierarchy
 	private final String[] subclassPropertyNameClosure;
 	private final String[] subclassPropertySubclassNameClosure;
 	private final Type[] subclassPropertyTypeClosure;
 	private final String[][] subclassPropertyFormulaTemplateClosure;
 	private final String[][] subclassPropertyColumnNameClosure;
 	private final String[][] subclassPropertyColumnReaderClosure;
 	private final String[][] subclassPropertyColumnReaderTemplateClosure;
 	private final FetchMode[] subclassPropertyFetchModeClosure;
 	private final boolean[] subclassPropertyNullabilityClosure;
 	private final boolean[] propertyDefinedOnSubclass;
 	private final int[][] subclassPropertyColumnNumberClosure;
 	private final int[][] subclassPropertyFormulaNumberClosure;
 	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
 
 	//information about all columns/formulas in class hierarchy
 	private final String[] subclassColumnClosure;
 	private final boolean[] subclassColumnLazyClosure;
 	private final String[] subclassColumnAliasClosure;
 	private final boolean[] subclassColumnSelectableClosure;
 	private final String[] subclassColumnReaderTemplateClosure;
 	private final String[] subclassFormulaClosure;
 	private final String[] subclassFormulaTemplateClosure;
 	private final String[] subclassFormulaAliasClosure;
 	private final boolean[] subclassFormulaLazyClosure;
 
 	// dynamic filters attached to the class-level
 	private final FilterHelper filterHelper;
 
 	private final Set affectingFetchProfileNames = new HashSet();
 
 	private final Map uniqueKeyLoaders = new HashMap();
 	private final Map lockers = new HashMap();
 	private final Map loaders = new HashMap();
 
 	// SQL strings
 	private String sqlVersionSelectString;
 	private String sqlSnapshotSelectString;
 	private String sqlLazySelectString;
 
 	private String sqlIdentityInsertString;
 	private String sqlUpdateByRowIdString;
 	private String sqlLazyUpdateByRowIdString;
 
 	private String[] sqlDeleteStrings;
 	private String[] sqlInsertStrings;
 	private String[] sqlUpdateStrings;
 	private String[] sqlLazyUpdateStrings;
 
 	private String sqlInsertGeneratedValuesSelectString;
 	private String sqlUpdateGeneratedValuesSelectString;
 
 	//Custom SQL (would be better if these were private)
 	protected boolean[] insertCallable;
 	protected boolean[] updateCallable;
 	protected boolean[] deleteCallable;
 	protected String[] customSQLInsert;
 	protected String[] customSQLUpdate;
 	protected String[] customSQLDelete;
 	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
 
 	private InsertGeneratedIdentifierDelegate identityDelegate;
 
 	private boolean[] tableHasColumns;
 
 	private final String loaderName;
 
 	private UniqueEntityLoader queryLoader;
 
 	private final String temporaryIdTableName;
 	private final String temporaryIdTableDDL;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	protected void addDiscriminatorToInsert(Insert insert) {}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}
 
 	protected abstract int[] getSubclassColumnTableNumberClosure();
 
 	protected abstract int[] getSubclassFormulaTableNumberClosure();
 
 	public abstract String getSubclassTableName(int j);
 
 	protected abstract String[] getSubclassTableKeyColumns(int j);
 
 	protected abstract boolean isClassOrSuperclassTable(int j);
 
 	protected abstract int getSubclassTableSpan();
 
 	protected abstract int getTableSpan();
 
 	protected abstract boolean isTableCascadeDeleteEnabled(int j);
 
 	protected abstract String getTableName(int j);
 
 	protected abstract String[] getKeyColumns(int j);
 
 	protected abstract boolean isPropertyOfTable(int property, int j);
 
 	protected abstract int[] getPropertyTableNumbersInSelect();
 
 	protected abstract int[] getPropertyTableNumbers();
 
 	protected abstract int getSubclassPropertyTableNumber(int i);
 
 	protected abstract String filterFragment(String alias) throws MappingException;
 
 	private static final String DISCRIMINATOR_ALIAS = "clazz_";
 
 	public String getDiscriminatorColumnName() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return DISCRIMINATOR_ALIAS;
 	}	
 	
 	public String getDiscriminatorColumnReaderTemplate() {
 		return DISCRIMINATOR_ALIAS;
 	}	
 	
 	protected String getDiscriminatorAlias() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return null;
 	}
 
 	protected boolean isInverseTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return false;
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return false;
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return entityMetamodel.getSubclassEntityNames().contains(entityName);
 	}
 
 	private boolean[] getTableHasColumns() {
 		return tableHasColumns;
 	}
 
 	public String[] getRootTableKeyColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	protected String[] getSQLUpdateByRowIdStrings() {
 		if ( sqlUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan() + 1];
 		result[0] = sqlUpdateByRowIdString;
 		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
 		return result;
 	}
 
 	protected String[] getSQLLazyUpdateByRowIdStrings() {
 		if ( sqlLazyUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan()];
 		result[0] = sqlLazyUpdateByRowIdString;
 		for ( int i = 1; i < getTableSpan(); i++ ) {
 			result[i] = sqlLazyUpdateStrings[i];
 		}
 		return result;
 	}
 
 	protected String getSQLSnapshotSelectString() {
 		return sqlSnapshotSelectString;
 	}
 
 	protected String getSQLLazySelectString() {
 		return sqlLazySelectString;
 	}
 
 	protected String[] getSQLDeleteStrings() {
 		return sqlDeleteStrings;
 	}
 
 	protected String[] getSQLInsertStrings() {
 		return sqlInsertStrings;
 	}
 
 	protected String[] getSQLUpdateStrings() {
 		return sqlUpdateStrings;
 	}
 
 	protected String[] getSQLLazyUpdateStrings() {
 		return sqlLazyUpdateStrings;
 	}
 
 	/**
 	 * The query that inserts a row, letting the database generate an id
 	 * 
 	 * @return The IDENTITY-based insertion query.
 	 */
 	protected String getSQLIdentityInsertString() {
 		return sqlIdentityInsertString;
 	}
 
 	protected String getVersionSelectString() {
 		return sqlVersionSelectString;
 	}
 
 	protected boolean isInsertCallable(int j) {
 		return insertCallable[j];
 	}
 
 	protected boolean isUpdateCallable(int j) {
 		return updateCallable[j];
 	}
 
 	protected boolean isDeleteCallable(int j) {
 		return deleteCallable[j];
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return false;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return false;
 	}
 
 	public boolean hasSequentialSelect() {
 		return false;
 	}
 
 	/**
 	 * Decide which tables need to be updated.
 	 * <p/>
 	 * The return here is an array of boolean values with each index corresponding
 	 * to a given table in the scope of this persister.
 	 *
 	 * @param dirtyProperties The indices of all the entity properties considered dirty.
 	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
 	 *
 	 * @return Array of booleans indicating which table require updating.
 	 */
 	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {
 
 		if ( dirtyProperties == null ) {
 			return getTableHasColumns(); // for objects that came in via update()
 		}
 		else {
 			boolean[] updateability = getPropertyUpdateability();
 			int[] propertyTableNumbers = getPropertyTableNumbers();
 			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				int property = dirtyProperties[i];
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan(property) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 			}
 			return tableUpdateNeeded;
 		}
 	}
 
 	public boolean hasRowId() {
 		return rowIdName != null;
 	}
 
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 		this.cacheEntryStructure = factory.getSettings().isStructuredCacheEntriesEnabled() ?
 				(CacheEntryStructure) new StructuredCacheEntry(this) :
 				(CacheEntryStructure) new UnstructuredCacheEntry();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = persistentClass.getRootTable().getRowId();
 
 		loaderName = persistentClass.getLoaderName();
 
 		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Column col = ( Column ) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		iter = persistentClass.getPropertyClosureIterator();
 		i = 0;
 		boolean foundFormula = false;
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			thisClassProperties.add( prop );
 
 			int span = prop.getColumnSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			Iterator colIter = prop.getColumnIterator();
 			int k = 0;
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column)thing;
 					colNames[k] = col.getQuotedName( factory.getDialect() );
 					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					colWriters[k] = col.getWriteExpr();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getName() );
 				lazyNames.add( prop.getName() );
 				lazyNumbers.add( new Integer( i ) );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList joinedFetchesList = new ArrayList();
 		ArrayList cascades = new ArrayList();
 		ArrayList definedBySubclass = new ArrayList();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			names.add( prop.getName() );
 			classes.add( prop.getPersistentClass().getEntityName() );
 			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
 			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
 			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
 			types.add( prop.getType() );
 
 			Iterator colIter = prop.getColumnIterator();
 			String[] cols = new String[prop.getColumnSpan()];
 			String[] readers = new String[prop.getColumnSpan()];
 			String[] readerTemplates = new String[prop.getColumnSpan()];
 			String[] forms = new String[prop.getColumnSpan()];
 			int[] colnos = new int[prop.getColumnSpan()];
 			int[] formnos = new int[prop.getColumnSpan()];
 			int l = 0;
 			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				if ( thing.isFormula() ) {
 					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( thing.getText( factory.getDialect() ) );
 					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					Column col = (Column)thing;
 					String colName = col.getQuotedName( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
 					columnsLazy.add( lazy );
 					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
 					
 					readers[l] = col.getReadExpr( factory.getDialect() );
 					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					readerTemplates[l] = readerTemplate;
 					columnReaderTemplates.add( readerTemplate );
 				}
 				l++;
 			}
 			propColumns.add( cols );
 			propColumnReaders.add( readers );
 			propColumnReaderTemplates.add( readerTemplates );
 			templates.add( forms );
 			propColumnNumbers.add( colnos );
 			propFormulaNumbers.add( formnos );
 
 			joinedFetchesList.add( prop.getValue().getFetchMode() );
 			cascades.add( prop.getCascadeStyle() );
 		}
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
 		iter = cascades.iterator();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilterMap(), factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add( new Integer( tableNumber ) );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( new Integer( colNumbers[j] ) );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( new Integer( formNumbers[j] ) );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
 		if ( log.isTraceEnabled() ) {
 			log.trace(
 					"initializing lazy properties of: " +
 					MessageHelper.infoString( this, id, getFactory() ) +
 					", field access: " + fieldName
 				);
 		}
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = new CacheKey(id, getIdentifierType(), getEntityName(), session.getEntityMode(), getFactory() );
 			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
 			if (ce!=null) {
 				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
 		if ( !hasLazyProperties() ) {
 			throw new AssertionFailure("no lazy properties");
 		}
 
 		log.trace("initializing lazy properties from datastore");
 
 		try {
 
 			Object result = null;
 			PreparedStatement ps = null;
 			try {
 				final String lazySelect = getSQLLazySelectString();
 				ResultSet rs = null;
 				try {
 					if ( lazySelect != null ) {
 						// null sql means that the only lazy properties
 						// are shared PK one-to-one associations which are
 						// handled differently in the Type#nullSafeGet code...
-						ps = session.getBatcher().prepareSelectStatement(lazySelect);
+						ps = session.getJDBCContext().getConnectionManager().prepareSelectStatement(lazySelect);
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = ps.executeQuery();
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
-					session.getBatcher().closeStatement( ps );
+					ps.close();
 				}
 			}
 
 			log.trace( "done initializing lazy properties" );
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize lazy properties: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getSQLLazySelectString()
 				);
 		}
 	}
 
 	private Object initializeLazyPropertiesFromCache(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final EntityEntry entry,
 			final CacheEntry cacheEntry
 	) {
 
 		log.trace("initializing lazy properties from second-level cache");
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[ lazyPropertyNumbers[j] ],
 					session,
 					entity
 				);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
 		log.trace( "done initializing lazy properties" );
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue, session.getEntityMode() );
 		if (snapshot != null) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, session.getEntityMode(), factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_NONE ||
 			( !isVersioned() && optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_VERSION ) ||
 			getFactory().getSettings().isJdbcBatchVersionedData();
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return getPropertySpaces();
 	}
 
 	protected Set getLazyProperties() {
 		return lazyProperties;
 	}
 
 	public boolean isBatchLoadable() {
 		return batchSize > 1;
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return rootTableKeyColumnReaders;
 	}	
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return rootTableKeyColumnReaderTemplates;
 	}	
 	
 	protected int getIdentifierColumnSpan() {
 		return identifierColumnSpan;
 	}
 
 	protected String[] getIdentifierAliases() {
 		return identifierAliases;
 	}
 
 	public String getVersionColumnName() {
 		return versionColumnName;
 	}
 
 	protected String getVersionedTableName() {
 		return getTableName( 0 );
 	}
 
 	protected boolean[] getSubclassColumnLazyiness() {
 		return subclassColumnLazyClosure;
 	}
 
 	protected boolean[] getSubclassFormulaLazyiness() {
 		return subclassFormulaLazyClosure;
 	}
 
 	/**
 	 * We can't immediately add to the cache if we have formulas
 	 * which must be evaluated, or if we have the possibility of
 	 * two concurrent updates to the same item being merged on
 	 * the database. This can happen if (a) the item is not
 	 * versioned and either (b) we have dynamic update enabled
 	 * or (c) we have multiple tables holding the state of the
 	 * item.
 	 */
 	public boolean isCacheInvalidationRequired() {
 		return hasFormulaProperties() ||
 				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return isLazyPropertiesCacheable;
 	}
 
 	public String selectFragment(String alias, String suffix) {
 		return identifierSelectFragment( alias, suffix ) +
 				propertySelectFragment( alias, suffix, false );
 	}
 
 	public String[] getIdentifierAliases(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
 	}
 
 	public String[] getPropertyAliases(String suffix, int i) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
 	}
 
 	public String getDiscriminatorAlias(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return entityMetamodel.hasSubclasses() ?
 				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
 				null;
 	}
 
 	public String identifierSelectFragment(String name, String suffix) {
 		return new SelectFragment()
 				.setSuffix( suffix )
 				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
 				.toFragmentString()
 				.substring( 2 ); //strip leading ", "
 	}
 
 
 	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
 		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
 	}
 
 	public SelectFragment propertySelectFragmentFragment(
 			String tableAlias,
 			String suffix,
 			boolean allProperties) {
 		SelectFragment select = new SelectFragment()
 				.setSuffix( suffix )
 				.setUsedAliases( getIdentifierAliases() );
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
 				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
 				subclassColumnSelectableClosure[i];
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
 				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
 				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
 				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
 			}
 		}
 
 		if ( entityMetamodel.hasSubclasses() ) {
 			addDiscriminatorToSelect( select, tableAlias, suffix );
 		}
 
 		if ( hasRowId() ) {
 			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
 		}
 
 		return select;
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Getting current persistent state for: " + MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
-			PreparedStatement ps = session.getBatcher().prepareSelectStatement( getSQLSnapshotSelectString() );
+			PreparedStatement ps = session.getJDBCContext().getConnectionManager().prepareSelectStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					//otherwise return the "hydrated" state (ie. associations are not resolved)
 					Type[] types = getPropertyTypes();
 					Object[] values = new Object[types.length];
 					boolean[] includeProperty = getPropertyUpdateability();
 					for ( int i = 0; i < types.length; i++ ) {
 						if ( includeProperty[i] ) {
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( ps );
+				ps.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve snapshot: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 			        getSQLSnapshotSelectString()
 				);
 		}
 
 	}
 
 	/**
 	 * Generate the SQL that selects the version number by id
 	 */
 	protected String generateSelectVersionString() {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getVersionedTableName() );
 		if ( isVersioned() ) {
 			select.addColumn( versionColumnName );
 		}
 		else {
 			select.addColumns( rootTableKeyColumnNames );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyInsertGenerationInclusions() );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyUpdateGenerationInclusions() );
 	}
 
 	private String generateGeneratedValuesSelectString(ValueInclusion[] inclusions) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment( getRootAlias(), inclusions );
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final ValueInclusion[] inclusions) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					// TODO : currently we really do not handle ValueInclusion.PARTIAL...
 					// ValueInclusion.PARTIAL would indicate parts of a component need to
 					// be included in the select; currently we then just render the entire
 					// component into the select clause in that case.
 					public boolean includeProperty(int propertyNumber) {
 						return inclusions[propertyNumber] != ValueInclusion.NONE;
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
 		if ( log.isTraceEnabled() ) {
 			log.trace(
 					"Forcing version increment [" + MessageHelper.infoString( this, id, getFactory() ) +
 					"; " + getVersionType().toLoggableString( currentVersion, getFactory() ) +
 					" -> " + getVersionType().toLoggableString( nextVersion, getFactory() ) + "]"
 			);
 		}
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			try {
-				st = session.getBatcher().prepareStatement( versionIncrementString );
+				st = session.getJDBCContext().getConnectionManager().prepareStatement( versionIncrementString, false );
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = st.executeUpdate();
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Getting version: " + MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 
-			PreparedStatement st = session.getBatcher().prepareSelectStatement( getVersionSelectString() );
+			PreparedStatement st = session.getJDBCContext().getConnectionManager().prepareSelectStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( st );
+				st.close();
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 	}
 
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
-					sequentialSelect = session.getBatcher().prepareSelectStatement( sql );
+					sequentialSelect = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
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
-				session.getBatcher().closeStatement( sequentialSelect );
+				sequentialSelect.close();
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
-				if ( callable ) {
-					insert = session.getBatcher().prepareBatchCallableStatement( sql );
-				}
-				else {
-					insert = session.getBatcher().prepareBatchStatement( sql );
-				}
+				insert = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 			}
 			else {
-				if ( callable ) {
-					insert = session.getBatcher().prepareCallableStatement( sql );
-				}
-				else {
-					insert = session.getBatcher().prepareStatement( sql );
-				}
+				insert = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index );
 
 				if ( useBatch ) {
 					// TODO : shouldnt inserts be Expectations.NONE?
-					session.getBatcher().addToBatch( expectation );
+					session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 				}
 				else {
 					expectation.verifyOutcome( insert.executeUpdate(), insert, -1 );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
-					session.getBatcher().abortBatch( sqle );
+					session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
-					session.getBatcher().closeStatement( insert );
+					insert.close();
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
-				if ( callable ) {
-					update = session.getBatcher().prepareBatchCallableStatement( sql );
-				}
-				else {
-					update = session.getBatcher().prepareBatchStatement( sql );
-				}
+				update = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 			}
 			else {
-				if ( callable ) {
-					update = session.getBatcher().prepareCallableStatement( sql );
-				}
-				else {
-					update = session.getBatcher().prepareStatement( sql );
-				}
+				update = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
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
-					session.getBatcher().addToBatch( expectation );
+					session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 					return true;
 				}
 				else {
 					return check( update.executeUpdate(), id, j, expectation, update );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
-					session.getBatcher().abortBatch( sqle );
+					session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
-					session.getBatcher().closeStatement( update );
+					update.close();
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
-				if ( callable ) {
-					delete = session.getBatcher().prepareBatchCallableStatement( sql );
-				}
-				else {
-					delete = session.getBatcher().prepareBatchStatement( sql );
-				}
+				delete = session.getJDBCContext().getConnectionManager().prepareBatchStatement( sql, callable );
 			}
 			else {
-				if ( callable ) {
-					delete = session.getBatcher().prepareCallableStatement( sql );
-				}
-				else {
-					delete = session.getBatcher().prepareStatement( sql );
-				}
+				delete = session.getJDBCContext().getConnectionManager().prepareStatement( sql, callable );
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
-					session.getBatcher().addToBatch( expectation );
+					session.getJDBCContext().getConnectionManager().addToBatch( expectation );
 				}
 				else {
 					check( delete.executeUpdate(), id, j, expectation, delete );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
-					session.getBatcher().abortBatch( sqle );
+					session.getJDBCContext().getConnectionManager().abortBatch( sqle );
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
-					session.getBatcher().closeStatement( delete );
+					delete.close();
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
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final int optimisticLockMode() {
 		return entityMetamodel.getOptimisticLockMode();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer( session.getEntityMode() )
 				.createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented(EntityMode entityMode) {
 		EntityTuplizer tuplizer = entityMetamodel.getTuplizerOrNull(entityMode);
 		return tuplizer!=null && tuplizer.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getTuplizer( session ).afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return entityMetamodel.getPropertyInsertGenerationInclusions();
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return entityMetamodel.getPropertyUpdateGenerationInclusions();
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass(EntityMode entityMode) {
 		Tuplizer tup = entityMetamodel.getTuplizerOrNull(entityMode);
 		return tup==null ? null : tup.getMappedClass();
 	}
 
 	public boolean implementsLifecycle(EntityMode entityMode) {
 		return getTuplizer( entityMode ).isLifecycleImplementor();
 	}
 
 	public boolean implementsValidatable(EntityMode entityMode) {
 		return getTuplizer( entityMode ).isValidatableImplementor();
 	}
 
 	public Class getConcreteProxyClass(EntityMode entityMode) {
 		return getTuplizer( entityMode ).getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getPropertyValues( object );
 	}
 
 	public Object getPropertyValue(Object object, int i, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getPropertyValue( object , i );
 	}
 
 	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getPropertyValue( object, propertyName );
 	}
 
 	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 		return getTuplizer( entityMode ).getIdentifier( object, null );
 	}
 
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getTuplizer( session.getEntityMode() ).getIdentifier( entity, session );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setIdentifier( entity, id, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getTuplizer( session ).setIdentifier( entity, id, session );
 	}
 
 	public Object getVersion(Object object, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getVersion( object );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object instantiate(Serializable id, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).instantiate( id, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object instantiate(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		return getTuplizer( session ).instantiate( id, session );
 	}
 
 	public boolean isInstance(Object object, EntityMode entityMode) {
 		return getTuplizer( entityMode ).isInstance( object );
 	}
 
 	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 		return getTuplizer( entityMode ).hasUninitializedLazyProperties( object );
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 		getTuplizer( entityMode ).resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getTuplizer( session ).resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityPersister getSubclassEntityPersister(
 			Object instance,
 			SessionFactoryImplementor factory,
 			EntityMode entityMode) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getTuplizer( entityMode )
 					.determineConcreteSubclassEntityName( instance, factory );
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public EntityMode guessEntityMode(Object object) {
 		return entityMetamodel.guessEntityMode(object);
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getTuplizer( session.getEntityMode() ).getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 	        ValueInclusion[] includeds) {
 
-		session.getBatcher().executeBatch(); //force immediate execution of the insert
+		session.getJDBCContext().getConnectionManager().executeBatch(); //force immediate execution of the insert
 
 		try {
-			PreparedStatement ps = session.getBatcher().prepareSelectStatement( selectionSQL );
+			PreparedStatement ps = session.getJDBCContext().getConnectionManager().prepareSelectStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					for ( int i = 0; i < getPropertySpan(); i++ ) {
 						if ( includeds[i] != ValueInclusion.NONE ) {
 							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 							setPropertyValue( entity, i, state[i], session.getEntityMode() );
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( ps );
+				ps.close();
 			}
 		}
 		catch( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
 		if ( log.isTraceEnabled() ) {
 			log.trace( "Getting current natural-id snapshot state for: " + MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
-			PreparedStatement ps = session.getBatcher().prepareSelectStatement( sql );
+			PreparedStatement ps = session.getJDBCContext().getConnectionManager().prepareSelectStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					final EntityKey key = new EntityKey( id, this, session.getEntityMode() );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
-				session.getBatcher().closeStatement( ps );
+				ps.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve snapshot: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 			        sql
 				);
 		}
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setPropertyValue( object, propertyName, value );
 	}
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransaction.java b/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransaction.java
index 436cf24348..ab56daed6d 100755
--- a/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransaction.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransaction.java
@@ -1,224 +1,224 @@
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
 package org.hibernate.transaction;
 
 import javax.transaction.Status;
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
 import org.hibernate.TransactionException;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.util.JTAHelper;
 
 /**
  * Implements a basic transaction strategy for CMT transactions. All work is
  * done in the context of the container managed transaction.
  * <p/>
  * The term 'CMT' is potentially misleading here; the pertinent point simply
  * being that the transactions are being managed by something other than the
  * Hibernate transaction mechanism.
  *
  * @author Gavin King
  */
 public class CMTTransaction implements Transaction {
 
 	private static final Logger log = LoggerFactory.getLogger(CMTTransaction.class);
 
 	protected final JDBCContext jdbcContext;
 	protected final TransactionFactory.Context transactionContext;
 
 	private boolean begun;
 
 	public CMTTransaction(JDBCContext jdbcContext, TransactionFactory.Context transactionContext) {
 		this.jdbcContext = jdbcContext;
 		this.transactionContext = transactionContext;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void begin() throws HibernateException {
 		if (begun) {
 			return;
 		}
 
 		log.debug("begin");
 		
 		boolean synchronization = jdbcContext.registerSynchronizationIfPossible();
 
 		if ( !synchronization ) {
 			throw new TransactionException("Could not register synchronization for container transaction");
 		}
 
 		begun = true;
 		
 		jdbcContext.afterTransactionBegin(this);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void commit() throws HibernateException {
 		if (!begun) {
 			throw new TransactionException("Transaction not successfully started");
 		}
 
 		log.debug("commit");
 
 		boolean flush = !transactionContext.isFlushModeNever() &&
 		        !transactionContext.isFlushBeforeCompletionEnabled();
 
 		if (flush) {
 			transactionContext.managedFlush(); //if an exception occurs during flush, user must call rollback()
 		}
 
 		begun = false;
 
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void rollback() throws HibernateException {
 		if (!begun) {
 			throw new TransactionException("Transaction not successfully started");
 		}
 
 		log.debug("rollback");
 
 		try {
 			getTransaction().setRollbackOnly();
 		}
 		catch (SystemException se) {
 			log.error("Could not set transaction to rollback only", se);
 			throw new TransactionException("Could not set transaction to rollback only", se);
 		}
 
 		begun = false;
 
 	}
 
 	/**
 	 * Getter for property 'transaction'.
 	 *
 	 * @return Value for property 'transaction'.
 	 */
 	public javax.transaction.Transaction getTransaction() throws SystemException {
 		return transactionContext.getFactory().getTransactionManager().getTransaction();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isActive() throws TransactionException {
 
 		if (!begun) return false;
 
 		final int status;
 		try {
 			status = getTransaction().getStatus();
 		}
 		catch (SystemException se) {
 			log.error("Could not determine transaction status", se);
 			throw new TransactionException("Could not determine transaction status: ", se);
 		}
 		if (status==Status.STATUS_UNKNOWN) {
 			throw new TransactionException("Could not determine transaction status");
 		}
 		else {
 			return status==Status.STATUS_ACTIVE;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean wasRolledBack() throws TransactionException {
 
 		if (!begun) return false;
 
 		final int status;
 		try {
 			status = getTransaction().getStatus();
 		}
 		catch (SystemException se) {
 			log.error("Could not determine transaction status", se);
 			throw new TransactionException("Could not determine transaction status", se);
 		}
 		if (status==Status.STATUS_UNKNOWN) {
 			throw new TransactionException("Could not determine transaction status");
 		}
 		else {
 			return JTAHelper.isRollback(status);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean wasCommitted() throws TransactionException {
 
 		if ( !begun ) return false;
 
 		final int status;
 		try {
 			status = getTransaction().getStatus();
 		}
 		catch (SystemException se) {
 			log.error("Could not determine transaction status", se);
 			throw new TransactionException("Could not determine transaction status: ", se);
 		}
 		if (status==Status.STATUS_UNKNOWN) {
 			throw new TransactionException("Could not determine transaction status");
 		}
 		else {
 			return status==Status.STATUS_COMMITTED;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void registerSynchronization(Synchronization sync) throws HibernateException {
 		try {
 			getTransaction().registerSynchronization(sync);
 		}
 		catch (Exception e) {
 			throw new TransactionException("Could not register synchronization", e);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setTimeout(int seconds) {
 		throw new UnsupportedOperationException("cannot set transaction timeout in CMT");
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java
index 24717c7da7..fac6e191d1 100755
--- a/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/CMTTransactionFactory.java
@@ -1,79 +1,79 @@
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
 package org.hibernate.transaction;
 
 import java.util.Properties;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
 import org.hibernate.TransactionException;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.util.JTAHelper;
-import org.hibernate.jdbc.JDBCContext;
 
 import javax.transaction.SystemException;
 
 /**
  * Factory for {@link CMTTransaction} instances.
  *
  * @author Gavin King
  */
 public class CMTTransactionFactory implements TransactionFactory {
 
 	public ConnectionReleaseMode getDefaultReleaseMode() {
 		return ConnectionReleaseMode.AFTER_STATEMENT;
 	}
 
 	public void configure(Properties props) throws HibernateException {}
 
 	public Transaction createTransaction(JDBCContext jdbcContext, Context transactionContext)
 	throws HibernateException {
 		return new CMTTransaction(jdbcContext, transactionContext);
 	}
 
 	public boolean isTransactionManagerRequired() {
 		return true;
 	}
 
 	public boolean areCallbacksLocalToHibernateTransactions() {
 		return false;
 	}
 
 	public boolean isTransactionInProgress(
 			JDBCContext jdbcContext,
 	        Context transactionContext,
 	        Transaction transaction) {
 		try {
 			return JTAHelper.isTransactionInProgress(
 					transactionContext.getFactory().getTransactionManager().getTransaction()
 			);
 		}
 		catch( SystemException se ) {
 			throw new TransactionException( "Unable to check transaction status", se );
 		}
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/CacheSynchronization.java b/hibernate-core/src/main/java/org/hibernate/transaction/CacheSynchronization.java
index 9c626ba8ba..885443f9f3 100644
--- a/hibernate-core/src/main/java/org/hibernate/transaction/CacheSynchronization.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/CacheSynchronization.java
@@ -1,134 +1,134 @@
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
 package org.hibernate.transaction;
 
 import javax.transaction.Status;
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.TransactionException;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.util.JTAHelper;
 
 /**
  * A JTA transaction synch used to allow the {@link org.hibernate.Session} to know about transaction
  * events.
  *
  * @author Gavin King
  */
 public final class CacheSynchronization implements Synchronization {
 
 	private static final Logger log = LoggerFactory.getLogger(CacheSynchronization.class);
 
 	private final TransactionFactory.Context ctx;
 	private JDBCContext jdbcContext;
 	private final Transaction transaction;
 	private final org.hibernate.Transaction hibernateTransaction;
 
 	public CacheSynchronization(
 			TransactionFactory.Context ctx, 
-			JDBCContext jdbcContext, 
+			JDBCContext jdbcContext,
 			Transaction transaction, 
 			org.hibernate.Transaction tx) {
 		this.ctx = ctx;
 		this.jdbcContext = jdbcContext;
 		this.transaction = transaction;
 		this.hibernateTransaction = tx;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void beforeCompletion() {
 		log.trace("transaction before completion callback");
 
 		boolean flush;
 		try {
 			flush = !ctx.isFlushModeNever() &&
 			        ctx.isFlushBeforeCompletionEnabled() && 
 			        !JTAHelper.isRollback( transaction.getStatus() ); 
 					//actually, this last test is probably unnecessary, since 
 					//beforeCompletion() doesn't get called during rollback
 		}
 		catch (SystemException se) {
 			log.error("could not determine transaction status", se);
 			setRollbackOnly();
 			throw new TransactionException("could not determine transaction status in beforeCompletion()", se);
 		}
 		
 		try {
 			if (flush) {
 				log.trace("automatically flushing session");
 				ctx.managedFlush();
 			}
 		}
 		catch (RuntimeException re) {
 			setRollbackOnly();
 			throw re;
 		}
 		finally {
 			jdbcContext.beforeTransactionCompletion(hibernateTransaction);
 		}
 	}
 
 	private void setRollbackOnly() {
 		try {
 			transaction.setRollbackOnly();
 		}
 		catch (SystemException se) {
 			log.error("could not set transaction to rollback only", se);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void afterCompletion(int status) {
 		if ( log.isTraceEnabled() ) {
 			log.trace("transaction after completion callback, status: " + status);
 		}
 		try {
 			jdbcContext.afterTransactionCompletion(status==Status.STATUS_COMMITTED, hibernateTransaction);
 		}
 		finally {
 			if ( ctx.shouldAutoClose() && !ctx.isClosed() ) {
 				log.trace("automatically closing session");
 				ctx.managedClose();
 			}
 		}
 	}
 	
 	/**
 	 * {@inheritDoc}
 	 */
 	public String toString() {
 		return CacheSynchronization.class.getName();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransaction.java b/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransaction.java
index db651d02d7..f89813b646 100644
--- a/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransaction.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransaction.java
@@ -1,276 +1,275 @@
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
 package org.hibernate.transaction;
 
 import java.sql.SQLException;
 import javax.transaction.Status;
 import javax.transaction.Synchronization;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
 import org.hibernate.TransactionException;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.engine.transaction.SynchronizationRegistry;
-import org.hibernate.jdbc.JDBCContext;
 
 /**
  * {@link Transaction} implementation based on transaction management through a JDBC {@link java.sql.Connection}.
  * <p/>
  * This the Hibernate's default transaction strategy.
  *
  * @author Anton van Straaten
  * @author Gavin King
  */
 public class JDBCTransaction implements Transaction {
 	private static final Logger log = LoggerFactory.getLogger(JDBCTransaction.class);
 
 	private final SynchronizationRegistry synchronizationRegistry = new SynchronizationRegistry();
 	private final JDBCContext jdbcContext;
 	private final TransactionFactory.Context transactionContext;
 
 	private boolean toggleAutoCommit;
 	private boolean begun;
 	private boolean rolledBack;
 	private boolean committed;
 	private boolean commitFailed;
 	private boolean callback;
 	private int timeout = -1;
 
 	public JDBCTransaction(JDBCContext jdbcContext, TransactionFactory.Context transactionContext) {
 		this.jdbcContext = jdbcContext;
 		this.transactionContext = transactionContext;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void begin() throws HibernateException {
 		if (begun) {
 			return;
 		}
 		if (commitFailed) {
 			throw new TransactionException("cannot re-start transaction after failed commit");
 		}
 
 		log.debug("begin");
 
 		try {
 			toggleAutoCommit = jdbcContext.connection().getAutoCommit();
 			if ( log.isDebugEnabled() ) {
 				log.debug("current autocommit status: " + toggleAutoCommit);
 			}
 			if (toggleAutoCommit) {
 				log.debug("disabling autocommit");
 				jdbcContext.connection().setAutoCommit(false);
 			}
 		}
 		catch (SQLException e) {
 			log.error("JDBC begin failed", e);
 			throw new TransactionException("JDBC begin failed: ", e);
 		}
 
 		callback = jdbcContext.registerCallbackIfNecessary();
 
 		begun = true;
 		committed = false;
 		rolledBack = false;
 
 		if ( timeout>0 ) {
 			jdbcContext.getConnectionManager()
-					.getBatcher()
 					.setTransactionTimeout(timeout);
 		}
 
 		jdbcContext.afterTransactionBegin(this);
 	}
 
 	private void closeIfRequired() throws HibernateException {
 		if ( callback && transactionContext.shouldAutoClose() && !transactionContext.isClosed() ) {
 			try {
 				transactionContext.managedClose();
 			}
 			catch (HibernateException he) {
 				log.error("Could not close session", he);
 				//swallow, the transaction was finished
 			}
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void commit() throws HibernateException {
 		if (!begun) {
 			throw new TransactionException("Transaction not successfully started");
 		}
 
 		log.debug("commit");
 
 		if ( !transactionContext.isFlushModeNever() && callback ) {
 			transactionContext.managedFlush(); //if an exception occurs during flush, user must call rollback()
 		}
 
 		notifySynchronizationsBeforeTransactionCompletion();
 		if ( callback ) {
 			jdbcContext.beforeTransactionCompletion( this );
 		}
 
 		try {
 			commitAndResetAutoCommit();
 			log.debug("committed JDBC Connection");
 			committed = true;
 			if ( callback ) {
 				jdbcContext.afterTransactionCompletion( true, this );
 			}
 			notifySynchronizationsAfterTransactionCompletion( Status.STATUS_COMMITTED );
 		}
 		catch (SQLException e) {
 			log.error("JDBC commit failed", e);
 			commitFailed = true;
 			if ( callback ) {
 				jdbcContext.afterTransactionCompletion( false, this );
 			}
 			notifySynchronizationsAfterTransactionCompletion( Status.STATUS_UNKNOWN );
 			throw new TransactionException("JDBC commit failed", e);
 		}
 		finally {
 			closeIfRequired();
 		}
 	}
 
 	private void commitAndResetAutoCommit() throws SQLException {
 		try {
 			jdbcContext.connection().commit();
 		}
 		finally {
 			toggleAutoCommit();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void rollback() throws HibernateException {
 
 		if (!begun && !commitFailed) {
 			throw new TransactionException("Transaction not successfully started");
 		}
 
 		log.debug("rollback");
 
 		if (!commitFailed) {
 
 			/*notifyLocalSynchsBeforeTransactionCompletion();
 			if ( callback ) {
 				jdbcContext.notifyLocalSynchsBeforeTransactionCompletion( this );
 			}*/
 
 			try {
 				rollbackAndResetAutoCommit();
 				log.debug("rolled back JDBC Connection");
 				rolledBack = true;
 				notifySynchronizationsAfterTransactionCompletion(Status.STATUS_ROLLEDBACK);
 			}
 			catch (SQLException e) {
 				log.error("JDBC rollback failed", e);
 				notifySynchronizationsAfterTransactionCompletion(Status.STATUS_UNKNOWN);
 				throw new TransactionException("JDBC rollback failed", e);
 			}
 			finally {
 				if ( callback ) {
 					jdbcContext.afterTransactionCompletion( false, this );
 				}
 				closeIfRequired();
 			}
 		}
 	}
 
 	private void rollbackAndResetAutoCommit() throws SQLException {
 		try {
 			jdbcContext.connection().rollback();
 		}
 		finally {
 			toggleAutoCommit();
 		}
 	}
 
 	private void toggleAutoCommit() {
 		try {
 			if (toggleAutoCommit) {
 				log.debug("re-enabling autocommit");
 				jdbcContext.connection().setAutoCommit( true );
 			}
 		}
 		catch (Exception sqle) {
 			log.error("Could not toggle autocommit", sqle);
 			//swallow it (the transaction _was_ successful or successfully rolled back)
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean wasRolledBack() {
 		return rolledBack;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean wasCommitted() {
 		return committed;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isActive() {
 		return begun && ! ( rolledBack || committed | commitFailed );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void registerSynchronization(Synchronization sync) {
 		synchronizationRegistry.registerSynchronization( sync );
 	}
 
 	private void notifySynchronizationsBeforeTransactionCompletion() {
 		synchronizationRegistry.notifySynchronizationsBeforeTransactionCompletion();
 	}
 
 	private void notifySynchronizationsAfterTransactionCompletion(int status) {
 		begun = false;
 		synchronizationRegistry.notifySynchronizationsAfterTransactionCompletion( status );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setTimeout(int seconds) {
 		timeout = seconds;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java
index c57de0dc88..a0f6136550 100644
--- a/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/JDBCTransactionFactory.java
@@ -1,96 +1,96 @@
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
 package org.hibernate.transaction;
 
 import java.util.Properties;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 
 /**
  * Factory for {@link JDBCTransaction} instances.
  *
  * @author Anton van Straaten
  */
 public final class JDBCTransactionFactory implements TransactionFactory {
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public ConnectionReleaseMode getDefaultReleaseMode() {
 		return ConnectionReleaseMode.AFTER_TRANSACTION;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Transaction createTransaction(JDBCContext jdbcContext, Context transactionContext)
 	throws HibernateException {
 		return new JDBCTransaction( jdbcContext, transactionContext );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void configure(Properties props) throws HibernateException {}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isTransactionManagerRequired() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean areCallbacksLocalToHibernateTransactions() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isTransactionInProgress(
 			JDBCContext jdbcContext,
 	        Context transactionContext,
 	        Transaction transaction) {
 //		try {
 //			// for JDBC-based transactions, we only want to return true
 //			// here if we (this transaction) are managing the transaction
 //			return transaction != null &&
 //			       transaction.isActive() &&
 //			       !jdbcContext.getConnectionManager().isAutoCommit();
 //		}
 //		catch ( SQLException e ) {
 //			// assume we are in auto-commit!
 //			return false;
 //		}
 		return transaction != null && transaction.isActive();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/JTATransaction.java b/hibernate-core/src/main/java/org/hibernate/transaction/JTATransaction.java
index 42a8905d54..856362414e 100644
--- a/hibernate-core/src/main/java/org/hibernate/transaction/JTATransaction.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/JTATransaction.java
@@ -1,359 +1,359 @@
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
 package org.hibernate.transaction;
 
 import javax.transaction.Status;
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 import javax.transaction.TransactionManager;
 import javax.transaction.UserTransaction;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
 import org.hibernate.TransactionException;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.util.JTAHelper;
 
 /**
  * {@link Transaction} implementation based on transaction management through
  * a JTA {@link UserTransaction}.  Similar to {@link CMTTransaction}, except
  * here we are actually managing the transactions through the Hibernate
  * transaction mechanism.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Les Hazlewood
  */
 public class JTATransaction implements Transaction {
 
 	private static final Logger log = LoggerFactory.getLogger( JTATransaction.class );
 
 	private final JDBCContext jdbcContext;
 	private final TransactionFactory.Context transactionContext;
 
 	private UserTransaction userTransaction;
 	private boolean newTransaction;
 	private boolean begun;
 	private boolean commitFailed;
 	private boolean commitSucceeded;
 	private boolean callback;
 
 	public JTATransaction(
 			UserTransaction userTransaction,
 			JDBCContext jdbcContext,
 			TransactionFactory.Context transactionContext) {
 		this.jdbcContext = jdbcContext;
 		this.transactionContext = transactionContext;
 		this.userTransaction = userTransaction;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void begin() throws HibernateException {
 		if ( begun ) {
 			return;
 		}
 		if ( commitFailed ) {
 			throw new TransactionException( "cannot re-start transaction after failed commit" );
 		}
 
 		log.debug( "begin" );
 
 		try {
 			newTransaction = userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION;
 			if ( newTransaction ) {
 				userTransaction.begin();
 				log.debug( "Began a new JTA transaction" );
 			}
 		}
 		catch ( Exception e ) {
 			log.error( "JTA transaction begin failed", e );
 			throw new TransactionException( "JTA transaction begin failed", e );
 		}
 
 		/*if (newTransaction) {
 			// don't need a synchronization since we are committing
 			// or rolling back the transaction ourselves - assuming
 			// that we do no work in beforeTransactionCompletion()
 			synchronization = false;
 		}*/
 
 		boolean synchronization = jdbcContext.registerSynchronizationIfPossible();
 
 		if ( !newTransaction && !synchronization ) {
 			log.warn( "You should set hibernate.transaction.manager_lookup_class if cache is enabled" );
 		}
 
 		if ( !synchronization ) {
 			//if we could not register a synchronization,
 			//do the before/after completion callbacks
 			//ourself (but we need to let jdbcContext
 			//know that this is what we are going to
 			//do, so it doesn't keep trying to register
 			//synchronizations)
 			callback = jdbcContext.registerCallbackIfNecessary();
 		}
 
 		begun = true;
 		commitSucceeded = false;
 
 		jdbcContext.afterTransactionBegin( this );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void commit() throws HibernateException {
 		if ( !begun ) {
 			throw new TransactionException( "Transaction not successfully started" );
 		}
 
 		log.debug( "commit" );
 
 		boolean flush = !transactionContext.isFlushModeNever()
 				&& ( callback || !transactionContext.isFlushBeforeCompletionEnabled() );
 
 		if ( flush ) {
 			transactionContext.managedFlush(); //if an exception occurs during flush, user must call rollback()
 		}
 
 		if ( callback && newTransaction ) {
 			jdbcContext.beforeTransactionCompletion( this );
 		}
 
 		closeIfRequired();
 
 		if ( newTransaction ) {
 			try {
 				userTransaction.commit();
 				commitSucceeded = true;
 				log.debug( "Committed JTA UserTransaction" );
 			}
 			catch ( Exception e ) {
 				commitFailed = true; // so the transaction is already rolled back, by JTA spec
 				log.error( "JTA commit failed", e );
 				throw new TransactionException( "JTA commit failed: ", e );
 			}
 			finally {
 				afterCommitRollback();
 			}
 		}
 		else {
 			// this one only really needed for badly-behaved applications!
 			// (if the TransactionManager has a Sychronization registered,
 			// its a noop)
 			// (actually we do need it for downgrading locks)
 			afterCommitRollback();
 		}
 
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void rollback() throws HibernateException {
 		if ( !begun && !commitFailed ) {
 			throw new TransactionException( "Transaction not successfully started" );
 		}
 
 		log.debug( "rollback" );
 
 		try {
 			closeIfRequired();
 		}
 		catch ( Exception e ) {
 			// swallow it, and continue to roll back JTA transaction
 			log.error( "could not close session during rollback", e );
 		}
 
 		try {
 			if ( newTransaction ) {
 				if ( !commitFailed ) {
 					userTransaction.rollback();
 					log.debug( "Rolled back JTA UserTransaction" );
 				}
 			}
 			else {
 				userTransaction.setRollbackOnly();
 				log.debug( "set JTA UserTransaction to rollback only" );
 			}
 		}
 		catch ( Exception e ) {
 			log.error( "JTA rollback failed", e );
 			throw new TransactionException( "JTA rollback failed", e );
 		}
 		finally {
 			afterCommitRollback();
 		}
 	}
 
 	private static final int NULL = Integer.MIN_VALUE;
 
 	private void afterCommitRollback() throws TransactionException {
 
 		begun = false;
 		// this method is a noop if there is a Synchronization!
 		if ( callback ) {
 			if ( !newTransaction ) {
 				log.warn( "You should set hibernate.transaction.manager_lookup_class if cache is enabled" );
 			}
 			int status = NULL;
 			try {
 				status = userTransaction.getStatus();
 			}
 			catch ( Exception e ) {
 				log.error( "Could not determine transaction status after commit", e );
 				throw new TransactionException( "Could not determine transaction status after commit", e );
 			}
 			finally {
 				jdbcContext.afterTransactionCompletion( status == Status.STATUS_COMMITTED, this );
 			}
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean wasRolledBack() throws TransactionException {
 		final int status;
 		try {
 			status = userTransaction.getStatus();
 		}
 		catch ( SystemException se ) {
 			log.error( "Could not determine transaction status", se );
 			throw new TransactionException( "Could not determine transaction status", se );
 		}
 		if ( status == Status.STATUS_UNKNOWN ) {
 			throw new TransactionException( "Could not determine transaction status" );
 		}
 		else {
 			return JTAHelper.isRollback( status );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean wasCommitted() throws TransactionException {
 		final int status;
 		try {
 			status = userTransaction.getStatus();
 		}
 		catch ( SystemException se ) {
 			log.error( "Could not determine transaction status", se );
 			throw new TransactionException( "Could not determine transaction status: ", se );
 		}
 		if ( status == Status.STATUS_UNKNOWN ) {
 			throw new TransactionException( "Could not determine transaction status" );
 		}
 		else {
 			return status == Status.STATUS_COMMITTED;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isActive() throws TransactionException {
 		if ( !begun || commitFailed || commitSucceeded ) {
 			return false;
 		}
 
 		final int status;
 		try {
 			status = userTransaction.getStatus();
 		}
 		catch ( SystemException se ) {
 			log.error( "Could not determine transaction status", se );
 			throw new TransactionException( "Could not determine transaction status: ", se );
 		}
 		if ( status == Status.STATUS_UNKNOWN ) {
 			throw new TransactionException( "Could not determine transaction status" );
 		}
 		else {
 			return status == Status.STATUS_ACTIVE;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void registerSynchronization(Synchronization sync) throws HibernateException {
 		if ( getTransactionManager() == null ) {
 			throw new IllegalStateException( "JTA TransactionManager not available" );
 		}
 		else {
 			try {
 				getTransactionManager().getTransaction().registerSynchronization( sync );
 			}
 			catch ( Exception e ) {
 				throw new TransactionException( "could not register synchronization", e );
 			}
 		}
 	}
 
 	/**
 	 * Getter for property 'transactionManager'.
 	 *
 	 * @return Value for property 'transactionManager'.
 	 */
 	private TransactionManager getTransactionManager() {
 		return transactionContext.getFactory().getTransactionManager();
 	}
 
 	private void closeIfRequired() throws HibernateException {
 		boolean close = callback &&
 				transactionContext.shouldAutoClose() &&
 				!transactionContext.isClosed();
 		if ( close ) {
 			transactionContext.managedClose();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setTimeout(int seconds) {
 		try {
 			userTransaction.setTransactionTimeout( seconds );
 		}
 		catch ( SystemException se ) {
 			throw new TransactionException( "could not set transaction timeout", se );
 		}
 	}
 
 	/**
 	 * Getter for property 'userTransaction'.
 	 *
 	 * @return Value for property 'userTransaction'.
 	 */
 	protected UserTransaction getUserTransaction() {
 		return userTransaction;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java
index 96b49ad089..82b2713545 100644
--- a/hibernate-core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/JTATransactionFactory.java
@@ -1,254 +1,254 @@
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
 package org.hibernate.transaction;
 
 import java.util.Properties;
 
 import javax.naming.InitialContext;
 import javax.naming.NamingException;
 import javax.transaction.SystemException;
 import javax.transaction.UserTransaction;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
 import org.hibernate.TransactionException;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.internal.util.jndi.JndiHelper;
-import org.hibernate.jdbc.JDBCContext;
 import org.hibernate.cfg.Environment;
 import org.hibernate.util.JTAHelper;
 
 /**
  * Factory for {@link JTATransaction} instances.
  * <p/>
  * To be completely accurate to the JTA spec, JTA implementations should
  * publish their contextual {@link UserTransaction} reference into JNDI.
  * However, in practice there are quite a few <tt>stand-alone</tt>
  * implementations intended for use outside of J2EE/JEE containers and
  * which therefore do not publish their {@link UserTransaction} references
  * into JNDI but which otherwise follow the aspects of the JTA specification.
  * This {@link TransactionFactory} implementation can support both models.
  * <p/>
  * For complete JTA implementations (including dependence on JNDI), the
  * {@link UserTransaction} reference is obtained by a call to
  * {@link #resolveInitialContext}.  Hibernate will then attempt to locate the
  * {@link UserTransaction} within this resolved
  * {@link InitialContext} based on the namespace returned by
  * {@link #resolveUserTransactionName}.
  * <p/>
  * For the so-called <tt>stand-alone</tt> implementations, we do not care at
  * all about the JNDI aspects just described.  Here, the implementation would
  * have a specific manner to obtain a reference to its contextual
  * {@link UserTransaction}; usually this would be a static code reference, but
  * again it varies.  Anyway, for each implementation the integration would need
  * to override the {@link #getUserTransaction} method and return the appropriate
  * thing.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Les Hazlewood
  */
 public class JTATransactionFactory implements TransactionFactory {
 	public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";
 	private static final Logger log = LoggerFactory.getLogger( JTATransactionFactory.class );
 
 	protected InitialContext initialContext;
 	protected String userTransactionName;
 
 	/**
 	 * Configure this transaction factory.  Specifically here we are attempting to
 	 * resolve both an {@link #getInitialContext InitialContext} as well as the
 	 * {@link #getUserTransactionName() JNDI namespace} for the {@link UserTransaction}.
 	 *
 	 * @param props The configuration properties
 	 *
 	 * @exception HibernateException
 	 */
 	public void configure(Properties props) throws HibernateException {
 		this.initialContext = resolveInitialContext( props );
 		this.userTransactionName = resolveUserTransactionName( props );
 		log.trace( "Configured JTATransactionFactory to use [{}] for UserTransaction JDNI namespace", userTransactionName );
 	}
 
 	/**
 	 * Given the lot of Hibernate configuration properties, resolve appropriate
 	 * reference to JNDI {@link InitialContext}.
 	 * <p/>
 	 * In general, the properties in which we are interested here all begin with
 	 * <tt>hibernate.jndi</tt>.  Especially important depending on your
 	 * environment are {@link Environment#JNDI_URL hibernate.jndi.url} and
 	 *  {@link Environment#JNDI_CLASS hibernate.jndi.class}
 	 *
 	 * @param properties The Hibernate config properties.
 	 * @return The resolved InitialContext.
 	 */
 	protected final InitialContext resolveInitialContext(Properties properties) {
 		try {
 			return JndiHelper.getInitialContext( properties );
 		}
 		catch ( NamingException ne ) {
 			throw new HibernateException( "Could not obtain initial context", ne );
 		}
 	}
 
 	/**
 	 * Given the lot of Hibernate configuration properties, resolve appropriate
 	 * JNDI namespace to use for {@link UserTransaction} resolution.
 	 * <p/>
 	 * We determine the namespace to use by<ol>
 	 * <li>Any specified {@link Environment#USER_TRANSACTION jta.UserTransaction} config property</li>
 	 * <li>If a {@link TransactionManagerLookup} was indicated, use its
 	 * {@link TransactionManagerLookup#getUserTransactionName}</li>
 	 * <li>finally, as a last resort, we use {@link #DEFAULT_USER_TRANSACTION_NAME}</li>
 	 * </ol>
 	 *
 	 * @param properties The Hibernate config properties.
 	 * @return The resolved {@link UserTransaction} namespace
 	 */
 	protected final String resolveUserTransactionName(Properties properties) {
 		String utName = properties.getProperty( Environment.USER_TRANSACTION );
 		if ( utName == null ) {
 			TransactionManagerLookup lookup = TransactionManagerLookupFactory.getTransactionManagerLookup( properties );
 			if ( lookup != null ) {
 				utName = lookup.getUserTransactionName();
 			}
 		}
 		return utName == null ? DEFAULT_USER_TRANSACTION_NAME : utName;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Transaction createTransaction(JDBCContext jdbcContext, Context transactionContext)
 			throws HibernateException {
 		UserTransaction ut = getUserTransaction();
 		return new JTATransaction( ut, jdbcContext, transactionContext );
 	}
 
 	/**
 	 * Get the {@link UserTransaction} reference.
 	 *
 	 * @return The appropriate {@link UserTransaction} reference.
 	 */
 	protected UserTransaction getUserTransaction() {
 		final String utName = getUserTransactionName();
 		log.trace( "Attempting to locate UserTransaction via JNDI [{}]", utName );
 
 		try {
 			UserTransaction ut = ( UserTransaction ) getInitialContext().lookup( utName );
 			if ( ut == null ) {
 				throw new TransactionException( "Naming service lookup for UserTransaction returned null [" + utName +"]" );
 			}
 
 			log.trace( "Obtained UserTransaction" );
 
 			return ut;
 		}
 		catch ( NamingException ne ) {
 			throw new TransactionException( "Could not find UserTransaction in JNDI [" + utName + "]", ne );
 		}
 	}
 
 	/**
 	 * Getter for property 'initialContext'.
 	 *
 	 * @return Value for property 'initialContext'.
 	 */
 	protected InitialContext getInitialContext() {
 		return initialContext;
 	}
 
 	/**
 	 * Getter for property 'userTransactionName'.
 	 * The algorithm here is
 	 *
 	 * @return Value for property 'userTransactionName'.
 	 */
 	protected String getUserTransactionName() {
 		return userTransactionName;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public ConnectionReleaseMode getDefaultReleaseMode() {
 		return ConnectionReleaseMode.AFTER_STATEMENT;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isTransactionManagerRequired() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean areCallbacksLocalToHibernateTransactions() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isTransactionInProgress(
 			JDBCContext jdbcContext,
 			Context transactionContext,
 			Transaction transaction) {
 		try {
 			// Essentially:
 			// 1) If we have a local (Hibernate) transaction in progress
 			//      and it already has the UserTransaction cached, use that
 			//      UserTransaction to determine the status.
 			// 2) If a transaction manager has been located, use
 			//      that transaction manager to determine the status.
 			// 3) Finally, as the last resort, try to lookup the
 			//      UserTransaction via JNDI and use that to determine the
 			//      status.
 			if ( transaction != null ) {
 				UserTransaction ut = ( ( JTATransaction ) transaction ).getUserTransaction();
 				if ( ut != null ) {
 					return JTAHelper.isInProgress( ut.getStatus() );
 				}
 			}
 
 			if ( jdbcContext.getFactory().getTransactionManager() != null ) {
 				return JTAHelper.isInProgress( jdbcContext.getFactory().getTransactionManager().getStatus() );
 			}
 			else {
 				UserTransaction ut = getUserTransaction();
 				return ut != null && JTAHelper.isInProgress( ut.getStatus() );
 			}
 		}
 		catch ( SystemException se ) {
 			throw new TransactionException( "Unable to check transaction status", se );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactory.java
index 20cb1ce13b..1f55957649 100644
--- a/hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/TransactionFactory.java
@@ -1,124 +1,124 @@
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
 package org.hibernate.transaction;
 
 import java.util.Properties;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 
 /**
  * Contract for generating Hibernate {@link Transaction} instances.
  * <p/>
  * The concrete implementation to be used is specified by the
  * {@link org.hibernate.cfg.Environment#TRANSACTION_STRATEGY} configuration
  * setting.
  * <p/>
  * Implementors must be threadsafe and should declare a public default constructor.
  *
  * @see Transaction
  *
  * @author Anton van Straaten
  * @author Gavin King
  */
 public interface TransactionFactory {
 
 	/**
 	 * Callback mechanism; a context is always a {@link org.hibernate.Session}
 	 * in the Hibernate usage.
 	 */
 	public static interface Context {
 		public SessionFactoryImplementor getFactory();
 		public boolean isClosed();
 
 		public boolean isFlushModeNever();
 		public boolean isFlushBeforeCompletionEnabled();
 		public void managedFlush();
 
 		public boolean shouldAutoClose();
 		public void managedClose();
 	}
 
 	/**
 	 * Begin a transaction and return the associated <tt>Transaction</tt> instance.
 	 *
 	 * @param jdbcContext  The jdbc context to which the transaction belongs
 	 * @param context The contract regarding the context in which this transaction will operate.
 	 * @return Transaction
 	 * @throws HibernateException Indicates a problem generating a transaction instance
 	 */
 	public Transaction createTransaction(JDBCContext jdbcContext, Context context) throws HibernateException;
 
 	/**
 	 * Configure from the given properties.
 	 *
 	 * @param props The configuration properties.
 	 * @throws HibernateException Indicates a problem configuring this factory.
 	 */
 	public void configure(Properties props) throws HibernateException;
 	
 	/**
 	 * Get the default connection release mode.
 	 *
 	 * @return The default release mode associated with this strategy
 	 */
 	public ConnectionReleaseMode getDefaultReleaseMode();
 	
 	/**
 	 * Do we require access to the JTA TransactionManager for
 	 * this strategy?
 	 *
 	 * @return True if this strategy requires access to the JTA TransactionManager;
 	 * false otherwise.
 	 */
 	public boolean isTransactionManagerRequired();
 
 	/**
 	 * Are all transaction callbacks local to Hibernate Transactions?
 	 * Or can the callbacks originate from some other source (e.g. a JTA
 	 * Synchronization).
 	 *
 	 * @return true if callbacks only ever originate from the Hibernate
 	 * {@link Transaction}; false otherwise.
 	 */
 	public boolean areCallbacksLocalToHibernateTransactions();
 
 	/**
 	 * Determine whether an underlying transaction is in progress.
 	 * <p/>
 	 * Mainly this is used in determining whether to register a
 	 * synchronization as well as whether or not to circumvent
 	 * auto flushing outside transactions.
 	 *
 	 * @param jdbcContext The JDBC context
 	 * @param transactionContext The transaction context
 	 * @param transaction The Hibernate transaction
 	 * @return true if an underlying transaction is know to be in effect.
 	 */
 	public boolean isTransactionInProgress(JDBCContext jdbcContext, Context transactionContext, Transaction transaction);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java b/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java
index 286b79e349..3f42cc2d8c 100644
--- a/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java
+++ b/hibernate-core/src/main/java/org/hibernate/transaction/synchronization/CallbackCoordinator.java
@@ -1,186 +1,185 @@
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
 package org.hibernate.transaction.synchronization;
 
-import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.transaction.Status;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
-import org.hibernate.HibernateException;
 import org.hibernate.TransactionException;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.util.JTAHelper;
 
 /**
  * Manages callbacks from the {@link javax.transaction.Synchronization} registered by Hibernate.
  *
  * @author Steve Ebersole
  */
 public class CallbackCoordinator {
 	private static final Logger log = LoggerFactory.getLogger( CallbackCoordinator.class );
 
 	private final TransactionFactory.Context ctx;
 	private JDBCContext jdbcContext;
 	private final Transaction jtaTransaction;
 	private final org.hibernate.Transaction hibernateTransaction;
 
 	private BeforeCompletionManagedFlushChecker beforeCompletionManagedFlushChecker;
 	private AfterCompletionAction afterCompletionAction;
 	private ExceptionMapper exceptionMapper;
 
 	public CallbackCoordinator(
 			TransactionFactory.Context ctx,
 			JDBCContext jdbcContext,
 			Transaction jtaTransaction,
 			org.hibernate.Transaction hibernateTransaction) {
 		this.ctx = ctx;
 		this.jdbcContext = jdbcContext;
 		this.jtaTransaction = jtaTransaction;
 		this.hibernateTransaction = hibernateTransaction;
 		reset();
 	}
 
 	public void reset() {
 		beforeCompletionManagedFlushChecker = STANDARD_MANAGED_FLUSH_CHECKER;
 		exceptionMapper = STANDARD_EXCEPTION_MAPPER;
 		afterCompletionAction = STANDARD_AFTER_COMPLETION_ACTION;
 	}
 
 	public BeforeCompletionManagedFlushChecker getBeforeCompletionManagedFlushChecker() {
 		return beforeCompletionManagedFlushChecker;
 	}
 
 	public void setBeforeCompletionManagedFlushChecker(BeforeCompletionManagedFlushChecker beforeCompletionManagedFlushChecker) {
 		this.beforeCompletionManagedFlushChecker = beforeCompletionManagedFlushChecker;
 	}
 
 	public ExceptionMapper getExceptionMapper() {
 		return exceptionMapper;
 	}
 
 	public void setExceptionMapper(ExceptionMapper exceptionMapper) {
 		this.exceptionMapper = exceptionMapper;
 	}
 
 	public AfterCompletionAction getAfterCompletionAction() {
 		return afterCompletionAction;
 	}
 
 	public void setAfterCompletionAction(AfterCompletionAction afterCompletionAction) {
 		this.afterCompletionAction = afterCompletionAction;
 	}
 
 
 	// sync callbacks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void beforeCompletion() {
 		log.trace( "transaction before completion callback" );
 
 		boolean flush;
 		try {
 			flush = beforeCompletionManagedFlushChecker.shouldDoManagedFlush( ctx, jtaTransaction );
 		}
 		catch ( SystemException se ) {
 			setRollbackOnly();
 			throw exceptionMapper.mapStatusCheckFailure( "could not determine transaction status in beforeCompletion()", se );
 		}
 
 		try {
 			if ( flush ) {
 				log.trace( "automatically flushing session" );
 				ctx.managedFlush();
 			}
 		}
 		catch ( RuntimeException re ) {
 			setRollbackOnly();
 			throw exceptionMapper.mapManagedFlushFailure( "error during managed flush", re );
 		}
 		finally {
 			jdbcContext.beforeTransactionCompletion( hibernateTransaction );
 		}
 	}
 
 	private void setRollbackOnly() {
 		try {
 			jtaTransaction.setRollbackOnly();
 		}
 		catch ( SystemException se ) {
 			// best effort
 			log.error( "could not set transaction to rollback only", se );
 		}
 	}
 
 	public void afterCompletion(int status) {
 		log.trace( "transaction after completion callback [status={}]", status );
 
 		try {
 			afterCompletionAction.doAction( ctx, status );
 
 			final boolean wasSuccessful = ( status == Status.STATUS_COMMITTED );
 			jdbcContext.afterTransactionCompletion( wasSuccessful, hibernateTransaction );
 		}
 		finally {
 			reset();
 			jdbcContext.cleanUpJtaSynchronizationCallbackCoordinator();
 			if ( ctx.shouldAutoClose() && !ctx.isClosed() ) {
 				log.trace( "automatically closing session" );
 				ctx.managedClose();
 			}
 		}
 	}
 
 	private static final BeforeCompletionManagedFlushChecker STANDARD_MANAGED_FLUSH_CHECKER = new BeforeCompletionManagedFlushChecker() {
 		public boolean shouldDoManagedFlush(TransactionFactory.Context ctx, Transaction jtaTransaction)
 				throws SystemException {
-			return !ctx.isFlushModeNever() &&
+			return !ctx.isClosed() &&
+					!ctx.isFlushModeNever() &&
 					ctx.isFlushBeforeCompletionEnabled() &&
 			        !JTAHelper.isRollback( jtaTransaction.getStatus() );
 					//actually, this last test is probably unnecessary, since
 					//beforeCompletion() doesn't get called during rollback
 		}
 	};
 
 	private static final ExceptionMapper STANDARD_EXCEPTION_MAPPER = new ExceptionMapper() {
 		public RuntimeException mapStatusCheckFailure(String message, SystemException systemException) {
 			log.error( "could not determine transaction status [{}]", systemException.getMessage() );
 			return new TransactionException( "could not determine transaction status in beforeCompletion()", systemException );
 		}
 
 		public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure) {
 			log.error( "Error during managed flush [{}]", failure.getMessage() );
 			return failure;
 		}
 	};
 
 	private static final AfterCompletionAction STANDARD_AFTER_COMPLETION_ACTION = new AfterCompletionAction() {
 		public void doAction(TransactionFactory.Context ctx, int status) {
 			// nothing to do by default.
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
index 56d0568268..46df7b29c0 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
@@ -1,159 +1,158 @@
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
 package org.hibernate.type;
 
 import java.sql.Timestamp;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.ResultSet;
 import java.sql.CallableStatement;
 import java.util.Date;
 
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.exception.JDBCExceptionHelper;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * <tt>dbtimestamp</tt>: An extension of {@link TimestampType} which
  * maps to the database's current timestamp, rather than the jvm's
  * current timestamp.
  * <p/>
  * Note: May/may-not cause issues on dialects which do not properly support
  * a true notion of timestamp (Oracle < 8, for example, where only its DATE
  * datatype is supported).  Depends on the frequency of DML operations...
  *
  * @author Steve Ebersole
  */
 public class DbTimestampType extends TimestampType {
 	public static final DbTimestampType INSTANCE = new DbTimestampType();
 
 	private static final Logger log = LoggerFactory.getLogger( DbTimestampType.class );
 	
 	public String getName() {
 		return "dbtimestamp";
 	}
 
 	@Override
 	public String[] getRegistrationKeys() {
 		return new String[] { getName() };
 	}
 
 	public Date seed(SessionImplementor session) {
 		if ( session == null ) {
 			log.trace( "incoming session was null; using current jvm time" );
 			return super.seed( session );
 		}
 		else if ( !session.getFactory().getDialect().supportsCurrentTimestampSelection() ) {
 			log.debug( "falling back to vm-based timestamp, as dialect does not support current timestamp selection" );
 			return super.seed( session );
 		}
 		else {
 			return getCurrentTimestamp( session );
 		}
 	}
 
 	private Date getCurrentTimestamp(SessionImplementor session) {
 		Dialect dialect = session.getFactory().getDialect();
 		String timestampSelectString = dialect.getCurrentTimestampSelectString();
 		if ( dialect.isCurrentTimestampSelectStringCallable() ) {
 			return useCallableStatement( timestampSelectString, session );
 		}
 		else {
 			return usePreparedStatement( timestampSelectString, session );
 		}
 	}
 
 	private Timestamp usePreparedStatement(String timestampSelectString, SessionImplementor session) {
 		PreparedStatement ps = null;
 		try {
-			ps = session.getBatcher().prepareStatement( timestampSelectString );
-			ResultSet rs = session.getBatcher().getResultSet( ps );
+			ps = session.getJDBCContext().getConnectionManager().prepareStatement( timestampSelectString, false );
+			ResultSet rs = ps.executeQuery();
 			rs.next();
 			Timestamp ts = rs.getTimestamp( 1 );
 			if ( log.isTraceEnabled() ) {
 				log.trace(
 				        "current timestamp retreived from db : " + ts +
 				        " (nanos=" + ts.getNanos() +
 				        ", time=" + ts.getTime() + ")"
 					);
 			}
 			return ts;
 		}
 		catch( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not select current db timestamp",
 			        timestampSelectString
 				);
 		}
 		finally {
 			if ( ps != null ) {
 				try {
-					session.getBatcher().closeStatement( ps );
+					ps.close();
 				}
 				catch( SQLException sqle ) {
 					log.warn( "unable to clean up prepared statement", sqle );
 				}
 			}
 		}
 	}
 
 	private Timestamp useCallableStatement(String callString, SessionImplementor session) {
 		CallableStatement cs = null;
 		try {
-			cs = session.getBatcher().prepareCallableStatement( callString );
+			cs = session.getJDBCContext().getConnectionManager().prepareCallableStatement( callString );
 			cs.registerOutParameter( 1, java.sql.Types.TIMESTAMP );
 			cs.execute();
 			Timestamp ts = cs.getTimestamp( 1 );
 			if ( log.isTraceEnabled() ) {
 				log.trace(
 				        "current timestamp retreived from db : " + ts +
 				        " (nanos=" + ts.getNanos() +
 				        ", time=" + ts.getTime() + ")"
 					);
 			}
 			return ts;
 		}
 		catch( SQLException sqle ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not call current db timestamp function",
 			        callString
 				);
 		}
 		finally {
 			if ( cs != null ) {
 				try {
-					session.getBatcher().closeStatement( cs );
+					cs.close();
 				}
 				catch( SQLException sqle ) {
 					log.warn( "unable to clean up callable statement", sqle );
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java b/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java
index 60965bd95b..1a207b62f7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/insertordering/InsertOrderingTest.java
@@ -1,127 +1,129 @@
 package org.hibernate.test.insertordering;
 
 import java.util.List;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.sql.SQLException;
 import java.sql.PreparedStatement;
 
 import junit.framework.Test;
 
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.testing.junit.functional.FunctionalTestCase;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.Session;
-import org.hibernate.Interceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.jdbc.BatchingBatcher;
-import org.hibernate.jdbc.ConnectionManager;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.jdbc.Batcher;
 
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
 		cfg.setProperty( Environment.BATCH_STRATEGY, StatsBatcherFactory.class.getName() );
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
 		StatsBatcher.reset();
 		s.getTransaction().commit();
 		s.close();
 
-		assertEquals( 6, StatsBatcher.batchSizes.size() );  // 2 batches of each insert statement
+		assertEquals( 3, StatsBatcher.batchSizes.size() );
 
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
 
 	public static class StatsBatcher extends BatchingBatcher {
 		private static String batchSQL;
 		private static List batchSizes = new ArrayList();
 		private static int currentBatch = -1;
 
-		public StatsBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-			super( connectionManager, interceptor );
+		public StatsBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
+			super( exceptionHelper, jdbcBatchSize );
 		}
 
 		static void reset() {
 			batchSizes = new ArrayList();
 			currentBatch = -1;
 			batchSQL = null;
 		}
 
-		public PreparedStatement prepareBatchStatement(String sql) throws SQLException {
-			PreparedStatement rtn = super.prepareBatchStatement( sql );
-			if ( batchSQL == null || !batchSQL.equals( sql ) ) {
+		public void setStatement(String sql, PreparedStatement ps) {
+			if ( batchSQL == null || ! batchSQL.equals( sql ) ) {
 				currentBatch++;
 				batchSQL = sql;
 				batchSizes.add( currentBatch, new Counter() );
 				System.out.println( "--------------------------------------------------------" );
 				System.out.println( "Preparing statement [" + sql + "]" );
 			}
-			return rtn;
+			super.setStatement( sql, ps );
 		}
 
 		public void addToBatch(Expectation expectation) throws SQLException, HibernateException {
 			Counter counter = ( Counter ) batchSizes.get( currentBatch );
 			counter.count++;
 			System.out.println( "Adding to batch [" + batchSQL + "]" );
 			super.addToBatch( expectation );
 		}
 
 		protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
 			System.out.println( "executing batch [" + batchSQL + "]" );
 			System.out.println( "--------------------------------------------------------" );
-			batchSQL = null;
 			super.doExecuteBatch( ps );
 		}
 	}
 
 	public static class StatsBatcherFactory implements BatcherFactory {
-		public Batcher createBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-			return new StatsBatcher( connectionManager, interceptor );
+		private int jdbcBatchSize;
+
+		public void setJdbcBatchSize(int jdbcBatchSize) {
+			this.jdbcBatchSize = jdbcBatchSize;
+		}
+		public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
+			return new StatsBatcher( exceptionHelper, jdbcBatchSize );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
index fb69008fe0..2a893a8fd2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
@@ -1,263 +1,282 @@
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
-import org.hibernate.stat.ConcurrentStatisticsImpl;
+import org.hibernate.jdbc.NonBatchingBatcherFactory;
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
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_STATEMENT,
+				services,
+				null,
+				new NonBatchingBatcherFactory()
+		);
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
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_STATEMENT,
+				services,
+				null,
+				new NonBatchingBatcherFactory()
+		);
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
+			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
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
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_STATEMENT,
+				services,
+				null,
+				new NonBatchingBatcherFactory()
+		);
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
index 016ea3f72d..7de8b8f8ef 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
@@ -1,133 +1,152 @@
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
+import org.hibernate.jdbc.NonBatchingBatcherFactory;
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
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_TRANSACTION, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_TRANSACTION,
+				services,
+				null,
+				new NonBatchingBatcherFactory()
+		);
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
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_TRANSACTION, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_TRANSACTION,
+				services,
+				null,
+				new NonBatchingBatcherFactory()
+		);
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
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_TRANSACTION, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_TRANSACTION,
+				services,
+				null,
+				new NonBatchingBatcherFactory()
+		);
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
index 6218b85d1d..8c71891ac3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/manytomany/batchload/BatchedManyToManyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/manytomany/batchload/BatchedManyToManyTest.java
@@ -1,176 +1,180 @@
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
 
+import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
 import org.hibernate.testing.junit.functional.FunctionalTestCase;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.Session;
 import org.hibernate.Hibernate;
 import org.hibernate.Interceptor;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.jdbc.BatcherFactory;
 import org.hibernate.jdbc.NonBatchingBatcher;
 import org.hibernate.jdbc.Batcher;
-import org.hibernate.jdbc.ConnectionManager;
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
 		cfg.setProperty( Environment.BATCH_STRATEGY, TestingBatcherFactory.class.getName() );
 	}
 
 	public static class TestingBatcherFactory implements BatcherFactory {
-		public Batcher createBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-			return new TestingBatcher( connectionManager, interceptor );
+		private int jdbcBatchSize;
+
+		public void setJdbcBatchSize(int jdbcBatchSize) {
+			this.jdbcBatchSize = jdbcBatchSize;
+		}
+		public Batcher createBatcher(SQLExceptionHelper exceptionHelper) {
+			return new TestingBatcher( exceptionHelper, jdbcBatchSize );
 		}
 	}
 
 	public static class TestingBatcher extends NonBatchingBatcher {
-		public TestingBatcher(ConnectionManager connectionManager, Interceptor interceptor) {
-			super( connectionManager, interceptor );
+		public TestingBatcher(SQLExceptionHelper exceptionHelper, int jdbcBatchSize) {
+			super( exceptionHelper );
 		}
-
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
index 727d1d9c1a..a55e3d85fb 100644
--- a/hibernate-core/src/test/resources/log4j.properties
+++ b/hibernate-core/src/test/resources/log4j.properties
@@ -1,16 +1,17 @@
 log4j.appender.stdout=org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target=System.out
 log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
 
 
 log4j.rootLogger=info, stdout
 
 log4j.logger.org.hibernate.test=info
 log4j.logger.org.hibernate.tool.hbm2ddl=debug
+log4j.logger.org.hibernate.engine.jdbc=trace
 log4j.logger.org.hibernate.hql.ast.QueryTranslatorImpl=trace
 log4j.logger.org.hibernate.hql.ast.HqlSqlWalker=trace
 log4j.logger.org.hibernate.hql.ast.SqlGenerator=trace
 log4j.logger.org.hibernate.hql.ast.AST=trace
 log4j.logger.org.hibernate.type.descriptor.sql.BasicBinder=trace
 log4j.logger.org.hibernate.type.BasicTypeRegistry=trace
\ No newline at end of file
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransaction.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransaction.java
index 0a1f304f35..d4531a0739 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransaction.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransaction.java
@@ -1,121 +1,121 @@
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
 package org.hibernate.ejb.transaction;
 
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 
 import org.hibernate.HibernateException;
 import org.hibernate.TransactionException;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.transaction.CMTTransaction;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.util.JTAHelper;
 
 /**
  * Implements a joinable transaction. Until the transaction is marked for joined, the TM.isTransactionInProgress()
  * must return false
  *
  * @author Emmanuel Bernard
  */
 public class JoinableCMTTransaction extends CMTTransaction {
 	private JoinStatus status;
 
 	public JoinableCMTTransaction(JDBCContext jdbcContext, TransactionFactory.Context transactionContext) {
 		super( jdbcContext, transactionContext );
 		//status = JoinStatus.MARKED_FOR_JOINED;
 		//tryJoiningTransaction();
 	}
 
 	public boolean isTransactionInProgress(
 			JDBCContext jdbcContext,
 			TransactionFactory.Context transactionContext) {
 		try {
 			return status == JoinStatus.JOINED && isTransactionInProgress(
 					transactionContext.getFactory().getTransactionManager().getTransaction()
 			);
 		}
 		catch (SystemException se) {
 			throw new TransactionException( "Unable to check transaction status", se );
 		}
 	}
 
 	private boolean isTransactionInProgress() {
 		try {
 			Transaction transaction = transactionContext.getFactory().getTransactionManager().getTransaction();
 			return isTransactionInProgress(transaction);
 		}
 		catch (SystemException se) {
 			throw new TransactionException( "Unable to check transaction status", se );
 		}
 	}
 
 	private boolean isTransactionInProgress(Transaction tx) throws SystemException {
 		return JTAHelper.isTransactionInProgress(tx) && ! JTAHelper.isRollback( tx.getStatus() );
 	}
 
 	void tryJoiningTransaction() {
 		if ( status == JoinStatus.MARKED_FOR_JOINED ) {
 			if ( isTransactionInProgress() ) {
 				status = JoinStatus.JOINED;
 			}
 			else {
 				status = JoinStatus.NOT_JOINED;
 			}
 		}
 	}
 
 	@Override
 	public void begin() throws HibernateException {
 		super.begin();
 		status = JoinStatus.JOINED;
 	}
 
 	@Override
 	public void commit() throws HibernateException {
 		/* this method is not supposed to be called
 		 * it breaks the flushBeforeCompletion flag optimizeation
 		 * regarding flushing skip.
 		 * In its current form, it will generate too much flush() calls
 		 */
 		super.commit();
 	}
 
 
 	public JoinStatus getStatus() {
 		return status;
 	}
 
 	public void resetStatus() {
 		status = JoinStatus.NOT_JOINED;
 	}
 
 	public void markForJoined() {
 		if ( status != JoinStatus.JOINED ) status = JoinStatus.MARKED_FOR_JOINED;
 	}
 
 	public static enum JoinStatus {
 		NOT_JOINED,
 		MARKED_FOR_JOINED,
 		JOINED
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransactionFactory.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransactionFactory.java
index f449acd519..b4f3239342 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransactionFactory.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/transaction/JoinableCMTTransactionFactory.java
@@ -1,54 +1,54 @@
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
 package org.hibernate.ejb.transaction;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Transaction;
-import org.hibernate.jdbc.JDBCContext;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.transaction.CMTTransactionFactory;
 
 /**
  * A transaction is in progress if the underlying JTA tx is in progress and if the Tx is marked as
  * MARKED_FOR_JOINED
  *
  * @author Emmanuel Bernard
  */
 public class JoinableCMTTransactionFactory extends CMTTransactionFactory {
 	public Transaction createTransaction(
 			JDBCContext jdbcContext,
 			Context transactionContext) throws HibernateException {
 		return new JoinableCMTTransaction( jdbcContext, transactionContext );
 	}
 
 	@Override
 	public boolean isTransactionInProgress(
 			JDBCContext jdbcContext,
 			Context transactionContext,
 			Transaction transaction) {
 		if ( transaction == null ) {
 			return false; //should not happen though
 		}
 		JoinableCMTTransaction joinableCMTTransaction = ( (JoinableCMTTransaction) transaction );
 		joinableCMTTransaction.tryJoiningTransaction();
 		return joinableCMTTransaction.isTransactionInProgress( jdbcContext, transactionContext );
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
index cdba266d91..5de4173e21 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
@@ -1,295 +1,290 @@
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
  */
 package org.hibernate.envers.entities.mapper.relation.lazy;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Transaction;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.NonFlushedChanges;
+import org.hibernate.engine.jdbc.spi.JDBCContext;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.event.EventListeners;
 import org.hibernate.impl.CriteriaImpl;
-import org.hibernate.jdbc.Batcher;
-import org.hibernate.jdbc.JDBCContext;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public abstract class AbstractDelegateSessionImplementor implements SessionImplementor {
     protected SessionImplementor delegate;
 
     public AbstractDelegateSessionImplementor(SessionImplementor delegate) {
         this.delegate = delegate;
     }
 
     public abstract Object doImmediateLoad(String entityName);
 
     public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
         return doImmediateLoad(entityName);
     }
 
     // Delegate methods
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return delegate.getLoadQueryInfluencers();
 	}
 
 	public Interceptor getInterceptor() {
         return delegate.getInterceptor();
     }
 
     public void setAutoClear(boolean enabled) {
         delegate.setAutoClear(enabled);
     }
 
     public boolean isTransactionInProgress() {
         return delegate.isTransactionInProgress();
     }
 
     public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
         delegate.initializeCollection(collection, writing);
     }
 
     public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
         return delegate.internalLoad(entityName, id, eager, nullable);
     }
 
     public long getTimestamp() {
         return delegate.getTimestamp();
     }
 
     public SessionFactoryImplementor getFactory() {
         return delegate.getFactory();
     }
 
-    public Batcher getBatcher() {
-        return delegate.getBatcher();
-    }
-
     public List list(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.list(query, queryParameters);
     }
 
     public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.iterate(query, queryParameters);
     }
 
     public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.scroll(query, queryParameters);
     }
 
     public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
         return delegate.scroll(criteria, scrollMode);
     }
 
     public List list(CriteriaImpl criteria) {
         return delegate.list(criteria);
     }
 
     public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
         return delegate.listFilter(collection, filter, queryParameters);
     }
 
     public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
         return delegate.iterateFilter(collection, filter, queryParameters);
     }
 
     public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
         return delegate.getEntityPersister(entityName, object);
     }
 
     public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
         return delegate.getEntityUsingInterceptor(key);
     }
 
     public void afterTransactionCompletion(boolean successful, Transaction tx) {
         delegate.afterTransactionCompletion(successful, tx);
     }
 
     public void beforeTransactionCompletion(Transaction tx) {
         delegate.beforeTransactionCompletion(tx);
     }
 
     public Serializable getContextEntityIdentifier(Object object) {
         return delegate.getContextEntityIdentifier(object);
     }
 
     public String bestGuessEntityName(Object object) {
         return delegate.bestGuessEntityName(object);
     }
 
     public String guessEntityName(Object entity) throws HibernateException {
         return delegate.guessEntityName(entity);
     }
 
     public Object instantiate(String entityName, Serializable id) throws HibernateException {
         return delegate.instantiate(entityName, id);
     }
 
     public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
         return delegate.listCustomQuery(customQuery, queryParameters);
     }
 
     public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
         return delegate.scrollCustomQuery(customQuery, queryParameters);
     }
 
     public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
         return delegate.list(spec, queryParameters);
     }
 
     public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
         return delegate.scroll(spec, queryParameters);
     }
 
     public Object getFilterParameterValue(String filterParameterName) {
         return delegate.getFilterParameterValue(filterParameterName);
     }
 
     public Type getFilterParameterType(String filterParameterName) {
         return delegate.getFilterParameterType(filterParameterName);
     }
 
     public Map getEnabledFilters() {
         return delegate.getEnabledFilters();
     }
 
     public int getDontFlushFromFind() {
         return delegate.getDontFlushFromFind();
     }
 
     public EventListeners getListeners() {
         return delegate.getListeners();
     }
 
     public PersistenceContext getPersistenceContext() {
         return delegate.getPersistenceContext();
     }
 
     public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.executeUpdate(query, queryParameters);
     }
 
     public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
         return delegate.executeNativeUpdate(specification, queryParameters);
     }
 
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		return delegate.getNonFlushedChanges();
 	}
 
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
 		delegate.applyNonFlushedChanges( nonFlushedChanges );		
 	}
 
     public EntityMode getEntityMode() {
         return delegate.getEntityMode();
     }
 
     public CacheMode getCacheMode() {
         return delegate.getCacheMode();
     }
 
     public void setCacheMode(CacheMode cm) {
         delegate.setCacheMode(cm);
     }
 
     public boolean isOpen() {
         return delegate.isOpen();
     }
 
     public boolean isConnected() {
         return delegate.isConnected();
     }
 
     public FlushMode getFlushMode() {
         return delegate.getFlushMode();
     }
 
     public void setFlushMode(FlushMode fm) {
         delegate.setFlushMode(fm);
     }
 
     public Connection connection() {
         return delegate.connection();
     }
 
     public void flush() {
         delegate.flush();
     }
 
     public Query getNamedQuery(String name) {
         return delegate.getNamedQuery(name);
     }
 
     public Query getNamedSQLQuery(String name) {
         return delegate.getNamedSQLQuery(name);
     }
 
     public boolean isEventSource() {
         return delegate.isEventSource();
     }
 
     public void afterScrollOperation() {
         delegate.afterScrollOperation();
     }
 
     public void setFetchProfile(String name) {
         delegate.setFetchProfile(name);
     }
 
     public String getFetchProfile() {
         return delegate.getFetchProfile();
     }
 
     public JDBCContext getJDBCContext() {
         return delegate.getJDBCContext();
     }
 
     public boolean isClosed() {
         return delegate.isClosed();
     }
 }
