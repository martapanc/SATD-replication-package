diff --git a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
index d4fc29ccbe..d1c46933e7 100644
--- a/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionFactory.java
@@ -1,418 +1,377 @@
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
 import javax.naming.Referenceable;
 
-import org.hibernate.boot.registry.StandardServiceRegistry;
-import org.hibernate.cfg.Settings;
-import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
-import org.hibernate.dialect.function.SQLFunction;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
-import org.hibernate.proxy.EntityNotFoundDelegate;
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
-	/**
-	 * Aggregator of special options used to build the SessionFactory.
-	 */
-	public interface SessionFactoryOptions {
-		/**
-		 * The service registry to use in building the factory.
-		 *
-		 * @return The service registry to use.
-		 */
-		public StandardServiceRegistry getServiceRegistry();
-
-		/**
-		 * Get the interceptor to use by default for all sessions opened from this factory.
-		 *
-		 * @return The interceptor to use factory wide.  May be {@code null}
-		 */
-		public Interceptor getInterceptor();
-
-		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
-		public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
-		public SessionFactoryObserver[] getSessionFactoryObservers();
-		public EntityNameResolver[] getEntityNameResolvers();
-		public Settings getSettings();
-
-		/**
-		 * Get the delegate for handling entity-not-found exception conditions.
-		 *
-		 * @return The specific EntityNotFoundDelegate to use,  May be {@code null}
-		 */
-		public EntityNotFoundDelegate getEntityNotFoundDelegate();
-
-		public Map<String, SQLFunction> getCustomSqlFunctionMap();
-
-		public Object getBeanManagerReference();
-
-		public Object getValidatorFactoryReference();
-	}
 
 	/**
 	 * Get the special options used to build the factory.
 	 *
 	 * @return The special options used to build the factory.
 	 */
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
 	 * Get the {@link CollectionMetadata} for all mapped collections.
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
 	 * Retrieve this factory's {@link TypeHelper}.
 	 *
 	 * @return The factory's {@link TypeHelper}
 	 */
 	public TypeHelper getTypeHelper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/Metadata.java b/hibernate-core/src/main/java/org/hibernate/boot/Metadata.java
index 24d776d5a2..ade375aa70 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/Metadata.java
@@ -1,203 +1,205 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot;
 
 import java.util.Map;
 import java.util.UUID;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 
 /**
  * Represents the ORM model as determined from all provided mapping sources.
  *
  * NOTE : for the time being this is essentially a copy of the legacy Mappings contract, split between
  * reading the mapping information exposed here and collecting it via InFlightMetadataCollector
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface Metadata extends Mapping {
 	/**
 	 * Get the builder for {@link org.hibernate.SessionFactory} instances based on this metamodel,
 	 *
 	 * @return The builder for {@link org.hibernate.SessionFactory} instances.
 	 */
 	SessionFactoryBuilder getSessionFactoryBuilder();
 
 	/**
 	 * Short-hand form of building a {@link org.hibernate.SessionFactory} through the builder without any additional
 	 * option overrides.
 	 *
 	 * @return THe built SessionFactory.
 	 */
 	SessionFactory buildSessionFactory();
 
 	/**
 	 * Gets the {@link java.util.UUID} for this metamodel.
 	 *
 	 * @return the UUID.
 	 */
 	UUID getUUID();
 
 	/**
 	 * Retrieve the database model.
 	 *
 	 * @return The database model.
 	 */
 	Database getDatabase();
 
 	/**
 	 * Retrieves the PersistentClass entity metadata representation for known all entities.
 	 *
 	 * Returned collection is immutable
 	 *
 	 * @return All PersistentClass representations.
 	 */
 	java.util.Collection<PersistentClass> getEntityBindings();
 
 	/**
 	 * Retrieves the PersistentClass entity mapping metadata representation for
 	 * the given entity name.
 	 *
 	 * @param entityName The entity name for which to retrieve the metadata.
 	 *
 	 * @return The entity mapping metadata, or {@code null} if no matching entity found.
 	 */
 	PersistentClass getEntityBinding(String entityName);
 
 	/**
 	 * Retrieves the Collection metadata representation for known all collections.
 	 *
 	 * Returned collection is immutable
 	 *
 	 * @return All Collection representations.
 	 */
 	java.util.Collection<Collection> getCollectionBindings();
 
 	/**
 	 * Retrieves the collection mapping metadata for the given collection role.
 	 *
 	 * @param role The collection role for which to retrieve the metadata.
 	 *
 	 * @return The collection mapping metadata, or {@code null} if no matching collection found.
 	 */
 	Collection getCollectionBinding(String role);
 
 	/**
 	 * Retrieves all defined imports (class renames).
 	 *
 	 * @return All imports
 	 */
 	Map<String,String> getImports();
 
 	/**
 	 * Retrieve named query metadata by name.
 	 *
 	 * @param name The query name
 	 *
 	 * @return The named query metadata, or {@code null}.
 	 */
 	NamedQueryDefinition getNamedQueryDefinition(String name);
 
 	java.util.Collection<NamedQueryDefinition> getNamedQueryDefinitions();
 
 	/**
 	 * Retrieve named SQL query metadata.
 	 *
 	 * @param name The SQL query name.
 	 *
 	 * @return The named query metadata, or {@code null}
 	 */
 	NamedSQLQueryDefinition getNamedNativeQueryDefinition(String name);
 
 	java.util.Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions();
 
 	java.util.Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions();
 
 	/**
 	 * Retrieve the metadata for a named SQL result set mapping.
 	 *
 	 * @param name The mapping name.
 	 *
 	 * @return The named result set mapping metadata, or {@code null} if none found.
 	 */
 	ResultSetMappingDefinition getResultSetMapping(String name);
 
 	Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions();
 
 	/**
 	 * Retrieve a type definition by name.
 	 *
 	 * @param typeName The name of the type definition to retrieve.
 	 *
 	 * @return The named type definition, or {@code null}
 	 */
 	TypeDefinition getTypeDefinition(String typeName);
 
 	/**
 	 * Retrieves the complete map of filter definitions.
 	 *
 	 * Returned map is immutable
 	 *
 	 * @return The filter definition map.
 	 */
 	Map<String,FilterDefinition> getFilterDefinitions();
 
 	/**
 	 * Retrieves a filter definition by name.
 	 *
 	 * @param name The name of the filter definition to retrieve
 	 * .
 	 * @return The filter definition, or {@code null}.
 	 */
 	FilterDefinition getFilterDefinition(String name);
 
 	FetchProfile getFetchProfile(String name);
 
 	java.util.Collection<FetchProfile> getFetchProfiles();
 
 	NamedEntityGraphDefinition getNamedEntityGraph(String name);
 
 	Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs();
 
 	IdentifierGeneratorDefinition getIdentifierGenerator(String name);
 
 	java.util.Collection<Table> collectTableMappings();
 
 	Map<String,SQLFunction> getSqlFunctionMap();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
index 50f72201db..f043784c37 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
@@ -1,420 +1,422 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
 import org.hibernate.boot.archive.scan.spi.ScanOptions;
 import org.hibernate.boot.archive.scan.spi.Scanner;
 import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
 import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.MetadataSourceType;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.type.BasicType;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 import org.jboss.jandex.IndexView;
 
 /**
  * Contract for specifying various overrides to be used in metamodel building.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
+ *
+ * @since 5.0
  */
 public interface MetadataBuilder {
 	/**
 	 * Specify the implicit schema name to apply to any unqualified database names
 	 *
 	 * @param implicitSchemaName The implicit schema name
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyImplicitSchemaName(String implicitSchemaName);
 
 	/**
 	 * Specify the implicit catalog name to apply to any unqualified database names
 	 *
 	 * @param implicitCatalogName The implicit catalog name
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyImplicitCatalogName(String implicitCatalogName);
 
 	/**
 	 * Specify the ImplicitNamingStrategy to use in building the Metadata
 	 *
 	 * @param namingStrategy The ImplicitNamingStrategy to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyImplicitNamingStrategy(ImplicitNamingStrategy namingStrategy);
 
 	/**
 	 * Specify the PhysicalNamingStrategy to use in building the Metadata
 	 *
 	 * @param namingStrategy The PhysicalNamingStrategy to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyPhysicalNamingStrategy(PhysicalNamingStrategy namingStrategy);
 
 	/**
 	 * Defines the Hibernate Commons Annotations ReflectionManager to use
 	 *
 	 * @param reflectionManager The ReflectionManager to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @deprecated Deprecated (with no replacement) to indicate that this will go away as
 	 * we migrate away from Hibernate Commons Annotations to Jandex for annotation handling
 	 * and XMl->annotation merging.
 	 */
 	@Deprecated
 	MetadataBuilder applyReflectionManager(ReflectionManager reflectionManager);
 
 	/**
 	 * Specify the second-level cache mode to be used.  This is the cache mode in terms of whether or
 	 * not to cache.
 	 *
 	 * @param cacheMode The cache mode.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #applyAccessType(org.hibernate.cache.spi.access.AccessType)
 	 */
 	MetadataBuilder applySharedCacheMode(SharedCacheMode cacheMode);
 
 	/**
 	 * Specify the second-level access-type to be used by default for entities and collections that define second-level
 	 * caching, but do not specify a granular access-type.
 	 *
 	 * @param accessType The access-type to use as default.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #applySharedCacheMode(javax.persistence.SharedCacheMode)
 	 */
 	MetadataBuilder applyAccessType(AccessType accessType);
 
 	/**
 	 * Allows specifying a specific Jandex index to use for reading annotation information.
 	 * <p/>
 	 * It is <i>important</i> to understand that if a Jandex index is passed in, it is expected that
 	 * this Jandex index already contains all entries for all classes.  No additional indexing will be
 	 * done in this case.
 	 *
 	 * @param jandexView The Jandex index to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyIndexView(IndexView jandexView);
 
 	/**
 	 * Specify the options to be used in performing scanning.
 	 *
 	 * @param scanOptions The scan options.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyScanOptions(ScanOptions scanOptions);
 
 	/**
 	 * Consider this temporary as discussed on {@link ScanEnvironment}
 	 *
 	 * @param scanEnvironment The environment for scanning
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyScanEnvironment(ScanEnvironment scanEnvironment);
 
 	/**
 	 * Specify a particular Scanner instance to use.
 	 *
 	 * @param scanner The scanner to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyScanner(Scanner scanner);
 
 	/**
 	 * Specify a particular ArchiveDescriptorFactory instance to use in scanning.
 	 *
 	 * @param factory The ArchiveDescriptorFactory to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyArchiveDescriptorFactory(ArchiveDescriptorFactory factory);
 
 	/**
 	 * Should we enable support for the "new" (since 3.2) identifier generator mappings for
 	 * handling:<ul>
 	 *     <li>{@link javax.persistence.GenerationType#SEQUENCE}</li>
 	 *     <li>{@link javax.persistence.GenerationType#IDENTITY}</li>
 	 *     <li>{@link javax.persistence.GenerationType#TABLE}</li>
 	 *     <li>{@link javax.persistence.GenerationType#AUTO}</li>
 	 * </ul>
 	 *
 	 * @param enable {@code true} to enable; {@code false} to disable;don't call for
 	 * default.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS
 	 */
 	MetadataBuilder enableNewIdentifierGeneratorSupport(boolean enable);
 
 	/**
 	 * Should we process or ignore explicitly defined discriminators in the case
 	 * of joined-subclasses.  The legacy behavior of Hibernate was to ignore the
 	 * discriminator annotations because Hibernate (unlike some providers) does
 	 * not need discriminators to determine the concrete type when it comes to
 	 * joined inheritance.  However, for portability reasons we do now allow using
 	 * explicit discriminators along with joined inheritance.  It is configurable
 	 * though to support legacy apps.
 	 *
 	 * @param enabled Should processing (not ignoring) explicit discriminators be
 	 * enabled?
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	MetadataBuilder enableExplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
 
 	/**
 	 * Similarly to {@link #enableExplicitDiscriminatorsForJoinedSubclassSupport},
 	 * but here how should we treat joined inheritance when there is no explicitly
 	 * defined discriminator annotations?  If enabled, we will handle joined
 	 * inheritance with no explicit discriminator annotations by implicitly
 	 * creating one (following the JPA implicit naming rules).
 	 * <p/>
 	 * Again the premise here is JPA portability, bearing in mind that some
 	 * JPA provider need these discriminators.
 	 *
 	 * @param enabled Should we implicitly create discriminator for joined
 	 * inheritance if one is not explicitly mentioned?
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	MetadataBuilder enableImplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
 
 	/**
 	 * For entities which do not explicitly say, should we force discriminators into
 	 * SQL selects?  The (historical) default is {@code false}
 	 *
 	 * @param supported {@code true} indicates we will force the discriminator into the select;
 	 * {@code false} indicates we will not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT
 	 */
 	MetadataBuilder enableImplicitForcingOfDiscriminatorsInSelect(boolean supported);
 
 	/**
 	 * Should nationalized variants of character data be used in the database types?
 	 *
 	 * For example, should {@code NVARCHAR} be used instead of {@code VARCHAR}?
 	 * {@code NCLOB} instead of {@code CLOB}?
 	 *
 	 * @param enabled {@code true} says to use nationalized variants; {@code false}
 	 * says to use the non-nationalized variants.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA
 	 */
 	MetadataBuilder enableGlobalNationalizedCharacterDataSupport(boolean enabled);
 
 	/**
 	 * Specify an additional or overridden basic type mapping.
 	 *
 	 * @param type The type addition or override.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyBasicType(BasicType type);
 
 	/**
 	 * Register an additional or overridden custom type mapping.
 	 *
 	 * @param type The custom type
 	 * @param keys The keys under which to register the custom type.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyBasicType(UserType type, String[] keys);
 
 	/**
 	 * Register an additional or overridden composite custom type mapping.
 	 *
 	 * @param type The composite custom type
 	 * @param keys The keys under which to register the composite custom type.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyBasicType(CompositeUserType type, String[] keys);
 
 	/**
 	 * Apply an explicit TypeContributor (implicit application via ServiceLoader will still happen too)
 	 *
 	 * @param typeContributor The contributor to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyTypes(TypeContributor typeContributor);
 
 	/**
 	 * Apply a CacheRegionDefinition to be applied to an entity, collection or query while building the
 	 * Metadata object.
 	 *
 	 * @param cacheRegionDefinition The cache region definition to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition);
 
 	/**
 	 * Apply a ClassLoader for use while building the Metadata.
 	 * <p/>
 	 * Ideally we should avoid accessing ClassLoaders when perform 1st phase of bootstrap.  This
 	 * is a ClassLoader that can be used in cases when we have to.  IN EE managed environments, this
 	 * is the ClassLoader mandated by
 	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}.  This ClassLoader
 	 * is thrown away by the container afterwards.  The idea being that the Class can still be enhanced
 	 * in the application ClassLoader.  In other environments, pass a ClassLoader that performs the
 	 * same function if desired.
 	 *
 	 * @param tempClassLoader ClassLoader for use during building the Metadata
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyTempClassLoader(ClassLoader tempClassLoader);
 
 	/**
 	 * Apply a specific ordering to the processing of sources.  Note that unlike most
 	 * of the methods on this contract that deal with multiple values internally, this
 	 * one *replaces* any already set (its more a setter) instead of adding to.
 	 *
 	 * @param sourceTypes The types, in the order they should be processed
 	 *
 	 * @return {@code this} for method chaining
 	 */
 	MetadataBuilder applySourceProcessOrdering(MetadataSourceType... sourceTypes);
 
 	MetadataBuilder applySqlFunction(String functionName, SQLFunction function);
 
 	MetadataBuilder applyAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
 
 	/**
 	 * Adds an AttributeConverter by a AttributeConverterDefinition
 	 *
 	 * @param definition The definition
 	 *
 	 * @return {@code this} for method chaining
 	 */
 	MetadataBuilder applyAttributeConverter(AttributeConverterDefinition definition);
 
 	/**
 	 * Adds an AttributeConverter by its Class.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(Class)
 	 */
 	MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass);
 
 	/**
 	 * Adds an AttributeConverter by its Class plus a boolean indicating whether to auto apply it.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(Class, boolean)
 	 */
 	MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply);
 
 	/**
 	 * Adds an AttributeConverter instance.
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(AttributeConverter)
 	 */
 	MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter);
 
 	/**
 	 * Adds an AttributeConverter instance, explicitly indicating whether to auto-apply.
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(AttributeConverter, boolean)
 	 */
 	MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter, boolean autoApply);
 
 	MetadataBuilder applyIdGenerationTypeInterpreter(IdGeneratorStrategyInterpreter interpreter);
 
 
 //	/**
 //	 * Specify the resolve to be used in identifying the backing members of a
 //	 * persistent attributes.
 //	 *
 //	 * @param resolver The resolver to use
 //	 *
 //	 * @return {@code this}, for method chaining
 //	 */
 //	public MetadataBuilder with(PersistentAttributeMemberResolver resolver);
 
 
 	/**
 	 * Actually build the metamodel
 	 *
 	 * @return The built metadata.
 	 */
 	public Metadata build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java b/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java
index 82e3abe205..10bb74e35a 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/MetadataSources.java
@@ -1,503 +1,505 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 import javax.xml.transform.dom.DOMSource;
 
 import org.hibernate.boot.archive.spi.InputStreamAccess;
 import org.hibernate.boot.internal.MetadataBuilderImpl;
 import org.hibernate.boot.jaxb.Origin;
 import org.hibernate.boot.jaxb.SourceType;
 import org.hibernate.boot.jaxb.internal.CacheableFileXmlSource;
 import org.hibernate.boot.jaxb.internal.FileXmlSource;
 import org.hibernate.boot.jaxb.internal.InputStreamXmlSource;
 import org.hibernate.boot.jaxb.internal.JarFileEntryXmlSource;
 import org.hibernate.boot.jaxb.internal.JaxpSourceXmlSource;
 import org.hibernate.boot.jaxb.internal.MappingBinder;
 import org.hibernate.boot.jaxb.internal.UrlXmlSource;
 import org.hibernate.boot.jaxb.spi.Binder;
 import org.hibernate.boot.jaxb.spi.Binding;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.SerializationException;
 
 import org.w3c.dom.Document;
 
 /**
  * Entry point into working with sources of metadata information (mapping XML, annotations).   Tell Hibernate
  * about sources and then call {@link #buildMetadata()}, or use {@link #getMetadataBuilder()} to customize
  * how sources are processed (naming strategies, etc).
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public class MetadataSources implements Serializable {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( MetadataSources.class );
 
 	private final ServiceRegistry serviceRegistry;
 
 	// NOTE : The boolean here indicates whether or not to perform validation as we load XML documents.
 	// Should we expose this setting?  Disabling would speed up JAXP and JAXB at runtime, but potentially
 	// at the cost of less obvious errors when a document is not valid.
 	private Binder mappingsBinder = new MappingBinder( true );
 
 	private List<Binding> xmlBindings = new ArrayList<Binding>();
 	private LinkedHashSet<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();
 	private LinkedHashSet<String> annotatedClassNames = new LinkedHashSet<String>();
 	private LinkedHashSet<String> annotatedPackages = new LinkedHashSet<String>();
 
 	public MetadataSources() {
 		this( new BootstrapServiceRegistryBuilder().build() );
 	}
 
 	/**
 	 * Create a metadata sources using the specified service registry.
 	 *
 	 * @param serviceRegistry The service registry to use.
 	 */
 	public MetadataSources(ServiceRegistry serviceRegistry) {
 		// service registry really should be either BootstrapServiceRegistry or StandardServiceRegistry type...
 		if ( ! isExpectedServiceRegistryType( serviceRegistry ) ) {
 			LOG.debugf(
 					"Unexpected ServiceRegistry type [%s] encountered during building of MetadataSources; may cause " +
 							"problems later attempting to construct MetadataBuilder",
 					serviceRegistry.getClass().getName()
 			);
 		}
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	protected static boolean isExpectedServiceRegistryType(ServiceRegistry serviceRegistry) {
 		return BootstrapServiceRegistry.class.isInstance( serviceRegistry )
 				|| StandardServiceRegistry.class.isInstance( serviceRegistry );
 	}
 
 	public List<Binding> getXmlBindings() {
 		return xmlBindings;
 	}
 
 	public Collection<String> getAnnotatedPackages() {
 		return annotatedPackages;
 	}
 
 	public Collection<Class<?>> getAnnotatedClasses() {
 		return annotatedClasses;
 	}
 
 	public Collection<String> getAnnotatedClassNames() {
 		return annotatedClassNames;
 	}
 
 	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	/**
 	 * Get a builder for metadata where non-default options can be specified.
 	 *
 	 * @return The built metadata.
 	 */
 	public MetadataBuilder getMetadataBuilder() {
 		return new MetadataBuilderImpl( this );
 	}
 
 	/**
 	 * Get a builder for metadata where non-default options can be specified.
 	 *
 	 * @return The built metadata.
 	 */
 	public MetadataBuilder getMetadataBuilder(StandardServiceRegistry serviceRegistry) {
 		return new MetadataBuilderImpl( this, serviceRegistry );
 	}
 
 	/**
 	 * Short-hand form of calling {@link #getMetadataBuilder()} and using its
 	 * {@link org.hibernate.boot.MetadataBuilder#build()} method in cases where the application wants
 	 * to accept the defaults.
 	 *
 	 * @return The built metadata.
 	 */
 	public Metadata buildMetadata() {
 		return getMetadataBuilder().build();
 	}
 
 	public Metadata buildMetadata(StandardServiceRegistry serviceRegistry) {
 		return getMetadataBuilder( serviceRegistry ).build();
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
 	 * Read metadata from the annotations attached to the given class.  The important
 	 * distinction here is that the {@link Class} will not be accessed until later
 	 * which is important for on-the-fly bytecode-enhancement
 	 *
 	 * @param annotatedClassName The name of a class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addAnnotatedClassName(String annotatedClassName) {
 		annotatedClassNames.add( annotatedClassName );
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
 	 * Read package-level metadata.
 	 *
 	 * @param packageRef Java Package reference
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addPackage(Package packageRef) {
 		annotatedPackages.add( packageRef.getName() );
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
 		final URL url = classLoaderService().locateResource( name );
 		if ( url == null ) {
 			throw new MappingNotFoundException( origin );
 		}
 
 		xmlBindings.add( new UrlXmlSource( origin, url ).doBind( mappingsBinder ) );
 
 		return this;
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class named {@code foo.bar.Foo} is
 	 * mapped by a file named {@code foo/bar/Foo.hbm.xml} which can be resolved as a classpath resource.
 	 *
 	 * @param entityClass The mapped class. Cannot be {@code null} null.
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @deprecated hbm.xml is a legacy mapping format now considered deprecated.
 	 */
 	@Deprecated
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
 	 * @param path The path to a file.  Expected to be resolvable by {@link java.io.File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addFile(java.io.File)
 	 */
 	public MetadataSources addFile(String path) {
 		addFile(
 				new Origin( SourceType.FILE, path ),
 				new File( path )
 		);
 		return this;
 	}
 
 	private void addFile(Origin origin, File file) {
 		LOG.tracef( "reading mappings from file : %s", origin.getName() );
 
 		if ( !file.exists() ) {
 			throw new MappingNotFoundException( origin );
 		}
 
 		xmlBindings.add( new FileXmlSource( origin, file ).doBind( mappingsBinder ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param file The reference to the XML file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addFile(File file) {
 		addFile(
 				new Origin( SourceType.FILE, file.getPath() ),
 				file
 		);
 		return this;
 	}
 
 	/**
 	 * See {@link #addCacheableFile(java.io.File)} for description
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link java.io.File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public MetadataSources addCacheableFile(String path) {
 		final Origin origin = new Origin( SourceType.FILE, path );
 		addCacheableFile( origin, new File( path ) );
 		return this;
 	}
 
 	private void addCacheableFile(Origin origin, File file) {
 		xmlBindings.add( new CacheableFileXmlSource( origin, file, false ).doBind( mappingsBinder ) );
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
 		final Origin origin = new Origin( SourceType.FILE, file.getName() );
 		addCacheableFile( origin, file );
 		return this;
 	}
 
 	/**
 	 * <b>INTENDED FOR TESTSUITE USE ONLY!</b>
 	 * <p/>
 	 * Much like {@link #addCacheableFile(java.io.File)} except that here we will fail immediately if
 	 * the cache version cannot be found or used for whatever reason
 	 *
 	 * @param file The xml file, not the bin!
 	 *
 	 * @return The dom "deserialized" from the cached file.
 	 *
 	 * @throws org.hibernate.type.SerializationException Indicates a problem deserializing the cached dom tree
 	 * @throws java.io.FileNotFoundException Indicates that the cached file was not found or was not usable.
 	 */
 	public MetadataSources addCacheableFileStrictly(File file) throws SerializationException, FileNotFoundException {
 		final Origin origin = new Origin( SourceType.FILE, file.getAbsolutePath() );
 		xmlBindings.add( new CacheableFileXmlSource( origin, file, true ).doBind( mappingsBinder ) );
 		return this;
 	}
 
 	/**
 	 * Read metadata from an {@link java.io.InputStream} access
 	 *
 	 * @param xmlInputStreamAccess Access to an input stream containing a DOM.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addInputStream(InputStreamAccess xmlInputStreamAccess) {
 		final Origin origin = new Origin( SourceType.INPUT_STREAM, xmlInputStreamAccess.getStreamName() );
 		InputStream xmlInputStream = xmlInputStreamAccess.accessInputStream();
 		try {
 			xmlBindings.add( new InputStreamXmlSource( origin, xmlInputStream, false ).doBind( mappingsBinder ) );
 		}
 		finally {
 			try {
 				xmlInputStream.close();
 			}
 			catch (IOException e) {
 				LOG.debugf( "Unable to close InputStream obtained from InputStreamAccess : " + xmlInputStreamAccess.getStreamName() );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Read metadata from an {@link java.io.InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addInputStream(InputStream xmlInputStream) {
 		final Origin origin = new Origin( SourceType.INPUT_STREAM, null );
 		xmlBindings.add( new InputStreamXmlSource( origin, xmlInputStream, false ).doBind( mappingsBinder ) );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a {@link java.net.URL}
 	 *
 	 * @param url The url for the mapping document to be read.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addURL(URL url) {
 		final String urlExternalForm = url.toExternalForm();
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		final Origin origin = new Origin( SourceType.URL, urlExternalForm );
 		xmlBindings.add( new UrlXmlSource( origin, url ).doBind( mappingsBinder ) );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a DOM {@link org.w3c.dom.Document}
 	 *
 	 * @param document The DOM document
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	@Deprecated
 	public MetadataSources addDocument(Document document) {
 		final Origin origin = new Origin( SourceType.DOM, Origin.UNKNOWN_FILE_PATH );
 		xmlBindings.add( new JaxpSourceXmlSource( origin, new DOMSource( document ) ).doBind( mappingsBinder ) );
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
 						xmlBindings.add(
 								new JarFileEntryXmlSource( origin, jarFile, zipEntry ).doBind( mappingsBinder )
 						);
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
 		if ( files != null && files.length > 0 ) {
 			for ( File file : files ) {
 				if ( file.isDirectory() ) {
 					addDirectory( file );
 				}
 				else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 					addFile( file );
 				}
 			}
 		}
 		return this;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/SchemaAutoTooling.java b/hibernate-core/src/main/java/org/hibernate/boot/SchemaAutoTooling.java
new file mode 100644
index 0000000000..b5a5d10844
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/SchemaAutoTooling.java
@@ -0,0 +1,77 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.boot;
+
+import org.hibernate.Hibernate;
+import org.hibernate.HibernateException;
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * Defines the possible values for "hbm2ddl_auto"
+ *
+ * @author Steve Ebersole
+ */
+public enum SchemaAutoTooling {
+	/**
+	 * Drop the schema and recreate it on SessionFactory startup.
+	 */
+	CREATE,
+	/**
+	 * Drop the schema and recreate it on SessionFactory startup.  Additionally, drop the
+	 * schema on SessionFactory shutdown.
+	 */
+	CREATE_DROP,
+	/**
+	 * Update (alter) the schema on SessionFactory startup.
+	 */
+	UPDATE,
+	/**
+	 * Validate the schema on SessionFactory startup.
+	 */
+	VALIDATE;
+
+	public static SchemaAutoTooling interpret(String configurationValue) {
+		if ( StringHelper.isEmpty( configurationValue ) ) {
+			return null;
+		}
+		else if ( "validate".equals( configurationValue ) ) {
+			return VALIDATE;
+		}
+		else if ( "update".equals( configurationValue ) ) {
+			return UPDATE;
+		}
+		else if ( "create".equals( configurationValue ) ) {
+			return CREATE;
+		}
+		else if ( "create-drop".equals( configurationValue ) ) {
+			return CREATE_DROP;
+		}
+		else {
+			throw new HibernateException(
+					"Unrecognized hbm2ddl_auto value : " + configurationValue
+							+ ".  Supported values include create, create-drop, update, and validate."
+			);
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
index d04983f754..d711e5c1bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
@@ -1,162 +1,668 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot;
 
+import java.util.Map;
+
+import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
+import org.hibernate.MultiTenancyStrategy;
+import org.hibernate.NullPrecedence;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
+import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.QueryTranslatorFactory;
+import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * The contract for building a {@link org.hibernate.SessionFactory} given a number of options.
  *
  * @author Steve Ebersole
  * @author Gail Badner
+ *
+ * @since 5.0
  */
 public interface SessionFactoryBuilder {
 	/**
-	 * Names an interceptor to be applied to the SessionFactory, which in turn means it will be used by all
-	 * Sessions unless one is explicitly specified in {@link org.hibernate.SessionBuilder#interceptor}
+	 * Apply a Bean Validation ValidatorFactory to the SessionFactory being built.
 	 *
-	 * @param interceptor The interceptor
+	 * NOTE : De-typed to avoid hard dependency on Bean Validation jar at runtime.
+	 *
+	 * @param validatorFactory The Bean Validation ValidatorFactory to use
 	 *
 	 * @return {@code this}, for method chaining
+	 */
+	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory);
+
+	/**
+	 * Apply a CDI BeanManager to the SessionFactory being built.
 	 *
-	 * @see org.hibernate.cfg.AvailableSettings#INTERCEPTOR
+	 * NOTE : De-typed to avoid hard dependency on CDI jar at runtime.
+	 *
+	 * @param beanManager The CDI BeanManager to use
+	 *
+	 * @return {@code this}, for method chaining
 	 */
-	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor);
+	public SessionFactoryBuilder applyBeanManager(Object beanManager);
 
 	/**
-	 * Specifies a custom entity dirtiness strategy to be applied to the SessionFactory.  See the contract
-	 * of {@link org.hibernate.CustomEntityDirtinessStrategy} for details.
+	 * Applies a SessionFactory name.
 	 *
-	 * @param strategy The custom strategy to be used.
+	 * @param sessionFactoryName The name to use for the SessionFactory being built
 	 *
 	 * @return {@code this}, for method chaining
 	 *
-	 * @see org.hibernate.cfg.AvailableSettings#CUSTOM_ENTITY_DIRTINESS_STRATEGY
+	 * @see org.hibernate.cfg.AvailableSettings#SESSION_FACTORY_NAME
 	 */
-	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy);
+	public SessionFactoryBuilder applyName(String sessionFactoryName);
 
 	/**
-	 * Specifies a strategy for resolving the notion of a "current" tenant-identifier when using multi-tenancy
-	 * together with current sessions
+	 * Applies a SessionFactory name.
 	 *
-	 * @param resolver The resolution strategy to use.
+	 * @param isJndiName {@code true} indicates that the name specified in
+	 * {@link #applyName} will be used for binding the SessionFactory into JNDI.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
-	 * @see org.hibernate.cfg.AvailableSettings#MULTI_TENANT_IDENTIFIER_RESOLVER
+	 * @see org.hibernate.cfg.AvailableSettings#SESSION_FACTORY_NAME_IS_JNDI
 	 */
-	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver);
+	public SessionFactoryBuilder applyNameAsJndiName(boolean isJndiName);
+
+	/**
+	 * Applies whether Sessions should be automatically closed at the end of the transaction.
+	 *
+	 * @param enabled {@code true} indicates they should be auto-closed; {@code false} indicates not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#AUTO_CLOSE_SESSION
+	 */
+	public SessionFactoryBuilder applyAutoClosing(boolean enabled);
+
+	/**
+	 * Applies whether Sessions should be automatically flushed at the end of the transaction.
+	 *
+	 * @param enabled {@code true} indicates they should be auto-flushed; {@code false} indicates not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#FLUSH_BEFORE_COMPLETION
+	 */
+	public SessionFactoryBuilder applyAutoFlushing(boolean enabled);
+
+	/**
+	 * Applies whether statistics gathering is enabled.
+	 *
+	 * @param enabled {@code true} indicates that statistics gathering should be enabled; {@code false} indicates not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#GENERATE_STATISTICS
+	 */
+	public SessionFactoryBuilder applyStatisticsSupport(boolean enabled);
+
+	/**
+	 * Names an interceptor to be applied to the SessionFactory, which in turn means it will be used by all
+	 * Sessions unless one is explicitly specified in {@link org.hibernate.SessionBuilder#interceptor}
+	 *
+	 * @param interceptor The interceptor
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#INTERCEPTOR
+	 */
+	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor);
 
 	/**
 	 * Specifies one or more observers to be applied to the SessionFactory.  Can be called multiple times to add
 	 * additional observers.
 	 *
 	 * @param observers The observers to add
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver... observers);
 
 	/**
+	 * Specifies a custom entity dirtiness strategy to be applied to the SessionFactory.  See the contract
+	 * of {@link org.hibernate.CustomEntityDirtinessStrategy} for details.
+	 *
+	 * @param strategy The custom strategy to be used.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#CUSTOM_ENTITY_DIRTINESS_STRATEGY
+	 */
+	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy);
+
+	/**
 	 * Specifies one or more entity name resolvers to be applied to the SessionFactory (see the {@link org.hibernate.EntityNameResolver}
 	 * contract for more information..  Can be called multiple times to add additional resolvers..
 	 *
 	 * @param entityNameResolvers The entityNameResolvers to add
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder addEntityNameResolver(EntityNameResolver... entityNameResolvers);
 
 	/**
 	 * Names the {@link org.hibernate.proxy.EntityNotFoundDelegate} to be applied to the SessionFactory.  EntityNotFoundDelegate is a
 	 * strategy that accounts for different exceptions thrown between Hibernate and JPA when an entity cannot be found.
 	 *
 	 * @param entityNotFoundDelegate The delegate/strategy to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate);
 
 	/**
+	 * Should generated identifiers be "unset" on entities during a rollback?
+	 *
+	 * @param enabled {@code true} indicates identifiers should be unset; {@code false} indicates not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_IDENTIFIER_ROLLBACK
+	 */
+	public SessionFactoryBuilder applyIdentifierRollbackSupport(boolean enabled);
+
+	/**
+	 * Applies the given entity mode as the default for the SessionFactory.
+	 *
+	 * @param entityMode The default entity mode to use.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_ENTITY_MODE
+	 *
+	 * @deprecated Different entity modes per entity is soon to be removed as a feature.
+	 */
+	@Deprecated
+	public SessionFactoryBuilder applyDefaultEntityMode(EntityMode entityMode);
+
+	/**
+	 * Should attributes using columns marked as not-null be checked (by Hibernate) for nullness?
+	 *
+	 * @param enabled {@code true} indicates that Hibernate should perform nullness checking; {@code false} indicates
+	 * it should not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#CHECK_NULLABILITY
+	 */
+	public SessionFactoryBuilder applyNullabilityChecking(boolean enabled);
+
+	/**
+	 * Should the application be allowed to initialize uninitialized lazy state outside the bounds of a transaction?
+	 *
+	 * @param enabled {@code true} indicates initialization outside the transaction should be allowed; {@code false}
+	 * indicates it should not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#ENABLE_LAZY_LOAD_NO_TRANS
+	 */
+	public SessionFactoryBuilder applyLazyInitializationOutsideTransaction(boolean enabled);
+
+	/**
 	 * Specify the EntityTuplizerFactory to use.
 	 *
 	 * @param entityTuplizerFactory The EntityTuplizerFactory to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory);
 
 	/**
 	 * Register the default {@link org.hibernate.tuple.entity.EntityTuplizer} to be applied to the SessionFactory.
 	 *
 	 * @param entityMode The entity mode that which this tuplizer will be applied.
 	 * @param tuplizerClass The custom tuplizer class.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder applyEntityTuplizer(
 			EntityMode entityMode,
 			Class<? extends EntityTuplizer> tuplizerClass);
 
 	/**
-	 * Apply a Bean Validation ValidatorFactory to the SessionFactory being built.
+	 * How should updates and deletes that span multiple tables be handled?
 	 *
-	 * NOTE : De-typed to avoid hard dependency on Bean Validation jar at runtime.
+	 * @param strategy The strategy for handling multi-table updates and deletes.
 	 *
-	 * @param validatorFactory The Bean Validation ValidatorFactory to use
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#HQL_BULK_ID_STRATEGY
+	 */
+	public SessionFactoryBuilder applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy);
+
+	/**
+	 * What style of batching should be used?
+	 *
+	 * @param style The style to use
 	 *
 	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#BATCH_FETCH_STYLE
 	 */
-	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory);
+	public SessionFactoryBuilder applyBatchFetchStyle(BatchFetchStyle style);
 
 	/**
-	 * Apply a CDI BeanManager to the SessionFactory being built.
+	 * Allows specifying a default batch-fetch size for all entities and collections
+	 * which do not otherwise specify a batch-fetch size.
 	 *
-	 * NOTE : De-typed to avoid hard dependency on CDI jar at runtime.
+	 * @param size The size to use for batch fetching for entities/collections which
+	 * do not specify an explicit batch fetch size.
 	 *
-	 * @param beanManager The CDI BeanManager to use
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_BATCH_FETCH_SIZE
+	 */
+	public SessionFactoryBuilder applyDefaultBatchFetchSize(int size);
+
+	/**
+	 * Apply a limit to the depth Hibernate will use for outer joins.  Note that this is different than an
+	 * overall limit on the number of joins...
+	 *
+	 * @param depth The depth for limiting joins.
 	 *
 	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#MAX_FETCH_DEPTH
 	 */
-	public SessionFactoryBuilder applyBeanManager(Object beanManager);
+	public SessionFactoryBuilder applyMaximumFetchDepth(int depth);
+
+	/**
+	 * Apply a null precedence (NULLS FIRST, NULLS LAST) to be applied order-by clauses rendered into
+	 * SQL queries.
+	 *
+	 * @param nullPrecedence The default null precedence to use.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_NULL_ORDERING
+	 */
+	public SessionFactoryBuilder applyDefaultNullPrecedence(NullPrecedence nullPrecedence);
+
+	/**
+	 * Apply whether ordering of inserts should be enabled.  This allows more efficient SQL
+	 * generation via the use of batching for the inserts; the cost is that the determination of the
+	 * ordering is far more inefficient than not ordering.
+	 *
+	 * @param enabled {@code true} indicates that ordering should be enabled; {@code false} indicates not
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#ORDER_INSERTS
+	 */
+	public SessionFactoryBuilder applyOrderingOfInserts(boolean enabled);
+
+	/**
+	 * Apply whether ordering of updates should be enabled.  This allows more efficient SQL
+	 * generation via the use of batching for the updates; the cost is that the determination of the
+	 * ordering is far more inefficient than not ordering.
+	 *
+	 * @param enabled {@code true} indicates that ordering should be enabled; {@code false} indicates not
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#ORDER_UPDATES
+	 */
+	public SessionFactoryBuilder applyOrderingOfUpdates(boolean enabled);
+
+	/**
+	 * Apply the form of multi-tenancy used by the application
+	 *
+	 * @param strategy The form of multi-tenancy in use.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#MULTI_TENANT
+	 */
+	public SessionFactoryBuilder applyMultiTenancyStrategy(MultiTenancyStrategy strategy);
+
+	/**
+	 * Specifies a strategy for resolving the notion of a "current" tenant-identifier when using multi-tenancy
+	 * together with current sessions
+	 *
+	 * @param resolver The resolution strategy to use.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#MULTI_TENANT_IDENTIFIER_RESOLVER
+	 */
+	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver);
+
+	/**
+	 * Should we track JTA transactions to attempt to detect timeouts?
+	 *
+	 * @param enabled {@code true} indicates we should track by thread; {@code false} indicates not
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#JTA_TRACK_BY_THREAD
+	 *
+	 * @deprecated This should be replaced by new TransactionCoordinator work...
+	 */
+	@Deprecated
+	public SessionFactoryBuilder applyJtaTrackingByThread(boolean enabled);
+
+	/**
+	 * Apply query substitutions to use in HQL queries.  Note, this is a legacy feature and almost always
+	 * never needed anymore...
+	 *
+	 * @param substitutions The substitution map
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#QUERY_SUBSTITUTIONS
+	 *
+	 * @deprecated This is a legacy feature and should never be needed anymore...
+	 */
+	@Deprecated
+	public SessionFactoryBuilder applyQuerySubstitutions(Map substitutions);
+
+	/**
+	 * Should we strictly adhere to JPA Query Language (JPQL) syntax, or more broadly support
+	 * all of Hibernate's superset (HQL)?
+	 * <p/>
+	 * Setting this to {@code true} may cause valid HQL to throw an exception because it violates
+	 * the JPQL subset.
+	 *
+	 * @param enabled {@code true} indicates that we should strictly adhere to the JPQL subset; {@code false}
+	 * indicates we should accept the broader HQL syntax.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#JPAQL_STRICT_COMPLIANCE
+	 */
+	public SessionFactoryBuilder applyStrictJpaQueryLanguageCompliance(boolean enabled);
+
+	/**
+	 * Should named queries be checked on startup?
+	 *
+	 * @param enabled {@code true} indicates that they should; {@code false} indicates they should not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#QUERY_STARTUP_CHECKING
+	 */
+	public SessionFactoryBuilder applyNamedQueryCheckingOnStartup(boolean enabled);
+
+	/**
+	 * Should second level caching support be enabled?
+	 *
+	 * @param enabled {@code true} indicates we should enable the use of second level caching; {@code false}
+	 * indicates we should disable the use of second level caching.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_SECOND_LEVEL_CACHE
+	 */
+	public SessionFactoryBuilder applySecondLevelCacheSupport(boolean enabled);
+
+	/**
+	 * Should second level query caching support be enabled?
+	 *
+	 * @param enabled {@code true} indicates we should enable the use of second level query
+	 * caching; {@code false} indicates we should disable the use of second level query caching.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_QUERY_CACHE
+	 */
+	public SessionFactoryBuilder applyQueryCacheSupport(boolean enabled);
+
+	/**
+	 * Specifies a QueryCacheFactory to use for building query cache handlers.
+	 *
+	 * @param factory The QueryCacheFactory to use
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#QUERY_CACHE_FACTORY
+	 */
+	public SessionFactoryBuilder applyQueryCacheFactory(QueryCacheFactory factory);
+
+	/**
+	 * Apply a prefix to prepended to all cache region names for this SessionFactory.
+	 *
+	 * @param prefix The prefix.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#CACHE_REGION_PREFIX
+	 */
+	public SessionFactoryBuilder applyCacheRegionPrefix(String prefix);
+
+	/**
+	 * By default, Hibernate will always just push data into the cache without first checking
+	 * if that data already exists.  For some caches (mainly distributed caches) this can have a
+	 * major adverse performance impact.  For these caches, it is best to enable this "minimal puts"
+	 * feature.
+	 * <p/>
+	 * Cache integrations also report whether "minimal puts" should be enabled by default.  So its is
+	 * very rare that users need to set this, generally speaking.
+	 *
+	 * @param enabled {@code true} indicates Hibernate should first check whether data exists and only
+	 * push to the cache if it does not already exist. {@code false} indicates to perform the default
+	 * behavior.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_MINIMAL_PUTS
+	 * @see org.hibernate.cache.spi.RegionFactory#isMinimalPutsEnabledByDefault()
+	 */
+	public SessionFactoryBuilder applyMinimalPutsForCaching(boolean enabled);
+
+	/**
+	 * By default, Hibernate stores data in the cache in its own optimized format.  However,
+	 * that format is impossible to "read" if browsing the cache.  The use of "structured" cache
+	 * entries allows the cached data to be read.
+	 *
+	 * @param enabled {@code true} indicates that structured cache entries (human readable) should be used;
+	 * {@code false} indicates that the native entry structure should be used.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_STRUCTURED_CACHE
+	 */
+	public SessionFactoryBuilder applyStructuredCacheEntries(boolean enabled);
 
+	/**
+	 * Generally, Hibernate will extract the information from an entity and put that
+	 * extracted information into the second-level cache.  This is by far the safest way to
+	 * second-level cache persistent data.  However, there are some cases where it is safe
+	 * to cache the entity instance directly.  This setting controls whether that is used
+	 * in those cases.
+	 *
+	 * @param enabled {@code true} indicates that applicable entities will be stored into the
+	 * second-level cache directly by reference; false indicates that all entities will be stored
+	 * via the extraction approach.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_DIRECT_REFERENCE_CACHE_ENTRIES
+	 */
+	public SessionFactoryBuilder applyDirectReferenceCaching(boolean enabled);
+
+	/**
+	 * When using bi-directional many-to-one associations and caching the one-to-many side
+	 * it is expected that both sides of the association are managed (actually that is true of
+	 * all bi-directional associations).  However, in this case, if the user forgets to manage the
+	 * one-to-many side stale data can be left in the second-level cache.
+	 * <p/>
+	 * Warning: enabling this will have a performance impact.  Hence why it is disabled by default
+	 * (for good citizens) and is an opt-in setting.
+	 *
+	 * @param enabled {@code true} indicates that these collection caches should be evicted automatically.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#AUTO_EVICT_COLLECTION_CACHE
+	 */
+	public SessionFactoryBuilder applyAutomaticEvictionOfCollectionCaches(boolean enabled);
+
+	/**
+	 * Specifies the maximum number of statements to batch together in a JDBC batch for
+	 * insert, update and delete operations.  A non-zero number enables batching, but really
+	 * only a number greater than zero will have any effect.  If used, a number great than 5
+	 * is suggested.
+	 *
+	 * @param size The batch size to use.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#STATEMENT_BATCH_SIZE
+	 */
+	public SessionFactoryBuilder applyJdbcBatchSize(int size);
+
+	/**
+	 * This setting controls whether versioned entities will be included in JDBC batching.  The reason
+	 * being that some JDBC drivers have a problems returning "accurate" update counts from batch statements.
+	 * This is setting is {@code false} by default.
+	 *
+	 * @param enabled The batch size to use.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#BATCH_VERSIONED_DATA
+	 */
+	public SessionFactoryBuilder applyJdbcBatchingForVersionedEntities(boolean enabled);
+
+	/**
+	 * Should scrollable results be supported in queries?  We ask the JDBC driver whether it
+	 * supports scrollable result sets as the default for this setting, but some drivers do not
+	 * accurately report this via DatabaseMetaData.  Also, needed if user is supplying connections
+	 * (and so no Connection is available when we bootstrap).
+	 *
+	 * @param enabled {@code true} to enable this support, {@code false} to disable it
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_SCROLLABLE_RESULTSET
+	 */
+	public SessionFactoryBuilder applyScrollableResultsSupport(boolean enabled);
+
+	/**
+	 * Hibernate currently accesses results from the JDBC ResultSet by name.  This is known
+	 * to be VERY slow on some drivers, especially older Oracle drivers.  This setting
+	 * allows Hibernate to wrap the ResultSet of the JDBC driver to manage the name->position
+	 * resolution itself.
+	 *
+	 * @param enabled {@code true} indicates Hibernate should wrap result sets; {@code false} indicates
+	 * it should not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#WRAP_RESULT_SETS
+	 */
+	public SessionFactoryBuilder applyResultSetsWrapping(boolean enabled);
+
+	/**
+	 * Should JDBC {@link java.sql.PreparedStatement#getGeneratedKeys()} feature be used for
+	 * retrieval of *insert-generated* ids?
+	 *
+	 * @param enabled {@code true} indicates we should use JDBC getGeneratedKeys support; {@code false}
+	 * indicates we should not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_GET_GENERATED_KEYS
+	 */
+	public SessionFactoryBuilder applyGetGeneratedKeysSupport(boolean enabled);
+
+	/**
+	 * Apply a fetch size to the JDBC driver for fetching results.
+	 *
+	 * @param size The fetch saize to be passed to the driver.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_GET_GENERATED_KEYS
+	 * @see java.sql.Statement#setFetchSize(int)
+	 */
+	public SessionFactoryBuilder applyJdbcFetchSize(int size);
+
+	/**
+	 * Apply a ConnectionReleaseMode.
+	 *
+	 * @param connectionReleaseMode The ConnectionReleaseMode to use.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#RELEASE_CONNECTIONS
+	 */
+	public SessionFactoryBuilder applyConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode);
+
+	/**
+	 * Should Hibernate apply comments to SQL it generates?
+	 *
+	 * @param enabled {@code true} indicates comments should be applied; {@code false} indicates not.
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_SQL_COMMENTS
+	 */
+	public SessionFactoryBuilder applySqlComments(boolean enabled);
+
+	/**
+	 * Apply a SQLFunction to the underlying {@link org.hibernate.dialect.function.SQLFunctionRegistry}.
+	 * <p/>
+	 * TODO : Ultimately I would like this to move to {@link org.hibernate.boot.MetadataBuilder} in conjunction with allowing mappings to reference SQLFunctions.
+	 * today mappings can only name SQL functions directly, not through the SQLFunctionRegistry indirection
+	 *
+	 * @param registrationName The name to register it under.
+	 * @param sqlFunction The SQLFunction impl
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public SessionFactoryBuilder applySqlFunction(String registrationName, SQLFunction sqlFunction);
 
 	/**
+	 * Allows unwrapping this builder as another, more specific type.
+	 *
+	 * @param type
+	 * @param <T>
+	 *
+	 * @return The unwrapped builder.
+	 */
+	public <T extends SessionFactoryBuilder> T unwrap(Class<T> type);
+
+	/**
 	 * After all options have been set, build the SessionFactory.
 	 *
 	 * @return The built SessionFactory.
 	 */
 	public SessionFactory build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
index dfd9b112fa..e60d28c3cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
@@ -1,261 +1,945 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.internal;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
-import java.util.Properties;
 
+import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
+import org.hibernate.MultiTenancyStrategy;
+import org.hibernate.NullPrecedence;
+import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
+import org.hibernate.boot.SchemaAutoTooling;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.boot.spi.SessionFactoryOptions;
+import org.hibernate.cache.internal.StandardQueryCacheFactory;
+import org.hibernate.cache.spi.QueryCacheFactory;
+import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.AvailableSettings;
-import org.hibernate.cfg.Settings;
-import org.hibernate.cfg.SettingsFactory;
+import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
+import org.hibernate.engine.config.internal.ConfigurationServiceImpl;
 import org.hibernate.engine.config.spi.ConfigurationService;
+import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.transaction.spi.TransactionFactory;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
+import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
 import org.hibernate.internal.SessionFactoryImpl;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
+import org.jboss.logging.Logger;
+
+import static org.hibernate.cfg.AvailableSettings.AUTO_CLOSE_SESSION;
+import static org.hibernate.cfg.AvailableSettings.AUTO_EVICT_COLLECTION_CACHE;
+import static org.hibernate.cfg.AvailableSettings.AUTO_SESSION_EVENTS_LISTENER;
+import static org.hibernate.cfg.AvailableSettings.BATCH_FETCH_STYLE;
+import static org.hibernate.cfg.AvailableSettings.BATCH_VERSIONED_DATA;
+import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_PREFIX;
+import static org.hibernate.cfg.AvailableSettings.CHECK_NULLABILITY;
+import static org.hibernate.cfg.AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY;
+import static org.hibernate.cfg.AvailableSettings.DEFAULT_BATCH_FETCH_SIZE;
+import static org.hibernate.cfg.AvailableSettings.DEFAULT_ENTITY_MODE;
+import static org.hibernate.cfg.AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS;
+import static org.hibernate.cfg.AvailableSettings.FLUSH_BEFORE_COMPLETION;
+import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
+import static org.hibernate.cfg.AvailableSettings.HQL_BULK_ID_STRATEGY;
+import static org.hibernate.cfg.AvailableSettings.INTERCEPTOR;
+import static org.hibernate.cfg.AvailableSettings.JPAQL_STRICT_COMPLIANCE;
+import static org.hibernate.cfg.AvailableSettings.JTA_TRACK_BY_THREAD;
+import static org.hibernate.cfg.AvailableSettings.LOG_SESSION_METRICS;
+import static org.hibernate.cfg.AvailableSettings.MAX_FETCH_DEPTH;
+import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER;
+import static org.hibernate.cfg.AvailableSettings.ORDER_INSERTS;
+import static org.hibernate.cfg.AvailableSettings.ORDER_UPDATES;
+import static org.hibernate.cfg.AvailableSettings.QUERY_CACHE_FACTORY;
+import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
+import static org.hibernate.cfg.AvailableSettings.QUERY_SUBSTITUTIONS;
+import static org.hibernate.cfg.AvailableSettings.RELEASE_CONNECTIONS;
+import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME;
+import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI;
+import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
+import static org.hibernate.cfg.AvailableSettings.STATEMENT_FETCH_SIZE;
+import static org.hibernate.cfg.AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES;
+import static org.hibernate.cfg.AvailableSettings.USE_GET_GENERATED_KEYS;
+import static org.hibernate.cfg.AvailableSettings.USE_IDENTIFIER_ROLLBACK;
+import static org.hibernate.cfg.AvailableSettings.USE_MINIMAL_PUTS;
+import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
+import static org.hibernate.cfg.AvailableSettings.USE_SCROLLABLE_RESULTSET;
+import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
+import static org.hibernate.cfg.AvailableSettings.USE_SQL_COMMENTS;
+import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;
+import static org.hibernate.cfg.AvailableSettings.WRAP_RESULT_SETS;
+import static org.hibernate.engine.config.spi.StandardConverters.BOOLEAN;
+
 /**
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
+	private static final Logger log = Logger.getLogger( SessionFactoryBuilderImpl.class );
+
 	private final MetadataImplementor metadata;
 	private final SessionFactoryOptionsImpl options;
 
 	SessionFactoryBuilderImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
 		options = new SessionFactoryOptionsImpl( metadata.getMetadataBuildingOptions().getServiceRegistry() );
 
 		if ( metadata.getSqlFunctionMap() != null ) {
 			for ( Map.Entry<String, SQLFunction> sqlFunctionEntry : metadata.getSqlFunctionMap().entrySet() ) {
 				applySqlFunction( sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue() );
 			}
 		}
 	}
 
 	@Override
-	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor) {
-		this.options.interceptor = interceptor;
+	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory) {
+		this.options.validatorFactoryReference = validatorFactory;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy) {
-		this.options.customEntityDirtinessStrategy = strategy;
+	public SessionFactoryBuilder applyBeanManager(Object beanManager) {
+		this.options.beanManagerReference = beanManager;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver) {
-		this.options.currentTenantIdentifierResolver = resolver;
+	public SessionFactoryBuilder applyName(String sessionFactoryName) {
+		this.options.sessionFactoryName = sessionFactoryName;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyNameAsJndiName(boolean isJndiName) {
+		this.options.sessionFactoryNameAlsoJndiName = isJndiName;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyAutoClosing(boolean enabled) {
+		this.options.autoCloseSessionEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyAutoFlushing(boolean enabled) {
+		this.options.flushBeforeCompletionEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyStatisticsSupport(boolean enabled) {
+		this.options.statisticsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver... observers) {
 		this.options.sessionFactoryObserverList.addAll( Arrays.asList( observers ) );
 		return this;
 	}
 
 	@Override
+	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor) {
+		this.options.interceptor = interceptor;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy) {
+		this.options.customEntityDirtinessStrategy = strategy;
+		return this;
+	}
+
+
+	@Override
 	public SessionFactoryBuilder addEntityNameResolver(EntityNameResolver... entityNameResolvers) {
 		this.options.entityNameResolvers.addAll( Arrays.asList( entityNameResolvers ) );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.options.entityNotFoundDelegate = entityNotFoundDelegate;
 		return this;
 	}
 
 	@Override
+	public SessionFactoryBuilder applyIdentifierRollbackSupport(boolean enabled) {
+		this.options.identifierRollbackEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyDefaultEntityMode(EntityMode entityMode) {
+		this.options.defaultEntityMode = entityMode;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyNullabilityChecking(boolean enabled) {
+		this.options.checkNullability = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyLazyInitializationOutsideTransaction(boolean enabled) {
+		this.options.initializeLazyStateOutsideTransactions = enabled;
+		return this;
+	}
+
+	@Override
 	public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
-		options.settings.setEntityTuplizerFactory( entityTuplizerFactory );
+		options.entityTuplizerFactory = entityTuplizerFactory;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyEntityTuplizer(
 			EntityMode entityMode,
 			Class<? extends EntityTuplizer> tuplizerClass) {
-		if ( options.settings.getEntityTuplizerFactory() == null ) {
-			options.settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
-		}
-		this.options.settings.getEntityTuplizerFactory().registerDefaultTuplizerClass( entityMode, tuplizerClass );
+		this.options.entityTuplizerFactory.registerDefaultTuplizerClass( entityMode, tuplizerClass );
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory) {
-		this.options.validatorFactoryReference = validatorFactory;
+	public SessionFactoryBuilder applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy) {
+		this.options.multiTableBulkIdStrategy = strategy;
 		return this;
 	}
 
 	@Override
-	public SessionFactoryBuilder applyBeanManager(Object beanManager) {
-		this.options.beanManagerReference = beanManager;
+	public SessionFactoryBuilder applyBatchFetchStyle(BatchFetchStyle style) {
+		this.options.batchFetchStyle = style;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyDefaultBatchFetchSize(int size) {
+		this.options.defaultBatchFetchSize = size;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyMaximumFetchDepth(int depth) {
+		this.options.maximumFetchDepth = depth;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyDefaultNullPrecedence(NullPrecedence nullPrecedence) {
+		this.options.defaultNullPrecedence = nullPrecedence;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyOrderingOfInserts(boolean enabled) {
+		this.options.orderInsertsEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyOrderingOfUpdates(boolean enabled) {
+		this.options.orderUpdatesEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyMultiTenancyStrategy(MultiTenancyStrategy strategy) {
+		this.options.multiTenancyStrategy = strategy;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver) {
+		this.options.currentTenantIdentifierResolver = resolver;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyJtaTrackingByThread(boolean enabled) {
+		this.options.jtaTrackByThread = enabled;
+		return this;
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public SessionFactoryBuilder applyQuerySubstitutions(Map substitutions) {
+		this.options.querySubstitutions.putAll( substitutions );
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyStrictJpaQueryLanguageCompliance(boolean enabled) {
+		this.options.strictJpaQueryLanguageCompliance = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyNamedQueryCheckingOnStartup(boolean enabled) {
+		this.options.namedQueryStartupCheckingEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applySecondLevelCacheSupport(boolean enabled) {
+		this.options.secondLevelCacheEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyQueryCacheSupport(boolean enabled) {
+		this.options.queryCacheEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyQueryCacheFactory(QueryCacheFactory factory) {
+		this.options.queryCacheFactory = factory;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyCacheRegionPrefix(String prefix) {
+		this.options.cacheRegionPrefix = prefix;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyMinimalPutsForCaching(boolean enabled) {
+		this.options.minimalPutsEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyStructuredCacheEntries(boolean enabled) {
+		this.options.structuredCacheEntriesEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyDirectReferenceCaching(boolean enabled) {
+		this.options.directReferenceCacheEntriesEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyAutomaticEvictionOfCollectionCaches(boolean enabled) {
+		this.options.autoEvictCollectionCache = enabled;
+		return this;
+	}
+
+
+	@Override
+	public SessionFactoryBuilder applyJdbcBatchSize(int size) {
+		this.options.jdbcBatchSize = size;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyJdbcBatchingForVersionedEntities(boolean enabled) {
+		this.options.jdbcBatchVersionedData = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyScrollableResultsSupport(boolean enabled) {
+		this.options.scrollableResultSetsEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyResultSetsWrapping(boolean enabled) {
+		this.options.wrapResultSetsEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyGetGeneratedKeysSupport(boolean enabled) {
+		this.options.getGeneratedKeysEnabled = enabled;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyJdbcFetchSize(int size) {
+		this.options.jdbcFetchSize = size;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applyConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
+		this.options.connectionReleaseMode = connectionReleaseMode;
+		return this;
+	}
+
+	@Override
+	public SessionFactoryBuilder applySqlComments(boolean enabled) {
+		this.options.commentsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applySqlFunction(String registrationName, SQLFunction sqlFunction) {
 		if ( this.options.sqlFunctions == null ) {
 			this.options.sqlFunctions = new HashMap<String, SQLFunction>();
 		}
 		this.options.sqlFunctions.put( registrationName, sqlFunction );
 		return this;
 	}
 
 	@Override
+	@SuppressWarnings("unchecked")
+	public <T extends SessionFactoryBuilder> T unwrap(Class<T> type) {
+		return (T) this;
+	}
+
+	@Override
 	public SessionFactory build() {
 		metadata.validate();
 		return new SessionFactoryImpl( metadata, options );
 	}
 
-	private static class SessionFactoryOptionsImpl implements SessionFactory.SessionFactoryOptions {
+	public static class SessionFactoryOptionsImpl implements SessionFactoryOptions {
 		private final StandardServiceRegistry serviceRegistry;
 
+		// integration
+		private Object beanManagerReference;
+		private Object validatorFactoryReference;
+
+		// SessionFactory behavior
+		private String sessionFactoryName;
+		private boolean sessionFactoryNameAlsoJndiName;
+
+		// Session behavior
+		private boolean flushBeforeCompletionEnabled;
+		private boolean autoCloseSessionEnabled;
+
+		// Statistics/Interceptor/observers
+		private boolean statisticsEnabled;
 		private Interceptor interceptor;
-		private CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
-		private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 		private List<SessionFactoryObserver> sessionFactoryObserverList = new ArrayList<SessionFactoryObserver>();
+		private BaselineSessionEventsListenerBuilder baselineSessionEventsListenerBuilder;	// not exposed on builder atm
+
+		// persistence behavior
+		private CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 		private List<EntityNameResolver> entityNameResolvers = new ArrayList<EntityNameResolver>();
 		private EntityNotFoundDelegate entityNotFoundDelegate;
-		private Settings settings;
-		private Object beanManagerReference;
-		private Object validatorFactoryReference;
+		private boolean identifierRollbackEnabled;
+		private EntityMode defaultEntityMode;
+		private EntityTuplizerFactory entityTuplizerFactory = new EntityTuplizerFactory();
+		private boolean checkNullability;
+		private boolean initializeLazyStateOutsideTransactions;
+		private MultiTableBulkIdStrategy multiTableBulkIdStrategy;
+		private BatchFetchStyle batchFetchStyle;
+		private int defaultBatchFetchSize;
+		private Integer maximumFetchDepth;
+		private NullPrecedence defaultNullPrecedence;
+		private boolean orderUpdatesEnabled;
+		private boolean orderInsertsEnabled;
+
+		// multi-tenancy
+		private MultiTenancyStrategy multiTenancyStrategy;
+		private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
+
+		// JTA timeout detection
+		private boolean jtaTrackByThread;
+
+		// Queries
+		private Map querySubstitutions;
+		private boolean strictJpaQueryLanguageCompliance;
+		private boolean namedQueryStartupCheckingEnabled;
+
+		// Caching
+		private boolean secondLevelCacheEnabled;
+		private boolean queryCacheEnabled;
+		private QueryCacheFactory queryCacheFactory;
+		private String cacheRegionPrefix;
+		private boolean minimalPutsEnabled;
+		private boolean structuredCacheEntriesEnabled;
+		private boolean directReferenceCacheEntriesEnabled;
+		private boolean autoEvictCollectionCache;
+
+		// Schema tooling
+		private SchemaAutoTooling schemaAutoTooling;
+
+		// JDBC Handling
+		private boolean dataDefinitionImplicitCommit;			// not exposed on builder atm
+		private boolean dataDefinitionInTransactionSupported;	// not exposed on builder atm
+		private boolean getGeneratedKeysEnabled;
+		private int jdbcBatchSize;
+		private boolean jdbcBatchVersionedData;
+		private Integer jdbcFetchSize;
+		private boolean scrollableResultSetsEnabled;
+		private boolean commentsEnabled;
+		private ConnectionReleaseMode connectionReleaseMode;
+		private boolean wrapResultSetsEnabled;
 
 		private Map<String, SQLFunction> sqlFunctions;
 
+
 		public SessionFactoryOptionsImpl(StandardServiceRegistry serviceRegistry) {
 			this.serviceRegistry = serviceRegistry;
 
-			final Map configurationSettings = serviceRegistry.getService( ConfigurationService.class ).getSettings();
-
 			final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
+			ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );
+			final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
+			final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
+
+			final Map configurationSettings = new HashMap();
+			//noinspection unchecked
+			configurationSettings.putAll( cfgService.getSettings() );
+			//noinspection unchecked
+			configurationSettings.putAll( jdbcServices.getJdbcEnvironment().getDialect().getDefaultProperties() );
+			cfgService = new ConfigurationServiceImpl( configurationSettings );
+			( (ConfigurationServiceImpl) cfgService ).injectServices( (ServiceRegistryImplementor) serviceRegistry );
+
+			this.beanManagerReference = configurationSettings.get( "javax.persistence.bean.manager" );
+			this.validatorFactoryReference = configurationSettings.get( "javax.persistence.validation.factory" );
+
+			this.sessionFactoryName = (String) configurationSettings.get( SESSION_FACTORY_NAME );
+			this.sessionFactoryNameAlsoJndiName = cfgService.getSetting(
+					SESSION_FACTORY_NAME_IS_JNDI,
+					BOOLEAN,
+					true
+			);
+
+			this.flushBeforeCompletionEnabled = cfgService.getSetting( FLUSH_BEFORE_COMPLETION, BOOLEAN, false );
+			this.autoCloseSessionEnabled = cfgService.getSetting( AUTO_CLOSE_SESSION, BOOLEAN, false );
 
+			this.statisticsEnabled = cfgService.getSetting( GENERATE_STATISTICS, BOOLEAN, false );
 			this.interceptor = strategySelector.resolveDefaultableStrategy(
 					Interceptor.class,
-					configurationSettings.get( AvailableSettings.INTERCEPTOR ),
+					configurationSettings.get( INTERCEPTOR ),
 					EmptyInterceptor.INSTANCE
 			);
+			// todo : expose this from builder?
+			final String autoSessionEventsListenerName = (String) configurationSettings.get(
+					AUTO_SESSION_EVENTS_LISTENER
+			);
+			final Class<? extends SessionEventListener> autoSessionEventsListener = autoSessionEventsListenerName == null
+					? null
+					: strategySelector.selectStrategyImplementor( SessionEventListener.class, autoSessionEventsListenerName );
 
-			this.entityNotFoundDelegate = StandardEntityNotFoundDelegate.INSTANCE;
+			final boolean logSessionMetrics = cfgService.getSetting( LOG_SESSION_METRICS, BOOLEAN, statisticsEnabled );
+			this.baselineSessionEventsListenerBuilder = new BaselineSessionEventsListenerBuilder( logSessionMetrics, autoSessionEventsListener );
 
 			this.customEntityDirtinessStrategy = strategySelector.resolveDefaultableStrategy(
 					CustomEntityDirtinessStrategy.class,
-					configurationSettings.get( AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY ),
+					configurationSettings.get( CUSTOM_ENTITY_DIRTINESS_STRATEGY ),
 					DefaultCustomEntityDirtinessStrategy.INSTANCE
 			);
 
+			this.entityNotFoundDelegate = StandardEntityNotFoundDelegate.INSTANCE;
+			this.identifierRollbackEnabled = cfgService.getSetting( USE_IDENTIFIER_ROLLBACK, BOOLEAN, false );
+			this.defaultEntityMode = EntityMode.parse( (String) configurationSettings.get( DEFAULT_ENTITY_MODE ) );
+			this.checkNullability = cfgService.getSetting( CHECK_NULLABILITY, BOOLEAN, true );
+			this.initializeLazyStateOutsideTransactions = cfgService.getSetting( ENABLE_LAZY_LOAD_NO_TRANS, BOOLEAN, false );
+
+			this.multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configurationSettings );
 			this.currentTenantIdentifierResolver = strategySelector.resolveStrategy(
 					CurrentTenantIdentifierResolver.class,
-					configurationSettings.get( AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER )
+					configurationSettings.get( MULTI_TENANT_IDENTIFIER_RESOLVER )
 			);
 
-			this.beanManagerReference = configurationSettings.get( "javax.persistence.bean.manager" );
-			this.validatorFactoryReference = configurationSettings.get( "javax.persistence.validation.factory" );
+			this.multiTableBulkIdStrategy = strategySelector.resolveStrategy(
+					MultiTableBulkIdStrategy.class,
+					configurationSettings.get( HQL_BULK_ID_STRATEGY )
+			);
+			if ( this.multiTableBulkIdStrategy == null ) {
+				this.multiTableBulkIdStrategy = jdbcServices.getDialect().supportsTemporaryTables()
+						? TemporaryTableBulkIdStrategy.INSTANCE
+						: new PersistentTableBulkIdStrategy();
+			}
 
-			Properties properties = new Properties();
-			properties.putAll( configurationSettings );
-			this.settings = new SettingsFactory().buildSettings( properties, serviceRegistry );
+			this.batchFetchStyle = BatchFetchStyle.interpret( configurationSettings.get( BATCH_FETCH_STYLE ) );
+			this.defaultBatchFetchSize = ConfigurationHelper.getInt( DEFAULT_BATCH_FETCH_SIZE, configurationSettings, -1 );
+			this.maximumFetchDepth = ConfigurationHelper.getInteger( MAX_FETCH_DEPTH, configurationSettings );
+			final String defaultNullPrecedence = ConfigurationHelper.getString(
+					AvailableSettings.DEFAULT_NULL_ORDERING, configurationSettings, "none", "first", "last"
+			);
+			this.defaultNullPrecedence = NullPrecedence.parse( defaultNullPrecedence );
+			this.orderUpdatesEnabled = ConfigurationHelper.getBoolean( ORDER_UPDATES, configurationSettings );
+			this.orderInsertsEnabled = ConfigurationHelper.getBoolean( ORDER_INSERTS, configurationSettings );
+
+			this.jtaTrackByThread = cfgService.getSetting( JTA_TRACK_BY_THREAD, BOOLEAN, true );
+
+			this.querySubstitutions = ConfigurationHelper.toMap( QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", configurationSettings );
+			this.strictJpaQueryLanguageCompliance = cfgService.getSetting( JPAQL_STRICT_COMPLIANCE, BOOLEAN, false );
+			this.namedQueryStartupCheckingEnabled = cfgService.getSetting( QUERY_STARTUP_CHECKING, BOOLEAN, true );
+
+			this.secondLevelCacheEnabled = cfgService.getSetting( USE_SECOND_LEVEL_CACHE, BOOLEAN, true );
+			this.queryCacheEnabled = cfgService.getSetting( USE_QUERY_CACHE, BOOLEAN, false );
+			this.queryCacheFactory = strategySelector.resolveDefaultableStrategy(
+					QueryCacheFactory.class,
+					configurationSettings.get( QUERY_CACHE_FACTORY ),
+					StandardQueryCacheFactory.INSTANCE
+			);
+			this.cacheRegionPrefix = ConfigurationHelper.extractPropertyValue(
+					CACHE_REGION_PREFIX,
+					configurationSettings
+			);
+			this.minimalPutsEnabled = cfgService.getSetting(
+					USE_MINIMAL_PUTS,
+					BOOLEAN,
+					serviceRegistry.getService( RegionFactory.class ).isMinimalPutsEnabledByDefault()
+			);
+			this.structuredCacheEntriesEnabled = cfgService.getSetting( USE_STRUCTURED_CACHE, BOOLEAN, false );
+			this.directReferenceCacheEntriesEnabled = cfgService.getSetting( USE_DIRECT_REFERENCE_CACHE_ENTRIES,BOOLEAN, false );
+			this.autoEvictCollectionCache = cfgService.getSetting( AUTO_EVICT_COLLECTION_CACHE, BOOLEAN, false );
 
-			this.settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
+			try {
+				this.schemaAutoTooling = SchemaAutoTooling.interpret( (String) configurationSettings.get( AvailableSettings.HBM2DDL_AUTO ) );
+			}
+			catch (Exception e) {
+				log.warn( e.getMessage() + "  Ignoring" );
+			}
+
+
+			final ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
+			this.dataDefinitionImplicitCommit = meta.doesDataDefinitionCauseTransactionCommit();
+			this.dataDefinitionInTransactionSupported = meta.supportsDataDefinitionInTransaction();
+
+			this.jdbcBatchSize = ConfigurationHelper.getInt( STATEMENT_BATCH_SIZE, configurationSettings, 0 );
+			if ( !meta.supportsBatchUpdates() ) {
+				this.jdbcBatchSize = 0;
+			}
+
+			this.jdbcBatchVersionedData = ConfigurationHelper.getBoolean( BATCH_VERSIONED_DATA, configurationSettings, false );
+			this.scrollableResultSetsEnabled = ConfigurationHelper.getBoolean(
+					USE_SCROLLABLE_RESULTSET,
+					configurationSettings,
+					meta.supportsScrollableResults()
+			);
+			this.wrapResultSetsEnabled = ConfigurationHelper.getBoolean(
+					WRAP_RESULT_SETS,
+					configurationSettings,
+					false
+			);
+			this.getGeneratedKeysEnabled = ConfigurationHelper.getBoolean(
+					USE_GET_GENERATED_KEYS,
+					configurationSettings,
+					meta.supportsGetGeneratedKeys()
+			);
+			this.jdbcFetchSize = ConfigurationHelper.getInteger( STATEMENT_FETCH_SIZE, configurationSettings );
+
+			final String releaseModeName = ConfigurationHelper.getString( RELEASE_CONNECTIONS, configurationSettings, "auto" );
+			if ( "auto".equals( releaseModeName ) ) {
+				this.connectionReleaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
+			}
+			else {
+				connectionReleaseMode = ConnectionReleaseMode.parse( releaseModeName );
+			}
+
+			this.commentsEnabled = ConfigurationHelper.getBoolean( USE_SQL_COMMENTS, configurationSettings );
 		}
 
 		@Override
 		public StandardServiceRegistry getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		@Override
+		public Object getBeanManagerReference() {
+			return beanManagerReference;
+		}
+
+		@Override
+		public Object getValidatorFactoryReference() {
+			return validatorFactoryReference;
+		}
+
+		@Override
+		public String getSessionFactoryName() {
+			return sessionFactoryName;
+		}
+
+		@Override
+		public boolean isSessionFactoryNameAlsoJndiName() {
+			return sessionFactoryNameAlsoJndiName;
+		}
+
+		@Override
+		public boolean isFlushBeforeCompletionEnabled() {
+			return flushBeforeCompletionEnabled;
+		}
+
+		@Override
+		public boolean isAutoCloseSessionEnabled() {
+			return autoCloseSessionEnabled;
+		}
+
+		@Override
+		public boolean isStatisticsEnabled() {
+			return statisticsEnabled;
+		}
+
+		@Override
 		public Interceptor getInterceptor() {
 			return interceptor;
 		}
 
 		@Override
-		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
-			return customEntityDirtinessStrategy;
+		public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
+			return baselineSessionEventsListenerBuilder;
+		}
+
+		@Override
+		public SessionFactoryObserver[] getSessionFactoryObservers() {
+			return sessionFactoryObserverList.toArray( new SessionFactoryObserver[sessionFactoryObserverList.size()] );
+		}
+
+		@Override
+		public boolean isIdentifierRollbackEnabled() {
+			return identifierRollbackEnabled;
+		}
+
+		@Override
+		public EntityMode getDefaultEntityMode() {
+			return defaultEntityMode;
+		}
+
+		public EntityTuplizerFactory getEntityTuplizerFactory() {
+			return entityTuplizerFactory;
+		}
+
+		@Override
+		public boolean isCheckNullability() {
+			return checkNullability;
+		}
+
+		@Override
+		public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
+			return initializeLazyStateOutsideTransactions;
+		}
+
+		@Override
+		public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
+			return multiTableBulkIdStrategy;
+		}
+
+		@Override
+		public BatchFetchStyle getBatchFetchStyle() {
+			return batchFetchStyle;
+		}
+
+		@Override
+		public int getDefaultBatchFetchSize() {
+			return defaultBatchFetchSize;
+		}
+
+		@Override
+		public Integer getMaximumFetchDepth() {
+			return maximumFetchDepth;
+		}
+
+		@Override
+		public NullPrecedence getDefaultNullPrecedence() {
+			return defaultNullPrecedence;
+		}
+
+		@Override
+		public boolean isOrderUpdatesEnabled() {
+			return orderUpdatesEnabled;
+		}
+
+		@Override
+		public boolean isOrderInsertsEnabled() {
+			return orderInsertsEnabled;
+		}
+
+		@Override
+		public MultiTenancyStrategy getMultiTenancyStrategy() {
+			return multiTenancyStrategy;
 		}
 
 		@Override
 		public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 			return currentTenantIdentifierResolver;
 		}
 
 		@Override
-		public Settings getSettings() {
-			return settings;
+		public boolean isJtaTrackByThread() {
+			return jtaTrackByThread;
 		}
 
 		@Override
-		public SessionFactoryObserver[] getSessionFactoryObservers() {
-			return sessionFactoryObserverList.toArray( new SessionFactoryObserver[sessionFactoryObserverList.size()] );
+		public Map getQuerySubstitutions() {
+			return querySubstitutions;
+		}
+
+		@Override
+		public boolean isStrictJpaQueryLanguageCompliance() {
+			return strictJpaQueryLanguageCompliance;
+		}
+
+		@Override
+		public boolean isNamedQueryStartupCheckingEnabled() {
+			return namedQueryStartupCheckingEnabled;
+		}
+
+		@Override
+		public boolean isSecondLevelCacheEnabled() {
+			return secondLevelCacheEnabled;
+		}
+
+		@Override
+		public boolean isQueryCacheEnabled() {
+			return queryCacheEnabled;
+		}
+
+		@Override
+		public QueryCacheFactory getQueryCacheFactory() {
+			return queryCacheFactory;
+		}
+
+		@Override
+		public String getCacheRegionPrefix() {
+			return cacheRegionPrefix;
+		}
+
+		@Override
+		public boolean isMinimalPutsEnabled() {
+			return minimalPutsEnabled;
+		}
+
+		@Override
+		public boolean isStructuredCacheEntriesEnabled() {
+			return structuredCacheEntriesEnabled;
+		}
+
+		@Override
+		public boolean isDirectReferenceCacheEntriesEnabled() {
+			return directReferenceCacheEntriesEnabled;
+		}
+
+		public boolean isAutoEvictCollectionCache() {
+			return autoEvictCollectionCache;
 		}
 
 		@Override
+		public SchemaAutoTooling getSchemaAutoTooling() {
+			return schemaAutoTooling;
+		}
+
+		@Override
+		public boolean isDataDefinitionImplicitCommit() {
+			return dataDefinitionImplicitCommit;
+		}
+
+		@Override
+		public boolean isDataDefinitionInTransactionSupported() {
+			return dataDefinitionInTransactionSupported;
+		}
+
+		@Override
+		public int getJdbcBatchSize() {
+			return jdbcBatchSize;
+		}
+
+		@Override
+		public boolean isJdbcBatchVersionedData() {
+			return jdbcBatchVersionedData;
+		}
+
+		@Override
+		public boolean isScrollableResultSetsEnabled() {
+			return scrollableResultSetsEnabled;
+		}
+
+		@Override
+		public boolean isWrapResultSetsEnabled() {
+			return wrapResultSetsEnabled;
+		}
+
+		@Override
+		public boolean isGetGeneratedKeysEnabled() {
+			return getGeneratedKeysEnabled;
+		}
+
+		@Override
+		public Integer getJdbcFetchSize() {
+			return jdbcFetchSize;
+		}
+
+		@Override
+		public ConnectionReleaseMode getConnectionReleaseMode() {
+			return connectionReleaseMode;
+		}
+
+		@Override
+		public boolean isCommentsEnabled() {
+			return commentsEnabled;
+		}
+
+		@Override
+		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
+			return customEntityDirtinessStrategy;
+		}
+
+
+		@Override
 		public EntityNameResolver[] getEntityNameResolvers() {
 			return entityNameResolvers.toArray( new EntityNameResolver[entityNameResolvers.size()] );
 		}
 
 		@Override
 		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 			return entityNotFoundDelegate;
 		}
 
 		@Override
 		public Map<String, SQLFunction> getCustomSqlFunctionMap() {
 			return sqlFunctions;
 		}
 
 		@Override
-		public Object getBeanManagerReference() {
-			return beanManagerReference;
-		}
-
-		@Override
-		public Object getValidatorFactoryReference() {
-			return validatorFactoryReference;
+		public void setCheckNullability(boolean enabled) {
+			this.checkNullability = enabled;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/ClassLoaderAccess.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/ClassLoaderAccess.java
index 184801d565..db10c1f6f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/ClassLoaderAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/ClassLoaderAccess.java
@@ -1,53 +1,55 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import java.net.URL;
 
 /**
  * During the process of building this metamodel, accessing the ClassLoader
  * is very discouraged.  However, sometimes it is needed.  This contract helps
  * mitigate accessing the ClassLoader in these cases.
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface ClassLoaderAccess {
 	/**
 	 * Obtain a Class reference by name
 	 *
 	 * @param name The name of the Class to get a reference to.
 	 *
 	 * @return The Class.
 	 */
 	public <T> Class<T> classForName(String name);
 
 	/**
 	 * Locate a resource by name
 	 *
 	 * @param resourceName The name of the resource to resolve.
 	 *
 	 * @return The located resource; may return {@code null} to indicate the resource was not found
 	 */
 	public URL locateResource(String resourceName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java
index cd857cd600..94e2161bed 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java
@@ -1,338 +1,340 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import java.io.Serializable;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.JPAIndexHolder;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.TypeResolver;
 
 /**
  * An in-flight representation of Metadata while Metadata is being built.
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface InFlightMetadataCollector extends Mapping, MetadataImplementor {
 
 	/**
 	 * Add the PersistentClass for an entity mapping.
 	 *
 	 * @param persistentClass The entity metadata
 	 *
 	 * @throws DuplicateMappingException Indicates there was already an entry
 	 * corresponding to the given entity name.
 	 */
 	void addEntityBinding(PersistentClass persistentClass) throws DuplicateMappingException;
 
 	/**
 	 * Needed for SecondPass handling
 	 */
 	Map<String, PersistentClass> getEntityBindingMap();
 
 	/**
 	 * Adds an import (HQL entity rename).
 	 *
 	 * @param entityName The entity name being renamed.
 	 * @param rename The rename
 	 *
 	 * @throws DuplicateMappingException If rename already is mapped to another
 	 * entity name in this repository.
 	 */
 	void addImport(String entityName, String rename) throws DuplicateMappingException;
 
 	/**
 	 * Add collection mapping metadata to this repository.
 	 *
 	 * @param collection The collection metadata
 	 *
 	 * @throws DuplicateMappingException Indicates there was already an entry
 	 * corresponding to the given collection role
 	 */
 	void addCollectionBinding(Collection collection) throws DuplicateMappingException;
 
 	/**
 	 * Adds table metadata to this repository returning the created
 	 * metadata instance.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @param subselect A select statement which defines a logical table, much
 	 * like a DB view.
 	 * @param isAbstract Is the table abstract (i.e. not really existing in the DB)?
 	 *
 	 * @return The created table metadata, or the existing reference.
 	 */
 	Table addTable(String schema, String catalog, String name, String subselect, boolean isAbstract);
 
 	/**
 	 * Adds a 'denormalized table' to this repository.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @param isAbstract Is the table abstract (i.e. not really existing in the DB)?
 	 * @param subselect A select statement which defines a logical table, much
 	 * like a DB view.
 	 * @param includedTable ???
 	 *
 	 * @return The created table metadata.
 	 *
 	 * @throws DuplicateMappingException If such a table mapping already exists.
 	 */
 	Table addDenormalizedTable(
 			String schema,
 			String catalog,
 			String name,
 			boolean isAbstract,
 			String subselect,
 			Table includedTable) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named query to this repository.
 	 *
 	 * @param query The metadata
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	void addNamedQuery(NamedQueryDefinition query) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named SQL query to this repository.
 	 *
 	 * @param query The metadata
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	void addNamedNativeQuery(NamedSQLQueryDefinition query) throws DuplicateMappingException;
 
 	/**
 	 * Adds the metadata for a named SQL result set mapping to this repository.
 	 *
 	 * @param sqlResultSetMapping The metadata
 	 *
 	 * @throws DuplicateMappingException If metadata for another SQL result mapping was
 	 * already found under the given name.
 	 */
 	void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named stored procedure call to this repository.
 	 *
 	 * @param definition The procedure call information
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named entity graph to this repository
 	 *
 	 * @param namedEntityGraphDefinition The procedure call information
 	 *
 	 * @throws DuplicateMappingException If an entity graph already exists with that name.
 	 */
 	void addNamedEntityGraph(NamedEntityGraphDefinition namedEntityGraphDefinition);
 
 	/**
 	 * Adds a type definition to this metadata repository.
 	 *
 	 * @param typeDefinition The named type definition to add.
 	 *
 	 * @throws DuplicateMappingException If a TypeDefinition already exists with that name.
 	 */
 	void addTypeDefinition(TypeDefinition typeDefinition);
 
 	/**
 	 * Adds a filter definition to this repository.
 	 *
 	 * @param definition The filter definition to add.
 	 *
 	 * @throws DuplicateMappingException If a FilterDefinition already exists with that name.
 	 */
 	void addFilterDefinition(FilterDefinition definition);
 
 	/**
 	 * Add metadata pertaining to an auxiliary database object to this repository.
 	 *
 	 * @param auxiliaryDatabaseObject The metadata.
 	 */
 	void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
 	void addFetchProfile(FetchProfile profile);
 
 	TypeResolver getTypeResolver();
 
 	Database getDatabase();
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// make sure these are account for better in metamodel
 
 	void addIdentifierGenerator(IdentifierGeneratorDefinition generatorDefinition);
 
 
 	void addAttributeConverter(AttributeConverterDefinition converter);
 	void addAttributeConverter(Class<? extends AttributeConverter> converterClass);
 	java.util.Collection<AttributeConverterDefinition> getAttributeConverters();
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// second passes
 
 	void addSecondPass(SecondPass secondPass);
 
 	void addSecondPass(SecondPass sp, boolean onTopOfTheQueue);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff needed for annotation binding :(
 
 	void addTableNameBinding(Identifier logicalName, Table table);
 	void addTableNameBinding(
 			String schema,
 			String catalog,
 			String logicalName,
 			String realTableName,
 			Table denormalizedSuperTable);
 	String getLogicalTableName(Table ownerTable);
 	String getPhysicalTableName(Identifier logicalName);
 	String getPhysicalTableName(String logicalName);
 
 	void addColumnNameBinding(Table table, Identifier logicalColumnName, Column column);
 	void addColumnNameBinding(Table table, String logicalColumnName, Column column);
 	String getPhysicalColumnName(Table table, Identifier logicalName) throws MappingException;
 	String getPhysicalColumnName(Table table, String logicalName) throws MappingException;
 	String getLogicalColumnName(Table table, Identifier physicalName);
 	String getLogicalColumnName(Table table, String physicalName);
 
 	void addDefaultIdentifierGenerator(IdentifierGeneratorDefinition generatorDefinition);
 
 	void addDefaultQuery(NamedQueryDefinition queryDefinition);
 
 	void addDefaultNamedNativeQuery(NamedSQLQueryDefinition query);
 
 	void addDefaultResultSetMapping(ResultSetMappingDefinition definition);
 
 	void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition procedureCallDefinition);
 
 	void addAnyMetaDef(AnyMetaDef defAnn);
 	AnyMetaDef getAnyMetaDef(String anyMetaDefName);
 
 	AnnotatedClassType addClassType(XClass clazz);
 	AnnotatedClassType getClassType(XClass clazz);
 
 	void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass);
 	MappedSuperclass getMappedSuperclass(Class type);
 
 	PropertyData getPropertyAnnotatedWithMapsId(XClass persistentXClass, String propertyName);
 	void addPropertyAnnotatedWithMapsId(XClass entity, PropertyData propertyAnnotatedElement);
 	void addPropertyAnnotatedWithMapsIdSpecj(XClass entity, PropertyData specJPropertyData, String s);
 
 	void addToOneAndIdProperty(XClass entity, PropertyData propertyAnnotatedElement);
 	PropertyData getPropertyAnnotatedWithIdAndToOne(XClass persistentXClass, String propertyName);
 
 	boolean isInSecondPass();
 
 	NaturalIdUniqueKeyBinder locateNaturalIdUniqueKeyBinder(String entityName);
 	void registerNaturalIdUniqueKeyBinder(String entityName, NaturalIdUniqueKeyBinder ukBinder);
 
 	public static interface DelayedPropertyReferenceHandler extends Serializable {
 		public void process(InFlightMetadataCollector metadataCollector);
 	}
 	public void addDelayedPropertyReferenceHandler(DelayedPropertyReferenceHandler handler);
 	void addPropertyReference(String entityName, String propertyName);
 	void addUniquePropertyReference(String entityName, String propertyName);
 
 	void addPropertyReferencedAssociation(String s, String propertyName, String syntheticPropertyName);
 	String getPropertyReferencedAssociation(String entityName, String mappedBy);
 
 	void addMappedBy(String name, String mappedBy, String propertyName);
 	String getFromMappedBy(String ownerEntityName, String propertyName);
 
 	void addUniqueConstraints(Table table, List uniqueConstraints);
 	void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraints);
 	void addJpaIndexHolders(Table table, List<JPAIndexHolder> jpaIndexHolders);
 
 
 	public static interface EntityTableXref {
 		void addSecondaryTable(LocalMetadataBuildingContext buildingContext, Identifier logicalName, Join secondaryTableJoin);
 		void addSecondaryTable(Identifier logicalName, Join secondaryTableJoin);
 		Table resolveTable(Identifier tableName);
 		Table getPrimaryTable();
 		Join locateJoin(Identifier tableName);
 	}
 
 	public static class DuplicateSecondaryTableException extends HibernateException {
 		private final Identifier tableName;
 
 		public DuplicateSecondaryTableException(Identifier tableName) {
 			super(
 					String.format(
 							Locale.ENGLISH,
 							"Table with that name [%s] already associated with entity",
 							tableName.render()
 					)
 			);
 			this.tableName = tableName;
 		}
 	}
 
 	public EntityTableXref getEntityTableXref(String entityName);
 	public EntityTableXref addEntityTableXref(String entityName, Identifier primaryTableLogicalName, Table primaryTable, EntityTableXref superEntityTableXref);
 	void addJoins(PersistentClass persistentClass, Map<String, Join> secondaryTables);
 	Map<String,Join> getJoins(String entityName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MappingDefaults.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MappingDefaults.java
index b643e1a5e5..519fbd0864 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MappingDefaults.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MappingDefaults.java
@@ -1,143 +1,145 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import org.hibernate.cache.spi.access.AccessType;
 
 /**
  * Defines a (contextual) set of values to use as defaults in the absence of related mapping information.  The
  * context here is conceptually a stack.  The "global" level is configuration settings.
  *
  * @author Steve Ebersole
  * @author Gail Badner
+ *
+ * @since 5.0
  */
 public interface MappingDefaults {
 	public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	public static final String DEFAULT_TENANT_IDENTIFIER_COLUMN_NAME = "tenant_id";
 	public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 	public static final String DEFAULT_CASCADE_NAME = "none";
 	public static final String DEFAULT_PROPERTY_ACCESS_NAME = "property";
 	/**
 	 * Identifies the database schema name to use if none specified in the mapping.
 	 *
 	 * @return The implicit schema name; may be {@code null}
 	 */
 	public String getImplicitSchemaName();
 
 	/**
 	 * Identifies the database catalog name to use if none specified in the mapping.
 	 *
 	 * @return The implicit catalog name; may be {@code null}
 	 */
 	public String getImplicitCatalogName();
 
 	/**
 	 * Should all database identifiers encountered in this context be implicitly quoted?
 	 *
 	 * {@code true} indicates that all identifier encountered within this context should be
 	 * quoted.  {@code false} indicates indicates that identifiers within this context are
 	 * onl;y quoted if explicitly quoted.
 	 *
 	 * @return {@code true}/{@code false}
 	 */
 	public boolean shouldImplicitlyQuoteIdentifiers();
 
 	/**
 	 * Identifies the column name to use for the identifier column if none specified in
 	 * the mapping.
 	 *
 	 * @return The implicit identifier column name
 	 */
 	public String getImplicitIdColumnName();
 
 	/**
 	 * Identifies the column name to use for the tenant identifier column if none is
 	 * specified in the mapping.
 	 *
 	 * @return The implicit tenant identifier column name
 	 */
 	public String getImplicitTenantIdColumnName();
 
 	/**
 	 * Identifies the column name to use for the discriminator column if none specified
 	 * in the mapping.
 	 *
 	 * @return The implicit discriminator column name
 	 */
 	public String getImplicitDiscriminatorColumnName();
 
 	/**
 	 * Identifies the package name to use if none specified in the mapping.  Really only
 	 * pertinent for {@code hbm.xml} mappings.
 	 *
 	 * @return The implicit package name.
 	 */
 	public String getImplicitPackageName();
 
 	/**
 	 * Is auto-importing of entity (short) names enabled?
 	 *
 	 * @return {@code true} if auto-importing is enabled; {@code false} otherwise.
 	 */
 	public boolean isAutoImportEnabled();
 
 	/**
 	 * Identifies the cascade style to apply to associations if none specified in the mapping.
 	 *
 	 * @return The implicit cascade style
 	 */
 	public String getImplicitCascadeStyleName();
 
 	/**
 	 * Identifies the default {@link org.hibernate.property.PropertyAccessor} name to use if none specified in the
 	 * mapping.
 	 *
 	 * @return The implicit property accessor name
 	 *
 	 * @see org.hibernate.property.PropertyAccessorFactory
 	 */
 	public String getImplicitPropertyAccessorName();
 
 	/**
 	 * Identifies whether singular associations (many-to-one, one-to-one) are lazy
 	 * by default if not specified in the mapping.
 	 *
 	 * @return The implicit association laziness
 	 */
 	public boolean areEntitiesImplicitlyLazy();
 
 	/**
 	 * Identifies whether plural attributes are lazy by default if not specified in the mapping.
 	 *
 	 * @return The implicit association laziness
 	 */
 	public boolean areCollectionsImplicitlyLazy();
 
 	/**
 	 * The cache access type to use if none is specified
 	 *
 	 * @return The implicit cache access type.
 	 */
 	public AccessType getImplicitCacheAccessType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuilderInitializer.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuilderInitializer.java
index 16a58572cd..d556be9619 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuilderInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuilderInitializer.java
@@ -1,40 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 
 /**
  * Contract for contributing to the initialization of MetadataBuilder.  Called
  * immediately after any configuration settings have been applied from
  * {@link org.hibernate.engine.config.spi.ConfigurationService}.  Any values specified
  * here override those.  Any values set here can still be overridden explicitly by the user
  * via the exposed config methods of {@link MetadataBuilder}
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface MetadataBuilderInitializer {
 	public void contribute(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingContext.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingContext.java
index dd5a3a4def..105406f57a 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingContext.java
@@ -1,71 +1,73 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import org.hibernate.boot.model.naming.ObjectNameNormalizer;
 
 /**
  * Describes the context in which the process of building Metadata out of MetadataSources occurs.
  *
  * BindingContext are generally hierarchical getting more specific as we "go
  * down".  E.g.  global -> PU -> document -> mapping
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface MetadataBuildingContext {
 	/**
 	 * Access to the options specified by the {@link org.hibernate.boot.MetadataBuilder}
 	 *
 	 * @return The options
 	 */
 	public MetadataBuildingOptions getBuildingOptions();
 
 	/**
 	 * Access to mapping defaults in effect for this context
 	 *
 	 * @return The mapping defaults.
 	 */
 	public MappingDefaults getMappingDefaults();
 
 	/**
 	 * Access to the collector of metadata as we build it.
 	 *
 	 * @return The metadata collector.
 	 */
 	public InFlightMetadataCollector getMetadataCollector();
 
 	/**
 	 * Provides access to ClassLoader services when needed during binding
 	 *
 	 * @return The ClassLoaderAccess
 	 */
 	public ClassLoaderAccess getClassLoaderAccess();
 
 	/**
 	 * Not sure how I feel about this exposed here
 	 *
 	 * @return
 	 */
 	public ObjectNameNormalizer getObjectNameNormalizer();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java
index f5f02f97a3..56883c9423 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataBuildingOptions.java
@@ -1,245 +1,247 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import java.util.List;
 import java.util.Map;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.boot.CacheRegionDefinition;
 import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
 import org.hibernate.boot.archive.scan.spi.ScanOptions;
 import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
 import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.MetadataSourceType;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.type.BasicType;
 
 import org.jboss.jandex.IndexView;
 
 /**
  * Describes the options used while building the Metadata object (during
  * {@link org.hibernate.boot.MetadataBuilder#build()} processing).
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface MetadataBuildingOptions {
 	/**
 	 * Access to the service registry.
 	 *
 	 * @return The service registry
 	 */
 	StandardServiceRegistry getServiceRegistry();
 
 	/**
 	 * Access to the mapping defaults.
 	 *
 	 * @return The mapping defaults
 	 */
 	MappingDefaults getMappingDefaults();
 
 	/**
 	 * Access the list of BasicType registrations.  These are the BasicTypes explicitly
 	 * registered via calls to:<ul>
 	 *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.type.BasicType)}</li>
 	 *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.usertype.UserType, java.lang.String[])}</li>
 	 *     <li>{@link org.hibernate.boot.MetadataBuilder#applyBasicType(org.hibernate.usertype.CompositeUserType, java.lang.String[])}</li>
 	 * </ul>
 	 *
 	 * @return The BasicType registrations
 	 */
 	List<BasicType> getBasicTypeRegistrations();
 
 	/**
 	 * Access to the Jandex index passed by call to
 	 * {@link org.hibernate.boot.MetadataBuilder#applyIndexView(org.jboss.jandex.IndexView)}, if any.
 	 *
 	 * @return The Jandex index
 	 */
 	IndexView getJandexView();
 
 	/**
 	 * Access to the options to be used for scanning
 	 *
 	 * @return The scan options
 	 */
 	ScanOptions getScanOptions();
 
 	/**
 	 * Access to the environment for scanning.  Consider this temporary; see discussion on
 	 * {@link ScanEnvironment}
 	 *
 	 * @return The scan environment
 	 */
 	ScanEnvironment getScanEnvironment();
 
 	/**
 	 * Access to the Scanner to be used for scanning.  Can be:<ul>
 	 *     <li>A Scanner instance</li>
 	 *     <li>A Class reference to the Scanner implementor</li>
 	 *     <li>A String naming the Scanner implementor</li>
 	 * </ul>
 	 *
 	 * @return The scanner
 	 */
 	Object getScanner();
 
 	/**
 	 * Access to the ArchiveDescriptorFactory to be used for scanning
 	 *
 	 * @return The ArchiveDescriptorFactory
 	 */
 	ArchiveDescriptorFactory getArchiveDescriptorFactory();
 
 	/**
 	 * Access the temporary ClassLoader passed to us as defined by
 	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}, if any.
 	 *
 	 * @return The tempo ClassLoader
 	 */
 	ClassLoader getTempClassLoader();
 
 	ImplicitNamingStrategy getImplicitNamingStrategy();
 	PhysicalNamingStrategy getPhysicalNamingStrategy();
 
 	ReflectionManager getReflectionManager();
 
 	/**
 	 * Access to the SharedCacheMode for determining whether we should perform second level
 	 * caching or not.
 	 *
 	 * @return The SharedCacheMode
 	 */
 	SharedCacheMode getSharedCacheMode();
 
 	/**
 	 * Access to any implicit cache AccessType.
 	 *
 	 * @return The implicit cache AccessType
 	 */
 	AccessType getImplicitCacheAccessType();
 
 	/**
 	 * Access to the MultiTenancyStrategy for this environment.
 	 *
 	 * @return The MultiTenancyStrategy
 	 */
 	MultiTenancyStrategy getMultiTenancyStrategy();
 
 	IdGeneratorStrategyInterpreter getIdGenerationTypeInterpreter();
 
 	/**
 	 * Access to all explicit cache region mappings.
 	 *
 	 * @return Explicit cache region mappings.
 	 */
 	List<CacheRegionDefinition> getCacheRegionDefinitions();
 
 	/**
 	 * Whether explicit discriminator declarations should be ignored for joined
 	 * subclass style inheritance.
 	 *
 	 * @return {@code true} indicates they should be ignored; {@code false}
 	 * indicates they should not be ignored.
 	 *
 	 * @see org.hibernate.boot.MetadataBuilder#enableExplicitDiscriminatorsForJoinedSubclassSupport
 	 * @see org.hibernate.cfg.AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	boolean ignoreExplicitDiscriminatorsForJoinedInheritance();
 
 	/**
 	 * Whether we should do discrimination implicitly joined subclass style inheritance when no
 	 * discriminator info is provided.
 	 *
 	 * @return {@code true} indicates we should do discrimination; {@code false} we should not.
 	 *
 	 * @see org.hibernate.boot.MetadataBuilder#enableImplicitDiscriminatorsForJoinedSubclassSupport
 	 * @see org.hibernate.cfg.AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	boolean createImplicitDiscriminatorsForJoinedInheritance();
 
 	/**
 	 * Whether we should implicitly force discriminators into SQL selects.  By default,
 	 * Hibernate will not.  This can be specified per discriminator in the mapping as well.
 	 *
 	 * @return {@code true} indicates we should force the discriminator in selects for any mappings
 	 * which do not say explicitly.
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT
 	 */
 	boolean shouldImplicitlyForceDiscriminatorInSelect();
 
 	/**
 	 * Should we use nationalized variants of character data (e.g. NVARCHAR rather than VARCHAR)
 	 * by default?
 	 *
 	 * @see org.hibernate.boot.MetadataBuilder#enableGlobalNationalizedCharacterDataSupport
 	 * @see org.hibernate.cfg.AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA
 	 *
 	 * @return {@code true} if nationalized character data should be used by default; {@code false} otherwise.
 	 */
 	public boolean useNationalizedCharacterData();
 
 	boolean isSpecjProprietarySyntaxEnabled();
 
 	/**
 	 * Retrieve the ordering in which sources should be processed.
 	 *
 	 * @return The order in which sources should be processed.
 	 */
 	List<MetadataSourceType> getSourceProcessOrdering();
 
 	/**
 	 * Access to any SQL functions explicitly registered with the MetadataBuilder.  This
 	 * does not include Dialect defined functions, etc.
 	 *
 	 * @return The SQLFunctions registered through MetadataBuilder
 	 */
 	Map<String,SQLFunction> getSqlFunctions();
 
 	/**
 	 * Access to any AuxiliaryDatabaseObject explicitly registered with the MetadataBuilder.  This
 	 * does not include AuxiliaryDatabaseObject defined in mappings.
 	 *
 	 * @return The AuxiliaryDatabaseObject registered through MetadataBuilder
 	 */
 	List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList();
 
 	List<AttributeConverterDefinition> getAttributeConverters();
 
 //	/**
 //	 * Obtain the selected strategy for resolving members identifying persistent attributes
 //	 *
 //	 * @return The select resolver strategy
 //	 */
 //	PersistentAttributeMemberResolver getPersistentAttributeMemberResolver();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataContributor.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataContributor.java
index ecc395ef3b..5b56fb31d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataContributor.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataContributor.java
@@ -1,44 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import org.jboss.jandex.IndexView;
 
 /**
  * Contract for contributing to Metadata (InFlightMetadataCollector).
  *
  * This hook occurs just after all processing of all {@link org.hibernate.boot.MetadataSources},
- * and just before {@link AdditionalJaxbRootProducer}.
+ * and just before {@link org.hibernate.boot.spi.AdditionalJaxbMappingProducer}.
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface MetadataContributor {
 	/**
 	 * Perform the contributions.
 	 *
 	 * @param metadataCollector The metadata collector, representing the in-flight metadata being built
 	 * @param jandexIndex The Jandex index
 	 */
 	public void contribute(InFlightMetadataCollector metadataCollector, IndexView jandexIndex);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataImplementor.java
index 7df876f935..ab72f15226 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataImplementor.java
@@ -1,58 +1,60 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.boot.Metadata;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.type.TypeResolver;
 
 /**
  * The SPI-level Metadata contract.
  *
  * @todo Should Mapping be implemented here, or on InFlightMetadataCollector instead?
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface MetadataImplementor extends Metadata, Mapping {
 	/**
 	 * Access to the options used to build this Metadata
 	 *
 	 * @return
 	 */
 	MetadataBuildingOptions getMetadataBuildingOptions();
 
 	TypeResolver getTypeResolver();
 
 	NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory);
 
 	void validate() throws MappingException;
 
 	Set<MappedSuperclass> getMappedSuperclassMappingsCopy();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataSourcesContributor.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataSourcesContributor.java
index 5002646295..fac88859d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataSourcesContributor.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/MetadataSourcesContributor.java
@@ -1,40 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.boot.spi;
 
 import org.hibernate.boot.MetadataSources;
 
 /**
  * A bootstrap process hook for contributing sources to MetadataSources.
  *
  * @author Steve Ebersole
+ *
+ * @since 5.0
  */
 public interface MetadataSourcesContributor {
 	/**
 	 * Perform the process of contributing to MetadataSources.
 	 *
 	 * @param metadataSources The MetadataSources, to which to contribute.
 	 */
 	public void contribute(MetadataSources metadataSources);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/SessionFactoryOptions.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/SessionFactoryOptions.java
new file mode 100644
index 0000000000..defb08c5d5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/SessionFactoryOptions.java
@@ -0,0 +1,187 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.boot.spi;
+
+import java.util.Map;
+
+import org.hibernate.ConnectionReleaseMode;
+import org.hibernate.CustomEntityDirtinessStrategy;
+import org.hibernate.EntityMode;
+import org.hibernate.EntityNameResolver;
+import org.hibernate.Interceptor;
+import org.hibernate.MultiTenancyStrategy;
+import org.hibernate.NullPrecedence;
+import org.hibernate.SessionFactoryObserver;
+import org.hibernate.boot.SchemaAutoTooling;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.cache.spi.QueryCacheFactory;
+import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
+import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
+import org.hibernate.dialect.function.SQLFunction;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.loader.BatchFetchStyle;
+import org.hibernate.proxy.EntityNotFoundDelegate;
+import org.hibernate.tuple.entity.EntityTuplizerFactory;
+
+/**
+ * Aggregator of special options used to build the SessionFactory.
+ *
+ * @since 5.0
+ */
+public interface SessionFactoryOptions {
+	/**
+	 * The service registry to use in building the factory.
+	 *
+	 * @return The service registry to use.
+	 */
+	public StandardServiceRegistry getServiceRegistry();
+
+	public Object getBeanManagerReference();
+
+	public Object getValidatorFactoryReference();
+
+	/**
+	 * The name to be used for the SessionFactory.  This is use both in:<ul>
+	 *     <li>in-VM serialization</li>
+	 *     <li>JNDI binding, depending on {@link #isSessionFactoryNameAlsoJndiName}</li>
+	 * </ul>
+	 *
+	 * @return The SessionFactory name
+	 */
+	public String getSessionFactoryName();
+
+	/**
+	 * Is the {@link #getSessionFactoryName SesssionFactory name} also a JNDI name, indicating we
+	 * should bind it into JNDI?
+	 *
+	 * @return {@code true} if the SessionFactory name is also a JNDI name; {@code false} otherwise.
+	 */
+	public boolean isSessionFactoryNameAlsoJndiName();
+
+	public boolean isFlushBeforeCompletionEnabled();
+
+	public boolean isAutoCloseSessionEnabled();
+
+	public boolean isStatisticsEnabled();
+
+	/**
+	 * Get the interceptor to use by default for all sessions opened from this factory.
+	 *
+	 * @return The interceptor to use factory wide.  May be {@code null}
+	 */
+	public Interceptor getInterceptor();
+
+	public SessionFactoryObserver[] getSessionFactoryObservers();
+
+	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder();
+
+	public boolean isIdentifierRollbackEnabled();
+
+	public EntityMode getDefaultEntityMode();
+
+	public EntityTuplizerFactory getEntityTuplizerFactory();
+
+	public boolean isCheckNullability();
+
+	public boolean isInitializeLazyStateOutsideTransactionsEnabled();
+
+	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy();
+
+	public BatchFetchStyle getBatchFetchStyle();
+
+	public int getDefaultBatchFetchSize();
+
+	public Integer getMaximumFetchDepth();
+
+	public NullPrecedence getDefaultNullPrecedence();
+
+	public boolean isOrderUpdatesEnabled();
+
+	public boolean isOrderInsertsEnabled();
+
+	public MultiTenancyStrategy getMultiTenancyStrategy();
+
+	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
+
+	public boolean isJtaTrackByThread();
+
+	public Map getQuerySubstitutions();
+
+	public boolean isStrictJpaQueryLanguageCompliance();
+
+	public boolean isNamedQueryStartupCheckingEnabled();
+
+	public boolean isSecondLevelCacheEnabled();
+
+	public boolean isQueryCacheEnabled();
+
+	public QueryCacheFactory getQueryCacheFactory();
+
+	public String getCacheRegionPrefix();
+
+	public boolean isMinimalPutsEnabled();
+
+	public boolean isStructuredCacheEntriesEnabled();
+
+	public boolean isDirectReferenceCacheEntriesEnabled();
+
+	public boolean isAutoEvictCollectionCache();
+
+	public SchemaAutoTooling getSchemaAutoTooling();
+
+	public boolean isDataDefinitionImplicitCommit();
+
+	public boolean isDataDefinitionInTransactionSupported();
+
+	public int getJdbcBatchSize();
+
+	public boolean isJdbcBatchVersionedData();
+
+	public boolean isScrollableResultSetsEnabled();
+
+	public boolean isWrapResultSetsEnabled();
+
+	public boolean isGetGeneratedKeysEnabled();
+
+	public Integer getJdbcFetchSize();
+
+	public ConnectionReleaseMode getConnectionReleaseMode();
+
+	public boolean isCommentsEnabled();
+
+
+	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
+	public EntityNameResolver[] getEntityNameResolvers();
+
+	/**
+	 * Get the delegate for handling entity-not-found exception conditions.
+	 *
+	 * @return The specific EntityNotFoundDelegate to use,  May be {@code null}
+	 */
+	public EntityNotFoundDelegate getEntityNotFoundDelegate();
+
+	public Map<String, SQLFunction> getCustomSqlFunctionMap();
+
+	void setCheckNullability(boolean enabled);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCacheFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCacheFactory.java
index 7b584d0be6..c1dfc0ea91 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCacheFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCacheFactory.java
@@ -1,47 +1,52 @@
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
 
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 
 /**
  * Standard Hibernate implementation of the QueryCacheFactory interface.  Returns instances of
  * {@link StandardQueryCache}.
  */
 public class StandardQueryCacheFactory implements QueryCacheFactory {
+	/**
+	 * Singleton access
+	 */
+	public static final StandardQueryCacheFactory INSTANCE = new StandardQueryCacheFactory();
+
 	@Override
 	public QueryCache getQueryCache(
 			final String regionName,
 			final UpdateTimestampsCache updateTimestampsCache,
 			final Settings settings,
 			final Properties props) throws HibernateException {
 		return new StandardQueryCache(settings, props, updateTimestampsCache, regionName);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index c68d4c876c..81715168df 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,539 +1,340 @@
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
 import org.hibernate.NullPrecedence;
+import org.hibernate.boot.Metadata;
+import org.hibernate.boot.SchemaAutoTooling;
+import org.hibernate.boot.model.naming.Identifier;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
+import org.jboss.logging.Logger;
+
 /**
  * Settings that affect the behaviour of Hibernate at runtime.
  *
  * @author Gavin King
+ * @author Steve Ebersole
+ *
+ * @deprecated Use {@link org.hibernate.boot.spi.SessionFactoryOptions} instead.
  */
+@Deprecated
 public final class Settings {
+	private static final Logger LOG = Logger.getLogger( Settings.class );
 
-	private Integer maximumFetchDepth;
-	private Map querySubstitutions;
-	private int jdbcBatchSize;
-	private int defaultBatchFetchSize;
-	private boolean scrollableResultSetsEnabled;
-	private boolean getGeneratedKeysEnabled;
-	private String defaultSchemaName;
-	private String defaultCatalogName;
-	private Integer jdbcFetchSize;
-	private String sessionFactoryName;
-	private boolean sessionFactoryNameAlsoJndiName;
-	private boolean autoCreateSchema;
-	private boolean autoDropSchema;
-	private boolean autoUpdateSchema;
-	private boolean autoValidateSchema;
-	private boolean queryCacheEnabled;
-	private boolean structuredCacheEntriesEnabled;
-	private boolean secondLevelCacheEnabled;
-	private boolean autoEvictCollectionCache;
-	private String cacheRegionPrefix;
-	private boolean minimalPutsEnabled;
-	private boolean commentsEnabled;
-	private boolean statisticsEnabled;
-	private boolean jdbcBatchVersionedData;
-	private boolean identifierRollbackEnabled;
-	private boolean flushBeforeCompletionEnabled;
-	private boolean autoCloseSessionEnabled;
-	private ConnectionReleaseMode connectionReleaseMode;
-	private RegionFactory regionFactory;
-	private QueryCacheFactory queryCacheFactory;
-	private QueryTranslatorFactory queryTranslatorFactory;
-	private boolean wrapResultSetsEnabled;
-	private boolean orderUpdatesEnabled;
-	private boolean orderInsertsEnabled;
-	private EntityMode defaultEntityMode;
-	private boolean dataDefinitionImplicitCommit;
-	private boolean dataDefinitionInTransactionSupported;
-	private boolean strictJPAQLCompliance;
-	private boolean namedQueryStartupCheckingEnabled;
-	private EntityTuplizerFactory entityTuplizerFactory;
-	private boolean checkNullability;
-	private NullPrecedence defaultNullPrecedence;
-	private boolean initializeLazyStateOutsideTransactions;
-//	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
-//	private BytecodeProvider bytecodeProvider;
-	private String importFiles;
-	private MultiTenancyStrategy multiTenancyStrategy;
-
-	private JtaPlatform jtaPlatform;
-
-	private MultiTableBulkIdStrategy multiTableBulkIdStrategy;
-	private BatchFetchStyle batchFetchStyle;
-	private boolean directReferenceCacheEntriesEnabled;
-	
-	private boolean jtaTrackByThread;
-	private BaselineSessionEventsListenerBuilder baselineSessionEventsListenerBuilder;
-
-
-	/**
-	 * Package protected constructor
-	 */
-	Settings() {
-	}
-
-	// public getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public String getImportFiles() {
-		return importFiles;
-	}
-
-	public void setImportFiles(String importFiles) {
-		this.importFiles = importFiles;
-	}
-
-	public String getDefaultSchemaName() {
-		return defaultSchemaName;
-	}
+	private final SessionFactoryOptions sessionFactoryOptions;
+	private final String defaultCatalogName;
+	private final String defaultSchemaName;
 
-	public String getDefaultCatalogName() {
-		return defaultCatalogName;
+	public Settings(SessionFactoryOptions sessionFactoryOptions) {
+		this( sessionFactoryOptions, null, null );
 	}
 
-	public int getJdbcBatchSize() {
-		return jdbcBatchSize;
+	public Settings(SessionFactoryOptions sessionFactoryOptions, Metadata metadata) {
+		this(
+				sessionFactoryOptions,
+				extractName( metadata.getDatabase().getDefaultSchema().getName().getCatalog() ),
+				extractName( metadata.getDatabase().getDefaultSchema().getName().getSchema() )
+		);
 	}
 
-	public int getDefaultBatchFetchSize() {
-		return defaultBatchFetchSize;
-	}
-
-	public Map getQuerySubstitutions() {
-		return querySubstitutions;
-	}
-
-	public boolean isIdentifierRollbackEnabled() {
-		return identifierRollbackEnabled;
-	}
-
-	public boolean isScrollableResultSetsEnabled() {
-		return scrollableResultSetsEnabled;
-	}
-
-	public boolean isGetGeneratedKeysEnabled() {
-		return getGeneratedKeysEnabled;
+	private static String extractName(Identifier identifier) {
+		return identifier == null ? null : identifier.render();
 	}
 
-	public boolean isMinimalPutsEnabled() {
-		return minimalPutsEnabled;
-	}
+	public Settings(SessionFactoryOptions sessionFactoryOptions, String defaultCatalogName, String defaultSchemaName) {
+		this.sessionFactoryOptions = sessionFactoryOptions;
+		this.defaultCatalogName = defaultCatalogName;
+		this.defaultSchemaName = defaultSchemaName;
 
-	public Integer getJdbcFetchSize() {
-		return jdbcFetchSize;
-	}
+		final boolean debugEnabled =  LOG.isDebugEnabled();
 
-	public String getSessionFactoryName() {
-		return sessionFactoryName;
-	}
+		if ( debugEnabled ) {
+			LOG.debugf( "SessionFactory name : %s", sessionFactoryOptions.getSessionFactoryName() );
+			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled( sessionFactoryOptions.isFlushBeforeCompletionEnabled() ) );
+			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled( sessionFactoryOptions.isAutoCloseSessionEnabled() ) );
 
-	public boolean isSessionFactoryNameAlsoJndiName() {
-		return sessionFactoryNameAlsoJndiName;
-	}
+			LOG.debugf( "Statistics: %s", enabledDisabled( sessionFactoryOptions.isStatisticsEnabled() ) );
 
-	public boolean isAutoCreateSchema() {
-		return autoCreateSchema;
-	}
+			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled( sessionFactoryOptions.isIdentifierRollbackEnabled() ) );
+			LOG.debugf( "Default entity-mode: %s", sessionFactoryOptions.getDefaultEntityMode() );
+			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled( sessionFactoryOptions.isCheckNullability() ) );
+			LOG.debugf( "Allow initialization of lazy state outside session : %s", enabledDisabled( sessionFactoryOptions.isInitializeLazyStateOutsideTransactionsEnabled() ) );
 
-	public boolean isAutoDropSchema() {
-		return autoDropSchema;
-	}
+			LOG.debugf( "Using BatchFetchStyle : " + sessionFactoryOptions.getBatchFetchStyle().name() );
+			LOG.debugf( "Default batch fetch size: %s", sessionFactoryOptions.getDefaultBatchFetchSize() );
+			LOG.debugf( "Maximum outer join fetch depth: %s", sessionFactoryOptions.getMaximumFetchDepth() );
+			LOG.debugf( "Default null ordering: %s", sessionFactoryOptions.getDefaultNullPrecedence() );
+			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled( sessionFactoryOptions.isOrderUpdatesEnabled() ) );
+			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled( sessionFactoryOptions.isOrderInsertsEnabled() ) );
 
-	public boolean isAutoUpdateSchema() {
-		return autoUpdateSchema;
-	}
+			LOG.debugf( "multi-tenancy strategy : %s", sessionFactoryOptions.getMultiTenancyStrategy() );
 
-	public Integer getMaximumFetchDepth() {
-		return maximumFetchDepth;
-	}
+			LOG.debugf( "JTA Track by Thread: %s", enabledDisabled( sessionFactoryOptions.isJtaTrackByThread() ) );
 
-	public RegionFactory getRegionFactory() {
-		return regionFactory;
-	}
+			LOG.debugf( "Query language substitutions: %s", sessionFactoryOptions.getQuerySubstitutions() );
+			LOG.debugf( "JPA query language strict compliance: %s", enabledDisabled( sessionFactoryOptions.isStrictJpaQueryLanguageCompliance() ) );
+			LOG.debugf( "Named query checking : %s", enabledDisabled( sessionFactoryOptions.isNamedQueryStartupCheckingEnabled() ) );
 
-	public boolean isQueryCacheEnabled() {
-		return queryCacheEnabled;
-	}
+			LOG.debugf( "Second-level cache: %s", enabledDisabled( sessionFactoryOptions.isSecondLevelCacheEnabled() ) );
+			LOG.debugf( "Second-level query cache: %s", enabledDisabled( sessionFactoryOptions.isQueryCacheEnabled() ) );
+			LOG.debugf( "Second-level query cache factory: %s", sessionFactoryOptions.getQueryCacheFactory() );
+			LOG.debugf( "Second-level cache region prefix: %s", sessionFactoryOptions.getCacheRegionPrefix() );
+			LOG.debugf( "Optimize second-level cache for minimal puts: %s", enabledDisabled( sessionFactoryOptions.isMinimalPutsEnabled() ) );
+			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled( sessionFactoryOptions.isStructuredCacheEntriesEnabled() ) );
+			LOG.debugf( "Second-level cache direct-reference entries: %s", enabledDisabled( sessionFactoryOptions.isDirectReferenceCacheEntriesEnabled() ) );
+			LOG.debugf( "Automatic eviction of collection cache: %s", enabledDisabled( sessionFactoryOptions.isAutoEvictCollectionCache() ) );
 
-	public boolean isCommentsEnabled() {
-		return commentsEnabled;
+			LOG.debugf( "JDBC batch size: %s", sessionFactoryOptions.getJdbcBatchSize() );
+			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled( sessionFactoryOptions.isJdbcBatchVersionedData() ) );
+			LOG.debugf( "Scrollable result sets: %s", enabledDisabled( sessionFactoryOptions.isScrollableResultSetsEnabled() ) );
+			LOG.debugf( "Wrap result sets: %s", enabledDisabled( sessionFactoryOptions.isWrapResultSetsEnabled() ) );
+			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled( sessionFactoryOptions.isGetGeneratedKeysEnabled() ) );
+			LOG.debugf( "JDBC result set fetch size: %s", sessionFactoryOptions.getJdbcFetchSize() );
+			LOG.debugf( "Connection release mode: %s", sessionFactoryOptions.getConnectionReleaseMode() );
+			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled( sessionFactoryOptions.isCommentsEnabled() ) );
+		}
 	}
 
-	public boolean isSecondLevelCacheEnabled() {
-		return secondLevelCacheEnabled;
+	private static String enabledDisabled(boolean value) {
+		return value ? "enabled" : "disabled";
 	}
 
-	public String getCacheRegionPrefix() {
-		return cacheRegionPrefix;
+	public String getDefaultSchemaName() {
+		return defaultSchemaName;
 	}
 
-	public QueryCacheFactory getQueryCacheFactory() {
-		return queryCacheFactory;
+	public String getDefaultCatalogName() {
+		return defaultCatalogName;
 	}
 
-	public boolean isStatisticsEnabled() {
-		return statisticsEnabled;
+	public String getSessionFactoryName() {
+		return sessionFactoryOptions.getSessionFactoryName();
 	}
 
-	public boolean isJdbcBatchVersionedData() {
-		return jdbcBatchVersionedData;
+	public boolean isSessionFactoryNameAlsoJndiName() {
+		return sessionFactoryOptions.isSessionFactoryNameAlsoJndiName();
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
-		return flushBeforeCompletionEnabled;
+		return sessionFactoryOptions.isFlushBeforeCompletionEnabled();
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
-		return autoCloseSessionEnabled;
-	}
-
-	public ConnectionReleaseMode getConnectionReleaseMode() {
-		return connectionReleaseMode;
+		return sessionFactoryOptions.isAutoCloseSessionEnabled();
 	}
 
-	public QueryTranslatorFactory getQueryTranslatorFactory() {
-		return queryTranslatorFactory;
-	}
-
-	public boolean isWrapResultSetsEnabled() {
-		return wrapResultSetsEnabled;
-	}
-
-	public boolean isOrderUpdatesEnabled() {
-		return orderUpdatesEnabled;
-	}
-
-	public boolean isOrderInsertsEnabled() {
-		return orderInsertsEnabled;
+	public boolean isStatisticsEnabled() {
+		return sessionFactoryOptions.isStatisticsEnabled();
 	}
 
-	public boolean isStructuredCacheEntriesEnabled() {
-		return structuredCacheEntriesEnabled;
+	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
+		return sessionFactoryOptions.getBaselineSessionEventsListenerBuilder();
 	}
 
-	public boolean isDirectReferenceCacheEntriesEnabled() {
-		return directReferenceCacheEntriesEnabled;
+	public boolean isIdentifierRollbackEnabled() {
+		return sessionFactoryOptions.isIdentifierRollbackEnabled();
 	}
 
 	public EntityMode getDefaultEntityMode() {
-		return defaultEntityMode;
-	}
-
-	public boolean isAutoValidateSchema() {
-		return autoValidateSchema;
-	}
-
-	public boolean isDataDefinitionImplicitCommit() {
-		return dataDefinitionImplicitCommit;
-	}
-
-	public boolean isDataDefinitionInTransactionSupported() {
-		return dataDefinitionInTransactionSupported;
-	}
-
-	public boolean isStrictJPAQLCompliance() {
-		return strictJPAQLCompliance;
-	}
-
-	public boolean isNamedQueryStartupCheckingEnabled() {
-		return namedQueryStartupCheckingEnabled;
+		return sessionFactoryOptions.getDefaultEntityMode();
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
-		return entityTuplizerFactory;
+		return sessionFactoryOptions.getEntityTuplizerFactory();
 	}
 
-//	public ComponentTuplizerFactory getComponentTuplizerFactory() {
-//		return componentTuplizerFactory;
-//	}
-
-	public NullPrecedence getDefaultNullPrecedence() {
-		return defaultNullPrecedence;
-	}
-
-	// package protected setters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	void setDefaultSchemaName(String string) {
-		defaultSchemaName = string;
-	}
-
-	void setDefaultCatalogName(String string) {
-		defaultCatalogName = string;
-	}
-
-	void setJdbcBatchSize(int i) {
-		jdbcBatchSize = i;
-	}
-
-	void setDefaultBatchFetchSize(int i) {
-		defaultBatchFetchSize = i;
-	}
-
-	void setQuerySubstitutions(Map map) {
-		querySubstitutions = map;
+	public boolean isCheckNullability() {
+		return sessionFactoryOptions.isCheckNullability();
 	}
 
-	void setIdentifierRollbackEnabled(boolean b) {
-		identifierRollbackEnabled = b;
+	public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
+		return sessionFactoryOptions.isInitializeLazyStateOutsideTransactionsEnabled();
 	}
 
-	void setMinimalPutsEnabled(boolean b) {
-		minimalPutsEnabled = b;
+	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
+		return sessionFactoryOptions.getMultiTableBulkIdStrategy();
 	}
 
-	void setScrollableResultSetsEnabled(boolean b) {
-		scrollableResultSetsEnabled = b;
+	public BatchFetchStyle getBatchFetchStyle() {
+		return sessionFactoryOptions.getBatchFetchStyle();
 	}
 
-	void setGetGeneratedKeysEnabled(boolean b) {
-		getGeneratedKeysEnabled = b;
+	public int getDefaultBatchFetchSize() {
+		return sessionFactoryOptions.getDefaultBatchFetchSize();
 	}
 
-	void setJdbcFetchSize(Integer integer) {
-		jdbcFetchSize = integer;
+	public Integer getMaximumFetchDepth() {
+		return sessionFactoryOptions.getMaximumFetchDepth();
 	}
 
-	void setSessionFactoryName(String string) {
-		sessionFactoryName = string;
+	public NullPrecedence getDefaultNullPrecedence() {
+		return sessionFactoryOptions.getDefaultNullPrecedence();
 	}
 
-	void setSessionFactoryNameAlsoJndiName(boolean sessionFactoryNameAlsoJndiName) {
-		this.sessionFactoryNameAlsoJndiName = sessionFactoryNameAlsoJndiName;
+	public boolean isOrderUpdatesEnabled() {
+		return sessionFactoryOptions.isOrderUpdatesEnabled();
 	}
 
-	void setAutoCreateSchema(boolean b) {
-		autoCreateSchema = b;
+	public boolean isOrderInsertsEnabled() {
+		return sessionFactoryOptions.isOrderInsertsEnabled();
 	}
 
-	void setAutoDropSchema(boolean b) {
-		autoDropSchema = b;
+	public MultiTenancyStrategy getMultiTenancyStrategy() {
+		return sessionFactoryOptions.getMultiTenancyStrategy();
 	}
-
-	void setAutoUpdateSchema(boolean b) {
-		autoUpdateSchema = b;
+	public boolean isJtaTrackByThread() {
+		return sessionFactoryOptions.isJtaTrackByThread();
 	}
 
-	void setMaximumFetchDepth(Integer i) {
-		maximumFetchDepth = i;
+	public Map getQuerySubstitutions() {
+		return sessionFactoryOptions.getQuerySubstitutions();
 	}
 
-	void setRegionFactory(RegionFactory regionFactory) {
-		this.regionFactory = regionFactory;
+	public boolean isStrictJPAQLCompliance() {
+		return sessionFactoryOptions.isStrictJpaQueryLanguageCompliance();
 	}
 
-	void setQueryCacheEnabled(boolean b) {
-		queryCacheEnabled = b;
+	public boolean isNamedQueryStartupCheckingEnabled() {
+		return sessionFactoryOptions.isNamedQueryStartupCheckingEnabled();
 	}
 
-	void setCommentsEnabled(boolean commentsEnabled) {
-		this.commentsEnabled = commentsEnabled;
+	public boolean isSecondLevelCacheEnabled() {
+		return sessionFactoryOptions.isSecondLevelCacheEnabled();
 	}
 
-	void setSecondLevelCacheEnabled(boolean secondLevelCacheEnabled) {
-		this.secondLevelCacheEnabled = secondLevelCacheEnabled;
+	public boolean isQueryCacheEnabled() {
+		return sessionFactoryOptions.isQueryCacheEnabled();
 	}
 
-	void setCacheRegionPrefix(String cacheRegionPrefix) {
-		this.cacheRegionPrefix = cacheRegionPrefix;
+	public QueryCacheFactory getQueryCacheFactory() {
+		return sessionFactoryOptions.getQueryCacheFactory();
 	}
 
-	void setQueryCacheFactory(QueryCacheFactory queryCacheFactory) {
-		this.queryCacheFactory = queryCacheFactory;
+	public String getCacheRegionPrefix() {
+		return sessionFactoryOptions.getCacheRegionPrefix();
 	}
 
-	void setStatisticsEnabled(boolean statisticsEnabled) {
-		this.statisticsEnabled = statisticsEnabled;
+	public boolean isMinimalPutsEnabled() {
+		return sessionFactoryOptions.isMinimalPutsEnabled();
 	}
 
-	void setJdbcBatchVersionedData(boolean jdbcBatchVersionedData) {
-		this.jdbcBatchVersionedData = jdbcBatchVersionedData;
+	public boolean isStructuredCacheEntriesEnabled() {
+		return sessionFactoryOptions.isStructuredCacheEntriesEnabled();
 	}
 
-	void setFlushBeforeCompletionEnabled(boolean flushBeforeCompletionEnabled) {
-		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
+	public boolean isDirectReferenceCacheEntriesEnabled() {
+		return sessionFactoryOptions.isDirectReferenceCacheEntriesEnabled();
 	}
 
-	void setAutoCloseSessionEnabled(boolean autoCloseSessionEnabled) {
-		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
+	public boolean isAutoEvictCollectionCache() {
+		return sessionFactoryOptions.isAutoEvictCollectionCache();
 	}
 
-	void setConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
-		this.connectionReleaseMode = connectionReleaseMode;
+	public boolean isAutoCreateSchema() {
+		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE
+				|| sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE_DROP;
 	}
 
-	void setQueryTranslatorFactory(QueryTranslatorFactory queryTranslatorFactory) {
-		this.queryTranslatorFactory = queryTranslatorFactory;
+	public boolean isAutoDropSchema() {
+		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE_DROP;
 	}
 
-	void setWrapResultSetsEnabled(boolean wrapResultSetsEnabled) {
-		this.wrapResultSetsEnabled = wrapResultSetsEnabled;
+	public boolean isAutoUpdateSchema() {
+		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.UPDATE;
 	}
 
-	void setOrderUpdatesEnabled(boolean orderUpdatesEnabled) {
-		this.orderUpdatesEnabled = orderUpdatesEnabled;
+	public boolean isAutoValidateSchema() {
+		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.VALIDATE;
 	}
 
-	void setOrderInsertsEnabled(boolean orderInsertsEnabled) {
-		this.orderInsertsEnabled = orderInsertsEnabled;
+	public boolean isDataDefinitionImplicitCommit() {
+		return sessionFactoryOptions.isDataDefinitionImplicitCommit();
 	}
 
-	void setStructuredCacheEntriesEnabled(boolean structuredCacheEntriesEnabled) {
-		this.structuredCacheEntriesEnabled = structuredCacheEntriesEnabled;
+	public boolean isDataDefinitionInTransactionSupported() {
+		return sessionFactoryOptions.isDataDefinitionInTransactionSupported();
 	}
 
-	void setDefaultEntityMode(EntityMode defaultEntityMode) {
-		this.defaultEntityMode = defaultEntityMode;
+	public int getJdbcBatchSize() {
+		return sessionFactoryOptions.getJdbcBatchSize();
 	}
 
-	void setAutoValidateSchema(boolean autoValidateSchema) {
-		this.autoValidateSchema = autoValidateSchema;
+	public boolean isJdbcBatchVersionedData() {
+		return sessionFactoryOptions.isJdbcBatchVersionedData();
 	}
 
-	void setDataDefinitionImplicitCommit(boolean dataDefinitionImplicitCommit) {
-		this.dataDefinitionImplicitCommit = dataDefinitionImplicitCommit;
+	public Integer getJdbcFetchSize() {
+		return sessionFactoryOptions.getJdbcFetchSize();
 	}
 
-	void setDataDefinitionInTransactionSupported(boolean dataDefinitionInTransactionSupported) {
-		this.dataDefinitionInTransactionSupported = dataDefinitionInTransactionSupported;
+	public boolean isScrollableResultSetsEnabled() {
+		return sessionFactoryOptions.isScrollableResultSetsEnabled();
 	}
 
-	void setStrictJPAQLCompliance(boolean strictJPAQLCompliance) {
-		this.strictJPAQLCompliance = strictJPAQLCompliance;
+	public boolean isWrapResultSetsEnabled() {
+		return sessionFactoryOptions.isWrapResultSetsEnabled();
 	}
 
-	void setNamedQueryStartupCheckingEnabled(boolean namedQueryStartupCheckingEnabled) {
-		this.namedQueryStartupCheckingEnabled = namedQueryStartupCheckingEnabled;
+	public boolean isGetGeneratedKeysEnabled() {
+		return sessionFactoryOptions.isGetGeneratedKeysEnabled();
 	}
 
-	public void setEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
-		this.entityTuplizerFactory = entityTuplizerFactory;
+	public ConnectionReleaseMode getConnectionReleaseMode() {
+		return sessionFactoryOptions.getConnectionReleaseMode();
 	}
 
-	public boolean isCheckNullability() {
-		return checkNullability;
+	public boolean isCommentsEnabled() {
+		return sessionFactoryOptions.isCommentsEnabled();
 	}
 
-	public void setCheckNullability(boolean checkNullability) {
-		this.checkNullability = checkNullability;
+	public RegionFactory getRegionFactory() {
+		return sessionFactoryOptions.getServiceRegistry().getService( RegionFactory.class );
 	}
 
-	//	void setComponentTuplizerFactory(ComponentTuplizerFactory componentTuplizerFactory) {
-//		this.componentTuplizerFactory = componentTuplizerFactory;
-//	}
-
-	//	public BytecodeProvider getBytecodeProvider() {
-//		return bytecodeProvider;
-//	}
-//
-//	void setBytecodeProvider(BytecodeProvider bytecodeProvider) {
-//		this.bytecodeProvider = bytecodeProvider;
-//	}
-
-
 	public JtaPlatform getJtaPlatform() {
-		return jtaPlatform;
-	}
-
-	void setJtaPlatform(JtaPlatform jtaPlatform) {
-		this.jtaPlatform = jtaPlatform;
-	}
-
-	public MultiTenancyStrategy getMultiTenancyStrategy() {
-		return multiTenancyStrategy;
+		return sessionFactoryOptions.getServiceRegistry().getService( JtaPlatform.class );
 	}
 
-	void setMultiTenancyStrategy(MultiTenancyStrategy multiTenancyStrategy) {
-		this.multiTenancyStrategy = multiTenancyStrategy;
-	}
-
-	public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
-		return initializeLazyStateOutsideTransactions;
-	}
-
-	void setInitializeLazyStateOutsideTransactions(boolean initializeLazyStateOutsideTransactions) {
-		this.initializeLazyStateOutsideTransactions = initializeLazyStateOutsideTransactions;
-	}
-
-	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
-		return multiTableBulkIdStrategy;
-	}
-
-	void setMultiTableBulkIdStrategy(MultiTableBulkIdStrategy multiTableBulkIdStrategy) {
-		this.multiTableBulkIdStrategy = multiTableBulkIdStrategy;
-	}
-
-	public BatchFetchStyle getBatchFetchStyle() {
-		return batchFetchStyle;
-	}
-
-	void setBatchFetchStyle(BatchFetchStyle batchFetchStyle) {
-		this.batchFetchStyle = batchFetchStyle;
-	}
-
-	public void setDirectReferenceCacheEntriesEnabled(boolean directReferenceCacheEntriesEnabled) {
-		this.directReferenceCacheEntriesEnabled = directReferenceCacheEntriesEnabled;
-	}
-
-	void setDefaultNullPrecedence(NullPrecedence defaultNullPrecedence) {
-		this.defaultNullPrecedence = defaultNullPrecedence;
-	}
-
-	public boolean isJtaTrackByThread() {
-		return jtaTrackByThread;
-	}
-
-	public void setJtaTrackByThread(boolean jtaTrackByThread) {
-		this.jtaTrackByThread = jtaTrackByThread;
-	}
-
-	public boolean isAutoEvictCollectionCache() {
-		return autoEvictCollectionCache;
-	}
-
-	public void setAutoEvictCollectionCache(boolean autoEvictCollectionCache) {
-		this.autoEvictCollectionCache = autoEvictCollectionCache;
-	}
-
-	public void setBaselineSessionEventsListenerBuilder(BaselineSessionEventsListenerBuilder baselineSessionEventsListenerBuilder) {
-		this.baselineSessionEventsListenerBuilder = baselineSessionEventsListenerBuilder;
+	public QueryTranslatorFactory getQueryTranslatorFactory() {
+		return sessionFactoryOptions.getServiceRegistry().getService( QueryTranslatorFactory.class );
 	}
 
-	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
-		return baselineSessionEventsListenerBuilder;
+	public void setCheckNullability(boolean enabled) {
+		// ugh, used by org.hibernate.cfg.beanvalidation.TypeSafeActivator as part of the BV integrator
+		sessionFactoryOptions.setCheckNullability( enabled );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
deleted file mode 100644
index 3d6563c379..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ /dev/null
@@ -1,511 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cfg;
-
-import java.io.Serializable;
-import java.util.Map;
-import java.util.Properties;
-
-import org.hibernate.ConnectionReleaseMode;
-import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
-import org.hibernate.MultiTenancyStrategy;
-import org.hibernate.NullPrecedence;
-import org.hibernate.SessionEventListener;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
-import org.hibernate.boot.registry.selector.spi.StrategySelector;
-import org.hibernate.cache.internal.NoCachingRegionFactory;
-import org.hibernate.cache.internal.RegionFactoryInitiator;
-import org.hibernate.cache.internal.StandardQueryCacheFactory;
-import org.hibernate.cache.spi.QueryCacheFactory;
-import org.hibernate.cache.spi.RegionFactory;
-import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
-import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
-import org.hibernate.engine.transaction.spi.TransactionFactory;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
-import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
-import org.hibernate.hql.spi.QueryTranslatorFactory;
-import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.loader.BatchFetchStyle;
-import org.hibernate.service.ServiceRegistry;
-import org.hibernate.tuple.entity.EntityTuplizerFactory;
-
-import org.jboss.logging.Logger;
-
-/**
- * Reads configuration properties and builds a {@link Settings} instance.
- *
- * @author Gavin King
- */
-public class SettingsFactory implements Serializable {
-
-	private static final long serialVersionUID = -1194386144994524825L;
-
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
-
-	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
-
-	public SettingsFactory() {
-	}
-
-	public Settings buildSettings(Map props, ServiceRegistry serviceRegistry) {
-		final boolean debugEnabled =  LOG.isDebugEnabled();
-		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
-		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
-
-		Settings settings = new Settings();
-
-		//SessionFactory name:
-
-		String sessionFactoryName = (String) props.get( AvailableSettings.SESSION_FACTORY_NAME );
-		settings.setSessionFactoryName( sessionFactoryName );
-		settings.setSessionFactoryNameAlsoJndiName(
-				ConfigurationHelper.getBoolean( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, props, true )
-		);
-
-		//JDBC and connection settings:
-
-		//Interrogate JDBC metadata
-		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
-
-		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
-		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
-
-		//use dialect default properties
-		final Properties properties = new Properties();
-		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
-		properties.putAll( props );
-
-		// Transaction settings:
-		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
-
-		MultiTableBulkIdStrategy multiTableBulkIdStrategy = strategySelector.resolveStrategy(
-				MultiTableBulkIdStrategy.class,
-				properties.getProperty( AvailableSettings.HQL_BULK_ID_STRATEGY )
-		);
-		if ( multiTableBulkIdStrategy == null ) {
-			multiTableBulkIdStrategy = jdbcServices.getDialect().supportsTemporaryTables()
-					? TemporaryTableBulkIdStrategy.INSTANCE
-					: new PersistentTableBulkIdStrategy();
-		}
-		settings.setMultiTableBulkIdStrategy( multiTableBulkIdStrategy );
-
-		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(AvailableSettings.FLUSH_BEFORE_COMPLETION, properties);
-		if ( debugEnabled ) {
-			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
-		}
-		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
-
-		boolean autoCloseSession = ConfigurationHelper.getBoolean(AvailableSettings.AUTO_CLOSE_SESSION, properties);
-		if ( debugEnabled ) {
-			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
-		}
-		settings.setAutoCloseSessionEnabled(autoCloseSession);
-
-		//JDBC and connection settings:
-
-		int batchSize = ConfigurationHelper.getInt(AvailableSettings.STATEMENT_BATCH_SIZE, properties, 0);
-		if ( !meta.supportsBatchUpdates() ) {
-			batchSize = 0;
-		}
-		if ( batchSize > 0 && debugEnabled ) {
-			LOG.debugf( "JDBC batch size: %s", batchSize );
-		}
-		settings.setJdbcBatchSize(batchSize);
-
-		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(AvailableSettings.BATCH_VERSIONED_DATA, properties, false);
-		if ( batchSize > 0 && debugEnabled ) {
-			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
-		}
-		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
-
-		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
-				AvailableSettings.USE_SCROLLABLE_RESULTSET,
-				properties,
-				meta.supportsScrollableResults()
-		);
-		if ( debugEnabled ) {
-			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
-		}
-		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
-
-		boolean wrapResultSets = ConfigurationHelper.getBoolean(AvailableSettings.WRAP_RESULT_SETS, properties, false);
-		if ( debugEnabled ) {
-			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
-		}
-		settings.setWrapResultSetsEnabled(wrapResultSets);
-
-		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(AvailableSettings.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
-		if ( debugEnabled ) {
-			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
-		}
-		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
-
-		Integer statementFetchSize = ConfigurationHelper.getInteger(AvailableSettings.STATEMENT_FETCH_SIZE, properties);
-		if ( statementFetchSize != null && debugEnabled ) {
-			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
-		}
-		settings.setJdbcFetchSize(statementFetchSize);
-
-		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
-		if ( debugEnabled ) {
-			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
-		}
-		settings.setMultiTenancyStrategy( multiTenancyStrategy );
-
-		String releaseModeName = ConfigurationHelper.getString( AvailableSettings.RELEASE_CONNECTIONS, properties, "auto" );
-		if ( debugEnabled ) {
-			LOG.debugf( "Connection release mode: %s", releaseModeName );
-		}
-		ConnectionReleaseMode releaseMode;
-		if ( "auto".equals(releaseModeName) ) {
-			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
-		}
-		else {
-			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
-			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
-				// we need to make sure the underlying JDBC connection access supports aggressive release...
-				boolean supportsAgrressiveRelease = multiTenancyStrategy.requiresMultiTenantConnectionProvider()
-						? serviceRegistry.getService( MultiTenantConnectionProvider.class ).supportsAggressiveRelease()
-						: serviceRegistry.getService( ConnectionProvider.class ).supportsAggressiveRelease();
-				if ( ! supportsAgrressiveRelease ) {
-					LOG.unsupportedAfterStatement();
-					releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
-				}
-			}
-		}
-		settings.setConnectionReleaseMode( releaseMode );
-
-		final BatchFetchStyle batchFetchStyle = BatchFetchStyle.interpret( properties.get( AvailableSettings.BATCH_FETCH_STYLE ) );
-		LOG.debugf( "Using BatchFetchStyle : " + batchFetchStyle.name() );
-		settings.setBatchFetchStyle( batchFetchStyle );
-
-
-		//SQL Generation settings:
-
-		String defaultSchema = properties.getProperty( AvailableSettings.DEFAULT_SCHEMA );
-		String defaultCatalog = properties.getProperty( AvailableSettings.DEFAULT_CATALOG );
-		if ( defaultSchema != null && debugEnabled ) {
-			LOG.debugf( "Default schema: %s", defaultSchema );
-		}
-		if ( defaultCatalog != null && debugEnabled ) {
-			LOG.debugf( "Default catalog: %s", defaultCatalog );
-		}
-		settings.setDefaultSchemaName( defaultSchema );
-		settings.setDefaultCatalogName( defaultCatalog );
-
-		Integer maxFetchDepth = ConfigurationHelper.getInteger( AvailableSettings.MAX_FETCH_DEPTH, properties );
-		if ( maxFetchDepth != null ) {
-			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
-		}
-		settings.setMaximumFetchDepth( maxFetchDepth );
-
-		int batchFetchSize = ConfigurationHelper.getInt(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
-		if ( debugEnabled ) {
-			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
-		}
-		settings.setDefaultBatchFetchSize( batchFetchSize );
-
-		boolean comments = ConfigurationHelper.getBoolean( AvailableSettings.USE_SQL_COMMENTS, properties );
-		if ( debugEnabled ) {
-			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
-		}
-		settings.setCommentsEnabled( comments );
-
-		boolean orderUpdates = ConfigurationHelper.getBoolean( AvailableSettings.ORDER_UPDATES, properties );
-		if ( debugEnabled ) {
-			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
-		}
-		settings.setOrderUpdatesEnabled( orderUpdates );
-
-		boolean orderInserts = ConfigurationHelper.getBoolean(AvailableSettings.ORDER_INSERTS, properties);
-		if ( debugEnabled ) {
-			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
-		}
-		settings.setOrderInsertsEnabled( orderInserts );
-
-		String defaultNullPrecedence = ConfigurationHelper.getString(
-				AvailableSettings.DEFAULT_NULL_ORDERING, properties, "none", "first", "last"
-		);
-		if ( debugEnabled ) {
-			LOG.debugf( "Default null ordering: %s", defaultNullPrecedence );
-		}
-		settings.setDefaultNullPrecedence( NullPrecedence.parse( defaultNullPrecedence ) );
-
-		//Query parser settings:
-
-		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
-
-		Map querySubstitutions = ConfigurationHelper.toMap( AvailableSettings.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
-		if ( debugEnabled ) {
-			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
-		}
-		settings.setQuerySubstitutions( querySubstitutions );
-
-		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( AvailableSettings.JPAQL_STRICT_COMPLIANCE, properties, false );
-		if ( debugEnabled ) {
-			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
-		}
-		settings.setStrictJPAQLCompliance( jpaqlCompliance );
-
-		// Second-level / query cache:
-
-		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( AvailableSettings.USE_SECOND_LEVEL_CACHE, properties, true );
-		if ( debugEnabled ) {
-			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
-		}
-		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
-
-		boolean useQueryCache = ConfigurationHelper.getBoolean(AvailableSettings.USE_QUERY_CACHE, properties);
-		if ( debugEnabled ) {
-			LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
-		}
-		settings.setQueryCacheEnabled( useQueryCache );
-		if (useQueryCache) {
-			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
-		}
-
-		settings.setRegionFactory( serviceRegistry.getService( RegionFactory.class ) );
-
-		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
-				AvailableSettings.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
-		);
-		if ( debugEnabled ) {
-			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
-		}
-		settings.setMinimalPutsEnabled( useMinimalPuts );
-
-		String prefix = properties.getProperty( AvailableSettings.CACHE_REGION_PREFIX );
-		if ( StringHelper.isEmpty(prefix) ) {
-			prefix=null;
-		}
-		if ( prefix != null && debugEnabled ) {
-			LOG.debugf( "Cache region prefix: %s", prefix );
-		}
-		settings.setCacheRegionPrefix( prefix );
-
-		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( AvailableSettings.USE_STRUCTURED_CACHE, properties, false );
-		if ( debugEnabled ) {
-			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
-		}
-		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
-
-		boolean useDirectReferenceCacheEntries = ConfigurationHelper.getBoolean(
-				AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES,
-				properties,
-				false
-		);
-		if ( debugEnabled ) {
-			LOG.debugf( "Second-level cache direct-reference entries: %s", enabledDisabled(useDirectReferenceCacheEntries) );
-		}
-		settings.setDirectReferenceCacheEntriesEnabled( useDirectReferenceCacheEntries );
-
-		boolean autoEvictCollectionCache = ConfigurationHelper.getBoolean( AvailableSettings.AUTO_EVICT_COLLECTION_CACHE, properties, false);
-		if ( debugEnabled ) {
-			LOG.debugf( "Automatic eviction of collection cache: %s", enabledDisabled(autoEvictCollectionCache) );
-		}
-		settings.setAutoEvictCollectionCache( autoEvictCollectionCache );
-
-		//Statistics and logging:
-
-		boolean useStatistics = ConfigurationHelper.getBoolean( AvailableSettings.GENERATE_STATISTICS, properties );
-		if ( debugEnabled ) {
-			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
-		}
-		settings.setStatisticsEnabled( useStatistics );
-
-		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( AvailableSettings.USE_IDENTIFIER_ROLLBACK, properties );
-		if ( debugEnabled ) {
-			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
-		}
-		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
-
-		//Schema export:
-
-		String autoSchemaExport = properties.getProperty( AvailableSettings.HBM2DDL_AUTO );
-		if ( "validate".equals(autoSchemaExport) ) {
-			settings.setAutoValidateSchema( true );
-		}
-		else if ( "update".equals(autoSchemaExport) ) {
-			settings.setAutoUpdateSchema( true );
-		}
-		else if ( "create".equals(autoSchemaExport) ) {
-			settings.setAutoCreateSchema( true );
-		}
-		else if ( "create-drop".equals( autoSchemaExport ) ) {
-			settings.setAutoCreateSchema( true );
-			settings.setAutoDropSchema( true );
-		}
-		else if ( !StringHelper.isEmpty( autoSchemaExport ) ) {
-			LOG.warn( "Unrecognized value for \"hibernate.hbm2ddl.auto\": " + autoSchemaExport );
-		}
-		settings.setImportFiles( properties.getProperty( AvailableSettings.HBM2DDL_IMPORT_FILES ) );
-
-		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( AvailableSettings.DEFAULT_ENTITY_MODE ) );
-		if ( debugEnabled ) {
-			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
-		}
-		settings.setDefaultEntityMode( defaultEntityMode );
-
-		boolean namedQueryChecking = ConfigurationHelper.getBoolean( AvailableSettings.QUERY_STARTUP_CHECKING, properties, true );
-		if ( debugEnabled ) {
-			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
-		}
-		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
-
-		boolean checkNullability = ConfigurationHelper.getBoolean(AvailableSettings.CHECK_NULLABILITY, properties, true);
-		if ( debugEnabled ) {
-			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
-		}
-		settings.setCheckNullability(checkNullability);
-
-		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
-		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
-
-//		String provider = properties.getProperty( AvailableSettings.BYTECODE_PROVIDER );
-//		log.info( "Bytecode provider name : " + provider );
-//		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
-//		settings.setBytecodeProvider( bytecodeProvider );
-
-		boolean initializeLazyStateOutsideTransactionsEnabled = ConfigurationHelper.getBoolean(
-				AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS,
-				properties,
-				false
-		);
-		if ( debugEnabled ) {
-			LOG.debugf( "Allow initialization of lazy state outside session : : %s", enabledDisabled( initializeLazyStateOutsideTransactionsEnabled ) );
-		}
-		settings.setInitializeLazyStateOutsideTransactions( initializeLazyStateOutsideTransactionsEnabled );
-
-		boolean jtaTrackByThread = ConfigurationHelper.getBoolean(
-				AvailableSettings.JTA_TRACK_BY_THREAD,
-				properties,
-				true
-		);
-		if ( debugEnabled ) {
-			LOG.debugf( "JTA Track by Thread: %s", enabledDisabled(jtaTrackByThread) );
-		}
-		settings.setJtaTrackByThread( jtaTrackByThread );
-
-		final String autoSessionEventsListenerName = properties.getProperty( AvailableSettings.AUTO_SESSION_EVENTS_LISTENER );
-		final Class<? extends SessionEventListener> autoSessionEventsListener = autoSessionEventsListenerName == null
-				? null
-				: strategySelector.selectStrategyImplementor( SessionEventListener.class, autoSessionEventsListenerName );
-
-		final boolean logSessionMetrics = ConfigurationHelper.getBoolean(
-				AvailableSettings.LOG_SESSION_METRICS,
-				properties,
-				useStatistics
-
-		);
-		settings.setBaselineSessionEventsListenerBuilder(
-				new BaselineSessionEventsListenerBuilder( logSessionMetrics, autoSessionEventsListener )
-		);
-
-		return settings;
-
-	}
-
-//	protected BytecodeProvider buildBytecodeProvider(String providerName) {
-//		if ( "javassist".equals( providerName ) ) {
-//			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
-//		}
-//		else {
-//            LOG.debug("Using javassist as bytecode provider by default");
-//			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
-//		}
-//	}
-
-	private static String enabledDisabled(boolean value) {
-		return value ? "enabled" : "disabled";
-	}
-
-	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
-		String queryCacheFactoryClassName = ConfigurationHelper.getString(
-				AvailableSettings.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
-		);
-		LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
-		try {
-			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
-					.classForName( queryCacheFactoryClassName )
-					.newInstance();
-		}
-		catch (Exception e) {
-			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
-		}
-	}
-	//todo remove this once we move to new metamodel
-	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
-		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
-		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
-				ConfigurationHelper.getString(
-						AvailableSettings.CACHE_REGION_FACTORY, properties, null
-				)
-		);
-		if ( regionFactoryClassName == null ) {
-			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
-		}
-		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
-		try {
-			try {
-				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
-						.getConstructor( Properties.class )
-						.newInstance( properties );
-			}
-			catch ( NoSuchMethodException e ) {
-				// no constructor accepting Properties found, try no arg constructor
-				LOG.debugf(
-						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
-						regionFactoryClassName
-				);
-				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
-						.newInstance();
-			}
-		}
-		catch ( Exception e ) {
-			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
-		}
-	}
-
-	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
-		String className = ConfigurationHelper.getString(
-				AvailableSettings.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
-		);
-		LOG.debugf( "Query translator: %s", className );
-		try {
-			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
-					.classForName( className )
-					.newInstance();
-		}
-		catch ( Exception e ) {
-			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
-		}
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeQueryInterpreterInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeQueryInterpreterInitiator.java
index 3d299916d4..528481e6e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeQueryInterpreterInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/NativeQueryInterpreterInitiator.java
@@ -1,53 +1,53 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.engine.query.spi;
 
-import org.hibernate.SessionFactory;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.query.internal.NativeQueryInterpreterStandardImpl;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 
 /**
  * @author Steve Ebersole
  */
 public class NativeQueryInterpreterInitiator implements SessionFactoryServiceInitiator<NativeQueryInterpreter> {
 	/**
 	 * Singleton access
 	 */
 	public static final NativeQueryInterpreterInitiator INSTANCE = new NativeQueryInterpreterInitiator();
 
 	@Override
 	public NativeQueryInterpreter initiateService(
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions sessionFactoryOptions,
+			SessionFactoryOptions sessionFactoryOptions,
 			ServiceRegistryImplementor registry) {
 		return NativeQueryInterpreterStandardImpl.INSTANCE;
 	}
 
 	@Override
 	public Class<NativeQueryInterpreter> getServiceInitiated() {
 		return NativeQueryInterpreter.class;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CacheInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CacheInitiator.java
index de1a4ae037..e0bd0f1e9c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/CacheInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CacheInitiator.java
@@ -1,52 +1,52 @@
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
 
-import org.hibernate.SessionFactory;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.internal.CacheImpl;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 
 /**
  * Initiator for second level cache support
  *
  * @author Steve Ebersole
  * @author Strong Liu
  */
 public class CacheInitiator implements SessionFactoryServiceInitiator<CacheImplementor> {
 	public static final CacheInitiator INSTANCE = new CacheInitiator();
 
 	@Override
 	public CacheImplementor initiateService(
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions sessionFactoryOptions,
+			SessionFactoryOptions sessionFactoryOptions,
 			ServiceRegistryImplementor registry) {
 		return new CacheImpl( sessionFactory );
 	}
 
 	@Override
 	public Class<CacheImplementor> getServiceInitiated() {
 		return CacheImplementor.class;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
index 2ab72c8230..295f6b9455 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
@@ -1,52 +1,52 @@
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
 package org.hibernate.event.service.internal;
 
-import org.hibernate.SessionFactory;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 
 /**
  * Service initiator for {@link EventListenerRegistry}
  *
  * @author Steve Ebersole
  */
 public class EventListenerServiceInitiator implements SessionFactoryServiceInitiator<EventListenerRegistry> {
 	public static final EventListenerServiceInitiator INSTANCE = new EventListenerServiceInitiator();
 
 	@Override
 	public Class<EventListenerRegistry> getServiceInitiated() {
 		return EventListenerRegistry.class;
 	}
 
 	@Override
 	public EventListenerRegistry initiateService(
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions sessionFactoryOptions,
+			SessionFactoryOptions sessionFactoryOptions,
 			ServiceRegistryImplementor registry) {
 		return new EventListenerRegistryImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/QueryTranslatorFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/QueryTranslatorFactoryInitiator.java
new file mode 100644
index 0000000000..c8144e32d4
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/QueryTranslatorFactoryInitiator.java
@@ -0,0 +1,78 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.hql.internal;
+
+import java.util.Map;
+
+import org.hibernate.HibernateException;
+import org.hibernate.boot.registry.StandardServiceInitiator;
+import org.hibernate.boot.registry.selector.spi.StrategySelector;
+import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
+import org.hibernate.hql.spi.QueryTranslatorFactory;
+import org.hibernate.internal.CoreLogging;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+
+import org.jboss.logging.Logger;
+
+import static org.hibernate.cfg.AvailableSettings.QUERY_TRANSLATOR;
+
+/**
+ * Initiator for the QueryTranslatorFactory service
+ *
+ * @author Steve Ebersole
+ */
+public class QueryTranslatorFactoryInitiator implements StandardServiceInitiator<QueryTranslatorFactory> {
+	private static final CoreMessageLogger log = CoreLogging.messageLogger( QueryTranslatorFactoryInitiator.class );
+
+	/**
+	 * Singleton access
+	 */
+	public static final QueryTranslatorFactoryInitiator INSTANCE = new QueryTranslatorFactoryInitiator();
+
+	@Override
+	public QueryTranslatorFactory initiateService(
+			Map configurationValues,
+			ServiceRegistryImplementor registry) {
+		final StrategySelector strategySelector = registry.getService( StrategySelector.class );
+		final QueryTranslatorFactory factory = strategySelector.resolveDefaultableStrategy(
+				QueryTranslatorFactory.class,
+				configurationValues.get( QUERY_TRANSLATOR ),
+				ASTQueryTranslatorFactory.INSTANCE
+		);
+
+		log.debugf( "QueryTranslatorFactory : %s", factory );
+		if ( factory instanceof ASTQueryTranslatorFactory ) {
+			log.usingAstQueryTranslatorFactory();
+		}
+
+		return factory;
+	}
+
+	@Override
+	public Class<QueryTranslatorFactory> getServiceInitiated() {
+		return QueryTranslatorFactory.class;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ASTQueryTranslatorFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ASTQueryTranslatorFactory.java
index e0deb164c3..840c80b619 100755
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ASTQueryTranslatorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ASTQueryTranslatorFactory.java
@@ -1,68 +1,73 @@
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
 package org.hibernate.hql.internal.ast;
 
 import java.util.Map;
 
 import org.hibernate.engine.query.spi.EntityGraphQueryHint;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.spi.FilterTranslator;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Generates translators which uses the Antlr-based parser to perform
  * the translation.
  *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public class ASTQueryTranslatorFactory implements QueryTranslatorFactory {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( ASTQueryTranslatorFactory.class );
 
+	/**
+	 * Singleton access
+	 */
+	public static final ASTQueryTranslatorFactory INSTANCE = new ASTQueryTranslatorFactory();
+
 	public ASTQueryTranslatorFactory() {
-		LOG.usingAstQueryTranslatorFactory();
 	}
 
 	@Override
 	public QueryTranslator createQueryTranslator(
 			String queryIdentifier,
 			String queryString,
 			Map filters,
 			SessionFactoryImplementor factory,
 			EntityGraphQueryHint entityGraphQueryHint) {
 		return new QueryTranslatorImpl( queryIdentifier, queryString, filters, factory, entityGraphQueryHint );
 	}
 
 	@Override
 	public FilterTranslator createFilterTranslator(
 			String queryIdentifier,
 			String queryString,
 			Map filters,
 			SessionFactoryImplementor factory) {
 		return new QueryTranslatorImpl( queryIdentifier, queryString, filters, factory );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslatorFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslatorFactory.java
index 3552d6e856..1ea88863db 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslatorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslatorFactory.java
@@ -1,62 +1,72 @@
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
 package org.hibernate.hql.spi;
 import java.util.Map;
 
 import org.hibernate.engine.query.spi.EntityGraphQueryHint;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.service.Service;
 
 /**
  * Facade for generation of {@link QueryTranslator} and {@link FilterTranslator} instances.
  *
  * @author Gavin King
+ * @author Steve Ebersole
  */
-public interface QueryTranslatorFactory {
+public interface QueryTranslatorFactory extends Service {
 	/**
 	 * Construct a {@link QueryTranslator} instance capable of translating
 	 * an HQL query string.
 	 *
 	 * @param queryIdentifier The query-identifier (used in
 	 * {@link org.hibernate.stat.QueryStatistics} collection). This is
 	 * typically the same as the queryString parameter except for the case of
 	 * split polymorphic queries which result in multiple physical sql
 	 * queries.
 	 * @param queryString The query string to be translated
 	 * @param filters Currently enabled filters
 	 * @param factory The session factory.
 	 * @param entityGraphQueryHint
 	 * @return an appropriate translator.
 	 */
-	public QueryTranslator createQueryTranslator(String queryIdentifier, String queryString, Map filters,
-			SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint);
+	public QueryTranslator createQueryTranslator(
+			String queryIdentifier,
+			String queryString,
+			Map filters,
+			SessionFactoryImplementor factory,
+			EntityGraphQueryHint entityGraphQueryHint);
 
 	/**
 	 * Construct a {@link FilterTranslator} instance capable of translating
 	 * an HQL filter string.
 	 *
 	 * @see #createQueryTranslator
 	 */
-	public FilterTranslator createFilterTranslator(String queryIdentifier, String queryString, Map filters, SessionFactoryImplementor factory);
+	public FilterTranslator createFilterTranslator(
+			String queryIdentifier,
+			String queryString,
+			Map filters,
+			SessionFactoryImplementor factory);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 9a760a661e..087ac1ac64 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1222 +1,1223 @@
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
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
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
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
 import org.hibernate.boot.cfgxml.spi.LoadedConfig;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.boot.spi.SessionFactoryOptions;
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
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
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
 
 import org.jboss.logging.Logger;
 
 
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
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 
 	public SessionFactoryImpl(final MetadataImplementor metadata, SessionFactoryOptions options) {
 		LOG.debug( "Building session factory" );
 
 		this.sessionFactoryOptions = options;
-		this.settings = options.getSettings();
+		this.settings = new Settings( options, metadata );
 
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
 		for ( final PersistentClass model : metadata.getEntityBindings() ) {
 			model.prepareTemporaryTables( metadata, getDialect() );
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
 				metadata
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
 
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
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
 				EntityRegion entityRegion = regionFactory.buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 																					 .decode( model ) );
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
 		private static final Logger log = CoreLogging.logger( SessionBuilderImpl.class );
 
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
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
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
index f4eeeeb762..a8119ecfc2 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
@@ -1,442 +1,487 @@
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
 package org.hibernate.internal.util.config;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.StringTokenizer;
 
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 /**
  * Collection of helper methods for dealing with configuration settings.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class ConfigurationHelper {
 
 	private static final String PLACEHOLDER_START = "${";
 
 	/**
 	 * Disallow instantiation
 	 */
 	private ConfigurationHelper() {
 	}
 
 	/**
 	 * Get the config value as a {@link String}
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config values
 	 *
 	 * @return The value, or null if not found
 	 */
 	public static String getString(String name, Map values) {
 		Object value = values.get( name );
 		if ( value == null ) {
 			return null;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return (String) value;
 		}
 		return value.toString();
 	}
 
 	/**
 	 * Get the config value as a {@link String}
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config values
 	 * @param defaultValue The default value to use if not found
 	 *
 	 * @return The value.
 	 */
 	public static String getString(String name, Map values, String defaultValue) {
 		final String value = getString( name, values );
 		return value == null ? defaultValue : value;
 	}
 
 	/**
 	 * Get the config value as a {@link String}.
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config parameters.
 	 * @param defaultValue The default value to use if not found.
 	 * @param otherSupportedValues List of other supported values. Does not need to contain the default one.
 	 *
 	 * @return The value.
 	 *
 	 * @throws ConfigurationException Unsupported value provided.
 	 *
 	 */
 	public static String getString(String name, Map values, String defaultValue, String ... otherSupportedValues) {
 		final String value = getString( name, values, defaultValue );
 		if ( !defaultValue.equals( value ) && ArrayHelper.indexOf( otherSupportedValues, value ) == -1 ) {
 			throw new ConfigurationException(
 					"Unsupported configuration [name=" + name + ", value=" + value + "]. " +
 							"Choose value between: '" + defaultValue + "', '" + StringHelper.join( "', '", otherSupportedValues ) + "'."
 			);
 		}
 		return value;
 	}
 
 	/**
 	 * Get the config value as a boolean (default of false)
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config values
 	 *
 	 * @return The value.
 	 */
 	public static boolean getBoolean(String name, Map values) {
 		return getBoolean( name, values, false );
 	}
 
 	/**
 	 * Get the config value as a boolean.
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config values
 	 * @param defaultValue The default value to use if not found
 	 *
 	 * @return The value.
 	 */
 	public static boolean getBoolean(String name, Map values, boolean defaultValue) {
 		Object value = values.get( name );
 		if ( value == null ) {
 			return defaultValue;
 		}
 		if ( Boolean.class.isInstance( value ) ) {
 			return ( (Boolean) value ).booleanValue();
 		}
 		if ( String.class.isInstance( value ) ) {
 			return Boolean.parseBoolean( (String) value );
 		}
 		throw new ConfigurationException(
 				"Could not determine how to handle configuration value [name=" + name + ", value=" + value + "] as boolean"
 		);
 	}
 
 	/**
 	 * Get the config value as a boolean (default of false)
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config values
 	 *
 	 * @return The value.
 	 */
 	public static Boolean getBooleanWrapper(String name, Map values, Boolean defaultValue) {
 		Object value = values.get( name );
 		if ( value == null ) {
 			return defaultValue;
 		}
 		if ( Boolean.class.isInstance( value ) ) {
 			return (Boolean) value;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return Boolean.valueOf( (String) value );
 		}
 		throw new ConfigurationException(
 				"Could not determine how to handle configuration value [name=" + name + ", value=" + value + "] as boolean"
 		);
 	}
 
 	/**
 	 * Get the config value as an int
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config values
 	 * @param defaultValue The default value to use if not found
 	 *
 	 * @return The value.
 	 */
 	public static int getInt(String name, Map values, int defaultValue) {
 		Object value = values.get( name );
 		if ( value == null ) {
 			return defaultValue;
 		}
 		if ( Integer.class.isInstance( value ) ) {
 			return (Integer) value;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return Integer.parseInt( (String) value );
 		}
 		throw new ConfigurationException(
 				"Could not determine how to handle configuration value [name=" + name +
 						", value=" + value + "(" + value.getClass().getName() + ")] as int"
 		);
 	}
 
 	/**
 	 * Get the config value as an {@link Integer}
 	 *
 	 * @param name The config setting name.
 	 * @param values The map of config values
 	 *
 	 * @return The value, or null if not found
 	 */
 	public static Integer getInteger(String name, Map values) {
 		Object value = values.get( name );
 		if ( value == null ) {
 			return null;
 		}
 		if ( Integer.class.isInstance( value ) ) {
 			return (Integer) value;
 		}
 		if ( String.class.isInstance( value ) ) {
 			//empty values are ignored
 			final String trimmed = value.toString().trim();
 			if ( trimmed.isEmpty() ) {
 				return null;
 			}
 			return Integer.valueOf( trimmed );
 		}
 		throw new ConfigurationException(
 				"Could not determine how to handle configuration value [name=" + name +
 						", value=" + value + "(" + value.getClass().getName() + ")] as Integer"
 		);
 	}
 
 	public static long getLong(String name, Map values, int defaultValue) {
 		Object value = values.get( name );
 		if ( value == null ) {
 			return defaultValue;
 		}
 		if ( Long.class.isInstance( value ) ) {
 			return (Long) value;
 		}
 		if ( String.class.isInstance( value ) ) {
 			return Long.parseLong( (String) value );
 		}
 		throw new ConfigurationException(
 				"Could not determine how to handle configuration value [name=" + name +
 						", value=" + value + "(" + value.getClass().getName() + ")] as long"
 		);
 	}
 
 	/**
 	 * Make a clone of the configuration values.
 	 *
 	 * @param configurationValues The config values to clone
 	 *
 	 * @return The clone
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public static Map clone(Map<?,?> configurationValues) {
 		if ( configurationValues == null ) {
 			return null;
 		}
 		// If a Properties object, leverage its clone() impl
 		if ( Properties.class.isInstance( configurationValues ) ) {
 			return (Properties) ( (Properties) configurationValues ).clone();
 		}
 		// Otherwise make a manual copy
 		HashMap clone = new HashMap();
 		for ( Map.Entry entry : configurationValues.entrySet() ) {
 			clone.put( entry.getKey(), entry.getValue() );
 		}
 		return clone;
 	}
 
 
 
 	/**
 	 * replace a property by a starred version
 	 *
 	 * @param props properties to check
 	 * @param key proeprty to mask
 	 *
 	 * @return cloned and masked properties
 	 */
 	public static Properties maskOut(Properties props, String key) {
 		Properties clone = ( Properties ) props.clone();
 		if ( clone.get( key ) != null ) {
 			clone.setProperty( key, "****" );
 		}
 		return clone;
 	}
 
 
 
 
 
 	/**
 	 * Extract a property value by name from the given properties object.
 	 * <p/>
 	 * Both <tt>null</tt> and <tt>empty string</tt> are viewed as the same, and return null.
 	 *
 	 * @param propertyName The name of the property for which to extract value
 	 * @param properties The properties object
 	 * @return The property value; may be null.
 	 */
 	public static String extractPropertyValue(String propertyName, Properties properties) {
 		String value = properties.getProperty( propertyName );
 		if ( value == null ) {
 			return null;
 		}
 		value = value.trim();
 		if ( StringHelper.isEmpty( value ) ) {
 			return null;
 		}
 		return value;
 	}
+	/**
+	 * Extract a property value by name from the given properties object.
+	 * <p/>
+	 * Both <tt>null</tt> and <tt>empty string</tt> are viewed as the same, and return null.
+	 *
+	 * @param propertyName The name of the property for which to extract value
+	 * @param properties The properties object
+	 * @return The property value; may be null.
+	 */
+	public static String extractPropertyValue(String propertyName, Map properties) {
+		String value = (String) properties.get( propertyName );
+		if ( value == null ) {
+			return null;
+		}
+		value = value.trim();
+		if ( StringHelper.isEmpty( value ) ) {
+			return null;
+		}
+		return value;
+	}
 
 	/**
 	 * Constructs a map from a property value.
 	 * <p/>
 	 * The exact behavior here is largely dependant upon what is passed in as
 	 * the delimiter.
 	 *
 	 * @see #extractPropertyValue(String, java.util.Properties)
 	 *
 	 * @param propertyName The name of the property for which to retrieve value
 	 * @param delim The string defining tokens used as both entry and key/value delimiters.
 	 * @param properties The properties object
 	 * @return The resulting map; never null, though perhaps empty.
 	 */
 	public static Map toMap(String propertyName, String delim, Properties properties) {
 		Map map = new HashMap();
 		String value = extractPropertyValue( propertyName, properties );
 		if ( value != null ) {
 			StringTokenizer tokens = new StringTokenizer( value, delim );
 			while ( tokens.hasMoreTokens() ) {
 				map.put( tokens.nextToken(), tokens.hasMoreElements() ? tokens.nextToken() : "" );
 			}
 		}
 		return map;
 	}
 
 	/**
+	 * Constructs a map from a property value.
+	 * <p/>
+	 * The exact behavior here is largely dependant upon what is passed in as
+	 * the delimiter.
+	 *
+	 * @see #extractPropertyValue(String, java.util.Properties)
+	 *
+	 * @param propertyName The name of the property for which to retrieve value
+	 * @param delim The string defining tokens used as both entry and key/value delimiters.
+	 * @param properties The properties object
+	 * @return The resulting map; never null, though perhaps empty.
+	 */
+	public static Map toMap(String propertyName, String delim, Map properties) {
+		Map map = new HashMap();
+		String value = extractPropertyValue( propertyName, properties );
+		if ( value != null ) {
+			StringTokenizer tokens = new StringTokenizer( value, delim );
+			while ( tokens.hasMoreTokens() ) {
+				map.put( tokens.nextToken(), tokens.hasMoreElements() ? tokens.nextToken() : "" );
+			}
+		}
+		return map;
+	}
+
+	/**
 	 * Get a property value as a string array.
 	 *
 	 * @see #extractPropertyValue(String, java.util.Properties)
 	 * @see #toStringArray(String, String)
 	 *
 	 * @param propertyName The name of the property for which to retrieve value
 	 * @param delim The delimiter used to separate individual array elements.
 	 * @param properties The properties object
 	 * @return The array; never null, though may be empty.
 	 */
 	public static String[] toStringArray(String propertyName, String delim, Properties properties) {
 		return toStringArray( extractPropertyValue( propertyName, properties ), delim );
 	}
 
 	/**
 	 * Convert a string to an array of strings.  The assumption is that
 	 * the individual array elements are delimited in the source stringForm
 	 * param by the delim param.
 	 *
 	 * @param stringForm The string form of the string array.
 	 * @param delim The delimiter used to separate individual array elements.
 	 * @return The array; never null, though may be empty.
 	 */
 	public static String[] toStringArray(String stringForm, String delim) {
 		// todo : move to StringHelper?
 		if ( stringForm != null ) {
 			return StringHelper.split( delim, stringForm );
 		}
 		else {
 			return ArrayHelper.EMPTY_STRING_ARRAY;
 		}
 	}
 
 	/**
 	 * Handles interpolation processing for all entries in a properties object.
 	 *
 	 * @param configurationValues The configuration map.
 	 */
 	public static void resolvePlaceHolders(Map<?,?> configurationValues) {
 		Iterator itr = configurationValues.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final Object value = entry.getValue();
 			if ( value != null && String.class.isInstance( value ) ) {
 				final String resolved = resolvePlaceHolder( ( String ) value );
 				if ( !value.equals( resolved ) ) {
 					if ( resolved == null ) {
 						itr.remove();
 					}
 					else {
 						entry.setValue( resolved );
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Handles interpolation processing for a single property.
 	 *
 	 * @param property The property value to be processed for interpolation.
 	 * @return The (possibly) interpolated property value.
 	 */
 	public static String resolvePlaceHolder(String property) {
 		if ( property.indexOf( PLACEHOLDER_START ) < 0 ) {
 			return property;
 		}
 		StringBuilder buff = new StringBuilder();
 		char[] chars = property.toCharArray();
 		for ( int pos = 0; pos < chars.length; pos++ ) {
 			if ( chars[pos] == '$' ) {
 				// peek ahead
 				if ( chars[pos+1] == '{' ) {
 					// we have a placeholder, spin forward till we find the end
 					String systemPropertyName = "";
 					int x = pos + 2;
 					for (  ; x < chars.length && chars[x] != '}'; x++ ) {
 						systemPropertyName += chars[x];
 						// if we reach the end of the string w/o finding the
 						// matching end, that is an exception
 						if ( x == chars.length - 1 ) {
 							throw new IllegalArgumentException( "unmatched placeholder start [" + property + "]" );
 						}
 					}
 					String systemProperty = extractFromSystem( systemPropertyName );
 					buff.append( systemProperty == null ? "" : systemProperty );
 					pos = x + 1;
 					// make sure spinning forward did not put us past the end of the buffer...
 					if ( pos >= chars.length ) {
 						break;
 					}
 				}
 			}
 			buff.append( chars[pos] );
 		}
 		String rtn = buff.toString();
 		return StringHelper.isEmpty( rtn ) ? null : rtn;
 	}
 
 	private static String extractFromSystem(String systemPropertyName) {
 		try {
 			return System.getProperty( systemPropertyName );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 3d71c9d7a5..264814661b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,101 +1,103 @@
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
 package org.hibernate.service;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.boot.cfgxml.internal.CfgXmlAccessServiceInitiator;
 import org.hibernate.boot.registry.StandardServiceInitiator;
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.engine.config.internal.ConfigurationServiceInitiator;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.engine.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
 import org.hibernate.engine.jdbc.cursor.internal.RefCursorSupportInitiator;
 import org.hibernate.engine.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.engine.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.jndi.internal.JndiServiceInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformResolverInitiator;
+import org.hibernate.hql.internal.QueryTranslatorFactoryInitiator;
 import org.hibernate.id.factory.internal.MutableIdentifierGeneratorFactoryInitiator;
 import org.hibernate.jmx.internal.JmxServiceInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractorInitiator;
 import org.hibernate.tool.schema.internal.SchemaManagementToolInitiator;
 
 /**
  * Central definition of the standard set of service initiators defined by Hibernate.
  * 
  * @author Steve Ebersole
  */
 public final class StandardServiceInitiators {
 	private StandardServiceInitiators() {
 	}
 
 	public static List<StandardServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<StandardServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<StandardServiceInitiator> serviceInitiators = new ArrayList<StandardServiceInitiator>();
 
 		serviceInitiators.add( CfgXmlAccessServiceInitiator.INSTANCE );
 		serviceInitiators.add( ConfigurationServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( ImportSqlCommandExtractorInitiator.INSTANCE );
 		serviceInitiators.add( SchemaManagementToolInitiator.INSTANCE );
 
 		serviceInitiators.add( JdbcEnvironmentInitiator.INSTANCE );
 		serviceInitiators.add( JndiServiceInitiator.INSTANCE );
 		serviceInitiators.add( JmxServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( PersisterClassResolverInitiator.INSTANCE );
 		serviceInitiators.add( PersisterFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( MultiTenantConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( BatchBuilderInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 		serviceInitiators.add( RefCursorSupportInitiator.INSTANCE );
 
+		serviceInitiators.add( QueryTranslatorFactoryInitiator.INSTANCE );
 		serviceInitiators.add( MutableIdentifierGeneratorFactoryInitiator.INSTANCE);
 
 		serviceInitiators.add( JtaPlatformResolverInitiator.INSTANCE );
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( SessionFactoryServiceRegistryFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( RegionFactoryInitiator.INSTANCE );
 
 		return Collections.unmodifiableList( serviceInitiators );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
index 64e796f8ab..0619877f07 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
@@ -1,51 +1,51 @@
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
 package org.hibernate.service.internal;
 
-import org.hibernate.SessionFactory;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 
 /**
  * Acts as a {@link Service} in the {@link org.hibernate.boot.registry.internal.StandardServiceRegistryImpl} whose function is as a factory for
  * {@link SessionFactoryServiceRegistryImpl} implementations.
  *
  * @author Steve Ebersole
  */
 public class SessionFactoryServiceRegistryFactoryImpl implements SessionFactoryServiceRegistryFactory {
 	private final ServiceRegistryImplementor theBasicServiceRegistry;
 
 	public SessionFactoryServiceRegistryFactoryImpl(ServiceRegistryImplementor theBasicServiceRegistry) {
 		this.theBasicServiceRegistry = theBasicServiceRegistry;
 	}
 
 	@Override
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions options) {
+			SessionFactoryOptions options) {
 		return new SessionFactoryServiceRegistryImpl( theBasicServiceRegistry, sessionFactory, options );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
index 843ea31d7d..f55c52b745 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
@@ -1,70 +1,70 @@
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
 package org.hibernate.service.internal;
 
-import org.hibernate.SessionFactory;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.spi.ServiceBinding;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public class SessionFactoryServiceRegistryImpl extends AbstractServiceRegistryImpl implements SessionFactoryServiceRegistry  {
 
-	private final SessionFactory.SessionFactoryOptions sessionFactoryOptions;
+	private final SessionFactoryOptions sessionFactoryOptions;
 	private final SessionFactoryImplementor sessionFactory;
 
 	@SuppressWarnings( {"unchecked"})
 	public SessionFactoryServiceRegistryImpl(
 			ServiceRegistryImplementor parent,
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions sessionFactoryOptions) {
+			SessionFactoryOptions sessionFactoryOptions) {
 		super( parent );
 
 		this.sessionFactory = sessionFactory;
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		// for now, just use the standard initiator list
 		for ( SessionFactoryServiceInitiator initiator : StandardSessionFactoryServiceInitiators.LIST ) {
 			// create the bindings up front to help identify to which registry services belong
 			createServiceBinding( initiator );
 		}
 	}
 
 	@Override
 	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
 		SessionFactoryServiceInitiator<R> sessionFactoryServiceInitiator = (SessionFactoryServiceInitiator<R>) serviceInitiator;
 		return sessionFactoryServiceInitiator.initiateService( sessionFactory, sessionFactoryOptions, this );
 	}
 
 	@Override
 	public <R extends Service> void configureService(ServiceBinding<R> serviceBinding) {
 		//TODO nothing to do here or should we inject SessionFactory properties?
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
index 06fa5f9ec2..c3d0a1b72e 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
@@ -1,54 +1,54 @@
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
 package org.hibernate.service.spi;
 
-import org.hibernate.SessionFactory;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.service.Service;
 
 /**
  * Contract for an initiator of services that target the specialized service registry
  * {@link SessionFactoryServiceRegistry}
  *
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceInitiator<R extends Service> extends ServiceInitiator<R>{
 	/**
 	 * Initiates the managed service.
 	 * <p/>
 	 * Note for implementors: signature is guaranteed to change once redesign of SessionFactory building is complete
 	 *
 	 * @param sessionFactory The session factory.  Note the the session factory is still in flux; care needs to be taken
 	 * in regards to what you call.
 	 * @param sessionFactoryOptions Options specified for building the SessionFactory
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
 	public R initiateService(
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions sessionFactoryOptions,
+			SessionFactoryOptions sessionFactoryOptions,
 			ServiceRegistryImplementor registry);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
index 475abd5107..d30bc691cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
@@ -1,53 +1,53 @@
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
 package org.hibernate.service.spi;
 
-import org.hibernate.SessionFactory;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
 
 /**
  * Contract for builder of {@link SessionFactoryServiceRegistry} instances.
  * <p/>
  * Is itself a service within the standard service registry.
  *
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceRegistryFactory extends Service {
 	/**
 	 * Create the registry.
 	 *
 	 * @param sessionFactory The (still being built) session factory.  Generally this is useful
 	 * for grabbing a reference for later use.  However, care should be taken when invoking on
 	 * the session factory until after it has been fully initialized.
 	 * @param sessionFactoryOptions The build options.
 	 *
 	 * @return The registry
 	 */
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions sessionFactoryOptions);
+			SessionFactoryOptions sessionFactoryOptions);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
index 89e69c732d..18864d971f 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
@@ -1,109 +1,109 @@
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
 package org.hibernate.stat.internal;
 
 import org.hibernate.HibernateException;
-import org.hibernate.SessionFactory;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.stat.spi.StatisticsFactory;
 import org.hibernate.stat.spi.StatisticsImplementor;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
 public class StatisticsInitiator implements SessionFactoryServiceInitiator<StatisticsImplementor> {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatisticsInitiator.class.getName() );
 
 	public static final StatisticsInitiator INSTANCE = new StatisticsInitiator();
 
 	/**
 	 * Names the {@link StatisticsFactory} to use.  Recognizes both a class name as well as an instance of
 	 * {@link StatisticsFactory}.
 	 */
 	public static final String STATS_BUILDER = "hibernate.stats.factory";
 
 	@Override
 	public Class<StatisticsImplementor> getServiceInitiated() {
 		return StatisticsImplementor.class;
 	}
 
 	@Override
 	public StatisticsImplementor initiateService(
 			SessionFactoryImplementor sessionFactory,
-			SessionFactory.SessionFactoryOptions sessionFactoryOptions,
+			SessionFactoryOptions sessionFactoryOptions,
 			ServiceRegistryImplementor registry) {
 		final Object configValue = registry.getService( ConfigurationService.class ).getSettings().get( STATS_BUILDER );
 		return initiateServiceInternal( sessionFactory, configValue, registry );
 	}
 
 	private StatisticsImplementor initiateServiceInternal(
 			SessionFactoryImplementor sessionFactory,
 			Object configValue,
 			ServiceRegistryImplementor registry) {
 
 		StatisticsFactory statisticsFactory;
 		if ( configValue == null ) {
 			statisticsFactory = DEFAULT_STATS_BUILDER;
 		}
 		else if ( StatisticsFactory.class.isInstance( configValue ) ) {
 			statisticsFactory = (StatisticsFactory) configValue;
 		}
 		else {
 			// assume it names the factory class
 			final ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
 			try {
 				statisticsFactory = (StatisticsFactory) classLoaderService.classForName( configValue.toString() ).newInstance();
 			}
 			catch (HibernateException e) {
 				throw e;
 			}
 			catch (Exception e) {
 				throw new HibernateException(
 						"Unable to instantiate specified StatisticsFactory implementation [" + configValue.toString() + "]",
 						e
 				);
 			}
 		}
 
 		StatisticsImplementor statistics = statisticsFactory.buildStatistics( sessionFactory );
 		final boolean enabled = sessionFactory.getSettings().isStatisticsEnabled();
 		statistics.setStatisticsEnabled( enabled );
 		LOG.debugf( "Statistics initialized [enabled=%s]", enabled );
 		return statistics;
 	}
 
 	private static StatisticsFactory DEFAULT_STATS_BUILDER = new StatisticsFactory() {
 		@Override
 		public StatisticsImplementor buildStatistics(SessionFactoryImplementor sessionFactory) {
 			return new ConcurrentStatisticsImpl( sessionFactory );
 		}
 	};
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java
index 92a9be421b..2c907ae7e0 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/JndiInfinispanRegionFactoryTestCase.java
@@ -1,22 +1,33 @@
 package org.hibernate.test.cache.infinispan;
 
-import java.util.Properties;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.cache.infinispan.JndiInfinispanRegionFactory;
+import org.hibernate.cache.spi.RegionFactory;
+import org.hibernate.cfg.AvailableSettings;
 
 import org.junit.Test;
 
-import org.hibernate.cfg.SettingsFactory;
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 
 /**
  * // TODO: Document this
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class JndiInfinispanRegionFactoryTestCase {
    @Test
    public void testConstruction() {
-      Properties p = new Properties();
-      p.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.infinispan.JndiInfinispanRegionFactory");
-      SettingsFactory.createRegionFactory(p, true);
+      StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
+              .applySetting( AvailableSettings.CACHE_REGION_FACTORY, JndiInfinispanRegionFactory.class.getName() )
+              .build();
+      try {
+         RegionFactory regionFactory = ssr.getService( RegionFactory.class );
+         assertTyping( JndiInfinispanRegionFactory.class, regionFactory );
+      }
+      finally {
+         StandardServiceRegistryBuilder.destroy( ssr );
+      }
    }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
index fd41b7e2fe..863e70d831 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
@@ -1,152 +1,157 @@
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
 package org.hibernate.test.cache.infinispan.util;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
+import org.hibernate.boot.internal.SessionFactoryBuilderImpl.SessionFactoryOptionsImpl;
+import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Settings;
-import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.service.ServiceRegistry;
 
 import org.hibernate.test.cache.infinispan.functional.SingleNodeTestCase;
 
 /**
  * Utilities for cache testing.
  * 
  * @author <a href="brian.stansberry@jboss.com">Brian Stansberry</a>
  */
 public class CacheTestUtil {
    @SuppressWarnings("unchecked")
    public static Map buildBaselineSettings(
            String regionPrefix,
            Class regionFactory,
            boolean use2ndLevel,
            boolean useQueries) {
       Map settings = new HashMap();
 
       settings.put( AvailableSettings.GENERATE_STATISTICS, "true" );
       settings.put( AvailableSettings.USE_STRUCTURED_CACHE, "true" );
       settings.put( AvailableSettings.JTA_PLATFORM, BatchModeJtaPlatform.class.getName() );
 
       settings.put( AvailableSettings.CACHE_REGION_FACTORY, regionFactory.getName() );
       settings.put( AvailableSettings.CACHE_REGION_PREFIX, regionPrefix );
       settings.put( AvailableSettings.USE_SECOND_LEVEL_CACHE, String.valueOf( use2ndLevel ) );
       settings.put( AvailableSettings.USE_QUERY_CACHE, String.valueOf( useQueries ) );
 
       return settings;
    }
 
    public static StandardServiceRegistryBuilder buildBaselineStandardServiceRegistryBuilder(
            String regionPrefix,
            Class regionFactory,
            boolean use2ndLevel,
            boolean useQueries) {
       StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
 
       ssrb.applySettings(
               buildBaselineSettings( regionPrefix, regionFactory, use2ndLevel, useQueries )
       );
 
       return ssrb;
    }
 
    public static StandardServiceRegistryBuilder buildCustomQueryCacheStandardServiceRegistryBuilder(
            String regionPrefix,
            String queryCacheName) {
       final StandardServiceRegistryBuilder ssrb = buildBaselineStandardServiceRegistryBuilder(
               regionPrefix, InfinispanRegionFactory.class, true, true
       );
       ssrb.applySetting( InfinispanRegionFactory.QUERY_CACHE_RESOURCE_PROP, queryCacheName );
       return ssrb;
    }
 
    public static InfinispanRegionFactory startRegionFactory(ServiceRegistry serviceRegistry) {
       try {
          final ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );
-         final Properties properties = toProperties( cfgService.getSettings() );
-         final Settings settings = new SettingsFactory().buildSettings( properties, serviceRegistry );
 
          String factoryType = cfgService.getSetting( AvailableSettings.CACHE_REGION_FACTORY, StandardConverters.STRING );
          Class clazz = Thread.currentThread().getContextClassLoader().loadClass( factoryType );
          InfinispanRegionFactory regionFactory;
          if (clazz == InfinispanRegionFactory.class) {
             regionFactory = new SingleNodeTestCase.TestInfinispanRegionFactory();
          }
          else {
             regionFactory = (InfinispanRegionFactory) clazz.newInstance();
          }
+
+         final SessionFactoryOptionsImpl sessionFactoryOptions = new SessionFactoryOptionsImpl( (StandardServiceRegistry) serviceRegistry );
+         final Settings settings = new Settings( sessionFactoryOptions );
+         final Properties properties = toProperties( cfgService.getSettings() );
+
          regionFactory.start( settings, properties );
+
          return regionFactory;
       }
       catch (RuntimeException e) {
          throw e;
       }
       catch (Exception e) {
          throw new RuntimeException(e);
       }
    }
 
    public static InfinispanRegionFactory startRegionFactory(
            ServiceRegistry serviceRegistry,
            CacheTestSupport testSupport) {
       InfinispanRegionFactory factory = startRegionFactory( serviceRegistry );
       testSupport.registerFactory( factory );
       return factory;
    }
 
    public static void stopRegionFactory(
            InfinispanRegionFactory factory,
            CacheTestSupport testSupport) {
       testSupport.unregisterFactory( factory );
       factory.stop();
    }
 
    public static Properties toProperties(Map map) {
       if ( map == null ) {
          return null;
       }
 
       if ( map instanceof Properties ) {
          return (Properties) map;
       }
 
       Properties properties = new Properties();
       properties.putAll( map );
       return properties;
    }
 
    /**
     * Prevent instantiation.
     */
    private CacheTestUtil() {
    }
 
 }
