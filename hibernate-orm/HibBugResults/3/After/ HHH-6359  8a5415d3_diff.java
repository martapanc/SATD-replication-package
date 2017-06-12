diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index d3fa342450..a4fa8138e4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,396 +1,400 @@
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
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
+import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
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
 
+		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
+		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
+
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
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
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
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
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
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java b/hibernate-core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java
index e38e37febd..74074e62e3 100755
--- a/hibernate-core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/DynamicMapInstantiator.java
@@ -1,85 +1,99 @@
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
 package org.hibernate.tuple;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.metamodel.binding.EntityBinding;
 
 
 public class DynamicMapInstantiator implements Instantiator {
 	public static final String KEY = "$type$";
 
 	private String entityName;
 	private Set isInstanceEntityNames = new HashSet();
 
 	public DynamicMapInstantiator() {
 		this.entityName = null;
 	}
 
 	public DynamicMapInstantiator(PersistentClass mappingInfo) {
 		this.entityName = mappingInfo.getEntityName();
 		isInstanceEntityNames.add( entityName );
 		if ( mappingInfo.hasSubclasses() ) {
 			Iterator itr = mappingInfo.getSubclassClosureIterator();
 			while ( itr.hasNext() ) {
 				final PersistentClass subclassInfo = ( PersistentClass ) itr.next();
 				isInstanceEntityNames.add( subclassInfo.getEntityName() );
 			}
 		}
 	}
 
+	public DynamicMapInstantiator(EntityBinding mappingInfo) {
+		this.entityName = mappingInfo.getEntity().getName();
+		isInstanceEntityNames.add( entityName );
+		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
+		//if ( mappingInfo.hasSubclasses() ) {
+		//	Iterator itr = mappingInfo.getSubclassClosureIterator();
+		//	while ( itr.hasNext() ) {
+		//		final PersistentClass subclassInfo = ( PersistentClass ) itr.next();
+		//		isInstanceEntityNames.add( subclassInfo.getEntityName() );
+		//	}
+		//}
+	}
+
 	public final Object instantiate(Serializable id) {
 		return instantiate();
 	}
 
 	public final Object instantiate() {
 		Map map = generateMap();
 		if ( entityName!=null ) {
 			map.put( KEY, entityName );
 		}
 		return map;
 	}
 
 	public final boolean isInstance(Object object) {
 		if ( object instanceof Map ) {
 			if ( entityName == null ) {
 				return true;
 			}
 			String type = ( String ) ( ( Map ) object ).get( KEY );
 			return type == null || isInstanceEntityNames.contains( type );
 		}
 		else {
 			return false;
 		}
 	}
 
 	protected Map generateMap() {
 		return new HashMap();
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java b/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
index 2e89248b6f..ee23c08945 100755
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
@@ -1,122 +1,138 @@
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
 package org.hibernate.tuple;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.InstantiationException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.metamodel.binding.EntityBinding;
 
 import org.jboss.logging.Logger;
 
 /**
  * Defines a POJO-based instantiator for use from the tuplizers.
  */
 public class PojoInstantiator implements Instantiator, Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PojoInstantiator.class.getName());
 
 	private transient Constructor constructor;
 
 	private final Class mappedClass;
 	private final transient ReflectionOptimizer.InstantiationOptimizer optimizer;
 	private final boolean embeddedIdentifier;
 	private final Class proxyInterface;
 
 	public PojoInstantiator(Component component, ReflectionOptimizer.InstantiationOptimizer optimizer) {
 		this.mappedClass = component.getComponentClass();
 		this.optimizer = optimizer;
 
 		this.proxyInterface = null;
 		this.embeddedIdentifier = false;
 
 		try {
 			constructor = ReflectHelper.getDefaultConstructor(mappedClass);
 		}
 		catch ( PropertyNotFoundException pnfe ) {
             LOG.noDefaultConstructor(mappedClass.getName());
 			constructor = null;
 		}
 	}
 
 	public PojoInstantiator(PersistentClass persistentClass, ReflectionOptimizer.InstantiationOptimizer optimizer) {
 		this.mappedClass = persistentClass.getMappedClass();
 		this.proxyInterface = persistentClass.getProxyInterface();
 		this.embeddedIdentifier = persistentClass.hasEmbeddedIdentifier();
 		this.optimizer = optimizer;
 
 		try {
 			constructor = ReflectHelper.getDefaultConstructor( mappedClass );
 		}
 		catch ( PropertyNotFoundException pnfe ) {
             LOG.noDefaultConstructor(mappedClass.getName());
 			constructor = null;
 		}
 	}
 
+	public PojoInstantiator(EntityBinding entityBinding, ReflectionOptimizer.InstantiationOptimizer optimizer) {
+		this.mappedClass = entityBinding.getEntity().getJavaType().getClassReference();
+		this.proxyInterface = entityBinding.getProxyInterfaceType().getClassReference();
+		this.embeddedIdentifier = entityBinding.getEntityIdentifier().isEmbedded();
+		this.optimizer = optimizer;
+
+		try {
+			constructor = ReflectHelper.getDefaultConstructor( mappedClass );
+		}
+		catch ( PropertyNotFoundException pnfe ) {
+			LOG.noDefaultConstructor(mappedClass.getName());
+			constructor = null;
+		}
+	}
+
 	private void readObject(java.io.ObjectInputStream stream)
 	throws ClassNotFoundException, IOException {
 		stream.defaultReadObject();
 		constructor = ReflectHelper.getDefaultConstructor( mappedClass );
 	}
 
 	public Object instantiate() {
 		if ( ReflectHelper.isAbstractClass(mappedClass) ) {
 			throw new InstantiationException( "Cannot instantiate abstract class or interface: ", mappedClass );
 		}
 		else if ( optimizer != null ) {
 			return optimizer.newInstance();
 		}
 		else if ( constructor == null ) {
 			throw new InstantiationException( "No default constructor for entity: ", mappedClass );
 		}
 		else {
 			try {
 				return constructor.newInstance( (Object[]) null );
 			}
 			catch ( Exception e ) {
 				throw new InstantiationException( "Could not instantiate entity: ", mappedClass, e );
 			}
 		}
 	}
 
 	public Object instantiate(Serializable id) {
 		final boolean useEmbeddedIdentifierInstanceAsEntity = embeddedIdentifier &&
 				id != null &&
 				id.getClass().equals(mappedClass);
 		return useEmbeddedIdentifierInstanceAsEntity ? id : instantiate();
 	}
 
 	public boolean isInstance(Object object) {
 		return mappedClass.isInstance(object) ||
 				( proxyInterface!=null && proxyInterface.isInstance(object) ); //this one needed only for guessEntityMode()
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index e3de7bd95c..d3f5cf690c 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,686 +1,793 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.id.Assigned;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
+import org.hibernate.metamodel.binding.AttributeBinding;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.VersionProperty;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Support for tuplizers relating to entities.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public abstract class AbstractEntityTuplizer implements EntityTuplizer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractEntityTuplizer.class.getName()
 	);
 
 	//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter() and getSetter() are implemented currently; yuck!
 
 	private final EntityMetamodel entityMetamodel;
 
 	private final Getter idGetter;
 	private final Setter idSetter;
 
 	protected final Getter[] getters;
 	protected final Setter[] setters;
 	protected final int propertySpan;
 	protected final boolean hasCustomAccessors;
 	private final Instantiator instantiator;
 	private final ProxyFactory proxyFactory;
 	private final CompositeType identifierMapperType;
 
 	public Type getIdentifierMapperType() {
 		return identifierMapperType;
 	}
 
 	/**
 	 * Build an appropriate Getter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Getter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(PersistentClass mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
 
 	/**
+	 * Build an appropriate Getter for the given property.
+	 *
+	 *
+	 * @param mappedProperty The property to be accessed via the built Getter.
+	 * @return An appropriate Getter instance.
+	 */
+	protected abstract Getter buildPropertyGetter(AttributeBinding mappedProperty);
+
+	/**
+	 * Build an appropriate Setter for the given property.
+	 *
+	 *
+	 * @param mappedProperty The property to be accessed via the built Setter.
+	 * @return An appropriate Setter instance.
+	 */
+	protected abstract Setter buildPropertySetter(AttributeBinding mappedProperty);
+
+	/**
+	 * Build an appropriate Instantiator for the given mapped entity.
+	 *
+	 * @param mappingInfo The mapping information regarding the mapped entity.
+	 * @return An appropriate Instantiator instance.
+	 */
+	protected abstract Instantiator buildInstantiator(EntityBinding mappingInfo);
+
+	/**
+	 * Build an appropriate ProxyFactory for the given mapped entity.
+	 *
+	 * @param mappingInfo The mapping information regarding the mapped entity.
+	 * @param idGetter The constructed Getter relating to the entity's id property.
+	 * @param idSetter The constructed Setter relating to the entity's id property.
+	 * @return An appropriate ProxyFactory instance.
+	 */
+	protected abstract ProxyFactory buildProxyFactory(EntityBinding mappingInfo, Getter idGetter, Setter idSetter);
+
+	/**
 	 * Constructs a new AbstractEntityTuplizer instance.
 	 *
 	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
 	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
 	 */
 	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
 		this.entityMetamodel = entityMetamodel;
 
 		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
 			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 		}
 		else {
 			idGetter = null;
 			idSetter = null;
 		}
 
 		propertySpan = entityMetamodel.getPropertySpan();
 
         getters = new Getter[propertySpan];
 		setters = new Setter[propertySpan];
 
 		Iterator itr = mappingInfo.getPropertyClosureIterator();
 		boolean foundCustomAccessor=false;
 		int i=0;
 		while ( itr.hasNext() ) {
 			//TODO: redesign how PropertyAccessors are acquired...
 			Property property = (Property) itr.next();
 			getters[i] = buildPropertyGetter(property, mappingInfo);
 			setters[i] = buildPropertySetter(property, mappingInfo);
 			if ( !property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
         instantiator = buildInstantiator( mappingInfo );
 
 		if ( entityMetamodel.isLazy() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if (proxyFactory == null) {
 				entityMetamodel.setLazy( false );
 			}
 		}
 		else {
 			proxyFactory = null;
 		}
 
 		Component mapper = mappingInfo.getIdentifierMapper();
 		if ( mapper == null ) {
 			identifierMapperType = null;
 			mappedIdentifierValueMarshaller = null;
 		}
 		else {
 			identifierMapperType = (CompositeType) mapper.getType();
 			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
 					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
 					(ComponentType) identifierMapperType
 			);
 		}
 	}
 
+	/**
+	 * Constructs a new AbstractEntityTuplizer instance.
+	 *
+	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
+	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
+	 */
+	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappingInfo) {
+		this.entityMetamodel = entityMetamodel;
+
+		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
+			idGetter = buildPropertyGetter( mappingInfo.getEntityIdentifier().getValueBinding() );
+			idSetter = buildPropertySetter( mappingInfo.getEntityIdentifier().getValueBinding() );
+		}
+		else {
+			idGetter = null;
+			idSetter = null;
+		}
+
+		propertySpan = entityMetamodel.getPropertySpan();
+
+		getters = new Getter[ propertySpan ];
+		setters = new Setter[ propertySpan ];
+
+		boolean foundCustomAccessor = false;
+		int i = 0;
+		for ( AttributeBinding property : mappingInfo.getAttributeBindingClosure() ) {
+			if ( property == mappingInfo.getEntityIdentifier().getValueBinding() ) {
+				continue; // ID binding processed above
+			}
+
+			//TODO: redesign how PropertyAccessors are acquired...
+			getters[ i ] = buildPropertyGetter( property );
+			setters[ i ] = buildPropertySetter( property );
+			if ( ! property.isBasicPropertyAccessor() ) {
+				foundCustomAccessor = true;
+			}
+			i++;
+		}
+		hasCustomAccessors = foundCustomAccessor;
+
+		instantiator = buildInstantiator( mappingInfo );
+
+		if ( entityMetamodel.isLazy() ) {
+			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
+			if ( proxyFactory == null ) {
+				entityMetamodel.setLazy( false );
+			}
+		}
+		else {
+			proxyFactory = null;
+		}
+
+
+		// TODO: Fix this when components are working (HHH-6173)
+		//Component mapper = mappingInfo.getEntityIdentifier().getIdentifierMapper();
+		Component mapper = null;
+		if ( mapper == null ) {
+			identifierMapperType = null;
+			mappedIdentifierValueMarshaller = null;
+		}
+		else {
+			identifierMapperType = ( CompositeType ) mapper.getType();
+			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
+					( ComponentType ) entityMetamodel.getIdentifierProperty().getType(),
+					( ComponentType ) identifierMapperType
+			);
+		}
+	}
+
 	/** Retreives the defined entity-name for the tuplized entity.
 	 *
 	 * @return The entity-name.
 	 */
 	protected String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	/**
 	 * Retrieves the defined entity-names for any subclasses defined for this
 	 * entity.
 	 *
 	 * @return Any subclass entity-names.
 	 */
 	protected Set getSubclassEntityNames() {
 		return entityMetamodel.getSubclassEntityNames();
 	}
 
 	public Serializable getIdentifier(Object entity) throws HibernateException {
 		return getIdentifier( entity, null );
 	}
 
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		final Object id;
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			id = entity;
 		}
 		else {
 			if ( idGetter == null ) {
 				if (identifierMapperType==null) {
 					throw new HibernateException( "The class has no identifier property: " + getEntityName() );
 				}
 				else {
 					id = mappedIdentifierValueMarshaller.getIdentifier( entity, getEntityMode(), session );
 				}
 			}
 			else {
                 id = idGetter.get( entity );
             }
         }
 
 		try {
 			return (Serializable) id;
 		}
 		catch ( ClassCastException cce ) {
 			StringBuffer msg = new StringBuffer( "Identifier classes must be serializable. " );
 			if ( id != null ) {
 				msg.append( id.getClass().getName() ).append( " is not serializable. " );
 			}
 			if ( cce.getMessage() != null ) {
 				msg.append( cce.getMessage() );
 			}
 			throw new ClassCastException( msg.toString() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		setIdentifier( entity, id, null );
 	}
 
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			if ( entity != id ) {
 				CompositeType copier = (CompositeType) entityMetamodel.getIdentifierProperty().getType();
 				copier.setPropertyValues( entity, copier.getPropertyValues( id, getEntityMode() ), getEntityMode() );
 			}
 		}
 		else if ( idSetter != null ) {
 			idSetter.set( entity, id, getFactory() );
 		}
 		else if ( identifierMapperType != null ) {
 			mappedIdentifierValueMarshaller.setIdentifier( entity, id, getEntityMode(), session );
 		}
 	}
 
 	private static interface MappedIdentifierValueMarshaller {
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
 	}
 
 	private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;
 
 	private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(
 			ComponentType mappedIdClassComponentType,
 			ComponentType virtualIdComponent) {
 		// so basically at this point we know we have a "mapped" composite identifier
 		// which is an awful way to say that the identifier is represented differently
 		// in the entity and in the identifier value.  The incoming value should
 		// be an instance of the mapped identifier class (@IdClass) while the incoming entity
 		// should be an instance of the entity class as defined by metamodel.
 		//
 		// However, even within that we have 2 potential scenarios:
 		//		1) @IdClass types and entity @Id property types match
 		//			- return a NormalMappedIdentifierValueMarshaller
 		//		2) They do not match
 		//			- return a IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
 		boolean wereAllEquivalent = true;
 		// the sizes being off is a much bigger problem that should have been caught already...
 		for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 			if ( virtualIdComponent.getSubtypes()[i].isEntityType()
 					&& ! mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
 				wereAllEquivalent = false;
 				break;
 			}
 		}
 
 		return wereAllEquivalent
 				? (MappedIdentifierValueMarshaller) new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
 				: (MappedIdentifierValueMarshaller) new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType );
 	}
 
 	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private NormalMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			virtualIdComponent.setPropertyValues(
 					entity,
 					mappedIdentifierType.getPropertyValues( id, session ),
 					entityMode
 			);
 		}
 	}
 
 	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			final Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			final Type[] subTypes = virtualIdComponent.getSubtypes();
 			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
 			final int length = subTypes.length;
 			for ( int i = 0 ; i < length; i++ ) {
 				if ( propertyValues[i] == null ) {
 					throw new HibernateException( "No part of a composite identifier may be null" );
 				}
 				//JPA 2 @MapsId + @IdClass points to the pk of the entity
 				if ( subTypes[i].isAssociationType() && ! copierSubTypes[i].isAssociationType() ) {
 					// we need a session to handle this use case
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final Object subId;
 					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
 						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
 					}
 					else {
 						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
 						if ( pcEntry != null ) {
 							subId = pcEntry.getId();
 						}
 						else {
                             LOG.debugf( "Performing implicit derived identity cascade" );
 							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
 							for ( PersistEventListener listener : persistEventListeners( session ) ) {
 								listener.onPersist( event );
 							}
 							pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
 							if ( pcEntry == null || pcEntry.getId() == null ) {
 								throw new HibernateException( "Unable to process implicit derived identity cascade" );
 							}
 							else {
 								subId = pcEntry.getId();
 							}
 						}
 					}
 					propertyValues[i] = subId;
 				}
 			}
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
 			final Object[] injectionValues = new Object[ extractedValues.length ];
 			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
 				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
 				if ( virtualPropertyType.isEntityType() && ! idClassPropertyType.isEntityType() ) {
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
 					final EntityKey entityKey = session.generateEntityKey(
 							(Serializable) extractedValues[i],
 							session.getFactory().getEntityPersister( associatedEntityName )
 					);
 					// it is conceivable there is a proxy, so check that first
 					Object association = session.getPersistenceContext().getProxy( entityKey );
 					if ( association == null ) {
 						// otherwise look for an initialized version
 						association = session.getPersistenceContext().getEntity( entityKey );
 					}
 					injectionValues[i] = association;
 				}
 				else {
 					injectionValues[i] = extractedValues[i];
 				}
 			}
 			virtualIdComponent.setPropertyValues( entity, injectionValues, entityMode );
 		}
 	}
 
 	private static Iterable<PersistEventListener> persistEventListeners(SessionImplementor session) {
 		return session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.PERSIST )
 				.listeners();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SessionImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned ) {
 		}
 		else {
 			//reset the id
 			Serializable result = entityMetamodel.getIdentifierProperty()
 					.getUnsavedValue()
 					.getDefaultValue( currentId );
 			setIdentifier( entity, result, session );
 			//reset the version
 			VersionProperty versionProperty = entityMetamodel.getVersionProperty();
 			if ( entityMetamodel.isVersioned() ) {
 				setPropertyValue(
 				        entity,
 				        entityMetamodel.getVersionPropertyIndex(),
 						versionProperty.getUnsavedValue().getDefaultValue( currentVersion )
 				);
 			}
 		}
 	}
 
 	public Object getVersion(Object entity) throws HibernateException {
 		if ( !entityMetamodel.isVersioned() ) return null;
 		return getters[ entityMetamodel.getVersionPropertyIndex() ].get( entity );
 	}
 
 	protected boolean shouldGetAllProperties(Object entity) {
 		return !hasUninitializedLazyProperties( entity );
 	}
 
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		boolean getAll = shouldGetAllProperties( entity );
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			StandardProperty property = entityMetamodel.getProperties()[j];
 			if ( getAll || !property.isLazy() ) {
 				result[j] = getters[j].get( entity );
 			}
 			else {
 				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 		}
 		return result;
 	}
 
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 	throws HibernateException {
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			result[j] = getters[j].getForInsert( entity, mergeMap, session );
 		}
 		return result;
 	}
 
 	public Object getPropertyValue(Object entity, int i) throws HibernateException {
 		return getters[i].get( entity );
 	}
 
 	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
 		int loc = propertyPath.indexOf('.');
 		String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		if (index == null) {
 			propertyPath = "_identifierMapper." + propertyPath;
 			loc = propertyPath.indexOf('.');
 			basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		}
 		index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		final Object baseValue = getPropertyValue( entity, index.intValue() );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) entityMetamodel.getPropertyTypes()[index.intValue()],
 					baseValue,
 					propertyPath.substring(loc+1)
 			);
 		}
 		else {
 			return baseValue;
 		}
 	}
 
 	/**
 	 * Extract a component property value.
 	 *
 	 * @param type The component property types.
 	 * @param component The component instance itself.
 	 * @param propertyPath The property path for the property to be extracted.
 	 * @return The property value extracted.
 	 */
 	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
 		final int loc = propertyPath.indexOf( '.' );
 		final String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		final int index = findSubPropertyIndex( type, basePropertyName );
 		final Object baseValue = type.getPropertyValue( component, index, getEntityMode() );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) type.getSubtypes()[index],
 					baseValue,
 					propertyPath.substring(loc+1)
 			);
 		}
 		else {
 			return baseValue;
 		}
 
 	}
 
 	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
 		final String[] propertyNames = type.getPropertyNames();
 		for ( int index = 0; index<propertyNames.length; index++ ) {
 			if ( subPropertyName.equals( propertyNames[index] ) ) {
 				return index;
 			}
 		}
 		throw new MappingException( "component property not found: " + subPropertyName );
 	}
 
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		boolean setAll = !entityMetamodel.hasLazyProperties();
 
 		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
 			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				setters[j].set( entity, values[j], getFactory() );
 			}
 		}
 	}
 
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
 		setters[i].set( entity, value, getFactory() );
 	}
 
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
 		setters[ entityMetamodel.getPropertyIndex( propertyName ) ].set( entity, value, getFactory() );
 	}
 
 	public final Object instantiate(Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		return instantiate( id, null );
 	}
 
 	public final Object instantiate(Serializable id, SessionImplementor session) {
 		Object result = getInstantiator().instantiate( id );
 		if ( id != null ) {
 			setIdentifier( result, id, session );
 		}
 		return result;
 	}
 
 	public final Object instantiate() throws HibernateException {
 		return instantiate( null, null );
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
 
 	public boolean hasUninitializedLazyProperties(Object entity) {
 		// the default is to simply not lazy fetch properties for now...
 		return false;
 	}
 
 	public final boolean isInstance(Object object) {
         return getInstantiator().isInstance( object );
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public final Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return getProxyFactory().getProxy( id, session );
 	}
 
 	public boolean isLifecycleImplementor() {
 		return false;
 	}
 
 	protected final EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	protected final SessionFactoryImplementor getFactory() {
 		return entityMetamodel.getSessionFactory();
 	}
 
 	protected final Instantiator getInstantiator() {
 		return instantiator;
 	}
 
 	protected final ProxyFactory getProxyFactory() {
 		return proxyFactory;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getEntityMetamodel().getName() + ')';
 	}
 
 	public Getter getIdentifierGetter() {
 		return idGetter;
 	}
 
 	public Getter getVersionGetter() {
 		if ( getEntityMetamodel().isVersioned() ) {
 			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
 		}
 		return null;
 	}
 
 	public Getter getGetter(int i) {
 		return getters[i];
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
index 286149fd8b..9a53bcbe32 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
@@ -1,195 +1,260 @@
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
 package org.hibernate.tuple.entity;
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
+import org.hibernate.metamodel.binding.AttributeBinding;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.proxy.map.MapProxyFactory;
 import org.hibernate.tuple.DynamicMapInstantiator;
 import org.hibernate.tuple.Instantiator;
 import org.jboss.logging.Logger;
 
 /**
  * An {@link EntityTuplizer} specific to the dynamic-map entity mode.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class DynamicMapEntityTuplizer extends AbstractEntityTuplizer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DynamicMapEntityTuplizer.class.getName());
 
 	DynamicMapEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super(entityMetamodel, mappedEntity);
 	}
 
+	DynamicMapEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
+		super(entityMetamodel, mappedEntity);
+	}
+
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityMode getEntityMode() {
 		return EntityMode.MAP;
 	}
 
 	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
 		if ( mappedProperty.isBackRef() ) {
 			return mappedProperty.getPropertyAccessor(null);
 		}
 		else {
 			return PropertyAccessorFactory.getDynamicMapPropertyAccessor();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Instantiator buildInstantiator(PersistentClass mappingInfo) {
         return new DynamicMapInstantiator( mappingInfo );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
 
 		ProxyFactory pf = new MapProxyFactory();
 		try {
 			//TODO: design new lifecycle for ProxyFactory
 			pf.postInstantiate(
 					getEntityName(),
 					null,
 					null,
 					null,
 					null,
 					null
 			);
 		}
 		catch ( HibernateException he ) {
             LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
+	private PropertyAccessor buildPropertyAccessor(AttributeBinding mappedProperty) {
+		// TODO: fix when backrefs are working in new metamodel
+		//if ( mappedProperty.isBackRef() ) {
+		//	return mappedProperty.getPropertyAccessor( null );
+		//}
+		//else {
+			return PropertyAccessorFactory.getDynamicMapPropertyAccessor();
+		//}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+	protected Getter buildPropertyGetter(AttributeBinding mappedProperty) {
+		return buildPropertyAccessor( mappedProperty ).getGetter( null, mappedProperty.getAttribute().getName() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+	protected Setter buildPropertySetter(AttributeBinding mappedProperty) {
+		return buildPropertyAccessor( mappedProperty ).getSetter( null, mappedProperty.getAttribute().getName() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+	protected Instantiator buildInstantiator(EntityBinding mappingInfo) {
+		return new DynamicMapInstantiator( mappingInfo );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+	protected ProxyFactory buildProxyFactory(EntityBinding mappingInfo, Getter idGetter, Setter idSetter) {
+
+		ProxyFactory pf = new MapProxyFactory();
+		try {
+			//TODO: design new lifecycle for ProxyFactory
+			pf.postInstantiate(
+					getEntityName(),
+					null,
+					null,
+					null,
+					null,
+					null
+			);
+		}
+		catch ( HibernateException he ) {
+			LOG.unableToCreateProxyFactory(getEntityName(), he);
+			pf = null;
+		}
+		return pf;
+	}
+
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class getMappedClass() {
 		return Map.class;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class getConcreteProxyClass() {
 		return Map.class;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return new EntityNameResolver[] { BasicEntityNameResolver.INSTANCE };
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		return extractEmbeddedEntityName( ( Map ) entityInstance );
 	}
 
 	public static String extractEmbeddedEntityName(Map entity) {
 		return ( String ) entity.get( DynamicMapInstantiator.KEY );
 	}
 
 	public static class BasicEntityNameResolver implements EntityNameResolver {
 		public static final BasicEntityNameResolver INSTANCE = new BasicEntityNameResolver();
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public String resolveEntityName(Object entity) {
 			if ( ! Map.class.isInstance( entity ) ) {
 				return null;
 			}
 			final String entityName = extractEmbeddedEntityName( ( Map ) entity );
 			if ( entityName == null ) {
 				throw new HibernateException( "Could not determine type of dynamic map entity" );
 			}
 			return entityName;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		@Override
         public boolean equals(Object obj) {
 			return getClass().equals( obj.getClass() );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		@Override
         public int hashCode() {
 			return getClass().hashCode();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 85ae60e4a1..2c2c61615d 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,930 +1,928 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.domain.TypeNature;
 import org.hibernate.tuple.IdentifierProperty;
 import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes metamodel information about an entity.
  *
  * @author Steve Ebersole
  */
 public class EntityMetamodel implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityMetamodel.class.getName());
 
 	private static final int NO_VERSION_INDX = -66;
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private final String name;
 	private final String rootName;
 	private final EntityType entityType;
 
 	private final IdentifierProperty identifierProperty;
 	private final boolean versioned;
 
 	private final int propertySpan;
 	private final int versionPropertyIndex;
 	private final StandardProperty[] properties;
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyLaziness;
 	private final boolean[] propertyUpdateability;
 	private final boolean[] nonlazyPropertyUpdateability;
 	private final boolean[] propertyCheckability;
 	private final boolean[] propertyInsertability;
 	private final ValueInclusion[] insertInclusions;
 	private final ValueInclusion[] updateInclusions;
 	private final boolean[] propertyNullability;
 	private final boolean[] propertyVersionability;
 	private final CascadeStyle[] cascadeStyles;
 	private final boolean hasInsertGeneratedValues;
 	private final boolean hasUpdateGeneratedValues;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
 	private final boolean hasCollections;
 	private final boolean hasMutableProperties;
 	private final boolean hasLazyProperties;
 	private final boolean hasNonIdentifierPropertyNamedId;
 
 	private final int[] naturalIdPropertyNumbers;
 	private final boolean hasImmutableNaturalId;
 
 	private boolean lazy; //not final because proxy factory creation can fail
 	private final boolean hasCascades;
 	private final boolean mutable;
 	private final boolean isAbstract;
 	private final boolean selectBeforeUpdate;
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 	private final int optimisticLockMode;
 
 	private final boolean polymorphic;
 	private final String superclass;  // superclass entity-name
 	private final boolean explicitPolymorphism;
 	private final boolean inherited;
 	private final boolean hasSubclasses;
 	private final Set subclassEntityNames = new HashSet();
 	private final Map entityNameByInheritenceClassMap = new HashMap();
 
 	private final EntityMode entityMode;
 	private final EntityTuplizer entityTuplizer;
 
 	public EntityMetamodel(PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = persistentClass.getEntityName();
 		rootName = persistentClass.getRootClass().getEntityName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        persistentClass,
 		        sessionFactory.getIdentifierGenerator( rootName )
 			);
 
 		versioned = persistentClass.isVersioned();
 
 		boolean lazyAvailable = persistentClass.hasPojoRepresentation() &&
 		                        FieldInterceptionHelper.isInstrumented( persistentClass.getMappedClass() );
 		boolean hasLazy = false;
 
 		propertySpan = persistentClass.getPropertyClosureSpan();
 		properties = new StandardProperty[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 
 			if ( prop == persistentClass.getVersion() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty( prop, lazyAvailable );
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( prop, lazyAvailable );
 			}
 
 			if ( prop.isNaturalIdentifier() ) {
 				naturalIdNumbers.add( i );
 				if ( prop.isUpdateable() ) {
 					foundUpdateableNaturalIdProperty = true;
 				}
 			}
 
 			if ( "id".equals( prop.getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = prop.isLazy() && lazyAvailable;
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(prop, i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
         if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
 
 		lazy = persistentClass.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				!persistentClass.hasPojoRepresentation() ||
 				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
 		);
 		mutable = persistentClass.isMutable();
 		if ( persistentClass.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = persistentClass.hasPojoRepresentation() &&
 			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
 		}
 		else {
 			isAbstract = persistentClass.isAbstract().booleanValue();
 			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
 			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
                 LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
 		dynamicUpdate = persistentClass.useDynamicUpdate();
 		dynamicInsert = persistentClass.useDynamicInsert();
 
 		polymorphic = persistentClass.isPolymorphic();
 		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
 		inherited = persistentClass.isInherited();
 		superclass = inherited ?
 				persistentClass.getSuperclass().getEntityName() :
 				null;
 		hasSubclasses = persistentClass.hasSubclasses();
 
 		optimisticLockMode = persistentClass.getOptimisticLockMode();
 		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		iter = persistentClass.getSubclassIterator();
 		while ( iter.hasNext() ) {
 			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		}
 		subclassEntityNames.add( name );
 
 		if ( persistentClass.hasPojoRepresentation() ) {
 			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
 			iter = persistentClass.getSubclassIterator();
 			while ( iter.hasNext() ) {
 				final PersistentClass pc = ( PersistentClass ) iter.next();
 				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
 			}
 		}
 
 		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
 		if ( tuplizerClassName == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
 		}
 	}
 
 	public EntityMetamodel(EntityBinding entityBinding, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = entityBinding.getEntity().getName();
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		//rootName = entityBinding.getRootEntityBinding().getName();
 		rootName = name;
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        entityBinding,
 		        sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = entityBinding.isVersioned();
 
 		boolean hasPojoRepresentation = false;
 		Class<?> mappedClass = null;
 		Class<?> proxyInterfaceClass = null;
 		boolean lazyAvailable = false;
 		if (  entityBinding.getEntity().getJavaType() != null ) {
 			hasPojoRepresentation = true;
 			mappedClass = entityBinding.getEntity().getJavaType().getClassReference();
 			proxyInterfaceClass = entityBinding.getProxyInterfaceType().getClassReference();
 			lazyAvailable = FieldInterceptionHelper.isInstrumented( mappedClass );
 		}
 
 		boolean hasLazy = false;
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		//SimpleAttributeBinding rootEntityIdentifier = entityBinding.getRootEntityBinding().getEntityIdentifier().getValueBinding();
 		SimpleAttributeBinding rootEntityIdentifier = entityBinding.getEntityIdentifier().getValueBinding();
 		// entityBinding.getAttributeClosureSpan() includes the identifier binding;
 		// "properties" here excludes the ID, so subtract 1 if the identifier binding is non-null
 		propertySpan = rootEntityIdentifier == null ?
 				entityBinding.getAttributeBindingClosureSpan() :
 				entityBinding.getAttributeBindingClosureSpan() - 1;
 
 		properties = new StandardProperty[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == rootEntityIdentifier ) {
 				// skip the identifier attribute binding
 				continue;
 			}
 
 			if ( attributeBinding == entityBinding.getVersioningValueBinding() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty( entityBinding.getVersioningValueBinding(), lazyAvailable );
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, lazyAvailable );
 			}
 
 			// TODO: fix when natural IDs are added (HHH-6354)
 			//if ( attributeBinding.isNaturalIdentifier() ) {
 			//	naturalIdNumbers.add( i );
 			//	if ( attributeBinding.isUpdateable() ) {
 			//		foundUpdateableNaturalIdProperty = true;
 			//	}
 			//}
 
 			if ( "id".equals( attributeBinding.getAttribute().getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = attributeBinding.isLazy() && lazyAvailable;
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( attributeBinding, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( attributeBinding, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(attributeBinding.getAttribute(), i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
 		if (hasLazyProperties) {
 			LOG.lazyPropertyFetchingAvailable( name );
 		}
 
 		lazy = entityBinding.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				! hasPojoRepresentation ||
 				! ReflectHelper.isFinalClass( proxyInterfaceClass )
 		);
 		mutable = entityBinding.isMutable();
 		if ( entityBinding.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = hasPojoRepresentation &&
 			             ReflectHelper.isAbstractClass( mappedClass );
 		}
 		else {
 			isAbstract = entityBinding.isAbstract().booleanValue();
 			if ( !isAbstract && hasPojoRepresentation &&
 					ReflectHelper.isAbstractClass( mappedClass ) ) {
 				LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = entityBinding.isSelectBeforeUpdate();
 		dynamicUpdate = entityBinding.isDynamicUpdate();
 		dynamicInsert = entityBinding.isDynamicInsert();
 
 		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
 		//  for now set hasSubclasses to false
 		//hasSubclasses = entityBinding.hasSubclasses();
 		hasSubclasses = false;
 
 		//polymorphic = ! entityBinding.isRoot() || entityBinding.hasSubclasses();
 		polymorphic = ! entityBinding.isRoot() || hasSubclasses;
 
 		explicitPolymorphism = entityBinding.isExplicitPolymorphism();
 		inherited = ! entityBinding.isRoot();
 		superclass = inherited ?
 				entityBinding.getEntity().getSuperType().getName() :
 				null;
 
 		optimisticLockMode = entityBinding.getOptimisticLockMode();
 		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
 		// TODO: uncomment when it's possible to get subclasses from an EntityBinding
 		//iter = entityBinding.getSubclassIterator();
 		//while ( iter.hasNext() ) {
 		//	subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		//}
 		subclassEntityNames.add( name );
 
 		if ( mappedClass != null ) {
 			entityNameByInheritenceClassMap.put( mappedClass, name );
 		// TODO: uncomment when it's possible to get subclasses from an EntityBinding
 		//	iter = entityBinding.getSubclassIterator();
 		//	while ( iter.hasNext() ) {
 		//		final EntityBinding subclassEntityBinding = ( EntityBinding ) iter.next();
 		//		entityNameByInheritenceClassMap.put(
 		//				subclassEntityBinding.getEntity().getPojoEntitySpecifics().getEntityClass(),
 		//				subclassEntityBinding.getEntity().getName() );
 		//	}
 		}
 
 		entityMode = hasPojoRepresentation ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		Class<EntityTuplizer> tuplizerClass = entityBinding.getEntityTuplizerClass();
 
-		// TODO: fix this when integrated into tuplizers (HHH-6359)
-		//if ( tuplizerClass == null ) {
-		//	entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
-		//}
-		//else {
-		//	entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
-		//}
-		entityTuplizer = null;
+		if ( tuplizerClass == null ) {
+			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
+		}
+		else {
+			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
+		}
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialInsertComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialInsertComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialUpdateComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialUpdateComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void mapPropertyToIndex(Property prop, int i) {
 		propertyIndexes.put( prop.getName(), i );
 		if ( prop.getValue() instanceof Component ) {
 			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
 			while ( iter.hasNext() ) {
 				Property subprop = (Property) iter.next();
 				propertyIndexes.put(
 						prop.getName() + '.' + subprop.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	private void mapPropertyToIndex(Attribute attribute, int i) {
 		propertyIndexes.put( attribute.getName(), i );
 		if ( attribute.isSingular() &&
 				( ( SingularAttribute ) attribute ).getSingularAttributeType().getNature() == TypeNature.COMPONENT ) {
 			org.hibernate.metamodel.domain.Component component =
 					( org.hibernate.metamodel.domain.Component ) ( ( SingularAttribute ) attribute ).getSingularAttributeType();
 			for ( Attribute subAttribute : component.getAttributes() ) {
 				propertyIndexes.put(
 						attribute.getName() + '.' + subAttribute.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	public EntityTuplizer getTuplizer() {
 		return entityTuplizer;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return naturalIdPropertyNumbers;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return naturalIdPropertyNumbers!=null;
 	}
 
 	public boolean hasImmutableNaturalId() {
 		return hasImmutableNaturalId;
 	}
 
 	public Set getSubclassEntityNames() {
 		return subclassEntityNames;
 	}
 
 	private boolean indicatesCollection(Type type) {
 		if ( type.isCollectionType() ) {
 			return true;
 		}
 		else if ( type.isComponentType() ) {
 			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
 			for ( int i = 0; i < subtypes.length; i++ ) {
 				if ( indicatesCollection( subtypes[i] ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getRootName() {
 		return rootName;
 	}
 
 	public EntityType getEntityType() {
 		return entityType;
 	}
 
 	public IdentifierProperty getIdentifierProperty() {
 		return identifierProperty;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public int getVersionPropertyIndex() {
 		return versionPropertyIndex;
 	}
 
 	public VersionProperty getVersionProperty() {
 		if ( NO_VERSION_INDX == versionPropertyIndex ) {
 			return null;
 		}
 		else {
 			return ( VersionProperty ) properties[ versionPropertyIndex ];
 		}
 	}
 
 	public StandardProperty[] getProperties() {
 		return properties;
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			throw new HibernateException("Unable to resolve property: " + propertyName);
 		}
 		return index.intValue();
 	}
 
 	public Integer getPropertyIndexOrNull(String propertyName) {
 		return (Integer) propertyIndexes.get( propertyName );
 	}
 
 	public boolean hasCollections() {
 		return hasCollections;
 	}
 
 	public boolean hasMutableProperties() {
 		return hasMutableProperties;
 	}
 
 	public boolean hasNonIdentifierPropertyNamedId() {
 		return hasNonIdentifierPropertyNamedId;
 	}
 
 	public boolean hasLazyProperties() {
 		return hasLazyProperties;
 	}
 
 	public boolean hasCascades() {
 		return hasCascades;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public int getOptimisticLockMode() {
 		return optimisticLockMode;
 	}
 
 	public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public String getSuperclass() {
 		return superclass;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public boolean isInherited() {
 		return inherited;
 	}
 
 	public boolean hasSubclasses() {
 		return hasSubclasses;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	/**
 	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
 	 *
 	 * @param inheritenceClass The class for which to resolve the entity-name.
 	 * @return The mapped entity-name, or null if no such mapping was found.
 	 */
 	public String findEntityNameByEntityClass(Class inheritenceClass) {
 		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
 	}
 
 	@Override
     public String toString() {
 		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Type[] getPropertyTypes() {
 		return propertyTypes;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return propertyLaziness;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return propertyUpdateability;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return propertyCheckability;
 	}
 
 	public boolean[] getNonlazyPropertyUpdateability() {
 		return nonlazyPropertyUpdateability;
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return propertyInsertability;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return insertInclusions;
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return updateInclusions;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return propertyVersionability;
 	}
 
 	public CascadeStyle[] getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	public boolean hasInsertGeneratedValues() {
 		return hasInsertGeneratedValues;
 	}
 
 	public boolean hasUpdateGeneratedValues() {
 		return hasUpdateGeneratedValues;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
index 373ebfcc8b..d035eb9712 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
@@ -1,300 +1,301 @@
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
 package org.hibernate.tuple.entity;
 import java.io.Serializable;
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.property.Getter;
 import org.hibernate.tuple.Tuplizer;
 
 /**
  * Defines further responsibilities reagarding tuplization based on
  * a mapped entity.
  * <p/>
- * EntityTuplizer implementations should have the following constructor signature:
+ * EntityTuplizer implementations should have the following constructor signatures:
  *      (org.hibernate.tuple.entity.EntityMetamodel, org.hibernate.mapping.PersistentClass)
+ *      (org.hibernate.tuple.entity.EntityMetamodel, org.hibernate.metamodel.binding.EntityBinding)
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface EntityTuplizer extends Tuplizer {
 	/**
 	 * Return the entity-mode handled by this tuplizer instance.
 	 *
 	 * @return The entity-mode
 	 */
 	public EntityMode getEntityMode();
 
     /**
      * Create an entity instance initialized with the given identifier.
      *
      * @param id The identifier value for the entity to be instantiated.
      * @return The instantiated entity.
      * @throws HibernateException
 	 *
 	 * @deprecated Use {@link #instantiate(Serializable, SessionImplementor)} instead.
 	 * @noinspection JavaDoc
      */
 	public Object instantiate(Serializable id) throws HibernateException;
 
     /**
      * Create an entity instance initialized with the given identifier.
      *
      * @param id The identifier value for the entity to be instantiated.
 	 * @param session The session from which is requests originates
 	 *
      * @return The instantiated entity.
      */
 	public Object instantiate(Serializable id, SessionImplementor session);
 
     /**
      * Extract the identifier value from the given entity.
      *
      * @param entity The entity from which to extract the identifier value.
 	 *
      * @return The identifier value.
 	 *
      * @throws HibernateException If the entity does not define an identifier property, or an
      * error occurs accessing its value.
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead.
      */
 	public Serializable getIdentifier(Object entity) throws HibernateException;
 
     /**
      * Extract the identifier value from the given entity.
      *
      * @param entity The entity from which to extract the identifier value.
 	 * @param session The session from which is requests originates
 	 *
      * @return The identifier value.
      */
 	public Serializable getIdentifier(Object entity, SessionImplementor session);
 
     /**
      * Inject the identifier value into the given entity.
      * </p>
      * Has no effect if the entity does not define an identifier property
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 *
 	 * @deprecated Use {@link #setIdentifier(Object, Serializable, SessionImplementor)} instead.
 	 * @noinspection JavaDoc
      */
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException;
 
     /**
      * Inject the identifier value into the given entity.
      * </p>
      * Has no effect if the entity does not define an identifier property
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 * @param session The session from which is requests originates
      */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
 
 	/**
 	 * Inject the given identifier and version into the entity, in order to
 	 * "roll back" to their original values.
 	 *
 	 * @param entity The entity for which to reset the id/version values
 	 * @param currentId The identifier value to inject into the entity.
 	 * @param currentVersion The version value to inject into the entity.
 	 *
 	 * @deprecated Use {@link #resetIdentifier(Object, Serializable, Object, SessionImplementor)} instead
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion);
 
 	/**
 	 * Inject the given identifier and version into the entity, in order to
 	 * "roll back" to their original values.
 	 *
 	 * @param entity The entity for which to reset the id/version values
 	 * @param currentId The identifier value to inject into the entity.
 	 * @param currentVersion The version value to inject into the entity.
 	 * @param session The session from which the request originated
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
 
     /**
      * Extract the value of the version property from the given entity.
      *
      * @param entity The entity from which to extract the version value.
      * @return The value of the version property, or null if not versioned.
      * @throws HibernateException
      */
 	public Object getVersion(Object entity) throws HibernateException;
 
 	/**
 	 * Inject the value of a particular property.
 	 *
 	 * @param entity The entity into which to inject the value.
 	 * @param i The property's index.
 	 * @param value The property value to inject.
 	 * @throws HibernateException
 	 */
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException;
 
 	/**
 	 * Inject the value of a particular property.
 	 *
 	 * @param entity The entity into which to inject the value.
 	 * @param propertyName The name of the property.
 	 * @param value The property value to inject.
 	 * @throws HibernateException
 	 */
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException;
 
 	/**
 	 * Extract the values of the insertable properties of the entity (including backrefs)
 	 *
 	 * @param entity The entity from which to extract.
 	 * @param mergeMap a map of instances being merged to merged instances
 	 * @param session The session in which the resuest is being made.
 	 * @return The insertable property values.
 	 * @throws HibernateException
 	 */
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Extract the value of a particular property from the given entity.
 	 *
 	 * @param entity The entity from which to extract the property value.
 	 * @param propertyName The name of the property for which to extract the value.
 	 * @return The current value of the given property on the given entity.
 	 * @throws HibernateException
 	 */
 	public Object getPropertyValue(Object entity, String propertyName) throws HibernateException;
 
     /**
      * Called just after the entities properties have been initialized.
      *
      * @param entity The entity being initialized.
      * @param lazyPropertiesAreUnfetched Are defined lazy properties currently unfecthed
      * @param session The session initializing this entity.
      */
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
 
 	/**
 	 * Does this entity, for this mode, present a possibility for proxying?
 	 *
 	 * @return True if this tuplizer can generate proxies for this entity.
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Generates an appropriate proxy representation of this entity for this
 	 * entity-mode.
 	 *
 	 * @param id The id of the instance for which to generate a proxy.
 	 * @param session The session to which the proxy should be bound.
 	 * @return The generate proxies.
 	 * @throws HibernateException Indicates an error generating the proxy.
 	 */
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Does the {@link #getMappedClass() class} managed by this tuplizer implement
 	 * the {@link org.hibernate.classic.Lifecycle} interface.
 	 *
 	 * @return True if the Lifecycle interface is implemented; false otherwise.
 	 */
 	public boolean isLifecycleImplementor();
 
 	/**
 	 * Returns the java class to which generated proxies will be typed.
 	 * <p/>
 	 * todo : look at fully encapsulating {@link org.hibernate.engine.spi.PersistenceContext#narrowProxy} here,
 	 * since that is the only external use of this method
 	 *
 	 * @return The java class to which generated proxies will be typed
 	 */
 	public Class getConcreteProxyClass();
 	
     /**
      * Does the given entity instance have any currently uninitialized lazy properties?
      *
      * @param entity The entity to be check for uninitialized lazy properties.
      * @return True if uninitialized lazy properties were found; false otherwise.
      */
 	public boolean hasUninitializedLazyProperties(Object entity);
 	
 	/**
 	 * Is it an instrumented POJO?
 	 */
 	public boolean isInstrumented();
 
 	/**
 	 * Get any {@link EntityNameResolver EntityNameResolvers} associated with this {@link Tuplizer}.
 	 *
 	 * @return The associated resolvers.  May be null or empty.
 	 */
 	public EntityNameResolver[] getEntityNameResolvers();
 
 	/**
 	 * Given an entity instance, determine the most appropriate (most targeted) entity-name which represents it.
 	 * This is called in situations where we already know an entity name for the given entityInstance; we are being
 	 * asked to determine if there is a more appropriate entity-name to use, specifically within an inheritence
 	 * hierarchy.
 	 * <p/>
 	 * For example, consider a case where a user calls <tt>session.update( "Animal", cat );</tt>.  Here, the
 	 * user has explicitly provided <tt>Animal</tt> as the entity-name.  However, they have passed in an instance
 	 * of <tt>Cat</tt> which is a subclass of <tt>Animal</tt>.  In this case, we would return <tt>Cat</tt> as the
 	 * entity-name.
 	 * <p/>
 	 * <tt>null</tt> may be returned from calls to this method.  The meaining of <tt>null</tt> in that case is assumed
 	 * to be that we should use whatever explicit entity-name the user provided (<tt>Animal</tt> rather than <tt>Cat</tt>
 	 * in the example above).
 	 *
 	 * @param entityInstance The entity instance.
 	 * @param factory Reference to the SessionFactory.
 	 *
 	 * @return The most appropriate entity name to use.
 	 *
 	 * @throws HibernateException If we are unable to determine an entity-name within the inheritence hierarchy.
 	 */
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory);
 
 	/**
 	 * Retrieve the getter for the identifier property.  May return null.
 	 *
 	 * @return The getter for the identifier property.
 	 */
 	public Getter getIdentifierGetter();
 
 	/**
 	 * Retrieve the getter for the version property.  May return null.
 	 *
 	 * @return The getter for the version property.
 	 */
 	public Getter getVersionGetter();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
index 21e9733ef9..0cf17f85a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
@@ -1,171 +1,252 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.metamodel.binding.EntityBinding;
 
 /**
  * A registry allowing users to define the default {@link EntityTuplizer} class to use per {@link EntityMode}.
  *
  * @author Steve Ebersole
  */
 public class EntityTuplizerFactory implements Serializable {
 	public static final Class[] ENTITY_TUP_CTOR_SIG = new Class[] { EntityMetamodel.class, PersistentClass.class };
+	public static final Class[] ENTITY_TUP_CTOR_SIG_NEW = new Class[] { EntityMetamodel.class, EntityBinding.class };
 
 	private Map<EntityMode,Class<? extends EntityTuplizer>> defaultImplClassByMode = buildBaseMapping();
 
 	/**
 	 * Method allowing registration of the tuplizer class to use as default for a particular entity-mode.
 	 *
 	 * @param entityMode The entity-mode for which to register the tuplizer class
 	 * @param tuplizerClass The class to use as the default tuplizer for the given entity-mode.
 	 */
 	public void registerDefaultTuplizerClass(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass) {
 		assert isEntityTuplizerImplementor( tuplizerClass )
 				: "Specified tuplizer class [" + tuplizerClass.getName() + "] does not implement " + EntityTuplizer.class.getName();
-		assert hasProperConstructor( tuplizerClass )
+		// TODO: for now we need constructors for both PersistentClass and EntityBinding
+		assert hasProperConstructor( tuplizerClass, ENTITY_TUP_CTOR_SIG )
+				: "Specified tuplizer class [" + tuplizerClass.getName() + "] is not properly instantiatable";
+		assert hasProperConstructor( tuplizerClass, ENTITY_TUP_CTOR_SIG_NEW )
 				: "Specified tuplizer class [" + tuplizerClass.getName() + "] is not properly instantiatable";
-
 		defaultImplClassByMode.put( entityMode, tuplizerClass );
 	}
 
 	/**
 	 * Construct an instance of the given tuplizer class.
 	 *
 	 * @param tuplizerClassName The name of the tuplizer class to instantiate
 	 * @param metamodel The metadata for the entity.
 	 * @param persistentClass The mapping info for the entity.
 	 *
 	 * @return The instantiated tuplizer
 	 *
 	 * @throws HibernateException If class name cannot be resolved to a class reference, or if the
 	 * {@link Constructor#newInstance} call fails.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public EntityTuplizer constructTuplizer(
 			String tuplizerClassName,
 			EntityMetamodel metamodel,
 			PersistentClass persistentClass) {
 		try {
 			Class<? extends EntityTuplizer> tuplizerClass = ReflectHelper.classForName( tuplizerClassName );
 			return constructTuplizer( tuplizerClass, metamodel, persistentClass );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new HibernateException( "Could not locate specified tuplizer class [" + tuplizerClassName + "]" );
 		}
 	}
 
 	/**
 	 * Construct an instance of the given tuplizer class.
 	 *
+	 * @param tuplizerClassName The name of the tuplizer class to instantiate
+	 * @param metamodel The metadata for the entity.
+	 * @param entityBinding The mapping info for the entity.
+	 *
+	 * @return The instantiated tuplizer
+	 *
+	 * @throws HibernateException If class name cannot be resolved to a class reference, or if the
+	 * {@link Constructor#newInstance} call fails.
+	 */
+	@SuppressWarnings({ "unchecked" })
+	public EntityTuplizer constructTuplizer(
+			String tuplizerClassName,
+			EntityMetamodel metamodel,
+			EntityBinding entityBinding) {
+		try {
+			Class<? extends EntityTuplizer> tuplizerClass = ReflectHelper.classForName( tuplizerClassName );
+			return constructTuplizer( tuplizerClass, metamodel, entityBinding );
+		}
+		catch ( ClassNotFoundException e ) {
+			throw new HibernateException( "Could not locate specified tuplizer class [" + tuplizerClassName + "]" );
+		}
+	}
+
+	/**
+	 * Construct an instance of the given tuplizer class.
+	 *
 	 * @param tuplizerClass The tuplizer class to instantiate
 	 * @param metamodel The metadata for the entity.
 	 * @param persistentClass The mapping info for the entity.
 	 *
 	 * @return The instantiated tuplizer
 	 *
 	 * @throws HibernateException if the {@link Constructor#newInstance} call fails.
 	 */
 	public EntityTuplizer constructTuplizer(
 			Class<? extends EntityTuplizer> tuplizerClass,
 			EntityMetamodel metamodel,
 			PersistentClass persistentClass) {
-		Constructor<? extends EntityTuplizer> constructor = getProperConstructor( tuplizerClass );
+		Constructor<? extends EntityTuplizer> constructor = getProperConstructor( tuplizerClass, ENTITY_TUP_CTOR_SIG );
 		assert constructor != null : "Unable to locate proper constructor for tuplizer [" + tuplizerClass.getName() + "]";
 		try {
 			return constructor.newInstance( metamodel, persistentClass );
 		}
 		catch ( Throwable t ) {
 			throw new HibernateException( "Unable to instantiate default tuplizer [" + tuplizerClass.getName() + "]", t );
 		}
 	}
 
 	/**
+	 * Construct an instance of the given tuplizer class.
+	 *
+	 * @param tuplizerClass The tuplizer class to instantiate
+	 * @param metamodel The metadata for the entity.
+	 * @param entityBinding The mapping info for the entity.
+	 *
+	 * @return The instantiated tuplizer
+	 *
+	 * @throws HibernateException if the {@link Constructor#newInstance} call fails.
+	 */
+	public EntityTuplizer constructTuplizer(
+			Class<? extends EntityTuplizer> tuplizerClass,
+			EntityMetamodel metamodel,
+			EntityBinding entityBinding) {
+		Constructor<? extends EntityTuplizer> constructor = getProperConstructor( tuplizerClass, ENTITY_TUP_CTOR_SIG_NEW );
+		assert constructor != null : "Unable to locate proper constructor for tuplizer [" + tuplizerClass.getName() + "]";
+		try {
+			return constructor.newInstance( metamodel, entityBinding );
+		}
+		catch ( Throwable t ) {
+			throw new HibernateException( "Unable to instantiate default tuplizer [" + tuplizerClass.getName() + "]", t );
+		}
+	}
+
+	/**
 	 * Construct am instance of the default tuplizer for the given entity-mode.
 	 *
 	 * @param entityMode The entity mode for which to build a default tuplizer.
 	 * @param metamodel The entity metadata.
 	 * @param persistentClass The entity mapping info.
 	 *
 	 * @return The instantiated tuplizer
 	 *
 	 * @throws HibernateException If no default tuplizer found for that entity-mode; may be re-thrown from
 	 * {@link #constructTuplizer} too.
 	 */
 	public EntityTuplizer constructDefaultTuplizer(
 			EntityMode entityMode,
 			EntityMetamodel metamodel,
 			PersistentClass persistentClass) {
 		Class<? extends EntityTuplizer> tuplizerClass = defaultImplClassByMode.get( entityMode );
 		if ( tuplizerClass == null ) {
 			throw new HibernateException( "could not determine default tuplizer class to use [" + entityMode + "]" );
 		}
 
 		return constructTuplizer( tuplizerClass, metamodel, persistentClass );
 	}
 
+	/**
+	 * Construct am instance of the default tuplizer for the given entity-mode.
+	 *
+	 * @param entityMode The entity mode for which to build a default tuplizer.
+	 * @param metamodel The entity metadata.
+	 * @param entityBinding The entity mapping info.
+	 *
+	 * @return The instantiated tuplizer
+	 *
+	 * @throws HibernateException If no default tuplizer found for that entity-mode; may be re-thrown from
+	 * {@link #constructTuplizer} too.
+	 */
+	public EntityTuplizer constructDefaultTuplizer(
+			EntityMode entityMode,
+			EntityMetamodel metamodel,
+			EntityBinding entityBinding) {
+		Class<? extends EntityTuplizer> tuplizerClass = defaultImplClassByMode.get( entityMode );
+		if ( tuplizerClass == null ) {
+			throw new HibernateException( "could not determine default tuplizer class to use [" + entityMode + "]" );
+		}
+
+		return constructTuplizer( tuplizerClass, metamodel, entityBinding );
+	}
+
 	private boolean isEntityTuplizerImplementor(Class tuplizerClass) {
 		return ReflectHelper.implementsInterface( tuplizerClass, EntityTuplizer.class );
 	}
 
-	private boolean hasProperConstructor(Class<? extends EntityTuplizer> tuplizerClass) {
-		return getProperConstructor( tuplizerClass ) != null
+	private boolean hasProperConstructor(Class<? extends EntityTuplizer> tuplizerClass, Class[] constructorArgs) {
+		return getProperConstructor( tuplizerClass, constructorArgs ) != null
 				&& ! ReflectHelper.isAbstractClass( tuplizerClass );
 	}
 
-	private Constructor<? extends EntityTuplizer> getProperConstructor(Class<? extends EntityTuplizer> clazz) {
+	private Constructor<? extends EntityTuplizer> getProperConstructor(
+			Class<? extends EntityTuplizer> clazz,
+			Class[] constructorArgs) {
 		Constructor<? extends EntityTuplizer> constructor = null;
 		try {
-			constructor = clazz.getDeclaredConstructor( ENTITY_TUP_CTOR_SIG );
+			constructor = clazz.getDeclaredConstructor( constructorArgs );
 			if ( ! ReflectHelper.isPublic( constructor ) ) {
 				try {
 					// found a constructor, but it was not publicly accessible so try to request accessibility
 					constructor.setAccessible( true );
 				}
 				catch ( SecurityException e ) {
 					constructor = null;
 				}
 			}
 		}
 		catch ( NoSuchMethodException ignore ) {
 		}
 
 		return constructor;
 	}
 
 	private static Map<EntityMode,Class<? extends EntityTuplizer>> buildBaseMapping() {
 		Map<EntityMode,Class<? extends EntityTuplizer>> map = new ConcurrentHashMap<EntityMode,Class<? extends EntityTuplizer>>();
 		map.put( EntityMode.POJO, PojoEntityTuplizer.class );
 		map.put( EntityMode.MAP, DynamicMapEntityTuplizer.class );
 		return map;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index 6244f7213c..626b9ac2ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,370 +1,554 @@
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
 package org.hibernate.tuple.entity;
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
+import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Subclass;
+import org.hibernate.metamodel.binding.AttributeBinding;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
+import org.hibernate.property.PropertyAccessor;
+import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.PojoInstantiator;
 import org.hibernate.type.CompositeType;
 
 /**
  * An {@link EntityTuplizer} specific to the pojo entity mode.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class PojoEntityTuplizer extends AbstractEntityTuplizer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PojoEntityTuplizer.class.getName());
 
 	private final Class mappedClass;
 	private final Class proxyInterface;
 	private final boolean lifecycleImplementor;
 	private final Set lazyPropertyNames = new HashSet();
 	private final ReflectionOptimizer optimizer;
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getMappedClass();
 		this.proxyInterface = mappedEntity.getProxyInterface();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 
 		Iterator iter = mappedEntity.getPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property property = (Property) iter.next();
 			if ( property.isLazy() ) {
 				lazyPropertyNames.add( property.getName() );
 			}
 		}
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[i].getMethodName();
 			setterNames[i] = setters[i].getMethodName();
 			propTypes[i] = getters[i].getReturnType();
 		}
 
 		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 
 	}
 
+	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
+		super( entityMetamodel, mappedEntity );
+		this.mappedClass = mappedEntity.getEntity().getJavaType().getClassReference();
+		this.proxyInterface = mappedEntity.getProxyInterfaceType().getClassReference();
+		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
+
+		for ( AttributeBinding property : mappedEntity.getAttributeBindingClosure() ) {
+			if ( property.isLazy() ) {
+				lazyPropertyNames.add( property.getAttribute().getName() );
+			}
+		}
+
+		String[] getterNames = new String[propertySpan];
+		String[] setterNames = new String[propertySpan];
+		Class[] propTypes = new Class[propertySpan];
+		for ( int i = 0; i < propertySpan; i++ ) {
+			getterNames[i] = getters[ i ].getMethodName();
+			setterNames[i] = setters[ i ].getMethodName();
+			propTypes[i] = getters[ i ].getReturnType();
+		}
+
+		if ( hasCustomAccessors || ! Environment.useReflectionOptimizer() ) {
+			optimizer = null;
+		}
+		else {
+			// todo : YUCK!!!
+			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
+					mappedClass, getterNames, setterNames, propTypes
+			);
+//			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
+//					mappedClass, getterNames, setterNames, propTypes
+//			);
+		}
+	}
+
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
         // determine all interfaces needed by the resulting proxy
 		HashSet<Class> proxyInterfaces = new HashSet<Class>();
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Class mappedClass = persistentClass.getMappedClass();
 		Class proxyInterface = persistentClass.getProxyInterface();
 
 		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
 			if ( !proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		Iterator subclasses = persistentClass.getSubclassIterator();
 		while ( subclasses.hasNext() ) {
 			final Subclass subclass = ( Subclass ) subclasses.next();
 			final Class subclassProxy = subclass.getProxyInterface();
 			final Class subclassClass = subclass.getMappedClass();
 			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
 				if ( !subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		Iterator properties = persistentClass.getPropertyIterator();
 		Class clazz = persistentClass.getMappedClass();
 		while ( properties.hasNext() ) {
 			Property property = (Property) properties.next();
 			Method method = property.getGetter(clazz).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.gettersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 			method = property.getSetter(clazz).getMethod();
             if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.settersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 		}
 
 		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
 		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
 
 		ProxyFactory pf = buildProxyFactoryInternal( persistentClass, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					persistentClass.hasEmbeddedIdentifier() ?
 			                (CompositeType) persistentClass.getIdentifier().getType() :
 			                null
 			);
 		}
 		catch ( HibernateException he ) {
             LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Instantiator buildInstantiator(PersistentClass persistentClass) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( persistentClass, null );
 		}
 		else {
 			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
+	protected ProxyFactory buildProxyFactory(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
+		// determine the id getter and setter methods from the proxy interface (if any)
+		// determine all interfaces needed by the resulting proxy
+		HashSet<Class> proxyInterfaces = new HashSet<Class>();
+		proxyInterfaces.add( HibernateProxy.class );
+
+		Class mappedClass = entityBinding.getEntity().getJavaType().getClassReference();
+		Class proxyInterface = entityBinding.getProxyInterfaceType().getClassReference();
+
+		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
+			if ( ! proxyInterface.isInterface() ) {
+				throw new MappingException(
+						"proxy must be either an interface, or the class itself: " + getEntityName()
+				);
+			}
+			proxyInterfaces.add( proxyInterface );
+		}
+
+		if ( mappedClass.isInterface() ) {
+			proxyInterfaces.add( mappedClass );
+		}
+
+		// TODO: fix when it's possible to get subclasses from an EntityBinding
+		//Iterator subclasses = entityBinding.getSubclassIterator();
+		//while ( subclasses.hasNext() ) {
+		//	final Subclass subclass = ( Subclass ) subclasses.next();
+		//	final Class subclassProxy = subclass.getProxyInterface();
+		//	final Class subclassClass = subclass.getMappedClass();
+		//	if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
+		//		if ( !subclassProxy.isInterface() ) {
+		//			throw new MappingException(
+		//					"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
+		//			);
+		//		}
+		//		proxyInterfaces.add( subclassProxy );
+		//	}
+		//}
+
+		for ( AttributeBinding property : entityBinding.getAttributeBindings() ) {
+			Method method = getGetter( property ).getMethod();
+			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
+				LOG.gettersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
+			}
+			method = getSetter( property ).getMethod();
+			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
+				LOG.settersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
+			}
+		}
+
+		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
+		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
+
+		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
+				null :
+		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
+		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
+				null :
+		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
+
+		ProxyFactory pf = buildProxyFactoryInternal( entityBinding, idGetter, idSetter );
+		try {
+			pf.postInstantiate(
+					getEntityName(),
+					mappedClass,
+					proxyInterfaces,
+					proxyGetIdentifierMethod,
+					proxySetIdentifierMethod,
+					entityBinding.getEntityIdentifier().isEmbedded() ?
+			                ( CompositeType ) entityBinding
+									.getEntityIdentifier()
+									.getValueBinding()
+									.getHibernateTypeDescriptor()
+									.getExplicitType() :
+			                null
+			);
+		}
+		catch ( HibernateException he ) {
+			LOG.unableToCreateProxyFactory(getEntityName(), he);
+			pf = null;
+		}
+		return pf;
+	}
+
+	protected ProxyFactory buildProxyFactoryInternal(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
+		// TODO : YUCK!!!  fix after HHH-1907 is complete
+		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
+//		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+	protected Instantiator buildInstantiator(EntityBinding entityBinding) {
+		if ( optimizer == null ) {
+			return new PojoInstantiator( entityBinding, null );
+		}
+		else {
+			return new PojoInstantiator( entityBinding, optimizer.getInstantiationOptimizer() );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
     public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			setPropertyValuesWithOptimizer( entity, values );
 		}
 		else {
 			super.setPropertyValues( entity, values );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public Object[] getPropertyValues(Object entity) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValues( entity );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValuesToInsert( entity, mergeMap, session );
 		}
 	}
 
 	protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
 		optimizer.getAccessOptimizer().setPropertyValues( object, values );
 	}
 
 	protected Object[] getPropertyValuesWithOptimizer(Object object) {
 		return optimizer.getAccessOptimizer().getPropertyValues( object );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class getMappedClass() {
 		return mappedClass;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public boolean isLifecycleImplementor() {
 		return lifecycleImplementor;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
+	@Override
+	protected Getter buildPropertyGetter(AttributeBinding mappedProperty) {
+		return getGetter( mappedProperty );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+	protected Setter buildPropertySetter(AttributeBinding mappedProperty) {
+		return getSetter( mappedProperty );
+	}
+
+	private Getter getGetter(AttributeBinding mappedProperty)  throws PropertyNotFoundException, MappingException {
+		return getPropertyAccessor( mappedProperty ).getGetter(
+				mappedProperty.getEntityBinding().getEntity().getJavaType().getClassReference(),
+				mappedProperty.getAttribute().getName()
+		);
+	}
+
+	private Setter getSetter(AttributeBinding mappedProperty) throws PropertyNotFoundException, MappingException {
+		return getPropertyAccessor( mappedProperty ).getSetter(
+				mappedProperty.getEntityBinding().getEntity().getJavaType().getClassReference(),
+				mappedProperty.getAttribute().getName()
+		);
+	}
+
+	private PropertyAccessor getPropertyAccessor(AttributeBinding mappedProperty) throws MappingException {
+		// TODO: Fix this then backrefs are working in new metamodel
+		return PropertyAccessorFactory.getPropertyAccessor(
+				mappedProperty.getEntityBinding().getEntity().getJavaType().getClassReference(),
+				mappedProperty.getPropertyAccessorName()
+		);
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
 	public Class getConcreteProxyClass() {
 		return proxyInterface;
 	}
 
     //TODO: need to make the majority of this functionality into a top-level support class for custom impl support
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		if ( isInstrumented() ) {
 			Set lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
 					lazyPropertyNames : null;
 			//TODO: if we support multiple fetch groups, we would need
 			//      to clone the set of lazy properties!
 			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     public boolean hasUninitializedLazyProperties(Object entity) {
 		if ( getEntityMetamodel().hasLazyProperties() ) {
 			FieldInterceptor callback = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			return callback != null && !callback.isInitialized();
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isInstrumented() {
 		return FieldInterceptionHelper.isInstrumented( getMappedClass() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		final Class concreteEntityClass = entityInstance.getClass();
 		if ( concreteEntityClass == getMappedClass() ) {
 			return getEntityName();
 		}
 		else {
 			String entityName = getEntityMetamodel().findEntityNameByEntityClass( concreteEntityClass );
 			if ( entityName == null ) {
 				throw new HibernateException(
 						"Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
 								+ " expected instance/subclass of [" + getEntityName() + "]"
 				);
 			}
 			return entityName;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityTuplizer.java b/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityTuplizer.java
index f47d0e33db..433919cfdd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityTuplizer.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityTuplizer.java
@@ -1,86 +1,91 @@
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
 package org.hibernate.test.dynamicentity.tuplizer2;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.test.dynamicentity.ProxyHelper;
 import org.hibernate.test.dynamicentity.tuplizer.MyEntityInstantiator;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.PojoEntityTuplizer;
 
 /**
  * @author Steve Ebersole
  */
 public class MyEntityTuplizer extends PojoEntityTuplizer {
 
 	public MyEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 	}
 
+	public MyEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
+		super( entityMetamodel, mappedEntity );
+	}
+
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return new EntityNameResolver[] { MyEntityNameResolver.INSTANCE };
 	}
 
 	protected Instantiator buildInstantiator(PersistentClass persistentClass) {
 		return new MyEntityInstantiator( persistentClass.getEntityName() );
 	}
 
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		String entityName = ProxyHelper.extractEntityName( entityInstance );
 		if ( entityName == null ) {
 			entityName = super.determineConcreteSubclassEntityName( entityInstance, factory );
 		}
 		return entityName;
 	}
 
 	protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// allows defining a custom proxy factory, which is responsible for
 		// generating lazy proxies for a given entity.
 		//
 		// Here we simply use the default...
 		return super.buildProxyFactory( persistentClass, idGetter, idSetter );
 	}
 
 	public static class MyEntityNameResolver implements EntityNameResolver {
 		public static final MyEntityNameResolver INSTANCE = new MyEntityNameResolver();
 
 		public String resolveEntityName(Object entity) {
 			return ProxyHelper.extractEntityName( entity );
 		}
 
 		public boolean equals(Object obj) {
 			return getClass().equals( obj.getClass() );
 		}
 
 		public int hashCode() {
 			return getClass().hashCode();
 		}
 	}
 }
\ No newline at end of file
