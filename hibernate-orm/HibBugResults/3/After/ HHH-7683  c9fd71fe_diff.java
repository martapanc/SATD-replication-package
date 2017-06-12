diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 2529b656c1..0f007f713c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,461 +1,471 @@
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
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Settings that affect the behaviour of Hibernate at runtime.
  *
  * @author Gavin King
  */
 public final class Settings {
 
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
 	private boolean sessionFactoryNameAlsoJndiName;
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
+	private boolean initializeLazyStateOutsideTransactions;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 //	private BytecodeProvider bytecodeProvider;
 	private String importFiles;
 	private MultiTenancyStrategy multiTenancyStrategy;
 
 	private JtaPlatform jtaPlatform;
 
 	/**
 	 * Package protected constructor
 	 */
 	Settings() {
 	}
 
 	// public getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getImportFiles() {
 		return importFiles;
 	}
 
 	public void setImportFiles(String importFiles) {
 		this.importFiles = importFiles;
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
 
 	public String getSessionFactoryName() {
 		return sessionFactoryName;
 	}
 
 	public boolean isSessionFactoryNameAlsoJndiName() {
 		return sessionFactoryNameAlsoJndiName;
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
 
+
 	// package protected setters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
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
 
 	void setSessionFactoryNameAlsoJndiName(boolean sessionFactoryNameAlsoJndiName) {
 		this.sessionFactoryNameAlsoJndiName = sessionFactoryNameAlsoJndiName;
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
 
 	public MultiTenancyStrategy getMultiTenancyStrategy() {
 		return multiTenancyStrategy;
 	}
 
 	void setMultiTenancyStrategy(MultiTenancyStrategy multiTenancyStrategy) {
 		this.multiTenancyStrategy = multiTenancyStrategy;
 	}
+
+	public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
+		return initializeLazyStateOutsideTransactions;
+	}
+
+	void setInitializeLazyStateOutsideTransactions(boolean initializeLazyStateOutsideTransactions) {
+		this.initializeLazyStateOutsideTransactions = initializeLazyStateOutsideTransactions;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 0eedef4f7e..b578c18036 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,460 +1,470 @@
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
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
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
 
-		String sessionFactoryName = props.getProperty( Environment.SESSION_FACTORY_NAME );
+		String sessionFactoryName = props.getProperty( AvailableSettings.SESSION_FACTORY_NAME );
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
 
-		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
+		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(AvailableSettings.FLUSH_BEFORE_COMPLETION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		}
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
-		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
+		boolean autoCloseSession = ConfigurationHelper.getBoolean(AvailableSettings.AUTO_CLOSE_SESSION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		}
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
-		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
+		int batchSize = ConfigurationHelper.getInt(AvailableSettings.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
-		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
+		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(AvailableSettings.BATCH_VERSIONED_DATA, properties, false);
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
-				Environment.USE_SCROLLABLE_RESULTSET,
+				AvailableSettings.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		}
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
-		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
+		boolean wrapResultSets = ConfigurationHelper.getBoolean(AvailableSettings.WRAP_RESULT_SETS, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		}
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
-		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
+		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(AvailableSettings.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		if ( debugEnabled ) {
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		}
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
-		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
+		Integer statementFetchSize = ConfigurationHelper.getInteger(AvailableSettings.STATEMENT_FETCH_SIZE, properties);
 		if ( statementFetchSize != null && debugEnabled ) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		}
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
-		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
+		String releaseModeName = ConfigurationHelper.getString( AvailableSettings.RELEASE_CONNECTIONS, properties, "auto" );
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
 
 		//SQL Generation settings:
 
-		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
-		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
+		String defaultSchema = properties.getProperty( AvailableSettings.DEFAULT_SCHEMA );
+		String defaultCatalog = properties.getProperty( AvailableSettings.DEFAULT_CATALOG );
 		if ( defaultSchema != null && debugEnabled ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
 		if ( defaultCatalog != null && debugEnabled ) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
-		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
+		Integer maxFetchDepth = ConfigurationHelper.getInteger( AvailableSettings.MAX_FETCH_DEPTH, properties );
 		if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
-		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
+		int batchFetchSize = ConfigurationHelper.getInt(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		}
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
-		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
+		boolean comments = ConfigurationHelper.getBoolean( AvailableSettings.USE_SQL_COMMENTS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		}
 		settings.setCommentsEnabled( comments );
 
-		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
+		boolean orderUpdates = ConfigurationHelper.getBoolean( AvailableSettings.ORDER_UPDATES, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		}
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
-		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
+		boolean orderInserts = ConfigurationHelper.getBoolean(AvailableSettings.ORDER_INSERTS, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		}
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
-		Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
+		Map querySubstitutions = ConfigurationHelper.toMap( AvailableSettings.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		}
 		settings.setQuerySubstitutions( querySubstitutions );
 
-		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
+		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( AvailableSettings.JPAQL_STRICT_COMPLIANCE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		}
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
-		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
+		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( AvailableSettings.USE_SECOND_LEVEL_CACHE, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		}
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
-		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
+		boolean useQueryCache = ConfigurationHelper.getBoolean(AvailableSettings.USE_QUERY_CACHE, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		}
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
-				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
+				AvailableSettings.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		}
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
-		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
+		String prefix = properties.getProperty( AvailableSettings.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
 		if ( prefix != null && debugEnabled ) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
-		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
+		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( AvailableSettings.USE_STRUCTURED_CACHE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		}
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
-		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
+		boolean useStatistics = ConfigurationHelper.getBoolean( AvailableSettings.GENERATE_STATISTICS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		}
 		settings.setStatisticsEnabled( useStatistics );
 
-		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
+		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( AvailableSettings.USE_IDENTIFIER_ROLLBACK, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		}
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
-		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
+		String autoSchemaExport = properties.getProperty( AvailableSettings.HBM2DDL_AUTO );
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
-		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
+		settings.setImportFiles( properties.getProperty( AvailableSettings.HBM2DDL_IMPORT_FILES ) );
 
-		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
+		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( AvailableSettings.DEFAULT_ENTITY_MODE ) );
 		if ( debugEnabled ) {
 			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		}
 		settings.setDefaultEntityMode( defaultEntityMode );
 
-		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
+		boolean namedQueryChecking = ConfigurationHelper.getBoolean( AvailableSettings.QUERY_STARTUP_CHECKING, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		}
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
-		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
+		boolean checkNullability = ConfigurationHelper.getBoolean(AvailableSettings.CHECK_NULLABILITY, properties, true);
 		if ( debugEnabled ) {
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		}
 		settings.setCheckNullability(checkNullability);
 
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
-//		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
+//		String provider = properties.getProperty( AvailableSettings.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
+		boolean initializeLazyStateOutsideTransactionsEnabled = ConfigurationHelper.getBoolean(
+				AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS,
+				properties,
+				false
+		);
+		if ( debugEnabled ) {
+			LOG.debugf( "Allow initialization of lazy state outside session : : %s", enabledDisabled( initializeLazyStateOutsideTransactionsEnabled ) );
+		}
+		settings.setInitializeLazyStateOutsideTransactions( initializeLazyStateOutsideTransactionsEnabled );
+
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
-				Environment.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
+				AvailableSettings.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
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
 
 	private static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
-						Environment.CACHE_REGION_FACTORY, properties, null
+						AvailableSettings.CACHE_REGION_FACTORY, properties, null
 				)
 		);
 		if ( regionFactoryClassName == null || !cachingEnabled) {
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
 	//todo remove this once we move to new metamodel
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
-						Environment.CACHE_REGION_FACTORY, properties, null
+						AvailableSettings.CACHE_REGION_FACTORY, properties, null
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
-				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
+				AvailableSettings.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
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
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
index 983f7929be..c3034c5832 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
@@ -1,1157 +1,1152 @@
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
 package org.hibernate.collection.internal;
 
 import javax.naming.NamingException;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.Session;
-import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.SessionFactoryRegistry;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * Base class implementing {@link org.hibernate.collection.spi.PersistentCollection}
  *
  * @author Gavin King
  */
 public abstract class AbstractPersistentCollection implements Serializable, PersistentCollection {
 	private static final Logger log = Logger.getLogger( AbstractPersistentCollection.class );
 
 	private transient SessionImplementor session;
 	private boolean initialized;
 	private transient List<DelayedOperation> operationQueue;
 	private transient boolean directlyAccessible;
 	private transient boolean initializing;
 	private Object owner;
 	private int cachedSize = -1;
 
 	private String role;
 	private Serializable key;
 	// collections detect changes made via their public interface and mark
 	// themselves as dirty as a performance optimization
 	private boolean dirty;
 	private Serializable storedSnapshot;
 
 	private String sessionFactoryUuid;
 	private boolean specjLazyLoad = false;
 
 	public final String getRole() {
 		return role;
 	}
 
 	public final Serializable getKey() {
 		return key;
 	}
 
 	public final boolean isUnreferenced() {
 		return role == null;
 	}
 
 	public final boolean isDirty() {
 		return dirty;
 	}
 
 	public final void clearDirty() {
 		dirty = false;
 	}
 
 	public final void dirty() {
 		dirty = true;
 	}
 
 	public final Serializable getStoredSnapshot() {
 		return storedSnapshot;
 	}
 
 	//Careful: these methods do not initialize the collection.
 
 	/**
 	 * Is the initialized collection empty?
 	 */
 	public abstract boolean empty();
 
 	/**
 	 * Called by any read-only method of the collection interface
 	 */
 	protected final void read() {
 		initialize( false );
 	}
 
 	/**
 	 * Called by the {@link Collection#size} method
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean readSize() {
 		if ( !initialized ) {
 			if ( cachedSize != -1 && !hasQueuedOperations() ) {
 				return true;
 			}
 			else {
 				boolean isExtraLazy = withTemporarySessionIfNeeded(
 						new LazyInitializationWork<Boolean>() {
 							@Override
 							public Boolean doWork() {
 								CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 
 								if ( entry != null ) {
 									CollectionPersister persister = entry.getLoadedPersister();
 									if ( persister.isExtraLazy() ) {
 										if ( hasQueuedOperations() ) {
 											session.flush();
 										}
 										cachedSize = persister.getSize( entry.getLoadedKey(), session );
 										return true;
 									}
 									else {
 										read();
 									}
 								}
 								else{
 									throwLazyInitializationExceptionIfNotConnected();
 								}
 								return false;
 							}
 						}
 				);
 				if ( isExtraLazy ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public static interface LazyInitializationWork<T> {
 		public T doWork();
 	}
 
 	private <T> T withTemporarySessionIfNeeded(LazyInitializationWork<T> lazyInitializationWork) {
 		SessionImplementor originalSession = null;
 		boolean isTempSession = false;
 
 		if ( session == null ) {
 			if ( specjLazyLoad ) {
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throw new LazyInitializationException( "could not initialize proxy - no Session" );
 			}
 		}
 		else if ( !session.isOpen() ) {
 			if ( specjLazyLoad ) {
 				originalSession = session;
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throw new LazyInitializationException( "could not initialize proxy - the owning Session was closed" );
 			}
 		}
 		else if ( !session.isConnected() ) {
 			if ( specjLazyLoad ) {
 				originalSession = session;
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throw new LazyInitializationException( "could not initialize proxy - the owning Session is disconnected" );
 			}
 		}
 
 		if ( isTempSession ) {
 			session.getPersistenceContext().addUninitializedDetachedCollection(
 					session.getFactory().getCollectionPersister( getRole() ),
 					this
 			);
 		}
 
 		try {
 			return lazyInitializationWork.doWork();
 		}
 		finally {
 			if ( isTempSession ) {
 				// make sure the just opened temp session gets closed!
 				try {
 					( (Session) session ).close();
 				}
 				catch (Exception e) {
 					log.warn( "Unable to close temporary session used to load lazy collection associated to no session" );
 				}
 				session = originalSession;
 			}
 		}
 	}
 
 	private SessionImplementor openTemporarySessionForLoading() {
 		if ( sessionFactoryUuid == null ) {
 			throwLazyInitializationException( "SessionFactory UUID not known to create temporary Session for loading" );
 		}
 
 		SessionFactoryImplementor sf = (SessionFactoryImplementor)
 				SessionFactoryRegistry.INSTANCE.getSessionFactory( sessionFactoryUuid );
 		return (SessionImplementor) sf.openSession();
 	}
 
 	protected Boolean readIndexExistence(final Object index) {
 		if ( !initialized ) {
 			Boolean extraLazyExistenceCheck = withTemporarySessionIfNeeded(
 					new LazyInitializationWork<Boolean>() {
 						@Override
 						public Boolean doWork() {
 							CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 							CollectionPersister persister = entry.getLoadedPersister();
 							if ( persister.isExtraLazy() ) {
 								if ( hasQueuedOperations() ) {
 									session.flush();
 								}
 								return persister.indexExists( entry.getLoadedKey(), index, session );
 							}
 							else {
 								read();
 							}
 							return null;
 						}
 					}
 			);
 			if ( extraLazyExistenceCheck != null ) {
 				return extraLazyExistenceCheck;
 			}
 		}
 		return null;
 	}
 
 	protected Boolean readElementExistence(final Object element) {
 		if ( !initialized ) {
 			Boolean extraLazyExistenceCheck = withTemporarySessionIfNeeded(
 					new LazyInitializationWork<Boolean>() {
 						@Override
 						public Boolean doWork() {
 							CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 							CollectionPersister persister = entry.getLoadedPersister();
 							if ( persister.isExtraLazy() ) {
 								if ( hasQueuedOperations() ) {
 									session.flush();
 								}
 								return persister.elementExists( entry.getLoadedKey(), element, session );
 							}
 							else {
 								read();
 							}
 							return null;
 						}
 					}
 			);
 			if ( extraLazyExistenceCheck != null ) {
 				return extraLazyExistenceCheck;
 			}
 		}
 		return null;
 	}
 
 	protected static final Object UNKNOWN = new MarkerObject( "UNKNOWN" );
 
 	protected Object readElementByIndex(final Object index) {
 		if ( !initialized ) {
 			class ExtraLazyElementByIndexReader implements LazyInitializationWork {
 				private boolean isExtraLazy;
 				private Object element;
 
 				@Override
 				public Object doWork() {
 					CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 					CollectionPersister persister = entry.getLoadedPersister();
 					isExtraLazy = persister.isExtraLazy();
 					if ( isExtraLazy ) {
 						if ( hasQueuedOperations() ) {
 							session.flush();
 						}
 						element = persister.getElementByIndex( entry.getLoadedKey(), index, session, owner );
 					}
 					else {
 						read();
 					}
 					return null;
 				}
 			}
 
 			ExtraLazyElementByIndexReader reader = new ExtraLazyElementByIndexReader();
 			//noinspection unchecked
 			withTemporarySessionIfNeeded( reader );
 			if ( reader.isExtraLazy ) {
 				return reader.element;
 			}
 		}
 		return UNKNOWN;
 
 	}
 
 	protected int getCachedSize() {
 		return cachedSize;
 	}
 
 	private boolean isConnectedToSession() {
 		return session != null &&
 				session.isOpen() &&
 				session.getPersistenceContext().containsCollection( this );
 	}
 
 	/**
 	 * Called by any writer method of the collection interface
 	 */
 	protected final void write() {
 		initialize( true );
 		dirty();
 	}
 
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" operations?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean isOperationQueueEnabled() {
 		return !initialized &&
 				isConnectedToSession() &&
 				isInverseCollection();
 	}
 
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" puts? This is a special case, because of orphan
 	 * delete.
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean isPutQueueEnabled() {
 		return !initialized &&
 				isConnectedToSession() &&
 				isInverseOneToManyOrNoOrphanDelete();
 	}
 
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" clear? This is a special case, because of orphan
 	 * delete.
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean isClearQueueEnabled() {
 		return !initialized &&
 				isConnectedToSession() &&
 				isInverseCollectionNoOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseCollection() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null && ce.getLoadedPersister().isInverse();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association with
 	 * no orphan delete enabled?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseCollectionNoOrphanDelete() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null &&
 				ce.getLoadedPersister().isInverse() &&
 				!ce.getLoadedPersister().hasOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional one-to-many, or
 	 * of a collection with no orphan delete?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseOneToManyOrNoOrphanDelete() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null && ce.getLoadedPersister().isInverse() && (
 				ce.getLoadedPersister().isOneToMany() ||
 						!ce.getLoadedPersister().hasOrphanDelete()
 		);
 	}
 
 	/**
 	 * Queue an addition
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected final void queueOperation(DelayedOperation operation) {
 		if ( operationQueue == null ) {
 			operationQueue = new ArrayList<DelayedOperation>( 10 );
 		}
 		operationQueue.add( operation );
 		dirty = true; //needed so that we remove this collection from the second-level cache
 	}
 
 	/**
 	 * After reading all existing elements from the database,
 	 * add the queued elements to the underlying collection.
 	 */
 	protected final void performQueuedOperations() {
 		for ( DelayedOperation operation : operationQueue ) {
 			operation.operate();
 		}
 	}
 
 	/**
 	 * After flushing, re-init snapshot state.
 	 */
 	public void setSnapshot(Serializable key, String role, Serializable snapshot) {
 		this.key = key;
 		this.role = role;
 		this.storedSnapshot = snapshot;
 	}
 
 	/**
 	 * After flushing, clear any "queued" additions, since the
 	 * database state is now synchronized with the memory state.
 	 */
 	public void postAction() {
 		operationQueue = null;
 		cachedSize = -1;
 		clearDirty();
 	}
 
 	/**
 	 * Not called by Hibernate, but used by non-JDK serialization,
 	 * eg. SOAP libraries.
 	 */
 	public AbstractPersistentCollection() {
 	}
 
 	protected AbstractPersistentCollection(SessionImplementor session) {
 		this.session = session;
 	}
 
 	/**
 	 * return the user-visible collection (or array) instance
 	 */
 	public Object getValue() {
 		return this;
 	}
 
 	/**
 	 * Called just before reading any rows from the JDBC result set
 	 */
 	public void beginRead() {
 		// override on some subclasses
 		initializing = true;
 	}
 
 	/**
 	 * Called after reading all rows from the JDBC result set
 	 */
 	public boolean endRead() {
 		//override on some subclasses
 		return afterInitialize();
 	}
 
 	public boolean afterInitialize() {
 		setInitialized();
 		//do this bit after setting initialized to true or it will recurse
 		if ( operationQueue != null ) {
 			performQueuedOperations();
 			operationQueue = null;
 			cachedSize = -1;
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 
 	/**
 	 * Initialize the collection, if possible, wrapping any exceptions
 	 * in a runtime exception
 	 *
 	 * @param writing currently obsolete
 	 *
 	 * @throws LazyInitializationException if we cannot initialize
 	 */
 	protected final void initialize(final boolean writing) {
 		if ( initialized ) {
 			return;
 		}
 
 		withTemporarySessionIfNeeded(
 				new LazyInitializationWork<Object>() {
 					@Override
 					public Object doWork() {
 						session.initializeCollection( AbstractPersistentCollection.this, writing );
 						return null;
 					}
 				}
 		);
 	}
 
 	private void throwLazyInitializationExceptionIfNotConnected() {
 		if ( !isConnectedToSession() ) {
 			throwLazyInitializationException( "no session or session was closed" );
 		}
 		if ( !session.isConnected() ) {
 			throwLazyInitializationException( "session is disconnected" );
 		}
 	}
 
 	private void throwLazyInitializationException(String message) {
 		throw new LazyInitializationException(
 				"failed to lazily initialize a collection" +
 						(role == null ? "" : " of role: " + role) +
 						", " + message
 		);
 	}
 
 	protected final void setInitialized() {
 		this.initializing = false;
 		this.initialized = true;
 	}
 
 	protected final void setDirectlyAccessible(boolean directlyAccessible) {
 		this.directlyAccessible = directlyAccessible;
 	}
 
 	/**
 	 * Could the application possibly have a direct reference to
 	 * the underlying collection implementation?
 	 */
 	public boolean isDirectlyAccessible() {
 		return directlyAccessible;
 	}
 
 	/**
 	 * Disassociate this collection from the given session.
 	 *
 	 * @return true if this was currently associated with the given session
 	 */
 	public final boolean unsetSession(SessionImplementor currentSession) {
 		prepareForPossibleSpecialSpecjInitialization();
 		if ( currentSession == this.session ) {
 			this.session = null;
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	protected void prepareForPossibleSpecialSpecjInitialization() {
 		if ( session != null ) {
-			specjLazyLoad = Boolean.parseBoolean(
-					session.getFactory()
-							.getProperties()
-							.getProperty( AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS )
-			);
+			specjLazyLoad = session.getFactory().getSettings().isInitializeLazyStateOutsideTransactionsEnabled();
 
 			if ( specjLazyLoad && sessionFactoryUuid == null ) {
 				try {
 					sessionFactoryUuid = (String) session.getFactory().getReference().get( "uuid" ).getContent();
 				}
 				catch (NamingException e) {
 					//not much we can do if this fails...
 				}
 			}
 		}
 	}
 
 
 	/**
 	 * Associate the collection with the given session.
 	 *
 	 * @return false if the collection was already associated with the session
 	 *
 	 * @throws HibernateException if the collection was already associated
 	 * with another open session
 	 */
 	public final boolean setCurrentSession(SessionImplementor session) throws HibernateException {
 		if ( session == this.session ) {
 			return false;
 		}
 		else {
 			if ( isConnectedToSession() ) {
 				CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 				if ( ce == null ) {
 					throw new HibernateException(
 							"Illegal attempt to associate a collection with two open sessions"
 					);
 				}
 				else {
 					throw new HibernateException(
 							"Illegal attempt to associate a collection with two open sessions: " +
 									MessageHelper.collectionInfoString(
 											ce.getLoadedPersister(),
 											ce.getLoadedKey(),
 											session.getFactory()
 									)
 					);
 				}
 			}
 			else {
 				this.session = session;
 				return true;
 			}
 		}
 	}
 
 	/**
 	 * Do we need to completely recreate this collection when it changes?
 	 */
 	public boolean needsRecreate(CollectionPersister persister) {
 		return false;
 	}
 
 	/**
 	 * To be called internally by the session, forcing
 	 * immediate initialization.
 	 */
 	public final void forceInitialization() throws HibernateException {
 		if ( !initialized ) {
 			if ( initializing ) {
 				throw new AssertionFailure( "force initialize loading collection" );
 			}
 			if ( session == null ) {
 				throw new HibernateException( "collection is not associated with any session" );
 			}
 			if ( !session.isConnected() ) {
 				throw new HibernateException( "disconnected session" );
 			}
 			session.initializeCollection( this, false );
 		}
 	}
 
 
 	/**
 	 * Get the current snapshot from the session
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected final Serializable getSnapshot() {
 		return session.getPersistenceContext().getSnapshot( this );
 	}
 
 	/**
 	 * Is this instance initialized?
 	 */
 	public final boolean wasInitialized() {
 		return initialized;
 	}
 
 	public boolean isRowUpdatePossible() {
 		return true;
 	}
 
 	/**
 	 * Does this instance have any "queued" additions?
 	 */
 	public final boolean hasQueuedOperations() {
 		return operationQueue != null;
 	}
 
 	/**
 	 * Iterate the "queued" additions
 	 */
 	public final Iterator queuedAdditionIterator() {
 		if ( hasQueuedOperations() ) {
 			return new Iterator() {
 				int i = 0;
 
 				public Object next() {
 					return operationQueue.get( i++ ).getAddedInstance();
 				}
 
 				public boolean hasNext() {
 					return i < operationQueue.size();
 				}
 
 				public void remove() {
 					throw new UnsupportedOperationException();
 				}
 			};
 		}
 		else {
 			return EmptyIterator.INSTANCE;
 		}
 	}
 
 	/**
 	 * Iterate the "queued" additions
 	 */
 	@SuppressWarnings({"unchecked"})
 	public final Collection getQueuedOrphans(String entityName) {
 		if ( hasQueuedOperations() ) {
 			Collection additions = new ArrayList( operationQueue.size() );
 			Collection removals = new ArrayList( operationQueue.size() );
 			for ( DelayedOperation operation : operationQueue ) {
 				additions.add( operation.getAddedInstance() );
 				removals.add( operation.getOrphan() );
 			}
 			return getOrphans( removals, additions, entityName, session );
 		}
 		else {
 			return Collections.EMPTY_LIST;
 		}
 	}
 
 	/**
 	 * Called before inserting rows, to ensure that any surrogate keys
 	 * are fully generated
 	 */
 	public void preInsert(CollectionPersister persister) throws HibernateException {
 	}
 
 	/**
 	 * Called after inserting a row, to fetch the natively generated id
 	 */
 	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {
 	}
 
 	/**
 	 * get all "orphaned" elements
 	 */
 	public abstract Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException;
 
 	/**
 	 * Get the current session
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
 	protected final class IteratorProxy implements Iterator {
 		protected final Iterator itr;
 
 		public IteratorProxy(Iterator itr) {
 			this.itr = itr;
 		}
 
 		public boolean hasNext() {
 			return itr.hasNext();
 		}
 
 		public Object next() {
 			return itr.next();
 		}
 
 		public void remove() {
 			write();
 			itr.remove();
 		}
 
 	}
 
 	protected final class ListIteratorProxy implements ListIterator {
 		protected final ListIterator itr;
 
 		public ListIteratorProxy(ListIterator itr) {
 			this.itr = itr;
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public void add(Object o) {
 			write();
 			itr.add( o );
 		}
 
 		public boolean hasNext() {
 			return itr.hasNext();
 		}
 
 		public boolean hasPrevious() {
 			return itr.hasPrevious();
 		}
 
 		public Object next() {
 			return itr.next();
 		}
 
 		public int nextIndex() {
 			return itr.nextIndex();
 		}
 
 		public Object previous() {
 			return itr.previous();
 		}
 
 		public int previousIndex() {
 			return itr.previousIndex();
 		}
 
 		public void remove() {
 			write();
 			itr.remove();
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public void set(Object o) {
 			write();
 			itr.set( o );
 		}
 
 	}
 
 	protected class SetProxy implements java.util.Set {
 		protected final Collection set;
 
 		public SetProxy(Collection set) {
 			this.set = set;
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public boolean add(Object o) {
 			write();
 			return set.add( o );
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public boolean addAll(Collection c) {
 			write();
 			return set.addAll( c );
 		}
 
 		public void clear() {
 			write();
 			set.clear();
 		}
 
 		public boolean contains(Object o) {
 			return set.contains( o );
 		}
 
 		public boolean containsAll(Collection c) {
 			return set.containsAll( c );
 		}
 
 		public boolean isEmpty() {
 			return set.isEmpty();
 		}
 
 		public Iterator iterator() {
 			return new IteratorProxy( set.iterator() );
 		}
 
 		public boolean remove(Object o) {
 			write();
 			return set.remove( o );
 		}
 
 		public boolean removeAll(Collection c) {
 			write();
 			return set.removeAll( c );
 		}
 
 		public boolean retainAll(Collection c) {
 			write();
 			return set.retainAll( c );
 		}
 
 		public int size() {
 			return set.size();
 		}
 
 		public Object[] toArray() {
 			return set.toArray();
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public Object[] toArray(Object[] array) {
 			return set.toArray( array );
 		}
 
 	}
 
 	protected final class ListProxy implements java.util.List {
 		protected final List list;
 
 		public ListProxy(List list) {
 			this.list = list;
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public void add(int index, Object value) {
 			write();
 			list.add( index, value );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean add(Object o) {
 			write();
 			return list.add( o );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean addAll(Collection c) {
 			write();
 			return list.addAll( c );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean addAll(int i, Collection c) {
 			write();
 			return list.addAll( i, c );
 		}
 
 		@Override
 		public void clear() {
 			write();
 			list.clear();
 		}
 
 		@Override
 		public boolean contains(Object o) {
 			return list.contains( o );
 		}
 
 		@Override
 		public boolean containsAll(Collection c) {
 			return list.containsAll( c );
 		}
 
 		@Override
 		public Object get(int i) {
 			return list.get( i );
 		}
 
 		@Override
 		public int indexOf(Object o) {
 			return list.indexOf( o );
 		}
 
 		@Override
 		public boolean isEmpty() {
 			return list.isEmpty();
 		}
 
 		@Override
 		public Iterator iterator() {
 			return new IteratorProxy( list.iterator() );
 		}
 
 		@Override
 		public int lastIndexOf(Object o) {
 			return list.lastIndexOf( o );
 		}
 
 		@Override
 		public ListIterator listIterator() {
 			return new ListIteratorProxy( list.listIterator() );
 		}
 
 		@Override
 		public ListIterator listIterator(int i) {
 			return new ListIteratorProxy( list.listIterator( i ) );
 		}
 
 		@Override
 		public Object remove(int i) {
 			write();
 			return list.remove( i );
 		}
 
 		@Override
 		public boolean remove(Object o) {
 			write();
 			return list.remove( o );
 		}
 
 		@Override
 		public boolean removeAll(Collection c) {
 			write();
 			return list.removeAll( c );
 		}
 
 		@Override
 		public boolean retainAll(Collection c) {
 			write();
 			return list.retainAll( c );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public Object set(int i, Object o) {
 			write();
 			return list.set( i, o );
 		}
 
 		@Override
 		public int size() {
 			return list.size();
 		}
 
 		@Override
 		public List subList(int i, int j) {
 			return list.subList( i, j );
 		}
 
 		@Override
 		public Object[] toArray() {
 			return list.toArray();
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public Object[] toArray(Object[] array) {
 			return list.toArray( array );
 		}
 
 	}
 
 	/**
 	 * Contract for operations which are part of a collection's operation queue.
 	 */
 	protected interface DelayedOperation {
 		public void operate();
 
 		public Object getAddedInstance();
 
 		public Object getOrphan();
 	}
 
 	/**
 	 * Given a collection of entity instances that used to
 	 * belong to the collection, and a collection of instances
 	 * that currently belong, return a collection of orphans
 	 */
 	@SuppressWarnings({"JavaDoc", "unchecked"})
 	protected static Collection getOrphans(
 			Collection oldElements,
 			Collection currentElements,
 			String entityName,
 			SessionImplementor session) throws HibernateException {
 
 		// short-circuit(s)
 		if ( currentElements.size() == 0 ) {
 			return oldElements; // no new elements, the old list contains only Orphans
 		}
 		if ( oldElements.size() == 0 ) {
 			return oldElements; // no old elements, so no Orphans neither
 		}
 
 		final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
 		final Type idType = entityPersister.getIdentifierType();
 
 		// create the collection holding the Orphans
 		Collection res = new ArrayList();
 
 		// collect EntityIdentifier(s) of the *current* elements - add them into a HashSet for fast access
 		java.util.Set currentIds = new HashSet();
 		java.util.Set currentSaving = new IdentitySet();
 		for ( Object current : currentElements ) {
 			if ( current != null && ForeignKeys.isNotTransient( entityName, current, null, session ) ) {
 				EntityEntry ee = session.getPersistenceContext().getEntry( current );
 				if ( ee != null && ee.getStatus() == Status.SAVING ) {
 					currentSaving.add( current );
 				}
 				else {
 					Serializable currentId = ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							entityName,
 							current,
 							session
 					);
 					currentIds.add( new TypedValue( idType, currentId, entityPersister.getEntityMode() ) );
 				}
 			}
 		}
 
 		// iterate over the *old* list
 		for ( Object old : oldElements ) {
 			if ( !currentSaving.contains( old ) ) {
 				Serializable oldId = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, old, session );
 				if ( !currentIds.contains( new TypedValue( idType, oldId, entityPersister.getEntityMode() ) ) ) {
 					res.add( old );
 				}
 			}
 		}
 
 		return res;
 	}
 
 	public static void identityRemove(
 			Collection list,
 			Object object,
 			String entityName,
 			SessionImplementor session) throws HibernateException {
 
 		if ( object != null && ForeignKeys.isNotTransient( entityName, object, null, session ) ) {
 			final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
 			Type idType = entityPersister.getIdentifierType();
 
 			Serializable idOfCurrent = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, object, session );
 			Iterator itr = list.iterator();
 			while ( itr.hasNext() ) {
 				Serializable idOfOld = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, itr.next(), session );
 				if ( idType.isEqual( idOfCurrent, idOfOld, session.getFactory() ) ) {
 					itr.remove();
 					break;
 				}
 			}
 
 		}
 	}
 
 	public Object getIdentifier(Object entry, int i) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object getOwner() {
 		return owner;
 	}
 
 	public void setOwner(Object owner) {
 		this.owner = owner;
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
index be044a96d7..4d7d0cf601 100755
--- a/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
@@ -1,395 +1,389 @@
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
 package org.hibernate.proxy;
 
 import javax.naming.NamingException;
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.Session;
 import org.hibernate.SessionException;
 import org.hibernate.TransientObjectException;
-import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.SessionFactoryRegistry;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Convenience base class for lazy initialization handlers.  Centralizes the basic plumbing of doing lazy
  * initialization freeing subclasses to acts as essentially adapters to their intended entity mode and/or
  * proxy generation strategy.
  *
  * @author Gavin King
  */
 public abstract class AbstractLazyInitializer implements LazyInitializer {
 	private static final Logger log = Logger.getLogger( AbstractLazyInitializer.class );
 
 	private String entityName;
 	private Serializable id;
 	private Object target;
 	private boolean initialized;
 	private boolean readOnly;
 	private boolean unwrap;
 	private transient SessionImplementor session;
 	private Boolean readOnlyBeforeAttachedToSession;
 
 	private String sessionFactoryUuid;
 	private boolean specjLazyLoad = false;
 
 	/**
 	 * For serialization from the non-pojo initializers (HHH-3309)
 	 */
 	protected AbstractLazyInitializer() {
 	}
 
 	/**
 	 * Main constructor.
 	 *
 	 * @param entityName The name of the entity being proxied.
 	 * @param id The identifier of the entity being proxied.
 	 * @param session The session owning the proxy.
 	 */
 	protected AbstractLazyInitializer(String entityName, Serializable id, SessionImplementor session) {
 		this.entityName = entityName;
 		this.id = id;
 		// initialize other fields depending on session state
 		if ( session == null ) {
 			unsetSession();
 		}
 		else {
 			setSession( session );
 		}
 	}
 
 	@Override
 	public final String getEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public final Serializable getIdentifier() {
 		return id;
 	}
 
 	@Override
 	public final void setIdentifier(Serializable id) {
 		this.id = id;
 	}
 
 	@Override
 	public final boolean isUninitialized() {
 		return !initialized;
 	}
 
 	@Override
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public final void setSession(SessionImplementor s) throws HibernateException {
 		if ( s != session ) {
 			// check for s == null first, since it is least expensive
 			if ( s == null ) {
 				unsetSession();
 			}
 			else if ( isConnectedToSession() ) {
 				//TODO: perhaps this should be some other RuntimeException...
 				throw new HibernateException( "illegally attempted to associate a proxy with two open Sessions" );
 			}
 			else {
 				// s != null
 				session = s;
 				if ( readOnlyBeforeAttachedToSession == null ) {
 					// use the default read-only/modifiable setting
 					final EntityPersister persister = s.getFactory().getEntityPersister( entityName );
 					setReadOnly( s.getPersistenceContext().isDefaultReadOnly() || !persister.isMutable() );
 				}
 				else {
 					// use the read-only/modifiable setting indicated during deserialization
 					setReadOnly( readOnlyBeforeAttachedToSession.booleanValue() );
 					readOnlyBeforeAttachedToSession = null;
 				}
 			}
 		}
 	}
 
 	private static EntityKey generateEntityKeyOrNull(Serializable id, SessionImplementor s, String entityName) {
 		if ( id == null || s == null || entityName == null ) {
 			return null;
 		}
 		return s.generateEntityKey( id, s.getFactory().getEntityPersister( entityName ) );
 	}
 
 	@Override
 	public final void unsetSession() {
 		prepareForPossibleSpecialSpecjInitialization();
 		session = null;
 		readOnly = false;
 		readOnlyBeforeAttachedToSession = null;
 	}
 
 	@Override
 	public final void initialize() throws HibernateException {
 		if ( !initialized ) {
 			if ( specjLazyLoad ) {
 				specialSpecjInitialization();
 			}
 			else if ( session == null ) {
 				throw new LazyInitializationException( "could not initialize proxy - no Session" );
 			}
 			else if ( !session.isOpen() ) {
 				throw new LazyInitializationException( "could not initialize proxy - the owning Session was closed" );
 			}
 			else if ( !session.isConnected() ) {
 				throw new LazyInitializationException( "could not initialize proxy - the owning Session is disconnected" );
 			}
 			else {
 				target = session.immediateLoad( entityName, id );
 				initialized = true;
 				checkTargetState();
 			}
 		}
 		else {
 			checkTargetState();
 		}
 	}
 
 	protected void specialSpecjInitialization() {
 		if ( session == null ) {
 			//we have a detached collection thats set to null, reattach
 			if ( sessionFactoryUuid == null ) {
 				throw new LazyInitializationException( "could not initialize proxy - no Session" );
 			}
 			try {
 				SessionFactoryImplementor sf = (SessionFactoryImplementor)
 						SessionFactoryRegistry.INSTANCE.getSessionFactory( sessionFactoryUuid );
 				SessionImplementor session = (SessionImplementor) sf.openSession();
 
 				try {
 					target = session.immediateLoad( entityName, id );
 				}
 				finally {
 					// make sure the just opened temp session gets closed!
 					try {
 						( (Session) session ).close();
 					}
 					catch (Exception e) {
 						log.warn( "Unable to close temporary session used to load lazy proxy associated to no session" );
 					}
 				}
 				initialized = true;
 				checkTargetState();
 			}
 			catch (Exception e) {
 				e.printStackTrace();
 				throw new LazyInitializationException( e.getMessage() );
 			}
 		}
 		else if ( session.isOpen() && session.isConnected() ) {
 			target = session.immediateLoad( entityName, id );
 			initialized = true;
 			checkTargetState();
 		}
 		else {
 			throw new LazyInitializationException( "could not initialize proxy - Session was closed or disced" );
 		}
 	}
 
 	protected void prepareForPossibleSpecialSpecjInitialization() {
 		if ( session != null ) {
-			specjLazyLoad =
-					Boolean.parseBoolean(
-							session.getFactory()
-									.getProperties()
-									.getProperty( AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS )
-					);
+			specjLazyLoad = session.getFactory().getSettings().isInitializeLazyStateOutsideTransactionsEnabled();
 
 			if ( specjLazyLoad && sessionFactoryUuid == null ) {
 				try {
 					sessionFactoryUuid = (String) session.getFactory().getReference().get( "uuid" ).getContent();
 				}
 				catch (NamingException e) {
 					//not much we can do if this fails...
 				}
 			}
 		}
 	}
 
 	private void checkTargetState() {
 		if ( !unwrap ) {
 			if ( target == null ) {
 				getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityName, id );
 			}
 		}
 	}
 
 	/**
 	 * Getter for property 'connectedToSession'.
 	 *
 	 * @return Value for property 'connectedToSession'.
 	 */
 	protected final boolean isConnectedToSession() {
 		return getProxyOrNull() != null;
 	}
 
 	private Object getProxyOrNull() {
 		final EntityKey entityKey = generateEntityKeyOrNull( getIdentifier(), session, getEntityName() );
 		if ( entityKey != null && session != null && session.isOpen() ) {
 			return session.getPersistenceContext().getProxy( entityKey );
 		}
 		return null;
 	}
 
 	@Override
 	public final Object getImplementation() {
 		initialize();
 		return target;
 	}
 
 	@Override
 	public final void setImplementation(Object target) {
 		this.target = target;
 		initialized = true;
 	}
 
 	@Override
 	public final Object getImplementation(SessionImplementor s) throws HibernateException {
 		final EntityKey entityKey = generateEntityKeyOrNull( getIdentifier(), s, getEntityName() );
 		return (entityKey == null ? null : s.getPersistenceContext().getEntity( entityKey ));
 	}
 
 	/**
 	 * Getter for property 'target'.
 	 * <p/>
 	 * Same as {@link #getImplementation()} except that this method will not force initialization.
 	 *
 	 * @return Value for property 'target'.
 	 */
 	protected final Object getTarget() {
 		return target;
 	}
 
 	@Override
 	public final boolean isReadOnlySettingAvailable() {
 		return (session != null && !session.isClosed());
 	}
 
 	private void errorIfReadOnlySettingNotAvailable() {
 		if ( session == null ) {
 			throw new TransientObjectException(
 					"Proxy is detached (i.e, session is null). The read-only/modifiable setting is only accessible when the proxy is associated with an open session."
 			);
 		}
 		if ( session.isClosed() ) {
 			throw new SessionException(
 					"Session is closed. The read-only/modifiable setting is only accessible when the proxy is associated with an open session."
 			);
 		}
 	}
 
 	@Override
 	public final boolean isReadOnly() {
 		errorIfReadOnlySettingNotAvailable();
 		return readOnly;
 	}
 
 	@Override
 	public final void setReadOnly(boolean readOnly) {
 		errorIfReadOnlySettingNotAvailable();
 		// only update if readOnly is different from current setting
 		if ( this.readOnly != readOnly ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 			if ( !persister.isMutable() && !readOnly ) {
 				throw new IllegalStateException( "cannot make proxies for immutable entities modifiable" );
 			}
 			this.readOnly = readOnly;
 			if ( initialized ) {
 				EntityKey key = generateEntityKeyOrNull( getIdentifier(), session, getEntityName() );
 				if ( key != null && session.getPersistenceContext().containsEntity( key ) ) {
 					session.getPersistenceContext().setReadOnly( target, readOnly );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Get the read-only/modifiable setting that should be put in affect when it is
 	 * attached to a session.
 	 * <p/>
 	 * This method should only be called during serialization when read-only/modifiable setting
 	 * is not available (i.e., isReadOnlySettingAvailable() == false)
 	 *
 	 * @return null, if the default setting should be used;
 	 *         true, for read-only;
 	 *         false, for modifiable
 	 *
 	 * @throws IllegalStateException if isReadOnlySettingAvailable() == true
 	 */
 	protected final Boolean isReadOnlyBeforeAttachedToSession() {
 		if ( isReadOnlySettingAvailable() ) {
 			throw new IllegalStateException(
 					"Cannot call isReadOnlyBeforeAttachedToSession when isReadOnlySettingAvailable == true"
 			);
 		}
 		return readOnlyBeforeAttachedToSession;
 	}
 
 	/**
 	 * Set the read-only/modifiable setting that should be put in affect when it is
 	 * attached to a session.
 	 * <p/>
 	 * This method should only be called during deserialization, before associating
 	 * the proxy with a session.
 	 *
 	 * @param readOnlyBeforeAttachedToSession, the read-only/modifiable setting to use when
 	 * associated with a session; null indicates that the default should be used.
 	 *
 	 * @throws IllegalStateException if isReadOnlySettingAvailable() == true
 	 */
 	/* package-private */
 	final void setReadOnlyBeforeAttachedToSession(Boolean readOnlyBeforeAttachedToSession) {
 		if ( isReadOnlySettingAvailable() ) {
 			throw new IllegalStateException(
 					"Cannot call setReadOnlyBeforeAttachedToSession when isReadOnlySettingAvailable == true"
 			);
 		}
 		this.readOnlyBeforeAttachedToSession = readOnlyBeforeAttachedToSession;
 	}
 
 	@Override
 	public boolean isUnwrap() {
 		return unwrap;
 	}
 
 	@Override
 	public void setUnwrap(boolean unwrap) {
 		this.unwrap = unwrap;
 	}
 }
