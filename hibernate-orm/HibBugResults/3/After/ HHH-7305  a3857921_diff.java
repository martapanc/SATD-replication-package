diff --git a/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java b/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
index 64f2c98337..fff40398ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
@@ -1,79 +1,84 @@
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
 package org.hibernate;
 
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Describes the methods for multi-tenancy understood by Hibernate.
  *
  * @author Steve Ebersole
  */
 public enum MultiTenancyStrategy {
-
 	/**
 	 * Multi-tenancy implemented by use of discriminator columns.
 	 */
 	DISCRIMINATOR,
 	/**
 	 * Multi-tenancy implemented as separate schemas.
 	 */
 	SCHEMA,
 	/**
 	 * Multi-tenancy implemented as separate databases.
 	 */
 	DATABASE,
 	/**
 	 * No multi-tenancy
 	 */
 	NONE;
+
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MultiTenancyStrategy.class.getName()
 	);
+
+	public boolean requiresMultiTenantConnectionProvider() {
+		return this == DATABASE || this == SCHEMA;
+	}
+
 	public static MultiTenancyStrategy determineMultiTenancyStrategy(Map properties) {
 		final Object strategy = properties.get( Environment.MULTI_TENANT );
 		if ( strategy == null ) {
 			return MultiTenancyStrategy.NONE;
 		}
 
 		if ( MultiTenancyStrategy.class.isInstance( strategy ) ) {
 			return (MultiTenancyStrategy) strategy;
 		}
 
 		final String strategyName = strategy.toString();
 		try {
 			return MultiTenancyStrategy.valueOf( strategyName.toUpperCase() );
 		}
 		catch ( RuntimeException e ) {
 			LOG.warn( "Unknown multi tenancy strategy [ " +strategyName +" ], using MultiTenancyStrategy.NONE." );
 			return MultiTenancyStrategy.NONE;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 97c197f799..d157ca406f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,453 +1,460 @@
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
 import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
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
 
 		String sessionFactoryName = props.getProperty( Environment.SESSION_FACTORY_NAME );
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
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		}
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		}
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				Environment.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		}
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		}
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		if ( debugEnabled ) {
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		}
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
 		if ( statementFetchSize != null && debugEnabled ) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
+		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
+		if ( debugEnabled ) {
+			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
+		}
+		settings.setMultiTenancyStrategy( multiTenancyStrategy );
+
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
 		if ( debugEnabled ) {
 			LOG.debugf( "Connection release mode: %s", releaseModeName );
 		}
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
-			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
-					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
-				LOG.unsupportedAfterStatement();
-				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
+			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
+				// we need to make sure the underlying JDBC connection access supports aggressive release...
+				boolean supportsAgrressiveRelease = multiTenancyStrategy.requiresMultiTenantConnectionProvider()
+						? serviceRegistry.getService( MultiTenantConnectionProvider.class ).supportsAggressiveRelease()
+						: serviceRegistry.getService( ConnectionProvider.class ).supportsAggressiveRelease();
+				if ( ! supportsAgrressiveRelease ) {
+					LOG.unsupportedAfterStatement();
+					releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
+				}
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		if ( defaultSchema != null && debugEnabled ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
 		if ( defaultCatalog != null && debugEnabled ) {
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
 		if ( debugEnabled ) {
 			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		}
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		}
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		}
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		}
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		}
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		}
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		}
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
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
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		}
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
 		if ( prefix != null && debugEnabled ) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		}
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		}
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		}
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
 		if ( debugEnabled ) {
 			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		}
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		}
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
 		if ( debugEnabled ) {
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		}
 		settings.setCheckNullability(checkNullability);
 
-		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
-		if ( debugEnabled ) {
-			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
-		}
-		settings.setMultiTenancyStrategy( multiTenancyStrategy );
-
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
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
 //            LOG.debug("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
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
 						Environment.CACHE_REGION_FACTORY, properties, null
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
 						Environment.CACHE_REGION_FACTORY, properties, null
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
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
index ef9b9f85c8..2b1c58ff02 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
@@ -1,469 +1,479 @@
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
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.exception.internal.SQLExceptionTypeDelegate;
 import org.hibernate.exception.internal.SQLStateConversionDelegate;
 import org.hibernate.exception.internal.StandardSQLExceptionConverter;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.jdbc.cursor.internal.StandardRefCursorSupport;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Standard implementation of the {@link JdbcServices} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesImpl implements JdbcServices, ServiceRegistryAwareService, Configurable {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JdbcServicesImpl.class.getName());
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private Dialect dialect;
 	private ConnectionProvider connectionProvider;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper sqlExceptionHelper;
 	private ExtractedDatabaseMetaData extractedMetaDataSupport;
 	private LobCreatorBuilder lobCreatorBuilder;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public void configure(Map configValues) {
 		final JdbcConnectionAccess jdbcConnectionAccess = buildJdbcConnectionAccess( configValues );
 		final DialectFactory dialectFactory = serviceRegistry.getService( DialectFactory.class );
 
 		Dialect dialect = null;
 		LobCreatorBuilder lobCreatorBuilder = null;
 
 		boolean metaSupportsRefCursors = false;
 		boolean metaSupportsNamedParams = false;
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
 				Connection connection = jdbcConnectionAccess.obtainConnection();
 				try {
 					DatabaseMetaData meta = connection.getMetaData();
 					if(LOG.isDebugEnabled()) {
 						LOG.debugf( "Database ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s",
 									meta.getDatabaseProductName(),
 									meta.getDatabaseProductVersion(),
 									meta.getDatabaseMajorVersion(),
 									meta.getDatabaseMinorVersion()
 						);
 						LOG.debugf( "Driver ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s",
 									meta.getDriverName(),
 									meta.getDriverVersion(),
 									meta.getDriverMajorVersion(),
 									meta.getDriverMinorVersion()
 						);
 						LOG.debugf( "JDBC version : %s.%s", meta.getJDBCMajorVersion(), meta.getJDBCMinorVersion() );
 					}
 
 					metaSupportsRefCursors = StandardRefCursorSupport.supportsRefCursors( meta );
 					metaSupportsNamedParams = meta.supportsNamedParameters();
 					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
 					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
 					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
 					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
 					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
 					extraKeywordsString = meta.getSQLKeywords();
 					sqlStateType = meta.getSQLStateType();
 					lobLocatorUpdateCopy = meta.locatorsUpdateCopy();
 					typeInfoSet.addAll( TypeInfoExtracter.extractTypeInfo( meta ) );
 
 					dialect = dialectFactory.buildDialect( configValues, connection );
 
 					catalogName = connection.getCatalog();
 					SchemaNameResolver schemaNameResolver = determineExplicitSchemaNameResolver( configValues );
 					if ( schemaNameResolver == null ) {
 // todo : add dialect method
 //						schemaNameResolver = dialect.getSchemaNameResolver();
 					}
 					if ( schemaNameResolver != null ) {
 						schemaName = schemaNameResolver.resolveSchemaName( connection );
 					}
 					lobCreatorBuilder = new LobCreatorBuilder( configValues, connection );
 				}
 				catch ( SQLException sqle ) {
 					LOG.unableToObtainConnectionMetadata( sqle.getMessage() );
 				}
 				finally {
 					if ( connection != null ) {
 						jdbcConnectionAccess.releaseConnection( connection );
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				LOG.unableToObtainConnectionToQueryMetadata( sqle.getMessage() );
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
 		this.lobCreatorBuilder = (
 				lobCreatorBuilder == null ?
 						new LobCreatorBuilder( configValues, null ) :
 						lobCreatorBuilder
 		);
 
 		this.sqlStatementLogger =  new SqlStatementLogger( showSQL, formatSQL );
 
 		this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl(
 				metaSupportsRefCursors,
 				metaSupportsNamedParams,
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
 
 		SQLExceptionConverter sqlExceptionConverter = dialect.buildSQLExceptionConverter();
 		if ( sqlExceptionConverter == null ) {
 			final StandardSQLExceptionConverter converter = new StandardSQLExceptionConverter();
 			sqlExceptionConverter = converter;
 			converter.addDelegate( dialect.buildSQLExceptionConversionDelegate() );
 			converter.addDelegate( new SQLExceptionTypeDelegate( dialect ) );
 			// todo : vary this based on extractedMetaDataSupport.getSqlStateType()
 			converter.addDelegate( new SQLStateConversionDelegate( dialect ) );
 		}
 		this.sqlExceptionHelper = new SqlExceptionHelper( sqlExceptionConverter );
 	}
 
 	private JdbcConnectionAccess buildJdbcConnectionAccess(Map configValues) {
 		final MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configValues );
 
 		if ( MultiTenancyStrategy.NONE == multiTenancyStrategy ) {
 			connectionProvider = serviceRegistry.getService( ConnectionProvider.class );
 			return new ConnectionProviderJdbcConnectionAccess( connectionProvider );
 		}
 		else {
 			connectionProvider = null;
 			final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService( MultiTenantConnectionProvider.class );
 			return new MultiTenantConnectionProviderJdbcConnectionAccess( multiTenantConnectionProvider );
 		}
 	}
 
 	private static class ConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final ConnectionProvider connectionProvider;
 
 		public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
+
+		@Override
+		public boolean supportsAggressiveRelease() {
+			return connectionProvider.supportsAggressiveRelease();
+		}
 	}
 
 	private static class MultiTenantConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getAnyConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.releaseAnyConnection( connection );
 		}
+
+		@Override
+		public boolean supportsAggressiveRelease() {
+			return connectionProvider.supportsAggressiveRelease();
+		}
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
 				LOG.unableToLocateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 			catch ( InvocationTargetException e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.getTargetException().toString() );
 			}
 			catch ( Exception e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 		}
 		return null;
 	}
 
 	private Set<String> parseKeywords(String extraKeywordsString) {
 		Set<String> keywordSet = new HashSet<String>();
 		keywordSet.addAll( Arrays.asList( extraKeywordsString.split( "," ) ) );
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
 		private final boolean supportsRefCursors;
 		private final boolean supportsNamedParameters;
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
 				boolean supportsRefCursors,
 				boolean supportsNamedParameters,
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
 			this.supportsRefCursors = supportsRefCursors;
 			this.supportsNamedParameters = supportsNamedParameters;
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
 
 		@Override
 		public boolean supportsRefCursors() {
 			return supportsRefCursors;
 		}
 
 		@Override
 		public boolean supportsNamedParameters() {
 			return supportsNamedParameters;
 		}
 
 		@Override
 		public boolean supportsScrollableResults() {
 			return supportsScrollableResults;
 		}
 
 		@Override
 		public boolean supportsGetGeneratedKeys() {
 			return supportsGetGeneratedKeys;
 		}
 
 		@Override
 		public boolean supportsBatchUpdates() {
 			return supportsBatchUpdates;
 		}
 
 		@Override
 		public boolean supportsDataDefinitionInTransaction() {
 			return supportsDataDefinitionInTransaction;
 		}
 
 		@Override
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return doesDataDefinitionCauseTransactionCommit;
 		}
 
 		@Override
 		public Set<String> getExtraKeywords() {
 			return extraKeywords;
 		}
 
 		@Override
 		public SQLStateType getSqlStateType() {
 			return sqlStateType;
 		}
 
 		@Override
 		public boolean doesLobLocatorUpdateCopy() {
 			return lobLocatorUpdateCopy;
 		}
 
 		@Override
 		public String getConnectionSchemaName() {
 			return connectionSchemaName;
 		}
 
 		@Override
 		public String getConnectionCatalogName() {
 			return connectionCatalogName;
 		}
 
 		@Override
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return typeInfoSet;
 		}
 	}
 
 	@Override
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	@Override
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	@Override
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	@Override
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	@Override
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return extractedMetaDataSupport;
 	}
 
 	@Override
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
 		return lobCreatorBuilder.buildLobCreator( lobCreationContext );
 	}
 
 	@Override
 	public ResultSetWrapper getResultSetWrapper() {
 		return ResultSetWrapperImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
index 5c2c658e43..276e61abb1 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
@@ -1,447 +1,447 @@
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
 import java.util.Iterator;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.jdbc.spi.NonDurableConnectionObserver;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.CollectionHelper;
 
 /**
  * Standard Hibernate {@link org.hibernate.engine.jdbc.spi.LogicalConnection} implementation
  * <p/>
  * IMPL NOTE : Custom serialization handling!
  *
  * @author Steve Ebersole
  */
 public class LogicalConnectionImpl implements LogicalConnectionImplementor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, LogicalConnectionImpl.class.getName() );
 
 	private transient Connection physicalConnection;
 	private transient Connection shareableConnectionProxy;
 
 	private final transient ConnectionReleaseMode connectionReleaseMode;
 	private final transient JdbcServices jdbcServices;
 	private final transient JdbcConnectionAccess jdbcConnectionAccess;
 	private final transient JdbcResourceRegistry jdbcResourceRegistry;
 	private final transient List<ConnectionObserver> observers;
 
 	private boolean releasesEnabled = true;
 
 	private final boolean isUserSuppliedConnection;
 
 	private boolean isClosed;
 
 	public LogicalConnectionImpl(
 			Connection userSuppliedConnection,
 			ConnectionReleaseMode connectionReleaseMode,
 			JdbcServices jdbcServices,
 			JdbcConnectionAccess jdbcConnectionAccess) {
 		this(
 				connectionReleaseMode,
 				jdbcServices,
 				jdbcConnectionAccess,
 				(userSuppliedConnection != null),
 				false,
 				new ArrayList<ConnectionObserver>()
 		);
 		this.physicalConnection = userSuppliedConnection;
 	}
 
 	private LogicalConnectionImpl(
 			ConnectionReleaseMode connectionReleaseMode,
 			JdbcServices jdbcServices,
 			JdbcConnectionAccess jdbcConnectionAccess,
 			boolean isUserSuppliedConnection,
 			boolean isClosed,
 			List<ConnectionObserver> observers) {
 		this.connectionReleaseMode = determineConnectionReleaseMode(
-				jdbcServices, isUserSuppliedConnection, connectionReleaseMode
+				jdbcConnectionAccess, isUserSuppliedConnection, connectionReleaseMode
 		);
 		this.jdbcServices = jdbcServices;
 		this.jdbcConnectionAccess = jdbcConnectionAccess;
 		this.jdbcResourceRegistry = new JdbcResourceRegistryImpl( getJdbcServices().getSqlExceptionHelper() );
 		this.observers = observers;
 
 		this.isUserSuppliedConnection = isUserSuppliedConnection;
 		this.isClosed = isClosed;
 	}
 
 	private static ConnectionReleaseMode determineConnectionReleaseMode(
-			JdbcServices jdbcServices,
+			JdbcConnectionAccess jdbcConnectionAccess,
 			boolean isUserSuppliedConnection,
 			ConnectionReleaseMode connectionReleaseMode) {
 		if ( isUserSuppliedConnection ) {
 			return ConnectionReleaseMode.ON_CLOSE;
 		}
 		else if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
-				! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
+				! jdbcConnectionAccess.supportsAggressiveRelease() ) {
 			LOG.debug( "Connection provider reports to not support aggressive release; overriding" );
 			return ConnectionReleaseMode.AFTER_TRANSACTION;
 		}
 		else {
 			return connectionReleaseMode;
 		}
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	@Override
 	public JdbcResourceRegistry getResourceRegistry() {
 		return jdbcResourceRegistry;
 	}
 
 	@Override
 	public void addObserver(ConnectionObserver observer) {
 		observers.add( observer );
 	}
 
 	@Override
 	public void removeObserver(ConnectionObserver connectionObserver) {
 		observers.remove( connectionObserver );
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed;
 	}
 
 	@Override
 	public boolean isPhysicallyConnected() {
 		return physicalConnection != null;
 	}
 
 	@Override
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
 
 	@Override
 	public Connection getShareableConnectionProxy() {
 		if ( shareableConnectionProxy == null ) {
 			shareableConnectionProxy = buildConnectionProxy();
 		}
 		return shareableConnectionProxy;
 	}
 
 	private Connection buildConnectionProxy() {
 		return ProxyBuilder.buildConnection( this );
 	}
 
 	@Override
 	public Connection getDistinctConnectionProxy() {
 		return buildConnectionProxy();
 	}
 
 	@Override
 	public Connection close() {
 		LOG.trace( "Closing logical connection" );
 		Connection c = isUserSuppliedConnection ? physicalConnection : null;
 		try {
 			releaseProxies();
 			jdbcResourceRegistry.close();
 			if ( !isUserSuppliedConnection && physicalConnection != null ) {
 				releaseConnection();
 			}
 			return c;
 		}
 		finally {
 			// no matter what
 			physicalConnection = null;
 			isClosed = true;
 			LOG.trace( "Logical connection closed" );
 			for ( ConnectionObserver observer : observers ) {
 				observer.logicalConnectionClosed();
 			}
 			observers.clear();
 		}
 	}
 
 	private void releaseProxies() {
 		if ( shareableConnectionProxy != null ) {
 			try {
 				shareableConnectionProxy.close();
 			}
 			catch (SQLException e) {
 				LOG.debug( "Error releasing shared connection proxy", e );
 			}
 		}
 		shareableConnectionProxy = null;
 	}
 
 	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public void afterStatementExecution() {
 		LOG.tracev( "Starting after statement execution processing [{0}]", connectionReleaseMode );
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			if ( ! releasesEnabled ) {
 				LOG.debug( "Skipping aggressive release due to manual disabling" );
 				return;
 			}
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
 				LOG.debug( "Skipping aggressive release due to registered resources" );
 				return;
 			}
 			releaseConnection();
 		}
 	}
 
 	@Override
 	public void afterTransaction() {
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ||
 				connectionReleaseMode == ConnectionReleaseMode.AFTER_TRANSACTION ) {
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
 				LOG.forcingContainerResourceCleanup();
 				jdbcResourceRegistry.releaseResources();
 			}
 			aggressiveRelease();
 		}
 	}
 
 	@Override
 	public void disableReleases() {
 		LOG.trace( "Disabling releases" );
 		releasesEnabled = false;
 	}
 
 	@Override
 	public void enableReleases() {
 		LOG.trace( "(Re)enabling releases" );
 		releasesEnabled = true;
 		afterStatementExecution();
 	}
 
 	/**
 	 * Force aggressive release of the underlying connection.
 	 */
 	public void aggressiveRelease() {
 		if ( isUserSuppliedConnection ) {
 			LOG.debug( "Cannot aggressively release user-supplied connection; skipping" );
 		}
 		else {
 			LOG.debug( "Aggressively releasing JDBC connection" );
 			if ( physicalConnection != null ) {
 				releaseConnection();
 			}
 		}
 	}
 
 
 	/**
 	 * Physically opens a JDBC Connection.
 	 *
 	 * @throws org.hibernate.JDBCException Indicates problem opening a connection
 	 */
 	private void obtainConnection() throws JDBCException {
 		LOG.debug( "Obtaining JDBC connection" );
 		try {
 			physicalConnection = jdbcConnectionAccess.obtainConnection();
 			for ( ConnectionObserver observer : observers ) {
 				observer.physicalConnectionObtained( physicalConnection );
 			}
 			LOG.debug( "Obtained JDBC connection" );
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
 		LOG.debug( "Releasing JDBC connection" );
 		if ( physicalConnection == null ) {
 			return;
 		}
 		try {
 			if ( !physicalConnection.isClosed() ) {
 				getJdbcServices().getSqlExceptionHelper().logAndClearWarnings( physicalConnection );
 			}
 			if ( !isUserSuppliedConnection ) {
 				jdbcConnectionAccess.releaseConnection( physicalConnection );
 			}
 		}
 		catch (SQLException e) {
 			throw getJdbcServices().getSqlExceptionHelper().convert( e, "Could not close connection" );
 		}
 		finally {
 			physicalConnection = null;
 		}
 		LOG.debug( "Released JDBC connection" );
 		for ( ConnectionObserver observer : observers ) {
 			observer.physicalConnectionReleased();
 		}
 		releaseNonDurableObservers();
 	}
 
 	private void releaseNonDurableObservers() {
 		Iterator observers = this.observers.iterator();
 		while ( observers.hasNext() ) {
 			if ( NonDurableConnectionObserver.class.isInstance( observers.next() ) ) {
 				observers.remove();
 			}
 		}
 	}
 
 	@Override
 	public Connection manualDisconnect() {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually disconnect because logical connection is already closed" );
 		}
 		releaseProxies();
 		Connection c = physicalConnection;
 		jdbcResourceRegistry.releaseResources();
 		releaseConnection();
 		return c;
 	}
 
 	@Override
 	public void manualReconnect(Connection suppliedConnection) {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually reconnect because logical connection is already closed" );
 		}
 		if ( !isUserSuppliedConnection ) {
 			throw new IllegalStateException( "cannot manually reconnect unless Connection was originally supplied" );
 		}
 		else {
 			if ( suppliedConnection == null ) {
 				throw new IllegalArgumentException( "cannot reconnect a null user-supplied connection" );
 			}
 			else if ( suppliedConnection == physicalConnection ) {
 				LOG.debug( "reconnecting the same connection that is already connected; should this connection have been disconnected?" );
 			}
 			else if ( physicalConnection != null ) {
 				throw new IllegalArgumentException(
 						"cannot reconnect to a new user-supplied connection because currently connected; must disconnect before reconnecting."
 				);
 			}
 			physicalConnection = suppliedConnection;
 			LOG.debug( "Reconnected JDBC connection" );
 		}
 	}
 
 	@Override
 	public boolean isAutoCommit() {
 		if ( !isOpen() || ! isPhysicallyConnected() ) {
 			return true;
 		}
 
 		try {
 			return getConnection().getAutoCommit();
 		}
 		catch (SQLException e) {
 			throw jdbcServices.getSqlExceptionHelper().convert( e, "could not inspect JDBC autocommit mode" );
 		}
 	}
 
 	@Override
 	public void notifyObserversStatementPrepared() {
 		for ( ConnectionObserver observer : observers ) {
 			observer.statementPrepared();
 		}
 	}
 
 	@Override
 	public boolean isReadyForSerialization() {
 		return isUserSuppliedConnection
 				? ! isPhysicallyConnected()
 				: ! getResourceRegistry().hasRegisteredResources();
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeBoolean( isUserSuppliedConnection );
 		oos.writeBoolean( isClosed );
 		List<ConnectionObserver> durableConnectionObservers = new ArrayList<ConnectionObserver>();
 		for ( ConnectionObserver observer : observers ) {
 			if ( ! NonDurableConnectionObserver.class.isInstance( observer ) ) {
 				durableConnectionObservers.add( observer );
 			}
 		}
 		oos.writeInt( durableConnectionObservers.size() );
 		for ( ConnectionObserver observer : durableConnectionObservers ) {
 			oos.writeObject( observer );
 		}
 	}
 
 	public static LogicalConnectionImpl deserialize(
 			ObjectInputStream ois,
 			TransactionContext transactionContext) throws IOException, ClassNotFoundException {
 		boolean isUserSuppliedConnection = ois.readBoolean();
 		boolean isClosed = ois.readBoolean();
 		int observerCount = ois.readInt();
 		List<ConnectionObserver> observers = CollectionHelper.arrayList( observerCount );
 		for ( int i = 0; i < observerCount; i++ ) {
 			observers.add( (ConnectionObserver) ois.readObject() );
 		}
 		return new LogicalConnectionImpl(
 				transactionContext.getConnectionReleaseMode(),
 				transactionContext.getTransactionEnvironment().getJdbcServices(),
 				transactionContext.getJdbcConnectionAccess(),
 				isUserSuppliedConnection,
 				isClosed,
 				observers
 		);
  	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcConnectionAccess.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcConnectionAccess.java
index 03b8438610..7256322d77 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcConnectionAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcConnectionAccess.java
@@ -1,39 +1,63 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 
 /**
  * Provides centralized access to JDBC connections.  Centralized to hide the complexity of accounting for contextual
  * (multi-tenant) versus non-contextual access.
  *
  * @author Steve Ebersole
  */
 public interface JdbcConnectionAccess extends Serializable {
+	/**
+	 * Obtain a JDBC connection
+	 *
+	 * @return The obtained connection
+	 *
+	 * @throws SQLException Indicates a problem getting the connection
+	 */
 	public Connection obtainConnection() throws SQLException;
+
+	/**
+	 * Release a previously obtained connection
+	 *
+	 * @param connection The connection to release
+	 *
+	 * @throws SQLException Indicates a problem releasing the connection
+	 */
 	public void releaseConnection(Connection connection) throws SQLException;
+
+	/**
+	 * Does the underlying provider of connections support aggressive releasing of connections (and re-acquisition
+	 * of those connections later, if need be) in JTA environments?
+	 *
+	 * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider#supportsAggressiveRelease()
+	 * @see org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider#supportsAggressiveRelease()
+	 */
+	public boolean supportsAggressiveRelease();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
index 657dbe6390..3ff092d19e 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
@@ -1,93 +1,95 @@
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
 
-import java.sql.Connection;
 import java.sql.ResultSet;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.service.Service;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
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
+	 *
+	 * @deprecated See deprecation notice on {@link org.hibernate.engine.spi.SessionFactoryImplementor#getConnectionProvider()}
+	 * for details
 	 */
+	@Deprecated
 	public ConnectionProvider getConnectionProvider();
 
 	/**
-	 * Obtain the dialect of the database to which {@link Connection connections} from
-	 * {@link #getConnectionProvider()} point.
+	 * Obtain the dialect of the database.
 	 *
 	 * @return The database dialect.
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Obtain service for logging SQL statements.
 	 *
 	 * @return The SQL statement logger.
 	 */
 	public SqlStatementLogger getSqlStatementLogger();
 
 	/**
 	 * Obtain service for dealing with exceptions.
 	 *
 	 * @return The exception helper service.
 	 */
 	public SqlExceptionHelper getSqlExceptionHelper();
 
 	/**
 	 * Obtain information about supported behavior reported by the JDBC driver.
 	 * <p/>
 	 * Yuck, yuck, yuck!  Much prefer this to be part of a "basic settings" type object.
 	 * 
 	 * @return The extracted database metadata, oddly enough :)
 	 */
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport();
 
 	/**
 	 * Create an instance of a {@link LobCreator} appropriate for the current environment, mainly meant to account for
 	 * variance between JDBC 4 (<= JDK 1.6) and JDBC3 (>= JDK 1.5).
 	 *
 	 * @param lobCreationContext The context in which the LOB is being created
 	 * @return The LOB creator.
 	 */
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext);
 
 	/**
 	 * Obtain service for wrapping a {@link ResultSet} in a "column name cache" wrapper.
 	 * @return The ResultSet wrapper.
 	 */
 	public ResultSetWrapper getResultSetWrapper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index bbc38adc99..7922d2bc99 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,278 +1,284 @@
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
 package org.hibernate.engine.spi;
 
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
  * @see org.hibernate.internal.SessionFactoryImpl
  * @author Gavin King
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory {
 	@Override
 	public SessionBuilderImplementor withOptions();
 
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
 	 * Get all entity persisters as a Map, which entity name its the key and the persister is the value.
 	 *
 	 * @return The Map contains all entity persisters.
 	 */
 	public Map<String,EntityPersister> getEntityPersisters();
 
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
 	 * Get all collection persisters as a Map, which collection role as the key and the persister is the value.
 	 *
 	 * @return The Map contains all collection persisters.
 	 */
 	public Map<String, CollectionPersister> getCollectionPersisters();
 
 	/**
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@code getJdbcServices().getDialect()}
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
+	 *
+	 * @deprecated Access to connections via {@link org.hibernate.engine.jdbc.spi.JdbcConnectionAccess} should
+	 * be preferred over access via {@link ConnectionProvider}, whenever possible.
+	 * {@link org.hibernate.engine.jdbc.spi.JdbcConnectionAccess} is tied to the Hibernate Session to
+	 * properly account for contextual information.  See {@link SessionImplementor#getJdbcConnectionAccess()}
 	 */
+	@Deprecated
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
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition);
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition);
 
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
 	 * Get a named naturalId cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getNaturalIdCacheRegion(String regionName);
 
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
 
 	public ServiceRegistryImplementor getServiceRegistry();
 
 	public void addObserver(SessionFactoryObserver observer);
 
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
index 16cd23fe72..ddf17c2cbc 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcIsolationDelegate.java
@@ -1,126 +1,126 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.transaction.spi.IsolationDelegate;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
-import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * The isolation delegate for JDBC {@link Connection} based transactions
  *
  * @author Steve Ebersole
  */
 public class JdbcIsolationDelegate implements IsolationDelegate {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JdbcIsolationDelegate.class.getName());
 
 	private final TransactionCoordinator transactionCoordinator;
 
 	public JdbcIsolationDelegate(TransactionCoordinator transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 	}
 
-	protected ConnectionProvider connectionProvider() {
-		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getJdbcServices().getConnectionProvider();
+	protected JdbcConnectionAccess jdbcConnectionAccess() {
+		return transactionCoordinator.getTransactionContext().getJdbcConnectionAccess();
 	}
 
 	protected SqlExceptionHelper sqlExceptionHelper() {
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getJdbcServices().getSqlExceptionHelper();
 	}
 
 	@Override
 	public <T> T delegateWork(WorkExecutorVisitable<T> work, boolean transacted) throws HibernateException {
 		boolean wasAutoCommit = false;
 		try {
 			// todo : should we use a connection proxy here?
-			Connection connection = connectionProvider().getConnection();
+			Connection connection = jdbcConnectionAccess().obtainConnection();
 			try {
 				if ( transacted ) {
 					if ( connection.getAutoCommit() ) {
 						wasAutoCommit = true;
 						connection.setAutoCommit( false );
 					}
 				}
 
 				T result = work.accept( new WorkExecutor<T>(), connection );
 
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
 					LOG.unableToRollbackConnection( ignore );
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
 						LOG.trace( "was unable to reset connection back to auto-commit" );
 					}
 				}
 				try {
-					connectionProvider().closeConnection( connection );
+					jdbcConnectionAccess().releaseConnection( connection );
 				}
 				catch ( Exception ignore ) {
 					LOG.unableToReleaseIsolatedConnection( ignore );
 				}
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw sqlExceptionHelper().convert( sqle, "unable to obtain isolated JDBC connection" );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
index fdf32c5f60..6ae26fb31a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaIsolationDelegate.java
@@ -1,185 +1,181 @@
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
 
-import java.sql.Connection;
-import java.sql.SQLException;
 import javax.transaction.NotSupportedException;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
+import java.sql.Connection;
+import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.transaction.spi.IsolationDelegate;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
-import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * An isolation delegate for JTA environments.
  *
  * @author Steve Ebersole
  */
 public class JtaIsolationDelegate implements IsolationDelegate {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JtaIsolationDelegate.class.getName());
 
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
 
-	protected ConnectionProvider connectionProvider() {
-		return transactionCoordinator.getTransactionContext()
-				.getTransactionEnvironment()
-				.getJdbcServices()
-				.getConnectionProvider();
+	protected JdbcConnectionAccess jdbcConnectionAccess() {
+		return transactionCoordinator.getTransactionContext().getJdbcConnectionAccess();
 	}
 
 	protected SqlExceptionHelper sqlExceptionHelper() {
 		return transactionCoordinator.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getSqlExceptionHelper();
 	}
 
 	@Override
 	public <T> T delegateWork(WorkExecutorVisitable<T> work, boolean transacted) throws HibernateException {
 		TransactionManager transactionManager = transactionManager();
 
 		try {
 			// First we suspend any current JTA transaction
 			Transaction surroundingTransaction = transactionManager.suspend();
 			LOG.debugf( "Surrounding JTA transaction suspended [%s]", surroundingTransaction );
 
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
 					LOG.debugf( "Surrounding JTA transaction resumed [%s]", surroundingTransaction );
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
 
 	private <T> T doTheWorkInNewTransaction(WorkExecutorVisitable<T> work, TransactionManager transactionManager) {
-		T result = null;
 		try {
 			// start the new isolated transaction
 			transactionManager.begin();
 
 			try {
-				result = doTheWork( work );
+				T result = doTheWork( work );
 				// if everything went ok, commit the isolated transaction
 				transactionManager.commit();
+				return result;
 			}
 			catch ( Exception e ) {
 				try {
 					transactionManager.rollback();
 				}
 				catch ( Exception ignore ) {
 					LOG.unableToRollbackIsolatedTransaction( e, ignore );
 				}
 				throw new HibernateException( "Could not apply work", e );
 			}
 		}
 		catch ( SystemException e ) {
 			throw new HibernateException( "Unable to start isolated transaction", e );
 		}
 		catch ( NotSupportedException e ) {
 			throw new HibernateException( "Unable to start isolated transaction", e );
 		}
-		return result;
 	}
 
 	private <T> T doTheWorkInNoTransaction(WorkExecutorVisitable<T> work) {
 		return doTheWork( work );
 	}
 
 	private <T> T doTheWork(WorkExecutorVisitable<T> work) {
 		try {
 			// obtain our isolated connection
-			Connection connection = connectionProvider().getConnection();
+			Connection connection = jdbcConnectionAccess().obtainConnection();
 			try {
 				// do the actual work
 				return work.accept( new WorkExecutor<T>(), connection );
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
-					connectionProvider().closeConnection( connection );
+					jdbcConnectionAccess().releaseConnection( connection );
 				}
 				catch ( Throwable ignore ) {
 					LOG.unableToReleaseIsolatedConnection( ignore );
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			throw sqlExceptionHelper().convert( e, "unable to obtain isolated JDBC connection" );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index 7e14ce91ba..51bf9caabd 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,361 +1,371 @@
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
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
 import org.hibernate.StoredProcedureCall;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.type.Type;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract,
 													 SessionImplementor, TransactionContext {
 	protected transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
 	private boolean closed = false;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory, String tenantIdentifier) {
 		this.factory = factory;
 		this.tenantIdentifier = tenantIdentifier;
 		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 			if ( tenantIdentifier != null ) {
 				throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
 			}
 		}
 		else {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "SessionFactory configured for multi-tenancy, but no tenant identifier specified" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public TransactionEnvironment getTransactionEnvironment() {
 		return factory.getTransactionEnvironment();
 	}
 
 	@Override
 	public <T> T execute(final LobCreationContext.Callback<T> callback) {
 		return getTransactionCoordinator().getJdbcCoordinator().coordinateWork(
 				new WorkExecutorVisitable<T>() {
 					@Override
 					public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 						try {
 							return callback.executeOnConnection( connection );
 						}
 						catch (SQLException e) {
 							throw getFactory().getSQLExceptionHelper().convert(
 									e,
 									"Error creating contextual LOB : " + e.getMessage()
 							);
 						}
 					}
 				}
 		);
 	}
 
 	@Override
 	public boolean isClosed() {
 		return closed;
 	}
 
 	protected void setClosed() {
 		closed = true;
 	}
 
 	protected void errorIfClosed() {
 		if ( closed ) {
 			throw new SessionException( "Session is closed!" );
 		}
 	}
 
 	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedQueryDefinition nqd = factory.getNamedQuery( queryName );
 		final Query query;
 		if ( nqd != null ) {
 			String queryString = nqd.getQueryString();
 			query = new QueryImpl(
 					queryString,
 			        nqd.getFlushMode(),
 			        this,
 			        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 			);
 			query.setComment( "named HQL query " + queryName );
 			if ( nqd.getLockOptions() != null ) {
 				query.setLockOptions( nqd.getLockOptions() );
 			}
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 			ParameterMetadata parameterMetadata = factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() );
 			query = new SQLQueryImpl(
 					nsqlqd,
 			        this,
 					parameterMetadata
 			);
 			query.setComment( "named native SQL query " + queryName );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
 	@Override
 	public Query getNamedSQLQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 		if ( nsqlqd==null ) {
 			throw new MappingException( "Named SQL query not known: " + queryName );
 		}
 		Query query = new SQLQueryImpl(
 				nsqlqd,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 		);
 		query.setComment( "named native SQL query " + queryName );
 		initQuery( query, nsqlqd );
 		return query;
 	}
 
 	@SuppressWarnings("UnnecessaryUnboxing")
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		// todo : cacheable and readonly should be Boolean rather than boolean...
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		query.setReadOnly( nqd.isReadOnly() );
 
 		if ( nqd.getTimeout() != null ) {
 			query.setTimeout( nqd.getTimeout().intValue() );
 		}
 		if ( nqd.getFetchSize() != null ) {
 			query.setFetchSize( nqd.getFetchSize().intValue() );
 		}
 		if ( nqd.getCacheMode() != null ) {
 			query.setCacheMode( nqd.getCacheMode() );
 		}
 		if ( nqd.getComment() != null ) {
 			query.setComment( nqd.getComment() );
 		}
 		if ( nqd.getFirstResult() != null ) {
 			query.setFirstResult( nqd.getFirstResult() );
 		}
 		if ( nqd.getMaxResults() != null ) {
 			query.setMaxResults( nqd.getMaxResults() );
 		}
 		if ( nqd.getFlushMode() != null ) {
 			query.setFlushMode( nqd.getFlushMode() );
 		}
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		QueryImpl query = new QueryImpl(
 				queryString,
 		        this,
 		        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( queryString );
 		return query;
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		SQLQueryImpl query = new SQLQueryImpl(
 				sql,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public StoredProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName );
 //		call.setComment( "Dynamic stored procedure call" );
 		return call;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public StoredProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName, resultClasses );
 //		call.setComment( "Dynamic stored procedure call" );
 		return call;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public StoredProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName, resultSetMappings );
 //		call.setComment( "Dynamic stored procedure call" );
 		return call;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
 		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return new EntityKey( id, persister, getTenantIdentifier() );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return new CacheKey( id, type, entityOrRoleName, getTenantIdentifier(), getFactory() );
 	}
 
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
 	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final ConnectionProvider connectionProvider;
 
 		private NonContextualJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
+
+		@Override
+		public boolean supportsAggressiveRelease() {
+			return connectionProvider.supportsAggressiveRelease();
+		}
 	}
 
 	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		private ContextualJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			return connectionProvider.getConnection( tenantIdentifier );
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			connectionProvider.releaseConnection( tenantIdentifier, connection );
 		}
+
+		@Override
+		public boolean supportsAggressiveRelease() {
+			return connectionProvider.supportsAggressiveRelease();
+		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java b/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java
index 89ff418298..7f32b1ce23 100644
--- a/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java
@@ -1,73 +1,96 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import static junit.framework.Assert.assertEquals;
 import static org.junit.Assert.assertArrayEquals;
 import static org.mockito.Matchers.anyObject;
 import static org.mockito.Matchers.eq;
 import static org.mockito.Mockito.mock;
 import static org.mockito.Mockito.when;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 import org.junit.Test;
 import org.mockito.invocation.InvocationOnMock;
 import org.mockito.stubbing.Answer;
 
 public class NaturalIdCacheKeyTest {
     @Test
     public void testSerializationRoundTrip() throws Exception {
         final EntityPersister entityPersister = mock(EntityPersister.class);
         final SessionImplementor sessionImplementor = mock(SessionImplementor.class);
         final SessionFactoryImplementor sessionFactoryImplementor = mock(SessionFactoryImplementor.class);
         final Type mockType = mock(Type.class);
         
         when (entityPersister.getRootEntityName()).thenReturn("EntityName");
         
         when(sessionImplementor.getFactory()).thenReturn(sessionFactoryImplementor);
         
         when(entityPersister.getNaturalIdentifierProperties()).thenReturn(new int[] {0, 1, 2});
         when(entityPersister.getPropertyTypes()).thenReturn(new Type[] {
                 mockType,
                 mockType,
                 mockType
         });
         
         when(mockType.getHashCode(anyObject(), eq(sessionFactoryImplementor))).thenAnswer(new Answer<Object>() {
             @Override
             public Object answer(InvocationOnMock invocation) throws Throwable {
                 return invocation.getArguments()[0].hashCode();
             }
         });
         
         when(mockType.disassemble(anyObject(), eq(sessionImplementor), eq(null))).thenAnswer(new Answer<Object>() {
             @Override
             public Object answer(InvocationOnMock invocation) throws Throwable {
                 return invocation.getArguments()[0];
             }
         });
         
         final NaturalIdCacheKey key = new NaturalIdCacheKey(new Object[] {"a", "b", "c"}, entityPersister, sessionImplementor);
         
         final ByteArrayOutputStream baos = new ByteArrayOutputStream();
         final ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(key);
         
         final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
         final NaturalIdCacheKey keyClone = (NaturalIdCacheKey)ois.readObject();
         
         assertEquals(key, keyClone);
         assertEquals(key.hashCode(), keyClone.hashCode());
         assertEquals(key.toString(), keyClone.toString());
         assertEquals(key.getEntityName(), keyClone.getEntityName());
         assertArrayEquals(key.getNaturalIdValues(), keyClone.getNaturalIdValues());
         assertEquals(key.getTenantId(), keyClone.getTenantId());
         
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/JdbcConnectionAccessImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/JdbcConnectionAccessImpl.java
index d9042a7631..8ba9d7555a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/JdbcConnectionAccessImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/JdbcConnectionAccessImpl.java
@@ -1,61 +1,66 @@
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
 
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * @author Steve Ebersole
  */
 public class JdbcConnectionAccessImpl implements JdbcConnectionAccess {
 	private final ConnectionProvider connectionProvider;
 
 	public JdbcConnectionAccessImpl(TransactionEnvironment transactionEnvironment) {
 		this( transactionEnvironment.getSessionFactory().getServiceRegistry() );
 	}
 
 	public JdbcConnectionAccessImpl(ConnectionProvider connectionProvider) {
 		this.connectionProvider = connectionProvider;
 	}
 
 	public JdbcConnectionAccessImpl(ServiceRegistry serviceRegistry) {
 		this( serviceRegistry.getService( ConnectionProvider.class ) );
 	}
 
 	@Override
 	public Connection obtainConnection() throws SQLException {
 		return connectionProvider.getConnection();
 	}
 
 	@Override
 	public void releaseConnection(Connection connection) throws SQLException {
 		connectionProvider.closeConnection( connection );
 	}
+
+	@Override
+	public boolean supportsAggressiveRelease() {
+		return connectionProvider.supportsAggressiveRelease();
+	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java
index 2003685ba2..f258c62f57 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java
@@ -1,29 +1,55 @@
 package org.hibernate.test.multitenancy;
 
 import org.junit.Test;
 
+import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.service.ServiceRegistryBuilder;
+import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 @TestForIssue(jiraKey = "HHH-7311")
 public class ConfigurationValidationTest extends BaseUnitTestCase {
 	@Test(expected = ServiceException.class)
 	public void testInvalidConnectionProvider() {
 		Configuration cfg = new Configuration();
 		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA );
 		cfg.setProperty( Environment.MULTI_TENANT_CONNECTION_PROVIDER, "class.not.present.in.classpath" );
 		cfg.buildMappings();
 		ServiceRegistryImplementor serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() ).buildServiceRegistry();
 		cfg.buildSessionFactory( serviceRegistry );
 	}
+
+	@Test
+	public void testReleaseMode() {
+		Configuration cfg = new Configuration();
+		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA );
+		cfg.getProperties().put( Environment.RELEASE_CONNECTIONS, ConnectionReleaseMode.AFTER_STATEMENT.name() );
+		cfg.buildMappings();
+
+		ServiceRegistryImplementor serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder()
+				.applySettings( cfg.getProperties() )
+				.addService(
+						MultiTenantConnectionProvider.class,
+						new TestingConnectionProvider(
+								new TestingConnectionProvider.NamedConnectionProviderPair(
+										"acme",
+										ConnectionProviderBuilder.buildConnectionProvider( "acme" )
+								)
+						)
+				)
+				.buildServiceRegistry();
+
+		cfg.buildSessionFactory( serviceRegistry );
+	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/TestingConnectionProvider.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/TestingConnectionProvider.java
new file mode 100644
index 0000000000..64d78abae8
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/TestingConnectionProvider.java
@@ -0,0 +1,69 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.multitenancy;
+
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.service.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
+import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+
+/**
+ * @author Steve Ebersole
+ */
+public class TestingConnectionProvider extends AbstractMultiTenantConnectionProvider {
+	private Map<String,ConnectionProvider> connectionProviderMap;
+
+	public TestingConnectionProvider(Map<String, ConnectionProvider> connectionProviderMap) {
+		this.connectionProviderMap = connectionProviderMap;
+	}
+
+	public TestingConnectionProvider(NamedConnectionProviderPair... pairs) {
+		Map<String,ConnectionProvider> map = new HashMap<String, ConnectionProvider>();
+		for ( NamedConnectionProviderPair pair : pairs ) {
+			map.put( pair.name, pair.connectionProvider );
+		}
+		this.connectionProviderMap = map;
+	}
+
+	@Override
+	protected ConnectionProvider getAnyConnectionProvider() {
+		return connectionProviderMap.values().iterator().next();
+	}
+
+	@Override
+	protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
+		return connectionProviderMap.get( tenantIdentifier );
+	}
+
+	public static class NamedConnectionProviderPair {
+		private final String name;
+		private final ConnectionProvider connectionProvider;
+
+		public NamedConnectionProviderPair(String name, ConnectionProvider connectionProvider) {
+			this.name = name;
+			this.connectionProvider = connectionProvider;
+		}
+	}
+}
