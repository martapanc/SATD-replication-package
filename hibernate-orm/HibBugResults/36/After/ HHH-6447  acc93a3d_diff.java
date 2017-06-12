diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
index f0a9d66014..eb7554a859 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
@@ -1,107 +1,107 @@
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
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
 
 	public static CacheDataDescriptionImpl decode(EntityBinding model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.isVersioned(),
 				getVersionComparator( model )
 		);
 	}
 
 	public static CacheDataDescriptionImpl decode(Collection model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.getOwner().isVersioned(),
 				model.getOwner().isVersioned() ? ( ( VersionType ) model.getOwner().getVersion().getType() ).getComparator() : null
 		);
 	}
 
-	public static CacheDataDescriptionImpl decode(PluralAttributeBinding model) {
+	public static CacheDataDescriptionImpl decode(AbstractPluralAttributeBinding model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.getEntityBinding().isVersioned(),
 				getVersionComparator( model.getEntityBinding() )
 		);
 	}
 
 	private static Comparator getVersionComparator(EntityBinding model ) {
 		Comparator versionComparator = null;
 		if ( model.isVersioned() ) {
 			versionComparator = (
 					( VersionType ) model
 								.getVersioningValueBinding()
 								.getHibernateTypeDescriptor()
 								.getResolvedTypeMapping()
 			).getComparator();
 		}
 		return versionComparator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 50aac19c74..c33dff2d1b 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1673 +1,1673 @@
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
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
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
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
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
 
 		identifierGenerators = new HashMap<String,IdentifierGenerator>();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				identifierGenerators.put(
 						entityBinding.getEntity().getName(),
 						entityBinding.getEntityIdentifier().getIdentifierGenerator()
 				);
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
-		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
+		for ( AbstractPluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
-				throw new IllegalStateException( "No attribute defined for a PluralAttributeBinding: " +  model );
+				throw new IllegalStateException( "No attribute defined for a AbstractPluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
-						"PluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
+						"AbstractPluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
-			// TODO: Add PluralAttributeBinding.getCaching()
+			// TODO: Add AbstractPluralAttributeBinding.getCaching()
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
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
index 0313e266ce..db19b8ac2e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
@@ -1,97 +1,97 @@
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
 
 import java.util.Map;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 
 /**
  * @author Steve Ebersole
  */
 public interface Metadata {
 	/**
 	 * Exposes the options used to produce a {@link Metadata} instance.
 	 */
 	public static interface Options {
 		public MetadataSourceProcessingOrder getMetadataSourceProcessingOrder();
 		public NamingStrategy getNamingStrategy();
 		public SharedCacheMode getSharedCacheMode();
 		public AccessType getDefaultAccessType();
 		public boolean useNewIdentifierGenerators();
         public boolean isGloballyQuotedIdentifiers();
 		public String getDefaultSchemaName();
 		public String getDefaultCatalogName();
 	}
 
 	public Options getOptions();
 
 	public SessionFactoryBuilder getSessionFactoryBuilder();
 
 	public SessionFactory buildSessionFactory();
 
 	public Iterable<EntityBinding> getEntityBindings();
 
 	public EntityBinding getEntityBinding(String entityName);
 
 	/**
 	 * Get the "root" entity binding
 	 * @param entityName
 	 * @return the "root entity binding; simply returns entityBinding if it is the root entity binding
 	 */
 	public EntityBinding getRootEntityBinding(String entityName);
 
-	public Iterable<PluralAttributeBinding> getCollectionBindings();
+	public Iterable<AbstractPluralAttributeBinding> getCollectionBindings();
 
 	public TypeDef getTypeDefinition(String name);
 
 	public Iterable<TypeDef> getTypeDefinitions();
 
 	public Iterable<FilterDefinition> getFilterDefinitions();
 
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions();
 
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions();
 
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions();
 
 	public Iterable<Map.Entry<String, String>> getImports();
 
 	public Iterable<FetchProfile> getFetchProfiles();
 
 	public IdGenerator getIdGenerator(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
index 97ec21dedd..5fdae4973a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
@@ -1,276 +1,165 @@
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
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
-import org.hibernate.MappingException;
-import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.binding.state.AttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
-import org.hibernate.metamodel.relational.Tuple;
-import org.hibernate.metamodel.relational.Value;
-import org.hibernate.metamodel.relational.state.SimpleValueRelationalState;
-import org.hibernate.metamodel.relational.state.TupleRelationalState;
-import org.hibernate.metamodel.relational.state.ValueCreator;
-import org.hibernate.metamodel.relational.state.ValueRelationalState;
+import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
  * Basic support for {@link AttributeBinding} implementors
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttributeBinding implements AttributeBinding {
 	private final EntityBinding entityBinding;
+	private final Attribute attribute;
 
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
 	private final Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
-	private Attribute attribute;
-	private Value value;
+	private boolean includedInOptimisticLocking;
 
 	private boolean isLazy;
 	private String propertyAccessorName;
 	private boolean isAlternateUniqueKey;
-	private Set<CascadeType> cascadeTypes;
-	private boolean optimisticLockable;
 
 	private MetaAttributeContext metaAttributeContext;
 
-	protected AbstractAttributeBinding(EntityBinding entityBinding) {
+	protected AbstractAttributeBinding(EntityBinding entityBinding, Attribute attribute) {
 		this.entityBinding = entityBinding;
+		this.attribute = attribute;
 	}
 
 	protected void initialize(AttributeBindingState state) {
 		hibernateTypeDescriptor.setExplicitTypeName( state.getExplicitHibernateTypeName() );
 		hibernateTypeDescriptor.setTypeParameters( state.getExplicitHibernateTypeParameters() );
 		hibernateTypeDescriptor.setJavaTypeName( state.getJavaTypeName() );
 		isLazy = state.isLazy();
-		propertyAccessorName = state.getPropertyAccessorName();
 		isAlternateUniqueKey = state.isAlternateUniqueKey();
-		cascadeTypes = state.getCascadeTypes();
-		optimisticLockable = state.isOptimisticLockable();
 		metaAttributeContext = state.getMetaAttributeContext();
 	}
 
 	@Override
 	public EntityBinding getEntityBinding() {
 		return entityBinding;
 	}
 
 	@Override
 	public Attribute getAttribute() {
 		return attribute;
 	}
 
-	protected void setAttribute(Attribute attribute) {
-		this.attribute = attribute;
-	}
-
-	public void setValue(Value value) {
-		this.value = value;
-	}
-
-	protected boolean forceNonNullable() {
-		return false;
-	}
-
-	protected boolean forceUnique() {
-		return false;
-	}
-
-	protected final boolean isPrimaryKey() {
-		return this == getEntityBinding().getEntityIdentifier().getValueBinding();
-	}
-
-	protected void initializeValueRelationalState(ValueRelationalState state) {
-		// TODO: change to have ValueRelationalState generate the value
-		value = ValueCreator.createValue(
-				getEntityBinding().getBaseTable(),
-				getAttribute().getName(),
-				state,
-				forceNonNullable(),
-				forceUnique()
-		);
-		// TODO: not sure I like this here...
-		if ( isPrimaryKey() ) {
-			if ( SimpleValue.class.isInstance( value ) ) {
-				if ( !Column.class.isInstance( value ) ) {
-					// this should never ever happen..
-					throw new MappingException( "Simple ID is not a column." );
-				}
-				entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( value ) );
-			}
-			else {
-				for ( SimpleValueRelationalState val : TupleRelationalState.class.cast( state )
-						.getRelationalStates() ) {
-					if ( Column.class.isInstance( val ) ) {
-						entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( val ) );
-					}
-				}
-			}
-		}
-	}
-
 	@Override
-	public Value getValue() {
-		return value;
+	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
+		return hibernateTypeDescriptor;
 	}
 
 	@Override
-	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
-		return hibernateTypeDescriptor;
+	public boolean isBasicPropertyAccessor() {
+		return propertyAccessorName == null || "property".equals( propertyAccessorName );
 	}
 
-	public Set<CascadeType> getCascadeTypes() {
-		return cascadeTypes;
+	@Override
+	public String getPropertyAccessorName() {
+		return propertyAccessorName;
 	}
 
-	public boolean isOptimisticLockable() {
-		return optimisticLockable;
+	public void setPropertyAccessorName(String propertyAccessorName) {
+		this.propertyAccessorName = propertyAccessorName;
 	}
 
 	@Override
-	public MetaAttributeContext getMetaAttributeContext() {
-		return metaAttributeContext;
+	public boolean isIncludedInOptimisticLocking() {
+		return includedInOptimisticLocking;
 	}
 
-	@Override
-	public int getValuesSpan() {
-		if ( value == null ) {
-			return 0;
-		}
-		else if ( value instanceof Tuple ) {
-			return ( ( Tuple ) value ).valuesSpan();
-		}
-		else {
-			return 1;
-		}
+	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
+		this.includedInOptimisticLocking = includedInOptimisticLocking;
 	}
 
+	protected boolean forceNonNullable() {
+		return false;
+	}
 
-	@Override
-	public Iterable<SimpleValue> getValues() {
-		return value == null
-				? Collections.<SimpleValue>emptyList()
-				: value instanceof Tuple
-				? ( (Tuple) value ).values()
-				: Collections.singletonList( (SimpleValue) value );
+	protected boolean forceUnique() {
+		return false;
 	}
 
-	@Override
-	public String getPropertyAccessorName() {
-		return propertyAccessorName;
+	protected final boolean isPrimaryKey() {
+		return this == getEntityBinding().getEntityIdentifier().getValueBinding();
 	}
 
 	@Override
-	public boolean isBasicPropertyAccessor() {
-		return propertyAccessorName==null || "property".equals( propertyAccessorName );
+	public MetaAttributeContext getMetaAttributeContext() {
+		return metaAttributeContext;
 	}
 
-
-	@Override
-	public boolean hasFormula() {
-		for ( SimpleValue simpleValue : getValues() ) {
-			if ( simpleValue instanceof DerivedValue ) {
-				return true;
-			}
-		}
-		return false;
+	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
+		this.metaAttributeContext = metaAttributeContext;
 	}
 
 	@Override
 	public boolean isAlternateUniqueKey() {
 		return isAlternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean alternateUniqueKey) {
 		this.isAlternateUniqueKey = alternateUniqueKey;
 	}
 
 	@Override
-	public boolean isNullable() {
-		for ( SimpleValue simpleValue : getValues() ) {
-			if ( simpleValue instanceof DerivedValue ) {
-				return true;
-			}
-			Column column = (Column) simpleValue;
-			if ( column.isNullable() ) {
-				return true;
-			}
-		}
-		return false;
-	}
-
-	@Override
-	public boolean[] getColumnInsertability() {
-		List<Boolean> tmp = new ArrayList<Boolean>();
-		for ( SimpleValue simpleValue : getValues() ) {
-			tmp.add( !( simpleValue instanceof DerivedValue ) );
-		}
-		boolean[] rtn = new boolean[tmp.size()];
-		int i = 0;
-		for ( Boolean insertable : tmp ) {
-			rtn[i++] = insertable.booleanValue();
-		}
-		return rtn;
-	}
-
-	@Override
-	public boolean[] getColumnUpdateability() {
-		return getColumnInsertability();
-	}
-
-	@Override
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public void setLazy(boolean isLazy) {
 		this.isLazy = isLazy;
 	}
 
 	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding referencingAttributeBinding) {
 		entityReferencingAttributeBindings.add( referencingAttributeBinding );
 	}
 
 	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
 		return Collections.unmodifiableSet( entityReferencingAttributeBindings );
 	}
 
 	public void validate() {
 		if ( !entityReferencingAttributeBindings.isEmpty() ) {
 			// TODO; validate that this AttributeBinding can be a target of an entity reference
 			// (e.g., this attribute is the primary key or there is a unique-key)
 			// can a unique attribute be used as a target? if so, does it need to be non-null?
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
new file mode 100644
index 0000000000..ca141eaad1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
@@ -0,0 +1,298 @@
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
+package org.hibernate.metamodel.binding;
+
+import java.util.ArrayList;
+import java.util.Comparator;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.List;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.FetchMode;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.relational.Table;
+
+/**
+ * TODO : javadoc
+ *
+ * @author Steve Ebersole
+ */
+public abstract class AbstractPluralAttributeBinding extends AbstractAttributeBinding implements PluralAttributeBinding {
+	private CollectionKey collectionKey;
+	private CollectionElement collectionElement;
+
+	private Table collectionTable;
+
+	private CascadeStyle cascadeStyle;
+	private FetchMode fetchMode;
+
+	private boolean extraLazy;
+	private boolean inverse;
+	private boolean mutable = true;
+	private boolean subselectLoadable;
+	private String cacheConcurrencyStrategy;
+	private String cacheRegionName;
+	private String orderBy;
+	private String where;
+	private String referencedPropertyName;
+	private boolean sorted;
+	private Comparator comparator;
+	private String comparatorClassName;
+	private boolean orphanDelete;
+	private int batchSize = -1;
+	private boolean embedded = true;
+	private boolean optimisticLocked = true;
+	private Class collectionPersisterClass;
+	private final java.util.Map filters = new HashMap();
+	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
+
+	private CustomSQL customSQLInsert;
+	private CustomSQL customSQLUpdate;
+	private CustomSQL customSQLDelete;
+	private CustomSQL customSQLDeleteAll;
+
+	private String loaderName;
+
+	protected AbstractPluralAttributeBinding(
+			EntityBinding entityBinding,
+			PluralAttribute attribute,
+			CollectionElementNature collectionElementNature) {
+		super( entityBinding, attribute );
+		this.collectionElement = interpretNature( collectionElementNature );
+	}
+
+	private CollectionElement interpretNature(CollectionElementNature collectionElementNature) {
+		switch ( collectionElementNature ) {
+			case BASIC: {
+				return new BasicCollectionElement( this );
+			}
+			case COMPOSITE: {
+				return new CompositeCollectionElement( this );
+			}
+			case ONE_TO_MANY: {
+				return new OneToManyCollectionElement( this );
+			}
+			case MANY_TO_MANY: {
+				return new ManyToManyCollectionElement( this );
+			}
+			case MANY_TO_ANY: {
+				return new ManyToAnyCollectionElement( this );
+			}
+			default: {
+				throw new AssertionFailure( "Unknown collection element nature : " + collectionElementNature );
+			}
+		}
+	}
+
+//	protected void initializeBinding(PluralAttributeBindingState state) {
+//		super.initialize( state );
+//		fetchMode = state.getFetchMode();
+//		extraLazy = state.isExtraLazy();
+//		collectionElement.setNodeName( state.getElementNodeName() );
+//		collectionElement.setTypeName( state.getElementTypeName() );
+//		inverse = state.isInverse();
+//		mutable = state.isMutable();
+//		subselectLoadable = state.isSubselectLoadable();
+//		if ( isSubselectLoadable() ) {
+//			getEntityBinding().setSubselectLoadableCollections( true );
+//		}
+//		cacheConcurrencyStrategy = state.getCacheConcurrencyStrategy();
+//		cacheRegionName = state.getCacheRegionName();
+//		orderBy = state.getOrderBy();
+//		where = state.getWhere();
+//		referencedPropertyName = state.getReferencedPropertyName();
+//		sorted = state.isSorted();
+//		comparator = state.getComparator();
+//		comparatorClassName = state.getComparatorClassName();
+//		orphanDelete = state.isOrphanDelete();
+//		batchSize = state.getBatchSize();
+//		embedded = state.isEmbedded();
+//		optimisticLocked = state.isOptimisticLocked();
+//		collectionPersisterClass = state.getCollectionPersisterClass();
+//		filters.putAll( state.getFilters() );
+//		synchronizedTables.addAll( state.getSynchronizedTables() );
+//		customSQLInsert = state.getCustomSQLInsert();
+//		customSQLUpdate = state.getCustomSQLUpdate();
+//		customSQLDelete = state.getCustomSQLDelete();
+//		customSQLDeleteAll = state.getCustomSQLDeleteAll();
+//		loaderName = state.getLoaderName();
+//	}
+
+	@Override
+	public boolean isAssociation() {
+		return collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_ANY
+				|| collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_MANY
+				|| collectionElement.getCollectionElementNature() == CollectionElementNature.ONE_TO_MANY;
+	}
+
+	public Table getCollectionTable() {
+		return collectionTable;
+	}
+
+	public void setCollectionTable(Table collectionTable) {
+		this.collectionTable = collectionTable;
+	}
+
+	public CollectionKey getCollectionKey() {
+		return collectionKey;
+	}
+
+	public void setCollectionKey(CollectionKey collectionKey) {
+		this.collectionKey = collectionKey;
+	}
+
+	public CollectionElement getCollectionElement() {
+		return collectionElement;
+	}
+
+	@Override
+	public CascadeStyle getCascadeStyle() {
+		return cascadeStyle;
+	}
+
+	@Override
+	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
+		List<CascadeStyle> cascadeStyleList = new ArrayList<CascadeStyle>();
+		for ( CascadeStyle style : cascadeStyles ) {
+			if ( style != CascadeStyle.NONE ) {
+				cascadeStyleList.add( style );
+			}
+		}
+		if ( cascadeStyleList.isEmpty() ) {
+			cascadeStyle = CascadeStyle.NONE;
+		}
+		else if ( cascadeStyleList.size() == 1 ) {
+			cascadeStyle = cascadeStyleList.get( 0 );
+		}
+		else {
+			cascadeStyle = new CascadeStyle.MultipleCascadeStyle(
+					cascadeStyleList.toArray( new CascadeStyle[ cascadeStyleList.size() ] )
+			);
+		}
+	}
+
+	@Override
+	public FetchMode getFetchMode() {
+		return fetchMode;
+	}
+
+	@Override
+	public void setFetchMode(FetchMode fetchMode) {
+		this.fetchMode = fetchMode;
+	}
+
+	public boolean isExtraLazy() {
+		return extraLazy;
+	}
+
+	public boolean isInverse() {
+		return inverse;
+	}
+
+	public boolean isMutable() {
+		return mutable;
+	}
+
+	public boolean isSubselectLoadable() {
+		return subselectLoadable;
+	}
+
+	public String getCacheConcurrencyStrategy() {
+		return cacheConcurrencyStrategy;
+	}
+
+	public String getCacheRegionName() {
+		return cacheRegionName;
+	}
+
+	public String getOrderBy() {
+		return orderBy;
+	}
+
+	public String getWhere() {
+		return where;
+	}
+
+	public String getReferencedPropertyName() {
+		return referencedPropertyName;
+	}
+
+	public boolean isSorted() {
+		return sorted;
+	}
+
+	public Comparator getComparator() {
+		return comparator;
+	}
+
+	public void setComparator(Comparator comparator) {
+		this.comparator = comparator;
+	}
+
+	public String getComparatorClassName() {
+		return comparatorClassName;
+	}
+
+	public boolean isOrphanDelete() {
+		return orphanDelete;
+	}
+
+	public int getBatchSize() {
+		return batchSize;
+	}
+
+	public Class getCollectionPersisterClass() {
+		return collectionPersisterClass;
+	}
+
+	public void addFilter(String name, String condition) {
+		filters.put( name, condition );
+	}
+
+	public java.util.Map getFilterMap() {
+		return filters;
+	}
+
+	public CustomSQL getCustomSQLInsert() {
+		return customSQLInsert;
+	}
+
+	public CustomSQL getCustomSQLUpdate() {
+		return customSQLUpdate;
+	}
+
+	public CustomSQL getCustomSQLDelete() {
+		return customSQLDelete;
+	}
+
+	public CustomSQL getCustomSQLDeleteAll() {
+		return customSQLDeleteAll;
+	}
+
+	public String getLoaderName() {
+		return loaderName;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractSingularAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractSingularAttributeBinding.java
new file mode 100644
index 0000000000..29ef6b0cfd
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractSingularAttributeBinding.java
@@ -0,0 +1,113 @@
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
+package org.hibernate.metamodel.binding;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.metamodel.domain.SingularAttribute;
+import org.hibernate.metamodel.relational.SimpleValue;
+import org.hibernate.metamodel.relational.Tuple;
+import org.hibernate.metamodel.relational.Value;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractSingularAttributeBinding
+		extends AbstractAttributeBinding
+		implements SingularAttributeBinding {
+
+	private Value value;
+	private List<SimpleValueBinding> simpleValueBindings = new ArrayList<SimpleValueBinding>();
+
+	private boolean hasDerivedValue;
+	private boolean isNullable = true;
+
+	protected AbstractSingularAttributeBinding(EntityBinding entityBinding, SingularAttribute attribute) {
+		super( entityBinding, attribute );
+	}
+
+	@Override
+	public SingularAttribute getAttribute() {
+		return (SingularAttribute) super.getAttribute();
+	}
+
+	public Value getValue() {
+		return value;
+	}
+
+	public void setSimpleValueBindings(Iterable<SimpleValueBinding> simpleValueBindings) {
+		List<SimpleValue> values = new ArrayList<SimpleValue>();
+		for ( SimpleValueBinding simpleValueBinding : simpleValueBindings ) {
+			this.simpleValueBindings.add( simpleValueBinding );
+			values.add( simpleValueBinding.getSimpleValue() );
+			this.hasDerivedValue = this.hasDerivedValue || simpleValueBinding.isDerived();
+			this.isNullable = this.isNullable && simpleValueBinding.isNullable();
+		}
+		if ( values.size() == 1 ) {
+			this.value = values.get( 0 );
+		}
+		else {
+			final Tuple tuple = values.get( 0 ).getTable().createTuple( getRole() );
+			for ( SimpleValue value : values ) {
+				tuple.addValue( value );
+			}
+			this.value = tuple;
+		}
+	}
+
+	private String getRole() {
+		return getEntityBinding().getEntity().getName() + '.' + getAttribute().getName();
+	}
+
+	@Override
+	public int getSimpleValueSpan() {
+		checkValueBinding();
+		return simpleValueBindings.size();
+	}
+
+	private void checkValueBinding() {
+		if ( value == null ) {
+			throw new AssertionFailure( "No values yet bound!" );
+		}
+	}
+
+	@Override
+	public Iterable<SimpleValueBinding> getSimpleValueBindings() {
+		return simpleValueBindings;
+	}
+
+	@Override
+	public boolean hasDerivedValue() {
+		checkValueBinding();
+		return hasDerivedValue;
+	}
+
+	@Override
+	public boolean isNullable() {
+		checkValueBinding();
+		return isNullable;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
index 3104c89e83..6f68ad6675 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
@@ -1,47 +1,52 @@
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
 
+import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 
 /**
  * Contract describing a binding for attributes which model associations.
  *
  * @author Steve Ebersole
  */
 public interface AssociationAttributeBinding extends AttributeBinding {
 	/**
-	 * Obtain the cascade styles in effect for this association.
+	 * Obtain the cascade style in effect for this association.
 	 *
-	 * @return THe cascade styles.
+	 * @return The (potentially aggregated) cascade style.
 	 */
-	public Iterable<CascadeStyle> getCascadeStyles();
+	public CascadeStyle getCascadeStyle();
 
 	/**
 	 * Set the cascade styles in effect for this association.
 	 *
 	 * @param cascadeStyles The cascade styles.
 	 */
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles);
+
+	public FetchMode getFetchMode();
+
+	public void setFetchMode(FetchMode fetchMode);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
index 23d0e31dec..7468e48e7a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
@@ -1,118 +1,91 @@
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
 
 import java.util.Set;
 
-import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.domain.Attribute;
-import org.hibernate.metamodel.relational.SimpleValue;
-import org.hibernate.metamodel.relational.Value;
+import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
- * The basic contract for binding between an {@link #getAttribute() attribute} and a {@link #getValue() value}
+ * The basic contract for binding a {@link #getAttribute() attribute} from the domain model to the relational model.
  *
  * @author Steve Ebersole
  */
 public interface AttributeBinding {
 	/**
 	 * Obtain the entity binding to which this attribute binding exists.
 	 *
 	 * @return The entity binding.
 	 */
 	public EntityBinding getEntityBinding();
 
 	/**
 	 * Obtain the attribute bound.
 	 *
 	 * @return The attribute.
 	 */
 	public Attribute getAttribute();
 
 	/**
-	 * Obtain the value bound
-	 *
-	 * @return The value
-	 */
-	public Value getValue();
-
-	/**
 	 * Obtain the descriptor for the Hibernate {@link org.hibernate.type.Type} for this binding.
 	 * <p/>
 	 * For information about the Java type, query the {@link Attribute} obtained from {@link #getAttribute()}
 	 * instead.
 	 *
 	 * @return The type descriptor
 	 */
 	public HibernateTypeDescriptor getHibernateTypeDescriptor();
 
-	/**
-	 * Obtain the meta attributes associated with this binding
-	 *
-	 * @return The meta attributes
-	 */
-	public MetaAttributeContext getMetaAttributeContext();
+	public boolean isAssociation();
 
-	/**
-	 * Returns the number of {@link org.hibernate.metamodel.relational.SimpleValue}
-	 * objects that will be returned by {@link #getValues()}
-	 *
-	 * @return the number of objects that will be returned by {@link #getValues()}.
-	 *
-	 * @see {@link org.hibernate.metamodel.relational.SimpleValue}
-	 * @see {@link #getValues()}
-	 */
-	public int getValuesSpan();
-
-	/**
-	 * @return In the case that {@link #getValue()} represents a {@link org.hibernate.metamodel.relational.Tuple} this method
-	 *         gives access to its compound values.  In the case of {@link org.hibernate.metamodel.relational.SimpleValue},
-	 *         we return an Iterable over that single simple value.
-	 */
-	public Iterable<SimpleValue> getValues();
+	public boolean isBasicPropertyAccessor();
 
 	public String getPropertyAccessorName();
 
-	public boolean isBasicPropertyAccessor();
+	public void setPropertyAccessorName(String propertyAccessorName);
 
-	public boolean hasFormula();
+	public boolean isIncludedInOptimisticLocking();
 
-	public boolean isAlternateUniqueKey();
+	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking);
 
-	public boolean isNullable();
+	/**
+	 * Obtain the meta attributes associated with this binding
+	 *
+	 * @return The meta attributes
+	 */
+	public MetaAttributeContext getMetaAttributeContext();
 
-	public boolean[] getColumnUpdateability();
 
-	public boolean[] getColumnInsertability();
 
-	public boolean isSimpleValue();
+	public boolean isAlternateUniqueKey();
 
 	public boolean isLazy();
 
 	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding attributeBinding);
 
 	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings();
 
 	public void validate();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java
index 604957677a..633339a80f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BagBinding.java
@@ -1,42 +1,37 @@
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
 
-import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
+import org.hibernate.metamodel.domain.PluralAttribute;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
-public class BagBinding extends PluralAttributeBinding {
-	protected BagBinding(EntityBinding entityBinding, CollectionElementType collectionElementType) {
-		super( entityBinding, collectionElementType );
-	}
-
-	public BagBinding initialize(PluralAttributeBindingState bindingState) {
-		super.initialize( bindingState );
-		return this;
+public class BagBinding extends AbstractPluralAttributeBinding {
+	protected BagBinding(EntityBinding entityBinding, PluralAttribute attribute, CollectionElementNature nature) {
+		super( entityBinding, attribute, nature );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BasicCollectionElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BasicCollectionElement.java
index 5493444139..277aeda19c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BasicCollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/BasicCollectionElement.java
@@ -1,33 +1,33 @@
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
 
 /**
  * @author Gail Badner
  */
 public class BasicCollectionElement extends CollectionElement {
-	public BasicCollectionElement(PluralAttributeBinding binding) {
-		super( binding, CollectionElementType.BASIC );
+	public BasicCollectionElement(AbstractPluralAttributeBinding binding) {
+		super( binding, CollectionElementNature.BASIC );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java
index 1ff1902f07..8a1c27b965 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElement.java
@@ -1,69 +1,61 @@
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
 
 import org.hibernate.metamodel.relational.Value;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class CollectionElement {
+	private final AbstractPluralAttributeBinding collectionBinding;
+	private final CollectionElementNature collectionElementNature;
 
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
-	private final PluralAttributeBinding collectionBinding;
-	private final CollectionElementType collectionElementType;
-	private String nodeName;
 
 	private Value elementValue;
 
-	CollectionElement(PluralAttributeBinding collectionBinding, CollectionElementType collectionElementType) {
+	CollectionElement(AbstractPluralAttributeBinding collectionBinding, CollectionElementNature collectionElementNature) {
 		this.collectionBinding = collectionBinding;
-		this.collectionElementType = collectionElementType;
+		this.collectionElementNature = collectionElementNature;
 	}
 
-	public final CollectionElementType getCollectionElementType() {
-		return collectionElementType;
+	public AbstractPluralAttributeBinding getCollectionBinding() {
+		return collectionBinding;
 	}
 
-	/* package-protected */
-	void setTypeName(String typeName) {
-		hibernateTypeDescriptor.setExplicitTypeName( typeName );
+	public Value getElementValue() {
+		return elementValue;
 	}
 
-	/* package-protected */
-	void setNodeName(String nodeName) {
-		this.nodeName = nodeName;
+	public final CollectionElementNature getCollectionElementNature() {
+		return collectionElementNature;
 	}
 
-	public boolean isOneToMany() {
-		return collectionElementType.isOneToMany();
+	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
+		return hibernateTypeDescriptor;
 	}
-
-	public boolean isManyToMany() {
-		return collectionElementType.isManyToMany();
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/SimpleAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElementNature.java
similarity index 65%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/SimpleAttributeBindingState.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElementNature.java
index 92df91297c..69fc83a7a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/SimpleAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElementNature.java
@@ -1,43 +1,40 @@
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
-package org.hibernate.metamodel.binding.state;
-
-import org.hibernate.mapping.PropertyGeneration;
+package org.hibernate.metamodel.binding;
 
 /**
+ * Describes the nature of persistent collection elements.
+ *
+ * @author Steve Ebersole
  * @author Gail Badner
- * @todo - We need to get a better split into the states. For example this SimpleAttributeBindingState contains
- * state which is only relevant for primary/foreign keys. This should be in a different interface. (HF)
+ *
+ * @todo Merge with {@link org.hibernate.metamodel.source.binder.PluralAttributeNature} ?  package separation kept me from doing that initially
  */
-public interface SimpleAttributeBindingState extends AttributeBindingState {
-	boolean isInsertable();
-
-	boolean isUpdatable();
-
-	boolean isKeyCascadeDeleteEnabled();
-
-	String getUnsavedValue();
-
-	public PropertyGeneration getPropertyGeneration();
+public enum CollectionElementNature {
+	BASIC,
+	COMPOSITE,
+	ONE_TO_MANY,
+	MANY_TO_MANY,
+	MANY_TO_ANY
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElementType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElementType.java
deleted file mode 100644
index 329efef6d1..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionElementType.java
+++ /dev/null
@@ -1,104 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
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
- */
-package org.hibernate.metamodel.binding;
-
-/**
- * @author Gail Badner
- */
-public enum CollectionElementType {
-	BASIC( "basic" ) {
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-			return new BasicCollectionElement( attributeBinding  );
-		}
-	},
-	COMPOSITE( "composite" ) {
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-			return new CompositeCollectionElement( attributeBinding  );
-		}
-	},
-	ONE_TO_MANY( "one-to-many" ) {
-		public boolean isOneToMany() {
-			return true;
-		}
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-			return new OneToManyCollectionElement( attributeBinding  );
-		}
-	},
-	MANY_TO_MANY( "many-to-many" ) {
-		public boolean isManyToMany() {
-			return true;
-		}
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-			return new ManyToManyCollectionElement( attributeBinding  );
-		}
-	},
-	MANY_TO_ANY( "many-to-any" ) {
-		//TODO: should isManyToMany() return true?
-		public boolean isManyToAny() {
-			return true;
-		}
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-			return new ManyToAnyCollectionElement( attributeBinding  );
-		}
-	};
-
-	private final String name;
-
-	private CollectionElementType(String name) {
-		this.name = name;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public String toString() {
-		return super.toString() + "[" + getName() + "]";
-	}
-
-	public boolean isOneToMany() {
-		return false;
-	}
-
-	public boolean isManyToMany() {
-		return false;
-	}
-
-	public boolean isManyToAny() {
-		return false;
-	}
-
-	protected abstract CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding);
-
-	/* package-protected */
-	 CollectionElement createCollectionElement(PluralAttributeBinding attributeBinding) {
-		 CollectionElement collectionElement = createCollectionElementInternal( attributeBinding );
-		 if ( collectionElement.getCollectionElementType() != this ) {
-			 throw new IllegalStateException( "Collection element has unexpected type nature: actual=[" +
-			 collectionElement.getCollectionElementType() + "; expected=[" + this + "]" );
-		 }
-		 return collectionElement;
-	 }
-}
-
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionKey.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionKey.java
index 3d75ce4707..387a3e43c5 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CollectionKey.java
@@ -1,46 +1,46 @@
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
 
 import org.hibernate.metamodel.relational.ForeignKey;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class CollectionKey {
-	private final PluralAttributeBinding collection;
+	private final AbstractPluralAttributeBinding collection;
 
 	private ForeignKey foreignKey;
 	private boolean inverse;
 	private HibernateTypeDescriptor hibernateTypeDescriptor;
 
 // todo : this would be nice to have but we do not always know it, especially in HBM case.
-//	private SimpleAttributeBinding otherSide;
+//	private SimpleSingularAttributeBinding otherSide;
 
-	public CollectionKey(PluralAttributeBinding collection) {
+	public CollectionKey(AbstractPluralAttributeBinding collection) {
 		this.collection = collection;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CompositeCollectionElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CompositeCollectionElement.java
index 6f007886c6..2b100ea00e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CompositeCollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/CompositeCollectionElement.java
@@ -1,33 +1,33 @@
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
 
 /**
  * @author Gail Badner
  */
 public class CompositeCollectionElement extends CollectionElement {
-	public CompositeCollectionElement(PluralAttributeBinding binding) {
-		super( binding, CollectionElementType.COMPOSITE );
+	public CompositeCollectionElement(AbstractPluralAttributeBinding binding) {
+		super( binding, CollectionElementNature.COMPOSITE );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index 71bc2e7659..f9688d3a56 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,458 +1,459 @@
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
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
-import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.Value;
-import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Provides the link between the domain and the relational model for an entity.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class EntityBinding {
 	private Entity entity;
 	private TableSpecification baseTable;
 
 	private EntityMode entityMode;
 	private Value<Class<?>> proxyInterfaceType;
 
 	private String jpaEntityName;
 
 	private Class<? extends EntityPersister> customEntityPersisterClass;
 	private Class<? extends EntityTuplizer> customEntityTuplizerClass;
 
 	private InheritanceType entityInheritanceType;
 	private EntityBinding superEntityBinding;
 
 	private final EntityIdentifier entityIdentifier = new EntityIdentifier( this );
 	private EntityDiscriminator entityDiscriminator;
-	private SimpleAttributeBinding versionBinding;
+	private SimpleSingularAttributeBinding versionBinding;
 
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 
 	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>( );
 	private Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
 	private Caching caching;
 
 	private MetaAttributeContext metaAttributeContext;
 
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
 	private OptimisticLockStyle optimisticLockStyle;
 
 	private Boolean isAbstract;
 
 	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 
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
 
+	public TableSpecification getTable(String containingTableName) {
+		// todo : implement this for secondary table look ups.  for now we just return the base table
+		return baseTable;
+	}
+
 	public boolean isRoot() {
 		return superEntityBinding == null;
 	}
 
 	public void setInheritanceType(InheritanceType entityInheritanceType) {
 		this.entityInheritanceType = entityInheritanceType;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return entityInheritanceType;
 	}
 
 	public void setSuperEntityBinding(EntityBinding superEntityBinding) {
 		this.superEntityBinding = superEntityBinding;
 	}
 
 	public EntityBinding getSuperEntityBinding() {
 		return superEntityBinding;
 	}
 
 	public EntityIdentifier getEntityIdentifier() {
 		return entityIdentifier;
 	}
 
 	public EntityDiscriminator getEntityDiscriminator() {
 		return entityDiscriminator;
 	}
 
 	public boolean isVersioned() {
 		return versionBinding != null;
 	}
 
-	public void setVersionBinding(SimpleAttributeBinding versionBinding) {
+	public void setVersionBinding(SimpleSingularAttributeBinding versionBinding) {
 		this.versionBinding = versionBinding;
 	}
 
-	public SimpleAttributeBinding getVersioningValueBinding() {
+	public SimpleSingularAttributeBinding getVersioningValueBinding() {
 		return versionBinding;
 	}
 
 	public Iterable<AttributeBinding> getAttributeBindings() {
 		return attributeBindingMap.values();
 	}
 
 	public AttributeBinding getAttributeBinding(String name) {
 		return attributeBindingMap.get( name );
 	}
 
 	/**
 	 * Gets the number of attribute bindings defined on this class, including the
 	 * identifier attribute binding and attribute bindings defined
 	 * as part of a join.
 	 *
 	 * @return The number of attribute bindings
 	 */
 	public int getAttributeBindingClosureSpan() {
 		// TODO: fix this after HHH-6337 is fixed; for now just return size of attributeBindingMap
 		// if this is not a root, then need to include the superclass attribute bindings
 		return attributeBindingMap.size();
 	}
 
 	/**
 	 * Gets the attribute bindings defined on this class, including the
 	 * identifier attribute binding and attribute bindings defined
 	 * as part of a join.
 	 *
 	 * @return The attribute bindings.
 	 */
 	public Iterable<AttributeBinding> getAttributeBindingClosure() {
 		// TODO: fix this after HHH-6337 is fixed. for now, just return attributeBindings
 		// if this is not a root, then need to include the superclass attribute bindings
 		return getAttributeBindings();
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition filterDefinition) {
 		filterDefinitions.add( filterDefinition );
 	}
 
 	public Iterable<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
 		return entityReferencingAttributeBindings;
 	}
 
-	public SimpleAttributeBinding makeSimpleIdAttributeBinding(Attribute attribute) {
-		final SimpleAttributeBinding binding = makeSimpleAttributeBinding( attribute, true, true );
+	public SimpleSingularAttributeBinding makeSimpleIdAttributeBinding(SingularAttribute attribute) {
+		final SimpleSingularAttributeBinding binding = makeSimpleAttributeBinding( attribute, true, true );
 		getEntityIdentifier().setValueBinding( binding );
 		return binding;
 	}
-
-	public EntityDiscriminator makeEntityDiscriminator(Attribute attribute) {
-		if ( entityDiscriminator != null ) {
-			throw new AssertionFailure( "Creation of entity discriminator was called more than once" );
-		}
-		entityDiscriminator = new EntityDiscriminator();
-		entityDiscriminator.setValueBinding( makeSimpleAttributeBinding( attribute, true, false ) );
-		return entityDiscriminator;
-	}
-
-	public SimpleAttributeBinding makeVersionBinding(Attribute attribute) {
+//
+//	public EntityDiscriminator makeEntityDiscriminator(Attribute attribute) {
+//		if ( entityDiscriminator != null ) {
+//			throw new AssertionFailure( "Creation of entity discriminator was called more than once" );
+//		}
+//		entityDiscriminator = new EntityDiscriminator();
+//		entityDiscriminator.setValueBinding( makeSimpleAttributeBinding( attribute, true, false ) );
+//		return entityDiscriminator;
+//	}
+
+	public SimpleSingularAttributeBinding makeVersionBinding(SingularAttribute attribute) {
 		versionBinding = makeSimpleAttributeBinding( attribute, true, false );
 		return versionBinding;
 	}
 
-	public SimpleAttributeBinding makeSimpleAttributeBinding(Attribute attribute) {
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
 		return makeSimpleAttributeBinding( attribute, false, false );
 	}
 
-	private SimpleAttributeBinding makeSimpleAttributeBinding(Attribute attribute, boolean forceNonNullable, boolean forceUnique) {
-		final SimpleAttributeBinding binding = new SimpleAttributeBinding( this, forceNonNullable, forceUnique );
-		binding.setAttribute( attribute );
+	private SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute, boolean forceNonNullable, boolean forceUnique) {
+		final SimpleSingularAttributeBinding binding = new SimpleSingularAttributeBinding( this, attribute, forceNonNullable, forceUnique );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
-	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(Attribute attribute) {
-		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this );
-		binding.setAttribute( attribute );
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
+		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this, attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
-	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementType collectionElementType) {
-		final BagBinding binding = new BagBinding( this, collectionElementType );
-		binding.setAttribute( attribute );
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+		final BagBinding binding = new BagBinding( this, attribute, nature );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	private void registerAttributeBinding(String name, SingularAssociationAttributeBinding attributeBinding) {
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
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
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
 
 	public Value<Class<?>> getProxyInterfaceType() {
 		return proxyInterfaceType;
 	}
 
 	public void setProxyInterfaceType(Value<Class<?>> proxyInterfaceType) {
 		this.proxyInterfaceType = proxyInterfaceType;
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
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	/* package-protected */
 	void setSubselectLoadableCollections(boolean hasSubselectLoadableCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectLoadableCollections;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
 		this.optimisticLockStyle = optimisticLockStyle;
 	}
 
 	public Class<? extends EntityPersister> getCustomEntityPersisterClass() {
 		return customEntityPersisterClass;
 	}
 
 	public void setCustomEntityPersisterClass(Class<? extends EntityPersister> customEntityPersisterClass) {
 		this.customEntityPersisterClass = customEntityPersisterClass;
 	}
 
 	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass() {
 		return customEntityTuplizerClass;
 	}
 
 	public void setCustomEntityTuplizerClass(Class<? extends EntityTuplizer> customEntityTuplizerClass) {
 		this.customEntityTuplizerClass = customEntityTuplizerClass;
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public void setAbstract(Boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 
 	public void addSynchronizedTable(String tableName) {
 		synchronizedTableNames.add( tableName );
 	}
 
 	public void addSynchronizedTableNames(java.util.Collection<String> synchronizedTableNames) {
 		this.synchronizedTableNames.addAll( synchronizedTableNames );
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public void setEntityMode(EntityMode entityMode) {
 		this.entityMode = entityMode;
 	}
 
 	public String getJpaEntityName() {
 		return jpaEntityName;
 	}
 
 	public void setJpaEntityName(String jpaEntityName) {
 		this.jpaEntityName = jpaEntityName;
 	}
 
 	public String getCustomLoaderName() {
 		return customLoaderName;
 	}
 
 	public void setCustomLoaderName(String customLoaderName) {
 		this.customLoaderName = customLoaderName;
 	}
 
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	public void setCustomInsert(CustomSQL customInsert) {
 		this.customInsert = customInsert;
 	}
 
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	public void setCustomUpdate(CustomSQL customUpdate) {
 		this.customUpdate = customUpdate;
 	}
 
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	public void setCustomDelete(CustomSQL customDelete) {
 		this.customDelete = customDelete;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
index e39dd223b2..d15420d1e3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityDiscriminator.java
@@ -1,102 +1,97 @@
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
 
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
 
 /**
  * Binding of the discriminator in a entity hierarchy
  *
  * @author Steve Ebersole
  */
 public class EntityDiscriminator {
-	private SimpleAttributeBinding valueBinding;
+	private SimpleSingularAttributeBinding valueBinding;
 	private String discriminatorValue;
 	private boolean forced;
 	private boolean inserted = true;
 
 	public EntityDiscriminator() {
 	}
 
-	public SimpleAttributeBinding getValueBinding() {
+	public SimpleSingularAttributeBinding getValueBinding() {
 		return valueBinding;
 	}
 
 	/* package-protected */
-	void setValueBinding(SimpleAttributeBinding valueBinding) {
+	void setValueBinding(SimpleSingularAttributeBinding valueBinding) {
 		this.valueBinding = valueBinding;
 	}
 
 	public EntityDiscriminator initialize(DiscriminatorBindingState state) {
 		if ( valueBinding == null ) {
 			throw new IllegalStateException( "Cannot bind state because the value binding has not been initialized." );
 		}
 		this.valueBinding.initialize( state );
 		this.discriminatorValue = state.getDiscriminatorValue();
 		this.forced = state.isForced();
 		this.inserted = state.isInserted();
 		return this;
 	}
 
-	public EntityDiscriminator initialize(ValueRelationalState state) {
-		valueBinding.initialize( state );
-		return this;
-	}
-
 	public String getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public void setDiscriminatorValue(String discriminatorValue) {
 		this.discriminatorValue = discriminatorValue;
 	}
 
 	public boolean isForced() {
 		return forced;
 	}
 
 	public void setForced(boolean forced) {
 		this.forced = forced;
 	}
 
 	public boolean isInserted() {
 		return inserted;
 	}
 
 	public void setInserted(boolean inserted) {
 		this.inserted = inserted;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "EntityDiscriminator" );
 		sb.append( "{valueBinding=" ).append( valueBinding );
 		sb.append( ", forced=" ).append( forced );
 		sb.append( ", inserted=" ).append( inserted );
 		sb.append( '}' );
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
index e3cc873501..aedde836a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
@@ -1,97 +1,97 @@
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
-	private SimpleAttributeBinding attributeBinding;
+	private SimpleSingularAttributeBinding attributeBinding;
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
 
-	public SimpleAttributeBinding getValueBinding() {
+	public SimpleSingularAttributeBinding getValueBinding() {
 		return attributeBinding;
 	}
 
-	public void setValueBinding(SimpleAttributeBinding attributeBinding) {
+	public void setValueBinding(SimpleSingularAttributeBinding attributeBinding) {
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
-		return attributeBinding.getValuesSpan()>1;
+		return attributeBinding.getSimpleValueSpan()>1;
 	}
 
 	public boolean isIdentifierMapper() {
 		return isIdentifierMapper;
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory factory, Properties properties) {
 		if ( idGenerator != null ) {
 			identifierGenerator = attributeBinding.createIdentifierGenerator( idGenerator, factory, properties );
 		}
 		return identifierGenerator;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/KeyValueBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/KeyValueBinding.java
index e1d2489769..cc48595d02 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/KeyValueBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/KeyValueBinding.java
@@ -1,37 +1,35 @@
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
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public interface KeyValueBinding extends AttributeBinding {
 	public boolean isKeyCascadeDeleteEnabled();
 
 	public String getUnsavedValue();
-
-	public boolean isUpdatable();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToAnyCollectionElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToAnyCollectionElement.java
index 895caa0fee..42a3904318 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToAnyCollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToAnyCollectionElement.java
@@ -1,37 +1,33 @@
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
 
-import java.util.HashMap;
-
-import org.dom4j.Element;
-
 /**
  * @author Gail Badner
  */
 public class ManyToAnyCollectionElement extends CollectionElement {
-	ManyToAnyCollectionElement(PluralAttributeBinding binding) {
-		super( binding, CollectionElementType.MANY_TO_ANY );
+	ManyToAnyCollectionElement(AbstractPluralAttributeBinding binding) {
+		super( binding, CollectionElementNature.MANY_TO_ANY );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToManyCollectionElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToManyCollectionElement.java
index c300517473..bb75aa035c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToManyCollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToManyCollectionElement.java
@@ -1,79 +1,80 @@
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
 
 import java.util.HashMap;
 
 import org.dom4j.Element;
 
 /**
  * @author Gail Badner
  */
 public class ManyToManyCollectionElement extends CollectionElement {
+
 	private final java.util.Map manyToManyFilters = new HashMap();
 	private String manyToManyWhere;
 	private String manyToManyOrderBy;
 
 
-	ManyToManyCollectionElement(PluralAttributeBinding binding) {
-		super( binding, CollectionElementType.MANY_TO_MANY );
+	ManyToManyCollectionElement(AbstractPluralAttributeBinding binding) {
+		super( binding, CollectionElementNature.MANY_TO_MANY );
 	}
 
 	public void fromHbmXml(Element node){
 	/*
     <!ELEMENT many-to-many (meta*,(column|formula)*,filter*)>
    	<!ATTLIST many-to-many class CDATA #IMPLIED>
 	<!ATTLIST many-to-many node CDATA #IMPLIED>
 	<!ATTLIST many-to-many embed-xml (true|false) "true">
 	<!ATTLIST many-to-many entity-name CDATA #IMPLIED>
 	<!ATTLIST many-to-many column CDATA #IMPLIED>
 	<!ATTLIST many-to-many formula CDATA #IMPLIED>
 	<!ATTLIST many-to-many not-found (exception|ignore) "exception">
 	<!ATTLIST many-to-many outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST many-to-many fetch (join|select) #IMPLIED>
 	<!ATTLIST many-to-many lazy (false|proxy) #IMPLIED>
 	<!ATTLIST many-to-many foreign-key CDATA #IMPLIED>
 	<!ATTLIST many-to-many unique (true|false) "false">
 	<!ATTLIST many-to-many where CDATA #IMPLIED>
 	<!ATTLIST many-to-many order-by CDATA #IMPLIED>
 	<!ATTLIST many-to-many property-ref CDATA #IMPLIED>
     */
 	}
 
 	public String getManyToManyWhere() {
 		return manyToManyWhere;
 	}
 
 	public void setManyToManyWhere(String manyToManyWhere) {
 		this.manyToManyWhere = manyToManyWhere;
 	}
 
 	public String getManyToManyOrderBy() {
 		return manyToManyOrderBy;
 	}
 
 	public void setManyToManyOrderBy(String manyToManyOrderBy) {
 		this.manyToManyOrderBy = manyToManyOrderBy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
index ce7bb42cbb..caf5eec45b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ManyToOneAttributeBinding.java
@@ -1,194 +1,203 @@
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
 
-import java.util.Iterator;
+import java.util.ArrayList;
+import java.util.List;
 
-import org.hibernate.MappingException;
+import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
-import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
-import org.hibernate.metamodel.relational.Column;
-import org.hibernate.metamodel.relational.ForeignKey;
-import org.hibernate.metamodel.relational.SimpleValue;
-import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
+import org.hibernate.metamodel.domain.SingularAttribute;
 
 /**
  * TODO : javadoc
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
-public class ManyToOneAttributeBinding extends SimpleAttributeBinding implements SingularAssociationAttributeBinding {
-	private String referencedAttributeName;
+public class ManyToOneAttributeBinding extends SimpleSingularAttributeBinding implements SingularAssociationAttributeBinding {
 	private String referencedEntityName;
+	private String referencedAttributeName;
+	private AttributeBinding referencedAttributeBinding;
 
 	private boolean isLogicalOneToOne;
 	private String foreignKeyName;
 
-	private AttributeBinding referencedAttributeBinding;
-
-	private Iterable<CascadeStyle> cascadeStyles;
+	private CascadeStyle cascadeStyle;
+	private FetchMode fetchMode;
 
-	ManyToOneAttributeBinding(EntityBinding entityBinding) {
-		super( entityBinding, false, false );
+	ManyToOneAttributeBinding(EntityBinding entityBinding, SingularAttribute attribute) {
+		super( entityBinding, attribute, false, false );
 	}
 
-	public final ManyToOneAttributeBinding initialize(ManyToOneAttributeBindingState state) {
-		super.initialize( state );
-		referencedAttributeName = state.getReferencedAttributeName();
-		referencedEntityName = state.getReferencedEntityName();
-		return this;
-	}
-
-	public final ManyToOneAttributeBinding initialize(ManyToOneRelationalState state) {
-		super.initializeValueRelationalState( state );
-		isLogicalOneToOne = state.isLogicalOneToOne();
-		foreignKeyName = state.getForeignKeyName();
-		return this;
+	@Override
+	public boolean isAssociation() {
+		return true;
 	}
 
 	@Override
 	public final boolean isPropertyReference() {
 		return referencedAttributeName != null;
 	}
 
 	@Override
 	public final String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	@Override
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	@Override
 	public final String getReferencedAttributeName() {
 		return referencedAttributeName;
 	}
 
 	@Override
 	public void setReferencedAttributeName(String referencedEntityAttributeName) {
 		this.referencedAttributeName = referencedEntityAttributeName;
 	}
 
 	@Override
-	public Iterable<CascadeStyle> getCascadeStyles() {
-		return cascadeStyles;
+	public CascadeStyle getCascadeStyle() {
+		return cascadeStyle;
 	}
 
 	@Override
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
-		this.cascadeStyles = cascadeStyles;
+		List<CascadeStyle> cascadeStyleList = new ArrayList<CascadeStyle>();
+		for ( CascadeStyle style : cascadeStyles ) {
+			if ( style != CascadeStyle.NONE ) {
+				cascadeStyleList.add( style );
+			}
+		}
+		if ( cascadeStyleList.isEmpty() ) {
+			cascadeStyle = CascadeStyle.NONE;
+		}
+		else if ( cascadeStyleList.size() == 1 ) {
+			cascadeStyle = cascadeStyleList.get( 0 );
+		}
+		else {
+			cascadeStyle = new CascadeStyle.MultipleCascadeStyle(
+					cascadeStyleList.toArray( new CascadeStyle[ cascadeStyleList.size() ] )
+			);
+		}
+	}
+
+	@Override
+	public FetchMode getFetchMode() {
+		return fetchMode;
+	}
+
+	@Override
+	public void setFetchMode(FetchMode fetchMode) {
+		this.fetchMode = fetchMode;
 	}
 
 	@Override
 	public final boolean isReferenceResolved() {
 		return referencedAttributeBinding != null;
 	}
 
 	@Override
 	public final void resolveReference(AttributeBinding referencedAttributeBinding) {
 		if ( !referencedEntityName.equals( referencedAttributeBinding.getEntityBinding().getEntity().getName() ) ) {
 			throw new IllegalStateException(
 					"attempt to set EntityBinding with name: [" +
 							referencedAttributeBinding.getEntityBinding().getEntity().getName() +
 							"; entity name should be: " + referencedEntityName
 			);
 		}
 		if ( referencedAttributeName == null ) {
 			referencedAttributeName = referencedAttributeBinding.getAttribute().getName();
 		}
 		else if ( !referencedAttributeName.equals( referencedAttributeBinding.getAttribute().getName() ) ) {
 			throw new IllegalStateException(
 					"Inconsistent attribute name; expected: " + referencedAttributeName +
 							"actual: " + referencedAttributeBinding.getAttribute().getName()
 			);
 		}
 		this.referencedAttributeBinding = referencedAttributeBinding;
-		buildForeignKey();
+//		buildForeignKey();
 	}
 
 	@Override
 	public AttributeBinding getReferencedAttributeBinding() {
 		if ( !isReferenceResolved() ) {
 			throw new IllegalStateException( "Referenced AttributeBiding has not been resolved." );
 		}
 		return referencedAttributeBinding;
 	}
 
 	@Override
 	public final EntityBinding getReferencedEntityBinding() {
 		return referencedAttributeBinding.getEntityBinding();
 	}
 
-	private void buildForeignKey() {
-		// TODO: move this stuff to relational model
-		ForeignKey foreignKey = getValue().getTable()
-				.createForeignKey( referencedAttributeBinding.getValue().getTable(), foreignKeyName );
-		Iterator<SimpleValue> referencingValueIterator = getValues().iterator();
-		Iterator<SimpleValue> targetValueIterator = referencedAttributeBinding.getValues().iterator();
-		while ( referencingValueIterator.hasNext() ) {
-			if ( !targetValueIterator.hasNext() ) {
-				// TODO: improve this message
-				throw new MappingException(
-						"number of values in many-to-one reference is greater than number of values in target"
-				);
-			}
-			SimpleValue referencingValue = referencingValueIterator.next();
-			SimpleValue targetValue = targetValueIterator.next();
-			if ( Column.class.isInstance( referencingValue ) ) {
-				if ( !Column.class.isInstance( targetValue ) ) {
-					// TODO improve this message
-					throw new MappingException( "referencing value is a column, but target is not a column" );
-				}
-				foreignKey.addColumnMapping( Column.class.cast( referencingValue ), Column.class.cast( targetValue ) );
-			}
-			else if ( Column.class.isInstance( targetValue ) ) {
-				// TODO: improve this message
-				throw new MappingException( "referencing value is not a column, but target is a column." );
-			}
-		}
-		if ( targetValueIterator.hasNext() ) {
-			throw new MappingException( "target value has more simple values than referencing value" );
-		}
-	}
-
-	public boolean isSimpleValue() {
-		return false;
-	}
-
-	public void validate() {
-		// can't check this until both the domain and relational states are initialized...
-		if ( getCascadeTypes().contains( CascadeType.DELETE_ORPHAN ) ) {
-			if ( !isLogicalOneToOne ) {
-				throw new MappingException(
-						"many-to-one attribute [" + getAttribute().getName() + "] does not support orphan delete as it is not unique"
-				);
-			}
-		}
-		//TODO: validate that the entity reference is resolved
-	}
-
+//	private void buildForeignKey() {
+//		// TODO: move this stuff to relational model
+//		ForeignKey foreignKey = getValue().getTable()
+//				.createForeignKey( referencedAttributeBinding.getValue().getTable(), foreignKeyName );
+//		Iterator<SimpleValue> referencingValueIterator = getSimpleValues().iterator();
+//		Iterator<SimpleValue> targetValueIterator = referencedAttributeBinding.getSimpleValues().iterator();
+//		while ( referencingValueIterator.hasNext() ) {
+//			if ( !targetValueIterator.hasNext() ) {
+//				// TODO: improve this message
+//				throw new MappingException(
+//						"number of values in many-to-one reference is greater than number of values in target"
+//				);
+//			}
+//			SimpleValue referencingValue = referencingValueIterator.next();
+//			SimpleValue targetValue = targetValueIterator.next();
+//			if ( Column.class.isInstance( referencingValue ) ) {
+//				if ( !Column.class.isInstance( targetValue ) ) {
+//					// TODO improve this message
+//					throw new MappingException( "referencing value is a column, but target is not a column" );
+//				}
+//				foreignKey.addColumnMapping( Column.class.cast( referencingValue ), Column.class.cast( targetValue ) );
+//			}
+//			else if ( Column.class.isInstance( targetValue ) ) {
+//				// TODO: improve this message
+//				throw new MappingException( "referencing value is not a column, but target is a column." );
+//			}
+//		}
+//		if ( targetValueIterator.hasNext() ) {
+//			throw new MappingException( "target value has more simple values than referencing value" );
+//		}
+//	}
+//
+//	public void validate() {
+//		// can't check this until both the domain and relational states are initialized...
+//		if ( getCascadeTypes().contains( CascadeType.DELETE_ORPHAN ) ) {
+//			if ( !isLogicalOneToOne ) {
+//				throw new MappingException(
+//						"many-to-one attribute [" + getAttribute().getName() + "] does not support orphan delete as it is not unique"
+//				);
+//			}
+//		}
+//		//TODO: validate that the entity reference is resolved
+//	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/OneToManyCollectionElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/OneToManyCollectionElement.java
index aae79124f5..0655ae788c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/OneToManyCollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/OneToManyCollectionElement.java
@@ -1,34 +1,34 @@
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
 
 /**
  * @author Gail Badner
  */
 public class OneToManyCollectionElement extends CollectionElement {
 
-	OneToManyCollectionElement(PluralAttributeBinding binding) {
-		super( binding, CollectionElementType.ONE_TO_MANY );
+	OneToManyCollectionElement(AbstractPluralAttributeBinding binding) {
+		super( binding, CollectionElementNature.ONE_TO_MANY );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java
index eb45168669..129d2574cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java
@@ -1,241 +1,40 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 
-import java.util.Comparator;
-import java.util.HashMap;
-import java.util.HashSet;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.FetchMode;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
 import org.hibernate.metamodel.relational.Table;
 
 /**
- * TODO : javadoc
- *
  * @author Steve Ebersole
  */
-public abstract class PluralAttributeBinding extends AbstractAttributeBinding {
-
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			CoreMessageLogger.class, PluralAttributeBinding.class.getName()
-	);
-
-	private Table collectionTable;
-
-	private CollectionKey collectionKey;
-	private final CollectionElement collectionElement;
-
-//	private String role;
-	private FetchMode fetchMode;
-	private boolean extraLazy;
-	private boolean inverse;
-	private boolean mutable = true;
-	private boolean subselectLoadable;
-	private String cacheConcurrencyStrategy;
-	private String cacheRegionName;
-	private String orderBy;
-	private String where;
-	private String referencedPropertyName;
-	private boolean sorted;
-	private Comparator comparator;
-	private String comparatorClassName;
-	private boolean orphanDelete;
-	private int batchSize = -1;
-	private boolean embedded = true;
-	private boolean optimisticLocked = true;
-	private Class collectionPersisterClass;
-	private final java.util.Map filters = new HashMap();
-	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
-
-	private CustomSQL customSQLInsert;
-	private CustomSQL customSQLUpdate;
-	private CustomSQL customSQLDelete;
-	private CustomSQL customSQLDeleteAll;
-
-	private String loaderName;
-
-	protected PluralAttributeBinding(EntityBinding entityBinding, CollectionElementType collectionElementType) {
-		super( entityBinding );
-		collectionElement = collectionElementType.createCollectionElement( this );
-	}
-
-	protected void initializeBinding(PluralAttributeBindingState state) {
-		super.initialize( state );
-		fetchMode = state.getFetchMode();
-		extraLazy = state.isExtraLazy();
-		collectionElement.setNodeName( state.getElementNodeName() );
-		collectionElement.setTypeName( state.getElementTypeName() );
-		inverse = state.isInverse();
-		mutable = state.isMutable();
-		subselectLoadable = state.isSubselectLoadable();
-		if ( isSubselectLoadable() ) {
-			getEntityBinding().setSubselectLoadableCollections( true );
-		}
-		cacheConcurrencyStrategy = state.getCacheConcurrencyStrategy();
-		cacheRegionName = state.getCacheRegionName();
-		orderBy = state.getOrderBy();
-		where = state.getWhere();
-		referencedPropertyName = state.getReferencedPropertyName();
-		sorted = state.isSorted();
-		comparator = state.getComparator();
-		comparatorClassName = state.getComparatorClassName();
-		orphanDelete = state.isOrphanDelete();
-		batchSize = state.getBatchSize();
-		embedded = state.isEmbedded();
-		optimisticLocked = state.isOptimisticLocked();
-		collectionPersisterClass = state.getCollectionPersisterClass();
-		filters.putAll( state.getFilters() );
-		synchronizedTables.addAll( state.getSynchronizedTables() );
-		customSQLInsert = state.getCustomSQLInsert();
-		customSQLUpdate = state.getCustomSQLUpdate();
-		customSQLDelete = state.getCustomSQLDelete();
-		customSQLDeleteAll = state.getCustomSQLDeleteAll();
-		loaderName = state.getLoaderName();
-	}
-
-	@Override
-	public boolean isSimpleValue() {
-		return false;
-	}
-
-	public Table getCollectionTable() {
-		return collectionTable;
-	}
-
-	public void setCollectionTable(Table collectionTable) {
-		this.collectionTable = collectionTable;
-	}
-
-	public CollectionKey getCollectionKey() {
-		return collectionKey;
-	}
-
-	public void setCollectionKey(CollectionKey collectionKey) {
-		this.collectionKey = collectionKey;
-	}
-
-	public CollectionElement getCollectionElement() {
-		return collectionElement;
-	}
-
-	public boolean isExtraLazy() {
-		return extraLazy;
-	}
-
-	public boolean isInverse() {
-		return inverse;
-	}
-
-	public boolean isMutable() {
-		return mutable;
-	}
-
-	public boolean isSubselectLoadable() {
-		return subselectLoadable;
-	}
-
-	public String getCacheConcurrencyStrategy() {
-		return cacheConcurrencyStrategy;
-	}
-
-	public String getCacheRegionName() {
-		return cacheRegionName;
-	}
-
-	public String getOrderBy() {
-		return orderBy;
-	}
-
-	public String getWhere() {
-		return where;
-	}
-
-	public String getReferencedPropertyName() {
-		return referencedPropertyName;
-	}
-
-	public boolean isSorted() {
-		return sorted;
-	}
-
-	public Comparator getComparator() {
-		return comparator;
-	}
-
-	public void setComparator(Comparator comparator) {
-		this.comparator = comparator;
-	}
-
-	public String getComparatorClassName() {
-		return comparatorClassName;
-	}
-
-	public boolean isOrphanDelete() {
-		return orphanDelete;
-	}
-
-	public int getBatchSize() {
-		return batchSize;
-	}
-
-	public boolean isOptimisticLocked() {
-		return optimisticLocked;
-	}
-
-	public Class getCollectionPersisterClass() {
-		return collectionPersisterClass;
-	}
-
-	public void addFilter(String name, String condition) {
-		filters.put( name, condition );
-	}
-
-	public java.util.Map getFilterMap() {
-		return filters;
-	}
-
-	public CustomSQL getCustomSQLInsert() {
-		return customSQLInsert;
-	}
+public interface PluralAttributeBinding extends AttributeBinding, AssociationAttributeBinding {
+	// todo : really it is the element (and/or index) that can be associative not the collection itself...
 
-	public CustomSQL getCustomSQLUpdate() {
-		return customSQLUpdate;
-	}
+	public CollectionKey getCollectionKey();
 
-	public CustomSQL getCustomSQLDelete() {
-		return customSQLDelete;
-	}
+	public CollectionElement getCollectionElement();
 
-	public CustomSQL getCustomSQLDeleteAll() {
-		return customSQLDeleteAll;
-	}
+	public Table getCollectionTable();
 
-	public String getLoaderName() {
-		return loaderName;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleSingularAttributeBinding.java
similarity index 76%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleSingularAttributeBinding.java
index c725199c37..3bd8b351f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleSingularAttributeBinding.java
@@ -1,241 +1,195 @@
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
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.mapping.PropertyGeneration;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.state.ColumnRelationalState;
-import org.hibernate.metamodel.relational.state.ValueRelationalState;
+import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
-public class SimpleAttributeBinding extends AbstractAttributeBinding implements SingularAttributeBinding, KeyValueBinding {
-	private boolean insertable;
-	private boolean updatable;
-	private PropertyGeneration generation;
+public class SimpleSingularAttributeBinding
+		extends AbstractSingularAttributeBinding
+		implements SingularAttributeBinding, KeyValueBinding {
 
-	private String propertyAccessorName;
 	private String unsavedValue;
+	private PropertyGeneration generation;
+	private boolean includedInOptimisticLocking;
 
 	private boolean forceNonNullable;
 	private boolean forceUnique;
 	private boolean keyCascadeDeleteEnabled;
 
-	private boolean includedInOptimisticLocking;
 	private MetaAttributeContext metaAttributeContext;
 
-	SimpleAttributeBinding(EntityBinding entityBinding, boolean forceNonNullable, boolean forceUnique) {
-		super( entityBinding );
+	SimpleSingularAttributeBinding(
+			EntityBinding entityBinding,
+			SingularAttribute attribute,
+			boolean forceNonNullable,
+			boolean forceUnique) {
+		super( entityBinding, attribute );
 		this.forceNonNullable = forceNonNullable;
 		this.forceUnique = forceUnique;
 	}
 
-	public final SimpleAttributeBinding initialize(SimpleAttributeBindingState state) {
-		super.initialize( state );
-		insertable = state.isInsertable();
-		updatable = state.isUpdatable();
-		keyCascadeDeleteEnabled = state.isKeyCascadeDeleteEnabled();
-		unsavedValue = state.getUnsavedValue();
-		generation = state.getPropertyGeneration() == null ? PropertyGeneration.NEVER : state.getPropertyGeneration();
-		return this;
-	}
-
-	public SimpleAttributeBinding initialize(ValueRelationalState state) {
-		super.initializeValueRelationalState( state );
-		return this;
-	}
-
-	private boolean isUnique(ColumnRelationalState state) {
-		return isPrimaryKey() || state.isUnique();
+	@Override
+	public boolean isAssociation() {
+		return false;
 	}
 
 	@Override
-	public SingularAttribute getAttribute() {
-		return (SingularAttribute) super.getAttribute();
+	public String getUnsavedValue() {
+		return unsavedValue;
 	}
 
-	@Override
-	public boolean isSimpleValue() {
-		return true;
+	public void setUnsavedValue(String unsavedValue) {
+		this.unsavedValue = unsavedValue;
 	}
 
-	public boolean isInsertable() {
-		return insertable;
+	@Override
+	public PropertyGeneration getGeneration() {
+		return generation;
 	}
 
-	public void setInsertable(boolean insertable) {
-		this.insertable = insertable;
+	public void setGeneration(PropertyGeneration generation) {
+		this.generation = generation;
 	}
 
-	public boolean isUpdatable() {
-		return updatable;
+	public boolean isIncludedInOptimisticLocking() {
+		return includedInOptimisticLocking;
 	}
 
-	public void setUpdatable(boolean updatable) {
-		this.updatable = updatable;
+	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
+		this.includedInOptimisticLocking = includedInOptimisticLocking;
 	}
 
 	@Override
 	public boolean isKeyCascadeDeleteEnabled() {
 		return keyCascadeDeleteEnabled;
 	}
 
 	public void setKeyCascadeDeleteEnabled(boolean keyCascadeDeleteEnabled) {
 		this.keyCascadeDeleteEnabled = keyCascadeDeleteEnabled;
 	}
 
-	@Override
-	public String getUnsavedValue() {
-		return unsavedValue;
-	}
-
-	public void setUnsavedValue(String unsaveValue) {
-		this.unsavedValue = unsaveValue;
-	}
-
 	public boolean forceNonNullable() {
 		return forceNonNullable;
 	}
 
 	public boolean forceUnique() {
 		return forceUnique;
 	}
 
-	public PropertyGeneration getGeneration() {
-		return generation;
-	}
-
-	public void setGeneration(PropertyGeneration generation) {
-		this.generation = generation;
-	}
-
-	public String getPropertyAccessorName() {
-		return propertyAccessorName;
-	}
-
-	public void setPropertyAccessorName(String propertyAccessorName) {
-		this.propertyAccessorName = propertyAccessorName;
-	}
-
-	public boolean isIncludedInOptimisticLocking() {
-		return includedInOptimisticLocking;
-	}
-
-	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
-		this.includedInOptimisticLocking = includedInOptimisticLocking;
-	}
-
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
 
 	/* package-protected */
 	IdentifierGenerator createIdentifierGenerator(
 			IdGenerator idGenerator,
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Properties properties) {
 		Properties params = new Properties();
 		params.putAll( properties );
 
 		// use the schema/catalog specified by getValue().getTable() - but note that
 		// if the schema/catalog were specified as params, they will already be initialized and
 		//will override the values set here (they are in idGenerator.getParameters().)
 		Schema schema = getValue().getTable().getSchema();
 		if ( schema != null ) {
 			if ( schema.getName().getSchema() != null ) {
 				params.setProperty( PersistentIdentifierGenerator.SCHEMA, schema.getName().getSchema().getName() );
 			}
 			if ( schema.getName().getCatalog() != null ) {
 				params.setProperty(PersistentIdentifierGenerator.CATALOG, schema.getName().getCatalog().getName() );
 			}
 		}
 
 		// TODO: not sure how this works for collection IDs...
 		//pass the entity-name, if not a collection-id
 		//if ( rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, getEntityBinding().getEntity().getName() );
 		//}
 
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getValue().getTable().getQualifiedName( identifierGeneratorFactory.getDialect() );
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 
 		//pass the column name (a generated id almost always has a single column)
 		if ( getValuesSpan() != 1 ) {
 			throw new MappingException( "A SimpleAttributeBinding has a more than 1 Value: " + getAttribute().getName() );
 		}
 		SimpleValue simpleValue = getValues().iterator().next();
 		if ( ! Column.class.isInstance( simpleValue ) ) {
 			throw new MappingException(
 					"Cannot create an IdentifierGenerator because the value is not a column: " +
 							simpleValue.toLoggableString()
 			);
 		}
 		params.setProperty(
 				PersistentIdentifierGenerator.PK,
 				( ( Column ) simpleValue ).getColumnName().encloseInQuotesIfQuoted(
 						identifierGeneratorFactory.getDialect()
 				)
 		);
 
 		// TODO: is this stuff necessary for SimpleValue???
 		//if (rootClass!=null) {
 		//	StringBuffer tables = new StringBuffer();
 		//	Iterator iter = rootClass.getIdentityTables().iterator();
 		//	while ( iter.hasNext() ) {
 		//		Table table= (Table) iter.next();
 		//		tables.append( table.getQuotedName(dialect) );
 		//		if ( iter.hasNext() ) tables.append(", ");
 		//	}
 		//	params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		//}
 		//else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		//}
 
 		params.putAll( idGenerator.getParameters() );
 
 		return identifierGeneratorFactory.createIdentifierGenerator(
 				idGenerator.getStrategy(), getHibernateTypeDescriptor().getResolvedTypeMapping(), params
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleValueBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleValueBinding.java
new file mode 100644
index 0000000000..0a8dd1afc3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleValueBinding.java
@@ -0,0 +1,100 @@
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
+package org.hibernate.metamodel.binding;
+
+import org.hibernate.metamodel.relational.Column;
+import org.hibernate.metamodel.relational.DerivedValue;
+import org.hibernate.metamodel.relational.SimpleValue;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SimpleValueBinding {
+	private SimpleValue simpleValue;
+	private boolean includeInInsert;
+	private boolean includeInUpdate;
+
+	public SimpleValueBinding() {
+		this( true, true );
+	}
+
+	public SimpleValueBinding(SimpleValue simpleValue) {
+		this();
+		setSimpleValue( simpleValue );
+	}
+
+	public SimpleValueBinding(SimpleValue simpleValue, boolean includeInInsert, boolean includeInUpdate) {
+		this( includeInInsert, includeInUpdate );
+		setSimpleValue( simpleValue );
+	}
+
+	public SimpleValueBinding(boolean includeInInsert, boolean includeInUpdate) {
+		this.includeInInsert = includeInInsert;
+		this.includeInUpdate = includeInUpdate;
+	}
+
+	public SimpleValue getSimpleValue() {
+		return simpleValue;
+	}
+
+	public void setSimpleValue(SimpleValue simpleValue) {
+		this.simpleValue = simpleValue;
+		if ( DerivedValue.class.isInstance( simpleValue ) ) {
+			includeInInsert = false;
+			includeInUpdate = false;
+		}
+	}
+
+	public boolean isDerived() {
+		return DerivedValue.class.isInstance( simpleValue );
+	}
+
+	public boolean isNullable() {
+		return isDerived() || Column.class.cast( simpleValue ).isNullable();
+	}
+
+	/**
+	 * Is the value to be inserted as part of its binding here?
+	 * <p/>
+	 * <b>NOTE</b> that a column may be bound to multiple attributes.  The purpose of this value is to track this
+	 * notion of "insertability" for this particular binding.
+	 *
+	 * @return {@code true} indicates the value should be included; {@code false} indicates it should not
+	 */
+	public boolean isIncludeInInsert() {
+		return includeInInsert;
+	}
+
+	public void setIncludeInInsert(boolean includeInInsert) {
+		this.includeInInsert = includeInInsert;
+	}
+
+	public boolean isIncludeInUpdate() {
+		return includeInUpdate;
+	}
+
+	public void setIncludeInUpdate(boolean includeInUpdate) {
+		this.includeInUpdate = includeInUpdate;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAttributeBinding.java
index f26c70294e..6baf951010 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAttributeBinding.java
@@ -1,30 +1,81 @@
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
 
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.relational.Value;
+
 /**
+ * Specialized binding contract for singular (non-collection) attributes
+ *
  * @author Steve Ebersole
  */
 public interface SingularAttributeBinding extends AttributeBinding {
+	/**
+	 * Obtain the value bound here.  This could potentially be a {@link org.hibernate.metamodel.relational.Tuple}
+	 * indicating multiple database values are bound, in which case access to the individual values can be achieved by
+	 * either casting this return to {@link org.hibernate.metamodel.relational.Tuple} and using its
+	 * {@link org.hibernate.metamodel.relational.Tuple#values()} method or using the {@link #getSimpleValueBindings()}
+	 * method here and accessing each bindings {@link SimpleValueBinding#getSimpleValue simple value}
+	 *
+	 * @return The bound value
+	 */
+	public Value getValue();
+
+	/**
+	 * Returns the number of {@link SimpleValueBinding} objects that will be returned by
+	 * {@link #getSimpleValueBindings()}
+	 *
+	 * @return the number of {@link SimpleValueBinding simple value bindings}
+	 *
+	 * @see #getSimpleValueBindings()
+	 */
+	public int getSimpleValueSpan();
+
+	public Iterable<SimpleValueBinding> getSimpleValueBindings();
+
+	public void setSimpleValueBindings(Iterable<SimpleValueBinding> simpleValueBindings);
+
+	/**
+	 * Convenience method to determine if any {@link SimpleValueBinding simple value bindings} are derived values
+	 * (formula mappings).
+	 *
+	 * @return {@code true} indicates that the binding contains a derived value; {@code false} indicates it does not.
+	 */
+	public boolean hasDerivedValue();
+
+	/**
+	 * Convenience method to determine if all {@link SimpleValueBinding simple value bindings} allow nulls.
+	 *
+	 * @return {@code true} indicates that all values allow {@code null}; {@code false} indicates one or more do not
+	 */
+	public boolean isNullable();
+
+	/**
+	 * Obtain the generation strategy for this attribute/value.
+	 *
+	 * @return The generation strategy
+	 */
+	public PropertyGeneration getGeneration();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
index 7d29096fc8..130f41222b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
@@ -1,78 +1,77 @@
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
 package org.hibernate.metamodel.source;
 
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.Metadata;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
-import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.type.TypeResolver;
 
 /**
  * @author Steve Ebersole
  */
 public interface MetadataImplementor extends Metadata, BindingContext, Mapping {
 	public BasicServiceRegistry getServiceRegistry();
 
 	public Database getDatabase();
 
 	public TypeResolver getTypeResolver();
 
 	public void addImport(String entityName, String entityName1);
 
 	public void addEntity(EntityBinding entityBinding);
 
-	public void addCollection(PluralAttributeBinding collectionBinding);
+	public void addCollection(AbstractPluralAttributeBinding collectionBinding);
 
 	public void addFetchProfile(FetchProfile profile);
 
 	public void addTypeDefinition(TypeDef typeDef);
 
 	public void addFilterDefinition(FilterDefinition filterDefinition);
 
 	public void addIdGenerator(IdGenerator generator);
 
 	public void registerIdentifierGenerator(String name, String clazz);
 
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def);
 
 	public void addNamedQuery(NamedQueryDefinition def);
 
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition);
 
 	// todo : this needs to move to AnnotationBindingContext
 	public void setGloballyQuotedIdentifiers(boolean b);
 
 	public MetaAttributeContext getGlobalMetaAttributeContext();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java
index b909be0f5c..37e6f730a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ColumnSourceImpl.java
@@ -1,107 +1,122 @@
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
 package org.hibernate.metamodel.source.annotations.attribute;
 
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.source.binder.ColumnSource;
 
 /**
  * @author Hardy Ferentschik
  */
 public class ColumnSourceImpl implements ColumnSource {
 	private final SimpleAttribute attribute;
 	private final ColumnValues columnValues;
 
 	ColumnSourceImpl(SimpleAttribute attribute) {
 		this.attribute = attribute;
 		this.columnValues = attribute.getColumnValues();
 	}
 
 	@Override
 	public String getName() {
 		return columnValues.getName().isEmpty() ? attribute.getName() : columnValues.getName();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return columnValues.isNullable();
 	}
 
 	@Override
 	public String getDefaultValue() {
 		// todo
 		return null;
 	}
 
 	@Override
 	public String getSqlType() {
 		// todo
 		return null;
 	}
 
 	@Override
 	public Datatype getDatatype() {
 		// todo
 		return null;
 	}
 
 	@Override
 	public Size getSize() {
 		return new Size(
 				columnValues.getPrecision(),
 				columnValues.getScale(),
 				columnValues.getLength(),
 				Size.LobMultiplier.NONE
 		);
 	}
 
 	@Override
 	public String getReadFragment() {
 		return attribute.getCustomReadFragment();
 	}
 
 	@Override
 	public String getWriteFragment() {
 		return attribute.getCustomWriteFragment();
 	}
 
 	@Override
 	public boolean isUnique() {
 		return columnValues.isUnique();
 	}
 
 	@Override
 	public String getCheckCondition() {
 		return attribute.getCheckCondition();
 	}
 
 	@Override
 	public String getComment() {
 		// todo
 		return null;
 	}
+
+	@Override
+	public boolean isIncludedInInsert() {
+		return columnValues.isInsertable();
+	}
+
+	@Override
+	public boolean isIncludedInUpdate() {
+		return columnValues.isUpdatable();
+	}
+
+	@Override
+	public String getContainingTableName() {
+		return columnValues.getTable();
+	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SingularAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SingularAttributeSourceImpl.java
index 260e1725db..78797ce3bf 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SingularAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/SingularAttributeSourceImpl.java
@@ -1,126 +1,142 @@
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
 package org.hibernate.metamodel.source.annotations.attribute;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 
 /**
  * @author Hardy Ferentschik
  */
 public class SingularAttributeSourceImpl implements SingularAttributeSource {
 	private final SimpleAttribute attribute;
 
 	public SingularAttributeSourceImpl(SimpleAttribute attribute) {
 		this.attribute = attribute;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
-		return false;  //To change body of implemented methods use File | Settings | File Templates.
+		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.BASIC;
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return new ExplicitHibernateTypeSource() {
 			@Override
 			public String getName() {
 				return attribute.getExplicitHibernateTypeName();
 			}
 
 			@Override
 			public Map<String, String> getParameters() {
 				return attribute.getExplicitHibernateTypeParameters();
 			}
 		};
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
-		return null;  //To change body of implemented methods use File | Settings | File Templates.
+		// todo : implememt
+		return null;
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return attribute.isInsertable();
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return attribute.isUpdatable();
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return attribute.getPropertyGeneration();
 	}
 
 	@Override
 	public boolean isLazy() {
 		return attribute.isLazy();
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return attribute.isOptimisticLockable();
 	}
 
 	@Override
 	public String getName() {
 		return attribute.getName();
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Collections.emptySet();
 	}
 
 	@Override
+	public boolean areValuesIncludedInInsertByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesIncludedInUpdateByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesNullableByDefault() {
+		return true;
+	}
+
+	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		List<RelationalValueSource> valueSources = new ArrayList<RelationalValueSource>();
 		valueSources.add( new ColumnSourceImpl( attribute ) );
 		return valueSources;
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ToOneAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ToOneAttributeSourceImpl.java
index 423976d06a..05c635ac6f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ToOneAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/ToOneAttributeSourceImpl.java
@@ -1,41 +1,77 @@
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
 package org.hibernate.metamodel.source.annotations.attribute;
 
-import java.util.Collections;
+import java.util.HashSet;
+import java.util.Set;
 
+import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.ToOneAttributeSource;
 
 /**
  * @author Hardy Ferentschik
  */
 public class ToOneAttributeSourceImpl extends SingularAttributeSourceImpl implements ToOneAttributeSource {
 	private final AssociationAttribute associationAttribute;
+	private final Set<CascadeStyle> cascadeStyles = new HashSet<CascadeStyle>();
 
 	public ToOneAttributeSourceImpl(AssociationAttribute associationAttribute) {
 		super( associationAttribute );
 		this.associationAttribute = associationAttribute;
+
+		for ( javax.persistence.CascadeType cascadeType : associationAttribute.getCascadeTypes() ) {
+			// todo : ...
+		}
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.MANY_TO_ONE;
 	}
 
 	@Override
 	public String getReferencedEntityName() {
 		return associationAttribute.getReferencedEntityType();
 	}
 
 	@Override
 	public String getReferencedEntityAttributeName() {
 		return null;
 	}
 
 	@Override
-	public Iterable<CascadeStyle> getCascadeStyle() {
-		return Collections.emptySet();
+	public Iterable<CascadeStyle> getCascadeStyles() {
+		return cascadeStyles;
+	}
+
+	@Override
+	public FetchMode getFetchMode() {
+		// todo : implement
+		return FetchMode.DEFAULT;
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
index 99f86cc63e..0a46dfd3f7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
@@ -1,636 +1,642 @@
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
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 import javax.persistence.AccessType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.binder.TableSource;
 
 /**
  * Represents an entity or mapped superclass configured via annotations/xml.
  *
  * @author Hardy Ferentschik
  */
 public class EntityClass extends ConfiguredClass {
 	private final IdType idType;
 	private final InheritanceType inheritanceType;
 	private final TableSource tableSource;
 	private final boolean hasOwnTable;
 	private final String explicitEntityName;
 	private final String customLoaderQueryName;
 	private final List<String> synchronizedTableNames;
 	private final String customTuplizer;
 	private final int batchSize;
 
 	private boolean isMutable;
 	private boolean isExplicitPolymorphism;
 	private OptimisticLockStyle optimisticLockStyle;
 	private String whereClause;
 	private String rowId;
 	private Caching caching;
 	private boolean isDynamicInsert;
 	private boolean isDynamicUpdate;
 	private boolean isSelectBeforeUpdate;
 	private String customPersister;
 
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private boolean isLazy;
 	private String proxy;
 
 	public EntityClass(
 			ClassInfo classInfo,
 			EntityClass parent,
 			AccessType hierarchyAccessType,
 			InheritanceType inheritanceType,
 			AnnotationBindingContext context) {
 		super( classInfo, hierarchyAccessType, parent, context );
 		this.inheritanceType = inheritanceType;
 		this.idType = determineIdType();
 		this.hasOwnTable = definesItsOwnTable();
 		this.explicitEntityName = determineExplicitEntityName();
 		this.tableSource = createTableSource();
 		this.customLoaderQueryName = determineCustomLoader();
 		this.synchronizedTableNames = determineSynchronizedTableNames();
 		this.customTuplizer = determineCustomTuplizer();
 		this.batchSize = determineBatchSize();
 
 		processHibernateEntitySpecificAnnotations();
 		processCustomSqlAnnotations();
 		processProxyGeneration();
 	}
 
 	private String determineExplicitEntityName() {
 		final AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.ENTITY
 		);
 
 		return JandexHelper.getValue( jpaEntityAnnotation, "name", String.class );
 	}
 
 	public InheritanceType getInheritanceType() {
 		return inheritanceType;
 	}
 
 	public IdType getIdType() {
 		return idType;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return isExplicitPolymorphism;
 	}
 
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public String getWhereClause() {
 		return whereClause;
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public Caching getCaching() {
 		return caching;
 	}
 
 	public TableSource getTableSource() {
 		if ( definesItsOwnTable() ) {
 			return tableSource;
 		}
 		else {
 			return ( (EntityClass) getParent() ).getTableSource();
 		}
 	}
 
 	public String getExplicitEntityName() {
 		return explicitEntityName;
 	}
 
 	public String getEntityName() {
 		return getConfiguredClass().getSimpleName();
 	}
 
 	public boolean isDynamicInsert() {
 		return isDynamicInsert;
 	}
 
 	public boolean isDynamicUpdate() {
 		return isDynamicUpdate;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return isSelectBeforeUpdate;
 	}
 
 	public String getCustomLoaderQueryName() {
 		return customLoaderQueryName;
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
 
 	public List<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 
 	public String getCustomPersister() {
 		return customPersister;
 	}
 
 	public String getCustomTuplizer() {
 		return customTuplizer;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public String getProxy() {
 		return proxy;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public boolean isEntityRoot() {
 		return getParent() == null;
 	}
 
 	private boolean definesItsOwnTable() {
 		return !InheritanceType.SINGLE_TABLE.equals( inheritanceType ) || isEntityRoot();
 	}
 
 	private String processTableAnnotation() {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(),
 				JPADotNames.TABLE
 		);
 
 		String tableName = null;
 		if ( tableAnnotation != null ) {
 			String explicitTableName = JandexHelper.getValue( tableAnnotation, "name", String.class );
 			if ( StringHelper.isNotEmpty( explicitTableName ) ) {
 				tableName = getContext().getNamingStrategy().tableName( explicitTableName );
 				if ( getContext().isGloballyQuotedIdentifiers() && !Identifier.isQuoted( explicitTableName ) ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 		}
 		return tableName;
 	}
 
 	private IdType determineIdType() {
 		List<AnnotationInstance> idAnnotations = findIdAnnotations( JPADotNames.ID );
 		List<AnnotationInstance> embeddedIdAnnotations = findIdAnnotations( JPADotNames.EMBEDDED_ID );
 
 		if ( !idAnnotations.isEmpty() && !embeddedIdAnnotations.isEmpty() ) {
 			throw new MappingException(
 					"@EmbeddedId and @Id cannot be used together. Check the configuration for " + getName() + "."
 			);
 		}
 
 		if ( !embeddedIdAnnotations.isEmpty() ) {
 			if ( embeddedIdAnnotations.size() == 1 ) {
 				return IdType.EMBEDDED;
 			}
 			else {
 				throw new AnnotationException( "Multiple @EmbeddedId annotations are not allowed" );
 			}
 		}
 
 		if ( !idAnnotations.isEmpty() ) {
 			if ( idAnnotations.size() == 1 ) {
 				return IdType.SIMPLE;
 			}
 			else {
 				return IdType.COMPOSED;
 			}
 		}
 		return IdType.NONE;
 	}
 
 	private List<AnnotationInstance> findIdAnnotations(DotName idAnnotationType) {
 		List<AnnotationInstance> idAnnotationList = new ArrayList<AnnotationInstance>();
 		if ( getClassInfo().annotations().get( idAnnotationType ) != null ) {
 			idAnnotationList.addAll( getClassInfo().annotations().get( idAnnotationType ) );
 		}
 		ConfiguredClass parent = getParent();
 		while ( parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) ) ) {
 			if ( parent.getClassInfo().annotations().get( idAnnotationType ) != null ) {
 				idAnnotationList.addAll( parent.getClassInfo().annotations().get( idAnnotationType ) );
 			}
 			parent = parent.getParent();
 
 		}
 		return idAnnotationList;
 	}
 
 	private void processHibernateEntitySpecificAnnotations() {
 		final AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		// see HHH-6400
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 			polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 		}
 		isExplicitPolymorphism = polymorphism == PolymorphismType.EXPLICIT;
 
 		// see HHH-6401
 		OptimisticLockType optimisticLockType = OptimisticLockType.VERSION;
 		if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
 			optimisticLockType = OptimisticLockType.valueOf(
 					hibernateEntityAnnotation.value( "optimisticLock" )
 							.asEnum()
 			);
 		}
 		optimisticLockStyle = OptimisticLockStyle.valueOf( optimisticLockType.name() );
 
 		final AnnotationInstance hibernateImmutableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		isMutable = hibernateImmutableAnnotation == null
 				&& hibernateEntityAnnotation != null
 				&& hibernateEntityAnnotation.value( "mutable" ) != null
 				&& hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 
 
 		final AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.WHERE
 		);
 		whereClause = whereAnnotation != null && whereAnnotation.value( "clause" ) != null ?
 				whereAnnotation.value( "clause" ).asString() : null;
 
 		final AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.ROW_ID
 		);
 		rowId = rowIdAnnotation != null && rowIdAnnotation.value() != null
 				? rowIdAnnotation.value().asString() : null;
 
 		caching = determineCachingSettings();
 
 		// see HHH-6397
 		isDynamicInsert =
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean();
 
 		// see HHH-6398
 		isDynamicUpdate =
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean();
 
 
 		// see HHH-6399
 		isSelectBeforeUpdate =
 				hibernateEntityAnnotation != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null
 						&& hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean();
 
 		// Custom persister
 		final String entityPersisterClass;
 		final AnnotationInstance persisterAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.PERSISTER
 		);
 		if ( persisterAnnotation == null || persisterAnnotation.value( "impl" ) == null ) {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				entityPersisterClass = hibernateEntityAnnotation.value( "persister" ).asString();
 			}
 			else {
 				entityPersisterClass = null;
 			}
 		}
 		else {
 			if ( hibernateEntityAnnotation != null && hibernateEntityAnnotation.value( "persister" ) != null ) {
 				// todo : error?
 			}
 			entityPersisterClass = persisterAnnotation.value( "impl" ).asString();
 		}
 		this.customPersister = entityPersisterClass;
 	}
 
 	private Caching determineCachingSettings() {
 		final AnnotationInstance hibernateCacheAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( hibernateCacheAnnotation != null ) {
 			final org.hibernate.cache.spi.access.AccessType accessType = hibernateCacheAnnotation.value( "usage" ) == null
 					? getContext().getMappingDefaults().getCacheAccessType()
 					: CacheConcurrencyStrategy.parse( hibernateCacheAnnotation.value( "usage" ).asEnum() )
 					.toAccessType();
 			return new Caching(
 					hibernateCacheAnnotation.value( "region" ) == null
 							? getName()
 							: hibernateCacheAnnotation.value( "region" ).asString(),
 					accessType,
 					hibernateCacheAnnotation.value( "include" ) != null
 							&& "all".equals( hibernateCacheAnnotation.value( "include" ).asString() )
 			);
 		}
 
 		final AnnotationInstance jpaCacheableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( jpaCacheableAnnotation != null && jpaCacheableAnnotation.value() != null ) {
 			cacheable = jpaCacheableAnnotation.value().asBoolean();
 		}
 
 		final boolean doCaching;
 		switch ( getContext().getMetadataImplementor().getOptions().getSharedCacheMode() ) {
 			case ALL: {
 				doCaching = true;
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				doCaching = cacheable;
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				doCaching = jpaCacheableAnnotation == null || cacheable;
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				doCaching = false;
 				break;
 			}
 		}
 
 		if ( !doCaching ) {
 			return null;
 		}
 
 		return new Caching(
 				getName(),
 				getContext().getMappingDefaults().getCacheAccessType(),
 				true
 		);
 	}
 
 	private TableSource createTableSource() {
 		if ( !hasOwnTable ) {
 			return null;
 		}
 
 		String schema = getContext().getMappingDefaults().getSchemaName();
 		String catalog = getContext().getMappingDefaults().getCatalogName();
 
 		final AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.TABLE
 		);
 		if ( tableAnnotation != null ) {
 			final AnnotationValue schemaValue = tableAnnotation.value( "schema" );
 			if ( schemaValue != null ) {
 				schema = schemaValue.asString();
 			}
 
 			final AnnotationValue catalogValue = tableAnnotation.value( "catalog" );
 			if ( catalogValue != null ) {
 				catalog = catalogValue.asString();
 			}
 		}
 
 		if ( getContext().isGloballyQuotedIdentifiers() ) {
 			schema = StringHelper.quote( schema );
 			catalog = StringHelper.quote( catalog );
 		}
 
 		String tableName = processTableAnnotation();
 		// use the simple table name as default in case there was no table annotation
 		if ( tableName == null ) {
 			if ( explicitEntityName == null ) {
 				tableName = getConfiguredClass().getSimpleName();
 			}
 			else {
 				tableName = explicitEntityName;
 			}
 		}
 		return new TableSourceImpl( schema, catalog, tableName );
 	}
 
 	private String determineCustomLoader() {
 		String customLoader = null;
 		// Custom sql loader
 		final AnnotationInstance sqlLoaderAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.LOADER
 		);
 		if ( sqlLoaderAnnotation != null ) {
 			customLoader = sqlLoaderAnnotation.value( "namedQuery" ).asString();
 		}
 		return customLoader;
 	}
 
 	private CustomSQL createCustomSQL(AnnotationInstance customSqlAnnotation) {
 		if ( customSqlAnnotation == null ) {
 			return null;
 		}
 
 		final String sql = customSqlAnnotation.value( "sql" ).asString();
 		final boolean isCallable = customSqlAnnotation.value( "callable" ) != null
 				&& customSqlAnnotation.value( "callable" ).asBoolean();
 
 		final ExecuteUpdateResultCheckStyle checkStyle = customSqlAnnotation.value( "check" ) == null
 				? isCallable
 				? ExecuteUpdateResultCheckStyle.NONE
 				: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.valueOf( customSqlAnnotation.value( "check" ).asEnum() );
 
 		return new CustomSQL( sql, isCallable, checkStyle );
 	}
 
 	private void processCustomSqlAnnotations() {
 		// Custom sql insert
 		final AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		customInsert = createCustomSQL( sqlInsertAnnotation );
 
 		// Custom sql update
 		final AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		customUpdate = createCustomSQL( sqlUpdateAnnotation );
 
 		// Custom sql delete
 		final AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		customDelete = createCustomSQL( sqlDeleteAnnotation );
 	}
 
 	private List<String> determineSynchronizedTableNames() {
 		final AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 		if ( synchronizeAnnotation != null ) {
 			final String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			return Arrays.asList( tableNames );
 		}
 		else {
 			return Collections.emptyList();
 		}
 	}
 
 	private String determineCustomTuplizer() {
 		// Custom tuplizer
 		String customTuplizer = null;
 		final AnnotationInstance pojoTuplizerAnnotation = locatePojoTuplizerAnnotation();
 		if ( pojoTuplizerAnnotation != null ) {
 			customTuplizer = pojoTuplizerAnnotation.value( "impl" ).asString();
 		}
 		return customTuplizer;
 	}
 
 	private AnnotationInstance locatePojoTuplizerAnnotation() {
 		final AnnotationInstance tuplizersAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.TUPLIZERS
 		);
 		if ( tuplizersAnnotation == null ) {
 			return null;
 		}
 
 		AnnotationInstance[] annotations = JandexHelper.getValue(
 				tuplizersAnnotation,
 				"value",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance tuplizerAnnotation : annotations ) {
 			if ( EntityMode.valueOf( tuplizerAnnotation.value( "entityModeType" ).asEnum() ) == EntityMode.POJO ) {
 				return tuplizerAnnotation;
 			}
 		}
 		return null;
 	}
 
 	private void processProxyGeneration() {
 		// Proxy generation
 		final AnnotationInstance hibernateProxyAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.PROXY
 		);
 		if ( hibernateProxyAnnotation != null ) {
 			isLazy = hibernateProxyAnnotation.value( "lazy" ) == null
 					|| hibernateProxyAnnotation.value( "lazy" ).asBoolean();
 			if ( isLazy ) {
 				final AnnotationValue proxyClassValue = hibernateProxyAnnotation.value( "proxyClass" );
 				if ( proxyClassValue == null ) {
 					proxy = getName();
 				}
 				else {
 					proxy = proxyClassValue.asString();
 				}
 			}
 			else {
 				proxy = null;
 			}
 		}
 		else {
 			isLazy = true;
 			proxy = getName();
 		}
 	}
 
 	private int determineBatchSize() {
 		final AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 		return batchSizeAnnotation == null ? -1 : batchSizeAnnotation.value( "size" ).asInt();
 	}
 
 	class TableSourceImpl implements TableSource {
 		private final String schema;
 		private final String catalog;
 		private final String tableName;
 
 		TableSourceImpl(String schema, String catalog, String tableName) {
 			this.schema = schema;
 			this.catalog = catalog;
 			this.tableName = tableName;
 		}
 
 		@Override
 		public String getExplicitSchemaName() {
 			return schema;
 		}
 
 		@Override
 		public String getExplicitCatalogName() {
 			return catalog;
 		}
 
 		@Override
 		public String getExplicitTableName() {
 			return tableName;
 		}
+
+		@Override
+		public String getLogicalName() {
+			// todo : (steve) hardy, not sure what to use here... null is ok for the primary table name.  this is part of the secondary table support.
+			return null;
+		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java
index 713c19e73c..90a8c80b61 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AssociationAttributeSource.java
@@ -1,40 +1,48 @@
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
 package org.hibernate.metamodel.source.binder;
 
+import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 
 /**
  * Contract describing sources for attributes which model associations.
  *
  * @author Steve Ebersole
  */
 public interface AssociationAttributeSource extends AttributeSource {
 	/**
 	 * Obtain the cascade styles to be applied to this association.
 	 *
 	 * @return The cascade styles.
 	 */
 	public Iterable<CascadeStyle> getCascadeStyles();
+
+	/**
+	 * Obtain the fetch mode to be applied to this association.
+	 *
+	 * @return The fetch mode.
+	 */
+	public FetchMode getFetchMode();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java
index 02ca5bbb20..296511654d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java
@@ -1,52 +1,57 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
  * Contract for sources of persistent attribute descriptions.
  *
  * @author Steve Ebersole
  */
 public interface AttributeSource {
 	/**
 	 * Obtain the attribute name.
 	 *
 	 * @return The attribute name.  {@code nulls} are NOT allowed!
 	 */
 	public String getName();
 
 	/**
 	 * Is this a singular attribute?  Specifically, can it be cast to {@link SingularAttributeSource}?
 	 *
 	 * @return {@code true} indicates this is castable to {@link SingularAttributeSource}; {@code false} otherwise.
 	 */
 	public boolean isSingular();
 
+	public String getPropertyAccessorName();
+
+	public boolean isIncludedInOptimisticLocking();
+
 	/**
 	 * Obtain the meta-attribute sources associated with this attribute.
 	 *
 	 * @return The meta-attribute sources.
 	 */
 	public Iterable<MetaAttributeSource> metaAttributes();
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
index ef61c8b6ac..4404801660 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
@@ -1,612 +1,653 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
+import org.hibernate.metamodel.binding.AttributeBinding;
+import org.hibernate.metamodel.binding.CollectionElementNature;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.MetaAttribute;
-import org.hibernate.metamodel.binding.SimpleAttributeBinding;
+import org.hibernate.metamodel.binding.SimpleSingularAttributeBinding;
+import org.hibernate.metamodel.binding.SimpleValueBinding;
+import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
+import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.hbm.Helper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * The common binder shared between annotations and {@code hbm.xml} processing.
  * <p/>
  * The API consists of {@link #Binder} and {@link #processEntityHierarchy}
  *
  * @author Steve Ebersole
  */
 public class Binder {
 	private final MetadataImplementor metadata;
 	private final List<String> processedEntityNames;
 
 	private InheritanceType currentInheritanceType;
 	private EntityMode currentHierarchyEntityMode;
 	private LocalBindingContext currentBindingContext;
 
 	public Binder(MetadataImplementor metadata, List<String> processedEntityNames) {
 		this.metadata = metadata;
 		this.processedEntityNames = processedEntityNames;
 	}
 
 	/**
 	 * Process an entity hierarchy.
 	 *
 	 * @param entityHierarchy THe hierarchy to process.
 	 */
 	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
 		currentInheritanceType = entityHierarchy.getHierarchyInheritanceType();
 		EntityBinding rootEntityBinding = createEntityBinding( entityHierarchy.getRootEntitySource(), null );
 		if ( currentInheritanceType != InheritanceType.NO_INHERITANCE ) {
 			processHierarchySubEntities( entityHierarchy.getRootEntitySource(), rootEntityBinding );
 		}
 		currentHierarchyEntityMode = null;
 	}
 
 	private void processHierarchySubEntities(SubclassEntityContainer subclassEntitySource, EntityBinding superEntityBinding) {
 		for ( SubclassEntitySource subEntity : subclassEntitySource.subclassEntitySources() ) {
 			EntityBinding entityBinding = createEntityBinding( subEntity, superEntityBinding );
 			processHierarchySubEntities( subEntity, entityBinding );
 		}
 	}
 
 
 	// Entities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityBinding createEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( processedEntityNames.contains( entitySource.getEntityName() ) ) {
 			return metadata.getEntityBinding( entitySource.getEntityName() );
 		}
 
 		currentBindingContext = entitySource.getBindingContext();
 		try {
 			final EntityBinding entityBinding = doCreateEntityBinding( entitySource, superEntityBinding );
 
 			metadata.addEntity( entityBinding );
 			processedEntityNames.add( entityBinding.getEntity().getName() );
 
 			processFetchProfiles( entitySource, entityBinding );
 
 			return entityBinding;
 		}
 		finally {
 			currentBindingContext = null;
 		}
 	}
 
 	private EntityBinding doCreateEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = createBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindSecondaryTables( entitySource, entityBinding );
 		bindAttributes( entitySource, entityBinding );
 
 		bindTableUniqueConstraints( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding createBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( superEntityBinding == null ) {
 			return makeRootEntityBinding( (RootEntitySource) entitySource );
 		}
 		else {
 			if ( currentInheritanceType == InheritanceType.SINGLE_TABLE ) {
 				return makeDiscriminatedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.JOINED ) {
 				return makeJoinedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.TABLE_PER_CLASS ) {
 				return makeUnionedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else {
 				// extreme internal error!
 				throw new AssertionFailure( "Internal condition failure" );
 			}
 		}
 	}
 
 	private EntityBinding makeRootEntityBinding(RootEntitySource entitySource) {
 		currentHierarchyEntityMode = entitySource.getEntityMode();
 
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, null );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		bindIdentifier( entitySource, entityBinding );
 		bindVersion( entityBinding, entitySource );
 		bindDiscriminator( entitySource, entityBinding );
 
 		entityBinding.setMutable( entitySource.isMutable() );
 		entityBinding.setExplicitPolymorphism( entitySource.isExplicitPolymorphism() );
 		entityBinding.setWhereFilter( entitySource.getWhere() );
 		entityBinding.setRowId( entitySource.getRowId() );
 		entityBinding.setOptimisticLockStyle( entitySource.getOptimisticLockStyle() );
 		entityBinding.setCaching( entitySource.getCaching() );
 
 		return entityBinding;
 	}
 
 
 	private EntityBinding buildBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setSuperEntityBinding( superEntityBinding );
 		entityBinding.setInheritanceType( currentInheritanceType );
 
 		entityBinding.setEntityMode( currentHierarchyEntityMode );
 
 		final String entityName = entitySource.getEntityName();
 		final String className = currentHierarchyEntityMode == EntityMode.POJO ? entitySource.getClassName() : null;
 
 		final Entity entity = new Entity(
 				entityName,
 				className,
 				currentBindingContext.makeClassReference( className ),
 				null
 		);
 		entityBinding.setEntity( entity );
 
 		entityBinding.setJpaEntityName( entitySource.getJpaEntityName() );
 
 		if ( entityBinding.getEntityMode() == EntityMode.POJO ) {
 			final String proxy = entitySource.getProxy();
 			if ( proxy != null ) {
 				entityBinding.setProxyInterfaceType(
 						currentBindingContext.makeClassReference(
 								currentBindingContext.qualifyClassName( proxy )
 						)
 				);
 				entityBinding.setLazy( true );
 			}
 			else if ( entitySource.isLazy() ) {
 				entityBinding.setProxyInterfaceType( entityBinding.getEntity().getClassReferenceUnresolved() );
 				entityBinding.setLazy( true );
 			}
 		}
 		else {
 			entityBinding.setProxyInterfaceType( null );
 			entityBinding.setLazy( entitySource.isLazy() );
 		}
 
 		final String customTuplizerClassName = entitySource.getCustomTuplizerClassName();
 		if ( customTuplizerClassName != null ) {
 			entityBinding.setCustomEntityTuplizerClass( currentBindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName ) );
 		}
 
 		final String customPersisterClassName = entitySource.getCustomPersisterClassName();
 		if ( customPersisterClassName != null ) {
 			entityBinding.setCustomEntityPersisterClass( currentBindingContext.<EntityPersister>locateClassByName( customPersisterClassName ) );
 		}
 
 		entityBinding.setMetaAttributeContext( buildMetaAttributeContext( entitySource ) );
 
 		entityBinding.setDynamicUpdate( entitySource.isDynamicUpdate() );
 		entityBinding.setDynamicInsert( entitySource.isDynamicInsert() );
 		entityBinding.setBatchSize( entitySource.getBatchSize() );
 		entityBinding.setSelectBeforeUpdate( entitySource.isSelectBeforeUpdate() );
 		entityBinding.setAbstract( entitySource.isAbstract() );
 
 		entityBinding.setCustomLoaderName( entitySource.getCustomLoaderName() );
 		entityBinding.setCustomInsert( entitySource.getCustomSqlInsert() );
 		entityBinding.setCustomUpdate( entitySource.getCustomSqlUpdate() );
 		entityBinding.setCustomDelete( entitySource.getCustomSqlDelete() );
 
 		if ( entitySource.getSynchronizedTableNames() != null ) {
 			entityBinding.addSynchronizedTableNames( entitySource.getSynchronizedTableNames() );
 		}
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeDiscriminatedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		entityBinding.setBaseTable( superEntityBinding.getBaseTable() );
 
 		bindDiscriminatorValue( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeJoinedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : join
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeUnionedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : ??
 
 		return entityBinding;
 	}
 
 
 	// Attributes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, EntityBinding entityBinding) {
-		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
-		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
-		// for now, simply assume all columns come from the base table....
-
-		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
-			if ( attributeSource.isSingular() ) {
-				doBasicSingularAttributeBindingCreation( (SingularAttributeSource) attributeSource, entityBinding );
-			}
-			// todo : components and collections
-		}
-	}
-
 	private void bindIdentifier(RootEntitySource entitySource, EntityBinding entityBinding) {
 		if ( entitySource.getIdentifierSource() == null ) {
 			throw new AssertionFailure( "Expecting identifier information on root entity descriptor" );
 		}
 		switch ( entitySource.getIdentifierSource().getNature() ) {
 			case SIMPLE: {
 				bindSimpleIdentifier( (SimpleIdentifierSource) entitySource.getIdentifierSource(), entityBinding );
 			}
 			case AGGREGATED_COMPOSITE: {
 				// composite id with an actual component class
 			}
 			case COMPOSITE: {
 				// what we used to term an "embedded composite identifier", which is not tobe confused with the JPA
 				// term embedded. Specifically a composite id where there is no component class, though there may
 				// be a @IdClass :/
 			}
 		}
 	}
 
 	private void bindSimpleIdentifier(SimpleIdentifierSource identifierSource, EntityBinding entityBinding) {
-		final SimpleAttributeBinding idAttributeBinding = doBasicSingularAttributeBindingCreation(
+		final SimpleSingularAttributeBinding idAttributeBinding = doBasicSingularAttributeBindingCreation(
 				identifierSource.getIdentifierAttributeSource(), entityBinding
 		);
 
 		entityBinding.getEntityIdentifier().setValueBinding( idAttributeBinding );
 		entityBinding.getEntityIdentifier().setIdGenerator( identifierSource.getIdentifierGeneratorDescriptor() );
 
 		final org.hibernate.metamodel.relational.Value relationalValue = idAttributeBinding.getValue();
 
 		if ( SimpleValue.class.isInstance( relationalValue ) ) {
 			if ( !Column.class.isInstance( relationalValue ) ) {
 				// this should never ever happen..
 				throw new AssertionFailure( "Simple-id was not a column." );
 			}
 			entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
 		}
 		else {
 			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
 				if ( Column.class.isInstance( subValue ) ) {
 					entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
 				}
 			}
 		}
 	}
 
 	private void bindVersion(EntityBinding entityBinding, RootEntitySource entitySource) {
 		final SingularAttributeSource versioningAttributeSource = entitySource.getVersioningAttributeSource();
 		if ( versioningAttributeSource == null ) {
 			return;
 		}
 
-		SimpleAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
+		SimpleSingularAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
 				versioningAttributeSource, entityBinding
 		);
 		entityBinding.setVersionBinding( attributeBinding );
 	}
 
 	private void bindDiscriminator(RootEntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindDiscriminatorValue(SubclassEntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
-	private SimpleAttributeBinding doBasicSingularAttributeBindingCreation(
+	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, EntityBinding entityBinding) {
+		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
+		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
+		// for now, simply assume all columns come from the base table....
+
+		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
+			if ( attributeSource.isSingular() ) {
+				final SingularAttributeSource singularAttributeSource = (SingularAttributeSource) attributeSource;
+				if ( singularAttributeSource.getNature() == SingularAttributeNature.COMPONENT ) {
+					throw new NotYetImplementedException( "Component binding not yet implemented :(" );
+				}
+				else {
+					doBasicSingularAttributeBindingCreation( singularAttributeSource, entityBinding );
+				}
+			}
+			else {
+				bindPersistentCollection( (PluralAttributeSource) attributeSource, entityBinding );
+			}
+		}
+	}
+
+	private void bindPersistentCollection(PluralAttributeSource attributeSource, EntityBinding entityBinding) {
+		final AbstractPluralAttributeBinding pluralAttributeBinding;
+		if ( attributeSource.getPluralAttributeNature() == PluralAttributeNature.BAG ) {
+			final PluralAttribute pluralAttribute = entityBinding.getEntity().locateOrCreateBag( attributeSource.getName() );
+			pluralAttributeBinding = entityBinding.makeBagAttributeBinding( pluralAttribute, convert( attributeSource.getPluralAttributeElementNature() ) );
+		}
+		else {
+			// todo : implement other collection types
+			throw new NotYetImplementedException( "Collections other than bag not yet implmented :(" );
+		}
+
+		doBasicAttributeBinding( attributeSource, pluralAttributeBinding );
+	}
+
+	private void doBasicAttributeBinding(AttributeSource attributeSource, AttributeBinding attributeBinding) {
+		attributeBinding.setPropertyAccessorName( attributeSource.getPropertyAccessorName() );
+		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
+	}
+
+	private CollectionElementNature convert(PluralAttributeElementNature pluralAttributeElementNature) {
+		return CollectionElementNature.valueOf( pluralAttributeElementNature.name() );
+	}
+
+	private SimpleSingularAttributeBinding doBasicSingularAttributeBindingCreation(
 			SingularAttributeSource attributeSource,
 			EntityBinding entityBinding) {
 		final SingularAttribute attribute = attributeSource.isVirtualAttribute()
 				? entityBinding.getEntity().locateOrCreateVirtualAttribute( attributeSource.getName() )
 				: entityBinding.getEntity().locateOrCreateSingularAttribute( attributeSource.getName() );
 
-		final SimpleAttributeBinding attributeBinding;
+		final SimpleSingularAttributeBinding attributeBinding;
 		if ( attributeSource.getNature() == SingularAttributeNature.BASIC ) {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 		}
 		else if ( attributeSource.getNature() == SingularAttributeNature.MANY_TO_ONE ) {
 			attributeBinding = entityBinding.makeManyToOneAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 			resolveToOneInformation( (ToOneAttributeSource) attributeSource, (ManyToOneAttributeBinding) attributeBinding );
 		}
 		else {
 			throw new NotYetImplementedException();
 		}
 
-		attributeBinding.setInsertable( attributeSource.isInsertable() );
-		attributeBinding.setUpdatable( attributeSource.isUpdatable() );
 		attributeBinding.setGeneration( attributeSource.getGeneration() );
 		attributeBinding.setLazy( attributeSource.isLazy() );
 		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
 
 		attributeBinding.setPropertyAccessorName(
 				Helper.getPropertyAccessorName(
 						attributeSource.getPropertyAccessorName(),
 						false,
 						currentBindingContext.getMappingDefaults().getPropertyAccessorName()
 				)
 		);
 
-		final org.hibernate.metamodel.relational.Value relationalValue = makeValue( attributeSource, attributeBinding );
-		attributeBinding.setValue( relationalValue );
+		bindRelationalValues( attributeSource, attributeBinding );
 
 		attributeBinding.setMetaAttributeContext(
 				buildMetaAttributeContext( attributeSource.metaAttributes(), entityBinding.getMetaAttributeContext() )
 		);
 
 		return attributeBinding;
 	}
 
-	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, SimpleAttributeBinding attributeBinding) {
+	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, SimpleSingularAttributeBinding attributeBinding) {
 		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
 		if ( attributeJavaType != null ) {
 			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
 			attributeBinding.getAttribute().resolveType( currentBindingContext.makeJavaType( attributeJavaType.getName() ) );
 		}
 
 		final String explicitTypeName = typeSource.getName();
 		if ( explicitTypeName != null ) {
 			final TypeDef typeDef = currentBindingContext.getMetadataImplementor().getTypeDefinition( explicitTypeName );
 			if ( typeDef != null ) {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
 			}
 			else {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
 			}
 			final Map<String,String> parameters = typeSource.getParameters();
 			if ( parameters != null ) {
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( parameters );
 			}
 		}
 		else {
 			if ( attributeJavaType == null ) {
 				// we will have problems later determining the Hibernate Type to use.  Should we throw an
 				// exception now?  Might be better to get better contextual info
 			}
 		}
 	}
 
 	private Class<?> determineJavaType(final Attribute attribute) {
 		try {
 			final Class ownerClass = attribute.getAttributeContainer().getClassReference();
 			AttributeJavaTypeDeterminerDelegate delegate = new AttributeJavaTypeDeterminerDelegate( attribute.getName() );
 			BeanInfoHelper.visitBeanInfo( ownerClass, delegate );
 			return delegate.javaType;
 		}
 		catch ( Exception ignore ) {
 			// todo : log it?
 		}
 		return null;
 	}
 
 	private static class AttributeJavaTypeDeterminerDelegate implements BeanInfoHelper.BeanInfoDelegate {
 		private final String attributeName;
 		private Class<?> javaType = null;
 
 		private AttributeJavaTypeDeterminerDelegate(String attributeName) {
 			this.attributeName = attributeName;
 		}
 
 		@Override
 		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
 			for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
 				if ( propertyDescriptor.getName().equals( attributeName ) ) {
 					javaType = propertyDescriptor.getPropertyType();
 					break;
 				}
 			}
 		}
 	}
 
 	private void resolveToOneInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
 		final String referencedEntityName = attributeSource.getReferencedEntityName() != null
 				? attributeSource.getReferencedEntityName()
 				: attributeBinding.getAttribute().getSingularAttributeType().getClassName();
 		attributeBinding.setReferencedEntityName( referencedEntityName );
 		// todo : we should consider basing references on columns instead of property-ref, which would require a resolution (later) of property-ref to column names
 		attributeBinding.setReferencedAttributeName( attributeSource.getReferencedEntityAttributeName() );
 
 		attributeBinding.setCascadeStyles( attributeSource.getCascadeStyles() );
+		attributeBinding.setFetchMode( attributeSource.getFetchMode() );
 	}
 
 	private MetaAttributeContext buildMetaAttributeContext(EntitySource entitySource) {
 		return buildMetaAttributeContext(
 				entitySource.metaAttributes(),
 				true,
 				currentBindingContext.getMetadataImplementor().getGlobalMetaAttributeContext()
 		);
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			MetaAttributeContext parentContext) {
 		return buildMetaAttributeContext( metaAttributeSources, false, parentContext );
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			boolean onlyInheritable,
 			MetaAttributeContext parentContext) {
 		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
 
 		for ( MetaAttributeSource metaAttributeSource : metaAttributeSources ) {
 			if ( onlyInheritable & !metaAttributeSource.isInheritable() ) {
 				continue;
 			}
 
 			final String name = metaAttributeSource.getName();
 			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
 			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				subContext.add( metaAttribute );
 			}
 			metaAttribute.addValue( metaAttributeSource.getValue() );
 		}
 
 		return subContext;
 	}
 
 
 	// Relational ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private void bindPrimaryTable(EntitySource entitySource, EntityBinding entityBinding) {
 		final TableSource tableSource = entitySource.getPrimaryTable();
 		final String schemaName = StringHelper.isEmpty( tableSource.getExplicitSchemaName() )
 				? currentBindingContext.getMappingDefaults().getSchemaName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 						? StringHelper.quote( tableSource.getExplicitSchemaName() )
 						: tableSource.getExplicitSchemaName();
 		final String catalogName = StringHelper.isEmpty( tableSource.getExplicitCatalogName() )
 				? currentBindingContext.getMappingDefaults().getCatalogName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 						? StringHelper.quote( tableSource.getExplicitCatalogName() )
 						: tableSource.getExplicitCatalogName();
 
 		String tableName = tableSource.getExplicitTableName();
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = currentBindingContext.getNamingStrategy()
 					.classToTableName( entityBinding.getEntity().getClassName() );
 		}
 		else {
 			tableName = currentBindingContext.getNamingStrategy().tableName( tableName );
 		}
 		if ( currentBindingContext.isGloballyQuotedIdentifiers() ) {
 			tableName = StringHelper.quote( tableName );
 		}
 
 		final org.hibernate.metamodel.relational.Table table = currentBindingContext.getMetadataImplementor()
 				.getDatabase()
 				.getSchema( new Schema.Name( schemaName, catalogName ) )
 				.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 
 		entityBinding.setBaseTable( table );
 	}
 
 	private void bindSecondaryTables(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindTableUniqueConstraints(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
-	private org.hibernate.metamodel.relational.Value makeValue(
+	private void bindRelationalValues(
 			RelationalValueSourceContainer relationalValueSourceContainer,
-			SimpleAttributeBinding attributeBinding) {
+			SingularAttributeBinding attributeBinding) {
 
-		// todo : to be completely correct, we need to know which table the value belongs to.
-		// 		There is a note about this somewhere else with ideas on the subject.
-		//		For now, just use the entity's base table.
-		final TableSpecification table = attributeBinding.getEntityBinding().getBaseTable();
+		List<SimpleValueBinding> valueBindings = new ArrayList<SimpleValueBinding>();
 
 		if ( relationalValueSourceContainer.relationalValueSources().size() > 0 ) {
-			List<SimpleValue> values = new ArrayList<SimpleValue>();
 			for ( RelationalValueSource valueSource : relationalValueSourceContainer.relationalValueSources() ) {
+				final TableSpecification table = attributeBinding.getEntityBinding()
+						.getTable( valueSource.getContainingTableName() );
+
 				if ( ColumnSource.class.isInstance( valueSource ) ) {
 					final ColumnSource columnSource = ColumnSource.class.cast( valueSource );
 					final Column column = table.locateOrCreateColumn( columnSource.getName() );
 					column.setNullable( columnSource.isNullable() );
 					column.setDefaultValue( columnSource.getDefaultValue() );
 					column.setSqlType( columnSource.getSqlType() );
 					column.setSize( columnSource.getSize() );
 					column.setDatatype( columnSource.getDatatype() );
 					column.setReadFragment( columnSource.getReadFragment() );
 					column.setWriteFragment( columnSource.getWriteFragment() );
 					column.setUnique( columnSource.isUnique() );
 					column.setCheckCondition( columnSource.getCheckCondition() );
 					column.setComment( columnSource.getComment() );
-					values.add( column );
+					valueBindings.add(
+							new SimpleValueBinding(
+									column,
+									columnSource.isIncludedInInsert(),
+									columnSource.isIncludedInUpdate()
+							)
+					);
 				}
 				else {
-					values.add( table.locateOrCreateDerivedValue( ( (DerivedValueSource) valueSource ).getExpression() ) );
+					valueBindings.add(
+							new SimpleValueBinding(
+									table.locateOrCreateDerivedValue( ( (DerivedValueSource) valueSource ).getExpression() )
+							)
+					);
 				}
 			}
-			if ( values.size() == 1 ) {
-				return values.get( 0 );
-			}
-			Tuple tuple = new Tuple( table, null );
-			for ( SimpleValue value : values ) {
-				tuple.addValue( value );
-			}
-			return tuple;
 		}
 		else {
-			// assume a column named based on the NamingStrategy
 			final String name = metadata.getOptions()
 					.getNamingStrategy()
 					.propertyToColumnName( attributeBinding.getAttribute().getName() );
-			return table.locateOrCreateColumn( name );
-		}
+			final SimpleValueBinding valueBinding = new SimpleValueBinding();
+			valueBindings.add(
+					new SimpleValueBinding(
+							attributeBinding.getEntityBinding().getBaseTable().locateOrCreateColumn( name )
+					)
+			);
+		}
+		attributeBinding.setSimpleValueBindings( valueBindings );
 	}
 
 	private void processFetchProfiles(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : process the entity-local fetch-profile declaration
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java
index b269128e84..f33cc18d54 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java
@@ -1,112 +1,114 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Size;
 
 /**
  * Contract for source information pertaining to a column definition.
  *
  * @author Steve Ebersole
  */
 public interface ColumnSource extends RelationalValueSource {
 	/**
 	 * Obtain the name of the column.
 	 *
 	 * @return The name of the column.  Can be {@code null}, in which case a naming strategy is applied.
 	 */
 	public String getName();
 
 	/**
 	 * A SQL fragment to apply to the column value on read.
 	 *
 	 * @return The SQL read fragment
 	 */
 	public String getReadFragment();
 
 	/**
 	 * A SQL fragment to apply to the column value on write.
 	 *
 	 * @return The SQL write fragment
 	 */
 	public String getWriteFragment();
 
 	/**
 	 * Is this column nullable?
 	 *
 	 * @return {@code true} indicates it is nullable; {@code false} non-nullable.
 	 */
 	public boolean isNullable();
 
 	/**
 	 * Obtain a specified default value for the column
 	 *
 	 * @return THe column default
 	 */
 	public String getDefaultValue();
 
 	/**
 	 * Obtain the free-hand definition of the column's type.
 	 *
 	 * @return The free-hand column type
 	 */
 	public String getSqlType();
 
 	/**
 	 * The deduced (and dialect convertible) type for this column
 	 *
 	 * @return The column's SQL data type.
 	 */
 	public Datatype getDatatype();
 
 	/**
 	 * Obtain the specified column size.
 	 *
 	 * @return The column size.
 	 */
 	public Size getSize();
 
 	/**
 	 * Is this column unique?
 	 *
 	 * @return {@code true} indicates it is unique; {@code false} non-unique.
 	 */
 	public boolean isUnique();
 
 	/**
 	 * Obtain the specified check constraint condition
 	 *
 	 * @return Check constraint condition
 	 */
 	public String getCheckCondition();
 
 	/**
 	 * Obtain the specified SQL comment
 	 *
 	 * @return SQL comment
 	 */
 	public String getComment();
 
+	public boolean isIncludedInInsert();
+	public boolean isIncludedInUpdate();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeElementNature.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeElementNature.java
new file mode 100644
index 0000000000..0e063a3b90
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeElementNature.java
@@ -0,0 +1,37 @@
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
+package org.hibernate.metamodel.source.binder;
+
+/**
+ * Describes the nature of the collection elements as declared by the metadata.
+ *
+ * @author Steve Ebersole
+ */
+public enum PluralAttributeElementNature {
+	BASIC,
+	COMPONENT,
+	ONE_TO_MANY,
+	MANY_TO_MANY,
+	MANY_TO_ANY
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeNature.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeNature.java
new file mode 100644
index 0000000000..1bd35fa9e8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeNature.java
@@ -0,0 +1,37 @@
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
+package org.hibernate.metamodel.source.binder;
+
+/**
+ * Describes the nature of the collection itself as declared by the metadata.
+ *
+ * @author Steve Ebersole
+ */
+public enum PluralAttributeNature {
+	BAG,
+	ID_BAG,
+	SET,
+	LIST,
+	MAP
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/ManyToOneAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeSource.java
similarity index 78%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/ManyToOneAttributeBindingState.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeSource.java
index 7cd420348c..453f7723b1 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/ManyToOneAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeSource.java
@@ -1,37 +1,33 @@
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
-package org.hibernate.metamodel.binding.state;
+package org.hibernate.metamodel.source.binder;
 
 /**
- * @author Gail Badner
+ * @author Steve Ebersole
  */
-public interface ManyToOneAttributeBindingState extends SimpleAttributeBindingState {
-	boolean isUnwrapProxy();
-
-	String getReferencedAttributeName();
-
-	String getReferencedEntityName();
-
-	boolean ignoreNotFound();
+public interface PluralAttributeSource extends AttributeSource {
+	public PluralAttributeNature getPluralAttributeNature();
+	public PluralAttributeElementNature getPluralAttributeElementNature();
+	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java
index 14fbab5d8a..60717f13da 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java
@@ -1,35 +1,41 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
  * Unifying interface for {@link ColumnSource} and {@link DerivedValueSource}.
  *
  * @author Steve Ebersole
  * 
  * @see ColumnSource
  * @see DerivedValueSource
  */
 public interface RelationalValueSource {
+	/**
+	 * Obtain the name of the table that contains this value.
+	 *
+	 * @return
+	 */
+	public String getContainingTableName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSourceContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSourceContainer.java
index 6a0757c8fc..8cf55ac7b8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSourceContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSourceContainer.java
@@ -1,40 +1,45 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import java.util.List;
 
 /**
  * Contract for a container of {@link RelationalValueSource} references.
  *
  * @author Steve Ebersole
  */
 public interface RelationalValueSourceContainer {
+	public boolean areValuesIncludedInInsertByDefault();
+	public boolean areValuesIncludedInUpdateByDefault();
+
+	public boolean areValuesNullableByDefault();
+
 	/**
 	 * Obtain the contained {@link RelationalValueSource} references.
 	 *
 	 * @return The contained {@link RelationalValueSource} references.
 	 */
 	public List<RelationalValueSource> relationalValueSources();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeNature.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeNature.java
index f5c9504e73..b620838605 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeNature.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeNature.java
@@ -1,36 +1,37 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
  * Describes the understood natures of a singular attribute.
  *
  * @author Steve Ebersole
  */
 public enum SingularAttributeNature {
 	BASIC,
+	COMPONENT,
 	MANY_TO_ONE,
 	ONE_TO_ONE,
 	ANY
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
index 0a4bbfd49e..8a916b6fc8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
@@ -1,52 +1,62 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
  * Contract describing source of table information
  *
  * @author Steve Ebersole
  */
 public interface TableSource {
 	/**
 	 * Obtain the supplied schema name
 	 *
 	 * @return The schema name.  If {@code null}, the binder will apply the default.
 	 */
 	public String getExplicitSchemaName();
 
 	/**
 	 * Obtain the supplied catalog name
 	 *
 	 * @return The catalog name.  If {@code null}, the binder will apply the default.
 	 */
 	public String getExplicitCatalogName();
 
 	/**
 	 * Obtain the supplied table name.
 	 *
 	 * @return The table name.
 	 */
 	public String getExplicitTableName();
+
+	/**
+	 * Obtain the logical name of the table.  This value is used to uniquely reference the table when binding
+	 * values to the binding model.
+	 * 
+	 * @return The logical name.  Can be {@code null} in the case of the "primary table".
+	 *
+	 * @see RelationalValueSource#getContainingTableName()
+	 */
+	public String getLogicalName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java
index 91d408037d..ff82abb4fe 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java
@@ -1,47 +1,49 @@
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
 package org.hibernate.metamodel.source.binder;
 
+import org.hibernate.FetchMode;
+
 /**
  * Further contract for sources of {@code *-to-one} style associations.
  *
  * @author Steve Ebersole
  */
 public interface ToOneAttributeSource extends SingularAttributeSource, AssociationAttributeSource {
 	/**
 	 * Obtain the name of the referenced entity.
 	 *
 	 * @return The name of the referenced entity
 	 */
 	public String getReferencedEntityName();
 
 	/**
 	 * Obtain the name of the referenced attribute.  Typically the reference is built based on the identifier
 	 * attribute of the {@link #getReferencedEntityName() referenced entity}, but this value allows using a different
 	 * attribute instead.
 	 *
 	 * @return The name of the referenced attribute; {@code null} indicates the identifier attribute.
 	 */
 	public String getReferencedEntityAttributeName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnAttributeSourceImpl.java
index 66899a144f..b2cf4cc46c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnAttributeSourceImpl.java
@@ -1,94 +1,119 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.source.binder.ColumnSource;
 
 /**
 * @author Steve Ebersole
 */
 class ColumnAttributeSourceImpl implements ColumnSource {
+	private final String tableName;
 	private final String columnName;
+	private boolean includedInInsert;
+	private boolean includedInUpdate;
 
-	ColumnAttributeSourceImpl(String columnName) {
+	ColumnAttributeSourceImpl(
+			String tableName,
+			String columnName,
+			boolean includedInInsert,
+			boolean includedInUpdate) {
+		this.tableName = tableName;
 		this.columnName = columnName;
+		this.includedInInsert = includedInInsert;
+		this.includedInUpdate = includedInUpdate;
+	}
+
+	@Override
+	public boolean isIncludedInInsert() {
+		return includedInInsert;
+	}
+
+	@Override
+	public boolean isIncludedInUpdate() {
+		return includedInUpdate;
+	}
+
+	@Override
+	public String getContainingTableName() {
+		return tableName;
 	}
 
 	@Override
 	public String getName() {
 		return columnName;
 	}
 
 	@Override
 	public boolean isNullable() {
 		return true;
 	}
 
 	@Override
 	public String getDefaultValue() {
 		return null;
 	}
 
 	@Override
 	public String getSqlType() {
 		return null;
 	}
 
 	@Override
 	public Datatype getDatatype() {
 		return null;
 	}
 
 	@Override
 	public Size getSize() {
 		return null;
 	}
 
 	@Override
 	public String getReadFragment() {
 		return null;
 	}
 
 	@Override
 	public String getWriteFragment() {
 		return null;
 	}
 
 	@Override
 	public boolean isUnique() {
 		return false;
 	}
 
 	@Override
 	public String getCheckCondition() {
 		return null;
 	}
 
 	@Override
 	public String getComment() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnSourceImpl.java
index dc7da3d807..e16dc786da 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ColumnSourceImpl.java
@@ -1,100 +1,125 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Size;
+import org.hibernate.metamodel.source.binder.ColumnSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
 
 /**
 * @author Steve Ebersole
 */
-class ColumnSourceImpl implements org.hibernate.metamodel.source.binder.ColumnSource {
+class ColumnSourceImpl implements ColumnSource {
+	private final String tableName;
 	private final XMLColumnElement columnElement;
+	private boolean includedInInsert;
+	private boolean includedInUpdate;
 
-	ColumnSourceImpl(XMLColumnElement columnElement) {
+	ColumnSourceImpl(
+			String tableName,
+			XMLColumnElement columnElement,
+			boolean isIncludedInInsert,
+			boolean isIncludedInUpdate) {
+		this.tableName = tableName;
 		this.columnElement = columnElement;
+		includedInInsert = isIncludedInInsert;
+		includedInUpdate = isIncludedInUpdate;
 	}
 
 	@Override
 	public String getName() {
 		return columnElement.getName();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return ! columnElement.isNotNull();
 	}
 
 	@Override
 	public String getDefaultValue() {
 		return columnElement.getDefault();
 	}
 
 	@Override
 	public String getSqlType() {
 		return columnElement.getSqlType();
 	}
 
 	@Override
 	public Datatype getDatatype() {
 		return null;
 	}
 
 	@Override
 	public Size getSize() {
 		return new Size(
 				Helper.getIntValue( columnElement.getPrecision(), -1 ),
 				Helper.getIntValue( columnElement.getScale(), -1 ),
 				Helper.getLongValue( columnElement.getLength(), -1 ),
 				Size.LobMultiplier.NONE
 		);
 	}
 
 	@Override
 	public String getReadFragment() {
 		return columnElement.getRead();
 	}
 
 	@Override
 	public String getWriteFragment() {
 		return columnElement.getWrite();
 	}
 
 	@Override
 	public boolean isUnique() {
 		return columnElement.isUnique();
 	}
 
 	@Override
 	public String getCheckCondition() {
 		return columnElement.getCheck();
 	}
 
 	@Override
 	public String getComment() {
 		return columnElement.getComment();
 	}
 
+	@Override
+	public boolean isIncludedInInsert() {
+		return includedInInsert;
+	}
+
+	@Override
+	public boolean isIncludedInUpdate() {
+		return includedInUpdate;
+	}
+
+	@Override
+	public String getContainingTableName() {
+		return tableName;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/FormulaImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/FormulaImpl.java
index 2db0051946..f452491c0e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/FormulaImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/FormulaImpl.java
@@ -1,42 +1,49 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import org.hibernate.metamodel.source.binder.DerivedValueSource;
 
 /**
 * @author Steve Ebersole
 */
 class FormulaImpl implements DerivedValueSource {
+	private String tableName;
 	private final String expression;
 
-	FormulaImpl(String expression) {
+	FormulaImpl(String tableName, String expression) {
+		this.tableName = tableName;
 		this.expression = expression;
 	}
 
 	@Override
 	public String getExpression() {
 		return expression;
 	}
+
+	@Override
+	public String getContainingTableName() {
+		return tableName;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
index 5733f821bf..1bbb06ec06 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
@@ -1,327 +1,326 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.ArrayList;
-import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
-import java.util.StringTokenizer;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.CustomSqlElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLMetaElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class Helper {
-	static final Iterable<CascadeStyle> NO_CASCADING = Collections.singleton( CascadeStyle.NONE );
-
 	public static final ExplicitHibernateTypeSource TO_ONE_ATTRIBUTE_TYPE_SOURCE = new ExplicitHibernateTypeSource() {
 		@Override
 		public String getName() {
 			return null;
 		}
 
 		@Override
 		public Map<String, String> getParameters() {
 			return null;
 		}
 	};
 
 	public static InheritanceType interpretInheritanceType(EntityElement entityElement) {
 		if ( XMLSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.SINGLE_TABLE;
 		}
 		else if ( XMLJoinedSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.JOINED;
 		}
 		else if ( XMLUnionSubclassElement.class.isInstance( entityElement ) ) {
 			return InheritanceType.TABLE_PER_CLASS;
 		}
 		else {
 			return InheritanceType.NO_INHERITANCE;
 		}
 	}
 
 	/**
 	 * Given a user-specified description of how to perform custom SQL, build the {@link CustomSQL} representation.
 	 *
 	 * @param customSqlElement User-specified description of how to perform custom SQL
 	 *
 	 * @return The {@link CustomSQL} representation
 	 */
 	public static CustomSQL buildCustomSql(CustomSqlElement customSqlElement) {
 		if ( customSqlElement == null ) {
 			return null;
 		}
 		final ExecuteUpdateResultCheckStyle checkStyle = customSqlElement.getCheck() == null
 				? customSqlElement.isCallable()
 						? ExecuteUpdateResultCheckStyle.NONE
 						: ExecuteUpdateResultCheckStyle.COUNT
 				: ExecuteUpdateResultCheckStyle.fromExternalName( customSqlElement.getCheck().value() );
 		return new CustomSQL( customSqlElement.getValue(), customSqlElement.isCallable(), checkStyle );
 	}
 
 	/**
 	 * Given the user-specified entity mapping, determine the appropriate entity name
 	 *
 	 * @param entityElement The user-specified entity mapping
 	 * @param unqualifiedClassPackage The package to use for unqualified class names
 	 *
 	 * @return The appropriate entity name
 	 */
 	public static String determineEntityName(EntityElement entityElement, String unqualifiedClassPackage) {
 		return entityElement.getEntityName() != null
 				? entityElement.getEntityName()
 				: qualifyIfNeeded( entityElement.getName(), unqualifiedClassPackage );
 	}
 
 	/**
 	 * Qualify a (supposed class) name with the unqualified-class package name if it is not already qualified
 	 *
 	 * @param name The name
 	 * @param unqualifiedClassPackage The unqualified-class package name
 	 *
 	 * @return {@code null} if the incoming name was {@code null}; or the qualified name.
 	 */
 	public static String qualifyIfNeeded(String name, String unqualifiedClassPackage) {
 		if ( name == null ) {
 			return null;
 		}
 		if ( name.indexOf( '.' ) < 0 && unqualifiedClassPackage != null ) {
 			return unqualifiedClassPackage + '.' + name;
 		}
 		return name;
 	}
 
 	public static String getPropertyAccessorName(String access, boolean isEmbedded, String defaultAccess) {
 		return getStringValue( access, isEmbedded ? "embedded" : defaultAccess );
 	}
 
 	public static MetaAttributeContext extractMetaAttributeContext(
 			List<XMLMetaElement> metaElementList,
-			MetaAttributeContext parentContext) {
-		return extractMetaAttributeContext( metaElementList, false, parentContext );
-	}
-
-	public static MetaAttributeContext extractMetaAttributeContext(
-			List<XMLMetaElement> metaElementList,
 			boolean onlyInheritable,
 			MetaAttributeContext parentContext) {
 		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
 
 		for ( XMLMetaElement metaElement : metaElementList ) {
 			if ( onlyInheritable & !metaElement.isInherit() ) {
 				continue;
 			}
 
 			final String name = metaElement.getAttribute();
 			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
 			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				subContext.add( metaAttribute );
 			}
 			metaAttribute.addValue( metaElement.getValue() );
 		}
 
 		return subContext;
 	}
 
 	public static String getStringValue(String value, String defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
 	public static int getIntValue(String value, int defaultValue) {
 		return value == null ? defaultValue : Integer.parseInt( value );
 	}
 
 	public static long getLongValue(String value, long defaultValue) {
 		return value == null ? defaultValue : Long.parseLong( value );
 	}
 
-	public static boolean getBooleanValue(String value, boolean defaultValue) {
-		return value == null ? defaultValue : Boolean.valueOf( value );
-	}
-
 	public static boolean getBooleanValue(Boolean value, boolean defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
-	public static Set<String> getStringValueTokens(String str, String delimiters) {
-		if ( str == null ) {
-			return Collections.emptySet();
-		}
-		else {
-			StringTokenizer tokenizer = new StringTokenizer( str, delimiters );
-			Set<String> tokens = new HashSet<String>();
-			while ( tokenizer.hasMoreTokens() ) {
-				tokens.add( tokenizer.nextToken() );
-			}
-			return tokens;
-		}
-	}
-
-	// todo : remove this once the state objects are cleaned up
-
-	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
-		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
-		try {
-			return classLoaderService.classForName( className );
-		}
-		catch ( ClassLoadingException e ) {
-			throw new MappingException( "Could not find class: " + className );
-		}
-	}
-
 	public static Iterable<CascadeStyle> interpretCascadeStyles(String cascades, LocalBindingContext bindingContext) {
 		final Set<CascadeStyle> cascadeStyles = new HashSet<CascadeStyle>();
 		if ( StringHelper.isEmpty( cascades ) ) {
 			cascades = bindingContext.getMappingDefaults().getCascadeStyle();
 		}
 		for ( String cascade : StringHelper.split( ",", cascades ) ) {
 			cascadeStyles.add( CascadeStyle.getCascadeStyle( cascade ) );
 		}
 		return cascadeStyles;
 	}
 
 	public static Map<String, String> extractParameters(List<XMLParamElement> xmlParamElements) {
 		if ( xmlParamElements == null || xmlParamElements.isEmpty() ) {
 			return null;
 		}
 		final HashMap<String,String> params = new HashMap<String, String>();
 		for ( XMLParamElement paramElement : xmlParamElements ) {
 			params.put( paramElement.getName(), paramElement.getValue() );
 		}
 		return params;
 	}
 
 	public static Iterable<MetaAttributeSource> buildMetaAttributeSources(List<XMLMetaElement> metaElements) {
 		ArrayList<MetaAttributeSource> result = new ArrayList<MetaAttributeSource>();
 		if ( metaElements == null || metaElements.isEmpty() ) {
 			// do nothing
 		}
 		else {
 			for ( final XMLMetaElement metaElement : metaElements ) {
 				result.add(
 						new MetaAttributeSource() {
 							@Override
 							public String getName() {
 								return metaElement.getAttribute();
 							}
 
 							@Override
 							public String getValue() {
 								return metaElement.getValue();
 							}
 
 							@Override
 							public boolean isInheritable() {
 								return metaElement.isInherit();
 							}
 						}
 				);
 			}
 		}
 		return result;
 	}
 
 	public static interface ValueSourcesAdapter {
+		public String getContainingTableName();
+		public boolean isIncludedInInsertByDefault();
+		public boolean isIncludedInUpdateByDefault();
 		public String getColumnAttribute();
 		public String getFormulaAttribute();
 		public List getColumnOrFormulaElements();
 	}
 
 	public static List<RelationalValueSource> buildValueSources(
 			ValueSourcesAdapter valueSourcesAdapter,
 			LocalBindingContext bindingContext) {
 		List<RelationalValueSource> result = new ArrayList<RelationalValueSource>();
 
 		if ( StringHelper.isNotEmpty( valueSourcesAdapter.getColumnAttribute() ) ) {
 			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						bindingContext.getOrigin()
 				);
 			}
 			if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column and formula attributes may not be used together",
 						bindingContext.getOrigin()
 				);
 			}
-			result.add(  new ColumnAttributeSourceImpl( valueSourcesAdapter.getColumnAttribute() ) );
+			result.add(
+					new ColumnAttributeSourceImpl(
+							valueSourcesAdapter.getContainingTableName(),
+							valueSourcesAdapter.getColumnAttribute(),
+							valueSourcesAdapter.isIncludedInInsertByDefault(),
+							valueSourcesAdapter.isIncludedInUpdateByDefault()
+					)
+			);
 		}
 		else if ( StringHelper.isNotEmpty( valueSourcesAdapter.getFormulaAttribute() ) ) {
 			if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 					&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"column/formula attribute may not be used together with <column>/<formula> subelement",
 						bindingContext.getOrigin()
 				);
 			}
 			// column/formula attribute combo checked already
-			result.add( new FormulaImpl( valueSourcesAdapter.getFormulaAttribute() ) );
+			result.add(
+					new FormulaImpl(
+							valueSourcesAdapter.getContainingTableName(),
+							valueSourcesAdapter.getFormulaAttribute()
+					)
+			);
 		}
 		else if ( valueSourcesAdapter.getColumnOrFormulaElements() != null
 				&& ! valueSourcesAdapter.getColumnOrFormulaElements().isEmpty() ) {
 			for ( Object columnOrFormulaElement : valueSourcesAdapter.getColumnOrFormulaElements() ) {
 				if ( XMLColumnElement.class.isInstance( columnOrFormulaElement ) ) {
-					result.add( new ColumnSourceImpl( (XMLColumnElement) columnOrFormulaElement ) );
+					result.add(
+							new ColumnSourceImpl(
+									valueSourcesAdapter.getContainingTableName(),
+									(XMLColumnElement) columnOrFormulaElement,
+									valueSourcesAdapter.isIncludedInInsertByDefault(),
+									valueSourcesAdapter.isIncludedInUpdateByDefault()
+							)
+					);
 				}
 				else {
-					result.add( new FormulaImpl( (String) columnOrFormulaElement ) );
+					result.add(
+							new FormulaImpl(
+									valueSourcesAdapter.getContainingTableName(),
+									(String) columnOrFormulaElement
+							)
+					);
 				}
 			}
 		}
 		return result;
 	}
+
+	// todo : remove this once the state objects are cleaned up
+
+	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
+		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
+		try {
+			return classLoaderService.classForName( className );
+		}
+		catch ( ClassLoadingException e ) {
+			throw new MappingException( "Could not find class: " + className );
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
index 10066304cd..68236ef0c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToOneAttributeSourceImpl.java
@@ -1,153 +1,192 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 
+import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.ToOneAttributeSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
 
 /**
  * Implementation for {@code <many-to-one/> mappings}
  *
  * @author Steve Ebersole
  */
 class ManyToOneAttributeSourceImpl implements ToOneAttributeSource {
 	private final XMLManyToOneElement manyToOneElement;
 	private final LocalBindingContext bindingContext;
 	private final List<RelationalValueSource> valueSources;
 
 	ManyToOneAttributeSourceImpl(final XMLManyToOneElement manyToOneElement, LocalBindingContext bindingContext) {
 		this.manyToOneElement = manyToOneElement;
 		this.bindingContext = bindingContext;
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return manyToOneElement.getColumn();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return manyToOneElement.getFormula();
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return manyToOneElement.getColumnOrFormula();
 					}
+
+					@Override
+					public String getContainingTableName() {
+						// todo : need to implement this...
+						return null;
+					}
+
+					@Override
+					public boolean isIncludedInInsertByDefault() {
+						return manyToOneElement.isInsert();
+					}
+
+					@Override
+					public boolean isIncludedInUpdateByDefault() {
+						return manyToOneElement.isUpdate();
+					}
 				},
 				bindingContext
 		);
 	}
 
 	@Override
 	public String getName() {
 			return manyToOneElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return Helper.TO_ONE_ATTRIBUTE_TYPE_SOURCE;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return manyToOneElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return manyToOneElement.isInsert();
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return manyToOneElement.isUpdate();
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return PropertyGeneration.NEVER;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return manyToOneElement.isOptimisticLock();
 	}
 
 	@Override
 	public Iterable<CascadeStyle> getCascadeStyles() {
 		return Helper.interpretCascadeStyles( manyToOneElement.getCascade(), bindingContext );
 	}
 
 	@Override
+	public FetchMode getFetchMode() {
+		return manyToOneElement.getFetch() == null
+				? FetchMode.DEFAULT
+				: FetchMode.valueOf( manyToOneElement.getFetch().value() );
+	}
+
+	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.MANY_TO_ONE;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
+	public boolean areValuesIncludedInInsertByDefault() {
+		return manyToOneElement.isInsert();
+	}
+
+	@Override
+	public boolean areValuesIncludedInUpdateByDefault() {
+		return manyToOneElement.isUpdate();
+	}
+
+	@Override
+	public boolean areValuesNullableByDefault() {
+		return ! Helper.getBooleanValue( manyToOneElement.isNotNull(), false );
+	}
+
+	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( manyToOneElement.getMeta() );
 	}
 
 	@Override
 	public String getReferencedEntityName() {
 		return manyToOneElement.getClazz() != null
 				? manyToOneElement.getClazz()
 				: manyToOneElement.getEntityName();
 	}
 
 	@Override
 	public String getReferencedEntityAttributeName() {
 		return manyToOneElement.getPropertyRef();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PropertyAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PropertyAttributeSourceImpl.java
index 2c046447cb..7ddfc39702 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PropertyAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PropertyAttributeSourceImpl.java
@@ -1,156 +1,186 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 import java.util.Map;
 
-import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
 
 /**
  * Implementation for {@code <property/>} mappings
  *
  * @author Steve Ebersole
  */
 class PropertyAttributeSourceImpl implements SingularAttributeSource {
 	private final XMLPropertyElement propertyElement;
 	private final ExplicitHibernateTypeSource typeSource;
 	private final List<RelationalValueSource> valueSources;
 
 	PropertyAttributeSourceImpl(final XMLPropertyElement propertyElement, LocalBindingContext bindingContext) {
 		this.propertyElement = propertyElement;
 		this.typeSource = new ExplicitHibernateTypeSource() {
 			private final String name = propertyElement.getTypeAttribute() != null
 					? propertyElement.getTypeAttribute()
 					: propertyElement.getType() != null
 							? propertyElement.getType().getName()
 							: null;
 			private final Map<String, String> parameters = ( propertyElement.getType() != null )
 					? Helper.extractParameters( propertyElement.getType().getParam() )
 					: null;
 
 			@Override
 			public String getName() {
 				return name;
 			}
 
 			@Override
 			public Map<String, String> getParameters() {
 				return parameters;
 			}
 		};
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return propertyElement.getColumn();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return propertyElement.getFormula();
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return propertyElement.getColumnOrFormula();
 					}
+
+					@Override
+					public String getContainingTableName() {
+						// todo : need to implement this...
+						return null;
+					}
+
+					@Override
+					public boolean isIncludedInInsertByDefault() {
+						return propertyElement.isInsert();
+					}
+
+					@Override
+					public boolean isIncludedInUpdateByDefault() {
+						return propertyElement.isUpdate();
+					}
 				},
 				bindingContext
 		);
 	}
 
 	@Override
 	public String getName() {
 		return propertyElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return typeSource;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return propertyElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return Helper.getBooleanValue( propertyElement.isInsert(), true );
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return Helper.getBooleanValue( propertyElement.isUpdate(), true );
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return PropertyGeneration.parse( propertyElement.getGenerated() );
 	}
 
 	@Override
 	public boolean isLazy() {
 		return Helper.getBooleanValue( propertyElement.isLazy(), false );
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return Helper.getBooleanValue( propertyElement.isOptimisticLock(), true );
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.BASIC;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
+	public boolean areValuesIncludedInInsertByDefault() {
+		return Helper.getBooleanValue( propertyElement.isInsert(), true );
+	}
+
+	@Override
+	public boolean areValuesIncludedInUpdateByDefault() {
+		return Helper.getBooleanValue( propertyElement.isUpdate(), true );
+	}
+
+	@Override
+	public boolean areValuesNullableByDefault() {
+		return ! Helper.getBooleanValue( propertyElement.isNotNull(), false );
+	}
+
+	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( propertyElement.getMeta() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
index 0ad2ac575f..2f391a7a2e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntitySourceImpl.java
@@ -1,178 +1,184 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import org.hibernate.EntityMode;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.binder.IdentifierSource;
 import org.hibernate.metamodel.source.binder.RootEntitySource;
 import org.hibernate.metamodel.source.binder.SimpleIdentifierSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLCacheElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
  * @author Steve Ebersole
  */
 public class RootEntitySourceImpl extends AbstractEntitySourceImpl implements RootEntitySource {
 	protected RootEntitySourceImpl(MappingDocument sourceMappingDocument, XMLHibernateMapping.XMLClass entityElement) {
 		super( sourceMappingDocument, entityElement );
 	}
 
 	@Override
 	protected XMLHibernateMapping.XMLClass entityElement() {
 		return (XMLHibernateMapping.XMLClass) super.entityElement();
 	}
 
 	@Override
 	public IdentifierSource getIdentifierSource() {
 		if ( entityElement().getId() != null ) {
 			return new SimpleIdentifierSource() {
 				@Override
 				public SingularAttributeSource getIdentifierAttributeSource() {
 					return new SingularIdentifierAttributeSourceImpl( entityElement().getId(), sourceMappingDocument().getMappingLocalBindingContext() );
 				}
 
 				@Override
 				public IdGenerator getIdentifierGeneratorDescriptor() {
 					if ( entityElement().getId().getGenerator() != null ) {
 						final String generatorName = entityElement().getId().getGenerator().getClazz();
 						IdGenerator idGenerator = sourceMappingDocument().getMappingLocalBindingContext()
 								.getMetadataImplementor()
 								.getIdGenerator( generatorName );
 						if ( idGenerator == null ) {
 							idGenerator = new IdGenerator(
 									getEntityName() + generatorName,
 									generatorName,
 									Helper.extractParameters( entityElement().getId().getGenerator().getParam() )
 							);
 						}
 						return idGenerator;
 					}
 					return null;
 				}
 
 				@Override
 				public Nature getNature() {
 					return Nature.SIMPLE;
 				}
 			};
 		}
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public SingularAttributeSource getVersioningAttributeSource() {
 		if ( entityElement().getVersion() != null ) {
 			return new VersionAttributeSourceImpl( entityElement().getVersion(), sourceMappingDocument().getMappingLocalBindingContext() );
 		}
 		else if ( entityElement().getTimestamp() != null ) {
 			return new TimestampAttributeSourceImpl( entityElement().getTimestamp(), sourceMappingDocument().getMappingLocalBindingContext() );
 		}
 		return null;
 	}
 
 	@Override
 	public SingularAttributeSource getDiscriminatorAttributeSource() {
 		// todo : implement
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return determineEntityMode();
 	}
 
 	@Override
 	public boolean isMutable() {
 		return entityElement().isMutable();
 	}
 
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return "explicit".equals( entityElement().getPolymorphism() );
 	}
 
 	@Override
 	public String getWhere() {
 		return entityElement().getWhere();
 	}
 
 	@Override
 	public String getRowId() {
 		return entityElement().getRowid();
 	}
 
 	@Override
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		final String optimisticLockModeString = Helper.getStringValue( entityElement().getOptimisticLock(), "version" );
 		try {
 			return OptimisticLockStyle.valueOf( optimisticLockModeString.toUpperCase() );
 		}
 		catch (Exception e) {
 			throw new MappingException(
 					"Unknown optimistic-lock value : " + optimisticLockModeString,
 					sourceMappingDocument().getOrigin()
 			);
 		}
 	}
 
 	@Override
 	public Caching getCaching() {
 		final XMLCacheElement cache = entityElement().getCache();
 		if ( cache == null ) {
 			return null;
 		}
 		final String region = cache.getRegion() != null ? cache.getRegion() : getEntityName();
 		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
 		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
 		return new Caching( region, accessType, cacheLazyProps );
 	}
 
 	@Override
 	public TableSource getPrimaryTable() {
 		return new TableSource() {
 			@Override
 			public String getExplicitSchemaName() {
 				return entityElement().getSchema();
 			}
 
 			@Override
 			public String getExplicitCatalogName() {
 				return entityElement().getCatalog();
 			}
 
 			@Override
 			public String getExplicitTableName() {
 				return entityElement().getTable();
 			}
+
+			@Override
+			public String getLogicalName() {
+				// logical name for the primary table is null
+				return null;
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SingularIdentifierAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SingularIdentifierAttributeSourceImpl.java
index 46105f931c..35354ec650 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SingularIdentifierAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SingularIdentifierAttributeSourceImpl.java
@@ -1,160 +1,190 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 import java.util.Map;
 
-import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
  * Implementation for {@code <id/>} mappings
  *
  * @author Steve Ebersole
  */
 class SingularIdentifierAttributeSourceImpl implements SingularAttributeSource {
 	private final XMLHibernateMapping.XMLClass.XMLId idElement;
 	private final ExplicitHibernateTypeSource typeSource;
 	private final List<RelationalValueSource> valueSources;
 
 	public SingularIdentifierAttributeSourceImpl(
 			final XMLHibernateMapping.XMLClass.XMLId idElement,
 			LocalBindingContext bindingContext) {
 		this.idElement = idElement;
 		this.typeSource = new ExplicitHibernateTypeSource() {
 			private final String name = idElement.getTypeAttribute() != null
 					? idElement.getTypeAttribute()
 					: idElement.getType() != null
 							? idElement.getType().getName()
 							: null;
 			private final Map<String, String> parameters = ( idElement.getType() != null )
 					? Helper.extractParameters( idElement.getType().getParam() )
 					: null;
 
 			@Override
 			public String getName() {
 				return name;
 			}
 
 			@Override
 			public Map<String, String> getParameters() {
 				return parameters;
 			}
 		};
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return idElement.getColumnAttribute();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return null;
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return idElement.getColumn();
 					}
+
+					@Override
+					public String getContainingTableName() {
+						// by definition, the identifier should be bound to the primary table of the root entity
+						return null;
+					}
+
+					@Override
+					public boolean isIncludedInInsertByDefault() {
+						return true;
+					}
+
+					@Override
+					public boolean isIncludedInUpdateByDefault() {
+						return false;
+					}
 				},
 				bindingContext
 		);
 	}
 
 	@Override
 	public String getName() {
 		return idElement.getName() == null
 				? "id"
 				: idElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return typeSource;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return idElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return true;
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return false;
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return PropertyGeneration.INSERT;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.BASIC;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
+	public boolean areValuesIncludedInInsertByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesIncludedInUpdateByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesNullableByDefault() {
+		return false;
+	}
+
+	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( idElement.getMeta() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
index 4472d793bd..e6206525d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SubclassEntitySourceImpl.java
@@ -1,80 +1,92 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLUnionSubclassElement;
 
 /**
  * @author Steve Ebersole
  */
 public class SubclassEntitySourceImpl extends AbstractEntitySourceImpl implements SubclassEntitySource {
 	protected SubclassEntitySourceImpl(MappingDocument sourceMappingDocument, EntityElement entityElement) {
 		super( sourceMappingDocument, entityElement );
 	}
 
 	@Override
 	public TableSource getPrimaryTable() {
 		if ( XMLJoinedSubclassElement.class.isInstance( entityElement() ) ) {
 			return new TableSource() {
 				@Override
 				public String getExplicitSchemaName() {
 					return ( (XMLJoinedSubclassElement) entityElement() ).getSchema();
 				}
 
 				@Override
 				public String getExplicitCatalogName() {
 					return ( (XMLJoinedSubclassElement) entityElement() ).getCatalog();
 				}
 
 				@Override
 				public String getExplicitTableName() {
 					return ( (XMLJoinedSubclassElement) entityElement() ).getTable();
 				}
+
+				@Override
+				public String getLogicalName() {
+					// logical name for the primary table is null
+					return null;
+				}
 			};
 		}
 		else if ( XMLUnionSubclassElement.class.isInstance( entityElement() ) ) {
 			return new TableSource() {
 				@Override
 				public String getExplicitSchemaName() {
 					return ( (XMLUnionSubclassElement) entityElement() ).getSchema();
 				}
 
 				@Override
 				public String getExplicitCatalogName() {
 					return ( (XMLUnionSubclassElement) entityElement() ).getCatalog();
 				}
 
 				@Override
 				public String getExplicitTableName() {
 					return ( (XMLUnionSubclassElement) entityElement() ).getTable();
 				}
+
+				@Override
+				public String getLogicalName() {
+					// logical name for the primary table is null
+					return null;
+				}
 			};
 		}
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
index abde9840a9..1717525194 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
@@ -1,171 +1,201 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 import java.util.Map;
 
-import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.internal.util.Value;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
  * Implementation for {@code <timestamp/>} mappings
  *
  * @author Steve Ebersole
  */
 class TimestampAttributeSourceImpl implements SingularAttributeSource {
 	private final XMLHibernateMapping.XMLClass.XMLTimestamp timestampElement;
 	private final LocalBindingContext bindingContext;
 	private final List<RelationalValueSource> valueSources;
 
 	TimestampAttributeSourceImpl(
 			final XMLHibernateMapping.XMLClass.XMLTimestamp timestampElement,
 			LocalBindingContext bindingContext) {
 		this.timestampElement = timestampElement;
 		this.bindingContext = bindingContext;
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return timestampElement.getColumn();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return null;
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return null;
 					}
+
+					@Override
+					public String getContainingTableName() {
+						// by definition the version should come from the primary table of the root entity.
+						return null;
+					}
+
+					@Override
+					public boolean isIncludedInInsertByDefault() {
+						return true;
+					}
+
+					@Override
+					public boolean isIncludedInUpdateByDefault() {
+						return true;
+					}
 				},
 				bindingContext
 		);
 	}
 
 	private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
 		@Override
 		public String getName() {
 			return "db".equals( timestampElement.getSource() ) ? "dbtimestamp" : "timestamp";
 		}
 
 		@Override
 		public Map<String, String> getParameters() {
 			return null;
 		}
 	};
 
 	@Override
 	public String getName() {
 		return timestampElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return typeSource;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return timestampElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return true;
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return true;
 	}
 
 	private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
 			new Value.DeferredInitializer<PropertyGeneration>() {
 				@Override
 				public PropertyGeneration initialize() {
 					final PropertyGeneration propertyGeneration = timestampElement.getGenerated() == null
 							? PropertyGeneration.NEVER
 							: PropertyGeneration.parse( timestampElement.getGenerated().value() );
 					if ( propertyGeneration == PropertyGeneration.INSERT ) {
 						throw new MappingException(
 								"'generated' attribute cannot be 'insert' for versioning property",
 								bindingContext.getOrigin()
 						);
 					}
 					return propertyGeneration;
 				}
 			}
 	);
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return propertyGenerationValue.getValue();
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.BASIC;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
+	public boolean areValuesIncludedInInsertByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesIncludedInUpdateByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesNullableByDefault() {
+		return true;
+	}
+
+	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( timestampElement.getMeta() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
index e65fea339f..c7735b9d44 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
@@ -1,171 +1,201 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 import java.util.Map;
 
-import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.internal.util.Value;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
  * Implementation for {@code <version/>} mappings
  *
  * @author Steve Ebersole
  */
 class VersionAttributeSourceImpl implements SingularAttributeSource {
 	private final XMLHibernateMapping.XMLClass.XMLVersion versionElement;
 	private final LocalBindingContext bindingContext;
 	private final List<RelationalValueSource> valueSources;
 
 	VersionAttributeSourceImpl(
 			final XMLHibernateMapping.XMLClass.XMLVersion versionElement,
 			LocalBindingContext bindingContext) {
 		this.versionElement = versionElement;
 		this.bindingContext = bindingContext;
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return versionElement.getColumnAttribute();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return null;
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return versionElement.getColumn();
 					}
+
+					@Override
+					public String getContainingTableName() {
+						// by definition the version should come from the primary table of the root entity.
+						return null;
+					}
+
+					@Override
+					public boolean isIncludedInInsertByDefault() {
+						return versionElement.isInsert();
+					}
+
+					@Override
+					public boolean isIncludedInUpdateByDefault() {
+						return true;
+					}
 				},
 				bindingContext
 		);
 	}
 
 	private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
 		@Override
 		public String getName() {
 			return versionElement.getType() == null ? "integer" : versionElement.getType();
 		}
 
 		@Override
 		public Map<String, String> getParameters() {
 			return null;
 		}
 	};
 
 	@Override
 	public String getName() {
 		return versionElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return typeSource;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return versionElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return Helper.getBooleanValue( versionElement.isInsert(), true );
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return true;
 	}
 
 	private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
 			new Value.DeferredInitializer<PropertyGeneration>() {
 				@Override
 				public PropertyGeneration initialize() {
 					final PropertyGeneration propertyGeneration = versionElement.getGenerated() == null
 							? PropertyGeneration.NEVER
 							: PropertyGeneration.parse( versionElement.getGenerated().value() );
 					if ( propertyGeneration == PropertyGeneration.INSERT ) {
 						throw new MappingException(
 								"'generated' attribute cannot be 'insert' for versioning property",
 								bindingContext.getOrigin()
 						);
 					}
 					return propertyGeneration;
 				}
 			}
 	);
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return propertyGenerationValue.getValue();
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.BASIC;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
+	public boolean areValuesIncludedInInsertByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesIncludedInUpdateByDefault() {
+		return true;
+	}
+
+	@Override
+	public boolean areValuesNullableByDefault() {
+		return true;
+	}
+
+	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( versionElement.getMeta() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
index 614e778f51..6508052a44 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/AttributeTypeResolver.java
@@ -1,153 +1,156 @@
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
 
 import java.util.Properties;
 
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
+import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Value;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.type.Type;
 
 /**
  * This is a TEMPORARY way to initialize HibernateTypeDescriptor.explicitType.
  * This class will be removed when types are resolved properly.
  *
  * @author Gail Badner
  */
 class AttributeTypeResolver {
 
 	private final MetadataImplementor metadata;
 
 	AttributeTypeResolver(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	void resolve() {
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindings() ) {
 				resolveTypeInformation( attributeBinding );
 			}
 		}
 	}
 
 	private void resolveTypeInformation(AttributeBinding attributeBinding) {
 		// perform any needed type resolutions
 
 		final HibernateTypeDescriptor hibernateTypeDescriptor = attributeBinding.getHibernateTypeDescriptor();
 
 		Type resolvedHibernateType = hibernateTypeDescriptor.getResolvedTypeMapping();
 		if ( resolvedHibernateType == null ) {
 			resolvedHibernateType = determineHibernateType( attributeBinding );
 			if ( resolvedHibernateType != null ) {
 				hibernateTypeDescriptor.setResolvedTypeMapping( resolvedHibernateType );
 			}
 		}
 
 		if ( resolvedHibernateType != null ) {
 			pushHibernateTypeInformationDownIfNeeded( attributeBinding, resolvedHibernateType );
 		}
 	}
 
 	private Type determineHibernateType(AttributeBinding attributeBinding) {
 		String typeName = null;
 		Properties typeParameters = new Properties();
 
 		// we can determine the Hibernate Type if either:
 		// 		1) the user explicitly named a Type
 		// 		2) we know the java type of the attribute
 
 		if ( attributeBinding.getHibernateTypeDescriptor().getExplicitTypeName() != null ) {
 			typeName = attributeBinding.getHibernateTypeDescriptor().getExplicitTypeName();
 			if ( attributeBinding.getHibernateTypeDescriptor().getTypeParameters() != null ) {
 				typeParameters.putAll( attributeBinding.getHibernateTypeDescriptor().getTypeParameters() );
 			}
 		}
 		else {
 			typeName = attributeBinding.getHibernateTypeDescriptor().getJavaTypeName();
 			if ( typeName == null ) {
 				if ( attributeBinding.getAttribute().isSingular() ) {
 					SingularAttribute singularAttribute = (SingularAttribute) attributeBinding.getAttribute();
 					if ( singularAttribute.getSingularAttributeType() != null ) {
 						typeName = singularAttribute.getSingularAttributeType().getClassName();
 					}
 				}
 			}
 		}
 
 		if ( typeName != null ) {
 			try {
 				return metadata.getTypeResolver().heuristicType( typeName, typeParameters );
 			}
 			catch (Exception ignore) {
 			}
 		}
 
 		return null;
 	}
 
 	private void pushHibernateTypeInformationDownIfNeeded(AttributeBinding attributeBinding, Type resolvedHibernateType) {
 		final HibernateTypeDescriptor hibernateTypeDescriptor = attributeBinding.getHibernateTypeDescriptor();
 
 		// java type information ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		if ( hibernateTypeDescriptor.getJavaTypeName() == null ) {
 			hibernateTypeDescriptor.setJavaTypeName( resolvedHibernateType.getReturnedClass().getName() );
 		}
 
 		if ( SingularAttribute.class.isInstance( attributeBinding.getAttribute() ) ) {
 			final SingularAttribute singularAttribute = (SingularAttribute) attributeBinding.getAttribute();
 			if ( ! singularAttribute.isTypeResolved() ) {
 				if ( hibernateTypeDescriptor.getJavaTypeName() != null ) {
 					singularAttribute.resolveType( metadata.makeJavaType( hibernateTypeDescriptor.getJavaTypeName() ) );
 				}
 			}
 		}
 
 
 		// sql type information ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		// todo : this can be made a lot smarter, but for now this will suffice.  currently we only handle single value bindings
 
-		Value value = attributeBinding.getValue();
-		if ( SimpleValue.class.isInstance( value ) ) {
-			SimpleValue simpleValue = (SimpleValue) value;
-			if ( simpleValue.getDatatype() == null ) {
-				simpleValue.setDatatype(
-						new Datatype(
-								resolvedHibernateType.sqlTypes( metadata )[0],
-								resolvedHibernateType.getName(),
-								resolvedHibernateType.getReturnedClass()
-						)
-				);
+		if ( SingularAttribute.class.isInstance( attributeBinding.getAttribute() ) ) {
+			final Value value = SingularAttributeBinding.class.cast( attributeBinding ).getValue();
+			if ( SimpleValue.class.isInstance( value ) ) {
+				SimpleValue simpleValue = (SimpleValue) value;
+				if ( simpleValue.getDatatype() == null ) {
+					simpleValue.setDatatype(
+							new Datatype(
+									resolvedHibernateType.sqlTypes( metadata )[0],
+									resolvedHibernateType.getName(),
+									resolvedHibernateType.getReturnedClass()
+							)
+					);
+				}
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index f95a0965fc..c35ec2fb4e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,603 +1,603 @@
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
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
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
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
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
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
 
 	private final DefaultIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private final Database database;
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 
-	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
+	private Map<String, AbstractPluralAttributeBinding> collectionBindingMap = new HashMap<String, AbstractPluralAttributeBinding>();
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
 		Dialect dialect = metadataSources.getServiceRegistry().getService( JdbcServices.class ).getDialect();
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory( dialect );
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
 		new AssociationResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 		// IdentifierGeneratorResolver.resolve() must execute after AttributeTypeResolver.resolve()
 		new IdentifierGeneratorResolver( this ).resolve();
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
 
-	public PluralAttributeBinding getCollection(String collectionRole) {
+	public AbstractPluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	@Override
-	public Iterable<PluralAttributeBinding> getCollectionBindings() {
+	public Iterable<AbstractPluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
-	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
+	public void addCollection(AbstractPluralAttributeBinding pluralAttributeBinding) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 9c775cca88..51ac39fc7b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1918 +1,1934 @@
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
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
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
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.SimpleValueBinding;
+import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
+import org.hibernate.metamodel.relational.Value;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 				   SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractEntityPersister.class.getName());
 
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
 
 	protected boolean[][] getPropertyColumnUpdateable() {
 		return propertyColumnUpdateable;
 	}
 
 	protected boolean[][] getPropertyColumnInsertable() {
 		return propertyColumnInsertable;
 	}
 
 	protected boolean[] getPropertySelectable() {
 		return propertySelectable;
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
 
 		final boolean lazyAvailable = isInstrumented();
 
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
 				lazyNumbers.add( i );
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
 
 
 	public AbstractEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 		// TODO: Implement! Initializing final fields to make compiler happy
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.isLazyPropertiesCacheable =
 				entityBinding.getCaching() == null ?
 						false :
 						entityBinding.getCaching().isCacheLazyProperties();
 		this.cacheEntryStructure =
 				factory.getSettings().isStructuredCacheEntriesEnabled() ?
 						new StructuredCacheEntry(this) :
 						new UnstructuredCacheEntry();
 		this.entityMetamodel = new EntityMetamodel( entityBinding, factory );
 		int batch = entityBinding.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = entityBinding.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
-		identifierColumnSpan = entityBinding.getEntityIdentifier().getValueBinding().getValuesSpan();
+		identifierColumnSpan = entityBinding.getEntityIdentifier().getValueBinding().getSimpleValueSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = entityBinding.getRowId();
 
 		loaderName = entityBinding.getCustomLoaderName();
 
 		int i = 0;
 		for ( org.hibernate.metamodel.relational.Column col : entityBinding.getBaseTable().getPrimaryKey().getColumns() ) {
 			rootTableKeyColumnNames[i] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			if ( col.getReadFragment() == null ) {
 				rootTableKeyColumnReaders[i] = rootTableKeyColumnNames[i];
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromColumn( col, factory );
 			}
 			else {
 				rootTableKeyColumnReaders[i] = col.getReadFragment();
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromString( col.getReadFragment(), factory );
 			}
 			// TODO: Fix when HHH-6337 is fixed; for now assume entityBinding is the root
 			// identifierAliases[i] = col.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getBaseTable() );
 			identifierAliases[i] = col.getAlias( factory.getDialect() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( entityBinding.isVersioned() ) {
-			// Use AttributeBinding.getValues() due to HHH-6380
-			Iterator<SimpleValue> valueIterator = entityBinding.getVersioningValueBinding().getValues().iterator();
-			SimpleValue versionValue = valueIterator.next();
-			if ( ! ( versionValue instanceof org.hibernate.metamodel.relational.Column ) || valueIterator.hasNext() ) {
-				throw new MappingException( "Version must be a single column value." );
+			final Value versioningValue = entityBinding.getVersioningValueBinding().getValue();
+			if ( ! org.hibernate.metamodel.relational.Column.class.isInstance( versioningValue ) ) {
+				throw new AssertionFailure( "Bad versioning attribute binding : " + versioningValue );
 			}
-			org.hibernate.metamodel.relational.Column versionColumn =
-					( org.hibernate.metamodel.relational.Column ) versionValue;
+			org.hibernate.metamodel.relational.Column versionColumn = org.hibernate.metamodel.relational.Column.class.cast( versioningValue );
 			versionColumnName = versionColumn.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( entityBinding.getWhereFilter() ) ? "( " + entityBinding.getWhereFilter() + ") " : null;
 		sqlWhereStringTemplate = getTemplateFromString( sqlWhereString, factory );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
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
 
 		i = 0;
 		boolean foundFormula = false;
-		for ( AttributeBinding prop : entityBinding.getAttributeBindingClosure() ) {
-			if ( prop == entityBinding.getEntityIdentifier().getValueBinding() ) {
+		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
+			if ( attributeBinding == entityBinding.getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
-			thisClassProperties.add( prop );
+			if ( ! attributeBinding.getAttribute().isSingular() ) {
+				// collections handled separately
+				continue;
+			}
+
+			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
+
+			thisClassProperties.add( singularAttributeBinding );
 
-			int span = prop.getValuesSpan();
+			propertySubclassNames[i] = singularAttributeBinding.getEntityBinding().getEntity().getName();
+
+			int span = singularAttributeBinding.getSimpleValueSpan();
 			propertyColumnSpans[i] = span;
-			propertySubclassNames[i] = prop.getEntityBinding().getEntity().getName();
+
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
+			boolean[] propertyColumnInsertability = new boolean[span];
+			boolean[] propertyColumnUpdatability = new boolean[span];
+
 			int k = 0;
-			for ( SimpleValue thing : prop.getValues() ) {
-				colAliases[k] = thing.getAlias( factory.getDialect() );
-				if ( thing instanceof DerivedValue ) {
+
+			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
+				colAliases[k] = valueBinding.getSimpleValue().getAlias( factory.getDialect() );
+				if ( valueBinding.isDerived() ) {
 					foundFormula = true;
-					formulaTemplates[ k ] = getTemplateFromString( ( (DerivedValue) thing ).getExpression(), factory );
+					formulaTemplates[ k ] = getTemplateFromString( ( (DerivedValue) valueBinding.getSimpleValue() ).getExpression(), factory );
 				}
 				else {
-					org.hibernate.metamodel.relational.Column col = ( org.hibernate.metamodel.relational.Column ) thing;
+					org.hibernate.metamodel.relational.Column col = ( org.hibernate.metamodel.relational.Column ) valueBinding.getSimpleValue();
 					colNames[k] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colReaderTemplates[k] = getTemplateFromColumn( col, factory );
 					colWriters[k] = col.getWriteFragment() == null ? "?" : col.getWriteFragment();
 				}
+				propertyColumnInsertability[k] = valueBinding.isIncludeInInsert();
+				propertyColumnUpdatability[k] = valueBinding.isIncludeInUpdate();
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
-			if ( lazyAvailable && prop.isLazy() ) {
-				lazyProperties.add( prop.getAttribute().getName() );
-				lazyNames.add( prop.getAttribute().getName() );
+			propertyColumnUpdateable[i] = propertyColumnInsertability;
+			propertyColumnInsertable[i] = propertyColumnUpdatability;
+
+			if ( lazyAvailable && singularAttributeBinding.isLazy() ) {
+				lazyProperties.add( singularAttributeBinding.getAttribute().getName() );
+				lazyNames.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNumbers.add( i );
-				lazyTypes.add( prop.getHibernateTypeDescriptor().getResolvedTypeMapping());
+				lazyTypes.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping());
 				lazyColAliases.add( colAliases );
 			}
 
-			propertyColumnUpdateable[i] = prop.getColumnUpdateability();
-			propertyColumnInsertable[i] = prop.getColumnInsertability();
 
 			// TODO: fix this when backrefs are working
-			//propertySelectable[i] = prop.isBackRef();
+			//propertySelectable[i] = singularAttributeBinding.isBackRef();
 			propertySelectable[i] = true;
 
-			propertyUniqueness[i] = prop.isAlternateUniqueKey();
+			propertyUniqueness[i] = singularAttributeBinding.isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		List<String> columns = new ArrayList<String>();
 		List<Boolean> columnsLazy = new ArrayList<Boolean>();
 		List<String> columnReaderTemplates = new ArrayList<String>();
 		List<String> aliases = new ArrayList<String>();
 		List<String> formulas = new ArrayList<String>();
 		List<String> formulaAliases = new ArrayList<String>();
 		List<String> formulaTemplates = new ArrayList<String>();
 		List<Boolean> formulasLazy = new ArrayList<Boolean>();
 		List<Type> types = new ArrayList<Type>();
 		List<String> names = new ArrayList<String>();
 		List<String> classes = new ArrayList<String>();
 		List<String[]> templates = new ArrayList<String[]>();
 		List<String[]> propColumns = new ArrayList<String[]>();
 		List<String[]> propColumnReaders = new ArrayList<String[]>();
 		List<String[]> propColumnReaderTemplates = new ArrayList<String[]>();
 		List<FetchMode> joinedFetchesList = new ArrayList<FetchMode>();
 		List<CascadeStyle> cascades = new ArrayList<CascadeStyle>();
 		List<Boolean> definedBySubclass = new ArrayList<Boolean>();
 		List<int[]> propColumnNumbers = new ArrayList<int[]>();
 		List<int[]> propFormulaNumbers = new ArrayList<int[]>();
 		List<Boolean> columnSelectables = new ArrayList<Boolean>();
 		List<Boolean> propNullables = new ArrayList<Boolean>();
 
 		// TODO: fix this when EntityBinding.getSubclassAttributeBindingClosure() is working
 		// for ( AttributeBinding prop : entityBinding.getSubclassAttributeBindingClosure() ) {
 		for ( AttributeBinding prop : entityBinding.getAttributeBindingClosure() ) {
 			if ( prop == entityBinding.getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			names.add( prop.getAttribute().getName() );
 			classes.add( prop.getEntityBinding().getEntity().getName() );
 			boolean isDefinedBySubclass = ! thisClassProperties.contains( prop );
 			definedBySubclass.add( isDefinedBySubclass );
 			propNullables.add( prop.isNullable() || isDefinedBySubclass ); //TODO: is this completely correct?
 			types.add( prop.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 			String[] cols = new String[ prop.getValuesSpan() ];
 			String[] readers = new String[ prop.getValuesSpan() ];
 			String[] readerTemplates = new String[ prop.getValuesSpan() ];
 			String[] forms = new String[ prop.getValuesSpan() ];
 			int[] colnos = new int[ prop.getValuesSpan() ];
 			int[] formnos = new int[ prop.getValuesSpan() ];
 			int l = 0;
 			Boolean lazy = prop.isLazy() && lazyAvailable;
 			for ( SimpleValue thing : prop.getValues() ) {
 				if ( DerivedValue.class.isInstance( thing ) ) {
 					DerivedValue derivedValue = DerivedValue.class.cast( thing );
 					String template = getTemplateFromString( derivedValue.getExpression(), factory );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( derivedValue.getExpression() );
 					formulaAliases.add( derivedValue.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else if ( org.hibernate.metamodel.relational.Column.class.isInstance( thing )) {
 					org.hibernate.metamodel.relational.Column col = org.hibernate.metamodel.relational.Column.class.cast( thing );
 					String colName = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect() ) );
 					columnsLazy.add( lazy );
 					// TODO: properties only selectable if they are non-plural???
 					columnSelectables.add( prop.isSimpleValue() );
 
 					readers[l] =
 							col.getReadFragment() == null ?
 									col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 									col.getReadFragment();
 					String readerTemplate = getTemplateFromColumn( col, factory );
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
 
 			// TODO: fix this when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
 			//joinedFetchesList.add( prop.getValue().getFetchMode() );
 			joinedFetchesList.add( FetchMode.DEFAULT );
 			// TODO: fix this when HHH-6355 is fixed; for now assume CascadeStyle.NONE
 			//cascades.add( prop.getCascadeStyle() );
 			cascades.add( CascadeStyle.NONE );
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
 
 		subclassPropertyCascadeStyleClosure = cascades.toArray( new CascadeStyle[ cascades.size() ] );
 		subclassPropertyFetchModeClosure = joinedFetchesList.toArray( new FetchMode[ joinedFetchesList.size() ] );
 
 		propertyDefinedOnSubclass = ArrayHelper.toBooleanArray( definedBySubclass );
 
 		Map<String, String> filterDefaultConditionsByName = new HashMap<String, String>();
 		for ( FilterDefinition filterDefinition : entityBinding.getFilterDefinitions() ) {
 			filterDefaultConditionsByName.put( filterDefinition.getFilterName(), filterDefinition.getDefaultFilterCondition() );
 		}
 		filterHelper = new FilterHelper( filterDefaultConditionsByName, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = null;
 		temporaryIdTableDDL = null;
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	public String getTemplateFromColumn(org.hibernate.metamodel.relational.Column column, SessionFactoryImplementor factory) {
 		String templateString;
 		if ( column.getReadFragment() != null ) {
 			templateString = getTemplateFromString( column.getReadFragment(), factory );
 		}
 		else {
 			String columnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			templateString = Template.TEMPLATE + '.' + columnName;
 		}
 		return templateString;
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
 			tableNumbers.add(  tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( colNumbers[j] );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( formNumbers[j] );
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
 
         if ( LOG.isTraceEnabled() ) {
 			LOG.trace(
 					"Initializing lazy properties of: " +
 							MessageHelper.infoString( this, id, getFactory() ) +
 							", field access: " + fieldName
 			);
 		}
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
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
 
         if (!hasLazyProperties()) throw new AssertionFailure("no lazy properties");
 
         LOG.trace("Initializing lazy properties from datastore");
 
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
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
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
 					ps.close();
 				}
 			}
 
             LOG.trace("Done initializing lazy properties");
 
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
 
         LOG.trace("Initializing lazy properties from second-level cache");
 
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
 
         LOG.trace("Done initializing lazy properties");
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
 		if ( snapshot != null ) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSettings().isJdbcBatchVersionedData();
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
 
         if (LOG.isTraceEnabled()) LOG.trace("Getting current persistent state for: "
                                             + MessageHelper.infoString(this, id, getFactory()));
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
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
 				ps.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
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
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
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
         if (LOG.isTraceEnabled()) LOG.trace("Forcing version increment [" + MessageHelper.infoString(this, id, getFactory()) + "; "
                                             + getVersionType().toLoggableString(currentVersion, getFactory()) + " -> "
                                             + getVersionType().toLoggableString(nextVersion, getFactory()) + "]");
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = st.executeUpdate();
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				st.close();
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
 
         if (LOG.isTraceEnabled()) LOG.trace("Getting version: " + MessageHelper.infoString(this, id, getFactory()));
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
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
 				st.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index 4f30c3d2f9..fbdeef1bdb 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,1024 +1,1034 @@
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
 package org.hibernate.persister.entity;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.SimpleValueBinding;
+import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.Type;
 
 /**
  * The default implementation of the <tt>EntityPersister</tt> interface.
  * Implements the "table-per-class-hierarchy" or "roll-up" mapping strategy
  * for an entity class and its inheritence hierarchy.  This is implemented
  * as a single table holding all classes in the hierarchy with a discrimator
  * column used to determine which concrete class is referenced.
  *
  * @author Gavin King
  */
 public class SingleTableEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int joinSpan;
 	private final String[] qualifiedTableNames;
 	private final boolean[] isInverseTable;
 	private final boolean[] isNullableTable;
 	private final String[][] keyColumnNames;
 	private final boolean[] cascadeDeleteEnabled;
 	private final boolean hasSequentialSelects;
 	
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final boolean[] subclassTableIsLazyClosure;
 	private final boolean[] isInverseSubclassTable;
 	private final boolean[] isNullableSubclassTable;
 	private final boolean[] subclassTableSequentialSelect;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] propertyTableNumbers;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	// discriminator column
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final boolean forceDiscriminator;
 	private final String discriminatorColumnName;
 	private final String discriminatorColumnReaders;
 	private final String discriminatorColumnReaderTemplate;
 	private final String discriminatorFormula;
 	private final String discriminatorFormulaTemplate;
 	private final String discriminatorAlias;
 	private final Type discriminatorType;
 	private final String discriminatorSQLValue;
 	private final boolean discriminatorInsertable;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//private final Map propertyTableNumbersByName = new HashMap();
 	private final Map propertyTableNumbersByNameAndSubclass = new HashMap();
 	
 	private final Map sequentialSelectStringsByEntityName = new HashMap();
 
 	private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
 	private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
 	private static final String NULL_STRING = "null";
 	private static final String NOT_NULL_STRING = "not null";
 
 	//INITIALIZATION:
 
 	public SingleTableEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		joinSpan = persistentClass.getJoinClosureSpan()+1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 		final Table table = persistentClass.getRootTable();
 		qualifiedTableNames[0] = table.getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		customSQLInsert[0] = persistentClass.getCustomSQLInsert();
 		insertCallable[0] = customSQLInsert[0] != null && persistentClass.isCustomInsertCallable();
 		insertResultCheckStyles[0] = persistentClass.getCustomSQLInsertCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[0], insertCallable[0] )
 									  : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLUpdate[0] = persistentClass.getCustomSQLUpdate();
 		updateCallable[0] = customSQLUpdate[0] != null && persistentClass.isCustomUpdateCallable();
 		updateResultCheckStyles[0] = persistentClass.getCustomSQLUpdateCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[0], updateCallable[0] )
 									  : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLDelete[0] = persistentClass.getCustomSQLDelete();
 		deleteCallable[0] = customSQLDelete[0] != null && persistentClass.isCustomDeleteCallable();
 		deleteResultCheckStyles[0] = persistentClass.getCustomSQLDeleteCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[0], deleteCallable[0] )
 									  : persistentClass.getCustomSQLDeleteCheckStyle();
 
 		// JOINS
 
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		int j = 1;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			qualifiedTableNames[j] = join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			);
 			isInverseTable[j] = join.isInverse();
 			isNullableTable[j] = join.isOptional();
 			cascadeDeleteEnabled[j] = join.getKey().isCascadeDeleteEnabled() && 
 				factory.getDialect().supportsCascadeDelete();
 
 			customSQLInsert[j] = join.getCustomSQLInsert();
 			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
 			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
 		                                  : join.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[j] = join.getCustomSQLUpdate();
 			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
 			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
 		                                  : join.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[j] = join.getCustomSQLDelete();
 			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
 			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
 		                                  : join.getCustomSQLDeleteCheckStyle();
 
 			Iterator iter = join.getKey().getColumnIterator();
 			keyColumnNames[j] = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyColumnNames[j][i++] = col.getQuotedName( factory.getDialect() );
 			}
 
 			j++;
 		}
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames, 
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 		
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassJoin(join) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isInverses.add( join.isInverse() );
 			isNullables.add( join.isOptional() );
 			isLazies.add( lazyAvailable && join.isLazy() );
 			if ( join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin(join) ) hasDeferred = true;
 			subclassTables.add( join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 			Iterator iter = join.getKey().getColumnIterator();
 			String[] keyCols = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyCols[i++] = col.getQuotedName( factory.getDialect() );
 			}
 			joinKeyColumns.add(keyCols);
 		}
 		
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		final Object discriminatorValue;
 		if ( persistentClass.isPolymorphic() ) {
 			Value discrimValue = persistentClass.getDiscriminator();
 			if (discrimValue==null) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = persistentClass.isForceDiscriminator();
 			Selectable selectable = (Selectable) discrimValue.getColumnIterator().next();
 			if ( discrimValue.hasFormula() ) {
 				Formula formula = (Formula) selectable;
 				discriminatorFormula = formula.getFormula();
 				discriminatorFormulaTemplate = formula.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				Column column = (Column) selectable;
 				discriminatorColumnName = column.getQuotedName( factory.getDialect() );
 				discriminatorColumnReaders = column.getReadExpr( factory.getDialect() );
 				discriminatorColumnReaderTemplate = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorAlias = column.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 			discriminatorType = persistentClass.getDiscriminator().getType();
 			if ( persistentClass.isDiscriminatorValueNull() ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( persistentClass.isDiscriminatorValueNotNull() ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = persistentClass.isDiscriminatorInsertable() && !discrimValue.hasFormula();
 				try {
 					DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 					discriminatorValue = dtype.stringToObject( persistentClass.getDiscriminatorValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i=0;
 		while( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			propertyTableNumbers[i++] = persistentClass.getJoinNumber(prop);
 
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 		
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 		
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Integer join = persistentClass.getJoinNumber(prop);
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( prop.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put( 
 					prop.getPersistentClass().getEntityName() + '.' + prop.getName(), 
 					join 
 			);
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaJoinedNumbers.add(join);
 				}
 				else {
 					columnJoinNumbers.add(join);
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( persistentClass.isPolymorphic() ) {
 			iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				if ( sc.isDiscriminatorValueNull() ) {
 					subclassesByDiscriminatorValue.put( NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else if ( sc.isDiscriminatorValueNotNull() ) {
 					subclassesByDiscriminatorValue.put( NOT_NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						subclassesByDiscriminatorValue.put(
 							dtype.stringToObject( sc.getDiscriminatorValue() ),
 							sc.getEntityName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException("Error parsing discriminator value", e);
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
 	public SingleTableEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( entityBinding, cacheAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		// TODO: fix when joins are working (HHH-6391)
 		//joinSpan = entityBinding.getJoinClosureSpan() + 1;
 		joinSpan = 1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 
 		// TODO: fix when EntityBinhding.getRootEntityBinding() exists (HHH-6337)
 		//final Table table = entityBinding.getRootEntityBinding().getBaseTable();
 		final TableSpecification table = entityBinding.getBaseTable();
 		qualifiedTableNames[0] = table.getQualifiedName( factory.getDialect() );
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		initializeCustomSql( entityBinding.getCustomInsert(), 0, customSQLInsert, insertCallable, insertResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomUpdate(), 0, customSQLUpdate, updateCallable, updateResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomDelete(), 0, customSQLDelete, deleteCallable, deleteResultCheckStyles );
 
 		// JOINS
 
 		// TODO: add join stuff when HHH-6391 is working
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames,
 				ArrayHelper.toStringArray( entityBinding.getSynchronizedTableNames() )
 		);
 
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 
 		// TODO: add join stuff when HHH-6391 is working
 
 
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
 		//  for now set hasSubclasses to false
 		//hasSubclasses = entityBinding.hasSubclasses();
 		boolean hasSubclasses = false;
 
 		//polymorphic = ! entityBinding.isRoot() || entityBinding.hasSubclasses();
 		boolean isPolymorphic = ! entityBinding.isRoot() || hasSubclasses;
 		final Object discriminatorValue;
 		if ( isPolymorphic ) {
 			org.hibernate.metamodel.relational.Value discrimValue =
 					entityBinding.getEntityDiscriminator().getValueBinding().getValue();
 			if (discrimValue==null) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = entityBinding.getEntityDiscriminator().isForced();
 			if ( ! SimpleValue.class.isInstance(  discrimValue ) ) {
 				throw new MappingException( "discriminator must be mapped to a single column or formula." );
 			}
 			if ( DerivedValue.class.isInstance( discrimValue ) ) {
 				DerivedValue formula = ( DerivedValue ) discrimValue;
 				discriminatorFormula = formula.getExpression();
 				discriminatorFormulaTemplate = getTemplateFromString( formula.getExpression(), factory );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else if ( org.hibernate.metamodel.relational.Column.class.isInstance( discrimValue ) ) {
 				org.hibernate.metamodel.relational.Column column = ( org.hibernate.metamodel.relational.Column ) discrimValue;
 				discriminatorColumnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 				discriminatorColumnReaders =
 						column.getReadFragment() == null ?
 								column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 								column.getReadFragment();
 				discriminatorColumnReaderTemplate = getTemplateFromColumn( column, factory );
 				// TODO: fix this when EntityBinding.getRootEntityBinding() is implemented;
 				// for now, assume entityBinding is the root
 				//discriminatorAlias = column.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getBaseTable );
 				discriminatorAlias = column.getAlias( factory.getDialect() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 			else {
 				throw new MappingException( "Unknown discriminator value type:" + discrimValue.toLoggableString() );
 			}
 			discriminatorType =
 					entityBinding
 							.getEntityDiscriminator()
 							.getValueBinding()
 							.getHibernateTypeDescriptor()
 							.getResolvedTypeMapping();
 			if ( entityBinding.getDiscriminatorValue() == null ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorValue().equals( NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorValue().equals( NOT_NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable =
 						entityBinding.getEntityDiscriminator().isInserted() &&
 								! DerivedValue.class.isInstance( discrimValue );
 				try {
 					DiscriminatorType dtype = ( DiscriminatorType ) discriminatorType;
 					discriminatorValue = dtype.stringToObject( entityBinding.getDiscriminatorValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		int i=0;
 		for( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			// TODO: fix when joins are working (HHH-6391)
 			//propertyTableNumbers[i++] = entityBinding.getJoinNumber( attributeBinding);
 			if ( attributeBinding == entityBinding.getEntityIdentifier().getValueBinding() ) {
 				continue; // skip identifier binding
 			}
+			if ( ! attributeBinding.getAttribute().isSingular() ) {
+				continue;
+			}
 			propertyTableNumbers[ i++ ] = 0;
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 
 		// TODO: fix when subclasses are working (HHH-6337)
 		//for ( AttributeBinding prop : entityBinding.getSubclassAttributeBindingClosure() ) {
-		for ( AttributeBinding prop : entityBinding.getAttributeBindingClosure() ) {
+		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
+			if ( ! attributeBinding.getAttribute().isSingular() ) {
+				continue;
+			}
+			SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
+
 			// TODO: fix when joins are working (HHH-6391)
-			//int join = entityBinding.getJoinNumber(prop);
+			//int join = entityBinding.getJoinNumber(singularAttributeBinding);
 			int join = 0;
 			propertyJoinNumbers.add(join);
 
-			//propertyTableNumbersByName.put( prop.getName(), join );
+			//propertyTableNumbersByName.put( singularAttributeBinding.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put(
-					prop.getEntityBinding().getEntity().getName() + '.' + prop.getAttribute().getName(),
+					singularAttributeBinding.getEntityBinding().getEntity().getName() + '.' + singularAttributeBinding.getAttribute().getName(),
 					join
 			);
 
-			for ( SimpleValue simpleValue : prop.getValues() ) {
-				if ( DerivedValue.class.isInstance( simpleValue ) ) {
+			for ( SimpleValueBinding simpleValueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
+				if ( DerivedValue.class.isInstance( simpleValueBinding.getSimpleValue() ) ) {
 					formulaJoinedNumbers.add( join );
 				}
 				else {
 					columnJoinNumbers.add( join );
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		// TODO; fix when subclasses are working (HHH-6337)
 		//int subclassSpan = entityBinding.getSubclassSpan() + 1;
 		int subclassSpan = 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( isPolymorphic ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 
 		// TODO; fix when subclasses are working (HHH-6337)
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( entityBinding );
 
 		postConstruct( mapping );
 	}
 
 	private static void initializeCustomSql(
 			CustomSQL customSql,
 			int i,
 			String[] sqlStrings,
 			boolean[] callable,
 			ExecuteUpdateResultCheckStyle[] checkStyles) {
 		sqlStrings[i] = customSql != null ?  customSql.getSql(): null;
 		callable[i] = sqlStrings[i] != null && customSql.isCallable();
 		checkStyles[i] = customSql != null && customSql.getCheckStyle() != null ?
 				customSql.getCheckStyle() :
 				ExecuteUpdateResultCheckStyle.determineDefault( sqlStrings[i], callable[i] );
 	}
 
 	protected boolean isInverseTable(int j) {
 		return isInverseTable[j];
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return isInverseSubclassTable[j];
 	}
 
 	public String getDiscriminatorColumnName() {
 		return discriminatorColumnName;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return discriminatorColumnReaders;
 	}			
 	
 	public String getDiscriminatorColumnReaderTemplate() {
 		return discriminatorColumnReaderTemplate;
 	}	
 	
 	protected String getDiscriminatorAlias() {
 		return discriminatorAlias;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return discriminatorFormulaTemplate;
 	}
 
 	public String getTableName() {
 		return qualifiedTableNames[0];
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		if (value==null) {
 			return (String) subclassesByDiscriminatorValue.get(NULL_DISCRIMINATOR);
 		}
 		else {
 			String result = (String) subclassesByDiscriminatorValue.get(value);
 			if (result==null) result = (String) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
 			return result;
 		}
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	//Access cached SQL
 
 	protected boolean isDiscriminatorFormula() {
 		return discriminatorColumnName==null;
 	}
 
 	protected String getDiscriminatorFormula() {
 		return discriminatorFormula;
 	}
 
 	protected String getTableName(int j) {
 		return qualifiedTableNames[j];
 	}
 	
 	protected String[] getKeyColumns(int j) {
 		return keyColumnNames[j];
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return cascadeDeleteEnabled[j];
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return propertyTableNumbers[property]==j;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 	
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' ' + name;
 	}
 
 	public String filterFragment(String alias) throws MappingException {
 		String result = discriminatorFilterFragment(alias);
 		if ( hasWhere() ) result += " and " + getSQLWhereString(alias);
 		return result;
 	}
 	
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return forceDiscriminator ?
 			discriminatorFilterFragment(alias) :
 			"";
 	}
 
 	private String discriminatorFilterFragment(String alias) throws MappingException {
 		if ( needsDiscriminator() ) {
 			InFragment frag = new InFragment();
 
 			if ( isDiscriminatorFormula() ) {
 				frag.setFormula( alias, getDiscriminatorFormulaTemplate() );
 			}
 			else {
 				frag.setColumn( alias, getDiscriminatorColumnName() );
 			}
 
 			String[] subclasses = getSubclassClosure();
 			for ( int i=0; i<subclasses.length; i++ ) {
 				final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclasses[i] );
 				if ( !queryable.isAbstract() ) frag.addValue( queryable.getDiscriminatorSQLValue() );
 			}
 
 			StringBuffer buf = new StringBuffer(50)
 				.append(" and ")
 				.append( frag.toFragmentString() );
 
 			return buf.toString();
 		}
 		else {
 			return "";
 		}
 	}
 
 	private boolean needsDiscriminator() {
 		return forceDiscriminator || isInherited();
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( isDiscriminatorFormula() ) {
 			select.addFormula( name, getDiscriminatorFormulaTemplate(), getDiscriminatorAlias() );
 		}
 		else {
 			select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 		}
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return joinSpan;
 	}
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 
 		if (discriminatorInsertable) {
 			insert.addColumn( getDiscriminatorColumnName(), discriminatorSQLValue );
 		}
 
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return propertyTableNumbers;
 	}
 		
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return hasSequentialSelects && 
 			isSubclassTableSequentialSelect( getSubclassPropertyTableNumber(propertyName, entityName) );
 	}
 	
 	public boolean hasSequentialSelect() {
 		return hasSequentialSelects;
 	}
 	
 	private int getSubclassPropertyTableNumber(String propertyName, String entityName) {
 		Type type = propertyMapping.toType(propertyName);
 		if ( type.isAssociationType() && ( (AssociationType) type ).useLHSPrimaryKey() ) return 0;
 		final Integer tabnum = (Integer) propertyTableNumbersByNameAndSubclass.get(entityName + '.' + propertyName);
 		return tabnum==null ? 0 : tabnum.intValue();
 	}
 	
 	protected String getSequentialSelect(String entityName) {
 		return (String) sequentialSelectStringsByEntityName.get(entityName);
 	}
 
 	private String generateSequentialSelect(Loadable persister) {
 		//if ( this==persister || !hasSequentialSelects ) return null;
 
 		//note that this method could easily be moved up to BasicEntityPersister,
 		//if we ever needed to reuse it from other subclasses
 		
 		//figure out which tables need to be fetched
 		AbstractEntityPersister subclassPersister = (AbstractEntityPersister) persister;
 		HashSet tableNumbers = new HashSet();
 		String[] props = subclassPersister.getPropertyNames();
 		String[] classes = subclassPersister.getPropertySubclassNames();
 		for ( int i=0; i<props.length; i++ ) {
 			int propTableNumber = getSubclassPropertyTableNumber( props[i], classes[i] );
 			if ( isSubclassTableSequentialSelect(propTableNumber) && !isSubclassTableLazy(propTableNumber) ) {
 				tableNumbers.add( propTableNumber);
 			}
 		}
 		if ( tableNumbers.isEmpty() ) return null;
 		
 		//figure out which columns are needed
 		ArrayList columnNumbers = new ArrayList();
 		final int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassColumnClosure().length; i++ ) {
 			if ( tableNumbers.contains( columnTableNumbers[i] ) ) {
 				columnNumbers.add( i );
 			}
 		}
 		
 		//figure out which formulas are needed
 		ArrayList formulaNumbers = new ArrayList();
 		final int[] formulaTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassFormulaTemplateClosure().length; i++ ) {
 			if ( tableNumbers.contains( formulaTableNumbers[i] ) ) {
 				formulaNumbers.add( i );
 			}
 		}
 		
 		//render the SQL
 		return renderSelect( 
 			ArrayHelper.toIntArray(tableNumbers),
 			ArrayHelper.toIntArray(columnNumbers),
 			ArrayHelper.toIntArray(formulaNumbers)
 		);
 	}
 		
 		
 	protected String[] getSubclassTableKeyColumns(int j) {
 		return subclassTableKeyColumnClosure[j];
 	}
 
 	public String getSubclassTableName(int j) {
 		return subclassTableNameClosure[j];
 	}
 
 	public int getSubclassTableSpan() {
 		return subclassTableNameClosure.length;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 	
 	protected boolean isNullableTable(int j) {
 		return isNullableTable[j];
 	}
 	
 	protected boolean isNullableSubclassTable(int j) {
 		return isNullableSubclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
 		if (index==null) return null;
 		return qualifiedTableNames[ propertyTableNumbers[ index.intValue() ] ];
 	}
 	
 	public void postInstantiate() {
 		super.postInstantiate();
 		if (hasSequentialSelects) {
 			String[] entityNames = getSubclassClosure();
 			for ( int i=1; i<entityNames.length; i++ ) {
 				Loadable loadable = (Loadable) getFactory().getEntityPersister( entityNames[i] );
 				if ( !loadable.isAbstract() ) { //perhaps not really necessary...
 					String sequentialSelect = generateSequentialSelect(loadable);
 					sequentialSelectStringsByEntityName.put( entityNames[i], sequentialSelect );
 				}
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return getTableSpan() > 1;
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index 0703c5743a..6ae929ff03 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,249 +1,249 @@
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
 package org.hibernate.persister.internal;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 /**
  * The standard Hibernate {@link PersisterFactory} implementation
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class PersisterFactoryImpl implements PersisterFactory, ServiceRegistryAwareService {
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			PersistentClass.class,
 			EntityRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations using
 	 * an {@link EntityBinding}.
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW to ENTITY_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			EntityBinding.class,
 			EntityRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			Collection.class,
 			CollectionRegionAccessStrategy.class,
 			Configuration.class,
 			SessionFactoryImplementor.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations using
-	 * a {@link PluralAttributeBinding}
+	 * a {@link org.hibernate.metamodel.binding.AbstractPluralAttributeBinding}
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW to COLLECTION_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
-			PluralAttributeBinding.class,
+			AbstractPluralAttributeBinding.class,
 			CollectionRegionAccessStrategy.class,
 			MetadataImplementor.class,
 			SessionFactoryImplementor.class
 	};
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(
 			PersistentClass metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(EntityBinding metadata,
 												 EntityRegionAccessStrategy cacheAccessStrategy,
 												 SessionFactoryImplementor factory,
 												 Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getCustomEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
 	// TODO: change metadata arg type to EntityBinding when new metadata is integrated
 	private static EntityPersister create(
 			Class<? extends EntityPersister> persisterClass,
 			Class[] persisterConstructorArgs,
 			Object metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException {
 		try {
 			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( metadata, cacheAccessStrategy, factory, cfg );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
 			Collection collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
 		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS, cfg, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(MetadataImplementor metadata,
-														 PluralAttributeBinding collectionMetadata,
+														 AbstractPluralAttributeBinding collectionMetadata,
 														 CollectionRegionAccessStrategy cacheAccessStrategy,
 														 SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
 		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
-	// TODO: change collectionMetadata arg type to PluralAttributeBinding when new metadata is integrated
+	// TODO: change collectionMetadata arg type to AbstractPluralAttributeBinding when new metadata is integrated
 	// TODO: change metadata arg type to MetadataImplementor when new metadata is integrated
 	private static CollectionPersister create(
 			Class<? extends CollectionPersister> persisterClass,
 			Class[] persisterConstructorArgs,
 			Object cfg,
 			Object collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		try {
 			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( collectionMetadata, cacheAccessStrategy, cfg, factory );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
index e67fa86a1f..b4ad304596 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
@@ -1,121 +1,124 @@
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
 package org.hibernate.persister.internal;
 
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.UnionSubclass;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
+import org.hibernate.metamodel.binding.CollectionElementNature;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.BasicCollectionPersister;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.OneToManyPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
 import org.hibernate.persister.entity.SingleTableEntityPersister;
 import org.hibernate.persister.entity.UnionSubclassEntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.UnknownPersisterException;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardPersisterClassResolver implements PersisterClassResolver {
 
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		// todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
 
 		if ( metadata.isRoot() ) {
 			return singleTableEntityPersister(); // EARLY RETURN!
 		}
 		switch ( metadata.getInheritanceType() ) {
 			case JOINED: {
 				return joinedSubclassEntityPersister();
 			}
 			case SINGLE_TABLE: {
 				return singleTableEntityPersister();
 			}
 			case TABLE_PER_CLASS: {
 				return unionSubclassEntityPersister();
 			}
 			default: {
 				throw new UnknownPersisterException(
 						"Could not determine persister implementation for entity [" + metadata.getEntity().getName() + "]"
 				);
 			}
 
 		}
 	}
 
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		// todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
 		if ( RootClass.class.isInstance( metadata ) ) {
 			return singleTableEntityPersister();
 		}
 		else if ( JoinedSubclass.class.isInstance( metadata ) ) {
 			return joinedSubclassEntityPersister();
 		}
 		else if ( UnionSubclass.class.isInstance( metadata ) ) {
 			return unionSubclassEntityPersister();
 		}
 		else {
 			throw new UnknownPersisterException(
 					"Could not determine persister implementation for entity [" + metadata.getEntityName() + "]"
 			);
 		}
 	}
 
 	public Class<? extends EntityPersister> singleTableEntityPersister() {
 		return SingleTableEntityPersister.class;
 	}
 
 	public Class<? extends EntityPersister> joinedSubclassEntityPersister() {
 		return JoinedSubclassEntityPersister.class;
 	}
 
 	public Class<? extends EntityPersister> unionSubclassEntityPersister() {
 		return UnionSubclassEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return metadata.isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
 	}
 
 	@Override
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
-		return metadata.getCollectionElement().isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
+		return metadata.getCollectionElement().getCollectionElementNature() == CollectionElementNature.ONE_TO_MANY
+				? oneToManyPersister()
+				: basicCollectionPersister();
 	}
 
 	private Class<OneToManyPersister> oneToManyPersister() {
 		return OneToManyPersister.class;
 	}
 
 	private Class<BasicCollectionPersister> basicCollectionPersister() {
 		return BasicCollectionPersister.class;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
index f82123e71e..d3660fb255 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
@@ -1,84 +1,84 @@
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
 package org.hibernate.persister.spi;
 
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.Service;
 
 /**
  * Provides persister classes based on the entity or collection role.
  * The persister class is chosen according to the following rules in decreasing priority:
  *  - the persister class defined explicitly via annotation or XML
  *  - the persister class returned by the PersisterClassResolver implementation (if not null)
  *  - the default provider as chosen by Hibernate Core (best choice most of the time)
  *
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  * @author Steve Ebersole
  */
 public interface PersisterClassResolver extends Service {
 	/**
 	 * Returns the entity persister class for a given entityName or null
 	 * if the entity persister class should be the default.
 	 *
 	 * @param metadata The entity metadata
 	 *
 	 * @return The entity persister class to use
 	 */
 	Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata);
 
 	/**
 	 * Returns the entity persister class for a given entityName or null
 	 * if the entity persister class should be the default.
 	 *
 	 * @param metadata The entity metadata
 	 *
 	 * @return The entity persister class to use
 	 */
 	Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata);
 
 	/**
 	 * Returns the collection persister class for a given collection role or null
 	 * if the collection persister class should be the default.
 	 *
 	 * @param metadata The collection metadata
 	 *
 	 * @return The collection persister class to use
 	 */
 	Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata);
 
 	/**
 	 * Returns the collection persister class for a given collection role or null
 	 * if the collection persister class should be the default.
 	 *
 	 * @param metadata The collection metadata
 	 *
 	 * @return The collection persister class to use
 	 */
-	Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata);
+	Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index 774ce9871a..15dfd4cb3e 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
@@ -1,129 +1,129 @@
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
 package org.hibernate.persister.spi;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.Service;
 
 /**
  * Contract for creating persister instances (both {@link EntityPersister} and {@link CollectionPersister} varieties).
  *
  * @author Steve Ebersole
  */
 public interface PersisterFactory extends Service {
 
 	// TODO: is it really necessary to provide Configuration to CollectionPersisters ?
 	// Should it not be enough with associated class ? or why does EntityPersister's not get access to configuration ?
 	//
 	// The only reason I could see that Configuration gets passed to collection persisters
 	// is so that they can look up the dom4j node name of the entity element in case
 	// no explicit node name was applied at the collection element level.  Are you kidding me?
 	// Trivial to fix then.  Just store and expose the node name on the entity persister
 	// (which the collection persister looks up anyway via other means...).
 
 	/**
 	 * Create an entity persister instance.
 	 *
 	 * @param model The O/R mapping metamodel definition for the entity
 	 * @param cacheAccessStrategy The caching strategy for this entity
 	 * @param factory The session factory
 	 * @param cfg The overall mapping
 	 *
 	 * @return An appropriate entity persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public EntityPersister createEntityPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException;
 
 	/**
 	 * Create an entity persister instance.
 	 *
 	 * @param model The O/R mapping metamodel definition for the entity
 	 * @param cacheAccessStrategy The caching strategy for this entity
 	 * @param factory The session factory
 	 * @param cfg The overall mapping
 	 *
 	 * @return An appropriate entity persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public EntityPersister createEntityPersister(
 			EntityBinding model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException;
 
 	/**
 	 * Create a collection persister instance.
 	 *
 	 * @param cfg The configuration
 	 * @param model The O/R mapping metamodel definition for the collection
 	 * @param cacheAccessStrategy The caching strategy for this collection
 	 * @param factory The session factory
 	 *
 	 * @return An appropriate collection persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
 			Collection model,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException;
 
 	/**
 	 * Create a collection persister instance.
 	 *
 	 * @param metadata The metadata
 	 * @param model The O/R mapping metamodel definition for the collection
 	 * @param cacheAccessStrategy The caching strategy for this collection
 	 * @param factory The session factory
 	 *
 	 * @return An appropriate collection persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public CollectionPersister createCollectionPersister(
 			MetadataImplementor metadata,
-			PluralAttributeBinding model,
+			AbstractPluralAttributeBinding model,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
index 00db55906e..b3a41a1dd8 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,381 +1,392 @@
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
 import java.lang.reflect.Constructor;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.engine.internal.UnsavedValueFactory;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.binding.AssociationAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
-import org.hibernate.metamodel.binding.SimpleAttributeBinding;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
+import org.hibernate.metamodel.binding.SimpleSingularAttributeBinding;
+import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * Responsible for generation of runtime metamodel {@link Property} representations.
  * Makes distinction between identifier, version, and other (standard) properties.
  *
  * @author Steve Ebersole
  */
 public class PropertyFactory {
 
 	/**
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierProperty(PersistentClass mappedEntity, IdentifierGenerator generator) {
 
 		String mappedUnsavedValue = mappedEntity.getIdentifier().getNullValue();
 		Type type = mappedEntity.getIdentifier().getType();
 		Property property = mappedEntity.getIdentifierProperty();
 		
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor(mappedEntity)
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					mappedEntity.hasIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getName(),
 					property.getNodeName(),
 					type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierProperty(EntityBinding mappedEntity, IdentifierGenerator generator) {
 
-		final SimpleAttributeBinding property = mappedEntity.getEntityIdentifier().getValueBinding();
+		final SimpleSingularAttributeBinding property = mappedEntity.getEntityIdentifier().getValueBinding();
 
 		// TODO: the following will cause an NPE with "virtual" IDs; how should they be set?
 		final String mappedUnsavedValue = property.getUnsavedValue();
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor( mappedEntity )
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.getEntityIdentifier().isEmbedded(),
 					mappedEntity.getEntityIdentifier().isIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getAttribute().getName(),
 					null,
 					type,
 					mappedEntity.getEntityIdentifier().isEmbedded(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(Property property, boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 		
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getType(),
 				getConstructor( property.getPersistentClass() )
 			);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		return new VersionProperty(
 		        property.getName(),
 		        property.getNodeName(),
 		        property.getValue().getType(),
 		        lazy,
 				property.isInsertable(),
 				property.isUpdateable(),
 		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				property.isUpdateable() && !lazy,
 				property.isOptimisticLocked(),
 		        property.getCascadeStyle(),
 		        unsavedValue
 			);
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
-	public static VersionProperty buildVersionProperty(SimpleAttributeBinding property, boolean lazyAvailable) {
+	public static VersionProperty buildVersionProperty(SimpleSingularAttributeBinding property, boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				( VersionType ) property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
 				getConstructor( property.getEntityBinding() )
 		);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
+		final CascadeStyle cascadeStyle = property.isAssociation()
+				? ( (AssociationAttributeBinding) property ).getCascadeStyle()
+				: CascadeStyle.NONE;
+
 		return new VersionProperty(
 		        property.getAttribute().getName(),
 		        null,
 		        property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
 		        lazy,
-				property.isInsertable(),
-				property.isUpdatable(),
-		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
+				true, // insertable
+				true, // updatable
+		        property.getGeneration() == PropertyGeneration.INSERT
+						|| property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isNullable(),
-				property.isUpdatable() && !lazy,
-				property.isOptimisticLockable(),
-				// TODO: get cascadeStyle from property when HHH-6355 is fixed; for now, assume NONE
-				//property.getCascadeStyle(),
-				CascadeStyle.NONE,
+				!lazy,
+				property.isIncludedInOptimisticLocking(),
+				cascadeStyle,
 		        unsavedValue
 			);
 	}
 
 	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate StandardProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
 		
 		final Type type = property.getValue().getType();
 		
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 		
 		// we need to dirty check many-to-ones with not-found="ignore" in order 
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 		
 		boolean alwaysDirtyCheck = type.isAssociationType() && 
 				( (AssociationType) type ).isAlwaysDirtyChecked(); 
 
 		return new StandardProperty(
 				property.getName(),
 				property.getNodeName(),
 				type,
 				lazyAvailable && property.isLazy(),
 				property.isInsertable(),
 				property.isUpdateable(),
 		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				alwaysDirtyCheck || property.isUpdateable(),
 				property.isOptimisticLocked(),
 				property.getCascadeStyle(),
 		        property.getValue().getFetchMode()
 			);
 	}
 
 	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate StandardProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(AttributeBinding property, boolean lazyAvailable) {
 
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
-		boolean alwaysDirtyCheck = type.isAssociationType() &&
-				( (AssociationType) type ).isAlwaysDirtyChecked();
+		final boolean alwaysDirtyCheck = type.isAssociationType() && ( (AssociationType) type ).isAlwaysDirtyChecked();
+
+		if ( property.getAttribute().isSingular() ) {
+			final SingularAttributeBinding singularAttributeBinding = ( SingularAttributeBinding ) property;
+			final CascadeStyle cascadeStyle = singularAttributeBinding.isAssociation()
+					? ( (AssociationAttributeBinding) singularAttributeBinding ).getCascadeStyle()
+					: CascadeStyle.NONE;
+			final FetchMode fetchMode = singularAttributeBinding.isAssociation()
+					? ( (AssociationAttributeBinding) singularAttributeBinding ).getFetchMode()
+					: FetchMode.DEFAULT;
 
-		if ( property.isSimpleValue() ) {
-			SimpleAttributeBinding simpleProperty = ( SimpleAttributeBinding ) property;
 			return new StandardProperty(
-					simpleProperty.getAttribute().getName(),
+					singularAttributeBinding.getAttribute().getName(),
 					null,
 					type,
-					lazyAvailable && simpleProperty.isLazy(),
-					simpleProperty.isInsertable(),
-					simpleProperty.isUpdatable(),
-					simpleProperty.getGeneration() == PropertyGeneration.INSERT || simpleProperty.getGeneration() == PropertyGeneration.ALWAYS,
-					simpleProperty.getGeneration() == PropertyGeneration.ALWAYS,
-					simpleProperty.isNullable(),
-					alwaysDirtyCheck || simpleProperty.isUpdatable(),
-					simpleProperty.isOptimisticLockable(),
-					// TODO: get cascadeStyle from simpleProperty when HHH-6355 is fixed; for now, assume NONE
-					//simpleProperty.getCascadeStyle(),
-					CascadeStyle.NONE,
-					// TODO: get fetchMode() from simpleProperty when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
-					//simpleProperty.getFetchMode()
-					FetchMode.DEFAULT
-				);
+					lazyAvailable && singularAttributeBinding.isLazy(),
+					true, // insertable
+					true, // updatable
+					singularAttributeBinding.getGeneration() == PropertyGeneration.INSERT
+							|| singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
+					singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
+					singularAttributeBinding.isNullable(),
+					alwaysDirtyCheck,
+					singularAttributeBinding.isIncludedInOptimisticLocking(),
+					cascadeStyle,
+					fetchMode
+			);
 		}
 		else {
-			PluralAttributeBinding pluralProperty = ( PluralAttributeBinding ) property;
+			final AbstractPluralAttributeBinding pluralAttributeBinding = (AbstractPluralAttributeBinding) property;
+			final CascadeStyle cascadeStyle = pluralAttributeBinding.isAssociation()
+					? ( (AssociationAttributeBinding) pluralAttributeBinding ).getCascadeStyle()
+					: CascadeStyle.NONE;
+			final FetchMode fetchMode = pluralAttributeBinding.isAssociation()
+					? ( (AssociationAttributeBinding) pluralAttributeBinding ).getFetchMode()
+					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
-					pluralProperty.getAttribute().getName(),
+					pluralAttributeBinding.getAttribute().getName(),
 					null,
 					type,
-					lazyAvailable && pluralProperty.isLazy(),
-					// TODO: fix this when HHH-6356 is fixed; for now assume PluralAttributeBinding is updatable and insertable
-					// pluralProperty.isInsertable(),
-					//pluralProperty.isUpdatable(),
+					lazyAvailable && pluralAttributeBinding.isLazy(),
+					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
+					// pluralAttributeBinding.isInsertable(),
+					//pluralAttributeBinding.isUpdatable(),
 					true,
 					true,
 					false,
 					false,
-					pluralProperty.isNullable(),
-					// TODO: fix this when HHH-6356 is fixed; for now assume PluralAttributeBinding is updatable and insertable
-					//alwaysDirtyCheck || pluralProperty.isUpdatable(),
+//					pluralAttributeBinding.isNullable(),
+					false, // nullable - not sure what that means for a collection
+					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
+					//alwaysDirtyCheck || pluralAttributeBinding.isUpdatable(),
 					true,
-					pluralProperty.isOptimisticLocked(),
-					// TODO: get cascadeStyle from property when HHH-6355 is fixed; for now, assume NONE
-					//pluralProperty.getCascadeStyle(),
-					CascadeStyle.NONE,
-					// TODO: get fetchMode() from simpleProperty when HHH-6357 is fixed; for now, assume FetchMode.DEFAULT
-					//pluralProperty.getFetchMode()
-					FetchMode.DEFAULT
+					pluralAttributeBinding.isIncludedInOptimisticLocking(),
+					cascadeStyle,
+					fetchMode
 				);
 		}
 	}
 
 	private static Constructor getConstructor(PersistentClass persistentClass) {
 		if ( persistentClass == null || !persistentClass.hasPojoRepresentation() ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( persistentClass.getMappedClass() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Constructor getConstructor(EntityBinding entityBinding) {
 		if ( entityBinding == null || entityBinding.getEntity() == null ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( entityBinding.getEntity().getClassReference() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Getter getGetter(Property mappingProperty) {
 		if ( mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation() ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter( mappingProperty.getPersistentClass().getMappedClass(), mappingProperty.getName() );
 	}
 
 	private static Getter getGetter(AttributeBinding mappingProperty) {
 		if ( mappingProperty == null || mappingProperty.getEntityBinding().getEntity() == null ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter(
 				mappingProperty.getEntityBinding().getEntity().getClassReference(),
 				mappingProperty.getAttribute().getName()
 		);
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 3baaad5a28..296ac2ce17 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,952 +1,951 @@
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
 import org.hibernate.engine.OptimisticLockStyle;
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
-import org.hibernate.metamodel.binding.SimpleAttributeBinding;
+import org.hibernate.metamodel.binding.SimpleSingularAttributeBinding;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
-import org.hibernate.metamodel.domain.TypeNature;
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
 	private final OptimisticLockStyle optimisticLockStyle;
 
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
 
 		optimisticLockStyle = interpretOptLockMode( persistentClass.getOptimisticLockMode() );
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
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
 
 	private OptimisticLockStyle interpretOptLockMode(int optimisticLockMode) {
 		switch ( optimisticLockMode ) {
 			case Versioning.OPTIMISTIC_LOCK_NONE: {
 				return OptimisticLockStyle.NONE;
 			}
 			case Versioning.OPTIMISTIC_LOCK_DIRTY: {
 				return OptimisticLockStyle.DIRTY;
 			}
 			case Versioning.OPTIMISTIC_LOCK_ALL: {
 				return OptimisticLockStyle.ALL;
 			}
 			default: {
 				return OptimisticLockStyle.VERSION;
 			}
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
 		if (  entityBinding.getEntity().getClassReferenceUnresolved() != null ) {
 			hasPojoRepresentation = true;
 			mappedClass = entityBinding.getEntity().getClassReference();
 			proxyInterfaceClass = entityBinding.getProxyInterfaceType().getValue();
 			lazyAvailable = FieldInterceptionHelper.isInstrumented( mappedClass );
 		}
 
 		boolean hasLazy = false;
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
-		//SimpleAttributeBinding rootEntityIdentifier = entityBinding.getRootEntityBinding().getEntityIdentifier().getValueBinding();
-		SimpleAttributeBinding rootEntityIdentifier = entityBinding.getEntityIdentifier().getValueBinding();
+		//SimpleSingularAttributeBinding rootEntityIdentifier = entityBinding.getRootEntityBinding().getEntityIdentifier().getValueBinding();
+		SimpleSingularAttributeBinding rootEntityIdentifier = entityBinding.getEntityIdentifier().getValueBinding();
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
 
 		optimisticLockStyle = entityBinding.getOptimisticLockStyle();
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
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
 		Class<? extends EntityTuplizer> tuplizerClass = entityBinding.getCustomEntityTuplizerClass();
 
 		if ( tuplizerClass == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
 		}
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
 				( ( SingularAttribute ) attribute ).getSingularAttributeType().isComponent() ) {
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
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index 21de2e1f1e..44887b3819 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,183 +1,180 @@
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
 
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Set;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.LongType;
 import org.hibernate.type.StringType;
 
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
 		assertRoot( metadata, entityBinding );
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
 
 		Set<SingularAssociationAttributeBinding> referenceBindings = simpleEntityBinding.getAttributeBinding( "id" )
 				.getEntityReferencingAttributeBindings();
 		assertEquals( "There should be only one reference binding", 1, referenceBindings.size() );
 
 		SingularAssociationAttributeBinding referenceBinding = referenceBindings.iterator().next();
 		EntityBinding referencedEntityBinding = referenceBinding.getReferencedEntityBinding();
 		// TODO - Is this assertion correct (HF)?
 		assertEquals( "Should be the same entity binding", referencedEntityBinding, simpleEntityBinding );
 
 		EntityBinding entityWithManyToOneBinding = metadata.getEntityBinding( ManyToOneEntity.class.getName() );
 		Iterator<SingularAssociationAttributeBinding> it = entityWithManyToOneBinding.getEntityReferencingAttributeBindings()
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
 
 		AttributeBinding idAttributeBinding = entityBinding.getAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getEntityIdentifier().getValueBinding() );
 		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 		assertTrue( idAttributeBinding.getAttribute().isSingular() );
 		assertNotNull( idAttributeBinding.getAttribute() );
+		SingularAttributeBinding singularIdAttributeBinding = (SingularAttributeBinding) idAttributeBinding;
 		SingularAttribute singularIdAttribute =  ( SingularAttribute ) idAttributeBinding.getAttribute();
 		BasicType basicIdAttributeType = ( BasicType ) singularIdAttribute.getSingularAttributeType();
 		assertSame( Long.class, basicIdAttributeType.getClassReference() );
 
-		assertNotNull( idAttributeBinding.getValue() );
-		assertTrue( idAttributeBinding.getValue() instanceof Column );
-		Datatype idDataType = ( (Column) idAttributeBinding.getValue() ).getDatatype();
+		assertNotNull( singularIdAttributeBinding.getValue() );
+		assertTrue( singularIdAttributeBinding.getValue() instanceof Column );
+		Datatype idDataType = ( (Column) singularIdAttributeBinding.getValue() ).getDatatype();
 		assertSame( Long.class, idDataType.getJavaType() );
 		assertSame( Types.BIGINT, idDataType.getTypeCode() );
 		assertSame( LongType.INSTANCE.getName(), idDataType.getTypeName() );
 
-		AttributeBinding nameBinding = entityBinding.getAttributeBinding( "name" );
-		assertNotNull( nameBinding );
+		assertNotNull( entityBinding.getAttributeBinding( "name" ) );
+		assertNotNull( entityBinding.getAttributeBinding( "name" ).getAttribute() );
+		assertTrue( entityBinding.getAttributeBinding( "name" ).getAttribute().isSingular() );
+
+		SingularAttributeBinding nameBinding = (SingularAttributeBinding) entityBinding.getAttributeBinding( "name" );
 		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
-
-		assertTrue( nameBinding.getAttribute().isSingular() );
-		assertNotNull( nameBinding.getAttribute() );
 		SingularAttribute singularNameAttribute =  ( SingularAttribute ) nameBinding.getAttribute();
 		BasicType basicNameAttributeType = ( BasicType ) singularNameAttribute.getSingularAttributeType();
 		assertSame( String.class, basicNameAttributeType.getClassReference() );
 
 		assertNotNull( nameBinding.getValue() );
-		// until HHH-6380 is fixed, need to call getValues()
-		assertEquals( 1, nameBinding.getValuesSpan() );
-		Iterator<SimpleValue> it = nameBinding.getValues().iterator();
-		assertTrue( it.hasNext() );
-		SimpleValue nameValue = it.next();
+		SimpleValue nameValue = (SimpleValue) nameBinding.getValue();
 		assertTrue( nameValue instanceof Column );
 		Datatype nameDataType = nameValue.getDatatype();
 		assertSame( String.class, nameDataType.getJavaType() );
 		assertSame( Types.VARCHAR, nameDataType.getTypeCode() );
 		assertSame( StringType.INSTANCE.getName(), nameDataType.getTypeName() );
 	}
 
 	protected void assertRoot(MetadataImplementor metadata, EntityBinding entityBinding) {
 		assertTrue( entityBinding.isRoot() );
 		assertSame( entityBinding, metadata.getRootEntityBinding( entityBinding.getEntity().getName() ) );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
index db8f41d1fd..c9b0fffac2 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
@@ -1,92 +1,92 @@
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
 
 import java.sql.Types;
 
 import org.junit.Test;
 
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertSame;
 
 /**
  * Basic binding "smoke" tests
  *
  * @author Steve Ebersole
  */
 public class SimpleValueBindingTests extends BaseUnitTestCase {
 	public static final Datatype BIGINT = new Datatype( Types.BIGINT, "BIGINT", Long.class );
 	public static final Datatype VARCHAR = new Datatype( Types.VARCHAR, "VARCHAR", String.class );
 
 
 	@Test
 	public void testBasicMiddleOutBuilding() {
 		Table table = new Table( new Schema( null, null ), "the_table" );
 		Entity entity = new Entity( "TheEntity", "NoSuchClass", makeJavaType( "NoSuchClass" ), null );
 		EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setEntity( entity );
 		entityBinding.setBaseTable( table );
 
 		SingularAttribute idAttribute = entity.locateOrCreateSingularAttribute( "id" );
-		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( idAttribute );
+		SimpleSingularAttributeBinding attributeBinding = entityBinding.makeSimpleAttributeBinding( idAttribute );
 		attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( "long" );
 		assertSame( idAttribute, attributeBinding.getAttribute() );
 
 		entityBinding.getEntityIdentifier().setValueBinding( attributeBinding );
 
 		Column idColumn = table.locateOrCreateColumn( "id" );
 		idColumn.setDatatype( BIGINT );
 		idColumn.setSize( Size.precision( 18, 0 ) );
 		table.getPrimaryKey().addColumn( idColumn );
 		table.getPrimaryKey().setName( "my_table_pk" );
 		//attributeBinding.setValue( idColumn );
 	}
 
 	Value<Class<?>> makeJavaType(final String name) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						try {
 							return Class.forName( name );
 						}
 						catch ( Exception e ) {
 							throw new ClassLoadingException( "Could not load class : " + name, e );
 						}
 					}
 				}
 		);
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java
index 0601b4653b..4889adbde4 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MappedSuperclassTests.java
@@ -1,118 +1,119 @@
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
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.MappedSuperclass;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.domain.NonEntity;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.testing.FailureExpected;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * Tests for {@link javax.persistence.MappedSuperclass} {@link javax.persistence.AttributeOverrides}
  * and {@link javax.persistence.AttributeOverride}.
  *
  * @author Hardy Ferentschik
  */
 @FailureExpected(jiraKey = "HHH-6447", message = "Work in progress")
 public class MappedSuperclassTests extends BaseAnnotationBindingTestCase {
 	@Test
 //	@Resources(annotatedClasses = { MyMappedSuperClass.class, MyEntity.class, MyMappedSuperClassBase.class })
 	public void testSimpleAttributeOverrideInMappedSuperclass() {
 		EntityBinding binding = getEntityBinding( MyEntity.class );
-		AttributeBinding nameBinding = binding.getAttributeBinding( "name" );
+		SingularAttributeBinding nameBinding = (SingularAttributeBinding) binding.getAttributeBinding( "name" );
 		assertNotNull( "the name attribute should be bound to MyEntity", nameBinding );
 
 		Column column = (Column) nameBinding.getValue();
 		assertEquals( "Wrong column name", "MY_NAME", column.getColumnName().toString() );
 	}
 
 	@Test
 //	@Resources(annotatedClasses = { MyMappedSuperClass.class, MyEntity.class, MyMappedSuperClassBase.class })
 	public void testLastAttributeOverrideWins() {
 		EntityBinding binding = getEntityBinding( MyEntity.class );
-		AttributeBinding fooBinding = binding.getAttributeBinding( "foo" );
+		SingularAttributeBinding fooBinding = (SingularAttributeBinding) binding.getAttributeBinding( "foo" );
 		assertNotNull( "the foo attribute should be bound to MyEntity", fooBinding );
 
 		Column column = (Column) fooBinding.getValue();
 		assertEquals( "Wrong column name", "MY_FOO", column.getColumnName().toString() );
 	}
 
 	@Test
 //	@Resources(annotatedClasses = { SubclassOfNoEntity.class, NoEntity.class })
 	public void testNonEntityBaseClass() {
 		EntityBinding binding = getEntityBinding( SubclassOfNoEntity.class );
 		assertEquals( "Wrong entity name", SubclassOfNoEntity.class.getName(), binding.getEntity().getName() );
 		assertEquals( "Wrong entity name", NoEntity.class.getName(), binding.getEntity().getSuperType().getName() );
 		assertTrue( binding.getEntity().getSuperType() instanceof NonEntity );
 	}
 
 	@MappedSuperclass
 	class MyMappedSuperClassBase {
 		@Id
 		private int id;
 		String foo;
 	}
 
 	@MappedSuperclass
 	@AttributeOverride(name = "foo", column = @javax.persistence.Column(name = "SUPER_FOO"))
 	class MyMappedSuperClass extends MyMappedSuperClassBase {
 		String name;
 	}
 
 	@Entity
 	@AttributeOverrides( {
 			@AttributeOverride(name = "name", column = @javax.persistence.Column(name = "MY_NAME")),
 			@AttributeOverride(name = "foo", column = @javax.persistence.Column(name = "MY_FOO"))
 	})
 	class MyEntity extends MyMappedSuperClass {
 		private Long count;
 
 	}
 
 	class NoEntity {
 		String name;
 		int age;
 	}
 
 	@Entity
 	class SubclassOfNoEntity extends NoEntity {
 		@Id
 		private int id;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 29f2b031ad..c11f66ffff 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,769 +1,769 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.cfg.persister;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	@Override
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.spi.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   SessionFactoryImplementor sf) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionType getCollectionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
index 29e17a5c74..24c43bbdee 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,562 +1,562 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.ejb.test.ejb3configuration;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Map;
 
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceException;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest extends junit.framework.TestCase {
 	public void testPersisterClassProvider() {
 		Ejb3Configuration conf = new Ejb3Configuration();
 		conf.getProperties().put( PersisterClassResolverInitiator.IMPL_NAME, GoofyPersisterClassProvider.class );
 		conf.addAnnotatedClass( Bell.class );
 		try {
 			final EntityManagerFactory entityManagerFactory = conf.buildEntityManagerFactory();
 			entityManagerFactory.close();
 		}
 		catch ( PersistenceException e ) {
 			assertNotNull( e.getCause() );
 			assertNotNull( e.getCause().getCause() );
 			assertEquals( GoofyException.class, e.getCause().getCause().getClass() );
 
 		}
 	}
 
 	public static class GoofyPersisterClassProvider implements PersisterClassResolver {
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 			return null;
 		}
 
 		@Override
-		public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+		public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
 			return null;
 		}
 	}
 
 	public static class GoofyProvider implements EntityPersister {
 
 		@SuppressWarnings( {"UnusedParameters"})
 		public GoofyProvider(
 				org.hibernate.mapping.PersistentClass persistentClass,
 				org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 				SessionFactoryImplementor sf,
 				Mapping mapping) {
 			throw new GoofyException();
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 	}
 
 	public static class GoofyException extends RuntimeException {
 
 	}
 }
