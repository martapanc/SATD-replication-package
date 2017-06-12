diff --git a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
index a4020d905f..decc751ba4 100644
--- a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
@@ -1,377 +1,380 @@
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
 package org.hibernate;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Set;
+
 import javax.naming.Referenceable;
 
+import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.stat.Statistics;
 
 /**
  * The main contract here is the creation of {@link Session} instances.  Usually
  * an application has a single {@link SessionFactory} instance and threads
  * servicing client requests obtain {@link Session} instances from this factory.
  * <p/>
  * The internal state of a {@link SessionFactory} is immutable.  Once it is created
  * this internal state is set.  This internal state includes all of the metadata
  * about Object/Relational Mapping.
  * <p/>
  * Implementors <strong>must</strong> be threadsafe.
  *
  * @see org.hibernate.cfg.Configuration
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface SessionFactory extends Referenceable, Serializable {
 
 	public interface SessionFactoryOptions {
-		Interceptor getInterceptor();
-		EntityNotFoundDelegate getEntityNotFoundDelegate();
+		public StandardServiceRegistry getServiceRegistry();
+		public Interceptor getInterceptor();
+		public EntityNotFoundDelegate getEntityNotFoundDelegate();
 	}
 
 	public SessionFactoryOptions getSessionFactoryOptions();
 
 	/**
 	 * Obtain a {@link Session} builder.
 	 *
 	 * @return The session builder
 	 */
 	public SessionBuilder withOptions();
 
 	/**
 	 * Open a {@link Session}.
 	 * <p/>
 	 * JDBC {@link Connection connection(s} will be obtained from the
 	 * configured {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} as needed
 	 * to perform requested work.
 	 *
 	 * @return The created session.
 	 *
 	 * @throws HibernateException Indicates a problem opening the session; pretty rare here.
 	 */
 	public Session openSession() throws HibernateException;
 
 	/**
 	 * Obtains the current session.  The definition of what exactly "current"
 	 * means controlled by the {@link org.hibernate.context.spi.CurrentSessionContext} impl configured
 	 * for use.
 	 * <p/>
 	 * Note that for backwards compatibility, if a {@link org.hibernate.context.spi.CurrentSessionContext}
 	 * is not configured but JTA is configured this will default to the {@link org.hibernate.context.internal.JTASessionContext}
 	 * impl.
 	 *
 	 * @return The current session.
 	 *
 	 * @throws HibernateException Indicates an issue locating a suitable current session.
 	 */
 	public Session getCurrentSession() throws HibernateException;
 
 	/**
 	 * Obtain a {@link StatelessSession} builder.
 	 *
 	 * @return The stateless session builder
 	 */
 	public StatelessSessionBuilder withStatelessOptions();
 
 	/**
 	 * Open a new stateless session.
 	 *
 	 * @return The created stateless session.
 	 */
 	public StatelessSession openStatelessSession();
 
 	/**
 	 * Open a new stateless session, utilizing the specified JDBC
 	 * {@link Connection}.
 	 *
 	 * @param connection Connection provided by the application.
 	 *
 	 * @return The created stateless session.
 	 */
 	public StatelessSession openStatelessSession(Connection connection);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
 	 *
 	 * @param entityClass The entity class
 	 *
 	 * @return The metadata associated with the given entity; may be null if no such
 	 * entity was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 */
 	public ClassMetadata getClassMetadata(Class entityClass);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} associated with the given entity class.
 	 *
 	 * @param entityName The entity class
 	 *
 	 * @return The metadata associated with the given entity; may be null if no such
 	 * entity was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 * @since 3.0
 	 */
 	public ClassMetadata getClassMetadata(String entityName);
 
 	/**
 	 * Get the {@link CollectionMetadata} associated with the named collection role.
 	 *
 	 * @param roleName The collection role (in form [owning-entity-name].[collection-property-name]).
 	 *
 	 * @return The metadata associated with the given collection; may be null if no such
 	 * collection was mapped.
 	 *
 	 * @throws HibernateException Generally null is returned instead of throwing.
 	 */
 	public CollectionMetadata getCollectionMetadata(String roleName);
 
 	/**
 	 * Retrieve the {@link ClassMetadata} for all mapped entities.
 	 *
 	 * @return A map containing all {@link ClassMetadata} keyed by the
 	 * corresponding {@link String} entity-name.
 	 *
 	 * @throws HibernateException Generally empty map is returned instead of throwing.
 	 *
 	 * @since 3.0 changed key from {@link Class} to {@link String}.
 	 */
 	public Map<String,ClassMetadata> getAllClassMetadata();
 
 	/**
 	 * Get the {@link CollectionMetadata} for all mapped collections
 	 *
 	 * @return a map from <tt>String</tt> to <tt>CollectionMetadata</tt>
 	 *
 	 * @throws HibernateException Generally empty map is returned instead of throwing.
 	 */
 	public Map getAllCollectionMetadata();
 
 	/**
 	 * Retrieve the statistics fopr this factory.
 	 *
 	 * @return The statistics.
 	 */
 	public Statistics getStatistics();
 
 	/**
 	 * Destroy this <tt>SessionFactory</tt> and release all resources (caches,
 	 * connection pools, etc).
 	 * <p/>
 	 * It is the responsibility of the application to ensure that there are no
 	 * open {@link Session sessions} before calling this method as the impact
 	 * on those {@link Session sessions} is indeterminate.
 	 * <p/>
 	 * No-ops if already {@link #isClosed closed}.
 	 *
 	 * @throws HibernateException Indicates an issue closing the factory.
 	 */
 	public void close() throws HibernateException;
 
 	/**
 	 * Is this factory already closed?
 	 *
 	 * @return True if this factory is already closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Obtain direct access to the underlying cache regions.
 	 *
 	 * @return The direct cache access API.
 	 */
 	public Cache getCache();
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param persistentClass The entity class for which to evict data.
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntityRegion(Class)} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evict(Class persistentClass) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level  cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param persistentClass The entity class for which to evict data.
 	 * @param id The entity id
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#containsEntity(Class, Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evict(Class persistentClass, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param entityName The entity name for which to evict data.
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntityRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evictEntity(String entityName) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level  cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param entityName The entity name for which to evict data.
 	 * @param id The entity id
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'persisttentClass' did not name a mapped entity or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictEntity(String,Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evictEntity(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict all entries from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param roleName The name of the collection role whose regions should be evicted
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'roleName' did not name a mapped collection or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictCollectionRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evictCollection(String roleName) throws HibernateException;
 
 	/**
 	 * Evict an entry from the second-level cache. This method occurs outside
 	 * of any transaction; it performs an immediate "hard" remove, so does not respect
 	 * any transaction isolation semantics of the usage strategy. Use with care.
 	 *
 	 * @param roleName The name of the collection role
 	 * @param id The id of the collection owner
 	 *
 	 * @throws HibernateException Generally will mean that either that
 	 * 'roleName' did not name a mapped collection or a problem
 	 * communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictCollection(String,Serializable)} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evictCollection(String roleName, Serializable id) throws HibernateException;
 
 	/**
 	 * Evict any query result sets cached in the named query cache region.
 	 *
 	 * @param cacheRegion The named query cache region from which to evict.
 	 *
 	 * @throws HibernateException Since a not-found 'cacheRegion' simply no-ops,
 	 * this should indicate a problem communicating with underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictQueryRegion(String)} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evictQueries(String cacheRegion) throws HibernateException;
 
 	/**
 	 * Evict any query result sets cached in the default query cache region.
 	 *
 	 * @throws HibernateException Indicate a problem communicating with
 	 * underlying cache impl.
 	 *
 	 * @deprecated Use {@link Cache#evictQueryRegions} accessed through
 	 * {@link #getCache()} instead.
 	 */
     @Deprecated
 	public void evictQueries() throws HibernateException;
 
 	/**
 	 * Obtain a set of the names of all filters defined on this SessionFactory.
 	 *
 	 * @return The set of filter names.
 	 */
 	public Set getDefinedFilterNames();
 
 	/**
 	 * Obtain the definition of a filter by name.
 	 *
 	 * @param filterName The name of the filter for which to obtain the definition.
 	 * @return The filter definition.
 	 * @throws HibernateException If no filter defined with the given name.
 	 */
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException;
 
 	/**
 	 * Determine if this session factory contains a fetch profile definition
 	 * registered under the given name.
 	 *
 	 * @param name The name to check
 	 * @return True if there is such a fetch profile; false otherwise.
 	 */
 	public boolean containsFetchProfileDefinition(String name);
 
 	/**
 	 * Retrieve this factory's {@link TypeHelper}
 	 *
 	 * @return The factory's {@link TypeHelper}
 	 */
 	public TypeHelper getTypeHelper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java
index c8a97c0660..eb7685f634 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/BootstrapServiceRegistryBuilder.java
@@ -1,171 +1,188 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.registry;
 
+import java.util.ArrayList;
 import java.util.LinkedHashSet;
+import java.util.List;
 
 import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.boot.registry.selector.Availability;
 import org.hibernate.boot.registry.selector.AvailabilityAnnouncer;
 import org.hibernate.integrator.internal.IntegratorServiceImpl;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.selector.internal.StrategySelectorBuilder;
 
 /**
  * Builder for bootstrap {@link org.hibernate.service.ServiceRegistry} instances.
  *
  * @author Steve Ebersole
  *
  * @see BootstrapServiceRegistryImpl
  * @see StandardServiceRegistryBuilder#StandardServiceRegistryBuilder(org.hibernate.boot.registry.BootstrapServiceRegistry)
  */
 public class BootstrapServiceRegistryBuilder {
 	private final LinkedHashSet<Integrator> providedIntegrators = new LinkedHashSet<Integrator>();
-	private ClassLoader applicationClassLoader;
-	private ClassLoader resourcesClassLoader;
-	private ClassLoader hibernateClassLoader;
-	private ClassLoader environmentClassLoader;
+	private List<ClassLoader> providedClassLoaders;
 
 	private StrategySelectorBuilder strategySelectorBuilder = new StrategySelectorBuilder();
 
 	/**
 	 * Add an {@link Integrator} to be applied to the bootstrap registry.
 	 *
 	 * @param integrator The integrator to add.
 	 * @return {@code this}, for method chaining
 	 */
 	public BootstrapServiceRegistryBuilder with(Integrator integrator) {
 		providedIntegrators.add( integrator );
 		return this;
 	}
 
 	/**
+	 * Adds a provided {@link ClassLoader} for use in class-loading and resource-lookup
+	 *
+	 * @param classLoader The class loader to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public BootstrapServiceRegistryBuilder with(ClassLoader classLoader) {
+		if ( providedClassLoaders == null ) {
+			providedClassLoaders = new ArrayList<ClassLoader>();
+		}
+		providedClassLoaders.add( classLoader );
+		return this;
+	}
+
+	/**
 	 * Applies the specified {@link ClassLoader} as the application class loader for the bootstrap registry
 	 *
 	 * @param classLoader The class loader to use
 	 * @return {@code this}, for method chaining
+	 *
+	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
+	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withApplicationClassLoader(ClassLoader classLoader) {
-		this.applicationClassLoader = classLoader;
-		return this;
+		return with( classLoader );
 	}
 
 	/**
 	 * Applies the specified {@link ClassLoader} as the resource class loader for the bootstrap registry
 	 *
 	 * @param classLoader The class loader to use
 	 * @return {@code this}, for method chaining
+	 *
+	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
+	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withResourceClassLoader(ClassLoader classLoader) {
-		this.resourcesClassLoader = classLoader;
-		return this;
+		return with( classLoader );
 	}
 
 	/**
 	 * Applies the specified {@link ClassLoader} as the Hibernate class loader for the bootstrap registry
 	 *
 	 * @param classLoader The class loader to use
 	 * @return {@code this}, for method chaining
+	 *
+	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
+	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withHibernateClassLoader(ClassLoader classLoader) {
-		this.hibernateClassLoader = classLoader;
-		return this;
+		return with( classLoader );
 	}
 
 	/**
 	 * Applies the specified {@link ClassLoader} as the environment (or system) class loader for the bootstrap registry
 	 *
 	 * @param classLoader The class loader to use
 	 * @return {@code this}, for method chaining
+	 *
+	 * @deprecated Use {@link #with(ClassLoader)} instead
 	 */
+	@Deprecated
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public BootstrapServiceRegistryBuilder withEnvironmentClassLoader(ClassLoader classLoader) {
-		this.environmentClassLoader = classLoader;
-		return this;
+		return with( classLoader );
 	}
 
 	/**
 	 * Applies a named strategy implementation to the bootstrap registry
 	 *
 	 * @param strategy The strategy
 	 * @param name The registered name
 	 * @param implementation The strategy implementation Class
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.boot.registry.selector.spi.StrategySelector#registerStrategyImplementor(Class, String, Class)
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public <T> BootstrapServiceRegistryBuilder withStrategySelector(Class<T> strategy, String name, Class<? extends T> implementation) {
 		this.strategySelectorBuilder.addExplicitAvailability( strategy, implementation, name );
 		return this;
 	}
 
 	/**
 	 * Applies one or more strategy selectors announced as available by the passed announcer.
 	 *
 	 * @param availabilityAnnouncer An announcer for one or more available selectors
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.boot.registry.selector.spi.StrategySelector#registerStrategyImplementor(Class, String, Class)
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public <T> BootstrapServiceRegistryBuilder withStrategySelectors(AvailabilityAnnouncer availabilityAnnouncer) {
 		for ( Availability availability : availabilityAnnouncer.getAvailabilities() ) {
 			this.strategySelectorBuilder.addExplicitAvailability( availability );
 		}
 		return this;
 	}
 
 	/**
 	 * Build the bootstrap registry.
 	 *
 	 * @return The built bootstrap registry
 	 */
 	public BootstrapServiceRegistry build() {
-		final ClassLoaderServiceImpl classLoaderService = new ClassLoaderServiceImpl(
-				applicationClassLoader,
-				resourcesClassLoader,
-				hibernateClassLoader,
-				environmentClassLoader
-		);
+		final ClassLoaderServiceImpl classLoaderService = new ClassLoaderServiceImpl( providedClassLoaders );
 
 		final IntegratorServiceImpl integratorService = new IntegratorServiceImpl(
 				providedIntegrators,
 				classLoaderService
 		);
 
 
 		return new BootstrapServiceRegistryImpl(
 				classLoaderService,
 				strategySelectorBuilder.buildSelector( classLoaderService ),
 				integratorService
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/StandardServiceRegistry.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/StandardServiceRegistry.java
new file mode 100644
index 0000000000..67f1ba97ab
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/StandardServiceRegistry.java
@@ -0,0 +1,34 @@
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
+package org.hibernate.boot.registry;
+
+import org.hibernate.service.ServiceRegistry;
+
+/**
+ * Specialization of the {@link org.hibernate.service.ServiceRegistry} contract mainly for type safety.
+ *
+ * @author Steve Ebersole
+ */
+public interface StandardServiceRegistry extends ServiceRegistry {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/StandardServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/StandardServiceRegistryBuilder.java
index 926c46613d..869455622f 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/StandardServiceRegistryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/StandardServiceRegistryBuilder.java
@@ -1,237 +1,247 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.registry;
 
 import java.util.ArrayList;
 import java.util.HashMap;
+import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.Environment;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.integrator.spi.ServiceContributingIntegrator;
 import org.hibernate.internal.jaxb.cfg.JaxbHibernateConfiguration;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.service.ConfigLoader;
 import org.hibernate.service.Service;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.internal.ProvidedService;
-import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
+import org.hibernate.service.spi.ServiceContributor;
 
 /**
  * Builder for standard {@link org.hibernate.service.ServiceRegistry} instances.
  *
  * @author Steve Ebersole
  * 
  * @see StandardServiceRegistryImpl
  * @see org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
  */
 public class StandardServiceRegistryBuilder {
 	public static final String DEFAULT_CFG_RESOURCE_NAME = "hibernate.cfg.xml";
 
 	private final Map settings;
 	private final List<StandardServiceInitiator> initiators = standardInitiatorList();
 	private final List<ProvidedService> providedServices = new ArrayList<ProvidedService>();
 
 	private final BootstrapServiceRegistry bootstrapServiceRegistry;
 	private final ConfigLoader configLoader;
 
 	/**
 	 * Create a default builder
 	 */
 	public StandardServiceRegistryBuilder() {
-		this( new BootstrapServiceRegistryImpl() );
+		this( new BootstrapServiceRegistryBuilder().build() );
 	}
 
 	/**
 	 * Create a builder with the specified bootstrap services.
 	 *
 	 * @param bootstrapServiceRegistry Provided bootstrap registry to use.
 	 */
 	public StandardServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		this.settings = Environment.getProperties();
 		this.bootstrapServiceRegistry = bootstrapServiceRegistry;
 		this.configLoader = new ConfigLoader( bootstrapServiceRegistry );
 	}
 
 	/**
 	 * Used from the {@link #initiators} variable initializer
 	 *
 	 * @return List of standard initiators
 	 */
 	private static List<StandardServiceInitiator> standardInitiatorList() {
 		final List<StandardServiceInitiator> initiators = new ArrayList<StandardServiceInitiator>();
 		initiators.addAll( StandardServiceInitiators.LIST );
 		return initiators;
 	}
 
 	public BootstrapServiceRegistry getBootstrapServiceRegistry() {
 		return bootstrapServiceRegistry;
 	}
 
 	/**
 	 * Read settings from a {@link Properties} file.  Differs from {@link #configure()} and {@link #configure(String)}
 	 * in that here we read a {@link Properties} file while for {@link #configure} we read the XML variant.
 	 *
 	 * @param resourceName The name by which to perform a resource look up for the properties file.
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @see #configure()
 	 * @see #configure(String)
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public StandardServiceRegistryBuilder loadProperties(String resourceName) {
 		settings.putAll( configLoader.loadProperties( resourceName ) );
 		return this;
 	}
 
 	/**
 	 * Read setting information from an XML file using the standard resource location
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @see #DEFAULT_CFG_RESOURCE_NAME
 	 * @see #configure(String)
 	 * @see #loadProperties(String)
 	 */
 	public StandardServiceRegistryBuilder configure() {
 		return configure( DEFAULT_CFG_RESOURCE_NAME );
 	}
 
 	/**
 	 * Read setting information from an XML file using the named resource location
 	 *
 	 * @param resourceName The named resource
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @see #loadProperties(String)
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public StandardServiceRegistryBuilder configure(String resourceName) {
 		JaxbHibernateConfiguration configurationElement = configLoader.loadConfigXmlResource( resourceName );
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbProperty xmlProperty : configurationElement.getSessionFactory().getProperty() ) {
 			settings.put( xmlProperty.getName(), xmlProperty.getValue() );
 		}
 
 		return this;
 	}
 
 	/**
 	 * Apply a setting value
 	 *
 	 * @param settingName The name of the setting
 	 * @param value The value to use.
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"unchecked", "UnusedDeclaration"})
 	public StandardServiceRegistryBuilder applySetting(String settingName, Object value) {
 		settings.put( settingName, value );
 		return this;
 	}
 
 	/**
 	 * Apply a groups of setting values
 	 *
 	 * @param settings The incoming settings to apply
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"unchecked", "UnusedDeclaration"})
 	public StandardServiceRegistryBuilder applySettings(Map settings) {
 		this.settings.putAll( settings );
 		return this;
 	}
 
 	/**
 	 * Adds a service initiator.
 	 *
 	 * @param initiator The initiator to be added
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public StandardServiceRegistryBuilder addInitiator(StandardServiceInitiator initiator) {
 		initiators.add( initiator );
 		return this;
 	}
 
 	/**
 	 * Adds a user-provided service
 	 *
 	 * @param serviceRole The role of the service being added
 	 * @param service The service implementation
 	 *
 	 * @return this, for method chaining
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public StandardServiceRegistryBuilder addService(final Class serviceRole, final Service service) {
 		providedServices.add( new ProvidedService( serviceRole, service ) );
 		return this;
 	}
 
-	/**
-	 * Build the service registry accounting for all settings and service initiators and services.
-	 *
-	 * @return The built service registry
-	 */
-	public ServiceRegistry buildServiceRegistry() {
+	public StandardServiceRegistry build() {
 		Map<?,?> settingsCopy = new HashMap();
 		settingsCopy.putAll( settings );
 		Environment.verifyProperties( settingsCopy );
 		ConfigurationHelper.resolvePlaceHolders( settingsCopy );
 
+		applyServiceContributingIntegrators();
+		applyServiceContributors();
+
+		return new StandardServiceRegistryImpl( bootstrapServiceRegistry, initiators, providedServices, settingsCopy );
+	}
+
+	@SuppressWarnings("deprecation")
+	private void applyServiceContributingIntegrators() {
 		for ( Integrator integrator : bootstrapServiceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
-			if ( ServiceContributingIntegrator.class.isInstance( integrator ) ) {
-				ServiceContributingIntegrator.class.cast( integrator ).prepareServices( this );
+			if ( org.hibernate.integrator.spi.ServiceContributingIntegrator.class.isInstance( integrator ) ) {
+				org.hibernate.integrator.spi.ServiceContributingIntegrator.class.cast( integrator ).prepareServices( this );
 			}
 		}
+	}
 
-		return new StandardServiceRegistryImpl( bootstrapServiceRegistry, initiators, providedServices, settingsCopy );
+	private void applyServiceContributors() {
+		LinkedHashSet<ServiceContributor> serviceContributors =
+				bootstrapServiceRegistry.getService( ClassLoaderService.class ).loadJavaServices( ServiceContributor.class );
+		for ( ServiceContributor serviceContributor : serviceContributors ) {
+			serviceContributor.contribute( this );
+		}
 	}
 
 	/**
 	 * Temporarily exposed since Configuration is still around and much code still uses Configuration.  This allows
 	 * code to configure the builder and access that to configure Configuration object (used from HEM atm).
 	 */
 	@Deprecated
 	public Map getSettings() {
 		return settings;
 	}
 
 	/**
 	 * Destroy a service registry.  Applications should only destroy registries they have explicitly created.
 	 *
 	 * @param serviceRegistry The registry to be closed.
 	 */
 	public static void destroy(ServiceRegistry serviceRegistry) {
 		( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
index bf2b37a391..dc2b5c301b 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
@@ -1,335 +1,329 @@
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
 package org.hibernate.boot.registry.classloading.internal;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
+import java.util.Collection;
+import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.ServiceLoader;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 
 /**
  * Standard implementation of the service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceImpl implements ClassLoaderService {
 	private static final Logger log = Logger.getLogger( ClassLoaderServiceImpl.class );
 
-	private final ClassLoader classClassLoader;
-	private final ClassLoader resourcesClassLoader;
-	private final ClassLoader serviceLoaderClassLoader;
+	private final ClassLoader aggregatedClassLoader;
 
 	public ClassLoaderServiceImpl() {
 		this( ClassLoaderServiceImpl.class.getClassLoader() );
 	}
 
 	public ClassLoaderServiceImpl(ClassLoader classLoader) {
-		this( classLoader, classLoader, classLoader, classLoader );
+		this( Collections.singletonList( classLoader ) );
 	}
 
-	public ClassLoaderServiceImpl(
-			ClassLoader applicationClassLoader,
-			ClassLoader resourcesClassLoader,
-			ClassLoader hibernateClassLoader,
-			ClassLoader environmentClassLoader) {
-		// Normalize missing loaders
-		if ( hibernateClassLoader == null ) {
-			hibernateClassLoader = ClassLoaderServiceImpl.class.getClassLoader();
-		}
+	public ClassLoaderServiceImpl(List<ClassLoader> providedClassLoaders) {
+		final LinkedHashSet<ClassLoader> orderedClassLoaderSet = new LinkedHashSet<ClassLoader>();
 
-		if ( environmentClassLoader == null || applicationClassLoader == null ) {
-			ClassLoader sysClassLoader = locateSystemClassLoader();
-			ClassLoader tccl = locateTCCL();
-			if ( environmentClassLoader == null ) {
-				environmentClassLoader = sysClassLoader != null ? sysClassLoader : hibernateClassLoader;
-			}
-			if ( applicationClassLoader == null ) {
-				applicationClassLoader = tccl != null ? tccl : hibernateClassLoader;
+		// first add all provided class loaders, if any
+		if ( providedClassLoaders != null ) {
+			for ( ClassLoader classLoader : providedClassLoaders ) {
+				if ( classLoader != null ) {
+					orderedClassLoaderSet.add( classLoader );
+				}
 			}
 		}
 
-		if ( resourcesClassLoader == null ) {
-			resourcesClassLoader = applicationClassLoader;
+		// normalize adding known class-loaders...
+		// first the Hibernate class loader
+		orderedClassLoaderSet.add( ClassLoaderServiceImpl.class.getClassLoader() );
+		// then the TCCL, if one...
+		final ClassLoader tccl = locateTCCL();
+		if ( tccl != null ) {
+			orderedClassLoaderSet.add( tccl );
+		}
+		// finally the system classloader
+		final ClassLoader sysClassLoader = locateSystemClassLoader();
+		if ( sysClassLoader != null ) {
+			orderedClassLoaderSet.add( sysClassLoader );
 		}
 
-		final LinkedHashSet<ClassLoader> classLoadingClassLoaders = new LinkedHashSet<ClassLoader>();
-		classLoadingClassLoaders.add( applicationClassLoader );
-		classLoadingClassLoaders.add( hibernateClassLoader );
-		classLoadingClassLoaders.add( environmentClassLoader );
-
-		this.classClassLoader = new ClassLoader(null) {
-			@Override
-			protected Class<?> findClass(String name) throws ClassNotFoundException {
-				for ( ClassLoader loader : classLoadingClassLoaders ) {
-					try {
-						return loader.loadClass( name );
-					}
-					catch (Exception ignore) {
-					}
-				}
-				throw new ClassNotFoundException( "Could not load requested class : " + name );
+		// now build the aggregated class loader...
+		this.aggregatedClassLoader = new AggregatedClassLoader( orderedClassLoaderSet );
+	}
+
+	@SuppressWarnings({"UnusedDeclaration", "unchecked", "deprecation"})
+	@Deprecated
+	public static ClassLoaderServiceImpl fromConfigSettings(Map configVales) {
+		final List<ClassLoader> providedClassLoaders = new ArrayList<ClassLoader>();
+
+		final Collection<ClassLoader> classLoaders = (Collection<ClassLoader>) configVales.get( AvailableSettings.CLASSLOADERS );
+		if ( classLoaders != null ) {
+			for ( ClassLoader classLoader : classLoaders ) {
+				providedClassLoaders.add( classLoader );
 			}
-		};
+		}
 
-		this.resourcesClassLoader = resourcesClassLoader;
+		addIfSet( providedClassLoaders, AvailableSettings.APP_CLASSLOADER, configVales );
+		addIfSet( providedClassLoaders, AvailableSettings.RESOURCES_CLASSLOADER, configVales );
+		addIfSet( providedClassLoaders, AvailableSettings.HIBERNATE_CLASSLOADER, configVales );
+		addIfSet( providedClassLoaders, AvailableSettings.ENVIRONMENT_CLASSLOADER, configVales );
 
-		this.serviceLoaderClassLoader = buildServiceLoaderClassLoader();
+		return new ClassLoaderServiceImpl( providedClassLoaders );
 	}
 
-	@SuppressWarnings( {"UnusedDeclaration"})
-	public static ClassLoaderServiceImpl fromConfigSettings(Map configVales) {
-		return new ClassLoaderServiceImpl(
-				(ClassLoader) configVales.get( AvailableSettings.APP_CLASSLOADER ),
-				(ClassLoader) configVales.get( AvailableSettings.RESOURCES_CLASSLOADER ),
-				(ClassLoader) configVales.get( AvailableSettings.HIBERNATE_CLASSLOADER ),
-				(ClassLoader) configVales.get( AvailableSettings.ENVIRONMENT_CLASSLOADER )
-		);
+	private static void addIfSet(List<ClassLoader> providedClassLoaders, String name, Map configVales) {
+		final ClassLoader providedClassLoader = (ClassLoader) configVales.get( name );
+		if ( providedClassLoader != null ) {
+			providedClassLoaders.add( providedClassLoader );
+		}
 	}
 
 	private static ClassLoader locateSystemClassLoader() {
 		try {
 			return ClassLoader.getSystemClassLoader();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
 	private static ClassLoader locateTCCL() {
 		try {
 			return Thread.currentThread().getContextClassLoader();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
-	private ClassLoader buildServiceLoaderClassLoader() {
-		return new ClassLoader(null) {
-			final ClassLoader[] classLoaderArray = new ClassLoader[] {
-					// first look on the hibernate class loader
-					getClass().getClassLoader(),
-					// next look on the resource class loader
-					resourcesClassLoader,
-					// finally look on the combined class class loader
-					classClassLoader
-			};
+	private static class AggregatedClassLoader extends ClassLoader {
+		private final ClassLoader[] individualClassLoaders;
 
-			@Override
-			public Enumeration<URL> getResources(String name) throws IOException {
-				final HashSet<URL> resourceUrls = new HashSet<URL>();
+		private AggregatedClassLoader(final LinkedHashSet<ClassLoader> orderedClassLoaderSet) {
+			super( null );
+			individualClassLoaders = orderedClassLoaderSet.toArray( new ClassLoader[ orderedClassLoaderSet.size() ] );
+		}
 
-				for ( ClassLoader classLoader : classLoaderArray ) {
-					final Enumeration<URL> urls = classLoader.getResources( name );
-					while ( urls.hasMoreElements() ) {
-						resourceUrls.add( urls.nextElement() );
-					}
-				}
+		@Override
+		public Enumeration<URL> getResources(String name) throws IOException {
+			final HashSet<URL> resourceUrls = new HashSet<URL>();
 
-				return new Enumeration<URL>() {
-					final Iterator<URL> resourceUrlIterator = resourceUrls.iterator();
-					@Override
-					public boolean hasMoreElements() {
-						return resourceUrlIterator.hasNext();
-					}
-
-					@Override
-					public URL nextElement() {
-						return resourceUrlIterator.next();
-					}
-				};
+			for ( ClassLoader classLoader : individualClassLoaders ) {
+				final Enumeration<URL> urls = classLoader.getResources( name );
+				while ( urls.hasMoreElements() ) {
+					resourceUrls.add( urls.nextElement() );
+				}
 			}
 
-			@Override
-			protected URL findResource(String name) {
-				for ( ClassLoader classLoader : classLoaderArray ) {
-					final URL resource = classLoader.getResource( name );
-					if ( resource != null ) {
-						return resource;
-					}
+			return new Enumeration<URL>() {
+				final Iterator<URL> resourceUrlIterator = resourceUrls.iterator();
+				@Override
+				public boolean hasMoreElements() {
+					return resourceUrlIterator.hasNext();
 				}
-				return super.findResource( name );
-			}
 
-			@Override
-			protected Class<?> findClass(String name) throws ClassNotFoundException {
-				for ( ClassLoader classLoader : classLoaderArray ) {
-					try {
-						return classLoader.loadClass( name );
-					}
-					catch (Exception ignore) {
-					}
+				@Override
+				public URL nextElement() {
+					return resourceUrlIterator.next();
 				}
+			};
+		}
 
-				throw new ClassNotFoundException( "Could not load requested class : " + name );
+		@Override
+		protected URL findResource(String name) {
+			for ( ClassLoader classLoader : individualClassLoaders ) {
+				final URL resource = classLoader.getResource( name );
+				if ( resource != null ) {
+					return resource;
+				}
 			}
-		};
+			return super.findResource( name );
+		}
+
+		@Override
+		protected Class<?> findClass(String name) throws ClassNotFoundException {
+			for ( ClassLoader classLoader : individualClassLoaders ) {
+				try {
+					return classLoader.loadClass( name );
+				}
+				catch (Exception ignore) {
+				}
+			}
+
+			throw new ClassNotFoundException( "Could not load requested class : " + name );
+		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> classForName(String className) {
 		try {
-			return (Class<T>) Class.forName( className, true, classClassLoader );
+			return (Class<T>) Class.forName( className, true, aggregatedClassLoader );
 		}
 		catch (Exception e) {
 			throw new ClassLoadingException( "Unable to load class [" + className + "]", e );
 		}
 	}
 
 	@Override
 	public URL locateResource(String name) {
 		// first we try name as a URL
 		try {
 			return new URL( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
-			return resourcesClassLoader.getResource( name );
+			return aggregatedClassLoader.getResource( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public InputStream locateResourceStream(String name) {
 		// first we try name as a URL
 		try {
 			log.tracef( "trying via [new URL(\"%s\")]", name );
 			return new URL( name ).openStream();
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
 			log.tracef( "trying via [ClassLoader.getResourceAsStream(\"%s\")]", name );
-			InputStream stream =  resourcesClassLoader.getResourceAsStream( name );
+			InputStream stream =  aggregatedClassLoader.getResourceAsStream( name );
 			if ( stream != null ) {
 				return stream;
 			}
 		}
 		catch ( Exception ignore ) {
 		}
 
 		final String stripped = name.startsWith( "/" ) ? name.substring(1) : null;
 
 		if ( stripped != null ) {
 			try {
 				log.tracef( "trying via [new URL(\"%s\")]", stripped );
 				return new URL( stripped ).openStream();
 			}
 			catch ( Exception ignore ) {
 			}
 
 			try {
 				log.tracef( "trying via [ClassLoader.getResourceAsStream(\"%s\")]", stripped );
-				InputStream stream = resourcesClassLoader.getResourceAsStream( stripped );
+				InputStream stream = aggregatedClassLoader.getResourceAsStream( stripped );
 				if ( stream != null ) {
 					return stream;
 				}
 			}
 			catch ( Exception ignore ) {
 			}
 		}
 
 		return null;
 	}
 
 	@Override
 	public List<URL> locateResources(String name) {
 		ArrayList<URL> urls = new ArrayList<URL>();
 		try {
-			Enumeration<URL> urlEnumeration = resourcesClassLoader.getResources( name );
+			Enumeration<URL> urlEnumeration = aggregatedClassLoader.getResources( name );
 			if ( urlEnumeration != null && urlEnumeration.hasMoreElements() ) {
 				while ( urlEnumeration.hasMoreElements() ) {
 					urls.add( urlEnumeration.nextElement() );
 				}
 			}
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return urls;
 	}
 
 	@Override
 	public <S> LinkedHashSet<S> loadJavaServices(Class<S> serviceContract) {
-		final ServiceLoader<S> loader = ServiceLoader.load( serviceContract, serviceLoaderClassLoader );
+		final ServiceLoader<S> loader = ServiceLoader.load( serviceContract, aggregatedClassLoader );
 		final LinkedHashSet<S> services = new LinkedHashSet<S>();
 		for ( S service : loader ) {
 			services.add( service );
 		}
 
 		return services;
 	}
 
 	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 	// completely temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 
 	public static interface Work<T> {
 		public T perform();
 	}
 
 	public <T> T withTccl(Work<T> work) {
 		final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 
 		boolean set = false;
 
 		try {
-			Thread.currentThread().setContextClassLoader( serviceLoaderClassLoader);
+			Thread.currentThread().setContextClassLoader( aggregatedClassLoader );
 			set = true;
 		}
 		catch (Exception ignore) {
 		}
 
 		try {
 			return work.perform();
 		}
 		finally {
 			if ( set ) {
 				Thread.currentThread().setContextClassLoader( tccl );
 			}
 		}
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java
index 23717955bc..966f2bebd9 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/StandardServiceRegistryImpl.java
@@ -1,81 +1,81 @@
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
 package org.hibernate.boot.registry.internal;
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.boot.registry.StandardServiceInitiator;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.service.Service;
-import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.AbstractServiceRegistryImpl;
 import org.hibernate.service.internal.ProvidedService;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceBinding;
 import org.hibernate.service.spi.ServiceInitiator;
 
 /**
  * Hibernate implementation of the standard service registry.
  *
  * @author Steve Ebersole
  */
-public class StandardServiceRegistryImpl extends AbstractServiceRegistryImpl implements ServiceRegistry {
+public class StandardServiceRegistryImpl extends AbstractServiceRegistryImpl implements StandardServiceRegistry {
 	private final Map configurationValues;
 
 	@SuppressWarnings( {"unchecked"})
 	public StandardServiceRegistryImpl(
 			BootstrapServiceRegistry bootstrapServiceRegistry,
 			List<StandardServiceInitiator> serviceInitiators,
 			List<ProvidedService> providedServices,
 			Map<?, ?> configurationValues) {
 		super( bootstrapServiceRegistry );
 
 		this.configurationValues = configurationValues;
 
 		// process initiators
 		for ( ServiceInitiator initiator : serviceInitiators ) {
 			createServiceBinding( initiator );
 		}
 
 		// then, explicitly provided service instances
 		for ( ProvidedService providedService : providedServices ) {
 			createServiceBinding( providedService );
 		}
 	}
 
 	@Override
 	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
 		// todo : add check/error for unexpected initiator types?
 		return ( (StandardServiceInitiator<R>) serviceInitiator ).initiateService( configurationValues, this );
 	}
 
 	@Override
 	public <R extends Service> void configureService(ServiceBinding<R> serviceBinding) {
 		if ( Configurable.class.isInstance( serviceBinding.getService() ) ) {
 			( (Configurable) serviceBinding.getService() ).configure( configurationValues );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index 6174541e47..10b4a7f515 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,603 +1,620 @@
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
 package org.hibernate.cfg;
 
 /**
  * @author Steve Ebersole
  */
 public interface AvailableSettings {
 	/**
 	 * Defines a name for the {@link org.hibernate.SessionFactory}.  Useful both to<ul>
 	 *     <li>allow serialization and deserialization to work across different jvms</li>
 	 *     <li>optionally allow the SessionFactory to be bound into JNDI</li>
 	 * </ul>
 	 *
 	 * @see #SESSION_FACTORY_NAME_IS_JNDI
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Does the value defined by {@link #SESSION_FACTORY_NAME} represent a {@literal JNDI} namespace into which
 	 * the {@link org.hibernate.SessionFactory} should be bound?
 	 */
 	public static final String SESSION_FACTORY_NAME_IS_JNDI = "hibernate.session_factory_name_is_jndi";
 
 	/**
 	 * Names the {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} to use for obtaining
 	 * JDBC connections.  Can either reference an instance of
 	 * {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} or a {@link Class} or {@link String}
 	 * reference to the {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} implementation
 	 * class.
 	 */
 	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 
 	/**
 	 * Names the {@literal JDBC} driver class
 	 */
 	public static final String DRIVER ="hibernate.connection.driver_class";
 
 	/**
 	 * Names the {@literal JDBC} connection url.
 	 */
 	public static final String URL ="hibernate.connection.url";
 
 	/**
 	 * Names the connection user.  This might mean one of 2 things in out-of-the-box Hibernate
 	 * {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider}: <ul>
 	 *     <li>The username used to pass along to creating the JDBC connection</li>
 	 *     <li>The username used to obtain a JDBC connection from a data source</li>
 	 * </ul>
 	 */
 	public static final String USER ="hibernate.connection.username";
 
 	/**
 	 * Names the connection password.  See usage discussion on {@link #USER}
 	 */
 	public static final String PASS ="hibernate.connection.password";
 
 	/**
 	 * Names the {@literal JDBC} transaction isolation level
 	 */
 	public static final String ISOLATION ="hibernate.connection.isolation";
 
 	/**
 	 * Names the {@literal JDBC} autocommit mode
 	 */
 	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
 
 	/**
 	 * Maximum number of inactive connections for the built-in Hibernate connection pool.
 	 */
 	public static final String POOL_SIZE ="hibernate.connection.pool_size";
 
 	/**
 	 * Names a {@link javax.sql.DataSource}.  Can either reference a {@link javax.sql.DataSource} instance or
 	 * a {@literal JNDI} name under which to locate the {@link javax.sql.DataSource}.
 	 */
 	public static final String DATASOURCE ="hibernate.connection.datasource";
 
 	/**
 	 * Names a prefix used to define arbitrary JDBC connection properties.  These properties are passed along to
 	 * the {@literal JDBC} provider when creating a connection.
 	 */
 	public static final String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * Names the {@literal JNDI} {@link javax.naming.InitialContext} class.
 	 *
 	 * @see javax.naming.Context#INITIAL_CONTEXT_FACTORY
 	 */
 	public static final String JNDI_CLASS ="hibernate.jndi.class";
 
 	/**
 	 * Names the {@literal JNDI} provider/connection url
 	 *
 	 * @see javax.naming.Context#PROVIDER_URL
 	 */
 	public static final String JNDI_URL ="hibernate.jndi.url";
 
 	/**
 	 * Names a prefix used to define arbitrary {@literal JNDI} {@link javax.naming.InitialContext} properties.  These
 	 * properties are passed along to {@link javax.naming.InitialContext#InitialContext(java.util.Hashtable)}
 	 */
 	public static final String JNDI_PREFIX = "hibernate.jndi";
 
 	/**
 	 * Names the Hibernate {@literal SQL} {@link org.hibernate.dialect.Dialect} class
 	 */
 	public static final String DIALECT ="hibernate.dialect";
 
 	/**
 	 * Names any additional {@link org.hibernate.engine.jdbc.dialect.spi.DialectResolver} implementations to
 	 * register with the standard {@link org.hibernate.engine.jdbc.dialect.spi.DialectFactory}.
 	 */
 	public static final String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
 
 
 	/**
 	 * A default database schema (owner) name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
 	/**
 	 * A default database catalog name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_CATALOG = "hibernate.default_catalog";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	public static final String SHOW_SQL ="hibernate.show_sql";
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	public static final String FORMAT_SQL ="hibernate.format_sql";
 	/**
 	 * Add comments to the generated SQL
 	 */
 	public static final String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 	/**
 	 * The default batch size for batch fetching
 	 */
 	public static final String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	public static final String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 	/**
 	 * An XSLT resource used to generate "custom" XML
 	 */
 	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 	/**
 	 * Idle time before a C3P0 pooled connection is validated
 	 */
 	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 * @deprecated Use {@link #PROXOOL_CONFIG_PREFIX} instead
 	 */
 	public static final String PROXOOL_PREFIX = "hibernate.proxool";
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	public static final String PROXOOL_XML = "hibernate.proxool.xml";
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	public static final String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	public static final String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 	/**
 	 * Specifies how Hibernate should release JDBC connections.
 	 */
 	public static final String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	public static final String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 
 	/**
 	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionFactory} to use for
 	 * creating {@link org.hibernate.Transaction} instances
 	 */
 	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 
 	/**
 	 * Names the {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform} implementation to use for integrating
 	 * with {@literal JTA} systems.  Can reference either a {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform}
 	 * instance or the name of the {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform} implementation class
 	 * @since 4.0
 	 */
 	public static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";
 
 	/**
 	 * The {@link org.hibernate.cache.spi.RegionFactory} implementation class
 	 */
 	public static final String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 	/**
 	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
 	 */
 	public static final String CACHE_NAMESPACE = "hibernate.cache.jndi";
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 	/**
 	 * The <tt>QueryCacheFactory</tt> implementation class.
 	 */
 	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	public static final String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	public static final String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 
 	/**
 	 * Enable statistics collection
 	 */
 	public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	public static final String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	public static final String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	public static final String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
 	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
 	 */
 	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed before the statements of the
 	 * following files.
 	 *
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 *
 	 * The default value is <tt>/import.sql</tt>
 	 */
 	public static final String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * {@link String} reference to {@link org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor} implementation class.
 	 * Referenced implementation is required to provide non-argument constructor.
 	 *
 	 * The default value is <tt>org.hibernate.tool.hbm2ddl.SingleLineSqlCommandExtractor</tt>.
 	 */
 	public static final String HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR = "hibernate.hbm2ddl.import_files_sql_extractor";
 
 	/**
 	 * The {@link org.hibernate.exception.spi.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	public static final String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	public static final String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	public static final String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * Default precedence of null values in {@code ORDER BY} clause.  Supported options: {@code none} (default),
 	 * {@code first}, {@code last}.
 	 */
 	public static final String DEFAULT_NULL_ORDERING = "hibernate.order_by.default_null_ordering";
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
     public static final String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
     /**
      * The jacc context id of the deployment
      */
     public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
 
 	/**
 	 * Should all database identifiers be quoted.
 	 */
 	public static final String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	public static final String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	public static final String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	public static final String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 */
 	public static final String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE}
 	 */
 	@Deprecated
 	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_MAX_SIZE}
 	 */
 	@Deprecated
 	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * The maximum number of entries including:
 	 * <ul>
 	 *     <li>{@link org.hibernate.engine.query.spi.HQLQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.FilterQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.NativeSQLQueryPlan}</li>
 	 * </ul>
 	 * 
 	 * maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_SIZE = "hibernate.query.plan_cache_max_size";
 
 	/**
 	 * The maximum number of {@link org.hibernate.engine.query.spi.ParameterMetadata} maintained 
 	 * by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 */
 	public static final String QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE = "hibernate.query.plan_parameter_metadata_max_size";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 	/**
+	 * Used to define a {@link java.util.Collection} of the {@link ClassLoader} instances Hibernate should use for
+	 * class-loading and resource-lookups.
+	 *
+	 * @since 5.0
+	 */
+	public static final String CLASSLOADERS = "hibernate.classLoaders";
+
+	/**
 	 * Names the {@link ClassLoader} used to load user application classes.
 	 * @since 4.0
+	 *
+	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
+	@Deprecated
 	public static final String APP_CLASSLOADER = "hibernate.classLoader.application";
 
 	/**
 	 * Names the {@link ClassLoader} Hibernate should use to perform resource loading.
 	 * @since 4.0
+	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
+	@Deprecated
 	public static final String RESOURCES_CLASSLOADER = "hibernate.classLoader.resources";
 
 	/**
 	 * Names the {@link ClassLoader} responsible for loading Hibernate classes.  By default this is
 	 * the {@link ClassLoader} that loaded this class.
 	 * @since 4.0
+	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
+	@Deprecated
 	public static final String HIBERNATE_CLASSLOADER = "hibernate.classLoader.hibernate";
 
 	/**
 	 * Names the {@link ClassLoader} used when Hibernate is unable to locates classes on the
 	 * {@link #APP_CLASSLOADER} or {@link #HIBERNATE_CLASSLOADER}.
 	 * @since 4.0
+	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
+	@Deprecated
 	public static final String ENVIRONMENT_CLASSLOADER = "hibernate.classLoader.environment";
 
 
 	public static final String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 
 	public static final String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 
 
 	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
 	public static final String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
 	public static final String JMX_AGENT_ID = "hibernate.jmx.agentId";
 	public static final String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
 	public static final String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
 	public static final String JMX_DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.TransactionManager} references.
 	 * @since 4.0
 	 */
 	public static final String JTA_CACHE_TM = "hibernate.jta.cacheTransactionManager";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.UserTransaction} references.
 	 * @since 4.0
 	 */
 	public static final String JTA_CACHE_UT = "hibernate.jta.cacheUserTransaction";
 
 	/**
 	 * Setting used to give the name of the default {@link org.hibernate.annotations.CacheConcurrencyStrategy}
 	 * to use when either {@link javax.persistence.Cacheable @Cacheable} or
 	 * {@link org.hibernate.annotations.Cache @Cache} is used.  {@link org.hibernate.annotations.Cache @Cache(strategy="..")} is used to override.
 	 */
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = "hibernate.cache.default_cache_concurrency_strategy";
 
 	/**
 	 * Setting which indicates whether or not the new {@link org.hibernate.id.IdentifierGenerator} are used
 	 * for AUTO, TABLE and SEQUENCE.
 	 * Default to false to keep backward compatibility.
 	 */
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";
 
 	/**
 	 * Setting to identify a {@link org.hibernate.CustomEntityDirtinessStrategy} to use.  May point to
 	 * either a class name or instance.
 	 */
 	public static final String CUSTOM_ENTITY_DIRTINESS_STRATEGY = "hibernate.entity_dirtiness_strategy";
 
 	/**
 	 * Strategy for multi-tenancy.
 
 	 * @see org.hibernate.MultiTenancyStrategy
 	 * @since 4.0
 	 */
 	public static final String MULTI_TENANT = "hibernate.multiTenancy";
 
 	/**
 	 * Names a {@link org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider} implementation to
 	 * use.  As MultiTenantConnectionProvider is also a service, can be configured directly through the
 	 * {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}
 	 *
 	 * @since 4.1
 	 */
 	public static final String MULTI_TENANT_CONNECTION_PROVIDER = "hibernate.multi_tenant_connection_provider";
 
 	/**
 	 * Names a {@link org.hibernate.context.spi.CurrentTenantIdentifierResolver} implementation to use.
 	 * <p/>
 	 * Can be<ul>
 	 *     <li>CurrentTenantIdentifierResolver instance</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation {@link Class} reference</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation class name</li>
 	 * </ul>
 	 *
 	 * @since 4.1
 	 */
 	public static final String MULTI_TENANT_IDENTIFIER_RESOLVER = "hibernate.tenant_identifier_resolver";
 
 	public static final String FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT = "hibernate.discriminator.force_in_select";
 
     public static final String ENABLE_LAZY_LOAD_NO_TRANS = "hibernate.enable_lazy_load_no_trans";
 
 	public static final String HQL_BULK_ID_STRATEGY = "hibernate.hql.bulk_id_strategy";
 
 	/**
 	 * Names the {@link org.hibernate.loader.BatchFetchStyle} to use.  Can specify either the
 	 * {@link org.hibernate.loader.BatchFetchStyle} name (insensitively), or a
 	 * {@link org.hibernate.loader.BatchFetchStyle} instance.
 	 */
 	public static final String BATCH_FETCH_STYLE = "hibernate.batch_fetch_style";
 
 	/**
 	 * Enable direct storage of entity references into the second level cache when applicable (immutable data, etc).
 	 * Default is to not store direct references.
 	 */
 	public static final String USE_DIRECT_REFERENCE_CACHE_ENTRIES = "hibernate.cache.use_reference_entries";
 
 	public static final String USE_NATIONALIZED_CHARACTER_DATA = "hibernate.use_nationalized_character_data";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 1671f0d0f9..fdc0c029a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -771,2001 +771,2001 @@ public class Configuration implements Serializable {
 	 * @param packageName java package name
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws MappingException in case there is an error in the mapping data
 	 */
 	public Configuration addPackage(String packageName) throws MappingException {
 		LOG.debugf( "Mapping Package %s", packageName );
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
 			LOG.unableToParseMetadata( packageName );
 			throw me;
 		}
 	}
 
 	/**
 	 * Read all mappings from a jar file
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addJar(File jar) throws MappingException {
 		LOG.searchingForMappingDocuments( jar.getName() );
 		JarFile jarFile = null;
 		try {
 			try {
 				jarFile = new JarFile( jar );
 			}
 			catch (IOException ioe) {
 				throw new InvalidMappingException(
 						"Could not read mapping documents from jar: " + jar.getName(), "jar", jar.getName(),
 						ioe
 				);
 			}
 			Enumeration jarEntries = jarFile.entries();
 			while ( jarEntries.hasMoreElements() ) {
 				ZipEntry ze = (ZipEntry) jarEntries.nextElement();
 				if ( ze.getName().endsWith( ".hbm.xml" ) ) {
 					LOG.foundMappingDocument( ze.getName() );
 					try {
 						addInputStream( jarFile.getInputStream( ze ) );
 					}
 					catch (Exception e) {
 						throw new InvalidMappingException(
 								"Could not read mapping documents from jar: " + jar.getName(),
 								"jar",
 								jar.getName(),
 								e
 						);
 					}
 				}
 			}
 		}
 		finally {
 			try {
 				if ( jarFile != null ) {
 					jarFile.close();
 				}
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseJar( ioe.getMessage() );
 			}
 		}
 
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addDirectory(File dir) throws MappingException {
 		File[] files = dir.listFiles();
 		for ( File file : files ) {
 			if ( file.isDirectory() ) {
 				addDirectory( file );
 			}
 			else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 				addFile( file );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Create a new <tt>Mappings</tt> to add class and collection mappings to.
 	 *
 	 * @return The created mappings
 	 */
 	public Mappings createMappings() {
 		return new MappingsImpl();
 	}
 
 
 	@SuppressWarnings({ "unchecked" })
 	private Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
 		TreeMap generators = new TreeMap();
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		for ( PersistentClass pc : classes.values() ) {
 			if ( !pc.isInherited() ) {
 				IdentifierGenerator ig = pc.getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						(RootClass) pc
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 				else if ( ig instanceof IdentifierGeneratorAggregator ) {
 					( (IdentifierGeneratorAggregator) ig ).registerPersistentGenerators( generators );
 				}
 			}
 		}
 
 		for ( Collection collection : collections.values() ) {
 			if ( collection.isIdentified() ) {
 				IdentifierGenerator ig = ( ( IdentifierCollection ) collection ).getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						null
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 			}
 		}
 
 		return generators.values().iterator();
 	}
 
 	/**
 	 * Generate DDL for dropping tables
 	 *
 	 * @param dialect The dialect for which to generate the drop script
 
 	 * @return The sequence of DDL commands to drop the schema objects
 
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		// drop them in reverse order in case db needs it done that way...
 		{
 			ListIterator itr = auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 			while ( itr.hasPrevious() ) {
 				AuxiliaryDatabaseObject object = (AuxiliaryDatabaseObject) itr.previous();
 				if ( object.appliesToDialect( dialect ) ) {
 					script.add( object.sqlDropString( dialect, defaultCatalog, defaultSchema ) );
 				}
 			}
 		}
 
 		if ( dialect.dropConstraints() ) {
 			Iterator itr = getTableMappings();
 			while ( itr.hasNext() ) {
 				Table table = (Table) itr.next();
 				if ( table.isPhysicalTable() ) {
 					Iterator subItr = table.getForeignKeyIterator();
 					while ( subItr.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subItr.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlDropString(
 											dialect,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 			}
 		}
 
 
 		Iterator itr = getTableMappings();
 		while ( itr.hasNext() ) {
 
 			Table table = (Table) itr.next();
 			if ( table.isPhysicalTable() ) {
 
 				/*Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
 						script.add( index.sqlDropString(dialect) );
 					}
 				}*/
 
 				script.add(
 						table.sqlDropString(
 								dialect,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 
 			}
 
 		}
 
 		itr = iterateGenerators( dialect );
 		while ( itr.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) itr.next() ).sqlDropStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 *
 	 * @return The sequence of DDL commands to create the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 				script.add(
 						table.sqlCreateString(
 								dialect,
 								mapping,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				Iterator subIter = table.getUniqueKeyIterator();
 				while ( subIter.hasNext() ) {
 					UniqueKey uk = (UniqueKey) subIter.next();
 					String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 					if (constraintString != null) script.add( constraintString );
 				}
 
 
 				subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlCreateString(
 											dialect, mapping,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) iter.next() ).sqlCreateStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : auxiliaryDatabaseObjects ) {
 			if ( auxiliaryDatabaseObject.appliesToDialect( dialect ) ) {
 				script.add( auxiliaryDatabaseObject.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 * @param databaseMetadata The database catalog information for the database to be updated; needed to work out what
 	 * should be created/altered
 	 *
 	 * @return The sequence of DDL commands to apply the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
 						table.isQuoted()
 				);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									tableCatalog,
 									tableSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							tableCatalog,
 							tableSchema
 						);
 					while ( subiter.hasNext() ) {
 						script.add( subiter.next() );
 					}
 				}
 
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
 						table.isQuoted()
 					);
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || (
 									tableInfo.getForeignKeyMetadata( fk ) == null && (
 											//Icky workaround for MySQL bug:
 											!( dialect instanceof MySQLDialect ) ||
 													tableInfo.getIndexMetadata( fk.getName() ) == null
 										)
 								);
 							if ( create ) {
 								script.add(
 										fk.sqlCreateString(
 												dialect,
 												mapping,
 												tableCatalog,
 												tableSchema
 											)
 									);
 							}
 						}
 					}
 				}
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					final Index index = (Index) subIter.next();
 					// Skip if index already exists
 					if ( tableInfo != null && StringHelper.isNotEmpty( index.getName() ) ) {
 						final IndexMetadata meta = tableInfo.getIndexMetadata( index.getName() );
 						if ( meta != null ) {
 							continue;
 						}
 					}
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									tableCatalog,
 									tableSchema
 							)
 					);
 				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				script.addAll( Arrays.asList( lines ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted());
 				if ( tableInfo == null ) {
 					throw new HibernateException( "Missing table: " + table.getName() );
 				}
 				else {
 					table.validateColumns( dialect, mapping, tableInfo );
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
 	}
 
 	private void validate() throws MappingException {
 		Iterator iter = classes.values().iterator();
 		while ( iter.hasNext() ) {
 			( (PersistentClass) iter.next() ).validate( mapping );
 		}
 		iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			( (Collection) iter.next() ).validate( mapping );
 		}
 	}
 
 	/**
 	 * Call this to ensure the mappings are fully compiled/built. Usefull to ensure getting
 	 * access to all information in the metamodel when calling e.g. getClassMappings().
 	 */
 	public void buildMappings() {
 		secondPassCompile();
 	}
 
 	protected void secondPassCompile() throws MappingException {
 		LOG.trace( "Starting secondPassCompile() processing" );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				Map defaults = reflectionManager.getDefaults();
 				final Object isDelimited = defaults.get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 				// Set default schema name if orm.xml declares it.
 				final String schema = (String) defaults.get( "schema" );
 				if ( StringHelper.isNotEmpty( schema ) ) {
 					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
 				}
 				// Set default catalog name if orm.xml declares it.
 				final String catalog = (String) defaults.get( "catalog" );
 				if ( StringHelper.isNotEmpty( catalog ) ) {
 					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
 				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
 		}
 
 
 
 		try {
 			inSecondPass = true;
 			processSecondPassesOfType( PkDrivenByDefaultMapsIdSecondPass.class );
 			processSecondPassesOfType( SetSimpleValueTypeSecondPass.class );
 			processSecondPassesOfType( CopyIdentifierComponentSecondPass.class );
 			processFkSecondPassInOrder();
 			processSecondPassesOfType( CreateKeySecondPass.class );
 			processSecondPassesOfType( SecondaryTableSecondPass.class );
 
 			originalSecondPassCompile();
 
 			inSecondPass = false;
 		}
 		catch ( RecoverableException e ) {
 			//the exception was not recoverable after all
 			throw ( RuntimeException ) e.getCause();
 		}
 
 		// process cache queue
 		{
 			for ( CacheHolder holder : caches ) {
 				if ( holder.isClass ) {
 					applyCacheConcurrencyStrategy( holder );
 				}
 				else {
 					applyCollectionCacheConcurrencyStrategy( holder );
 				}
 			}
 			caches.clear();
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "key" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 	}
 
 	private void processSecondPassesOfType(Class<? extends SecondPass> type) {
 		Iterator iter = secondPasses.iterator();
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of simple value types first and remove them
 			if ( type.isInstance( sp ) ) {
 				sp.doSecondPass( classes );
 				iter.remove();
 			}
 		}
 	}
 
 	/**
 	 * Processes FKSecondPass instances trying to resolve any
 	 * graph circularity (ie PK made of a many to one linking to
 	 * an entity having a PK made of a ManyToOne ...).
 	 */
 	private void processFkSecondPassInOrder() {
 		LOG.debug("Processing fk mappings (*ToOne and JoinedSubclass)");
 		List<FkSecondPass> fkSecondPasses = getFKSecondPassesOnly();
 
 		if ( fkSecondPasses.size() == 0 ) {
 			return; // nothing to do here
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( FkSecondPass sp : fkSecondPasses ) {
 			if ( sp.isInPrimaryKey() ) {
 				String referenceEntityName = sp.getReferencedEntityName();
 				PersistentClass classMapping = getClassMapping( referenceEntityName );
 				String dependentTable = quotedTableName(classMapping.getTable());
 				if ( !isADependencyOf.containsKey( dependentTable ) ) {
 					isADependencyOf.put( dependentTable, new HashSet<FkSecondPass>() );
 				}
 				isADependencyOf.get( dependentTable ).add( sp );
 			}
 			else {
 				endOfQueueFkSecondPasses.add( sp );
 			}
 		}
 
 		// using the isADependencyOf map we order the FkSecondPass recursively instances into the right order for processing
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( classes );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 	}
 
 	/**
 	 * @return Returns a list of all <code>secondPasses</code> instances which are a instance of
 	 *         <code>FkSecondPass</code>.
 	 */
 	private List<FkSecondPass> getFKSecondPassesOnly() {
 		Iterator iter = secondPasses.iterator();
 		List<FkSecondPass> fkSecondPasses = new ArrayList<FkSecondPass>( secondPasses.size() );
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of fk before the others and remove them
 			if ( sp instanceof FkSecondPass ) {
 				fkSecondPasses.add( ( FkSecondPass ) sp );
 				iter.remove();
 			}
 		}
 		return fkSecondPasses;
 	}
 
 	/**
 	 * Recursively builds a list of FkSecondPass instances ready to be processed in this order.
 	 * Checking all dependencies recursively seems quite expensive, but the original code just relied
 	 * on some sort of table name sorting which failed in certain circumstances.
 	 * <p/>
 	 * See <tt>ANN-722</tt> and <tt>ANN-730</tt>
 	 *
 	 * @param orderedFkSecondPasses The list containing the <code>FkSecondPass<code> instances ready
 	 * for processing.
 	 * @param isADependencyOf Our lookup data structure to determine dependencies between tables
 	 * @param startTable Table name to start recursive algorithm.
 	 * @param currentTable The current table name used to check for 'new' dependencies.
 	 */
 	private void buildRecursiveOrderedFkSecondPasses(
 			List<FkSecondPass> orderedFkSecondPasses,
 			Map<String, Set<FkSecondPass>> isADependencyOf,
 			String startTable,
 			String currentTable) {
 
 		Set<FkSecondPass> dependencies = isADependencyOf.get( currentTable );
 
 		// bottom out
 		if ( dependencies == null || dependencies.size() == 0 ) {
 			return;
 		}
 
 		for ( FkSecondPass sp : dependencies ) {
 			String dependentTable = quotedTableName(sp.getValue().getTable());
 			if ( dependentTable.compareTo( startTable ) == 0 ) {
 				StringBuilder sb = new StringBuilder(
 						"Foreign key circularity dependency involving the following tables: "
 				);
 				throw new AnnotationException( sb.toString() );
 			}
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, startTable, dependentTable );
 			if ( !orderedFkSecondPasses.contains( sp ) ) {
 				orderedFkSecondPasses.add( 0, sp );
 			}
 		}
 	}
 
 	private String quotedTableName(Table table) {
 		return Table.qualify( table.getCatalog(), table.getQuotedSchema(), table.getQuotedName() );
 	}
 
 	private void processEndOfQueue(List<FkSecondPass> endOfQueueFkSecondPasses) {
 		/*
 		 * If a second pass raises a recoverableException, queue it for next round
 		 * stop of no pass has to be processed or if the number of pass to processes
 		 * does not diminish between two rounds.
 		 * If some failing pass remain, raise the original exception
 		 */
 		boolean stopProcess = false;
 		RuntimeException originalException = null;
 		while ( !stopProcess ) {
 			List<FkSecondPass> failingSecondPasses = new ArrayList<FkSecondPass>();
 			for ( FkSecondPass pass : endOfQueueFkSecondPasses ) {
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch (RecoverableException e) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = (RuntimeException) e.getCause();
 					}
 				}
 			}
 			stopProcess = failingSecondPasses.size() == 0 || failingSecondPasses.size() == endOfQueueFkSecondPasses.size();
 			endOfQueueFkSecondPasses = failingSecondPasses;
 		}
 		if ( endOfQueueFkSecondPasses.size() > 0 ) {
 			throw originalException;
 		}
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = normalizer.normalizeIdentifierQuoting( columnNames[index] );
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( logicalColumnName, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( logicalColumnName ) );
 			}
 		}
 		UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uk.addColumn( column );
 				unbound.remove( column );
 			}
 		}
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": database column " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
 		LOG.debug( "Processing extends queue" );
 		processExtendsQueue();
 
 		LOG.debug( "Processing collection mappings" );
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
 		LOG.debug( "Processing native query and ResultSetMapping mappings" );
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
 		LOG.debug( "Processing association property references" );
 
 		itr = propertyReferences.iterator();
 		while ( itr.hasNext() ) {
 			Mappings.PropertyReference upr = (Mappings.PropertyReference) itr.next();
 
 			PersistentClass clazz = getClassMapping( upr.referencedClass );
 			if ( clazz == null ) {
 				throw new MappingException(
 						"property-ref to unmapped class: " +
 						upr.referencedClass
 					);
 			}
 
 			Property prop = clazz.getReferencedProperty( upr.propertyName );
 			if ( upr.unique ) {
 				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 		
 		//TODO: Somehow add the newly created foreign keys to the internal collection
 
 		LOG.debug( "Creating tables' unique integer identifiers" );
 		LOG.debug( "Processing foreign key constraints" );
 
 		itr = getTableMappings();
 		int uniqueInteger = 0;
 		Set<ForeignKey> done = new HashSet<ForeignKey>();
 		while ( itr.hasNext() ) {
 			Table table = (Table) itr.next();
 			table.setUniqueInteger( uniqueInteger++ );
 			secondPassCompileForeignKeys( table, done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
 		LOG.debug( "Processing extends queue" );
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
 			StringBuilder buf = new StringBuilder( "Following super classes referenced in extends not found: " );
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = ( ExtendsQueueEntry ) iterator.next();
 				buf.append( entry.getExplicitName() );
 				if ( entry.getMappingPackage() != null ) {
 					buf.append( "[" ).append( entry.getMappingPackage() ).append( "]" );
 				}
 				if ( iterator.hasNext() ) {
 					buf.append( "," );
 				}
 			}
 			throw new MappingException( buf.toString() );
 		}
 
 		return added;
 	}
 
 	protected ExtendsQueueEntry findPossibleExtends() {
 		Iterator<ExtendsQueueEntry> itr = extendsQueue.keySet().iterator();
 		while ( itr.hasNext() ) {
 			final ExtendsQueueEntry entry = itr.next();
 			boolean found = getClassMapping( entry.getExplicitName() ) != null
 					|| getClassMapping( HbmBinder.getClassName( entry.getExplicitName(), entry.getMappingPackage() ) ) != null;
 			if ( found ) {
 				itr.remove();
 				return entry;
 			}
 		}
 		return null;
 	}
 
 	protected void secondPassCompileForeignKeys(Table table, Set<ForeignKey> done) throws MappingException {
 		table.createForeignKeys();
 		Iterator iter = table.getForeignKeyIterator();
 		while ( iter.hasNext() ) {
 
 			ForeignKey fk = (ForeignKey) iter.next();
 			if ( !done.contains( fk ) ) {
 				done.add( fk );
 				final String referencedEntityName = fk.getReferencedEntityName();
 				if ( referencedEntityName == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" does not specify the referenced entity"
 						);
 				}
 				LOG.debugf( "Resolving reference to class: %s", referencedEntityName );
 				PersistentClass referencedClass = classes.get( referencedEntityName );
 				if ( referencedClass == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" refers to an unmapped class: " +
 							referencedEntityName
 						);
 				}
 				if ( referencedClass.isJoinedSubclass() ) {
 					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done );
 				}
 				fk.setReferencedTable( referencedClass.getTable() );
 				fk.alignColumns();
 			}
 		}
 	}
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @param serviceRegistry The registry of services to be used in creating this session factory.
 	 *
 	 * @return The built {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		LOG.debugf( "Preparing to build session factory with filters : %s", filterDefinitions );
 
 		secondPassCompile();
 		if ( !metadataSourceQueue.isEmpty() ) {
 			LOG.incompleteMappingMetadataCacheProcessing();
 		}
 
 		validate();
 
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
 		Settings settings = buildSettings( copy, serviceRegistry );
 
 		return new SessionFactoryImpl(
 				this,
 				mapping,
 				serviceRegistry,
 				settings,
 				sessionFactoryObserver
 			);
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 *
 	 * @deprecated Use {@link #buildSessionFactory(ServiceRegistry)} instead
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		final ServiceRegistry serviceRegistry =  new StandardServiceRegistryBuilder()
 				.applySettings( properties )
-				.buildServiceRegistry();
+				.build();
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	/**
 	 * Retrieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory built}
 	 * {@link SessionFactory}.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setInterceptor(Interceptor interceptor) {
 		this.interceptor = interceptor;
 		return this;
 	}
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
 	}
 
 	/**
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value currently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		return properties.getProperty( propertyName );
 	}
 
 	/**
 	 * Specify a completely new set of properties
 	 *
 	 * @param properties The new set of properties
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperties(Properties properties) {
 		this.properties = properties;
 		return this;
 	}
 
 	/**
 	 * Add the given properties to ours.
 	 *
 	 * @param extraProperties The properties to add.
 	 *
 	 * @return this for method chaining
 	 *
 	 */
 	public Configuration addProperties(Properties extraProperties) {
 		this.properties.putAll( extraProperties );
 		return this;
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for ethod chaining
 	 */
 	public Configuration mergeProperties(Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( this.properties.containsKey( entry.getKey() ) ) {
 				continue;
 			}
 			this.properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
 		}
 		return this;
 	}
 
 	/**
 	 * Set a property value by name
 	 *
 	 * @param propertyName The name of the property to set
 	 * @param value The new property value
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperty(String propertyName, String value) {
 		properties.setProperty( propertyName, value );
 		return this;
 	}
 
 	private void addProperties(Element parent) {
 		Iterator itr = parent.elementIterator( "property" );
 		while ( itr.hasNext() ) {
 			Element node = (Element) itr.next();
 			String name = node.attributeValue( "name" );
 			String value = node.getText().trim();
 			LOG.debugf( "%s=%s", name, value );
 			properties.setProperty( name, value );
 			if ( !name.startsWith( "hibernate" ) ) {
 				properties.setProperty( "hibernate." + name, value );
 			}
 		}
 		Environment.verifyProperties( properties );
 	}
 
 	/**
 	 * Use the mappings and properties specified in an application resource named <tt>hibernate.cfg.xml</tt>.
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find <tt>hibernate.cfg.xml</tt>
 	 *
 	 * @see #configure(String)
 	 */
 	public Configuration configure() throws HibernateException {
 		configure( "/hibernate.cfg.xml" );
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 * <p/>
 	 * The resource is found via {@link #getConfigurationInputStream}
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(String resource) throws HibernateException {
 		LOG.configuringFromResource( resource );
 		InputStream stream = getConfigurationInputStream( resource );
 		return doConfigure( stream, resource );
 	}
 
 	/**
 	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
 	 * by subclasses to allow the configuration to be located by some arbitrary
 	 * mechanism.
 	 * <p/>
 	 * By default here we use classpath resource resolution
 	 *
 	 * @param resource The resource to locate
 	 *
 	 * @return The stream
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
 		LOG.configurationResource( resource );
 		return ConfigHelper.getResourceAsStream( resource );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(URL url) throws HibernateException {
 		LOG.configuringFromUrl( url );
 		try {
 			return doConfigure( url.openStream(), url.toString() );
 		}
 		catch (IOException ioe) {
 			throw new HibernateException( "could not configure from URL: " + url, ioe );
 		}
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application file. The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param configFile File from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the file
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
 		LOG.configuringFromFile( configFile.getName() );
 		try {
 			return doConfigure( new FileInputStream( configFile ), configFile.toString() );
 		}
 		catch (FileNotFoundException fnfe) {
 			throw new HibernateException( "could not find file: " + configFile, fnfe );
 		}
 	}
 
 	/**
 	 * Configure this configuration's state from the contents of the given input stream.  The expectation is that
 	 * the stream contents represent an XML document conforming to the Hibernate Configuration DTD.  See
 	 * {@link #doConfigure(Document)} for further details.
 	 *
 	 * @param stream The input stream from which to read
 	 * @param resourceName The name to use in warning/error messages
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem reading the stream contents.
 	 */
 	protected Configuration doConfigure(InputStream stream, String resourceName) throws HibernateException {
 		try {
 			ErrorLogger errorLogger = new ErrorLogger( resourceName );
 			Document document = xmlHelper.createSAXReader( errorLogger,  entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errorLogger.hasErrors() ) {
 				throw new MappingException( "invalid configuration", errorLogger.getErrors().get( 0 ) );
 			}
 			doConfigure( document );
 		}
 		catch (DocumentException e) {
 			throw new HibernateException( "Could not parse configuration: " + resourceName, e );
 		}
 		finally {
 			try {
 				stream.close();
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseInputStreamForResource( resourceName, ioe );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given XML document.
 	 * The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param document an XML document from which you wish to load the configuration
 	 * @return A configuration configured via the <tt>Document</tt>
 	 * @throws HibernateException if there is problem in accessing the file.
 	 */
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
 		LOG.configuringFromXmlDocument();
 		return doConfigure( xmlHelper.createDOMReader().read( document ) );
 	}
 
 	/**
 	 * Parse a dom4j document conforming to the Hibernate Configuration DTD (<tt>hibernate-configuration-3.0.dtd</tt>)
 	 * and use its information to configure this {@link Configuration}'s state
 	 *
 	 * @param doc The dom4j document
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem performing the configuration task
 	 */
 	protected Configuration doConfigure(Document doc) throws HibernateException {
 		Element sfNode = doc.getRootElement().element( "session-factory" );
 		String name = sfNode.attributeValue( "name" );
 		if ( name != null ) {
 			properties.setProperty( Environment.SESSION_FACTORY_NAME, name );
 		}
 		addProperties( sfNode );
 		parseSessionFactory( sfNode, name );
 
 		Element secNode = doc.getRootElement().element( "security" );
 		if ( secNode != null ) {
 			parseSecurity( secNode );
 		}
 
 		LOG.configuredSessionFactory( name );
 		LOG.debugf( "Properties: %s", properties );
 
 		return this;
 	}
 
 
 	private void parseSessionFactory(Element sfNode, String name) {
 		Iterator elements = sfNode.elementIterator();
 		while ( elements.hasNext() ) {
 			Element subelement = (Element) elements.next();
 			String subelementName = subelement.getName();
 			if ( "mapping".equals( subelementName ) ) {
 				parseMappingElement( subelement, name );
 			}
 			else if ( "class-cache".equals( subelementName ) ) {
 				String className = subelement.attributeValue( "class" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? className : regionNode.getValue();
 				boolean includeLazy = !"non-lazy".equals( subelement.attributeValue( "include" ) );
 				setCacheConcurrencyStrategy( className, subelement.attributeValue( "usage" ), region, includeLazy );
 			}
 			else if ( "collection-cache".equals( subelementName ) ) {
 				String role = subelement.attributeValue( "collection" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? role : regionNode.getValue();
 				setCollectionCacheConcurrencyStrategy( role, subelement.attributeValue( "usage" ), region );
 			}
 		}
 	}
 
 	private void parseMappingElement(Element mappingElement, String name) {
 		final Attribute resourceAttribute = mappingElement.attribute( "resource" );
 		final Attribute fileAttribute = mappingElement.attribute( "file" );
 		final Attribute jarAttribute = mappingElement.attribute( "jar" );
 		final Attribute packageAttribute = mappingElement.attribute( "package" );
 		final Attribute classAttribute = mappingElement.attribute( "class" );
 
 		if ( resourceAttribute != null ) {
 			final String resourceName = resourceAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named resource [%s] for mapping", name, resourceName );
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named file [%s] for mapping", name, fileName );
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName );
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named package [%s] for mapping", name, packageName );
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named class [%s] for mapping", name, className );
 			try {
 				addAnnotatedClass( ReflectHelper.classForName( className ) );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Unable to load class [ " + className + "] declared in Hibernate configuration <mapping/> entry",
 						e
 				);
 			}
 		}
 		else {
 			throw new MappingException( "<mapping> element in configuration specifies no known attributes" );
 		}
 	}
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 		LOG.jaccContextId( contextId );
 		JACCConfiguration jcfg = new JACCConfiguration( contextId );
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			Element grantElement = (Element) grantElements.next();
 			String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jcfg.addPermission(
 						grantElement.attributeValue( "role" ),
 						grantElement.attributeValue( "entity-name" ),
 						grantElement.attributeValue( "actions" )
 					);
 			}
 		}
 	}
 
 	RootClass getRootClassMapping(String clazz) throws MappingException {
 		try {
 			return (RootClass) getClassMapping( clazz );
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "You may only specify a cache for root <class> mappings" );
 		}
 	}
 
 	/**
 	 * Set up a cache for an entity class
 	 *
 	 * @param entityName The name of the entity to which we shoudl associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, entityName );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for an entity class, giving an explicit region name
 	 *
 	 * @param entityName The name of the entity to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy, String region) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, region, true );
 		return this;
 	}
 
 	public void setCacheConcurrencyStrategy(
 			String entityName,
 			String concurrencyStrategy,
 			String region,
 			boolean cacheLazyProperty) throws MappingException {
 		caches.add( new CacheHolder( entityName, concurrencyStrategy, region, true, cacheLazyProperty ) );
 	}
 
 	private void applyCacheConcurrencyStrategy(CacheHolder holder) {
 		RootClass rootClass = getRootClassMapping( holder.role );
 		if ( rootClass == null ) {
 			throw new MappingException( "Cannot cache an unknown entity: " + holder.role );
 		}
 		rootClass.setCacheConcurrencyStrategy( holder.usage );
 		rootClass.setCacheRegionName( holder.region );
 		rootClass.setLazyPropertiesCacheable( holder.cacheLazy );
 	}
 
 	/**
 	 * Set up a cache for a collection role
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy) {
 		setCollectionCacheConcurrencyStrategy( collectionRole, concurrencyStrategy, collectionRole );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for a collection role, giving an explicit region name
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 */
 	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region) {
 		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
 	}
 
 	private void applyCollectionCacheConcurrencyStrategy(CacheHolder holder) {
 		Collection collection = getCollectionMapping( holder.role );
 		if ( collection == null ) {
 			throw new MappingException( "Cannot cache an unknown collection: " + holder.role );
 		}
 		collection.setCacheConcurrencyStrategy( holder.usage );
 		collection.setCacheRegionName( holder.region );
 	}
 
 	/**
 	 * Get the query language imports
 	 *
 	 * @return a mapping from "import" names to fully qualified class names
 	 */
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	/**
 	 * Create an object-oriented view of the configuration properties
 	 *
 	 * @param serviceRegistry The registry of services to be used in building these settings.
 	 *
 	 * @return The build settings
 	 */
 	public Settings buildSettings(ServiceRegistry serviceRegistry) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
 		return buildSettingsInternal( clone, serviceRegistry );
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) throws HibernateException {
 		return buildSettingsInternal( props, serviceRegistry );
 	}
 
 	private Settings buildSettingsInternal(Properties props, ServiceRegistry serviceRegistry) {
 		final Settings settings = settingsFactory.buildSettings( props, serviceRegistry );
 		settings.setEntityTuplizerFactory( this.getEntityTuplizerFactory() );
 //		settings.setComponentTuplizerFactory( this.getComponentTuplizerFactory() );
 		return settings;
 	}
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	/**
 	 * Set a custom naming strategy
 	 *
 	 * @param namingStrategy the NamingStrategy to set
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		this.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this configuration.
 	 *
 	 * @return This configuration's IdentifierGeneratorFactory.
 	 */
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	public Mapping buildMapping() {
 		return new Mapping() {
 			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 				return identifierGeneratorFactory;
 			}
 
 			/**
 			 * Returns the identifier type of a mapped class
 			 */
 			public Type getIdentifierType(String entityName) throws MappingException {
 				PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				return pc.getIdentifier().getType();
 			}
 
 			public String getIdentifierPropertyName(String entityName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				if ( !pc.hasIdentifierProperty() ) {
 					return null;
 				}
 				return pc.getIdentifierProperty().getName();
 			}
 
 			public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				Property prop = pc.getReferencedProperty( propertyName );
 				if ( prop == null ) {
 					throw new MappingException(
 							"property not known: " +
 							entityName + '.' + propertyName
 						);
 				}
 				return prop.getType();
 			}
 		};
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		//we need  reflectionManager before reading the other components (MetadataSourceQueue in particular)
 		final MetadataProvider metadataProvider = (MetadataProvider) ois.readObject();
 		this.mapping = buildMapping();
 		xmlHelper = new XMLHelper();
 		createReflectionManager(metadataProvider);
 		ois.defaultReadObject();
 	}
 
 	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 		//We write MetadataProvider first as we need  reflectionManager before reading the other components
 		final MetadataProvider metadataProvider = ( ( MetadataProviderInjector ) reflectionManager ).getMetadataProvider();
 		out.writeObject( metadataProvider );
 		out.defaultWriteObject();
 	}
 
 	private void createReflectionManager() {
 		createReflectionManager( new JPAMetadataProvider() );
 	}
 
 	private void createReflectionManager(MetadataProvider metadataProvider) {
 		reflectionManager = new JavaReflectionManager();
 		( ( MetadataProviderInjector ) reflectionManager ).setMetadataProvider( metadataProvider );
 	}
 
 	public Map getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		filterDefinitions.put( definition.getFilterName(), definition );
 	}
 
 	public Iterator iterateFetchProfiles() {
 		return fetchProfiles.values().iterator();
 	}
 
 	public void addFetchProfile(FetchProfile fetchProfile) {
 		fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		auxiliaryDatabaseObjects.add( object );
 	}
 
 	public Map getSqlFunctions() {
 		return sqlFunctions;
 	}
 
 	public void addSqlFunction(String functionName, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( functionName.toLowerCase(), function );
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public void registerTypeOverride(BasicType type) {
 		getTypeResolver().registerTypeOverride( type );
 	}
 
 
 	public void registerTypeOverride(UserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public void registerTypeOverride(CompositeUserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
 		this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
 		final AttributeConverter attributeConverter;
 		try {
 			attributeConverter = attributeConverterClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]"
 			);
 		}
 		addAttributeConverter( attributeConverter, autoApply );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
 		if ( attributeConverterDefinitionsByClass == null ) {
 			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
 		}
 
 		final Object old = attributeConverterDefinitionsByClass.put(
 				attributeConverter.getClass(),
 				new AttributeConverterDefinition( attributeConverter, autoApply )
 		);
 
 		if ( old != null ) {
 			throw new AssertionFailure(
 					"AttributeConverter class [" + attributeConverter.getClass() + "] registered multiple times"
 			);
 		}
 	}
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
 	@SuppressWarnings( {"deprecation", "unchecked"})
 	protected class MappingsImpl implements ExtendedMappings, Serializable {
 
 		private String schemaName;
 
 		public String getSchemaName() {
 			return schemaName;
 		}
 
 		public void setSchemaName(String schemaName) {
 			this.schemaName = schemaName;
 		}
 
 
 		private String catalogName;
 
 		public String getCatalogName() {
 			return catalogName;
 		}
 
 		public void setCatalogName(String catalogName) {
 			this.catalogName = catalogName;
 		}
 
 
 		private String defaultPackage;
 
 		public String getDefaultPackage() {
 			return defaultPackage;
 		}
 
 		public void setDefaultPackage(String defaultPackage) {
 			this.defaultPackage = defaultPackage;
 		}
 
 
 		private boolean autoImport;
 
 		public boolean isAutoImport() {
 			return autoImport;
 		}
 
 		public void setAutoImport(boolean autoImport) {
 			this.autoImport = autoImport;
 		}
 
 
 		private boolean defaultLazy;
 
 		public boolean isDefaultLazy() {
 			return defaultLazy;
 		}
 
 		public void setDefaultLazy(boolean defaultLazy) {
 			this.defaultLazy = defaultLazy;
 		}
 
 
 		private String defaultCascade;
 
 		public String getDefaultCascade() {
 			return defaultCascade;
 		}
 
 		public void setDefaultCascade(String defaultCascade) {
 			this.defaultCascade = defaultCascade;
 		}
 
 
 		private String defaultAccess;
 
 		public String getDefaultAccess() {
 			return defaultAccess;
 		}
 
 		public void setDefaultAccess(String defaultAccess) {
 			this.defaultAccess = defaultAccess;
 		}
 
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		public void setNamingStrategy(NamingStrategy namingStrategy) {
 			Configuration.this.namingStrategy = namingStrategy;
 		}
 
 		public TypeResolver getTypeResolver() {
 			return typeResolver;
 		}
 
 		public Iterator<PersistentClass> iterateClasses() {
 			return classes.values().iterator();
 		}
 
 		public PersistentClass getClass(String entityName) {
 			return classes.get( entityName );
 		}
 
 		public PersistentClass locatePersistentClassByEntityName(String entityName) {
 			PersistentClass persistentClass = classes.get( entityName );
 			if ( persistentClass == null ) {
 				String actualEntityName = imports.get( entityName );
 				if ( StringHelper.isNotEmpty( actualEntityName ) ) {
 					persistentClass = classes.get( actualEntityName );
 				}
 			}
 			return persistentClass;
 		}
 
 		public void addClass(PersistentClass persistentClass) throws DuplicateMappingException {
 			Object old = classes.put( persistentClass.getEntityName(), persistentClass );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "class/entity", persistentClass.getEntityName() );
 			}
 		}
 
 		public void addImport(String entityName, String rename) throws DuplicateMappingException {
 			String existing = imports.put( rename, entityName );
 			if ( existing != null ) {
                 if (existing.equals(entityName)) LOG.duplicateImport(entityName, rename);
                 else throw new DuplicateMappingException("duplicate import: " + rename + " refers to both " + entityName + " and "
                                                          + existing + " (try using auto-import=\"false\")", "import", rename);
 			}
 		}
 
 		public Collection getCollection(String role) {
 			return collections.get( role );
 		}
 
 		public Iterator<Collection> iterateCollections() {
 			return collections.values().iterator();
 		}
 
 		public void addCollection(Collection collection) throws DuplicateMappingException {
 			Object old = collections.put( collection.getRole(), collection );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "collection role", collection.getRole() );
 			}
 		}
 
 		public Table getTable(String schema, String catalog, String name) {
 			String key = Table.qualify(catalog, schema, name);
 			return tables.get(key);
 		}
 
 		public Iterator<Table> iterateTables() {
 			return tables.values().iterator();
 		}
 
 		public Table addTable(
 				String schema,
 				String catalog,
 				String name,
 				String subselect,
 				boolean isAbstract) {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify( catalog, schema, name ) : subselect;
 			Table table = tables.get( key );
 
 			if ( table == null ) {
 				table = new Table();
 				table.setAbstract( isAbstract );
 				table.setName( name );
 				table.setSchema( schema );
 				table.setCatalog( catalog );
 				table.setSubselect( subselect );
 				tables.put( key, table );
 			}
 			else {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 			}
 
 			return table;
 		}
 
 		public Table addDenormalizedTable(
 				String schema,
 				String catalog,
 				String name,
 				boolean isAbstract,
 				String subselect,
 				Table includedTable) throws DuplicateMappingException {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify(catalog, schema, name) : subselect;
 			if ( tables.containsKey( key ) ) {
 				throw new DuplicateMappingException( "table", name );
 			}
 
 			Table table = new DenormalizedTable( includedTable );
 			table.setAbstract( isAbstract );
 			table.setName( name );
 			table.setSchema( schema );
 			table.setCatalog( catalog );
 			table.setSubselect( subselect );
 
 			tables.put( key, table );
 			return table;
 		}
 
 		public NamedQueryDefinition getQuery(String name) {
 			return namedQueries.get( name );
 		}
 
 		public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedQueryNames.contains( name ) ) {
 				applyQuery( name, query );
 			}
 		}
 
 		private void applyQuery(String name, NamedQueryDefinition query) {
 			checkQueryName( name );
 			namedQueries.put( name.intern(), query );
 		}
 
 		private void checkQueryName(String name) throws DuplicateMappingException {
 			if ( namedQueries.containsKey( name ) || namedSqlQueries.containsKey( name ) ) {
 				throw new DuplicateMappingException( "query", name );
 			}
 		}
 
 		public void addDefaultQuery(String name, NamedQueryDefinition query) {
 			applyQuery( name, query );
 			defaultNamedQueryNames.add( name );
 		}
 
 		public NamedSQLQueryDefinition getSQLQuery(String name) {
 			return namedSqlQueries.get( name );
 		}
 
 		public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedNativeQueryNames.contains( name ) ) {
 				applySQLQuery( name, query );
 			}
 		}
 
 		private void applySQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			checkQueryName( name );
 			namedSqlQueries.put( name.intern(), query );
 		}
 
 		public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query) {
 			applySQLQuery( name, query );
 			defaultNamedNativeQueryNames.add( name );
 		}
 
 		public ResultSetMappingDefinition getResultSetMapping(String name) {
 			return sqlResultSetMappings.get(name);
 		}
 
 		public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			if ( !defaultSqlResultSetMappingNames.contains( sqlResultSetMapping.getName() ) ) {
 				applyResultSetMapping( sqlResultSetMapping );
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index 8cc6667472..677df04ed4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,354 +1,354 @@
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
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.sql.Connection;
 import java.sql.Timestamp;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Version;
 import org.hibernate.bytecode.spi.BytecodeProvider;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 
 /**
  * Provides access to configuration info passed in <tt>Properties</tt> objects.
  * <br><br>
  * Hibernate has two property scopes:
  * <ul>
  * <li><b>Factory-level</b> properties may be passed to the <tt>SessionFactory</tt> when it
  * instantiated. Each instance might have different property values. If no
  * properties are specified, the factory calls <tt>Environment.getProperties()</tt>.
  * <li><b>System-level</b> properties are shared by all factory instances and are always
  * determined by the <tt>Environment</tt> properties.
  * </ul>
  * The only system-level properties are
  * <ul>
  * <li><tt>hibernate.jdbc.use_streams_for_binary</tt>
  * <li><tt>hibernate.cglib.use_reflection_optimizer</tt>
  * </ul>
  * <tt>Environment</tt> properties are populated by calling <tt>System.getProperties()</tt>
  * and then from a resource named <tt>/hibernate.properties</tt> if it exists. System
  * properties override properties specified in <tt>hibernate.properties</tt>.<br>
  * <br>
  * The <tt>SessionFactory</tt> is controlled by the following properties.
  * Properties may be either be <tt>System</tt> properties, properties
  * defined in a resource named <tt>/hibernate.properties</tt> or an instance of
  * <tt>java.util.Properties</tt> passed to
- * <tt>Configuration.buildSessionFactory()</tt><br>
+ * <tt>Configuration.build()</tt><br>
  * <br>
  * <table>
  * <tr><td><b>property</b></td><td><b>meaning</b></td></tr>
  * <tr>
  *   <td><tt>hibernate.dialect</tt></td>
  *   <td>classname of <tt>org.hibernate.dialect.Dialect</tt> subclass</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.provider_class</tt></td>
  *   <td>classname of <tt>ConnectionProvider</tt>
  *   subclass (if not specified hueristics are used)</td>
  * </tr>
  * <tr><td><tt>hibernate.connection.username</tt></td><td>database username</td></tr>
  * <tr><td><tt>hibernate.connection.password</tt></td><td>database password</td></tr>
  * <tr>
  *   <td><tt>hibernate.connection.url</tt></td>
  *   <td>JDBC URL (when using <tt>java.sql.DriverManager</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.driver_class</tt></td>
  *   <td>classname of JDBC driver</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.isolation</tt></td>
  *   <td>JDBC transaction isolation level (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  *   <td><tt>hibernate.connection.pool_size</tt></td>
  *   <td>the maximum size of the connection pool (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.datasource</tt></td>
  *   <td>databasource JNDI name (when using <tt>javax.sql.Datasource</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.url</tt></td><td>JNDI <tt>InitialContext</tt> URL</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.class</tt></td><td>JNDI <tt>InitialContext</tt> classname</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.max_fetch_depth</tt></td>
  *   <td>maximum depth of outer join fetching</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.batch_size</tt></td>
  *   <td>enable use of JDBC2 batch API for drivers which support it</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.fetch_size</tt></td>
  *   <td>set the JDBC fetch size</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_scrollable_resultset</tt></td>
  *   <td>enable use of JDBC2 scrollable resultsets (you only need this specify
  *   this property when using user supplied connections)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_getGeneratedKeys</tt></td>
  *   <td>enable use of JDBC3 PreparedStatement.getGeneratedKeys() to retrieve
  *   natively generated keys after insert. Requires JDBC3+ driver and JRE1.4+</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.hbm2ddl.auto</tt></td>
  *   <td>enable auto DDL export</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_schema</tt></td>
  *   <td>use given schema name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_catalog</tt></td>
  *   <td>use given catalog name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.session_factory_name</tt></td>
  *   <td>If set, the factory attempts to bind this name to itself in the
  *   JNDI context. This name is also used to support cross JVM <tt>
  *   Session</tt> (de)serialization.</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.manager_lookup_class</tt></td>
  *   <td>classname of <tt>org.hibernate.transaction.TransactionManagerLookup</tt>
  *   implementor</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.factory_class</tt></td>
  *   <td>the factory to use for instantiating <tt>Transaction</tt>s.
  *   (Defaults to <tt>JdbcTransactionFactory</tt>.)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.query.substitutions</tt></td><td>query language token substitutions</td>
  * </tr>
  * </table>
  *
  * @see org.hibernate.SessionFactory
  * @author Gavin King
  */
 public final class Environment implements AvailableSettings {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Environment.class.getName());
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final Map<Integer,String> ISOLATION_LEVELS;
 
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
 	/**
 	 * Issues warnings to the user when any obsolete or renamed property names are used.
 	 *
 	 * @param configurationValues The specified properties.
 	 */
 	public static void verifyProperties(Map<?,?> configurationValues) {
 		final Map propertiesToAdd = new HashMap();
 		for ( Map.Entry entry : configurationValues.entrySet() ) {
 			final Object replacementKey = OBSOLETE_PROPERTIES.get( entry.getKey() );
 			if ( replacementKey != null ) {
 				LOG.unsupportedProperty( entry.getKey(), replacementKey );
 			}
 			final Object renamedKey = RENAMED_PROPERTIES.get( entry.getKey() );
 			if ( renamedKey != null ) {
 				LOG.renamedProperty( entry.getKey(), renamedKey );
 				propertiesToAdd.put( renamedKey, entry.getValue() );
 			}
 		}
 		configurationValues.putAll( propertiesToAdd );
 	}
 
 	static {
 		Version.logVersion();
 
 		Map<Integer,String> temp = new HashMap<Integer,String>();
 		temp.put( Connection.TRANSACTION_NONE, "NONE" );
 		temp.put( Connection.TRANSACTION_READ_UNCOMMITTED, "READ_UNCOMMITTED" );
 		temp.put( Connection.TRANSACTION_READ_COMMITTED, "READ_COMMITTED" );
 		temp.put( Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE_READ" );
 		temp.put( Connection.TRANSACTION_SERIALIZABLE, "SERIALIZABLE" );
 		ISOLATION_LEVELS = Collections.unmodifiableMap( temp );
 		GLOBAL_PROPERTIES = new Properties();
 		//Set USE_REFLECTION_OPTIMIZER to false to fix HHH-227
 		GLOBAL_PROPERTIES.setProperty( USE_REFLECTION_OPTIMIZER, Boolean.FALSE.toString() );
 
 		try {
 			InputStream stream = ConfigHelper.getResourceAsStream( "/hibernate.properties" );
 			try {
 				GLOBAL_PROPERTIES.load(stream);
 				LOG.propertiesLoaded( ConfigurationHelper.maskOut( GLOBAL_PROPERTIES, PASS ) );
 			}
 			catch (Exception e) {
 				LOG.unableToLoadProperties();
 			}
 			finally {
 				try{
 					stream.close();
 				}
 				catch (IOException ioe){
 					LOG.unableToCloseStreamError( ioe );
 				}
 			}
 		}
 		catch (HibernateException he) {
 			LOG.propertiesNotFound();
 		}
 
 		try {
 			GLOBAL_PROPERTIES.putAll( System.getProperties() );
 		}
 		catch (SecurityException se) {
 			LOG.unableToCopySystemProperties();
 		}
 
 		verifyProperties(GLOBAL_PROPERTIES);
 
 		ENABLE_BINARY_STREAMS = ConfigurationHelper.getBoolean(USE_STREAMS_FOR_BINARY, GLOBAL_PROPERTIES);
 		if ( ENABLE_BINARY_STREAMS ) {
 			LOG.usingStreams();
 		}
 
 		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
 		if ( ENABLE_REFLECTION_OPTIMIZER ) {
 			LOG.usingReflectionOptimizer();
 		}
 
 		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
 		if ( JVM_HAS_TIMESTAMP_BUG ) {
 			LOG.usingTimestampWorkaround();
 		}
 	}
 
 	public static BytecodeProvider getBytecodeProvider() {
 		return BYTECODE_PROVIDER_INSTANCE;
 	}
 
 	/**
 	 * Does this JVM's implementation of {@link java.sql.Timestamp} have a bug in which the following is true:<code>
 	 * new java.sql.Timestamp( x ).getTime() != x
 	 * </code>
 	 * <p/>
 	 * NOTE : IBM JDK 1.3.1 the only known JVM to exhibit this behavior.
 	 *
 	 * @return True if the JVM's {@link Timestamp} implementa
 	 */
 	public static boolean jvmHasTimestampBug() {
 		return JVM_HAS_TIMESTAMP_BUG;
 	}
 
 	/**
 	 * Should we use streams to bind binary types to JDBC IN parameters?
 	 *
 	 * @return True if streams should be used for binary data handling; false otherwise.
 	 *
 	 * @see #USE_STREAMS_FOR_BINARY
 	 */
 	public static boolean useStreamsForBinary() {
 		return ENABLE_BINARY_STREAMS;
 	}
 
 	/**
 	 * Should we use reflection optimization?
 	 *
 	 * @return True if reflection optimization should be used; false otherwise.
 	 *
 	 * @see #USE_REFLECTION_OPTIMIZER
 	 * @see #getBytecodeProvider()
 	 * @see BytecodeProvider#getReflectionOptimizer
 	 */
 	public static boolean useReflectionOptimizer() {
 		return ENABLE_REFLECTION_OPTIMIZER;
 	}
 
 	/**
 	 * Disallow instantiation
 	 */
 	private Environment() {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Return <tt>System</tt> properties, extended by any properties specified
 	 * in <tt>hibernate.properties</tt>.
 	 * @return Properties
 	 */
 	public static Properties getProperties() {
 		Properties copy = new Properties();
 		copy.putAll(GLOBAL_PROPERTIES);
 		return copy;
 	}
 
 	/**
 	 * Get the name of a JDBC transaction isolation level
 	 *
 	 * @see java.sql.Connection
 	 * @param isolation as defined by <tt>java.sql.Connection</tt>
 	 * @return a human-readable name
 	 */
 	public static String isolationLevelToString(int isolation) {
 		return ISOLATION_LEVELS.get( isolation );
 	}
 
 	public static BytecodeProvider buildBytecodeProvider(Properties properties) {
 		String provider = ConfigurationHelper.getString( BYTECODE_PROVIDER, properties, "javassist" );
 		LOG.bytecodeProvider( provider );
 		return buildBytecodeProvider( provider );
 	}
 
 	private static BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
 			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 		}
 
 		LOG.unknownBytecodeProvider( providerName );
 		return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 624552cacb..3d0a9e6db3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -2043,1177 +2043,1177 @@ public final class HbmBinder {
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setName("_UniqueKey");
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				final Property property = createProperty(
 						value,
 						propertyName,
 						persistentClass.getClassName(),
 						subnode,
 						mappings,
 						inheritedMetas
 				);
 				if ( !mutable ) {
 					property.setUpdateable(false);
 				}
 				if ( naturalId ) {
 					property.setNaturalIdentifier( true );
 				}
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) {
 					uniqueKey.addColumns( property.getColumnIterator() );
 				}
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
 		);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + list.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( list.getRole() );
 			ib.setEntityName( list.getOwner().getEntityName() );
 			ib.setValue( list.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	public static void bindIdentifierCollectionSecondPass(Element node,
 			IdentifierCollection collection, java.util.Map persistentClasses, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, collection, persistentClasses, mappings, inheritedMetas );
 
 		Element subnode = node.element( "collection-id" );
 		SimpleValue id = new SimpleValue( mappings, collection.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				id,
 				false,
 				IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME,
 				mappings
 			);
 		collection.setIdentifier( id );
 		makeIdentifier( subnode, id, mappings );
 
 	}
 
 	/**
 	 * Called for Maps
 	 */
 	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			Iterator aliasesIterator = filterElement.elementIterator("aliases");
 			java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 			while (aliasesIterator.hasNext()){
 				Element alias = (Element) aliasesIterator.next();
 				aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 			}
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
 			String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 			boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 			collection.addManyToManyFilter(name, condition, autoAliasInjection, aliasTables, null);
 		}
 	}
 
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
 		LOG.debugf( "Named query: %s -> %s", queryName, query );
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinitionBuilder().setName( queryName )
 				.setQuery( query )
 				.setCacheable( cacheable )
 				.setCacheRegion( region )
 				.setTimeout( timeout )
 				.setFetchSize( fetchSize )
 				.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
 				.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
 				.setReadOnly( readOnly )
 				.setComment( comment )
 				.setParameterTypes( getParameterTypes( queryElem ) )
 				.createNamedQueryDefinition();
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
 	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
 
 		if ( olAtt == null ) return Versioning.OPTIMISTIC_LOCK_VERSION;
 		String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
 			if ( onlyInheritable & !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
 		LOG.debugf( "Parsing filter-def [%s]", name );
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
 			LOG.debugf( "Adding filter parameter : %s -> %s", paramName, paramType );
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
 			LOG.debugf( "Parameter heuristic type : %s", heuristicType );
 			paramMappings.put( paramName, heuristicType );
 		}
 		LOG.debugf( "Parsed filter-def [%s]", name );
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
-		//      map directly) sometime during Configuration.buildSessionFactory
+		//      map directly) sometime during Configuration.build
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		Iterator aliasesIterator = filterElement.elementIterator("aliases");
 		java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 		while (aliasesIterator.hasNext()){
 			Element alias = (Element) aliasesIterator.next();
 			aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
 		String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 		boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 		filterable.addFilter(name, condition, autoAliasInjection, aliasTables, null);
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 	
 	private static class ResolveUserTypeMappingSecondPass implements SecondPass{
 
 		private SimpleValue simpleValue;
 		private String typeName;
 		private Mappings mappings;
 		private Properties parameters;
 
 		public ResolveUserTypeMappingSecondPass(SimpleValue simpleValue,
 				String typeName, Mappings mappings, Properties parameters) {
 			this.simpleValue=simpleValue;
 			this.typeName=typeName;
 			this.parameters=parameters;
 			this.mappings=mappings;
 		}
 
 		@Override
 		public void doSecondPass(java.util.Map persistentClasses)
 				throws MappingException {
 			resolveAndBindTypeDef(simpleValue, mappings, typeName, parameters);		
 		}
 		
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 88d7c1ff5d..1de346d488 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1669 +1,1675 @@
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
 
-import javax.naming.Reference;
-import javax.naming.StringRefAddr;
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
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
-import org.jboss.logging.Logger;
+import javax.naming.Reference;
+import javax.naming.StringRefAddr;
 
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
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
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
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
+import org.hibernate.engine.config.spi.ConfigurationService;
+import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
+import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
-import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
-import org.hibernate.engine.config.spi.ConfigurationService;
-import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.engine.jndi.spi.JndiService;
-import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
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
+import org.jboss.logging.Logger;
 
 
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
 public final class SessionFactoryImpl
 		implements SessionFactoryImplementor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map<String,CollectionPersister> collectionPersisters;
 	private final transient Map<String,CollectionMetadata> collectionMetadata;
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
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient CacheImplementor cacheAccess;
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 	private final transient CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 	private final transient CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 
 	@SuppressWarnings( {"unchecked", "ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			final Configuration cfg,
 			Mapping mapping,
-			ServiceRegistry serviceRegistry,
+			final ServiceRegistry serviceRegistry,
 			Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
 			LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
+			public StandardServiceRegistry getServiceRegistry() {
+				return (StandardServiceRegistry) serviceRegistry;
+			}
+
+			@Override
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
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 
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
 
 		final PersisterFactory persisterFactory = serviceRegistry.getService( PersisterFactory.class );
 
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
 					LOG.tracef( "Building shared cache region for entity data [%s]", model.getEntityName() );
 					EntityRegion entityRegion = regionFactory.buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 				}
 			}
 			
 			NaturalIdRegionAccessStrategy naturalIdAccessStrategy = null;
 			if ( model.hasNaturalId() && model.getNaturalIdCacheRegionName() != null ) {
 				final String naturalIdCacheRegionName = cacheRegionPrefix + model.getNaturalIdCacheRegionName();
 				naturalIdAccessStrategy = ( NaturalIdRegionAccessStrategy ) entityAccessStrategies.get( naturalIdCacheRegionName );
 				
 				if ( naturalIdAccessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 					final CacheDataDescriptionImpl cacheDataDescription = CacheDataDescriptionImpl.decode( model );
 					
 					NaturalIdRegion naturalIdRegion = null;
 					try {
 						naturalIdRegion = regionFactory.buildNaturalIdRegion( naturalIdCacheRegionName, properties,
 								cacheDataDescription );
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
 						entityAccessStrategies.put( naturalIdCacheRegionName, naturalIdAccessStrategy );
 						cacheAccess.addCacheRegion(  naturalIdCacheRegionName, naturalIdRegion );
 					}
 				}
 			}
 			
 			EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					this,
 					mapping
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				LOG.tracev( "Building shared cache region for collection data [{0}]", model.getRole() );
 				CollectionRegion collectionRegion = regionFactory.buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = persisterFactory.createCollectionPersister(
 					cfg,
 					model,
 					accessStrategy,
 					this
 			) ;
 			collectionPersisters.put( model.getRole(), persister );
 			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
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
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
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
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		LOG.debug( "Instantiated session factory" );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				cfg.createMappings(),
 				cfg.buildMapping(),
 				properties
 		);
 
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg )
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
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
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
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( cfg.getCurrentTenantIdentifierResolver() );
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
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
 
 	@SuppressWarnings({ "unchecked" })
 	private CustomEntityDirtinessStrategy determineCustomEntityDirtinessStrategy() {
 		CustomEntityDirtinessStrategy defaultValue = new CustomEntityDirtinessStrategy() {
 			@Override
 			public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public boolean isDirty(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public void resetDirty(Object entity, EntityPersister persister, Session session) {
 			}
 
 			@Override
 			public void findDirty(
 					Object entity,
 					EntityPersister persister,
 					Session session,
 					DirtyCheckContext dirtyCheckContext) {
 				// todo : implement proper method body
 			}
 		};
 		return serviceRegistry.getService( ConfigurationService.class ).getSetting(
 				AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY,
 				CustomEntityDirtinessStrategy.class,
 				defaultValue
 		);
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private CurrentTenantIdentifierResolver determineCurrentTenantIdentifierResolver(
 			CurrentTenantIdentifierResolver explicitResolver) {
 		if ( explicitResolver != null ) {
 			return explicitResolver;
 		}
 		return serviceRegistry.getService( ConfigurationService.class )
 				.getSetting(
 						AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
 						CurrentTenantIdentifierResolver.class,
 						null
 				);
 
 	}
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
 		LOG.debug( "Building session factory" );
 
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
-				metadata.getServiceRegistry()
+				sessionFactoryOptions.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 
 		// TODO: get SQL functions from JdbcServices (HHH-6559)
 		//this.sqlFunctionRegistry = new SQLFunctionRegistry( this.jdbcServices.getSqlFunctions() );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( this.dialect, new HashMap<String, SQLFunction>() );
 
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
 			}
 		}
 
         final IntegratorObserver integratorObserver = new IntegratorObserver();
         this.observer.addObserver(integratorObserver);
         for (Integrator integrator : serviceRegistry.getService(IntegratorService.class).getIntegrators()) {
             integrator.integrate(metadata, this, this.serviceRegistry);
             integratorObserver.integrators.add(integrator);
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
 
 		entityPersisters = new HashMap<String,EntityPersister>();
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
 					if ( LOG.isTraceEnabled() ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntity().getName() );
 					}
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion(
 							cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 					);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
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
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String, CollectionMetadata> tmpCollectionMetadata = new HashMap<String, CollectionMetadata>();
 		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a AbstractPluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"AbstractPluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			final String cacheRegionName = cacheRegionPrefix + model.getCaching().getRegion();
 			final AccessType accessType = model.getCaching().getAccessType();
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Building cache for collection data [{0}]", model.getAttribute().getRole() );
 				}
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion(
 						cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion(  cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry
 					.getService( PersisterFactory.class )
 					.createCollectionPersister( metadata, model, accessStrategy, this );
 			collectionPersisters.put( model.getAttribute().getRole(), persister );
 			tmpCollectionMetadata.put( model.getAttribute().getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 		for ( Map.Entry<String, Set<String>> entry : tmpEntityToCollectionRoleMap.entrySet() ) {
 			entry.setValue( Collections.unmodifiableSet( entry.getValue() ) );
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
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid, 
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		LOG.debug("Instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( metadata )
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
 					failingQueries.append( entry.getKey() ).append( sep );
 					sep = ", ";
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
 				final EntityPersister owner = entityName == null ? null : entityPersisters.get( entityName );
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
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( null );
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
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		Map<String,HibernateException> errors = new HashMap<String,HibernateException>();
 
 		// Check named HQL queries
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named HQL queries", namedQueries.size() );
 		}
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named query: %s", queryName );
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, Collections.EMPTY_MAP );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 
 
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named SQL queries", namedSqlQueries.size() );
 		}
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named SQL query: %s", queryName );
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( qd.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = sqlResultSetMappings.get( qd.getResultSetRef() );
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
 		EntityPersister result = entityPersisters.get(entityName);
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = collectionPersisters.get(role);
 		if ( result == null ) {
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
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		if ( NamedSQLQueryDefinition.class.isInstance( definition ) ) {
 			throw new IllegalArgumentException( "NamedSQLQueryDefinition instance incorrectly passed to registerNamedQueryDefinition" );
 		}
 		final NamedQueryDefinition previous = namedQueries.put( name, definition );
 		if ( previous != null ) {
 			LOG.debugf(
 					"registering named query definition [%s] overriding previously registered definition [%s]",
 					name,
 					previous
 			);
 		}
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueries.get( queryName );
 	}
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		final NamedSQLQueryDefinition previous = namedSqlQueries.put( name, definition );
 		if ( previous != null ) {
 			LOG.debugf(
 					"registering named SQL query definition [%s] overriding previously registered definition [%s]",
 					name,
 					previous
 			);
 		}
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedSqlQueries.get( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return sqlResultSetMappings.get( resultSetName );
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return collectionMetadata.get(roleName);
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
 
 	private TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( TransactionFactory.class );
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
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					sessionOwner,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
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
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
index 0313e266ce..ce58902d7d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
@@ -1,97 +1,100 @@
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
+import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 
 /**
  * @author Steve Ebersole
  */
 public interface Metadata {
 	/**
 	 * Exposes the options used to produce a {@link Metadata} instance.
 	 */
 	public static interface Options {
+		public StandardServiceRegistry getServiceRegistry();
+
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
 
 	public Iterable<PluralAttributeBinding> getCollectionBindings();
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java
index d8d9351afc..1295a80923 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java
@@ -1,47 +1,47 @@
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
 
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 
 /**
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public interface MetadataBuilder {
 	public MetadataBuilder with(NamingStrategy namingStrategy);
 
 	public MetadataBuilder with(MetadataSourceProcessingOrder metadataSourceProcessingOrder);
 
 	public MetadataBuilder with(SharedCacheMode cacheMode);
 
 	public MetadataBuilder with(AccessType accessType);
 
 	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled);
 
-	public Metadata buildMetadata();
+	public Metadata build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
index ba0b78e69c..c8a3db5ccd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
@@ -1,382 +1,423 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
-import org.jboss.logging.Logger;
-import org.w3c.dom.Document;
-import org.xml.sax.EntityResolver;
-
+import org.hibernate.boot.registry.BootstrapServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.cfg.EJB3DTDEntityResolver;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.jaxb.JaxbRoot;
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.jaxb.SourceType;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.MappingNotFoundException;
 import org.hibernate.metamodel.source.internal.JaxbHelper;
 import org.hibernate.metamodel.source.internal.MetadataBuilderImpl;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.jboss.logging.Logger;
+import org.w3c.dom.Document;
+import org.xml.sax.EntityResolver;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataSources {
 	private static final Logger LOG = Logger.getLogger( MetadataSources.class );
 
+	private final ServiceRegistry serviceRegistry;
+	
 	private List<JaxbRoot> jaxbRootList = new ArrayList<JaxbRoot>();
 	private LinkedHashSet<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();
 	private LinkedHashSet<String> annotatedPackages = new LinkedHashSet<String>();
 
 	private final JaxbHelper jaxbHelper;
 
-	private final ServiceRegistry serviceRegistry;
 	private final EntityResolver entityResolver;
 	private final NamingStrategy namingStrategy;
 
 	private final MetadataBuilderImpl metadataBuilder;
 
 	public MetadataSources(ServiceRegistry serviceRegistry) {
 		this( serviceRegistry, EJB3DTDEntityResolver.INSTANCE, EJB3NamingStrategy.INSTANCE );
 	}
 
 	public MetadataSources(ServiceRegistry serviceRegistry, EntityResolver entityResolver, NamingStrategy namingStrategy) {
 		this.serviceRegistry = serviceRegistry;
 		this.entityResolver = entityResolver;
 		this.namingStrategy = namingStrategy;
 
 		this.jaxbHelper = new JaxbHelper( this );
 		this.metadataBuilder = new MetadataBuilderImpl( this );
+		
+		// service registry really should be either BootstrapServiceRegistry or StandardServiceRegistry type...
+		if ( ! isExpectedServiceRegistryType( serviceRegistry ) ) {
+			LOG.debugf(
+					"Unexpected ServiceRegistry type [%s] encountered during building of MetadataSources; may cause " +
+							"problems later attempting to construct MetadataBuilder",
+					serviceRegistry.getClass().getName()
+			);
+		}
+	}
+
+	protected static boolean isExpectedServiceRegistryType(ServiceRegistry serviceRegistry) {
+		return BootstrapServiceRegistry.class.isInstance( serviceRegistry )
+				|| StandardServiceRegistry.class.isInstance( serviceRegistry );
 	}
 
 	public List<JaxbRoot> getJaxbRootList() {
 		return jaxbRootList;
 	}
 
 	public Iterable<String> getAnnotatedPackages() {
 		return annotatedPackages;
 	}
 
 	public Iterable<Class<?>> getAnnotatedClasses() {
 		return annotatedClasses;
 	}
 
 	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
+	/**
+	 * Get a builder for metadata where non-default options can be specified.
+	 *
+	 * @return The built metadata.
+	 */
 	public MetadataBuilder getMetadataBuilder() {
-		return metadataBuilder;
+		return new MetadataBuilderImpl( this );
 	}
 
+	/**
+	 * Get a builder for metadata where non-default options can be specified.
+	 *
+	 * @return The built metadata.
+	 */
+	public MetadataBuilder getMetadataBuilder(StandardServiceRegistry serviceRegistry) {
+		return new MetadataBuilderImpl( this, serviceRegistry );
+	}
+
+	/**
+	 * Short-hand form of calling {@link #getMetadataBuilder()} and using its
+	 * {@link org.hibernate.metamodel.MetadataBuilder#build()} method in cases where the application wants
+	 * to accept the defaults.
+	 *
+	 * @return The built metadata.
+	 */
 	public Metadata buildMetadata() {
-		return getMetadataBuilder().buildMetadata();
+		return getMetadataBuilder().build();
+	}
+
+	public Metadata buildMetadata(StandardServiceRegistry serviceRegistry) {
+		return getMetadataBuilder( serviceRegistry ).build();
 	}
 
 	/**
 	 * Read metadata from the annotations attached to the given class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addAnnotatedClass(Class annotatedClass) {
 		annotatedClasses.add( annotatedClass );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
 	 * @param packageName java package name without trailing '.', cannot be {@code null}
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addPackage(String packageName) {
 		if ( packageName == null ) {
 			throw new IllegalArgumentException( "The specified package name cannot be null" );
 		}
 		if ( packageName.endsWith( "." ) ) {
 			packageName = packageName.substring( 0, packageName.length() - 1 );
 		}
 		annotatedPackages.add( packageName );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup).
 	 *
 	 * @param name The resource name
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addResource(String name) {
 		LOG.tracef( "reading mappings from resource : %s", name );
 
 		final Origin origin = new Origin( SourceType.RESOURCE, name );
 		InputStream resourceInputStream = classLoaderService().locateResourceStream( name );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( origin );
 		}
 		add( resourceInputStream, origin, true );
 
 		return this;
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	private JaxbRoot add(InputStream inputStream, Origin origin, boolean close) {
 		try {
 			JaxbRoot jaxbRoot = jaxbHelper.unmarshal( inputStream, origin );
 			jaxbRootList.add( jaxbRoot );
 			return jaxbRoot;
 		}
 		finally {
 			if ( close ) {
 				try {
 					inputStream.close();
 				}
 				catch ( IOException ignore ) {
 					LOG.trace( "Was unable to close input stream" );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class named {@code foo.bar.Foo} is
 	 * mapped by a file named {@code foo/bar/Foo.hbm.xml} which can be resolved as a classpath resource.
 	 *
 	 * @param entityClass The mapped class. Cannot be {@code null} null.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addClass(Class entityClass) {
 		if ( entityClass == null ) {
 			throw new IllegalArgumentException( "The specified class cannot be null" );
 		}
 		LOG.debugf( "adding resource mappings from class convention : %s", entityClass.getName() );
 		final String mappingResourceName = entityClass.getName().replace( '.', '/' ) + ".hbm.xml";
 		addResource( mappingResourceName );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addFile(java.io.File)
 	 */
 	public MetadataSources addFile(String path) {
 		return addFile( new File( path ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param file The reference to the XML file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addFile(File file) {
 		final String name = file.getAbsolutePath();
 		LOG.tracef( "reading mappings from file : %s", name );
 		final Origin origin = new Origin( SourceType.FILE, name );
 		try {
 			add( new FileInputStream( file ), origin, true );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * See {@link #addCacheableFile(java.io.File)} for description
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public MetadataSources addCacheableFile(String path) {
 		return this; // todo : implement method body
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation of the DOM structure of a
 	 * particular mapping.  It is saved from a previous call as a file with the name {@code {xmlFile}.bin}
 	 * where {@code {xmlFile}} is the name of the original mapping file.
 	 * </p>
 	 * If a cached {@code {xmlFile}.bin} exists and is newer than {@code {xmlFile}}, the {@code {xmlFile}.bin}
 	 * file will be read directly. Otherwise {@code {xmlFile}} is read and then serialized to {@code {xmlFile}.bin} for
 	 * use the next time.
 	 *
 	 * @param file The cacheable mapping file to be added, {@code {xmlFile}} in above discussion.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addCacheableFile(File file) {
 		return this; // todo : implement method body
 	}
 
 	/**
 	 * Read metadata from an {@link InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addInputStream(InputStream xmlInputStream) {
 		add( xmlInputStream, new Origin( SourceType.INPUT_STREAM, "<unknown>" ), false );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a {@link URL}
 	 *
 	 * @param url The url for the mapping document to be read.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addURL(URL url) {
 		final String urlExternalForm = url.toExternalForm();
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		final Origin origin = new Origin( SourceType.URL, urlExternalForm );
 		try {
 			add( url.openStream(), origin, true );
 		}
 		catch ( IOException e ) {
 			throw new MappingNotFoundException( "Unable to open url stream [" + urlExternalForm + "]", e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * Read mappings from a DOM {@link Document}
 	 *
 	 * @param document The DOM document
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addDocument(Document document) {
 		final Origin origin = new Origin( SourceType.DOM, "<unknown>" );
 		JaxbRoot jaxbRoot = jaxbHelper.unmarshal( document, origin );
 		jaxbRootList.add( jaxbRoot );
 		return this;
 	}
 
 	/**
 	 * Read all mappings from a jar file.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addJar(File jar) {
 		LOG.debugf( "Seeking mapping documents in jar file : %s", jar.getName() );
 		final Origin origin = new Origin( SourceType.JAR, jar.getAbsolutePath() );
 		try {
 			JarFile jarFile = new JarFile( jar );
 			try {
 				Enumeration jarEntries = jarFile.entries();
 				while ( jarEntries.hasMoreElements() ) {
 					final ZipEntry zipEntry = (ZipEntry) jarEntries.nextElement();
 					if ( zipEntry.getName().endsWith( ".hbm.xml" ) ) {
 						LOG.tracef( "found mapping document : %s", zipEntry.getName() );
 						try {
 							add( jarFile.getInputStream( zipEntry ), origin, true );
 						}
 						catch ( Exception e ) {
 							throw new MappingException( "could not read mapping documents", e, origin );
 						}
 					}
 				}
 			}
 			finally {
 				try {
 					jarFile.close();
 				}
 				catch ( Exception ignore ) {
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw new MappingNotFoundException( e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public MetadataSources addDirectory(File dir) {
 		File[] files = dir.listFiles();
 		for ( File file : files ) {
 			if ( file.isDirectory() ) {
 				addDirectory( file );
 			}
 			else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 				addFile( file );
 			}
 		}
 		return this;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/SessionFactoryBuilder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/SessionFactoryBuilder.java
index c97ede7191..c3cb204e36 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/SessionFactoryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/SessionFactoryBuilder.java
@@ -1,39 +1,44 @@
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
 
 import org.hibernate.Interceptor;
 import org.hibernate.SessionFactory;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 
 /**
  * @author Gail Badner
  */
 public interface SessionFactoryBuilder {
 	public SessionFactoryBuilder with(Interceptor interceptor);
 
 	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate);
 
-	public SessionFactory buildSessionFactory();
+	/**
+	 * After all options have been set, build the SessionFactory.
+	 *
+	 * @return The built SessionFactory.
+	 */
+	public SessionFactory build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbHelper.java
index f1fdb0bf6d..1e56b113d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbHelper.java
@@ -1,313 +1,313 @@
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
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
+
 import javax.xml.XMLConstants;
 import javax.xml.bind.JAXBContext;
 import javax.xml.bind.JAXBException;
 import javax.xml.bind.Unmarshaller;
 import javax.xml.bind.ValidationEvent;
 import javax.xml.bind.ValidationEventHandler;
 import javax.xml.bind.ValidationEventLocator;
 import javax.xml.namespace.QName;
 import javax.xml.stream.XMLEventReader;
 import javax.xml.stream.XMLInputFactory;
 import javax.xml.stream.XMLStreamException;
 import javax.xml.stream.events.Attribute;
 import javax.xml.stream.events.XMLEvent;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.stream.StreamSource;
 import javax.xml.validation.Schema;
 import javax.xml.validation.SchemaFactory;
 
-import org.jboss.logging.Logger;
-import org.w3c.dom.Document;
-import org.w3c.dom.Element;
-import org.xml.sax.SAXException;
-
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.internal.jaxb.JaxbRoot;
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbHibernateMapping;
 import org.hibernate.internal.jaxb.mapping.orm.JaxbEntityMappings;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.XsdException;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.jboss.logging.Logger;
+import org.w3c.dom.Document;
+import org.w3c.dom.Element;
+import org.xml.sax.SAXException;
 
 /**
  * Helper class for unmarshalling xml configuration using StAX and JAXB.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class JaxbHelper {
 	private static final Logger log = Logger.getLogger( JaxbHelper.class );
 
 	public static final String ASSUMED_ORM_XSD_VERSION = "2.0";
 
 	private final MetadataSources metadataSources;
 
 	public JaxbHelper(MetadataSources metadataSources) {
 		this.metadataSources = metadataSources;
 	}
 
 	public JaxbRoot unmarshal(InputStream stream, Origin origin) {
 		try {
 			XMLEventReader staxReader = staxFactory().createXMLEventReader( stream );
 			try {
 				return unmarshal( staxReader, origin );
 			}
 			finally {
 				try {
 					staxReader.close();
 				}
 				catch ( Exception ignore ) {
 				}
 			}
 		}
 		catch ( XMLStreamException e ) {
 			throw new MappingException( "Unable to create stax reader", e, origin );
 		}
 	}
 
 	private XMLInputFactory staxFactory;
 
 	private XMLInputFactory staxFactory() {
 		if ( staxFactory == null ) {
 			staxFactory = buildStaxFactory();
 		}
 		return staxFactory;
 	}
 
 	@SuppressWarnings( { "UnnecessaryLocalVariable" })
 	private XMLInputFactory buildStaxFactory() {
 		XMLInputFactory staxFactory = XMLInputFactory.newInstance();
 		return staxFactory;
 	}
 
 	private static final QName ORM_VERSION_ATTRIBUTE_QNAME = new QName( "version" );
 
 	@SuppressWarnings( { "unchecked" })
 	private JaxbRoot unmarshal(XMLEventReader staxEventReader, final Origin origin) {
 		XMLEvent event;
 		try {
 			event = staxEventReader.peek();
 			while ( event != null && !event.isStartElement() ) {
 				staxEventReader.nextEvent();
 				event = staxEventReader.peek();
 			}
 		}
 		catch ( Exception e ) {
 			throw new MappingException( "Error accessing stax stream", e, origin );
 		}
 
 		if ( event == null ) {
 			throw new MappingException( "Could not locate root element", origin );
 		}
 
 		final Schema validationSchema;
 		final Class jaxbTarget;
 
 		final String elementName = event.asStartElement().getName().getLocalPart();
 
 		if ( "entity-mappings".equals( elementName ) ) {
 			final Attribute attribute = event.asStartElement().getAttributeByName( ORM_VERSION_ATTRIBUTE_QNAME );
 			final String explicitVersion = attribute == null ? null : attribute.getValue();
 			validationSchema = resolveSupportedOrmXsd( explicitVersion );
 			jaxbTarget = JaxbEntityMappings.class;
 		}
 		else {
 			validationSchema = hbmSchema();
 			jaxbTarget = JaxbHibernateMapping.class;
 		}
 
 		final Object target;
 		final ContextProvidingValidationEventHandler handler = new ContextProvidingValidationEventHandler();
 		try {
 			JAXBContext jaxbContext = JAXBContext.newInstance( jaxbTarget );
 			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 			unmarshaller.setSchema( validationSchema );
 			unmarshaller.setEventHandler( handler );
 			target = unmarshaller.unmarshal( staxEventReader );
 		}
 
 		catch ( JAXBException e ) {
 			StringBuilder builder = new StringBuilder();
 			builder.append( "Unable to perform unmarshalling at line number " );
 			builder.append( handler.getLineNumber() );
 			builder.append( " and column " );
 			builder.append( handler.getColumnNumber() );
 			builder.append( ". Message: " );
 			builder.append( handler.getMessage() );
 			throw new MappingException( builder.toString(), e, origin );
 		}
 
 		return new JaxbRoot( target, origin );
 	}
 
 	@SuppressWarnings( { "unchecked" })
 	public JaxbRoot unmarshal(Document document, Origin origin) {
 		Element rootElement = document.getDocumentElement();
 		if ( rootElement == null ) {
 			throw new MappingException( "No root element found", origin );
 		}
 
 		final Schema validationSchema;
 		final Class jaxbTarget;
 
 		if ( "entity-mappings".equals( rootElement.getNodeName() ) ) {
 			final String explicitVersion = rootElement.getAttribute( "version" );
 			validationSchema = resolveSupportedOrmXsd( explicitVersion );
 			jaxbTarget = JaxbEntityMappings.class;
 		}
 		else {
 			validationSchema = hbmSchema();
 			jaxbTarget = JaxbHibernateMapping.class;
 		}
 
 		final Object target;
 		try {
 			JAXBContext jaxbContext = JAXBContext.newInstance( jaxbTarget );
 			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 			unmarshaller.setSchema( validationSchema );
 			target = unmarshaller.unmarshal( new DOMSource( document ) );
 		}
 		catch ( JAXBException e ) {
 			throw new MappingException( "Unable to perform unmarshalling", e, origin );
 		}
 
 		return new JaxbRoot( target, origin );
 	}
 
 	private Schema resolveSupportedOrmXsd(String explicitVersion) {
 		final String xsdVersionString = explicitVersion == null ? ASSUMED_ORM_XSD_VERSION : explicitVersion;
 		if ( "1.0".equals( xsdVersionString ) ) {
 			return orm1Schema();
 		}
 		else if ( "2.0".equals( xsdVersionString ) ) {
 			return orm2Schema();
 		}
 		throw new IllegalArgumentException( "Unsupported orm.xml XSD version encountered [" + xsdVersionString + "]" );
 	}
 
 	public static final String HBM_SCHEMA_NAME = "org/hibernate/hibernate-mapping-4.0.xsd";
 	public static final String ORM_1_SCHEMA_NAME = "org/hibernate/ejb/orm_1_0.xsd";
 	public static final String ORM_2_SCHEMA_NAME = "org/hibernate/ejb/orm_2_0.xsd";
 
 	private Schema hbmSchema;
 
 	private Schema hbmSchema() {
 		if ( hbmSchema == null ) {
 			hbmSchema = resolveLocalSchema( HBM_SCHEMA_NAME );
 		}
 		return hbmSchema;
 	}
 
 	private Schema orm1Schema;
 
 	private Schema orm1Schema() {
 		if ( orm1Schema == null ) {
 			orm1Schema = resolveLocalSchema( ORM_1_SCHEMA_NAME );
 		}
 		return orm1Schema;
 	}
 
 	private Schema orm2Schema;
 
 	private Schema orm2Schema() {
 		if ( orm2Schema == null ) {
 			orm2Schema = resolveLocalSchema( ORM_2_SCHEMA_NAME );
 		}
 		return orm2Schema;
 	}
 
 	private Schema resolveLocalSchema(String schemaName) {
 		return resolveLocalSchema( schemaName, XMLConstants.W3C_XML_SCHEMA_NS_URI );
 	}
 
 	private Schema resolveLocalSchema(String schemaName, String schemaLanguage) {
 		URL url = metadataSources.getServiceRegistry()
 				.getService( ClassLoaderService.class )
 				.locateResource( schemaName );
 		if ( url == null ) {
 			throw new XsdException( "Unable to locate schema [" + schemaName + "] via classpath", schemaName );
 		}
 		try {
 			InputStream schemaStream = url.openStream();
 			try {
 				StreamSource source = new StreamSource( url.openStream() );
 				SchemaFactory schemaFactory = SchemaFactory.newInstance( schemaLanguage );
 				return schemaFactory.newSchema( source );
 			}
 			catch ( SAXException e ) {
 				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
 			}
 			catch ( IOException e ) {
 				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
 			}
 			finally {
 				try {
 					schemaStream.close();
 				}
 				catch ( IOException e ) {
 					log.debugf( "Problem closing schema stream [%s]", e.toString() );
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw new XsdException( "Stream error handling schema url [" + url.toExternalForm() + "]", schemaName );
 		}
 	}
 
 	static class ContextProvidingValidationEventHandler implements ValidationEventHandler {
 		private int lineNumber;
 		private int columnNumber;
 		private String message;
 
 		@Override
 		public boolean handleEvent(ValidationEvent validationEvent) {
 			ValidationEventLocator locator = validationEvent.getLocator();
 			lineNumber = locator.getLineNumber();
 			columnNumber = locator.getColumnNumber();
 			message = validationEvent.getMessage();
 			return false;
 		}
 
 		public int getLineNumber() {
 			return lineNumber;
 		}
 
 		public int getColumnNumber() {
 			return columnNumber;
 		}
 
 		public String getMessage() {
 			return message;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
index 5af4980c7e..8b61fefaaf 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
@@ -1,196 +1,243 @@
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
 
 import javax.persistence.SharedCacheMode;
 
+import org.hibernate.HibernateException;
+import org.hibernate.boot.registry.BootstrapServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataBuilder;
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.engine.config.spi.ConfigurationService;
+import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataBuilderImpl implements MetadataBuilder {
+	private static final Logger log = Logger.getLogger( MetadataBuilderImpl.class );
+
 	private final MetadataSources sources;
 	private final OptionsImpl options;
 
 	public MetadataBuilderImpl(MetadataSources sources) {
+		this(
+				sources,
+				getStandardServiceRegistry( sources.getServiceRegistry() )
+		);
+	}
+
+	private static StandardServiceRegistry getStandardServiceRegistry(ServiceRegistry serviceRegistry) {
+		if ( serviceRegistry == null ) {
+			throw new HibernateException( "ServiceRegistry passed to MetadataBuilder cannot be null" );
+		}
+
+		if ( StandardServiceRegistry.class.isInstance( serviceRegistry ) ) {
+			return ( StandardServiceRegistry ) serviceRegistry;
+		}
+		else if ( BootstrapServiceRegistry.class.isInstance( serviceRegistry ) ) {
+			log.debugf(
+					"ServiceRegistry passed to MetadataBuilder was a BootstrapServiceRegistry; this likely wont end well" +
+							"if attempt is made to build SessionFactory"
+			);
+			return new StandardServiceRegistryBuilder( (BootstrapServiceRegistry) serviceRegistry ).build();
+		}
+		else {
+			throw new HibernateException(
+					String.format(
+							"Unexpected type of ServiceRegistry [%s] encountered in attempt to build MetadataBuilder",
+							serviceRegistry.getClass().getName()
+					)
+			);
+		}
+	}
+
+	public MetadataBuilderImpl(MetadataSources sources, StandardServiceRegistry serviceRegistry) {
 		this.sources = sources;
-		this.options = new OptionsImpl( sources.getServiceRegistry() );
+		this.options = new OptionsImpl( serviceRegistry );
 	}
 
 	@Override
 	public MetadataBuilder with(NamingStrategy namingStrategy) {
 		this.options.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(MetadataSourceProcessingOrder metadataSourceProcessingOrder) {
 		this.options.metadataSourceProcessingOrder = metadataSourceProcessingOrder;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(SharedCacheMode sharedCacheMode) {
 		this.options.sharedCacheMode = sharedCacheMode;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(AccessType accessType) {
 		this.options.defaultCacheAccessType = accessType;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled) {
 		this.options.useNewIdentifierGenerators = enabled;
 		return this;
 	}
 
 	@Override
-	public Metadata buildMetadata() {
+	public Metadata build() {
 		return new MetadataImpl( sources, options );
 	}
 
-	private static class OptionsImpl implements Metadata.Options {
+	public static class OptionsImpl implements Metadata.Options {
+		private final StandardServiceRegistry serviceRegistry;
+
 		private MetadataSourceProcessingOrder metadataSourceProcessingOrder = MetadataSourceProcessingOrder.HBM_FIRST;
 		private NamingStrategy namingStrategy = EJB3NamingStrategy.INSTANCE;
 		private SharedCacheMode sharedCacheMode = SharedCacheMode.ENABLE_SELECTIVE;
 		private AccessType defaultCacheAccessType;
         private boolean useNewIdentifierGenerators;
         private boolean globallyQuotedIdentifiers;
 		private String defaultSchemaName;
 		private String defaultCatalogName;
 
-		public OptionsImpl(ServiceRegistry serviceRegistry) {
+		public OptionsImpl(StandardServiceRegistry serviceRegistry) {
+			this.serviceRegistry = serviceRegistry;
+
 			ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
 
 			// cache access type
 			defaultCacheAccessType = configService.getSetting(
 					AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY,
 					new ConfigurationService.Converter<AccessType>() {
 						@Override
 						public AccessType convert(Object value) {
 							return AccessType.fromExternalName( value.toString() );
 						}
 					}
 			);
 
 			useNewIdentifierGenerators = configService.getSetting(
 					AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS,
 					new ConfigurationService.Converter<Boolean>() {
 						@Override
 						public Boolean convert(Object value) {
 							return Boolean.parseBoolean( value.toString() );
 						}
 					},
 					false
 			);
 
 			defaultSchemaName = configService.getSetting(
 					AvailableSettings.DEFAULT_SCHEMA,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
 
 			defaultCatalogName = configService.getSetting(
 					AvailableSettings.DEFAULT_CATALOG,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
 
             globallyQuotedIdentifiers = configService.getSetting(
                     AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS,
                     new ConfigurationService.Converter<Boolean>() {
                         @Override
                         public Boolean convert(Object value) {
                             return Boolean.parseBoolean( value.toString() );
                         }
                     },
                     false
             );
 		}
 
+		@Override
+		public StandardServiceRegistry getServiceRegistry() {
+			return serviceRegistry;
+		}
 
 		@Override
 		public MetadataSourceProcessingOrder getMetadataSourceProcessingOrder() {
 			return metadataSourceProcessingOrder;
 		}
 
 		@Override
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		@Override
 		public AccessType getDefaultAccessType() {
 			return defaultCacheAccessType;
 		}
 
 		@Override
 		public SharedCacheMode getSharedCacheMode() {
 			return sharedCacheMode;
 		}
 
 		@Override
         public boolean useNewIdentifierGenerators() {
             return useNewIdentifierGenerators;
         }
 
         @Override
         public boolean isGloballyQuotedIdentifiers() {
             return globallyQuotedIdentifiers;
         }
 
         @Override
 		public String getDefaultSchemaName() {
 			return defaultSchemaName;
 		}
 
 		@Override
 		public String getDefaultCatalogName() {
 			return defaultCatalogName;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 0e25b152ed..7693ed0ea8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,602 +1,601 @@
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
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.MetadataSourceProcessor;
 import org.hibernate.metamodel.source.annotations.AnnotationMetadataSourceProcessorImpl;
 import org.hibernate.metamodel.source.hbm.HbmMetadataSourceProcessorImpl;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.type.TypeResolver;
+import org.jboss.logging.Logger;
 
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
 
 	private final ServiceRegistry serviceRegistry;
 	private final Options options;
 
 	private final ValueHolder<ClassLoaderService> classLoaderService;
 	private final ValueHolder<PersisterClassResolver> persisterClassResolverService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private final MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
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
-		this.serviceRegistry =  metadataSources.getServiceRegistry();
+		this.serviceRegistry =  options.getServiceRegistry();
 		this.options = options;
 		this.identifierGeneratorFactory = serviceRegistry.getService( MutableIdentifierGeneratorFactory.class );
 				//new DefaultIdentifierGeneratorFactory( dialect );
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
 
 		this.classLoaderService = new ValueHolder<ClassLoaderService>(
 				new ValueHolder.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return serviceRegistry.getService( ClassLoaderService.class );
 					}
 				}
 		);
 		this.persisterClassResolverService = new ValueHolder<PersisterClassResolver>(
 				new ValueHolder.DeferredInitializer<PersisterClassResolver>() {
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
 		new HibernateTypeResolver( this ).resolve();
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
-	public SessionFactory buildSessionFactory() {
-		return sessionFactoryBuilder.buildSessionFactory();
-	}
-
-	@Override
 	public ServiceRegistry getServiceRegistry() {
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
 	public ValueHolder<Class<?>> makeClassReference(final String className) {
 		return new ValueHolder<Class<?>>(
 				new ValueHolder.DeferredInitializer<Class<?>>() {
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
 		LOG.tracev( "Import: {0} -> {1}", importName, entityName );
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
+	public SessionFactory buildSessionFactory() {
+		return getSessionFactoryBuilder().build();
+	}
+
+	@Override
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
 
 		private final ValueHolder<AccessType> regionFactorySpecifiedDefaultAccessType = new ValueHolder<AccessType>(
 				new ValueHolder.DeferredInitializer<AccessType>() {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java
index 3be406488b..2a1f3cf45f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java
@@ -1,88 +1,99 @@
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
 
 import java.io.Serializable;
 
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.Interceptor;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.SessionFactory;
+import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 
 /**
  * @author Gail Badner
  */
 public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
 	SessionFactoryOptionsImpl options;
 
 	private final MetadataImplementor metadata;
 
 	/* package-protected */
 	SessionFactoryBuilderImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
-		options = new SessionFactoryOptionsImpl();
+		options = new SessionFactoryOptionsImpl( metadata.getOptions().getServiceRegistry() );
 	}
 
 	@Override
 	public SessionFactoryBuilder with(Interceptor interceptor) {
 		this.options.interceptor = interceptor;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.options.entityNotFoundDelegate = entityNotFoundDelegate;
 		return this;
 	}
 
 	@Override
-	public SessionFactory buildSessionFactory() {
+	public SessionFactory build() {
 		return new SessionFactoryImpl(metadata, options, null );
 	}
 
 	private static class SessionFactoryOptionsImpl implements SessionFactory.SessionFactoryOptions {
+		private final StandardServiceRegistry serviceRegistry;
 		private Interceptor interceptor = EmptyInterceptor.INSTANCE;
+		
+		public SessionFactoryOptionsImpl(StandardServiceRegistry serviceRegistry) {
+			this.serviceRegistry = serviceRegistry;
+		}
 
 		// TODO: should there be a DefaultEntityNotFoundDelegate.INSTANCE?
 		private EntityNotFoundDelegate entityNotFoundDelegate = new EntityNotFoundDelegate() {
 				public void handleEntityNotFound(String entityName, Serializable id) {
 					throw new ObjectNotFoundException( id, entityName );
 				}
 		};
 
 		@Override
+		public StandardServiceRegistry getServiceRegistry() {
+			return serviceRegistry;
+		}
+
+		@Override
 		public Interceptor getInterceptor() {
 			return interceptor;
 		}
 
 		@Override
 		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 			return entityNotFoundDelegate;
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
index 6d9c358aef..4039dfe4d8 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
@@ -1,89 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.service;
 
 import java.util.Map;
 
 import org.hibernate.boot.registry.StandardServiceInitiator;
+import org.hibernate.boot.registry.StandardServiceRegistry;
 
 /**
  * @deprecated Use {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder} instead
  */
 @Deprecated
 public class ServiceRegistryBuilder extends org.hibernate.boot.registry.StandardServiceRegistryBuilder {
 	public ServiceRegistryBuilder() {
 		super();    //To change body of overridden methods use File | Settings | File Templates.
 	}
 
 	public ServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		super( bootstrapServiceRegistry );    //To change body of overridden methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public ServiceRegistryBuilder loadProperties(String resourceName) {
 		super.loadProperties( resourceName );
 		return this;
 	}
 
 	@Override
 	public ServiceRegistryBuilder configure() {
 		super.configure();
 		return this;
 	}
 
 	@Override
 	public ServiceRegistryBuilder configure(String resourceName) {
 		super.configure( resourceName );
 		return this;
 	}
 
 	@Override
 	public ServiceRegistryBuilder applySetting(String settingName, Object value) {
 		super.applySetting( settingName, value );
 		return this;
 	}
 
 	@Override
 	public ServiceRegistryBuilder applySettings(Map settings) {
 		super.applySettings( settings );
 		return this;
 	}
 
 	@Override
 	public ServiceRegistryBuilder addInitiator(StandardServiceInitiator initiator) {
 		super.addInitiator( initiator );
 		return this;
 	}
 
 	@Override
 	public ServiceRegistryBuilder addService(Class serviceRole, Service service) {
 		super.addService( serviceRole, service );
 		return this;
 	}
 
-	@Override
-	public ServiceRegistry buildServiceRegistry() {
-		return super.buildServiceRegistry();
+	public StandardServiceRegistry build() {
+		return super.build();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceContributor.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceContributor.java
new file mode 100644
index 0000000000..538687a925
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceContributor.java
@@ -0,0 +1,40 @@
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
+package org.hibernate.service.spi;
+
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+
+/**
+ * Contract for contributing services.
+ *
+ * @author Steve Ebersole
+ */
+public interface ServiceContributor {
+	/**
+	 * Contribute services to the indicated registry builder.
+	 *
+	 * @param serviceRegistryBuilder The builder to which services (or initiators) should be contributed.
+	 */
+	public void contribute(StandardServiceRegistryBuilder serviceRegistryBuilder);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
index 411866f517..9f722270d8 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
@@ -1,107 +1,107 @@
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
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * A {@link ConnectionHelper} implementation based on an internally
  * built and managed {@link ConnectionProvider}.
  *
  * @author Steve Ebersole
  */
 class ManagedProviderConnectionHelper implements ConnectionHelper {
 	private Properties cfgProperties;
 	private StandardServiceRegistryImpl serviceRegistry;
 	private Connection connection;
 
 	public ManagedProviderConnectionHelper(Properties cfgProperties) {
 		this.cfgProperties = cfgProperties;
 	}
 
 	public void prepare(boolean needsAutoCommit) throws SQLException {
 		serviceRegistry = createServiceRegistry( cfgProperties );
 		connection = serviceRegistry.getService( ConnectionProvider.class ).getConnection();
 		if ( needsAutoCommit && ! connection.getAutoCommit() ) {
 			connection.commit();
 			connection.setAutoCommit( true );
 		}
 	}
 
 	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).build();
 	}
 
 	public Connection getConnection() throws SQLException {
 		return connection;
 	}
 
 	public void release() throws SQLException {
 		try {
 			releaseConnection();
 		}
 		finally {
 			releaseServiceRegistry();
 		}
 	}
 
 	private void releaseConnection() throws SQLException {
 		if ( connection != null ) {
 			try {
 				new SqlExceptionHelper().logAndClearWarnings( connection );
 			}
 			finally {
 				try  {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				finally {
 					connection = null;
 				}
 			}
 		}
 	}
 
 	private void releaseServiceRegistry() {
 		if ( serviceRegistry != null ) {
 			try {
 				serviceRegistry.destroy();
 			}
 			finally {
 				serviceRegistry = null;
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index 271d6fec23..adada24997 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,621 +1,621 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * Commandline tool to export table schema to the database. This class may also be called from inside an application.
  *
  * @author Daniel Bradby
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class SchemaExport {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaExport.class.getName());
 	private static final String DEFAULT_IMPORT_FILE = "/import.sql";
 
 	public static enum Type {
 		CREATE,
 		DROP,
 		NONE,
 		BOTH;
 
 		public boolean doCreate() {
 			return this == BOTH || this == CREATE;
 		}
 
 		public boolean doDrop() {
 			return this == BOTH || this == DROP;
 		}
 	}
 
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final String[] dropSQL;
 	private final String[] createSQL;
 	private final String importFiles;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 	private ImportSqlCommandExtractor importSqlCommandExtractor = ImportSqlCommandExtractorInitiator.DEFAULT_EXTRACTOR;
 
 	private String outputFile = null;
 	private String delimiter;
 	private boolean haltOnError = false;
 
 	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				serviceRegistry.getService( ConnectionProvider.class )
 		);
 		this.sqlStatementLogger = serviceRegistry.getService( JdbcServices.class ).getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = serviceRegistry.getService( JdbcServices.class ).getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	public SchemaExport(MetadataImplementor metadata) {
 		ServiceRegistry serviceRegistry = metadata.getServiceRegistry();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				serviceRegistry.getService( ConnectionProvider.class )
 		);
         JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				serviceRegistry.getService( ConfigurationService.class ).getSettings(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = jdbcServices.getDialect();
 		this.dropSQL = metadata.getDatabase().generateDropSchemaScript( dialect );
 		this.createSQL = metadata.getDatabase().generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration) {
 		this( configuration, configuration.getProperties() );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, with the given
 	 * database connection properties.
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @param properties The properties from which to configure connectivity etc.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 *
 	 * @deprecated properties may be specified via the Configuration object
 	 */
 	@Deprecated
     public SchemaExport(Configuration configuration, Properties properties) throws HibernateException {
 		final Dialect dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				properties,
 				DEFAULT_IMPORT_FILE
 		);
 
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, using the supplied connection for connectivity.
 	 *
 	 * @param configuration The configuration to use.
 	 * @param connection The JDBC connection to use.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration, Connection connection) throws HibernateException {
 		this.connectionHelper = new SuppliedConnectionHelper( connection );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = Dialect.getDialect( configuration.getProperties() );
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	public SchemaExport(
 			ConnectionHelper connectionHelper,
 			String[] dropSql,
 			String[] createSql) {
 		this.connectionHelper = connectionHelper;
 		this.dropSQL = dropSql;
 		this.createSQL = createSql;
 		this.importFiles = "";
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.formatter = FormatStyle.DDL.getFormatter();
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
 	 * Set <i>import.sql</i> command extractor. By default {@link SingleLineSqlCommandExtractor} is used.
 	 *
 	 * @param importSqlCommandExtractor <i>import.sql</i> command extractor.
 	 * @return this
 	 */
 	public SchemaExport setImportSqlCommandExtractor(ImportSqlCommandExtractor importSqlCommandExtractor) {
 		this.importSqlCommandExtractor = importSqlCommandExtractor;
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
 	 * Run the schema creation script; drop script is automatically
 	 * executed before running the creation script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void create(boolean script, boolean export) {
 		create( Target.interpret( script, export ) );
 	}
 
 	/**
 	 * Run the schema creation script; drop script is automatically
 	 * executed before running the creation script.
 	 *
 	 * @param output the target of the script.
 	 */
 	public void create(Target output) {
 		// need to drop tables before creating so need to specify Type.BOTH
 		execute( output, Type.BOTH );
 	}
 
 	/**
 	 * Run the drop schema script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void drop(boolean script, boolean export) {
 		drop( Target.interpret( script, export ) );
 	}
 
 	public void drop(Target output) {
 		execute( output, Type.DROP );
 	}
 
 	public void execute(boolean script, boolean export, boolean justDrop, boolean justCreate) {
 		execute( Target.interpret( script, export ), interpretType( justDrop, justCreate ) );
 	}
 
 	private Type interpretType(boolean justDrop, boolean justCreate) {
 		if ( justDrop ) {
 			return Type.DROP;
 		}
 		else if ( justCreate ) {
 			return Type.CREATE;
 		}
 		else {
 			return Type.BOTH;
 		}
 	}
 
 	public void execute(Target output, Type type) {
 		if ( (outputFile == null && output == Target.NONE) || type == SchemaExport.Type.NONE ) {
 			return;
 		}
 		exceptions.clear();
 
 		LOG.runningHbm2ddlSchemaExport();
 
 		final List<NamedReader> importFileReaders = new ArrayList<NamedReader>();
 		for ( String currentFile : importFiles.split(",") ) {
 			try {
 				final String resourceName = currentFile.trim();
 				InputStream stream = ConfigHelper.getResourceAsStream( resourceName );
 				importFileReaders.add( new NamedReader( resourceName, stream ) );
 			}
 			catch ( HibernateException e ) {
 				LOG.debugf("Import file not found: %s", currentFile);
 			}
 		}
 
 		final List<Exporter> exporters = new ArrayList<Exporter>();
 		try {
 			// prepare exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( output.doScript() ) {
 				exporters.add( new ScriptExporter() );
 			}
 			if ( outputFile != null ) {
 				exporters.add( new FileExporter( outputFile ) );
 			}
 			if ( output.doExport() ) {
 				exporters.add( new DatabaseExporter( connectionHelper, sqlExceptionHelper ) );
 			}
 
 			// perform exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( type.doDrop() ) {
 				perform( dropSQL, exporters );
 			}
 			if ( type.doCreate() ) {
 				perform( createSQL, exporters );
 				if ( ! importFileReaders.isEmpty() ) {
 					for ( NamedReader namedReader : importFileReaders ) {
 						importScript( namedReader, exporters );
 					}
 				}
 			}
 		}
 		catch (Exception e) {
 			exceptions.add( e );
 			LOG.schemaExportUnsuccessful( e );
 		}
 		finally {
 			// release exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.release();
 				}
 				catch (Exception ignore) {
 				}
 			}
 
 			// release the named readers from import scripts
 			for ( NamedReader namedReader : importFileReaders ) {
 				try {
 					namedReader.getReader().close();
 				}
 				catch (Exception ignore) {
 				}
 			}
             LOG.schemaExportComplete();
 		}
 	}
 
 	private void perform(String[] sqlCommands, List<Exporter> exporters) {
 		for ( String sqlCommand : sqlCommands ) {
 			String formatted = formatter.format( sqlCommand );
 	        if ( delimiter != null ) {
 				formatted += delimiter;
 			}
 			sqlStatementLogger.logStatement( sqlCommand, formatter );
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.export( formatted );
 				}
 				catch (Exception e) {
 					if ( haltOnError ) {
 						throw new HibernateException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
 					LOG.unsuccessfulCreate( sqlCommand );
 					LOG.error( e.getMessage() );
 				}
 			}
 		}
 	}
 
 	private void importScript(NamedReader namedReader, List<Exporter> exporters) throws Exception {
 		BufferedReader reader = new BufferedReader( namedReader.getReader() );
 		String[] statements = importSqlCommandExtractor.extractCommands( reader );
 		if (statements != null) {
 			for ( String statement : statements ) {
 				if ( statement != null ) {
 					String trimmedSql = statement.trim();
 					if ( trimmedSql.endsWith( ";" )) {
 						trimmedSql = trimmedSql.substring( 0, statement.length() - 1 );
 					}
 					if ( !StringHelper.isEmpty( trimmedSql ) ) {
 						try {
 							for ( Exporter exporter : exporters ) {
 								if ( exporter.acceptsImportScripts() ) {
 									exporter.export( trimmedSql );
 								}
 							}
 						}
 						catch ( Exception e ) {
 							throw new ImportScriptException( "Error during statement execution (file: '" + namedReader.getName() + "'): " + trimmedSql, e );
 						}
 					}
 				}
 			}
 		}
 	}
 
 	private static class NamedReader {
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
 
 	private void execute(boolean script, boolean export, Writer fileOutput, Statement statement, final String sql)
 			throws IOException, SQLException {
 		final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
 
 		String formatted = formatter.format( sql );
         if (delimiter != null) formatted += delimiter;
         if (script) System.out.println(formatted);
         LOG.debug(formatted);
 		if ( outputFile != null ) {
 			fileOutput.write( formatted + "\n" );
 		}
 		if ( export ) {
 
 			statement.executeUpdate( sql );
 			try {
 				SQLWarning warnings = statement.getWarnings();
 				if ( warnings != null) {
 					sqlExceptionHelper.logAndClearWarnings( connectionHelper.getConnection() );
 				}
 			}
 			catch( SQLException sqle ) {
                 LOG.unableToLogSqlWarnings(sqle);
 			}
 		}
 
 	}
 
 	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).build();
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
 				cfg.setProperty( AvailableSettings.HBM2DDL_IMPORT_FILES, importFile );
 			}
 
 			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				SchemaExport se = new SchemaExport( serviceRegistry, cfg )
 						.setHaltOnError( halt )
 						.setOutputFile( outFile )
 						.setDelimiter( delim )
 						.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 				if ( format ) {
 					se.setFormat( true );
 				}
 				se.execute( script, export, drop, create );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToCreateSchema(e);
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
index 451fae85bf..5a2ea85822 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
@@ -1,295 +1,295 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 
 /**
  * A commandline tool to update a database schema. May also be called from inside an application.
  *
  * @author Christoph Sturm
  * @author Steve Ebersole
  */
 public class SchemaUpdate {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaUpdate.class.getName());
 
 	private final Configuration configuration;
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final Dialect dialect;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 
 	private boolean haltOnError = false;
 	private boolean format = true;
 	private String outputFile = null;
 	private String delimiter;
 
 	public SchemaUpdate(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaUpdate(Configuration configuration, Properties properties) throws HibernateException {
 		this.configuration = configuration;
 		this.dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 	}
 
 	public SchemaUpdate(ServiceRegistry serviceRegistry, Configuration cfg) throws HibernateException {
 		this.configuration = cfg;
 
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.dialect = jdbcServices.getDialect();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).build();
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
 
 			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaUpdate( serviceRegistry, cfg ).execute( script, doUpdate );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Execute the schema updates
 	 *
 	 * @param script print all DDL to the console
 	 */
 	public void execute(boolean script, boolean doUpdate) {
 		execute( Target.interpret( script, doUpdate ) );
 	}
 	
 	public void execute(Target target) {
         LOG.runningHbm2ddlSchemaUpdate();
 
 		Connection connection = null;
 		Statement stmt = null;
 		Writer outputFileWriter = null;
 
 		exceptions.clear();
 
 		try {
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect );
 				stmt = connection.createStatement();
 			}
 			catch ( SQLException sqle ) {
 				exceptions.add( sqle );
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
             LOG.updatingSchema();
 
 			if ( outputFile != null ) {
                 LOG.writingGeneratedSchemaToFile( outputFile );
 				outputFileWriter = new FileWriter( outputFile );
 			}
 
 			String[] sqlStrings = configuration.generateSchemaUpdateScript( dialect, meta );
 			for ( String sql : sqlStrings ) {
 				String formatted = formatter.format( sql );
 				try {
 					if ( delimiter != null ) {
 						formatted += delimiter;
 					}
 					if ( target.doScript() ) {
 						System.out.println( formatted );
 					}
 					if ( outputFile != null ) {
 						outputFileWriter.write( formatted + "\n" );
 					}
 					if ( target.doExport() ) {
                         LOG.debug( sql );
 						stmt.executeUpdate( formatted );
 					}
 				}
 				catch ( SQLException e ) {
 					if ( haltOnError ) {
 						throw new JDBCException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
                     LOG.unsuccessful(sql);
                     LOG.error(e.getMessage());
 				}
 			}
 
             LOG.schemaUpdateComplete();
 
 		}
 		catch ( Exception e ) {
 			exceptions.add( e );
             LOG.unableToCompleteSchemaUpdate(e);
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
                 LOG.unableToCloseConnection(e);
 			}
 			try {
 				if( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch(Exception e) {
 				exceptions.add(e);
                 LOG.unableToCloseConnection(e);
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
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
index 8a748cffdc..9137c91976 100755
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
@@ -1,172 +1,172 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.FileInputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 
 /**
  * A commandline tool to update a database schema. May also be called from
  * inside an application.
  *
  * @author Christoph Sturm
  */
 public class SchemaValidator {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaValidator.class.getName());
 
 	private ConnectionHelper connectionHelper;
 	private Configuration configuration;
 	private Dialect dialect;
 
 	public SchemaValidator(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaValidator(Configuration cfg, Properties connectionProperties) throws HibernateException {
 		this.configuration = cfg;
 		dialect = Dialect.getDialect( connectionProperties );
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( connectionProperties );
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 	}
 
 	public SchemaValidator(ServiceRegistry serviceRegistry, Configuration cfg ) throws HibernateException {
 		this.configuration = cfg;
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.dialect = jdbcServices.getDialect();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 	}
 
 	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().applySettings( properties ).build();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
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
 
 			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaValidator( serviceRegistry, cfg ).validate();
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Perform the validations.
 	 */
 	public void validate() {
 
         LOG.runningSchemaValidator();
 
 		Connection connection = null;
 
 		try {
 
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( false );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect, false );
 			}
 			catch ( SQLException sqle ) {
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
 			configuration.validateSchema( dialect, meta );
 
 		}
 		catch ( SQLException e ) {
             LOG.unableToCompleteSchemaValidation(e);
 		}
 		finally {
 
 			try {
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
                 LOG.unableToCloseConnection(e);
 			}
 
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index 1fecfaeabd..2598e426dd 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
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
 package org.hibernate.metamodel.binding;
 
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Set;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
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
 
 	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().build();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	protected ServiceRegistry basicServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Test
 	public void testSimpleEntityMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForSimpleEntityBinding( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertRoot( metadata, entityBinding );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding() );
 	}
 
 	@Test
 	public void testSimpleVersionedEntityMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForSimpleVersionedEntityBinding( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleVersionedEntity.class.getName() );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNotNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding() );
 		assertNotNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding().getAttribute() );
 	}
 
 	@Test
 	public void testEntityWithManyToOneMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForSimpleEntityBinding( sources );
 		addSourcesForManyToOne( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding simpleEntityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertIdAndSimpleProperty( simpleEntityBinding );
 
 		Set<SingularAssociationAttributeBinding> referenceBindings = simpleEntityBinding.locateAttributeBinding( "id" )
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
 		assertSame( entityWithManyToOneBinding.locateAttributeBinding( "simpleEntity" ), it.next() );
 		assertFalse( it.hasNext() );
 	}
 
 	@Test
 	public void testSimpleEntityWithSimpleComponentMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForComponentBinding( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntityWithSimpleComponent.class.getName() );
 		assertRoot( metadata, entityBinding );
 		assertIdAndSimpleProperty( entityBinding );
 
 		ComponentAttributeBinding componentAttributeBinding = (ComponentAttributeBinding) entityBinding.locateAttributeBinding( "simpleComponent" );
 		assertNotNull( componentAttributeBinding );
 		assertSame( componentAttributeBinding.getAttribute().getSingularAttributeType(), componentAttributeBinding.getAttributeContainer() );
 		assertEquals( SimpleEntityWithSimpleComponent.class.getName() + ".simpleComponent", componentAttributeBinding.getPathBase() );
 		assertSame( entityBinding, componentAttributeBinding.seekEntityBinding() );
 		assertNotNull( componentAttributeBinding.getComponent() );
 	}
 
 	public abstract void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
 
 	public abstract void addSourcesForSimpleEntityBinding(MetadataSources sources);
 
 	public abstract void addSourcesForManyToOne(MetadataSources sources);
 
 	public abstract void addSourcesForComponentBinding(MetadataSources sources);
 
 	protected void assertIdAndSimpleProperty(EntityBinding entityBinding) {
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getHierarchyDetails().getEntityIdentifier() );
 		assertNotNull( entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 
 		AttributeBinding idAttributeBinding = entityBinding.locateAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 		assertTrue( idAttributeBinding.getAttribute().isSingular() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		SingularAttributeBinding singularIdAttributeBinding = (SingularAttributeBinding) idAttributeBinding;
 		assertFalse( singularIdAttributeBinding.isNullable() );
 		SingularAttribute singularIdAttribute =  ( SingularAttribute ) idAttributeBinding.getAttribute();
 		BasicType basicIdAttributeType = ( BasicType ) singularIdAttribute.getSingularAttributeType();
 		assertSame( Long.class, basicIdAttributeType.getClassReference() );
 
 		assertNotNull( singularIdAttributeBinding.getValue() );
 		assertTrue( singularIdAttributeBinding.getValue() instanceof Column );
 		Datatype idDataType = ( (Column) singularIdAttributeBinding.getValue() ).getDatatype();
 		assertSame( Long.class, idDataType.getJavaType() );
 		assertSame( Types.BIGINT, idDataType.getTypeCode() );
 		assertSame( LongType.INSTANCE.getName(), idDataType.getTypeName() );
 
 		assertNotNull( entityBinding.locateAttributeBinding( "name" ) );
 		assertNotNull( entityBinding.locateAttributeBinding( "name" ).getAttribute() );
 		assertTrue( entityBinding.locateAttributeBinding( "name" ).getAttribute().isSingular() );
 
 		SingularAttributeBinding nameBinding = (SingularAttributeBinding) entityBinding.locateAttributeBinding( "name" );
 		assertTrue( nameBinding.isNullable() );
 		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 		SingularAttribute singularNameAttribute =  ( SingularAttribute ) nameBinding.getAttribute();
 		BasicType basicNameAttributeType = ( BasicType ) singularNameAttribute.getSingularAttributeType();
 		assertSame( String.class, basicNameAttributeType.getClassReference() );
 
 		assertNotNull( nameBinding.getValue() );
 		SimpleValue nameValue = (SimpleValue) nameBinding.getValue();
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
index 98b8e26dc3..2deb0afe3f 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
@@ -1,90 +1,90 @@
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
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicCollectionBindingTests extends BaseUnitTestCase {
 	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().build();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 //	@Test
 //	public void testAnnotations() {
 //		doTest( MetadataSourceProcessingOrder.ANNOTATIONS_FIRST );
 //	}
 
 	@Test
 	public void testHbm() {
 		doTest( MetadataSourceProcessingOrder.HBM_FIRST );
 	}
 
 	private void doTest(MetadataSourceProcessingOrder processingOrder) {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 //		sources.addAnnotatedClass( EntityWithBasicCollections.class );
 		sources.addResource( "org/hibernate/metamodel/binding/EntityWithBasicCollections.hbm.xml" );
-		MetadataImpl metadata = (MetadataImpl) sources.getMetadataBuilder().with( processingOrder ).buildMetadata();
+		MetadataImpl metadata = (MetadataImpl) sources.getMetadataBuilder().with( processingOrder ).build();
 
 		final EntityBinding entityBinding = metadata.getEntityBinding( EntityWithBasicCollections.class.getName() );
 		assertNotNull( entityBinding );
 
 		PluralAttributeBinding bagBinding = metadata.getCollection( EntityWithBasicCollections.class.getName() + ".theBag" );
 		assertNotNull( bagBinding );
 		assertSame( bagBinding, entityBinding.locateAttributeBinding( "theBag" ) );
 		assertNotNull( bagBinding.getCollectionTable() );
 		assertEquals( CollectionElementNature.BASIC, bagBinding.getCollectionElement().getCollectionElementNature() );
 		assertEquals( String.class.getName(), ( (BasicCollectionElement) bagBinding.getCollectionElement() ).getHibernateTypeDescriptor().getJavaTypeName() );
 
 		PluralAttributeBinding setBinding = metadata.getCollection( EntityWithBasicCollections.class.getName() + ".theSet" );
 		assertNotNull( setBinding );
 		assertSame( setBinding, entityBinding.locateAttributeBinding( "theSet" ) );
 		assertNotNull( setBinding.getCollectionTable() );
 		assertEquals( CollectionElementNature.BASIC, setBinding.getCollectionElement().getCollectionElementNature() );
 		assertEquals( String.class.getName(), ( (BasicCollectionElement) setBinding.getCollectionElement() ).getHibernateTypeDescriptor().getJavaTypeName() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
index 09e8ccd094..52a040af39 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
@@ -1,129 +1,130 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.metamodel.source.annotations.entity;
 
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.metamodel.MetadataBuilder;
+import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.After;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.MethodRule;
 import org.junit.runners.model.FrameworkMethod;
 import org.junit.runners.model.Statement;
 
-import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
-import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
-
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-
 /**
  * @author Hardy Ferentschik
  */
 public abstract class BaseAnnotationBindingTestCase extends BaseUnitTestCase {
 	protected MetadataSources sources;
 	protected MetadataImpl meta;
 
 	@Rule
 	public MethodRule buildMetaData = new MethodRule() {
 		@Override
 		public Statement apply(final Statement statement, FrameworkMethod frameworkMethod, Object o) {
 			return new KeepSetupFailureStatement( statement, frameworkMethod );
 		}
 	};
 
 	@After
 	public void tearDown() {
 		sources = null;
 		meta = null;
 	}
 
 	public EntityBinding getEntityBinding(Class<?> clazz) {
 		return meta.getEntityBinding( clazz.getName() );
 	}
 
 	public EntityBinding getRootEntityBinding(Class<?> clazz) {
 		return meta.getRootEntityBinding( clazz.getName() );
 	}
 
 	class KeepSetupFailureStatement extends Statement {
 		private final Statement origStatement;
 		private final FrameworkMethod origFrameworkMethod;
 		private Throwable setupError;
 		private boolean expectedException;
 
 		KeepSetupFailureStatement(Statement statement, FrameworkMethod frameworkMethod) {
 			this.origStatement = statement;
 			this.origFrameworkMethod = frameworkMethod;
 		}
 
 		@Override
 		public void evaluate() throws Throwable {
 			try {
 				createBindings();
 				origStatement.evaluate();
 				if ( setupError != null ) {
 					throw setupError;
 				}
 			}
 			catch ( Throwable t ) {
 				if ( setupError == null ) {
 					throw t;
 				}
 				else {
 					if ( !expectedException ) {
 						throw setupError;
 					}
 				}
 			}
 		}
 
 		private void createBindings() {
 			try {
-				sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+				sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
+				MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
 				Resources resourcesAnnotation = origFrameworkMethod.getAnnotation( Resources.class );
 				if ( resourcesAnnotation != null ) {
-					sources.getMetadataBuilder().with( resourcesAnnotation.cacheMode() );
+					metadataBuilder.with( resourcesAnnotation.cacheMode() );
 
 					for ( Class<?> annotatedClass : resourcesAnnotation.annotatedClasses() ) {
 						sources.addAnnotatedClass( annotatedClass );
 					}
 					if ( !resourcesAnnotation.ormXmlPath().isEmpty() ) {
 						sources.addResource( resourcesAnnotation.ormXmlPath() );
 					}
 				}
-				meta = (MetadataImpl) sources.buildMetadata();
+
+				meta = ( MetadataImpl ) metadataBuilder.build();
 			}
 			catch ( final Throwable t ) {
 				setupError = t;
 				Test testAnnotation = origFrameworkMethod.getAnnotation( Test.class );
 				Class<?> expected = testAnnotation.expected();
 				if ( t.getClass().equals( expected ) ) {
 					expectedException = true;
 				}
 			}
 		}
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/IdentifierGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/IdentifierGeneratorTest.java
index 3180f826e7..c5f0afbbf0 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/IdentifierGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/IdentifierGeneratorTest.java
@@ -1,184 +1,184 @@
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
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 import static junit.framework.Assert.fail;
 
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.GenerationType;
 import javax.persistence.Id;
 
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.id.Assigned;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityIdentifier;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.testing.RequiresDialect;
 import org.junit.Test;
 
 /**
  * @author Hardy Ferentschik
  */
 @RequiresDialect(H2Dialect.class)
 public class IdentifierGeneratorTest extends BaseAnnotationBindingTestCase {
 	@Entity
 	class NoGenerationEntity {
 		@Id
 		private long id;
 	}
 
 	@Test
 	@Resources(annotatedClasses = NoGenerationEntity.class)
 	public void testNoIdGeneration() {
 		EntityBinding binding = getEntityBinding( NoGenerationEntity.class );
         EntityIdentifier identifier = binding.getHierarchyDetails().getEntityIdentifier();
 		IdentifierGenerator generator =identifier.getIdentifierGenerator();
         assertNotNull( generator );
         assertEquals( "Wrong generator", Assigned.class, generator.getClass() );
         assertFalse( identifier.isEmbedded() );
 
 	}
 
 	@Entity
 	class AutoEntity {
 		@Id
 		@GeneratedValue
 		private long id;
 
 		public long getId() {
 			return id;
 		}
 	}
 
 	@Test
 	@Resources(annotatedClasses = AutoEntity.class)
 	public void testAutoGenerationType() {
 		EntityBinding binding = getEntityBinding( AutoEntity.class );
 		IdentifierGenerator generator = binding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator();
 
 		assertEquals( "Wrong generator", IdentityGenerator.class, generator.getClass() );
 	}
 
 	@Entity
 	class TableEntity {
 		@Id
 		@GeneratedValue(strategy = GenerationType.TABLE)
 		private long id;
 
 		public long getId() {
 			return id;
 		}
 	}
 
 	@Test
 	@Resources(annotatedClasses = TableEntity.class)
 	public void testTableGenerationType() {
 		EntityBinding binding = getEntityBinding( TableEntity.class );
 		IdentifierGenerator generator = binding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator();
 
 		assertEquals( "Wrong generator", MultipleHiLoPerTableGenerator.class, generator.getClass() );
 	}
 
 	@Entity
 	class SequenceEntity {
 		@Id
 		@GeneratedValue(strategy = GenerationType.SEQUENCE)
 		private long id;
 
 		public long getId() {
 			return id;
 		}
 	}
 
 	@Test
 	@Resources(annotatedClasses = SequenceEntity.class)
 	public void testSequenceGenerationType() {
 		EntityBinding binding = getEntityBinding( SequenceEntity.class );
 		IdentifierGenerator generator = binding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator();
 
 		assertEquals( "Wrong generator", SequenceHiLoGenerator.class, generator.getClass() );
 	}
 
 
 	@Entity
 	class NamedGeneratorEntity {
 		@Id
 		@GeneratedValue(generator = "my-generator")
 		private long id;
 
 		public long getId() {
 			return id;
 		}
 	}
 
 	@Test
 	public void testUndefinedGenerator() {
 		try {
-			sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+			sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 			sources.addAnnotatedClass( NamedGeneratorEntity.class );
 			sources.buildMetadata();
 			fail();
 		}
 		catch ( MappingException e ) {
 			assertTrue( e.getMessage().startsWith( "Unable to find named generator" ) );
 		}
 	}
 
 	@Entity
 	@GenericGenerator(name = "my-generator", strategy = "uuid")
 	class NamedGeneratorEntity2 {
 		@Id
 		@GeneratedValue(generator = "my-generator")
 		private long id;
 
 		public long getId() {
 			return id;
 		}
 	}
 
 	@Test
 	@Resources(annotatedClasses = NamedGeneratorEntity2.class)
 	public void testNamedGenerator() {
 		EntityBinding binding = getEntityBinding( NamedGeneratorEntity2.class );
 		IdentifierGenerator generator = binding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator();
 
 		assertEquals( "Wrong generator", UUIDHexGenerator.class, generator.getClass() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MapsIdTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MapsIdTest.java
index 810e552820..81b0a3b8b5 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MapsIdTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/MapsIdTest.java
@@ -1,96 +1,96 @@
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
 
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.MapsId;
 import javax.persistence.OneToMany;
 
 import org.junit.Test;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MappingException;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertTrue;
 import static junit.framework.Assert.fail;
 
 /**
  * @author Hardy Ferentschik
  */
 public class MapsIdTest extends BaseAnnotationBindingTestCase {
 	@Entity
 	public class Employee {
 		@Id
 		long empId;
 		String name;
 	}
 
 	@Embeddable
 	public class DependentId {
 		String name;
 		long empid; // corresponds to PK type of Employee
 	}
 
 	@Entity
 	public class Dependent {
 		@Id
 		// should be @EmbeddedId, but embedded id are not working atm
 				DependentId id;
 
 		@MapsId("empid")
 		@OneToMany
 		Employee emp; // maps the empid attribute of embedded id @ManyToOne Employee emp;
 	}
 
 	@Test
 	@Resources(annotatedClasses = DependentId.class)
 	public void testMapsIsOnOneToManyThrowsException() {
 		try {
-			sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+			sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 			sources.addAnnotatedClass( DependentId.class );
 			sources.addAnnotatedClass( Dependent.class );
 			sources.addAnnotatedClass( Employee.class );
 			sources.buildMetadata();
 			fail();
 		}
 		catch ( MappingException e ) {
 			assertTrue(
 					e.getMessage()
 							.startsWith( "@MapsId can only be specified on a many-to-one or one-to-one associations" )
 			);
 			assertEquals(
 					"Wrong error origin",
 					"org.hibernate.metamodel.source.annotations.entity.MapsIdTest$Dependent",
 					e.getOrigin().getName()
 			);
 		}
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
index c82551a5f8..a686f1b03e 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
@@ -1,146 +1,146 @@
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
 
 import java.util.Iterator;
 
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContextImpl;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.fail;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class FetchProfileBinderTest extends BaseUnitTestCase {
 
 	private StandardServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().build();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testSingleFetchProfile() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.JOIN)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertEquals( "Wrong fetch profile name", "foo", profile.getName() );
 		org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 		assertEquals( "Wrong association name", "bar", fetch.getAssociation() );
 		assertEquals( "Wrong association type", Foo.class.getName(), fetch.getEntity() );
 	}
 
 	@Test
 	public void testFetchProfiles() {
 		Index index = JandexHelper.indexForClass( service, FooBar.class );
 		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 
 		assertTrue( mappedFetchProfiles.hasNext() );
 		profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 	}
 
 	private void assertProfiles(org.hibernate.metamodel.binding.FetchProfile profile) {
 		if ( profile.getName().equals( "foobar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "foobar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else if ( profile.getName().equals( "fubar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "fubar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else {
 			fail( "Wrong fetch name:" + profile.getName() );
 		}
 	}
 
 	@Test(expected = MappingException.class)
 	public void testNonJoinFetchThrowsException() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.SELECT)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 	}
 
 	@FetchProfiles( {
 			@FetchProfile(name = "foobar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "foobar", mode = FetchMode.JOIN)
 			}),
 			@FetchProfile(name = "fubar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "fubar", mode = FetchMode.JOIN)
 			})
 	})
 	class FooBar {
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
index f84ee9b688..dc27113eb1 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
@@ -1,98 +1,98 @@
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
 
 import javax.persistence.NamedNativeQuery;
 
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContextImpl;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class QueryBinderTest extends BaseUnitTestCase {
 
 	private StandardServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().build();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test(expected = NotYetImplementedException.class)
 	public void testNoResultClass() {
 		@NamedNativeQuery(name = "fubar", query = "SELECT * FROM FOO")
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 		QueryBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 	}
 
 	@Test
 	public void testResultClass() {
 		@NamedNativeQuery(name = "fubar", query = "SELECT * FROM FOO", resultClass = Foo.class)
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 		QueryBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		NamedSQLQueryDefinition namedQuery = meta.getNamedNativeQuery( "fubar" );
 		assertNotNull( namedQuery );
 		NativeSQLQueryReturn queryReturns[] = namedQuery.getQueryReturns();
 		assertTrue( "Wrong number of returns", queryReturns.length == 1 );
 		assertTrue( "Wrong query return type", queryReturns[0] instanceof NativeSQLQueryRootReturn );
 		NativeSQLQueryRootReturn rootReturn = (NativeSQLQueryRootReturn) queryReturns[0];
 		assertEquals( "Wrong result class", Foo.class.getName(), rootReturn.getReturnEntityName() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/BaseAnnotationIndexTestCase.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/BaseAnnotationIndexTestCase.java
index d067e55ecd..fd57f6d3fe 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/BaseAnnotationIndexTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/BaseAnnotationIndexTestCase.java
@@ -1,80 +1,80 @@
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
 package org.hibernate.metamodel.source.annotations.util;
 
 import java.util.Set;
 import javax.persistence.AccessType;
 
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContextImpl;
 import org.hibernate.metamodel.source.annotations.EntityHierarchyBuilder;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.annotations.entity.EmbeddableHierarchy;
 import org.hibernate.metamodel.source.binder.EntityHierarchy;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Hardy Ferentschik
  */
 public abstract class BaseAnnotationIndexTestCase extends BaseUnitTestCase {
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		meta = (MetadataImpl) sources.buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 	}
 
 	public Set<EntityHierarchy> createEntityHierarchies(Class<?>... clazz) {
 		Index index = JandexHelper.indexForClass(
 				meta.getServiceRegistry().getService( ClassLoaderService.class ),
 				clazz
 		);
 		AnnotationBindingContext context = new AnnotationBindingContextImpl( meta, index );
 		return EntityHierarchyBuilder.createEntityHierarchies( context );
 	}
 
 	public EmbeddableHierarchy createEmbeddableHierarchy(AccessType accessType, Class<?>... configuredClasses) {
 		Index index = JandexHelper.indexForClass(
 				meta.getServiceRegistry().getService( ClassLoaderService.class ),
 				configuredClasses
 		);
 		AnnotationBindingContext context = new AnnotationBindingContextImpl( meta, index );
 		return EmbeddableHierarchy.createEmbeddableHierarchy( configuredClasses[0], "", accessType, context );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java
index a77347ccab..9817f8281a 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java
@@ -1,249 +1,249 @@
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
 package org.hibernate.metamodel.source.annotations.util;
 
 import java.util.List;
 import java.util.Map;
 import javax.persistence.AttributeOverride;
 import javax.persistence.Basic;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.LockModeType;
 import javax.persistence.NamedQuery;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.NamedNativeQuery;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertTrue;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;
 
 /**
  * Tests for the helper class {@link JandexHelper}.
  *
  * @author Hardy Ferentschik
  */
 public class JandexHelperTest extends BaseUnitTestCase {
 	private StandardServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService classLoaderService;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder().build();
 		classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testGetMemberAnnotations() {
 		class Foo {
 			@Column
 			@Basic
 			private String bar;
 			private String fubar;
 		}
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 
 		ClassInfo classInfo = index.getClassByName( DotName.createSimple( Foo.class.getName() ) );
 		Map<DotName, List<AnnotationInstance>> memberAnnotations = JandexHelper.getMemberAnnotations(
 				classInfo, "bar"
 		);
 		assertTrue(
 				"property bar should defines @Column annotation",
 				memberAnnotations.containsKey( DotName.createSimple( Column.class.getName() ) )
 		);
 		assertTrue(
 				"property bar should defines @Basic annotation",
 				memberAnnotations.containsKey( DotName.createSimple( Basic.class.getName() ) )
 		);
 
 		memberAnnotations = JandexHelper.getMemberAnnotations( classInfo, "fubar" );
 		assertTrue( "there should be no annotations in fubar", memberAnnotations.isEmpty() );
 	}
 
 	@Test
 	public void testGettingNestedAnnotation() {
 		@AttributeOverride(name = "foo", column = @Column(name = "FOO"))
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.ATTRIBUTE_OVERRIDE );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		// try to retrieve the name
 		String name = JandexHelper.getValue( annotationInstance, "name", String.class );
 		assertEquals( "Wrong nested annotation", "foo", name );
 
 		// try to retrieve the nested column annotation instance
 		AnnotationInstance columnAnnotationInstance = JandexHelper.getValue(
 				annotationInstance,
 				"column",
 				AnnotationInstance.class
 		);
 		assertNotNull( columnAnnotationInstance );
 		assertEquals(
 				"Wrong nested annotation",
 				"javax.persistence.Column",
 				columnAnnotationInstance.name().toString()
 		);
 	}
 
 	@Test(expected = AssertionFailure.class)
 	public void testTryingToRetrieveWrongType() {
 		@AttributeOverride(name = "foo", column = @Column(name = "FOO"))
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.ATTRIBUTE_OVERRIDE );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		JandexHelper.getValue( annotationInstance, "name", Float.class );
 	}
 
 	@Test
 	public void testRetrieveDefaultEnumElement() {
 		@NamedQuery(name = "foo", query = "fubar")
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.NAMED_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		LockModeType lockMode = JandexHelper.getEnumValue( annotationInstance, "lockMode", LockModeType.class );
 		assertEquals( "Wrong lock mode", LockModeType.NONE, lockMode );
 	}
 
 	@Test
 	public void testRetrieveExplicitEnumElement() {
 		@NamedQuery(name = "foo", query = "bar", lockMode = LockModeType.OPTIMISTIC)
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.NAMED_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		LockModeType lockMode = JandexHelper.getEnumValue( annotationInstance, "lockMode", LockModeType.class );
 		assertEquals( "Wrong lock mode", LockModeType.OPTIMISTIC, lockMode );
 	}
 
 	@Test
 	public void testRetrieveStringArray() {
 		class Foo {
 			@org.hibernate.annotations.Index(name = "index", columnNames = { "a", "b", "c" })
 			private String foo;
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( HibernateDotNames.INDEX );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		String[] columnNames = JandexHelper.getValue( annotationInstance, "columnNames", String[].class );
 		Assert.assertTrue( columnNames.length == 3 );
 	}
 
 	@Test(expected = AssertionFailure.class)
 	public void testRetrieveClassParameterAsClass() {
 		@NamedNativeQuery(name = "foo", query = "bar", resultClass = Foo.class)
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		JandexHelper.getValue( annotationInstance, "resultClass", Class.class );
 	}
 
 	@Test
 	public void testRetrieveClassParameterAsString() {
 		@NamedNativeQuery(name = "foo", query = "bar", resultClass = Foo.class)
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		String fqcn = JandexHelper.getValue( annotationInstance, "resultClass", String.class );
 		assertEquals( "Wrong class names", Foo.class.getName(), fqcn );
 	}
 
 	@Test
 	public void testRetrieveUnknownParameter() {
 		@Entity
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.ENTITY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		try {
 			JandexHelper.getValue( annotationInstance, "foo", String.class );
 			fail();
 		}
 		catch ( AssertionFailure e ) {
 			assertTrue(
 					e.getMessage()
 							.startsWith( "The annotation javax.persistence.Entity does not define a parameter 'foo'" )
 			);
 		}
 	}
 
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
index c1935b1efd..e31cea2ea4 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
@@ -1,70 +1,68 @@
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
 package org.hibernate.metamodel.source.annotations.xml;
 
-import org.junit.Test;
+import static junit.framework.Assert.assertNotNull;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
-
 import org.hibernate.testing.junit4.BaseUnitTestCase;
-
-import static junit.framework.Assert.assertNotNull;
+import org.junit.Test;
 
 /**
  * @author Hardy Ferentschik
  */
 public class OrmXmlParserTests extends BaseUnitTestCase {
 	@Test
 	public void testSimpleOrmVersion2() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addResource( "org/hibernate/metamodel/source/annotations/xml/orm-father.xml" );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding binding = metadata.getEntityBinding( Father.class.getName() );
 		assertNotNull( binding );
 	}
 
 	@Test
 	public void testSimpleOrmVersion1() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addResource( "org/hibernate/metamodel/source/annotations/xml/orm-star.xml" );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding binding = metadata.getEntityBinding( Star.class.getName() );
 		assertNotNull( binding );
 	}
 
 	@Test(expected = MappingException.class)
 	public void testInvalidOrmXmlThrowsException() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addResource( "org/hibernate/metamodel/source/annotations/xml/orm-invalid.xml" );
 		sources.buildMetadata();
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
index 4fb1caa912..73d450352e 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
@@ -1,111 +1,109 @@
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
 
-import java.util.Iterator;
+import static junit.framework.Assert.assertEquals;
+import static junit.framework.Assert.assertFalse;
+import static junit.framework.Assert.assertNotNull;
+import static junit.framework.Assert.assertTrue;
 
-import org.junit.Test;
+import java.util.Iterator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.FetchProfile;
-
 import org.hibernate.testing.junit4.BaseUnitTestCase;
-
-import static junit.framework.Assert.assertEquals;
-import static junit.framework.Assert.assertFalse;
-import static junit.framework.Assert.assertNotNull;
-import static junit.framework.Assert.assertTrue;
+import org.junit.Test;
 
 /**
  * @author Hardy Ferentschik
  */
 public class MetadataImplTest extends BaseUnitTestCase {
 
 	@Test(expected = IllegalArgumentException.class)
 	public void testAddingNullClass() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addClass( null );
 		sources.buildMetadata();
 	}
 
 	@Test(expected = IllegalArgumentException.class)
 	public void testAddingNullPackageName() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addPackage( null );
 		sources.buildMetadata();
 	}
 
 	@Test(expected = HibernateException.class)
 	public void testAddingNonExistingPackageName() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addPackage( "not.a.package" );
 		sources.buildMetadata();
 	}
 
 	@Test
 	public void testAddingPackageName() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addPackage( "org.hibernate.metamodel.source.internal" );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		assertFetchProfile( metadata );
 	}
 
 	@Test
 	public void testAddingPackageNameWithTrailingDot() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addPackage( "org.hibernate.metamodel.source.internal." );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		assertFetchProfile( metadata );
 	}
 
 	@Test
 	public void testGettingSessionFactoryBuilder() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		Metadata metadata = sources.buildMetadata();
 
 		SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
 		assertNotNull( sessionFactoryBuilder );
 		assertTrue( SessionFactoryBuilderImpl.class.isInstance( sessionFactoryBuilder ) );
 
 		SessionFactory sessionFactory = metadata.buildSessionFactory();
 		assertNotNull( sessionFactory );
 	}
 
 	private void assertFetchProfile(MetadataImpl metadata) {
 		Iterator<FetchProfile> profiles = metadata.getFetchProfiles().iterator();
 		assertTrue( profiles.hasNext() );
 		FetchProfile profile = profiles.next();
 		assertEquals( "wrong profile name", "package-configured-profile", profile.getName() );
 		assertFalse( profiles.hasNext() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
index 5f91b112c2..43e87b6fb5 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
@@ -1,198 +1,198 @@
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
 import java.util.Iterator;
 
 import org.junit.Test;
 
 import org.hibernate.CallbackException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.Interceptor;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.Type;
 
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertSame;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class SessionFactoryBuilderImplTest extends BaseUnitTestCase {
 
 	@Test
 	public void testGettingSessionFactoryBuilder() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		assertNotNull( sessionFactoryBuilder );
 		assertTrue( SessionFactoryBuilderImpl.class.isInstance( sessionFactoryBuilder ) );
 	}
 
 	@Test
 	public void testBuildSessionFactoryWithDefaultOptions() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
-		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
+		SessionFactory sessionFactory = sessionFactoryBuilder.build();
 		assertSame( EmptyInterceptor.INSTANCE, sessionFactory.getSessionFactoryOptions().getInterceptor() );
 		assertTrue( EntityNotFoundDelegate.class.isInstance(
 				sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate()
 		) );
 		sessionFactory.close();
 	}
 
 	@Test
 	public void testBuildSessionFactoryWithUpdatedOptions() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		Interceptor interceptor = new AnInterceptor();
 		EntityNotFoundDelegate entityNotFoundDelegate = new EntityNotFoundDelegate() {
 			@Override
 			public void handleEntityNotFound(String entityName, Serializable id) {
 				throw new ObjectNotFoundException( id, entityName );
 			}
 		};
 		sessionFactoryBuilder.with( interceptor );
 		sessionFactoryBuilder.with( entityNotFoundDelegate );
-		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
+		SessionFactory sessionFactory = sessionFactoryBuilder.build();
 		assertSame( interceptor, sessionFactory.getSessionFactoryOptions().getInterceptor() );
 		assertSame( entityNotFoundDelegate, sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate() );
 		sessionFactory.close();
 	}
 
 	private SessionFactoryBuilder getSessionFactoryBuilder() {
-		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().buildServiceRegistry() );
+		MetadataSources sources = new MetadataSources( new StandardServiceRegistryBuilder().build() );
 		sources.addAnnotatedClass( SimpleEntity.class );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		return  metadata.getSessionFactoryBuilder();
 	}
 
 	private static class AnInterceptor implements Interceptor {
 		private static final Interceptor INSTANCE = EmptyInterceptor.INSTANCE;
 
 		@Override
 		public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onLoad( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onFlushDirty( entity, id, currentState, previousState, propertyNames, types );
 		}
 
 		@Override
 		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onSave( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			INSTANCE.onDelete( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionRecreate( collection, key );
 		}
 
 		@Override
 		public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionRemove( collection, key );
 		}
 
 		@Override
 		public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionUpdate( collection, key );
 		}
 
 		@Override
 		public void preFlush(Iterator entities) throws CallbackException {
 			INSTANCE.preFlush( entities );
 		}
 
 		@Override
 		public void postFlush(Iterator entities) throws CallbackException {
 			INSTANCE.postFlush( entities );
 		}
 
 		@Override
 		public Boolean isTransient(Object entity) {
 			return INSTANCE.isTransient( entity );
 		}
 
 		@Override
 		public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
 			return INSTANCE.findDirty( entity, id, currentState, previousState, propertyNames, types );
 		}
 
 		@Override
 		public Object instantiate(String entityName, EntityMode entityMode, Serializable id)
 				throws CallbackException {
 			return INSTANCE.instantiate( entityName, entityMode, id );
 		}
 
 		@Override
 		public String getEntityName(Object object) throws CallbackException {
 			return INSTANCE.getEntityName( object );
 		}
 
 		@Override
 		public Object getEntity(String entityName, Serializable id) throws CallbackException {
 			return INSTANCE.getEntity( entityName, id );
 		}
 
 		@Override
 		public void afterTransactionBegin(Transaction tx) {
 			INSTANCE.afterTransactionBegin( tx );
 		}
 
 		@Override
 		public void beforeTransactionCompletion(Transaction tx) {
 			INSTANCE.beforeTransactionCompletion( tx );
 		}
 
 		@Override
 		public void afterTransactionCompletion(Transaction tx) {
 			INSTANCE.afterTransactionCompletion( tx );
 		}
 
 		@Override
 		public String onPrepareStatement(String sql) {
 			return INSTANCE.onPrepareStatement( sql );
 		}
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/embeddables/EmbeddableIntegratorTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/embeddables/EmbeddableIntegratorTest.java
index 6f3defb65a..24db6ca2e5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/embeddables/EmbeddableIntegratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/embeddables/EmbeddableIntegratorTest.java
@@ -1,118 +1,118 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.embeddables;
 
 import static org.junit.Assert.assertEquals;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.exception.GenericJDBCException;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 /**
  * @author Chris Pheby
  */
 @RequiresDialect(H2Dialect.class)
 public class EmbeddableIntegratorTest extends BaseUnitTestCase {
 
 	/**
 	 * Throws a mapping exception because DollarValue is not mapped
 	 */
 	@Test(expected=GenericJDBCException.class)
 	public void testWithoutIntegrator() {
 		
 		ServiceRegistry reg = new StandardServiceRegistryBuilder(new BootstrapServiceRegistryImpl())
-		.buildServiceRegistry();
+		.build();
 		
 		SessionFactory sf = new Configuration()
 		.addAnnotatedClass( Investor.class )
 
 		.buildSessionFactory(reg);
 		
 		Session sess = sf.openSession();
 		Investor myInv = getInvestor();
 		myInv.setId(1L);
 		
 		sess.save(myInv);
 		sess.flush();
 		sess.clear();
 		
 		Investor inv = (Investor) sess.get(Investor.class, 1L);
 		assertEquals(new BigDecimal("100"), inv.getInvestments().get(0).getAmount().getAmount());
 		
 		sess.close();
 	}
 
 	@Test
 	public void testWithIntegrator() {
 		ServiceRegistry reg = new StandardServiceRegistryBuilder(
 				new BootstrapServiceRegistryBuilder().with( new InvestorIntegrator() ).build()
-		).buildServiceRegistry();
+		).build();
 		
 		SessionFactory sf = new Configuration()
 		.addAnnotatedClass( Investor.class )
 
 		.setProperty("hibernate.hbm2ddl.auto", "create-drop")
 		.buildSessionFactory(reg);
 		
 		Session sess = sf.openSession();
 		Investor myInv = getInvestor();
 		myInv.setId(2L);
 		
 		sess.save(myInv);
 		sess.flush();
 		sess.clear();
 		
 		Investor inv = (Investor) sess.get(Investor.class, 2L);
 		assertEquals(new BigDecimal("100"), inv.getInvestments().get(0).getAmount().getAmount());
 		
 		sess.close();
 	}
 	
 	private Investor getInvestor() {
 		Investor i = new Investor();
 		List<Investment> investments = new ArrayList<Investment>();
 		Investment i1 = new Investment();
 		i1.setAmount(new DollarValue(new BigDecimal("100")));
 		i1.setDate(new MyDate(new Date()));
 		i1.setDescription("Test Investment");
 		investments.add(i1);
 		i.setInvestments(investments);
 		
 		return i;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
index 706391588b..7378540fd2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
@@ -1,121 +1,121 @@
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
 
 import org.junit.Test;
 
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.ServiceRegistry;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest extends BaseUnitTestCase {
 	@Test
 	public void testPersisterClassProvider() throws Exception {
 
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
-				.buildServiceRegistry();
+				.build();
 		//no exception as the GoofyPersisterClassProvider is not set
 		SessionFactory sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 		sessionFactory.close();
 		StandardServiceRegistryBuilder.destroy( serviceRegistry );
 
 		serviceRegistry = new StandardServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
 				.addService( PersisterClassResolver.class, new GoofyPersisterClassProvider() )
-				.buildServiceRegistry();
+				.build();
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
             fail("The entity persister should be overridden");
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The entity persister should be overridden",
 					GoofyPersisterClassProvider.NoopEntityPersister.class,
 					( (GoofyException) e.getCause() ).getValue()
 			);
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Portal.class );
 		cfg.addAnnotatedClass( Window.class );
 		serviceRegistry = new StandardServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
 				.addService( PersisterClassResolver.class, new GoofyPersisterClassProvider() )
-				.buildServiceRegistry();
+				.build();
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
             fail("The collection persister should be overridden but not the entity persister");
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The collection persister should be overridden but not the entity persister",
 					GoofyPersisterClassProvider.NoopCollectionPersister.class,
 					( (GoofyException) e.getCause() ).getValue() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 
 
         cfg = new Configuration();
 		cfg.addAnnotatedClass( Tree.class );
 		cfg.addAnnotatedClass( Palmtree.class );
 		serviceRegistry = new StandardServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
 				.addService( PersisterClassResolver.class, new GoofyPersisterClassProvider() )
-				.buildServiceRegistry();
+				.build();
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
             fail("The entity persisters should be overridden in a class hierarchy");
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The entity persisters should be overridden in a class hierarchy",
 					GoofyPersisterClassProvider.NoopEntityPersister.class,
 					( (GoofyException) e.getCause() ).getValue() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/internal/BatchingTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/internal/BatchingTest.java
index 2db01943b3..9f504c0f46 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/internal/BatchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/internal/BatchingTest.java
@@ -1,198 +1,197 @@
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
 package org.hibernate.test.jdbc.internal;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 import java.sql.PreparedStatement;
 import java.sql.Statement;
 
 import org.hibernate.Session;
-import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
 import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
 import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.test.common.JournalingBatchObserver;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public class BatchingTest extends BaseCoreFunctionalTestCase implements BatchKey {
 	@Override
 	public int getBatchedStatementCount() {
 		return 1;
 	}
 
 	@Override
 	public Expectation getExpectation() {
 		return Expectations.BASIC;
 	}
 
 	@Test
 	public void testNonBatchingUsage() throws Exception {
 		Session session = openSession();
 		SessionImplementor sessionImpl = (SessionImplementor) session;
 		
 		TransactionCoordinator transactionCoordinator = sessionImpl.getTransactionCoordinator();
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 
 		// set up some tables to use
 		Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
 		jdbcCoordinator.getResultSetReturn().execute( statement, "drop table SANDBOX_JDBC_TST if exists" );
 		jdbcCoordinator.getResultSetReturn().execute( statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		jdbcCoordinator.release( statement );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 
 		final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
 
 		final BatchBuilder batchBuilder = new BatchBuilderImpl( -1 );
 		final BatchKey batchKey = new BasicBatchKey( "this", Expectations.BASIC );
 		final Batch insertBatch = batchBuilder.buildBatch( batchKey, jdbcCoordinator );
 
 		final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
 		insertBatch.addObserver( batchObserver );
 
 		assertTrue( "unexpected Batch impl", NonBatchingBatch.class.isInstance( insertBatch ) );
 		PreparedStatement insert = insertBatch.getBatchStatement( insertSql, false );
 		insert.setLong( 1, 1 );
 		insert.setString( 2, "name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		session.close();
 	}
 
 	@Test
 	public void testBatchingUsage() throws Exception {
 		Session session = openSession();
 		SessionImplementor sessionImpl = (SessionImplementor) session;
 		
 		TransactionCoordinator transactionCoordinator = sessionImpl.getTransactionCoordinator();
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 
 		// set up some tables to use
 		Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
 		jdbcCoordinator.getResultSetReturn().execute( statement, "drop table SANDBOX_JDBC_TST if exists" );
 		jdbcCoordinator.getResultSetReturn().execute( statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		jdbcCoordinator.release( statement );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 
 		final BatchBuilder batchBuilder = new BatchBuilderImpl( 2 );
 		final BatchKey batchKey = new BasicBatchKey( "this", Expectations.BASIC );
 		final Batch insertBatch = batchBuilder.buildBatch( batchKey, jdbcCoordinator );
 		assertTrue( "unexpected Batch impl", BatchingBatch.class.isInstance( insertBatch ) );
 
 		final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
 		insertBatch.addObserver( batchObserver );
 
 		final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
 
 		PreparedStatement insert = insertBatch.getBatchStatement( insertSql, false );
 		insert.setLong( 1, 1 );
 		insert.setString( 2, "name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		assertTrue( jdbcCoordinator.hasRegisteredResources() );
 
 		PreparedStatement insert2 = insertBatch.getBatchStatement( insertSql, false );
 		assertSame( insert, insert2 );
 		insert = insert2;
 		insert.setLong( 1, 2 );
 		insert.setString( 2, "another name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertTrue( jdbcCoordinator.hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		session.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java
index b84e8e4435..f05fde3e99 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/ConfigurationValidationTest.java
@@ -1,55 +1,55 @@
 package org.hibernate.test.multitenancy;
 
 import org.junit.Test;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
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
 		ServiceRegistryImplementor serviceRegistry = (ServiceRegistryImplementor) new StandardServiceRegistryBuilder()
-				.applySettings( cfg.getProperties() ).buildServiceRegistry();
+				.applySettings( cfg.getProperties() ).build();
 		cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@Test
 	public void testReleaseMode() {
 		Configuration cfg = new Configuration();
 		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA );
 		cfg.getProperties().put( Environment.RELEASE_CONNECTIONS, ConnectionReleaseMode.AFTER_STATEMENT.name() );
 		cfg.buildMappings();
 
 		ServiceRegistryImplementor serviceRegistry = (ServiceRegistryImplementor) new StandardServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
 				.addService(
 						MultiTenantConnectionProvider.class,
 						new TestingConnectionProvider(
 								new TestingConnectionProvider.NamedConnectionProviderPair(
 										"acme",
 										ConnectionProviderBuilder.buildConnectionProvider( "acme" )
 								)
 						)
 				)
-				.buildServiceRegistry();
+				.build();
 
 		cfg.buildSessionFactory( serviceRegistry );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
index 5ddcfc1837..86bde2f2e1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
@@ -1,300 +1,300 @@
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
 package org.hibernate.test.multitenancy.schema;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import org.junit.After;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.testing.cache.CachingRegionFactory;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.tool.hbm2ddl.ConnectionHelper;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 
 /**
  * @author Steve Ebersole
  */
 public class SchemaBasedMultiTenancyTest extends BaseUnitTestCase {
 	private DriverManagerConnectionProviderImpl acmeProvider;
 	private DriverManagerConnectionProviderImpl jbossProvider;
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	protected SessionFactoryImplementor sessionFactory;
 
 	@Before
 	public void setUp() {
 		AbstractMultiTenantConnectionProvider multiTenantConnectionProvider = buildMultiTenantConnectionProvider();
 		Configuration cfg = buildConfiguration();
 
 		serviceRegistry = (ServiceRegistryImplementor) new StandardServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
 				.addService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider )
-				.buildServiceRegistry();
+				.build();
 
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	protected Configuration buildConfiguration() {
 		Configuration cfg = new Configuration();
 		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA );
 		cfg.setProperty( Environment.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName() );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.addAnnotatedClass( Customer.class );
 
 		cfg.buildMappings();
 		RootClass meta = (RootClass) cfg.getClassMapping( Customer.class.getName() );
 		meta.setCacheConcurrencyStrategy( "read-write" );
 
 		// do the acme export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = acmeProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						acmeProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute(		 // so stupid...
 						   false,	 // do not script the export (write it to file)
 						   true,	 // do run it against the database
 						   false,	 // do not *just* perform the drop
 						   false	// do not *just* perform the create
 		);
 
 		// do the jboss export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = jbossProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						jbossProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute( 		// so stupid...
 						   false, 	// do not script the export (write it to file)
 						   true, 	// do run it against the database
 						   false, 	// do not *just* perform the drop
 						   false	// do not *just* perform the create
 		);
 		return cfg;
 	}
 
 	private AbstractMultiTenantConnectionProvider buildMultiTenantConnectionProvider() {
 		acmeProvider = ConnectionProviderBuilder.buildConnectionProvider( "acme" );
 		jbossProvider = ConnectionProviderBuilder.buildConnectionProvider( "jboss" );
 		return new AbstractMultiTenantConnectionProvider() {
 			@Override
 			protected ConnectionProvider getAnyConnectionProvider() {
 				return acmeProvider;
 			}
 
 			@Override
 			protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
 				if ( "acme".equals( tenantIdentifier ) ) {
 					return acmeProvider;
 				}
 				else if ( "jboss".equals( tenantIdentifier ) ) {
 					return jbossProvider;
 				}
 				throw new HibernateException( "Unknown tenant identifier" );
 			}
 		};
 	}
 
 	@After
 	public void tearDown() {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 		if ( jbossProvider != null ) {
 			jbossProvider.stop();
 		}
 		if ( acmeProvider != null ) {
 			acmeProvider.stop();
 		}
 	}
 
 	@Test
 	public void testBasicExpectedBehavior() {
 		Session session = getNewSession("jboss");
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = getNewSession("acme");
 		try {
 			session.beginTransaction();
 			Customer check = (Customer) session.get( Customer.class, steve.getId() );
 			Assert.assertNull( "tenancy not properly isolated", check );
 		}
 		finally {
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = getNewSession("jboss");
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testSameIdentifiers() {
 		// create a customer 'steve' in jboss
 		Session session = getNewSession("jboss");
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		// now, create a customer 'john' in acme
 		session = getNewSession("acme");
 		session.beginTransaction();
 		Customer john = new Customer( 1L, "john" );
 		session.save( john );
 		session.getTransaction().commit();
 		session.close();
 
 		sessionFactory.getStatisticsImplementor().clear();
 
 		// make sure we get the correct people back, from cache
 		// first, jboss
 		{
 			session = getNewSession("jboss");
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = getNewSession("acme");
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		// make sure the same works from datastore too
 		sessionFactory.getStatisticsImplementor().clear();
 		sessionFactory.getCache().evictEntityRegions();
 		// first jboss
 		{
 			session = getNewSession("jboss");
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = getNewSession("acme");
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = getNewSession("jboss");
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = getNewSession("acme");
 		session.beginTransaction();
 		session.delete( john );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	protected Session getNewSession(String tenant) {
 		return sessionFactory.withOptions().tenantIdentifier( tenant ).openSession();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
index f92c6f060a..5a3dc82bc2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
@@ -1,106 +1,105 @@
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
 package org.hibernate.test.service;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 import java.util.Properties;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.testing.RequiresDialect;
-import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 /**
  * @author Steve Ebersole
  */
 @RequiresDialect( H2Dialect.class )
 public class ServiceBootstrappingTest extends BaseUnitTestCase {
 	@Test
 	public void testBasicBuild() {
 		StandardServiceRegistryImpl serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
-				.buildServiceRegistry();
+				.build();
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertFalse( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithLogging() {
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.put( Environment.SHOW_SQL, "true" );
 
 		StandardServiceRegistryImpl serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( props )
-				.buildServiceRegistry();
+				.build();
 
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertTrue( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithServiceOverride() {
 		StandardServiceRegistryImpl serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
-				.buildServiceRegistry();
+				.build();
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.setProperty( Environment.DIALECT, H2Dialect.class.getName() );
 
 		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( props )
 				.addService( ConnectionProvider.class, new UserSuppliedConnectionProviderImpl() )
-				.buildServiceRegistry();
+				.build();
 		jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( UserSuppliedConnectionProviderImpl.class ) );
 
 		serviceRegistry.destroy();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/smoke/SessionFactoryBuildingUseCasesTest.java b/hibernate-core/src/test/java/org/hibernate/test/smoke/SessionFactoryBuildingUseCasesTest.java
new file mode 100644
index 0000000000..5432bc6d6d
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/smoke/SessionFactoryBuildingUseCasesTest.java
@@ -0,0 +1,144 @@
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
+package org.hibernate.test.smoke;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+import org.hibernate.Session;
+import org.hibernate.SessionFactory;
+import org.hibernate.boot.registry.BootstrapServiceRegistry;
+import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.metamodel.Metadata;
+import org.hibernate.metamodel.MetadataSources;
+
+import org.junit.Test;
+
+import org.hibernate.testing.FailureExpected;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+/**
+ * @author Steve Ebersole
+ */
+@TestForIssue( jiraKey = "HHH-7580" )
+public class SessionFactoryBuildingUseCasesTest extends BaseUnitTestCase {
+	@Test
+	public void testTheSimplestUseCase() {
+		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
+				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
+				.build();
+		MetadataSources metadataSources = new MetadataSources( registry ).addAnnotatedClass( MyEntity.class );
+		SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
+
+		useSessionFactory( sessionFactory );
+
+		sessionFactory.close();
+		StandardServiceRegistryBuilder.destroy( registry );
+	}
+
+	private void useSessionFactory(SessionFactory sessionFactory) {
+		Session session = sessionFactory.openSession();
+		session.beginTransaction();
+		session.createQuery( "from MyEntity" ).list();
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testStillSimpleOnePhaseUseCase() {
+		BootstrapServiceRegistry bootRegistry = new BootstrapServiceRegistryBuilder().build();
+		StandardServiceRegistry registry = new StandardServiceRegistryBuilder( bootRegistry )
+				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
+				.build();
+		MetadataSources metadataSources = new MetadataSources( registry ).addAnnotatedClass( MyEntity.class );
+		SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
+
+		useSessionFactory( sessionFactory );
+
+		sessionFactory.close();
+		StandardServiceRegistryBuilder.destroy( registry );
+	}
+
+	@Test
+	public void testSimpleTwoPhaseUseCase() {
+		BootstrapServiceRegistry bootRegistry = new BootstrapServiceRegistryBuilder().build();
+		MetadataSources metadataSources = new MetadataSources( bootRegistry ).addAnnotatedClass( MyEntity.class );
+
+		StandardServiceRegistry registry = new StandardServiceRegistryBuilder( bootRegistry )
+				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
+				.build();
+		SessionFactory sessionFactory = metadataSources.buildMetadata( registry ).buildSessionFactory();
+
+		useSessionFactory( sessionFactory );
+
+		sessionFactory.close();
+		StandardServiceRegistryBuilder.destroy( registry );
+	}
+
+	@Test
+	public void testFullTwoPhaseUseCase() {
+		BootstrapServiceRegistry bootRegistry = new BootstrapServiceRegistryBuilder().build();
+		MetadataSources metadataSources = new MetadataSources( bootRegistry ).addAnnotatedClass( MyEntity.class );
+
+		StandardServiceRegistry registry = new StandardServiceRegistryBuilder( bootRegistry )
+				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
+				.build();
+		Metadata metadata = metadataSources.getMetadataBuilder( registry ).build();
+		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build(); // todo rename
+
+		useSessionFactory( sessionFactory );
+
+		sessionFactory.close();
+		StandardServiceRegistryBuilder.destroy( registry );
+	}
+
+	@Test
+	@FailureExpected( jiraKey = "HHH-7580" )
+	public void testInvalidQuasiUseCase() {
+		BootstrapServiceRegistry bootRegistry = new BootstrapServiceRegistryBuilder().build();
+		MetadataSources metadataSources = new MetadataSources( bootRegistry ).addAnnotatedClass( MyEntity.class );
+
+		StandardServiceRegistry registry = new StandardServiceRegistryBuilder( bootRegistry )
+				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
+				.build();
+		SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
+
+		useSessionFactory( sessionFactory );
+
+		sessionFactory.close();
+		StandardServiceRegistryBuilder.destroy( registry );
+	}
+
+	@Entity( name = "MyEntity" )
+	@Table( name = "TST_ENT" )
+	public static class MyEntity {
+		@Id
+		private Long id;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
index 629381f3e1..42a4f7e03b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
@@ -1,136 +1,136 @@
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
 package org.hibernate.test.transaction.jdbc;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 /**
  * @author Steve Ebersole
  */
 public class TestExpectedUsage extends BaseUnitTestCase {
 	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
 		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
-				.buildServiceRegistry();
+				.build();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		};
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 
 		// set up some tables to use
 		Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
 		jdbcCoordinator.getResultSetReturn().execute( statement, "drop table SANDBOX_JDBC_TST if exists" );
 		jdbcCoordinator.getResultSetReturn().execute( statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		jdbcCoordinator.release( statement );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		try {
 			PreparedStatement ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			jdbcCoordinator.getResultSetReturn().execute( ps );
 			assertTrue( jdbcCoordinator.hasRegisteredResources() );
 			jdbcCoordinator.release( ps );
 			assertFalse( jdbcCoordinator.hasRegisteredResources() );
 
 			ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			jdbcCoordinator.getResultSetReturn().extract( ps );
 			ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "delete from SANDBOX_JDBC_TST" );
 			jdbcCoordinator.getResultSetReturn().execute( ps );
 			// lets forget to close these...
 			assertTrue( jdbcCoordinator.hasRegisteredResources() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// we should now have:
 			//		1) no resources because of after_transaction release mode
 			assertFalse( jdbcCoordinator.hasRegisteredResources() );
 			//		2) non-physically connected logical connection, again because of after_transaction release mode
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			//		3) transaction observer callbacks
 			assertEquals( 1, observer.getBeforeCompletions() );
 			assertEquals( 1, observer.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
index c395dbdb29..386b98e42f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
@@ -1,158 +1,158 @@
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
 package org.hibernate.test.transaction.jta;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 /**
  * Testing transaction handling when the JTA transaction facade is the driver.
  *
  * @author Steve Ebersole
  */
 public class BasicDrivingTest extends BaseUnitTestCase {
 	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName() );
 		TestingJtaBootstrap.prepare( configValues );
 		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( configValues )
-				.buildServiceRegistry();
+				.build();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 
 		// set up some tables to use
 		Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
 		jdbcCoordinator.getResultSetReturn().execute( statement, "drop table SANDBOX_JDBC_TST if exists" );
 		jdbcCoordinator.getResultSetReturn().execute( statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		jdbcCoordinator.release( statement );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 		assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		assertTrue( txn.isInitiator() );
 		try {
 			PreparedStatement ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			jdbcCoordinator.getResultSetReturn().execute( ps );
 			assertTrue( jdbcCoordinator.hasRegisteredResources() );
 			jdbcCoordinator.release( ps );
 			assertFalse( jdbcCoordinator.hasRegisteredResources() );
 
 			ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			jdbcCoordinator.getResultSetReturn().extract( ps );
 			ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "delete from SANDBOX_JDBC_TST" );
 			jdbcCoordinator.getResultSetReturn().execute( ps );
 			// lets forget to close these...
 			assertTrue( jdbcCoordinator.hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// we should now have:
 			//		1) no resources because of after_transaction release mode
 			assertFalse( jdbcCoordinator.hasRegisteredResources() );
 			//		2) non-physically connected logical connection, again because of after_transaction release mode
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			//		3) transaction observer callbacks
 			assertEquals( 1, observer.getBeforeCompletions() );
 			assertEquals( 1, observer.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			try {
 				serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			throw reThrowable;
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
index ec8a0293b7..60a33288a0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
@@ -1,176 +1,176 @@
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
 package org.hibernate.test.transaction.jta;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 /**
  * Testing transaction facade handling when the transaction is being driven by something other than the facade.
  *
  * @author Steve Ebersole
  */
 @RequiresDialect(H2Dialect.class)
 public class ManagedDrivingTest extends BaseUnitTestCase {
 	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		TestingJtaBootstrap.prepare( configValues );
 		configValues.put( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName() );
 
 		serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( configValues )
-				.buildServiceRegistry();
+				.build();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_STATEMENT;
 			}
 		};
 
 		final TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		final JournalingTransactionObserver transactionObserver = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( transactionObserver );
 
 		JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 
 		// set up some tables to use
 		Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
 		jdbcCoordinator.getResultSetReturn().execute( statement, "drop table SANDBOX_JDBC_TST if exists" );
 		jdbcCoordinator.getResultSetReturn().execute( statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( jdbcCoordinator.hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		jdbcCoordinator.release( statement );
 		assertFalse( jdbcCoordinator.hasRegisteredResources() );
 		assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 
 		JtaPlatform instance = serviceRegistry.getService( JtaPlatform.class );
 		TransactionManager transactionManager = instance.retrieveTransactionManager();
 
 		// start the cmt
 		transactionManager.begin();
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, transactionObserver.getBegins() );
 		assertFalse( txn.isInitiator() );
 		try {
 			PreparedStatement ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			jdbcCoordinator.getResultSetReturn().execute( ps );
 			assertTrue( jdbcCoordinator.hasRegisteredResources() );
 			jdbcCoordinator.release( ps );
 			assertFalse( jdbcCoordinator.hasRegisteredResources() );
 
 			ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			jdbcCoordinator.getResultSetReturn().extract( ps );
 			ps = jdbcCoordinator.getStatementPreparer().prepareStatement( "delete from SANDBOX_JDBC_TST" );
 			jdbcCoordinator.getResultSetReturn().execute( ps );
 			// lets forget to close these...
 			assertTrue( jdbcCoordinator.hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// since txn is not a driver, nothing should have changed...
 			assertTrue( jdbcCoordinator.hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 0, transactionObserver.getBeforeCompletions() );
 			assertEquals( 0, transactionObserver.getAfterCompletions() );
 
 			transactionManager.commit();
 			assertFalse( jdbcCoordinator.hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 1, transactionObserver.getBeforeCompletions() );
 			assertEquals( 1, transactionObserver.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			throw reThrowable;
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
index 6d729f297c..5f9fe063b2 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
@@ -1,1228 +1,1227 @@
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
 package org.hibernate.jpa.boot.internal;
 
-import javax.persistence.AttributeConverter;
-import javax.persistence.Converter;
-import javax.persistence.Embeddable;
-import javax.persistence.Entity;
-import javax.persistence.EntityManagerFactory;
-import javax.persistence.EntityNotFoundException;
-import javax.persistence.MappedSuperclass;
-import javax.persistence.PersistenceException;
-import javax.persistence.spi.PersistenceUnitTransactionType;
-import javax.sql.DataSource;
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
-import org.jboss.jandex.AnnotationInstance;
-import org.jboss.jandex.ClassInfo;
-import org.jboss.jandex.CompositeIndex;
-import org.jboss.jandex.DotName;
-import org.jboss.jandex.Index;
-import org.jboss.jandex.IndexView;
-import org.jboss.jandex.Indexer;
-
-import org.jboss.logging.Logger;
+import javax.persistence.AttributeConverter;
+import javax.persistence.Converter;
+import javax.persistence.Embeddable;
+import javax.persistence.Entity;
+import javax.persistence.EntityManagerFactory;
+import javax.persistence.EntityNotFoundException;
+import javax.persistence.MappedSuperclass;
+import javax.persistence.PersistenceException;
+import javax.persistence.spi.PersistenceUnitTransactionType;
+import javax.sql.DataSource;
 
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
+import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
-import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
-import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
+import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.jaxb.cfg.JaxbHibernateConfiguration;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.ValueHolder;
+import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
 import org.hibernate.jpa.boot.spi.IntegratorProvider;
 import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
+import org.hibernate.jpa.event.spi.JpaIntegrator;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
-import org.hibernate.jpa.event.spi.JpaIntegrator;
 import org.hibernate.jpa.internal.util.LogHelper;
 import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
 import org.hibernate.jpa.packaging.internal.NativeScanner;
 import org.hibernate.jpa.packaging.spi.NamedInputStream;
 import org.hibernate.jpa.packaging.spi.Scanner;
 import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.internal.JACCConfiguration;
-import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.service.ConfigLoader;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.jboss.jandex.AnnotationInstance;
+import org.jboss.jandex.ClassInfo;
+import org.jboss.jandex.CompositeIndex;
+import org.jboss.jandex.DotName;
+import org.jboss.jandex.Index;
+import org.jboss.jandex.IndexView;
+import org.jboss.jandex.Indexer;
+import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryBuilderImpl implements EntityManagerFactoryBuilder {
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
 			EntityManagerMessageLogger.class,
 			EntityManagerFactoryBuilderImpl.class.getName()
 	);
 
 	private static final String META_INF_ORM_XML = "META-INF/orm.xml";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// New settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	/**
 	 * Names a {@link IntegratorProvider}
 	 */
 	public static final String INTEGRATOR_PROVIDER = "hibernate.integrator_provider";
 
 	/**
 	 * Names a Jandex {@link Index} instance to use.
 	 */
 	public static final String JANDEX_INDEX = "hibernate.jandex_index";
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Explicit "injectables"
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private Object validatorFactory;
 	private DataSource dataSource;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final PersistenceUnitDescriptor persistenceUnit;
 	private final SettingsImpl settings = new SettingsImpl();
 	private final StandardServiceRegistryBuilder serviceRegistryBuilder;
 	private final Map configurationValues;
 
 	private final List<JaccDefinition> jaccDefinitions = new ArrayList<JaccDefinition>();
 	private final List<CacheRegionDefinition> cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
 	// todo : would much prefer this as a local variable...
 	private final List<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping> cfgXmlNamedMappings = new ArrayList<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping>();
 	private Interceptor sessionFactoryInterceptor;
 	private NamingStrategy namingStrategy;
 	private SessionFactoryObserver suppliedSessionFactoryObserver;
 
 	private MetadataSources metadataSources;
 	private Configuration hibernateConfiguration;
 
 	private static EntityNotFoundDelegate jpaEntityNotFoundDelegate = new JpaEntityNotFoundDelegate();
 
 	private static class JpaEntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException( "Unable to find " + entityName  + " with id " + id );
 		}
 	}
 
 	public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
 		LogHelper.logPersistenceUnitInformation( persistenceUnit );
 
 		this.persistenceUnit = persistenceUnit;
 
 		if ( integrationSettings == null ) {
 			integrationSettings = Collections.emptyMap();
 		}
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// First we build the boot-strap service registry, which mainly handles class loader interactions
 		final BootstrapServiceRegistry bootstrapServiceRegistry = buildBootstrapServiceRegistry( integrationSettings );
 		// And the main service registry.  This is needed to start adding configuration values, etc
 		this.serviceRegistryBuilder = new StandardServiceRegistryBuilder( bootstrapServiceRegistry );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Next we build a merged map of all the configuration values
 		this.configurationValues = mergePropertySources( persistenceUnit, integrationSettings, bootstrapServiceRegistry );
 		// add all merged configuration values into the service registry builder
 		this.serviceRegistryBuilder.applySettings( configurationValues );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Next we do a preliminary pass at metadata processing, which involves:
 		//		1) scanning
 		ScanResult scanResult = scan( bootstrapServiceRegistry );
 		//		2) building a Jandex index
 		Set<String> collectedManagedClassNames = collectManagedClassNames( scanResult );
 		IndexView jandexIndex = locateOrBuildJandexIndex( collectedManagedClassNames, scanResult.getPackageNames(), bootstrapServiceRegistry );
 		//		3) building "metadata sources" to keep for later to use in building the SessionFactory
 		metadataSources = prepareMetadataSources( jandexIndex, collectedManagedClassNames, scanResult, bootstrapServiceRegistry );
 
 		withValidatorFactory( configurationValues.get( AvailableSettings.VALIDATION_FACTORY ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// push back class transformation to the environment; for the time being this only has any effect in EE
 		// container situations, calling back into PersistenceUnitInfo#addClassTransformer
 		final boolean useClassTransformer = "true".equals( configurationValues.remove( AvailableSettings.USE_CLASS_ENHANCER ) );
 		if ( useClassTransformer ) {
 			persistenceUnit.pushClassTransformer( metadataSources.collectMappingClassNames() );
 		}
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// temporary!
 	@SuppressWarnings("unchecked")
 	public Map getConfigurationValues() {
 		return Collections.unmodifiableMap( configurationValues );
 	}
 
 	public Configuration getHibernateConfiguration() {
 		return hibernateConfiguration;
 	}
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	@SuppressWarnings("unchecked")
 	private MetadataSources prepareMetadataSources(
 			IndexView jandexIndex,
 			Set<String> collectedManagedClassNames,
 			ScanResult scanResult,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		// todo : this needs to tie into the metamodel branch...
 		MetadataSources metadataSources = new MetadataSources();
 
 		for ( String className : collectedManagedClassNames ) {
 			final ClassInfo classInfo = jandexIndex.getClassByName( DotName.createSimple( className ) );
 			if ( classInfo == null ) {
 				// Not really sure what this means.  Most likely it is explicitly listed in the persistence unit,
 				// but mapped via mapping file.  Anyway assume its a mapping class...
 				metadataSources.annotatedMappingClassNames.add( className );
 				continue;
 			}
 
 			// logic here assumes an entity is not also a converter...
 			AnnotationInstance converterAnnotation = JandexHelper.getSingleAnnotation(
 					classInfo.annotations(),
 					JPADotNames.CONVERTER
 			);
 			if ( converterAnnotation != null ) {
 				metadataSources.converterDescriptors.add(
 						new MetadataSources.ConverterDescriptor(
 								className,
 								JandexHelper.getValue( converterAnnotation, "autoApply", boolean.class )
 						)
 				);
 			}
 			else {
 				metadataSources.annotatedMappingClassNames.add( className );
 			}
 		}
 
 		metadataSources.packageNames.addAll( scanResult.getPackageNames() );
 
 		metadataSources.namedMappingFileInputStreams.addAll( scanResult.getHbmFiles() );
 
 		metadataSources.mappingFileResources.addAll( scanResult.getMappingFiles() );
 		final String explicitHbmXmls = (String) configurationValues.remove( AvailableSettings.HBXML_FILES );
 		if ( explicitHbmXmls != null ) {
 			metadataSources.mappingFileResources.addAll( Arrays.asList( StringHelper.split( ", ", explicitHbmXmls ) ) );
 		}
 		final List<String> explicitOrmXml = (List<String>) configurationValues.remove( AvailableSettings.XML_FILE_NAMES );
 		if ( explicitOrmXml != null ) {
 			metadataSources.mappingFileResources.addAll( explicitOrmXml );
 		}
 
 		return metadataSources;
 	}
 
 	private Set<String> collectManagedClassNames(ScanResult scanResult) {
 		Set<String> collectedNames = new HashSet<String>();
 		if ( persistenceUnit.getManagedClassNames() != null ) {
 			collectedNames.addAll( persistenceUnit.getManagedClassNames() );
 		}
 		collectedNames.addAll( scanResult.getManagedClassNames() );
 		return collectedNames;
 	}
 
 	private IndexView locateOrBuildJandexIndex(
 			Set<String> collectedManagedClassNames,
 			List<String> packageNames,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		// for now create a whole new Index to work with, eventually we need to:
 		//		1) accept an Index as an incoming config value
 		//		2) pass that Index along to the metamodel code...
 		//
 		// (1) is mocked up here, but JBoss AS does not currently pass in any Index to use...
 		IndexView jandexIndex = (IndexView) configurationValues.get( JANDEX_INDEX );
 		if ( jandexIndex == null ) {
 			jandexIndex = buildJandexIndex( collectedManagedClassNames, packageNames, bootstrapServiceRegistry );
 		}
 		return jandexIndex;
 	}
 
 	private IndexView buildJandexIndex(Set<String> classNamesSource, List<String> packageNames, BootstrapServiceRegistry bootstrapServiceRegistry) {
 		Indexer indexer = new Indexer();
 
 		for ( String className : classNamesSource ) {
 			indexResource( className.replace( '.', '/' ) + ".class", indexer, bootstrapServiceRegistry );
 		}
 
 		// add package-info from the configured packages
 		for ( String packageName : packageNames ) {
 			indexResource( packageName.replace( '.', '/' ) + "/package-info.class", indexer, bootstrapServiceRegistry );
 		}
 
 		// for now we just skip entities defined in (1) orm.xml files and (2) hbm.xml files.  this part really needs
 		// metamodel branch...
 
 		// for now, we also need to wrap this in a CompositeIndex until Jandex is updated to use a common interface
 		// between the 2...
 		return CompositeIndex.create( indexer.complete() );
 	}
 
 	private void indexResource(String resourceName, Indexer indexer, BootstrapServiceRegistry bootstrapServiceRegistry) {
 		InputStream stream = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResourceStream( resourceName );
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw persistenceException( "Unable to open input stream for resource " + resourceName, e );
 		}
 	}
 
 	/**
 	 * Builds the {@link BootstrapServiceRegistry} used to eventually build the {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}; mainly
 	 * used here during instantiation to define class-loading behavior.
 	 *
 	 * @param integrationSettings Any integration settings passed by the EE container or SE application
 	 *
 	 * @return The built BootstrapServiceRegistry
 	 */
 	private BootstrapServiceRegistry buildBootstrapServiceRegistry(Map integrationSettings) {
 		final BootstrapServiceRegistryBuilder bootstrapServiceRegistryBuilder = new BootstrapServiceRegistryBuilder();
 		bootstrapServiceRegistryBuilder.with( new JpaIntegrator() );
 
 		final IntegratorProvider integratorProvider = (IntegratorProvider) integrationSettings.get( INTEGRATOR_PROVIDER );
 		if ( integratorProvider != null ) {
 			integrationSettings.remove( INTEGRATOR_PROVIDER );
 			for ( Integrator integrator : integratorProvider.getIntegrators() ) {
 				bootstrapServiceRegistryBuilder.with( integrator );
 			}
 		}
 
 		ClassLoader classLoader = (ClassLoader) integrationSettings.get( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		if ( classLoader != null ) {
 			integrationSettings.remove( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		}
 		else {
 			classLoader = persistenceUnit.getClassLoader();
 		}
 		bootstrapServiceRegistryBuilder.withApplicationClassLoader( classLoader );
 
 		return bootstrapServiceRegistryBuilder.build();
 	}
 
 	@SuppressWarnings("unchecked")
 	private Map mergePropertySources(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map integrationSettings,
 			final BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Map merged = new HashMap();
 		// first, apply persistence.xml-defined settings
 		if ( persistenceUnit.getProperties() != null ) {
 			merged.putAll( persistenceUnit.getProperties() );
 		}
 
 		merged.put( AvailableSettings.PERSISTENCE_UNIT_NAME, persistenceUnit.getName() );
 
 		// see if the persistence.xml settings named a Hibernate config file....
 		final ValueHolder<ConfigLoader> configLoaderHolder = new ValueHolder<ConfigLoader>(
 				new ValueHolder.DeferredInitializer<ConfigLoader>() {
 					@Override
 					public ConfigLoader initialize() {
 						return new ConfigLoader( bootstrapServiceRegistry );
 					}
 				}
 		);
 
 		{
 			final String cfgXmlResourceName = (String) merged.remove( AvailableSettings.CFG_FILE );
 			if ( StringHelper.isNotEmpty( cfgXmlResourceName ) ) {
 				// it does, so load those properties
 				JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue()
 						.loadConfigXmlResource( cfgXmlResourceName );
 				processHibernateConfigurationElement( configurationElement, merged );
 			}
 		}
 
 		// see if integration settings named a Hibernate config file....
 		{
 			final String cfgXmlResourceName = (String) integrationSettings.get( AvailableSettings.CFG_FILE );
 			if ( StringHelper.isNotEmpty( cfgXmlResourceName ) ) {
 				integrationSettings.remove( AvailableSettings.CFG_FILE );
 				// it does, so load those properties
 				JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue().loadConfigXmlResource(
 						cfgXmlResourceName
 				);
 				processHibernateConfigurationElement( configurationElement, merged );
 			}
 		}
 
 		// finally, apply integration-supplied settings (per JPA spec, integration settings should override other sources)
 		merged.putAll( integrationSettings );
 
 		if ( ! merged.containsKey( AvailableSettings.VALIDATION_MODE ) ) {
 			if ( persistenceUnit.getValidationMode() != null ) {
 				merged.put( AvailableSettings.VALIDATION_MODE, persistenceUnit.getValidationMode() );
 			}
 		}
 
 		if ( ! merged.containsKey( AvailableSettings.SHARED_CACHE_MODE ) ) {
 			if ( persistenceUnit.getSharedCacheMode() != null ) {
 				merged.put( AvailableSettings.SHARED_CACHE_MODE, persistenceUnit.getSharedCacheMode() );
 			}
 		}
 
 		// was getting NPE exceptions from the underlying map when just using #putAll, so going this safer route...
 		Iterator itr = merged.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			if ( entry.getValue() == null ) {
 				itr.remove();
 			}
 		}
 
 		return merged;
 	}
 
 	@SuppressWarnings("unchecked")
 	private void processHibernateConfigurationElement(
 			JaxbHibernateConfiguration configurationElement,
 			Map mergeMap) {
 		if ( ! mergeMap.containsKey( org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME ) ) {
 			String cfgName = configurationElement.getSessionFactory().getName();
 			if ( cfgName != null ) {
 				mergeMap.put( org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME, cfgName );
 			}
 		}
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbProperty jaxbProperty : configurationElement.getSessionFactory().getProperty() ) {
 			mergeMap.put( jaxbProperty.getName(), jaxbProperty.getValue() );
 		}
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping jaxbMapping : configurationElement.getSessionFactory().getMapping() ) {
 			cfgXmlNamedMappings.add( jaxbMapping );
 		}
 
 		for ( Object cacheDeclaration : configurationElement.getSessionFactory().getClassCacheOrCollectionCache() ) {
 			if ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache.class.isInstance( cacheDeclaration ) ) {
 				final JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache jaxbClassCache
 						= (JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache) cacheDeclaration;
 				cacheRegionDefinitions.add(
 						new CacheRegionDefinition(
 								CacheRegionDefinition.CacheType.ENTITY,
 								jaxbClassCache.getClazz(),
 								jaxbClassCache.getUsage().value(),
 								jaxbClassCache.getRegion(),
 								"all".equals( jaxbClassCache.getInclude() )
 						)
 				);
 			}
 			else {
 				final JaxbHibernateConfiguration.JaxbSessionFactory.JaxbCollectionCache jaxbCollectionCache
 						= (JaxbHibernateConfiguration.JaxbSessionFactory.JaxbCollectionCache) cacheDeclaration;
 				cacheRegionDefinitions.add(
 						new CacheRegionDefinition(
 								CacheRegionDefinition.CacheType.COLLECTION,
 								jaxbCollectionCache.getCollection(),
 								jaxbCollectionCache.getUsage().value(),
 								jaxbCollectionCache.getRegion(),
 								false
 						)
 				);
 			}
 		}
 
 		if ( configurationElement.getSecurity() != null ) {
 			final String contextId = configurationElement.getSecurity().getContext();
 			for ( JaxbHibernateConfiguration.JaxbSecurity.JaxbGrant grant : configurationElement.getSecurity().getGrant() ) {
 				jaccDefinitions.add(
 						new JaccDefinition(
 								contextId,
 								grant.getRole(),
 								grant.getEntityName(),
 								grant.getActions()
 						)
 				);
 			}
 		}
 	}
 
 	private String jaccContextId;
 
 	private void addJaccDefinition(String key, Object value) {
 		if ( jaccContextId == null ) {
 			jaccContextId = (String) configurationValues.get( AvailableSettings.JACC_CONTEXT_ID );
 			if ( jaccContextId == null ) {
 				throw persistenceException(
 						"Entities have been configured for JACC, but "
 								+ AvailableSettings.JACC_CONTEXT_ID + " has not been set"
 				);
 			}
 		}
 
 		try {
 			final int roleStart = AvailableSettings.JACC_PREFIX.length() + 1;
 			final String role = key.substring( roleStart, key.indexOf( '.', roleStart ) );
 			final int classStart = roleStart + role.length() + 1;
 			final String clazz = key.substring( classStart, key.length() );
 
 			final JaccDefinition def = new JaccDefinition( jaccContextId, role, clazz, (String) value );
 
 			jaccDefinitions.add( def );
 
 		}
 		catch ( IndexOutOfBoundsException e ) {
 			throw persistenceException( "Illegal usage of " + AvailableSettings.JACC_PREFIX + ": " + key );
 		}
 	}
 
 	private void addCacheRegionDefinition(String role, String value, CacheRegionDefinition.CacheType cacheType) {
 		final StringTokenizer params = new StringTokenizer( value, ";, " );
 		if ( !params.hasMoreTokens() ) {
 			StringBuilder error = new StringBuilder( "Illegal usage of " );
 			if ( cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 				error.append( AvailableSettings.CLASS_CACHE_PREFIX )
 						.append( ": " )
 						.append( AvailableSettings.CLASS_CACHE_PREFIX );
 			}
 			else {
 				error.append( AvailableSettings.COLLECTION_CACHE_PREFIX )
 						.append( ": " )
 						.append( AvailableSettings.COLLECTION_CACHE_PREFIX );
 			}
 			error.append( '.' )
 					.append( role )
 					.append( ' ' )
 					.append( value )
 					.append( ".  Was expecting configuration, but found none" );
 			throw persistenceException( error.toString() );
 		}
 
 		String usage = params.nextToken();
 		String region = null;
 		if ( params.hasMoreTokens() ) {
 			region = params.nextToken();
 		}
 		boolean lazyProperty = true;
 		if ( cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 			if ( params.hasMoreTokens() ) {
 				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
 			}
 		}
 		else {
 			lazyProperty = false;
 		}
 
 		final CacheRegionDefinition def = new CacheRegionDefinition( cacheType, role, usage, region, lazyProperty );
 		cacheRegionDefinitions.add( def );
 	}
 
 	@SuppressWarnings("unchecked")
 	private ScanResult scan(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		Scanner scanner = locateOrBuildScanner( bootstrapServiceRegistry );
 		ScanningContext scanningContext = new ScanningContext();
 
 		final ScanResult scanResult = new ScanResult();
 		if ( persistenceUnit.getMappingFileNames() != null ) {
 			scanResult.getMappingFiles().addAll( persistenceUnit.getMappingFileNames() );
 		}
 
 		// dunno, but the old code did it...
 		scanningContext.setSearchOrm( ! scanResult.getMappingFiles().contains( META_INF_ORM_XML ) );
 
 		if ( persistenceUnit.getJarFileUrls() != null ) {
 			prepareAutoDetectionSettings( scanningContext, false );
 			for ( URL jar : persistenceUnit.getJarFileUrls() ) {
 				scanningContext.setUrl( jar );
 				scanInContext( scanner, scanningContext, scanResult );
 			}
 		}
 
 		prepareAutoDetectionSettings( scanningContext, persistenceUnit.isExcludeUnlistedClasses() );
 		scanningContext.setUrl( persistenceUnit.getPersistenceUnitRootUrl() );
 		scanInContext( scanner, scanningContext, scanResult );
 
 		return scanResult;
 	}
 
 	@SuppressWarnings("unchecked")
 	private Scanner locateOrBuildScanner(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Object value = configurationValues.remove( AvailableSettings.SCANNER );
 		if ( value == null ) {
 			return new NativeScanner();
 		}
 
 		if ( Scanner.class.isInstance( value ) ) {
 			return (Scanner) value;
 		}
 
 		Class<? extends Scanner> scannerClass;
 		if ( Class.class.isInstance( value ) ) {
 			try {
 				scannerClass = (Class<? extends Scanner>) value;
 			}
 			catch ( ClassCastException e ) {
 				throw persistenceException( "Expecting Scanner implementation, but found " + ((Class) value).getName() );
 			}
 		}
 		else {
 			final String scannerClassName = value.toString();
 			try {
 				scannerClass = bootstrapServiceRegistry.getService( ClassLoaderService.class ).classForName( scannerClassName );
 			}
 			catch ( ClassCastException e ) {
 				throw persistenceException( "Expecting Scanner implementation, but found " + scannerClassName );
 			}
 		}
 
 		try {
 			return scannerClass.newInstance();
 		}
 		catch ( Exception e ) {
 			throw persistenceException( "Unable to instantiate Scanner class: " + scannerClass, e );
 		}
 	}
 
 	private void prepareAutoDetectionSettings(ScanningContext context, boolean excludeUnlistedClasses) {
 		final String detectionSetting = (String) configurationValues.get( AvailableSettings.AUTODETECTION );
 
 		if ( detectionSetting == null ) {
 			if ( excludeUnlistedClasses ) {
 				context.setDetectClasses( false );
 				context.setDetectHbmFiles( false );
 			}
 			else {
 				context.setDetectClasses( true );
 				context.setDetectHbmFiles( true );
 			}
 		}
 		else {
 			for ( String token : StringHelper.split( ", ", detectionSetting ) ) {
 				if ( "class".equalsIgnoreCase( token ) ) {
 					context.setDetectClasses( true );
 				}
 				if ( "hbm".equalsIgnoreCase( token ) ) {
 					context.setDetectClasses( true );
 				}
 			}
 		}
 	}
 
 	private void scanInContext(
 			Scanner scanner,
 			ScanningContext scanningContext,
 			ScanResult scanResult) {
 		if ( scanningContext.getUrl() == null ) {
 			// not sure i like just ignoring this being null, but this is exactly what the old code does...
 			LOG.containerProvidingNullPersistenceUnitRootUrl();
 			return;
 		}
 
 		try {
 			if ( scanningContext.isDetectClasses() ) {
 				Set<Package> matchingPackages = scanner.getPackagesInJar( scanningContext.url, new HashSet<Class<? extends Annotation>>(0) );
 				for ( Package pkg : matchingPackages ) {
 					scanResult.getPackageNames().add( pkg.getName() );
 				}
 
 				Set<Class<? extends Annotation>> annotationsToLookFor = new HashSet<Class<? extends Annotation>>();
 				annotationsToLookFor.add( Entity.class );
 				annotationsToLookFor.add( MappedSuperclass.class );
 				annotationsToLookFor.add( Embeddable.class );
 				annotationsToLookFor.add( Converter.class );
 				Set<Class<?>> matchingClasses = scanner.getClassesInJar( scanningContext.url, annotationsToLookFor );
 				for ( Class<?> clazz : matchingClasses ) {
 					scanResult.getManagedClassNames().add( clazz.getName() );
 				}
 			}
 
 			Set<String> patterns = new HashSet<String>();
 			if ( scanningContext.isSearchOrm() ) {
 				patterns.add( META_INF_ORM_XML );
 			}
 			if ( scanningContext.isDetectHbmFiles() ) {
 				patterns.add( "**/*.hbm.xml" );
 			}
 			if ( ! scanResult.getMappingFiles().isEmpty() ) {
 				patterns.addAll( scanResult.getMappingFiles() );
 			}
 			if ( patterns.size() != 0 ) {
 				Set<NamedInputStream> files = scanner.getFilesInJar( scanningContext.getUrl(), patterns );
 				for ( NamedInputStream file : files ) {
 					scanResult.getHbmFiles().add( file );
 					scanResult.getMappingFiles().remove( file.getName() );
 				}
 			}
 		}
 		catch (PersistenceException e ) {
 			throw e;
 		}
 		catch ( RuntimeException e ) {
 			throw persistenceException( "error trying to scan url: " + scanningContext.getUrl().toString(), e );
 		}
 	}
 
 	@Override
 	public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory) {
 		this.validatorFactory = validatorFactory;
 
 		if ( validatorFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validatorFactory );
 		}
 		return this;
 	}
 
 	@Override
 	public EntityManagerFactoryBuilder withDataSource(DataSource dataSource) {
 		this.dataSource = dataSource;
 
 		return this;
 	}
 
 	@Override
 	public void cancel() {
 		// todo : close the bootstrap registry (not critical, but nice to do)
 
 	}
 
 	@SuppressWarnings("unchecked")
 	public EntityManagerFactory build() {
 		processProperties();
 
 		final ServiceRegistry serviceRegistry = buildServiceRegistry();
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 		// IMPL NOTE : TCCL handling here is temporary.
 		//		It is needed because this code still uses Hibernate Configuration and Hibernate commons-annotations
 		// 		in turn which relies on TCCL being set.
 
 		return ( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
 				new ClassLoaderServiceImpl.Work<EntityManagerFactoryImpl>() {
 					@Override
 					public EntityManagerFactoryImpl perform() {
 						hibernateConfiguration = buildHibernateConfiguration( serviceRegistry );
 
 						SessionFactoryImplementor sessionFactory;
 						try {
 							sessionFactory = (SessionFactoryImplementor) hibernateConfiguration.buildSessionFactory( serviceRegistry );
 						}
 						catch (MappingException e) {
 							throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 						}
 
 						if ( suppliedSessionFactoryObserver != null ) {
 							sessionFactory.addObserver( suppliedSessionFactoryObserver );
 						}
 						sessionFactory.addObserver( new ServiceRegistryCloser() );
 
 						// NOTE : passing cfg is temporary until
 						return new EntityManagerFactoryImpl( persistenceUnit.getName(), sessionFactory, settings, configurationValues, hibernateConfiguration );
 					}
 				}
 		);
 	}
 
 	private void processProperties() {
 		applyJdbcConnectionProperties();
 		applyTransactionProperties();
 
 		Object validationFactory = this.validatorFactory;
 		if ( validationFactory == null ) {
 			validationFactory = configurationValues.get( AvailableSettings.VALIDATION_FACTORY );
 		}
 		if ( validationFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validationFactory );
 			serviceRegistryBuilder.applySetting( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 		}
 
 		// flush before completion validation
 		if ( "true".equals( configurationValues.get( Environment.FLUSH_BEFORE_COMPLETION ) ) ) {
 			serviceRegistryBuilder.applySetting( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 			LOG.definingFlushBeforeCompletionIgnoredInHem( Environment.FLUSH_BEFORE_COMPLETION );
 		}
 
 		final StrategySelector strategySelector = serviceRegistryBuilder.getBootstrapServiceRegistry().getService( StrategySelector.class );
 
 		for ( Object oEntry : configurationValues.entrySet() ) {
 			Map.Entry entry = (Map.Entry) oEntry;
 			if ( entry.getKey() instanceof String ) {
 				final String keyString = (String) entry.getKey();
 
 				if ( AvailableSettings.INTERCEPTOR.equals( keyString ) ) {
 					sessionFactoryInterceptor = strategySelector.resolveStrategy( Interceptor.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.SESSION_INTERCEPTOR.equals( keyString ) ) {
 					settings.setSessionInterceptorClass(
 							loadSessionInterceptorClass( entry.getValue(), strategySelector )
 					);
 				}
 				else if ( AvailableSettings.NAMING_STRATEGY.equals( keyString ) ) {
 					namingStrategy = strategySelector.resolveStrategy( NamingStrategy.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.SESSION_FACTORY_OBSERVER.equals( keyString ) ) {
 					suppliedSessionFactoryObserver = strategySelector.resolveStrategy( SessionFactoryObserver.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.DISCARD_PC_ON_CLOSE.equals( keyString ) ) {
 					settings.setReleaseResourcesOnCloseEnabled( "true".equals( entry.getValue() ) );
 				}
 				else if ( keyString.startsWith( AvailableSettings.CLASS_CACHE_PREFIX ) ) {
 					addCacheRegionDefinition(
 							keyString.substring( AvailableSettings.CLASS_CACHE_PREFIX.length() + 1 ),
 							(String) entry.getValue(),
 							CacheRegionDefinition.CacheType.ENTITY
 					);
 				}
 				else if ( keyString.startsWith( AvailableSettings.COLLECTION_CACHE_PREFIX ) ) {
 					addCacheRegionDefinition(
 							keyString.substring( AvailableSettings.COLLECTION_CACHE_PREFIX.length() + 1 ),
 							(String) entry.getValue(),
 							CacheRegionDefinition.CacheType.COLLECTION
 					);
 				}
 				else if ( keyString.startsWith( AvailableSettings.JACC_PREFIX )
 						&& ! ( keyString.equals( AvailableSettings.JACC_CONTEXT_ID )
 						|| keyString.equals( AvailableSettings.JACC_ENABLED ) ) ) {
 					addJaccDefinition( (String) entry.getKey(), entry.getValue() );
 				}
 			}
 		}
 	}
 
 	private void applyJdbcConnectionProperties() {
 		if ( dataSource != null ) {
 			serviceRegistryBuilder.applySetting( Environment.DATASOURCE, dataSource );
 		}
 		else if ( persistenceUnit.getJtaDataSource() != null ) {
 			serviceRegistryBuilder.applySetting( Environment.DATASOURCE, persistenceUnit.getJtaDataSource() );
 		}
 		else if ( persistenceUnit.getNonJtaDataSource() != null ) {
 			serviceRegistryBuilder.applySetting( Environment.DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 		}
 		else {
 			final String driver = (String) configurationValues.get( AvailableSettings.JDBC_DRIVER );
 			if ( StringHelper.isNotEmpty( driver ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DRIVER, driver );
 			}
 			final String url = (String) configurationValues.get( AvailableSettings.JDBC_URL );
 			if ( StringHelper.isNotEmpty( url ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.URL, url );
 			}
 			final String user = (String) configurationValues.get( AvailableSettings.JDBC_USER );
 			if ( StringHelper.isNotEmpty( user ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.USER, user );
 			}
 			final String pass = (String) configurationValues.get( AvailableSettings.JDBC_PASSWORD );
 			if ( StringHelper.isNotEmpty( pass ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.PASS, pass );
 			}
 		}
 	}
 
 	private void applyTransactionProperties() {
 		PersistenceUnitTransactionType txnType = PersistenceUnitTransactionTypeHelper.interpretTransactionType(
 				configurationValues.get( AvailableSettings.TRANSACTION_TYPE )
 		);
 		if ( txnType == null ) {
 			txnType = persistenceUnit.getTransactionType();
 		}
 		if ( txnType == null ) {
 			// is it more appropriate to have this be based on bootstrap entry point (EE vs SE)?
 			txnType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 		}
 		settings.setTransactionType( txnType );
 		boolean hasTxStrategy = configurationValues.containsKey( Environment.TRANSACTION_STRATEGY );
 		if ( hasTxStrategy ) {
 			LOG.overridingTransactionStrategyDangerous( Environment.TRANSACTION_STRATEGY );
 		}
 		else {
 			if ( txnType == PersistenceUnitTransactionType.JTA ) {
 				serviceRegistryBuilder.applySetting( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class );
 			}
 			else if ( txnType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 				serviceRegistryBuilder.applySetting( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class );
 			}
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	private Class<? extends Interceptor> loadSessionInterceptorClass(Object value, StrategySelector strategySelector) {
 		if ( value == null ) {
 			return null;
 		}
 
 		return Class.class.isInstance( value )
 				? (Class<? extends Interceptor>) value
 				: strategySelector.selectStrategyImplementor( Interceptor.class, value.toString() );
 	}
 
 	public ServiceRegistry buildServiceRegistry() {
-		return serviceRegistryBuilder.buildServiceRegistry();
+		return serviceRegistryBuilder.build();
 	}
 
 	public Configuration buildHibernateConfiguration(ServiceRegistry serviceRegistry) {
 		Properties props = new Properties();
 		props.putAll( configurationValues );
 		Configuration cfg = new Configuration().setProperties( props );
 
 		cfg.setEntityNotFoundDelegate( jpaEntityNotFoundDelegate );
 
 		if ( namingStrategy != null ) {
 			cfg.setNamingStrategy( namingStrategy );
 		}
 
 		if ( sessionFactoryInterceptor != null ) {
 			cfg.setInterceptor( sessionFactoryInterceptor );
 		}
 
 		final Object strategyProviderValue = props.get( AvailableSettings.IDENTIFIER_GENERATOR_STRATEGY_PROVIDER );
 		final IdentifierGeneratorStrategyProvider strategyProvider = strategyProviderValue == null
 				? null
 				: serviceRegistry.getService( StrategySelector.class )
 						.resolveStrategy( IdentifierGeneratorStrategyProvider.class, strategyProviderValue );
 
 		if ( strategyProvider != null ) {
 			final MutableIdentifierGeneratorFactory identifierGeneratorFactory = cfg.getIdentifierGeneratorFactory();
 			for ( Map.Entry<String,Class<?>> entry : strategyProvider.getStrategies().entrySet() ) {
 				identifierGeneratorFactory.register( entry.getKey(), entry.getValue() );
 			}
 		}
 
 		if ( jaccDefinitions != null ) {
 			for ( JaccDefinition jaccDefinition : jaccDefinitions ) {
 				JACCConfiguration jaccCfg = new JACCConfiguration( jaccDefinition.contextId );
 				jaccCfg.addPermission( jaccDefinition.role, jaccDefinition.clazz, jaccDefinition.actions );
 			}
 		}
 
 		if ( cacheRegionDefinitions != null ) {
 			for ( CacheRegionDefinition cacheRegionDefinition : cacheRegionDefinitions ) {
 				if ( cacheRegionDefinition.cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 					cfg.setCacheConcurrencyStrategy(
 							cacheRegionDefinition.role,
 							cacheRegionDefinition.usage,
 							cacheRegionDefinition.region,
 							cacheRegionDefinition.cacheLazy
 					);
 				}
 				else {
 					cfg.setCollectionCacheConcurrencyStrategy(
 							cacheRegionDefinition.role,
 							cacheRegionDefinition.usage,
 							cacheRegionDefinition.region
 					);
 				}
 			}
 		}
 
 
 		// todo : need to have this use the metamodel codebase eventually...
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping jaxbMapping : cfgXmlNamedMappings ) {
 			if ( jaxbMapping.getClazz() != null ) {
 				cfg.addAnnotatedClass(
 						serviceRegistry.getService( ClassLoaderService.class ).classForName( jaxbMapping.getClazz() )
 				);
 			}
 			else if ( jaxbMapping.getResource() != null ) {
 				cfg.addResource( jaxbMapping.getResource() );
 			}
 			else if ( jaxbMapping.getJar() != null ) {
 				cfg.addJar( new File( jaxbMapping.getJar() ) );
 			}
 			else if ( jaxbMapping.getPackage() != null ) {
 				cfg.addPackage( jaxbMapping.getPackage() );
 			}
 		}
 
 		List<Class> loadedAnnotatedClasses = (List<Class>) configurationValues.remove( AvailableSettings.LOADED_CLASSES );
 		if ( loadedAnnotatedClasses != null ) {
 			for ( Class cls : loadedAnnotatedClasses ) {
 				cfg.addAnnotatedClass( cls );
 			}
 		}
 
 		for ( String className : metadataSources.getAnnotatedMappingClassNames() ) {
 			cfg.addAnnotatedClass( serviceRegistry.getService( ClassLoaderService.class ).classForName( className ) );
 		}
 
 		for ( MetadataSources.ConverterDescriptor converterDescriptor : metadataSources.getConverterDescriptors() ) {
 			final Class<? extends AttributeConverter> converterClass;
 			try {
 				Class theClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( converterDescriptor.converterClassName );
 				converterClass = (Class<? extends AttributeConverter>) theClass;
 			}
 			catch (ClassCastException e) {
 				throw persistenceException(
 						String.format(
 								"AttributeConverter implementation [%s] does not implement AttributeConverter interface",
 								converterDescriptor.converterClassName
 						)
 				);
 			}
 			cfg.addAttributeConverter( converterClass, converterDescriptor.autoApply );
 		}
 
 		for ( String resourceName : metadataSources.mappingFileResources ) {
 			Boolean useMetaInf = null;
 			try {
 				if ( resourceName.endsWith( META_INF_ORM_XML ) ) {
 					useMetaInf = true;
 				}
 				cfg.addResource( resourceName );
 			}
 			catch( MappingNotFoundException e ) {
 				if ( ! resourceName.endsWith( META_INF_ORM_XML ) ) {
 					throw persistenceException( "Unable to find XML mapping file in classpath: " + resourceName );
 				}
 				else {
 					useMetaInf = false;
 					//swallow it, the META-INF/orm.xml is optional
 				}
 			}
 			catch( MappingException me ) {
 				throw persistenceException( "Error while reading JPA XML file: " + resourceName, me );
 			}
 
 			if ( Boolean.TRUE.equals( useMetaInf ) ) {
 				LOG.exceptionHeaderFound( getExceptionHeader(), META_INF_ORM_XML );
 			}
 			else if (Boolean.FALSE.equals(useMetaInf)) {
 				LOG.exceptionHeaderNotFound( getExceptionHeader(), META_INF_ORM_XML );
 			}
 		}
 		for ( NamedInputStream namedInputStream : metadataSources.namedMappingFileInputStreams ) {
 			try {
 				//addInputStream has the responsibility to close the stream
 				cfg.addInputStream( new BufferedInputStream( namedInputStream.getStream() ) );
 			}
 			catch (MappingException me) {
 				//try our best to give the file name
 				if ( StringHelper.isEmpty( namedInputStream.getName() ) ) {
 					throw me;
 				}
 				else {
 					throw new MappingException("Error while parsing file: " + namedInputStream.getName(), me );
 				}
 			}
 		}
 		for ( String packageName : metadataSources.packageNames ) {
 			cfg.addPackage( packageName );
 		}
 		return cfg;
 	}
 
 	public static class ServiceRegistryCloser implements SessionFactoryObserver {
 		@Override
 		public void sessionFactoryCreated(SessionFactory sessionFactory) {
 			// nothing to do
 		}
 
 		@Override
 		public void sessionFactoryClosed(SessionFactory sessionFactory) {
 			SessionFactoryImplementor sfi = ( (SessionFactoryImplementor) sessionFactory );
 			sfi.getServiceRegistry().destroy();
 			ServiceRegistry basicRegistry = sfi.getServiceRegistry().getParentServiceRegistry();
 			( (ServiceRegistryImplementor) basicRegistry ).destroy();
 		}
 	}
 
 	private PersistenceException persistenceException(String message) {
 		return persistenceException( message, null );
 	}
 
 	private PersistenceException persistenceException(String message, Exception cause) {
 		return new PersistenceException(
 				getExceptionHeader() + message,
 				cause
 		);
 	}
 
 	private String getExceptionHeader() {
 		return "[PersistenceUnit: " + persistenceUnit.getName() + "] ";
 	}
 
 	public static class CacheRegionDefinition {
 		public static enum CacheType { ENTITY, COLLECTION }
 
 		public final CacheType cacheType;
 		public final String role;
 		public final String usage;
 		public final String region;
 		public final boolean cacheLazy;
 
 		public CacheRegionDefinition(
 				CacheType cacheType,
 				String role,
 				String usage,
 				String region, boolean cacheLazy) {
 			this.cacheType = cacheType;
 			this.role = role;
 			this.usage = usage;
 			this.region = region;
 			this.cacheLazy = cacheLazy;
 		}
 	}
 
 	public static class JaccDefinition {
 		public final String contextId;
 		public final String role;
 		public final String clazz;
 		public final String actions;
 
 		public JaccDefinition(String contextId, String role, String clazz, String actions) {
 			this.contextId = contextId;
 			this.role = role;
 			this.clazz = clazz;
 			this.actions = actions;
 		}
 	}
 
 	public static class ScanningContext {
 		private URL url;
 		private boolean detectClasses;
 		private boolean detectHbmFiles;
 		private boolean searchOrm;
 
 		public URL getUrl() {
 			return url;
 		}
 
 		public void setUrl(URL url) {
 			this.url = url;
 		}
 
 		public boolean isDetectClasses() {
 			return detectClasses;
 		}
 
 		public void setDetectClasses(boolean detectClasses) {
 			this.detectClasses = detectClasses;
 		}
 
 		public boolean isDetectHbmFiles() {
 			return detectHbmFiles;
 		}
 
 		public void setDetectHbmFiles(boolean detectHbmFiles) {
 			this.detectHbmFiles = detectHbmFiles;
 		}
 
 		public boolean isSearchOrm() {
 			return searchOrm;
 		}
 
 		public void setSearchOrm(boolean searchOrm) {
 			this.searchOrm = searchOrm;
 		}
 	}
 
 	private static class ScanResult {
 		private final List<String> managedClassNames = new ArrayList<String>();
 		private final List<String> packageNames = new ArrayList<String>();
 		private final List<NamedInputStream> hbmFiles = new ArrayList<NamedInputStream>();
 		private final List<String> mappingFiles = new ArrayList<String>();
 
 		public List<String> getManagedClassNames() {
 			return managedClassNames;
 		}
 
 		public List<String> getPackageNames() {
 			return packageNames;
 		}
 
 		public List<NamedInputStream> getHbmFiles() {
 			return hbmFiles;
 		}
 
 		public List<String> getMappingFiles() {
 			return mappingFiles;
 		}
 	}
 
 	public static class MetadataSources {
 		private final List<String> annotatedMappingClassNames = new ArrayList<String>();
 		private final List<ConverterDescriptor> converterDescriptors = new ArrayList<ConverterDescriptor>();
 		private final List<NamedInputStream> namedMappingFileInputStreams = new ArrayList<NamedInputStream>();
 		private final List<String> mappingFileResources = new ArrayList<String>();
 		private final List<String> packageNames = new ArrayList<String>();
 
 		public List<String> getAnnotatedMappingClassNames() {
 			return annotatedMappingClassNames;
 		}
 
 		public List<ConverterDescriptor> getConverterDescriptors() {
 			return converterDescriptors;
 		}
 
 		public List<NamedInputStream> getNamedMappingFileInputStreams() {
 			return namedMappingFileInputStreams;
 		}
 
 		public List<String> getPackageNames() {
 			return packageNames;
 		}
 
 		public List<String> collectMappingClassNames() {
 			// todo : the complete answer to this involves looking through the mapping files as well.
 			// 		Really need the metamodel branch code to do that properly
 			return annotatedMappingClassNames;
 		}
 
 		public static class ConverterDescriptor {
 			private final String converterClassName;
 			private final boolean autoApply;
 
 			public ConverterDescriptor(String converterClassName, boolean autoApply) {
 				this.converterClassName = converterClassName;
 				this.autoApply = autoApply;
 			}
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index d2ed123d8e..338340285e 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,232 +1,232 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Set;
 
 import org.infinispan.AdvancedCache;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.jboss.logging.Logger;
 import org.junit.Test;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.spi.GeneralDataRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cfg.Configuration;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 
 /**
  * Base class for tests of QueryResultsRegion and TimestampsRegion.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractGeneralDataRegionTestCase extends AbstractRegionImplTestCase {
 	private static final Logger log = Logger.getLogger( AbstractGeneralDataRegionTestCase.class );
 
 	protected static final String KEY = "Key";
 
 	protected static final String VALUE1 = "value1";
 	protected static final String VALUE2 = "value2";
 
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, false, true );
 	}
 
 	@Override
 	protected void putInRegion(Region region, Object key, Object value) {
 		((GeneralDataRegion) region).put( key, value );
 	}
 
 	@Override
 	protected void removeFromRegion(Region region, Object key) {
 		((GeneralDataRegion) region).evict( key );
 	}
 
 	@Test
 	public void testEvict() throws Exception {
 		evictOrRemoveTest();
 	}
 
 	private void evictOrRemoveTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 				cfg,
 				getCacheTestSupport()
 		);
 		boolean invalidation = false;
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ), cfg.getProperties(), null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
       regionPut(localRegion);
       assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// allow async propagation
 		sleep( 250 );
 		Object expected = invalidation ? null : VALUE1;
 		assertEquals( expected, remoteRegion.get( KEY ) );
 
       regionEvict(localRegion);
 
       // allow async propagation
 		sleep( 250 );
 		assertEquals( null, localRegion.get( KEY ) );
 		assertEquals( null, remoteRegion.get( KEY ) );
 	}
 
    protected void regionEvict(GeneralDataRegion region) throws Exception {
       region.evict(KEY);
    }
 
    protected void regionPut(GeneralDataRegion region) throws Exception {
       region.put(KEY, VALUE1);
    }
 
    protected abstract String getStandardRegionName(String regionPrefix);
 
 	/**
 	 * Test method for {@link QueryResultsRegion#evictAll()}.
 	 * <p/>
 	 * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
 	 * CollectionRegionAccessStrategy API.
 	 */
 	public void testEvictAll() throws Exception {
 		evictOrRemoveAllTest( "entity" );
 	}
 
 	private void evictOrRemoveAllTest(String configName) throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 				cfg,
 				getCacheTestSupport()
 		);
 		AdvancedCache localCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 				cfg,
 				getCacheTestSupport()
 		);
       AdvancedCache remoteCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		Set keys = localCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		keys = remoteCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
       regionPut(localRegion);
       assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// Allow async propagation
 		sleep( 250 );
 
       regionPut(remoteRegion);
       assertEquals( VALUE1, remoteRegion.get( KEY ) );
 
 		// Allow async propagation
 		sleep( 250 );
 
 		localRegion.evictAll();
 
 		// allow async propagation
 		sleep( 250 );
 		// This should re-establish the region root node in the optimistic case
 		assertNull( localRegion.get( KEY ) );
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( localCache.keySet() ) );
 
 		// Re-establishing the region root on the local node doesn't
 		// propagate it to other nodes. Do a get on the remote node to re-establish
 		// This only adds a node in the case of optimistic locking
 		assertEquals( null, remoteRegion.get( KEY ) );
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( remoteCache.keySet() ) );
 
 		assertEquals( "local is clean", null, localRegion.get( KEY ) );
 		assertEquals( "remote is clean", null, remoteRegion.get( KEY ) );
 	}
 
 	protected void rollback() {
 		try {
 			BatchModeTransactionManager.getInstance().rollback();
 		}
 		catch (Exception e) {
 			log.error( e.getMessage(), e );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
index 5a5c884748..695ff8cfcf 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
@@ -1,153 +1,153 @@
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Defines the environment for a node.
  *
  * @author Steve Ebersole
  */
 public class NodeEnvironment {
 
    private final Configuration configuration;
 
    private StandardServiceRegistryImpl serviceRegistry;
    private InfinispanRegionFactory regionFactory;
 
    private Map<String, EntityRegionImpl> entityRegionMap;
    private Map<String, CollectionRegionImpl> collectionRegionMap;
 
    public NodeEnvironment(Configuration configuration) {
       this.configuration = configuration;
    }
 
    public Configuration getConfiguration() {
       return configuration;
    }
 
    public StandardServiceRegistryImpl getServiceRegistry() {
       return serviceRegistry;
    }
 
    public EntityRegionImpl getEntityRegion(String name, CacheDataDescription cacheDataDescription) {
       if (entityRegionMap == null) {
          entityRegionMap = new HashMap<String, EntityRegionImpl>();
          return buildAndStoreEntityRegion(name, cacheDataDescription);
       }
       EntityRegionImpl region = entityRegionMap.get(name);
       if (region == null) {
          region = buildAndStoreEntityRegion(name, cacheDataDescription);
       }
       return region;
    }
 
    private EntityRegionImpl buildAndStoreEntityRegion(String name, CacheDataDescription cacheDataDescription) {
       EntityRegionImpl region = (EntityRegionImpl) regionFactory.buildEntityRegion(
             name,
             configuration.getProperties(),
             cacheDataDescription
       );
       entityRegionMap.put(name, region);
       return region;
    }
 
    public CollectionRegionImpl getCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
       if (collectionRegionMap == null) {
          collectionRegionMap = new HashMap<String, CollectionRegionImpl>();
          return buildAndStoreCollectionRegion(name, cacheDataDescription);
       }
       CollectionRegionImpl region = collectionRegionMap.get(name);
       if (region == null) {
          region = buildAndStoreCollectionRegion(name, cacheDataDescription);
          collectionRegionMap.put(name, region);
       }
       return region;
    }
 
    private CollectionRegionImpl buildAndStoreCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
       CollectionRegionImpl region;
       region = (CollectionRegionImpl) regionFactory.buildCollectionRegion(
             name,
             configuration.getProperties(),
             cacheDataDescription
       );
       return region;
    }
 
    public void prepare() throws Exception {
       serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
             .applySettings(configuration.getProperties())
-            .buildServiceRegistry();
+            .build();
       regionFactory = CacheTestUtil.startRegionFactory(serviceRegistry, configuration);
    }
 
    public void release() throws Exception {
       try {
          if (entityRegionMap != null) {
             for (EntityRegionImpl region : entityRegionMap.values()) {
                try {
                   region.getCache().stop();
                } catch (Exception e) {
                   // Ignore...
                }
             }
             entityRegionMap.clear();
          }
          if (collectionRegionMap != null) {
             for (CollectionRegionImpl reg : collectionRegionMap.values()) {
                try {
                   reg.getCache().stop();
                } catch (Exception e) {
                   // Ignore...
                }
             }
             collectionRegionMap.clear();
          }
       } finally {
          try {
             if (regionFactory != null) {
                // Currently the RegionFactory is shutdown by its registration
                // with the CacheTestSetup from CacheTestUtil when built
                regionFactory.stop();
             }
          } finally {
             if (serviceRegistry != null) {
                serviceRegistry.destroy();
             }
          }
       }
    }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
index 6a803ddc9f..3ec976b6b1 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
@@ -1,357 +1,357 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.query;
 
 import java.util.Properties;
 import java.util.concurrent.Callable;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import junit.framework.AssertionFailedError;
 import org.hibernate.cache.infinispan.util.Caches;
 import org.infinispan.AdvancedCache;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.infinispan.util.concurrent.IsolationLevel;
 import org.jboss.logging.Logger;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.internal.StandardQueryCache;
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.GeneralDataRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cfg.Configuration;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests of QueryResultRegionImpl.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class QueryRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 	private static final Logger log = Logger.getLogger( QueryRegionImplTestCase.class );
 
 	@Override
 	protected Region createRegion(
 			InfinispanRegionFactory regionFactory,
 			String regionName,
 			Properties properties,
 			CacheDataDescription cdd) {
 		return regionFactory.buildQueryResultsRegion( regionName, properties );
 	}
 
 	@Override
 	protected String getStandardRegionName(String regionPrefix) {
 		return regionPrefix + "/" + StandardQueryCache.class.getName();
 	}
 
    @Override
    protected void regionPut(final GeneralDataRegion region) throws Exception {
       Caches.withinTx(BatchModeTransactionManager.getInstance(), new Callable<Void>() {
          @Override
          public Void call() throws Exception {
             region.put(KEY, VALUE1);
             return null;
          }
       });
    }
 
    @Override
    protected void regionEvict(final GeneralDataRegion region) throws Exception {
       Caches.withinTx(BatchModeTransactionManager.getInstance(), new Callable<Void>() {
          @Override
          public Void call() throws Exception {
             region.evict(KEY);
             return null;
          }
       });
    }
 
    @Override
 	protected AdvancedCache getInfinispanCache(InfinispanRegionFactory regionFactory) {
 		return regionFactory.getCacheManager().getCache( "local-query" ).getAdvancedCache();
 	}
 
 	@Override
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildCustomQueryCacheConfiguration( "test", "replicated-query" );
 	}
 
 	private void putDoesNotBlockGetTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		final CountDownLatch readerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread reader = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					log.debug( "Transaction began, get value for key" );
 					assertTrue( VALUE2.equals( region.get( KEY ) ) == false );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (AssertionFailedError e) {
 					holder.a1 = e;
 					rollback();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					readerLatch.countDown();
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					log.debug( "Put value2" );
 					region.put( KEY, VALUE2 );
 					log.debug( "Put finished for value2, await writer latch" );
 					writerLatch.await();
 					log.debug( "Writer latch finished" );
 					BatchModeTransactionManager.getInstance().commit();
 					log.debug( "Transaction committed" );
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		reader.setDaemon( true );
 		writer.setDaemon( true );
 
 		writer.start();
 		assertFalse( "Writer is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		// Start the reader
 		reader.start();
 		assertTrue( "Reader finished promptly", readerLatch.await( 1000000000, TimeUnit.MILLISECONDS ) );
 
 		writerLatch.countDown();
 		assertTrue( "Reader finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		assertEquals( VALUE2, region.get( KEY ) );
 
 		if ( holder.a1 != null ) {
 			throw holder.a1;
 		}
 		else if ( holder.a2 != null ) {
 			throw holder.a2;
 		}
 
 		assertEquals( "writer saw no exceptions", null, holder.e1 );
 		assertEquals( "reader saw no exceptions", null, holder.e2 );
 	}
 
 	public void testGetDoesNotBlockPut() throws Exception {
 		getDoesNotBlockPutTest();
 	}
 
 	private void getDoesNotBlockPutTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+				new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		// final Fqn rootFqn = getRegionFqn(getStandardRegionName(REGION_PREFIX), REGION_PREFIX);
 		final AdvancedCache jbc = getInfinispanCache(regionFactory);
 
 		final CountDownLatch blockerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread blocker = new Thread() {
 
 			@Override
 			public void run() {
 				// Fqn toBlock = new Fqn(rootFqn, KEY);
 				GetBlocker blocker = new GetBlocker( blockerLatch, KEY );
 				try {
 					jbc.addListener( blocker );
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.get( KEY );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					jbc.removeListener( blocker );
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 
 			@Override
 			public void run() {
 				try {
 					writerLatch.await();
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.put( KEY, VALUE2 );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		blocker.setDaemon( true );
 		writer.setDaemon( true );
 
 		boolean unblocked = false;
 		try {
 			blocker.start();
 			writer.start();
 
 			assertFalse( "Blocker is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 			// Start the writer
 			writerLatch.countDown();
 			assertTrue( "Writer finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 			blockerLatch.countDown();
 			unblocked = true;
 
 			if ( IsolationLevel.REPEATABLE_READ.equals( jbc.getConfiguration().getIsolationLevel() ) ) {
 				assertEquals( VALUE1, region.get( KEY ) );
 			}
 			else {
 				assertEquals( VALUE2, region.get( KEY ) );
 			}
 
 			if ( holder.a1 != null ) {
 				throw holder.a1;
 			}
 			else if ( holder.a2 != null ) {
 				throw holder.a2;
 			}
 
 			assertEquals( "blocker saw no exceptions", null, holder.e1 );
 			assertEquals( "writer saw no exceptions", null, holder.e2 );
 		}
 		finally {
 			if ( !unblocked ) {
 				blockerLatch.countDown();
 			}
 		}
 	}
 
 	@Listener
 	public class GetBlocker {
 
 		private CountDownLatch latch;
 		// private Fqn fqn;
 		private Object key;
 
 		GetBlocker(
 				CountDownLatch latch,
 				Object key
 		) {
 			this.latch = latch;
 			this.key = key;
 		}
 
 		@CacheEntryVisited
 		public void nodeVisisted(CacheEntryVisitedEvent event) {
 			if ( event.isPre() && event.getKey().equals( key ) ) {
 				try {
 					latch.await();
 				}
 				catch (InterruptedException e) {
 					log.error( "Interrupted waiting for latch", e );
 				}
 			}
 		}
 	}
 
 	private class ExceptionHolder {
 		Exception e1;
 		Exception e2;
 		AssertionFailedError a1;
 		AssertionFailedError a2;
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
index f8a661a56e..a96b9ec8c0 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
@@ -1,194 +1,194 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc. and/or it's affiliates.
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
 package org.hibernate.test.cache.infinispan.timestamp;
 
 import java.util.Properties;
 
 import org.hibernate.test.cache.infinispan.functional.SingleNodeTestCase;
 import org.infinispan.AdvancedCache;
 import org.infinispan.context.Flag;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryActivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryEvicted;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryPassivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.Event;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.test.cache.infinispan.util.ClassLoaderAwareCache;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Configuration;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.functional.classloader.Account;
 import org.hibernate.test.cache.infinispan.functional.classloader.AccountHolder;
 import org.hibernate.test.cache.infinispan.functional.classloader.SelectedClassnameClassLoader;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Tests of TimestampsRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TimestampsRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
     @Override
    protected String getStandardRegionName(String regionPrefix) {
       return regionPrefix + "/" + UpdateTimestampsCache.class.getName();
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildTimestampsRegion(regionName, properties);
    }
 
    @Override
    protected AdvancedCache getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return regionFactory.getCacheManager().getCache("timestamps").getAdvancedCache();
    }
 
    public void testClearTimestampsRegionInIsolated() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+			  new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 			  cfg,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       Configuration cfg2 = createConfiguration();
       InfinispanRegionFactory regionFactory2 = CacheTestUtil.startRegionFactory(
-			  new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).buildServiceRegistry(),
+			  new StandardServiceRegistryBuilder().applySettings( cfg.getProperties() ).build(),
 			  cfg2,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       TimestampsRegionImpl region = (TimestampsRegionImpl) regionFactory.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg.getProperties());
       TimestampsRegionImpl region2 = (TimestampsRegionImpl) regionFactory2.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 //      QueryResultsRegion region2 = regionFactory2.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 
 //      ClassLoader cl = Thread.currentThread().getContextClassLoader();
 //      Thread.currentThread().setContextClassLoader(cl.getParent());
 //      log.info("TCCL is " + cl.getParent());
 
       Account acct = new Account();
       acct.setAccountHolder(new AccountHolder());
       region.getCache().withFlags(Flag.FORCE_SYNCHRONOUS).put(acct, "boo");
 
 //      region.put(acct, "boo");
 //
 //      region.evictAll();
 
 //      Account acct = new Account();
 //      acct.setAccountHolder(new AccountHolder());
 
 
 
    }
 
    @Override
    protected Configuration createConfiguration() {
       return CacheTestUtil.buildConfiguration("test", MockInfinispanRegionFactory.class, false, true);
    }
 
    public static class MockInfinispanRegionFactory extends SingleNodeTestCase.TestInfinispanRegionFactory {
 
       public MockInfinispanRegionFactory() {
       }
 
 //      @Override
 //      protected TimestampsRegionImpl createTimestampsRegion(CacheAdapter cacheAdapter, String regionName) {
 //         return new MockTimestampsRegionImpl(cacheAdapter, regionName, getTransactionManager(), this);
 //      }
 
       @Override
       protected AdvancedCache createCacheWrapper(AdvancedCache cache) {
          return new ClassLoaderAwareCache(cache, Thread.currentThread().getContextClassLoader()) {
             @Override
             public void addListener(Object listener) {
                super.addListener(new MockClassLoaderAwareListener(listener, this));
             }
          };
       }
 
       @Listener
       public static class MockClassLoaderAwareListener extends ClassLoaderAwareCache.ClassLoaderAwareListener {
          MockClassLoaderAwareListener(Object listener, ClassLoaderAwareCache cache) {
             super(listener, cache);
          }
 
          @CacheEntryActivated
          @CacheEntryCreated
          @CacheEntryEvicted
          @CacheEntryInvalidated
          @CacheEntryLoaded
          @CacheEntryModified
          @CacheEntryPassivated
          @CacheEntryRemoved
          @CacheEntryVisited
          public void event(Event event) throws Throwable {
             ClassLoader cl = Thread.currentThread().getContextClassLoader();
             String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
             String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
             SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
             Thread.currentThread().setContextClassLoader(visible);
             super.event(event);
             Thread.currentThread().setContextClassLoader(cl);
          }
       }
    }
 
 //   @Listener
 //   public static class MockTimestampsRegionImpl extends TimestampsRegionImpl {
 //
 //      public MockTimestampsRegionImpl(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager, RegionFactory factory) {
 //         super(cacheAdapter, name, transactionManager, factory);
 //      }
 //
 //      @CacheEntryModified
 //      public void nodeModified(CacheEntryModifiedEvent event) {
 ////         ClassLoader cl = Thread.currentThread().getContextClassLoader();
 ////         String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
 ////         String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
 ////         SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
 ////         Thread.currentThread().setContextClassLoader(visible);
 //         super.nodeModified(event);
 ////         Thread.currentThread().setContextClassLoader(cl);
 //      }
 //   }
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
index efab4e2d63..bab28e4d33 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
@@ -1,50 +1,50 @@
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
 package org.hibernate.testing;
 
 import java.util.Map;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.Environment;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceRegistryBuilder {
 	public static StandardServiceRegistryImpl buildServiceRegistry() {
 		return buildServiceRegistry( Environment.getProperties() );
 	}
 
 	public static StandardServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
 		return (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
 				.applySettings( serviceRegistryConfig )
-				.buildServiceRegistry();
+				.build();
 	}
 
 	public static void destroy(ServiceRegistry serviceRegistry) {
 		( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index f52184d3b5..30a9d64eed 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,551 +1,546 @@
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
 package org.hibernate.testing.junit4;
 
+import static org.junit.Assert.fail;
+
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
-import org.junit.After;
-import org.junit.Before;
-
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
+import org.hibernate.boot.registry.BootstrapServiceRegistry;
+import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
+import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.boot.registry.BootstrapServiceRegistry;
-import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
-import org.hibernate.service.ServiceRegistry;
-import org.hibernate.engine.config.spi.ConfigurationService;
-import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.SkipLog;
 import org.hibernate.testing.cache.CachingRegionFactory;
-
-import static org.junit.Assert.fail;
+import org.junit.After;
+import org.junit.Before;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"deprecation"} )
 public abstract class BaseCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 	public static final String USE_NEW_METADATA_MAPPINGS = "hibernate.test.new_metadata_mappings";
 
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private boolean isMetadataUsed;
 	private Configuration configuration;
 	private StandardServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	protected Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
 	protected StandardServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected Session openSession() throws HibernateException {
 		session = sessionFactory().openSession();
 		return session;
 	}
 
 	protected Session openSession(Interceptor interceptor) throws HibernateException {
 		session = sessionFactory().withOptions().interceptor( interceptor ).openSession();
 		return session;
 	}
 
 
 	// before/after test class ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@BeforeClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected void buildSessionFactory() {
 		// for now, build the configuration to get all the property settings
 		configuration = constructAndConfigureConfiguration();
-		serviceRegistry = buildServiceRegistry( configuration );
+		BootstrapServiceRegistry bootRegistry = buildBootstrapServiceRegistry();
+		serviceRegistry = buildServiceRegistry( bootRegistry, configuration );
 		isMetadataUsed = serviceRegistry.getService( ConfigurationService.class ).getSetting(
 				USE_NEW_METADATA_MAPPINGS,
 				new ConfigurationService.Converter<Boolean>() {
 					@Override
 					public Boolean convert(Object value) {
 						return Boolean.parseBoolean( ( String ) value );
 					}
 				},
 				false
 		);
 		if ( isMetadataUsed ) {
-			sessionFactory = ( SessionFactoryImplementor ) buildMetadata( serviceRegistry ).buildSessionFactory();
+			MetadataImplementor metadataImplementor = buildMetadata( bootRegistry, serviceRegistry );
+			afterConstructAndConfigureMetadata( metadataImplementor );
+			sessionFactory = ( SessionFactoryImplementor ) metadataImplementor.buildSessionFactory();
 		}
 		else {
 			// this is done here because Configuration does not currently support 4.0 xsd
 			afterConstructAndConfigureConfiguration( configuration );
 			sessionFactory = ( SessionFactoryImplementor ) configuration.buildSessionFactory( serviceRegistry );
 		}
 		afterSessionFactoryBuilt();
 	}
 
-	private MetadataImplementor buildMetadata(ServiceRegistry serviceRegistry) {
-		 	MetadataSources sources = new MetadataSources( serviceRegistry );
-			addMappings( sources );
-			return (MetadataImplementor) sources.buildMetadata();
+	protected void rebuildSessionFactory() {
+		if ( sessionFactory == null ) {
+			return;
+		}
+		buildSessionFactory();
+	}
+
+
+	protected void afterConstructAndConfigureMetadata(MetadataImplementor metadataImplementor) {
+
+	}
+
+	private MetadataImplementor buildMetadata(
+			BootstrapServiceRegistry bootRegistry,
+			StandardServiceRegistryImpl serviceRegistry) {
+		MetadataSources sources = new MetadataSources( bootRegistry );
+		addMappings( sources );
+		return (MetadataImplementor) sources.getMetadataBuilder( serviceRegistry ).build();
 	}
 
 	// TODO: is this still needed?
 	protected Configuration buildConfiguration() {
 		Configuration cfg = constructAndConfigureConfiguration();
 		afterConstructAndConfigureConfiguration( cfg );
 		return cfg;
 	}
 
 	private Configuration constructAndConfigureConfiguration() {
 		Configuration cfg = constructConfiguration();
 		configure( cfg );
 		return cfg;
 	}
 
 	private void afterConstructAndConfigureConfiguration(Configuration cfg) {
 		addMappings( cfg );
 		cfg.buildMappings();
 		applyCacheSettings( cfg );
 		afterConfigurationBuilt( cfg );
 	}
 
 	protected Configuration constructConfiguration() {
 		Configuration configuration = new Configuration()
 				.setProperty(Environment.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName()  );
 		configuration.setProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 			final String secondSchemaName = createSecondSchema();
 			if ( StringHelper.isNotEmpty( secondSchemaName ) ) {
 				if ( !( getDialect() instanceof H2Dialect ) ) {
 					throw new UnsupportedOperationException( "Only H2 dialect supports creation of second schema." );
 				}
 				Helper.createH2Schema( secondSchemaName, configuration );
 			}
 		}
 		configuration.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return configuration;
 	}
 
 	protected void configure(Configuration configuration) {
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource(
 						getBaseForMappings() + mapping,
 						getClass().getClassLoader()
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				configuration.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				configuration.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				configuration.addInputStream( is );
 			}
 		}
 	}
 
 	protected void addMappings(MetadataSources sources) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				sources.addResource(
 						getBaseForMappings() + mapping
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				sources.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				sources.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				sources.addInputStream( is );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return NO_MAPPINGS;
 	}
 
 	protected String[] getXmlFiles() {
 		// todo : rename to getOrmXmlFiles()
 		return NO_MAPPINGS;
 	}
 
 	protected void applyCacheSettings(Configuration configuration) {
 		if ( getCacheConcurrencyStrategy() != null ) {
 			Iterator itr = configuration.getClassMappings();
 			while ( itr.hasNext() ) {
 				PersistentClass clazz = (PersistentClass) itr.next();
 				Iterator props = clazz.getPropertyClosureIterator();
 				boolean hasLob = false;
 				while ( props.hasNext() ) {
 					Property prop = (Property) props.next();
 					if ( prop.getValue().isSimpleValue() ) {
 						String type = ( (SimpleValue) prop.getValue() ).getTypeName();
 						if ( "blob".equals(type) || "clob".equals(type) ) {
 							hasLob = true;
 						}
 						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) {
 							hasLob = true;
 						}
 					}
 				}
 				if ( !hasLob && !clazz.isInherited() && overrideCacheStrategy() ) {
 					configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), getCacheConcurrencyStrategy() );
 				}
 			}
 			itr = configuration.getCollectionMappings();
 			while ( itr.hasNext() ) {
 				Collection coll = (Collection) itr.next();
 				configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	protected boolean overrideCacheStrategy() {
 		return true;
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected void afterConfigurationBuilt(Configuration configuration) {
 		afterConfigurationBuilt( configuration.createMappings(), getDialect() );
 	}
 
 	protected void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 	}
 
-	protected StandardServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
-		Properties properties = new Properties();
-		properties.putAll( configuration.getProperties() );
-		Environment.verifyProperties( properties );
-		ConfigurationHelper.resolvePlaceHolders( properties );
-
-		final BootstrapServiceRegistry bootstrapServiceRegistry = generateBootstrapRegistry( properties );
-		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder( bootstrapServiceRegistry )
-				.applySettings( properties );
-		prepareBasicRegistryBuilder( registryBuilder );
-		return (StandardServiceRegistryImpl) registryBuilder.buildServiceRegistry();
-	}
-
-	protected BootstrapServiceRegistry generateBootstrapRegistry(Properties properties) {
+	protected BootstrapServiceRegistry buildBootstrapServiceRegistry() {
 		final BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
 		prepareBootstrapRegistryBuilder( builder );
 		return builder.build();
 	}
 
 	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 	}
 
+	protected StandardServiceRegistryImpl buildServiceRegistry(BootstrapServiceRegistry bootRegistry, Configuration configuration) {
+		Properties properties = new Properties();
+		properties.putAll( configuration.getProperties() );
+		Environment.verifyProperties( properties );
+		ConfigurationHelper.resolvePlaceHolders( properties );
+
+		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder( bootRegistry ).applySettings( properties );
+		prepareBasicRegistryBuilder( registryBuilder );
+		return (StandardServiceRegistryImpl) registryBuilder.build();
+	}
+
 	protected void prepareBasicRegistryBuilder(StandardServiceRegistryBuilder serviceRegistryBuilder) {
 	}
 
 	protected void afterSessionFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	/**
 	 * Feature supported only by H2 dialect.
 	 * @return Provide not empty name to create second schema.
 	 */
 	protected String createSecondSchema() {
 		return null;
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@AfterClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void releaseSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		sessionFactory = null;
 		configuration = null;
         if(serviceRegistry == null){
             return;
         }
         serviceRegistry.destroy();
         serviceRegistry=null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
 	}
 
-	protected void rebuildSessionFactory() {
-		if ( sessionFactory == null ) {
-			return;
-		}
-		sessionFactory.close();
-		serviceRegistry.destroy();
-
-		serviceRegistry = buildServiceRegistry( configuration );
-		if ( isMetadataUsed ) {
-			// need to rebuild metadata because serviceRegistry was recreated
-			sessionFactory = ( SessionFactoryImplementor ) buildMetadata( serviceRegistry ).buildSessionFactory();
-		}
-		else {
-			sessionFactory = ( SessionFactoryImplementor ) configuration.buildSessionFactory( serviceRegistry );
-		}
-		afterSessionFactoryBuilt();
-	}
-
 
 	// before/after each test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Before
 	public final void beforeTest() throws Exception {
 		prepareTest();
 	}
 
 	protected void prepareTest() throws Exception {
 	}
 
 	@After
 	public final void afterTest() throws Exception {
 		if ( isCleanupTestDataRequired() ) {
 			cleanupTestData();
 		}
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
 
 	}
 
 	protected void cleanupCache() {
 		if ( sessionFactory != null ) {
 			sessionFactory.getCache().evictCollectionRegions();
 			sessionFactory.getCache().evictDefaultQueryRegion();
 			sessionFactory.getCache().evictEntityRegions();
 			sessionFactory.getCache().evictQueryRegions();
 			sessionFactory.getCache().evictNaturalIdRegions();
 		}
 	}
 	
 	protected boolean isCleanupTestDataRequired() { return false; }
 	
 	protected void cleanupTestData() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete from java.lang.Object" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 
 	private void cleanupSession() {
 		if ( session != null && ! ( (SessionImplementor) session ).isClosed() ) {
 			if ( session.isConnected() ) {
 				session.doWork( new RollbackWork() );
 			}
 			session.close();
 		}
 		session = null;
 	}
 
 	public class RollbackWork implements Work {
 		public void execute(Connection connection) throws SQLException {
 			connection.rollback();
 		}
 	}
 
 	protected void cleanupTest() throws Exception {
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing", "UnnecessaryUnboxing"})
 	protected void assertAllDataRemoved() {
 		if ( !createSchema() ) {
 			return; // no tables were created...
 		}
 		if ( !Boolean.getBoolean( VALIDATE_DATA_CLEANUP ) ) {
 			return;
 		}
 
 		Session tmpSession = sessionFactory.openSession();
 		try {
 			List list = tmpSession.createQuery( "select o from java.lang.Object o" ).list();
 
 			Map<String,Integer> items = new HashMap<String,Integer>();
 			if ( !list.isEmpty() ) {
 				for ( Object element : list ) {
 					Integer l = items.get( tmpSession.getEntityName( element ) );
 					if ( l == null ) {
 						l = 0;
 					}
 					l = l + 1 ;
 					items.put( tmpSession.getEntityName( element ), l );
 					System.out.println( "Data left: " + element );
 				}
 				fail( "Data is left in the database: " + items.toString() );
 			}
 		}
 		finally {
 			try {
 				tmpSession.close();
 			}
 			catch( Throwable t ) {
 				// intentionally empty
 			}
 		}
 	}
 
 	protected boolean readCommittedIsolationMaintained(String scenario) {
 		int isolation = java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
 		Session testSession = null;
 		try {
 			testSession = openSession();
 			isolation = testSession.doReturningWork(
 					new AbstractReturningWork<Integer>() {
 						@Override
 						public Integer execute(Connection connection) throws SQLException {
 							return connection.getTransactionIsolation();
 						}
 					}
 			);
 		}
 		catch( Throwable ignore ) {
 		}
 		finally {
 			if ( testSession != null ) {
 				try {
 					testSession.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		if ( isolation < java.sql.Connection.TRANSACTION_READ_COMMITTED ) {
 			SkipLog.reportSkip( "environment does not support at least read committed isolation", scenario );
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 }
