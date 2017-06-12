diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
index d98a5d9659..42537fbdca 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
@@ -1,144 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Method;
 import java.util.HashMap;
 
 import javassist.util.proxy.MethodFilter;
 import javassist.util.proxy.MethodHandler;
 import javassist.util.proxy.Proxy;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.bytecode.spi.BasicProxyFactory;
 import org.hibernate.bytecode.spi.ProxyFactoryFactory;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.proxy.pojo.javassist.JavassistProxyFactory;
 
 /**
  * A factory for Javassist-based {@link ProxyFactory} instances.
  *
  * @author Steve Ebersole
  */
 public class ProxyFactoryFactoryImpl implements ProxyFactoryFactory {
 
 	/**
 	 * Builds a Javassist-based proxy factory.
 	 *
 	 * @return a new Javassist-based proxy factory.
 	 */
-	public ProxyFactory buildProxyFactory() {
+	@Override
+	public ProxyFactory buildProxyFactory(SessionFactoryImplementor sessionFactory) {
 		return new JavassistProxyFactory();
 	}
 
 	/**
 	 * Constructs a BasicProxyFactoryImpl
 	 *
 	 * @param superClass The abstract super class (or null if none).
 	 * @param interfaces Interfaces to be proxied (or null if none).
 	 *
 	 * @return The constructed BasicProxyFactoryImpl
 	 */
+	@Override
 	public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces) {
 		return new BasicProxyFactoryImpl( superClass, interfaces );
 	}
 
 	private static class BasicProxyFactoryImpl implements BasicProxyFactory {
 		private final Class proxyClass;
 
 		public BasicProxyFactoryImpl(Class superClass, Class[] interfaces) {
 			if ( superClass == null && ( interfaces == null || interfaces.length < 1 ) ) {
 				throw new AssertionFailure( "attempting to build proxy without any superclass or interfaces" );
 			}
 
 			final javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
 			factory.setFilter( FINALIZE_FILTER );
 			if ( superClass != null ) {
 				factory.setSuperclass( superClass );
 			}
 			if ( interfaces != null && interfaces.length > 0 ) {
 				factory.setInterfaces( interfaces );
 			}
 			proxyClass = factory.createClass();
 		}
 
 		public Object getProxy() {
 			try {
 				final Proxy proxy = (Proxy) proxyClass.newInstance();
 				proxy.setHandler( new PassThroughHandler( proxy, proxyClass.getName() ) );
 				return proxy;
 			}
 			catch ( Throwable t ) {
 				throw new HibernateException( "Unable to instantiated proxy instance" );
 			}
 		}
 
 		public boolean isInstance(Object object) {
 			return proxyClass.isInstance( object );
 		}
 	}
 
 	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
 		public boolean isHandled(Method m) {
 			// skip finalize methods
 			return !( m.getParameterTypes().length == 0 && m.getName().equals( "finalize" ) );
 		}
 	};
 
 	private static class PassThroughHandler implements MethodHandler {
 		private HashMap data = new HashMap();
 		private final Object proxiedObject;
 		private final String proxiedClassName;
 
 		public PassThroughHandler(Object proxiedObject, String proxiedClassName) {
 			this.proxiedObject = proxiedObject;
 			this.proxiedClassName = proxiedClassName;
 		}
 
 		@SuppressWarnings("unchecked")
 		public Object invoke(
 				Object object,
 				Method method,
 				Method method1,
 				Object[] args) throws Exception {
 			final String name = method.getName();
 			if ( "toString".equals( name ) ) {
 				return proxiedClassName + "@" + System.identityHashCode( object );
 			}
 			else if ( "equals".equals( name ) ) {
 				return proxiedObject == object;
 			}
 			else if ( "hashCode".equals( name ) ) {
 				return System.identityHashCode( object );
 			}
 
 			final boolean hasGetterSignature = method.getParameterTypes().length == 0
 					&& method.getReturnType() != null;
 			final boolean hasSetterSignature = method.getParameterTypes().length == 1
 					&& ( method.getReturnType() == null || method.getReturnType() == void.class );
 
 			if ( name.startsWith( "get" ) && hasGetterSignature ) {
 				final String propName = name.substring( 3 );
 				return data.get( propName );
 			}
 			else if ( name.startsWith( "is" ) && hasGetterSignature ) {
 				final String propName = name.substring( 2 );
 				return data.get( propName );
 			}
 			else if ( name.startsWith( "set" ) && hasSetterSignature) {
 				final String propName = name.substring( 3 );
 				data.put( propName, args[0] );
 				return null;
 			}
 			else {
 				// todo : what else to do here?
 				return null;
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ProxyFactoryFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ProxyFactoryFactory.java
index 7808207f25..7c94d55aa5 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ProxyFactoryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ProxyFactoryFactory.java
@@ -1,43 +1,47 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.spi;
 
+import org.hibernate.SessionFactory;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.proxy.ProxyFactory;
+import org.hibernate.service.ServiceRegistry;
 
 /**
  * An interface for factories of {@link ProxyFactory proxy factory} instances.
  * <p/>
  * Currently used to abstract from the tupizer whether we are using CGLIB or
  * Javassist for lazy proxy generation.
  *
  * @author Steve Ebersole
  */
 public interface ProxyFactoryFactory {
 	/**
 	 * Build a proxy factory specifically for handling runtime
 	 * lazy loading.
 	 *
 	 * @return The lazy-load proxy factory.
 	 */
-	public ProxyFactory buildProxyFactory();
+	public ProxyFactory buildProxyFactory(SessionFactoryImplementor sessionFactory);
 
 	/**
 	 * Build a proxy factory for basic proxy concerns.  The return
 	 * should be capable of properly handling newInstance() calls.
 	 * <p/>
 	 * Should build basic proxies essentially equivalent to JDK proxies in
 	 * terms of capabilities, but should be able to deal with abstract super
 	 * classes in addition to proxy interfaces.
 	 * <p/>
 	 * Must pass in either superClass or interfaces (or both).
 	 *
 	 * @param superClass The abstract super class (or null if none).
 	 * @param interfaces Interfaces to be proxied (or null if none).
 	 * @return The proxy class
 	 */
 	public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java
index 9d40e98005..6dc65a61a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java
@@ -1,409 +1,414 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 
 import org.hibernate.Cache;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.spi.SessionFactoryOptions;
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
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Base delegating implementation of the SessionFactory and SessionFactoryImplementor
  * contracts for intended for easier implementation of SessionFactory.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({"deprecation", "unused"})
 public class SessionFactoryDelegatingImpl implements SessionFactoryImplementor, SessionFactory {
 	private final SessionFactoryImplementor delegate;
 
 	public SessionFactoryDelegatingImpl(SessionFactoryImplementor delegate) {
 		this.delegate = delegate;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return delegate.getSessionFactoryOptions();
 	}
 
 	@Override
 	public SessionBuilderImplementor withOptions() {
 		return delegate.withOptions();
 	}
 
 	@Override
 	public Session openSession() throws HibernateException {
 		return delegate.openSession();
 	}
 
 	@Override
 	public Session getCurrentSession() throws HibernateException {
 		return delegate.getCurrentSession();
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return delegate.withStatelessOptions();
 	}
 
 	@Override
 	public StatelessSession openStatelessSession() {
 		return delegate.openStatelessSession();
 	}
 
 	@Override
 	public StatelessSession openStatelessSession(Connection connection) {
 		return delegate.openStatelessSession( connection );
 	}
 
 	@Override
 	public ClassMetadata getClassMetadata(Class entityClass) {
 		return delegate.getClassMetadata( entityClass );
 	}
 
 	@Override
 	public ClassMetadata getClassMetadata(String entityName) {
 		return delegate.getClassMetadata( entityName );
 	}
 
 	@Override
 	public CollectionMetadata getCollectionMetadata(String roleName) {
 		return delegate.getCollectionMetadata( roleName );
 	}
 
 	@Override
 	public Map<String, ClassMetadata> getAllClassMetadata() {
 		return delegate.getAllClassMetadata();
 	}
 
 	@Override
 	public Map getAllCollectionMetadata() {
 		return delegate.getAllCollectionMetadata();
 	}
 
 	@Override
 	public Statistics getStatistics() {
 		return delegate.getStatistics();
 	}
 
 	@Override
 	public void close() throws HibernateException {
 		delegate.close();
 	}
 
 	@Override
 	public boolean isClosed() {
 		return delegate.isClosed();
 	}
 
 	@Override
 	public Cache getCache() {
 		return delegate.getCache();
 	}
 
 	@Override
 	public Set getDefinedFilterNames() {
 		return delegate.getDefinedFilterNames();
 	}
 
 	@Override
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		return delegate.getFilterDefinition( filterName );
 	}
 
 	@Override
 	public boolean containsFetchProfileDefinition(String name) {
 		return delegate.containsFetchProfileDefinition( name );
 	}
 
 	@Override
 	public TypeHelper getTypeHelper() {
 		return delegate.getTypeHelper();
 	}
 
 	@Override
 	public TypeResolver getTypeResolver() {
 		return delegate.getTypeResolver();
 	}
 
 	@Override
 	public Properties getProperties() {
 		return delegate.getProperties();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		return delegate.getEntityPersister( entityName );
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return delegate.getEntityPersisters();
 	}
 
 	@Override
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		return delegate.getCollectionPersister( role );
 	}
 
 	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return delegate.getCollectionPersisters();
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return delegate.getJdbcServices();
 	}
 
 	@Override
 	public Dialect getDialect() {
 		return delegate.getDialect();
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return delegate.getInterceptor();
 	}
 
 	@Override
 	public QueryPlanCache getQueryPlanCache() {
 		return delegate.getQueryPlanCache();
 	}
 
 	@Override
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return delegate.getReturnTypes( queryString );
 	}
 
 	@Override
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return delegate.getReturnAliases( queryString );
 	}
 
 	@Override
 	public String[] getImplementors(String className) throws MappingException {
 		return delegate.getImplementors( className );
 	}
 
 	@Override
 	public String getImportedClassName(String name) {
 		return delegate.getImportedClassName( name );
 	}
 
 	@Override
 	public QueryCache getQueryCache() {
 		return delegate.getQueryCache();
 	}
 
 	@Override
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return delegate.getQueryCache( regionName );
 	}
 
 	@Override
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return delegate.getUpdateTimestampsCache();
 	}
 
 	@Override
 	public StatisticsImplementor getStatisticsImplementor() {
 		return delegate.getStatisticsImplementor();
 	}
 
 	@Override
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return delegate.getNamedQuery( queryName );
 	}
 
 	@Override
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		delegate.registerNamedQueryDefinition( name, definition );
 	}
 
 	@Override
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return delegate.getNamedSQLQuery( queryName );
 	}
 
 	@Override
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		delegate.registerNamedQueryDefinition( name, definition );
 	}
 
 	@Override
 	public ResultSetMappingDefinition getResultSetMapping(String name) {
 		return delegate.getResultSetMapping( name );
 	}
 
 	@Override
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return delegate.getIdentifierGenerator( rootEntityName );
 	}
 
 	@Override
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return delegate.getSecondLevelCacheRegion( regionName );
 	}
 
 	@Override
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return delegate.getNaturalIdCacheRegion( regionName );
 	}
 
 	@Override
 	public Map getAllSecondLevelCacheRegions() {
 		return delegate.getAllSecondLevelCacheRegions();
 	}
 
 	@Override
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return delegate.getSQLExceptionConverter();
 	}
 
 	@Override
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return delegate.getSQLExceptionHelper();
 	}
 
 	@Override
 	public Settings getSettings() {
 		return delegate.getSettings();
 	}
 
 	@Override
 	public Session openTemporarySession() throws HibernateException {
 		return delegate.openTemporarySession();
 	}
 
 	@Override
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return delegate.getCollectionRolesByEntityParticipant( entityName );
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return delegate.getEntityNotFoundDelegate();
 	}
 
 	@Override
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return delegate.getSqlFunctionRegistry();
 	}
 
 	@Override
 	public FetchProfile getFetchProfile(String name) {
 		return delegate.getFetchProfile( name );
 	}
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return delegate.getServiceRegistry();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		delegate.addObserver( observer );
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return delegate.getCustomEntityDirtinessStrategy();
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return delegate.getCurrentTenantIdentifierResolver();
 	}
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return delegate.getNamedQueryRepository();
 	}
 
 	@Override
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return delegate.iterateEntityNameResolvers();
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(Class byClass) {
 		return delegate.locateEntityPersister( byClass );
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(String byName) {
 		return delegate.locateEntityPersister( byName );
 	}
 
 	@Override
+	public DeserializationResolver getDeserializationResolver() {
+		return delegate.getDeserializationResolver();
+	}
+
+	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return delegate.getIdentifierGeneratorFactory();
 	}
 
 	@Override
 	public Type getIdentifierType(String className) throws MappingException {
 		return delegate.getIdentifierType( className );
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return delegate.getIdentifierPropertyName( className );
 	}
 
 	@Override
 	public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
 		return delegate.getReferencedPropertyType( className, propertyName );
 	}
 
 	@Override
 	public Reference getReference() throws NamingException {
 		return delegate.getReference();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index da77d0ecac..a679dd8f5c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,305 +1,315 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
+import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityNameResolver;
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
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
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
 	SessionBuilderImplementor withOptions();
 
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get all entity persisters as a Map, which entity name its the key and the persister is the value.
 	 *
 	 * @return The Map contains all entity persisters.
 	 */
 	Map<String,EntityPersister> getEntityPersisters();
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get all collection persisters as a Map, which collection role as the key and the persister is the value.
 	 *
 	 * @return The Map contains all collection persisters.
 	 */
 	Map<String, CollectionPersister> getCollectionPersisters();
 
 	/**
 	 * Get the JdbcServices.
 	 *
 	 * @return the JdbcServices
 	 *
 	 * @deprecated since 5.0; use {@link #getServiceRegistry()} instead to locate the JdbcServices
 	 */
 	@Deprecated
 	JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@code getJdbcServices().getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	Interceptor getInterceptor();
 
 	QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	String getImportedClassName(String name);
 
 	/**
 	 * Get the default query cache
 	 */
 	QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	QueryCache getQueryCache(String regionName) throws HibernateException;
 
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	StatisticsImplementor getStatisticsImplementor();
 
 	NamedQueryDefinition getNamedQuery(String queryName);
 
 	void registerNamedQueryDefinition(String name, NamedQueryDefinition definition);
 
 	NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 
 	void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition);
 
 	ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	Region getSecondLevelCacheRegion(String regionName);
 	
 	/**
 	 * Get a named naturalId cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	Region getNaturalIdCacheRegion(String regionName);
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 * @deprecated since 5.0; use {@link JdbcServices#getSqlExceptionHelper()} ->
 	 * {@link SqlExceptionHelper#getSqlExceptionConverter()} instead as obtained from {@link #getServiceRegistry()}
 	 */
 	@Deprecated
 	SQLExceptionConverter getSQLExceptionConverter();
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 * @deprecated since 5.0; use {@link JdbcServices#getSqlExceptionHelper()} instead as
 	 * obtained from {@link #getServiceRegistry()}
 	 */
 	@Deprecated
 	SqlExceptionHelper getSQLExceptionHelper();
 
 	/**
 	 * @deprecated since 5.0; use {@link #getSessionFactoryOptions()} instead
 	 */
 	@Deprecated
 	@SuppressWarnings("deprecation")
 	Settings getSettings();
 
 	/**
 	 * Get a non-transactional "current" session for Hibernate EntityManager
 	 */
 	Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	FetchProfile getFetchProfile(String name);
 
 	ServiceRegistryImplementor getServiceRegistry();
 
 	void addObserver(SessionFactoryObserver observer);
 
 	CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 
 	/**
 	 * Provides access to the named query repository
 	 *
 	 * @return The repository for named query definitions
 	 */
 	NamedQueryRepository getNamedQueryRepository();
 
 	Iterable<EntityNameResolver> iterateEntityNameResolvers();
 
 	/**
 	 * Locate an EntityPersister by the entity class.  The passed Class might refer to either
 	 * the entity name directly, or it might name a proxy interface for the entity.  This
 	 * method accounts for both, preferring the direct named entity name.
 	 *
 	 * @param byClass The concrete Class or proxy interface for the entity to locate the persister for.
 	 *
 	 * @return The located EntityPersister, never {@code null}
 	 *
 	 * @throws HibernateException If a matching EntityPersister cannot be located
 	 */
 	EntityPersister locateEntityPersister(Class byClass);
 
 	/**
 	 * Locate the entity persister by name.
 	 *
 	 * @param byName The entity name
 	 *
 	 * @return The located EntityPersister, never {@code null}
 	 *
 	 * @throws HibernateException If a matching EntityPersister cannot be located
 	 */
 	EntityPersister locateEntityPersister(String byName);
+
+	/**
+	 * Contract for resolving this SessionFactory on deserialization
+	 */
+	interface DeserializationResolver<T extends SessionFactoryImplementor> extends Serializable {
+		T resolve();
+	}
+
+	DeserializationResolver getDeserializationResolver();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 5ae0a321ce..4459629915 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1518 +1,1528 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
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
 import org.hibernate.Transaction;
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
 import org.hibernate.boot.cfgxml.spi.LoadedConfig;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.ReturnMetadata;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.config.ConfigurationException;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.secure.spi.JaccService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
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
  * @see org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl implements SessionFactoryImplementor {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( SessionFactoryImpl.class );
 
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map<Class,String> entityProxyInterfaceMap;
 	private final transient Map<String,CollectionPersister> collectionPersisters;
 	private final transient Map<String,CollectionMetadata> collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient NamedQueryRepository namedQueryRepository;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient JdbcServices jdbcServices;
 	private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient CacheImplementor cacheAccess;
 	private transient boolean isClosed;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 
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
 
 		this.properties = new Properties();
 		this.properties.putAll( serviceRegistry.getService( ConfigurationService.class ).getSettings() );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), options.getCustomSqlFunctionMap() );
 
 		for ( SessionFactoryObserver sessionFactoryObserver : options.getSessionFactoryObservers() ) {
 			this.observer.addObserver( sessionFactoryObserver );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( metadata.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
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
 
 		this.identifierGenerators = new HashMap<String, IdentifierGenerator>();
 		for ( PersistentClass model : metadata.getEntityBindings() ) {
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						metadata.getIdentifierGeneratorFactory(),
 						getDialect(),
 						settings.getDefaultCatalogName(),
 						settings.getDefaultSchemaName(),
 						(RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 		this.imports = new HashMap<String,String>( metadata.getImports() );
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final PersisterCreationContext persisterCreationContext = new PersisterCreationContext() {
 			@Override
 			public SessionFactoryImplementor getSessionFactory() {
 				return SessionFactoryImpl.this;
 			}
 
 			@Override
 			public MetadataImplementor getMetadata() {
 				return metadata;
 			}
 		};
 
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 		final PersisterFactory persisterFactory = serviceRegistry.getService( PersisterFactory.class );
 
 		// todo : consider removing this silliness and just have EntityPersister directly implement ClassMetadata
 		//		EntityPersister.getClassMetadata() for the internal impls simply "return this";
 		//		collapsing those would allow us to remove this "extra" Map
 		//
 		// todo : similar for CollectionPersister/CollectionMetadata
 
 		this.entityPersisters = new HashMap<String,EntityPersister>();
 		Map cacheAccessStrategiesMap = new HashMap();
 		Map<String,ClassMetadata> inFlightClassMetadataMap = new HashMap<String,ClassMetadata>();
 		this.entityProxyInterfaceMap = CollectionHelper.concurrentMap( metadata.getEntityBindings().size() );
 		for ( final PersistentClass model : metadata.getEntityBindings() ) {
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			final EntityRegionAccessStrategy accessStrategy = determineEntityRegionAccessStrategy(
 					regionFactory,
 					cacheAccessStrategiesMap,
 					model,
 					cacheRegionName
 			);
 
 			final NaturalIdRegionAccessStrategy naturalIdAccessStrategy = determineNaturalIdRegionAccessStrategy(
 					regionFactory,
 					cacheRegionPrefix,
 					cacheAccessStrategiesMap,
 					model
 			);
 
 			final EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					persisterCreationContext
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			inFlightClassMetadataMap.put( model.getEntityName(), cp.getClassMetadata() );
 
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
 					LOG.debugf( "Entity [%s] mapped same interface [%s] as class and proxy", cp.getEntityName(), cp.getMappedClass() );
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
 		this.classMetadata = Collections.unmodifiableMap( inFlightClassMetadataMap );
 
 		this.collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String,Set<String>> inFlightEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		for ( final Collection model : metadata.getCollectionBindings() ) {
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			final CollectionRegionAccessStrategy accessStrategy;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				LOG.tracev( "Building shared cache region for collection data [{0}]", model.getRole() );
 				CollectionRegion collectionRegion = regionFactory.buildCollectionRegion(
 						cacheRegionName,
 						properties,
 						CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				cacheAccessStrategiesMap.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, collectionRegion );
 			}
 			else {
 				accessStrategy = null;
 			}
 
 			final CollectionPersister persister = persisterFactory.createCollectionPersister(
 					model,
 					accessStrategy,
 					persisterCreationContext
 			);
 			collectionPersisters.put( model.getRole(), persister );
 			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set<String> roles = inFlightEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					inFlightEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set<String> roles = inFlightEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					inFlightEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		this.collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 
 		for ( Map.Entry<String,Set<String>> entityToCollectionRoleMapEntry : inFlightEntityToCollectionRoleMap.entrySet() ) {
 			entityToCollectionRoleMapEntry.setValue(
 					Collections.unmodifiableSet( entityToCollectionRoleMapEntry.getValue() )
 			);
 		}
 		this.collectionRolesByEntityParticipant = Collections.unmodifiableMap( inFlightEntityToCollectionRoleMap );
 
 		//Named Queries:
 		this.namedQueryRepository = metadata.buildNamedQueryRepository( this );
 
 		// after *all* persisters and named queries are registered
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.generateEntityDefinition();
 		}
 
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 		}
 		for ( CollectionPersister persister : collectionPersisters.values() ) {
 			persister.postInstantiate();
 		}
 
 		LOG.debug( "Instantiated session factory" );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				metadata,
 				sessionFactoryOptions
 		);
 
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, metadata ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, metadata ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
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
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.mapping.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null
 						? null
 						: entityPersisters.get( entityName );
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
 
 	private NaturalIdRegionAccessStrategy determineNaturalIdRegionAccessStrategy(
 			RegionFactory regionFactory,
 			String cacheRegionPrefix,
 			Map cacheAccessStrategiesMap,
 			PersistentClass model) {
 		NaturalIdRegionAccessStrategy naturalIdAccessStrategy = null;
 		if ( model.hasNaturalId() && model.getNaturalIdCacheRegionName() != null ) {
 			final String naturalIdCacheRegionName = cacheRegionPrefix + model.getNaturalIdCacheRegionName();
 			naturalIdAccessStrategy = ( NaturalIdRegionAccessStrategy ) cacheAccessStrategiesMap.get( naturalIdCacheRegionName );
 
 			if ( naturalIdAccessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final CacheDataDescriptionImpl cacheDataDescription = CacheDataDescriptionImpl.decode( model );
 
 				NaturalIdRegion naturalIdRegion = null;
 				try {
 					naturalIdRegion = regionFactory.buildNaturalIdRegion(
 							naturalIdCacheRegionName,
 							properties,
 							cacheDataDescription
 					);
 				}
 				catch ( UnsupportedOperationException e ) {
 					LOG.warnf(
 							"Shared cache region factory [%s] does not support natural id caching; " +
 									"shared NaturalId caching will be disabled for not be enabled for %s",
 							regionFactory.getClass().getName(),
 							model.getEntityName()
 					);
 				}
 
 				if (naturalIdRegion != null) {
 					naturalIdAccessStrategy = naturalIdRegion.buildAccessStrategy( regionFactory.getDefaultAccessType() );
 					cacheAccessStrategiesMap.put( naturalIdCacheRegionName, naturalIdAccessStrategy );
 					cacheAccess.addCacheRegion(  naturalIdCacheRegionName, naturalIdRegion );
 				}
 			}
 		}
 		return naturalIdAccessStrategy;
 	}
 
 	private EntityRegionAccessStrategy determineEntityRegionAccessStrategy(
 			RegionFactory regionFactory,
 			Map cacheAccessStrategiesMap,
 			PersistentClass model,
 			String cacheRegionName) {
 		EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) cacheAccessStrategiesMap.get( cacheRegionName );
 		if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			if ( accessType != null ) {
 				LOG.tracef( "Building shared cache region for entity data [%s]", model.getEntityName() );
 				EntityRegion entityRegion = regionFactory.buildEntityRegion(
 						cacheRegionName,
 						properties,
 						CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = entityRegion.buildAccessStrategy( accessType );
 				cacheAccessStrategiesMap.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 			}
 		}
 		return accessStrategy;
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
 
 	@Override
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		return namedQueryRepository.checkNamedQueries( queryPlanCache );
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = entityPersisters.get( entityName );
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(Class byClass) {
 		EntityPersister entityPersister = entityPersisters.get( byClass.getName() );
 		if ( entityPersister == null ) {
 			String mappedEntityName = entityProxyInterfaceMap.get( byClass );
 			if ( mappedEntityName != null ) {
 				entityPersister = entityPersisters.get( mappedEntityName );
 			}
 		}
 
 		if ( entityPersister == null ) {
 			throw new HibernateException( "Unable to locate persister: " + byClass.getName() );
 		}
 
 		return entityPersister;
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(String byName) {
 		final EntityPersister entityPersister = entityPersisters.get( byName );
 		if ( entityPersister == null ) {
 			throw new HibernateException( "Unable to locate persister: " + byName );
 		}
 		return entityPersister;
 	}
 
 	@Override
+	public DeserializationResolver getDeserializationResolver() {
+		return new DeserializationResolver() {
+			@Override
+			public SessionFactoryImplementor resolve() {
+				return (SessionFactoryImplementor) SessionFactoryRegistry.INSTANCE.findSessionFactory( uuid, name );
+			}
+		};
+	}
+
+	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = collectionPersisters.get(role);
 		if ( result == null ) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	@SuppressWarnings("deprecation")
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
 	public Reference getReference() {
 		// from javax.naming.Referenceable
 		LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return namedQueryRepository;
 	}
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		namedQueryRepository.registerNamedQueryDefinition( name, definition );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueryRepository.getNamedQueryDefinition( queryName );
 	}
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		namedQueryRepository.registerNamedSQLQueryDefinition( name, definition );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedQueryRepository.getNamedSQLQueryDefinition( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String mappingName) {
 		return namedQueryRepository.getResultSetMappingDefinition( mappingName );
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
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
 		return collectionMetadata.get( roleName );
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get( entityName );
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
 			clazz = serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 		}
 		catch (ClassLoadingException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList<String> results = new ArrayList<String>();
 		for ( EntityPersister checkPersister : entityPersisters.values() ) {
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
 							Class mappedSuperclass = getEntityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
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
 	public String getImportedClassName(String className) {
 		String result = imports.get( className );
 		if ( result == null ) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
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
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		settings.getMultiTableBulkIdStrategy().release( jdbcServices, buildLocalConnectionAccess() );
 
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
 
 		cacheAccess.close();
 
 		queryPlanCache.cleanup();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
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
 		cacheAccess.evictQueries();
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return cacheAccess.getUpdateTimestampsCache();
 	}
 
 	public QueryCache getQueryCache() {
 		return cacheAccess.getQueryCache();
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return cacheAccess.getQueryCache( regionName );
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return cacheAccess.getSecondLevelCacheRegion( regionName );
 	}
 
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return cacheAccess.getNaturalIdCacheRegion( regionName );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Map getAllSecondLevelCacheRegions() {
 		return cacheAccess.getAllSecondLevelCacheRegions();
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
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
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
 
 	static class SessionBuilderImpl implements SessionBuilderImplementor {
 		private static final Logger log = CoreLogging.logger( SessionBuilderImpl.class );
 
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private StatementInspector statementInspector;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 		private List<SessionEventListener> listeners;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.statementInspector = sessionFactory.getSessionFactoryOptions().getStatementInspector();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 
 			listeners = settings.getBaselineSessionEventsListenerBuilder().buildBaselineList();
 		}
 
 		protected TransactionCoordinator getTransactionCoordinator() {
 			return null;
 		}
 
 		protected JdbcCoordinatorImpl getJdbcCoordinator() {
 			return null;
 		}
 
 		protected Transaction getTransaction() {
 			return null;
 		}
 
 		protected ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			log.tracef( "Opening Hibernate Session.  tenant=%s, owner=%s", tenantIdentifier, sessionOwner );
 			final SessionImpl session = new SessionImpl(
 					connection,
 					sessionFactory,
 					sessionOwner,
 					getTransactionCoordinator(),
 					getJdbcCoordinator(),
 					getTransaction(),
 					getTransactionCompletionProcesses(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					statementInspector,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 
 			for ( SessionEventListener listener : listeners ) {
 				session.getEventListenerManager().addListener( listener );
 			}
 
 			return session;
 		}
 
 		@Override
 		public SessionBuilder owner(SessionOwner sessionOwner) {
 			this.sessionOwner = sessionOwner;
 			return this;
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
 		public SessionBuilder statementInspector(StatementInspector statementInspector) {
 			this.statementInspector = statementInspector;
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
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder eventListeners(SessionEventListener... listeners) {
 			Collections.addAll( this.listeners, listeners );
 			return this;
 		}
 
 		@Override
 		public SessionBuilder clearEventListeners() {
 			listeners.clear();
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
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
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory,
 					sessionFactory.settings.getRegionFactory().nextTimestamp() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java
index a710f0365d..1450439faf 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java
@@ -1,232 +1,242 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.util.Hashtable;
 import java.util.concurrent.ConcurrentHashMap;
 import javax.naming.Context;
 import javax.naming.Name;
 import javax.naming.Reference;
 import javax.naming.event.NamespaceChangeListener;
 import javax.naming.event.NamingEvent;
 import javax.naming.event.NamingExceptionEvent;
 import javax.naming.spi.ObjectFactory;
 
 import org.hibernate.SessionFactory;
+import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.engine.jndi.JndiException;
 import org.hibernate.engine.jndi.JndiNameException;
 import org.hibernate.engine.jndi.spi.JndiService;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * A registry of all {@link SessionFactory} instances for the same classloader as this class.
  * <p/>
  * This registry is used for serialization/deserialization as well as JNDI binding.
  *
  * @author Steve Ebersole
  */
 public class SessionFactoryRegistry {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( SessionFactoryRegistry.class );
 
 	/**
 	 * Singleton access
 	 */
 	public static final SessionFactoryRegistry INSTANCE = new SessionFactoryRegistry();
 
 	/**
 	 * A map for mapping the UUID of a SessionFactory to the corresponding SessionFactory instance
 	 */
 	private final ConcurrentHashMap<String, SessionFactory> sessionFactoryMap = new ConcurrentHashMap<String, SessionFactory>();
 
 	/**
 	 * A cross-reference for mapping a SessionFactory name to its UUID.  Not all SessionFactories get named,
 	 */
 	private final ConcurrentHashMap<String, String> nameUuidXref = new ConcurrentHashMap<String, String>();
 
 	private SessionFactoryRegistry() {
 		LOG.debugf( "Initializing SessionFactoryRegistry : %s", this );
 	}
 
 	/**
 	 * Adds a SessionFactory to the registry
 	 *
 	 * @param uuid The uuid under which to register the SessionFactory
 	 * @param name The optional name under which to register the SessionFactory
 	 * @param isNameAlsoJndiName Is name, if provided, also a JNDI name?
 	 * @param instance The SessionFactory instance
 	 * @param jndiService The JNDI service, so we can register a listener if name is a JNDI name
 	 */
 	public void addSessionFactory(
 			String uuid,
 			String name,
 			boolean isNameAlsoJndiName,
 			SessionFactory instance,
 			JndiService jndiService) {
 		if ( uuid == null ) {
 			throw new IllegalArgumentException( "SessionFactory UUID cannot be null" );
 		}
 
 		LOG.debugf( "Registering SessionFactory: %s (%s)", uuid, name == null ? "<unnamed>" : name );
 		sessionFactoryMap.put( uuid, instance );
 		if ( name != null ) {
 			nameUuidXref.put( name, uuid );
 		}
 
 		if ( name == null || !isNameAlsoJndiName ) {
 			LOG.debug( "Not binding SessionFactory to JNDI, no JNDI name configured" );
 			return;
 		}
 
 		LOG.debugf( "Attempting to bind SessionFactory [%s] to JNDI", name );
 
 		try {
 			jndiService.bind( name, instance );
 			LOG.factoryBoundToJndiName( name );
 			try {
 				jndiService.addListener( name, listener );
 			}
 			catch (Exception e) {
 				LOG.couldNotBindJndiListener();
 			}
 		}
 		catch (JndiNameException e) {
 			LOG.invalidJndiName( name, e );
 		}
 		catch (JndiException e) {
 			LOG.unableToBindFactoryToJndi( e );
 		}
 	}
 
 	/**
 	 * Remove a previously added SessionFactory
 	 *
 	 * @param uuid The uuid
 	 * @param name The optional name
 	 * @param isNameAlsoJndiName Is name, if provided, also a JNDI name?
 	 * @param jndiService The JNDI service
 	 */
 	public void removeSessionFactory(
 			String uuid,
 			String name,
 			boolean isNameAlsoJndiName,
 			JndiService jndiService) {
 		if ( name != null ) {
 			nameUuidXref.remove( name );
 
 			if ( isNameAlsoJndiName ) {
 				try {
 					LOG.tracef( "Unbinding SessionFactory from JNDI : %s", name );
 					jndiService.unbind( name );
 					LOG.factoryUnboundFromJndiName( name );
 				}
 				catch (JndiNameException e) {
 					LOG.invalidJndiName( name, e );
 				}
 				catch (JndiException e) {
 					LOG.unableToUnbindFactoryFromJndi( e );
 				}
 			}
 		}
 
 		sessionFactoryMap.remove( uuid );
 	}
 
 	/**
 	 * Get a registered SessionFactory by name
 	 *
 	 * @param name The name
 	 *
 	 * @return The SessionFactory
 	 */
 	public SessionFactory getNamedSessionFactory(String name) {
 		LOG.debugf( "Lookup: name=%s", name );
 		final String uuid = nameUuidXref.get( name );
 		// protect against NPE -- see HHH-8428
 		return uuid == null ? null : getSessionFactory( uuid );
 	}
 
 	public SessionFactory getSessionFactory(String uuid) {
 		LOG.debugf( "Lookup: uid=%s", uuid );
 		final SessionFactory sessionFactory = sessionFactoryMap.get( uuid );
 		if ( sessionFactory == null && LOG.isDebugEnabled() ) {
 			LOG.debugf( "Not found: %s", uuid );
 			LOG.debugf( sessionFactoryMap.toString() );
 		}
 		return sessionFactory;
 	}
 
+	public SessionFactory findSessionFactory(String uuid, String name) {
+		SessionFactory sessionFactory = getSessionFactory( uuid );
+		if ( sessionFactory == null && StringHelper.isNotEmpty( name ) ) {
+			sessionFactory = getNamedSessionFactory( name );
+		}
+		return sessionFactory;
+	}
+
 	/**
 	 * Does this registry currently contain registrations?
 	 *
 	 * @return true/false
 	 */
 	public boolean hasRegistrations() {
 		return !sessionFactoryMap.isEmpty();
 	}
 
 	public void clearRegistrations() {
 		nameUuidXref.clear();
 		for ( SessionFactory factory : sessionFactoryMap.values() ) {
 			try {
 				factory.close();
 			}
 			catch (Exception ignore) {
 			}
 		}
 		sessionFactoryMap.clear();
 	}
 
 	/**
 	 * Implementation of {@literal JNDI} {@link javax.naming.event.NamespaceChangeListener} contract to listener for context events
 	 * and react accordingly if necessary
 	 */
 	private final NamespaceChangeListener listener = new NamespaceChangeListener() {
 		@Override
 		public void objectAdded(NamingEvent evt) {
 			LOG.debugf( "A factory was successfully bound to name: %s", evt.getNewBinding().getName() );
 		}
 
 		@Override
 		public void objectRemoved(NamingEvent evt) {
 			final String jndiName = evt.getOldBinding().getName();
 			LOG.factoryUnboundFromName( jndiName );
 
 			final String uuid = nameUuidXref.remove( jndiName );
 			if ( uuid == null ) {
 				// serious problem... but not sure what to do yet
 			}
 			sessionFactoryMap.remove( uuid );
 		}
 
 		@Override
 		public void objectRenamed(NamingEvent evt) {
 			final String oldJndiName = evt.getOldBinding().getName();
 			final String newJndiName = evt.getNewBinding().getName();
 
 			LOG.factoryJndiRename( oldJndiName, newJndiName );
 
 			final String uuid = nameUuidXref.remove( oldJndiName );
 			nameUuidXref.put( newJndiName, uuid );
 		}
 
 		@Override
 		public void namingExceptionThrown(NamingExceptionEvent evt) {
 			//noinspection ThrowableResultOfMethodCallIgnored
 			LOG.namingExceptionAccessingFactory( evt.getException() );
 		}
 	};
 
 	public static class ObjectFactoryImpl implements ObjectFactory {
 		@Override
 		public Object getObjectInstance(Object reference, Name name, Context nameCtx, Hashtable<?, ?> environment)
 				throws Exception {
 			LOG.debugf( "JNDI lookup: %s", name );
 			final String uuid = (String) ( (Reference) reference ).get( 0 ).getContent();
 			LOG.tracef( "Resolved to UUID = %s", uuid );
 			return INSTANCE.getSessionFactory( uuid );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
index 628e63710f..608e3c5ed3 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
@@ -1,67 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.proxy;
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.CompositeType;
 
 /**
  * Contract for run-time, proxy-based lazy initialization proxies.
  *
  * @author Gavin King
  */
 public interface ProxyFactory {
 
 	/**
 	 * Called immediately after instantiation of this factory.
 	 * <p/>
 	 * Essentially equivalent to constructor injection, but contracted
 	 * here via interface.
 	 *
 	 * @param entityName The name of the entity for which this factory should
 	 * generate proxies.
 	 * @param persistentClass The entity class for which to generate proxies;
 	 * not always the same as the entityName.
 	 * @param interfaces The interfaces to expose in the generated proxy;
 	 * {@link HibernateProxy} is already included in this collection.
 	 * @param getIdentifierMethod Reference to the identifier getter method;
 	 * invocation on this method should not force initialization
 	 * @param setIdentifierMethod Reference to the identifier setter method;
 	 * invocation on this method should not force initialization
 	 * @param componentIdType For composite identifier types, a reference to
 	 * the {@link org.hibernate.type.ComponentType type} of the identifier
 	 * property; again accessing the id should generally not cause
 	 * initialization - but need to bear in mind <key-many-to-one/>
 	 * mappings.
 	 * @throws HibernateException Indicates a problem completing post
 	 * instantiation initialization.
 	 */
 	public void postInstantiate(
 			String entityName,
 			Class persistentClass,
-			Set interfaces,
+			Set<Class> interfaces,
 			Method getIdentifierMethod,
 			Method setIdentifierMethod,
 			CompositeType componentIdType) throws HibernateException;
 
 	/**
 	 * Create a new proxy instance
 	 *
 	 * @param id The id value for the proxy to be generated.
 	 * @param session The session to which the generated proxy will be
 	 * associated.
 	 * @return The generated proxy.
 	 * @throws HibernateException Indicates problems generating the requested
 	 * proxy.
 	 */
 	public HibernateProxy getProxy(Serializable id,SessionImplementor session) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
index 9c22795b80..a9761188a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
@@ -1,228 +1,128 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.proxy.pojo.javassist;
 
 import java.io.Serializable;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
-import javassist.util.proxy.MethodFilter;
 import javassist.util.proxy.MethodHandler;
-import javassist.util.proxy.Proxy;
-import javassist.util.proxy.ProxyFactory;
 
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.pojo.BasicLazyInitializer;
 import org.hibernate.type.CompositeType;
 
+import static org.hibernate.internal.CoreLogging.messageLogger;
+
 /**
  * A Javassist-based lazy initializer proxy.
  *
  * @author Muga Nishizawa
  */
 public class JavassistLazyInitializer extends BasicLazyInitializer implements MethodHandler {
-	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( JavassistLazyInitializer.class );
+	private static final CoreMessageLogger LOG = messageLogger( JavassistLazyInitializer.class );
 
-	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
-		public boolean isHandled(Method m) {
-			// skip finalize methods
-			return !( m.getParameterTypes().length == 0 && m.getName().equals( "finalize" ) );
-		}
-	};
+	private final Class[] interfaces;
 
-	private Class[] interfaces;
 	private boolean constructed;
 
-	private JavassistLazyInitializer(
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Serializable id,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
-			final CompositeType componentIdType,
-			final SessionImplementor session,
-			final boolean overridesEquals) {
+	public JavassistLazyInitializer(
+			String entityName,
+			Class persistentClass,
+			Class[] interfaces,
+			Serializable id,
+			Method getIdentifierMethod,
+			Method setIdentifierMethod,
+			CompositeType componentIdType,
+			SessionImplementor session,
+			boolean overridesEquals) {
 		super( entityName, persistentClass, id, getIdentifierMethod, setIdentifierMethod, componentIdType, session, overridesEquals );
 		this.interfaces = interfaces;
 	}
 
-	public static HibernateProxy getProxy(
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
-			CompositeType componentIdType,
-			final Serializable id,
-			final SessionImplementor session) throws HibernateException {
-		// note: interface is assumed to already contain HibernateProxy.class
-		try {
-			final JavassistLazyInitializer instance = new JavassistLazyInitializer(
-					entityName,
-					persistentClass,
-					interfaces,
-					id,
-					getIdentifierMethod,
-					setIdentifierMethod,
-					componentIdType,
-					session,
-					ReflectHelper.overridesEquals(persistentClass)
-			);
-			ProxyFactory factory = new ProxyFactory();
-			factory.setSuperclass( interfaces.length == 1 ? persistentClass : null );
-			factory.setInterfaces( interfaces );
-			factory.setFilter( FINALIZE_FILTER );
-			Class cl = factory.createClass();
-			final HibernateProxy proxy = ( HibernateProxy ) cl.newInstance();
-			( ( Proxy ) proxy ).setHandler( instance );
-			instance.constructed = true;
-			return proxy;
-		}
-		catch ( Throwable t ) {
-			LOG.error(LOG.javassistEnhancementFailed(entityName), t);
-			throw new HibernateException(LOG.javassistEnhancementFailed(entityName), t);
-		}
-	}
-
-	public static HibernateProxy getProxy(
-			final Class factory,
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
-			final CompositeType componentIdType,
-			final Serializable id,
-			final SessionImplementor session,
-			final boolean classOverridesEquals) throws HibernateException {
-
-		final JavassistLazyInitializer instance = new JavassistLazyInitializer(
-				entityName,
-				persistentClass,
-				interfaces, id,
-				getIdentifierMethod,
-				setIdentifierMethod,
-				componentIdType,
-				session,
-				classOverridesEquals
-		);
-
-		final HibernateProxy proxy;
-		try {
-			proxy = ( HibernateProxy ) factory.newInstance();
-		}
-		catch ( Exception e ) {
-			throw new HibernateException(
-					"Javassist Enhancement failed: "
-					+ persistentClass.getName(), e
-			);
-		}
-		( ( Proxy ) proxy ).setHandler( instance );
-		instance.constructed = true;
-		return proxy;
-	}
-
-	public static Class getProxyFactory(
-			Class persistentClass,
-			Class[] interfaces) throws HibernateException {
-		// note: interfaces is assumed to already contain HibernateProxy.class
-
-		try {
-			ProxyFactory factory = new ProxyFactory();
-			factory.setSuperclass( interfaces.length == 1 ? persistentClass : null );
-			factory.setInterfaces( interfaces );
-			factory.setFilter( FINALIZE_FILTER );
-			return factory.createClass();
-		}
-		catch ( Throwable t ) {
-			LOG.error(LOG.javassistEnhancementFailed(persistentClass.getName()), t);
-			throw new HibernateException(LOG.javassistEnhancementFailed(persistentClass.getName()), t);
-		}
+	protected void constructed() {
+		constructed = true;
 	}
 
 	@Override
 	public Object invoke(
 			final Object proxy,
 			final Method thisMethod,
 			final Method proceed,
 			final Object[] args) throws Throwable {
 		if ( this.constructed ) {
 			Object result;
 			try {
 				result = this.invoke( thisMethod, args, proxy );
 			}
 			catch ( Throwable t ) {
 				throw new Exception( t.getCause() );
 			}
 			if ( result == INVOKE_IMPLEMENTATION ) {
 				Object target = getImplementation();
 				final Object returnValue;
 				try {
 					if ( ReflectHelper.isPublic( persistentClass, thisMethod ) ) {
 						if ( !thisMethod.getDeclaringClass().isInstance( target ) ) {
 							throw new ClassCastException(
 									target.getClass().getName()
 									+ " incompatible with "
 									+ thisMethod.getDeclaringClass().getName()
 							);
 						}
 						returnValue = thisMethod.invoke( target, args );
 					}
 					else {
 						thisMethod.setAccessible( true );
 						returnValue = thisMethod.invoke( target, args );
 					}
 					
 					if ( returnValue == target ) {
 						if ( returnValue.getClass().isInstance(proxy) ) {
 							return proxy;
 						}
 						else {
 							LOG.narrowingProxy( returnValue.getClass() );
 						}
 					}
 					return returnValue;
 				}
 				catch ( InvocationTargetException ite ) {
 					throw ite.getTargetException();
 				}
 			}
 			else {
 				return result;
 			}
 		}
 		else {
 			// while constructor is running
 			if ( thisMethod.getName().equals( "getHibernateLazyInitializer" ) ) {
 				return this;
 			}
 			else {
 				return proceed.invoke( proxy, args );
 			}
 		}
 	}
 
 	@Override
 	protected Object serializableProxy() {
 		return new SerializableProxy(
 				getEntityName(),
 				persistentClass,
 				interfaces,
 				getIdentifier(),
 				( isReadOnlySettingAvailable() ? Boolean.valueOf( isReadOnly() ) : isReadOnlyBeforeAttachedToSession() ),
 				getIdentifierMethod,
 				setIdentifierMethod,
 				componentIdType
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
index 2f7920d7f0..93fe05ba56 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistProxyFactory.java
@@ -1,72 +1,215 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.proxy.pojo.javassist;
+
 import java.io.Serializable;
 import java.lang.reflect.Method;
+import java.util.Locale;
 import java.util.Set;
 
+import javassist.util.proxy.MethodFilter;
+import javassist.util.proxy.Proxy;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.type.CompositeType;
 
+import static org.hibernate.internal.CoreLogging.messageLogger;
+
 /**
  * A {@link ProxyFactory} implementation for producing Javassist-based proxies.
  *
  * @author Muga Nishizawa
  */
 public class JavassistProxyFactory implements ProxyFactory, Serializable {
+	private static final CoreMessageLogger LOG = messageLogger( JavassistProxyFactory.class );
+
+	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
+		public boolean isHandled(Method m) {
+			// skip finalize methods
+			return !( m.getParameterTypes().length == 0 && m.getName().equals( "finalize" ) );
+		}
+	};
 
-	protected static final Class[] NO_CLASSES = new Class[0];
 	private Class persistentClass;
 	private String entityName;
 	private Class[] interfaces;
 	private Method getIdentifierMethod;
 	private Method setIdentifierMethod;
 	private CompositeType componentIdType;
-	private Class factory;
 	private boolean overridesEquals;
 
+	private Class proxyClass;
+
+	public JavassistProxyFactory() {
+	}
+
 	@Override
 	public void postInstantiate(
 			final String entityName,
 			final Class persistentClass,
-			final Set interfaces,
+			final Set<Class> interfaces,
 			final Method getIdentifierMethod,
 			final Method setIdentifierMethod,
 			CompositeType componentIdType) throws HibernateException {
 		this.entityName = entityName;
 		this.persistentClass = persistentClass;
-		this.interfaces = (Class[]) interfaces.toArray(NO_CLASSES);
+		this.interfaces = toArray( interfaces );
 		this.getIdentifierMethod = getIdentifierMethod;
 		this.setIdentifierMethod = setIdentifierMethod;
 		this.componentIdType = componentIdType;
-		this.factory = JavassistLazyInitializer.getProxyFactory( persistentClass, this.interfaces );
-		this.overridesEquals = ReflectHelper.overridesEquals(persistentClass);
+		this.overridesEquals = ReflectHelper.overridesEquals( persistentClass );
+
+		this.proxyClass = buildJavassistProxyFactory().createClass();
+	}
+
+	private Class[] toArray(Set<Class> interfaces) {
+		if ( interfaces == null ) {
+			return ArrayHelper.EMPTY_CLASS_ARRAY;
+		}
+
+		return interfaces.toArray( new Class[interfaces.size()] );
+	}
+
+	private javassist.util.proxy.ProxyFactory buildJavassistProxyFactory() {
+		return buildJavassistProxyFactory(
+				persistentClass,
+				interfaces
+		);
+	}
+
+	public static javassist.util.proxy.ProxyFactory buildJavassistProxyFactory(
+			final Class persistentClass,
+			final Class[] interfaces) {
+		javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory() {
+			@Override
+			protected ClassLoader getClassLoader() {
+				return persistentClass.getClassLoader();
+			}
+		};
+		factory.setSuperclass( interfaces.length == 1 ? persistentClass : null );
+		factory.setInterfaces( interfaces );
+		factory.setFilter( FINALIZE_FILTER );
+		return factory;
 	}
 
 	@Override
 	public HibernateProxy getProxy(
 			Serializable id,
 			SessionImplementor session) throws HibernateException {
-		return JavassistLazyInitializer.getProxy(
-				factory,
+		final JavassistLazyInitializer initializer = new JavassistLazyInitializer(
 				entityName,
 				persistentClass,
 				interfaces,
+				id,
 				getIdentifierMethod,
 				setIdentifierMethod,
 				componentIdType,
-				id,
 				session,
 				overridesEquals
 		);
+
+		try {
+			final HibernateProxy proxy = (HibernateProxy) proxyClass.newInstance();
+			( (Proxy) proxy ).setHandler( initializer );
+			initializer.constructed();
+
+			return proxy;
+		}
+		catch (Throwable t) {
+			LOG.error( LOG.javassistEnhancementFailed( entityName ), t );
+			throw new HibernateException( LOG.javassistEnhancementFailed( entityName ), t );
+		}
+	}
+
+	public static HibernateProxy deserializeProxy(SerializableProxy serializableProxy) {
+		final JavassistLazyInitializer initializer = new JavassistLazyInitializer(
+				serializableProxy.getEntityName(),
+				serializableProxy.getPersistentClass(),
+				serializableProxy.getInterfaces(),
+				serializableProxy.getId(),
+				resolveIdGetterMethod( serializableProxy ),
+				resolveIdSetterMethod( serializableProxy ),
+				serializableProxy.getComponentIdType(),
+				null,
+				ReflectHelper.overridesEquals( serializableProxy.getPersistentClass() )
+		);
+
+		final javassist.util.proxy.ProxyFactory factory = buildJavassistProxyFactory(
+				serializableProxy.getPersistentClass(),
+				serializableProxy.getInterfaces()
+		);
+
+		// note: interface is assumed to already contain HibernateProxy.class
+		try {
+			final Class proxyClass = factory.createClass();
+			final HibernateProxy proxy = ( HibernateProxy ) proxyClass.newInstance();
+			( (Proxy) proxy ).setHandler( initializer );
+			initializer.constructed();
+			return proxy;
+		}
+		catch ( Throwable t ) {
+			final String message = LOG.javassistEnhancementFailed( serializableProxy.getEntityName() );
+			LOG.error( message, t );
+			throw new HibernateException( message, t );
+		}
+	}
+
+	@SuppressWarnings("unchecked")
+	private static Method resolveIdGetterMethod(SerializableProxy serializableProxy) {
+		if ( serializableProxy.getIdentifierGetterMethodName() == null ) {
+			return null;
+		}
+
+		try {
+			return serializableProxy.getIdentifierGetterMethodClass().getDeclaredMethod( serializableProxy.getIdentifierGetterMethodName() );
+		}
+		catch (NoSuchMethodException e) {
+			throw new HibernateException(
+					String.format(
+							Locale.ENGLISH,
+							"Unable to deserialize proxy [%s, %s]; could not locate id getter method [%s] on entity class [%s]",
+							serializableProxy.getEntityName(),
+							serializableProxy.getId(),
+							serializableProxy.getIdentifierGetterMethodName(),
+							serializableProxy.getIdentifierGetterMethodClass()
+					)
+			);
+		}
 	}
 
+	@SuppressWarnings("unchecked")
+	private static Method resolveIdSetterMethod(SerializableProxy serializableProxy) {
+		if ( serializableProxy.getIdentifierSetterMethodName() == null ) {
+			return null;
+		}
+
+		try {
+			return serializableProxy.getIdentifierSetterMethodClass().getDeclaredMethod(
+					serializableProxy.getIdentifierSetterMethodName(),
+					serializableProxy.getIdentifierSetterMethodParams()
+			);
+		}
+		catch (NoSuchMethodException e) {
+			throw new HibernateException(
+					String.format(
+							Locale.ENGLISH,
+							"Unable to deserialize proxy [%s, %s]; could not locate id setter method [%s] on entity class [%s]",
+							serializableProxy.getEntityName(),
+							serializableProxy.getId(),
+							serializableProxy.getIdentifierSetterMethodName(),
+							serializableProxy.getIdentifierSetterMethodClass()
+					)
+			);
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java
index a434260f46..9e9785336e 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/SerializableProxy.java
@@ -1,80 +1,120 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.proxy.pojo.javassist;
+
 import java.io.Serializable;
 import java.lang.reflect.Method;
 
-import org.hibernate.HibernateException;
 import org.hibernate.proxy.AbstractSerializableProxy;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.CompositeType;
 
 /**
  * Serializable placeholder for Javassist proxies
  */
 public final class SerializableProxy extends AbstractSerializableProxy {
+	private final Class persistentClass;
+	private final Class[] interfaces;
 
-	private Class persistentClass;
-	private Class[] interfaces;
-	private Class getIdentifierMethodClass;
-	private Class setIdentifierMethodClass;
-	private String getIdentifierMethodName;
-	private String setIdentifierMethodName;
-	private Class[] setIdentifierMethodParams;
-	private CompositeType componentIdType;
+	private final String identifierGetterMethodName;
+	private final Class identifierGetterMethodClass;
 
-	public SerializableProxy() {
-	}
+	private final String identifierSetterMethodName;
+	private final Class identifierSetterMethodClass;
+	private final Class[] identifierSetterMethodParams;
+
+	private final CompositeType componentIdType;
 
 	public SerializableProxy(
-			final String entityName,
-			final Class persistentClass,
-			final Class[] interfaces,
-			final Serializable id,
-			final Boolean readOnly,
-			final Method getIdentifierMethod,
-			final Method setIdentifierMethod,
+			String entityName,
+			Class persistentClass,
+			Class[] interfaces,
+			Serializable id,
+			Boolean readOnly,
+			Method getIdentifierMethod,
+			Method setIdentifierMethod,
 			CompositeType componentIdType) {
 		super( entityName, id, readOnly );
 		this.persistentClass = persistentClass;
 		this.interfaces = interfaces;
-		if (getIdentifierMethod!=null) {
-			getIdentifierMethodClass = getIdentifierMethod.getDeclaringClass();
-			getIdentifierMethodName = getIdentifierMethod.getName();
+		if ( getIdentifierMethod != null ) {
+			identifierGetterMethodName = getIdentifierMethod.getName();
+			identifierGetterMethodClass = getIdentifierMethod.getDeclaringClass();
+		}
+		else {
+			identifierGetterMethodName = null;
+			identifierGetterMethodClass = null;
 		}
-		if (setIdentifierMethod!=null) {
-			setIdentifierMethodClass = setIdentifierMethod.getDeclaringClass();
-			setIdentifierMethodName = setIdentifierMethod.getName();
-			setIdentifierMethodParams = setIdentifierMethod.getParameterTypes();
+
+		if ( setIdentifierMethod != null ) {
+			identifierSetterMethodName = setIdentifierMethod.getName();
+			identifierSetterMethodClass = setIdentifierMethod.getDeclaringClass();
+			identifierSetterMethodParams = setIdentifierMethod.getParameterTypes();
+		}
+		else {
+			identifierSetterMethodName = null;
+			identifierSetterMethodClass = null;
+			identifierSetterMethodParams = null;
 		}
+
 		this.componentIdType = componentIdType;
 	}
 
+	@Override
+	protected String getEntityName() {
+		return super.getEntityName();
+	}
+
+	@Override
+	protected Serializable getId() {
+		return super.getId();
+	}
+
+	protected Class getPersistentClass() {
+		return persistentClass;
+	}
+
+	protected Class[] getInterfaces() {
+		return interfaces;
+	}
+
+	protected String getIdentifierGetterMethodName() {
+		return identifierGetterMethodName;
+	}
+
+	protected Class getIdentifierGetterMethodClass() {
+		return identifierGetterMethodClass;
+	}
+
+	protected String getIdentifierSetterMethodName() {
+		return identifierSetterMethodName;
+	}
+
+	protected Class getIdentifierSetterMethodClass() {
+		return identifierSetterMethodClass;
+	}
+
+	protected Class[] getIdentifierSetterMethodParams() {
+		return identifierSetterMethodParams;
+	}
+
+	protected CompositeType getComponentIdType() {
+		return componentIdType;
+	}
+
+	/**
+	 * Deserialization hook.  This method is called by JDK deserialization.  We use this hook
+	 * to replace the serial form with a live form.
+	 *
+	 * @return The live form.
+	 */
 	private Object readResolve() {
-		try {
-			HibernateProxy proxy = JavassistLazyInitializer.getProxy(
-				getEntityName(),
-				persistentClass,
-				interfaces,
-				getIdentifierMethodName==null
-						? null
-						: getIdentifierMethodClass.getDeclaredMethod( getIdentifierMethodName, (Class[]) null ),
-				setIdentifierMethodName==null
-						? null 
-						: setIdentifierMethodClass.getDeclaredMethod(setIdentifierMethodName, setIdentifierMethodParams),
-				componentIdType,
-				getId(),
-				null
-			);
-			setReadOnlyBeforeAttachedToSession( ( JavassistLazyInitializer ) proxy.getHibernateLazyInitializer() );
-			return proxy;
-		}
-		catch (NoSuchMethodException nsme) {
-			throw new HibernateException("could not create proxy for entity: " + getEntityName(), nsme);
-		}
+		HibernateProxy proxy = JavassistProxyFactory.deserializeProxy( this );
+		setReadOnlyBeforeAttachedToSession( ( JavassistLazyInitializer ) proxy.getHibernateLazyInitializer() );
+		return proxy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index 03a9988753..3e10d85d85 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,332 +1,332 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.Setter;
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
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( PojoEntityTuplizer.class );
 
 	private final Class mappedClass;
 	private final Class proxyInterface;
 	private final boolean lifecycleImplementor;
 	private final Set<String> lazyPropertyNames = new HashSet<String>();
 	private final ReflectionOptimizer optimizer;
 	private final boolean isInstrumented;
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getMappedClass();
 		this.proxyInterface = mappedEntity.getProxyInterface();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 		this.isInstrumented = entityMetamodel.isInstrumented();
 
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
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
 					mappedClass,
 					getterNames,
 					setterNames,
 					propTypes
 			);
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 	}
 
 	@Override
 	protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
 		// determine all interfaces needed by the resulting proxy
 		
 		/*
 		 * We need to preserve the order of the interfaces they were put into the set, since javassist will choose the
 		 * first one's class-loader to construct the proxy class with. This is also the reason why HibernateProxy.class
 		 * should be the last one in the order (on JBossAS7 its class-loader will be org.hibernate module's class-
 		 * loader, which will not see the classes inside deployed apps.  See HHH-3078
 		 */
 		Set<Class> proxyInterfaces = new java.util.LinkedHashSet<Class>();
 
 		Class mappedClass = persistentClass.getMappedClass();
 		Class proxyInterface = persistentClass.getProxyInterface();
 
 		if ( proxyInterface != null && !mappedClass.equals( proxyInterface ) ) {
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
 
 		Iterator<Subclass> subclasses = persistentClass.getSubclassIterator();
 		while ( subclasses.hasNext() ) {
 			final Subclass subclass = subclasses.next();
 			final Class subclassProxy = subclass.getProxyInterface();
 			final Class subclassClass = subclass.getMappedClass();
 			if ( subclassProxy != null && !subclassClass.equals( subclassProxy ) ) {
 				if ( !subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Iterator properties = persistentClass.getPropertyIterator();
 		Class clazz = persistentClass.getMappedClass();
 		while ( properties.hasNext() ) {
 			Property property = (Property) properties.next();
 			Method method = property.getGetter( clazz ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.gettersOfLazyClassesCannotBeFinal( persistentClass.getEntityName(), property.getName() );
 			}
 			method = property.getSetter( clazz ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.settersOfLazyClassesCannotBeFinal( persistentClass.getEntityName(), property.getName() );
 			}
 		}
 
 		Method idGetterMethod = idGetter == null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter == null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod == null || proxyInterface == null ?
 				null :
 				ReflectHelper.getMethod( proxyInterface, idGetterMethod );
 		Method proxySetIdentifierMethod = idSetterMethod == null || proxyInterface == null ?
 				null :
 				ReflectHelper.getMethod( proxyInterface, idSetterMethod );
 
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
 		catch (HibernateException he) {
 			LOG.unableToCreateProxyFactory( getEntityName(), he );
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(
 			PersistentClass persistentClass,
 			Getter idGetter,
 			Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
-		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
+		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory( getFactory() );
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
 	@Override
 	protected Instantiator buildInstantiator(PersistentClass persistentClass) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( persistentClass, null );
 		}
 		else {
 			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			setPropertyValuesWithOptimizer( entity, values );
 		}
 		else {
 			super.setPropertyValues( entity, values );
 		}
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValues( entity );
 		}
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 			throws HibernateException {
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
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return mappedClass;
 	}
 
 	@Override
 	public boolean isLifecycleImplementor() {
 		return lifecycleImplementor;
 	}
 
 	@Override
 	protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
 	}
 
 	@Override
 	protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return proxyInterface;
 	}
 
 	//TODO: need to make the majority of this functionality into a top-level support class for custom impl support
 
 	@Override
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		if ( isInstrumented() ) {
 			Set<String> lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
 					lazyPropertyNames : null;
 			//TODO: if we support multiple fetch groups, we would need
 			//      to clone the set of lazy properties!
 			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
 
 			//also clear the fields that are marked as dirty in the dirtyness tracker
 			if ( entity instanceof org.hibernate.engine.spi.SelfDirtinessTracker ) {
 				( (org.hibernate.engine.spi.SelfDirtinessTracker) entity ).$$_hibernate_clearDirtyAttributes();
 			}
 		}
 	}
 
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
 
 	@Override
 	public boolean isInstrumented() {
 		return isInstrumented;
 	}
 
 	@Override
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
 
 	@Override
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return null;
 	}
 }
