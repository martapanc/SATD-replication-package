diff --git a/hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java b/hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
index bbc277d898..2c40c4677b 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
@@ -1,140 +1,154 @@
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
 package org.hibernate.id.factory;
 
 import java.io.Serializable;
 import java.util.Properties;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.persistence.GenerationType;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.id.Assigned;
 import org.hibernate.id.Configurable;
 import org.hibernate.id.ForeignGenerator;
 import org.hibernate.id.GUIDGenerator;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.IncrementGenerator;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.SelectGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.SequenceIdentityGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.id.enhanced.TableGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.Type;
 
 /**
  * Basic <tt>templated</tt> support for {@link IdentifierGeneratorFactory} implementations.
  *
  * @author Steve Ebersole
  */
 public class DefaultIdentifierGeneratorFactory implements IdentifierGeneratorFactory, Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultIdentifierGeneratorFactory.class.getName());
 
 	private transient Dialect dialect;
 	private ConcurrentHashMap<String, Class> generatorStrategyToClassNameMap = new ConcurrentHashMap<String, Class>();
 
 	/**
+	 * Constructs a new DefaultIdentifierGeneratorFactory
+	 *
+	 * @param dialect The dialect.
+	 */
+	public DefaultIdentifierGeneratorFactory(Dialect dialect) {
+		this();
+		this.dialect = dialect;
+	}
+
+	/**
 	 * Constructs a new DefaultIdentifierGeneratorFactory.
 	 */
 	public DefaultIdentifierGeneratorFactory() {
 		register( "uuid2", UUIDGenerator.class );
 		register( "guid", GUIDGenerator.class );			// can be done with UUIDGenerator + strategy
 		register( "uuid", UUIDHexGenerator.class );			// "deprecated" for new use
 		register( "uuid.hex", UUIDHexGenerator.class ); 	// uuid.hex is deprecated
 		register( "hilo", TableHiLoGenerator.class );
 		register( "assigned", Assigned.class );
 		register( "identity", IdentityGenerator.class );
 		register( "select", SelectGenerator.class );
 		register( "sequence", SequenceGenerator.class );
 		register( "seqhilo", SequenceHiLoGenerator.class );
 		register( "increment", IncrementGenerator.class );
 		register( "foreign", ForeignGenerator.class );
 		register( "sequence-identity", SequenceIdentityGenerator.class );
 		register( "enhanced-sequence", SequenceStyleGenerator.class );
 		register( "enhanced-table", TableGenerator.class );
 	}
 
 	public void register(String strategy, Class generatorClass) {
 		LOG.debugf( "Registering IdentifierGenerator strategy [%s] -> [%s]", strategy, generatorClass.getName() );
 		final Class previous = generatorStrategyToClassNameMap.put( strategy, generatorClass );
 		if ( previous != null ) {
 			LOG.debugf( "    - overriding [%s]", previous.getName() );
 		}
 	}
 
+	@Override
+	public Dialect getDialect() {
+		return dialect;
+	}
 
 	@Override
 	public void setDialect(Dialect dialect) {
         LOG.debugf( "Setting dialect [%s]", dialect );
 		this.dialect = dialect;
 	}
 
 	@Override
 	public IdentifierGenerator createIdentifierGenerator(String strategy, Type type, Properties config) {
 		try {
 			Class clazz = getIdentifierGeneratorClass( strategy );
 			IdentifierGenerator identifierGenerator = ( IdentifierGenerator ) clazz.newInstance();
 			if ( identifierGenerator instanceof Configurable ) {
 				( ( Configurable ) identifierGenerator ).configure( type, config, dialect );
 			}
 			return identifierGenerator;
 		}
 		catch ( Exception e ) {
 			final String entityName = config.getProperty( IdentifierGenerator.ENTITY_NAME );
 			throw new MappingException( String.format( "Could not instantiate id generator [entity-name=%s]", entityName ), e );
 		}
 	}
 
 	@Override
 	public Class getIdentifierGeneratorClass(String strategy) {
 		if ( "native".equals( strategy ) ) {
 			return dialect.getNativeIdentifierGeneratorClass();
 		}
 
 		Class generatorClass = generatorStrategyToClassNameMap.get( strategy );
 		try {
 			if ( generatorClass == null ) {
 				generatorClass = ReflectHelper.classForName( strategy );
 			}
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new MappingException( String.format( "Could not interpret id generator strategy [%s]", strategy ) );
 		}
 		return generatorClass;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java b/hibernate-core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java
index d41fdfea73..30f81ea54c 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/factory/IdentifierGeneratorFactory.java
@@ -1,65 +1,71 @@
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
 package org.hibernate.id.factory;
 import java.util.Properties;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.type.Type;
 
 /**
  * Contract for a <tt>factory</tt> of {@link IdentifierGenerator} instances.
  *
  * @author Steve Ebersole
  */
 public interface IdentifierGeneratorFactory {
+	/**
+	 * Get the dialect.
+	 *
+	 * @return the dialect
+	 */
+	public Dialect getDialect();
 
 	/**
 	 * Allow injection of the dialect to use.
 	 *
 	 * @param dialect The dialect
 	 * @deprecated The intention is that Dialect should be required to be specified up-front and it would then get
 	 * ctor injected.
 	 */
 	public void setDialect(Dialect dialect);
 
 	/**
 	 * Given a strategy, retrieve the appropriate identifier generator instance.
 	 *
 	 * @param strategy The generation strategy.
 	 * @param type The mapping type for the identifier values.
 	 * @param config Any configuraion properties given in the generator mapping.
 	 *
 	 * @return The appropriate generator instance.
 	 */
 	public IdentifierGenerator createIdentifierGenerator(String strategy, Type type, Properties config);
 
 	/**
 	 * Retrieve the class that will be used as the {@link IdentifierGenerator} for the given strategy.
 	 *
 	 * @param strategy The strategy
 	 * @return The generator class.
 	 */
 	public Class getIdentifierGeneratorClass(String strategy);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 74acd5fa83..50aac19c74 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1612 +1,1609 @@
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
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
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
-	private final transient Map identifierGenerators;
+	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient Map<String, NamedQueryDefinition> namedQueries;
 	private final transient Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	private final transient Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
         private final transient JdbcServices jdbcServices;
         private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient Cache cacheAccess = new CacheImpl();
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 
 	@SuppressWarnings( {"unchecked"} )
 	public SessionFactoryImpl(
 			final Configuration cfg,
 	        Mapping mapping,
 			ServiceRegistry serviceRegistry,
 	        Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
 			public Interceptor getInterceptor() {
 				return cfg.getInterceptor();
 			}
 
 			@Override
 			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 				if ( entityNotFoundDelegate == null ) {
 					if ( cfg.getEntityNotFoundDelegate() != null ) {
 						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 					}
 					else {
 						entityNotFoundDelegate = new EntityNotFoundDelegate() {
 							public void handleEntityNotFound(String entityName, Serializable id) {
 								throw new ObjectNotFoundException( id, entityName );
 							}
 						};
 					}
 				}
 				return entityNotFoundDelegate;
 			}
 		};
 
 		this.settings = settings;
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
                 this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
                 this.dialect = this.jdbcServices.getDialect();
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
 		namedQueries = new HashMap<String, NamedQueryDefinition>( cfg.getNamedQueries() );
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>( cfg.getNamedSQLQueries() );
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>( cfg.getSqlResultSetMappings() );
 		imports = new HashMap<String,String>( cfg.getImports() );
 
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
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		// TODO: remove initialization of final variables; just setting to null to make compiler happy
 		this.sqlFunctionRegistry = null;
 
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		this.properties = createPropertiesFromMap(
 				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
 		// TODO: these should be moved into SessionFactoryOptions
 		this.settings = new SettingsFactory().buildSettings(
 				properties,
 				metadata.getServiceRegistry()
 		);
 
 		this.serviceRegistry =
 				metadata.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 
 		// TODO: get SQL functions from a new service
 		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
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
         LOG.debugf("Instantiating session factory with properties: %s", properties );
 
 		// TODO: get RegionFactory from service registry
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
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
 			// TODO: add Integrator.integrate(MetadataImplementor, ...)
 			// integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 
 		//Generators:
 
-		identifierGenerators = new HashMap();
+		identifierGenerators = new HashMap<String,IdentifierGenerator>();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
-				// TODO: create the IdentifierGenerator while the metadata is being build, then simply
-				// use EntityBinding.getIdentifierGenerator() (also remove getIdentifierGeneratorFactory from Mappings)
-				// TODO: this is broken; throws NullPointerException
-				//IdentifierGenerator generator = entityBinding.getEntityIdentifier().createIdentifierGenerator(
-				//		metadata.getIdentifierGeneratorFactory()
-				//);
-				//identifierGenerators.put( entityBinding.getEntity().getName(), generator );
+				identifierGenerators.put(
+						entityBinding.getEntity().getName(),
+						entityBinding.getEntityIdentifier().getIdentifierGenerator()
+				);
 			}
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		StringBuilder stringBuilder = new StringBuilder();
 		if ( settings.getCacheRegionPrefix() != null) {
 			stringBuilder
 					.append( settings.getCacheRegionPrefix() )
 					.append( '.' );
 		}
 		final String cacheRegionPrefix = stringBuilder.toString();
 
 		entityPersisters = new HashMap();
 		Map<String, RegionAccessStrategy> entityAccessStrategies = new HashMap<String, RegionAccessStrategy>();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		for ( EntityBinding model : metadata.getEntityBindings() ) {
 			// TODO: should temp table prep happen when metadata is being built?
 			//model.prepareTemporaryTables( metadata, getDialect() );
 			// cache region is defined by the root-class in the hierarchy...
 			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
 			EntityRegionAccessStrategy accessStrategy = null;
 			if ( settings.isSecondLevelCacheEnabled() &&
 					rootEntityBinding.getCaching() != null &&
 					model.getCaching() != null &&
 					model.getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getCaching().getRegion();
 				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getCaching().getAccessType();
 					LOG.trace("Building cache for entity data [" + model.getEntity().getName() + "]");
 					EntityRegion entityRegion =
 							settings.getRegionFactory().buildEntityRegion(
 									cacheRegionName,
 									properties,
 									CacheDataDescriptionImpl.decode( model )
 							);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model, accessStrategy, this, metadata
 			);
 			entityPersisters.put( model.getEntity().getName(), cp );
 			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap();
 		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a PluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"PluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			// TODO: Add PluralAttributeBinding.getCaching()
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				// TODO: is model.getAttribute().getName() the collection's role??? For now, assuming it is
                 LOG.trace("Building cache for collection data [" + model.getAttribute().getName() + "]");
 				CollectionRegion collectionRegion =
 						settings.getRegionFactory()
 								.buildCollectionRegion(
 										cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 								);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister =
 					serviceRegistry
 							.getService( PersisterFactory.class )
 							.createCollectionPersister( metadata, model, accessStrategy, this );
 			// TODO: is model.getAttribute().getName() the collection's role??? For now, assuming it is
 			collectionPersisters.put( model.getAttribute().getName(), persister.getCollectionMetadata() );
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
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		for ( NamedQueryDefinition namedQueryDefinition :  metadata.getNamedQueryDefinitions() ) {
 			namedQueries.put( namedQueryDefinition.getName(), namedQueryDefinition );
 		}
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>();
 		for ( NamedSQLQueryDefinition namedNativeQueryDefinition: metadata.getNamedNativeQueryDefinitions() ) {
 			namedSqlQueries.put( namedNativeQueryDefinition.getName(), namedNativeQueryDefinition );
 		}
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 		for( ResultSetMappingDefinition resultSetMappingDefinition : metadata.getResultSetMappingDefinitions() ) {
 			sqlResultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 		}
 		imports = new HashMap<String,String>();
 		for ( Map.Entry<String,String> importEntry : metadata.getImports() ) {
 			imports.put( importEntry.getKey(), importEntry.getValue() );
 		}
 
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
 			new SchemaExport( metadata ).create( false, true );
 		}
 		/*
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( metadata ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( metadata ).validate();
 		}
 		*/
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( metadata );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache( settings, properties );
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache( null, updateTimestampsCache, settings, properties );
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
 			if ( ! errors.isEmpty() ) {
 				Set keys = errors.keySet();
 				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
 				for ( Iterator<String> iterator = keys.iterator() ; iterator.hasNext() ; ) {
 					String queryName = iterator.next();
 					HibernateException e = ( HibernateException ) errors.get( queryName );
 					failingQueries.append( queryName );
                     if ( iterator.hasNext() ) failingQueries.append( ", " );
 					LOG.namedQueryError( queryName, e );
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.metamodel.binding.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.metamodel.binding.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
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
 				if ( associationType == null || ! associationType.isAssociationType() ) {
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
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
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
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
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
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
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
 						final Class mappedClass = testQueryable.getMappedClass();
 						if ( mappedClass!=null && clazz.isAssignableFrom( mappedClass ) ) {
 							final boolean assignableSuperclass;
 							if ( testQueryable.isInherited() ) {
 								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass();
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
 		return jdbcServices.getConnectionProvider();
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
 					null, 						// have to assume non tenancy
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
 					null,						// have to assume non tenancy
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
 		return sessionFactoryOptions.getEntityNotFoundDelegate();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
index 67898e6826..e3cc873501 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
@@ -1,105 +1,97 @@
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
 
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Binds the entity identifier.
  *
  * @author Steve Ebersole
  */
 public class EntityIdentifier {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			EntityIdentifier.class.getName()
 	);
 
 	private final EntityBinding entityBinding;
 	private SimpleAttributeBinding attributeBinding;
 	private IdentifierGenerator identifierGenerator;
 	private IdGenerator idGenerator;
 	private boolean isIdentifierMapper = false;
 	// todo : mappers, etc
 
 	/**
 	 * Create an identifier
 	 *
 	 * @param entityBinding the entity binding for which this instance is the id
 	 */
 	public EntityIdentifier(EntityBinding entityBinding) {
 		this.entityBinding = entityBinding;
 	}
 
 	public SimpleAttributeBinding getValueBinding() {
 		return attributeBinding;
 	}
 
 	public void setValueBinding(SimpleAttributeBinding attributeBinding) {
 		if ( this.attributeBinding != null ) {
 			// todo : error?  or just log?  For now throw exception and see what happens. Easier to see whether this
 			// method gets called multiple times
 			LOG.entityIdentifierValueBindingExists( entityBinding.getEntity().getName() );
 		}
 		this.attributeBinding = attributeBinding;
 	}
 
 	public void setIdGenerator(IdGenerator idGenerator) {
 		this.idGenerator = idGenerator;
 	}
 
 	public boolean isEmbedded() {
 		return attributeBinding.getValuesSpan()>1;
 	}
 
 	public boolean isIdentifierMapper() {
 		return isIdentifierMapper;
 	}
 
-	public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory factory) {
-		if ( identifierGenerator == null ) {
-			Properties props = new Properties();
-			if ( idGenerator != null ) {
-				props.putAll( idGenerator.getParameters() );
-			}
-			identifierGenerator = factory.createIdentifierGenerator(
-					idGenerator.getStrategy(),
-					getValueBinding().getHibernateTypeDescriptor().getResolvedTypeMapping(),
-					props
-			);
+	public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory factory, Properties properties) {
+		if ( idGenerator != null ) {
+			identifierGenerator = attributeBinding.createIdentifierGenerator( idGenerator, factory, properties );
 		}
 		return identifierGenerator;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/IdGenerator.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/IdGenerator.java
index 151a0c68f5..d882dc38b5 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/IdGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/IdGenerator.java
@@ -1,68 +1,75 @@
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
 package org.hibernate.metamodel.binding;
 
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.Map;
 
 /**
  * Identifier generator container, Useful to keep named generator in annotations
  *
  * @author Emmanuel Bernard
  */
 public class IdGenerator implements Serializable {
     private final String name;
     private final String strategy;
     private final Map<String, String> parameters;
 
     public IdGenerator( String name,
                         String strategy,
                         Map<String, String> parameters ) {
         this.name = name;
         this.strategy = strategy;
         this.parameters = parameters;
     }
 
     /**
      * @return identifier generator strategy
      */
     public String getStrategy() {
         return strategy;
     }
 
     /**
      * @return generator name
      */
     public String getName() {
         return name;
     }
 
     /**
      * @return generator configuration parameters
      */
     public Map<String, String> getParameters() {
-        return Collections.unmodifiableMap(parameters);
+		Map<String, String> returnedParameters;
+		if ( parameters == null ) {
+			returnedParameters = Collections.emptyMap();
+		}
+		else {
+			returnedParameters = Collections.unmodifiableMap(parameters);
+		}
+		return returnedParameters;
     }
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
index cc1d8c98da..8e03d1fdb2 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
@@ -1,162 +1,244 @@
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
 
+import java.util.Properties;
+
+import org.hibernate.MappingException;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.id.IdentifierGenerator;
+import org.hibernate.id.PersistentIdentifierGenerator;
+import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.mapping.PropertyGeneration;
-import org.hibernate.metamodel.domain.AbstractAttributeContainer;
+import org.hibernate.metamodel.relational.Column;
+import org.hibernate.metamodel.relational.Schema;
+import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.state.ColumnRelationalState;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class SimpleAttributeBinding extends AbstractAttributeBinding implements KeyValueBinding {
 	private boolean insertable;
 	private boolean updatable;
 	private PropertyGeneration generation;
 
 	private String propertyAccessorName;
 	private String unsavedValue;
 
 	private boolean forceNonNullable;
 	private boolean forceUnique;
 	private boolean keyCascadeDeleteEnabled;
 
 	private boolean includedInOptimisticLocking;
 	private MetaAttributeContext metaAttributeContext;
 
 	SimpleAttributeBinding(EntityBinding entityBinding, boolean forceNonNullable, boolean forceUnique) {
 		super( entityBinding );
 		this.forceNonNullable = forceNonNullable;
 		this.forceUnique = forceUnique;
 	}
 
 	public final SimpleAttributeBinding initialize(SimpleAttributeBindingState state) {
 		super.initialize( state );
 		insertable = state.isInsertable();
 		updatable = state.isUpdatable();
 		keyCascadeDeleteEnabled = state.isKeyCascadeDeleteEnabled();
 		unsavedValue = state.getUnsavedValue();
 		generation = state.getPropertyGeneration() == null ? PropertyGeneration.NEVER : state.getPropertyGeneration();
 		return this;
 	}
 
 	public SimpleAttributeBinding initialize(ValueRelationalState state) {
 		super.initializeValueRelationalState( state );
 		return this;
 	}
 
 	private boolean isUnique(ColumnRelationalState state) {
 		return isPrimaryKey() || state.isUnique();
 	}
 
 	@Override
 	public SingularAttribute getAttribute() {
 		return (SingularAttribute) super.getAttribute();
 	}
 
 	@Override
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isInsertable() {
 		return insertable;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public boolean isUpdatable() {
 		return updatable;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	@Override
 	public boolean isKeyCascadeDeleteEnabled() {
 		return keyCascadeDeleteEnabled;
 	}
 
 	public void setKeyCascadeDeleteEnabled(boolean keyCascadeDeleteEnabled) {
 		this.keyCascadeDeleteEnabled = keyCascadeDeleteEnabled;
 	}
 
 	@Override
 	public String getUnsavedValue() {
 		return unsavedValue;
 	}
 
 	public void setUnsavedValue(String unsaveValue) {
 		this.unsavedValue = unsaveValue;
 	}
 
 	public boolean forceNonNullable() {
 		return forceNonNullable;
 	}
 
 	public boolean forceUnique() {
 		return forceUnique;
 	}
 
 	public PropertyGeneration getGeneration() {
 		return generation;
 	}
 
 	public void setGeneration(PropertyGeneration generation) {
 		this.generation = generation;
 	}
 
 	public String getPropertyAccessorName() {
 		return propertyAccessorName;
 	}
 
 	public void setPropertyAccessorName(String propertyAccessorName) {
 		this.propertyAccessorName = propertyAccessorName;
 	}
 
 	public boolean isIncludedInOptimisticLocking() {
 		return includedInOptimisticLocking;
 	}
 
 	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
 		this.includedInOptimisticLocking = includedInOptimisticLocking;
 	}
 
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
+
+	/* package-protected */
+	IdentifierGenerator createIdentifierGenerator(
+			IdGenerator idGenerator,
+			IdentifierGeneratorFactory identifierGeneratorFactory,
+			Properties properties) {
+		Properties params = new Properties();
+		params.putAll( properties );
+
+		// use the schema/catalog specified by getValue().getTable() - but note that
+		// if the schema/catalog were specified as params, they will already be initialized and
+		//will override the values set here (they are in idGenerator.getParameters().)
+		Schema schema = getValue().getTable().getSchema();
+		if ( schema != null ) {
+			if ( schema.getName().getSchema() != null ) {
+				params.setProperty( PersistentIdentifierGenerator.SCHEMA, schema.getName().getSchema().getName() );
+			}
+			if ( schema.getName().getCatalog() != null ) {
+				params.setProperty(PersistentIdentifierGenerator.CATALOG, schema.getName().getCatalog().getName() );
+			}
+		}
+
+		// TODO: not sure how this works for collection IDs...
+		//pass the entity-name, if not a collection-id
+		//if ( rootClass!=null) {
+			params.setProperty( IdentifierGenerator.ENTITY_NAME, getEntityBinding().getEntity().getName() );
+		//}
+
+		//init the table here instead of earlier, so that we can get a quoted table name
+		//TODO: would it be better to simply pass the qualified table name, instead of
+		//      splitting it up into schema/catalog/table names
+		String tableName = getValue().getTable().getQualifiedName( identifierGeneratorFactory.getDialect() );
+		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
+
+		//pass the column name (a generated id almost always has a single column)
+		if ( getValuesSpan() != 1 ) {
+			throw new MappingException( "A SimpleAttributeBinding has a more than 1 Value: " + getAttribute().getName() );
+		}
+		SimpleValue simpleValue = getValues().iterator().next();
+		if ( ! Column.class.isInstance( simpleValue ) ) {
+			throw new MappingException(
+					"Cannot create an IdentifierGenerator because the value is not a column: " +
+							simpleValue.toLoggableString()
+			);
+		}
+		params.setProperty(
+				PersistentIdentifierGenerator.PK,
+				( ( Column ) simpleValue ).getColumnName().encloseInQuotesIfQuoted(
+						identifierGeneratorFactory.getDialect()
+				)
+		);
+
+		// TODO: is this stuff necessary for SimpleValue???
+		//if (rootClass!=null) {
+		//	StringBuffer tables = new StringBuffer();
+		//	Iterator iter = rootClass.getIdentityTables().iterator();
+		//	while ( iter.hasNext() ) {
+		//		Table table= (Table) iter.next();
+		//		tables.append( table.getQuotedName(dialect) );
+		//		if ( iter.hasNext() ) tables.append(", ");
+		//	}
+		//	params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
+		//}
+		//else {
+			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
+		//}
+
+		params.putAll( idGenerator.getParameters() );
+
+		return identifierGeneratorFactory.createIdentifierGenerator(
+				idGenerator.getStrategy(), getHibernateTypeDescriptor().getResolvedTypeMapping(), params
+		);
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/IdentifierGeneratorResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/IdentifierGeneratorResolver.java
new file mode 100644
index 0000000000..de5fe956a2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/IdentifierGeneratorResolver.java
@@ -0,0 +1,97 @@
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
+import java.util.Properties;
+
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.id.PersistentIdentifierGenerator;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.service.config.spi.ConfigurationService;
+
+/**
+ * @author Gail Badner
+ */
+public class IdentifierGeneratorResolver {
+
+	private final MetadataImplementor metadata;
+
+	IdentifierGeneratorResolver(MetadataImplementor metadata) {
+		this.metadata = metadata;
+	}
+
+	// IdentifierGeneratorResolver.resolve() must execute after AttributeTypeResolver.resolve()
+	// to ensure that identifier type is resolved.
+	@SuppressWarnings( {"unchecked"} )
+	void resolve() {
+		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
+			if ( entityBinding.isRoot() ) {
+				Properties properties = new Properties( );
+				properties.putAll(
+						metadata.getServiceRegistry()
+								.getService( ConfigurationService.class )
+								.getSettings()
+				);
+				//TODO: where should these be added???
+				if ( ! properties.contains( AvailableSettings.PREFER_POOLED_VALUES_LO ) ) {
+					properties.put( AvailableSettings.PREFER_POOLED_VALUES_LO, "false" );
+				}
+				if ( ! properties.contains( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER ) ) {
+					properties.put(
+							PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
+							new ObjectNameNormalizerImpl( metadata )
+					);
+				}
+				entityBinding.getEntityIdentifier().createIdentifierGenerator(
+						metadata.getIdentifierGeneratorFactory(),
+						properties
+				);
+			}
+		}
+	}
+
+	private static class ObjectNameNormalizerImpl extends ObjectNameNormalizer implements Serializable {
+		private final boolean useQuotedIdentifiersGlobally;
+		private final NamingStrategy namingStrategy;
+
+		private ObjectNameNormalizerImpl(MetadataImplementor metadata ) {
+			this.useQuotedIdentifiersGlobally = metadata.isGloballyQuotedIdentifiers();
+			this.namingStrategy = metadata.getNamingStrategy();
+		}
+
+		@Override
+		protected boolean isUseQuotedIdentifiersGlobally() {
+			return useQuotedIdentifiersGlobally;
+		}
+
+		@Override
+		protected NamingStrategy getNamingStrategy() {
+			return namingStrategy;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 35289f7f12..c0958549bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,597 +1,603 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.ResultSetMappingDefinition;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.MetadataSourceProcessor;
 import org.hibernate.metamodel.source.annotations.AnnotationMetadataSourceProcessorImpl;
 import org.hibernate.metamodel.source.hbm.HbmMetadataSourceProcessorImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MetadataImpl.class.getName()
 	);
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final Options options;
 
 	private final Value<ClassLoaderService> classLoaderService;
 	private final Value<PersisterClassResolver> persisterClassResolverService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
-	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
+	private final DefaultIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private final Database database;
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports = new HashMap<String, String>();
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
 	private Map<String, ResultSetMappingDefinition> resultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
     private boolean globallyQuotedIdentifiers = false;
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
+		Dialect dialect = metadataSources.getServiceRegistry().getService( JdbcServices.class ).getDialect();
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
+		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory( dialect );
 		this.database = new Database( options );
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final MetadataSourceProcessor[] metadataSourceProcessors;
 		if ( options.getMetadataSourceProcessingOrder() == MetadataSourceProcessingOrder.HBM_FIRST ) {
 			metadataSourceProcessors = new MetadataSourceProcessor[] {
 					new HbmMetadataSourceProcessorImpl( this ),
 					new AnnotationMetadataSourceProcessorImpl( this )
 			};
 		}
 		else {
 			metadataSourceProcessors = new MetadataSourceProcessor[] {
 					new AnnotationMetadataSourceProcessorImpl( this ),
 					new HbmMetadataSourceProcessorImpl( this )
 			};
 		}
 
 		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return serviceRegistry.getService( ClassLoaderService.class );
 					}
 				}
 		);
 		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
 					@Override
 					public PersisterClassResolver initialize() {
 						return serviceRegistry.getService( PersisterClassResolver.class );
 					}
 				}
 		);
 
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( metadataSourceProcessors, metadataSources );
 		bindIndependentMetadata( metadataSourceProcessors, metadataSources );
 		bindTypeDependentMetadata( metadataSourceProcessors, metadataSources );
 		bindMappingMetadata( metadataSourceProcessors, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( metadataSourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
+		// IdentifierGeneratorResolver.resolve() must execute after AttributeTypeResolver.resolve()
+		new IdentifierGeneratorResolver( this ).resolve();
 	}
 
 	private void prepare(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processMappingDependentMetadata( metadataSources );
 		}
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		if ( profile == null || profile.getName() == null ) {
 			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
 		}
 		fetchProfiles.put( profile.getName(), profile );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition def) {
 		if ( def == null || def.getFilterName() == null ) {
 			throw new IllegalArgumentException( "Filter definition object or name is null: "  + def );
 		}
 		filterDefs.put( def.getFilterName(), def );
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefs.values();
 	}
 
 	@Override
 	public void addIdGenerator(IdGenerator generator) {
 		if ( generator == null || generator.getName() == null ) {
 			throw new IllegalArgumentException( "ID generator object or name is null." );
 		}
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
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named native query definition object or name is null: " + def.getQueryString() );
 		}
 		namedNativeQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid native query name" );
 		}
 		return namedNativeQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
 		return namedNativeQueryDefs.values();
 	}
 
 	@Override
 	public void addNamedQuery(NamedQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
 		}
 		namedQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions() {
 		return namedQueryDefs.values();
 	}
 
 	@Override
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		if ( resultSetMappingDefinition == null || resultSetMappingDefinition.getName() == null ) {
 			throw new IllegalArgumentException( "Result-set mapping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 		else if ( typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition name is null: " + typeDef.getTypeClass() );
 		}
 		final TypeDef previous = typeDefs.put( typeDef.getName(), typeDef );
 		if ( previous != null ) {
 			LOG.debugf( "Duplicate typedef name [%s] now -> %s", typeDef.getName(), typeDef.getTypeClass() );
 		}
 	}
 
 	@Override
 	public Iterable<TypeDef> getTypeDefinitions() {
 		return typeDefs.values();
 	}
 
 	@Override
 	public TypeDef getTypeDefinition(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return classLoaderService.getValue();
 	}
 
 	private PersisterClassResolver persisterClassResolverService() {
 		return persisterClassResolverService.getValue();
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		return sessionFactoryBuilder.buildSessionFactory();
 	}
 
 	@Override
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> locateClassByName(String name) {
 		return classLoaderService().classForName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		// todo : have this perform some analysis of the incoming type name to determine appropriate return
 		return new BasicType( className, makeClassReference( className ) );
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(final String className) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.getValue().classForName( className );
 					}
 				}
 		);
 	}
 
 	@Override
 	public String qualifyClassName(String name) {
 		return name;
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
 		EntityBinding binding = entityBindingMap.get( entityName );
 		if ( binding == null ) {
 			throw new IllegalStateException( "Unknown entity binding: " + entityName );
 		}
 
 		do {
 			if ( binding.isRoot() ) {
 				return binding;
 			}
 			binding = binding.getSuperEntityBinding();
 		} while ( binding != null );
 
 		throw new AssertionFailure( "Entity binding has no root: " + entityName );
 	}
 
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
 
 	@Override
 	public Iterable<PluralAttributeBinding> getCollectionBindings() {
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
 		if ( importName == null || entityName == null ) {
 			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 	@Override
 	public Iterable<Map.Entry<String, String>> getImports() {
 		return imports.entrySet();
 	}
 
 	@Override
 	public Iterable<FetchProfile> getFetchProfiles() {
 		return fetchProfiles.values();
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		return sessionFactoryBuilder;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return options.getNamingStrategy();
 	}
 
     @Override
     public boolean isGloballyQuotedIdentifiers() {
         return globallyQuotedIdentifiers || getOptions().isGloballyQuotedIdentifiers();
     }
 
     public void setGloballyQuotedIdentifiers(boolean globallyQuotedIdentifiers){
        this.globallyQuotedIdentifiers = globallyQuotedIdentifiers;
     }
 
     @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getGlobalMetaAttributeContext() {
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
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getResolvedTypeMapping();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		AttributeBinding idBinding = entityBinding.getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping();
 	}
 
 	private class MappingDefaultsImpl implements MappingDefaults {
 
 		@Override
 		public String getPackageName() {
 			return null;
 		}
 
 		@Override
 		public String getSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
 		public String getCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
 		public String getIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getCascadeStyle() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
 		public String getPropertyAccessorName() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
 		public boolean areAssociationsLazy() {
 			return true;
 		}
 
 		private final Value<AccessType> regionFactorySpecifiedDefaultAccessType = new Value<AccessType>(
 				new Value.DeferredInitializer<AccessType>() {
 					@Override
 					public AccessType initialize() {
 						final RegionFactory regionFactory = getServiceRegistry().getService( RegionFactory.class );
 						return regionFactory.getDefaultAccessType();
 					}
 				}
 		);
 
 		@Override
 		public AccessType getCacheAccessType() {
 			return options.getDefaultAccessType() != null
 					? options.getDefaultAccessType()
 					: regionFactorySpecifiedDefaultAccessType.getValue();
 		}
 	}
 }
