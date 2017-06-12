diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index e52bc7bc9f..4e945f2347 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,541 +1,547 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Metamodel;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
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
 import org.hibernate.graph.spi.EntityGraphImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.metamodel.spi.MetamodelImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.query.spi.NamedQueryRepository;
 import org.hibernate.query.spi.QueryParameterBindingTypeResolver;
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
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory, QueryParameterBindingTypeResolver {
 	/**
 	 * Get the UUID for this SessionFactory.  The value is generated as a {@link java.util.UUID}, but kept
 	 * as a String.
 	 *
 	 * @return The UUID for this SessionFactory.
 	 *
 	 * @see org.hibernate.internal.SessionFactoryRegistry#getSessionFactory
 	 */
 	String getUuid();
 
 	/**
 	 * Access to the name (if one) assigned to the SessionFactory
 	 *
 	 * @return The name for the SessionFactory
 	 */
 	String getName();
 
 	@Override
 	SessionBuilderImplementor withOptions();
 
 	/**
 	 * Get a non-transactional "current" session (used by hibernate-envers)
 	 */
 	Session openTemporarySession() throws HibernateException;
 
 	@Override
 	CacheImplementor getCache();
 
 	@Override
 	StatisticsImplementor getStatistics();
 
 	/**
 	 * Access to the ServiceRegistry for this SessionFactory.
 	 *
 	 * @return The factory's ServiceRegistry
 	 */
 	ServiceRegistryImplementor getServiceRegistry();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 *
 	 * @deprecated (since 5.2) if access to the SessionFactory-scoped Interceptor is needed, use
 	 * {@link SessionFactoryOptions#getInterceptor()} instead.  However, generally speaking this access
 	 * is not needed.
 	 */
 	@Deprecated
 	Interceptor getInterceptor();
 
 	/**
 	 * Access to the cachres of HQL/JPQL and native query plans.
 	 *
 	 * @return The query plan cache
 	 */
 	QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Provides access to the named query repository
 	 *
 	 * @return The repository for named query definitions
 	 */
 	NamedQueryRepository getNamedQueryRepository();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	FetchProfile getFetchProfile(String name);
 
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	TypeResolver getTypeResolver();
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 
 	EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	SQLFunctionRegistry getSqlFunctionRegistry();
 
 
 	void addObserver(SessionFactoryObserver observer);
 
 	/**
 	 * @todo make a Service ?
 	 */
 	CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	/**
 	 * @todo make a Service ?
 	 */
 	CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 
-	Iterable<EntityNameResolver> iterateEntityNameResolvers();
+	/**
+	 * @deprecated (since 5.2) use {@link #getMetamodel()} -> {@link MetamodelImplementor#getEntityNameResolvers()}
+	 */
+	@Deprecated
+	default Iterable<EntityNameResolver> iterateEntityNameResolvers() {
+		return getMetamodel().getEntityNameResolvers();
+	}
 
 	/**
 	 * Contract for resolving this SessionFactory on deserialization
 	 */
 	interface DeserializationResolver<T extends SessionFactoryImplementor> extends Serializable {
 		T resolve();
 	}
 
 	DeserializationResolver getDeserializationResolver();
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Deprecations
 
 	/**
 	 * Get the return types of a query
 	 *
 	 * @deprecated No replacement.
 	 */
 	@Deprecated
 	default Type[] getReturnTypes(String queryString) {
 		throw new UnsupportedOperationException( "Concept of query return org.hibernate.type.Types is no longer supported" );
 	}
 
 	/**
 	 * Get the return aliases of a query
 	 *
 	 * @deprecated No replacement.
 	 */
 	@Deprecated
 	default String[] getReturnAliases(String queryString) {
 		throw new UnsupportedOperationException( "Access to of query return aliases via Sessionfactory is no longer supported" );
 	}
 
 
 
 	/**
 	 * @deprecated Just use {@link #getStatistics} (with covariant return here as {@link StatisticsImplementor}).
 	 */
 	@Deprecated
 	default StatisticsImplementor getStatisticsImplementor() {
 		return getStatistics();
 	}
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// NamedQueryRepository
 
 	/**
 	 * @deprecated (since 5.2) Use {@link NamedQueryRepository#getNamedQueryDefinition(java.lang.String)} instead.
 	 */
 	@Deprecated
 	default NamedQueryDefinition getNamedQuery(String queryName) {
 		return getNamedQueryRepository().getNamedQueryDefinition( queryName );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link NamedQueryRepository#registerNamedQueryDefinition} instead.
 	 */
 	@Deprecated
 	default void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		getNamedQueryRepository().registerNamedQueryDefinition( name, definition );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link NamedQueryRepository#getNamedSQLQueryDefinition} instead.
 	 */
 	@Deprecated
 	default NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return getNamedQueryRepository().getNamedSQLQueryDefinition( queryName );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link NamedQueryRepository#registerNamedSQLQueryDefinition} instead.
 	 */
 	@Deprecated
 	default void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		getNamedQueryRepository().registerNamedSQLQueryDefinition( name, definition );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link NamedQueryRepository#getResultSetMappingDefinition} instead.
 	 */
 	@Deprecated
 	default ResultSetMappingDefinition getResultSetMapping(String name) {
 		return getNamedQueryRepository().getResultSetMappingDefinition( name );
 	}
 
 	/**
 	 * Get the JdbcServices.
 	 *
 	 * @return the JdbcServices
 	 */
 	JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@code getJdbcServices().getDialect()}
 	 *
 	 * @return The dialect
 	 *
 	 * @deprecated (since 5.2) instead, use this factory's {{@link #getServiceRegistry()}} ->
 	 * {@link JdbcServices#getDialect()}
 	 */
 	@Deprecated
 	default Dialect getDialect() {
 		if ( getServiceRegistry() == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 
 		return getServiceRegistry().getService( JdbcServices.class ).getDialect();
 	}
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 * @deprecated since 5.0; use {@link JdbcServices#getSqlExceptionHelper()} ->
 	 * {@link SqlExceptionHelper#getSqlExceptionConverter()} instead as obtained from {@link #getServiceRegistry()}
 	 */
 	@Deprecated
 	default SQLExceptionConverter getSQLExceptionConverter() {
 		return getServiceRegistry().getService( JdbcServices.class ).getSqlExceptionHelper().getSqlExceptionConverter();
 	}
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 * @deprecated since 5.0; use {@link JdbcServices#getSqlExceptionHelper()} instead as
 	 * obtained from {@link #getServiceRegistry()}
 	 */
 	@Deprecated
 	default SqlExceptionHelper getSQLExceptionHelper() {
 		return getServiceRegistry().getService( JdbcServices.class ).getSqlExceptionHelper();
 	}
 
 	/**
 	 * @deprecated since 5.0; use {@link #getSessionFactoryOptions()} instead
 	 */
 	@Deprecated
 	@SuppressWarnings("deprecation")
 	Settings getSettings();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// map these to Metamodel
 
 
 	@Override
 	MetamodelImplementor getMetamodel();
 
 	/**
 	 * @deprecated (since 5.2) Use {@link MetamodelImplementor#entityPersister(Class)} instead.
 	 */
 	@Deprecated
 	default EntityPersister getEntityPersister(String entityName) throws MappingException {
 		return getMetamodel().entityPersister( entityName );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link MetamodelImplementor#entityPersisters} instead.
 	 */
 	@Deprecated
 	default Map<String,EntityPersister> getEntityPersisters() {
 		return getMetamodel().entityPersisters();
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link MetamodelImplementor#collectionPersister(String)} instead.
 	 */
 	@Deprecated
 	default CollectionPersister getCollectionPersister(String role) throws MappingException {
 		return getMetamodel().collectionPersister( role );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link MetamodelImplementor#collectionPersisters} instead.
 	 */
 	@Deprecated
 	default Map<String, CollectionPersister> getCollectionPersisters() {
 		return getMetamodel().collectionPersisters();
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link MetamodelImplementor#collectionPersisters} instead.
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	default Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return getMetamodel().getCollectionRolesByEntityParticipant( entityName );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link MetamodelImplementor#locateEntityPersister(Class)} instead.
 	 */
 	@Deprecated
 	default EntityPersister locateEntityPersister(Class byClass) {
 		return getMetamodel().locateEntityPersister( byClass );
 	}
 
 	/**
 	 * @deprecated (since 5.2) Use {@link MetamodelImplementor#locateEntityPersister(String)} instead.
 	 */
 	@Deprecated
 	default EntityPersister locateEntityPersister(String byName) {
 		return getMetamodel().locateEntityPersister( byName );
 	}
 
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 *
 	 * @deprecated Use {@link Metamodel#getImplementors(java.lang.String)} instead
 	 */
 	@Deprecated
 	default String[] getImplementors(String entityName) {
 		return getMetamodel().getImplementors( entityName );
 	}
 
 	/**
 	 * Get a class name, using query language imports
 	 *
 	 * @deprecated Use {@link Metamodel#getImportedClassName(java.lang.String)} instead
 	 */
 	@Deprecated
 	default String getImportedClassName(String name) {
 		return getMetamodel().getImportedClassName( name );
 	}
 
 	EntityGraphImplementor findEntityGraphByName(String name);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Move to CacheImplementor calls
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 *
 	 * @return The name of the region
 	 *
 	 * @deprecated (since 5.2) Use this factory's {@link #getCache()} reference
 	 * to access Region via {@link CacheImplementor#determineEntityRegionAccessStrategy} or
 	 * {@link CacheImplementor#determineCollectionRegionAccessStrategy} instead.
 	 */
 	@Deprecated
 	default Region getSecondLevelCacheRegion(String regionName) {
 		final EntityRegionAccessStrategy entityRegionAccess = getCache().getEntityRegionAccess( regionName );
 		if ( entityRegionAccess != null ) {
 			return entityRegionAccess.getRegion();
 		}
 
 		final CollectionRegionAccessStrategy collectionRegionAccess = getCache().getCollectionRegionAccess( regionName );
 		if ( collectionRegionAccess != null ) {
 			return collectionRegionAccess.getRegion();
 		}
 
 		return null;
 	}
 
 	/**
 	 * Find the "access strategy" for the named cache region.
 	 *
 	 * @param regionName The name of the region
 	 *
 	 * @return That region's "access strategy"
 	 *
 	 *
 	 * @deprecated (since 5.2) Use this factory's {@link #getCache()} reference
 	 * to access {@link CacheImplementor#determineEntityRegionAccessStrategy} or
 	 * {@link CacheImplementor#determineCollectionRegionAccessStrategy} instead.
 	 */
 	@Deprecated
 	default RegionAccessStrategy getSecondLevelCacheRegionAccessStrategy(String regionName) {
 		final EntityRegionAccessStrategy entityRegionAccess = getCache().getEntityRegionAccess( regionName );
 		if ( entityRegionAccess != null ) {
 			return entityRegionAccess;
 		}
 
 		final CollectionRegionAccessStrategy collectionRegionAccess = getCache().getCollectionRegionAccess( regionName );
 		if ( collectionRegionAccess != null ) {
 			return collectionRegionAccess;
 		}
 
 		return null;
 	}
 
 	/**
 	 * Get a named natural-id cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 *
 	 * @return The region
 	 *
 	 * @deprecated (since 5.2) Use this factory's {@link #getCache()} ->
 	 * {@link CacheImplementor#getNaturalIdCacheRegionAccessStrategy(String)} ->
 	 * {@link NaturalIdRegionAccessStrategy#getRegion()} instead.
 	 */
 	@Deprecated
 	default Region getNaturalIdCacheRegion(String regionName) {
 		return getCache().getNaturalIdCacheRegionAccessStrategy( regionName ).getRegion();
 	}
 
 	/**
 	 * Find the "access strategy" for the named naturalId cache region.
 	 *
 	 * @param regionName The region name
 	 *
 	 * @return That region's "access strategy"
 	 *
 	 * @deprecated (since 5.2) Use this factory's {@link #getCache()} ->
 	 * {@link CacheImplementor#getNaturalIdCacheRegionAccessStrategy(String)} instead.
 	 */
 	@Deprecated
 	default RegionAccessStrategy getNaturalIdCacheRegionAccessStrategy(String regionName) {
 		return getCache().getNaturalIdCacheRegionAccessStrategy( regionName );
 	}
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 *
 	 * @deprecated (since 5.2) with no direct replacement; use this factory's {@link #getCache()} reference
 	 * to access cache objects as needed.
 	 */
 	@Deprecated
 	Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Get the default query cache.
 	 *
 	 * @deprecated Use {@link CacheImplementor#getDefaultQueryCache()} instead
 	 */
 	@Deprecated
 	default QueryCache getQueryCache() {
 		return getCache().getDefaultQueryCache();
 	}
 
 	/**
 	 * Get a particular named query cache, or the default cache
 	 *
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 *
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 *
 	 * @deprecated Use {@link CacheImplementor#getQueryCache(String)} instead
 	 */
 	@Deprecated
 	default QueryCache getQueryCache(String regionName) {
 		return getCache().getQueryCache( regionName );
 	}
 
 	/**
 	 * Get the cache of table update timestamps
 	 *
 	 * @deprecated Use {@link CacheImplementor#getUpdateTimestampsCache()} instead
 	 */
 	@Deprecated
 	default UpdateTimestampsCache getUpdateTimestampsCache() {
 		return getCache().getUpdateTimestampsCache();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoordinatingEntityNameResolver.java b/hibernate-core/src/main/java/org/hibernate/internal/CoordinatingEntityNameResolver.java
index bd061b81f4..da9bb214a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoordinatingEntityNameResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoordinatingEntityNameResolver.java
@@ -1,46 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class CoordinatingEntityNameResolver implements EntityNameResolver {
 	private final SessionFactoryImplementor sessionFactory;
 	private final Interceptor interceptor;
 
 	public CoordinatingEntityNameResolver(SessionFactoryImplementor sessionFactory, Interceptor interceptor) {
 		this.sessionFactory = sessionFactory;
 		this.interceptor = interceptor;
 	}
 
 	@Override
 	public String resolveEntityName(Object entity) {
 		String entityName = interceptor.getEntityName( entity );
 		if ( entityName != null ) {
 			return entityName;
 		}
 
-		for ( EntityNameResolver resolver : sessionFactory.iterateEntityNameResolvers() ) {
+		for ( EntityNameResolver resolver : sessionFactory.getMetamodel().getEntityNameResolvers() ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 37ad266e4f..686a4042a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1394 +1,1360 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
-import java.util.concurrent.ConcurrentHashMap;
-import java.util.concurrent.ConcurrentMap;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import javax.persistence.EntityGraph;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceUnitUtil;
 import javax.persistence.Query;
 import javax.persistence.SynchronizationType;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.ConnectionAcquisitionMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
-import org.hibernate.EntityNameResolver;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
 import org.hibernate.boot.cfgxml.spi.LoadedConfig;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.ReturnMetadata;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.graph.spi.EntityGraphImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.util.config.ConfigurationException;
 import org.hibernate.jpa.internal.AfterCompletionActionLegacyJpaImpl;
 import org.hibernate.jpa.internal.ExceptionMapperLegacyJpaImpl;
 import org.hibernate.jpa.internal.ManagedFlushCheckerLegacyJpaImpl;
 import org.hibernate.jpa.internal.PersistenceUnitUtilImpl;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.internal.MetamodelImpl;
 import org.hibernate.metamodel.spi.MetamodelImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
 import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.backend.jta.internal.synchronization.AfterCompletionAction;
 import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ExceptionMapper;
 import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ManagedFlushChecker;
 import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.secure.spi.JaccService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.schema.spi.DelayedDropAction;
 import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
-import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.SerializableType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 import org.jboss.logging.Logger;
 
 import static org.hibernate.metamodel.internal.JpaMetaModelPopulationSetting.determineJpaMetaModelPopulationSetting;
 
 
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
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class SessionFactoryImpl implements SessionFactoryImplementor {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( SessionFactoryImpl.class );
 
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 	private transient boolean isClosed;
 
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 	private final transient Settings settings;
 	private final transient Map<String,Object> properties;
 
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private transient JdbcServices jdbcServices;
 
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 
 	// todo : org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor too?
 
 	private final transient MetamodelImpl metamodel;
 	private final transient CriteriaBuilderImpl criteriaBuilder;
 	private final transient PersistenceUnitUtilImpl persistenceUnitUtil;
 	private final transient CacheImplementor cacheAccess;
 	private final transient org.hibernate.query.spi.NamedQueryRepository namedQueryRepository;
 	private final transient QueryPlanCache queryPlanCache;
 
 	private final transient CurrentSessionContext currentSessionContext;
 
 	private DelayedDropAction delayedDropAction;
 
 	// todo : move to MetamodelImpl
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
-	private final transient ConcurrentMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<>();
 
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 
 
 	public SessionFactoryImpl(final MetadataImplementor metadata, SessionFactoryOptions options) {
 		LOG.debug( "Building session factory" );
 
 		this.sessionFactoryOptions = options;
 		this.settings = new Settings( options, metadata );
 
 		this.serviceRegistry = options.getServiceRegistry()
 				.getService( SessionFactoryServiceRegistryFactory.class )
 				.buildServiceRegistry( this, options );
 
 		final CfgXmlAccessService cfgXmlAccessService = serviceRegistry.getService( CfgXmlAccessService.class );
 
 		String sfName = settings.getSessionFactoryName();
 		if ( cfgXmlAccessService.getAggregatedConfig() != null ) {
 			if ( sfName == null ) {
 				sfName = cfgXmlAccessService.getAggregatedConfig().getSessionFactoryName();
 			}
 			applyCfgXmlValues( cfgXmlAccessService.getAggregatedConfig(), serviceRegistry );
 		}
 
 		this.name = sfName;
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		this.properties = new HashMap<>();
 		this.properties.putAll( serviceRegistry.getService( ConfigurationService.class ).getSettings() );
 
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( jdbcServices.getJdbcEnvironment().getDialect(), options.getCustomSqlFunctionMap() );
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		this.criteriaBuilder = new CriteriaBuilderImpl( this );
 		this.persistenceUnitUtil = new PersistenceUnitUtilImpl( this );
 
 		for ( SessionFactoryObserver sessionFactoryObserver : options.getSessionFactoryObservers() ) {
 			this.observer.addObserver( sessionFactoryObserver );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<>();
 		this.filters.putAll( metadata.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 				integrators.clear();
 			}
 		}
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( metadata, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 		//Generators:
 
 		this.identifierGenerators = new HashMap<>();
 		metadata.getEntityBindings().stream().filter( model -> !model.isInherited() ).forEach( model -> {
 			IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 					metadata.getIdentifierGeneratorFactory(),
 					jdbcServices.getJdbcEnvironment().getDialect(),
 					settings.getDefaultCatalogName(),
 					settings.getDefaultSchemaName(),
 					(RootClass) model
 			);
 			identifierGenerators.put( model.getEntityName(), generator );
 		} );
 
 
 		//Named Queries:
 		this.namedQueryRepository = metadata.buildNamedQueryRepository( this );
 
 
 		LOG.debug( "Instantiated session factory" );
 
 		this.metamodel = new MetamodelImpl( this );
 		this.metamodel.initialize( metadata, determineJpaMetaModelPopulationSetting( properties ) );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				metadata,
 				sessionFactoryOptions
 		);
 
 		SchemaManagementToolCoordinator.process(
 				metadata,
 				serviceRegistry,
 				properties,
 				action -> SessionFactoryImpl.this.delayedDropAction = action
 		);
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( sep ).append( entry.getKey() );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen afterQuery persisters are all ready to go...
 		this.fetchProfiles = new HashMap<>();
 		for ( org.hibernate.mapping.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = metamodel.getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null
 						? null
 						: metamodel.entityPersister( entityName );
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
 				((Loadable) owner).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.observer.sessionFactoryCreated( this );
 
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 	}
 
 	private void applyCfgXmlValues(LoadedConfig aggregatedConfig, SessionFactoryServiceRegistry serviceRegistry) {
 		final JaccService jaccService = serviceRegistry.getService( JaccService.class );
 		if ( jaccService.getContextId() != null ) {
 			final JaccPermissionDeclarations permissions = aggregatedConfig.getJaccPermissions( jaccService.getContextId() );
 			if ( permissions != null ) {
 				for ( GrantedPermission grantedPermission : permissions.getPermissionDeclarations() ) {
 					jaccService.addPermission( grantedPermission );
 				}
 			}
 		}
 
 		if ( aggregatedConfig.getEventListenerMap() != null ) {
 			final ClassLoaderService cls = serviceRegistry.getService( ClassLoaderService.class );
 			final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 			for ( Map.Entry<EventType, Set<String>> entry : aggregatedConfig.getEventListenerMap().entrySet() ) {
 				final EventListenerGroup group = eventListenerRegistry.getEventListenerGroup( entry.getKey() );
 				for ( String listenerClassName : entry.getValue() ) {
 					try {
 						group.appendListener( cls.classForName( listenerClassName ).newInstance() );
 					}
 					catch (Exception e) {
 						throw new ConfigurationException( "Unable to instantiate event listener class : " + listenerClassName, e );
 					}
 				}
 			}
 		}
 	}
 
 	private JdbcConnectionAccess buildLocalConnectionAccess() {
 		return new JdbcConnectionAccess() {
 			@Override
 			public Connection obtainConnection() throws SQLException {
 				return settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE
 						? serviceRegistry.getService( ConnectionProvider.class ).getConnection()
 						: serviceRegistry.getService( MultiTenantConnectionProvider.class ).getAnyConnection();
 			}
 
 			@Override
 			public void releaseConnection(Connection connection) throws SQLException {
 				if ( settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE ) {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				else {
 					serviceRegistry.getService( MultiTenantConnectionProvider.class ).releaseAnyConnection( connection );
 				}
 			}
 
 			@Override
 			public boolean supportsAggressiveRelease() {
 				return false;
 			}
 		};
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
 	public SessionBuilderImplementor withOptions() {
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
 
 	@Override
 	public Map<String, Object> getProperties() {
 		validateNotClosed();
 		return properties;
 	}
 
 	protected void validateNotClosed() {
 		if ( isClosed ) {
 			throw new IllegalStateException( "EntityManagerFactory is closed" );
 		}
 	}
 
 	@Override
 	public String getUuid() {
 		return uuid;
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		if ( jdbcServices == null ) {
 			jdbcServices = getServiceRegistry().getService( JdbcServices.class );
 		}
 		return jdbcServices;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
-	private void registerEntityNameResolvers(EntityPersister persister) {
-		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
-			return;
-		}
-		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
-	}
-
-	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
-		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
-		if ( resolvers == null ) {
-			return;
-		}
-
-		for ( EntityNameResolver resolver : resolvers ) {
-			registerEntityNameResolver( resolver );
-		}
-	}
-
-	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
-
-	public void registerEntityNameResolver(EntityNameResolver resolver) {
-		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
-	}
-
-	@Override
-	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
-		return entityNameResolvers.keySet();
-	}
-
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		return namedQueryRepository.checkNamedQueries( queryPlanCache );
 	}
 
 	@Override
 	public DeserializationResolver getDeserializationResolver() {
 		return new DeserializationResolver() {
 			@Override
 			public SessionFactoryImplementor resolve() {
 				return (SessionFactoryImplementor) SessionFactoryRegistry.INSTANCE.findSessionFactory( uuid, name );
 			}
 		};
 	}
 
 	@SuppressWarnings("deprecation")
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
 		return null;
 	}
 
 
 
 	// todo : (5.2) review synchronizationType, persistenceContextType, transactionType usage
 
 	// SynchronizationType -> should we auto enlist in transactions
 	private transient SynchronizationType synchronizationType;
 
 	// PersistenceContextType -> influences FlushMode and 'autoClose'
 	private transient PersistenceContextType persistenceContextType;
 
 
 	@Override
 	public Session createEntityManager() {
 		return buildEntityManager( SynchronizationType.SYNCHRONIZED, Collections.emptyMap() );
 	}
 
 	private Session buildEntityManager(SynchronizationType synchronizationType, Map map) {
 		SessionBuilderImplementor builder = withOptions();
 		if ( synchronizationType == SynchronizationType.SYNCHRONIZED ) {
 			builder.autoJoinTransactions( true );
 		}
 		else {
 			builder.autoJoinTransactions( false );
 		}
 		return builder.openSession();
 	}
 
 	@Override
 	public Session createEntityManager(Map map) {
 		return buildEntityManager( SynchronizationType.SYNCHRONIZED, map );
 	}
 
 	@Override
 	public Session createEntityManager(SynchronizationType synchronizationType) {
 		errorIfResourceLocalDueToExplicitSynchronizationType();
 		return buildEntityManager( synchronizationType, Collections.emptyMap() );
 	}
 
 	private void errorIfResourceLocalDueToExplicitSynchronizationType() {
 		// JPA requires that we throw IllegalStateException in cases where:
 		//		1) the PersistenceUnitTransactionType (TransactionCoordinator) is non-JTA
 		//		2) an explicit SynchronizationType is specified
 		if ( !getServiceRegistry().getService( TransactionCoordinatorBuilder.class ).isJta() ) {
 			throw new IllegalStateException(
 					"Illegal attempt to specify a SynchronizationType when building an EntityManager from a " +
 							"EntityManagerFactory defined as RESOURCE_LOCAL (as opposed to JTA)"
 			);
 		}
 	}
 
 	@Override
 	public Session createEntityManager(SynchronizationType synchronizationType, Map map) {
 		errorIfResourceLocalDueToExplicitSynchronizationType();
 		return buildEntityManager( synchronizationType, map );
 	}
 
 	@Override
 	public CriteriaBuilder getCriteriaBuilder() {
 		return criteriaBuilder;
 	}
 
 	@Override
 	public MetamodelImplementor getMetamodel() {
 		return metamodel;
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed;
 	}
 
 	@Override
 	public EntityGraphImplementor findEntityGraphByName(String name) {
 		return null;
 	}
 
 	@Override
 	public Map getAllSecondLevelCacheRegions() {
 		return null;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
 	}
 
 	@Override
 	public Reference getReference() {
 		// from javax.naming.Referenceable
 		LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", getUuid()),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	@Override
 	public org.hibernate.query.spi.NamedQueryRepository getNamedQueryRepository() {
 		return namedQueryRepository;
 	}
 
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getMetamodel().entityPersister( className ).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getMetamodel().entityPersister( className ).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		final ReturnMetadata metadata = queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata();
 		return metadata == null ? null : metadata.getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		final ReturnMetadata metadata = queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata();
 		return metadata == null ? null : metadata.getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return (CollectionMetadata) getMetamodel().collectionPersister( roleName );
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return (ClassMetadata) getMetamodel().entityPersister( entityName );
 	}
 
 	@Override
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		throw new UnsupportedOperationException( "org.hibernate.SessionFactory.getAllClassMetadata is no longer supported" );
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		throw new UnsupportedOperationException( "org.hibernate.SessionFactory.getAllCollectionMetadata is no longer supported" );
 	}
 
 	public Type getReferencedPropertyType(String className, String propertyName)
 		throws MappingException {
 		return getMetamodel().entityPersister( className ).getPropertyType( propertyName );
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
 	 * be a "heavy" object memory wise afterQuery close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		settings.getMultiTableBulkIdStrategy().release( serviceRegistry.getService( JdbcServices.class ), buildLocalConnectionAccess() );
 
 		cacheAccess.close();
 		metamodel.close();
 
 		cacheAccess.close();
 
 		queryPlanCache.cleanup();
 
 		if ( delayedDropAction != null ) {
 			delayedDropAction.perform( serviceRegistry );
 		}
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
 	}
 
 	public CacheImplementor getCache() {
 		return cacheAccess;
 	}
 
 	@Override
 	public PersistenceUnitUtil getPersistenceUnitUtil() {
 		return null;
 	}
 
 	@Override
 	public void addNamedQuery(String name, Query query) {
 
 	}
 
 	@Override
 	public <T> T unwrap(Class<T> cls) {
 		return null;
 	}
 
 	@Override
 	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
 
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	private transient StatisticsImplementor statistics;
 
 	public StatisticsImplementor getStatistics() {
 		if ( statistics == null ) {
 			statistics = serviceRegistry.getService( StatisticsImplementor.class );
 		}
 		return statistics;
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = filters.get( filterName );
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
 		return identifierGenerators.get(rootEntityName);
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
 		String impl = (String) properties.get( Environment.CURRENT_SESSION_CONTEXT_CLASS );
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
 //			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
 //				LOG.autoFlushWillNotWork();
 //			}
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
 				Class implClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( impl );
 				return (CurrentSessionContext)
 						implClass.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
 				LOG.unableToConstructCurrentSessionContext( impl, t );
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
 		return fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	@Override
 	public Type resolveParameterBindType(Object bindValue) {
 		if ( bindValue == null ) {
 			// we can't guess
 			return null;
 		}
 
 		final Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy( bindValue );
 		String typename = clazz.getName();
 		Type type = getTypeResolver().heuristicType( typename );
 		boolean serializable = type != null && type instanceof SerializableType;
 		if ( type == null || serializable ) {
 			try {
 				getMetamodel().entityPersister( clazz.getName() );
 			}
 			catch (MappingException me) {
 				if ( serializable ) {
 					return type;
 				}
 				else {
 					throw new HibernateException( "Could not determine a type for class: " + typename );
 				}
 			}
 			return getTypeHelper().entity( clazz );
 		}
 		else {
 			return type;
 		}
 	}
 
 	static class SessionBuilderImpl<T extends SessionBuilder> implements SessionBuilderImplementor<T>, SessionCreationOptions {
 		private static final Logger log = CoreLogging.logger( SessionBuilderImpl.class );
 
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private StatementInspector statementInspector;
 		private Connection connection;
 		private PhysicalConnectionHandlingMode connectionHandlingMode;
 		private boolean autoJoinTransactions = true;
 		private FlushMode flushMode;
 		private boolean autoClose;
 		private boolean autoClear;
 		private String tenantIdentifier;
 		private List<SessionEventListener> listeners;
 
 		//todo : expose setting
 		private SessionOwnerBehavior sessionOwnerBehavior = SessionOwnerBehavior.LEGACY_NATIVE;
 		private PersistenceUnitTransactionType persistenceUnitTransactionType;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 
 			// set up default builder values...
 			this.statementInspector = sessionFactory.getSessionFactoryOptions().getStatementInspector();
 			this.connectionHandlingMode = sessionFactory.getSessionFactoryOptions().getPhysicalConnectionHandlingMode();
 			this.autoClose = sessionFactory.getSessionFactoryOptions().isAutoCloseSessionEnabled();
 			this.flushMode = sessionFactory.getSessionFactoryOptions().isFlushBeforeCompletionEnabled()
 					? FlushMode.AUTO
 					: FlushMode.MANUAL;
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 
 			listeners = sessionFactory.getSessionFactoryOptions().getBaselineSessionEventsListenerBuilder().buildBaselineList();
 		}
 
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// SessionCreationOptions
 
 		@Override
 		public SessionOwner getSessionOwner() {
 			return sessionOwner;
 		}
 
 		@Override
 		public ExceptionMapper getExceptionMapper() {
 			if ( sessionOwner != null ) {
 				return sessionOwner.getExceptionMapper();
 			}
 			else {
 				return sessionOwnerBehavior == SessionOwnerBehavior.LEGACY_JPA
 						? ExceptionMapperLegacyJpaImpl.INSTANCE
 						: null;
 			}
 		}
 
 		@Override
 		public AfterCompletionAction getAfterCompletionAction() {
 			if ( sessionOwner != null ) {
 				return sessionOwner.getAfterCompletionAction();
 			}
 			return sessionOwnerBehavior == SessionOwnerBehavior.LEGACY_JPA
 					? AfterCompletionActionLegacyJpaImpl.INSTANCE
 					: null;
 		}
 
 		@Override
 		public ManagedFlushChecker getManagedFlushChecker() {
 			if ( sessionOwner != null ) {
 				return sessionOwner.getManagedFlushChecker();
 			}
 			return sessionOwnerBehavior == SessionOwnerBehavior.LEGACY_JPA
 					? ManagedFlushCheckerLegacyJpaImpl.INSTANCE
 					: null;
 		}
 
 		@Override
 		public boolean shouldAutoJoinTransactions() {
 			return autoJoinTransactions;
 		}
 
 		@Override
 		public FlushMode getInitialSessionFlushMode() {
 			return flushMode;
 		}
 
 		@Override
 		public boolean shouldAutoClose() {
 			return autoClose;
 		}
 
 		@Override
 		public boolean shouldAutoClear() {
 			return autoClear;
 		}
 
 		@Override
 		public Connection getConnection() {
 			return connection;
 		}
 
 		@Override
 		public Interceptor getInterceptor() {
 			if ( interceptor != null && interceptor != EmptyInterceptor.INSTANCE ) {
 				return interceptor;
 			}
 
 			// prefer the SF-scoped interceptor, prefer that to any Session-scoped interceptor prototype
 			if ( sessionFactory.getSessionFactoryOptions().getInterceptor() != null ) {
 				return sessionFactory.getSessionFactoryOptions().getInterceptor();
 			}
 
 			if ( sessionFactory.getSessionFactoryOptions().getStatelessInterceptorImplementor() != null ) {
 				try {
 					return sessionFactory.getSessionFactoryOptions().getStatelessInterceptorImplementor().newInstance();
 				}
 				catch (InstantiationException | IllegalAccessException e) {
 					throw new HibernateException( "Could not instantiate session-scoped SessionFactory Interceptor", e );
 				}
 			}
 
 			return null;
 		}
 
 		@Override
 		public StatementInspector getStatementInspector() {
 			return statementInspector;
 		}
 
 		@Override
 		public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
 			return connectionHandlingMode;
 		}
 
 		@Override
 		public String getTenantIdentifier() {
 			return tenantIdentifier;
 		}
 
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// SessionBuilder
 
 		@Override
 		public Session openSession() {
 			log.tracef( "Opening Hibernate Session.  tenant=%s, owner=%s", tenantIdentifier, sessionOwner );
 			final SessionImpl session = new SessionImpl( sessionFactory, this );
 
 			for ( SessionEventListener listener : listeners ) {
 				session.getEventListenerManager().addListener( listener );
 			}
 
 			return session;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T owner(SessionOwner sessionOwner) {
 			this.sessionOwner = sessionOwner;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T statementInspector(StatementInspector statementInspector) {
 			this.statementInspector = statementInspector;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T connection(Connection connection) {
 			this.connection = connection;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			// NOTE : Legacy behavior (when only ConnectionReleaseMode was exposed) was to always acquire a
 			// Connection using ConnectionAcquisitionMode.AS_NEEDED..
 
 			final PhysicalConnectionHandlingMode handlingMode = PhysicalConnectionHandlingMode.interpret(
 					ConnectionAcquisitionMode.AS_NEEDED,
 					connectionReleaseMode
 			);
 			connectionHandlingMode( handlingMode );
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T connectionHandlingMode(PhysicalConnectionHandlingMode connectionHandlingMode) {
 			this.connectionHandlingMode = connectionHandlingMode;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T autoClear(boolean autoClear) {
 			this.autoClear = autoClear;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T flushMode(FlushMode flushMode) {
 			this.flushMode = flushMode;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T eventListeners(SessionEventListener... listeners) {
 			Collections.addAll( this.listeners, listeners );
 			return (T) this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T clearEventListeners() {
 			listeners.clear();
 			return (T) this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder, SessionCreationOptions {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( sessionFactory, this );
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
 
 		@Override
 		public boolean shouldAutoJoinTransactions() {
 			return true;
 		}
 
 		@Override
 		public FlushMode getInitialSessionFlushMode() {
 			return FlushMode.ALWAYS;
 		}
 
 		@Override
 		public boolean shouldAutoClose() {
 			return false;
 		}
 
 		@Override
 		public boolean shouldAutoClear() {
 			return false;
 		}
 
 		@Override
 		public Connection getConnection() {
 			return connection;
 		}
 
 		@Override
 		public Interceptor getInterceptor() {
 			// prefer the SF-scoped interceptor, prefer that to any Session-scoped interceptor prototype
 			if ( sessionFactory.getSessionFactoryOptions().getInterceptor() != null ) {
 				return sessionFactory.getSessionFactoryOptions().getInterceptor();
 			}
 
 			if ( sessionFactory.getSessionFactoryOptions().getStatelessInterceptorImplementor() != null ) {
 				try {
 					return sessionFactory.getSessionFactoryOptions().getStatelessInterceptorImplementor().newInstance();
 				}
 				catch (InstantiationException | IllegalAccessException e) {
 					throw new HibernateException( "Could not instantiate session-scoped SessionFactory Interceptor", e );
 				}
 			}
 
 			return null;
 		}
 
 		@Override
 		public StatementInspector getStatementInspector() {
 			return null;
 		}
 
 		@Override
 		public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
 			return null;
 		}
 
 		@Override
 		public String getTenantIdentifier() {
 			return tenantIdentifier;
 		}
 
 		@Override
 		public SessionOwner getSessionOwner() {
 			return null;
 		}
 
 		@Override
 		public ExceptionMapper getExceptionMapper() {
 			return null;
 		}
 
 		@Override
 		public AfterCompletionAction getAfterCompletionAction() {
 			return null;
 		}
 
 		@Override
 		public ManagedFlushChecker getManagedFlushChecker() {
 			return null;
 		}
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return getSessionFactoryOptions().getCustomEntityDirtinessStrategy();
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return getSessionFactoryOptions().getCurrentTenantIdentifierResolver();
 	}
 
 
 	// Serialization handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly serialized
 	 *
 	 * @param out The stream into which the object is being serialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 */
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		LOG.debugf( "Serializing: %s", uuid );
 		out.defaultWriteObject();
 		LOG.trace( "Serialized" );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized
 	 *
 	 * @param in The stream from which the object is being deserialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 * @throws ClassNotFoundException Again, can be thrown by the stream
 	 */
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing" );
 		in.defaultReadObject();
 		LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized.
 	 * Here we resolve the uuid/name read from the stream previously to resolve the SessionFactory
 	 * instance to use based on the registrations with the {@link SessionFactoryRegistry}
 	 *
 	 * @return The resolved factory to use.
 	 *
 	 * @throws InvalidObjectException Thrown if we could not resolve the factory by uuid/name.
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		LOG.trace( "Resolving serialized SessionFactory" );
 		return locateSessionFactoryOnDeserialization( uuid, name );
 	}
 
 	private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException{
 		final SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( uuidResult != null ) {
 			LOG.debugf( "Resolved SessionFactory by UUID [%s]", uuid );
 			return uuidResult;
 		}
 
 		// in case we were deserialized in a different JVM, look for an instance with the same name
 		// (provided we were given a name)
 		if ( name != null ) {
 			final SessionFactory namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( namedResult != null ) {
 				LOG.debugf( "Resolved SessionFactory by name [%s]", name );
 				return namedResult;
 			}
 		}
 
 		throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
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
 		LOG.trace( "Deserializing SessionFactory from Session" );
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/internal/MetamodelImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/internal/MetamodelImpl.java
index 3543b7efbf..9e7d52e702 100755
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/internal/MetamodelImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/internal/MetamodelImpl.java
@@ -1,572 +1,573 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.metamodel.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import javax.persistence.metamodel.EmbeddableType;
 import javax.persistence.metamodel.EntityType;
 import javax.persistence.metamodel.ManagedType;
 import javax.persistence.metamodel.MappedSuperclassType;
 
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.UnknownEntityTypeException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.EntityManagerMessageLogger;
 import org.hibernate.internal.HEMLogging;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.spi.MetamodelImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 
 /**
  * Hibernate implementation of the JPA {@link javax.persistence.metamodel.Metamodel} contract.
  *
  * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 public class MetamodelImpl implements MetamodelImplementor, Serializable {
 	// todo : Integrate EntityManagerLogger into CoreMessageLogger
 	private static final EntityManagerMessageLogger log = HEMLogging.messageLogger( MetamodelImpl.class );
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private final Map<String,String> imports = new ConcurrentHashMap<>();
 	private final Map<String,EntityPersister> entityPersisterMap = new ConcurrentHashMap<>();
 	private final Map<Class,String> entityProxyInterfaceMap = new ConcurrentHashMap<>();
 	private final Map<String,CollectionPersister> collectionPersisterMap = new ConcurrentHashMap<>();
 	private final Map<String,Set<String>> collectionRolesByEntityParticipant = new ConcurrentHashMap<>();
 	private final ConcurrentMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<>();
 
 
 	private final Map<Class<?>, EntityTypeImpl<?>> jpaEntityTypeMap = new ConcurrentHashMap<>();
 	private final Map<Class<?>, EmbeddableTypeImpl<?>> jpaEmbeddableTypeMap = new ConcurrentHashMap<>();
 	private final Map<Class<?>, MappedSuperclassType<?>> jpaMappedSuperclassTypeMap = new ConcurrentHashMap<>();
 	private final Map<String, EntityTypeImpl<?>> jpaEntityTypesByEntityName = new ConcurrentHashMap<>();
 
 	public MetamodelImpl(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	/**
 	 * Prepare the metamodel using the information from the collection of Hibernate
 	 * {@link PersistentClass} models
 	 *
 	 * @param mappingMetadata The mapping information
 	 * @param jpaMetaModelPopulationSetting Should the JPA Metamodel be built as well?
 	 */
 	public void initialize(MetadataImplementor mappingMetadata, JpaMetaModelPopulationSetting jpaMetaModelPopulationSetting) {
 		this.imports.putAll( mappingMetadata.getImports() );
 
 		final PersisterCreationContext persisterCreationContext = new PersisterCreationContext() {
 			@Override
 			public SessionFactoryImplementor getSessionFactory() {
 				return sessionFactory;
 			}
 
 			@Override
 			public MetadataImplementor getMetadata() {
 				return mappingMetadata;
 			}
 		};
 
 		final PersisterFactory persisterFactory = sessionFactory.getServiceRegistry().getService( PersisterFactory.class );
 
 		for ( final PersistentClass model : mappingMetadata.getEntityBindings() ) {
 			final EntityRegionAccessStrategy accessStrategy = sessionFactory.getCache().determineEntityRegionAccessStrategy(
 					model
 			);
 
 			final NaturalIdRegionAccessStrategy naturalIdAccessStrategy = sessionFactory.getCache().determineNaturalIdRegionAccessStrategy(
 					model
 			);
 
 			final EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					persisterCreationContext
 			);
 			entityPersisterMap.put( model.getEntityName(), cp );
 
 			if ( cp.getConcreteProxyClass() != null
 					&& cp.getConcreteProxyClass().isInterface()
 					&& !Map.class.isAssignableFrom( cp.getConcreteProxyClass() )
 					&& cp.getMappedClass() != cp.getConcreteProxyClass() ) {
 				// IMPL NOTE : we exclude Map based proxy interfaces here because that should
 				//		indicate MAP entity mode.0
 
 				if ( cp.getMappedClass().equals( cp.getConcreteProxyClass() ) ) {
 					// this part handles an odd case in the Hibernate test suite where we map an interface
 					// as the class and the proxy.  I cannot think of a real life use case for that
 					// specific test, but..
 					log.debugf( "Entity [%s] mapped same interface [%s] as class and proxy", cp.getEntityName(), cp.getMappedClass() );
 				}
 				else {
 					final String old = entityProxyInterfaceMap.put( cp.getConcreteProxyClass(), cp.getEntityName() );
 					if ( old != null ) {
 						throw new HibernateException(
 								String.format(
 										Locale.ENGLISH,
 										"Multiple entities [%s, %s] named the same interface [%s] as their proxy which is not supported",
 										old,
 										cp.getEntityName(),
 										cp.getConcreteProxyClass().getName()
 								)
 						);
 					}
 				}
 			}
 		}
 
 		for ( final Collection model : mappingMetadata.getCollectionBindings() ) {
 			final CollectionRegionAccessStrategy accessStrategy = sessionFactory.getCache().determineCollectionRegionAccessStrategy(
 					model
 			);
 
 			final CollectionPersister persister = persisterFactory.createCollectionPersister(
 					model,
 					accessStrategy,
 					persisterCreationContext
 			);
 			collectionPersisterMap.put( model.getRole(), persister );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( (AssociationType) indexType ).getAssociatedEntityName( sessionFactory );
 				Set<String> roles = collectionRolesByEntityParticipant.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<>();
 					collectionRolesByEntityParticipant.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( sessionFactory );
 				Set<String> roles = collectionRolesByEntityParticipant.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<>();
 					collectionRolesByEntityParticipant.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 
 		// after *all* persisters and named queries are registered
 		entityPersisterMap.values().forEach( EntityPersister::generateEntityDefinition );
 
 		for ( EntityPersister persister : entityPersisterMap.values() ) {
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister, entityNameResolvers );
 		}
 		collectionPersisterMap.values().forEach( CollectionPersister::postInstantiate );
 
 		MetadataContext context = new MetadataContext(
 				sessionFactory,
 				mappingMetadata.getMappedSuperclassMappingsCopy(),
 				jpaMetaModelPopulationSetting
 		);
 		if ( jpaMetaModelPopulationSetting != JpaMetaModelPopulationSetting.DISABLED ) {
 			for ( PersistentClass entityBinding : mappingMetadata.getEntityBindings() ) {
 				locateOrBuildEntityType( entityBinding, context );
 			}
 			handleUnusedMappedSuperclasses( context );
 		}
 		context.wrapUp();
 
 		this.jpaEntityTypeMap.putAll( context.getEntityTypeMap() );
 		this.jpaEmbeddableTypeMap.putAll( context.getEmbeddableTypeMap() );
 		this.jpaMappedSuperclassTypeMap.putAll( context.getMappedSuperclassTypeMap() );
 		this.jpaEntityTypesByEntityName.putAll( context.getEntityTypesByEntityName() );
 	}
 
+	@Override
 	public java.util.Collection<EntityNameResolver> getEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	private static void registerEntityNameResolvers(EntityPersister persister, Map<EntityNameResolver,Object> entityNameResolvers) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer(), entityNameResolvers );
 	}
 
 	private static void registerEntityNameResolvers(EntityTuplizer tuplizer, Map<EntityNameResolver,Object> entityNameResolvers) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 		}
 	}
 
 	private static void handleUnusedMappedSuperclasses(MetadataContext context) {
 		final Set<MappedSuperclass> unusedMappedSuperclasses = context.getUnusedMappedSuperclasses();
 		if ( !unusedMappedSuperclasses.isEmpty() ) {
 			for ( MappedSuperclass mappedSuperclass : unusedMappedSuperclasses ) {
 				log.unusedMappedSuperclass( mappedSuperclass.getMappedClass().getName() );
 				locateOrBuildMappedsuperclassType( mappedSuperclass, context );
 			}
 		}
 	}
 
 	private static EntityTypeImpl<?> locateOrBuildEntityType(PersistentClass persistentClass, MetadataContext context) {
 		EntityTypeImpl<?> entityType = context.locateEntityType( persistentClass );
 		if ( entityType == null ) {
 			entityType = buildEntityType( persistentClass, context );
 		}
 		return entityType;
 	}
 
 	//TODO remove / reduce @SW scope
 	@SuppressWarnings("unchecked")
 	private static EntityTypeImpl<?> buildEntityType(PersistentClass persistentClass, MetadataContext context) {
 		final Class javaType = persistentClass.getMappedClass();
 		context.pushEntityWorkedOn( persistentClass );
 		final MappedSuperclass superMappedSuperclass = persistentClass.getSuperMappedSuperclass();
 		AbstractIdentifiableType<?> superType = superMappedSuperclass == null
 				? null
 				: locateOrBuildMappedsuperclassType( superMappedSuperclass, context );
 		//no mappedSuperclass, check for a super entity
 		if ( superType == null ) {
 			final PersistentClass superPersistentClass = persistentClass.getSuperclass();
 			superType = superPersistentClass == null
 					? null
 					: locateOrBuildEntityType( superPersistentClass, context );
 		}
 		EntityTypeImpl entityType = new EntityTypeImpl(
 				javaType,
 				superType,
 				persistentClass
 		);
 
 		context.registerEntityType( persistentClass, entityType );
 		context.popEntityWorkedOn( persistentClass );
 		return entityType;
 	}
 
 	private static MappedSuperclassTypeImpl<?> locateOrBuildMappedsuperclassType(
 			MappedSuperclass mappedSuperclass, MetadataContext context) {
 		MappedSuperclassTypeImpl<?> mappedSuperclassType = context.locateMappedSuperclassType( mappedSuperclass );
 		if ( mappedSuperclassType == null ) {
 			mappedSuperclassType = buildMappedSuperclassType( mappedSuperclass, context );
 		}
 		return mappedSuperclassType;
 	}
 
 	//TODO remove / reduce @SW scope
 	@SuppressWarnings("unchecked")
 	private static MappedSuperclassTypeImpl<?> buildMappedSuperclassType(
 			MappedSuperclass mappedSuperclass,
 			MetadataContext context) {
 		final MappedSuperclass superMappedSuperclass = mappedSuperclass.getSuperMappedSuperclass();
 		AbstractIdentifiableType<?> superType = superMappedSuperclass == null
 				? null
 				: locateOrBuildMappedsuperclassType( superMappedSuperclass, context );
 		//no mappedSuperclass, check for a super entity
 		if ( superType == null ) {
 			final PersistentClass superPersistentClass = mappedSuperclass.getSuperPersistentClass();
 			superType = superPersistentClass == null
 					? null
 					: locateOrBuildEntityType( superPersistentClass, context );
 		}
 		final Class javaType = mappedSuperclass.getMappedClass();
 		MappedSuperclassTypeImpl mappedSuperclassType = new MappedSuperclassTypeImpl(
 				javaType,
 				mappedSuperclass,
 				superType
 		);
 		context.registerMappedSuperclassType( mappedSuperclass, mappedSuperclassType );
 		return mappedSuperclassType;
 	}
 
 //	/**
 //	 * Instantiate the metamodel.
 //	 *
 //	 * @param entityNameResolvers
 //	 * @param entities The entity mappings.
 //	 * @param embeddables The embeddable (component) mappings.
 //	 * @param mappedSuperclassTypeMap The {@link javax.persistence.MappedSuperclass} mappings
 //	 */
 //	private MetamodelImpl(
 //			SessionFactoryImplementor sessionFactory,
 //			Map<String, String> imports,
 //			Map<String, EntityPersister> entityPersisterMap,
 //			Map<Class, String> entityProxyInterfaceMap,
 //			ConcurrentHashMap<EntityNameResolver, Object> entityNameResolvers,
 //			Map<String, CollectionPersister> collectionPersisterMap,
 //			Map<String, Set<String>> collectionRolesByEntityParticipant,
 //			Map<Class<?>, EntityTypeImpl<?>> entities,
 //			Map<Class<?>, EmbeddableTypeImpl<?>> embeddables,
 //			Map<Class<?>, MappedSuperclassType<?>> mappedSuperclassTypeMap,
 //			Map<String, EntityTypeImpl<?>> entityTypesByEntityName) {
 //		this.sessionFactory = sessionFactory;
 //		this.imports = imports;
 //		this.entityPersisterMap = entityPersisterMap;
 //		this.entityProxyInterfaceMap = entityProxyInterfaceMap;
 //		this.entityNameResolvers = entityNameResolvers;
 //		this.collectionPersisterMap = collectionPersisterMap;
 //		this.collectionRolesByEntityParticipant = collectionRolesByEntityParticipant;
 //		this.entities = entities;
 //		this.embeddables = embeddables;
 //		this.mappedSuperclassTypeMap = mappedSuperclassTypeMap;
 //		this.entityTypesByEntityName = entityTypesByEntityName;
 //	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked"})
 	public <X> EntityType<X> entity(Class<X> cls) {
 		final EntityType<?> entityType = jpaEntityTypeMap.get( cls );
 		if ( entityType == null ) {
 			throw new IllegalArgumentException( "Not an entity: " + cls );
 		}
 		return (EntityType<X>) entityType;
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked"})
 	public <X> ManagedType<X> managedType(Class<X> cls) {
 		ManagedType<?> type = jpaEntityTypeMap.get( cls );
 		if ( type == null ) {
 			type = jpaMappedSuperclassTypeMap.get( cls );
 		}
 		if ( type == null ) {
 			type = jpaEmbeddableTypeMap.get( cls );
 		}
 		if ( type == null ) {
 			throw new IllegalArgumentException( "Not a managed type: " + cls );
 		}
 		return (ManagedType<X>) type;
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked"})
 	public <X> EmbeddableType<X> embeddable(Class<X> cls) {
 		final EmbeddableType<?> embeddableType = jpaEmbeddableTypeMap.get( cls );
 		if ( embeddableType == null ) {
 			throw new IllegalArgumentException( "Not an embeddable: " + cls );
 		}
 		return (EmbeddableType<X>) embeddableType;
 	}
 
 	@Override
 	public Set<ManagedType<?>> getManagedTypes() {
 		final int setSize = CollectionHelper.determineProperSizing(
 				jpaEntityTypeMap.size() + jpaMappedSuperclassTypeMap.size() + jpaEmbeddableTypeMap.size()
 		);
 		final Set<ManagedType<?>> managedTypes = new HashSet<ManagedType<?>>( setSize );
 		managedTypes.addAll( jpaEntityTypeMap.values() );
 		managedTypes.addAll( jpaMappedSuperclassTypeMap.values() );
 		managedTypes.addAll( jpaEmbeddableTypeMap.values() );
 		return managedTypes;
 	}
 
 	@Override
 	public Set<EntityType<?>> getEntities() {
 		return new HashSet<>( jpaEntityTypesByEntityName.values() );
 	}
 
 	@Override
 	public Set<EmbeddableType<?>> getEmbeddables() {
 		return new HashSet<>( jpaEmbeddableTypeMap.values() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <X> EntityType<X> entity(String entityName) {
 		return (EntityType<X>) jpaEntityTypesByEntityName.get( entityName );
 	}
 
 	@Override
 	public String getImportedClassName(String className) {
 		String result = imports.get( className );
 		if ( result == null ) {
 			try {
 				sessionFactory.getServiceRegistry().getService( ClassLoaderService.class ).classForName( className );
 				imports.put( className, className );
 				return className;
 			}
 			catch ( ClassLoadingException cnfe ) {
 				return null;
 			}
 		}
 		else {
 			return result;
 		}
 	}
 
 	/**
 	 * Given the name of an entity class, determine all the class and interface names by which it can be
 	 * referenced in an HQL query.
 	 *
 	 * @param className The name of the entity class
 	 *
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = getSessionFactory().getServiceRegistry().getService( ClassLoaderService.class ).classForName( className );
 		}
 		catch (ClassLoadingException e) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList<String> results = new ArrayList<>();
 		for ( EntityPersister checkPersister : entityPersisters().values() ) {
 			if ( ! Queryable.class.isInstance( checkPersister ) ) {
 				continue;
 			}
 			final Queryable checkQueryable = Queryable.class.cast( checkPersister );
 			final String checkQueryableEntityName = checkQueryable.getEntityName();
 			final boolean isMappedClass = className.equals( checkQueryableEntityName );
 			if ( checkQueryable.isExplicitPolymorphism() ) {
 				if ( isMappedClass ) {
 					return new String[] { className }; //NOTE EARLY EXIT
 				}
 			}
 			else {
 				if ( isMappedClass ) {
 					results.add( checkQueryableEntityName );
 				}
 				else {
 					final Class mappedClass = checkQueryable.getMappedClass();
 					if ( mappedClass != null && clazz.isAssignableFrom( mappedClass ) ) {
 						final boolean assignableSuperclass;
 						if ( checkQueryable.isInherited() ) {
 							Class mappedSuperclass = entityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
 							assignableSuperclass = clazz.isAssignableFrom( mappedSuperclass );
 						}
 						else {
 							assignableSuperclass = false;
 						}
 						if ( !assignableSuperclass ) {
 							results.add( checkQueryableEntityName );
 						}
 					}
 				}
 			}
 		}
 		return results.toArray( new String[results.size()] );
 	}
 
 	@Override
 	public Map<String, EntityPersister> entityPersisters() {
 		return entityPersisterMap;
 	}
 
 	@Override
 	public CollectionPersister collectionPersister(String role) {
 		final CollectionPersister persister = collectionPersisterMap.get( role );
 		if ( persister == null ) {
 			throw new MappingException( "Could not locate CollectionPersister for role : " + role );
 		}
 		return persister;
 	}
 
 	@Override
 	public Map<String, CollectionPersister> collectionPersisters() {
 		return collectionPersisterMap;
 	}
 
 	@Override
 	public EntityPersister entityPersister(Class entityClass) {
 		return entityPersister( entityClass.getName() );
 	}
 
 	@Override
 	public EntityPersister entityPersister(String entityName) throws MappingException {
 		EntityPersister result = entityPersisterMap.get( entityName );
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 
 	@Override
 	public EntityPersister locateEntityPersister(Class byClass) {
 		EntityPersister entityPersister = entityPersisterMap.get( byClass.getName() );
 		if ( entityPersister == null ) {
 			String mappedEntityName = entityProxyInterfaceMap.get( byClass );
 			if ( mappedEntityName != null ) {
 				entityPersister = entityPersisterMap.get( mappedEntityName );
 			}
 		}
 
 		if ( entityPersister == null ) {
 			throw new UnknownEntityTypeException( "Unable to locate persister: " + byClass.getName() );
 		}
 
 		return entityPersister;
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(String byName) {
 		final EntityPersister entityPersister = entityPersisterMap.get( byName );
 		if ( entityPersister == null ) {
 			throw new UnknownEntityTypeException( "Unable to locate persister: " + byName );
 		}
 		return entityPersister;
 	}
 
 	@Override
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	@Override
 	public String[] getAllEntityNames() {
 		return ArrayHelper.toStringArray( entityPersisterMap.keySet() );
 	}
 
 	@Override
 	public String[] getAllCollectionRoles() {
 		return ArrayHelper.toStringArray( entityPersisterMap.keySet() );
 	}
 
 	@Override
 	public void close() {
 		// anything to do ?
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/spi/MetamodelImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/spi/MetamodelImplementor.java
index b269d9557d..4e3e808bae 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/spi/MetamodelImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/spi/MetamodelImplementor.java
@@ -1,121 +1,125 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.metamodel.spi;
 
+import java.util.Collection;
 import java.util.Map;
 import java.util.Set;
 
+import org.hibernate.EntityNameResolver;
 import org.hibernate.MappingException;
 import org.hibernate.Metamodel;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Steve Ebersole
  */
 public interface MetamodelImplementor extends Metamodel {
 	@Override
 	SessionFactoryImplementor getSessionFactory();
 
+	Collection<EntityNameResolver> getEntityNameResolvers();
+
 	/**
 	 * Locate an EntityPersister by the entity class.  The passed Class might refer to either
 	 * the entity name directly, or it might name a proxy interface for the entity.  This
 	 * method accounts for both, preferring the direct named entity name.
 	 *
 	 * @param byClass The concrete Class or proxy interface for the entity to locate the persister for.
 	 *
 	 * @return The located EntityPersister, never {@code null}
 	 *
 	 * @throws org.hibernate.UnknownEntityTypeException If a matching EntityPersister cannot be located
 	 */
 	EntityPersister locateEntityPersister(Class byClass);
 
 	/**
 	 * Locate the entity persister by name.
 	 *
 	 * @param byName The entity name
 	 *
 	 * @return The located EntityPersister, never {@code null}
 	 *
 	 * @throws org.hibernate.UnknownEntityTypeException If a matching EntityPersister cannot be located
 	 */
 	EntityPersister locateEntityPersister(String byName);
 
 	/**
 	 * Locate the persister for an entity by the entity class.
 	 *
 	 * @param entityClass The entity class
 	 *
 	 * @return The entity persister
 	 *
 	 * @throws MappingException Indicates persister for that class could not be found.
 	 */
 	EntityPersister entityPersister(Class entityClass);
 
 	/**
 	 * Locate the persister for an entity by the entity-name
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 *
 	 * @return The persister
 	 *
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	EntityPersister entityPersister(String entityName);
 
 	/**
 	 * Get all entity persisters as a Map, which entity name its the key and the persister is the value.
 	 *
 	 * @return The Map contains all entity persisters.
 	 */
 	Map<String,EntityPersister> entityPersisters();
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role of the collection for which to retrieve the persister.
 	 *
 	 * @return The persister
 	 *
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	CollectionPersister collectionPersister(String role);
 
 	/**
 	 * Get all collection persisters as a Map, which collection role as the key and the persister is the value.
 	 *
 	 * @return The Map contains all collection persisters.
 	 */
 	Map<String,CollectionPersister> collectionPersisters();
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity is a participant, as either an
 	 * index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 *
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	/**
 	 * Get the names of all entities known to this Metamodel
 	 *
 	 * @return All of the entity names
 	 */
 	String[] getAllEntityNames();
 
 	/**
 	 * Get the names of all collections known to this Metamodel
 	 *
 	 * @return All of the entity names
 	 */
 	String[] getAllCollectionRoles();
 
 	void close();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingImpl.java b/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingImpl.java
index 6169597ee8..0585494dd8 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingImpl.java
@@ -1,59 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.internal;
 
 import javax.persistence.TemporalType;
 
 import org.hibernate.query.spi.QueryParameterBinding;
 import org.hibernate.query.spi.QueryParameterBindingTypeResolver;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public class QueryParameterBindingImpl<T> implements QueryParameterBinding<T> {
 	private final QueryParameterBindingTypeResolver typeResolver;
 
+	private boolean isBound;
+
 	private Type bindType;
 	private T bindValue;
 
 	public QueryParameterBindingImpl(Type type, QueryParameterBindingTypeResolver typeResolver) {
 		this.bindType = type;
 		this.typeResolver = typeResolver;
 	}
 
 	@Override
+	public boolean isBound() {
+		return isBound;
+	}
+
+	@Override
 	public T getBindValue() {
 		return bindValue;
 	}
 
 	@Override
 	public Type getBindType() {
 		return bindType;
 	}
 
 	@Override
 	public void setBindValue(T value) {
+		this.isBound = true;
 		this.bindValue = value;
 
 		if ( bindType == null ) {
 			this.bindType = typeResolver.resolveParameterBindType( value );
 		}
 	}
 
 	@Override
 	public void setBindValue(T value, Type clarifiedType) {
 		setBindValue( value );
 		this.bindType = clarifiedType;
 	}
 
 	@Override
 	public void setBindValue(T value, TemporalType clarifiedTemporalType) {
 		setBindValue( value );
 		this.bindType = BindingTypeHelper.INSTANCE.determineTypeForTemporalType( clarifiedTemporalType, bindType, value );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingsImpl.java b/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingsImpl.java
index 7c058f4336..59676650e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/QueryParameterBindingsImpl.java
@@ -1,536 +1,536 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.internal;
 
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.TreeMap;
 import java.util.stream.Collectors;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Incubating;
 import org.hibernate.QueryException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.query.spi.NamedParameterDescriptor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.classic.ParserHelper;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.query.ParameterMetadata;
 import org.hibernate.query.QueryParameter;
 import org.hibernate.query.spi.QueryParameterBinding;
 import org.hibernate.query.spi.QueryParameterBindings;
 import org.hibernate.query.spi.QueryParameterListBinding;
 import org.hibernate.type.SerializableType;
 import org.hibernate.type.Type;
 
 /**
  * Manages the group of QueryParameterBinding for a particular query.
  *
  * @author Steve Ebersole
  */
 @Incubating
 public class QueryParameterBindingsImpl implements QueryParameterBindings {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( QueryParameterBindingsImpl.class );
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private Map<QueryParameter, QueryParameterBinding> parameterBindingMap;
 	private Map<QueryParameter, QueryParameterListBinding> parameterListBindingMap;
 
 	public static QueryParameterBindingsImpl from(ParameterMetadata parameterMetadata, SessionFactoryImplementor sessionFactory) {
 		if ( parameterMetadata == null ) {
 			return new QueryParameterBindingsImpl( sessionFactory );
 		}
 		else {
 			return new QueryParameterBindingsImpl( sessionFactory, parameterMetadata.collectAllParameters() );
 		}
 	}
 
 	public QueryParameterBindingsImpl(SessionFactoryImplementor sessionFactory) {
 		this( sessionFactory, Collections.emptySet() );
 	}
 
 	public QueryParameterBindingsImpl(SessionFactoryImplementor sessionFactory, Set<QueryParameter<?>> queryParameters) {
 		this.sessionFactory = sessionFactory;
 
 		if ( queryParameters == null || queryParameters.isEmpty() ) {
 			parameterBindingMap = Collections.emptyMap();
 		}
 		else {
 			parameterBindingMap = new HashMap<>();
 
 			for ( QueryParameter queryParameter : queryParameters ) {
 				parameterBindingMap.put( queryParameter, makeBinding( queryParameter ) );
 			}
 		}
 
 		parameterListBindingMap = new HashMap<>();
 	}
 
 	protected QueryParameterBinding makeBinding(QueryParameter queryParameter) {
 		return makeBinding( queryParameter.getType() );
 	}
 
 	protected QueryParameterBinding makeBinding(Type bindType) {
 		return new QueryParameterBindingImpl( bindType, sessionFactory );
 	}
 
 	public boolean isBound(QueryParameter parameter) {
 		final QueryParameterBinding binding = locateBinding( parameter );
 		if ( binding != null ) {
 			return binding.getBindValue() != null;
 		}
 
 		final QueryParameterListBinding listBinding = locateQueryParameterListBinding( parameter );
 		if ( listBinding != null ) {
 			return listBinding.getBindValues() != null;
 		}
 
 		return false;
 	}
 
 	@SuppressWarnings("unchecked")
 	public <T> QueryParameterBinding<T> getBinding(QueryParameter<T> parameter) {
 		final QueryParameterBinding<T> binding = locateBinding( parameter );
 
 		if ( binding == null ) {
 			throw new IllegalArgumentException(
 					"Could not resolve QueryParameter reference [" + parameter + "] to QueryParameterBinding"
 			);
 		}
 
 		return binding;
 	}
 
 	@SuppressWarnings("unchecked")
 	public <T> QueryParameterBinding<T> locateBinding(QueryParameter<T> parameter) {
 		// see if this exact instance is known as a key
 		if ( parameterBindingMap.containsKey( parameter ) ) {
 			return parameterBindingMap.get( parameter );
 		}
 
 		// if the incoming parameter has a name, try to find it by name
 		if ( StringHelper.isNotEmpty( parameter.getName() ) ) {
 			final QueryParameterBinding binding = locateBinding( parameter.getName() );
 			if ( binding != null ) {
 				return binding;
 			}
 		}
 
 		// if the incoming parameter has a position, try to find it by position
 		if ( parameter.getPosition() != null ) {
 			final QueryParameterBinding binding = locateBinding( parameter.getPosition() );
 			if ( binding != null ) {
 				return binding;
 			}
 		}
 
 		return null;
 	}
 
 	protected QueryParameterBinding locateBinding(String name) {
 		for ( Map.Entry<QueryParameter, QueryParameterBinding> entry : parameterBindingMap.entrySet() ) {
 			if ( name.equals( entry.getKey().getName() ) ) {
 				return entry.getValue();
 			}
 		}
 
 		return null;
 	}
 
 	protected QueryParameterBinding locateAndRemoveBinding(String name) {
 		final Iterator<Map.Entry<QueryParameter,QueryParameterBinding>> entryIterator = parameterBindingMap.entrySet().iterator();
 		while ( entryIterator.hasNext() ) {
 			final Map.Entry<QueryParameter,QueryParameterBinding> entry = entryIterator.next();
 			if ( name.equals( entry.getKey().getName() ) ) {
 				entryIterator.remove();
 				return entry.getValue();
 			}
 		}
 
 		return null;
 	}
 
 	protected QueryParameterBinding locateBinding(int position) {
 		for ( Map.Entry<QueryParameter, QueryParameterBinding> entry : parameterBindingMap.entrySet() ) {
 			if ( entry.getKey().getPosition() != null && position == entry.getKey().getPosition() ) {
 				return entry.getValue();
 			}
 		}
 
 		return null;
 	}
 
 	public QueryParameterBinding getBinding(String name) {
 		final QueryParameterBinding binding = locateBinding( name );
 		if ( binding == null ) {
 			throw new IllegalArgumentException( "Unknown parameter name : " + name );
 		}
 
 		return binding;
 	}
 
 	public QueryParameterBinding getBinding(int position) {
 		final QueryParameterBinding binding = locateBinding( position );
 		if ( binding == null ) {
 			throw new IllegalArgumentException( "Unknown parameter position : " + position );
 		}
 
 		return binding;
 	}
 
 	public void verifyParametersBound(boolean reserveFirstParameter) {
 		for ( Map.Entry<QueryParameter, QueryParameterBinding> bindEntry : parameterBindingMap.entrySet() ) {
-			if ( bindEntry.getValue().getBindValue() == null ) {
-				if ( bindEntry.getKey().getName() == null ) {
+			if ( !bindEntry.getValue().isBound() ) {
+				if ( bindEntry.getKey().getName() != null ) {
 					throw new QueryException( "Named parameter [" + bindEntry.getKey().getName() + "] not set" );
 				}
 				else if ( bindEntry.getKey().getPosition() != null ) {
 					throw new QueryException( "Positional parameter [" + bindEntry.getKey().getPosition() + "] not set" );
 				}
 				else {
 					throw new QueryException( "Parameter memento [" + bindEntry.getKey() + "] not set" );
 				}
 			}
 		}
 	}
 
 	/**
 	 * @deprecated (since 5.2) expect a different approach to org.hibernate.engine.spi.QueryParameters in 6.0
 	 */
 	@Deprecated
 	public Collection<Type> collectBindTypes() {
 		return parameterBindingMap.values()
 				.stream()
 				.map( QueryParameterBinding::getBindType )
 				.collect( Collectors.toList() );
 	}
 
 	/**
 	 * @deprecated (since 5.2) expect a different approach to org.hibernate.engine.spi.QueryParameters in 6.0
 	 */
 	@Deprecated
 	public Collection<Object> collectBindValues() {
 		return parameterBindingMap.values()
 				.stream()
 				.map( QueryParameterBinding::getBindValue )
 				.collect( Collectors.toList() );
 	}
 
 	/**
 	 * @deprecated (since 5.2) expect a different approach to org.hibernate.engine.spi.QueryParameters in 6.0
 	 */
 	@Deprecated
 	public Type[] collectPositionalBindTypes() {
 		TreeMap<Integer,QueryParameterBinding> positionalParameterBindingMap = collectPositionalParameterBindings();
 		Type[] types = new Type[ positionalParameterBindingMap.size() ];
 
 		// NOTE : bindings should be ordered by position by nature of a TreeMap...
 		// NOTE : we also assume the contiguity of the positions
 
 		for ( Map.Entry<Integer, QueryParameterBinding> entry : positionalParameterBindingMap.entrySet() ) {
 			final int position = entry.getKey();
 
 			Type type = entry.getValue().getBindType();
 			if ( type == null ) {
 				log.debugf( "Binding for positional-parameter [%s] did not define type, using SerializableType", position );
 				type = SerializableType.INSTANCE;
 			}
 
 			types[ position ] = type;
 		}
 
 		return types;
 	}
 
 	private TreeMap<Integer, QueryParameterBinding> collectPositionalParameterBindings() {
 		final TreeMap<Integer, QueryParameterBinding> bindings = new TreeMap<>();
 
 		for ( Map.Entry<QueryParameter, QueryParameterBinding> entry : parameterBindingMap.entrySet() ) {
 			if ( entry.getKey().getPosition() == null ) {
 				continue;
 			}
 
 			final int position = entry.getKey().getPosition();
 
 			// these should be contiguous
 			bindings.put( position, entry.getValue() );
 		}
 
 		return bindings;
 	}
 
 	/**
 	 * @deprecated (since 5.2) expect a different approach to org.hibernate.engine.spi.QueryParameters in 6.0
 	 */
 	@Deprecated
 	public Object[] collectPositionalBindValues() {
 		TreeMap<Integer,QueryParameterBinding> positionalParameterBindingMap = collectPositionalParameterBindings();
 		Object[] values = new Object[ positionalParameterBindingMap.size() ];
 
 		// NOTE : bindings should be ordered by position by nature of a TreeMap...
 		// NOTE : we also assume the contiguity of the positions
 
 		for ( Map.Entry<Integer, QueryParameterBinding> entry : positionalParameterBindingMap.entrySet() ) {
 			final int position = entry.getKey();
 			values[ position ] = entry.getValue().getBindValue();
 		}
 
 		return values;
 	}
 
 	/**
 	 * @deprecated (since 5.2) expect a different approach to org.hibernate.engine.spi.QueryParameters in 6.0
 	 */
 	@Deprecated
 	public Map<String, TypedValue> collectNamedParameterBindings() {
 		Map<String,TypedValue> collectedBindings = new HashMap<>();
 		for ( Map.Entry<QueryParameter, QueryParameterBinding> entry : parameterBindingMap.entrySet() ) {
 			if ( entry.getKey().getName() == null ) {
 				continue;
 			}
 
 			Type bindType = entry.getValue().getBindType();
 			if ( bindType == null ) {
 				log.debugf( "Binding for named-parameter [%s] did not define type", entry.getKey().getName() );
 				bindType = SerializableType.INSTANCE;
 			}
 
 			collectedBindings.put(
 					entry.getKey().getName(),
 					new TypedValue( bindType, entry.getValue().getBindValue() )
 			);
 		}
 
 		return collectedBindings;
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Parameter list binding - expect changes in 6.0
 
 	/**
 	 * @deprecated (since 5.2) expected changes to "collection-valued parameter binding" in 6.0
 	 */
 	@Deprecated
 	@SuppressWarnings("unchecked")
 	public <T> QueryParameterListBinding<T> getQueryParameterListBinding(QueryParameter<T> queryParameter) {
 		QueryParameterListBinding result = parameterListBindingMap.get( queryParameter );
 		if ( result == null ) {
 			result = transformQueryParameterBindingToQueryParameterListBinding( queryParameter );
 		}
 		return result;
 	}
 
 	/**
 	 * @deprecated (since 5.2) expected changes to "collection-valued parameter binding" in 6.0
 	 */
 	@Deprecated
 	private QueryParameterListBinding locateQueryParameterListBinding(QueryParameter queryParameter) {
 		QueryParameterListBinding result = parameterListBindingMap.get( queryParameter );
 
 		if ( result == null && queryParameter.getName() != null ) {
 			for ( Map.Entry<QueryParameter, QueryParameterListBinding> entry : parameterListBindingMap.entrySet() ) {
 				if ( queryParameter.getName().equals( entry.getKey().getName() ) ) {
 					result = entry.getValue();
 					break;
 				}
 			}
 		}
 
 		return result;
 	}
 
 	/**
 	 * @deprecated (since 5.2) expected changes to "collection-valued parameter binding" in 6.0
 	 */
 	@Deprecated
 	private <T> QueryParameterListBinding<T> transformQueryParameterBindingToQueryParameterListBinding(QueryParameter<T> queryParameter) {
 		log.debugf( "Converting QueryParameterBinding to QueryParameterListBinding for given QueryParameter : %s", queryParameter );
 		final QueryParameterBinding binding = getAndRemoveBinding( queryParameter );
 		if ( binding == null ) {
 			throw new IllegalArgumentException(
 					"Could not locate QueryParameterBinding for given QueryParameter : " + queryParameter +
 							"; parameter list must be defined using named parameter"
 			);
 		}
 
 		final QueryParameterListBinding<T> convertedBinding = new QueryParameterListBindingImpl<>( binding.getBindType() );
 		parameterListBindingMap.put( queryParameter, convertedBinding );
 
 		return convertedBinding;
 	}
 
 	/**
 	 * @deprecated (since 5.2) expected changes to "collection-valued parameter binding" in 6.0
 	 */
 	@Deprecated
 	@SuppressWarnings("unchecked")
 	private  <T> QueryParameterBinding<T> getAndRemoveBinding(QueryParameter<T> parameter) {
 		// see if this exact instance is known as a key
 		if ( parameterBindingMap.containsKey( parameter ) ) {
 			return parameterBindingMap.remove( parameter );
 		}
 
 		// if the incoming parameter has a name, try to find it by name
 		if ( StringHelper.isNotEmpty( parameter.getName() ) ) {
 			final QueryParameterBinding binding = locateAndRemoveBinding( parameter.getName() );
 			if ( binding != null ) {
 				return binding;
 			}
 		}
 
 		// NOTE : getAndRemoveBinding is only intended for usage from #transformQueryParameterBindingToQueryParameterListBinding
 		//		which only supports named parameters, so there is no need to look into legacy positional parameters
 
 		throw new IllegalArgumentException(
 				"Could not resolve QueryParameter reference [" + parameter + "] to QueryParameterBinding"
 		);
 	}
 
 	/**
 	 * @deprecated (since 5.2) expected changes to "collection-valued parameter binding" in 6.0
 	 */
 	@Deprecated
 	@SuppressWarnings("unchecked")
 	public <T> QueryParameterListBinding<T> getQueryParameterListBinding(String name) {
 		// find the QueryParameter instance for the given name
 		final QueryParameter<T> queryParameter = resolveQueryParameter( name );
 		return getQueryParameterListBinding( queryParameter );
 	}
 
 	/**
 	 * @deprecated (since 5.2) expected changes to "collection-valued parameter binding" in 6.0
 	 */
 	@Deprecated
 	@SuppressWarnings("unchecked")
 	private <T> QueryParameter<T> resolveQueryParameter(String name) {
 		for ( QueryParameter queryParameter : parameterListBindingMap.keySet() ) {
 			if ( name.equals( queryParameter.getName() ) ) {
 				return queryParameter;
 			}
 		}
 
 		for ( QueryParameter queryParameter : parameterBindingMap.keySet() ) {
 			if ( name.equals( queryParameter.getName() ) ) {
 				return queryParameter;
 			}
 		}
 
 		throw new IllegalArgumentException(
 				"Unable to resolve given parameter name [" + name + "] to QueryParameter reference"
 		);
 	}
 
 	/**
 	 * @deprecated (since 5.2) expected changes to "collection-valued parameter binding" in 6.0
 	 */
 	@Deprecated
 	@SuppressWarnings("unchecked")
 	public String expandListValuedParameters(String queryString, SharedSessionContractImplementor session) {
 		if ( queryString == null ) {
 			return null;
 		}
 
 		// more-or-less... for each entry in parameterListBindingMap we will create an
 		//		entry in parameterBindingMap for each of the values in the bound value list.  afterwards
 		//		we will clear the parameterListBindingMap.
 		//
 		// NOTE that this is essentially the legacy logical prior to modeling QueryParameterBinding/QueryParameterListBinding.
 		// 		Fully expect the details of how this is handled in 6.0
 
 		// HHH-1123
 		// Some DBs limit number of IN expressions.  For now, warn...
 		final Dialect dialect = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getJdbcEnvironment().getDialect();
 		final int inExprLimit = dialect.getInExpressionCountLimit();
 
 		for ( Map.Entry<QueryParameter, QueryParameterListBinding> entry : parameterListBindingMap.entrySet() ) {
 			final NamedParameterDescriptor sourceParam = (NamedParameterDescriptor) entry.getKey();
 			final Collection bindValues = entry.getValue().getBindValues();
 
 			if ( inExprLimit > 0 && bindValues.size() > inExprLimit ) {
 				log.tooManyInExpressions( dialect.getClass().getName(), inExprLimit, sourceParam.getName(), bindValues.size() );
 			}
 
 			final boolean isJpaPositionalParam = sourceParam.isJpaPositionalParameter();
 			final String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
 			final String placeholder = paramPrefix + sourceParam.getName();
 			final int loc = queryString.indexOf( placeholder );
 
 			if ( loc < 0 ) {
 				continue;
 			}
 
 			final String beforePlaceholder = queryString.substring( 0, loc );
 			final String afterPlaceholder = queryString.substring( loc + placeholder.length() );
 
 			// check if placeholder is already immediately enclosed in parentheses
 			// (ignoring whitespace)
 			boolean isEnclosedInParens =
 					StringHelper.getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' &&
 							StringHelper.getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')';
 
 			if ( bindValues.size() == 1 && isEnclosedInParens ) {
 				// short-circuit for performance when only 1 value and the
 				// placeholder is already enclosed in parentheses...
 				final QueryParameterBinding syntheticBinding = makeBinding( entry.getValue().getBindType() );
 				syntheticBinding.setBindValue( bindValues.iterator().next() );
 				parameterBindingMap.put( sourceParam, syntheticBinding );
 				continue;
 			}
 
 			StringBuilder expansionList = new StringBuilder();
 
 			int i = 0;
 			for ( Object bindValue : entry.getValue().getBindValues() ) {
 				// for each value in the bound list-of-values we:
 				//		1) create a synthetic named parameter
 				//		2) expand the queryString to include each synthetic named param in place of the original
 				//		3) create a new synthetic binding for just that single value under the synthetic name
 				final String syntheticName = (isJpaPositionalParam ? 'x' : "") + sourceParam.getName() + '_' + i;
 				if ( i > 0 ) {
 					expansionList.append( ", " );
 				}
 				expansionList.append( ParserHelper.HQL_VARIABLE_PREFIX ).append( syntheticName );
 				final QueryParameter syntheticParam = new QueryParameterNamedImpl<>(
 						syntheticName,
 						sourceParam.getSourceLocations(),
 						sourceParam.isJpaPositionalParameter(),
 						sourceParam.getType()
 				);
 				final QueryParameterBinding syntheticBinding = makeBinding( entry.getValue().getBindType() );
 				syntheticBinding.setBindValue( bindValue );
 				if ( parameterBindingMap.put( syntheticParam, syntheticBinding ) != null ) {
 					throw new HibernateException( "Repeated usage of synthetic parameter name [" + syntheticName + "] while expanding list parameter." );
 				}
 				i++;
 			}
 
 			queryString = StringHelper.replace(
 					beforePlaceholder,
 					afterPlaceholder,
 					placeholder,
 					expansionList.toString(),
 					true,
 					true
 			);
 		}
 
 		parameterListBindingMap.clear();
 
 		return queryString;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/procedure/internal/ProcedureParameterBindingImpl.java b/hibernate-core/src/main/java/org/hibernate/query/procedure/internal/ProcedureParameterBindingImpl.java
index c62f73d6d9..46ff69ba91 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/procedure/internal/ProcedureParameterBindingImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/procedure/internal/ProcedureParameterBindingImpl.java
@@ -1,50 +1,55 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.procedure.internal;
 
 import javax.persistence.TemporalType;
 
 import org.hibernate.query.procedure.spi.ProcedureParameterBindingImplementor;
 import org.hibernate.query.procedure.spi.ProcedureParameterImplementor;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public class ProcedureParameterBindingImpl<T> implements ProcedureParameterBindingImplementor<T> {
 	private final ProcedureParameterImplementor<T> parameter;
 
 	public ProcedureParameterBindingImpl(ProcedureParameterImplementor<T> parameter) {
 		this.parameter = parameter;
 	}
 
 	@Override
+	public boolean isBound() {
+		return parameter.getNativeParameterRegistration().getBind() != null;
+	}
+
+	@Override
 	public void setBindValue(T value) {
 		parameter.getNativeParameterRegistration().bindValue( value );
 	}
 
 	@Override
 	public void setBindValue(T value, Type clarifiedType) {
 		parameter.getNativeParameterRegistration().setHibernateType( clarifiedType );
 		parameter.getNativeParameterRegistration().bindValue( value );
 	}
 
 	@Override
 	public void setBindValue(T value, TemporalType clarifiedTemporalType) {
 		parameter.getNativeParameterRegistration().bindValue( value, clarifiedTemporalType );
 	}
 
 	@Override
 	public T getBindValue() {
 		return parameter.getNativeParameterRegistration().getBind().getValue();
 	}
 
 	@Override
 	public Type getBindType() {
 		return parameter.getNativeParameterRegistration().getHibernateType();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/spi/QueryParameterBinding.java b/hibernate-core/src/main/java/org/hibernate/query/spi/QueryParameterBinding.java
index 2bd27ebd5e..94ed66820c 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/spi/QueryParameterBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/spi/QueryParameterBinding.java
@@ -1,57 +1,59 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.spi;
 
 import javax.persistence.TemporalType;
 
 import org.hibernate.Incubating;
 import org.hibernate.type.Type;
 
 /**
  * The value/type binding information for a particular query parameter.
  *
  * @author Steve Ebersole
  */
 @Incubating
 public interface QueryParameterBinding<T> {
+	boolean isBound();
+
 	/**
 	 * Sets the parameter binding value.  The inherent parameter type (if known) is assumed
 	 *
 	 * @param value The bind value
 	 */
 	void setBindValue(T value);
 
 	/**
 	 * Sets the parameter binding value using the explicit Type.
 	 *
 	 * @param value The bind value
 	 * @param clarifiedType The explicit Type to use
 	 */
 	void setBindValue(T value, Type clarifiedType);
 
 	/**
 	 * Sets the parameter binding value using the explicit TemporalType.
 	 *
 	 * @param value The bind value
 	 * @param clarifiedTemporalType The temporal type to use
 	 */
 	void setBindValue(T value, TemporalType clarifiedTemporalType);
 
 	/**
 	 * Get the value current bound.
 	 *
 	 * @return The currently bound value
 	 */
 	T getBindValue();
 
 	/**
 	 * Get the Type currently associated with this binding.
 	 *
 	 * @return The currently associated Type
 	 */
 	Type getBindType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index 914ab551ad..29289f10bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,516 +1,516 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
 	private final TypeFactory.TypeScope scope;
 	private final Type identifierType;
 	private final Type discriminatorType;
 
 	/**
 	 * Intended for use only from legacy {@link ObjectType} type definition
 	 */
 	protected AnyType(Type discriminatorType, Type identifierType) {
 		this( null, discriminatorType, identifierType );
 	}
 
 	public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType) {
 		this.scope = scope;
 		this.discriminatorType = discriminatorType;
 		this.identifierType = identifierType;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 
 	// general Type metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getName() {
 		return "object";
 	}
 
 	@Override
 	public Class getReturnedClass() {
 		return Object.class;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.sqlTypes( mapping ), identifierType.sqlTypes( mapping ) );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.dictatedSizes( mapping ), identifierType.dictatedSizes( mapping ) );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.defaultSizes( mapping ), identifierType.defaultSizes( mapping ) );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isAnyType() {
 		return true;
 	}
 
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponentType() {
 		return true;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value;
 	}
 
 
 	// general Type functionality ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int compare(Object x, Object y) {
 		if ( x == null ) {
 			// if y is also null, return that they are the same (no option for "UNKNOWN")
 			// if y is not null, return that y is "greater" (-1 because the result is from the perspective of
 			// 		the first arg: x)
 			return y == null ? 0 : -1;
 		}
 		else if ( y == null ) {
 			// x is not null, but y is.  return that x is "greater"
 			return 1;
 		}
 
 		// At this point we know both are non-null.
 		final Object xId = extractIdentifier( x );
 		final Object yId = extractIdentifier( y );
 
 		return getIdentifierType().compare( xId, yId );
 	}
 
 	private Object extractIdentifier(Object entity) {
 		final EntityPersister concretePersister = guessEntityPersister( entity );
 		return concretePersister == null
 				? null
 				: concretePersister.getEntityTuplizer().getIdentifier( entity, null );
 	}
 
 	private EntityPersister guessEntityPersister(Object object) {
 		if ( scope == null ) {
 			return null;
 		}
 
 		String entityName = null;
 
 		// this code is largely copied from Session's bestGuessEntityName
 		Object entity = object;
 		if ( entity instanceof HibernateProxy ) {
 			final LazyInitializer initializer = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
 			if ( initializer.isUninitialized() ) {
 				entityName = initializer.getEntityName();
 			}
 			entity = initializer.getImplementation();
 		}
 
 		if ( entityName == null ) {
-			for ( EntityNameResolver resolver : scope.resolveFactory().iterateEntityNameResolvers() ) {
+			for ( EntityNameResolver resolver : scope.resolveFactory().getMetamodel().getEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			// the old-time stand-by...
 			entityName = object.getClass().getName();
 		}
 
 		return scope.resolveFactory().getMetamodel().entityPersister( entityName );
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
 			throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		else if ( old == null ) {
 			return true;
 		}
 
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
 		final boolean[] idCheckable = new boolean[checkable.length-1];
 		System.arraycopy( checkable, 1, idCheckable, 0, idCheckable.length );
 		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName( current ) ) )
 				|| identifierType.isModified( holder.id, getIdentifier( current, session ), idCheckable, session );
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		final boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
 			throws HibernateException {
 		return isDirty( old, current, session );
 	}
 
 	@Override
 	public int getColumnSpan(Mapping session) {
 		return 2;
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String[] names,	SharedSessionContractImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		return resolveAny(
 				(String) discriminatorType.nullSafeGet( rs, names[0], session, owner ),
 				(Serializable) identifierType.nullSafeGet( rs, names[1], session, owner ),
 				session
 		);
 	}
 
 	@Override
 	public Object hydrate(ResultSet rs,	String[] names,	SharedSessionContractImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		final String entityName = (String) discriminatorType.nullSafeGet( rs, names[0], session, owner );
 		final Serializable id = (Serializable) identifierType.nullSafeGet( rs, names[1], session, owner );
 		return new ObjectTypeCacheEntry( entityName, id );
 	}
 
 	@Override
 	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
 		return resolveAny( holder.entityName, holder.id, session );
 	}
 
 	private Object resolveAny(String entityName, Serializable id, SharedSessionContractImplementor session)
 			throws HibernateException {
 		return entityName==null || id==null
 				? null
 				: session.internalLoad( entityName, id, false, false );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, null, session );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		Serializable id;
 		String entityName;
 		if ( value == null ) {
 			id = null;
 			entityName = null;
 		}
 		else {
 			entityName = session.bestGuessEntityName( value );
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, value, session );
 		}
 
 		// discriminatorType is assumed to be single-column type
 		if ( settable == null || settable[0] ) {
 			discriminatorType.nullSafeSet( st, entityName, index, session );
 		}
 		if ( settable == null ) {
 			identifierType.nullSafeSet( st, id, index+1, session );
 		}
 		else {
 			final boolean[] idSettable = new boolean[ settable.length-1 ];
 			System.arraycopy( settable, 1, idSettable, 0, idSettable.length );
 			identifierType.nullSafeSet( st, id, index+1, idSettable, session );
 		}
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		//TODO: terrible implementation!
 		return value == null
 				? "null"
 				: factory.getTypeHelper()
 				.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
 				.toLoggableString( value, factory );
 	}
 
 	@Override
 	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
 		return e == null ? null : session.internalLoad( e.entityName, e.id, false, false );
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			return new ObjectTypeCacheEntry(
 					session.bestGuessEntityName( value ),
 					ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							session.bestGuessEntityName( value ),
 							value,
 							session
 					)
 			);
 		}
 	}
 
 	@Override
 	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache)
 			throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		else {
 			final String entityName = session.bestGuessEntityName( original );
 			final Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, original, session );
 			return session.internalLoad( entityName, id, false, false );
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String name, SharedSessionContractImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "object is a multicolumn type" );
 	}
 
 	@Override
 	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "any mappings may not form part of a property-ref" );
 	}
 
 	// CompositeType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
 	@Override
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	@Override
 	public int getPropertyIndex(String name) {
 		if ( PROPERTY_NAMES[0].equals( name ) ) {
 			return 0;
 		}
 		else if ( PROPERTY_NAMES[1].equals( name ) ) {
 			return 1;
 		}
 
 		throw new PropertyNotFoundException( "Unable to locate property named " + name + " on AnyType" );
 	}
 
 	@Override
 	public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session) throws HibernateException {
 		return i==0
 				? session.bestGuessEntityName( component )
 				: getIdentifier( component, session );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session) throws HibernateException {
 		return new Object[] {
 				session.bestGuessEntityName( component ),
 				getIdentifier( component, session )
 		};
 	}
 
 	private Serializable getIdentifier(Object value, SharedSessionContractImplementor session) throws HibernateException {
 		try {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					session.bestGuessEntityName( value ),
 					value,
 					session
 			);
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	private static final boolean[] NULLABILITY = new boolean[] { false, false };
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return NULLABILITY;
 	}
 
 	@Override
 	public boolean hasNotNullProperty() {
 		// both are non-nullable
 		return true;
 	}
 
 	@Override
 	public Type[] getSubtypes() {
 		return new Type[] {discriminatorType, identifierType };
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyles.NONE;
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.SELECT;
 	}
 
 
 	// AssociationType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FROM_PARENT;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Used to externalize discrimination per a given identifier.  For example, when writing to
 	 * second level cache we write the discrimination resolved concrete type for each entity written.
 	 */
 	public static final class ObjectTypeCacheEntry implements Serializable {
 		final String entityName;
 		final Serializable id;
 
 		ObjectTypeCacheEntry(String entityName, Serializable id) {
 			this.entityName = entityName;
 			this.id = id;
 		}
 	}
 }
