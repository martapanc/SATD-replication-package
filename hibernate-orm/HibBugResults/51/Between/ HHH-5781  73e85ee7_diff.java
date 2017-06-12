diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index fbc5e31eac..01ec78b7dc 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,481 +1,453 @@
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
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.engine.jdbc.JdbcSupport;
 import org.hibernate.hql.QueryTranslatorFactory;
-import org.hibernate.jdbc.util.SQLStatementLogger;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 import java.util.Map;
 
 /**
  * Settings that affect the behaviour of Hibernate at runtime.
  *
  * @author Gavin King
  */
 public final class Settings {
 
-//	private boolean showSql;
-//	private boolean formatSql;
-	private SQLStatementLogger sqlStatementLogger;
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
 
 	private JtaPlatform jtaPlatform;
 
 	/**
 	 * Package protected constructor
 	 */
 	Settings() {
 	}
 
 	// public getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-//	public boolean isShowSqlEnabled() {
-//		return showSql;
-//	}
-//
-//	public boolean isFormatSqlEnabled() {
-//		return formatSql;
-//	}
-
 	public String getImportFiles() {
 		return importFiles;
 	}
 
 	public void setImportFiles(String importFiles) {
 		this.importFiles = importFiles;
 	}
 
-	public SQLStatementLogger getSqlStatementLogger() {
-		return sqlStatementLogger;
-	}
-
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
 
-//	void setShowSqlEnabled(boolean b) {
-//		showSql = b;
-//	}
-//
-//	void setFormatSqlEnabled(boolean b) {
-//		formatSql = b;
-//	}
-
-	void setSqlStatementLogger(SQLStatementLogger sqlStatementLogger) {
-		this.sqlStatementLogger = sqlStatementLogger;
-	}
-
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
 
 
 	public JtaPlatform getJtaPlatform() {
 		return jtaPlatform;
 	}
 
 	void setJtaPlatform(JtaPlatform jtaPlatform) {
 		this.jtaPlatform = jtaPlatform;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 73801f2b98..b6944c84d0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,346 +1,336 @@
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
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.jdbc.util.SQLStatementLogger;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistry;
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
 
 		settings.setJdbcSupport( new JdbcSupport( ! ConfigurationHelper.getBoolean( Environment.NON_CONTEXTUAL_LOB_CREATION, properties ) ) );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
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
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
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
 
-		boolean showSql = ConfigurationHelper.getBoolean(Environment.SHOW_SQL, properties);
-		if (showSql) log.info("Echoing all SQL to stdout");
-//		settings.setShowSqlEnabled(showSql);
-
-		boolean formatSql = ConfigurationHelper.getBoolean(Environment.FORMAT_SQL, properties);
-//		settings.setFormatSqlEnabled(formatSql);
-
-		settings.setSqlStatementLogger( new SQLStatementLogger( showSql, formatSql ) );
-
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
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
index a72b2aee50..8c876a7041 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
@@ -1,260 +1,258 @@
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
 
-import javax.transaction.TransactionManager;
-
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
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
 import org.hibernate.service.spi.ServiceRegistry;
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
-	 * Retrieves the SQLExceptionHelper in effect for this SessionFactory.
+	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
-	 * @return The SQLExceptionHelper for this SessionFactory.
+	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 */
-	public SQLExceptionHelper getSQLExceptionHelper();
+	public SqlExceptionHelper getSQLExceptionHelper();
 
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
 
 	public ServiceRegistry getServiceRegistry();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
index a0c4bd2b97..41560f003b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
@@ -1,206 +1,206 @@
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
 
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.batch.spi.BatchObserver;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 
 /**
  * Convenience base class for implementors of the Batch interface.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractBatchImpl implements Batch {
 	private static final Logger log = LoggerFactory.getLogger( AbstractBatchImpl.class );
 
 	private final BatchKey key;
 	private final JdbcCoordinator jdbcCoordinator;
 	private LinkedHashMap<String,PreparedStatement> statements = new LinkedHashMap<String,PreparedStatement>();
 	private LinkedHashSet<BatchObserver> observers = new LinkedHashSet<BatchObserver>();
 
 	protected AbstractBatchImpl(BatchKey key, JdbcCoordinator jdbcCoordinator) {
 		if ( key == null ) {
 			throw new IllegalArgumentException( "batch key cannot be null" );
 		}
 		if ( jdbcCoordinator == null ) {
 			throw new IllegalArgumentException( "JDBC coordinator cannot be null" );
 		}
 		this.key = key;
 		this.jdbcCoordinator = jdbcCoordinator;
 	}
 
 	/**
 	 * Perform batch execution.
 	 * <p/>
 	 * This is called from the explicit {@link #execute() execution}, but may also be called from elsewhere
 	 * depending on the exact implementation.
 	 */
 	protected abstract void doExecuteBatch();
 
 	/**
 	 * Convenience access to the SQLException helper.
 	 *
 	 * @return The underlying SQLException helper.
 	 */
-	protected SQLExceptionHelper sqlExceptionHelper() {
+	protected SqlExceptionHelper sqlExceptionHelper() {
 		return jdbcCoordinator.getTransactionCoordinator()
 				.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getSqlExceptionHelper();
 	}
 
 	/**
 	 * Convenience access to the SQL statement logger.
 	 *
 	 * @return The underlying JDBC services.
 	 */
-	protected SQLStatementLogger sqlStatementLogger() {
+	protected SqlStatementLogger sqlStatementLogger() {
 		return jdbcCoordinator.getTransactionCoordinator()
 				.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getSqlStatementLogger();
 	}
 
 	/**
 	 * Access to the batch's map of statements (keyed by SQL statement string).
 	 *
 	 * @return This batch's statements.
 	 */
 	protected LinkedHashMap<String,PreparedStatement> getStatements() {
 		return statements;
 	}
 
 	@Override
 	public final BatchKey getKey() {
 		return key;
 	}
 
 	@Override
 	public void addObserver(BatchObserver observer) {
 		observers.add( observer );
 	}
 
 	@Override
 	public PreparedStatement getBatchStatement(String sql, boolean callable) {
 		if ( sql == null ) {
 			throw new IllegalArgumentException( "sql must be non-null." );
 		}
 		PreparedStatement statement = statements.get( sql );
 		if ( statement == null ) {
 			statement = buildBatchStatement( sql, callable );
 			statements.put( sql, statement );
 		}
 		else {
 			log.debug( "reusing batch statement" );
 			sqlStatementLogger().logStatement( sql );
 		}
 		return statement;
 	}
 
 	private PreparedStatement buildBatchStatement(String sql, boolean callable) {
 		try {
 			if ( callable ) {
 				return jdbcCoordinator.getLogicalConnection().getShareableConnectionProxy().prepareCall( sql );
 			}
 			else {
 				return jdbcCoordinator.getLogicalConnection().getShareableConnectionProxy().prepareStatement( sql );
 			}
 		}
 		catch ( SQLException sqle ) {
 			log.error( "sqlexception escaped proxy", sqle );
 			throw sqlExceptionHelper().convert( sqle, "could not prepare batch statement", sql );
 		}
 	}
 
 	@Override
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
 				releaseStatements();
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
 				log.error( "unable to release batch statement; sqlexception escaped proxy", e );
 			}
 		}
 		getStatements().clear();
 	}
 
 	/**
 	 * Convenience method to notify registered observers of an explicit execution of this batch.
 	 */
 	protected final void notifyObserversExplicitExecution() {
 		for ( BatchObserver observer : observers ) {
 			observer.batchExplicitlyExecuted();
 		}
 	}
 
 	/**
 	 * Convenience method to notify registered observers of an implicit execution of this batch.
 	 */
 	protected final void notifyObserversImplicitExecution() {
 		for ( BatchObserver observer : observers ) {
 			observer.batchImplicitlyExecuted();
 		}
 	}
 
 	@Override
 	public void release() {
 		if ( getStatements() != null && !getStatements().isEmpty() ) {
 			log.info( "On release of batch it still contained JDBC statements" );
 		}
 		releaseStatements();
 		observers.clear();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/util/BasicFormatterImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/BasicFormatterImpl.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/jdbc/util/BasicFormatterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/BasicFormatterImpl.java
index 68f6a8d89f..8f85174cfd 100755
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/util/BasicFormatterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/BasicFormatterImpl.java
@@ -1,397 +1,399 @@
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
-package org.hibernate.jdbc.util;
+package org.hibernate.engine.jdbc.internal;
 
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.util.StringHelper;
 
 /**
  * Performs formatting of basic SQL statements (DML + query).
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class BasicFormatterImpl implements Formatter {
 
-	private static final Set BEGIN_CLAUSES = new HashSet();
-	private static final Set END_CLAUSES = new HashSet();
-	private static final Set LOGICAL = new HashSet();
-	private static final Set QUANTIFIERS = new HashSet();
-	private static final Set DML = new HashSet();
-	private static final Set MISC = new HashSet();
+	private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
+	private static final Set<String> END_CLAUSES = new HashSet<String>();
+	private static final Set<String> LOGICAL = new HashSet<String>();
+	private static final Set<String> QUANTIFIERS = new HashSet<String>();
+	private static final Set<String> DML = new HashSet<String>();
+	private static final Set<String> MISC = new HashSet<String>();
 
 	static {
 		BEGIN_CLAUSES.add( "left" );
 		BEGIN_CLAUSES.add( "right" );
 		BEGIN_CLAUSES.add( "inner" );
 		BEGIN_CLAUSES.add( "outer" );
 		BEGIN_CLAUSES.add( "group" );
 		BEGIN_CLAUSES.add( "order" );
 
 		END_CLAUSES.add( "where" );
 		END_CLAUSES.add( "set" );
 		END_CLAUSES.add( "having" );
 		END_CLAUSES.add( "join" );
 		END_CLAUSES.add( "from" );
 		END_CLAUSES.add( "by" );
 		END_CLAUSES.add( "join" );
 		END_CLAUSES.add( "into" );
 		END_CLAUSES.add( "union" );
 
 		LOGICAL.add( "and" );
 		LOGICAL.add( "or" );
 		LOGICAL.add( "when" );
 		LOGICAL.add( "else" );
 		LOGICAL.add( "end" );
 
 		QUANTIFIERS.add( "in" );
 		QUANTIFIERS.add( "all" );
 		QUANTIFIERS.add( "exists" );
 		QUANTIFIERS.add( "some" );
 		QUANTIFIERS.add( "any" );
 
 		DML.add( "insert" );
 		DML.add( "update" );
 		DML.add( "delete" );
 
 		MISC.add( "select" );
 		MISC.add( "on" );
 	}
 
 	static final String indentString = "    ";
 	static final String initial = "\n    ";
 
 	public String format(String source) {
 		return new FormatProcess( source ).perform();
 	}
 
 	private static class FormatProcess {
 		boolean beginLine = true;
 		boolean afterBeginBeforeEnd = false;
 		boolean afterByOrSetOrFromOrSelect = false;
 		boolean afterValues = false;
 		boolean afterOn = false;
 		boolean afterBetween = false;
 		boolean afterInsert = false;
 		int inFunction = 0;
 		int parensSinceSelect = 0;
-		private LinkedList parenCounts = new LinkedList();
-		private LinkedList afterByOrFromOrSelects = new LinkedList();
+		private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
+		private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();
 
 		int indent = 1;
 
 		StringBuffer result = new StringBuffer();
 		StringTokenizer tokens;
 		String lastToken;
 		String token;
 		String lcToken;
 
 		public FormatProcess(String sql) {
 			tokens = new StringTokenizer(
 					sql,
 					"()+*/-=<>'`\"[]," + StringHelper.WHITESPACE,
 					true
 			);
 		}
 
 		public String perform() {
 
 			result.append( initial );
 
 			while ( tokens.hasMoreTokens() ) {
 				token = tokens.nextToken();
 				lcToken = token.toLowerCase();
 
 				if ( "'".equals( token ) ) {
 					String t;
 					do {
 						t = tokens.nextToken();
 						token += t;
 					}
 					while ( !"'".equals( t ) && tokens.hasMoreTokens() ); // cannot handle single quotes
 				}
 				else if ( "\"".equals( token ) ) {
 					String t;
 					do {
 						t = tokens.nextToken();
 						token += t;
 					}
 					while ( !"\"".equals( t ) );
 				}
 
 				if ( afterByOrSetOrFromOrSelect && ",".equals( token ) ) {
 					commaAfterByOrFromOrSelect();
 				}
 				else if ( afterOn && ",".equals( token ) ) {
 					commaAfterOn();
 				}
 
 				else if ( "(".equals( token ) ) {
 					openParen();
 				}
 				else if ( ")".equals( token ) ) {
 					closeParen();
 				}
 
 				else if ( BEGIN_CLAUSES.contains( lcToken ) ) {
 					beginNewClause();
 				}
 
 				else if ( END_CLAUSES.contains( lcToken ) ) {
 					endNewClause();
 				}
 
 				else if ( "select".equals( lcToken ) ) {
 					select();
 				}
 
 				else if ( DML.contains( lcToken ) ) {
 					updateOrInsertOrDelete();
 				}
 
 				else if ( "values".equals( lcToken ) ) {
 					values();
 				}
 
 				else if ( "on".equals( lcToken ) ) {
 					on();
 				}
 
 				else if ( afterBetween && lcToken.equals( "and" ) ) {
 					misc();
 					afterBetween = false;
 				}
 
 				else if ( LOGICAL.contains( lcToken ) ) {
 					logical();
 				}
 
 				else if ( isWhitespace( token ) ) {
 					white();
 				}
 
 				else {
 					misc();
 				}
 
 				if ( !isWhitespace( token ) ) {
 					lastToken = lcToken;
 				}
 
 			}
 			return result.toString();
 		}
 
 		private void commaAfterOn() {
 			out();
 			indent--;
 			newline();
 			afterOn = false;
 			afterByOrSetOrFromOrSelect = true;
 		}
 
 		private void commaAfterByOrFromOrSelect() {
 			out();
 			newline();
 		}
 
 		private void logical() {
 			if ( "end".equals( lcToken ) ) {
 				indent--;
 			}
 			newline();
 			out();
 			beginLine = false;
 		}
 
 		private void on() {
 			indent++;
 			afterOn = true;
 			newline();
 			out();
 			beginLine = false;
 		}
 
 		private void misc() {
 			out();
 			if ( "between".equals( lcToken ) ) {
 				afterBetween = true;
 			}
 			if ( afterInsert ) {
 				newline();
 				afterInsert = false;
 			}
 			else {
 				beginLine = false;
 				if ( "case".equals( lcToken ) ) {
 					indent++;
 				}
 			}
 		}
 
 		private void white() {
 			if ( !beginLine ) {
 				result.append( " " );
 			}
 		}
 
 		private void updateOrInsertOrDelete() {
 			out();
 			indent++;
 			beginLine = false;
 			if ( "update".equals( lcToken ) ) {
 				newline();
 			}
 			if ( "insert".equals( lcToken ) ) {
 				afterInsert = true;
 			}
 		}
 
+		@SuppressWarnings( {"UnnecessaryBoxing"})
 		private void select() {
 			out();
 			indent++;
 			newline();
-			parenCounts.addLast( new Integer( parensSinceSelect ) );
+			parenCounts.addLast( Integer.valueOf( parensSinceSelect ) );
 			afterByOrFromOrSelects.addLast( Boolean.valueOf( afterByOrSetOrFromOrSelect ) );
 			parensSinceSelect = 0;
 			afterByOrSetOrFromOrSelect = true;
 		}
 
 		private void out() {
 			result.append( token );
 		}
 
 		private void endNewClause() {
 			if ( !afterBeginBeforeEnd ) {
 				indent--;
 				if ( afterOn ) {
 					indent--;
 					afterOn = false;
 				}
 				newline();
 			}
 			out();
 			if ( !"union".equals( lcToken ) ) {
 				indent++;
 			}
 			newline();
 			afterBeginBeforeEnd = false;
 			afterByOrSetOrFromOrSelect = "by".equals( lcToken )
 					|| "set".equals( lcToken )
 					|| "from".equals( lcToken );
 		}
 
 		private void beginNewClause() {
 			if ( !afterBeginBeforeEnd ) {
 				if ( afterOn ) {
 					indent--;
 					afterOn = false;
 				}
 				indent--;
 				newline();
 			}
 			out();
 			beginLine = false;
 			afterBeginBeforeEnd = true;
 		}
 
 		private void values() {
 			indent--;
 			newline();
 			out();
 			indent++;
 			newline();
 			afterValues = true;
 		}
 
+		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		private void closeParen() {
 			parensSinceSelect--;
 			if ( parensSinceSelect < 0 ) {
 				indent--;
-				parensSinceSelect = ( ( Integer ) parenCounts.removeLast() ).intValue();
-				afterByOrSetOrFromOrSelect = ( ( Boolean ) afterByOrFromOrSelects.removeLast() ).booleanValue();
+				parensSinceSelect = parenCounts.removeLast().intValue();
+				afterByOrSetOrFromOrSelect = afterByOrFromOrSelects.removeLast().booleanValue();
 			}
 			if ( inFunction > 0 ) {
 				inFunction--;
 				out();
 			}
 			else {
 				if ( !afterByOrSetOrFromOrSelect ) {
 					indent--;
 					newline();
 				}
 				out();
 			}
 			beginLine = false;
 		}
 
 		private void openParen() {
 			if ( isFunctionName( lastToken ) || inFunction > 0 ) {
 				inFunction++;
 			}
 			beginLine = false;
 			if ( inFunction > 0 ) {
 				out();
 			}
 			else {
 				out();
 				if ( !afterByOrSetOrFromOrSelect ) {
 					indent++;
 					newline();
 					beginLine = true;
 				}
 			}
 			parensSinceSelect++;
 		}
 
 		private static boolean isFunctionName(String tok) {
 			final char begin = tok.charAt( 0 );
 			final boolean isIdentifier = Character.isJavaIdentifierStart( begin ) || '"' == begin;
 			return isIdentifier &&
 					!LOGICAL.contains( tok ) &&
 					!END_CLAUSES.contains( tok ) &&
 					!QUANTIFIERS.contains( tok ) &&
 					!DML.contains( tok ) &&
 					!MISC.contains( tok );
 		}
 
 		private static boolean isWhitespace(String token) {
 			return StringHelper.WHITESPACE.indexOf( token ) >= 0;
 		}
 
 		private void newline() {
 			result.append( "\n" );
 			for ( int i = 0; i < indent; i++ ) {
 				result.append( indentString );
 			}
 			beginLine = true;
 		}
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/util/DDLFormatterImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/DDLFormatterImpl.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/jdbc/util/DDLFormatterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/DDLFormatterImpl.java
index e3a06dbaf3..52ba4fc71a 100755
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/util/DDLFormatterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/DDLFormatterImpl.java
@@ -1,157 +1,156 @@
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
-package org.hibernate.jdbc.util;
+package org.hibernate.engine.jdbc.internal;
 
 import java.util.StringTokenizer;
 
 /**
  * Performs formatting of DDL SQL statements.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class DDLFormatterImpl implements Formatter {
 	/**
 	 * Format an SQL statement using simple rules<ul>
 	 * <li>Insert newline after each comma</li>
 	 * <li>Indent three spaces after each inserted newline</li>
 	 * </ul>
 	 * If the statement contains single/double quotes return unchanged,
 	 * it is too complex and could be broken by simple formatting.
 	 * 
 	 * @param sql The statement to be fornmatted.
 	 */
 	public String format(String sql) {
 		if ( sql.toLowerCase().startsWith( "create table" ) ) {
 			return formatCreateTable( sql );
 		}
 		else if ( sql.toLowerCase().startsWith( "alter table" ) ) {
 			return formatAlterTable( sql );
 		}
 		else if ( sql.toLowerCase().startsWith( "comment on" ) ) {
 			return formatCommentOn( sql );
 		}
 		else {
 			return "\n    " + sql;
 		}
 	}
 
 	private String formatCommentOn(String sql) {
 		StringBuffer result = new StringBuffer( 60 ).append( "\n    " );
 		StringTokenizer tokens = new StringTokenizer( sql, " '[]\"", true );
 
 		boolean quoted = false;
 		while ( tokens.hasMoreTokens() ) {
 			String token = tokens.nextToken();
 			result.append( token );
 			if ( isQuote( token ) ) {
 				quoted = !quoted;
 			}
 			else if ( !quoted ) {
 				if ( "is".equals( token ) ) {
 					result.append( "\n       " );
 				}
 			}
 		}
 
 		return result.toString();
 	}
 
 	private String formatAlterTable(String sql) {
 		StringBuffer result = new StringBuffer( 60 ).append( "\n    " );
 		StringTokenizer tokens = new StringTokenizer( sql, " (,)'[]\"", true );
 
 		boolean quoted = false;
 		while ( tokens.hasMoreTokens() ) {
 			String token = tokens.nextToken();
 			if ( isQuote( token ) ) {
 				quoted = !quoted;
 			}
 			else if ( !quoted ) {
 				if ( isBreak( token ) ) {
 					result.append( "\n        " );
 				}
 			}
 			result.append( token );
 		}
 
 		return result.toString();
 	}
 
 	private String formatCreateTable(String sql) {
 		StringBuffer result = new StringBuffer( 60 ).append( "\n    " );
 		StringTokenizer tokens = new StringTokenizer( sql, "(,)'[]\"", true );
 
 		int depth = 0;
 		boolean quoted = false;
 		while ( tokens.hasMoreTokens() ) {
 			String token = tokens.nextToken();
 			if ( isQuote( token ) ) {
 				quoted = !quoted;
 				result.append( token );
 			}
 			else if ( quoted ) {
 				result.append( token );
 			}
 			else {
 				if ( ")".equals( token ) ) {
 					depth--;
 					if ( depth == 0 ) {
 						result.append( "\n    " );
 					}
 				}
 				result.append( token );
 				if ( ",".equals( token ) && depth == 1 ) {
 					result.append( "\n       " );
 				}
 				if ( "(".equals( token ) ) {
 					depth++;
 					if ( depth == 1 ) {
 						result.append( "\n        " );
 					}
 				}
 			}
 		}
 
 		return result.toString();
 	}
 
 	private static boolean isBreak(String token) {
 		return "drop".equals( token ) ||
 				"add".equals( token ) ||
 				"references".equals( token ) ||
 				"foreign".equals( token ) ||
 				"on".equals( token );
 	}
 
 	private static boolean isQuote(String tok) {
 		return "\"".equals( tok ) ||
 				"`".equals( tok ) ||
 				"]".equals( tok ) ||
 				"[".equals( tok ) ||
 				"'".equals( tok );
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/util/FormatStyle.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/FormatStyle.java
similarity index 65%
rename from hibernate-core/src/main/java/org/hibernate/jdbc/util/FormatStyle.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/FormatStyle.java
index 00da9d4e50..260d4c601a 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/util/FormatStyle.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/FormatStyle.java
@@ -1,76 +1,57 @@
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
-package org.hibernate.jdbc.util;
+package org.hibernate.engine.jdbc.internal;
 
 /**
  * Represents the the understood types or styles of formatting. 
  *
  * @author Steve Ebersole
  */
-public class FormatStyle {
-	public static final FormatStyle BASIC = new FormatStyle( "basic", new BasicFormatterImpl() );
-	public static final FormatStyle DDL = new FormatStyle( "ddl", new DDLFormatterImpl() );
-	public static final FormatStyle NONE = new FormatStyle( "none", new NoFormatImpl() );
+public enum FormatStyle {
+	BASIC( "basic", new BasicFormatterImpl() ),
+	DDL( "ddl", new DDLFormatterImpl() ),
+	NONE( "none", new NoFormatImpl() );
 
 	private final String name;
 	private final Formatter formatter;
 
 	private FormatStyle(String name, Formatter formatter) {
 		this.name = name;
 		this.formatter = formatter;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Formatter getFormatter() {
 		return formatter;
 	}
 
-	public boolean equals(Object o) {
-		if ( this == o ) {
-			return true;
-		}
-		if ( o == null || getClass() != o.getClass() ) {
-			return false;
-		}
-
-		FormatStyle that = ( FormatStyle ) o;
-
-		return name.equals( that.name );
-
-	}
-
-	public int hashCode() {
-		return name.hashCode();
-	}
-
 	private static class NoFormatImpl implements Formatter {
 		public String format(String source) {
 			return source;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/util/Formatter.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/Formatter.java
similarity index 78%
rename from hibernate-core/src/main/java/org/hibernate/jdbc/util/Formatter.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/Formatter.java
index 2e43de74c7..3d9248c876 100644
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/util/Formatter.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/Formatter.java
@@ -1,34 +1,40 @@
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
-package org.hibernate.jdbc.util;
+package org.hibernate.engine.jdbc.internal;
 
 /**
  * Formatter contract
  *
  * @author Steve Ebersole
  */
 public interface Formatter {
+	/**
+	 * Format the source SQL string.
+	 *
+	 * @param source The original SQL string
+	 *
+	 * @return The formatted version
+	 */
 	public String format(String source);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
index dbd92ad62b..3a217786e7 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
@@ -1,260 +1,260 @@
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
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.StatementPreparer;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.jdbc.ReturningWork;
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
 
-	private SQLExceptionHelper sqlExceptionHelper() {
+	private SqlExceptionHelper sqlExceptionHelper() {
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
 
 	@Override
 	public <T> T coordinateWork(ReturningWork<T> work) {
 		Connection connection = getLogicalConnection().getDistinctConnectionProxy();
 		try {
 			T result = work.execute( connection );
 			getLogicalConnection().afterStatementExecution();
 			return result;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
index bcace088a0..69a5edf3ec 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcResourceRegistryImpl.java
@@ -1,305 +1,305 @@
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
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.spi.JdbcWrapper;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.InvalidatableWrapper;
 
 /**
  * Standard implementation of the {@link org.hibernate.engine.jdbc.spi.JdbcResourceRegistry} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcResourceRegistryImpl implements JdbcResourceRegistry {
 	private static final Logger log = LoggerFactory.getLogger( JdbcResourceRegistryImpl.class );
 
 	private final HashMap<Statement,Set<ResultSet>> xref = new HashMap<Statement,Set<ResultSet>>();
 	private final Set<ResultSet> unassociatedResultSets = new HashSet<ResultSet>();
-	private final SQLExceptionHelper exceptionHelper;
+	private final SqlExceptionHelper exceptionHelper;
 
 	private Statement lastQuery;
 
-	public JdbcResourceRegistryImpl(SQLExceptionHelper exceptionHelper) {
+	public JdbcResourceRegistryImpl(SqlExceptionHelper exceptionHelper) {
 		this.exceptionHelper = exceptionHelper;
 	}
 
 	public void register(Statement statement) {
 		log.trace( "registering statement [" + statement + "]" );
 		if ( xref.containsKey( statement ) ) {
 			throw new HibernateException( "statement already registered with JDBCContainer" );
 		}
 		xref.put( statement, null );
 	}
 
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
index e5585eab4d..a32cd6560d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
@@ -1,339 +1,337 @@
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
 
 import java.lang.reflect.InvocationTargetException;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SchemaNameResolver;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.internal.util.jdbc.TypeInfo;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.InjectService;
 import org.hibernate.util.ReflectHelper;
-import org.hibernate.internal.util.jdbc.TypeInfoExtracter;
 
 /**
  * Standard implementation of the {@link JdbcServices} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesImpl implements JdbcServices, Configurable {
 	private static final Logger log = LoggerFactory.getLogger( JdbcServicesImpl.class );
 
 	private ConnectionProvider connectionProvider;
 
 	@InjectService
 	public void setConnectionProvider(ConnectionProvider connectionProvider) {
 		this.connectionProvider = connectionProvider;
 	}
 
 	private DialectFactory dialectFactory;
 
 	@InjectService
 	public void setDialectFactory(DialectFactory dialectFactory) {
 		this.dialectFactory = dialectFactory;
 	}
 
 	private Dialect dialect;
-	private SQLStatementLogger sqlStatementLogger;
-	private SQLExceptionHelper sqlExceptionHelper;
+	private SqlStatementLogger sqlStatementLogger;
+	private SqlExceptionHelper sqlExceptionHelper;
 	private ExtractedDatabaseMetaData extractedMetaDataSupport;
 
 	public void configure(Map configValues) {
 		Dialect dialect = null;
 
 		boolean metaSupportsScrollable = false;
 		boolean metaSupportsGetGeneratedKeys = false;
 		boolean metaSupportsBatchUpdates = false;
 		boolean metaReportsDDLCausesTxnCommit = false;
 		boolean metaReportsDDLInTxnSupported = true;
 		String extraKeywordsString = "";
 		int sqlStateType = -1;
 		boolean lobLocatorUpdateCopy = false;
 		String catalogName = null;
 		String schemaName = null;
 		LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
 
 		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
 		// The need for it is intended to be alleviated with future development, thus it is
 		// not defined as an Environment constant...
 		//
 		// it is used to control whether we should consult the JDBC metadata to determine
 		// certain Settings default values; it is useful to *not* do this when the database
 		// may not be available (mainly in tools usage).
 		boolean useJdbcMetadata = ConfigurationHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", configValues, true );
 		if ( useJdbcMetadata ) {
 			try {
 				Connection conn = connectionProvider.getConnection();
 				try {
 					DatabaseMetaData meta = conn.getMetaData();
 					log.info( "Database ->\n" +
 							"       name : " + meta.getDatabaseProductName() + '\n' +
 							"    version : " +  meta.getDatabaseProductVersion() + '\n' +
 							"      major : " + meta.getDatabaseMajorVersion() + '\n' +
 							"      minor : " + meta.getDatabaseMinorVersion()
 					);
 					log.info( "Driver ->\n" +
 							"       name : " + meta.getDriverName() + '\n' +
 							"    version : " + meta.getDriverVersion() + '\n' +
 							"      major : " + meta.getDriverMajorVersion() + '\n' +
 							"      minor : " + meta.getDriverMinorVersion()
 					);
 					log.info( "JDBC version : " + meta.getJDBCMajorVersion() + "." + meta.getJDBCMinorVersion() );
 
 					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
 					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
 					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
 					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
 					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
 					extraKeywordsString = meta.getSQLKeywords();
 					sqlStateType = meta.getSQLStateType();
 					lobLocatorUpdateCopy = meta.locatorsUpdateCopy();
 					typeInfoSet.addAll( TypeInfoExtracter.extractTypeInfo( meta ) );
 
 					dialect = dialectFactory.buildDialect( configValues, conn );
 
 					catalogName = conn.getCatalog();
 					SchemaNameResolver schemaNameResolver = determineExplicitSchemaNameResolver( configValues );
 					if ( schemaNameResolver == null ) {
 // todo : add dialect method
 //						schemaNameResolver = dialect.getSchemaNameResolver();
 					}
 					if ( schemaNameResolver != null ) {
 						schemaName = schemaNameResolver.resolveSchemaName( conn );
 					}
 				}
 				catch ( SQLException sqle ) {
 					log.warn( "Could not obtain connection metadata", sqle );
 				}
 				finally {
 					connectionProvider.closeConnection( conn );
 				}
 			}
 			catch ( SQLException sqle ) {
 				log.warn( "Could not obtain connection to query metadata", sqle );
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 			catch ( UnsupportedOperationException uoe ) {
 				// user supplied JDBC connections
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 		}
 		else {
 			dialect = dialectFactory.buildDialect( configValues, null );
 		}
 
 		final boolean showSQL = ConfigurationHelper.getBoolean( Environment.SHOW_SQL, configValues, false );
 		final boolean formatSQL = ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, configValues, false );
 
 		this.dialect = dialect;
-		this.sqlStatementLogger =  new SQLStatementLogger( showSQL, formatSQL );
-		this.sqlExceptionHelper = new SQLExceptionHelper( dialect.buildSQLExceptionConverter() );
+		this.sqlStatementLogger =  new SqlStatementLogger( showSQL, formatSQL );
+		this.sqlExceptionHelper = new SqlExceptionHelper( dialect.buildSQLExceptionConverter() );
 		this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl(
 				metaSupportsScrollable,
 				metaSupportsGetGeneratedKeys,
 				metaSupportsBatchUpdates,
 				metaReportsDDLInTxnSupported,
 				metaReportsDDLCausesTxnCommit,
 				parseKeywords( extraKeywordsString ),
 				parseSQLStateType( sqlStateType ),
 				lobLocatorUpdateCopy,
 				schemaName,
 				catalogName,
 				typeInfoSet
 		);
 	}
 
 	// todo : add to Environment
 	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
 
 	private SchemaNameResolver determineExplicitSchemaNameResolver(Map configValues) {
 		Object setting = configValues.get( SCHEMA_NAME_RESOLVER );
 		if ( SchemaNameResolver.class.isInstance( setting ) ) {
 			return (SchemaNameResolver) setting;
 		}
 
 		String resolverClassName = (String) setting;
 		if ( resolverClassName != null ) {
 			try {
 				Class resolverClass = ReflectHelper.classForName( resolverClassName, getClass() );
 				return (SchemaNameResolver) ReflectHelper.getDefaultConstructor( resolverClass ).newInstance();
 			}
 			catch ( ClassNotFoundException e ) {
 				log.warn( "Unable to locate configured schema name resolver class [" + resolverClassName + "]" + e.toString() );
 			}
 			catch ( InvocationTargetException e ) {
 				log.warn( "Unable to instantiate configured schema name resolver [" + resolverClassName + "]" + e.getTargetException().toString() );
 			}
 			catch ( Exception e ) {
 				log.warn( "Unable to instantiate configured schema name resolver [" + resolverClassName + "]" + e.toString() );
 			}
 		}
 		return null;
 	}
 
 	private Set<String> parseKeywords(String extraKeywordsString) {
 		Set<String> keywordSet = new HashSet<String>();
 		for ( String keyword : extraKeywordsString.split( "," ) ) {
 			keywordSet.add( keyword );
 		}
 		return keywordSet;
 	}
 
 	private ExtractedDatabaseMetaData.SQLStateType parseSQLStateType(int sqlStateType) {
 		switch ( sqlStateType ) {
 			case DatabaseMetaData.sqlStateSQL99 : {
 				return ExtractedDatabaseMetaData.SQLStateType.SQL99;
 			}
 			case DatabaseMetaData.sqlStateXOpen : {
 				return ExtractedDatabaseMetaData.SQLStateType.XOpen;
 			}
 			default : {
 				return ExtractedDatabaseMetaData.SQLStateType.UNKOWN;
 			}
 		}
 	}
 
 	private static class ExtractedDatabaseMetaDataImpl implements ExtractedDatabaseMetaData {
 		private final boolean supportsScrollableResults;
 		private final boolean supportsGetGeneratedKeys;
 		private final boolean supportsBatchUpdates;
 		private final boolean supportsDataDefinitionInTransaction;
 		private final boolean doesDataDefinitionCauseTransactionCommit;
 		private final Set<String> extraKeywords;
 		private final SQLStateType sqlStateType;
 		private final boolean lobLocatorUpdateCopy;
 		private final String connectionSchemaName;
 		private final String connectionCatalogName;
 		private final LinkedHashSet<TypeInfo> typeInfoSet;
 
 		private ExtractedDatabaseMetaDataImpl(
 				boolean supportsScrollableResults,
 				boolean supportsGetGeneratedKeys,
 				boolean supportsBatchUpdates,
 				boolean supportsDataDefinitionInTransaction,
 				boolean doesDataDefinitionCauseTransactionCommit,
 				Set<String> extraKeywords,
 				SQLStateType sqlStateType,
 				boolean lobLocatorUpdateCopy,
 				String connectionSchemaName,
 				String connectionCatalogName,
 				LinkedHashSet<TypeInfo> typeInfoSet) {
 			this.supportsScrollableResults = supportsScrollableResults;
 			this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
 			this.supportsBatchUpdates = supportsBatchUpdates;
 			this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
 			this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
 			this.extraKeywords = extraKeywords;
 			this.sqlStateType = sqlStateType;
 			this.lobLocatorUpdateCopy = lobLocatorUpdateCopy;
 			this.connectionSchemaName = connectionSchemaName;
 			this.connectionCatalogName = connectionCatalogName;
 			this.typeInfoSet = typeInfoSet;
 		}
 
 		public boolean supportsScrollableResults() {
 			return supportsScrollableResults;
 		}
 
 		public boolean supportsGetGeneratedKeys() {
 			return supportsGetGeneratedKeys;
 		}
 
 		public boolean supportsBatchUpdates() {
 			return supportsBatchUpdates;
 		}
 
 		public boolean supportsDataDefinitionInTransaction() {
 			return supportsDataDefinitionInTransaction;
 		}
 
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return doesDataDefinitionCauseTransactionCommit;
 		}
 
 		public Set<String> getExtraKeywords() {
 			return extraKeywords;
 		}
 
 		public SQLStateType getSqlStateType() {
 			return sqlStateType;
 		}
 
 		public boolean doesLobLocatorUpdateCopy() {
 			return lobLocatorUpdateCopy;
 		}
 
 		public String getConnectionSchemaName() {
 			return connectionSchemaName;
 		}
 
 		public String getConnectionCatalogName() {
 			return connectionCatalogName;
 		}
 
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return typeInfoSet;
 		}
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
-	public SQLStatementLogger getSqlStatementLogger() {
+	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
-	public SQLExceptionHelper getSqlExceptionHelper() {
+	public SqlExceptionHelper getSqlExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return extractedMetaDataSupport;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/StatementPreparerImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/StatementPreparerImpl.java
index ed4fa259b0..ff26f46259 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/StatementPreparerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/StatementPreparerImpl.java
@@ -1,223 +1,223 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.ScrollMode;
 import org.hibernate.TransactionException;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.StatementPreparer;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
 * @author Steve Ebersole
 */
 class StatementPreparerImpl implements StatementPreparer {
 	private long transactionTimeOut = -1;
 	private JdbcCoordinatorImpl jdbcCoordinator;
 
 	StatementPreparerImpl(JdbcCoordinatorImpl jdbcCoordinator) {
 		this.jdbcCoordinator = jdbcCoordinator;
 	}
 
 	protected final Settings settings() {
 		return jdbcCoordinator.sessionFactory().getSettings();
 	}
 
 	protected final Connection connectionProxy() {
 		return logicalConnection().getShareableConnectionProxy();
 	}
 
 	protected final LogicalConnectionImplementor logicalConnection() {
 		return jdbcCoordinator.getLogicalConnection();
 	}
 
-	protected final SQLExceptionHelper sqlExceptionHelper() {
+	protected final SqlExceptionHelper sqlExceptionHelper() {
 		return jdbcCoordinator.getTransactionCoordinator()
 				.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getSqlExceptionHelper();
 	}
 
 	@Override
 	public PreparedStatement prepareStatement(String sql) {
 		return buildPreparedStatementPreparationTemplate( sql, false ).prepareStatement();
 	}
 
 	@Override
 	public PreparedStatement prepareStatement(String sql, final boolean isCallable) {
 		jdbcCoordinator.executeBatch();
 		return buildPreparedStatementPreparationTemplate( sql, isCallable ).prepareStatement();
 	}
 
 	private StatementPreparationTemplate buildPreparedStatementPreparationTemplate(String sql, final boolean isCallable) {
 		return new StatementPreparationTemplate( sql ) {
 			@Override
 			protected PreparedStatement doPrepare() throws SQLException {
 				return isCallable
 						? connectionProxy().prepareCall( sql )
 						: connectionProxy().prepareStatement( sql );
 			}
 		};
 	}
 
 	private void checkAutoGeneratedKeysSupportEnabled() {
 		if ( ! settings().isGetGeneratedKeysEnabled() ) {
 			throw new AssertionFailure( "getGeneratedKeys() support is not enabled" );
 		}
 	}
 
 	@Override
 	public PreparedStatement prepareStatement(String sql, final int autoGeneratedKeys) {
 		if ( autoGeneratedKeys == PreparedStatement.RETURN_GENERATED_KEYS ) {
 			checkAutoGeneratedKeysSupportEnabled();
 		}
 		jdbcCoordinator.executeBatch();
 		return new StatementPreparationTemplate( sql ) {
 			public PreparedStatement doPrepare() throws SQLException {
 				return connectionProxy().prepareStatement( sql, autoGeneratedKeys );
 			}
 		}.prepareStatement();
 	}
 
 	@Override
 	public PreparedStatement prepareStatement(String sql, final String[] columnNames) {
 		checkAutoGeneratedKeysSupportEnabled();
 		jdbcCoordinator.executeBatch();
 		return new StatementPreparationTemplate( sql ) {
 			public PreparedStatement doPrepare() throws SQLException {
 				return connectionProxy().prepareStatement( sql, columnNames );
 			}
 		}.prepareStatement();
 	}
 
 	@Override
 	public PreparedStatement prepareQueryStatement(
 			String sql,
 			final boolean isCallable,
 			final ScrollMode scrollMode) {
 		if ( scrollMode != null && !scrollMode.equals( ScrollMode.FORWARD_ONLY ) ) {
 			if ( ! settings().isScrollableResultSetsEnabled() ) {
 				throw new AssertionFailure("scrollable result sets are not enabled");
 			}
 			PreparedStatement ps = new QueryStatementPreparationTemplate( sql ) {
 				public PreparedStatement doPrepare() throws SQLException {
 						return isCallable
 								? connectionProxy().prepareCall(
 								sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
 						)
 								: connectionProxy().prepareStatement(
 								sql, scrollMode.toResultSetType(), ResultSet.CONCUR_READ_ONLY
 						);
 				}
 			}.prepareStatement();
 			logicalConnection().getResourceRegistry().registerLastQuery( ps );
 			return ps;
 		}
 		else {
 			PreparedStatement ps = new QueryStatementPreparationTemplate( sql ) {
 				public PreparedStatement doPrepare() throws SQLException {
 						return isCallable
 								? connectionProxy().prepareCall( sql )
 								: connectionProxy().prepareStatement( sql );
 				}
 			}.prepareStatement();
 			logicalConnection().getResourceRegistry().registerLastQuery( ps );
 			return ps;
 		}
 	}
 
 	@Override
 	public void setTransactionTimeOut(int timeOut) {
 		transactionTimeOut = timeOut;
 	}
 
 	@Override
 	public void unsetTransactionTimeOut() {
 		transactionTimeOut = -1;
 	}
 
 	private abstract class StatementPreparationTemplate {
 		protected final String sql;
 
 		protected StatementPreparationTemplate(String sql) {
 			this.sql = sql;
 		}
 
 		public PreparedStatement prepareStatement() {
 			try {
 				PreparedStatement preparedStatement = doPrepare();
 				setStatementTimeout( preparedStatement );
 				postProcess( preparedStatement );
 				return preparedStatement;
 			}
 			catch ( SQLException e ) {
 				throw sqlExceptionHelper().convert( e, "could not prepare statement", sql );
 			}
 		}
 
 		protected abstract PreparedStatement doPrepare() throws SQLException;
 
 		public void postProcess(PreparedStatement preparedStatement) throws SQLException {
 		}
 
 		private void setStatementTimeout(PreparedStatement preparedStatement) throws SQLException {
 			if ( transactionTimeOut > 0 ) {
 				int timeout = (int) ( transactionTimeOut - ( System.currentTimeMillis() / 1000 ) );
 				if ( timeout <= 0 ) {
 					throw new TransactionException( "transaction timeout expired" );
 				}
 				else {
 					preparedStatement.setQueryTimeout( timeout );
 				}
 			}
 		}
 
 	}
 
 	private abstract class QueryStatementPreparationTemplate extends StatementPreparationTemplate {
 		protected QueryStatementPreparationTemplate(String sql) {
 			super( sql );
 		}
 
 		public void postProcess(PreparedStatement preparedStatement) throws SQLException {
 			super.postProcess( preparedStatement );
 			setStatementFetchSize( preparedStatement );
 		}
 	}
 
 	private void setStatementFetchSize(PreparedStatement statement) throws SQLException {
 		if ( settings().getJdbcFetchSize() != null ) {
 			statement.setFetchSize( settings().getJdbcFetchSize() );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfo.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfo.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfo.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfo.java
index 543913df56..b84fe7b5dd 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfo.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfo.java
@@ -1,126 +1,126 @@
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
-package org.hibernate.internal.util.jdbc;
+package org.hibernate.engine.jdbc.internal;
 
 /**
  * Models type info extracted from {@link java.sql.DatabaseMetaData#getTypeInfo()}
  *
  * @author Steve Ebersole
  */
 public class TypeInfo {
 	private final String typeName;
 	private final int jdbcTypeCode;
 	private final String[] createParams;
 	private final boolean unsigned;
 	private final int precision;
 	private final short minimumScale;
 	private final short maximumScale;
 	private final boolean fixedPrecisionScale;
 	private final String literalPrefix;
 	private final String literalSuffix;
 	private final boolean caseSensitive;
 	private final TypeSearchability searchability;
 	private final TypeNullability nullability;
 
 	public TypeInfo(
 			String typeName,
 			int jdbcTypeCode,
 			String[] createParams,
 			boolean unsigned,
 			int precision,
 			short minimumScale,
 			short maximumScale,
 			boolean fixedPrecisionScale,
 			String literalPrefix,
 			String literalSuffix,
 			boolean caseSensitive,
 			TypeSearchability searchability,
 			TypeNullability nullability) {
 		this.typeName = typeName;
 		this.jdbcTypeCode = jdbcTypeCode;
 		this.createParams = createParams;
 		this.unsigned = unsigned;
 		this.precision = precision;
 		this.minimumScale = minimumScale;
 		this.maximumScale = maximumScale;
 		this.fixedPrecisionScale = fixedPrecisionScale;
 		this.literalPrefix = literalPrefix;
 		this.literalSuffix = literalSuffix;
 		this.caseSensitive = caseSensitive;
 		this.searchability = searchability;
 		this.nullability = nullability;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public int getJdbcTypeCode() {
 		return jdbcTypeCode;
 	}
 
 	public String[] getCreateParams() {
 		return createParams;
 	}
 
 	public boolean isUnsigned() {
 		return unsigned;
 	}
 
 	public int getPrecision() {
 		return precision;
 	}
 
 	public short getMinimumScale() {
 		return minimumScale;
 	}
 
 	public short getMaximumScale() {
 		return maximumScale;
 	}
 
 	public boolean isFixedPrecisionScale() {
 		return fixedPrecisionScale;
 	}
 
 	public String getLiteralPrefix() {
 		return literalPrefix;
 	}
 
 	public String getLiteralSuffix() {
 		return literalSuffix;
 	}
 
 	public boolean isCaseSensitive() {
 		return caseSensitive;
 	}
 
 	public TypeSearchability getSearchability() {
 		return searchability;
 	}
 
 	public TypeNullability getNullability() {
 		return nullability;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfoExtracter.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfoExtracter.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfoExtracter.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfoExtracter.java
index e39cd086eb..cac3401e4e 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeInfoExtracter.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfoExtracter.java
@@ -1,104 +1,104 @@
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
-package org.hibernate.internal.util.jdbc;
+package org.hibernate.engine.jdbc.internal;
 
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.LinkedHashSet;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.util.ArrayHelper;
 
 /**
  * Helper to extract type innformation from {@link DatabaseMetaData JDBC metadata}
  *
  * @author Steve Ebersole
  */
 public class TypeInfoExtracter {
 	private static final Logger log = LoggerFactory.getLogger( TypeInfoExtracter.class );
 
 	private TypeInfoExtracter() {
 	}
 
 	/**
 	 * Perform the extraction
 	 *
 	 * @param metaData The JDBC metadata
 	 *
 	 * @return The extracted metadata
 	 */
 	public static LinkedHashSet<TypeInfo> extractTypeInfo(DatabaseMetaData metaData) {
 		LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
 		try {
 			ResultSet resultSet = metaData.getTypeInfo();
 			try {
 				while ( resultSet.next() ) {
 					typeInfoSet.add(
 							new TypeInfo(
 									resultSet.getString( "TYPE_NAME" ),
 									resultSet.getInt( "DATA_TYPE" ),
 									interpretCreateParams( resultSet.getString( "CREATE_PARAMS" ) ),
 									resultSet.getBoolean( "UNSIGNED_ATTRIBUTE" ),
 									resultSet.getInt( "PRECISION" ),
 									resultSet.getShort( "MINIMUM_SCALE" ),
 									resultSet.getShort( "MAXIMUM_SCALE" ),
 									resultSet.getBoolean( "FIXED_PREC_SCALE" ),
 									resultSet.getString( "LITERAL_PREFIX" ),
 									resultSet.getString( "LITERAL_SUFFIX" ),
 									resultSet.getBoolean( "CASE_SENSITIVE" ),
 									TypeSearchability.interpret( resultSet.getShort( "SEARCHABLE" ) ),
 									TypeNullability.interpret( resultSet.getShort( "NULLABLE" ) )
 							)
 					);
 				}
 			}
 			catch ( SQLException e ) {
 				log.warn( "Error accessing type info result set : " + e.toString() );
 			}
 			finally {
 				try {
 					resultSet.close();
 				}
 				catch ( SQLException e ) {
 					log.warn( "Unable to release type info result set" );
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			log.warn( "Unable to retrieve type info result set : " + e.toString() );
 		}
 
 		return typeInfoSet;
 	}
 
 	private static String[] interpretCreateParams(String value) {
 		if ( value == null || value.length() == 0 ) {
 			return ArrayHelper.EMPTY_STRING_ARRAY;
 		}
 		return value.split( "," );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeNullability.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeNullability.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeNullability.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeNullability.java
index 2dad8be345..cdbfb04e7c 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeNullability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeNullability.java
@@ -1,74 +1,74 @@
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
-package org.hibernate.internal.util.jdbc;
+package org.hibernate.engine.jdbc.internal;
 
 import java.sql.DatabaseMetaData;
 
 /**
  * Describes the instrinsic nullability of a data type as reported by the JDBC driver.
  *
  * @author Steve Ebersole
  */
 public enum TypeNullability {
 	/**
 	 * The data type can accept nulls
 	 * @see DatabaseMetaData#typeNullable
 	 */
 	NULLABLE,
 	/**
 	 * The data type cannot accept nulls
 	 * @see DatabaseMetaData#typeNoNulls
 	 */
 	NON_NULLABLE,
 	/**
 	 * It is unknown if the data type accepts nulls
 	 * @see DatabaseMetaData#typeNullableUnknown
 	 */
 	UNKNOWN;
 
 	/**
 	 * Based on the code retrieved from {@link DatabaseMetaData#getTypeInfo()} for the {@code NULLABLE}
 	 * column, return the appropriate enum.
 	 *
 	 * @param code The retrieved code value.
 	 *
 	 * @return The corresponding enum.
 	 */
 	public static TypeNullability interpret(short code) {
 		switch ( code ) {
 			case DatabaseMetaData.typeNullable: {
 				return NULLABLE;
 			}
 			case DatabaseMetaData.typeNoNulls: {
 				return NON_NULLABLE;
 			}
 			case DatabaseMetaData.typeNullableUnknown: {
 				return UNKNOWN;
 			}
 			default: {
 				throw new IllegalArgumentException( "Unknown type nullability code [" + code + "] enountered" );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeSearchability.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeSearchability.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeSearchability.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeSearchability.java
index 555524f62f..96aef2e42d 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/jdbc/TypeSearchability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeSearchability.java
@@ -1,82 +1,82 @@
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
-package org.hibernate.internal.util.jdbc;
+package org.hibernate.engine.jdbc.internal;
 
 import java.sql.DatabaseMetaData;
 
 /**
  * Describes the searchability of a data type as reported by the JDBC driver.
  *
  * @author Steve Ebersole
  */
 public enum TypeSearchability {
 	/**
 	 * Type is not searchable.
 	 * @see java.sql.DatabaseMetaData#typePredNone
 	 */
 	NONE,
 	/**
 	 * Type is fully searchable
 	 * @see java.sql.DatabaseMetaData#typeSearchable
 	 */
 	FULL,
 	/**
 	 * Type is valid only in {@code WHERE ... LIKE}
 	 * @see java.sql.DatabaseMetaData#typePredChar
 	 */
 	CHAR,
 	/**
 	 * Type is supported only in {@code WHERE ... LIKE}
 	 * @see java.sql.DatabaseMetaData#typePredBasic
 	 */
 	BASIC;
 
 	/**
 	 * Based on the code retrieved from {@link java.sql.DatabaseMetaData#getTypeInfo()} for the {@code SEARCHABLE}
 	 * column, return the appropriate enum.
 	 *
 	 * @param code The retrieved code value.
 	 *
 	 * @return The corresponding enum.
 	 */
 	public static TypeSearchability interpret(short code) {
 		switch ( code ) {
 			case DatabaseMetaData.typeSearchable: {
 				return FULL;
 			}
 			case DatabaseMetaData.typePredNone: {
 				return NONE;
 			}
 			case DatabaseMetaData.typePredBasic: {
 				return BASIC;
 			}
 			case DatabaseMetaData.typePredChar: {
 				return CHAR;
 			}
 			default: {
 				throw new IllegalArgumentException( "Unknown type searchability code [" + code + "] enountered" );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
index 2a9cda5a1d..10bfe61040 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
@@ -1,133 +1,133 @@
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
 
 import java.util.LinkedHashSet;
 import java.util.Set;
 
-import org.hibernate.internal.util.jdbc.TypeInfo;
+import org.hibernate.engine.jdbc.internal.TypeInfo;
 
 /**
  * Information extracted from {@link java.sql.DatabaseMetaData} regarding what the JDBC driver reports as
  * being supported or not.  Obviously {@link java.sql.DatabaseMetaData} reports many things, these are a few in
  * which we have particular interest.
  *
  * @author Steve Ebersole
  */
 public interface ExtractedDatabaseMetaData {
 
 	public enum SQLStateType {
 		XOpen,
 		SQL99,
 		UNKOWN
 	}
 
 	/**
 	 * Did the driver report to supporting scrollable result sets?
 	 *
 	 * @return True if the driver reported to support {@link java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE}.
 	 * @see java.sql.DatabaseMetaData#supportsResultSetType
 	 */
 	public boolean supportsScrollableResults();
 
 	/**
 	 * Did the driver report to supporting retrieval of generated keys?
 	 *
 	 * @return True if the if the driver reported to support calls to {@link java.sql.Statement#getGeneratedKeys}
 	 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys
 	 */
 	public boolean supportsGetGeneratedKeys();
 
 	/**
 	 * Did the driver report to supporting batched updates?
 	 *
 	 * @return True if the driver supports batched updates
 	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates
 	 */
 	public boolean supportsBatchUpdates();
 
 	/**
 	 * Did the driver report to support performing DDL within transactions?
 	 *
 	 * @return True if the drivers supports DDL statements within transactions.
 	 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions
 	 */
 	public boolean supportsDataDefinitionInTransaction();
 
 	/**
 	 * Did the driver report to DDL statements performed within a transaction performing an implicit commit of the
 	 * transaction.
 	 *
 	 * @return True if the driver/database performs an implicit commit of transaction when DDL statement is
 	 * performed
 	 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
 	 */
 	public boolean doesDataDefinitionCauseTransactionCommit();
 
 	/**
 	 * Get the list of extra keywords (beyond standard SQL92 keywords) reported by the driver.
 	 *
 	 * @return The extra keywords used by this database.
 	 * @see java.sql.DatabaseMetaData#getSQLKeywords()
 	 */
 	public Set<String> getExtraKeywords();
 
 	/**
 	 * Retrieve the type of codes the driver says it uses for {@code SQLState}.  They might follow either
 	 * the X/Open standard or the SQL92 standard.
 	 *
 	 * @return The SQLState strategy reportedly used by this driver/database.
 	 * @see java.sql.DatabaseMetaData#getSQLStateType()
 	 */
 	public SQLStateType getSqlStateType();
 
 	/**
 	 * Did the driver report that updates to a LOB locator affect a copy of the LOB?
 	 *
 	 * @return True if updates to the state of a LOB locator update only a copy.
 	 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy() 
 	 */
 	public boolean doesLobLocatorUpdateCopy();
 
 	/**
 	 * Retrieve the name of the schema in effect when we connected to the database.
 	 *
 	 * @return The schema name
 	 */
 	public String getConnectionSchemaName();
 
 	/**
 	 * Retrieve the name of the catalog in effect when we connected to the database.
 	 *
 	 * @return The catalog name
 	 */
 	public String getConnectionCatalogName();
 
 	/**
 	 * Set of type info reported by the driver.
 	 * 
 	 * @return
 	 */
 	public LinkedHashSet<TypeInfo> getTypeInfoSet();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
index 9a5747996d..6a25234082 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
@@ -1,76 +1,76 @@
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
 
 import java.sql.Connection;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Service;
 
 /**
  * Contract for services around JDBC operations.  These represent shared resources, aka not varied by session/use.
  *
  * @author Steve Ebersole
  */
 public interface JdbcServices extends Service {
 	/**
 	 * Obtain service for providing JDBC connections.
 	 *
 	 * @return The connection provider.
 	 */
 	public ConnectionProvider getConnectionProvider();
 
 	/**
 	 * Obtain the dialect of the database to which {@link Connection connections} from
 	 * {@link #getConnectionProvider()} point.
 	 *
 	 * @return The database dialect.
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Obtain service for logging SQL statements.
 	 *
 	 * @return The SQL statement logger.
 	 */
-	public SQLStatementLogger getSqlStatementLogger();
+	public SqlStatementLogger getSqlStatementLogger();
 
 	/**
 	 * Obtain service for dealing with exceptions.
 	 *
 	 * @return The exception helper service.
 	 */
-	public SQLExceptionHelper getSqlExceptionHelper();
+	public SqlExceptionHelper getSqlExceptionHelper();
 
 	/**
 	 * Obtain infomration about supported behavior reported by the JDBC driver.
 	 * <p/>
 	 * Yuck, yuck, yuck!  Much prefer this to be part of a "basic settings" type object.  See discussion
 	 * on {@link org.hibernate.cfg.internal.JdbcServicesBuilder}
 	 * 
 	 * @return
 	 */
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLExceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLExceptionHelper.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
index 0db12c826b..69885d8f53 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLExceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
@@ -1,213 +1,213 @@
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
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.JDBCException;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.exception.SQLStateConverter;
 import org.hibernate.exception.ViolatedConstraintNameExtracter;
 import org.hibernate.util.StringHelper;
 
 /**
  * Helper for handling SQLExceptions in various manners.
  *
  * @author Steve Ebersole
  */
-public class SQLExceptionHelper implements Serializable {
-	private static final Logger log = LoggerFactory.getLogger( SQLExceptionHelper.class );
+public class SqlExceptionHelper implements Serializable {
+	private static final Logger log = LoggerFactory.getLogger( SqlExceptionHelper.class );
 
 	public static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";
 	public static final String DEFAULT_WARNING_MSG = "SQL Warning";
 
 	public static final SQLExceptionConverter DEFAULT_CONVERTER = new SQLStateConverter(
 			new ViolatedConstraintNameExtracter() {
 				public String extractConstraintName(SQLException e) {
 					return null;
 				}
 			}
 	);
 
 	private SQLExceptionConverter sqlExceptionConverter;
 
 	/**
 	 * Create an exception helper with a default exception converter.
 	 */
-	public SQLExceptionHelper() {
+	public SqlExceptionHelper() {
 		sqlExceptionConverter = DEFAULT_CONVERTER;
 	}
 
 	/**
 	 * Create an exception helper with a specific exception converter.
 	 *
 	 * @param sqlExceptionConverter The exception converter to use.
 	 */
-	public SQLExceptionHelper(SQLExceptionConverter sqlExceptionConverter) {
+	public SqlExceptionHelper(SQLExceptionConverter sqlExceptionConverter) {
 		this.sqlExceptionConverter = sqlExceptionConverter;
 	}
 
 	/**
 	 * Access the current exception converter being used internally.
 	 *
 	 * @return The current exception converter.
 	 */
 	public SQLExceptionConverter getSqlExceptionConverter() {
 		return sqlExceptionConverter;
 	}
 
 	/**
 	 * Inject the exception converter to use.
 	 * <p/>
 	 * NOTE : <tt>null</tt> is allowed and signifies to use the default.
 	 *
 	 * @param sqlExceptionConverter The converter to use.
 	 */
 	public void setSqlExceptionConverter(SQLExceptionConverter sqlExceptionConverter) {
 		this.sqlExceptionConverter = ( sqlExceptionConverter == null ? DEFAULT_CONVERTER : sqlExceptionConverter );
 	}
 
 	/**
 	 * Convert an SQLException using the current converter, doing some logging first.
 	 *
 	 * @param sqle The exception to convert
 	 * @param message An error message.
 	 * @return The converted exception
 	 */
 	public JDBCException convert(SQLException sqle, String message) {
 		return convert( sqle, message, "n/a" );
 	}
 
 	/**
 	 * Convert an SQLException using the current converter, doing some logging first.
 	 *
 	 * @param sqle The exception to convert
 	 * @param message An error message.
 	 * @param sql The SQL being executed when the exception occurred
 	 * @return The converted exception
 	 */
 	public JDBCException convert(SQLException sqle, String message, String sql) {
 		logExceptions( sqle, message + " [" + sql + "]" );
 		return sqlExceptionConverter.convert( sqle, message, sql );
 	}
 
 	/**
 	 * Log any {@link java.sql.SQLWarning}s registered with the connection.
 	 *
 	 * @param connection The connection to check for warnings.
 	 */
 	public void logAndClearWarnings(Connection connection) {
 		if ( log.isWarnEnabled() ) {
 			try {
 				logWarnings( connection.getWarnings() );
 			}
 			catch ( SQLException sqle ) {
 				//workaround for WebLogic
 				log.debug( "could not log warnings", sqle );
 			}
 		}
 		try {
 			//Sybase fail if we don't do that, sigh...
 			connection.clearWarnings();
 		}
 		catch ( SQLException sqle ) {
 			log.debug( "could not clear warnings", sqle );
 		}
 
 	}
 
 	/**
 	 * Log the given (and any nested) warning.
 	 *
 	 * @param warning The warning
 	 */
 	public void logWarnings(SQLWarning warning) {
 		logWarnings( warning, null );
 	}
 
 	/**
 	 * Log the given (and any nested) warning.
 	 *
 	 * @param warning The warning
 	 * @param message The message text to use as a preamble.
 	 */
 	public void logWarnings(SQLWarning warning, String message) {
 		if ( log.isWarnEnabled() ) {
 			if ( log.isDebugEnabled() && warning != null ) {
 				message = StringHelper.isNotEmpty( message ) ? message : DEFAULT_WARNING_MSG;
 				log.debug( message, warning );
 			}
 			while ( warning != null ) {
 				StringBuffer buf = new StringBuffer( 30 )
 						.append( "SQL Warning: " )
 						.append( warning.getErrorCode() )
 						.append( ", SQLState: " )
 						.append( warning.getSQLState() );
 				log.warn( buf.toString() );
 				log.warn( warning.getMessage() );
 				warning = warning.getNextWarning();
 			}
 		}
 	}
 
 	/**
 	 * Log the given (and any nested) exception.
 	 *
 	 * @param sqlException The exception to log
 	 */
 	public void logExceptions(SQLException sqlException) {
 		logExceptions( sqlException, null );
 	}
 
 	/**
 	 * Log the given (and any nested) exception.
 	 *
 	 * @param sqlException The exception to log
 	 * @param message The message text to use as a preamble.
 	 */
 	public void logExceptions(SQLException sqlException, String message) {
 		if ( log.isErrorEnabled() ) {
 			if ( log.isDebugEnabled() ) {
 				message = StringHelper.isNotEmpty( message ) ? message : DEFAULT_EXCEPTION_MSG;
 				log.debug( message, sqlException );
 			}
 			while ( sqlException != null ) {
 				StringBuffer buf = new StringBuffer( 30 )
 						.append( "SQL Error: " )
 						.append( sqlException.getErrorCode() )
 						.append( ", SQLState: " )
 						.append( sqlException.getSQLState() );
 				log.warn( buf.toString() );
 				log.error( sqlException.getMessage() );
 				sqlException = sqlException.getNextException();
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLStatementLogger.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlStatementLogger.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLStatementLogger.java
rename to hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlStatementLogger.java
index 5be6dd39ce..be469d9bb7 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SQLStatementLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlStatementLogger.java
@@ -1,109 +1,109 @@
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
 
-import org.hibernate.jdbc.util.FormatStyle;
-import org.hibernate.jdbc.util.Formatter;
+import org.hibernate.engine.jdbc.internal.FormatStyle;
+import org.hibernate.engine.jdbc.internal.Formatter;
 
 /**
  * Centralize logging for SQL statements.
  *
  * @author Steve Ebersole
  */
-public class SQLStatementLogger {
-	private static final Logger log = LoggerFactory.getLogger( SQLStatementLogger.class );
+public class SqlStatementLogger {
+	private static final Logger log = LoggerFactory.getLogger( SqlStatementLogger.class );
 
 	private boolean logToStdout;
 	private boolean format;
 
 	/**
-	 * Constructs a new SQLStatementLogger instance.
+	 * Constructs a new SqlStatementLogger instance.
 	 */
-	public SQLStatementLogger() {
+	public SqlStatementLogger() {
 		this( false, false );
 	}
 
 	/**
-	 * Constructs a new SQLStatementLogger instance.
+	 * Constructs a new SqlStatementLogger instance.
 	 *
 	 * @param logToStdout Should we log to STDOUT in addition to our internal logger.
 	 * @param format Should we format the statements prior to logging
 	 */
-	public SQLStatementLogger(boolean logToStdout, boolean format) {
+	public SqlStatementLogger(boolean logToStdout, boolean format) {
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
 		logStatement( statement, FormatStyle.BASIC.getFormatter() );
 	}
 
 	public void logStatement(String statement, Formatter formatter) {
 		if ( format ) {
 			if ( logToStdout || log.isDebugEnabled() ) {
 				statement = formatter.format( statement );
 			}
 		}
 		log.debug( statement );
 		if ( logToStdout ) {
 			System.out.println( "Hibernate: " + statement );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
index f00fbf55ca..36269665ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
@@ -1,188 +1,188 @@
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
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.transaction.spi.IsolationDelegate;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.jdbc.ReturningWork;
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
 
-	protected SQLExceptionHelper sqlExceptionHelper() {
+	protected SqlExceptionHelper sqlExceptionHelper() {
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
 
 	@Override
 	public <T> T delegateWork(ReturningWork<T> work, boolean transacted) throws HibernateException {
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
 
 				T result = work.execute( connection );
 
 				if ( transacted ) {
 					connection.commit();
 				}
 
 				return result;
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
index 42bcfa9966..aeab29e538 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
@@ -1,294 +1,294 @@
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
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.transaction.spi.IsolationDelegate;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.jdbc.ReturningWork;
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
 
-	protected SQLExceptionHelper sqlExceptionHelper() {
+	protected SqlExceptionHelper sqlExceptionHelper() {
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
 
 	@Override
 	public <T> T delegateWork(ReturningWork<T> work, boolean transacted) throws HibernateException {
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
 					return doTheWorkInNewTransaction( work, transactionManager );
 				}
 				else {
 					return doTheWorkInNoTransaction( work );
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
 
 	private <T> T doTheWorkInNewTransaction(ReturningWork<T> work, TransactionManager transactionManager) {
 		T result = null;
 		try {
 			// start the new isolated transaction
 			transactionManager.begin();
 
 			try {
 				result = doTheWork( work );
 				// if everything went ok, commit the isolated transaction
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
 		return result;
 	}
 
 	private <T> T doTheWorkInNoTransaction(ReturningWork<T> work) {
 		return doTheWork( work );
 	}
 
 	private <T> T doTheWork(ReturningWork<T> work) {
 		try {
 			// obtain our isolated connection
 			Connection connection = connectionProvider().getConnection();
 			try {
 				// do the actual work
 				return work.execute( connection );
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
 		catch ( SQLException e ) {
 			throw sqlExceptionHelper().convert( e, "unable to obtain isolated JDBC connection" );
 		}
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
index 265c0ae216..f70535534b 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
@@ -1,292 +1,292 @@
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.id.enhanced.AccessCallback;
 import org.hibernate.id.enhanced.OptimizerFactory;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.ReturningWork;
-import org.hibernate.jdbc.util.FormatStyle;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
 
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
 public class MultipleHiLoPerTableGenerator implements PersistentIdentifierGenerator, Configurable {
 	private static final Logger log = LoggerFactory.getLogger( MultipleHiLoPerTableGenerator.class );
 	
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
 
 	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
 		final ReturningWork<IntegralDataTypeHolder> work = new ReturningWork<IntegralDataTypeHolder>() {
 			@Override
 			public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 				IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( returnClass );
-				SQLStatementLogger statementLogger = session
+				SqlStatementLogger statementLogger = session
 						.getFactory()
 						.getServiceRegistry()
 						.getService( JdbcServices.class )
 						.getSqlStatementLogger();
 				int rows;
 				do {
 					statementLogger.logStatement( query, FormatStyle.BASIC.getFormatter() );
 					PreparedStatement qps = connection.prepareStatement( query );
 					PreparedStatement ips = null;
 					try {
 						ResultSet rs = qps.executeQuery();
 						boolean isInitialized = rs.next();
 						if ( !isInitialized ) {
 							value.initialize( 0 );
 							statementLogger.logStatement( insert, FormatStyle.BASIC.getFormatter() );
 							ips = connection.prepareStatement( insert );
 							value.bind( ips, 1 );
 							ips.execute();
 						}
 						else {
 							value.initialize( rs, 0 );
 						}
 						rs.close();
 					}
 					catch (SQLException sqle) {
 						log.error("could not read or init a hi value", sqle);
 						throw sqle;
 					}
 					finally {
 						if (ips != null) {
 							ips.close();
 						}
 						qps.close();
 					}
 
 					statementLogger.logStatement( update, FormatStyle.BASIC.getFormatter() );
 					PreparedStatement ups = connection.prepareStatement( update );
 					try {
 						value.copy().increment().bind( ups, 1 );
 						value.bind( ups, 2 );
 						rows = ups.executeUpdate();
 					}
 					catch (SQLException sqle) {
 						log.error("could not update hi value in: " + tableName, sqle);
 						throw sqle;
 					}
 					finally {
 						ups.close();
 					}
 				} while ( rows==0 );
 
 				return value;
 			}
 		};
 
 		// maxLo < 1 indicates a hilo generator with no hilo :?
 		if ( maxLo < 1 ) {
 			//keep the behavior consistent even for boundary usages
 			IntegralDataTypeHolder value = null;
 			while ( value == null || value.lt( 1 ) ) {
 				value = session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork( work, true );
 			}
 			return value.makeValue();
 		}
 
 		return hiloOptimizer.generate(
 				new AccessCallback() {
 					public IntegralDataTypeHolder getNextValue() {
 						return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork( work, true );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
index 1c8019b5df..fa2790142a 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
@@ -1,227 +1,227 @@
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
 package org.hibernate.id;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.ReturningWork;
-import org.hibernate.jdbc.util.FormatStyle;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
 
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
 public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
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
-		final SQLStatementLogger statementLogger = session
+		final SqlStatementLogger statementLogger = session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getSqlStatementLogger();
 		return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
 				new ReturningWork<IntegralDataTypeHolder>() {
 					@Override
 					public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 						IntegralDataTypeHolder value = buildHolder();
 						int rows;
 						do {
 							// The loop ensures atomicity of the
 							// select + update even for no transaction
 							// or read committed isolation level
 
 							statementLogger.logStatement( query, FormatStyle.BASIC.getFormatter() );
 							PreparedStatement qps = connection.prepareStatement( query );
 							try {
 								ResultSet rs = qps.executeQuery();
 								if ( !rs.next() ) {
 									String err = "could not read a hi value - you need to populate the table: " + tableName;
 									log.error(err);
 									throw new IdentifierGenerationException(err);
 								}
 								value.initialize( rs, 1 );
 								rs.close();
 							}
 							catch (SQLException e) {
 								log.error("could not read a hi value", e);
 								throw e;
 							}
 							finally {
 								qps.close();
 							}
 
 							statementLogger.logStatement( update, FormatStyle.BASIC.getFormatter() );
 							PreparedStatement ups = connection.prepareStatement(update);
 							try {
 								value.copy().increment().bind( ups, 1 );
 								value.bind( ups, 2 );
 								rows = ups.executeUpdate();
 							}
 							catch (SQLException sqle) {
 								log.error("could not update hi value in: " + tableName, sqle);
 								throw sqle;
 							}
 							finally {
 								ups.close();
 							}
 						}
 						while (rows==0);
 						return value;
 					}
 				},
 				true
 		);
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
 
 	protected IntegralDataTypeHolder buildHolder() {
 		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
index b2bc06083d..3c704095eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
@@ -1,580 +1,580 @@
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.id.Configurable;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.jdbc.ReturningWork;
-import org.hibernate.jdbc.util.FormatStyle;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 import org.hibernate.util.StringHelper;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Collections;
 import java.util.Map;
 import java.util.Properties;
 
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
 public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
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
 
 	@Override
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
 
 	@Override
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
 
 	@Override
 	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
-		final SQLStatementLogger statementLogger = session
+		final SqlStatementLogger statementLogger = session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getSqlStatementLogger();
 		return optimizer.generate(
 				new AccessCallback() {
 					@Override
 					public IntegralDataTypeHolder getNextValue() {
 						return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
 								new ReturningWork<IntegralDataTypeHolder>() {
 									@Override
 									public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 										IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
 										int rows;
 										do {
 											statementLogger.logStatement( selectQuery, FormatStyle.BASIC.getFormatter() );
 											PreparedStatement selectPS = connection.prepareStatement( selectQuery );
 											try {
 												selectPS.setString( 1, segmentValue );
 												ResultSet selectRS = selectPS.executeQuery();
 												if ( !selectRS.next() ) {
 													value.initialize( initialValue );
 													PreparedStatement insertPS = null;
 													try {
 														statementLogger.logStatement( insertQuery, FormatStyle.BASIC.getFormatter() );
 														insertPS = connection.prepareStatement( insertQuery );
 														insertPS.setString( 1, segmentValue );
 														value.bind( insertPS, 2 );
 														insertPS.execute();
 													}
 													finally {
 														if ( insertPS != null ) {
 															insertPS.close();
 														}
 													}
 												}
 												else {
 													value.initialize( selectRS, 1 );
 												}
 												selectRS.close();
 											}
 											catch ( SQLException e ) {
 												log.error( "could not read or init a hi value", e );
 												throw e;
 											}
 											finally {
 												selectPS.close();
 											}
 
 											statementLogger.logStatement( updateQuery, FormatStyle.BASIC.getFormatter() );
 											PreparedStatement updatePS = connection.prepareStatement( updateQuery );
 											try {
 												final IntegralDataTypeHolder updateValue = value.copy();
 												if ( optimizer.applyIncrementSizeToSourceValues() ) {
 													updateValue.add( incrementSize );
 												}
 												else {
 													updateValue.increment();
 												}
 												updateValue.bind( updatePS, 1 );
 												value.bind( updatePS, 2 );
 												updatePS.setString( 3, segmentValue );
 												rows = updatePS.executeUpdate();
 											}
 											catch ( SQLException e ) {
 												log.error( "could not updateQuery hi value in: " + tableName, e );
 												throw e;
 											}
 											finally {
 												updatePS.close();
 											}
 										}
 										while ( rows == 0 );
 
 										accessCount++;
 
 										return value;
 									}
 								},
 								true
 						);
 					}
 				}
 		);
 	}
 
 	@Override
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
 
 	@Override
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
index cbad28cb17..2464d8858d 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
@@ -1,198 +1,198 @@
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
 package org.hibernate.id.enhanced;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.jdbc.ReturningWork;
-import org.hibernate.jdbc.util.FormatStyle;
+import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 /**
  * Describes a table used to mimic sequence behavior
  *
  * @author Steve Ebersole
  */
 public class TableStructure implements DatabaseStructure {
 	private static final Logger log = LoggerFactory.getLogger( TableStructure.class );
 
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
 
 	@Override
 	public String getName() {
 		return tableName;
 	}
 
 	@Override
 	public int getInitialValue() {
 		return initialValue;
 	}
 
 	@Override
 	public int getIncrementSize() {
 		return incrementSize;
 	}
 
 	@Override
 	public int getTimesAccessed() {
 		return accessCounter;
 	}
 
 	@Override
 	public void prepare(Optimizer optimizer) {
 		applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
 	}
 
 	@Override
 	public AccessCallback buildCallback(final SessionImplementor session) {
 		return new AccessCallback() {
 			@Override
 			public IntegralDataTypeHolder getNextValue() {
 				return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
 						new ReturningWork<IntegralDataTypeHolder>() {
 							@Override
 							public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
-								final SQLStatementLogger statementLogger = session
+								final SqlStatementLogger statementLogger = session
 										.getFactory()
 										.getServiceRegistry()
 										.getService( JdbcServices.class )
 										.getSqlStatementLogger();
 								IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( numberType );
 								int rows;
 								do {
 									statementLogger.logStatement( selectQuery, FormatStyle.BASIC.getFormatter() );
 									PreparedStatement selectStatement = connection.prepareStatement( selectQuery );
 									try {
 										ResultSet selectRS = selectStatement.executeQuery();
 										if ( !selectRS.next() ) {
 											String err = "could not read a hi value - you need to populate the table: " + tableName;
 											log.error( err );
 											throw new IdentifierGenerationException( err );
 										}
 										value.initialize( selectRS, 1 );
 										selectRS.close();
 									}
 									catch ( SQLException sqle ) {
 										log.error( "could not read a hi value", sqle );
 										throw sqle;
 									}
 									finally {
 										selectStatement.close();
 									}
 
 									statementLogger.logStatement( updateQuery, FormatStyle.BASIC.getFormatter() );
 									PreparedStatement updatePS = connection.prepareStatement( updateQuery );
 									try {
 										final int increment = applyIncrementSizeToSourceValues ? incrementSize : 1;
 										final IntegralDataTypeHolder updateValue = value.copy().add( increment );
 										updateValue.bind( updatePS, 1 );
 										value.bind( updatePS, 2 );
 										rows = updatePS.executeUpdate();
 									}
 									catch ( SQLException e ) {
 										log.error( "could not updateQuery hi value in: " + tableName, e );
 										throw e;
 									}
 									finally {
 										updatePS.close();
 									}
 								} while ( rows == 0 );
 
 								accessCounter++;
 
 								return value;
 							}
 						},
 						true
 				);
 			}
 		};
 	}
 
 	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 				dialect.getCreateTableString() + " " + tableName + " ( " + valueColumnName + " " + dialect.getTypeName( Types.BIGINT ) + " )",
 				"insert into " + tableName + " values ( " + initialValue + " )"
 		};
 	}
 
 	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index 323bfcf835..9209d8cbe7 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1346 +1,1345 @@
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
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
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
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
-import org.hibernate.persister.internal.PersisterFactoryImpl;
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
 import org.hibernate.stat.ConcurrentStatisticsImpl;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.EmptyIterator;
 import org.hibernate.util.ReflectHelper;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
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
 public final class SessionFactoryImpl
 		implements SessionFactory, SessionFactoryImplementor {
 
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
 				log.trace( "Building cache for collection data [" + model.getRole() + "]" );
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
 
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
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
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
-	public SQLExceptionHelper getSQLExceptionHelper() {
+	public SqlExceptionHelper getSQLExceptionHelper() {
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
 				if ( settings.isQueryCacheEnabled() ) {
 					QueryCache namedQueryCache = queryCaches.get( regionName );
 					if ( namedQueryCache != null ) {
 						namedQueryCache.clear();
 						// TODO : cleanup entries in queryCaches + allCacheRegions ?
 					}
 				}
 			}
 		}
 
 		public void evictQueryRegions() {
 			for ( QueryCache queryCache : queryCaches.values() ) {
 				queryCache.clear();
 				// TODO : cleanup entries in queryCaches + allCacheRegions ?
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
 				log.warn( "JTASessionContext being used with JdbcTransactionFactory; auto-flush will not operate correctly with getCurrentSession()" );
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
 				log.error( "Unable to construct current session context [" + impl + "]", t );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/package.html b/hibernate-core/src/main/java/org/hibernate/jdbc/package.html
index 8b9aeb146c..e8e3d0d377 100755
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/package.html
@@ -1,38 +1,33 @@
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
-	This package abstracts the mechanism for dispatching SQL statements 
-	to the database, and implements interaction with JDBC.
-</p>
-<p>
-	Concrete implementations of the <tt>Batcher</tt> interface may be 
-	selected by specifying <tt>hibernate.jdbc.factory_class</tt>.
+    Essentially defines {@link Work}, {@link ReturningWork} and {@link Expectation} as well as some exceptions
 </p>
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/util/SQLStatementLogger.java b/hibernate-core/src/main/java/org/hibernate/jdbc/util/SQLStatementLogger.java
deleted file mode 100644
index 5fea4adab6..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/util/SQLStatementLogger.java
+++ /dev/null
@@ -1,120 +0,0 @@
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
-package org.hibernate.jdbc.util;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
-/**
- * Centralize logging handling for SQL statements.
- *
- * @author Steve Ebersole
- */
-public class SQLStatementLogger {
-	// todo : for 4.0
-//	private static final Logger log = LoggerFactory.getLogger( SQLStatementLogger.class );
-	// this is the legacy logging 'category'...
-	private static final Logger log = LoggerFactory.getLogger( "org.hibernate.SQL" );
-
-	private boolean logToStdout;
-	private boolean formatSql;
-
-	/**
-	 * Constructs a new SQLStatementLogger instance.
-	 */
-	public SQLStatementLogger() {
-		this( false, false );
-	}
-
-	/**
-	 * Constructs a new SQLStatementLogger instance.
-	 *
-	 * @param logToStdout Should we log to STDOUT in addition to our internal logger.
-	 * @param formatSql Should we format SQL ('prettify') prior to logging.
-	 */
-	public SQLStatementLogger(boolean logToStdout, boolean formatSql) {
-		this.logToStdout = logToStdout;
-		this.formatSql = formatSql;
-	}
-
-	/**
-	 * Getter for property 'logToStdout'.
-	 * @see #setLogToStdout
-	 *
-	 * @return Value for property 'logToStdout'.
-	 */
-	public boolean isLogToStdout() {
-		return logToStdout;
-	}
-
-	/**
-	 * Setter for property 'logToStdout'.
-	 *
-	 * @param logToStdout Value to set for property 'logToStdout'.
-	 */
-	public void setLogToStdout(boolean logToStdout) {
-		this.logToStdout = logToStdout;
-	}
-
-	/**
-	 * Getter for property 'formatSql'.
-	 * @see #setFormatSql
-	 *
-	 * @return Value for property 'formatSql'.
-	 */
-	public boolean isFormatSql() {
-		return formatSql;
-	}
-
-	/**
-	 * Setter for property 'formatSql'.
-	 *
-	 * @param formatSql Value to set for property 'formatSql'.
-	 */
-	public void setFormatSql(boolean formatSql) {
-		this.formatSql = formatSql;
-	}
-
-	/**
-	 * Log a SQL statement string.
-	 *
-	 * @param statement The SQL statement.
-	 * @param style The requested formatting style.
-	 */
-	public void logStatement(String statement, FormatStyle style) {
-		if ( log.isDebugEnabled() || logToStdout ) {
-			style = determineActualStyle( style );
-			statement = style.getFormatter().format( statement );
-		}
-		log.debug( statement );
-		if ( logToStdout ) {
-			System.out.println( "Hibernate: " + statement );
-		}
-	}
-
-	private FormatStyle determineActualStyle(FormatStyle style) {
-		return formatSql ? style : FormatStyle.NONE;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Datatype.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Datatype.java
index 35ef2d0a59..3b86023612 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Datatype.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Datatype.java
@@ -1,91 +1,91 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.metamodel.relational;
 
 /**
  * Models a JDBC {@link java.sql.Types DATATYPE}
  *
- * @todo Do we somehow link this in with {@link org.hibernate.internal.util.jdbc.TypeInfo} ?
+ * @todo Do we somehow link this in with {@link org.hibernate.engine.jdbc.internal.TypeInfo} ?
  *
  * @author Steve Ebersole
  */
 public class Datatype {
 	private final int typeCode;
 	private final String typeName;
 	private final Class javaType;
 	private final int hashCode;
 
 	public Datatype(int typeCode, String typeName, Class javaType) {
 		this.typeCode = typeCode;
 		this.typeName = typeName;
 		this.javaType = javaType;
 		this.hashCode = generateHashCode();
 	}
 
 	private int generateHashCode() {
 		int result = typeCode;
 		result = 31 * result + typeName.hashCode();
 		result = 31 * result + javaType.hashCode();
 		return result;
 	}
 
 	public int getTypeCode() {
 		return typeCode;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public Class getJavaType() {
 		return javaType;
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
 
 		Datatype datatype = (Datatype) o;
 
 		return typeCode == datatype.typeCode
 				&& javaType.equals( datatype.javaType )
 				&& typeName.equals( datatype.typeName );
 
 	}
 
 	@Override
 	public int hashCode() {
 		return hashCode;
 	}
 
 	@Override
 	public String toString() {
 		return super.toString() + "[code=" + typeCode + ", name=" + typeName + ", javaClass=" + javaType.getName() + "]";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 9136e97486..f22134c0cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1893 +1,1893 @@
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
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
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
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 
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
-	private final SQLExceptionHelper sqlExceptionHelper;
+	private final SqlExceptionHelper sqlExceptionHelper;
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
 
 	private BasicBatchKey removeBatchKey;
 
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
 					if ( removeBatchKey == null ) {
 						removeBatchKey = new BasicBatchKey(
 								getRole() + "#REMOVE",
 								expectation
 						);
 					}
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 
 				try {
 					offset+= expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
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
 
 	private BasicBatchKey recreateBatchKey;
 
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
 					Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 					collection.preInsert( this );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isInsertCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLInsertRowString();
 
 							if ( useBatch ) {
 								if ( recreateBatchKey == null ) {
 									recreateBatchKey = new BasicBatchKey(
 											getRole() + "#RECREATE",
 											expectation
 									);
 								}
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
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
 									session.getTransactionCoordinator()
 											.getJdbcCoordinator()
 											.getBatch( recreateBatchKey )
 											.addToBatch();
 								}
 								else {
 									expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 								}
 
 								collection.afterRowInsert( this, entry, i );
 								count++;
 							}
 							catch ( SQLException sqle ) {
 								if ( useBatch ) {
 									session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
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
 
 	private BasicBatchKey deleteBatchKey;
 
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
 			final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 			try {
 				//delete all the deleted entries
 				Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 				if ( deletes.hasNext() ) {
 					int offset = 1;
 					int count = 0;
 					while ( deletes.hasNext() ) {
 						PreparedStatement st = null;
 						boolean callable = isDeleteCallable();
 						boolean useBatch = expectation.canBeBatched();
 						String sql = getSQLDeleteRowString();
 
 						if ( useBatch ) {
 							if ( deleteBatchKey == null ) {
 								deleteBatchKey = new BasicBatchKey(
 										getRole() + "#DELETE",
 										expectation
 								);
 							}
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
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
 								session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
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
 
 	private BasicBatchKey insertBatchKey;
 
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
 							if ( insertBatchKey == null ) {
 								insertBatchKey = new BasicBatchKey(
 										getRole() + "#INSERT",
 										expectation
 								);
 							}
 							if ( st == null ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( insertBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
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
 								session.getTransactionCoordinator().getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
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
-	protected SQLExceptionHelper getSQLExceptionHelper() {
+	protected SqlExceptionHelper getSQLExceptionHelper() {
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
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
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
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
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
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index 4916f13734..f84de0bf11 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,542 +1,542 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.jdbc.util.FormatStyle;
-import org.hibernate.jdbc.util.Formatter;
+import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.util.ConfigHelper;
 import org.hibernate.util.JDBCExceptionReporter;
 import org.hibernate.util.ReflectHelper;
 
 /**
  * Commandline tool to export table schema to the database. This class may also be called from inside an application.
  *
  * @author Daniel Bradby
  * @author Gavin King
  */
 public class SchemaExport {
 
 	private static final Logger log = LoggerFactory.getLogger( SchemaExport.class );
 
 	private ConnectionHelper connectionHelper;
 	private String[] dropSQL;
 	private String[] createSQL;
 	private String outputFile = null;
 	private String importFiles;
 	private Dialect dialect;
 	private String delimiter;
 	private final List exceptions = new ArrayList();
 	private boolean haltOnError = false;
 	private Formatter formatter;
-	private SQLStatementLogger sqlStatementLogger;
+	private SqlStatementLogger sqlStatementLogger;
 	private static final String DEFAULT_IMPORT_FILE = "/import.sql";
 
 	/**
 	 * Create a schema exporter for the given Configuration
 	 *
 	 * @param cfg The configuration from which to build a schema export.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration and given settings
 	 *
 	 * @param cfg The configuration from which to build a schema export.
 	 * @param settings The 'parsed' settings.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(JdbcServices jdbcServices, Configuration cfg) throws HibernateException {
 		dialect = jdbcServices.getDialect();
 		connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 		dropSQL = cfg.generateDropSchemaScript( dialect );
 		createSQL = cfg.generateSchemaCreationScript( dialect );
 		sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES, cfg.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, with the given
 	 * database connection properties.
 	 *
 	 * @param cfg The configuration from which to build a schema export.
 	 * @param properties The properties from which to configure connectivity etc.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 *
 	 * @deprecated properties may be specified via the Configuration object
 	 */
 	public SchemaExport(Configuration cfg, Properties properties) throws HibernateException {
 		dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 		dropSQL = cfg.generateDropSchemaScript( dialect );
 		createSQL = cfg.generateSchemaCreationScript( dialect );
 
 		formatter = ( ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, props ) ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 
 		importFiles = ConfigurationHelper.getString( Environment.HBM2DDL_IMPORT_FILES, props, DEFAULT_IMPORT_FILE );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, using the supplied connection for connectivity.
 	 *
 	 * @param cfg The configuration to use.
 	 * @param connection The JDBC connection to use.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration cfg, Connection connection) throws HibernateException {
 		this.connectionHelper = new SuppliedConnectionHelper( connection );
 		dialect = Dialect.getDialect( cfg.getProperties() );
 		dropSQL = cfg.generateDropSchemaScript( dialect );
 		createSQL = cfg.generateSchemaCreationScript( dialect );
 		formatter = ( ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, cfg.getProperties() ) ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		importFiles = ConfigurationHelper.getString( Environment.HBM2DDL_IMPORT_FILES, cfg.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 	}
 
 	/**
 	 * For generating a export script file, this is the file which will be written.
 	 *
 	 * @param filename The name of the file to which to write the export script.
 	 * @return this
 	 */
 	public SchemaExport setOutputFile(String filename) {
 		outputFile = filename;
 		return this;
 	}
 
 	/**
 	 * An import file, containing raw SQL statements to be executed.
 	 *
 	 * @param filename The import file name.
 	 * @return this
 	 * @deprecated use {@link org.hibernate.cfg.Environment.HBM2DDL_IMPORT_FILE}
 	 */
 	public SchemaExport setImportFile(String filename) {
 		importFiles = filename;
 		return this;
 	}
 
 	/**
 	 * Set the end of statement delimiter
 	 *
 	 * @param delimiter The delimiter
 	 * @return this
 	 */
 	public SchemaExport setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 		return this;
 	}
 
 	/**
 	 * Should we format the sql strings?
 	 *
 	 * @param format Should we format SQL strings
 	 * @return this
 	 */
 	public SchemaExport setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		return this;
 	}
 
 	/**
 	 * Should we stop once an error occurs?
 	 *
 	 * @param haltOnError True if export should stop after error.
 	 * @return this
 	 */
 	public SchemaExport setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 		return this;
 	}
 
 	/**
 	 * Run the schema creation script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void create(boolean script, boolean export) {
 		execute( script, export, false, false );
 	}
 
 	/**
 	 * Run the drop schema script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void drop(boolean script, boolean export) {
 		execute( script, export, true, false );
 	}
 
 	public void execute(boolean script, boolean export, boolean justDrop, boolean justCreate) {
 
 		log.info( "Running hbm2ddl schema export" );
 
 		Connection connection = null;
 		Writer outputFileWriter = null;
 		List<NamedReader> importFileReaders = new ArrayList<NamedReader>();
 		Statement statement = null;
 
 		exceptions.clear();
 
 		try {
 
 			for ( String currentFile : importFiles.split(",") ) {
 				try {
 					final String resourceName = currentFile.trim();
 					InputStream stream = ConfigHelper.getResourceAsStream( resourceName );
 					importFileReaders.add( new NamedReader( resourceName, stream ) );
 				}
 				catch ( HibernateException e ) {
 					log.debug( "import file not found: " + currentFile );
 				}
 			}
 
 			if ( outputFile != null ) {
 				log.info( "writing generated schema to file: " + outputFile );
 				outputFileWriter = new FileWriter( outputFile );
 			}
 
 			if ( export ) {
 				log.info( "exporting generated schema to database" );
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				statement = connection.createStatement();
 			}
 
 			if ( !justCreate ) {
 				drop( script, export, outputFileWriter, statement );
 			}
 
 			if ( !justDrop ) {
 				create( script, export, outputFileWriter, statement );
 				if ( export && importFileReaders.size() > 0 ) {
 					for (NamedReader reader : importFileReaders) {
 						importScript( reader, statement );
 					}
 				}
 			}
 
 			log.info( "schema export complete" );
 
 		}
 
 		catch ( Exception e ) {
 			exceptions.add( e );
 			log.error( "schema export unsuccessful", e );
 		}
 
 		finally {
 
 			try {
 				if ( statement != null ) {
 					statement.close();
 				}
 				if ( connection != null ) {
 					connectionHelper.release();
 				}
 			}
 			catch ( Exception e ) {
 				exceptions.add( e );
 				log.error( "Could not close connection", e );
 			}
 
 			try {
 				if ( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch ( IOException ioe ) {
 				exceptions.add( ioe );
 				log.error( "Error closing output file: " + outputFile, ioe );
 			}
 				for (NamedReader reader : importFileReaders) {
 					try {
 						reader.getReader().close();
 					}
 					catch ( IOException ioe ) {
 						exceptions.add( ioe );
 						log.error( "Error closing imput files: " + reader.getName(), ioe );
 					}
 				}
 
 
 		}
 	}
 
 	private class NamedReader {
 		private final Reader reader;
 		private final String name;
 
 		public NamedReader(String name, InputStream stream) {
 			this.name = name;
 			this.reader = new InputStreamReader( stream );
 		}
 
 		public Reader getReader() {
 			return reader;
 		}
 
 		public String getName() {
 			return name;
 		}
 	}
 
 	private void importScript(NamedReader importFileReader, Statement statement)
 			throws IOException {
 		log.info( "Executing import script: " + importFileReader.getName() );
 		BufferedReader reader = new BufferedReader( importFileReader.getReader() );
 		long lineNo = 0;
 		for ( String sql = reader.readLine(); sql != null; sql = reader.readLine() ) {
 			try {
 				lineNo++;
 				String trimmedSql = sql.trim();
 				if ( trimmedSql.length() == 0 ||
 				     trimmedSql.startsWith( "--" ) ||
 				     trimmedSql.startsWith( "//" ) ||
 				     trimmedSql.startsWith( "/*" ) ) {
 					continue;
 				}
 				else {
 					if ( trimmedSql.endsWith( ";" ) ) {
 						trimmedSql = trimmedSql.substring( 0, trimmedSql.length() - 1 );
 					}
 					log.debug( trimmedSql );
 					statement.execute( trimmedSql );
 				}
 			}
 			catch ( SQLException e ) {
 				throw new JDBCException( "Error during import script execution at line " + lineNo, e );
 			}
 		}
 	}
 
 	private void create(boolean script, boolean export, Writer fileOutput, Statement statement)
 			throws IOException {
 		for ( int j = 0; j < createSQL.length; j++ ) {
 			try {
 				execute( script, export, fileOutput, statement, createSQL[j] );
 			}
 			catch ( SQLException e ) {
 				if ( haltOnError ) {
 					throw new JDBCException( "Error during DDL export", e );
 				}
 				exceptions.add( e );
 				log.error( "Unsuccessful: " + createSQL[j] );
 				log.error( e.getMessage() );
 			}
 		}
 	}
 
 	private void drop(boolean script, boolean export, Writer fileOutput, Statement statement)
 			throws IOException {
 		for ( int i = 0; i < dropSQL.length; i++ ) {
 			try {
 				execute( script, export, fileOutput, statement, dropSQL[i] );
 			}
 			catch ( SQLException e ) {
 				exceptions.add( e );
 				log.debug( "Unsuccessful: " + dropSQL[i] );
 				log.debug( e.getMessage() );
 			}
 		}
 	}
 
 	private void execute(boolean script, boolean export, Writer fileOutput, Statement statement, final String sql)
 			throws IOException, SQLException {
 		String formatted = formatter.format( sql );
 		if ( delimiter != null ) {
 			formatted += delimiter;
 		}
 		if ( script ) {
 			System.out.println( formatted );
 		}
 		log.debug( formatted );
 		if ( outputFile != null ) {
 			fileOutput.write( formatted + "\n" );
 		}
 		if ( export ) {
 
 			statement.executeUpdate( sql );
 			try {
 				SQLWarning warnings = statement.getWarnings();
 				if ( warnings != null) {
 					JDBCExceptionReporter.logAndClearWarnings( connectionHelper.getConnection() );
 				}
 			}
 			catch( SQLException sqle ) {
 				log.warn( "unable to log SQLWarnings : " + sqle );
 			}
 		}
 
 		
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			boolean drop = false;
 			boolean create = false;
 			boolean halt = false;
 			boolean export = true;
 			String outFile = null;
 			String importFile = DEFAULT_IMPORT_FILE;
 			String propFile = null;
 			boolean format = false;
 			String delim = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].equals( "--drop" ) ) {
 						drop = true;
 					}
 					else if ( args[i].equals( "--create" ) ) {
 						create = true;
 					}
 					else if ( args[i].equals( "--haltonerror" ) ) {
 						halt = true;
 					}
 					else if ( args[i].equals( "--text" ) ) {
 						export = false;
 					}
 					else if ( args[i].startsWith( "--output=" ) ) {
 						outFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--import=" ) ) {
 						importFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].equals( "--format" ) ) {
 						format = true;
 					}
 					else if ( args[i].startsWith( "--delimiter=" ) ) {
 						delim = args[i].substring( 12 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) )
 										.newInstance()
 						);
 					}
 				}
 				else {
 					String filename = args[i];
 					if ( filename.endsWith( ".jar" ) ) {
 						cfg.addJar( new File( filename ) );
 					}
 					else {
 						cfg.addFile( filename );
 					}
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			if (importFile != null) {
 				cfg.setProperty( Environment.HBM2DDL_IMPORT_FILES, importFile );
 			}
 			SchemaExport se = new SchemaExport( cfg )
 					.setHaltOnError( halt )
 					.setOutputFile( outFile )
 					.setDelimiter( delim );
 			if ( format ) {
 				se.setFormat( true );
 			}
 			se.execute( script, export, drop, create );
 
 		}
 		catch ( Exception e ) {
 			log.error( "Error creating schema ", e );
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
index 09bf4db55c..94f480469e 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
@@ -1,274 +1,273 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.internal.FormatStyle;
+import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.jdbc.util.FormatStyle;
-import org.hibernate.jdbc.util.Formatter;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.util.ReflectHelper;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * A commandline tool to update a database schema. May also be called from
  * inside an application.
  *
  * @author Christoph Sturm
  */
 public class SchemaUpdate {
 
 	private static final Logger log = LoggerFactory.getLogger( SchemaUpdate.class );
 	private ConnectionHelper connectionHelper;
 	private Configuration configuration;
 	private Dialect dialect;
 	private List exceptions;
 	private boolean haltOnError = false;
 	private boolean format = true;
 	private String outputFile = null;
 	private String delimiter;
 	private Formatter formatter;
-	private SQLStatementLogger sqlStatementLogger;
+	private SqlStatementLogger sqlStatementLogger;
 
 	public SchemaUpdate(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaUpdate(Configuration cfg, Properties connectionProperties) throws HibernateException {
 		this.configuration = cfg;
 		dialect = Dialect.getDialect( connectionProperties );
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( connectionProperties );
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 		exceptions = new ArrayList();
 		formatter = ( ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, props ) ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public SchemaUpdate(JdbcServices jdbcServices, Configuration cfg) throws HibernateException {
 		this.configuration = cfg;
 		dialect = jdbcServices.getDialect();
 		connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				jdbcServices.getConnectionProvider()
 		);
 		exceptions = new ArrayList();
 		sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			// If true then execute db updates, otherwise just generate and display updates
 			boolean doUpdate = true;
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--text" ) ) {
 						doUpdate = false;
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			new SchemaUpdate( cfg ).execute( script, doUpdate );
 		}
 		catch ( Exception e ) {
 			log.error( "Error running schema update", e );
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Execute the schema updates
 	 *
 	 * @param script print all DDL to the console
 	 */
 	public void execute(boolean script, boolean doUpdate) {
 
 		log.info( "Running hbm2ddl schema update" );
 
 		Connection connection = null;
 		Statement stmt = null;
 		Writer outputFileWriter = null;
 
 		exceptions.clear();
 
 		try {
 
 			DatabaseMetadata meta;
 			try {
 				log.info( "fetching database metadata" );
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect );
 				stmt = connection.createStatement();
 			}
 			catch ( SQLException sqle ) {
 				exceptions.add( sqle );
 				log.error( "could not get database metadata", sqle );
 				throw sqle;
 			}
 
 			log.info( "updating schema" );
 
 			
 			if ( outputFile != null ) {
 				log.info( "writing generated schema to file: " + outputFile );
 				outputFileWriter = new FileWriter( outputFile );
 			}
 			 
 			String[] createSQL = configuration.generateSchemaUpdateScript( dialect, meta );
 			for ( int j = 0; j < createSQL.length; j++ ) {
 
 				final String sql = createSQL[j];
 				String formatted = formatter.format( sql );
 				try {
 					if ( delimiter != null ) {
 						formatted += delimiter;
 					}
 					if ( script ) {
 						System.out.println( formatted );
 					}
 					if ( outputFile != null ) {
 						outputFileWriter.write( formatted + "\n" );
 					}
 					if ( doUpdate ) {
 						log.debug( sql );
 						stmt.executeUpdate( formatted );
 					}
 				}
 				catch ( SQLException e ) {
 					if ( haltOnError ) {
 						throw new JDBCException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
 					log.error( "Unsuccessful: " + sql );
 					log.error( e.getMessage() );
 				}
 			}
 
 			log.info( "schema update complete" );
 
 		}
 		catch ( Exception e ) {
 			exceptions.add( e );
 			log.error( "could not complete schema update", e );
 		}
 		finally {
 
 			try {
 				if ( stmt != null ) {
 					stmt.close();
 				}
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
 				exceptions.add( e );
 				log.error( "Error closing connection", e );
 			}
 			try {
 				if( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch(Exception e) {
 				exceptions.add(e);
 				log.error( "Error closing connection", e );
 			}
 		}
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 
 	public void setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 	}
 
 	public void setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public void setOutputFile(String outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	public void setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java b/hibernate-core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java
index 15f86ecea3..4bb27efd00 100644
--- a/hibernate-core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/jdbc/util/BasicFormatterTest.java
@@ -1,69 +1,71 @@
 /*
  * Copyright (c) 2007, Red Hat Middleware, LLC. All rights reserved.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, v. 2.1. This program is distributed in the
  * hope that it will be useful, but WITHOUT A WARRANTY; without even the implied
  * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details. You should have received a
  * copy of the GNU Lesser General Public License, v.2.1 along with this
  * distribution; if not, write to the Free Software Foundation, Inc.,
  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
  *
  * Red Hat Author(s): Steve Ebersole
  */
 package org.hibernate.jdbc.util;
 
 import java.util.StringTokenizer;
 
 import junit.framework.TestCase;
 
+import org.hibernate.engine.jdbc.internal.FormatStyle;
+
 /**
  * BasicFormatterTest implementation
  *
  * @author Steve Ebersole
  */
 public class BasicFormatterTest extends TestCase {
 	public BasicFormatterTest(String name) {
 		super( name );
 	}
 
 	public void testNoLoss() {
 		assertNoLoss( "insert into Address (city, state, zip, \"from\") values (?, ?, ?, 'insert value')" );
 		assertNoLoss( "delete from Address where id = ? and version = ?" );
 		assertNoLoss( "update Address set city = ?, state=?, zip=?, version = ? where id = ? and version = ?" );
 		assertNoLoss( "update Address set city = ?, state=?, zip=?, version = ? where id in (select aid from Person)" );
 		assertNoLoss(
 				"select p.name, a.zipCode, count(*) from Person p left outer join Employee e on e.id = p.id and p.type = 'E' and (e.effective>? or e.effective<?) join Address a on a.pid = p.id where upper(p.name) like 'G%' and p.age > 100 and (p.sex = 'M' or p.sex = 'F') and coalesce( trim(a.street), a.city, (a.zip) ) is not null order by p.name asc, a.zipCode asc"
 		);
 		assertNoLoss(
 				"select ( (m.age - p.age) * 12 ), trim(upper(p.name)) from Person p, Person m where p.mother = m.id and ( p.age = (select max(p0.age) from Person p0 where (p0.mother=m.id)) and p.name like ? )"
 		);
 		assertNoLoss(
 				"select * from Address a join Person p on a.pid = p.id, Person m join Address b on b.pid = m.id where p.mother = m.id and p.name like ?"
 		);
 		assertNoLoss(
 				"select case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end from Person p where ( case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end ) like ?"
 		);
 		assertNoLoss(
 				"/* Here we' go! */ select case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end from Person p where ( case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end ) like ?"
 		);
 	}
 
 	private void assertNoLoss(String query) {
 		String formattedQuery = FormatStyle.BASIC.getFormatter().format( query );
 		StringTokenizer formatted = new StringTokenizer( formattedQuery, " \t\n\r\f()" );
 		StringTokenizer plain = new StringTokenizer( query, " \t\n\r\f()" );
 
 		System.out.println( "Original: " + query );
 		System.out.println( "Formatted: " + formattedQuery );
 		while ( formatted.hasMoreTokens() && plain.hasMoreTokens() ) {
 			String plainToken = plain.nextToken();
 			String formattedToken = formatted.nextToken();
 			assertEquals( "formatter did not return the same token", plainToken, formattedToken );
 		}
 		assertFalse( formatted.hasMoreTokens() );
 		assertFalse( plain.hasMoreTokens() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
index b4c21660f5..011a0bdd8c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
@@ -1,145 +1,145 @@
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
 package org.hibernate.test.common;
 
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.JdbcSupport;
+import org.hibernate.engine.jdbc.internal.TypeInfo;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
-import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
-import org.hibernate.engine.jdbc.spi.SQLStatementLogger;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.internal.util.jdbc.TypeInfo;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Stoppable;
 
 
 /**
  * Implementation of the {@link JdbcServices} contract for use by these
  * tests.
  *
  * @author Steve Ebersole
  */
 public class BasicTestingJdbcServiceImpl implements JdbcServices {
 	private ConnectionProvider connectionProvider;
 	private Dialect dialect;
-	private SQLStatementLogger sqlStatementLogger;
-	private SQLExceptionHelper exceptionHelper;
+	private SqlStatementLogger sqlStatementLogger;
+	private SqlExceptionHelper exceptionHelper;
 	private final ExtractedDatabaseMetaData metaDataSupport = new MetaDataSupportImpl();
 
 
 	public void start() {
 	}
 
 	public void stop() {
 		release();
 	}
 
 	public void prepare(boolean allowAggressiveRelease) {
 		connectionProvider = ConnectionProviderBuilder.buildConnectionProvider( allowAggressiveRelease );
 		dialect = ConnectionProviderBuilder.getCorrespondingDialect();
-		sqlStatementLogger = new SQLStatementLogger( true, false );
-		exceptionHelper = new SQLExceptionHelper();
+		sqlStatementLogger = new SqlStatementLogger( true, false );
+		exceptionHelper = new SqlExceptionHelper();
 
 	}
 
 	public void release() {
 		if ( connectionProvider instanceof Stoppable ) {
 			( (Stoppable) connectionProvider ).stop();
 		}
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	public JdbcSupport getJdbcSupport() {
 		return null;
 	}
 
-	public SQLStatementLogger getSqlStatementLogger() {
+	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
-	public SQLExceptionHelper getSqlExceptionHelper() {
+	public SqlExceptionHelper getSqlExceptionHelper() {
 		return exceptionHelper;
 	}
 
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return metaDataSupport;
 	}
 
 	private static class MetaDataSupportImpl implements ExtractedDatabaseMetaData {
 		public boolean supportsScrollableResults() {
 			return false;
 		}
 
 		public boolean supportsGetGeneratedKeys() {
 			return false;
 		}
 
 		public boolean supportsBatchUpdates() {
 			return false;
 		}
 
 		public boolean supportsDataDefinitionInTransaction() {
 			return false;
 		}
 
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return false;
 		}
 
 		public Set<String> getExtraKeywords() {
 			return Collections.emptySet();
 		}
 
 		public SQLStateType getSqlStateType() {
 			return SQLStateType.UNKOWN;
 		}
 
 		public boolean doesLobLocatorUpdateCopy() {
 			return false;
 		}
 
 		public String getConnectionSchemaName() {
 			return null;
 		}
 
 		public String getConnectionCatalogName() {
 			return null;
 		}
 
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/TypeInfoTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/TypeInfoTest.java
index d93f53b602..417092cfc5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/TypeInfoTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/TypeInfoTest.java
@@ -1,70 +1,70 @@
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
 package org.hibernate.test.jdbc;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.LinkedHashSet;
 
-import org.hibernate.internal.util.jdbc.TypeInfo;
-import org.hibernate.internal.util.jdbc.TypeInfoExtracter;
+import org.hibernate.engine.jdbc.internal.TypeInfo;
+import org.hibernate.engine.jdbc.internal.TypeInfoExtracter;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.test.common.ConnectionProviderBuilder;
 import org.hibernate.testing.junit.UnitTestCase;
 import org.hibernate.util.ArrayHelper;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class TypeInfoTest extends UnitTestCase {
 
 	public TypeInfoTest(String string) {
 		super( string );
 	}
 
 	public void testExtractTypeInfo() throws SQLException {
 		ConnectionProvider connectionProvider = ConnectionProviderBuilder.buildConnectionProvider();
 		Connection connection = connectionProvider.getConnection();
 		LinkedHashSet<TypeInfo> typeInfoSet = TypeInfoExtracter.extractTypeInfo( connection.getMetaData() );
 		for ( TypeInfo typeInfo : typeInfoSet ) {
 			System.out.println( "~~~~~~ TYPE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
 			System.out.println( "             type name : " + typeInfo.getTypeName() );
 			System.out.println( "             data type : " + typeInfo.getJdbcTypeCode() );
 			System.out.println( "         create params : " + ArrayHelper.toString( typeInfo.getCreateParams() ) );
 			System.out.println( "              unsigned : " + typeInfo.isUnsigned() );
 			System.out.println( "             precision : " + typeInfo.getPrecision() );
 			System.out.println( "         minimum scale : " + typeInfo.getMinimumScale() );
 			System.out.println( "         maximum scale : " + typeInfo.getMaximumScale() );
 			System.out.println( " fixed-precision scale : " + typeInfo.isFixedPrecisionScale() );
 			System.out.println( "        literal prefix : " + typeInfo.getLiteralPrefix() );
 			System.out.println( "        literal suffix : " + typeInfo.getLiteralSuffix() );
 			System.out.println( "        case sensitive : " + typeInfo.isCaseSensitive() );
 			System.out.println( "            searchable : " + typeInfo.getSearchability().toString() );
 			System.out.println( "            nulability : " + typeInfo.getNullability().toString() );
 			System.out.println( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
 		}
 	}
 }
