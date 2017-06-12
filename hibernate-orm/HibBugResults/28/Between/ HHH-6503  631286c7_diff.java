diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
index ecfe6932f5..17c801f09c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
@@ -1,107 +1,105 @@
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
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.type.VersionType;
 
 /**
- * {@inheritDoc}
- *
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
 
-	public static CacheDataDescriptionImpl decode(AbstractPluralAttributeBinding model) {
+	public static CacheDataDescriptionImpl decode(PluralAttributeBinding model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.getContainer().seekEntityBinding().isVersioned(),
 				getVersionComparator( model.getContainer().seekEntityBinding() )
 		);
 	}
 
 	private static Comparator getVersionComparator(EntityBinding model ) {
 		Comparator versionComparator = null;
 		if ( model.isVersioned() ) {
 			versionComparator = (
 					( VersionType ) model.getHierarchyDetails()
 							.getVersioningAttributeBinding()
 							.getHibernateTypeDescriptor()
 							.getResolvedTypeMapping()
 			).getComparator();
 		}
 		return versionComparator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index f03391d78f..7df0a0c134 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1664 +1,1665 @@
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
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
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
 						entityBinding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator()
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
 					rootEntityBinding.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getHierarchyDetails().getCaching().getRegion();
 				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getHierarchyDetails().getCaching().getAccessType();
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
-		for ( AbstractPluralAttributeBinding model : metadata.getCollectionBindings() ) {
+		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a AbstractPluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"AbstractPluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			// TODO: Add AbstractPluralAttributeBinding.getCaching()
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				// TODO: is model.locateAttribute().getName() the collection's role??? For now, assuming it is
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
 			// TODO: is model.locateAttribute().getName() the collection's role??? For now, assuming it is
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
index db19b8ac2e..0313e266ce 100644
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
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
 
-	public Iterable<AbstractPluralAttributeBinding> getCollectionBindings();
+	public Iterable<PluralAttributeBinding> getCollectionBindings();
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
index ee5466a53f..dfe6194d35 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractPluralAttributeBinding.java
@@ -1,298 +1,295 @@
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
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.relational.Table;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractPluralAttributeBinding extends AbstractAttributeBinding implements PluralAttributeBinding {
-	private CollectionKey collectionKey;
-	private CollectionElement collectionElement;
+	private final CollectionKey collectionKey;
+	private final CollectionElement collectionElement;
 
 	private Table collectionTable;
 
 	private CascadeStyle cascadeStyle;
 	private FetchMode fetchMode;
 
 	private boolean extraLazy;
 	private boolean inverse;
 	private boolean mutable = true;
 	private boolean subselectLoadable;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String orderBy;
 	private String where;
 	private String referencedPropertyName;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 	private boolean orphanDelete;
 	private int batchSize = -1;
 	private boolean embedded = true;
 	private boolean optimisticLocked = true;
 	private Class collectionPersisterClass;
 	private final java.util.Map filters = new HashMap();
 	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
 
 	private CustomSQL customSQLInsert;
 	private CustomSQL customSQLUpdate;
 	private CustomSQL customSQLDelete;
 	private CustomSQL customSQLDeleteAll;
 
 	private String loaderName;
 
 	protected AbstractPluralAttributeBinding(
 			AttributeBindingContainer container,
 			PluralAttribute attribute,
 			CollectionElementNature collectionElementNature) {
 		super( container, attribute );
+		this.collectionKey = new CollectionKey( this );
 		this.collectionElement = interpretNature( collectionElementNature );
 	}
 
 	private CollectionElement interpretNature(CollectionElementNature collectionElementNature) {
 		switch ( collectionElementNature ) {
 			case BASIC: {
 				return new BasicCollectionElement( this );
 			}
 			case COMPOSITE: {
 				return new CompositeCollectionElement( this );
 			}
 			case ONE_TO_MANY: {
 				return new OneToManyCollectionElement( this );
 			}
 			case MANY_TO_MANY: {
 				return new ManyToManyCollectionElement( this );
 			}
 			case MANY_TO_ANY: {
 				return new ManyToAnyCollectionElement( this );
 			}
 			default: {
 				throw new AssertionFailure( "Unknown collection element nature : " + collectionElementNature );
 			}
 		}
 	}
 
 //	protected void initializeBinding(PluralAttributeBindingState state) {
 //		super.initialize( state );
 //		fetchMode = state.getFetchMode();
 //		extraLazy = state.isExtraLazy();
 //		collectionElement.setNodeName( state.getElementNodeName() );
 //		collectionElement.setTypeName( state.getElementTypeName() );
 //		inverse = state.isInverse();
 //		mutable = state.isMutable();
 //		subselectLoadable = state.isSubselectLoadable();
 //		if ( isSubselectLoadable() ) {
 //			getEntityBinding().setSubselectLoadableCollections( true );
 //		}
 //		cacheConcurrencyStrategy = state.getCacheConcurrencyStrategy();
 //		cacheRegionName = state.getCacheRegionName();
 //		orderBy = state.getOrderBy();
 //		where = state.getWhere();
 //		referencedPropertyName = state.getReferencedPropertyName();
 //		sorted = state.isSorted();
 //		comparator = state.getComparator();
 //		comparatorClassName = state.getComparatorClassName();
 //		orphanDelete = state.isOrphanDelete();
 //		batchSize = state.getBatchSize();
 //		embedded = state.isEmbedded();
 //		optimisticLocked = state.isOptimisticLocked();
 //		collectionPersisterClass = state.getCollectionPersisterClass();
 //		filters.putAll( state.getFilters() );
 //		synchronizedTables.addAll( state.getSynchronizedTables() );
 //		customSQLInsert = state.getCustomSQLInsert();
 //		customSQLUpdate = state.getCustomSQLUpdate();
 //		customSQLDelete = state.getCustomSQLDelete();
 //		customSQLDeleteAll = state.getCustomSQLDeleteAll();
 //		loaderName = state.getLoaderName();
 //	}
 
 	@Override
 	public boolean isAssociation() {
 		return collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_ANY
 				|| collectionElement.getCollectionElementNature() == CollectionElementNature.MANY_TO_MANY
 				|| collectionElement.getCollectionElementNature() == CollectionElementNature.ONE_TO_MANY;
 	}
 
 	public Table getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table collectionTable) {
 		this.collectionTable = collectionTable;
 	}
 
 	public CollectionKey getCollectionKey() {
 		return collectionKey;
 	}
 
-	public void setCollectionKey(CollectionKey collectionKey) {
-		this.collectionKey = collectionKey;
-	}
-
 	public CollectionElement getCollectionElement() {
 		return collectionElement;
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle() {
 		return cascadeStyle;
 	}
 
 	@Override
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
 		List<CascadeStyle> cascadeStyleList = new ArrayList<CascadeStyle>();
 		for ( CascadeStyle style : cascadeStyles ) {
 			if ( style != CascadeStyle.NONE ) {
 				cascadeStyleList.add( style );
 			}
 		}
 		if ( cascadeStyleList.isEmpty() ) {
 			cascadeStyle = CascadeStyle.NONE;
 		}
 		else if ( cascadeStyleList.size() == 1 ) {
 			cascadeStyle = cascadeStyleList.get( 0 );
 		}
 		else {
 			cascadeStyle = new CascadeStyle.MultipleCascadeStyle(
 					cascadeStyleList.toArray( new CascadeStyle[ cascadeStyleList.size() ] )
 			);
 		}
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	@Override
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	public boolean isExtraLazy() {
 		return extraLazy;
 	}
 
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName;
 	}
 
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public String getWhere() {
 		return where;
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	public Comparator getComparator() {
 		return comparator;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 
 	public boolean isOrphanDelete() {
 		return orphanDelete;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void addFilter(String name, String condition) {
 		filters.put( name, condition );
 	}
 
 	public java.util.Map getFilterMap() {
 		return filters;
 	}
 
 	public CustomSQL getCustomSQLInsert() {
 		return customSQLInsert;
 	}
 
 	public CustomSQL getCustomSQLUpdate() {
 		return customSQLUpdate;
 	}
 
 	public CustomSQL getCustomSQLDelete() {
 		return customSQLDelete;
 	}
 
 	public CustomSQL getCustomSQLDeleteAll() {
 		return customSQLDeleteAll;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBindingContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBindingContainer.java
index fd1b8599b6..c208c3911c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBindingContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBindingContainer.java
@@ -1,126 +1,136 @@
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
 
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
  * Common contract for {@link EntityBinding} and {@link ComponentAttributeBinding} in so far as they are both
  * containers for {@link AttributeBinding} descriptors
  *
  * @author Steve Ebersole
  */
 public interface AttributeBindingContainer {
 	/**
 	 * Obtain the path base of this container.  Intended to help uniquely identify each attribute binding.
 	 *
 	 * @return The path base for this container.
 	 */
 	public String getPathBase();
 
 	/**
 	 * Obtain the underlying domain attribute container.
 	 *
 	 * @return The attribute container
 	 */
 	public AttributeContainer getAttributeContainer();
 
 	/**
 	 * Obtain all attribute bindings
 	 *
 	 * @return All attribute bindings
 	 */
 	public Iterable<AttributeBinding> attributeBindings();
 
 	/**
 	 * Locate a specific attribute binding, by its local name.
 	 *
 	 * @param name The name of the attribute, local to this container.
 	 *
 	 * @return The attribute binding.
 	 */
 	public AttributeBinding locateAttributeBinding(String name);
 
 	/**
 	 * Factory method for basic attribute bindings.
 	 *
 	 * @param attribute The attribute for which to make a binding.
 	 *
 	 * @return The attribute binding instance.
 	 */
 	public BasicAttributeBinding makeBasicAttributeBinding(SingularAttribute attribute);
 
 	/**
 	 * Factory method for component attribute bindings.
 	 *
 	 * @param attribute The attribute for which to make a binding.
 	 *
 	 * @return The attribute binding instance.
 	 */
 	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute);
 
 	/**
 	 * Factory method for many-to-one attribute bindings.
 	 *
 	 * @param attribute The attribute for which to make a binding.
 	 *
 	 * @return The attribute binding instance.
 	 */
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute);
 
 	/**
 	 * Factory method for bag attribute bindings.
 	 *
 	 * @param attribute The attribute for which to make a binding.
 	 * @param nature The nature of the collection elements.
 	 *
 	 * @return The attribute binding instance.
 	 */
 	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature);
 
 	/**
+	 * Factory method for bag attribute bindings.
+	 *
+	 * @param attribute The attribute for which to make a binding.
+	 * @param nature The nature of the collection elements.
+	 *
+	 * @return The attribute binding instance.
+	 */
+	public SetBinding makeSetAttributeBinding(PluralAttribute attribute, CollectionElementNature nature);
+
+	/**
 	 * Seeks out the entity binding that is the root of this component path.
 	 *
 	 * @return The entity binding
 	 */
 	public EntityBinding seekEntityBinding();
 
 	/**
 	 * Obtain the {@link Class} reference for this attribute container.  Generally this is used to perform reflection
 	 * on the attributes.
 	 *
 	 * @return The {@link Class} reference
 	 */
 	public Class<?> getClassReference();
 
 	/**
 	 * Obtain the meta-attribute context for this container.
 	 *
 	 * @return The meta-attribute context.
 	 */
 	public MetaAttributeContext getMetaAttributeContext();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ComponentAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ComponentAttributeBinding.java
index 2014d37baf..b64b79eabe 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ComponentAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/ComponentAttributeBinding.java
@@ -1,154 +1,164 @@
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
 import java.util.Map;
 
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Component;
 import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.domain.PluralAttributeNature;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 
 /**
  * @author Steve Ebersole
  */
 public class ComponentAttributeBinding extends AbstractSingularAttributeBinding implements AttributeBindingContainer {
 	private final String path;
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 	private SingularAttribute parentReference;
 	private MetaAttributeContext metaAttributeContext;
 
 	public ComponentAttributeBinding(AttributeBindingContainer container, SingularAttribute attribute) {
 		super( container, attribute );
 		this.path = container.getPathBase() + '.' + attribute.getName();
 	}
 
 	@Override
 	public EntityBinding seekEntityBinding() {
 		return getContainer().seekEntityBinding();
 	}
 
 	@Override
 	public String getPathBase() {
 		return path;
 	}
 
 	@Override
 	public AttributeContainer getAttributeContainer() {
 		return getComponent();
 	}
 
 	public Component getComponent() {
 		return (Component) getAttribute().getSingularAttributeType();
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return false;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
 
 	@Override
 	public AttributeBinding locateAttributeBinding(String name) {
 		return attributeBindingMap.get( name );
 	}
 
 	@Override
 	public Iterable<AttributeBinding> attributeBindings() {
 		return attributeBindingMap.values();
 	}
 
 	@Override
 	protected void checkValueBinding() {
 		// do nothing here...
 	}
 
 	@Override
 	public BasicAttributeBinding makeBasicAttributeBinding(SingularAttribute attribute) {
 		final BasicAttributeBinding binding = new BasicAttributeBinding(
 				this,
 				attribute,
 				isNullable(),
 				isAlternateUniqueKey() // todo : is this accurate?
 		);
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	protected void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
 		// todo : hook this into the EntityBinding notion of "entity referencing attribute bindings"
 		attributeBindingMap.put( name, attributeBinding );
 	}
 
 	@Override
 	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
 		final ComponentAttributeBinding binding = new ComponentAttributeBinding( this, attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
 		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this, attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+		Helper.checkPluralAttributeNature( attribute, PluralAttributeNature.BAG );
 		final BagBinding binding = new BagBinding( this, attribute, nature );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
+	public SetBinding makeSetAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+		Helper.checkPluralAttributeNature( attribute, PluralAttributeNature.SET );
+		final SetBinding binding = new SetBinding( this, attribute, nature );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
 	public Class<?> getClassReference() {
 		return getComponent().getClassReference();
 	}
 
 	public SingularAttribute getParentReference() {
 		return parentReference;
 	}
 
 	public void setParentReference(SingularAttribute parentReference) {
 		this.parentReference = parentReference;
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		// todo : not sure the correct thing to return here since it essentially relies on the simple sub-attributes.
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index 8144933668..6d8e083443 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,467 +1,477 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.domain.PluralAttributeNature;
 import org.hibernate.metamodel.domain.SingularAttribute;
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
 public class EntityBinding implements AttributeBindingContainer {
 	private final EntityBinding superEntityBinding;
 	private final HierarchyDetails hierarchyDetails;
 
 	private Entity entity;
 	private TableSpecification primaryTable;
     private String primaryTableName;
 	private Map<String, TableSpecification> secondaryTables = new HashMap<String, TableSpecification>();
 
 	private Value<Class<?>> proxyInterfaceType;
 
 	private String jpaEntityName;
 
 	private Class<? extends EntityPersister> customEntityPersisterClass;
 	private Class<? extends EntityTuplizer> customEntityTuplizerClass;
 
 	private String discriminatorMatchValue;
 
 	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>();
 	private Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
 	private MetaAttributeContext metaAttributeContext;
 
 	private boolean lazy;
 	private boolean mutable;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private boolean hasSubselectLoadableCollections;
 
 	private Boolean isAbstract;
 
 	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 
 	/**
 	 * Used to instantiate the EntityBinding for an entity that is the root of an inheritance hierarchy
 	 *
 	 * @param inheritanceType The inheritance type for the hierarchy
 	 * @param entityMode The entity mode used in this hierarchy.
 	 */
 	public EntityBinding(InheritanceType inheritanceType, EntityMode entityMode) {
 		this.superEntityBinding = null;
 		this.hierarchyDetails = new HierarchyDetails( this, inheritanceType, entityMode );
 	}
 
 	/**
 	 * Used to instantiate the EntityBinding for an entity that is a subclass (sub-entity) in an inheritance hierarchy
 	 *
 	 * @param superEntityBinding The entity binding of this binding's super
 	 */
 	public EntityBinding(EntityBinding superEntityBinding) {
 		this.superEntityBinding = superEntityBinding;
 		this.hierarchyDetails = superEntityBinding.getHierarchyDetails();
 	}
 
 	public HierarchyDetails getHierarchyDetails() {
 		return hierarchyDetails;
 	}
 
 	public EntityBinding getSuperEntityBinding() {
 		return superEntityBinding;
 	}
 
 	public boolean isRoot() {
 		return superEntityBinding == null;
 	}
 
 	public Entity getEntity() {
 		return entity;
 	}
 
 	public void setEntity(Entity entity) {
 		this.entity = entity;
 	}
 
 	public TableSpecification getPrimaryTable() {
 		return primaryTable;
 	}
 
 	public void setPrimaryTable(TableSpecification primaryTable) {
 		this.primaryTable = primaryTable;
 	}
 
     public TableSpecification locateTable(String tableName) {
         if ( tableName == null || tableName.equals( getPrimaryTableName() ) ) {
             return primaryTable;
         }
         TableSpecification tableSpec = secondaryTables.get( tableName );
         if ( tableSpec == null ) {
             throw new AssertionFailure(
                     String.format(
                             "Unable to find table %s amongst tables %s",
                             tableName,
                             secondaryTables.keySet()
                     )
             );
         }
         return tableSpec;
     }
     public String getPrimaryTableName() {
         return primaryTableName;
     }
 
     public void setPrimaryTableName(String primaryTableName) {
         this.primaryTableName = primaryTableName;
     }
 
 	public void addSecondaryTable(String tableName, TableSpecification table) {
 		secondaryTables.put( tableName, table );
 	}
 
 	public boolean isVersioned() {
 		return getHierarchyDetails().getVersioningAttributeBinding() != null;
 	}
 
 	public String getDiscriminatorMatchValue() {
 		return discriminatorMatchValue;
 	}
 
 	public void setDiscriminatorMatchValue(String discriminatorMatchValue) {
 		this.discriminatorMatchValue = discriminatorMatchValue;
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
 
 	@Override
 	public EntityBinding seekEntityBinding() {
 		return this;
 	}
 
 	@Override
 	public String getPathBase() {
 		return getEntity().getName();
 	}
 
 	@Override
 	public Class<?> getClassReference() {
 		return getEntity().getClassReference();
 	}
 
 	@Override
 	public AttributeContainer getAttributeContainer() {
 		return getEntity();
 	}
 
 	protected void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
 		if ( SingularAssociationAttributeBinding.class.isInstance( attributeBinding ) ) {
 			entityReferencingAttributeBindings.add( (SingularAssociationAttributeBinding) attributeBinding );
 		}
 		attributeBindingMap.put( name, attributeBinding );
 	}
 
 	@Override
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
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
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
 
 	public void addSynchronizedTableNames(java.util.Collection<String> synchronizedTableNames) {
 		this.synchronizedTableNames.addAll( synchronizedTableNames );
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
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "EntityBinding" );
 		sb.append( "{entity=" ).append( entity != null ? entity.getName() : "not set" );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	@Override
 	public BasicAttributeBinding makeBasicAttributeBinding(SingularAttribute attribute) {
 		return makeSimpleAttributeBinding( attribute, false, false );
 	}
 
 	private BasicAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute, boolean forceNonNullable, boolean forceUnique) {
 		final BasicAttributeBinding binding = new BasicAttributeBinding(
 				this,
 				attribute,
 				forceNonNullable,
 				forceUnique
 		);
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
 		final ComponentAttributeBinding binding = new ComponentAttributeBinding( this, attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
 		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this, attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+		Helper.checkPluralAttributeNature( attribute, PluralAttributeNature.BAG );
 		final BagBinding binding = new BagBinding( this, attribute, nature );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
+	public SetBinding makeSetAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+		Helper.checkPluralAttributeNature( attribute, PluralAttributeNature.SET );
+		final SetBinding binding = new SetBinding( this, attribute, nature );
+		registerAttributeBinding( attribute.getName(), binding );
+		return binding;
+	}
+
+	@Override
 	public AttributeBinding locateAttributeBinding(String name) {
 		return attributeBindingMap.get( name );
 	}
 
 	@Override
 	public Iterable<AttributeBinding> attributeBindings() {
 		return attributeBindingMap.values();
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
 		return attributeBindings();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/Helper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/Helper.java
new file mode 100644
index 0000000000..1bd2427d81
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/Helper.java
@@ -0,0 +1,47 @@
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
+import org.hibernate.AssertionFailure;
+import org.hibernate.metamodel.domain.PluralAttribute;
+import org.hibernate.metamodel.domain.PluralAttributeNature;
+
+/**
+ * Helper utilities specific to the binding package.
+ *
+ * @author Steve Ebersole
+ */
+public class Helper {
+	public static void checkPluralAttributeNature(PluralAttribute attribute, PluralAttributeNature expected) {
+		if ( attribute.getNature() != expected ) {
+			throw new AssertionFailure(
+					String.format(
+							"Mismatched collection natures; expecting %s, but found %s",
+							expected.getName(),
+							attribute.getNature().getName()
+					)
+			);
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java
index 129d2574cf..348ddf3bf4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/PluralAttributeBinding.java
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
 package org.hibernate.metamodel.binding;
 
 import org.hibernate.metamodel.relational.Table;
+import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * @author Steve Ebersole
  */
 public interface PluralAttributeBinding extends AttributeBinding, AssociationAttributeBinding {
 	// todo : really it is the element (and/or index) that can be associative not the collection itself...
 
 	public CollectionKey getCollectionKey();
 
 	public CollectionElement getCollectionElement();
 
 	public Table getCollectionTable();
 
+	public boolean isMutable();
+
+	public String getCacheRegionName();
+
+	public String getCacheConcurrencyStrategy();
+
+	public Class<CollectionPersister> getCollectionPersisterClass();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SetBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SetBinding.java
new file mode 100644
index 0000000000..98a0b05d02
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SetBinding.java
@@ -0,0 +1,50 @@
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
+import java.util.Comparator;
+
+import org.hibernate.metamodel.domain.PluralAttribute;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SetBinding extends AbstractPluralAttributeBinding {
+	private Comparator comparator;
+
+	protected SetBinding(
+			AttributeBindingContainer container,
+			PluralAttribute attribute,
+			CollectionElementNature collectionElementNature) {
+		super( container, attribute, collectionElementNature );
+	}
+
+	public Comparator getComparator() {
+		return comparator;
+	}
+
+	public void setComparator(Comparator comparator) {
+		this.comparator = comparator;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/PluralAttributeNature.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/PluralAttributeNature.java
index 7d81937073..492299ad68 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/PluralAttributeNature.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/PluralAttributeNature.java
@@ -1,63 +1,64 @@
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
 package org.hibernate.metamodel.domain;
 
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 /**
  * Identifies the specific semantic of a plural valued attribute.
  *
  * @author Steve Ebersole
  */
 public enum PluralAttributeNature {
 	BAG( "bag", Collection.class ),
+	IDBAG( "idbag", Collection.class ),
 	SET( "set", Set.class ),
 	LIST( "list", List.class ),
 	MAP( "map", Map.class );
 
 	private final String name;
 	private final Class javaContract;
 	private final boolean indexed;
 
 	PluralAttributeNature(String name, Class javaContract) {
 		this.name = name;
 		this.javaContract = javaContract;
 		this.indexed = Map.class.isAssignableFrom( javaContract ) || List.class.isAssignableFrom( javaContract );
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Class getJavaContract() {
 		return javaContract;
 	}
 
 	public boolean isIndexed() {
 		return indexed;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
index 130f41222b..2e7f489e2c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
@@ -1,77 +1,77 @@
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
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
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
 
-	public void addCollection(AbstractPluralAttributeBinding collectionBinding);
+	public void addCollection(PluralAttributeBinding collectionBinding);
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/JandexHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/JandexHelper.java
index dfde432367..f921778f98 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/JandexHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/JandexHelper.java
@@ -1,366 +1,388 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import java.beans.Introspector;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationTarget;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.FieldInfo;
 import org.jboss.jandex.Index;
 import org.jboss.jandex.Indexer;
 import org.jboss.jandex.MethodInfo;
 import org.jboss.jandex.Type;
 
 import org.hibernate.AssertionFailure;
+import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Utility methods for working with the jandex annotation index.
  *
  * @author Hardy Ferentschik
  */
 public class JandexHelper {
 	private static final Map<String, Object> DEFAULT_VALUES_BY_ELEMENT = new HashMap<String, Object>();
 
 	/**
 	 * Retrieves a jandex annotation element value. If the value is {@code null}, the default value specified in the
 	 * annotation class is retrieved instead.
 	 * <p>
 	 * There are two special cases. {@code Class} parameters should be retrieved as strings (and then can later be
 	 * loaded) and enumerated values should be retrieved via {@link #getEnumValue(AnnotationInstance, String, Class)}.
 	 * </p>
 	 *
 	 * @param annotation the annotation containing the element with the supplied name
 	 * @param element the name of the element value to be retrieve
 	 * @param type the type of element to retrieve. The following types are supported:
 	 * <ul>
 	 * <li>Byte</li>
 	 * <li>Short</li>
 	 * <li>Integer</li>
 	 * <li>Character</li>
 	 * <li>Float</li>
 	 * <li>Double</li>
 	 * <li>Long</li>
 	 * <li>Boolean</li>
 	 * <li>String</li>
 	 * <li>AnnotationInstance</li>
 	 *
 	 * @return the value if not {@code null}, else the default value if not
 	 *         {@code null}, else {@code null}.
 	 *
 	 * @throws AssertionFailure in case the specified {@code type} is a class instance or the specified type causes a {@code ClassCastException}
 	 * when retrieving the value.
 	 */
 	@SuppressWarnings("unchecked")
 	public static <T> T getValue(AnnotationInstance annotation, String element, Class<T> type) throws AssertionFailure {
 		if ( Class.class.equals( type ) ) {
 			throw new AssertionFailure(
 					"Annotation parameters of type Class should be retrieved as strings (fully qualified class names)"
 			);
 		}
 
 		// try getting the untyped value from Jandex
 		AnnotationValue annotationValue = annotation.value( element );
 
 		try {
 			if ( annotationValue != null ) {
 				return explicitAnnotationParameter( annotationValue, type );
 			}
 			else {
 				return defaultAnnotationParameter( getDefaultValue( annotation, element ), type );
 			}
 		}
 		catch ( ClassCastException e ) {
 			throw new AssertionFailure(
 					String.format(
 							"the annotation property %s of annotation %s is not of type %s",
 							element,
 							annotation.name(),
 							type.getName()
 					)
 			);
 		}
 	}
 
 	/**
 	 * Retrieves a jandex annotation element value, converting it to the supplied enumerated type.  If the value is
 	 * <code>null</code>, the default value specified in the annotation class is retrieved instead.
 	 *
 	 * @param <T> an enumerated type
 	 * @param annotation the annotation containing the enumerated element with the supplied name
 	 * @param element the name of the enumerated element value to be retrieve
 	 * @param type the type to which to convert the value before being returned
 	 *
 	 * @return the value converted to the supplied enumerated type if the value is not <code>null</code>, else the default value if
 	 *         not <code>null</code>, else <code>null</code>.
 	 *
 	 * @see #getValue(AnnotationInstance, String, Class)
 	 */
 	@SuppressWarnings("unchecked")
 	public static <T extends Enum<T>> T getEnumValue(AnnotationInstance annotation, String element, Class<T> type) {
 		AnnotationValue val = annotation.value( element );
 		if ( val == null ) {
 			return (T) getDefaultValue( annotation, element );
 		}
 		return Enum.valueOf( type, val.asEnum() );
 	}
 
 	/**
 	 * Expects a method or field annotation target and returns the property name for this target
 	 *
 	 * @param target the annotation target
 	 *
 	 * @return the property name of the target. For a field it is the field name and for a method name it is
 	 *         the method name stripped of 'is', 'has' or 'get'
 	 */
 	public static String getPropertyName(AnnotationTarget target) {
 		if ( !( target instanceof MethodInfo || target instanceof FieldInfo ) ) {
 			throw new AssertionFailure( "Unexpected annotation target " + target.toString() );
 		}
 
 		if ( target instanceof FieldInfo ) {
 			return ( (FieldInfo) target ).name();
 		}
 		else {
 			final String methodName = ( (MethodInfo) target ).name();
 			String propertyName;
 			if ( methodName.startsWith( "is" ) ) {
 				propertyName = Introspector.decapitalize( methodName.substring( 2 ) );
 			}
 			else if ( methodName.startsWith( "has" ) ) {
 				propertyName = Introspector.decapitalize( methodName.substring( 3 ) );
 			}
 			else if ( methodName.startsWith( "get" ) ) {
 				propertyName = Introspector.decapitalize( methodName.substring( 3 ) );
 			}
 			else {
 				throw new AssertionFailure( "Expected a method following the Java Bean notation" );
 			}
 			return propertyName;
 		}
 	}
 
 	/**
 	 * @param classInfo the class info from which to retrieve the annotation instance
 	 * @param annotationName the annotation to retrieve from the class info
 	 *
 	 * @return the single annotation defined on the class or {@code null} in case the annotation is not specified at all
 	 *
 	 * @throws org.hibernate.AssertionFailure in case there is there is more than one annotation of this type.
 	 */
 	public static AnnotationInstance getSingleAnnotation(ClassInfo classInfo, DotName annotationName)
 			throws AssertionFailure {
 		return getSingleAnnotation( classInfo.annotations(), annotationName );
 	}
 
 	/**
 	 * @param annotations List of annotation instances keyed against their dot name.
 	 * @param annotationName the annotation to retrieve from map
 	 *
 	 * @return the single annotation of the specified dot name or {@code null} in case the annotation is not specified at all
 	 *
 	 * @throws org.hibernate.AssertionFailure in case there is there is more than one annotation of this type.
 	 */
 	public static AnnotationInstance getSingleAnnotation(Map<DotName, List<AnnotationInstance>> annotations, DotName annotationName)
 			throws AssertionFailure {
 		List<AnnotationInstance> annotationList = annotations.get( annotationName );
 		if ( annotationList == null ) {
 			return null;
 		}
 		else if ( annotationList.size() == 1 ) {
 			return annotationList.get( 0 );
 		}
 		else {
 			throw new AssertionFailure(
 					"Found more than one instance of the annotation "
 							+ annotationList.get( 0 ).name().toString()
 							+ ". Expected was one."
 			);
 		}
 	}
 
 	/**
 	 * Creates a jandex index for the specified classes
 	 *
 	 * @param classLoaderService class loader service
 	 * @param classes the classes to index
 	 *
 	 * @return an annotation repository w/ all the annotation discovered in the specified classes
 	 */
 	public static Index indexForClass(ClassLoaderService classLoaderService, Class<?>... classes) {
 		Indexer indexer = new Indexer();
 		for ( Class<?> clazz : classes ) {
 			InputStream stream = classLoaderService.locateResourceStream(
 					clazz.getName().replace( '.', '/' ) + ".class"
 			);
 			try {
 				indexer.index( stream );
 			}
 			catch ( IOException e ) {
 				StringBuilder builder = new StringBuilder();
 				builder.append( "[" );
 				int count = 0;
 				for ( Class<?> c : classes ) {
 					builder.append( c.getName() );
 					if ( count < classes.length - 1 ) {
 						builder.append( "," );
 					}
 					count++;
 				}
 				builder.append( "]" );
 				throw new HibernateException( "Unable to create annotation index for " + builder.toString() );
 			}
 		}
 		return indexer.complete();
 	}
 
 	public static Map<DotName, List<AnnotationInstance>> getMemberAnnotations(ClassInfo classInfo, String name) {
 		if ( classInfo == null ) {
 			throw new IllegalArgumentException( "classInfo cannot be null" );
 		}
 
 		if ( name == null ) {
 			throw new IllegalArgumentException( "name cannot be null" );
 		}
 
 		Map<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
 		for ( List<AnnotationInstance> annotationList : classInfo.annotations().values() ) {
 			for ( AnnotationInstance instance : annotationList ) {
 				String targetName = null;
 				if ( instance.target() instanceof FieldInfo ) {
 					targetName = ( (FieldInfo) instance.target() ).name();
 				}
 				else if ( instance.target() instanceof MethodInfo ) {
 					targetName = ( (MethodInfo) instance.target() ).name();
 				}
 				if ( targetName != null && name.equals( targetName ) ) {
 					addAnnotationToMap( instance, annotations );
 				}
 			}
 		}
 		return annotations;
 	}
 
 	public static Map<DotName, List<AnnotationInstance>> getTypeAnnotations(ClassInfo classInfo) {
 		if ( classInfo == null ) {
 			throw new IllegalArgumentException( "classInfo cannot be null" );
 		}
 
 		Map<DotName, List<AnnotationInstance>> annotations = new HashMap<DotName, List<AnnotationInstance>>();
 		for ( List<AnnotationInstance> annotationList : classInfo.annotations().values() ) {
 			for ( AnnotationInstance instance : annotationList ) {
 				if ( instance.target() instanceof ClassInfo ) {
 					addAnnotationToMap( instance, annotations );
 				}
 			}
 		}
 		return annotations;
 	}
 
 	public static void addAnnotationToMap(AnnotationInstance instance, Map<DotName, List<AnnotationInstance>> annotations) {
 		DotName dotName = instance.name();
 		List<AnnotationInstance> list;
 		if ( annotations.containsKey( dotName ) ) {
 			list = annotations.get( dotName );
 		}
 		else {
 			list = new ArrayList<AnnotationInstance>();
 			annotations.put( dotName, list );
 		}
 		list.add( instance );
 	}
 
 	private JandexHelper() {
 	}
 
 	private static Object getDefaultValue(AnnotationInstance annotation, String element) {
 		String name = annotation.name().toString();
 		String fqElement = name + '.' + element;
 		Object val = DEFAULT_VALUES_BY_ELEMENT.get( fqElement );
 		if ( val != null ) {
 			return val;
 		}
 		try {
 			val = Index.class.getClassLoader().loadClass( name ).getMethod( element ).getDefaultValue();
 			DEFAULT_VALUES_BY_ELEMENT.put( fqElement, val );
 			return val == null ? null : val;
 		}
 		catch ( RuntimeException error ) {
 			throw error;
 		}
 		catch ( Exception error ) {
 			throw new AssertionFailure(
 					String.format( "The annotation %s does not define a parameter '%s'", name, element ),
 					error
 			);
 		}
 	}
 
 	private static <T> T defaultAnnotationParameter(Object defaultValue, Class<T> type) {
 		Object returnValue = defaultValue;
 
 		// resolve some mismatches between what's stored in jandex and what the defaults are for annotations
 		// in case of nested annotation arrays, jandex returns arrays of AnnotationInstances, hence we return
 		// an empty array of this type here
 		if ( defaultValue.getClass().isArray() && defaultValue.getClass().getComponentType().isAnnotation() ) {
 			returnValue = new AnnotationInstance[0];
 		}
 		return type.cast( returnValue );
 	}
 
 	private static <T> T explicitAnnotationParameter(AnnotationValue annotationValue, Class<T> type) {
 		Object returnValue = annotationValue.value();
 
 		// if the jandex return type is Type we actually try to retrieve a class parameter
 		// for our purposes we just return the fqcn of the class
 		if ( returnValue instanceof Type ) {
 			returnValue = ( (Type) returnValue ).name().toString();
 		}
 
 		// arrays we have to handle explicitly
 		if ( type.isArray() ) {
 			AnnotationValue[] values = (AnnotationValue[]) returnValue;
 			Class<?> componentType = type.getComponentType();
 			Object[] arr = (Object[]) Array.newInstance( componentType, values.length );
 			for ( int i = 0; i < values.length; i++ ) {
 				arr[i] = componentType.cast( values[i].value() );
 			}
 			returnValue = arr;
 		}
 
 		return type.cast( returnValue );
 	}
+
+	public static AnnotationInstance locatePojoTuplizerAnnotation(ClassInfo classInfo) {
+		final AnnotationInstance tuplizersAnnotation = getSingleAnnotation(
+				classInfo, HibernateDotNames.TUPLIZERS
+		);
+		if ( tuplizersAnnotation == null ) {
+			return null;
+		}
+
+		AnnotationInstance[] annotations = getValue(
+				tuplizersAnnotation,
+				"value",
+				AnnotationInstance[].class
+		);
+		for ( AnnotationInstance tuplizerAnnotation : annotations ) {
+			if ( EntityMode.valueOf( tuplizerAnnotation.value( "entityModeType" ).asEnum() ) == EntityMode.POJO ) {
+				return tuplizerAnnotation;
+			}
+		}
+		return null;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java
index 699804367c..c0b31bbf18 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java
@@ -1,191 +1,204 @@
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
 import java.util.Collections;
 import java.util.List;
 
+import org.jboss.jandex.AnnotationInstance;
+
 import org.hibernate.internal.util.Value;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.BasicAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SingularAttributeSourceImpl;
 import org.hibernate.metamodel.source.annotations.attribute.ToOneAttributeSourceImpl;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.ComponentAttributeSource;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 
 /**
  * Annotation backed implementation of {@code ComponentAttributeSource}.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
 	private final EmbeddableClass embeddableClass;
 	private final Value<Class<?>> classReference;
 	private final String path;
 
 	public ComponentAttributeSourceImpl(EmbeddableClass embeddableClass) {
 		this.embeddableClass = embeddableClass;
 		this.classReference = new Value<Class<?>>( embeddableClass.getClass() );
 		String tmpPath = embeddableClass.getEmbeddedAttributeName();
 		ConfiguredClass parent = embeddableClass.getParent();
 		while ( parent != null && parent instanceof EmbeddableClass ) {
 			tmpPath = ( (EmbeddableClass) parent ).getEmbeddedAttributeName() + "." + tmpPath;
 		}
 		path = tmpPath;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.COMPONENT;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public String getClassName() {
 		return embeddableClass.getClass().getName();
 	}
 
 	@Override
 	public Value<Class<?>> getClassReference() {
 		return classReference;
 	}
 
 	@Override
 	public String getName() {
 		return embeddableClass.getEmbeddedAttributeName();
 	}
 
 	@Override
+	public String getExplicitTuplizerClassName() {
+		String customTuplizer = null;
+		final AnnotationInstance pojoTuplizerAnnotation = JandexHelper.locatePojoTuplizerAnnotation( embeddableClass.getClassInfo() );
+		if ( pojoTuplizerAnnotation != null ) {
+			customTuplizer = pojoTuplizerAnnotation.value( "impl" ).asString();
+		}
+		return customTuplizer;
+	}
+
+	@Override
 	public String getPropertyAccessorName() {
 		return embeddableClass.getClassAccessType().toString().toLowerCase();
 	}
 
 	@Override
 	public LocalBindingContext getLocalBindingContext() {
 		return embeddableClass.getLocalBindingContext();
 	}
 
 	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeList = new ArrayList<AttributeSource>();
 		for ( BasicAttribute attribute : embeddableClass.getSimpleAttributes() ) {
 			attributeList.add( new SingularAttributeSourceImpl( attribute ) );
 		}
 		for ( EmbeddableClass component : embeddableClass.getEmbeddedClasses().values() ) {
 			attributeList.add( new ComponentAttributeSourceImpl( component ) );
 		}
 		for ( AssociationAttribute associationAttribute : embeddableClass.getAssociationAttributes() ) {
 			attributeList.add( new ToOneAttributeSourceImpl( associationAttribute ) );
 		}
 		return attributeList;
 	}
 
 	@Override
 	public String getPath() {
 		return path;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		// not relevant for annotations
 		return Collections.emptySet();
 	}
 
 	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		// none, they are defined on the simple sub-attributes
 		return null;
 	}
 
 	@Override
 	public String getParentReferenceAttributeName() {
 		// see HHH-6501
 		return null;
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		// probably need to check for @Target in EmbeddableClass (HF)
 		return null;
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return true;
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return true;
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazy() {
 		// todo : implement
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		// todo : implement
 		return true;
 	}
 
 	@Override
 	public boolean areValuesIncludedInInsertByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesIncludedInUpdateByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesNullableByDefault() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
index 927a482005..d183e810cb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
@@ -1,790 +1,766 @@
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
 
+import javax.persistence.AccessType;
+import javax.persistence.DiscriminatorType;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
-import javax.persistence.AccessType;
-import javax.persistence.DiscriminatorType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
-import org.hibernate.EntityMode;
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
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.annotations.attribute.ColumnValues;
-import org.hibernate.metamodel.source.annotations.attribute.DerivedValueSourceImpl;
 import org.hibernate.metamodel.source.annotations.attribute.FormulaValue;
 import org.hibernate.metamodel.source.binder.ConstraintSource;
-import org.hibernate.metamodel.source.binder.DerivedValueSource;
 import org.hibernate.metamodel.source.binder.TableSource;
 
 /**
  * Represents an entity or mapped superclass configured via annotations/orm-xml.
  *
  * @author Hardy Ferentschik
  */
 public class EntityClass extends ConfiguredClass {
 	private final IdType idType;
 	private final InheritanceType inheritanceType;
 
 	private final String explicitEntityName;
 	private final String customLoaderQueryName;
 	private final List<String> synchronizedTableNames;
 	private final String customTuplizer;
 	private final int batchSize;
 
 	private final TableSource primaryTableSource;
 	private final Set<TableSource> secondaryTableSources;
 	private final Set<ConstraintSource> constraintSources;
 
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
 
 	private ColumnValues discriminatorColumnValues;
     private FormulaValue discriminatorFormula;
 	private Class<?> discriminatorType;
 	private String discriminatorMatchValue;
 	private boolean isDiscriminatorForced = true;
 	private boolean isDiscriminatorIncludedInSql = true;
 
 
 	public EntityClass(
 			ClassInfo classInfo,
 			EntityClass parent,
 			AccessType hierarchyAccessType,
 			InheritanceType inheritanceType,
 			AnnotationBindingContext context) {
 		super( classInfo, hierarchyAccessType, parent, context );
 		this.inheritanceType = inheritanceType;
 		this.idType = determineIdType();
 		boolean hasOwnTable = definesItsOwnTable();
 		this.explicitEntityName = determineExplicitEntityName();
 		this.constraintSources = new HashSet<ConstraintSource>();
 
 		if ( hasOwnTable ) {
 			this.primaryTableSource = createTableSource(
 					JandexHelper.getSingleAnnotation(
 							getClassInfo(),
 							JPADotNames.TABLE
 					)
 			);
 		}
 		else {
 			this.primaryTableSource = null;
 		}
 
 		this.secondaryTableSources = createSecondaryTableSources();
 		this.customLoaderQueryName = determineCustomLoader();
 		this.synchronizedTableNames = determineSynchronizedTableNames();
 		this.customTuplizer = determineCustomTuplizer();
 		this.batchSize = determineBatchSize();
 
 		processHibernateEntitySpecificAnnotations();
 		processCustomSqlAnnotations();
 		processProxyGeneration();
 
 		processDiscriminator();
 	}
 
 	public ColumnValues getDiscriminatorColumnValues() {
 		return discriminatorColumnValues;
 	}
 
     public FormulaValue getDiscriminatorFormula() {
         return discriminatorFormula;
     }
 
 	public Class<?> getDiscriminatorType() {
 		return discriminatorType;
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
 
 	public TableSource getPrimaryTableSource() {
 		if ( definesItsOwnTable() ) {
 			return primaryTableSource;
 		}
 		else {
 			return ( (EntityClass) getParent() ).getPrimaryTableSource();
 		}
 	}
 
 	public Set<TableSource> getSecondaryTableSources() {
 		return secondaryTableSources;
 	}
 
 	public Set<ConstraintSource> getConstraintSources() {
 		return constraintSources;
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
 
 	public boolean isDiscriminatorForced() {
 		return isDiscriminatorForced;
 	}
 
 	public boolean isDiscriminatorIncludedInSql() {
 		return isDiscriminatorIncludedInSql;
 	}
 
 	private String determineExplicitEntityName() {
 		final AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.ENTITY
 		);
 		return JandexHelper.getValue( jpaEntityAnnotation, "name", String.class );
 	}
 
 
 	private boolean definesItsOwnTable() {
 		return !InheritanceType.SINGLE_TABLE.equals( inheritanceType ) || isEntityRoot();
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
 
 	private void processDiscriminator() {
 		if ( !InheritanceType.SINGLE_TABLE.equals( inheritanceType ) ) {
 			return;
 		}
 
 		final AnnotationInstance discriminatorValueAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.DISCRIMINATOR_VALUE
 		);
 		if ( discriminatorValueAnnotation != null ) {
 			this.discriminatorMatchValue = discriminatorValueAnnotation.value().asString();
 		}
 
 		final AnnotationInstance discriminatorColumnAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), JPADotNames.DISCRIMINATOR_COLUMN
 		);
 
         final AnnotationInstance discriminatorFormulaAnnotation = JandexHelper.getSingleAnnotation(
                 getClassInfo(),
                 HibernateDotNames.DISCRIMINATOR_FORMULA
         );
 
 
 		Class<?> type = String.class; // string is the discriminator default
         if ( discriminatorFormulaAnnotation != null ) {
             String expression = JandexHelper.getValue( discriminatorFormulaAnnotation, "value", String.class );
             discriminatorFormula = new FormulaValue( getPrimaryTableSource().getExplicitTableName(), expression );
         }
          discriminatorColumnValues = new ColumnValues( null ); //(stliu) give null here, will populate values below
             discriminatorColumnValues.setNullable( false ); // discriminator column cannot be null
 		if ( discriminatorColumnAnnotation != null ) {
 
 			DiscriminatorType discriminatorType = Enum.valueOf(
 					DiscriminatorType.class, discriminatorColumnAnnotation.value( "discriminatorType" ).asEnum()
 			);
 			switch ( discriminatorType ) {
 				case STRING: {
 					type = String.class;
 					break;
 				}
 				case CHAR: {
 					type = Character.class;
 					break;
 				}
 				case INTEGER: {
 					type = Integer.class;
 					break;
 				}
 				default: {
 					throw new AnnotationException( "Unsupported discriminator type: " + discriminatorType );
 				}
 			}
 
 			discriminatorColumnValues.setName(
 					JandexHelper.getValue(
 							discriminatorColumnAnnotation,
 							"name",
 							String.class
 					)
 			);
 			discriminatorColumnValues.setLength(
 					JandexHelper.getValue(
 							discriminatorColumnAnnotation,
 							"length",
 							Integer.class
 					)
 			);
             discriminatorColumnValues.setColumnDefinition(
 						JandexHelper.getValue(
                                 discriminatorColumnAnnotation,
                                 "columnDefinition",
                                 String.class
                         )
 				);
 		}
 		discriminatorType = type;
 
 		AnnotationInstance discriminatorOptionsAnnotation = JandexHelper.getSingleAnnotation(
 				getClassInfo(), HibernateDotNames.DISCRIMINATOR_OPTIONS
 		);
 		if ( discriminatorOptionsAnnotation != null ) {
 			isDiscriminatorForced = discriminatorOptionsAnnotation.value( "force" ).asBoolean();
 			isDiscriminatorIncludedInSql = discriminatorOptionsAnnotation.value( "insert" ).asBoolean();
 		}
 		else {
 			isDiscriminatorForced = false;
 			isDiscriminatorIncludedInSql = true;
 		}
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
 					? getLocalBindingContext().getMappingDefaults().getCacheAccessType()
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
 		switch ( getLocalBindingContext().getMetadataImplementor().getOptions().getSharedCacheMode() ) {
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
 				getLocalBindingContext().getMappingDefaults().getCacheAccessType(),
 				true
 		);
 	}
 
 	/**
      * todo see {@code Binder#createTable}
 	 * @param tableAnnotation a annotation instance, either {@link javax.persistence.Table} or {@link javax.persistence.SecondaryTable}
 	 *
 	 * @return A table source for the specified annotation instance
 	 */
 	private TableSource createTableSource(AnnotationInstance tableAnnotation) {
 		String schema = null;
 		String catalog = null;
 		if ( tableAnnotation != null ) {
             schema = JandexHelper.getValue( tableAnnotation, "schema", String.class );
             catalog = JandexHelper.getValue( tableAnnotation, "catalog", String.class );
 		}
 		// process the table name
 		String tableName = null;
 		String logicalTableName = null;
 
 		if ( tableAnnotation != null ) {
 			logicalTableName = JandexHelper.getValue( tableAnnotation, "name", String.class );
 			if ( StringHelper.isNotEmpty( logicalTableName ) ) {
                 tableName = logicalTableName;
 			}
 			createUniqueConstraints( tableAnnotation, tableName );
 		}
 
 		TableSourceImpl tableSourceImpl;
 		if ( tableAnnotation == null || JPADotNames.TABLE.equals( tableAnnotation.name() ) ) {
 			// for the main table @Table we use 'null' as logical name
 			tableSourceImpl = new TableSourceImpl( schema, catalog, tableName, null );
 		}
 		else {
 			// for secondary tables a name must be specified which is used as logical table name
 			tableSourceImpl = new TableSourceImpl( schema, catalog, tableName, logicalTableName );
 		}
 		return tableSourceImpl;
 	}
 
     private Set<TableSource> createSecondaryTableSources() {
 		Set<TableSource> secondaryTableSources = new HashSet<TableSource>();
         AnnotationInstance secondaryTables = JandexHelper.getSingleAnnotation(
                 getClassInfo(),
                 JPADotNames.SECONDARY_TABLES
         );
         AnnotationInstance secondaryTable = JandexHelper.getSingleAnnotation(
                 getClassInfo(),
                 JPADotNames.SECONDARY_TABLE
         );
 		// collect all @secondaryTable annotations
 		List<AnnotationInstance> secondaryTableAnnotations = new ArrayList<AnnotationInstance>();
         if ( secondaryTable != null ) {
             secondaryTableAnnotations.add(
                     secondaryTable
             );
         }
 
 		if ( secondaryTables != null ) {
 			secondaryTableAnnotations.addAll(
 					Arrays.asList(
 							JandexHelper.getValue( secondaryTables, "value", AnnotationInstance[].class )
 					)
 			);
 		}
 
 		// create table sources
 		for ( AnnotationInstance annotationInstance : secondaryTableAnnotations ) {
 			secondaryTableSources.add( createTableSource( annotationInstance ) );
 		}
 
 		return secondaryTableSources;
 	}
 
 
 	private void createUniqueConstraints(AnnotationInstance tableAnnotation, String tableName) {
 		AnnotationValue value = tableAnnotation.value( "uniqueConstraints" );
 		if ( value == null ) {
 			return;
 		}
 
 		AnnotationInstance[] uniqueConstraints = value.asNestedArray();
 		for ( AnnotationInstance unique : uniqueConstraints ) {
 			String name = unique.value( "name" ).asString();
 			String[] columnNames = unique.value( "columnNames" ).asStringArray();
 			UniqueConstraintSourceImpl uniqueConstraintSource =
 					new UniqueConstraintSourceImpl(
 							name, tableName, Arrays.asList( columnNames )
 					);
 			constraintSources.add( uniqueConstraintSource );
 		}
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
-		final AnnotationInstance pojoTuplizerAnnotation = locatePojoTuplizerAnnotation();
+		final AnnotationInstance pojoTuplizerAnnotation = JandexHelper.locatePojoTuplizerAnnotation( getClassInfo() );
 		if ( pojoTuplizerAnnotation != null ) {
 			customTuplizer = pojoTuplizerAnnotation.value( "impl" ).asString();
 		}
 		return customTuplizer;
 	}
 
-	private AnnotationInstance locatePojoTuplizerAnnotation() {
-		final AnnotationInstance tuplizersAnnotation = JandexHelper.getSingleAnnotation(
-				getClassInfo(), HibernateDotNames.TUPLIZERS
-		);
-		if ( tuplizersAnnotation == null ) {
-			return null;
-		}
-
-		AnnotationInstance[] annotations = JandexHelper.getValue(
-				tuplizersAnnotation,
-				"value",
-				AnnotationInstance[].class
-		);
-		for ( AnnotationInstance tuplizerAnnotation : annotations ) {
-			if ( EntityMode.valueOf( tuplizerAnnotation.value( "entityModeType" ).asEnum() ) == EntityMode.POJO ) {
-				return tuplizerAnnotation;
-			}
-		}
-		return null;
-	}
-
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
 
 	public String getDiscriminatorMatchValue() {
 		return discriminatorMatchValue;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/BasicPluralAttributeElementSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/BasicPluralAttributeElementSource.java
new file mode 100644
index 0000000000..bf1d055bd8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/BasicPluralAttributeElementSource.java
@@ -0,0 +1,34 @@
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
+import java.util.List;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface BasicPluralAttributeElementSource extends PluralAttributeElementSource {
+	public List<RelationalValueSource> getValueSources();
+	public ExplicitHibernateTypeSource getExplicitHibernateTypeSource();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
index be8b09b081..5f7515a511 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
@@ -1,789 +1,851 @@
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
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBindingContainer;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.CollectionElementNature;
 import org.hibernate.metamodel.binding.ComponentAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Component;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.relational.UniqueKey;
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
 
 		currentBindingContext = entitySource.getLocalBindingContext();
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
 
 		entityBinding.getHierarchyDetails().setCaching( entitySource.getCaching() );
 		entityBinding.getHierarchyDetails().setExplicitPolymorphism( entitySource.isExplicitPolymorphism() );
 		entityBinding.getHierarchyDetails().setOptimisticLockStyle( entitySource.getOptimisticLockStyle() );
 
 		entityBinding.setMutable( entitySource.isMutable() );
 		entityBinding.setWhereFilter( entitySource.getWhere() );
 		entityBinding.setRowId( entitySource.getRowId() );
 
 		return entityBinding;
 	}
 
 
 	private EntityBinding buildBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = superEntityBinding == null
 				? new EntityBinding( currentInheritanceType, currentHierarchyEntityMode )
 				: new EntityBinding( superEntityBinding );
 
 		final String entityName = entitySource.getEntityName();
 		final String className = currentHierarchyEntityMode == EntityMode.POJO ? entitySource.getClassName() : null;
 
 		final Entity entity = new Entity(
 				entityName,
 				className,
 				currentBindingContext.makeClassReference( className ),
 				superEntityBinding == null ? null : superEntityBinding.getEntity()
 		);
 		entityBinding.setEntity( entity );
 
 		entityBinding.setJpaEntityName( entitySource.getJpaEntityName() );
 
 		if ( currentHierarchyEntityMode == EntityMode.POJO ) {
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
 			entityBinding.setCustomEntityTuplizerClass(
 					currentBindingContext.<EntityTuplizer>locateClassByName(
 							customTuplizerClassName
 					)
 			);
 		}
 
 		final String customPersisterClassName = entitySource.getCustomPersisterClassName();
 		if ( customPersisterClassName != null ) {
 			entityBinding.setCustomEntityPersisterClass(
 					currentBindingContext.<EntityPersister>locateClassByName(
 							customPersisterClassName
 					)
 			);
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
 
 		entityBinding.setPrimaryTable( superEntityBinding.getPrimaryTable() );
         entityBinding.setPrimaryTableName( superEntityBinding.getPrimaryTableName() );
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
 		final BasicAttributeBinding idAttributeBinding = doBasicSingularAttributeBindingCreation(
 				identifierSource.getIdentifierAttributeSource(), entityBinding
 		);
 
 		entityBinding.getHierarchyDetails().getEntityIdentifier().setValueBinding( idAttributeBinding );
 		entityBinding.getHierarchyDetails()
 				.getEntityIdentifier()
 				.setIdGenerator( identifierSource.getIdentifierGeneratorDescriptor() );
 
 		final org.hibernate.metamodel.relational.Value relationalValue = idAttributeBinding.getValue();
 
 		if ( SimpleValue.class.isInstance( relationalValue ) ) {
 			if ( !Column.class.isInstance( relationalValue ) ) {
 				// this should never ever happen..
 				throw new AssertionFailure( "Simple-id was not a column." );
 			}
 			entityBinding.getPrimaryTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
 		}
 		else {
 			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
 				if ( Column.class.isInstance( subValue ) ) {
 					entityBinding.getPrimaryTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
 				}
 			}
 		}
 	}
 
 	private void bindVersion(EntityBinding entityBinding, RootEntitySource entitySource) {
 		final SingularAttributeSource versioningAttributeSource = entitySource.getVersioningAttributeSource();
 		if ( versioningAttributeSource == null ) {
 			return;
 		}
 
 		BasicAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
 				versioningAttributeSource, entityBinding
 		);
 		entityBinding.getHierarchyDetails().setVersioningAttributeBinding( attributeBinding );
 	}
 
 	private void bindDiscriminator(RootEntitySource entitySource, EntityBinding entityBinding) {
 		final DiscriminatorSource discriminatorSource = entitySource.getDiscriminatorSource();
 		if ( discriminatorSource == null ) {
 			return;
 		}
 
 		EntityDiscriminator discriminator = new EntityDiscriminator();
 		SimpleValue relationalValue = makeSimpleValue(
 				entityBinding,
 				discriminatorSource.getDiscriminatorRelationalValueSource()
 		);
 		discriminator.setBoundValue( relationalValue );
 
 		discriminator.getExplicitHibernateTypeDescriptor().setExplicitTypeName(
 				discriminatorSource.getExplicitHibernateTypeName() != null
 						? discriminatorSource.getExplicitHibernateTypeName()
 						: "string"
 		);
 
 		discriminator.setInserted( discriminatorSource.isInserted() );
 		discriminator.setForced( discriminatorSource.isForced() );
 
 		entityBinding.getHierarchyDetails().setEntityDiscriminator( discriminator );
 	}
 
 	private void bindDiscriminatorValue(SubclassEntitySource entitySource, EntityBinding entityBinding) {
 		final String discriminatorValue = entitySource.getDiscriminatorMatchValue();
 		if ( discriminatorValue == null ) {
 			return;
 		}
 		entityBinding.setDiscriminatorMatchValue( discriminatorValue );
 	}
 
 	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, AttributeBindingContainer attributeBindingContainer) {
 		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
 		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
 		// for now, simply assume all columns come from the base table....
 
 		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
 			if ( attributeSource.isSingular() ) {
 				final SingularAttributeSource singularAttributeSource = (SingularAttributeSource) attributeSource;
 				if ( singularAttributeSource.getNature() == SingularAttributeNature.COMPONENT ) {
 					bindComponent( (ComponentAttributeSource) singularAttributeSource, attributeBindingContainer );
 				}
 				else {
 					doBasicSingularAttributeBindingCreation( singularAttributeSource, attributeBindingContainer );
 				}
 			}
 			else {
 				bindPersistentCollection( (PluralAttributeSource) attributeSource, attributeBindingContainer );
 			}
 		}
 	}
 
 	private void bindComponent(ComponentAttributeSource attributeSource, AttributeBindingContainer container) {
 		final String attributeName = attributeSource.getName();
 		SingularAttribute attribute = container.getAttributeContainer().locateComponentAttribute( attributeName );
 		if ( attribute == null ) {
 			final Component component = new Component(
 					attributeSource.getPath(),
 					attributeSource.getClassName(),
 					attributeSource.getClassReference(),
 					null // component inheritance not YET supported
 			);
 			attribute = container.getAttributeContainer().createComponentAttribute( attributeName, component );
 		}
 		ComponentAttributeBinding componentAttributeBinding = container.makeComponentAttributeBinding( attribute );
 
 		if ( StringHelper.isNotEmpty( attributeSource.getParentReferenceAttributeName() ) ) {
 			final SingularAttribute parentReferenceAttribute =
 					componentAttributeBinding.getComponent().createSingularAttribute( attributeSource.getParentReferenceAttributeName() );
 			componentAttributeBinding.setParentReference( parentReferenceAttribute );
 		}
 
 		componentAttributeBinding.setMetaAttributeContext(
 				buildMetaAttributeContext( attributeSource.metaAttributes(), container.getMetaAttributeContext() )
 		);
 
 		bindAttributes( attributeSource, componentAttributeBinding );
 	}
 
 	private void bindPersistentCollection(PluralAttributeSource attributeSource, AttributeBindingContainer attributeBindingContainer) {
 		final PluralAttribute existingAttribute = attributeBindingContainer.getAttributeContainer().locatePluralAttribute(
                 attributeSource.getName()
         );
 		final AbstractPluralAttributeBinding pluralAttributeBinding;
 
 		if ( attributeSource.getPluralAttributeNature() == PluralAttributeNature.BAG ) {
 			final PluralAttribute attribute = existingAttribute != null
 					? existingAttribute
 					: attributeBindingContainer.getAttributeContainer().createBag( attributeSource.getName() );
 			pluralAttributeBinding = attributeBindingContainer.makeBagAttributeBinding(
 					attribute,
-					convert( attributeSource.getPluralAttributeElementNature() )
+					convert( attributeSource.getElementSource().getNature() )
+			);
+		}
+		else if ( attributeSource.getPluralAttributeNature() == PluralAttributeNature.SET ) {
+			final PluralAttribute attribute = existingAttribute != null
+					? existingAttribute
+					: attributeBindingContainer.getAttributeContainer().createSet( attributeSource.getName() );
+			pluralAttributeBinding = attributeBindingContainer.makeSetAttributeBinding(
+					attribute,
+					convert( attributeSource.getElementSource().getNature() )
 			);
 		}
 		else {
 			// todo : implement other collection types
-			throw new NotYetImplementedException( "Collections other than bag not yet implmented :(" );
+			throw new NotYetImplementedException( "Collections other than bag not yet implemented :(" );
 		}
 
 		doBasicAttributeBinding( attributeSource, pluralAttributeBinding );
+
+		bindCollectionTable( attributeSource, pluralAttributeBinding );
+		bindCollectionKey( attributeSource, pluralAttributeBinding );
+		bindSortingAndOrdering( attributeSource, pluralAttributeBinding );
+
+		metadata.addCollection( pluralAttributeBinding );
+	}
+
+	private void bindCollectionTable(
+			PluralAttributeSource attributeSource,
+			AbstractPluralAttributeBinding pluralAttributeBinding) {
+		if ( attributeSource.getElementSource().getNature() == PluralAttributeElementNature.ONE_TO_MANY ) {
+			return;
+		}
+
+		if ( StringHelper.isNotEmpty( attributeSource.getExplicitCollectionTableName() ) ) {
+			final Identifier tableIdentifier = Identifier.toIdentifier( attributeSource.getExplicitCollectionTableName() );
+			Table collectionTable = metadata.getDatabase().getDefaultSchema().locateTable( tableIdentifier );
+			if ( collectionTable == null ) {
+				collectionTable = metadata.getDatabase().getDefaultSchema().createTable( tableIdentifier );
+			}
+			pluralAttributeBinding.setCollectionTable( collectionTable );
+		}
+		else {
+			// todo : we need to infer the name, but that requires possibly knowing the other side
+		}
+	}
+
+	private void bindCollectionKey(
+			PluralAttributeSource attributeSource,
+			AbstractPluralAttributeBinding pluralAttributeBinding) {
+		// todo : implement
+	}
+
+	private void bindSortingAndOrdering(
+			PluralAttributeSource attributeSource,
+			AbstractPluralAttributeBinding pluralAttributeBinding) {
+		if ( Sortable.class.isInstance( attributeSource ) ) {
+			final Sortable sortable = Sortable.class.cast( attributeSource );
+			if ( sortable.isSorted() ) {
+				// todo : handle setting comparator
+
+				// and then return because sorting and ordering are mutually exclusive
+				return;
+			}
+		}
+
+		if ( Orderable.class.isInstance( attributeSource ) ) {
+			final Orderable orderable = Orderable.class.cast( attributeSource );
+			if ( orderable.isOrdered() ) {
+				// todo : handle setting ordering
+			}
+		}
 	}
 
 	private void doBasicAttributeBinding(AttributeSource attributeSource, AttributeBinding attributeBinding) {
 		attributeBinding.setPropertyAccessorName( attributeSource.getPropertyAccessorName() );
 		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
 	}
 
 	private CollectionElementNature convert(PluralAttributeElementNature pluralAttributeElementNature) {
 		return CollectionElementNature.valueOf( pluralAttributeElementNature.name() );
 	}
 
 	private BasicAttributeBinding doBasicSingularAttributeBindingCreation(
 			SingularAttributeSource attributeSource,
 			AttributeBindingContainer attributeBindingContainer) {
 		final SingularAttribute existingAttribute = attributeBindingContainer.getAttributeContainer().locateSingularAttribute(
                 attributeSource.getName()
         );
 		final SingularAttribute attribute;
 		if ( existingAttribute != null ) {
 			attribute = existingAttribute;
 		}
 		else if ( attributeSource.isVirtualAttribute() ) {
 			attribute = attributeBindingContainer.getAttributeContainer().createVirtualSingularAttribute( attributeSource.getName() );
 		}
 		else {
 			attribute = attributeBindingContainer.getAttributeContainer().createSingularAttribute( attributeSource.getName() );
 		}
 
 		final BasicAttributeBinding attributeBinding;
 		if ( attributeSource.getNature() == SingularAttributeNature.BASIC ) {
 			attributeBinding = attributeBindingContainer.makeBasicAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 		}
 		else if ( attributeSource.getNature() == SingularAttributeNature.MANY_TO_ONE ) {
 			attributeBinding = attributeBindingContainer.makeManyToOneAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 			resolveToOneInformation(
 					(ToOneAttributeSource) attributeSource,
 					(ManyToOneAttributeBinding) attributeBinding
 			);
 		}
 		else {
 			throw new NotYetImplementedException();
 		}
 
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
 
 		bindRelationalValues( attributeSource, attributeBinding );
 
 		attributeBinding.setMetaAttributeContext(
 				buildMetaAttributeContext( attributeSource.metaAttributes(), attributeBindingContainer.getMetaAttributeContext() )
 		);
 
 		return attributeBinding;
 	}
 
 	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, BasicAttributeBinding attributeBinding) {
 		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
 		if ( attributeJavaType != null ) {
 			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
 			attributeBinding.getAttribute()
 					.resolveType( currentBindingContext.makeJavaType( attributeJavaType.getName() ) );
 		}
 
 		final String explicitTypeName = typeSource.getName();
 		if ( explicitTypeName != null ) {
 			final TypeDef typeDef = currentBindingContext.getMetadataImplementor()
 					.getTypeDefinition( explicitTypeName );
 			if ( typeDef != null ) {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
 			}
 			else {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
 			}
 			final Map<String, String> parameters = typeSource.getParameters();
 			if ( parameters!=null) {
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
 			final Class<?> ownerClass = attribute.getAttributeContainer().getClassReference();
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
 		attributeBinding.setFetchMode( attributeSource.getFetchMode() );
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
 		final Table table = createTable( entityBinding, tableSource );
 		entityBinding.setPrimaryTable( table );
         entityBinding.setPrimaryTableName( table.getTableName().getName() );
 	}
 
 	private void bindSecondaryTables(EntitySource entitySource, EntityBinding entityBinding) {
 		for ( TableSource secondaryTableSource : entitySource.getSecondaryTables() ) {
 			final Table table = createTable( entityBinding, secondaryTableSource );
 			entityBinding.addSecondaryTable( secondaryTableSource.getLogicalName(), table );
 		}
 	}
 
 	private Table createTable(EntityBinding entityBinding, TableSource tableSource) {
 		final String schemaName = StringHelper.isEmpty( tableSource.getExplicitSchemaName() )
 				? currentBindingContext.getMappingDefaults().getSchemaName()
 				: currentBindingContext.isGloballyQuotedIdentifiers()
 				? StringHelper.quote( tableSource.getExplicitSchemaName() )
 				: tableSource.getExplicitSchemaName();
 		final String catalogName = StringHelper.isEmpty( tableSource.getExplicitCatalogName() )
 				? currentBindingContext.getMappingDefaults().getCatalogName()
 				: currentBindingContext.isGloballyQuotedIdentifiers()
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
 
 		return currentBindingContext.getMetadataImplementor()
 				.getDatabase()
 				.getSchema( new Schema.Name( schemaName, catalogName ) )
 				.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 	}
 
 	private void bindTableUniqueConstraints(EntitySource entitySource, EntityBinding entityBinding) {
 		for ( ConstraintSource constraintSource : entitySource.getConstraints() ) {
 			if ( constraintSource instanceof UniqueConstraintSource ) {
 				TableSpecification table = entityBinding.locateTable( constraintSource.getTableName() );
 				if ( table == null ) {
 					// throw exception !?
 				}
 				String constraintName = constraintSource.name();
 				if ( constraintName == null ) {
 					// create a default name
 				}
 
 				UniqueKey uniqueKey = table.getOrCreateUniqueKey( constraintName );
 				for ( String columnName : constraintSource.columnNames() ) {
 					uniqueKey.addColumn( table.locateOrCreateColumn( columnName ) );
 				}
 			}
 		}
 	}
 
 	private void bindRelationalValues(
 			RelationalValueSourceContainer relationalValueSourceContainer,
 			SingularAttributeBinding attributeBinding) {
 
 		List<SimpleValueBinding> valueBindings = new ArrayList<SimpleValueBinding>();
 
 		if ( relationalValueSourceContainer.relationalValueSources().size() > 0 ) {
 			for ( RelationalValueSource valueSource : relationalValueSourceContainer.relationalValueSources() ) {
 				final TableSpecification table = attributeBinding.getContainer()
 						.seekEntityBinding()
 						.locateTable( valueSource.getContainingTableName() );
 
 				if ( ColumnSource.class.isInstance( valueSource ) ) {
 					final ColumnSource columnSource = ColumnSource.class.cast( valueSource );
 					final Column column = makeColumn( (ColumnSource) valueSource, table );
 					valueBindings.add(
 							new SimpleValueBinding(
 									column,
 									columnSource.isIncludedInInsert(),
 									columnSource.isIncludedInUpdate()
 							)
 					);
 				}
 				else {
 					valueBindings.add(
 							new SimpleValueBinding(
 									makeDerivedValue( ( (DerivedValueSource) valueSource ), table )
 							)
 					);
 				}
 			}
 		}
 		else {
 			final String name = metadata.getOptions()
 					.getNamingStrategy()
 					.propertyToColumnName( attributeBinding.getAttribute().getName() );
 			valueBindings.add(
 					new SimpleValueBinding(
 							attributeBinding.getContainer().seekEntityBinding().getPrimaryTable().locateOrCreateColumn( name )
 					)
 			);
 		}
 		attributeBinding.setSimpleValueBindings( valueBindings );
 	}
 
 	private SimpleValue makeSimpleValue(
 			EntityBinding entityBinding,
 			RelationalValueSource valueSource) {
 		final TableSpecification table = entityBinding.locateTable( valueSource.getContainingTableName() );
 
 		if ( ColumnSource.class.isInstance( valueSource ) ) {
 			return makeColumn( (ColumnSource) valueSource, table );
 		}
 		else {
 			return makeDerivedValue( (DerivedValueSource) valueSource, table );
 		}
 	}
 
 	private Column makeColumn(ColumnSource columnSource, TableSpecification table) {
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
 		return column;
 	}
 
 	private DerivedValue makeDerivedValue(DerivedValueSource derivedValueSource, TableSpecification table) {
 		return table.locateOrCreateDerivedValue( derivedValueSource.getExpression() );
 	}
 
 	private void processFetchProfiles(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : process the entity-local fetch-profile declaration
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
index debe349634..f23c26f8aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
@@ -1,37 +1,39 @@
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
 
 import org.hibernate.internal.util.Value;
 
 /**
  * @author Steve Ebersole
  */
 public interface ComponentAttributeSource extends SingularAttributeSource, AttributeSourceContainer {
 	public String getClassName();
 
 	public Value<Class<?>> getClassReference();
 
 	public String getParentReferenceAttributeName();
+
+	public String getExplicitTuplizerClassName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/CompositePluralAttributeElementSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/CompositePluralAttributeElementSource.java
new file mode 100644
index 0000000000..8b190e32af
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/CompositePluralAttributeElementSource.java
@@ -0,0 +1,39 @@
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
+import org.hibernate.internal.util.Value;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface CompositePluralAttributeElementSource extends PluralAttributeElementSource, AttributeSourceContainer {
+	public String getClassName();
+
+	public Value<Class<?>> getClassReference();
+
+	public String getParentReferenceAttributeName();
+
+	public String getExplicitTuplizerClassName();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ManyToAnyPluralAttributeElementSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ManyToAnyPluralAttributeElementSource.java
new file mode 100644
index 0000000000..89d49a8a05
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ManyToAnyPluralAttributeElementSource.java
@@ -0,0 +1,30 @@
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
+ * @author Steve Ebersole
+ */
+public interface ManyToAnyPluralAttributeElementSource extends PluralAttributeElementSource {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ManyToManyPluralAttributeElementSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ManyToManyPluralAttributeElementSource.java
new file mode 100644
index 0000000000..e1c9cfb8ae
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ManyToManyPluralAttributeElementSource.java
@@ -0,0 +1,48 @@
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
+import java.util.List;
+
+import org.hibernate.FetchMode;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface ManyToManyPluralAttributeElementSource extends PluralAttributeElementSource {
+	public String getReferencedEntityName();
+	public String getReferencedEntityAttributeName();
+
+	public List<RelationalValueSource> getValueSources(); // these describe the "outgoing" link
+
+	public boolean isNotFoundAnException();
+	public String getExplicitForeignKeyName();
+	public boolean isUnique();
+
+	public String getOrderBy();
+	public String getWhere();
+
+	public FetchMode getFetchMode();
+	public boolean fetchImmediately();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/OneToManyPluralAttributeElementSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/OneToManyPluralAttributeElementSource.java
new file mode 100644
index 0000000000..aecc755706
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/OneToManyPluralAttributeElementSource.java
@@ -0,0 +1,32 @@
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
+ * @author Steve Ebersole
+ */
+public interface OneToManyPluralAttributeElementSource extends PluralAttributeElementSource {
+	public String getReferencedEntityName();
+	public boolean isNotFoundAnException();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Orderable.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Orderable.java
new file mode 100644
index 0000000000..115f67aef9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Orderable.java
@@ -0,0 +1,32 @@
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
+ * @author Steve Ebersole
+ */
+public interface Orderable {
+	public boolean isOrdered();
+	public String getOrder();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeElementSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeElementSource.java
new file mode 100644
index 0000000000..aeb60a31ea
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeElementSource.java
@@ -0,0 +1,32 @@
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
+ * @author Steve Ebersole
+ */
+public interface PluralAttributeElementSource {
+	public PluralAttributeElementNature getNature();
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeKeySource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeKeySource.java
new file mode 100644
index 0000000000..815a7828fa
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeKeySource.java
@@ -0,0 +1,38 @@
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
+import java.util.List;
+
+import org.hibernate.metamodel.relational.ForeignKey;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface PluralAttributeKeySource {
+	public List<RelationalValueSource> getValueSources();
+	public String getExplicitForeignKeyName();
+	public ForeignKey.ReferentialAction getOnDeleteAction();
+	public String getReferencedEntityAttributeName();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeSource.java
index 9c6ac98059..2c5673497b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/PluralAttributeSource.java
@@ -1,33 +1,39 @@
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
  * @author Steve Ebersole
  */
-public interface PluralAttributeSource extends AttributeSource {
+public interface PluralAttributeSource extends AssociationAttributeSource {
 	public PluralAttributeNature getPluralAttributeNature();
 
-	public PluralAttributeElementNature getPluralAttributeElementNature();
+	public PluralAttributeKeySource getKeySource();
+
+	public PluralAttributeElementSource getElementSource();
+
+	public String getExplicitCollectionTableName();
+
+	public boolean isInverse();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Sortable.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Sortable.java
new file mode 100644
index 0000000000..cba0da7996
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Sortable.java
@@ -0,0 +1,33 @@
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
+ * @author Steve Ebersole
+ */
+public interface Sortable {
+	public boolean isSorted();
+	public String getComparatorName();
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
index 578ccfe97e..bef0dea833 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntitySourceImpl.java
@@ -1,280 +1,308 @@
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 
+import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.ConstraintSource;
 import org.hibernate.metamodel.source.binder.EntitySource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.SubclassEntitySource;
 import org.hibernate.metamodel.source.binder.TableSource;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLBagElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLComponentElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLIdbagElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLListElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLMapElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSetElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
 
 /**
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public abstract class AbstractEntitySourceImpl implements EntitySource {
 	private final MappingDocument sourceMappingDocument;
 	private final EntityElement entityElement;
 
 	private List<SubclassEntitySource> subclassEntitySources = new ArrayList<SubclassEntitySource>();
 
 	protected AbstractEntitySourceImpl(MappingDocument sourceMappingDocument, EntityElement entityElement) {
 		this.sourceMappingDocument = sourceMappingDocument;
 		this.entityElement = entityElement;
 	}
 
 	protected EntityElement entityElement() {
 		return entityElement;
 	}
 
 	protected MappingDocument sourceMappingDocument() {
 		return sourceMappingDocument;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return sourceMappingDocument.getOrigin();
 	}
 
 	@Override
 	public LocalBindingContext getLocalBindingContext() {
 		return sourceMappingDocument.getMappingLocalBindingContext();
 	}
 
 	@Override
 	public String getEntityName() {
 		return StringHelper.isNotEmpty( entityElement.getEntityName() )
 				? entityElement.getEntityName()
 				: getClassName();
 	}
 
 	@Override
 	public String getClassName() {
 		return getLocalBindingContext().qualifyClassName( entityElement.getName() );
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAbstract() {
 		return Helper.getBooleanValue( entityElement.isAbstract(), false );
 	}
 
 	@Override
 	public boolean isLazy() {
 		return Helper.getBooleanValue( entityElement.isAbstract(), true );
 	}
 
 	@Override
 	public String getProxy() {
 		return entityElement.getProxy();
 	}
 
 	@Override
 	public int getBatchSize() {
 		return Helper.getIntValue( entityElement.getBatchSize(), -1 );
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return entityElement.isDynamicInsert();
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return entityElement.isDynamicUpdate();
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return entityElement.isSelectBeforeUpdate();
 	}
 
 	protected EntityMode determineEntityMode() {
 		return StringHelper.isNotEmpty( getClassName() ) ? EntityMode.POJO : EntityMode.MAP;
 	}
 
 	@Override
 	public String getCustomTuplizerClassName() {
 		if ( entityElement.getTuplizer() == null ) {
 			return null;
 		}
 		final EntityMode entityMode = determineEntityMode();
 		for ( XMLTuplizerElement tuplizerElement : entityElement.getTuplizer() ) {
 			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
 				return tuplizerElement.getClazz();
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public String getCustomPersisterClassName() {
 		return getLocalBindingContext().qualifyClassName( entityElement.getPersister() );
 	}
 
 	@Override
 	public String getCustomLoaderName() {
 		return entityElement.getLoader() != null ? entityElement.getLoader().getQueryRef() : null;
 	}
 
 	@Override
 	public CustomSQL getCustomSqlInsert() {
 		return Helper.buildCustomSql( entityElement.getSqlInsert() );
 	}
 
 	@Override
 	public CustomSQL getCustomSqlUpdate() {
 		return Helper.buildCustomSql( entityElement.getSqlUpdate() );
 	}
 
 	@Override
 	public CustomSQL getCustomSqlDelete() {
 		return Helper.buildCustomSql( entityElement.getSqlDelete() );
 	}
 
 	@Override
 	public List<String> getSynchronizedTableNames() {
 		List<String> tableNames = new ArrayList<String>();
 		for ( XMLSynchronizeElement synchronizeElement : entityElement.getSynchronize() ) {
 			tableNames.add( synchronizeElement.getTable() );
 		}
 		return tableNames;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( entityElement.getMeta() );
 	}
 
 	@Override
 	public String getPath() {
 		return sourceMappingDocument.getMappingLocalBindingContext().determineEntityName( entityElement );
 	}
 
 	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
 		for ( Object attributeElement : entityElement.getPropertyOrManyToOneOrOneToOne() ) {
 			if ( XMLPropertyElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new PropertyAttributeSourceImpl(
 								XMLPropertyElement.class.cast( attributeElement ),
 								sourceMappingDocument().getMappingLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLComponentElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ComponentAttributeSourceImpl(
 								(XMLComponentElement) attributeElement,
 								this,
 								sourceMappingDocument.getMappingLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLManyToOneElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ManyToOneAttributeSourceImpl(
 								XMLManyToOneElement.class.cast( attributeElement ),
 								sourceMappingDocument().getMappingLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLOneToOneElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLAnyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
-			else if ( XMLOneToManyElement.class.isInstance( attributeElement ) ) {
+			else if ( XMLBagElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new BagAttributeSourceImpl(
+								XMLBagElement.class.cast( attributeElement ),
+								this
+						)
+				);
+			}
+			else if ( XMLIdbagElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
-			else if ( XMLManyToManyElement.class.isInstance( attributeElement ) ) {
+			else if ( XMLSetElement.class.isInstance( attributeElement ) ) {
+				attributeSources.add(
+						new SetAttributeSourceImpl(
+								XMLSetElement.class.cast( attributeElement ),
+								this
+						)
+				);
+			}
+			else if ( XMLListElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
+			else if ( XMLMapElement.class.isInstance( attributeElement ) ) {
+				// todo : implement
+			}
+			else {
+				throw new AssertionFailure( "Unexpected attribute element type encountered : " + attributeElement.getClass() );
+			}
 		}
 		return attributeSources;
 	}
 
 	private EntityHierarchyImpl entityHierarchy;
 
 	public void injectHierarchy(EntityHierarchyImpl entityHierarchy) {
 		this.entityHierarchy = entityHierarchy;
 	}
 
 	@Override
 	public void add(SubclassEntitySource subclassEntitySource) {
 		add( (SubclassEntitySourceImpl) subclassEntitySource );
 	}
 
 	public void add(SubclassEntitySourceImpl subclassEntitySource) {
 		entityHierarchy.processSubclass( subclassEntitySource );
 		subclassEntitySources.add( subclassEntitySource );
 	}
 
 	@Override
 	public Iterable<SubclassEntitySource> subclassEntitySources() {
 		return subclassEntitySources;
 	}
 
 	@Override
 	public String getDiscriminatorMatchValue() {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public Iterable<ConstraintSource> getConstraints() {
 		return Collections.emptySet();
 	}
 
 	@Override
 	public Iterable<TableSource> getSecondaryTables() {
 		return Collections.emptySet();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractPluralAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractPluralAttributeSourceImpl.java
new file mode 100644
index 0000000000..25fbedbf4f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractPluralAttributeSourceImpl.java
@@ -0,0 +1,52 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.AttributeSourceContainer;
+import org.hibernate.metamodel.source.binder.PluralAttributeSource;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractPluralAttributeSourceImpl implements PluralAttributeSource {
+	private final AttributeSourceContainer container;
+
+	protected AbstractPluralAttributeSourceImpl(AttributeSourceContainer container) {
+		this.container = container;
+	}
+
+	protected AttributeSourceContainer container() {
+		return container;
+	}
+
+	protected LocalBindingContext bindingContext() {
+		return container().getLocalBindingContext();
+	}
+
+	@Override
+	public boolean isSingular() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BagAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BagAttributeSourceImpl.java
new file mode 100644
index 0000000000..9c319cb465
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BagAttributeSourceImpl.java
@@ -0,0 +1,149 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.FetchMode;
+import org.hibernate.cfg.NotYetImplementedException;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.binder.AttributeSourceContainer;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.PluralAttributeElementSource;
+import org.hibernate.metamodel.source.binder.PluralAttributeKeySource;
+import org.hibernate.metamodel.source.binder.PluralAttributeNature;
+import org.hibernate.metamodel.source.binder.PluralAttributeSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLBagElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BagAttributeSourceImpl implements PluralAttributeSource {
+	private final XMLBagElement bagElement;
+	private final AttributeSourceContainer container;
+
+	// todo : a lot of this could be consolidated with common JAXB interface for collection mappings and moved to a base class
+
+	private final PluralAttributeKeySource keySource;
+	private final PluralAttributeElementSource elementSource;
+
+	public BagAttributeSourceImpl(XMLBagElement bagElement, AttributeSourceContainer container) {
+		this.bagElement = bagElement;
+		this.container = container;
+
+		this.keySource = new PluralAttributeKeySourceImpl( bagElement.getKey(), container );
+		this.elementSource = interpretElementType( bagElement );
+	}
+
+	private PluralAttributeElementSource interpretElementType(XMLBagElement bagElement) {
+		if ( bagElement.getElement() != null ) {
+			return new BasicPluralAttributeElementSourceImpl( bagElement.getElement(), container.getLocalBindingContext() );
+		}
+		else if ( bagElement.getCompositeElement() != null ) {
+			return new CompositePluralAttributeElementSourceImpl( bagElement.getCompositeElement(), container.getLocalBindingContext() );
+		}
+		else if ( bagElement.getOneToMany() != null ) {
+			return new OneToManyPluralAttributeElementSourceImpl( bagElement.getOneToMany(), container.getLocalBindingContext() );
+		}
+		else if ( bagElement.getManyToMany() != null ) {
+			return new ManyToManyPluralAttributeElementSourceImpl( bagElement.getManyToMany(), container.getLocalBindingContext() );
+		}
+		else if ( bagElement.getManyToAny() != null ) {
+			throw new NotYetImplementedException( "Support for many-to-any not yet implemented" );
+//			return PluralAttributeElementNature.MANY_TO_ANY;
+		}
+		else {
+			throw new MappingException(
+					"Unexpected collection element type : " + bagElement.getName(),
+					bindingContext().getOrigin()
+			);
+		}
+	}
+
+	@Override
+	public PluralAttributeNature getPluralAttributeNature() {
+		return PluralAttributeNature.BAG;
+	}
+
+	@Override
+	public PluralAttributeKeySource getKeySource() {
+		return keySource;
+	}
+
+	@Override
+	public PluralAttributeElementSource getElementSource() {
+		return elementSource;
+	}
+
+	@Override
+	public String getExplicitCollectionTableName() {
+		return bagElement.getTable();
+	}
+
+	private LocalBindingContext bindingContext() {
+		return container.getLocalBindingContext();
+	}
+
+	@Override
+	public String getName() {
+		return bagElement.getName();
+	}
+
+	@Override
+	public boolean isSingular() {
+		return false;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return bagElement.getAccess();
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return bagElement.isOptimisticLock();
+	}
+
+	@Override
+	public boolean isInverse() {
+		return bagElement.isInverse();
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( bagElement.getMeta() );
+	}
+
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyles() {
+		return Helper.interpretCascadeStyles( bagElement.getCascade(), bindingContext() );
+	}
+
+	@Override
+	public FetchMode getFetchMode() {
+		return bagElement.getFetch() == null
+				? FetchMode.DEFAULT
+				: FetchMode.valueOf( bagElement.getFetch().value() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BasicPluralAttributeElementSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BasicPluralAttributeElementSourceImpl.java
new file mode 100644
index 0000000000..1372f7d789
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/BasicPluralAttributeElementSourceImpl.java
@@ -0,0 +1,118 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.BasicPluralAttributeElementSource;
+import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
+import org.hibernate.metamodel.source.binder.PluralAttributeElementNature;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLElementElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicPluralAttributeElementSourceImpl implements BasicPluralAttributeElementSource {
+	private final List<RelationalValueSource> valueSources;
+	private final ExplicitHibernateTypeSource typeSource;
+
+	public BasicPluralAttributeElementSourceImpl(
+			final XMLElementElement elementElement,
+			LocalBindingContext bindingContext) {
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
+					@Override
+					public String getContainingTableName() {
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
+
+					@Override
+					public String getColumnAttribute() {
+						return elementElement.getColumn();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return elementElement.getFormula();
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return elementElement.getColumnOrFormula();
+					}
+				},
+				bindingContext
+		);
+
+		this.typeSource = new ExplicitHibernateTypeSource() {
+			@Override
+			public String getName() {
+				if ( elementElement.getTypeAttribute() != null ) {
+					return elementElement.getTypeAttribute();
+				}
+				else if ( elementElement.getType() != null ) {
+					return elementElement.getType().getName();
+				}
+				else {
+					return null;
+				}
+			}
+
+			@Override
+			public Map<String, String> getParameters() {
+				return elementElement.getType() != null
+						? Helper.extractParameters( elementElement.getType().getParam() )
+						: java.util.Collections.<String, String>emptyMap();
+			}
+		};
+	}
+
+	@Override
+	public PluralAttributeElementNature getNature() {
+		return PluralAttributeElementNature.BASIC;
+	}
+
+	@Override
+	public List<RelationalValueSource> getValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public ExplicitHibernateTypeSource getExplicitHibernateTypeSource() {
+		return typeSource;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
index 70f6726e3b..b0bd294ee7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
@@ -1,220 +1,239 @@
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
 
 import java.util.ArrayList;
 import java.util.List;
 
+import org.hibernate.EntityMode;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.AttributeSourceContainer;
 import org.hibernate.metamodel.source.binder.ComponentAttributeSource;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLAnyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLComponentElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToManyElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToOneElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLPropertyElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
 
 /**
  * @author Steve Ebersole
  */
 public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
 	private final XMLComponentElement componentElement;
 	private final AttributeSourceContainer parentContainer;
 
 	private final Value<Class<?>> componentClassReference;
 	private final String path;
 
 	public ComponentAttributeSourceImpl(
 			XMLComponentElement componentElement,
 			AttributeSourceContainer parentContainer,
 			LocalBindingContext bindingContext) {
 		this.componentElement = componentElement;
 		this.parentContainer = parentContainer;
 
-		this.componentClassReference = bindingContext.makeClassReference( componentElement.getClazz() );
+		this.componentClassReference = bindingContext.makeClassReference(
+				bindingContext.qualifyClassName( componentElement.getClazz() )
+		);
 		this.path = parentContainer.getPath() + '.' + componentElement.getName();
 	}
 
 	@Override
 	public String getClassName() {
 		return componentElement.getClazz();
 	}
 
 	@Override
 	public Value<Class<?>> getClassReference() {
 		return componentClassReference;
 	}
 
 	@Override
 	public String getPath() {
 		return path;
 	}
 
 	@Override
 	public LocalBindingContext getLocalBindingContext() {
 		return parentContainer.getLocalBindingContext();
 	}
 
 	@Override
 	public String getParentReferenceAttributeName() {
 		return componentElement.getParent() == null ? null : componentElement.getParent().getName();
 	}
 
 	@Override
+	public String getExplicitTuplizerClassName() {
+		if ( componentElement.getTuplizer() == null ) {
+			return null;
+		}
+		final EntityMode entityMode = StringHelper.isEmpty( componentElement.getClazz() ) ? EntityMode.MAP : EntityMode.POJO;
+		for ( XMLTuplizerElement tuplizerElement : componentElement.getTuplizer() ) {
+			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
+				return tuplizerElement.getClazz();
+			}
+		}
+		return null;
+	}
+
+	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
 		for ( Object attributeElement : componentElement.getPropertyOrManyToOneOrOneToOne() ) {
 			if ( XMLPropertyElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new PropertyAttributeSourceImpl(
 								XMLPropertyElement.class.cast( attributeElement ),
 								getLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLComponentElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ComponentAttributeSourceImpl(
 								(XMLComponentElement) attributeElement,
 								this,
 								getLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLManyToOneElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ManyToOneAttributeSourceImpl(
 								XMLManyToOneElement.class.cast( attributeElement ),
 								getLocalBindingContext()
 						)
 				);
 			}
 			else if ( XMLOneToOneElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLAnyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLOneToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( XMLManyToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 		}
 		return attributeSources;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.COMPONENT;
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		// <component/> does not support type information.
 		return null;
 	}
 
 	@Override
 	public String getName() {
 		return componentElement.getName();
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return componentElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return componentElement.isInsert();
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return componentElement.isUpdate();
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		// todo : is this correct here?
 		return null;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return componentElement.isLazy();
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return componentElement.isOptimisticLock();
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( componentElement.getMeta() );
 	}
 
 	@Override
 	public boolean areValuesIncludedInInsertByDefault() {
 		return isInsertable();
 	}
 
 	@Override
 	public boolean areValuesIncludedInUpdateByDefault() {
 		return isUpdatable();
 	}
 
 	@Override
 	public boolean areValuesNullableByDefault() {
 		return true;
 	}
 
 	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		// none, they are defined on the simple sub-attributes
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/CompositePluralAttributeElementSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/CompositePluralAttributeElementSourceImpl.java
new file mode 100644
index 0000000000..da2c6f1b3e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/CompositePluralAttributeElementSourceImpl.java
@@ -0,0 +1,108 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.EntityMode;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.Value;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.AttributeSource;
+import org.hibernate.metamodel.source.binder.CompositePluralAttributeElementSource;
+import org.hibernate.metamodel.source.binder.PluralAttributeElementNature;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLCompositeElementElement;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLTuplizerElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositePluralAttributeElementSourceImpl implements CompositePluralAttributeElementSource {
+	private final XMLCompositeElementElement compositeElement;
+	private final LocalBindingContext bindingContext;
+
+	public CompositePluralAttributeElementSourceImpl(
+			XMLCompositeElementElement compositeElement,
+			LocalBindingContext bindingContext) {
+		this.compositeElement = compositeElement;
+		this.bindingContext = bindingContext;
+	}
+
+	@Override
+	public PluralAttributeElementNature getNature() {
+		return PluralAttributeElementNature.COMPONENT;
+	}
+
+	@Override
+	public String getClassName() {
+		return bindingContext.qualifyClassName( compositeElement.getClazz() );
+	}
+
+	@Override
+	public Value<Class<?>> getClassReference() {
+		return bindingContext.makeClassReference( getClassName() );
+	}
+
+	@Override
+	public String getParentReferenceAttributeName() {
+		return compositeElement.getParent() != null
+				? compositeElement.getParent().getName()
+				: null;
+	}
+
+	@Override
+	public String getExplicitTuplizerClassName() {
+		if ( compositeElement.getTuplizer() == null ) {
+			return null;
+		}
+		final EntityMode entityMode = StringHelper.isEmpty( compositeElement.getClazz() ) ? EntityMode.MAP : EntityMode.POJO;
+		for ( XMLTuplizerElement tuplizerElement : compositeElement.getTuplizer() ) {
+			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
+				return tuplizerElement.getClazz();
+			}
+		}
+		return null;
+	}
+
+	@Override
+	public String getPath() {
+		// todo : implementing this requires passing in the collection source and being able to resolve the collection's role
+		return null;
+	}
+
+	@Override
+	public Iterable<AttributeSource> attributeSources() {
+		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
+		for ( Object attribute : compositeElement.getPropertyOrManyToOneOrAny() ) {
+
+		}
+		return attributeSources;
+	}
+
+	@Override
+	public LocalBindingContext getLocalBindingContext() {
+		return bindingContext;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToManyPluralAttributeElementSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToManyPluralAttributeElementSourceImpl.java
new file mode 100644
index 0000000000..430ee8a005
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ManyToManyPluralAttributeElementSourceImpl.java
@@ -0,0 +1,159 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+
+import org.hibernate.FetchMode;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.ManyToManyPluralAttributeElementSource;
+import org.hibernate.metamodel.source.binder.PluralAttributeElementNature;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLManyToManyElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ManyToManyPluralAttributeElementSourceImpl implements ManyToManyPluralAttributeElementSource {
+	private final XMLManyToManyElement manyToManyElement;
+	private final LocalBindingContext bindingContext;
+
+	private final List<RelationalValueSource> valueSources;
+
+	public ManyToManyPluralAttributeElementSourceImpl(
+			final XMLManyToManyElement manyToManyElement,
+			final LocalBindingContext bindingContext) {
+		this.manyToManyElement = manyToManyElement;
+		this.bindingContext = bindingContext;
+
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
+					@Override
+					public String getContainingTableName() {
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
+
+					@Override
+					public String getColumnAttribute() {
+						return manyToManyElement.getColumn();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return manyToManyElement.getFormula();
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return manyToManyElement.getColumnOrFormula();
+					}
+				},
+				bindingContext
+		);
+	}
+
+	@Override
+	public PluralAttributeElementNature getNature() {
+		return PluralAttributeElementNature.MANY_TO_MANY;
+	}
+
+	@Override
+	public String getReferencedEntityName() {
+		return StringHelper.isNotEmpty( manyToManyElement.getEntityName() )
+				? manyToManyElement.getEntityName()
+				: bindingContext.qualifyClassName( manyToManyElement.getClazz() );
+	}
+
+	@Override
+	public String getReferencedEntityAttributeName() {
+		return manyToManyElement.getPropertyRef();
+	}
+
+	@Override
+	public List<RelationalValueSource> getValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public boolean isNotFoundAnException() {
+		return manyToManyElement.getNotFound() == null
+				|| ! "ignore".equals( manyToManyElement.getNotFound().value() );
+	}
+
+	@Override
+	public String getExplicitForeignKeyName() {
+		return manyToManyElement.getForeignKey();
+	}
+
+	@Override
+	public boolean isUnique() {
+		return manyToManyElement.isUnique();
+	}
+
+	@Override
+	public String getOrderBy() {
+		return manyToManyElement.getOrderBy();
+	}
+
+	@Override
+	public String getWhere() {
+		return manyToManyElement.getWhere();
+	}
+
+	@Override
+	public FetchMode getFetchMode() {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public boolean fetchImmediately() {
+		if ( manyToManyElement.getLazy() != null ) {
+			if ( "false".equals( manyToManyElement.getLazy().value() ) ) {
+				return true;
+			}
+		}
+
+		if ( manyToManyElement.getOuterJoin() == null ) {
+			return ! bindingContext.getMappingDefaults().areAssociationsLazy();
+		}
+		else {
+			final String value = manyToManyElement.getOuterJoin().value();
+			if ( "auto".equals( value ) ) {
+				return ! bindingContext.getMappingDefaults().areAssociationsLazy();
+			}
+			return "true".equals( value );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/OneToManyPluralAttributeElementSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/OneToManyPluralAttributeElementSourceImpl.java
new file mode 100644
index 0000000000..7920072af4
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/OneToManyPluralAttributeElementSourceImpl.java
@@ -0,0 +1,63 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.binder.OneToManyPluralAttributeElementSource;
+import org.hibernate.metamodel.source.binder.PluralAttributeElementNature;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLOneToManyElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class OneToManyPluralAttributeElementSourceImpl implements OneToManyPluralAttributeElementSource {
+	private final XMLOneToManyElement oneToManyElement;
+	private final LocalBindingContext bindingContext;
+
+	public OneToManyPluralAttributeElementSourceImpl(
+			XMLOneToManyElement oneToManyElement,
+			LocalBindingContext bindingContext) {
+		this.oneToManyElement = oneToManyElement;
+		this.bindingContext = bindingContext;
+	}
+
+	@Override
+	public PluralAttributeElementNature getNature() {
+		return PluralAttributeElementNature.ONE_TO_MANY;
+	}
+
+	@Override
+	public String getReferencedEntityName() {
+		return StringHelper.isNotEmpty( oneToManyElement.getEntityName() )
+				? oneToManyElement.getEntityName()
+				: bindingContext.qualifyClassName( oneToManyElement.getClazz() );
+	}
+
+	@Override
+	public boolean isNotFoundAnException() {
+		return oneToManyElement.getNotFound() == null
+				|| ! "ignore".equals( oneToManyElement.getNotFound().value() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PluralAttributeKeySourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PluralAttributeKeySourceImpl.java
new file mode 100644
index 0000000000..4de22b245d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/PluralAttributeKeySourceImpl.java
@@ -0,0 +1,104 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import java.util.List;
+
+import org.hibernate.metamodel.relational.ForeignKey;
+import org.hibernate.metamodel.source.binder.AttributeSourceContainer;
+import org.hibernate.metamodel.source.binder.PluralAttributeKeySource;
+import org.hibernate.metamodel.source.binder.RelationalValueSource;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLKeyElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class PluralAttributeKeySourceImpl implements PluralAttributeKeySource {
+	private final XMLKeyElement keyElement;
+
+	private final List<RelationalValueSource> valueSources;
+
+	public PluralAttributeKeySourceImpl(
+			final XMLKeyElement keyElement,
+			final AttributeSourceContainer container) {
+		this.keyElement = keyElement;
+
+		this.valueSources = Helper.buildValueSources(
+				new Helper.ValueSourcesAdapter() {
+					@Override
+					public String getContainingTableName() {
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
+						return Helper.getBooleanValue( keyElement.isUpdate(), true );
+					}
+
+					@Override
+					public String getColumnAttribute() {
+						return keyElement.getColumnAttribute();
+					}
+
+					@Override
+					public String getFormulaAttribute() {
+						return null;
+					}
+
+					@Override
+					public List getColumnOrFormulaElements() {
+						return keyElement.getColumn();
+					}
+				},
+				container.getLocalBindingContext()
+		);
+	}
+
+	@Override
+	public List<RelationalValueSource> getValueSources() {
+		return valueSources;
+	}
+
+	@Override
+	public String getExplicitForeignKeyName() {
+		return keyElement.getForeignKey();
+	}
+
+	@Override
+	public ForeignKey.ReferentialAction getOnDeleteAction() {
+		return "cascade".equals( keyElement.getOnDelete() )
+				? ForeignKey.ReferentialAction.CASCADE
+				: ForeignKey.ReferentialAction.NO_ACTION;
+	}
+
+	@Override
+	public String getReferencedEntityAttributeName() {
+		return keyElement.getPropertyRef();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SetAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SetAttributeSourceImpl.java
new file mode 100644
index 0000000000..078b4fc57f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/SetAttributeSourceImpl.java
@@ -0,0 +1,172 @@
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
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.FetchMode;
+import org.hibernate.cfg.NotYetImplementedException;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.source.LocalBindingContext;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.binder.AttributeSourceContainer;
+import org.hibernate.metamodel.source.binder.MetaAttributeSource;
+import org.hibernate.metamodel.source.binder.Orderable;
+import org.hibernate.metamodel.source.binder.PluralAttributeElementSource;
+import org.hibernate.metamodel.source.binder.PluralAttributeKeySource;
+import org.hibernate.metamodel.source.binder.PluralAttributeNature;
+import org.hibernate.metamodel.source.binder.PluralAttributeSource;
+import org.hibernate.metamodel.source.binder.Sortable;
+import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSetElement;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SetAttributeSourceImpl implements PluralAttributeSource, Sortable, Orderable {
+	private XMLSetElement setElement;
+	private AttributeSourceContainer container;
+
+	// todo : a lot of this could be consolidated with common JAXB interface for collection mappings and moved to a base class
+
+	private final PluralAttributeKeySource keySource;
+	private final PluralAttributeElementSource elementSource;
+
+	public SetAttributeSourceImpl(XMLSetElement setElement, AttributeSourceContainer container) {
+		this.setElement = setElement;
+		this.container = container;
+
+		this.keySource = new PluralAttributeKeySourceImpl( setElement.getKey(), container );
+		this.elementSource = interpretElementType( setElement );
+	}
+
+	private PluralAttributeElementSource interpretElementType(XMLSetElement setElement) {
+		if ( setElement.getElement() != null ) {
+			return new BasicPluralAttributeElementSourceImpl( setElement.getElement(), container.getLocalBindingContext() );
+		}
+		else if ( setElement.getCompositeElement() != null ) {
+			return new CompositePluralAttributeElementSourceImpl( setElement.getCompositeElement(), container.getLocalBindingContext() );
+		}
+		else if ( setElement.getOneToMany() != null ) {
+			return new OneToManyPluralAttributeElementSourceImpl( setElement.getOneToMany(), container.getLocalBindingContext() );
+		}
+		else if ( setElement.getManyToMany() != null ) {
+			return new ManyToManyPluralAttributeElementSourceImpl( setElement.getManyToMany(), container.getLocalBindingContext() );
+		}
+		else if ( setElement.getManyToAny() != null ) {
+			throw new NotYetImplementedException( "Support for many-to-any not yet implemented" );
+//			return PluralAttributeElementNature.MANY_TO_ANY;
+		}
+		else {
+			throw new MappingException(
+					"Unexpected collection element type : " + setElement.getName(),
+					bindingContext().getOrigin()
+			);
+		}
+	}
+
+	private LocalBindingContext bindingContext() {
+		return container.getLocalBindingContext();
+	}
+
+	@Override
+	public PluralAttributeNature getPluralAttributeNature() {
+		return PluralAttributeNature.BAG;
+	}
+
+	@Override
+	public PluralAttributeKeySource getKeySource() {
+		return keySource;
+	}
+
+	@Override
+	public PluralAttributeElementSource getElementSource() {
+		return elementSource;
+	}
+
+	@Override
+	public String getExplicitCollectionTableName() {
+		return setElement.getTable();
+	}
+
+	@Override
+	public boolean isInverse() {
+		return setElement.isInverse();
+	}
+
+	@Override
+	public String getName() {
+		return setElement.getName();
+	}
+
+	@Override
+	public boolean isSingular() {
+		return false;
+	}
+
+	@Override
+	public String getPropertyAccessorName() {
+		return setElement.getAccess();
+	}
+
+	@Override
+	public boolean isIncludedInOptimisticLocking() {
+		return setElement.isOptimisticLock();
+	}
+
+	@Override
+	public Iterable<MetaAttributeSource> metaAttributes() {
+		return Helper.buildMetaAttributeSources( setElement.getMeta() );
+	}
+
+	@Override
+	public Iterable<CascadeStyle> getCascadeStyles() {
+		return Helper.interpretCascadeStyles( setElement.getCascade(), bindingContext() );
+	}
+
+	@Override
+	public FetchMode getFetchMode() {
+		return setElement.getFetch() == null
+				? FetchMode.DEFAULT
+				: FetchMode.valueOf( setElement.getFetch().value() );
+	}
+
+	@Override
+	public boolean isSorted() {
+		return StringHelper.isNotEmpty( setElement.getSort() );
+	}
+
+	@Override
+	public String getComparatorName() {
+		return setElement.getSort();
+	}
+
+	@Override
+	public boolean isOrdered() {
+		return StringHelper.isNotEmpty( setElement.getOrderBy() );
+	}
+
+	@Override
+	public String getOrder() {
+		return setElement.getOrderBy();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 70b2eeda6a..1820aa72e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,604 +1,605 @@
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
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
 
-	private Map<String, AbstractPluralAttributeBinding> collectionBindingMap = new HashMap<String, AbstractPluralAttributeBinding>();
+	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
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
 
-	public AbstractPluralAttributeBinding getCollection(String collectionRole) {
+	public PluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	@Override
-	public Iterable<AbstractPluralAttributeBinding> getCollectionBindings() {
+	public Iterable<PluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
-	public void addCollection(AbstractPluralAttributeBinding pluralAttributeBinding) {
+	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
 		final String owningEntityName = pluralAttributeBinding.getContainer().getPathBase();
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
 				.getHierarchyDetails()
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
 		AttributeBinding idBinding = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.locateAttributeBinding( propertyName );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index 6ae929ff03..38ca40f772 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,249 +1,251 @@
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
 
+import java.lang.reflect.Constructor;
+import java.lang.reflect.InvocationTargetException;
+
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
-import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
-import java.lang.reflect.Constructor;
-import java.lang.reflect.InvocationTargetException;
-
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
 	 * a {@link org.hibernate.metamodel.binding.AbstractPluralAttributeBinding}
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW to COLLECTION_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			AbstractPluralAttributeBinding.class,
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
-	public CollectionPersister createCollectionPersister(MetadataImplementor metadata,
-														 AbstractPluralAttributeBinding collectionMetadata,
-														 CollectionRegionAccessStrategy cacheAccessStrategy,
-														 SessionFactoryImplementor factory) throws HibernateException {
+	public CollectionPersister createCollectionPersister(
+			MetadataImplementor metadata,
+			PluralAttributeBinding collectionMetadata,
+			CollectionRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
 		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
 	// TODO: change collectionMetadata arg type to AbstractPluralAttributeBinding when new metadata is integrated
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
index 3feda73f9a..349bc558a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
@@ -1,122 +1,122 @@
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
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.CollectionElementNature;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
 		if ( metadata.isRoot() ) {
 			return singleTableEntityPersister(); // EARLY RETURN!
 		}
 		switch ( metadata.getHierarchyDetails().getInheritanceType() ) {
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
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 		return metadata.getCollectionElement().getCollectionElementNature() == CollectionElementNature.ONE_TO_MANY
 				? oneToManyPersister()
 				: basicCollectionPersister();
 	}
 
 	private Class<OneToManyPersister> oneToManyPersister() {
 		return OneToManyPersister.class;
 	}
 
 	private Class<BasicCollectionPersister> basicCollectionPersister() {
 		return BasicCollectionPersister.class;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
index d3660fb255..f82123e71e 100644
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
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
-	Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata);
+	Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index 15dfd4cb3e..4716385032 100644
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
-import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.source.MetadataImplementor;
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
-			AbstractPluralAttributeBinding model,
+			PluralAttributeBinding model,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
new file mode 100644
index 0000000000..c5cf91f295
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
@@ -0,0 +1,91 @@
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
+import org.hibernate.metamodel.MetadataSourceProcessingOrder;
+import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.service.ServiceRegistryBuilder;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertSame;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicCollectionBindingTests extends BaseUnitTestCase {
+	private BasicServiceRegistryImpl serviceRegistry;
+
+	@Before
+	public void setUp() {
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
+	}
+
+	@After
+	public void tearDown() {
+		serviceRegistry.destroy();
+	}
+
+//	@Test
+//	public void testAnnotations() {
+//		doTest( MetadataSourceProcessingOrder.ANNOTATIONS_FIRST );
+//	}
+
+	@Test
+	public void testHbm() {
+		doTest( MetadataSourceProcessingOrder.HBM_FIRST );
+	}
+
+	private void doTest(MetadataSourceProcessingOrder processingOrder) {
+		MetadataSources sources = new MetadataSources( serviceRegistry );
+//		sources.addAnnotatedClass( EntityWithBasicCollections.class );
+		sources.addResource( "org/hibernate/metamodel/binding/EntityWithBasicCollections.hbm.xml" );
+		MetadataImpl metadata = (MetadataImpl) sources.getMetadataBuilder().with( processingOrder ).buildMetadata();
+
+		final EntityBinding entityBinding = metadata.getEntityBinding( EntityWithBasicCollections.class.getName() );
+		assertNotNull( entityBinding );
+
+		PluralAttributeBinding bagBinding = metadata.getCollection( EntityWithBasicCollections.class.getName() + ".theBag" );
+		assertNotNull( bagBinding );
+		assertSame( bagBinding, entityBinding.locateAttributeBinding( "theBag" ) );
+		assertNotNull( bagBinding.getCollectionTable() );
+		assertEquals( CollectionElementNature.BASIC, bagBinding.getCollectionElement().getCollectionElementNature() );
+		assertEquals( String.class.getName(), bagBinding.getCollectionElement().getHibernateTypeDescriptor().getJavaTypeName() );
+
+		PluralAttributeBinding setBinding = metadata.getCollection( EntityWithBasicCollections.class.getName() + ".theSet" );
+		assertNotNull( setBinding );
+		assertSame( setBinding, entityBinding.locateAttributeBinding( "theSet" ) );
+		assertNotNull( setBinding.getCollectionTable() );
+		assertEquals( CollectionElementNature.BASIC, setBinding.getCollectionElement().getCollectionElementNature() );
+		assertEquals( String.class.getName(), setBinding.getCollectionElement().getHibernateTypeDescriptor().getJavaTypeName() );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithBasicCollections.hbm.xml b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithBasicCollections.hbm.xml
new file mode 100644
index 0000000000..3eb4ca7633
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithBasicCollections.hbm.xml
@@ -0,0 +1,24 @@
+<?xml version="1.0"?>
+<hibernate-mapping
+        xmlns="http://www.hibernate.org/xsd/hibernate-mapping"
+        package="org.hibernate.metamodel.binding" >
+
+    <class name="EntityWithBasicCollections">
+
+    	<id name="id">
+    		<generator class="increment"/>
+    	</id>
+        <property name="name"/>
+
+        <bag name="theBag">
+            <key column="owner_id"/>
+            <element column="bag_stuff" type="string"/>
+        </bag>
+
+        <set name="theSet">
+            <key column="pid"/>
+            <element column="set_stuff" type="string"/>
+        </set>
+	</class>
+
+</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithCollection.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithBasicCollections.java
similarity index 65%
rename from hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithCollection.java
rename to hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithBasicCollections.java
index 9ada02b520..6a23ccea39 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithCollection.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithBasicCollections.java
@@ -1,67 +1,86 @@
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
 
+import javax.persistence.ElementCollection;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import java.util.ArrayList;
+import java.util.Collection;
 import java.util.HashSet;
 import java.util.Set;
 
 /**
  * @author Gail Badner
+ * @author Steve Ebersole
  */
-public class EntityWithCollection {
+@Entity
+public class EntityWithBasicCollections {
 	private Long id;
 	private String name;
-	Set<String> otherNames = new HashSet<String>();
+	private Collection<String> theBag = new ArrayList<String>();
+	private Set<String> theSet = new HashSet<String>();
 
-	public EntityWithCollection() {
+	public EntityWithBasicCollections() {
 	}
 
-	public EntityWithCollection(String name) {
+	public EntityWithBasicCollections(String name) {
 		this.name = name;
 	}
 
+	@Id
 	public Long getId() {
 		return id;
 	}
 
 	public void setId(Long id) {
 		this.id = id;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
-	public Set<String> getOtherNames() {
-		return otherNames;
+	@ElementCollection
+	public Collection<String> getTheBag() {
+		return theBag;
 	}
 
-	public void setOtherNames(Set<String> otherNames) {
-		this.otherNames = otherNames;
+	public void setTheBag(Collection<String> theBag) {
+		this.theBag = theBag;
+	}
+
+	@ElementCollection
+	public Set<String> getTheSet() {
+		return theSet;
+	}
+
+	public void setTheSet(Set<String> theSet) {
+		this.theSet = theSet;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithCollection.hbm.xml b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithCollection.hbm.xml
deleted file mode 100644
index 1fec13b714..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/EntityWithCollection.hbm.xml
+++ /dev/null
@@ -1,18 +0,0 @@
-<?xml version="1.0"?>
-<hibernate-mapping package="org.hibernate.metamodel.binding" xmlns="http://www.hibernate.org/xsd/hibernate-mapping"
-                   xsi:schemaLocation="http://www.hibernate.org/xsd/hibernate-mapping hibernate-mapping-4.0.xsd"
-                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
-
-    <class name="EntityWithCollection">
-
-    	<id name="id">
-    		<generator class="increment"/>
-    	</id>
-        <property name="name"/>
-        <set name="otherNames">
-            <key column="pid"/>
-            <element column="otherNames" type="string"/>
-        </set>
-	</class>
-
-</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index c11f66ffff..29f2b031ad 100644
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
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
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
index 24c43bbdee..8ea49b15c9 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,562 +1,561 @@
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
 
+import javax.persistence.EntityManagerFactory;
+import javax.persistence.PersistenceException;
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Map;
 
-import javax.persistence.EntityManagerFactory;
-import javax.persistence.PersistenceException;
-
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
-import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
-import org.hibernate.persister.internal.PersisterClassResolverInitiator;
-import org.hibernate.persister.spi.PersisterClassResolver;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.internal.PersisterClassResolverInitiator;
+import org.hibernate.persister.spi.PersisterClassResolver;
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
-		public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
+		public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
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
