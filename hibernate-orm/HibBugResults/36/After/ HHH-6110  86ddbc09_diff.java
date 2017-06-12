diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index bb78df3c5c..8de17b587f 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1424 +1,1467 @@
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
 import java.util.LinkedHashSet;
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
 import org.hibernate.EntityMode;
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
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.integrator.spi.IntegratorService;
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
 	private final transient Map identifierGenerators;
 	private final transient Map namedQueries;
 	private final transient Map namedSqlQueries;
 	private final transient Map sqlResultSetMappings;
 	private final transient Map filters;
 	private final transient Map fetchProfiles;
 	private final transient Map imports;
 	private final transient Interceptor interceptor;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient EntityNotFoundDelegate entityNotFoundDelegate;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
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
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		this.settings = settings;
 		this.interceptor = cfg.getInterceptor();
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap();
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
 
+	public SessionFactoryImpl(
+			MetadataImplementor metadata,
+	        Mapping mapping,
+			ServiceRegistry serviceRegistry,
+			SessionFactoryObserver observer) throws HibernateException {
+        LOG.debug( "Building session factory" );
+
+		// TODO: remove initialization of final variables; just setting to null to make compiler happy
+		this.name = null;
+		this.uuid = null;
+		this.entityPersisters = null;
+		this.classMetadata = null;
+		this.collectionPersisters = null;
+		this.collectionMetadata = null;
+		this.collectionRolesByEntityParticipant = null;
+		this.identifierGenerators = null;
+		this.namedQueries = null;
+		this.namedSqlQueries = null;
+		this.sqlResultSetMappings = null;
+		this.filters = null;
+		this.fetchProfiles = null;
+		this.imports = null;
+		this.interceptor = null;
+		this.serviceRegistry = null;
+		this.settings = null;
+		this.properties = null;
+		this.queryCache = null;
+		this.updateTimestampsCache = null;
+		this.queryCaches = null;
+		this.currentSessionContext = null;
+		this.entityNotFoundDelegate = null;
+		this.sqlFunctionRegistry = null;
+		this.queryPlanCache = null;
+		this.typeResolver = null;
+		this.typeHelper = null;
+		this.transactionEnvironment = null;
+
+		// TODO: implement
+
+	}
+
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
 					EntityMode.POJO,			// we have to assume POJO
 					null, 						// and also assume non tenancy
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
 					EntityMode.POJO,			// we have to assume POJO
 					null,						// and also assume non tenancy
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
 		private EntityMode entityMode;
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
 			this.entityMode = settings.getDefaultEntityMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					entityMode,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 		}
 
 		@Override
 		public SessionBuilder interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			this.flushBeforeCompletion = flushBeforeCompletion;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder entityMode(EntityMode entityMode) {
 			this.entityMode = entityMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
index 5c48314641..f67886d9d7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
@@ -1,89 +1,89 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterDefs;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.type.Type;
 
 public class FilterDefBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			FilterDefBinder.class.getName()
 	);
 
 	/**
 	 * Binds all {@link FilterDefs} and {@link FilterDef} annotations to the supplied metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the jandex index
 	 */
 	public static void bind(MetadataImpl metadata,
 							Index jandex) {
 		for ( AnnotationInstance filterDef : jandex.getAnnotations( HibernateDotNames.FILTER_DEF ) ) {
 			bind( metadata, filterDef );
 		}
 		for ( AnnotationInstance filterDefs : jandex.getAnnotations( HibernateDotNames.FILTER_DEFS ) ) {
 			for ( AnnotationInstance filterDef : JandexHelper.getValueAsArray( filterDefs, "value" ) ) {
 				bind( metadata, filterDef );
 			}
 		}
 	}
 
 	private static void bind(MetadataImpl metadata,
 							 AnnotationInstance filterDef) {
 		String name = JandexHelper.getValueAsString( filterDef, "name" );
 		Map<String, Type> prms = new HashMap<String, Type>();
 		for ( AnnotationInstance prm : JandexHelper.getValueAsArray( filterDef, "parameters" ) ) {
 			prms.put(
 					JandexHelper.getValueAsString( prm, "name" ),
-					metadata.typeResolver().heuristicType( JandexHelper.getValueAsString( prm, "type" ) )
+					metadata.getTypeResolver().heuristicType( JandexHelper.getValueAsString( prm, "type" ) )
 			);
 		}
 		metadata.addFilterDef(
 				new FilterDefinition(
 						name,
 						JandexHelper.getValueAsString( filterDef, "defaultCondition" ),
 						prms
 				)
 		);
 		LOG.debugf( "Binding filter definition: %s", name );
 	}
 
 	private FilterDefBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 1089c00de8..7bdc8721dc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,269 +1,274 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.jboss.jandex.Index;
 import org.jboss.jandex.Indexer;
 import org.jboss.logging.Logger;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotation.xml.XMLEntityMappings;
 import org.hibernate.metamodel.source.annotations.AnnotationBinder;
 import org.hibernate.metamodel.source.annotations.xml.OrmXmlParser;
 import org.hibernate.metamodel.source.hbm.HibernateXmlBinder;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, MetadataImpl.class.getName());
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final Options options;
 
 	private final Database database = new Database();
     private TypeResolver typeResolver = new TypeResolver();
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports;
     private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
     private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
     private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
     private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
     private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			applyHibernateMappings( metadataSources, processedEntityNames );
 			applyAnnotationMappings( metadataSources, processedEntityNames );
 		}
 		else {
 			applyAnnotationMappings( metadataSources, processedEntityNames );
 			applyHibernateMappings( metadataSources, processedEntityNames );
 		}
 
 		new EntityReferenceResolver( this ).resolve();
 	}
 
     public void addFetchProfile( FetchProfile profile ) {
         fetchProfiles.put(profile.getName(), profile);
     }
 
     public void addFilterDef( FilterDefinition def ) {
         filterDefs.put(def.getFilterName(), def);
     }
 
+	public Map<String, FilterDefinition> getFilterDefinitions() {
+		return filterDefs;
+
+	}
+
     public void addIdGenerator( IdGenerator generator ) {
         idGenerators.put(generator.getName(), generator);
     }
 
     public void addNamedNativeQuery( String name,
                                      NamedSQLQueryDefinition def ) {
         namedNativeQueryDefs.put(name, def);
     }
 
     public void addNamedQuery( String name,
                                NamedQueryDefinition def ) {
         namedQueryDefs.put(name, def);
     }
 
     public void addTypeDef(String name, TypeDef typeDef) {
         // TODO - should we check whether the typedef already exists? Log it? Exception? (HF)
         typeDefs.put( name, typeDef );
     }
 
 	private void applyHibernateMappings(MetadataSources metadataSources, List<String> processedEntityNames) {
 		final HibernateXmlBinder hibernateXmlBinder = new HibernateXmlBinder( this );
 		for ( JaxbRoot jaxbRoot : metadataSources.getJaxbRootList() ) {
 			// filter to just hbm-based roots
 			if ( jaxbRoot.getRoot() instanceof XMLHibernateMapping ) {
 				hibernateXmlBinder.bindRoot( jaxbRoot );
 			}
 		}
 	}
 
 	private void applyAnnotationMappings(MetadataSources metadataSources, List<String> processedEntityNames) {
 		// create a jandex index from the annotated classes
 		Indexer indexer = new Indexer();
 		for ( Class<?> clazz : metadataSources.getAnnotatedClasses() ) {
 			indexClass( indexer, clazz.getName().replace( '.', '/' ) + ".class" );
 		}
 
 		// add package-info from the configured packages
 		for ( String packageName : metadataSources.getAnnotatedPackages() ) {
 			indexClass( indexer, packageName.replace( '.', '/' ) + "/package-info.class" );
 		}
 		Index index = indexer.complete();
 
 
 		List<JaxbRoot<XMLEntityMappings>> mappings = new ArrayList<JaxbRoot<XMLEntityMappings>>();
 		for ( JaxbRoot<?> root : metadataSources.getJaxbRootList() ) {
 			if ( root.getRoot() instanceof XMLEntityMappings ) {
 				mappings.add( (JaxbRoot<XMLEntityMappings>) root );
 			}
 		}
 		if ( !mappings.isEmpty() ) {
 			// process the xml configuration
 			final OrmXmlParser ormParser = new OrmXmlParser( this );
 			index = ormParser.parseAndUpdateIndex( mappings, index );
 		}
 
 		// create the annotation binder and pass it the final annotation index
 		final AnnotationBinder annotationBinder = new AnnotationBinder( this, index );
 		annotationBinder.bind();
 	}
 
 	/**
 	 * Adds a single class to the jandex index
 	 *
 	 * @param indexer the jandex indexer
 	 * @param className the fully qualified name of the class
 	 */
 	private void indexClass(Indexer indexer, String className) {
 		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		InputStream stream = classLoaderService.locateResourceStream( className );
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw new HibernateException( "Unable to open input stream for class " + className, e );
 		}
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		// todo : implement!!!!
 		return null;
 	}
 
 	@Override
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
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
 
 	public Iterable<PluralAttributeBinding> getCollections() {
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
 		if ( imports == null ) {
 			imports = new HashMap<String, String>();
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 	public TypeDef getTypeDef(String name) {
 		return typeDefs.get( name );
 	}
 
     public Iterable<FetchProfile> getFetchProfiles() {
         return fetchProfiles.values();
     }
 
-    public TypeResolver typeResolver() {
+    public TypeResolver getTypeResolver() {
         return typeResolver;
     }
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
index 2b254004fb..e20823bcf4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
@@ -1,50 +1,58 @@
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
 package org.hibernate.metamodel.source.spi;
 
+import java.util.Map;
+
+import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.type.TypeResolver;
 
 /**
  * @author Steve Ebersole
  */
 public interface MetadataImplementor extends Metadata {
 	public BasicServiceRegistry getServiceRegistry();
 	public Database getDatabase();
 
 	public Iterable<EntityBinding> getEntityBindings();
 	public EntityBinding getEntityBinding(String entityName);
 
+	public TypeResolver getTypeResolver();
+
+	public Map<String, FilterDefinition> getFilterDefinitions();
+
 	public void addImport(String entityName, String entityName1);
 
 	public void addEntity(EntityBinding entityBinding);
 
 	public void addCollection(PluralAttributeBinding collectionBinding);
 
     public void addFetchProfile( FetchProfile profile );
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index 73d09b2c37..271c78856c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,183 +1,248 @@
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
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
+	 * The constructor signature for {@link EntityPersister} implementations using
+	 * an {@link EntityBinding}.
+	 *
+	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
+	 * @todo change ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW to ENTITY_PERSISTER_CONSTRUCTOR_ARGS
+	 * when new metamodel is integrated
+	 */
+	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
+			EntityBinding.class,
+			EntityRegionAccessStrategy.class,
+			SessionFactoryImplementor.class,
+			Mapping.class
+	};
+
+	/**
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
 
+	/**
+	 * The constructor signature for {@link CollectionPersister} implementations using
+	 * a {@link PluralAttributeBinding}
+	 *
+	 * @todo still need to make collection persisters EntityMode-aware
+	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
+	 * @todo change COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW to COLLECTION_PERSISTER_CONSTRUCTOR_ARGS
+	 * when new metamodel is integrated
+	 */
+	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
+			PluralAttributeBinding.class,
+			CollectionRegionAccessStrategy.class,
+			Configuration.class,
+			SessionFactoryImplementor.class
+	};
+
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
-		return create( persisterClass, metadata, cacheAccessStrategy, factory, cfg );
+		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public EntityPersister createEntityPersister(EntityBinding metadata,
+												 EntityRegionAccessStrategy cacheAccessStrategy,
+												 SessionFactoryImplementor factory,
+												 Mapping cfg) {
+		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
+		if ( persisterClass == null ) {
+			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
+		}
+		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, cacheAccessStrategy, factory, cfg );
+	}
+
+	// TODO: change metadata arg type to EntityBinding when new metadata is integrated
 	private static EntityPersister create(
 			Class<? extends EntityPersister> persisterClass,
-			PersistentClass metadata,
+			Class[] persisterConstructorArgs,
+			Object metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException {
 		try {
-			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( ENTITY_PERSISTER_CONSTRUCTOR_ARGS );
+			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
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
 			Collection metadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = metadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( metadata );
 		}
 
-		return create( persisterClass, cfg, metadata, cacheAccessStrategy, factory );
+		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS, cfg, metadata, cacheAccessStrategy, factory );
 	}
 
+	@Override
+	@SuppressWarnings( {"unchecked"})
+
+	public CollectionPersister createCollectionPersister(Configuration cfg,
+														 PluralAttributeBinding metadata,
+														 CollectionRegionAccessStrategy cacheAccessStrategy,
+														 SessionFactoryImplementor factory) throws HibernateException {
+		Class<? extends CollectionPersister> persisterClass = metadata.getCollectionPersisterClass();
+		if ( persisterClass == null ) {
+			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( metadata );
+		}
+
+		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW, cfg, metadata, cacheAccessStrategy, factory );
+	}
+
+	// TODO: change metadata arg type to PluralAttributeBinding when new metadata is integrated
 	private static CollectionPersister create(
 			Class<? extends CollectionPersister> persisterClass,
+			Class[] persisterConstructorArgs,
 			Configuration cfg,
-			Collection metadata,
+			Object metadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		try {
-			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( COLLECTION_PERSISTER_CONSTRUCTOR_ARGS );
+			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( metadata, cacheAccessStrategy, cfg, factory );
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
index 6261b85578..9c6ab9b647 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
@@ -1,88 +1,117 @@
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
+import org.hibernate.metamodel.binding.EntityBinding;
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
+
+	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
+		// todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
+		switch ( metadata.getInheritanceType() ) {
+			case JOINED: {
+				joinedSubclassEntityPersister();
+			}
+			case SINGLE_TABLE: {
+				return singleTableEntityPersister();
+			}
+			case TABLE_PER_CLASS: {
+				return unionSubclassEntityPersister();
+			}
+			default: {
+				throw new UnknownPersisterException(
+						"Could not determine persister implementation for entity [" + metadata.getEntity().getName() + "]"
+				);
+			}
+
+		}
+	}
+
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
 
+	@Override
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+		return metadata.getCollectionElement().isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
+	}
+
 	private Class<OneToManyPersister> oneToManyPersister() {
 		return OneToManyPersister.class;
 	}
 
 	private Class<BasicCollectionPersister> basicCollectionPersister() {
 		return BasicCollectionPersister.class;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
index 9ec5515f84..f82123e71e 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
@@ -1,62 +1,84 @@
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
+import org.hibernate.metamodel.binding.EntityBinding;
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
+	 * Returns the entity persister class for a given entityName or null
+	 * if the entity persister class should be the default.
+	 *
+	 * @param metadata The entity metadata
+	 *
+	 * @return The entity persister class to use
+	 */
+	Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata);
+
+	/**
 	 * Returns the collection persister class for a given collection role or null
 	 * if the collection persister class should be the default.
 	 *
 	 * @param metadata The collection metadata
 	 *
 	 * @return The collection persister class to use
 	 */
 	Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata);
+
+	/**
+	 * Returns the collection persister class for a given collection role or null
+	 * if the collection persister class should be the default.
+	 *
+	 * @param metadata The collection metadata
+	 *
+	 * @return The collection persister class to use
+	 */
+	Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index 01a043be59..b8cf709380 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
@@ -1,89 +1,128 @@
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
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
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
+	 * Create an entity persister instance.
+	 *
+	 * @param model The O/R mapping metamodel definition for the entity
+	 * @param cacheAccessStrategy The caching strategy for this entity
+	 * @param factory The session factory
+	 * @param cfg The overall mapping
+	 *
+	 * @return An appropriate entity persister instance.
+	 *
+	 * @throws HibernateException Indicates a problem building the persister.
+	 */
+	public EntityPersister createEntityPersister(
+			EntityBinding model,
+			EntityRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory,
+			Mapping cfg) throws HibernateException;
+
+	/**
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
+
+	/**
+	 * Create a collection persister instance.
+	 *
+	 * @param cfg The configuration
+	 * @param model The O/R mapping metamodel definition for the collection
+	 * @param cacheAccessStrategy The caching strategy for this collection
+	 * @param factory The session factory
+	 *
+	 * @return An appropriate collection persister instance.
+	 *
+	 * @throws HibernateException Indicates a problem building the persister.
+	 */
+	public CollectionPersister createCollectionPersister(
+			Configuration cfg,
+			PluralAttributeBinding model,
+			CollectionRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory) throws HibernateException;
+
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index f703d97874..2be4699351 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,701 +1,713 @@
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
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
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
+	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
+		return NoopEntityPersister.class;
+	}
+
+	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
+	@Override
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+		return NoopCollectionPersister.class;
+	}
+
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRootEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMetamodel getEntityMetamodel() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSubclassEntityName(String entityName) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasProxy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasMutableProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasSubselectLoadableCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCascades() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInherited() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isIdentifierAssignedByInsert() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIdentifierProperty() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean canExtractIdOutOfEntity() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Comparator getVersionComparator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public VersionType getVersionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getVersionProperty() {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasNaturalIdentifier() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasLazyProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type[] getPropertyTypes() {
 			return new Type[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getPropertyNames() {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierPropertyName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCacheInvalidationRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazyPropertiesCacheable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ClassMetadata getClassMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isBatchLoadable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMode guessEntityMode(Object object) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstrumented(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasInsertGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUpdateGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersionPropertyGenerated() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterReassociate(Object entity, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 				throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getMappedClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsLifecycle(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsValidatable(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getConcreteProxyClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstance(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
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
index ea4dcbb518..f563be033e 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,492 +1,504 @@
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
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
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
+		public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
+			return GoofyProvider.class;
+		}
+
+		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 			return null;
 		}
+
+		@Override
+		public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+			return null;
+		}
 	}
 
 	public static class GoofyProvider implements EntityPersister {
 
 		public GoofyProvider(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException();
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRootEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMetamodel getEntityMetamodel() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSubclassEntityName(String entityName) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasProxy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasMutableProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasSubselectLoadableCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCascades() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInherited() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isIdentifierAssignedByInsert() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIdentifierProperty() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean canExtractIdOutOfEntity() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Comparator getVersionComparator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public VersionType getVersionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getVersionProperty() {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasNaturalIdentifier() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasLazyProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type[] getPropertyTypes() {
 			return new Type[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getPropertyNames() {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierPropertyName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCacheInvalidationRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazyPropertiesCacheable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ClassMetadata getClassMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isBatchLoadable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMode guessEntityMode(Object object) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstrumented(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasInsertGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUpdateGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersionPropertyGenerated() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterReassociate(Object entity, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 				throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getMappedClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsLifecycle(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsValidatable(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getConcreteProxyClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstance(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 
 	public static class GoofyException extends RuntimeException {
 
 	}
 }
